package org.osgi.test.cases.remoteserviceadmin.junit;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.ImportRegistration;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.test.cases.remoteserviceadmin.common.ModifiableService;
import org.osgi.test.cases.remoteserviceadmin.common.TestEventHandler;
import org.osgi.test.cases.remoteserviceadmin.common.Utils;

public class RemoteServiceAdminUpdateTest extends MultiFrameworkTestCase {
	private static final String SYSTEM_PACKAGES_EXTRA = "org.osgi.test.cases.remoteserviceadmin.system.packages.extra";
	private long m_timeout;

	public Map<String, String> getConfiguration() {
		Map<String, String> configuration = new HashMap<String, String>();
		configuration.put(Constants.FRAMEWORK_STORAGE_CLEAN, "true");

		// make sure that the server framework System Bundle exports the
		// interfaces
		String systemPackagesXtra = getProperty(SYSTEM_PACKAGES_EXTRA);
		configuration.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA,
				systemPackagesXtra);
		int console = getIntegerProperty("osgi.console", 0);
		if (console != 0) {
			configuration.put("osgi.console", "" + console + 1);
		}

		m_timeout = getLongProperty("rsa.ct.timeout", 300000L);

		return configuration;
	}

	/**
	 * Uses tb8 to export a "modifiable" service, imports this service and asks
	 * it to modify it own service export. Then check if the updated service can
	 * be updated and contains the newly added parameter that changed its
	 * registration.
	 * */
	public void testExportImportManually() throws Exception {
		verifyFramework();

		TestEventHandler eventHandler = new TestEventHandler(m_timeout);

		Hashtable<String, Object> props = new Hashtable<String, Object>();
		props.put(EventConstants.EVENT_TOPIC, new String[] {
				"org/osgi/service/remoteserviceadmin/IMPORT_REGISTRATION",
				"org/osgi/service/remoteserviceadmin/IMPORT_UNREGISTRATION",
				"org/osgi/service/remoteserviceadmin/IMPORT_ERROR",
				"org/osgi/service/remoteserviceadmin/IMPORT_UPDATE" });

		// register an event handler to verify that the required register and
		// update events are generated
		registerService(EventHandler.class.getName(), eventHandler, props);

		//
		// install test bundle in child framework
		//
		BundleContext childContext = getFramework().getBundleContext();


		Bundle tb8Bundle = installBundle(childContext, "/tb8.jar");
		assertNotNull(tb8Bundle);
		tb8Bundle.start();

		//
		// find the RSA in the parent framework and import the
		// service
		//
		ServiceReference rsaRef = getContext().getServiceReference(
				RemoteServiceAdmin.class.getName());
		assertNotNull(rsaRef);
		RemoteServiceAdmin rsa = (RemoteServiceAdmin) getContext().getService(
				rsaRef);
		assertNotNull(rsa);

		try {
			// give the child framework and tb8 some time to start and export
			// the service
			Thread.sleep(10000);

			// reconstruct the endpoint description in version 1.0.0
			EndpointDescription endpoint = Utils.reconstructEndpoint("1.0.0",
					this);
			// gather all the service and exporting intents
			List<String> endpointIntents = endpoint.getIntents();
			assertNotNull(endpointIntents);
			assertFalse(endpointIntents.isEmpty());

			//
			// 122.4.2: Importing
			// positive test: import the service
			//
			ImportRegistration importReg = rsa.importService(endpoint);
			assertNotNull(importReg);
			assertNull(importReg.getException());

			{ // check if the required IMPORT_REGISTRATION event was raised
				Event event = eventHandler
					.getNextEventForTopic("org/osgi/service/remoteserviceadmin/IMPORT_REGISTRATION");
				assertNotNull(event);
				RemoteServiceAdminEvent rsaevent = TestEventHandler
						.verifyBasicRsaEventProperties(rsaRef, event);
				assertNotNull(rsaevent);
				assertNotNull(event.getProperty("timestamp"));

				// check event type
				String topic = event.getTopic();
				assertNull("cause in event", event.getProperty("cause"));
				assertEquals(RemoteServiceAdminEvent.IMPORT_REGISTRATION,
						rsaevent.getType());
			}

			// get the modifiable service that has been exported from tb8
			ModifiableService modifiableService = null;
			{
				ServiceReference modifiableServiceRef = getContext()
						.getServiceReference(ModifiableService.class.getName());
				assertNotNull(modifiableServiceRef);
				modifiableService = (ModifiableService) getContext()
						.getService(
						modifiableServiceRef);
				assertNotNull(modifiableService);
			}

			//
			// Now as we have imported the service, ask the service to modify
			// itself and the export description
			//
			modifiableService.addServiceProperty();

			//
			// Give the remoting implementation some time to actually update the
			// service export
			//
			Thread.sleep(5000);
			

			//
			// now try to import the updated service export and verify that the
			// new property is available
			//

			// reconstruct the endpoint description of the modified endpoint
			EndpointDescription endpointModified = Utils.reconstructEndpoint(
					"1.0.0", this, 1);

			// request the update of the import
			assertTrue(importReg.update(endpointModified));
			assertNull(importReg.getException());

			// get the modified service that has been exported by tb8
			ModifiableService alreadyModifiedService = null;
			{
				ServiceReference alreadyModifiedServiceRefs[] = getContext()
						.getServiceReferences(
								ModifiableService.class.getName(),
								"(someNewProp=SomeValue)");

				assertTrue(
						"after importing the updated remote service, we need to be able to find at least one suitable service",
						alreadyModifiedServiceRefs.length >= 1);

				ServiceReference alreadyModifiedServiceRef = alreadyModifiedServiceRefs[0];
				
				assertNotNull(alreadyModifiedServiceRef);
				alreadyModifiedService = (ModifiableService) getContext()
						.getService(
						alreadyModifiedServiceRef);
				assertNotNull(alreadyModifiedService);
			}

			{ // check if the required IMPORT_UPDATE event was raised
				Event event = eventHandler
						.getNextEventForTopic("org/osgi/service/remoteserviceadmin/IMPORT_UPDATE");
				assertNotNull(
						"After the upddate of an imported endpoit we should have received an IMPORT_UPDATE event",
						event);

				RemoteServiceAdminEvent rsaevent = TestEventHandler
						.verifyBasicRsaEventProperties(
						rsaRef, event);
				assertNotNull(rsaevent);
				assertNotNull(event.getProperty("timestamp"));

				// check event type
				String topic = event.getTopic();
				assertNull("cause in event", event.getProperty("cause"));
				assertEquals(RemoteServiceAdminEvent.IMPORT_UPDATE,
						rsaevent.getType());
			}

		} finally {
			// Make sure the service instance of the RSA can be closed by the
			// RSA Service Factory
			getContext().ungetService(rsaRef);
		}
	}
}

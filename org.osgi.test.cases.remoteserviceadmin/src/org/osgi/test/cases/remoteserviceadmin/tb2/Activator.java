/*
 * Copyright (c) OSGi Alliance (2008, 2009, 2010). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.test.cases.remoteserviceadmin.tb2;

import static junit.framework.TestCase.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.ExportReference;
import org.osgi.service.remoteserviceadmin.ExportRegistration;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;
import org.osgi.test.cases.remoteserviceadmin.common.A;
import org.osgi.test.cases.remoteserviceadmin.common.B;
import org.osgi.test.cases.remoteserviceadmin.common.RemoteServiceConstants;
import org.osgi.test.cases.remoteserviceadmin.common.TestRemoteServiceAdminListener;
import org.osgi.test.cases.remoteserviceadmin.common.Utils;
import org.osgi.test.support.OSGiTestCaseProperties;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 * @version 1.0.0
 */
public class Activator implements BundleActivator, A, B {


	ServiceRegistration            registration;
	BundleContext                  context;
	RemoteServiceAdmin             rsa;
	Collection<ExportRegistration> exportRegistrations;
	TestRemoteServiceAdminListener remoteServiceAdminListener;
	long timeout;
	String version;
	ServiceTracker<RemoteServiceAdmin, RemoteServiceAdmin> rsaTracker;

	/**
	 * Used for writing the endpoint description to a system property to be used
	 * by parent framework @see {@link Utils.exportEndpointDescription}
	 */
	private int registrationCounter = 0;

	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		this.context = context;
		timeout = OSGiTestCaseProperties.getLongProperty("rsa.ct.timeout",
				300000L);
		rsaTracker = new ServiceTracker<RemoteServiceAdmin, RemoteServiceAdmin>(context, RemoteServiceAdmin.class,
				null);
		rsaTracker.open();

		// read my version from the bundle header
		version = context.getBundle().getHeaders().get("RSA-Version");
		assertNotNull(version);

		Set<String> set = new HashSet<String>();
		set.add("one");
		set.add("two");
		List<String> list = new LinkedList<String>();
		list.add("first");
		list.add("second");

		Hashtable<String, Object> dictionary = new Hashtable<String, Object>();
		dictionary.put("mykey", "will be overridden");
		dictionary.put("myprop", "myvalue");
		dictionary.put("myset", set);
		dictionary.put("mylist", list);
		dictionary.put("myfloat", 3.1415f);
		dictionary.put("mydouble", -3.1415d);
		dictionary.put("mychar", 't');
		dictionary.put("myxml", "<myxml>test</myxml>");
		dictionary.put(".do_not_forward", "private");
		dictionary.put(RemoteServiceConstants.SERVICE_EXPORTED_INTERFACES, A.class.getName());

		registration = context.registerService(new String[]{A.class.getName()}, this, dictionary);

		test();
	}

	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		registration.unregister();
		teststop();
	}

	/**
	 * @see org.osgi.test.cases.remoteserviceadmin.common.A#getA()
	 */
	public String getA() {
		return "this is A";
	}

	/**
	 * @see org.osgi.test.cases.remoteserviceadmin.common.B#getB()
	 */
	public String getB() {
		return "this is B";
	}

	public void test() throws Exception {
		// lookup RemoteServiceAdmin service
		rsa = rsaTracker.waitForService(timeout);
		assertNotNull(rsa);

		try {
			//
			// register a RemoteServiceAdminListener to receive the export
			// notification
			//
			remoteServiceAdminListener = new TestRemoteServiceAdminListener(
					timeout);
			ServiceRegistration sr = context.registerService(RemoteServiceAdminListener.class.getName(), remoteServiceAdminListener, null);
			assertNotNull(sr);


			//
			// 122.4.1 export the service, positive tests
			//
			// load the external properties file with the config types for the server side service
			Map<String, Object> properties = Utils
					.loadServerTCKProperties(context);
			properties.put("mykey", "has been overridden");
			properties.put("objectClass", "can.not.be.changed.Class");
			properties.put("service.id", "can.not.be.changed.Id");
			Utils.processFreePortProperties(properties);

			// export the service
			exportRegistrations = rsa.exportService(registration.getReference(), properties);
			assertNotNull(exportRegistrations);
			assertFalse(exportRegistrations.isEmpty());

			for (Iterator<ExportRegistration> it = exportRegistrations.iterator(); it.hasNext();) {
				ExportRegistration er = it.next();

				assertNull(er.getException());
				ExportReference ref = er.getExportReference();
				assertNotNull(ref);

				// 122.4.1 Exporting
				assertEquals(registration.getReference(), ref.getExportedService());

				assertEquals(DefaultTestBundleControl.arrayToString((Object[]) registration.getReference().getProperty("objectClass"), true),
						DefaultTestBundleControl.arrayToString((Object[]) ref.getExportedService().getProperty("objectClass"), true));
				assertEquals(registration.getReference().getProperty("service.id"), ref.getExportedService().getProperty("service.id"));

				EndpointDescription ed = ref.getExportedEndpoint();
				assertNotNull(ed);
				assertNotNull(ed.getProperties().get("objectClass"));
				assertTrue(ed.getInterfaces().contains(A.class.getName()));
				assertFalse(ed.getInterfaces().contains(B.class.getName()));

				assertNotNull(ed.getId());
				assertNotNull(ed.getConfigurationTypes());
				assertFalse(ed.getConfigurationTypes().isEmpty());
				assertEquals(context.getProperty("org.osgi.framework.uuid"),
						ed.getFrameworkUUID());
				assertNotNull(ed.getProperties().get("endpoint.service.id"));

				Utils.exportEndpointDescription(ed, version,
						registrationCounter++);
			}

			//
			// 122.4.1 Exporting
			// 122.10.12 verify that export notification was sent to RemoteServiceAdminListeners
			//
			RemoteServiceAdminEvent event = remoteServiceAdminListener.getNextEvent();
			assertNotNull("no RemoteServiceAdminEvent received", event);
			assertNotNull("122.10.11: source must not be null", event.getSource());
			assertNull(event.getException());
			assertEquals("122.10.11: event type is wrong", RemoteServiceAdminEvent.EXPORT_REGISTRATION, event.getType());

			ExportReference exportReference = event.getExportReference();
			assertNotNull("ExportReference expected in event", exportReference);
			assertEquals(registration.getReference(), exportReference.getExportedService());

			EndpointDescription ed = exportReference.getExportedEndpoint();
			assertNotNull(ed);
			assertTrue(ed.getInterfaces().contains(A.class.getName()));
			assertFalse(ed.getInterfaces().contains(B.class.getName()));

			assertNotNull(ed.getId());
			assertNotNull(ed.getConfigurationTypes());
			assertFalse(ed.getConfigurationTypes().isEmpty());
			assertEquals(context.getProperty("org.osgi.framework.uuid"), ed
					.getFrameworkUUID());

		} finally {
			// as the exported services are now required to be exported after this method returns
			// the rsaRef can't be released as this also causes the services to be unexported
			//context.ungetService(rsaRef);
		}
	}

	/**
	 * 122.4.1 Exporting
	 * Once the ExportRegistration is closed, a notification is expected to be sent
	 * to the RemoteServiceAdminListener that the service is no longer exported.
	 */
	private void teststop() throws Exception {
		try{
		assertNotNull(exportRegistrations);
		assertFalse(exportRegistrations.isEmpty());


		// close ExportRegistrations
		for (Iterator<ExportRegistration> it = exportRegistrations.iterator(); it.hasNext();) {
			ExportRegistration er = it.next();

			assertNull(er.getException());
			er.close();
		}

		//
		// 122.10.12 verify that export notification was sent to RemoteServiceAdminListeners
		//
		RemoteServiceAdminEvent event = remoteServiceAdminListener.getNextEvent();
		assertNotNull("no RemoteServiceAdminEvent received", event);
		assertNotNull("122.10.11: source must not be null", event.getSource());
		assertNull(event.getException());
		assertEquals("122.10.11: event type is wrong", RemoteServiceAdminEvent.EXPORT_UNREGISTRATION, event.getType());

		ExportReference exportReference = event.getExportReference();
		assertNotNull("ExportReference expected in event", exportReference);

		assertNull(exportReference.getExportedEndpoint());

		} finally {
			// the release of the rsa service will also trigger the unexport of the services of this bundle
			if (rsaTracker != null)
				rsaTracker.close();
		}
	}






}

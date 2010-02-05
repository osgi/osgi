/*
 * Copyright (c) OSGi Alliance (2008 -  2010). All Rights Reserved.
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
package org.osgi.test.cases.remoteserviceadmin.junit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import junit.framework.Assert;

import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.ExportReference;
import org.osgi.service.remoteserviceadmin.ExportRegistration;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;
import org.osgi.test.cases.remoteserviceadmin.common.A;
import org.osgi.test.cases.remoteserviceadmin.common.B;
import org.osgi.test.cases.remoteserviceadmin.common.RemoteServiceConstants;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.compatibility.Semaphore;

/**
 * RSA 122.4.1 Exporting test cases
 * 
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 * @version 1.0.0
 */
public class RemoteServiceAdminExportTest extends DefaultTestBundleControl {
	private RemoteServiceAdmin remoteServiceAdmin;

	/**
	 * @see org.osgi.test.support.compatibility.DefaultTestBundleControl#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		remoteServiceAdmin = (RemoteServiceAdmin) getService(RemoteServiceAdmin.class);
	}
	/**
	 * @see org.osgi.test.support.compatibility.DefaultTestBundleControl#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		ungetAllServices();
	}
	
	/**
	 * 122.4.1 Exporting
	 * 
	 * Tests the export of a service with negative edge cases according to
	 * the spec.
	 */
	public void testExportUnsupportedConfigType() throws Exception {
		// create an register a service
		Hashtable<String, String> dictionary = new Hashtable<String, String>();
		dictionary.put("mykey", "will be overridden");
		dictionary.put("myprop", "myvalue");
		dictionary.put(RemoteServiceConstants.SERVICE_EXPORTED_INTERFACES, A.class.getName());

		TestService service = new TestService();
		
		ServiceRegistration registration = getContext().registerService(new String[]{A.class.getName(), B.class.getName()},
				                                                        service, dictionary);
		assertNotNull(registration);

		try {
			//
			// 122.4.1 export the service
			//
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("mykey", "has been overridden");
			properties.put(RemoteConstants.SERVICE_INTENTS, "my_intent_is_for_this_to_work");
			properties.put("objectClass", "can.not.be.changed.Class");
			properties.put("service.id", "can.not.be.changed.Id");

			//
			// negative test: give a configuration type that is not supported by the RSA
			// implementation. The exportService call has to be ignored then
			properties.put(RemoteConstants.SERVICE_EXPORTED_CONFIGS, "guaranteed_not_supported_" + System.currentTimeMillis());

			Collection<ExportRegistration> exportRegistrations = remoteServiceAdmin.exportService(registration.getReference(), properties);
			// TODO this should be returning an empty collection and not null
			assertNull("122.4.1: service must not be exported due to unsatisfied config type", exportRegistrations);
			//		assertTrue("122.4.1: service must not be exported due to unsatisfied config type", exportRegistrations.isEmpty());
		} finally {
			registration.unregister();
		}
	}

	/**
	 * 122.4.1 Exporting
	 * 
	 * Tests the export of a service with negative edge cases according to
	 * the spec.
	 */
	public void testExportUnsupportedIntent() throws Exception {
		// create an register a service
		Hashtable<String, String> dictionary = new Hashtable<String, String>();
		dictionary.put("mykey", "will be overridden");
		dictionary.put("myprop", "myvalue");
		dictionary.put(RemoteServiceConstants.SERVICE_EXPORTED_INTERFACES, A.class.getName());

		TestService service = new TestService();
		
		ServiceRegistration registration = getContext().registerService(new String[]{A.class.getName(), B.class.getName()},
				                                                        service, dictionary);
		assertNotNull(registration);

		try {
			//
			// 122.4.1 export the service
			//
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("mykey", "has been overridden");
			properties.put(RemoteConstants.SERVICE_INTENTS, "my_intent_is_for_this_to_work");
			properties.put("objectClass", "can.not.be.changed.Class");
			properties.put("service.id", "can.not.be.changed.Id");

			//
			// negative test: give a service intent that is not supported by the RSA
			// implementation. The exportService call has to be ignored then
			properties.put(RemoteConstants.SERVICE_EXPORTED_INTENTS, "guaranteed_not_supported_" + System.currentTimeMillis());

			Collection<ExportRegistration> exportRegistrations = remoteServiceAdmin.exportService(registration.getReference(), properties);
			// TODO this should be returning an empty collection and not null
			assertNull("122.4.1: service must not be exported due to unsatisfied service intent", exportRegistrations);
			//		assertTrue("122.4.1: service must not be exported due to unsatisfied config type", exportRegistrations.isEmpty());
		} finally {
			registration.unregister();
		}
	}

	/**
	 * 122.4.1 Exporting
	 * 
	 * Tests the export of a service with negative edge cases according to
	 * the spec.
	 */
	public void testExportInvalidInterface() throws Exception {
		// create an register a service
		Hashtable<String, String> dictionary = new Hashtable<String, String>();
		dictionary.put("mykey", "will be overridden");
		dictionary.put("myprop", "myvalue");
		dictionary.put(RemoteServiceConstants.SERVICE_EXPORTED_INTERFACES, A.class.getName());

		TestService service = new TestService();
		
		ServiceRegistration registration = getContext().registerService(new String[]{A.class.getName(), B.class.getName()},
				                                                        service, dictionary);
		assertNotNull(registration);

		try {
			//
			// 122.4.1 export the service
			//
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("mykey", "has been overridden");
			properties.put(RemoteConstants.SERVICE_INTENTS, "my_intent_is_for_this_to_work");
			properties.put("objectClass", "can.not.be.changed.Class");
			properties.put("service.id", "can.not.be.changed.Id");

			properties.put(RemoteConstants.SERVICE_EXPORTED_INTERFACES, "something.else.Interface");

			Collection<ExportRegistration> exportRegistrations = remoteServiceAdmin.exportService(registration.getReference(), properties);
			// TODO this should be returning an empty collection and not null
			assertNull("122.4.1: service must not be exported due to invalid interface", exportRegistrations);
			//		assertTrue("122.4.1: service must not be exported due to unsatisfied config type", exportRegistrations.isEmpty());
		} finally {
			registration.unregister();
		}
	}

	/**
	 * 122.4.1 Exporting
	 * 
	 * Tests the export of a service with negative edge cases according to
	 * the spec.
	 * Register a service multiple times. Each registration is supposed to return
	 * a new ExportRegistration
	 */
	public void testExportRSAListenerNotification() throws Exception {
		// create an register a service
		Hashtable<String, String> dictionary = new Hashtable<String, String>();
		dictionary.put("mykey", "will be overridden");
		dictionary.put("myprop", "myvalue");
		dictionary.put(RemoteServiceConstants.SERVICE_EXPORTED_INTERFACES, A.class.getName());

		TestService service = new TestService();
		
		ServiceRegistration registration = getContext().registerService(new String[]{A.class.getName(), B.class.getName()},
				                                                        service, dictionary);
		assertNotNull(registration);

		try {
			//
			// register a RemoteServiceAdminListener to receive the export
			// notification
			//
			TestRemoteServiceAdminListener remoteServiceAdminListener = new TestRemoteServiceAdminListener();
			registerService(RemoteServiceAdminListener.class.getName(), remoteServiceAdminListener, null);

			//
			// 122.4.1 export the service
			//
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("mykey", "has been overridden");
			properties.put(RemoteConstants.SERVICE_INTENTS, "my_intent_is_for_this_to_work");
			properties.put("objectClass", "can.not.be.changed.Class");
			properties.put("service.id", "can.not.be.changed.Id");

			Collection<ExportRegistration> exportRegistrations = remoteServiceAdmin.exportService(registration.getReference(), properties);
			assertNotNull(exportRegistrations);

			//
			// 122.8 verify that export notification was sent to RemoteServiceAdminListeners
			//
			boolean exportFailed = false;
			
			RemoteServiceAdminEvent event = remoteServiceAdminListener.getNextEvent();
			assertNotNull("no RemoteServiceAdminEvent received", event);
			assertEquals(0, remoteServiceAdminListener.getEventCount());
			assertNotNull("122.10.11: source must not be null", event.getSource());

			ExportReference exportReference  = null;
			
			switch (event.getType()) {
			case RemoteServiceAdminEvent.EXPORT_REGISTRATION:
				assertNull(event.getException());
				assertEquals("122.10.11: event type is wrong", RemoteServiceAdminEvent.EXPORT_REGISTRATION, event.getType());

				exportReference = event.getExportReference();
				assertNotNull("ExportReference expected in event", exportReference);

				assertNotNull(exportReference.getExportedEndpoint());
				break;
			case RemoteServiceAdminEvent.EXPORT_ERROR:
				exportFailed = true;
				assertNotNull(event.getException());
				try {
					event.getExportReference();
					fail("IllegalStateException expected");
				} catch (IllegalStateException ie) {}
				break;
			default:
				fail("wrong event type");
			}


			// ungetting the RSA service will also close the ExportRegistration and therefore
			// emit an event
			ungetService(remoteServiceAdmin);

			if (!exportFailed) {
				assertNull("122.4.1: after closing ExportRegistration, ExportReference.getExportedEndpoint must return null",
						exportReference.getExportedEndpoint());
				assertNull("122.4.1: after closing ExportRegistration, ExportReference.getExportedService must return null",
						exportReference.getExportedService());
				
				event = remoteServiceAdminListener.getNextEvent();
				assertNotNull("no RemoteServiceAdminEvent received", event);
				assertNotNull("122.10.11: source must not be null", event.getSource());
				assertNull(event.getException());
				assertEquals("122.10.11: event type is wrong", RemoteServiceAdminEvent.EXPORT_UNREGISTRATION, event.getType());

				exportReference = event.getExportReference();
				assertNotNull("ExportReference expected in event", exportReference);

				assertNull(exportReference.getExportedEndpoint());

				assertEquals(0, remoteServiceAdminListener.getEventCount());
			}

		} finally {
			registration.unregister();
		}
	}

	/**
	 * 122.4.1 Exporting
	 * 
	 * Tests the export of a service with negative edge cases according to
	 * the spec.
	 * Register a service multiple times. Each registration is supposed to return
	 * a new ExportRegistration
	 */
	public void testExportEventNotification() throws Exception {
		// create an register a service
		Hashtable<String, String> dictionary = new Hashtable<String, String>();
		dictionary.put("mykey", "will be overridden");
		dictionary.put("myprop", "myvalue");
		dictionary.put(RemoteServiceConstants.SERVICE_EXPORTED_INTERFACES, A.class.getName());

		TestService service = new TestService();
		
		ServiceRegistration registration = getContext().registerService(new String[]{A.class.getName(), B.class.getName()},
				                                                        service, dictionary);
		assertNotNull(registration);

		try {
			//
			// register a EventHandler to receive the export
			// notification
			//
			Hashtable<String, Object> props = new Hashtable<String, Object>();
			props.put(EventConstants.EVENT_TOPIC, new String[]{
					"org/osgi/service/remoteserviceadmin/EXPORT_REGISTRATION",
					"org/osgi/service/remoteserviceadmin/EXPORT_UNREGISTRATION",
					"org/osgi/service/remoteserviceadmin/EXPORT_ERROR"});
			TestEventHandler eventHandler = new TestEventHandler();
			
			registerService(EventHandler.class.getName(), eventHandler, props);

			//
			// 122.4.1 export the service
			//
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("mykey", "has been overridden");
			properties.put(RemoteConstants.SERVICE_INTENTS, "my_intent_is_for_this_to_work");
			properties.put("objectClass", "can.not.be.changed.Class");
			properties.put("service.id", "can.not.be.changed.Id");

			Collection<ExportRegistration> exportRegistrations = remoteServiceAdmin.exportService(registration.getReference(), properties);
			assertNotNull(exportRegistrations);

			ExportRegistration exportRegistration = exportRegistrations.iterator().next();
			assertNotNull(exportRegistration);
			ExportReference reference = exportRegistration.getExportReference();
			assertNotNull(reference);
			EndpointDescription description = reference.getExportedEndpoint();
			assertNotNull(description);
			
			//
			// 122.8.1 verify that export notification was sent to EventHandler
			//
			ServiceReference sref = getServiceReference(remoteServiceAdmin);
			
			Event event = eventHandler.getNextEvent();
			assertNotNull("no Event received", event);
			assertEquals(0, eventHandler.getEventCount());
			
			assertEquals(sref.getBundle(), event.getProperty("bundle"));
			assertEquals(sref.getBundle().getSymbolicName(), event.getProperty("bundle.symbolicname"));
			assertEquals(description, event.getProperty("export.registration"));
			assertEquals(sref.getBundle().getBundleId(), event.getProperty("bundle.id"));
			assertEquals(sref.getBundle().getVersion(), event.getProperty("bundle.version"));
			assertNotNull(event.getProperty("timestamp"));
			RemoteServiceAdminEvent rsaevent = (RemoteServiceAdminEvent) event.getProperty("event");
			assertNotNull(rsaevent);
			
			boolean exportFailed = false;
			if ("org/osgi/service/remoteserviceadmin/EXPORT_REGISTRATION".equals(event.getTopic())) {
				assertNull(event.getProperty("cause"));
				assertEquals(RemoteServiceAdminEvent.EXPORT_REGISTRATION, rsaevent.getType());
			} else if ("org/osgi/service/remoteserviceadmin/EXPORT_ERROR".equals(event.getTopic())) {
				exportFailed = true;
				assertNotNull(event.getProperty("cause"));
				assertEquals(RemoteServiceAdminEvent.EXPORT_ERROR, rsaevent.getType());
			}


			// ungetting the RSA service will also close the ExportRegistration and therefore
			// emit an event
			ungetService(remoteServiceAdmin);

			if (!exportFailed) {
				event = eventHandler.getNextEvent();
				assertNotNull("no Event received", event);
				assertEquals(0, eventHandler.getEventCount());

				assertEquals("org/osgi/service/remoteserviceadmin/EXPORT_UNREGISTRATION", event.getTopic());
				assertEquals(sref.getBundle(), event.getProperty("bundle"));
				assertEquals(sref.getBundle().getSymbolicName(), event.getProperty("bundle.symbolicname"));
				// Marc Schaaf: changed from export.unregistration to export.registration
				assertEquals(description, event.getProperty("export.registration"));
				rsaevent = (RemoteServiceAdminEvent) event.getProperty("event");
				assertNotNull(rsaevent);
				assertEquals(RemoteServiceAdminEvent.EXPORT_UNREGISTRATION, rsaevent.getType());
				assertEquals(sref.getBundle().getBundleId(), event.getProperty("bundle.id"));
				assertEquals(sref.getBundle().getVersion(), event.getProperty("bundle.version"));
				assertNotNull(event.getProperty("timestamp"));
			}
		} finally {
			registration.unregister();
		}
	}

	/**
	 * 122.4.1 Exporting
	 * 
	 * Tests the export of a service with negative edge cases according to
	 * the spec.
	 * Register a service multiple times. Each registration is supposed to return
	 * a new ExportRegistration
	 */
	public void testExportMultipleRegistrations() throws Exception {
		// create an register a service
		Hashtable<String, String> dictionary = new Hashtable<String, String>();
		dictionary.put("mykey", "will be overridden");
		dictionary.put("myprop", "myvalue");
		dictionary.put(RemoteServiceConstants.SERVICE_EXPORTED_INTERFACES, A.class.getName());

		TestService service = new TestService();
		
		ServiceRegistration registration = getContext().registerService(new String[]{A.class.getName(), B.class.getName()},
				                                                        service, dictionary);
		assertNotNull(registration);

		try {
			// lookup RemoteServiceAdmin service 
			ServiceReference rsaRef = getContext().getServiceReference(RemoteServiceAdmin.class.getName());
			assertNotNull(rsaRef);
			RemoteServiceAdmin rsa = (RemoteServiceAdmin) getContext().getService(rsaRef);
			assertNotNull(rsa);

			try {
				//
				// 122.4.1 export the service
				//
				Map<String, Object> properties = new HashMap<String, Object>();
				properties.put("mykey", "has been overridden");
				properties.put(RemoteConstants.SERVICE_INTENTS, "my_intent_is_for_this_to_work");
				properties.put("objectClass", "can.not.be.changed.Class");
				properties.put("service.id", "can.not.be.changed.Id");

				Collection<ExportRegistration> exportRegistrations = rsa.exportService(registration.getReference(), properties);
				assertNotNull(exportRegistrations);
				
				// do it a second time
				Collection<ExportRegistration> exportRegistrations2 = rsa.exportService(registration.getReference(), properties);
				assertNotNull(exportRegistrations2);
				
				assertEquals(exportRegistrations.size(), exportRegistrations2.size());
		
				// 122.4.1: Exporting
				// all registrations have to point to the same exported service
				// and endpoints are shared among the registrations
				List<EndpointDescription> endpoints = new ArrayList<EndpointDescription>();
				
				for (Iterator<ExportRegistration> it = exportRegistrations.iterator(); it.hasNext();) {
					ExportRegistration er = it.next();
					
					ExportReference ref = er.getExportReference();
					assertNotNull(ref);
					
					assertEquals(registration.getReference(), ref.getExportedService());
					EndpointDescription ed = ref.getExportedEndpoint();
					endpoints.add(ed);
					// 122.2.5: verify EndpointDescription object
					for (Iterator<String> i = ed.getProperties().keySet().iterator(); i.hasNext();) {
						Assert.assertFalse(i.next().startsWith("service.exports."));
					}
					Assert.assertNotNull(ed.getProperties().get("service.imported"));
				}
				
				for (Iterator<ExportRegistration> it = exportRegistrations2.iterator(); it.hasNext();) {
					ExportRegistration er = it.next();
					
					ExportReference ref = er.getExportReference();
					assertNotNull(ref);
					
					assertEquals(registration.getReference(), ref.getExportedService());
					assertTrue(endpoints.contains(ref.getExportedEndpoint()));
				}
				
				assertEquals(exportRegistrations.size() + exportRegistrations2.size(), rsa.getExportedServices().size());
				
				// now close one of the registrations and ensure that service is still
				// exported until all registrations are closed
				for (Iterator<ExportRegistration> it = exportRegistrations2.iterator(); it.hasNext();) {
					ExportRegistration er = it.next();
					
					er.close();
					
					assertNull("122.4.1: after closing ExportRegistration, ExportReference.getExportedEndpoint must return null",
							er.getExportReference().getExportedEndpoint());
					assertNull("122.4.1: after closing ExportRegistration, ExportReference.getExportedService must return null",
							er.getExportReference().getExportedService());
				}
				assertEquals(exportRegistrations.size(), rsa.getExportedServices().size());
				for (Iterator<ExportRegistration> it = exportRegistrations.iterator(); it.hasNext();) {
					ExportRegistration er = it.next();
					
					er.close();
					
					assertNull("122.4.1: after closing ExportRegistration, ExportReference.getExportedEndpoint must return null",
							er.getExportReference().getExportedEndpoint());
					assertNull("122.4.1: after closing ExportRegistration, ExportReference.getExportedService must return null",
							er.getExportReference().getExportedService());
				}
				assertNotNull(rsa.getExportedServices());
				assertEquals(0, rsa.getExportedServices().size());
				
			} finally {
				getContext().ungetService(rsaRef);
			}
		} finally {
			registration.unregister();
		}
	}

	/**
	 * 122.4.1 Exporting
	 * 
	 * Tests the export of a service with negative edge cases according to
	 * the spec.
	 * Register a service. Read the configuration types from the EndpointDescription
	 * and ensure that there is a property present that starts with the name of the
	 * config type
	 */
	public void testExportConfigurationType() throws Exception {
		// read supported configuration types from the DistributionProvider
		ServiceReference[] dprefs = getContext().getServiceReferences(null, "(remote.configs.supported=*)");
		assertNotNull(dprefs);
		ServiceReference dpref = dprefs[0];
		String[] supportedConfigTypes = getConfigTypes(dpref.getProperty("remote.configs.supported"));
		assertTrue(supportedConfigTypes.length > 0);
		
		// create and register a service
		Hashtable<String, Object> dictionary = new Hashtable<String, Object>();
		dictionary.put("mykey", "will be overridden");
		dictionary.put("myprop", "myvalue");
		dictionary.put(RemoteServiceConstants.SERVICE_EXPORTED_INTERFACES, A.class.getName());
		dictionary.put(RemoteConstants.SERVICE_EXPORTED_CONFIGS, dpref.getProperty("remote.configs.supported"));

		TestService service = new TestService();
		
		ServiceRegistration registration = getContext().registerService(new String[]{A.class.getName(), B.class.getName()},
				                                                        service, dictionary);
		assertNotNull(registration);

		try {
			//
			// register a RemoteServiceAdminListener to receive the export
			// notification
			//
			TestRemoteServiceAdminListener remoteServiceAdminListener = new TestRemoteServiceAdminListener();
			registerService(RemoteServiceAdminListener.class.getName(), remoteServiceAdminListener, null);

			//
			// 122.4.1 export the service
			//
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("mykey", "has been overridden");
			properties.put(RemoteConstants.SERVICE_INTENTS, "my_intent_is_for_this_to_work");
			properties.put("objectClass", "can.not.be.changed.Class");
			properties.put("service.id", "can.not.be.changed.Id");
			properties.put(RemoteConstants.SERVICE_EXPORTED_CONFIGS, new String[]{supportedConfigTypes[0]});

			Collection<ExportRegistration> exportRegistrations = remoteServiceAdmin.exportService(registration.getReference(), properties);
			assertNotNull(exportRegistrations);
			assertFalse(exportRegistrations.isEmpty());
			
			//
			// 122.8 verify that export notification was sent to RemoteServiceAdminListeners
			//
			boolean exportFailed = false;
			
			RemoteServiceAdminEvent event = remoteServiceAdminListener.getNextEvent();
			assertNotNull("no RemoteServiceAdminEvent received", event);
			assertEquals(0, remoteServiceAdminListener.getEventCount());
			assertNotNull("122.10.11: source must not be null", event.getSource());

			ExportReference exportReference  = null;
			
			switch (event.getType()) {
			case RemoteServiceAdminEvent.EXPORT_REGISTRATION:
				assertNull(event.getException());
				assertEquals("122.10.11: event type is wrong", RemoteServiceAdminEvent.EXPORT_REGISTRATION, event.getType());

				exportReference = event.getExportReference();
				assertNotNull("ExportReference expected in event", exportReference);

				assertNotNull(exportReference.getExportedEndpoint());
				break;
			case RemoteServiceAdminEvent.EXPORT_ERROR:
				exportFailed = true;
				assertNotNull(event.getException());
				try {
					event.getExportReference();
					fail("IllegalStateException expected");
				} catch (IllegalStateException ie) {}
				break;
			default:
				fail("wrong event type");
			}

			List<String> configproperties = new ArrayList<String>();
			
			// extract configuration type and configuration properties
			if (!exportFailed) {
				EndpointDescription epd = exportReference.getExportedEndpoint();
				List<String> configtypes = epd.getConfigurationTypes();
				assertNotNull(configtypes);
				for (Iterator<String> it = configtypes.iterator(); it.hasNext();) {
					String configtype = it.next();
					
					// check for config type properties
					assertNotNull(configtype);
					assertEquals("service was exported with different config type than requested", supportedConfigTypes[0], configtype);
					
					for (Iterator<String> keys = epd.getProperties().keySet().iterator(); keys.hasNext(); ) {
						String key = keys.next();
						
						if (key.startsWith(configtype + ".")) {
							configproperties.add(key);
						}
					}
					assertFalse("122.4: config type properties missing from EndpointDescription", configproperties.isEmpty());
				}
			}

			// ungetting the RSA service will also close the ExportRegistration and therefore
			// emit an event
			ungetService(remoteServiceAdmin);

			if (!exportFailed) {
				event = remoteServiceAdminListener.getNextEvent();
				assertNotNull("no RemoteServiceAdminEvent received", event);
				assertNotNull("122.10.11: source must not be null", event.getSource());
				assertNull(event.getException());
				assertEquals("122.10.11: event type is wrong", RemoteServiceAdminEvent.EXPORT_UNREGISTRATION, event.getType());

				exportReference = event.getExportReference();
				assertNotNull("ExportReference expected in event", exportReference);

				assertNull(exportReference.getExportedEndpoint());
				assertEquals(0, remoteServiceAdminListener.getEventCount());
			}
		} finally {
			registration.unregister();
		}
	}

	/**
	 * 122.4.1 Exporting
	 * 
	 * Tests the export of a service with negative edge cases according to
	 * the spec.
	 * Register a service. Read the configuration types from the EndpointDescription
	 * and ensure that there is a property present that starts with the name of the
	 * config type. Then modify the configuration properties and try to export the
	 * service again. It should fail and generate events.
	 */
	public void testForceExportFailure() throws Exception {
		// read supported configuration types from the DistributionProvider
		ServiceReference[] dprefs = getContext().getServiceReferences(null, "(remote.configs.supported=*)");
		assertNotNull(dprefs);
		ServiceReference dpref = dprefs[0];
		String[] supportedConfigTypes = getConfigTypes(dpref.getProperty("remote.configs.supported"));
		assertTrue(supportedConfigTypes.length > 0);
		System.out.println("DSP supported config types:");
		for (String c : supportedConfigTypes) {
			System.out.println(" " + c);
		}
		
		// create and register a service
		Hashtable<String, Object> dictionary = new Hashtable<String, Object>();
		dictionary.put("mykey", "will be overridden");
		dictionary.put("myprop", "myvalue");
		dictionary.put(RemoteServiceConstants.SERVICE_EXPORTED_INTERFACES, A.class.getName());
		dictionary.put(RemoteConstants.SERVICE_EXPORTED_CONFIGS, dpref.getProperty("remote.configs.supported"));

		TestService service = new TestService();
		
		ServiceRegistration registration = getContext().registerService(new String[]{A.class.getName(), B.class.getName()},
				                                                        service, dictionary);
		assertNotNull(registration);

		try {
			//
			// register a RemoteServiceAdminListener to receive the export
			// notification
			//
			TestRemoteServiceAdminListener remoteServiceAdminListener = new TestRemoteServiceAdminListener();
			registerService(RemoteServiceAdminListener.class.getName(), remoteServiceAdminListener, null);

			//
			// 122.4.1 export the service
			//
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("mykey", "has been overridden");
			properties.put(RemoteConstants.SERVICE_INTENTS, "my_intent_is_for_this_to_work");
			properties.put("objectClass", "can.not.be.changed.Class");
			properties.put("service.id", "can.not.be.changed.Id");
			properties.put(RemoteConstants.SERVICE_EXPORTED_CONFIGS, new String[]{supportedConfigTypes[0]});

			Collection<ExportRegistration> exportRegistrations = remoteServiceAdmin.exportService(registration.getReference(), properties);
			assertNotNull(exportRegistrations);
			assertEquals("only one export registration was expected since only one config type was requested", 1, exportRegistrations.size());
			
			//
			// 122.8 verify that export notification was sent to RemoteServiceAdminListeners
			//
			RemoteServiceAdminEvent event = remoteServiceAdminListener.getNextEvent();
			assertNotNull("no RemoteServiceAdminEvent received", event);
			assertEquals(0, remoteServiceAdminListener.getEventCount());
			assertNotNull("122.10.11: source must not be null", event.getSource());

			ExportReference exportReference  = null;
			
			switch (event.getType()) {
			case RemoteServiceAdminEvent.EXPORT_REGISTRATION:
				assertNull(event.getException());
				assertEquals("122.10.11: event type is wrong", RemoteServiceAdminEvent.EXPORT_REGISTRATION, event.getType());

				exportReference = event.getExportReference();
				assertNotNull("ExportReference expected in event", exportReference);

				assertNotNull(exportReference.getExportedEndpoint());
				break;
			case RemoteServiceAdminEvent.EXPORT_ERROR:
				assertNotNull(event.getException());
				try {
					event.getExportReference();
					fail("IllegalStateException expected");
				} catch (IllegalStateException ie) {}
				return;
			default:
				fail("wrong event type");
			}

			
			// extract configuration type and configuration properties
			ArrayList<String> configproperties = new ArrayList<String>();

			EndpointDescription epd = exportReference.getExportedEndpoint();
			List<String> configtypes = epd.getConfigurationTypes();
			assertNotNull(configtypes);
			for (Iterator<String> it = configtypes.iterator(); it.hasNext();) {
				String configtype = it.next();

				// check for config type properties
				assertNotNull(configtype);
				assertEquals("service was exported with different config type than requested", supportedConfigTypes[0], configtype);

				for (Iterator<String> keys = epd.getProperties().keySet().iterator(); keys.hasNext(); ) {
					String key = keys.next();

					if (key.startsWith(configtype + ".")) {
						configproperties.add(key);
					}
				}
				assertFalse("122.4: config type properties missing from EndpointDescription", configproperties.isEmpty());
			}

			// unexport the service
			registration.unregister();

			event = remoteServiceAdminListener.getNextEvent();
			assertNotNull("no RemoteServiceAdminEvent received", event);
			assertNotNull("122.10.11: source must not be null", event.getSource());
			assertNull(event.getException());
			assertEquals("122.10.11: event type is wrong", RemoteServiceAdminEvent.EXPORT_UNREGISTRATION, event.getType());

			exportReference = event.getExportReference();
			assertNotNull("ExportReference expected in event", exportReference);

			assertNull(exportReference.getExportedEndpoint());
			assertEquals(0, remoteServiceAdminListener.getEventCount());

			// register the service again
			dictionary.remove(RemoteConstants.SERVICE_EXPORTED_CONFIGS);
			registration = getContext().registerService(new String[]{A.class.getName(), B.class.getName()},
					service, dictionary);

			// manipulate the config property
			// by putting garbage in them
			for (Iterator<String> it = configproperties.iterator(); it.hasNext();) {
				properties.put(it.next(), new TestService());
			}

			// export the service again
			exportRegistrations = remoteServiceAdmin.exportService(registration.getReference(), properties);
			assertNotNull(exportRegistrations);
			assertEquals("only one export registration was expected since only one config type was requested", 1, exportRegistrations.size());

			// expect ERROR event
			event = remoteServiceAdminListener.getNextEvent();
			assertNotNull("no RemoteServiceAdminEvent received", event);
			assertEquals(0, remoteServiceAdminListener.getEventCount());
			assertNotNull("122.10.11: source must not be null", event.getSource());
			assertEquals(RemoteServiceAdminEvent.EXPORT_ERROR, event.getType());
			assertNotNull(event.getException());
			try {
				event.getExportReference();
				fail("IllegalStateException expected");
			} catch (IllegalStateException ie) {}
			

			// ungetting the RSA service will also close the ExportRegistration and therefore
			// emit an event
			ungetService(remoteServiceAdmin);
		} finally {
			registration.unregister();
		}
	}

	/**
	 * @param property
	 * @return
	 */
	private String[] getConfigTypes(Object property) {
		if (property instanceof String) {
			return new String[]{(String) property};
		} else if (property instanceof String[]) {
			return (String[]) property;
		} else if (property instanceof Collection<?>) {
			return ((Collection<String>)property).toArray(new String[]{});
		}
		return new String[]{};
	}

	class TestService implements A, B, Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

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
		
	}

	/**
	 * RemoteServiceAdminListener implementation, which collects and
	 * returns the received events in order.
	 * 
	 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
	 *
	 */
	class TestRemoteServiceAdminListener implements RemoteServiceAdminListener {
		private LinkedList<RemoteServiceAdminEvent> eventlist = new LinkedList<RemoteServiceAdminEvent>();
		private Semaphore sem = new Semaphore(0);

		/**
		 * @see org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener#remoteAdminEvent(org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent)
		 */
		public void remoteAdminEvent(final RemoteServiceAdminEvent event) {
			eventlist.add(event);
			sem.signal();
		}
		
		RemoteServiceAdminEvent getNextEvent() {
			try {
				sem.waitForSignal(60000); // wait max 1min for async notification
			} catch (InterruptedException e1) {
				return null;
			}
			
			try {
				return eventlist.removeFirst();
			} catch (NoSuchElementException e) {
				return null;
			}
		}
		
		int getEventCount() {
			return eventlist.size();
		}
	}
	
	class TestEventHandler implements EventHandler {
		private LinkedList<Event> eventlist = new LinkedList<Event>();
		private Semaphore sem = new Semaphore(0);


		/**
		 * @see org.osgi.service.event.EventHandler#handleEvent(org.osgi.service.event.Event)
		 */
		public void handleEvent(Event event) {
			eventlist.add(event);
			sem.signal();
		}
		
		Event getNextEvent() {
			try {
				sem.waitForSignal(60000); // wait max 1min for async notification
			} catch (InterruptedException e1) {
				return null;
			}
			
			try {
				return eventlist.removeFirst();
			} catch (NoSuchElementException e) {
				return null;
			}
		}
		
		int getEventCount() {
			return eventlist.size();
		}
	}
}

/*
 * Copyright (c) OSGi Alliance (2008, 2009). All Rights Reserved.
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import junit.framework.Assert;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.ImportReference;
import org.osgi.service.remoteserviceadmin.ImportRegistration;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;
import org.osgi.test.cases.remoteserviceadmin.common.A;
import org.osgi.test.cases.remoteserviceadmin.common.B;
import org.osgi.test.support.compatibility.Semaphore;

/**
 * Use RSA service to register a service in a child framework and then import
 * the same service on the parent framework. This test does not explicitly use
 * the discovery, but manually conveys the endpoint information.
 * 
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 * @version 1.0.0
 */
public class RemoteServiceAdminTest extends MultiFrameworkTestCase {
	private static final String	SYSTEM_PACKAGES_EXTRA	= "org.osgi.test.cases.remoteserviceadmin.system.packages.extra";

	/**
	 * @see org.osgi.test.cases.remoteserviceadmin.junit.MultiFrameworkTestCase#getConfiguration()
	 */
	public Map<String, String> getConfiguration() {
		Map<String, String> configuration = new HashMap<String, String>();
		configuration.put(Constants.FRAMEWORK_STORAGE_CLEAN, "true");
		
		//make sure that the server framework System Bundle exports the interfaces
		String systemPackagesXtra = System.getProperty(SYSTEM_PACKAGES_EXTRA);
        configuration.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, systemPackagesXtra);
        configuration.put("osgi.console", "1112");
		return configuration;
	}

	/**
	 * Sets up a child framework in which a service is exported. In the parent framework the
	 * EndpointDescription is passed to the RSA service to import the service from the child
	 * framework. This manual step bypasses Discovery, which would normally do the transport
	 * between the two frameworks.
	 */
	public void testExportImportManually() throws Exception {
		verifyFramework();
		
		//
		// install test bundle in child framework
		//
		BundleContext childContext = getFramework().getBundleContext();
		
		Bundle tb2Bundle = installBundle(childContext, "/tb2.jar");
		assertNotNull(tb2Bundle);
		
		// start test bundle in child framework
		// this will run the test in the child framework and fail
		tb2Bundle.start();
		
		//
		// find the RSA in the parent framework and import the
		// service
		//
		ServiceReference rsaRef = getContext().getServiceReference(RemoteServiceAdmin.class.getName());
		Assert.assertNotNull(rsaRef);
		RemoteServiceAdmin rsa = (RemoteServiceAdmin) getContext().getService(rsaRef);
		Assert.assertNotNull(rsa);
		
		// reconstruct the endpoint description
		EndpointDescription endpoint = reconstructEndpoint();
		// gather all the service and exporting intents
		List<String> endpointIntents = endpoint.getIntents();
		assertNotNull(endpointIntents);
		assertFalse(endpointIntents.isEmpty());
		
		//
		// 122.4.2: Importing
		// test an unsupported config type
		//
		Map<String, Object> map = new HashMap<String, Object>(endpoint.getProperties());
		map.put(RemoteConstants.SERVICE_IMPORTED_CONFIGS, "guaranteed_unsupported_" + System.currentTimeMillis());
		EndpointDescription clone = new EndpointDescription(map);
		assertNull("122.4.2: with no supported config type no service may be imported", rsa.importService(clone));

		//
		// 122.4.8: Events
		// add a RemoteServiceAdminLister and an EventHandler and check for import events

		//
		// register a RemoteServiceAdminListener to receive the import
		// notification
		//
		TestRemoteServiceAdminListener remoteServiceAdminListener = new TestRemoteServiceAdminListener();
		registerService(RemoteServiceAdminListener.class.getName(), remoteServiceAdminListener, null);

		//
		// register a EventHandler to receive the import
		// notification
		//
		Hashtable<String, Object> props = new Hashtable<String, Object>();
		props.put(EventConstants.EVENT_TOPIC, new String[]{
				"org/osgi/service/remoteserviceadmin/IMPORT_REGISTRATION",
				"org/osgi/service/remoteserviceadmin/IMPORT_UNREGISTRATION",
				"org/osgi/service/remoteserviceadmin/IMPORT_ERROR"});
		TestEventHandler eventHandler = new TestEventHandler();
		registerService(EventHandler.class.getName(), eventHandler, props);

		//
		// 122.4.2: Importing
		// positive test: import the service
		//
		ImportRegistration importReg = rsa.importService(endpoint);
		assertNotNull(importReg);
		assertNull(importReg.getException());
		
		ImportReference importRef = importReg.getImportReference();
		assertNotNull(importRef);
		ServiceReference sref = importRef.getImportedService();
		assertNotNull(sref);
		assertEquals("has been overridden", sref.getProperty("mykey"));
		assertNotNull("122.4.2: the service.imported property has to be set", sref.getProperty(RemoteConstants.SERVICE_IMPORTED));
		assertNotNull(sref.getProperty(RemoteConstants.SERVICE_IMPORTED_CONFIGS));
		List<String> intents = getPropertyAsList(sref.getProperty(RemoteConstants.SERVICE_INTENTS));
		assertNotNull("122.4.2: service reference has to have service.intents property set", intents);
		assertTrue("122.4.2: imported service.intents has to include all service.intents of exported service", intents.containsAll(endpointIntents));
		
		// check for event notifications
		//
		// 122.8 verify that import notification was sent to RemoteServiceAdminListeners
		//
		RemoteServiceAdminEvent rsaevent = remoteServiceAdminListener.getNextEvent();
		assertEquals(0, remoteServiceAdminListener.getEventCount());
		assertNotNull("no RemoteServiceAdminEvent received", rsaevent);
		assertNotNull("122.10.11: source must not be null", rsaevent.getSource());

		ImportReference importReference = null;
		EndpointDescription importedED = null;

		//
		// check received event type whether import was successful or not
		boolean errorImporting = false;
		int eventType = rsaevent.getType();
		if (RemoteServiceAdminEvent.IMPORT_ERROR == eventType) {
			errorImporting = true;
			assertNotNull(rsaevent.getException());
			try {
				rsaevent.getImportReference();
				fail("122.4.5: getImportReference must throw IllegalStateException");
			} catch (IllegalStateException ie) {
			}
		} else if (RemoteServiceAdminEvent.IMPORT_REGISTRATION == eventType) {
			assertNull(rsaevent.getException());
			importReference = rsaevent.getImportReference();
			assertNotNull("ImportReference expected in event", importReference);
			importedED = importReference.getImportedEndpoint();
			assertNotNull(importedED);
			// compare endpoints
			assertTrue(endpoint.isSameService(importedED));
		} else {
			fail("122.10.11: event type is wrong");
		}
		

		//
		// 122.8.1 verify that import notification was sent to EventHandler
		//
		Event event = eventHandler.getNextEvent();
		assertNotNull("no Event received", event);
		assertEquals(0, eventHandler.getEventCount());
		
		assertEquals(sref.getBundle(), event.getProperty("bundle"));
		assertEquals(sref.getBundle().getSymbolicName(), event.getProperty("bundle.symbolicname"));
		rsaevent = (RemoteServiceAdminEvent) event.getProperty("event");
		assertNotNull(rsaevent);
		assertEquals(sref.getBundle().getBundleId(), event.getProperty("bundle.id"));
		assertEquals(sref.getBundle().getVersion(), event.getProperty("bundle.version"));
		assertNotNull(event.getProperty("timestamp"));

		// check event type
		String topic = event.getTopic();
		assertTrue("org/osgi/service/remoteserviceadmin/IMPORT_REGISTRATION".equals(topic) ||
				"org/osgi/service/remoteserviceadmin/IMPORT_ERROR".equals(topic));
		if ("org/osgi/service/remoteserviceadmin/IMPORT_ERROR".equals(topic)) {
			assertNotNull("no cause in event", event.getProperty("cause"));
			assertEquals(RemoteServiceAdminEvent.IMPORT_ERROR, rsaevent.getType());
		} else if ("org/osgi/service/remoteserviceadmin/IMPORT_REGISTRATION".equals(topic)) {
			assertEquals(importedED, event.getProperty("import.registration"));
			assertNull("cause in event", event.getProperty("cause"));
			assertEquals(RemoteServiceAdminEvent.IMPORT_REGISTRATION, rsaevent.getType());
		} else {
			fail("122.8.1: wrong event topic");
		}

		//
		// 122.4.2: Importing
		// import the EndpointDescription a second time
		// ensure that it is pointing to the same EndpointDescrition/proxy combination
		// but returning a new ImportRegistration instance
		ImportRegistration importReg2 = rsa.importService(endpoint);
		assertNotNull(importReg2);
		assertNull(importReg2.getException());
		assertNotSame("122.4.2: RSA has to create a new ImportRegistration on importService", importReg, importReg2);
		
		// check for events on second import
		rsaevent = remoteServiceAdminListener.getNextEvent();
		assertNotNull("no RemoteServiceAdminEvent received", rsaevent);
		assertEquals(0, remoteServiceAdminListener.getEventCount());
		assertNotNull("122.10.11: source must not be null", rsaevent.getSource());
		
		int type = rsaevent.getType();
		if (RemoteServiceAdminEvent.IMPORT_REGISTRATION == type) {
			assertNull(rsaevent.getException());
			ImportReference importRef2 = importReg.getImportReference();
			assertNotNull(importRef2);
			ServiceReference sref2 = importRef.getImportedService();
			assertNotNull(sref2);
			assertSame("122.4.2: ImportRegistration has to point to the same proxy service", sref, sref2);
		} else if (RemoteServiceAdminEvent.IMPORT_ERROR == type) {
			assertNotNull(rsaevent.getException());
		} else {
			fail("122.10.11: event type is wrong");
		}

		event = eventHandler.getNextEvent();
		assertNotNull("no Event received", event);
		assertEquals(0, eventHandler.getEventCount());
		topic = event.getTopic();
		if ("org/osgi/service/remoteserviceadmin/IMPORT_REGISTRATION".equals(topic)) {
			assertNull("cause in event", event.getProperty("cause"));
		} else if ("org/osgi/service/remoteserviceadmin/IMPORT_ERROR".equals(topic)) {
			assertNotNull("no cause in event", event.getProperty("cause"));
		} else {
			fail("122.4.8: wrong event topic");
		}
		
		A serviceA = null;
		
		//
		// invoke the service
		//
		if (!errorImporting) {
			serviceA = (A) getService(A.class);
			assertNotNull(serviceA);
			assertEquals("this is A", serviceA.getA());

			ungetService(serviceA);
		}
		
		//
		// test remove
		//
		tb2Bundle.stop();
		
		//
		// 122.4.2: Importing
		// there are 2 ImportRegistrations now open. Ensure that the proxy
		// is still available after closing only one of them
		importReg2.close();
		importReg2.close(); // calling this multiple times must not cause a problem
		
		if (!errorImporting) {
			serviceA = (A) getService(A.class);
			assertNotNull(serviceA);
			ungetService(serviceA);
			
			//
			// 122.4.8 on close of the ImportRegistration expect another event
			//
			rsaevent = remoteServiceAdminListener.getNextEvent();
			assertNotNull("no RemoteServiceAdminEvent received", rsaevent);
			assertNotNull("122.10.11: source must not be null", rsaevent.getSource());
			assertNull(rsaevent.getException());
			assertEquals("122.10.11: event type is wrong", RemoteServiceAdminEvent.IMPORT_UNREGISTRATION, rsaevent.getType());

			importReference = rsaevent.getImportReference();
			assertNotNull("ImportReference expected in event", importReference);

			assertNull(importReference.getImportedEndpoint());
			
			event = eventHandler.getNextEvent();
			assertNotNull("no Event received", event);
			assertEquals(0, eventHandler.getEventCount());
			topic = event.getTopic();
			if ("org/osgi/service/remoteserviceadmin/IMPORT_UNREGISTRATION".equals(topic)) {
				assertNull("cause in event", event.getProperty("cause"));
			} else {
				fail("122.4.8: wrong event topic");
			}
		}

		//
		// now close the last one, this should remove the proxy service
		importReg.close();
		importReg.close(); // calling this multiple times must not cause a problem
		
		if (!errorImporting) {
			serviceA = (A) getContext().getService(sref);
			assertNull("122.4.2: the last ImportRegistration has been closed, but proxy is still available", serviceA);
			assertFalse("122.4.2: the last ImportRegistration has been closed, but RSA still lists it", rsa.getImportedEndpoints().contains(importRef));

			//
			// 122.4.8 on close of the ImportRegistration expect another event
			//
			rsaevent = remoteServiceAdminListener.getNextEvent();
			assertNotNull("no RemoteServiceAdminEvent received", rsaevent);
			assertNotNull("122.10.11: source must not be null", rsaevent.getSource());
			assertNull(rsaevent.getException());
			assertEquals("122.10.11: event type is wrong", RemoteServiceAdminEvent.IMPORT_UNREGISTRATION, rsaevent.getType());

			importReference = rsaevent.getImportReference();
			assertNotNull("ImportReference expected in event", importReference);

			assertNull(importReference.getImportedEndpoint());
			
			event = eventHandler.getNextEvent();
			assertNotNull("no Event received", event);
			assertEquals(0, eventHandler.getEventCount());
			topic = event.getTopic();
			if ("org/osgi/service/remoteserviceadmin/IMPORT_UNREGISTRATION".equals(topic)) {
				assertNull("cause in event", event.getProperty("cause"));
			} else {
				fail("122.4.8: wrong event topic");
			}
		}
		// Marc Schaaf: Make sure the service instance of the RSA can be closed by the RSA Service Factory
		getContext().ungetService(rsaRef);
	}

	/**
	 * @param property Object
	 * @return List<String> of content of the property
	 */
	private List<String> getPropertyAsList(Object property) throws Exception {
		if (property instanceof List) {
			return (List<String>) property;
		} else if (property instanceof String[]) {
			return Arrays.asList((String[])property);
		} else if (property instanceof String) {
			ArrayList<String> list = new ArrayList<String>();
			StringTokenizer st = new StringTokenizer((String) property, " ,");
			while (st.hasMoreTokens()) {
				list.add(st.nextToken());
			}
			return list;
		} else {
			throw new Exception("unsupported property type");
		}
	}

	/**
	 * @return
	 * @throws IOException 
	 */
	private EndpointDescription reconstructEndpoint() throws IOException {
		String propstr = System.getProperty("RSA_TCK.EndpointDescription_0");
		
		// see org.osgi.test.cases.remoteserviceadmin.tb2.Activator#exportEndpointDescription()
		// decode byte[] from hex
		byte[] ba = new byte[propstr.length()/2];
		
		for (int x=0; x < ba.length; ++x) {
            int sp = x*2;
            int a = Integer.parseInt(""+propstr.charAt(sp),16);
            int b = Integer.parseInt(""+propstr.charAt(sp+1),16);
            ba[x] = (byte)(a*16 + b);
		}
		
		ByteArrayInputStream bis = new ByteArrayInputStream(ba);
		ObjectInputStream ois = new ObjectInputStream(bis);
		
		Map<String,Object> props = null;
		try {
			props = (Map<String, Object>)ois.readObject();
		} catch (ClassNotFoundException e) {e.printStackTrace();}
		
		assert(props!=null);
		
		return new EndpointDescription(props);
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

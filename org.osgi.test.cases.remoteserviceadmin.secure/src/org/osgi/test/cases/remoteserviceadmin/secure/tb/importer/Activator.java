/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.test.cases.remoteserviceadmin.secure.tb.importer;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.ImportReference;
import org.osgi.service.remoteserviceadmin.ImportRegistration;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;
import org.osgi.test.cases.remoteserviceadmin.secure.common.A;
import org.osgi.test.support.OSGiTestCaseProperties;

/**
 * This class imports an endpoint.
 * 
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 * @version 1.0.0
 */
public class Activator implements BundleActivator {
	BundleContext                  context;
	RemoteServiceAdmin             rsa;
	long timeout;

	ServiceReference<RemoteServiceAdmin>	rsaRef;
	ImportRegistration importReg;

	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		this.context = context;
		timeout = OSGiTestCaseProperties.getLongProperty("rsa.tck.timeout",
				300000L);
		test();
	}

	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		teststop();
	}

	public void test() throws Exception {
		// lookup RemoteServiceAdmin service 
		rsaRef = context
				.getServiceReference(RemoteServiceAdmin.class);
		assertNotNull(rsaRef);
		rsa = context.getService(rsaRef);
		assertNotNull(rsa);
		
		//
		// register a RemoteServiceAdminListener to receive the export
		// notification
		//
		TestRemoteServiceAdminListener remoteServiceAdminListener = new TestRemoteServiceAdminListener();
		ServiceRegistration<RemoteServiceAdminListener> sr = context
				.registerService(RemoteServiceAdminListener.class,
						remoteServiceAdminListener, null);
		assertNotNull(sr);

		try {
			// reconstruct the endpoint description
			EndpointDescription endpoint = reconstructEndpoint();
			// gather all the service and exporting intents
			List<String> endpointIntents = endpoint.getIntents();
			assertNotNull(endpointIntents);
			assertFalse(endpointIntents.isEmpty());

			//
			// 122.4.2: Importing
			// positive test: import the service
			//
			importReg = rsa.importService(endpoint);
			assertNotNull(importReg);
			assertNull(importReg.getException());

			ImportReference importRef = importReg.getImportReference();
			assertNotNull(importRef);
			ServiceReference< ? > sref = importRef.getImportedService();
			assertNotNull(sref);
			assertEquals("has been overridden", sref.getProperty("mykey"));
			assertNotNull("122.4.2: the service.imported property has to be set", sref.getProperty(RemoteConstants.SERVICE_IMPORTED));
			assertNotNull(sref.getProperty(RemoteConstants.SERVICE_IMPORTED_CONFIGS));
			List<String> intents = getPropertyAsList(sref.getProperty(RemoteConstants.SERVICE_INTENTS));
			assertNotNull("122.4.2: service reference has to have service.intents property set", intents);
			assertTrue("122.4.2: imported service.intents has to include all service.intents of exported service", intents.containsAll(endpointIntents));

			// validate EndpointDescription
			EndpointDescription description = importRef.getImportedEndpoint();
			assertNotNull(description);
			assertEquals("has been overridden", description.getProperties().get("mykey"));
			try {
				new EndpointDescription(sref, description.getProperties()); // this throws an exception if
			} catch (IllegalArgumentException ie) {
				fail("invalid endpoint description returned from imported service");
			}
			assertNotNull(description.getProperties().get(RemoteConstants.SERVICE_IMPORTED));
			assertTrue(description.getInterfaces().size() == 1);
			assertTrue(description.getInterfaces().contains(A.class.getName()));

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
			assertEquals(RemoteServiceAdminEvent.IMPORT_REGISTRATION, rsaevent.getType());
			assertNull(rsaevent.getException());
			importReference = rsaevent.getImportReference();
			assertNotNull("ImportReference expected in event", importReference);
			importedED = importReference.getImportedEndpoint();
			assertNotNull(importedED);
			// compare endpoints
			assertTrue(endpoint.isSameService(importedED));

			// invoke service
			A serviceA = null;

			//
			// invoke the service
			//
			// no version
			ServiceReference<A> ref = context.getServiceReference(A.class);
			assertNotNull(ref);
			serviceA = context.getService(ref);
			try {
				assertNotNull(serviceA);
				assertEquals("this is A", serviceA.getA());
			} finally {
				context.ungetService(ref);
			}

		} finally {
			sr.unregister();
		}
	}

	/**
	 * 122.4.1 Exporting Once the ExportRegistration is closed, a notification
	 * is expected to be sent to the RemoteServiceAdminListener that the service
	 * is no longer exported.
	 */
	private void teststop() throws Exception {

		TestRemoteServiceAdminListener remoteServiceAdminListener = new TestRemoteServiceAdminListener();
		ServiceRegistration<RemoteServiceAdminListener> sr = context
				.registerService(RemoteServiceAdminListener.class,
				remoteServiceAdminListener, null);
		assertNotNull(sr);
		try {
			importReg.close();
			importReg.close(); // calling this multiple times must not cause a
								// problem

			//
			// 122.4.8 on close of the ImportRegistration expect another event
			//
			RemoteServiceAdminEvent rsaevent = remoteServiceAdminListener
					.getNextEvent();
			assertNotNull("no RemoteServiceAdminEvent received",
					rsaevent);
			assertNotNull("122.10.11: source must not be null",
					rsaevent.getSource());
			assertNull(rsaevent.getException());
			assertEquals("122.10.11: event type is wrong",
					RemoteServiceAdminEvent.IMPORT_UNREGISTRATION,
					rsaevent.getType());

			ImportReference importReference = rsaevent.getImportReference();
			assertNotNull("ImportReference expected in event",
					importReference);

			assertNull(importReference.getImportedEndpoint());

		} finally {
			sr.unregister();
			context.ungetService(rsaRef);
		}
	}

	/**
	 * @return EndpointDescription reconstructed from a HEX string passed
	 *         by the exporting bundle in the child framework
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	private EndpointDescription reconstructEndpoint() throws IOException {
		String propstr = context.getProperty("RSA_TCK.EndpointDescription_0");
		
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
		
		assertTrue(props!=null);
		
		return new EndpointDescription(props);
	}

	/**
	 * @param property Object
	 * @return List<String> of content of the property
	 */
	@SuppressWarnings("unchecked")
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
		@Override
		public void remoteAdminEvent(final RemoteServiceAdminEvent event) {
			eventlist.add(event);
			sem.release();
		}
		
		RemoteServiceAdminEvent getNextEvent() {
			try {
				sem.tryAcquire(timeout, TimeUnit.MILLISECONDS);
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

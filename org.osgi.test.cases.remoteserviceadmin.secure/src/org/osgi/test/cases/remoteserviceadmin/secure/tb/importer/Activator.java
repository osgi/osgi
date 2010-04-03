/*
 * Copyright (c) OSGi Alliance (2010). All Rights Reserved.
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
package org.osgi.test.cases.remoteserviceadmin.secure.tb.importer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import junit.framework.Assert;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.ExportReference;
import org.osgi.service.remoteserviceadmin.ExportRegistration;
import org.osgi.service.remoteserviceadmin.ImportReference;
import org.osgi.service.remoteserviceadmin.ImportRegistration;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;
import org.osgi.test.cases.remoteserviceadmin.secure.common.A;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.compatibility.Semaphore;

/**
 * This class imports an endpoint.
 * 
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 * @version 1.0.0
 */
public class Activator implements BundleActivator {
	ServiceRegistration            registration;
	BundleContext                  context;
	RemoteServiceAdmin             rsa;
	TestRemoteServiceAdminListener remoteServiceAdminListener;
	long timeout;
	int  factor;

	public Activator() {
		timeout = Long.getLong("rsa.ct.timeout", 300000L);
	}

	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		this.context = context;
		test();
	}

	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		teststop();
	}

	public void test() throws Exception {
		// lookup RemoteServiceAdmin service 
		ServiceReference rsaRef = context.getServiceReference(RemoteServiceAdmin.class.getName());
		Assert.assertNotNull(rsaRef);
		rsa = (RemoteServiceAdmin) context.getService(rsaRef);
		Assert.assertNotNull(rsa);
		
		//
		// register a RemoteServiceAdminListener to receive the export
		// notification
		//
		remoteServiceAdminListener = new TestRemoteServiceAdminListener();
		ServiceRegistration sr = context.registerService(RemoteServiceAdminListener.class.getName(), remoteServiceAdminListener, null);
		Assert.assertNotNull(sr);

		try {
			// reconstruct the endpoint description
			EndpointDescription endpoint = reconstructEndpoint();
			// gather all the service and exporting intents
			List<String> endpointIntents = endpoint.getIntents();
			Assert.assertNotNull(endpointIntents);
			Assert.assertFalse(endpointIntents.isEmpty());

			//
			// 122.4.8: Events
			// add a RemoteServiceAdminLister and an EventHandler and check for import events

			//
			// register a RemoteServiceAdminListener to receive the import
			// notification
			//
			TestRemoteServiceAdminListener remoteServiceAdminListener = new TestRemoteServiceAdminListener();
			context.registerService(RemoteServiceAdminListener.class.getName(), remoteServiceAdminListener, null);

			//
			// 122.4.2: Importing
			// positive test: import the service
			//
			ImportRegistration importReg = rsa.importService(endpoint);
			Assert.assertNotNull(importReg);
			Assert.assertNull(importReg.getException());

			ImportReference importRef = importReg.getImportReference();
			Assert.assertNotNull(importRef);
			ServiceReference sref = importRef.getImportedService();
			Assert.assertNotNull(sref);
			Assert.assertEquals("has been overridden", sref.getProperty("mykey"));
			Assert.assertNotNull("122.4.2: the service.imported property has to be set", sref.getProperty(RemoteConstants.SERVICE_IMPORTED));
			Assert.assertNotNull(sref.getProperty(RemoteConstants.SERVICE_IMPORTED_CONFIGS));
			List<String> intents = getPropertyAsList(sref.getProperty(RemoteConstants.SERVICE_INTENTS));
			Assert.assertNotNull("122.4.2: service reference has to have service.intents property set", intents);
			Assert.assertTrue("122.4.2: imported service.intents has to include all service.intents of exported service", intents.containsAll(endpointIntents));

			// validate EndpointDescription
			EndpointDescription description = importRef.getImportedEndpoint();
			Assert.assertNotNull(description);
			Assert.assertEquals("has been overridden", description.getProperties().get("mykey"));
			try {
				new EndpointDescription(sref, description.getProperties()); // this throws an exception if
			} catch (IllegalArgumentException ie) {
				Assert.fail("invalid endpoint description returned from imported service");
			}
			Assert.assertNotNull(description.getProperties().get(RemoteConstants.SERVICE_IMPORTED));
			Assert.assertTrue(description.getInterfaces().size() == 1);
			Assert.assertTrue(description.getInterfaces().contains(A.class.getName()));

			// check for event notifications
			//
			// 122.8 verify that import notification was sent to RemoteServiceAdminListeners
			//
			RemoteServiceAdminEvent rsaevent = remoteServiceAdminListener.getNextEvent();
			Assert.assertEquals(0, remoteServiceAdminListener.getEventCount());
			Assert.assertNotNull("no RemoteServiceAdminEvent received", rsaevent);
			Assert.assertNotNull("122.10.11: source must not be null", rsaevent.getSource());

			ImportReference importReference = null;
			EndpointDescription importedED = null;

			//
			// check received event type whether import was successful or not
			Assert.assertEquals(RemoteServiceAdminEvent.IMPORT_REGISTRATION, rsaevent.getType());
			Assert.assertNull(rsaevent.getException());
			importReference = rsaevent.getImportReference();
			Assert.assertNotNull("ImportReference expected in event", importReference);
			importedED = importReference.getImportedEndpoint();
			Assert.assertNotNull(importedED);
			// compare endpoints
			Assert.assertTrue(endpoint.isSameService(importedED));

			// invoke service
			A serviceA = null;

			//
			// invoke the service
			//
			// no version
			ServiceReference ref = context.getServiceReference(A.class.getName());
			Assert.assertNotNull(ref);
			serviceA = (A) context.getService(ref);
			try {
				Assert.assertNotNull(serviceA);
				Assert.assertEquals("this is A", serviceA.getA());
			} finally {
				context.ungetService(ref);
			}

			importReg.close();
			importReg.close(); // calling this multiple times must not cause a problem

			ref = context.getServiceReference(A.class.getName());
			serviceA = (A) context.getService(ref);
			Assert.assertNull("122.4.2: the last ImportRegistration has been closed, but proxy is still available", serviceA);
			Assert.assertFalse("122.4.2: the last ImportRegistration has been closed, but RSA still lists it", rsa.getImportedEndpoints().contains(importRef));

			//
			// 122.4.8 on close of the ImportRegistration expect another event
			//
			rsaevent = remoteServiceAdminListener.getNextEvent();
			Assert.assertNotNull("no RemoteServiceAdminEvent received", rsaevent);
			Assert.assertNotNull("122.10.11: source must not be null", rsaevent.getSource());
			Assert.assertNull(rsaevent.getException());
			Assert.assertEquals("122.10.11: event type is wrong", RemoteServiceAdminEvent.IMPORT_UNREGISTRATION, rsaevent.getType());

			importReference = rsaevent.getImportReference();
			Assert.assertNotNull("ImportReference expected in event", importReference);

			Assert.assertNull(importReference.getImportedEndpoint());

		} finally {
			// Make sure the service instance of the RSA can be closed by the RSA Service Factory
			context.ungetService(rsaRef);
		}
	}

	/**
	 * 122.4.1 Exporting
	 * Once the ExportRegistration is closed, a notification is expected to be sent
	 * to the RemoteServiceAdminListener that the service is no longer exported.
	 */
	private void teststop() throws Exception {
	}

	/**
	 * @return EndpointDescription reconstructed from a HEX string passed
	 *         by the exporting bundle in the child framework
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
		
		Assert.assertTrue(props!=null);
		
		return new EndpointDescription(props);
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
				sem.waitForSignal(timeout);
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

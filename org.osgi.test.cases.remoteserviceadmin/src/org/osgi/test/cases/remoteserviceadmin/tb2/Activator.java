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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;

import junit.framework.Assert;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
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
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.compatibility.Semaphore;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 * @version 1.0.0
 */
public class Activator implements BundleActivator, A, B {
	/** 
	 * Magic value. Properties with this value will be replaced by a socket port number that is currently free. 
	 */
    private static final String FREE_PORT = "@@FREE_PORT@@";
    
	ServiceRegistration            registration;
	BundleContext                  context;
	RemoteServiceAdmin             rsa;
	Collection<ExportRegistration> exportRegistrations;
	TestRemoteServiceAdminListener remoteServiceAdminListener;
	long timeout;
	int  factor;

	public Activator() {
		timeout = Long.getLong("rsa.ct.timeout", 300000L);
		factor = Integer.getInteger("rsa.ct.timeout.factor", 3);
	}

	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		this.context = context;
		
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
		dictionary.put("myfloat", (float)3.1415f);
		dictionary.put("mydouble", (double)-3.1415d);
		dictionary.put("mychar", (char)'t');
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
		ServiceReference rsaRef = context.getServiceReference(RemoteServiceAdmin.class.getName());
		Assert.assertNotNull(rsaRef);
		rsa = (RemoteServiceAdmin) context.getService(rsaRef);
		Assert.assertNotNull(rsa);
		
		try {
			//
			// register a RemoteServiceAdminListener to receive the export
			// notification
			//
			remoteServiceAdminListener = new TestRemoteServiceAdminListener();
			ServiceRegistration sr = context.registerService(RemoteServiceAdminListener.class.getName(), remoteServiceAdminListener, null);
			Assert.assertNotNull(sr);

			//
			// 122.4.1 export the service, positive tests
			//
			// load the external properties file with the config types for the server side service
			Map<String, Object> properties = loadServerTCKProperties();
			properties.put("mykey", "has been overridden");
			properties.put("objectClass", "can.not.be.changed.Class");
			properties.put("service.id", "can.not.be.changed.Id");
			processFreePortProperties(properties);

			// export the service
			exportRegistrations = rsa.exportService(registration.getReference(), properties);
			Assert.assertNotNull(exportRegistrations);
			Assert.assertFalse(exportRegistrations.isEmpty());

			for (Iterator<ExportRegistration> it = exportRegistrations.iterator(); it.hasNext();) {
				ExportRegistration er = it.next();

				Assert.assertNull(er.getException());
				ExportReference ref = er.getExportReference();
				Assert.assertNotNull(ref);

				// 122.4.1 Exporting
				Assert.assertEquals(registration.getReference(), ref.getExportedService());

				Assert.assertEquals(DefaultTestBundleControl.arrayToString((Object[]) registration.getReference().getProperty("objectClass"), true),
						DefaultTestBundleControl.arrayToString((Object[]) ref.getExportedService().getProperty("objectClass"), true));
				Assert.assertEquals(registration.getReference().getProperty("service.id"), ref.getExportedService().getProperty("service.id"));

				EndpointDescription ed = ref.getExportedEndpoint();
				Assert.assertNotNull(ed);
				Assert.assertNotNull(ed.getProperties().get("objectClass"));
				Assert.assertTrue(ed.getInterfaces().contains(A.class.getName()));
				Assert.assertFalse(ed.getInterfaces().contains(B.class.getName()));

				Assert.assertNotNull(ed.getId());
				Assert.assertNotNull(ed.getConfigurationTypes());
				Assert.assertFalse(ed.getConfigurationTypes().isEmpty());
				Assert.assertEquals(context.getProperty("org.osgi.framework.uuid"),
						ed.getFrameworkUUID());
				Assert.assertNotNull(ed.getProperties().get("endpoint.service.id"));

				exportEndpointDescription(ed);
			}

			//
			// 122.4.1 Exporting
			// 122.10.12 verify that export notification was sent to RemoteServiceAdminListeners
			//
			RemoteServiceAdminEvent event = remoteServiceAdminListener.getNextEvent();
			Assert.assertNotNull("no RemoteServiceAdminEvent received", event);
			Assert.assertNotNull("122.10.11: source must not be null", event.getSource());
			Assert.assertNull(event.getException());
			Assert.assertEquals("122.10.11: event type is wrong", RemoteServiceAdminEvent.EXPORT_REGISTRATION, event.getType());

			ExportReference exportReference = event.getExportReference();
			Assert.assertNotNull("ExportReference expected in event", exportReference);
			Assert.assertEquals(registration.getReference(), exportReference.getExportedService());

			EndpointDescription ed = exportReference.getExportedEndpoint();
			Assert.assertNotNull(ed);
			Assert.assertTrue(ed.getInterfaces().contains(A.class.getName()));
			Assert.assertFalse(ed.getInterfaces().contains(B.class.getName()));

			Assert.assertNotNull(ed.getId());
			Assert.assertNotNull(ed.getConfigurationTypes());
			Assert.assertFalse(ed.getConfigurationTypes().isEmpty());
			Assert.assertEquals(context.getProperty("org.osgi.framework.uuid"), ed
					.getFrameworkUUID());

			Assert.assertEquals(0, remoteServiceAdminListener.getEventCount());
		} finally {
			context.ungetService(rsaRef);
		}
	}

	/**
	 * 122.4.1 Exporting
	 * Once the ExportRegistration is closed, a notification is expected to be sent
	 * to the RemoteServiceAdminListener that the service is no longer exported.
	 */
	private void teststop() throws Exception {
		Assert.assertNotNull(exportRegistrations);
		Assert.assertFalse(exportRegistrations.isEmpty());
		
		// close ExportRegistrations
		for (Iterator<ExportRegistration> it = exportRegistrations.iterator(); it.hasNext();) {
			ExportRegistration er = it.next();
			
			Assert.assertNull(er.getException());
			er.close();
		}

		Assert.assertEquals(0, rsa.getExportedServices().size());
		
		//
		// 122.10.12 verify that export notification was sent to RemoteServiceAdminListeners
		//
		RemoteServiceAdminEvent event = remoteServiceAdminListener.getNextEvent();
		Assert.assertNotNull("no RemoteServiceAdminEvent received", event);
		Assert.assertNotNull("122.10.11: source must not be null", event.getSource());
		Assert.assertNull(event.getException());
		Assert.assertEquals("122.10.11: event type is wrong", RemoteServiceAdminEvent.EXPORT_UNREGISTRATION, event.getType());

		ExportReference exportReference = event.getExportReference();
		Assert.assertNotNull("ExportReference expected in event", exportReference);
		
		Assert.assertNull(exportReference.getExportedEndpoint());
		
		Assert.assertEquals(0, remoteServiceAdminListener.getEventCount());
	}

    private void processFreePortProperties(Map<String, Object> properties) {
        String freePort = getFreePort();
        for (Iterator it = properties.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            if (entry.getValue().toString().trim().equals(FREE_PORT)) {
                entry.setValue(freePort);
            }
        }
    }

    private String getFreePort() {
        try {
            ServerSocket ss = new ServerSocket(0);
            return "" + ss.getLocalPort();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
	 * @return
	 */
	private Map<String, Object> loadServerTCKProperties() {
		String serverconfig = System
				.getProperty("org.osgi.test.cases.remoteserviceadmin.serverconfig");
		Assert.assertNotNull(
				"did not find org.osgi.test.cases.remoteserviceadmin.serverconfig system property",
				serverconfig);
		Map<String, Object> properties = new HashMap<String, Object>();
		
		for (StringTokenizer tok = new StringTokenizer(serverconfig, ","); tok
				.hasMoreTokens();) {
			String propertyName = tok.nextToken();
			String value = System.getProperty(propertyName);
			Assert.assertNotNull("system property not found: " + propertyName, value);
			properties.put(propertyName, value);
		}
		
		return properties;
	}

	/**
	 * Write the contents of the EndpointDescription into System properties for the parent framework to
	 * read and then import.
	 * 
	 * @param ed
	 * @throws IOException 
	 */
	private void exportEndpointDescription(EndpointDescription ed) throws IOException {
		// Marc Schaaf: I switched to Java servialization to support String[] and lists as 
		// EndpointDescription Properties. The Byte Array is encoded as a HEX string to save 
		// it as a system property
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
			
		
		Map<String, Object> props = new HashMap<String, Object>();
		for (Iterator<String> it = ed.getProperties().keySet().iterator(); it.hasNext();) {
			String key = it.next();
			props.put(key, ed.getProperties().get(key));
		}

		oos.writeObject(props);

		// encode byte[] as hex 
		byte[] ba = bos.toByteArray();
		String out = "";
		for (int x=0; x < ba.length; ++x) {
			out += Integer.toString( ( ba[x] & 0xff ) + 0x100, 16).substring( 1 );
		}
		
		
		System.getProperties().put("RSA_TCK.EndpointDescription_" + registrationCounter++, out);
		
	}
	
	private int registrationCounter = 0;
	
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

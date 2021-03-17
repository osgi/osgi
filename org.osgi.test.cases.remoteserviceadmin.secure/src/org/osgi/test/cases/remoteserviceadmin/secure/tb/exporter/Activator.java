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
package org.osgi.test.cases.remoteserviceadmin.secure.tb.exporter;

import static junit.framework.TestCase.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.ExportReference;
import org.osgi.service.remoteserviceadmin.ExportRegistration;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;
import org.osgi.test.cases.remoteserviceadmin.secure.common.A;
import org.osgi.test.support.OSGiTestCaseProperties;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 * @version 1.0.0
 */
public class Activator implements BundleActivator, A {
	/**
	 * Magic value. Properties with this value will be replaced by a socket port number that is currently free.
	 */
    private static final String FREE_PORT = "@@FREE_PORT@@";

	ServiceRegistration<A>			registration;
	BundleContext                  context;
	RemoteServiceAdmin             rsa;
	Collection<ExportRegistration> exportRegistrations;
	TestRemoteServiceAdminListener remoteServiceAdminListener;
	long timeout;
	int  factor;

	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		this.context = context;
		timeout = OSGiTestCaseProperties.getLongProperty("rsa.tck.timeout",
				300000L);

		Hashtable<String, String> dictionary = new Hashtable<String, String>();
		dictionary.put("mykey", "will be overridden");
		dictionary.put("myprop", "myvalue");

		registration = context.registerService(A.class, this, dictionary);

		test();
	}

	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		registration.unregister();
		teststop();
	}

	/**
	 * @see org.osgi.test.cases.remoteserviceadmin.secure.common.A#getA()
	 */
	@Override
	public String getA() {
		return "this is A";
	}

	public void test() throws Exception {
		// lookup RemoteServiceAdmin service
		ServiceTracker<RemoteServiceAdmin,RemoteServiceAdmin> rsaTracker = new ServiceTracker<>(
				context, RemoteServiceAdmin.class, null);
		rsaTracker.open();

		rsa = rsaTracker.waitForService(timeout);
		assertNotNull(rsa);

		//
		// register a RemoteServiceAdminListener to receive the export
		// notification
		//
		remoteServiceAdminListener = new TestRemoteServiceAdminListener();
		ServiceRegistration<RemoteServiceAdminListener> sr = context
				.registerService(RemoteServiceAdminListener.class,
						remoteServiceAdminListener, null);
		assertNotNull(sr);

		//
		// 122.4.1 export the service, positive tests
		//
		Map<String, Object> properties = loadCTProperties();
		properties.put("mykey", "has been overridden");
		properties.put("objectClass", "can.not.be.changed.Class");
		properties.put("service.id", "can.not.be.changed.Id");
        properties.put(RemoteConstants.SERVICE_EXPORTED_INTERFACES, A.class.getName());

		// export the service
		exportRegistrations = rsa.exportService(registration.getReference(), properties);
		assertNotNull(exportRegistrations);
		assertFalse(exportRegistrations.isEmpty());

		for (Iterator<ExportRegistration> it = exportRegistrations.iterator(); it.hasNext();) {
			ExportRegistration er = it.next();

			Throwable exception = er.getException();
			if (exception != null) {
				throw new InvocationTargetException(exception, "Export Failed");
			}
			assertNull(exception);
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

			assertNotNull(ed.getId());
			assertNotNull(ed.getConfigurationTypes());
			assertFalse(ed.getConfigurationTypes().isEmpty());
			assertEquals(context.getProperty("org.osgi.framework.uuid"),
					ed.getFrameworkUUID());
			assertNotNull(ed.getProperties().get("endpoint.service.id"));

			exportEndpointDescription(ed);
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

		assertNotNull(ed.getId());
		assertNotNull(ed.getConfigurationTypes());
		assertFalse(ed.getConfigurationTypes().isEmpty());
		assertEquals(context.getProperty("org.osgi.framework.uuid"), ed
				.getFrameworkUUID());

		assertEquals(0, remoteServiceAdminListener.getEventCount());
	}

	/**
	 * 122.4.1 Exporting
	 * Once the ExportRegistration is closed, a notification is expected to be sent
	 * to the RemoteServiceAdminListener that the service is no longer exported.
	 */
	private void teststop() throws Exception {
		assertNotNull(exportRegistrations);
		assertFalse(exportRegistrations.isEmpty());

		// close ExportRegistrations
		for (Iterator<ExportRegistration> it = exportRegistrations.iterator(); it.hasNext();) {
			ExportRegistration er = it.next();

			assertNull(er.getException());
			er.close();
		}

		assertEquals(0, rsa.getExportedServices().size());

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

		assertEquals(0, remoteServiceAdminListener.getEventCount());
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

    /**
     * Substitute the free port placeholder for a free port
     *
     * @param properties
     */
    private void processFreePortProperties(Map<String, Object> properties) {
        String freePort = getFreePort();
		for (Iterator<Entry<String,Object>> it = properties.entrySet()
				.iterator(); it.hasNext();) {
			Entry<String,Object> entry = it.next();
            if (entry.getValue().toString().trim().equals(FREE_PORT)) {
                entry.setValue(freePort);
            }
        }
    }

    /**
     * @return a free socket port
     */
    private String getFreePort() {
        try {
            ServerSocket ss = new ServerSocket(0);
            String port = "" + ss.getLocalPort();
            ss.close();
            return port;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Load the properties from the bnd.bnd file and substitute the free port placeholder.
     *
	 * @return map of properties set in the "org.osgi.test.cases.remoteserviceadmin.serverconfig" property
	 *         in the runoptions in bnd.bnd
	 */
	protected Map<String, Object> loadCTProperties() {
		String serverconfig = context
				.getProperty("org.osgi.test.cases.remoteserviceadmin.secure.serverconfig");
		assertNotNull(
				"did not find org.osgi.test.cases.remoteserviceadmin.secure.serverconfig system property",
				serverconfig);
		Map<String, Object> properties = new HashMap<String, Object>();

		for (StringTokenizer tok = new StringTokenizer(serverconfig, ","); tok
				.hasMoreTokens();) {
			String propertyName = tok.nextToken();
			String value = context.getProperty(propertyName);
			assertNotNull("system property not found: " + propertyName, value);
			properties.put(propertyName, value);
		}

		processFreePortProperties(properties);

		return properties;
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

/*
 * Copyright (c) OSGi Alliance (2008, 2015). All Rights Reserved.
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
package org.osgi.test.cases.remoteserviceadmin.tb1;

import static junit.framework.TestCase.*;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.EndpointListener;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.osgi.test.cases.remoteserviceadmin.common.A;
import org.osgi.test.cases.remoteserviceadmin.common.B;
import org.osgi.test.cases.remoteserviceadmin.common.RemoteServiceConstants;
import org.osgi.test.cases.remoteserviceadmin.common.Utils;
import org.osgi.test.support.compatibility.Semaphore;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 * @deprecated
 */
public class Activator implements BundleActivator, A, B {
	ServiceRegistration registration;
	BundleContext       context;
	EndpointDescription endpoint;

	Semaphore semaphore = new Semaphore();

	ServiceTracker<EndpointListener, EndpointListener> tracker;

	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		this.context = context;
		
		Hashtable<String, String> dictionary = new Hashtable<String, String>();
		dictionary.put("mykey", "will be overridden");
		dictionary.put("myprop", "myvalue");
		dictionary.put(RemoteServiceConstants.SERVICE_EXPORTED_INTERFACES, A.class.getName());

		registration = context.registerService(new String[]{A.class.getName()}, this, dictionary);
		
		test();
	}

	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		registration.unregister();
		
		stoptest();

		if (tracker != null)
			tracker.close();
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
	
	private void test() throws Exception {
		//
		// create an EndpointDescription
		//
		Long endpointID = new Long(12345);
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("mykey", "has been overridden");
//		properties.put(Constants.OBJECTCLASS, new String [] {A.class.getName()}); // needed? no, already in servicereference
		properties.put(RemoteConstants.SERVICE_IMPORTED, A.class.getName());
		properties.put(RemoteConstants.ENDPOINT_SERVICE_ID, endpointID);
		properties.put(RemoteConstants.ENDPOINT_FRAMEWORK_UUID, context.getProperty("org.osgi.framework.uuid"));
		properties.put(RemoteConstants.ENDPOINT_ID, "someURI"); // mandatory
		properties.put(RemoteConstants.SERVICE_IMPORTED_CONFIGS, "A"); // mandatory
		endpoint = new EndpointDescription(registration.getReference(), properties);
		
		assertNotNull(endpoint);
		assertEquals("Endpoint properties are supposed to trump service properties", "has been overridden", endpoint.getProperties().get("mykey"));

		// 
		// find the EndpointListeners and call them with the endpoint description
		//
		String filter = "(&(" + Constants.OBJECTCLASS + "="
				+ EndpointListener.class.getName() + ")("
				+ EndpointListener.ENDPOINT_LISTENER_SCOPE + "=*))"; // see
																		// 122.6.1

		tracker = new ServiceTracker<EndpointListener, EndpointListener>(
				context, FrameworkUtil.createFilter(filter), null) {

			@Override
			public EndpointListener addingService(
					ServiceReference<EndpointListener> reference) {
				EndpointListener listener = super.addingService(reference);

				Object scope = reference
						.getProperty(EndpointListener.ENDPOINT_LISTENER_SCOPE);

				String matchedFilter = Utils.isInterested(scope, endpoint);

				if (matchedFilter != null) {
					listener.endpointAdded(endpoint, matchedFilter);
					semaphore.signal();
				}

				return listener;
			}
		};

		tracker.open();
		
		String timeout = context.getProperty("rsa.ct.timeout");
		
		assertTrue("no interested EndpointListener found", semaphore
				.waitForSignal(Long.parseLong(timeout != null ? timeout
						: "30000")));
	}

	/**
	 * 
	 */
	private void stoptest() throws Exception {
		// 
		// find the EndpointListeners and call them with the endpoint description
		//
		ServiceReference[] listeners = tracker.getServiceReferences();
		assertNotNull("no EndpointListeners found", listeners);
		
		boolean foundListener = false;
		for (ServiceReference sr : listeners) {
			EndpointListener listener = (EndpointListener) context.getService(sr);
			Object scope = sr.getProperty(EndpointListener.ENDPOINT_LISTENER_SCOPE);
			
			String matchedFilter = Utils.isInterested(scope, endpoint);
			
			if (matchedFilter != null) {
				foundListener = true;
				listener.endpointRemoved(endpoint, matchedFilter);
			}
		}
		assertTrue("no interested EndpointListener found", foundListener);
	}
	


}

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
package org.osgi.test.cases.remoteserviceadmin.tb1;

import static junit.framework.TestCase.*;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.osgi.test.cases.remoteserviceadmin.common.A;
import org.osgi.test.cases.remoteserviceadmin.common.B;
import org.osgi.test.cases.remoteserviceadmin.common.RemoteServiceConstants;
import org.osgi.test.cases.remoteserviceadmin.common.Utils;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 * @deprecated
 */
@Deprecated
public class Activator implements BundleActivator, A, B {
	ServiceRegistration< ? >																									registration;
	BundleContext       context;
	EndpointDescription endpoint;

	Semaphore																													semaphore	= new Semaphore(
			0);

	ServiceTracker<org.osgi.service.remoteserviceadmin.EndpointListener,org.osgi.service.remoteserviceadmin.EndpointListener>	tracker;

	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
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
	@Override
	public void stop(BundleContext context) throws Exception {
		registration.unregister();
		
		stoptest();

		if (tracker != null)
			tracker.close();
	}

	/**
	 * @see org.osgi.test.cases.remoteserviceadmin.common.A#getA()
	 */
	@Override
	public String getA() {
		return "this is A";
	}

	/**
	 * @see org.osgi.test.cases.remoteserviceadmin.common.B#getB()
	 */
	@Override
	public String getB() {
		return "this is B";
	}
	
	private void test() throws Exception {
		//
		// create an EndpointDescription
		//
		Long endpointID = Long.valueOf(12345);
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
				+ org.osgi.service.remoteserviceadmin.EndpointListener.class
						.getName()
				+ ")("
				+ org.osgi.service.remoteserviceadmin.EndpointListener.ENDPOINT_LISTENER_SCOPE
				+ "=*))"; // see
																		// 122.6.1

		tracker = new ServiceTracker<org.osgi.service.remoteserviceadmin.EndpointListener,org.osgi.service.remoteserviceadmin.EndpointListener>(
				context, FrameworkUtil.createFilter(filter), null) {

			@Override
			public org.osgi.service.remoteserviceadmin.EndpointListener addingService(
					ServiceReference<org.osgi.service.remoteserviceadmin.EndpointListener> reference) {
				org.osgi.service.remoteserviceadmin.EndpointListener listener = super.addingService(
						reference);

				Object scope = reference
						.getProperty(
								org.osgi.service.remoteserviceadmin.EndpointListener.ENDPOINT_LISTENER_SCOPE);

				String matchedFilter = Utils.isInterested(scope, endpoint);

				if (matchedFilter != null) {
					listener.endpointAdded(endpoint, matchedFilter);
					semaphore.release();
				}

				return listener;
			}
		};

		tracker.open();
		
		String timeout = context.getProperty("rsa.ct.timeout");
		
		assertTrue("no interested EndpointListener found", semaphore
				.tryAcquire(Long.parseLong(timeout != null ? timeout
						: "30000"), TimeUnit.MILLISECONDS));
	}

	/**
	 * 
	 */
	private void stoptest() throws Exception {
		// 
		// find the EndpointListeners and call them with the endpoint description
		//
		ServiceReference<org.osgi.service.remoteserviceadmin.EndpointListener>[] listeners = tracker
				.getServiceReferences();
		assertNotNull("no EndpointListeners found", listeners);
		
		boolean foundListener = false;
		for (ServiceReference<org.osgi.service.remoteserviceadmin.EndpointListener> sr : listeners) {
			org.osgi.service.remoteserviceadmin.EndpointListener listener = context
					.getService(sr);
			Object scope = sr.getProperty(
					org.osgi.service.remoteserviceadmin.EndpointListener.ENDPOINT_LISTENER_SCOPE);
			
			String matchedFilter = Utils.isInterested(scope, endpoint);
			
			if (matchedFilter != null) {
				foundListener = true;
				listener.endpointRemoved(endpoint, matchedFilter);
			}
		}
		assertTrue("no interested EndpointListener found", foundListener);
	}
	


}

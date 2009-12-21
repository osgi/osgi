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
package org.osgi.test.cases.remoteserviceadmin.tb1;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import junit.framework.Assert;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.EndpointListener;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.osgi.test.cases.remoteserviceadmin.common.A;
import org.osgi.test.cases.remoteserviceadmin.common.RemoteServiceConstants;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 */
public class Activator implements BundleActivator, A {
	ServiceRegistration registration;
	BundleContext       context;

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
	}

	/**
	 * @see org.osgi.test.cases.remoteserviceadmin.common.A#getA()
	 */
	public String getA() {
		return "this is A";
	}

	/**
	 * @see org.osgi.test.cases.remoteserviceadmin.common.TestService#test()
	 */
	public void test() throws Exception {
		//
		// create an EndpointDescription
		//
		Long endpointID = new Long(12345);
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("mykey", "has been overridden");
//		properties.put(Constants.OBJECTCLASS, new String [] {A.class.getName()}); // needed? no, already in servicereference
		properties.put(RemoteConstants.SERVICE_IMPORTED, A.class.getName());
		properties.put(RemoteConstants.SERVICE_INTENTS, "my_intent_is_for_this_to_work");
		properties.put(RemoteConstants.ENDPOINT_ID, endpointID);
		properties.put(RemoteConstants.ENDPOINT_FRAMEWORK_UUID, context.getProperty("org.osgi.framework.uuid"));
		properties.put(RemoteConstants.ENDPOINT_URI, "someURI"); // mandatory
		properties.put(RemoteConstants.SERVICE_IMPORTED_CONFIGS, "A"); // mandatory
		EndpointDescription endpoint = new EndpointDescription(registration.getReference(), properties);
		
		Assert.assertNotNull(endpoint);
		Assert.assertEquals("Endpoint properties are supposed to trump service properties", "has been overridden", endpoint.getProperties().get("mykey"));

		// 
		// find the EndpointListeners and call them with the endpoint description
		//
		String filter = "(" + EndpointListener.ENDPOINT_LISTENER_SCOPE + "=*)"; // see 122.6.1
		ServiceReference[] listeners = context.getServiceReferences(EndpointListener.class.getName(), filter);
		Assert.assertNotNull("no EndpointListeners found", listeners);
		
		boolean foundListener = false;
		for (ServiceReference sr : listeners) {
			EndpointListener listener = (EndpointListener) context.getService(sr);
			Object scope = sr.getProperty(EndpointListener.ENDPOINT_LISTENER_SCOPE);
			
			String matchedFilter = isInterested(scope, endpoint);
			
			if (matchedFilter != null) {
				foundListener = true;
				listener.endpointAdded(endpoint, matchedFilter);
			}
		}
		Assert.assertTrue("no interested EndpointListener found", foundListener);
	}
	
	/**
	 * @param scope
	 * @param description
	 * @return
	 */
	private String isInterested(Object scopeobj, EndpointDescription description) {
		if (scopeobj instanceof List<?>) {
			List<String> scope = (List<String>) scopeobj;
			for (Iterator<String> it = scope.iterator(); it.hasNext();) {
				String filter = it.next();

				if (description.matches(filter)) {
					return filter;
				}
			}
		} else if (scopeobj instanceof String[]) {
			String[] scope = (String[]) scopeobj;
			for (String filter : scope) {
				if (description.matches(filter)) {
					return filter;
				}
			}
		} else if (scopeobj instanceof String) {
			StringTokenizer st = new StringTokenizer((String)scopeobj, " ");
			for (; st.hasMoreTokens();) {
				String filter = st.nextToken();
				if (description.matches(filter)) {
					return filter;
				}
			}
		}
		return null;
	}

}

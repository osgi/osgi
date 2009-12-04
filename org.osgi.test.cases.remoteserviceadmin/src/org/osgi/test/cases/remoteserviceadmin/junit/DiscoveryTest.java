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

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import static org.osgi.service.remoteserviceadmin.RemoteConstants.*;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.launch.Framework;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.EndpointListener;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.osgi.test.cases.remoteserviceadmin.common.A;

/**
 * Test the discovery portion of the spec by registering a EndpointDescription
 * in one framework and expecting it to show up in the other framework. 
 * 
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 */
public class DiscoveryTest extends MultiFrameworkTestCase {
	/**
	 * Package to be exported by the server side System Bundle
	 */
	private static final String ORG_OSGI_SERVICE_REMOTESERVICEADMIN = "org.osgi.service.remoteserviceadmin";
	/**
	 * Package to be exported by the server side System Bundle
	 */
	private static final String ORG_OSGI_TEST_CASES_REMOTESERVICES_COMMON = "org.osgi.test.cases.remoteserviceadmin.common";

	/**
	 * @see org.osgi.test.cases.remoteserviceadmin.junit.MultiFrameworkTestCase#getConfiguration()
	 */
	public Map<String, String> getConfiguration() {
		Map<String, String> configuration = new HashMap<String, String>();
		configuration.put(Constants.FRAMEWORK_STORAGE_CLEAN, "true");
		
		//make sure that the server framework System Bundle exports the interfaces
        String systemPacakagesXtra = (String) configuration.get(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA);
        if (systemPacakagesXtra == null) {
            systemPacakagesXtra = ORG_OSGI_SERVICE_REMOTESERVICEADMIN + ","
                                  + ORG_OSGI_TEST_CASES_REMOTESERVICES_COMMON;
        } else {
            systemPacakagesXtra = systemPacakagesXtra + ","
                                  + ORG_OSGI_SERVICE_REMOTESERVICEADMIN + ","
                                  + ORG_OSGI_TEST_CASES_REMOTESERVICES_COMMON;
        }
        configuration.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, systemPacakagesXtra);
		return configuration;
	}

	/**
	 * @throws Exception
	 */
	public void testDiscovery() throws Exception {
		// verify that the server framework is exporting the test packages
		verifyFramework();
		
		// create and register a local service in the parent framework
		final A service = new A() {
			public String getA() { return "this is A";};
		};
		Hashtable<String, String> dictionary = new Hashtable<String, String>();
		dictionary.put("mykey", "will be overridden");

		ServiceRegistration registration = getContext().registerService(A.class.getName(), service, dictionary);
		assertNotNull(registration);
		ServiceReference reference = registration.getReference();
		assertNotNull(reference);
		assertEquals("will be overridden", reference.getProperty("mykey"));
		
		
		// create an EndpointDescription
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("mykey", "has been overridden");
		properties.put(RemoteConstants.SERVICE_IMPORTED, A.class.getName());
		properties.put(RemoteConstants.SERVICE_INTENTS, "my_intent_is_for_this_to_work");
		properties.put(RemoteConstants.ENDPOINT_ID, "myid");
		properties.put(RemoteConstants.ENDPOINT_FRAMEWORK_UUID, getContext().getProperty("org.osgi.framework.uuid"));
		properties.put(RemoteConstants.ENDPOINT_URI, "someURI");
		EndpointDescription endpoint = new EndpointDescription(reference, properties);
		assertNotNull(endpoint);
		
		// TODO on the child framework side:
		//  register an EndpointListener interested in remote services
		
		// on the parent side:
		//  emulate a manager bundle and tell the network/discovery bundle about the endpoint
		String filter = "(" + EndpointListener.ENDPOINT_LISTENER_SCOPE + "=*)"; // see 122.6.1
		ServiceReference[] listeners = getContext().getServiceReferences(EndpointListener.class.getName(), filter);
		assertNotNull("no EndpointListeners found", listeners);
		
		boolean foundListener = false;
		for (ServiceReference sr : listeners) {
			EndpointListener listener = (EndpointListener) getContext().getService(sr);
			String scope = (String) sr.getProperty(EndpointListener.ENDPOINT_LISTENER_SCOPE);
			String matchedFilter = isInterested(scope, properties);
			
			if (matchedFilter != null) {
				foundListener = true;
				listener.endpointAdded(endpoint, matchedFilter);
			}
		}
		assertTrue("no interested EndointListener found", foundListener);
		
		// TODO check that the EndpointListener on the child framework is called with the description
		// of the parent framework EndpointDescription
	}
	
	/**
	 * @param scope
	 * @param properties
	 * @return
	 */
	private String isInterested(String scope, Map<String, Object> properties) {
		// TODO
		return null;
	}

	/**
	 * Verifies the server side framework that it exports the test packages for the interface
	 * used by the test service.
	 * @throws Exception
	 */
	private void verifyFramework() throws Exception {
		Framework f = getFramework();
		ServiceReference sr = f.getBundleContext().getServiceReference(PackageAdmin.class.getName());
		assertNotNull("Framework is not supplying PackageAdmin service", sr);
		
		PackageAdmin pkgAdmin = (PackageAdmin) f.getBundleContext().getService(sr);
		ExportedPackage[] exportedPkgs = pkgAdmin.getExportedPackages(f.getBundleContext().getBundle());
		assertNotNull(exportedPkgs);
		f.getBundleContext().ungetService(sr);
		
		boolean found = false; 
		for (int i=0;i<exportedPkgs.length && !found;i++) {
			found = ORG_OSGI_SERVICE_REMOTESERVICEADMIN.equals(exportedPkgs[i].getName());
		}
		assertTrue("Framework System Bundle is not exporting package " + ORG_OSGI_SERVICE_REMOTESERVICEADMIN, found);
	}

}

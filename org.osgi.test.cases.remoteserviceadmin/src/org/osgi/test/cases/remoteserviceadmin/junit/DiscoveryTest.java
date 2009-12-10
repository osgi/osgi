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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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
import org.osgi.test.support.compatibility.Semaphore;

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
	private static final String ORG_OSGI_TEST_CASES_REMOTESERVICES_JUNIT = "org.osgi.test.cases.remoteserviceadmin.junit";
	private static final String ORG_OSGI_TEST_CASES_REMOTESERVICES_COMMON = "org.osgi.test.cases.remoteserviceadmin.common";

	/**
	 * @see org.osgi.test.cases.remoteserviceadmin.junit.MultiFrameworkTestCase#getConfiguration()
	 */
	public Map<String, String> getConfiguration() {
		Map<String, String> configuration = new HashMap<String, String>();
		configuration.put(Constants.FRAMEWORK_STORAGE_CLEAN, "true");
		
		//make sure that the server framework System Bundle exports the interfaces
        String systemPackagesXtra = ORG_OSGI_SERVICE_REMOTESERVICEADMIN + ","
                                  + ORG_OSGI_TEST_CASES_REMOTESERVICES_COMMON + ","
                                  + ORG_OSGI_TEST_CASES_REMOTESERVICES_JUNIT;
        configuration.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, systemPackagesXtra);
        configuration.put("osgi.console", "1112");
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
		Long endpointID = new Long(12345);
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("mykey", "has been overridden");
//		properties.put(Constants.OBJECTCLASS, new String [] {A.class.getName()}); // needed?
		properties.put(RemoteConstants.SERVICE_IMPORTED, A.class.getName());
		properties.put(RemoteConstants.SERVICE_INTENTS, "my_intent_is_for_this_to_work");
		properties.put(RemoteConstants.ENDPOINT_ID, endpointID);
		properties.put(RemoteConstants.ENDPOINT_FRAMEWORK_UUID, getContext().getProperty("org.osgi.framework.uuid"));
		properties.put(RemoteConstants.ENDPOINT_URI, "someURI");
		EndpointDescription endpoint = new EndpointDescription(reference, properties);
		assertNotNull(endpoint);
		assertEquals("Endpoint properties are supposed to trump service properties", "has been overridden", endpoint.getProperties().get("mykey"));
		
		// on the child framework side:
		//  register an EndpointListener interested in remote services
		final String endpointListenerFilter = "(&(objectClass=" + A.class.getName() + ")!(org.osgi.framework.uuid=" + getFramework().getBundleContext().getProperty("org.osgi.framework.uuid") + "))";
		Hashtable<String, String> endpointListenerProperties = new Hashtable<String, String>();
		endpointListenerProperties.put(EndpointListener.ENDPOINT_LISTENER_SCOPE, endpointListenerFilter);
		
		/* */ // davidb some diagnostics here:
        ServiceReference paSR = getFramework().getBundleContext().getServiceReference(PackageAdmin.class.getName());
        PackageAdmin pkgAdmin = (PackageAdmin) getFramework().getBundleContext().getService(paSR);
        ExportedPackage exportedPackage = pkgAdmin.getExportedPackage("org.osgi.service.remoteserviceadmin");
        System.out.println("*** Exported package: " + exportedPackage);
		System.out.println("*** org.osgi.service.remoteserviceadmin exported by " + exportedPackage.getExportingBundle());
		System.out.println("*** org.osgi.service.remoteserviceadmin imported by " + Arrays.asList(exportedPackage.getImportingBundles()));
		
		Class epCLS = exportedPackage.getExportingBundle().loadClass(EndpointListener.class.getName());
		System.out.println("*** Endpoint Listener Class loaded from Bundle: " + epCLS);
		System.out.println("*** Endpoint Listener Class in test: " + EndpointListener.class);
		System.out.println("*** Are they equal?: " + EndpointListener.class.equals(epCLS));
		
		// davidb ok, so let's register a service with the exporter's bundle context that implements its version of EndpointListener...
		Object epl = 
		    Proxy.newProxyInstance(epCLS.getClassLoader(), new Class [] {epCLS}, new InvocationHandler() {            
		        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		            System.out.println("### Endpoint Listener Invoked: " + method + "#" + (args != null ? Arrays.asList(args) : null));
		            return null;
		        }
        });
		exportedPackage.getExportingBundle().getBundleContext().registerService(EndpointListener.class.getName(), epl, endpointListenerProperties);
        /* */ // davidb end additions
		
		EndpointListenerImpl endpointListenerImpl = new EndpointListenerImpl();
		
		ServiceRegistration endpointListenerRegistration = getFramework().getBundleContext().registerService(
				EndpointListener.class.getName(), endpointListenerImpl, endpointListenerProperties);
		assertNotNull(endpointListenerRegistration);
		
		// on the parent side:
		//  emulate a manager bundle and tell the network/discovery bundle about the endpoint
		String filter = "(" + EndpointListener.ENDPOINT_LISTENER_SCOPE + "=*)"; // see 122.6.1
		ServiceReference[] listeners = getContext().getServiceReferences(EndpointListener.class.getName(), filter);
		assertNotNull("no EndpointListeners found", listeners);
		
		boolean foundListener = false;
		for (ServiceReference sr : listeners) {
			EndpointListener listener = (EndpointListener) getContext().getService(sr);
			Object scope = sr.getProperty(EndpointListener.ENDPOINT_LISTENER_SCOPE);
			String matchedFilter = isInterested(scope, endpoint);
			
			if (matchedFilter != null) {
				foundListener = true;
				listener.endpointAdded(endpoint, matchedFilter);
			}
		}
		assertTrue("no interested EndointListener found", foundListener);
		System.out.println("informed EndpointListener in parent frmaework about service A");
		
		// check that the EndpointListener on the child framework is called with the description
		// of the parent framework EndpointDescription
		endpointListenerImpl.getSem().waitForSignal(6000);
		
		assertEquals("filter doesn't match", endpointListenerFilter, endpointListenerImpl.getMatchedFilter());
		EndpointDescription ep = endpointListenerImpl.getAddedEndpoint(); 
		assertNotNull(ep);
		assertEquals(endpointID.longValue(), ep.getRemoteServiceID());
		assertEquals("someURI", ep.getRemoteURI());
		assertEquals(getContext().getProperty("org.osgi.framework.uuid"), ep.getRemoteFrameworkUUID());
		assertTrue(ep.getInterfaces().contains(A.class.getName()));
		assertTrue(ep.getIntents().contains("my_intent_is_for_this_to_work"));
		assertEquals("has been overridden", ep.getProperties().get("mykey")); 
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

	/**
	 * Verifies the child framework that it exports the test packages for the interface
	 * used by the test service.
	 * @throws Exception
	 */
	private void verifyFramework() throws Exception {
		Framework f = getFramework();
//		assertFalse("child framework must have a different UUID",
//				getContext().getProperty("org.osgi.framework.uuid").equals(f.getBundleContext().getProperty("org.osgi.framework.uuid")));
		
		ServiceReference sr = f.getBundleContext().getServiceReference(PackageAdmin.class.getName());
		assertNotNull("Framework is not supplying PackageAdmin service", sr);
		
		PackageAdmin pkgAdmin = (PackageAdmin) f.getBundleContext().getService(sr);
		ExportedPackage[] exportedPkgs = pkgAdmin.getExportedPackages(f.getBundleContext().getBundle());
		assertNotNull(exportedPkgs);
		f.getBundleContext().ungetService(sr);
		
		String pkgXtras = f.getBundleContext().getProperty(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA);
		List<String> pkgList = splitString(pkgXtras, ",");
		
		for (int i=0;i<exportedPkgs.length;i++) {
			pkgList.remove(exportedPkgs[i].getName());
		}
		assertTrue("Framework does not export some packages " + pkgList, pkgList.isEmpty());
	}

	private List<String> splitString(String string, String delim) {
		List<String> result = new ArrayList<String>();
		for (StringTokenizer st = new StringTokenizer(string, delim); st
				.hasMoreTokens();) {
			result.add(st.nextToken());
		}
		return result;
	}

	class EndpointListenerImpl implements EndpointListener {
		private Semaphore sem = new Semaphore(0);
		private String matchedFilter;
		private EndpointDescription addedEndpoint;
		private EndpointDescription removedEndpoint;

		/**
		 * @see org.osgi.service.remoteserviceadmin.EndpointListener#endpointAdded(org.osgi.service.remoteserviceadmin.EndpointDescription, java.lang.String)
		 */
		public void endpointAdded(EndpointDescription endpoint,
				String matchedFilter) {
			this.addedEndpoint = endpoint;
			this.matchedFilter = matchedFilter;
			sem.signal();
		}

		/**
		 * @see org.osgi.service.remoteserviceadmin.EndpointListener#endpointRemoved(org.osgi.service.remoteserviceadmin.EndpointDescription, java.lang.String)
		 */
		public void endpointRemoved(EndpointDescription endpoint,
				String matchedFilter) {
			this.removedEndpoint = endpoint;
			this.matchedFilter = matchedFilter;
			sem.signal();
		}
		
		/**
		 * @return the sem
		 */
		public Semaphore getSem() {
			return sem;
		}
		
		/**
		 * @return the matchedFilter
		 */
		public String getMatchedFilter() {
			return matchedFilter;
		}
		
		/**
		 * @return the addedEndpoint
		 */
		public EndpointDescription getAddedEndpoint() {
			return addedEndpoint;
		}
		
		/**
		 * @return the removedEndpoint
		 */
		public EndpointDescription getRemovedEndpoint() {
			return removedEndpoint;
		}
	}
}

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
package org.osgi.test.cases.remoteservices.junit;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.launch.Framework;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.test.cases.remoteservices.common.A;
import org.osgi.test.cases.remoteservices.common.B;
import org.osgi.test.cases.remoteservices.common.C;
import org.osgi.test.cases.remoteservices.impl.TestServiceImpl;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 */
public class SimpleTest extends MultiFrameworkTestCase {

	/**
	 * Package to be exported by the server side System Bundle
	 */
	private static final String ORG_OSGI_TEST_CASES_REMOTESERVICES_COMMON = "org.osgi.test.cases.remoteservices.common";

	/** 
	 * Magic value. Properties with this value will be replaced by a socket port number that is currently free. 
	 */
    private static final String FREE_PORT = "@@FREE_PORT@@";

	private long timeout;
	
	/**
	 * @see org.osgi.test.cases.remoteserviceadmin.junit.MultiFrameworkTestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		timeout = Long.getLong("rsa.ct.timeout", 300000L).longValue();
	}
	
	/**
	 * @see org.osgi.test.cases.remoteservices.junit.MultiFrameworkTestCase#getConfiguration()
	 */
	public Map getConfiguration() {
		Map configuration = new HashMap();
		configuration.put(Constants.FRAMEWORK_STORAGE_CLEAN, "true");
		
		//make sure that the server framework System Bundle exports the interfaces
		String systemPacakagesXtra = ORG_OSGI_TEST_CASES_REMOTESERVICES_COMMON;
        configuration.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, systemPacakagesXtra);
		return configuration;
	}

	/**
	 * @throws Exception
	 */
	public void testSimpleRegistration() throws Exception {
		// verify that the server framework is exporting the test packages
		verifyFramework();
		
		Set supportedConfigTypes = getSupportedConfigTypes();

		// load the external properties file with the config types for the server side service
		Hashtable properties = loadServerTCKProperties();

		// make sure the given config type is in the set of supported config types
		String str = (String) properties.get(RemoteServiceConstants.SERVICE_EXPORTED_CONFIGS);
		
		// I hate not having String.split() available
		StringTokenizer st = new StringTokenizer(str, " ");
		boolean found = false;
		while (st.hasMoreTokens()) {
			if (supportedConfigTypes.contains(st.nextToken())) {
				found = true;
				break;
			}
		}
		assertTrue("the given service.exported.configs type is not supported by the installed Distribution Provider", found);
		
		// add some properties for testing
		properties.put(RemoteServiceConstants.SERVICE_EXPORTED_INTERFACES, "*");
		properties.put("implementation", "1");
		properties.put(".myprop", "must not be visible on client side");
		processFreePortProperties(properties);
		
		// install server side test service in the sub-framework
		TestServiceImpl impl = new TestServiceImpl();
		
		// register the service in the server side framework on behalf of the System Bundle
		// the interface package is exported by the System Bundle
		ServiceRegistration srTestService = getFramework().getBundleContext().registerService(
				new String[]{A.class.getName(), B.class.getName()}, impl, properties);
		assertNotNull(srTestService);

		System.out.println("registered test service A and B on server side");

		Thread.sleep(1000);

		try {
			// now check on the hosting framework for the service to become available
			ServiceTracker clientTracker = new ServiceTracker(getContext(), A.class.getName(), null);
			clientTracker.open();

			// the proxy should appear in this framework
			A client = (A)clientTracker.waitForService(timeout);
			assertNotNull("no proxy for service A found!", client);

			ServiceReference sr = clientTracker.getServiceReference();
			System.out.println(sr);
			assertNotNull(sr);
			assertNotNull(sr.getProperty("service.imported"));
			assertNotNull(sr.getProperty("service.id"));
			assertNotNull(sr.getProperty("endpoint.id"));
			assertNotNull(sr.getProperty("service.imported.configs"));
			assertNull("private properties must not be exported", sr.getProperty(".myprop"));
			assertEquals("property implementation missing from proxy", "1", sr.getProperty("implementation"));
			assertNull(sr.getProperty(RemoteServiceConstants.SERVICE_EXPORTED_INTENTS));
			assertNull(sr.getProperty(RemoteServiceConstants.SERVICE_EXPORTED_INTENTS_EXTRA));
			assertNull(sr.getProperty(RemoteServiceConstants.SERVICE_EXPORTED_INTERFACES));
			assertNull(sr.getProperty(RemoteServiceConstants.SERVICE_EXPORTED_CONFIGS));

			// invoke the proxy
			assertEquals("A", client.getA());

			clientTracker.close();

			// make sure C was not registered, since it wasn't exported
			assertNull("service C should not have been found as it was not exported", getContext().getServiceReference(C.class.getName()));
			
			// check for service B
			clientTracker = new ServiceTracker(getContext(), B.class.getName(), null);
			clientTracker.open();

			// the proxy should appear in this framework
			B clientB = (B)clientTracker.waitForService(timeout);
			assertNotNull("no proxy for service B found!", clientB);

			sr = clientTracker.getServiceReference();
			System.out.println(sr);
			assertNotNull(sr);
			assertNotNull(sr.getProperty("service.imported"));
			assertNotNull(sr.getProperty("service.id"));
			assertNotNull(sr.getProperty("endpoint.id"));
			assertNotNull(sr.getProperty("service.imported.configs"));
			assertNull("private properties must not be exported", sr.getProperty(".myprop"));
			assertEquals("property implementation missing from proxy", "1", sr.getProperty("implementation"));
			assertNull(sr.getProperty(RemoteServiceConstants.SERVICE_EXPORTED_INTENTS));
			assertNull(sr.getProperty(RemoteServiceConstants.SERVICE_EXPORTED_INTENTS_EXTRA));
			assertNull(sr.getProperty(RemoteServiceConstants.SERVICE_EXPORTED_INTERFACES));
			assertNull(sr.getProperty(RemoteServiceConstants.SERVICE_EXPORTED_CONFIGS));

			// invoke the proxy
			assertEquals("B", clientB.getB());

			clientTracker.close();
			
		} finally {
			srTestService.unregister();
		}
	}

    private void processFreePortProperties(Hashtable properties) {
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
	private Hashtable loadServerTCKProperties() {
		String serverconfig = System
				.getProperty("org.osgi.test.cases.remoteservices.serverconfig");
		assertNotNull(
				"did not find org.osgi.test.cases.remoteservices.serverconfig system property",
				serverconfig);
		Hashtable properties = new Hashtable();
		
		for (StringTokenizer tok = new StringTokenizer(serverconfig, ","); tok
				.hasMoreTokens();) {
			String propertyName = tok.nextToken();
			String value = System.getProperty(propertyName);
			assertNotNull("system property not found: " + propertyName, value);
			properties.put(propertyName, value);
		}
		
		return properties;
	}
	
	private Set getSupportedConfigTypes() throws Exception {
		// make sure there is a Distribution Provider installed in the framework
//		Filter filter = getFramework().getBundleContext().createFilter("(&(objectClass=*)(" +
//				DistributionProviderConstants.REMOTE_CONFIGS_SUPPORTED + "=*))");
		Filter filter = getFramework().getBundleContext().createFilter("(" +
				DistributionProviderConstants.REMOTE_CONFIGS_SUPPORTED + "=*)");
		ServiceTracker dpTracker = new ServiceTracker(getFramework().getBundleContext(), filter, null);
		dpTracker.open();
		
		Object dp = dpTracker.waitForService(0);
		assertNotNull("No DistributionProvider found", dp);
		ServiceReference dpReference = dpTracker.getServiceReference();
		assertNotNull(dpReference);
		assertNotNull(dpReference.getProperty(DistributionProviderConstants.REMOTE_INTENTS_SUPPORTED));
		
		Set supportedConfigTypes = new HashSet(); // collect all supported config types
		
		Object configProperty = dpReference.getProperty(DistributionProviderConstants.REMOTE_CONFIGS_SUPPORTED);
		if (configProperty instanceof String) {
			StringTokenizer st = new StringTokenizer((String)configProperty, " ");
			while (st.hasMoreTokens()) {
				supportedConfigTypes.add(st.nextToken());
			}
		} else if (configProperty instanceof Collection) {
			Collection col = (Collection) supportedConfigTypes;
			for (Iterator it=col.iterator(); it.hasNext(); ) {
				supportedConfigTypes.add(it.next());
			}
		} else { // assume String[]
			String[] arr = (String[])configProperty;
			for (int i=0; i<arr.length; i++) {
				supportedConfigTypes.add(arr[i]);
			}
		}
		dpTracker.close();
		
		return supportedConfigTypes;
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
			found = ORG_OSGI_TEST_CASES_REMOTESERVICES_COMMON.equals(exportedPkgs[i].getName());
		}
		assertTrue("Framework System Bundle is not exporting package " + ORG_OSGI_TEST_CASES_REMOTESERVICES_COMMON, found);
	}
}

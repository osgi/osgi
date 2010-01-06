/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2008). All Rights Reserved.
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

package org.osgi.test.cases.scaconfigtype.junit;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.test.cases.scaconfigtype.common.A;
import org.osgi.test.cases.scaconfigtype.common.B;
import org.osgi.test.cases.scaconfigtype.common.Utils;
import org.osgi.util.tracker.ServiceTracker;

import static org.osgi.framework.Constants.*;
import static org.osgi.test.cases.scaconfigtype.common.RemoteServiceConstants.*;
import static org.osgi.test.cases.scaconfigtype.common.SCAConfigConstants.*;

/**
 * Tests are documented in the <a href="https://www.osgi.org/members/svn/documents/trunk/rfcs/rfc0119/working_docs/service.scaconfigurationtype.tck.odt">SCA TCK Planning Document</a>
 * 
 * @author <a href="mailto:david.savage@paremus.com">David Savage</a>
 *
 */
public class SCAConfigTypeTestCase extends MultiFrameworkTestCase {

	/**
	 * Package to be exported by the server side System Bundle
	 */
	private static final String ORG_OSGI_TEST_CASES_SCACONFIGTYPE_COMMON = "org.osgi.test.cases.scaconfigtype.common";
	
	
	protected void setUp() throws Exception {
		super.setUp();
		
		// verify that the server framework is exporting the test packages
		verifyFramework();
	}
	
	/**
	 * CT.1
	 * @throws Exception
	 */
//	public void testEndpointLifecycle() throws Exception {		
//		fail("TODO not yet implemented");
//	}
	
	/**
	 * CT.4
	 * @throws Exception
	 */
//	public void testSCAConfigurationManifestHeader() throws Exception {
//		fail("TODO not yet implemented");
//	}
	
	/**
	 * CT.6
	 * @throws Exception
	 */
//	public void testFindEntriesSCAConfigurationManifestHeader() throws Exception {
//		fail("TODO not yet implemented");
//	}
	
	/**
	 * CT.7
	 * @throws Exception
	 */
//	public void testAbsolutePathSCAConfigurationManifestHeader() throws Exception {
//		fail("TODO not yet implemented");
//	}
	
	/**
	 * CT.8
	 * @throws Exception
	 */
//	public void testDirectorySCAConfigurationManifestHeader() throws Exception {
//		fail("TODO not yet implemented");
//	}
	
	/**
	 * CT.9
	 * @throws Exception
	 */
//	public void testAbsentSCAConfigurationManifestHeader() throws Exception {
//		fail("TODO not yet implemented");
//	}
	
	/**
	 * CT.11
	 * @throws Exception
	 */
	public void testSCAConfigTypeServiceHeader() throws Exception {
		assertTrue( "Expected supported config type " + ORG_OSGI_SCA, Utils.getSupportedConfigTypes(getContext()).contains( ORG_OSGI_SCA ) );
		assertTrue( "Expected supported config type " + ORG_OSGI_SCA, Utils.getSupportedConfigTypes(getFramework().getBundleContext()).contains( ORG_OSGI_SCA ) );
	}
	
	/**
	 * CT.12
	 * @throws Exception
	 */
	public void testExportedConfigs() throws Exception {
		// install test bundle in child framework
		BundleContext childContext = getFramework().getBundleContext();
		
		Bundle ctBundle = installBundle(childContext, "/ct12.jar");
		assertNotNull(ctBundle);
		ctBundle.start();
		
		// wait for test service to be registered in this framework
		ServiceTracker tracker = new ServiceTracker(getContext(), A.class.getName(), null);
		tracker.open();
		A serviceA = (A) tracker.waitForService(10000);
		
		assertNotNull( "Missing test service", serviceA );
		ServiceReference[] refs = tracker.getServiceReferences();
		assertEquals( "Unexpected service reference length", 1, refs.length );

		// check service is functional
		assertEquals( "Invalid service response", A.A, serviceA.getA() );
		
		// check service is registered with sca config type header
		Object config = refs[0].getProperty(SERVICE_IMPORTED_CONFIGS);
		assertTrue( Utils.propertyToList( config ).contains(ORG_OSGI_SCA) );
		tracker.close();
		
		// search for b service which is registered with a fabricated config type
		// assert this is not picked up by the RI
		tracker = new ServiceTracker(getContext(), B.class.getName(), null);
		tracker.open();
		B serviceB = (B) tracker.waitForService(10000);
		assertNull( "Unexpected test service", serviceB );
		
		tracker.close();
	}
	
	/**
	 * CT.13
	 * @throws Exception
	 */
	public void testImportedConfigs() throws Exception {
		// install test bundle in child framework
		BundleContext childContext = getFramework().getBundleContext();
		
		Bundle ctBundle = installBundle(childContext, "/ct13.jar");
		assertNotNull(ctBundle);
		ctBundle.start();
		
		// wait for test service to be registered in this framework
		ServiceTracker tracker = new ServiceTracker(getContext(), A.class.getName(), null);
		tracker.open();
		A serviceA = (A) tracker.waitForService(10000);
		
		assertNotNull( "Missing test service", serviceA );
		ServiceReference[] refs = tracker.getServiceReferences();
		assertEquals( "Unexpected service reference length", 1, refs.length );
		
		// check service is functional
		assertEquals( "Invalid service response", A.A, serviceA.getA() );
		
		// check service is registered with sca config type header
		Object config = refs[0].getProperty(SERVICE_IMPORTED_CONFIGS);
		assertTrue( Utils.propertyToList( config ).contains(ORG_OSGI_SCA) );
		
		tracker.close();
	}
	
	/**
	 * CT.14
	 * @throws Exception
	 */
//	public void testQNameForm() throws Exception {
//		fail("TODO not yet implemented");
//	}
	
	/**
	 * CT.15
	 * @throws Exception
	 */
//	public void testNCNameForm() throws Exception {
//		fail("TODO not yet implemented");
//	}
	
	/**
	 * CT.23
	 * @throws Exception
	 */
//	public void testInvalidBindingXML() throws Exception {
//		fail("TODO not yet implemented");
//	}
	
	/**
	 * CT.24
	 * @throws Exception
	 */
//	public void testDuplicateBinding() throws Exception {
//		fail("TODO not yet implemented");
//	}
	
	/**
	 * CT.25
	 * @throws Exception
	 */
//	public void testUnknownBinding() throws Exception {
//		fail("TODO not yet implemented");
//	}
	
	/**
	 * CT.26
	 * @throws Exception
	 */
//	public void testSupportedIntents() throws Exception {
//		fail("TODO not yet implemented");
//	}
	
	/**
	 * CT.30
	 * @throws Exception
	 */
//	public void testEndpointImported() throws Exception {
//		fail("TODO not yet implemented");
//	}
	
	/**
	 * CT.31
	 * @throws Exception
	 */
	// TODO is this a duplicate?
//	public void testSCAConfigTypeServiceHeader2() throws Exception {
//		fail("TODO not yet implemented");
//	}
	
	/**
	 * CT.32
	 * @throws Exception
	 */
//	public void testBindingTypeTypesHeader() throws Exception {
//		fail("TODO not yet implemented");
//	}
	
	/**
	 * CT.35, CT.36, CT.37
	 * @throws Exception
	 */
//	public void testEndpointConfig() throws Exception {
//		fail("TODO not yet implemented");
//	}	
		
	/**
	 * @param context
	 * @param bundle
	 * @return
	 */
	private Bundle installBundle(BundleContext context, String bundle) throws Exception {
		if (!bundle.startsWith(getWebServer())) {
			bundle = getWebServer() + bundle;
		}
		URL location = new URL(bundle);
		InputStream inputStream = location.openStream();
		
		Bundle b = context.installBundle(bundle, inputStream);
		return b;
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
			found = ORG_OSGI_TEST_CASES_SCACONFIGTYPE_COMMON.equals(exportedPkgs[i].getName());
		}
		assertTrue("Framework System Bundle is not exporting package " + ORG_OSGI_TEST_CASES_SCACONFIGTYPE_COMMON, found);
		f.getBundleContext().ungetService(sr);
	}
	
	public Map getConfiguration() {
		Map configuration = new HashMap();
		configuration.put(FRAMEWORK_STORAGE_CLEAN, "true");
		
		//make sure that the server framework System Bundle exports the interfaces
        String systemPackagesXtra = (String) configuration.get(FRAMEWORK_SYSTEMPACKAGES_EXTRA);
        if (systemPackagesXtra == null) {
            systemPackagesXtra = ORG_OSGI_TEST_CASES_SCACONFIGTYPE_COMMON;
        } else {
            systemPackagesXtra = systemPackagesXtra
                                  + ","
                                  + ORG_OSGI_TEST_CASES_SCACONFIGTYPE_COMMON;
        }
        configuration.put(FRAMEWORK_SYSTEMPACKAGES_EXTRA, systemPackagesXtra);
		return configuration;
	}
}

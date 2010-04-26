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
package org.osgi.test.cases.remoteserviceadmin.secure;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

/**
 * Use RSA service to register a service in a child framework and then import
 * the same service on the parent framework. This test does not explicitly use
 * the discovery, but manually conveys the endpoint information.
 * 
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 * @version 1.0.0
 */
public class RemoteServiceAdminSecureTest extends MultiFrameworkTestCase {
	private static final String	SYSTEM_PACKAGES_EXTRA	= "org.osgi.test.cases.remoteserviceadmin.secure.system.packages.extra";
	
	/**
	 * @see org.osgi.test.cases.remoteserviceadmin.secure.MultiFrameworkTestCase#getConfiguration()
	 */
	public Map<String, String> getConfiguration() {
		Map<String, String> configuration = new HashMap<String, String>();
		configuration.put(Constants.FRAMEWORK_STORAGE_CLEAN, "true");
		
		//make sure that the server framework System Bundle exports the interfaces
		String systemPackagesXtra = System.getProperty(SYSTEM_PACKAGES_EXTRA);
        configuration.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, systemPackagesXtra);
        configuration.put("osgi.console", "" + (Integer.getInteger("osgi.console", 1111).intValue() + 1));
		return configuration;
	}

	/**
	 * Sets up a child framework in which a service is exported. In the parent framework the
	 * EndpointDescription is passed to the RSA service to import the service from the child
	 * framework. This manual step bypasses Discovery, which would normally do the transport
	 * between the two frameworks.
	 */
	public void testExportImportManuallyWithPermision() throws Exception {
		verifyFramework();
		
		//
		// install test bundle in child framework
		//
		BundleContext childContext = getFramework().getBundleContext();
		
		Bundle tbexporterBundle = installBundle(childContext, "/tb_exporter.jar");
		assertNotNull(tbexporterBundle);
		
		// start test bundle in child framework
		// this will run the test in the child framework and fail
		tbexporterBundle.start();

		// now install the importer in the parent (this) framework
		Bundle tbimporterBundle = installBundle(getContext(), "/tb_importer.jar");
		assertNotNull(tbimporterBundle);
		
		tbimporterBundle.start();
		
		Thread.sleep(2000); // wait 2 s
		
		// install the reader bundle to test the READ permission
		Bundle creaderBundle = installBundle(childContext, "/tb_reader.jar");
		assertNotNull(creaderBundle);
		Bundle preaderBundle = installBundle(getContext(), "/tb_reader.jar");
		assertNotNull(preaderBundle);
		
		// start the reader bundles
		creaderBundle.start();
		
		preaderBundle.start();
		
		tbimporterBundle.stop();
		
		tbexporterBundle.stop();
	}


	/**
	 * Sets up a child framework.
	 * Install and try to start an exporter, an importer, and a reader without
	 * the appropriate permissions.
	 */
	public void testNoPermissions() throws Exception {
		verifyFramework();
		
		//
		// install test bundle in child framework
		//
		BundleContext childContext = getFramework().getBundleContext();
		
		Bundle tbexporterBundle = installBundle(childContext, "/tb_exporter_noperm.jar");
		assertNotNull(tbexporterBundle);
		
		// start test bundle in child framework
		// this will run the test in the child framework and fail
		try {
			tbexporterBundle.start();
			fail("SecurityException expected");
		} catch (Exception se) {
			assertNotNull(se.getCause());
			assertTrue(se.getCause() instanceof SecurityException);
		}

		tbexporterBundle.stop();
		
		
		// now instal a buld that has the required permissions so that a service is exported that can later be imported
		Bundle tbexporterPermBundle = installBundle(childContext, "/tb_exporter.jar");
		assertNotNull(tbexporterPermBundle);
		tbexporterPermBundle.start();
		
		
		// now install the importer in the parent (this) framework
		Bundle tbimporterBundle = installBundle(getContext(), "/tb_importer_noperm.jar");
		assertNotNull(tbimporterBundle);
		
		try {
			tbimporterBundle.start();
			fail("SecurityException expected");
		} catch (Exception se) {
			assertNotNull(se.getCause());
			assertTrue(se.getCause() instanceof SecurityException);
		}
		
		// install the reader bundle to test the READ permission
		Bundle preaderBundle = installBundle(getContext(), "/tb_reader_noperm.jar");
		assertNotNull(preaderBundle);

		try {
			preaderBundle.start();
			fail("SecurityException expected");
		} catch (Exception se) {
			assertNotNull(se.getCause());
			assertTrue(se.getCause() instanceof SecurityException);
		}
		
		tbexporterPermBundle.stop();
		tbimporterBundle.stop();
		
		
	}
}

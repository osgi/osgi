/*
 * Copyright (c) OSGi Alliance (2010, 2018). All Rights Reserved.
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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;
import org.osgi.test.support.sleep.Sleep;

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
		String systemPackagesXtra = getProperty(SYSTEM_PACKAGES_EXTRA);
		if (systemPackagesXtra != null) {
			configuration.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA,
					systemPackagesXtra);
		}
		int console = getIntegerProperty("osgi.console", 0);
		if (console != 0) {
			configuration.put("osgi.console", "" + console + 1);
		}

		configuration.put("org.osgi.test.cases.remoteserviceadmin.secure.role",
				"exporter");

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
		assertEquals(
				"exporter",
				childContext
						.getProperty("org.osgi.test.cases.remoteserviceadmin.secure.role"));

		// start test bundle in child framework
		// this will run the test in the child framework and fail
		tbexporterBundle.start();

		// now install the importer in the parent (this) framework
		Bundle tbimporterBundle = installBundle(getContext(), "/tb_importer.jar");
		assertNotNull(tbimporterBundle);
		assertNull(getContext().getProperty(
						"org.osgi.test.cases.remoteserviceadmin.secure.role"));

		tbimporterBundle.start();

		Sleep.sleep(2000); // wait 2 s

		// //////////////DEBUG REMOVE
		ServiceReference rsaRef = getContext()
				.getServiceReference(RemoteServiceAdmin.class.getName());
		assertNotNull(rsaRef);
		RemoteServiceAdmin rsa = (RemoteServiceAdmin) getContext()
				.getService(rsaRef);
		assertNotNull(rsa);
		System.out.println("##############  " + rsa.getImportedEndpoints());
		// //////////////DEBUG REMOVE END


		// install the reader bundle to test the READ permission
		Bundle creaderBundle = installBundle(childContext, "/tb_reader.jar");
		assertNotNull(creaderBundle);
		Bundle preaderBundle = installBundle(getContext(), "/tb_reader.jar");
		assertNotNull(preaderBundle);

		// start the reader bundles which will try to call getExportedEndpoints
		// / getImportedEndpoints and check if the results are correct for the
		// exporting and the importing side
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
			fail("The bundle should not start cleanly");
		} catch (BundleException be) {
			assertNotNull(be.getCause());
			assertTrue("Not caused by a failed ExportRegistration",
					be.getCause() instanceof InvocationTargetException
							&& "Export Failed"
									.equals(be.getCause().getMessage()));
			assertTrue(be.getCause().getCause() instanceof SecurityException);
		}

		tbexporterBundle.stop();


		// now install a build that has the required permissions so that a
		// service is exported that can later be imported
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

		// Bug 2596: calls to getExportedServices / getImportedServices should
		// not return a SecurityException but return an empty List. The bundle
		// Activator checks for this and fails with an assertion
		try {
			preaderBundle.start();
			// fail("SecurityException expected");
		} catch (Exception se) {
			assertNotNull(se.getCause());
			assertTrue(se.getCause() instanceof SecurityException);
		}

		tbexporterPermBundle.stop();
		tbimporterBundle.stop();


	}
}

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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author <a href="mailto:david.savage@paremus.com">David Savage</a>
 *
 */
public class SCAConfigTypeTestCase extends MultiFrameworkTestCase {

	private static final String ORG_OSGI_SCA_CONFIG_TYPE = "org.osgi.sca";

	/**
	 * Package to be exported by the server side System Bundle
	 */
	private static final String ORG_OSGI_SERVICE_SCACONFIGTYPE = "org.osgi.service.scaconfigtype";
	
	/**
	 * Package to be exported by the server side System Bundle
	 */
	private static final String ORG_OSGI_TEST_CASES_SCACONFIGTYPE_COMMON = "org.osgi.test.cases.scaconfigtype.common";
	
	public void testSCAConfigTypeServiceHeader() throws Exception {
		// verify that the server framework is exporting the test packages
		verifyFramework();
		assertTrue( "Expected supported config type " + ORG_OSGI_SCA_CONFIG_TYPE, getSupportedConfigTypes().contains( ORG_OSGI_SCA_CONFIG_TYPE ) );
	}
	
	private Set getSupportedConfigTypes() throws Exception {
		// make sure there is a Distribution Provider installed in the framework
//		Filter filter = getFramework().getBundleContext().createFilter("(&(objectClass=*)(" +
//				DistributionProviderConstants.REMOTE_CONFIGS_SUPPORTED + "=*))");
		Filter filter = getFramework().getBundleContext().createFilter("(" +
				DistributionProviderConstants.REMOTE_CONFIGS_SUPPORTED + "=*)");
		ServiceTracker dpTracker = new ServiceTracker(getFramework().getBundleContext(), filter, null);
		dpTracker.open();
		
		Object dp = dpTracker.waitForService(10000L);
		assertNotNull("No DistributionProvider found", dp);
		ServiceReference dpReference = dpTracker.getServiceReference();
		assertNotNull(dpReference);
		
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


	public Map getConfiguration() {
		Map configuration = new HashMap();
		configuration.put(Constants.FRAMEWORK_STORAGE_CLEAN, "true");
		
		//make sure that the server framework System Bundle exports the interfaces
        String systemPacakagesXtra = (String) configuration.get(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA);
        if (systemPacakagesXtra == null) {
            systemPacakagesXtra = ORG_OSGI_TEST_CASES_SCACONFIGTYPE_COMMON;
        } else {
            systemPacakagesXtra = systemPacakagesXtra
                                  + ","
                                  + ORG_OSGI_TEST_CASES_SCACONFIGTYPE_COMMON
                                  + ORG_OSGI_SERVICE_SCACONFIGTYPE;
        }
        configuration.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, systemPacakagesXtra);
		return configuration;
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
	}
	
}

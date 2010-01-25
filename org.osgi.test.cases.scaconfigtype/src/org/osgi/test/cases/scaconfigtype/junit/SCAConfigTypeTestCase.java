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

import static org.osgi.framework.Constants.FRAMEWORK_STORAGE_CLEAN;
import static org.osgi.framework.Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA;
import static org.osgi.test.cases.scaconfigtype.common.DistributionProviderConstants.REMOTE_CONFIGS_SUPPORTED;
import static org.osgi.test.cases.scaconfigtype.common.RemoteServiceConstants.SERVICE_IMPORTED_CONFIGS;
import static org.osgi.test.cases.scaconfigtype.common.RemoteServiceConstants.SERVICE_INTENTS;
import static org.osgi.test.cases.scaconfigtype.common.SCAConfigConstants.ORG_OSGI_SCA_CONFIG;
import static org.osgi.test.cases.scaconfigtype.common.TestConstants.CLIENT_FRAMEWORK;
import static org.osgi.test.cases.scaconfigtype.common.TestConstants.ORG_OSGI_TEST_CASES_SCACONFIGTYPE_COMMON;
import static org.osgi.test.cases.scaconfigtype.common.TestConstants.SERVER_FRAMEWORK;
import static org.osgi.test.cases.scaconfigtype.common.TestConstants.SERVICE_TIMEOUT;

import java.io.InputStream;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.test.cases.scaconfigtype.common.A;
import org.osgi.test.cases.scaconfigtype.common.B;
import org.osgi.test.cases.scaconfigtype.common.Utils;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Tests are documented in the <a href="https://www.osgi.org/members/svn/documents/trunk/rfcs/rfc0119/working_docs/service.scaconfigurationtype.tck.odt">SCA TCK Planning Document</a>
 * 
 * @author <a href="mailto:david.savage@paremus.com">David Savage</a>
 *
 */
public class SCAConfigTypeTestCase extends MultiFrameworkTestCase {

    protected void setUp() throws Exception {
        super.setUp();

        // verify that the server framework is exporting the test packages
        verifyFramework(getFramework(SERVER_FRAMEWORK));
    }

    /**
     * CT.1
     * @throws Exception
     */
    // [dsavage] not sure if this test is possible with RI as is 
    // [dsavage] need version of tuscany that doesn't have discover enabled
    //	public void testEndpointLifecycle() throws Exception {		
    //		fail("TODO not yet implemented");
    //	}

    /**
     * CT.4
     * @throws Exception
     */
    public void testSCAConfigurationManifestHeader() throws Exception {
        // install test bundle in child framework
        BundleContext serverContext = getFramework(SERVER_FRAMEWORK).getBundleContext();
        BundleContext clientContext = getFramework(CLIENT_FRAMEWORK).getBundleContext();

        // TODO don't technically need to start client bundle but this checks it's resolved
        Bundle clientBundle = installAndStartBundle(clientContext, "/ct04client.jar");
        
        Bundle serverBundle;

        // specific location
        serverBundle = installAndStartBundle(serverContext, "/ct04specificLocation.jar");
        
        assertAAvailability(clientBundle, true);
        
        serverBundle.uninstall();
        
        assertAAvailability(clientBundle, false);

        // wild card location
        serverBundle = installAndStartBundle(serverContext, "/ct04wildcardLocation.jar");
        
        assertAAvailability(clientBundle, true);
        
        serverBundle.uninstall();

        assertAAvailability(clientBundle, false);
        
        // [dsavage] do this test last as it's failing at the moment
        // default location
        serverBundle = installAndStartBundle(serverContext, "/ct04defaultLocation.jar");
        
        // [dsavage] dumping headers to check SCA-Configuration header is present
        // [dsavage] replace this with assert
        System.out.println( "*****************************" );
        Dictionary h = serverBundle.getHeaders();
        for ( Enumeration e = h.keys(); e.hasMoreElements(); ) {
        	String k = (String) e.nextElement();
        	String v = (String) h.get(k);
        	System.out.println( k + "=" + v );
        }
        System.out.println( "*****************************" );
        
        assertAAvailability(clientBundle, true);

        serverBundle.uninstall();

        assertAAvailability(clientBundle, false);
    }

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
    public void testAbsentSCAConfigurationManifestHeader() throws Exception {
        // install test bundle in child framework
        BundleContext serverContext = getFramework(SERVER_FRAMEWORK).getBundleContext();
        BundleContext clientContext = getFramework(CLIENT_FRAMEWORK).getBundleContext();

        installAndStartBundle(serverContext, "/ct09.jar");
        // TODO don't technically need to start client bundle but this checks it's resolved
        Bundle clientBundle = installAndStartBundle(clientContext, "/ct09client.jar");

        // service should not be registered as ct9.jar does not include an SCA-Configuration header
        assertAAvailability(clientBundle, false);
    }

    /**
     * CT.11
     * @throws Exception
     */
    public void testSCAConfigTypeServiceHeader() throws Exception {
        // sanity check that sca is not installed in test framework - do all testing in sub frameworks
        List configs = Utils.getServiceAdvert(getContext(), REMOTE_CONFIGS_SUPPORTED);
        assertFalse("Unexpected supported config type " + ORG_OSGI_SCA_CONFIG, configs.contains(ORG_OSGI_SCA_CONFIG));

        // check server framework contains sca provider
        configs = Utils.getServiceAdvert(getFramework(SERVER_FRAMEWORK).getBundleContext(), REMOTE_CONFIGS_SUPPORTED);
        assertTrue("Expected supported config type " + ORG_OSGI_SCA_CONFIG, configs.contains(ORG_OSGI_SCA_CONFIG));
    }

    /**
     * CT.12
     * @throws InterruptedException 
     */
    public void testExportedConfigs() throws InterruptedException {
        // install test bundle in child framework
        BundleContext serverContext = getFramework(SERVER_FRAMEWORK).getBundleContext();
        BundleContext clientContext = getFramework(CLIENT_FRAMEWORK).getBundleContext();

        installAndStartBundle(serverContext, "/ct12.jar");
        // TODO don't technically need to start client bundle but this checks it's resolved
        Bundle clientBundle = installAndStartBundle(clientContext, "/ct12client.jar");

        // wait for test service to be registered in this framework
        // [rfeng] We need to use the client bundle as it's the one that can load A.class
        ServiceTracker tracker = new ServiceTracker(clientBundle.getBundleContext(), A.class.getName(), null);
        tracker.open();
        A serviceA = Utils.cast(tracker.waitForService(SERVICE_TIMEOUT), A.class);

        assertNotNull("Missing test service", serviceA);
        ServiceReference[] refs = tracker.getServiceReferences();
        assertEquals("Unexpected service reference length", 1, refs.length);

        // check service is functional
        assertEquals("Invalid service response", A.A, serviceA.getA());

        // check service is registered with sca config type header
        Object config = refs[0].getProperty(SERVICE_IMPORTED_CONFIGS);
        assertTrue(Utils.propertyToList(config).contains(ORG_OSGI_SCA_CONFIG));
        tracker.close();

        // search for b service which is registered with a fabricated config type
        // assert this is not picked up by the RI
        tracker = new ServiceTracker(clientBundle.getBundleContext(), B.class.getName(), null);
        tracker.open();
        B serviceB = Utils.cast(tracker.waitForService(SERVICE_TIMEOUT), B.class);
        assertIsUnavailable(serviceB);

        tracker.close();
    }

    /**
     * CT.13
     * @throws InterruptedException 
     */
    public void testImportedConfigs() throws InterruptedException {
        // install test bundle in child framework
        BundleContext serverContext = getFramework(SERVER_FRAMEWORK).getBundleContext();
        BundleContext clientContext = getFramework(CLIENT_FRAMEWORK).getBundleContext();

        installAndStartBundle(serverContext, "/ct13.jar");
        // TODO don't technically need to start client bundle but this checks it's resolved
        Bundle clientBundle = installAndStartBundle(clientContext, "/ct13client.jar");

        // wait for test service to be registered in this framework
        ServiceTracker tracker = new ServiceTracker(clientBundle.getBundleContext(), A.class.getName(), null);
        tracker.open();
        A serviceA = Utils.cast(tracker.waitForService(SERVICE_TIMEOUT), A.class);

        assertNotNull("Missing test service", serviceA);
        ServiceReference[] refs = tracker.getServiceReferences();
        assertEquals("Unexpected service reference length", 1, refs.length);

        // check service is functional
        assertEquals("Invalid service response", A.A, serviceA.getA());

        // check service is registered with sca config type header
        Object config = refs[0].getProperty(SERVICE_IMPORTED_CONFIGS);
        assertTrue(Utils.propertyToList(config).contains(ORG_OSGI_SCA_CONFIG));

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
    public void testSupportedIntents() throws Exception {
        // install test bundle in child framework
        BundleContext serverContext = getFramework(SERVER_FRAMEWORK).getBundleContext();
        BundleContext clientContext = getFramework(CLIENT_FRAMEWORK).getBundleContext();

        installAndStartBundle(serverContext, "/ct26.jar");
        // TODO don't technically need to start client bundle but this checks it's resolved
        Bundle clientBundle = installAndStartBundle(clientContext, "/ct26client.jar");

        // wait for test service to be registered in this framework
        ServiceTracker tracker = new ServiceTracker(clientBundle.getBundleContext(), A.class.getName(), null);
        tracker.open();
        A serviceA = Utils.cast(tracker.waitForService(SERVICE_TIMEOUT), A.class);

        assertNotNull("Missing test service", serviceA);
        ServiceReference[] refs = tracker.getServiceReferences();
        assertEquals("Unexpected service reference length", 1, refs.length);

        // check service is functional
        assertEquals("Invalid service response", A.A, serviceA.getA());

        // check service is registered with the intents header
        Object intents = refs[0].getProperty(SERVICE_INTENTS);
        assertFalse(Utils.propertyToList(intents).isEmpty());
        tracker.close();

        // search for b service which is registered with a fabricated intent type
        // assert this is not picked up by the RI
        tracker = new ServiceTracker(clientBundle.getBundleContext(), B.class.getName(), null);
        tracker.open();
        B serviceB = Utils.cast(tracker.waitForService(SERVICE_TIMEOUT), B.class);
        
        assertIsUnavailable(serviceB);

        tracker.close();
    }

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

    private Bundle installAndStartBundle(BundleContext context, String bundle) {
        Bundle b = installBundle(context, bundle);
        try {
            b.start();

        } catch (BundleException e) {
            fail("Failed to start bundle " + bundle, e);
        }
        return b;
    }

    /**
     * @param context
     * @param bundle
     * @return
     */
    private Bundle installBundle(BundleContext context, String bundle) {
        Bundle b = null;

        if (!bundle.startsWith(getWebServer())) {
            bundle = getWebServer() + bundle;
        }

        try {
            URL location = new URL(bundle);
            InputStream inputStream = location.openStream();

            b = context.installBundle(bundle, inputStream);
        } catch (Exception e) {
            fail("Failed to install bundle " + bundle, e);
        }

        assertNotNull(b);

        return b;
    }

    /**
     * Verifies the server side framework that it exports the test packages for the interface
     * used by the test service.
     * @throws Exception
     */
    private void verifyFramework(Framework f) throws Exception {
        ServiceReference sr = f.getBundleContext().getServiceReference(PackageAdmin.class.getName());
        assertNotNull("Framework is not supplying PackageAdmin service", sr);

        PackageAdmin pkgAdmin = (PackageAdmin)f.getBundleContext().getService(sr);
        ExportedPackage[] exportedPkgs = pkgAdmin.getExportedPackages(f.getBundleContext().getBundle());
        assertNotNull(exportedPkgs);
        f.getBundleContext().ungetService(sr);

        boolean found = false;
        for (int i = 0; i < exportedPkgs.length && !found; i++) {
            found = ORG_OSGI_TEST_CASES_SCACONFIGTYPE_COMMON.equals(exportedPkgs[i].getName());
        }
        assertTrue("Framework System Bundle is not exporting package " + ORG_OSGI_TEST_CASES_SCACONFIGTYPE_COMMON,
                   found);
        f.getBundleContext().ungetService(sr);
    }
   
    private void assertAAvailability(Bundle clientBundle, boolean available) throws InterruptedException {
    	if ( available ) {
	        // wait for test service to be registered in this framework
	        // [rfeng] We need to use the client bundle as it's the one that can load A.class
	        ServiceTracker tracker = new ServiceTracker(clientBundle.getBundleContext(), A.class.getName(), null);
	        tracker.open();
	        A serviceA = Utils.cast(tracker.waitForService(SERVICE_TIMEOUT), A.class);
	
	        assertNotNull("Missing test service", serviceA);
	        ServiceReference[] refs = tracker.getServiceReferences();
	        assertEquals("Unexpected service reference length", 1, refs.length);
	
	        // check service is functional
	        assertEquals("Invalid service response", A.A, serviceA.getA());
    	}
    	else {
            // wait for test service to be registered in this framework
            ServiceTracker tracker = new ServiceTracker(clientBundle.getBundleContext(), A.class.getName(), null);
            tracker.open();
            A serviceA = Utils.cast(tracker.waitForService(SERVICE_TIMEOUT), A.class);
            if (serviceA != null) {
                try {
                    serviceA.getA();
                    fail("Unexpected test service A");
                } catch (ServiceException e) {
                    // Expected
                    assertEquals(ServiceException.REMOTE, e.getType());
                }
            }
            tracker.close();
    	}
	}

    private void assertIsUnavailable(B serviceB) {
        if (serviceB != null) {
            try {
                serviceB.getB();
                fail("Unexpected test service B");
            } catch (ServiceException e) {
                // Expected
                assertEquals(ServiceException.REMOTE, e.getType());
            }
        }
	}    

    public Map<String, String> getConfiguration() {
        Map<String, String> configuration = new HashMap<String, String>();
        configuration.put(FRAMEWORK_STORAGE_CLEAN, "true");

        //make sure that the server framework System Bundle exports the interfaces
        String systemPackagesXtra = (String)configuration.get(FRAMEWORK_SYSTEMPACKAGES_EXTRA);
        if (systemPackagesXtra == null) {
            systemPackagesXtra = ORG_OSGI_TEST_CASES_SCACONFIGTYPE_COMMON;
        } else {
            systemPackagesXtra = systemPackagesXtra + "," + ORG_OSGI_TEST_CASES_SCACONFIGTYPE_COMMON;
        }
        configuration.put(FRAMEWORK_SYSTEMPACKAGES_EXTRA, systemPackagesXtra);
        return configuration;
    }
}

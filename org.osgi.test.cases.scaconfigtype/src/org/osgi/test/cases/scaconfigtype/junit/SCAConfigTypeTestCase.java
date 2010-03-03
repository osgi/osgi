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

import java.io.IOException;
import java.net.ServerSocket;
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
import org.osgi.test.cases.scaconfigtype.common.SCAConfigConstants;
import org.osgi.test.cases.scaconfigtype.common.TestConstants;
import org.osgi.test.cases.scaconfigtype.common.TestEvent;
import org.osgi.test.cases.scaconfigtype.common.TestEventHandler;
import org.osgi.test.cases.scaconfigtype.common.Utils;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Tests are documented in the <a href="https://www.osgi.org/members/svn/documents/trunk/rfcs/rfc0119/working_docs/service.scaconfigurationtype.tck.odt">SCA TCK Planning Document</a>
 * 
 * @author <a href="mailto:david.savage@paremus.com">David Savage</a>
 *
 */
public class SCAConfigTypeTestCase extends MultiFrameworkTestCase {

	private static final String	SYSTEM_PACKAGES_EXTRA	= "org.osgi.test.cases.scaconfigtype.system.packages.extra";
	
	private static final String REQUIRED_PORTS = System.getProperty("org.osgi.test.cases.scaconfigtype.required.ports", "");
	
    protected void setUp() throws Exception {
        super.setUp();
        verifySockets();
        // verify that the server framework is exporting the test packages
        verifyFramework(getFramework(SERVER_FRAMEWORK));
    }
    
    

    /**
     * CT.1
     * use endpoint and assert that two services are found vs one
     * @throws Exception
     */
    public void testEndpointLifecycle() throws Exception {		
        // install test bundle in child framework
        BundleContext serverContext = getFramework(SERVER_FRAMEWORK).getBundleContext();
        BundleContext clientContext = getFramework(CLIENT_FRAMEWORK).getBundleContext();

        installAndStartBundle(serverContext, "/ct00.jar");
        // TODO don't technically need to start client bundle but this checks it's resolved
        Bundle clientBundle = installAndStartBundle(clientContext, "/ct00client.jar");

        ServiceTracker tracker = new ServiceTracker(clientBundle.getBundleContext(), A.class.getName(), null);
        // [rfeng] We need to keep the tracker open so that there is matching filter for the service listener
        // when the endpoint bundle is installed
        tracker.open();

        ServiceReference[] refs = lookupA(tracker, true);
        
        assertEquals(1, refs.length);
        
        // install endpoint
        Bundle endpointBundle = installAndStartBundle(clientContext, "/ct01endpoint.jar");

        // Wait for 10 seconds so that 2nd service will be imported into the osgi registry
        Thread.sleep(TestConstants.SERVICE_TIMEOUT);
        
        refs = lookupA(tracker, true);
        
        // assert two services found
        assertEquals(2, refs.length);
        
        // uninstall endpoint
        endpointBundle.uninstall();
        
        Thread.sleep(TestConstants.SERVICE_TIMEOUT);
        
        // assert one service found
        refs = lookupA(tracker, true);
        
        assertEquals(1, refs.length);
        
        tracker.close();
    }

	/**
     * CT.4
     * @throws Exception
     */
    public void testSCAConfigurationManifestHeader() throws Exception {
        // install test bundle in child framework
        BundleContext serverContext = getFramework(SERVER_FRAMEWORK).getBundleContext();
        BundleContext clientContext = getFramework(CLIENT_FRAMEWORK).getBundleContext();

        // TODO don't technically need to start client bundle but this checks it's resolved
        Bundle clientBundle = installAndStartBundle(clientContext, "/ct00client.jar");
        
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

        // assert empty header used
        assertEquals("", serverBundle.getHeaders().get(SCAConfigConstants.SCA_CONFIGURATION_HEADER));
        
        assertAAvailability(clientBundle, true);

        serverBundle.uninstall();

        assertAAvailability(clientBundle, false);
    }

	/**
     * CT.6
     * @throws Exception
     */
    public void testFindEntriesSCAConfigurationManifestHeader() throws Exception {
        // install test bundle in child framework
        BundleContext serverContext = getFramework(SERVER_FRAMEWORK).getBundleContext();
        BundleContext clientContext = getFramework(CLIENT_FRAMEWORK).getBundleContext();

        installAndStartBundle(serverContext, "/ct06.jar");
        // TODO don't technically need to start client bundle but this checks it's resolved
        Bundle clientBundle = installAndStartBundle(clientContext, "/ct00client.jar");

        assertAAvailability(clientBundle, true);
        
        // this bundle should not be found as the config document is in a nested subdirectory
        assertBAvailability(clientBundle, false);
    }

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
        Bundle clientBundle = installAndStartBundle(clientContext, "/ct00client.jar");

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
        Bundle clientBundle = installAndStartBundle(clientContext, "/ct00client.jar");

        ServiceReference[] refs = assertAAvailability(clientBundle, true);
        
        // check service is registered with sca config type header
        Object config = refs[0].getProperty(SERVICE_IMPORTED_CONFIGS);
        assertTrue(Utils.propertyToList(config).contains(ORG_OSGI_SCA_CONFIG));

        // search for b service which is registered with a fabricated config type
        // assert this is not picked up by the RI
        assertBAvailability(clientBundle, false);
    }

    /**
     * CT.13
     * @throws InterruptedException 
     */
    public void testImportedConfigs() throws InterruptedException {
        // install test bundle in child framework
        BundleContext serverContext = getFramework(SERVER_FRAMEWORK).getBundleContext();
        BundleContext clientContext = getFramework(CLIENT_FRAMEWORK).getBundleContext();

        installAndStartBundle(serverContext, "/ct00.jar");
        // TODO don't technically need to start client bundle but this checks it's resolved
        Bundle clientBundle = installAndStartBundle(clientContext, "/ct00client.jar");

        ServiceReference[] refs = assertAAvailability(clientBundle, true);
        
        // check service is registered with sca config type header
        Object config = refs[0].getProperty(SERVICE_IMPORTED_CONFIGS);
        assertTrue(Utils.propertyToList(config).contains(ORG_OSGI_SCA_CONFIG));
    }

    /**
     * CT.14
     * @throws Exception
     */
    // [dsavage] this is covered by other tests
    //	public void testQNameForm() throws Exception {
    //		fail("TODO not yet implemented");
    //	}

    /**
     * CT.15
     * @throws Exception
     */
    public void testNCNameForm() throws Exception {
        // install test bundle in child framework
        BundleContext serverContext = getFramework(SERVER_FRAMEWORK).getBundleContext();
        BundleContext clientContext = getFramework(CLIENT_FRAMEWORK).getBundleContext();

        installAndStartBundle(serverContext, "/ct15.jar");
        // TODO don't technically need to start client bundle but this checks it's resolved
        Bundle clientBundle = installAndStartBundle(clientContext, "/ct00client.jar");
        
        assertAAvailability(clientBundle, true);
    }

    /**
     * CT.23
     * Break xml with invalid characters or remove binding etc – number of possible tests
     * Expected result: service not available
     * @throws Exception
     */
    //	public void testInvalidBindingXML() throws Exception {
    //		fail("TODO this requires a way to mangle the binding files as part of the build");
    //	}

    /**
     * CT.24
     * Create binding twice in same document
	 * Create config bundles that contain:
     *  bind A only
     *  binding A and B
     * Check that B is not exported as A is a duplicate
     * @throws Exception
     */
    //	public void testDuplicateBinding() throws Exception {
    //    fail("TODO this requires a way to modify standard binding files as part of build");
    //	}

    /**
     * CT.25
     * Create valid xml in but giberish test positive and negative cases of mustUnderstand attribute
     * TODO [dsavage] In property value? 
     * @throws Exception
     */
    //	public void testUnknownBinding() throws Exception {
    //		fail("TODO this requires a way to modify standard binding files as part of build");
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
        Bundle clientBundle = installAndStartBundle(clientContext, "/ct00client.jar");

        ServiceReference[] refs = assertAAvailability(clientBundle, true);
        // check service is registered with the intents header
        Object intents = refs[0].getProperty(SERVICE_INTENTS);
        assertFalse(Utils.propertyToList(intents).isEmpty());

        // search for b service which is registered with a fabricated intent type
        // assert this is not picked up by the RI
        assertBAvailability(clientBundle, false);
    }

	/**
     * CT.30
     * @throws Exception
     */
    public void testEndpointImported() throws Exception {
        // install test bundle in child framework
        BundleContext serverContext = getFramework(SERVER_FRAMEWORK).getBundleContext();
        BundleContext clientContext = getFramework(CLIENT_FRAMEWORK).getBundleContext();

        installAndStartBundle(serverContext, "/ct00.jar");

        Bundle clientBundle = installAndStartBundle(clientContext, "/ct26client.jar");
        
        // wait for A to be found
        assertAAvailability(clientBundle, true);
        
        // [rfeng] We need to use the client bundle as it's the one that can load A.class
        ServiceTracker tracker = new ServiceTracker(clientBundle.getBundleContext(), TestEventHandler.class.getName(), null);
        tracker.open();

        TestEventHandler eventHandler = Utils.cast(tracker.waitForService(SERVICE_TIMEOUT), TestEventHandler.class);
        
        assertNotNull(eventHandler);
        
        assertEquals( "Unexpected number of events", 1, eventHandler.getEventCount() );
        
        TestEvent evt = Utils.cast(eventHandler.getNextEvent(), TestEvent.class);
        
        Object configs = evt.getProperty(SERVICE_IMPORTED_CONFIGS);
        assertTrue( "Missing header " + SERVICE_IMPORTED_CONFIGS, configs != null );
        
        assertTrue( Utils.propertyToList(configs).contains( SCAConfigConstants.ORG_OSGI_SCA_CONFIG ) );
        
        Object bindings = evt.getProperty(SCAConfigConstants.ORG_OSGI_SCA_BINDING);
        assertTrue( "Missing header " + SCAConfigConstants.ORG_OSGI_SCA_BINDING, bindings != null );
    }

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
     * Check that this attribute is published - [dsavage] is this a duplicate test?
     * Check each binding type is supported? - TODO [dsavage] not sure this is possible in current test framework
     * Check that bindings supplied by distribution provider for test are included in the list - TODO [dsavage] how to read binding out of xml - hard
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

    private ServiceReference[] assertAAvailability(Bundle clientBundle, boolean available) throws InterruptedException {
        ServiceTracker tracker = new ServiceTracker(clientBundle.getBundleContext(), A.class.getName(), null);
        tracker.open();
        ServiceReference[] refs = lookupA(tracker, available);
        
        tracker.close();
                
        return refs;
	}



    private ServiceReference[] lookupA(ServiceTracker tracker, boolean available) throws InterruptedException {
        // wait for test service to be registered in this framework
        // [rfeng] We need to use the client bundle as it's the one that can load A.class
        A serviceA = Utils.cast(tracker.waitForService(SERVICE_TIMEOUT), A.class);
        
        ServiceReference[] refs = tracker.getServiceReferences();
        
        if ( available ) {	
	        assertNotNull("Missing test service", serviceA);
	        
	        assertTrue("Unexpected service reference length", refs.length >= 1);
	
	        // check service is functional
	        assertEquals("Invalid service response", A.A, serviceA.getA());
    	}
    	else {
            if (serviceA != null) {
                try {
                    serviceA.getA();
                    fail("Unexpected test service A");
                } catch (ServiceException e) {
                    // Expected
                    assertEquals(ServiceException.REMOTE, e.getType());
                }
            }
    	}
        return refs;
    }

    private void assertBAvailability(Bundle clientBundle, boolean available) throws InterruptedException {
        // wait for test service to be registered in this framework
        // [rfeng] We need to use the client bundle as it's the one that can load A.class
        ServiceTracker tracker = new ServiceTracker(clientBundle.getBundleContext(), B.class.getName(), null);
        tracker.open();
        B serviceB = Utils.cast(tracker.waitForService(SERVICE_TIMEOUT), B.class);

    	if ( available ) {
	        assertNotNull("Missing test service", serviceB);
	        ServiceReference[] refs = tracker.getServiceReferences();
	        assertEquals("Unexpected service reference length", 1, refs.length);
	
	        // check service is functional
	        assertEquals("Invalid service response", B.B, serviceB.getB());
    	}
    	else {
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
    	
        tracker.close();
	}
    
    public Map<String, String> getConfiguration() {
        Map<String, String> configuration = new HashMap<String, String>();
        configuration.put(FRAMEWORK_STORAGE_CLEAN, "true");

        //make sure that the server framework System Bundle exports the interfaces
		String systemPackagesXtra = System.getProperty(SYSTEM_PACKAGES_EXTRA);
        if (systemPackagesXtra == null) {
            systemPackagesXtra = ORG_OSGI_TEST_CASES_SCACONFIGTYPE_COMMON;
        } else {
            systemPackagesXtra = systemPackagesXtra + "," + ORG_OSGI_TEST_CASES_SCACONFIGTYPE_COMMON;
        }
        configuration.put(FRAMEWORK_SYSTEMPACKAGES_EXTRA, systemPackagesXtra);
        return configuration;
    }
    
    private void verifySockets() {
    	for ( String p : REQUIRED_PORTS.split(",") ) {
    		if ( p.trim().length() > 0 ) {
    			int port = Integer.parseInt(p.trim());
    			assertSocketAvailable(port);
    		}
    	}
    }
    
    private void assertSocketAvailable(int port) {
    	ServerSocket socket = null;
    	try {
			socket = new ServerSocket(port);
		} catch (IOException e) {
			fail( "Failed to open socket to port " + port, e);
		}
		finally {
			if ( socket != null ) {
				try {
					socket.close();
				} catch (IOException e) {
					fail( "Failed to close socket to port " + port, e);
				}
			}
		}
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
}

/*
 * Copyright (c) 2011 TIBCO Software Inc.
 * All Rights Reserved.
 */

package org.osgi.test.cases.serviceloader.junit;

import java.util.Collection;
import java.util.List;

import junit.framework.AssertionFailedError;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.test.support.OSGiTestCase;

/**
 * TODO Test cases to write:
 * <ul>
 * <li> 134.3.2 provide API in two different versions and have client and server wire to different versions. Ensure that the mediator does not wire them
 * <li> 134.3.3 stop the provider bundle and ensure that the client does not find the provider
 * <li> 134.3.4 client must not see a provider it is not wired to
 * <li> 134.4.2 create service provider w/ advertisement but w/o publishing, ensure client does not find it
 * <li> 134.4.2 different publishing scenarios with custom properties for the OSGi service, wild cards in capability, etc., uses constraints
 * <li> 134.4.2 write a client that uses the service provider from the OSGi registry directly
 * <li> 134.5.1 verify registered provider service
 * </ul>
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 *
 * @since 1.0.0
 */
public class ServiceLoaderServiceClientTest extends OSGiTestCase {
	private Bundle spiBundle;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		spiBundle = install("spi.jar");
		spiBundle.start();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		
		spiBundle.stop();
		spiBundle.uninstall();
	}
	
	/**
	 * Test Case 2.1.1:
	 *  Install a client bundle that doesn't have the requirement on the mediator. Ensure it is not extended (wired).
	 * 
	 * @throws Exception
	 */
	public void testNotExtended() throws Exception {
        Bundle implBundle = install("implexplicitregister.jar");
        implBundle.start();

        try {
        	Bundle clientBundle = install("clientnotextended.jar");
        	assertNotNull(clientBundle);

        	try {
        		clientBundle.start();
        		
        		BundleRevision rev = clientBundle.adapt(BundleRevision.class);
        		List<BundleWire> wires = rev.getWiring().getRequiredWires("osgi.extender");
        		assertNotNull(wires);
        		assertTrue(wires.isEmpty());


        		Collection<ServiceReference<TestBridge>> refs = getContext().getServiceReferences(TestBridge.class, "(test=client)");
        		assertNotNull(refs);
        		assertEquals(1, refs.size());

        		TestBridge service = getContext().getService(refs.iterator().next());
        		assertNotNull("client bundle did not register its service", service);

        		try {
        			service.run();

        			fail("Expecting the client not to find a provider");
        		} catch (AssertionFailedError ex) {
        		}
        	} finally {
        		clientBundle.stop();
        		clientBundle.uninstall();
        	}
        } finally {
        	implBundle.stop();
        	implBundle.uninstall();
        }
	}

    /**
     * TestCase 2.2:
     *  
     * @throws Exception
     */
    public void testServiceClient() throws Exception {
        Bundle implBundle = install("implfirstprovider.jar");
        implBundle.start();

        try {
        	Bundle client = install("client.jar");
        	assertNotNull(client);
        	
        	try {
        		client.start();

        		BundleRevision rev = client.adapt(BundleRevision.class);
        		List<BundleWire> wires = rev.getWiring().getRequiredWires("osgi.extender");
        		assertNotNull(wires);
        		assertFalse(wires.isEmpty());
        		
        		Collection<ServiceReference<TestBridge>> refs = getContext().getServiceReferences(TestBridge.class, "(test=client)");
        		assertNotNull(refs);
        		assertEquals(1, refs.size());

        		TestBridge service = getContext().getService(refs.iterator().next());
        		assertNotNull("client bundle did not register its service", service);

        		service.run();
        	} finally {
        		client.stop();
        		client.uninstall();
        	}
        } finally {
        	implBundle.stop();
        	implBundle.uninstall();
        }
    }

    /**
     * TestCase 2.2:
     *  
     * @throws Exception
     */
    public void testServiceClientWithFilter() throws Exception {
    	Bundle implBundle = install("implfirstprovider.jar");
    	implBundle.start();
    	try {
    		Bundle implBundle2 = install("implsecondprovider.jar");
    		implBundle2.start();

    		try {
    			Bundle client = install("clientfilter.jar");
    			assertNotNull(client);

    			try {
    				client.start();

    				BundleRevision rev = client.adapt(BundleRevision.class);
    				List<BundleWire> wires = rev.getWiring().getRequiredWires("osgi.extender");
    				assertNotNull(wires);
    				assertEquals("expecting exactly one mediator wired", 1, wires.size());
    				
    				wires = rev.getWiring().getRequiredWires("osgi.serviceloader");
    				assertNotNull(wires);
    				assertEquals("expecting 2 providers to be wired", 2, wires.size());

    				Collection<ServiceReference<TestBridge>> refs = getContext().getServiceReferences(TestBridge.class, "(test=client)");
    				assertNotNull(refs);
    				assertEquals(1, refs.size());

    				TestBridge service = getContext().getService(refs.iterator().next());
    				assertNotNull("client bundle did not register its service", service);

    				service.run();
    			} finally {
    				client.stop();
    				client.uninstall();
    			}
    		} finally {
    			implBundle2.stop();
    			implBundle2.uninstall();
    		}
    	} finally {
    		implBundle.stop();
    		implBundle.uninstall();
    	}
    }
}

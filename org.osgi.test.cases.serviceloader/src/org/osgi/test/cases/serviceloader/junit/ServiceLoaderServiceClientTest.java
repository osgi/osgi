/*
 * Copyright (c) 2011 TIBCO Software Inc.
 * All Rights Reserved.
 */

package org.osgi.test.cases.serviceloader.junit;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.AssertionFailedError;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
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
        			service.run("green");

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
        	final Bundle client = install("client.jar");
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

        		service.run("green");
        		
        		// 2.5.1: OPTIONALLY, the client bundle is refreshed when the provider bundle is stopped
        		// thus, there is no assertion here
        		final CountDownLatch latch = new CountDownLatch(1);
        		FrameworkListener listener = new FrameworkListener() {
					
					@Override
					public void frameworkEvent(FrameworkEvent frameworkEvent) {
						if (frameworkEvent.getBundle().equals(client) && frameworkEvent.getType() == FrameworkEvent.PACKAGES_REFRESHED) {
							latch.countDown();
						}
					}
				};
        		getContext().addFrameworkListener(listener);
        		
        		try {
        			implBundle.stop();
        			
        			if (latch.await(5, TimeUnit.SECONDS)) {
        				System.out.println("133.3.4: client bundle was refreshed after provider bundle was stopped");
        			} else {
        				System.out.println("133.3.4: client bundle was not refreshed after provider bundle was stopped");
        			}
        		} finally {
        			getContext().removeFrameworkListener(listener);
        		}
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
     * TestCase 2.2.2:
     * Multiple providers are registered and cardinality:=multiple is set on requirement
     *  
     * @throws Exception
     */
    public void testServiceClientWithFilter() throws Exception {
    	String property = getContext().getProperty("serviceloader.ct.framework.supports.multiple=true");
    	
    	if (Boolean.parseBoolean(property)) {
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

    					service.run("anything");
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
    	} else {
    		System.out.println("Test case testServiceClientWithFilter for cardinality:=multiple is disabled. Set the property serviceloader.ct.framework.supports.multiple=true to enable the test.");
    	}
    }

    /**
     * TestCase 2.2.3:
     * Client bundle restricts potential providers based on filter:="(type=two)"
     *  
     * @throws Exception
     */
    public void testServiceClientWithFilterOne() throws Exception {
    	Bundle implBundle = install("implfirstprovider.jar");
    	implBundle.start();
    	try {
    		Bundle implBundle2 = install("implsecondprovider.jar");
    		implBundle2.start();

    		try {
    			Bundle client = install("clientfilterone.jar");
    			assertNotNull(client);

    			try {
    				client.start();

    				BundleRevision rev = client.adapt(BundleRevision.class);
    				List<BundleWire> wires = rev.getWiring().getRequiredWires("osgi.extender");
    				assertNotNull(wires);
    				assertEquals("expecting exactly one mediator wired", 1, wires.size());
    				
    				wires = rev.getWiring().getRequiredWires("osgi.serviceloader");
    				assertNotNull(wires);
    				assertEquals("expecting 1 provider to be wired", 1, wires.size());

    				Collection<ServiceReference<TestBridge>> refs = getContext().getServiceReferences(TestBridge.class, "(test=client)");
    				assertNotNull(refs);
    				assertEquals(1, refs.size());

    				TestBridge service = getContext().getService(refs.iterator().next());
    				assertNotNull("client bundle did not register its service", service);

    				service.run("red");
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

    /**
     * TestCase 2.3.1:
     * Client bundle restricts potential services based on filter:="(type=two)"
     *  
     * @throws Exception
     */
    public void testServiceClientOSGi() throws Exception {
    	Bundle implBundle = install("implfirstprovider.jar");
    	implBundle.start();
    	try {
    		Bundle implBundle2 = install("implsecondprovider.jar");
    		implBundle2.start();

    		try {
    			Bundle client = install("clientosgi.jar");
    			assertNotNull(client);

    			try {
    				client.start();

    				BundleRevision rev = client.adapt(BundleRevision.class);
    				List<BundleWire> wires = rev.getWiring().getRequiredWires("osgi.extender");
    				assertNotNull(wires);
    				assertEquals("expecting exactly one mediator wired", 1, wires.size());
    				
    				Collection<ServiceReference<TestBridge>> refs = getContext().getServiceReferences(TestBridge.class, "(test=client)");
    				assertNotNull(refs);
    				assertEquals(1, refs.size());

    				TestBridge service = getContext().getService(refs.iterator().next());
    				assertNotNull("client bundle did not register its service", service);

    				service.run("red");
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

    /**
     * TestCase 2.5.2:
     * Client is wired to provider bundle. Stop the provider bundle. The service provided by the stopped bundle
     * does not show up in the iteration.
     *  
     * @throws Exception
     */
    public void testServiceClientLifecycle() throws Exception {
    	Bundle implBundle = install("implfirstprovider.jar");
    	implBundle.start();
    	try {
    		Bundle client = install("clientlifecycle.jar");
    		assertNotNull(client);

    		try {
    			client.start();

    			BundleRevision rev = client.adapt(BundleRevision.class);
    			List<BundleWire> wires = rev.getWiring().getRequiredWires("osgi.extender");
    			assertNotNull(wires);
    			assertEquals("expecting exactly one mediator wired", 1, wires.size());

    			wires = rev.getWiring().getRequiredWires("osgi.serviceloader");
    			assertNotNull(wires);
    			assertEquals("expecting 1 provider to be wired", 1, wires.size());

    			Collection<ServiceReference<TestBridge>> refs = getContext().getServiceReferences(TestBridge.class, "(test=client)");
    			assertNotNull(refs);
    			assertEquals(1, refs.size());

    			TestBridge service = getContext().getService(refs.iterator().next());
    			assertNotNull("client bundle did not register its service", service);

    			// stop the first provider bundle
    			implBundle.stop();

    			// iterator must return no provider, since its only provider was stopped
    			service.run(null);
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
     * Test Case 2.4.1:
     *  Once a client is wired to a bundle because of its dependency on the serviceloader namespace, it
     *  gains access to all service types provided by that bundle (in legacy mode) even though it has only
     *  requested one service type.
     * @throws Exception
     */
    public void testAccessToAllServices() throws Exception {
    	Bundle implBundle = install("implmultiprovider.jar");
    	implBundle.start();
    	
    	try {
    		Bundle client = install("legacyclient.jar");
    		assertNotNull(client);

    		try {
    			client.start();

    			BundleRevision rev = client.adapt(BundleRevision.class);
    			List<BundleWire> wires = rev.getWiring().getRequiredWires("osgi.extender");
    			assertNotNull(wires);
    			assertEquals("expecting exactly one mediator wired", 1, wires.size());

    			wires = rev.getWiring().getRequiredWires("osgi.serviceloader");
    			assertNotNull(wires);
    			assertEquals("expecting 1 provider to be wired", 1, wires.size());

    			Collection<ServiceReference<TestBridge>> refs = getContext().getServiceReferences(TestBridge.class, "(test=client)");
    			assertNotNull(refs);
    			assertEquals(1, refs.size());

    			TestBridge service = getContext().getService(refs.iterator().next());
    			assertNotNull("client bundle did not register its service", service);

    			service.run(null);
    		} finally {
    			client.stop();
    			client.uninstall();
    		}
    		
    	} finally {
    		implBundle.stop();
    		implBundle.uninstall();
    	}
    }
}

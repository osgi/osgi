/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.test.cases.serviceloader.junit;

import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.test.support.sleep.Sleep;

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
public class ServiceLoaderTest extends OSGiTestCase {
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
	 * 3.1.1 Install a provider w/o the requirement on the mediator -> no provider gets registered
	 * 
	 * @throws Exception
	 */
	public void testImplNoExtender() throws Exception {
		Bundle implBundle = install("implnoreq.jar");
		assertNotNull(implBundle);
		
		try {
			implBundle.start();
			
			Sleep.sleep(500); // wait 500 ms in case the extender takes a little to extend this bundle
		
			assertNull("bundle should not be extended and have registered services", implBundle.getRegisteredServices());
		} finally {
			implBundle.uninstall();
		}
	}
	
	/**
	 * 3.1.2 Install a provider and see the registered service, but not from the Mediator
	 * 
	 * @throws Exception
	 */
	public void testImplNotExtended() throws Exception {
		Bundle implBundle = install("implnotextended.jar");
		assertNotNull(implBundle);
		
		try {
			implBundle.start();
			
			Sleep.sleep(500); // wait 500 ms in case the extender takes a little to extend this bundle
			
			ServiceReference<?>[] refs = implBundle.getRegisteredServices();
			assertNotNull(refs);
			assertEquals((long)-1, refs[0].getProperty("serviceloader.mediator"));
			
			BundleRevision rev = implBundle.adapt(BundleRevision.class);
			List<BundleWire> wires = rev.getWiring().getRequiredWires("osgi.extender");
			assertTrue("bundle must not be extended by the mediator as it doesn't have the requirement set", wires.isEmpty());
		} finally {
			implBundle.uninstall();
		}
	}

	/**
	 * 3.2.3 Install a provider with the requirement on the mediator, but no provided capability -> no provider gets registered
	 * 
	 * @throws Exception
	 */
	public void testImplNoCapability() throws Exception {
		Bundle implBundle = install("implnocap.jar");
		assertNotNull(implBundle);
		
		try {
			implBundle.start();
			
			Sleep.sleep(500); // wait 500 ms in case the extender takes a little to extend this bundle
		
			BundleRevision rev = implBundle.adapt(BundleRevision.class);
			List<BundleWire> wires = rev.getWiring().getRequiredWires("osgi.extender");
			assertFalse("bundle must be wired but not extended by the mediator as it doesn't have the capability set", wires.isEmpty());
			
			assertNull("bundle should not be extended and have registered services", implBundle.getRegisteredServices());
		} finally {
			implBundle.uninstall();
		}
	}

	/**
	 * 3.2.2 Install a provider with the requirement on the mediator, but provided capability w/ wildcards-> no provider gets registered
	 * 
	 * @throws Exception
	 */
	public void testImplWildcardCapability() throws Exception {
		Bundle implBundle = install("implwccap.jar");
		assertNotNull(implBundle);
		
		try {
			implBundle.start();
			
			Sleep.sleep(500); // wait 500 ms in case the extender takes a little to extend this bundle
		
			BundleRevision rev = implBundle.adapt(BundleRevision.class);
			List<BundleWire> wires = rev.getWiring().getRequiredWires("osgi.extender");
			assertFalse("bundle must be wired but not extended by the mediator as its capability uses wildacrd", wires.isEmpty());
			
			assertNull("bundle should not be extended and have registered services since its capability uses wildcards", implBundle.getRegisteredServices());
		} finally {
			implBundle.uninstall();
		}
	}
	
	/**
	 * 3.3 Register a provider.
	 * 
	 * @throws Exception
	 */
	public void testAutoRegister() throws Exception {
		Bundle implBundle = install("implautoregister.jar");
		assertNotNull(implBundle);
		
		Bundle mediatorBundle = null;
		
		try {
			implBundle.start();
			
			Sleep.sleep(500); // wait 500 ms in case the extender takes a little to extend this bundle
			
			// find the Mediator bundle
			BundleRevision rev = implBundle.adapt(BundleRevision.class);
			List<BundleWire> wires = rev.getWiring().getRequiredWires("osgi.extender");
			assertNotNull(wires);
			BundleWire wire = wires.get(0);
			assertNotNull(wire);
			mediatorBundle = wire.getProvider().getBundle();
			assertNotNull(mediatorBundle);
			
			ServiceReference<?>[] refs = implBundle.getRegisteredServices();
			assertNotNull(refs);
			assertEquals(2, refs.length);
			assertEquals("expecting serviceloader.mediator property to contain id of mediator bundle", mediatorBundle.getBundleId(), refs[0].getProperty("serviceloader.mediator"));
			assertEquals("public property must be registered with the service", "TCK", refs[0].getProperty("provider"));
			assertEquals("public property must be registered with the service", "TCK", refs[1].getProperty("provider"));
			assertNull("private properties must not be registered with the service", refs[0].getProperty(".hint"));
			assertNull("private properties must not be registered with the service", refs[1].getProperty(".hint"));
			
			implBundle.stop();
			refs = implBundle.getRegisteredServices();
			assertNull("provider services should have been unregistered", refs);
			
			// starting again
			implBundle.start();
			
			Sleep.sleep(500); // wait 500 ms in case the extender takes a little to extend this bundle
			
			refs = implBundle.getRegisteredServices();
			assertNotNull("no services registered", refs);
			assertEquals(2, refs.length);
			assertEquals("expecting serviceloader.mediator property to contain id of mediator bundle", mediatorBundle.getBundleId(), refs[0].getProperty("serviceloader.mediator"));
			assertEquals("public property must be registered with the service", "TCK", refs[0].getProperty("provider"));
			assertEquals("public property must be registered with the service", "TCK", refs[1].getProperty("provider"));
			assertNull("private properties must not be registered with the service", refs[0].getProperty(".hint"));
			assertNull("private properties must not be registered with the service", refs[1].getProperty(".hint"));

			mediatorBundle.stop();
			refs = mediatorBundle.getRegisteredServices();
			assertNull("provider services should have been unregistered when the mediator is stopped", refs);
		} finally {
			implBundle.stop();
			implBundle.uninstall();
			
			if (mediatorBundle != null) { 
				mediatorBundle.start();
			}
		}
	}
	
	/**
	 * 3.3 Register a provider with a single service
	 * 
	 * @throws Exception
	 */
	public void testExplicitRegistration() throws Exception {
		Bundle implBundle = install("implexplicitregister.jar");
		assertNotNull(implBundle);
		
		Bundle mediatorBundle = null;
		
		try {
			implBundle.start();
			
			Sleep.sleep(500); // wait 500 ms in case the extender takes a little to extend this bundle
			
			// find the Mediator bundle
			BundleRevision rev = implBundle.adapt(BundleRevision.class);
			List<BundleWire> wires = rev.getWiring().getRequiredWires("osgi.extender");
			assertNotNull(wires);
			BundleWire wire = wires.get(0);
			assertNotNull(wire);
			mediatorBundle = wire.getProvider().getBundle();
			assertNotNull(mediatorBundle);
			
			ServiceReference<?>[] refs = implBundle.getRegisteredServices();
			assertNotNull(refs);
			assertEquals("only one implementation should have been registered", 1, refs.length);
			assertEquals("expecting serviceloader.mediator property to contain id of mediator bundle", mediatorBundle.getBundleId(), refs[0].getProperty("serviceloader.mediator"));
			assertEquals("public property must be registered with the service", "TCK", refs[0].getProperty("provider"));
			assertNull("private properties must not be registered with the service", refs[0].getProperty(".hint"));

			implBundle.stop();
			refs = implBundle.getRegisteredServices();
			assertNull("provider services should have been unregistered", refs);
			
		} finally {
			implBundle.stop();
			implBundle.uninstall();
		}
	}
	
	/**
	 * The registered service provider has to be a ServiceFactory (133.5.2 in the spec).
	 * 
	 * @throws Exception
	 */
	public void testServiceFactory() throws Exception {
		Bundle implBundle = install("implexplicitregister.jar");
		assertNotNull(implBundle);
		
		Bundle mediatorBundle = null;
		
		try {
			implBundle.start();
			
			Sleep.sleep(500); // wait 500 ms in case the extender takes a little to extend this bundle
			
			// find the Mediator bundle
			BundleRevision rev = implBundle.adapt(BundleRevision.class);
			List<BundleWire> wires = rev.getWiring().getRequiredWires("osgi.extender");
			assertNotNull(wires);
			BundleWire wire = wires.get(0);
			assertNotNull(wire);
			mediatorBundle = wire.getProvider().getBundle();
			assertNotNull(mediatorBundle);
			
			ServiceReference<?>[] refs = implBundle.getRegisteredServices();
			assertNotNull(refs);
			assertEquals(1, refs.length);
			assertEquals("expecting serviceloader.mediator property to contain id of mediator bundle", mediatorBundle.getBundleId(), refs[0].getProperty("serviceloader.mediator"));

			// register 2 client bundles and compare the returned service object to make sure they are different
			Bundle client1 = install("client1.jar");
			Bundle client2 = install("client2.jar");
			
			try {
				client1.start();
				client2.start();

				ServiceReference< ? > ref1 = client1.getBundleContext()
						.getServiceReference(
								"org.osgi.test.cases.serviceloader.spi.ColorProvider");
				assertNotNull(ref1);
				Object service1 = client1.getBundleContext().getService(ref1);

				ServiceReference< ? > ref2 = client2.getBundleContext()
						.getServiceReference(
								"org.osgi.test.cases.serviceloader.spi.ColorProvider");
				assertNotNull(ref2);
				Object service2 = client2.getBundleContext().getService(ref2);

				assertNotSame(
						"provider must be registered as a ServiceFactory and return different objects to different clients",
						service1, service2);
			} finally {
				client2.uninstall();
				client1.uninstall();
			}
		} finally {
			implBundle.stop();
			implBundle.uninstall();
		}
	}
	
//    public void testClient() throws Exception {
//        Bundle implBundle = install("impl.jar");
//        Bundle clientBundle = install("client.jar");
//        
//        implBundle.start();
//        clientBundle.start();
//
//    	Collection<ServiceReference<TestBridge>> refs = getContext().getServiceReferences(TestBridge.class, "(test=client)");
//    	assertNotNull(refs);
//    	assertEquals(1, refs.size());
//    	
//        TestBridge service = getContext().getService(refs.iterator().next());
//        assertNotNull("client bundle did not register its service", service);
//
//        service.run();
//    }
    
}

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

package org.osgi.test.cases.serviceloader.secure.junit;

import java.util.Collection;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.test.cases.serviceloader.secure.export.TestBridge;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.test.support.sleep.Sleep;

public class ServiceLoaderSecureTest extends OSGiTestCase {
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
	 * Install a single provider with permissions to register services. Installs a client looking up the provider as OSGi service.
	 * Expected behavior is the same as in non-secure setup. The client should be wired to the provider and be able to obtain the
	 * provider service.
	 * 
	 * @throws Exception
	 */
	public void testServiceRegistrationWithPermission() throws Exception {
		Bundle implBundle = install("implregister.jar");
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
			assertEquals("public property must be registered with the service", "CT", refs[0].getProperty("provider"));
			assertNull("private properties must not be registered with the service", refs[0].getProperty(".hint"));

			Bundle client = install("clientosgi.jar");
			assertNotNull(client);
			
    		try {
    			client.start();

    			rev = client.adapt(BundleRevision.class);
    			wires = rev.getWiring().getRequiredWires("osgi.extender");
    			assertNotNull(wires);
    			assertEquals("expecting exactly one mediator wired", 1, wires.size());

    			wires = rev.getWiring().getRequiredWires("osgi.serviceloader");
    			assertNotNull(wires);
    			assertEquals("expecting 1 provider to be wired", 1, wires.size());

    			Collection<ServiceReference<TestBridge>> reffs = getContext().getServiceReferences(TestBridge.class, "(test=client)");
    			assertNotNull(reffs);
    			assertEquals(1, reffs.size());

    			TestBridge service = getContext().getService(reffs.iterator().next());
    			assertNotNull("client bundle did not register its service", service);

    			service.run("green");
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
	 * Installs a single provider without service registration permissions. The client is expected not to be able to obtain
	 * the service from the service registry. Otherwise, the mediator would have granted the provider more permissions
	 * than are configured.
	 * 
	 * @throws Exception
	 */
	public void testServiceRegistrationWithOutPermission() throws Exception {
		Bundle implBundle = install("implregister_noperm.jar");
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
			
			Bundle client = install("clientosgi.jar");
			assertNotNull(client);
			
    		try {
    			client.start();

    			rev = client.adapt(BundleRevision.class);
    			wires = rev.getWiring().getRequiredWires("osgi.extender");
    			assertNotNull(wires);
    			assertEquals("expecting exactly one mediator wired", 1, wires.size());

    			wires = rev.getWiring().getRequiredWires("osgi.serviceloader");
    			assertNotNull(wires);
    			assertEquals("expecting 1 provider to be wired", 1, wires.size());

    			Collection<ServiceReference<TestBridge>> reffs = getContext().getServiceReferences(TestBridge.class, "(test=client)");
    			assertNotNull(reffs);
    			assertEquals(1, reffs.size());

    			TestBridge service = getContext().getService(reffs.iterator().next());
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
	
	
	/**
	 * Installs a single provider with service registration permissions and a client bundle without service GET permissions.
	 * The client is expected not to be able to obtain the service from the service registry. Otherwise, the mediator would
	 * have granted the provider more permissions than are configured.
	 * 
	 * @throws Exception
	 */
	public void testClientWithOutPermission() throws Exception {
		Bundle implBundle = install("implregister.jar");
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
			assertEquals("public property must be registered with the service", "CT", refs[0].getProperty("provider"));
			assertNull("private properties must not be registered with the service", refs[0].getProperty(".hint"));

			Bundle client = install("clientosgi_noperm.jar");
			assertNotNull(client);
			
    		try {
    			client.start();

    			rev = client.adapt(BundleRevision.class);
    			wires = rev.getWiring().getRequiredWires("osgi.extender");
    			assertNotNull(wires);
    			assertEquals("expecting exactly one mediator wired", 1, wires.size());

    			wires = rev.getWiring().getRequiredWires("osgi.serviceloader");
    			assertNotNull(wires);
    			assertEquals("expecting 1 provider to be wired", 1, wires.size());

    			Collection<ServiceReference<TestBridge>> reffs = getContext().getServiceReferences(TestBridge.class, "(test=client)");
    			assertNotNull(reffs);
    			assertEquals(1, reffs.size());

    			TestBridge service = getContext().getService(reffs.iterator().next());
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
	
	
	/**
	 * Installs a single provider with service registration permissions and a client bundle with service GET permissions.
	 * The client is expected to be able to obtain the provided service from the ServiceLoader.
	 * 
	 * @throws Exception
	 */
	public void testLegacyClientWithPermission() throws Exception {
		Bundle implBundle = install("implregister.jar");
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
			assertEquals("public property must be registered with the service", "CT", refs[0].getProperty("provider"));
			assertNull("private properties must not be registered with the service", refs[0].getProperty(".hint"));

			Bundle client = install("client.jar");
			assertNotNull(client);
			
    		try {
    			client.start();

    			rev = client.adapt(BundleRevision.class);
    			wires = rev.getWiring().getRequiredWires("osgi.extender");
    			assertNotNull(wires);
    			assertEquals("expecting exactly one mediator wired", 1, wires.size());

    			wires = rev.getWiring().getRequiredWires("osgi.serviceloader");
    			assertNotNull(wires);
    			assertEquals("expecting 1 provider to be wired", 1, wires.size());

    			Collection<ServiceReference<TestBridge>> reffs = getContext().getServiceReferences(TestBridge.class, "(test=client)");
    			assertNotNull(reffs);
    			assertEquals(1, reffs.size());

    			TestBridge service = getContext().getService(reffs.iterator().next());
    			assertNotNull("client bundle did not register its service", service);

    			service.run("green");
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
	 * Installs a single provider with service registration permissions and a client bundle without service GET permissions.
	 * The legacy client is expected not to be able to use the service from the ServiceLoader. Otherwise, the mediator would
	 * have granted the provider more permissions than are configured.
	 * 
	 * @throws Exception
	 */
	public void testLegacyClientWithOutPermission() throws Exception {
		Bundle implBundle = install("implregister.jar");
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
			assertEquals("public property must be registered with the service", "CT", refs[0].getProperty("provider"));
			assertNull("private properties must not be registered with the service", refs[0].getProperty(".hint"));

			Bundle client = install("client_noperm.jar");
			assertNotNull(client);
			
    		try {
    			client.start();

    			rev = client.adapt(BundleRevision.class);
    			wires = rev.getWiring().getRequiredWires("osgi.extender");
    			assertNotNull(wires);
    			assertEquals("expecting exactly one mediator wired", 1, wires.size());

    			wires = rev.getWiring().getRequiredWires("osgi.serviceloader");
    			assertNotNull(wires);
    			assertEquals("expecting 1 provider to be wired", 1, wires.size());

    			Collection<ServiceReference<TestBridge>> reffs = getContext().getServiceReferences(TestBridge.class, "(test=client)");
    			assertNotNull(reffs);
    			assertEquals(1, reffs.size());

    			TestBridge service = getContext().getService(reffs.iterator().next());
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

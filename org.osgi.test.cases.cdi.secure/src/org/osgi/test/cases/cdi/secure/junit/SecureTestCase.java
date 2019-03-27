/**
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
 */

package org.osgi.test.cases.cdi.secure.junit;

import static org.junit.Assert.*;

import java.security.AccessControlContext;
import java.security.AccessControlException;
import java.time.Instant;
import java.util.concurrent.Callable;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;

import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServicePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cdi.runtime.CDIComponentRuntime;
import org.osgi.service.log.LoggerFactory;

public class SecureTestCase extends AbstractTestCase {

	@Before
	public void setUp() throws Exception {
		super.setUp();
		assertNotNull(System.getSecurityManager());
	}

	/*
	 * The CCR implementation must be granted ServicePermission[interfaceName,
	 * GET] to retrieve the CDIComponentRuntime services from the service
	 * registry. Since a Resource may be associated with any service, the RI
	 * needs GET on interfaces '*'.
	 */
	@Test
	public void testImplHasGETServicePermission() throws Exception {
		ServiceReference<CDIComponentRuntime> serviceReference = bundleContext
				.getServiceReference(CDIComponentRuntime.class);

		Bundle bundle = serviceReference.getBundle();

		AccessControlContext acc = bundle.adapt(AccessControlContext.class);

		try {
			acc.checkPermission(new ServicePermission("*", ServicePermission.GET));
		} catch (AccessControlException ace) {
			fail();
		}
	}

	/*
	 * Bundles that need to introspect the state of the CDIComponentRuntime will
	 * need ServicePermission[org.osgi.service.cdi.runtime.CDIComponentRuntime,
	 * GET] to obtain the CDIComponentRuntime service and access the DTO types.
	 */
	@Test
	public void testCDIComponentRuntimeClientHasGETServicePermission_tb1()
			throws Exception {
		Bundle extensionBundle = install("tbextension.jar");
		Bundle bundle = install("tb1.jar");

		try {
			extensionBundle.start();
			bundle.start();

			AccessControlContext acc = bundle.adapt(AccessControlContext.class);

			acc.checkPermission(new ServicePermission(CDIComponentRuntime.class.getName(), ServicePermission.GET));
		} catch (AccessControlException ace) {
			fail();
		} finally {
			bundle.uninstall();
			extensionBundle.uninstall();
		}
	}

	/*
	 * Bundles that need to introspect the state of the CDIComponentRuntime will
	 * need ServicePermission[org.osgi.service.cdi.runtime.CDIComponentRuntime,
	 * GET] to obtain the CDIComponentRuntime service and access the DTO types.
	 */
	@Test
	public void testCDIComponentRuntimeClientHasNoGETServicePermission_tb2()
			throws Exception {
		Bundle bundle = install("tb2.jar");

		try {
			bundle.start();

			AccessControlContext acc = bundle.adapt(AccessControlContext.class);

			acc.checkPermission(
					new ServicePermission(CDIComponentRuntime.class.getName(),
							ServicePermission.GET));

			fail();
		} catch (AccessControlException ace) {
		} finally {
			bundle.uninstall();
		}
	}

	/*
	 * CDI Bundles that depend on Portable Extensions need
	 * ServicePermission[javax.enterprise.inject.spi.Extension, GET] to obtain
	 * the Extension service.
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testExtensionClientHasGETServicePermission_tb1()
			throws Exception {
		Bundle extensionBundle = install("tbextension.jar");
		Bundle bundle = install("tb1.jar");

		try (CloseableTracker<Callable,Callable> tracker = track(
				"(objectClass=%s)", Callable.class.getName())) {
			extensionBundle.start();

			Instant before = Instant.now();

			bundle.start();

			Callable service = tracker.waitForService(4000);

			assertNotNull(service);

			Instant after = Instant.now();

			Instant whenExtensionCalled = (Instant) service.call();

			assertNotNull(whenExtensionCalled);

			assertTrue(whenExtensionCalled.isAfter(before));
			assertTrue(whenExtensionCalled.isBefore(after));

			AccessControlContext acc = bundle.adapt(AccessControlContext.class);

			acc.checkPermission(new ServicePermission(Extension.class.getName(),
					ServicePermission.GET));
		} catch (AccessControlException ace) {
			fail();
		} finally {
			bundle.uninstall();
			extensionBundle.uninstall();
		}
	}

	/*
	 * CDI Bundles that depend on Portable Extensions need
	 * ServicePermission[javax.enterprise.inject.spi.Extension, GET] to obtain
	 * the Extension service.
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testExtensionClientHasNOGETServicePermission_tb5()
			throws Exception {
		Bundle extensionBundle = install("tbextension.jar");
		Bundle bundle = install("tb5.jar");

		try (CloseableTracker<Callable,Callable> tracker = track(
				"(objectClass=%s)", Callable.class.getName())) {
			extensionBundle.start();

			bundle.start();

			Callable service = tracker.waitForService(4000);

			assertNull(service);

			AccessControlContext acc = bundle.adapt(AccessControlContext.class);

			acc.checkPermission(new ServicePermission(Extension.class.getName(),
					ServicePermission.GET));
			fail();
		} catch (AccessControlException ace) {
		} finally {
			bundle.uninstall();
			extensionBundle.uninstall();
		}
	}

	/*
	 * Bundles that refer to services will need ServicePermission[interface,
	 * GET] to obtain the service.
	 */
	@Test
	public void testServiceClientHasGETServicePermission_tb3()
			throws Exception {
		Bundle bundle = install("tb3.jar");

		try {
			bundle.start();

			AccessControlContext acc = bundle.adapt(AccessControlContext.class);

			acc.checkPermission(
					new ServicePermission(java.lang.Integer.class.getName(),
							ServicePermission.GET));
		} catch (AccessControlException ace) {
			fail();
		} finally {
			bundle.uninstall();
		}
	}

	/*
	 * Bundles that refer to services will need ServicePermission[interface,
	 * GET] to obtain the service.
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testServiceClientHasNOGETServicePermission_tb4()
			throws Exception {
		Bundle bundle = install("tb4.jar");

		ServiceRegistration<Integer> registration = bundleContext
				.registerService(Integer.class, new Integer(25), null);

		try (CloseableTracker<Callable,Callable> tracker = track(
				"(objectClass=%s)",
				Callable.class.getName())) {
			bundle.start();

			Callable service = tracker.waitForService(4000);
			assertNull(service);

			AccessControlContext acc = bundle.adapt(AccessControlContext.class);

			acc.checkPermission(new ServicePermission(
					java.lang.Integer.class.getName(), ServicePermission.GET));
			fail();
		} catch (AccessControlException ace) {
		} finally {
			bundle.uninstall();
			registration.unregister();
		}
	}

	/*
	 * CCR must verify the CDI bundle has
	 * ServicePermission[javax.enterprise.inject.spi.BeanManager, REGISTER]
	 * before registering the CDI bundle's BeanManager service.
	 */
	@Test
	public void testHasNOREGISTERBeanManagerServicePermission_tb2()
			throws Exception {
		Bundle bundle = install("tb2.jar");

		try (CloseableTracker<BeanManager,BeanManager> tracker = track(
				"(objectClass=%s)", BeanManager.class.getName())) {

			bundle.start();

			BeanManager beanManager = tracker.waitForService(4000);

			assertNull(beanManager);

			AccessControlContext acc = bundle.adapt(AccessControlContext.class);

			acc.checkPermission(new ServicePermission(
					BeanManager.class.getName(), ServicePermission.REGISTER));
			fail();
		} catch (AccessControlException ace) {
		} finally {
			bundle.uninstall();
		}
	}

	/*
	 * CCR must verify the CDI bundle has
	 * ServicePermission[javax.enterprise.inject.spi.BeanManager, REGISTER]
	 * before registering the CDI bundle's BeanManager service.
	 */
	@Test
	public void testHasREGISTERBeanManagerServicePermission_tb3()
			throws Exception {
		Bundle bundle = install("tb3.jar");
	
		try (CloseableTracker<BeanManager,BeanManager> tracker = track(
				"(objectClass=%s)", BeanManager.class.getName())) {
	
			bundle.start();
	
			BeanManager beanManager = tracker.waitForService(4000);
	
			assertNotNull(beanManager);
	
			AccessControlContext acc = bundle.adapt(AccessControlContext.class);
	
			acc.checkPermission(new ServicePermission(
					BeanManager.class.getName(), ServicePermission.REGISTER));
		} catch (AccessControlException ace) {
			fail();
		} finally {
			bundle.uninstall();
		}
	}

	/*
	 * CCR must verify the CDI bundle has ServicePermission[interface, REGISTER]
	 * before registering a @Service service.
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testHasREGISTERServicePermission_tb2()
			throws Exception {
		Bundle bundle = install("tb2.jar");

		try (CloseableTracker<Callable,Callable> tracker = track(
				"(objectClass=%s)", Callable.class.getName())) {

			bundle.start();

			Callable service = tracker.waitForService(4000);

			assertNotNull(service);

			AccessControlContext acc = bundle.adapt(AccessControlContext.class);

			acc.checkPermission(new ServicePermission(Callable.class.getName(),
					ServicePermission.REGISTER));
		} catch (AccessControlException ace) {
			fail();
		} finally {
			bundle.uninstall();
		}
	}

	/*
	 * CCR must verify the CDI bundle has ServicePermission[interface, REGISTER]
	 * before registering a @Service service.
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testHasNOREGISTERServicePermission_tb3() throws Exception {
		Bundle bundle = install("tb3.jar");

		try (CloseableTracker<Callable,Callable> tracker = track(
				"(objectClass=%s)", Callable.class.getName())) {

			bundle.start();

			Callable service = tracker.waitForService(4000);
			assertNull(service);

			AccessControlContext acc = bundle.adapt(AccessControlContext.class);

			acc.checkPermission(new ServicePermission(Callable.class.getName(),
					ServicePermission.REGISTER));
			fail();
		} catch (AccessControlException ace) {
		} finally {
			bundle.uninstall();
		}
	}

	/*
	 * CCR must verify the CDI bundle has
	 * ServicePermission[org.osgi.service.log.LoggerFactory, GET] before getting
	 * the CDI bundle's LoggerFactory service.
	 */
	@Test
	public void testHasNOGETLoggerFactoryServicePermission_tb2()
			throws Exception {
		Bundle bundle = install("tb2.jar");

		try {
			bundle.start();

			AccessControlContext acc = bundle.adapt(AccessControlContext.class);

			acc.checkPermission(new ServicePermission(
					LoggerFactory.class.getName(), ServicePermission.REGISTER));
			fail();
		} catch (AccessControlException ace) {
		} finally {
			bundle.uninstall();
		}
	}

	/*
	 * CCR must verify the CDI bundle has
	 * ServicePermission[org.osgi.service.log.LoggerFactory, GET] before getting
	 * the CDI bundle's LoggerFactory service.
	 */
	@Test
	public void testHasGETLoggerFactoryServicePermission_tb3()
			throws Exception {
		Bundle bundle = install("tb3.jar");

		try {
			bundle.start();

			AccessControlContext acc = bundle.adapt(AccessControlContext.class);

			acc.checkPermission(new ServicePermission(
					LoggerFactory.class.getName(), ServicePermission.GET));
		} catch (AccessControlException ace) {
			fail();
		} finally {
			bundle.uninstall();
		}
	}

}

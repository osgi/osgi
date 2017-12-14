/*
 * Copyright (c) OSGi Alliance (2013, 2017). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.cdi.junit;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.enterprise.inject.spi.BeanManager;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.util.tracker.ServiceTracker;

public class CDITestCase extends DefaultTestBundleControl {
	/**
	 * Test that a component annotation with @SingleComponent appears in the OSGi
	 * Service Registry when the bundle is started and disappears again when the
	 * bundle is stopped.
	 */
	public void testBundleLifecycle() throws Exception {
		Bundle tb1 = installBundle("tb1.jar", false);

		BundleContext ctx = getContext();
		ServiceTracker<Callable<String>, Callable<String>> st = new ServiceTracker<Callable<String>, Callable<String>>(
				ctx, ctx.createFilter("(&(objectClass=" + Callable.class.getName() + ")(tb=tb1))"), null);
		st.open();
		TestServiceListener tsl = new TestServiceListener();
		ctx.addServiceListener(tsl);

		try {
			assertNull("No service should be found since the bundle was not yet started", st.waitForService(2500));

			tb1.start();
			Callable<String> client = st.waitForService(2500);
			assertEquals("From Foo", client.call());

			ServiceReference<Callable<String>> ref = st.getServiceReference();
			long serviceID = (Long) ref.getProperty(Constants.SERVICE_ID);

			tb1.stop();
			assertTrue("Stopping the bundle should unregister the service",
					tsl.waitForEvent(ServiceEvent.UNREGISTERING, serviceID, 2500));
		} finally {
			st.close();
			tb1.uninstall();
			ctx.removeServiceListener(tsl);
		}
	}

	/**
	 * Test that a component annotation with @SingleComponent appears in the OSGi
	 * Service Registry when the bundle and it's attached fragment is started and
	 * disappears again when the bundle is stopped. This test is similar to the
	 * testBundleLifecycle() except that the CDI components reside in a fragment.
	 */
	public void testFragmentLifecycle() throws Exception {
		Bundle tb3 = installBundle("tb3.jar", false);
		Bundle tf3 = installBundle("tf3.jar", false);

		BundleContext ctx = getContext();
		ServiceTracker<Callable<String>, Callable<String>> st = new ServiceTracker<Callable<String>, Callable<String>>(
				ctx, ctx.createFilter("(&(objectClass=" + Callable.class.getName() + ")(!(tb=*)))"), null);
		st.open();
		TestServiceListener tsl = new TestServiceListener();
		ctx.addServiceListener(tsl);

		try {
			assertNull("No service should be found since the bundle was not yet started", st.waitForService(2500));

			tb3.start();
			Callable<String> client = st.waitForService(2500);
			assertEquals("From Fragment", client.call());

			ServiceReference<Callable<String>> ref = st.getServiceReference();
			assertEquals("Even though registered by a fragment, the service should be reported to come from tb3",
					"org.osgi.test.cases.cdi.tb3", ref.getBundle().getSymbolicName());
			String[] objectClass = (String[]) ref.getProperty(Constants.OBJECTCLASS);
			assertEquals("Should only have registered the implemented interfaces, 1 in this case", 1,
					objectClass.length);
			assertEquals(Callable.class.getName(), objectClass[0]);

			long serviceID = (Long) ref.getProperty(Constants.SERVICE_ID);

			tb3.stop();
			assertTrue("Stopping the bundle should unregister the service",
					tsl.waitForEvent(ServiceEvent.UNREGISTERING, serviceID, 2500));
		} finally {
			st.close();
			tf3.uninstall();
			tb3.uninstall();
			ctx.removeServiceListener(tsl);
		}
	}

	/**
	 * This test ensures that packages that are imported, and have CDI annotations
	 * are not processed as beans. In this case tb4 imports a package from tb2. This
	 * package contains a class with the @SingleComponent annotation but tb2 is not
	 * an OSGi/CDI bundle as it does not have
	 * {@code Require-Capability: osgi.extender; filter:="(osgi.extender=osgi.cdi)"}
	 * . Therefore the component from tb2 should not turned into a CDI bean that is
	 * registered in the OSGi Service Registry.
	 */
	public void testImportedPackageNotProcessed() throws Exception {
		Bundle tb2 = installBundle("tb2.jar");
		Bundle tb4 = installBundle("tb4.jar");

		BundleContext ctx = getContext();
		ServiceTracker<Callable<String>, Callable<String>> st1 = new ServiceTracker<Callable<String>, Callable<String>>(
				ctx, ctx.createFilter("(&(objectClass=" + Callable.class.getName() + ")(tb=tb2))"), null);
		st1.open();
		ServiceTracker<Callable<String>, Callable<String>> st2 = new ServiceTracker<Callable<String>, Callable<String>>(
				ctx, ctx.createFilter("(&(objectClass=" + Callable.class.getName() + ")(tb=tb4))"), null);
		st2.open();

		try {
			Class<?> cls = tb4.loadClass("org.osgi.test.cases.cdi.tb4.pkg4.StaticAccess");
			Method m = cls.getDeclaredMethod("getFromImport");
			assertEquals("Precondition", "From tb2", m.invoke(null));

			Callable<String> client = st2.waitForService(2500);
			assertEquals("From tb4", client.call());

			assertNull("No service should be found since the class was only found via an imported package",
					st1.waitForService(2500));
		} finally {
			st2.close();
			st1.close();
			tb4.uninstall();
			tb2.uninstall();
		}
	}

	/**
	 * Test that a CDI bean is created an registered in the Service Registry if
	 * the
	 * {@code Require-Capability: osgi.extender; filter:="(osgi.extender=osgi.cdi)"}
	 * header is present.
	 */
	public void testRequireCapabilityPresent() throws Exception {
		Bundle tb1 = installBundle("tb1.jar");

		BundleContext ctx = getContext();
		ServiceTracker<Callable<String>, Callable<String>> st = new ServiceTracker<Callable<String>, Callable<String>>(
				ctx, ctx.createFilter("(&(objectClass=" + Callable.class.getName() + ")(tb=tb1))"), null);
		st.open();

		try {
			Callable<String> client = st.waitForService(2500);
			assertEquals("From Foo", client.call());
		} finally {
			st.close();
			tb1.uninstall();
		}
	}

	/**
	 * Test that no CDI bean is registered in the Service Registry if the
	 * {@code Require-Capability: osgi.extender; filter:="(osgi.extender=osgi.cdi)"}
	 * header is not present.
	 */
	public void testNoRequireCapabilityPresent() throws Exception {
		Bundle tb2 = installBundle("tb2.jar");

		BundleContext ctx = getContext();
		ServiceTracker<Callable<String>, Callable<String>> st = new ServiceTracker<Callable<String>, Callable<String>>(
				ctx, ctx.createFilter("(&(objectClass=" + Callable.class.getName() + ")(tb=tb2))"), null);
		st.open();

		try {
			assertNull("No service should be found since the bundle did not opt in via Require-Capability",
					st.waitForService(2500));
		} finally {
			st.close();
			tb2.uninstall();
		}
	}

	/**
	 * Test that the JavaInject and JavaCDI contracts are provided.
	 */
	/*
	 * public void testJavaContracts() throws Exception { Bundle tb5 =
	 * installBundle("tb5.jar");
	 *
	 * BundleContext ctx = getContext(); ServiceTracker<Callable<String>,
	 * Callable<String>> st = new ServiceTracker<Callable<String>,
	 * Callable<String>>( ctx, ctx.createFilter("(&(objectClass=" +
	 * Callable.class.getName() + ")(tb=tb5))"), null); st.open();
	 *
	 * try { Callable<String> client = st.waitForService(30000);
	 * assertEquals("From tb5", client.call()); } finally { st.close();
	 * tb5.uninstall(); } }
	 */

	/**
	 * Test that a CDI bean is created an registered in the Service Registry
	 * without it having an @Component annotation. The beans are marked as
	 * services via the {@code Require-Capability} header.
	 */
	public void testServicesViaManifest() throws Exception {
		Bundle tb1 = installBundle("tb6.jar");

		BundleContext ctx = getContext();
		ServiceTracker<Object, Object> st = new ServiceTracker<Object, Object>(ctx,
				"org.osgi.test.cases.cdi.tb6.intf.ClientInterface", null);
		st.open(true);

		try {
			st.waitForService(2500);

			List<String> results = new ArrayList<String>();
			for (Object svc : st.getServices()) {
				Class<?>[] clientIntf = svc.getClass().getInterfaces();
				assertEquals("Should only implement the ClientInterface interface", 1, clientIntf.length);
				assertEquals("org.osgi.test.cases.cdi.tb6.intf.ClientInterface", clientIntf[0].getName());

				// Invoke reflectively as the test does not have the
				// ClientInterface class
				Method m = svc.getClass().getMethod("callClient");
				String res = (String) m.invoke(svc);
				results.add(res);
			}

			assertEquals(2, results.size());
			assertTrue(results.contains("From tb6_1"));
			assertTrue(results.contains("From tb6_2"));
		} finally {
			st.close();
			tb1.uninstall();
		}
	}

	/**
	 * Test the BeanManager service.
	 */
	public void testBeanManagerService() throws Exception {
		Bundle tb1 = installBundle("tb1.jar");

		BundleContext ctx = getContext();
		ServiceTracker<Callable<String>, Callable<String>> st = new ServiceTracker<Callable<String>, Callable<String>>(
				ctx, ctx.createFilter("(&(objectClass=" + Callable.class.getName() + ")(tb=tb1))"), null);
		st.open();
		ServiceTracker<BeanManager, BeanManager> ccst = new ServiceTracker<BeanManager, BeanManager>(ctx,
				BeanManager.class, null);
		ccst.open();

		try {
			Callable<String> client = st.waitForService(2500);
			assertEquals("Precondition", "From Foo", client.call());

			BeanManager bm = ccst.waitForService(2500);
			assertNotNull("BeanManager service not found", bm);
			// System.out.println("***$$$ " + cc);
			// TODO finish this test once we actually have a BeanManager
			// service...
		} finally {
			ccst.close();
			st.close();
			tb1.uninstall();
		}
	}

	/**
	 * Test that an @Inject @Service will inject a reference to the service
	 */
	public void testInjectService() throws Exception {
		Bundle apiBundle = installBundle("serviceapi.jar");
		Bundle fooBundle = installBundle("servicefoo.jar");
		Bundle clientBundle = installBundle("serviceclient.jar");

		BundleContext ctx = getContext();
		ServiceTracker<Callable<String>, Callable<String>> st = new ServiceTracker<Callable<String>, Callable<String>>(
				ctx, ctx.createFilter("(objectClass="
						+ Callable.class.getName() + ")"), null);
		st.open();

		try {
			Callable<String> client = st.waitForService(2500);
			assertEquals("Precondition", "test", client.call());
		} finally {
			st.close();
			apiBundle.uninstall();
			fooBundle.uninstall();
			clientBundle.uninstall();
		}
	}

	/**
	 * Test that an @Inject @Reference Instance<?> will not see object when the
	 * service dependency is not available.
	 */
	public void testInjectServiceNullObject() throws Exception {
		Bundle apiBundle = installBundle("serviceapi.jar");
		Bundle clientBundle = installBundle("serviceclient.jar");
		Bundle fooBundle = installBundle("servicefoo.jar", false);

		BundleContext ctx = getContext();
		ServiceTracker<Callable<String>, Callable<String>> st = new ServiceTracker<Callable<String>, Callable<String>>(
				ctx, ctx.createFilter("(objectClass="
						+ Callable.class.getName() + ")"), null);
		st.open();

		try {
			Callable<String> client = st.waitForService(2500);
			assertEquals("Precondition", null, client.call());
		} finally {
			st.close();
			apiBundle.uninstall();
			clientBundle.uninstall();
			fooBundle.uninstall();
		}
	}

	/**
	 * Test that an @Inject @Service will not register until it's required
	 * dependency becomes available
	 */
	public void testInjectServiceRequired() throws Exception {
		Bundle apiBundle = installBundle("serviceapi.jar");
		Bundle clientBundle = installBundle("serviceclient.required.jar");
		Bundle fooBundle = installBundle("servicefoo.jar", false);

		BundleContext ctx = getContext();
		ServiceTracker<Callable<String>, Callable<String>> st = new ServiceTracker<Callable<String>, Callable<String>>(
				ctx, ctx.createFilter("(objectClass="
						+ Callable.class.getName() + ")"), null);
		st.open();

		try {
			assertNull(
					"No service should be found since the bundle was not yet started",
					st.waitForService(2500));
			fooBundle.start();

			Callable<String> client = st.waitForService(2500);
			assertEquals("Precondition", "test", client.call());
		} finally {
			st.close();
			apiBundle.uninstall();
			clientBundle.uninstall();
			fooBundle.uninstall();
		}
	}

	/**
	 * Test that an @Inject @Service will unregister from the service registry
	 * when it's required dependency is unregistered. dependency becomes
	 * available
	 */
	public void testInjectServiceRequiredBecomesUnavailable() throws Exception {
		Bundle apiBundle = installBundle("serviceapi.jar");
		Bundle clientBundle = installBundle("serviceclient.required.jar");
		Bundle fooBundle = installBundle("servicefoo.jar");

		BundleContext ctx = getContext();
		ServiceTracker<Callable<String>, Callable<String>> st = new ServiceTracker<Callable<String>, Callable<String>>(
				ctx, ctx.createFilter("(objectClass="
						+ Callable.class.getName() + ")"), null);
		st.open();

		try {

			Callable<String> client = st.waitForService(2500);
			assertEquals("Precondition", "test", client.call());

			fooBundle.stop();

			assertNull(
					"No service should be found since the bundle was not yet started",
					st.waitForService(2500));
		} finally {
			st.close();
			apiBundle.uninstall();
			clientBundle.uninstall();
			fooBundle.uninstall();
		}
	}

	/**
	 * Test that an @Inject will inject a reference to the service if the
	 * Service requirement was added via the Require-Capability.
	 */
	/*
	 * public void testReferencesViaManifest() throws Exception { Bundle
	 * apiBundle = installBundle("serviceapi.jar"); Bundle fooBundle =
	 * installBundle("servicefoo.jar"); Bundle clientBundle =
	 * installBundle("serviceclient.manifest.jar");
	 *
	 * BundleContext ctx = getContext(); ServiceTracker<Object, Object> st = new
	 * ServiceTracker<Object, Object>(ctx, ctx.createFilter(
	 * "(objectClass=org.osgi.test.cases.cdi.serviceclient.manifest.TestInterface)"
	 * ), null); st.open(true);
	 *
	 * try { Object client = st.waitForService(30000); Class<?>[]
	 * clientInterfaces = client.getClass().getInterfaces(); assertEquals(1,
	 * clientInterfaces.length); assertEquals(
	 * "org.osgi.test.cases.cdi.serviceclient.manifest.TestInterface",
	 * clientInterfaces[0].getName());
	 *
	 * Method m = client.getClass().getMethod("invokeIt");
	 * assertEquals("test_via_manifest", m.invoke(client)); } finally {
	 * st.close(); apiBundle.uninstall(); fooBundle.uninstall();
	 * clientBundle.uninstall(); } }
	 */

	static class TestServiceListener implements ServiceListener {
		List<ServiceEvent> events = new ArrayList<ServiceEvent>();

		synchronized void clear() {
			events.clear();
		}

		public synchronized void serviceChanged(ServiceEvent event) {
			events.add(event);
			notifyAll();
		}

		boolean waitForEvent(int type, long serviceID, long timeout) {
			synchronized (this) {
				boolean found;
				while ((found = findEvent(type, serviceID)) == false) {
					try {
						wait(timeout);
					} catch (InterruptedException e) {
						// ignore
					}
				}
				return found;
			}
		}

		private boolean findEvent(int type, long serviceID) {
			for (ServiceEvent ev : events) {
				if (ev.getType() == type
						&& ev.getServiceReference().getProperty(Constants.SERVICE_ID).equals(serviceID)) {
					return true;
				}
			}
			return false;
		}
	}
}

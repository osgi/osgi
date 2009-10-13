/*
 * Copyright (c) OSGi Alliance (2000-2009).
 * All Rights Reserved.
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

package org.osgi.test.cases.tracker.junit;

import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.tracker.service.TestService1;
import org.osgi.test.cases.tracker.service.TestService2;
import org.osgi.test.cases.tracker.service.TestService3;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.compatibility.Semaphore;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class ServiceTrackerTests extends DefaultTestBundleControl {

	public void testOpenClose() throws Exception {
		// 2.23.1 Testcase1 (tc1), tracking a service
		// Tb1 contains service: testservice1
		// Install tb1
		Bundle tb = installBundle("tb1.jar");

		// Creates ServiceTracker object with ServiceReference to testservice1
		ServiceReference sr = getContext().getServiceReference(
				TestService1.NAME);
		ServiceTracker st = new ServiceTracker(getContext(), sr, null);
		st.open();

		try {
			// Call ServiceTracker.size()
			// Should reply 1
			assertEquals(
					"The number of Services being tracked by ServiceTracker is: 1",
					1, st.size());

			// Call ServiceTracker.getServiceReferences()
			ServiceReference[] srs = st.getServiceReferences();
			assertNotNull(
					"ServiceReference for the tracked service can be reached at this time: true",
					srs);
			assertEquals(
					"ServiceReference for the tracked service can be reached at this time: 1",
					1, srs.length);
			// Call ServiceTracker.getService()
			Object ss = st.getService();
			// Call ServiceTracker.getServices()
			Object[] sss = st.getServices();
			// Call ServiceTracker.getService(ServiceReference)
			Object ssr = st.getService(sr);
			// All should be equal and testservice1
			assertSame(
					"Tracked services can be reached at this time and are equal in the different methods",
					ss, sss[0]);
			assertSame(
					"Tracked services can be reached at this time and are equal in the different methods",
					ss, ssr);

			// Call ServiceTracker.close()
			st.close();

			// Call ServiceTracker.getServiceReferences()
			srs = st.getServiceReferences();
			assertNull(
					"No ServiceReferences for tracked services can be reached at this time",
					srs);
			// Call ServiceTracker.getService()
			ss = st.getService();
			// Call ServiceTracker.getServices()
			sss = st.getServices();
			// Call ServiceTracker.getService(ServiceReference)
			ssr = st.getService(sr);
			// All should be null
			assertNull(
					"No Services for tracked services can be reached at this time: true",
					ss);
			assertNull(
					"No Services for tracked services can be reached at this time: true",
					sss);
			assertNull(
					"No Services for tracked services can be reached at this time: true",
					ssr);

			// Call ServiceTracker.size()
			// Should reply 0
			assertEquals(
					"The number of Services being tracked by ServiceTracker is: 0 ",
					0, st.size());

			st.open();

			// Call ServiceTracker.size()
			// Should reply 1
			assertEquals(
					"The number of Services being tracked by ServiceTracker is: 1",
					1, st.size());

			uninstallBundle(tb);
			tb = null;
			// Call ServiceTracker.getServiceReferences()
			srs = st.getServiceReferences();
			assertNull(
					"No ServiceReferences for tracked services can be reached at this time: true",
					srs);
			// Call ServiceTracker.getService()
			ss = st.getService();
			// Call ServiceTracker.getServices()
			sss = st.getServices();
			// Call ServiceTracker.getService(ServiceReference)
			ssr = st.getService(sr);
			// Should reply with null
			assertNull(
					"No Services for tracked services can be reached at this time: true",
					ss);
			assertNull(
					"No Services for tracked services can be reached at this time: true",
					sss);
			assertNull(
					"No Services for tracked services can be reached at this time: true",
					ssr);

			// Call ServiceTracker.size()
			// Should reply 0
			assertEquals(
					"The number of Services being tracked by ServiceTracker is: 0 ",
					0, st.size());
		}
		finally {
			st.close();
			if (tb != null) {
				uninstallBundle(tb);
			}
		}
	}

	public void testWaitForService() throws Exception {

		BundleContext context = getContext();

		// 2.23.2 Testcase2 (tc2), waitforService
		// Tb1 contains service: testservice1
		// Tb2 contains service: testservice2
		// Tb3 contains service: testservice3
		// Install tb1, tb2 and tb3
		Bundle b1 = installBundle("tb1.jar", false);
		Bundle b2 = installBundle("tb2.jar", false);
		Bundle b3 = installBundle("tb3.jar", false);

		// Creates ServiceTracker1 object with testservice1
		// Call ServiceTracker.open()
		ServiceTracker st1 = new ServiceTracker(context, TestService1.NAME,
				null);
		st1.open();
		// Creates ServiceTracker2 object with testservice2
		// Call ServiceTracker.open()
		ServiceTracker st2 = new ServiceTracker(context, TestService2.NAME,
				null);
		st2.open();
		// Creates ServiceTracker3 object with testservice3
		// Call ServiceTracker.open()
		ServiceTracker st3 = new ServiceTracker(context, TestService3.NAME,
				null);
		st3.open();

		try {
			// Call ServiceTracker.size()
			assertEquals(
					"The number of Services being tracked by ServiceTracker 1 is: 0",
					0, st1.size());
			// Call ServiceTracker.size()
			assertEquals(
					"The number of Services being tracked by ServiceTracker 2 is: 0",
					0, st2.size());
			// Call ServiceTracker.size()
			assertEquals(
					"The number of Services being tracked by ServiceTracker 3 is: 0",
					0, st3.size());

			Semaphore s1 = new Semaphore();
			BundleStarter t1 = new BundleStarter(b1, s1);
			t1.start();
			s1.signal();
			Object tt1 = st1.waitForService(0);
			assertNotNull("Returned an object in ServiceTracker 1?:  true", tt1);
			assertEquals(
					"The number of Services being tracked by ServiceTracker 1 is: 1",
					1, st1.size());
			assertEquals(
					"The number of Services being tracked by ServiceTracker 2 is: 0",
					0, st2.size());
			assertEquals(
					"The number of Services being tracked by ServiceTracker 3 is: 0",
					0, st3.size());

			Semaphore s2 = new Semaphore();
			BundleStarter t2 = new BundleStarter(b2, s2);
			t2.start();
			Object tt2 = st2.waitForService(1000);
			assertNull("Returned an object in ServiceTracker 2?: false", tt2);
			assertEquals(
					"The number of Services being tracked by ServiceTracker 1 is: 1",
					1, st1.size());
			assertEquals(
					"The number of Services being tracked by ServiceTracker 2 is: 0",
					0, st2.size());
			assertEquals(
					"The number of Services being tracked by ServiceTracker 3 is: 0",
					0, st3.size());
			s2.signal();

			Semaphore s3 = new Semaphore(1);
			BundleStarter t3 = new BundleStarter(b3, s3);
			t3.start();

			// wait for threads to complete
			st2.waitForService(5000);
			st3.waitForService(5000);

			assertEquals(
					"The number of Services being tracked by ServiceTracker 1 is: 1",
					1, st1.size());
			assertEquals(
					"The number of Services being tracked by ServiceTracker 2 is: 1",
					1, st2.size());
			assertEquals(
					"The number of Services being tracked by ServiceTracker 3 is: 1",
					1, st3.size());
		}
		finally {
			// Call ServiceTracker.close()
			st1.close();
			st2.close();
			st3.close();

			b3.uninstall();
			b2.uninstall();
			b1.uninstall();
		}
	}

	private class BundleStarter extends Thread {
		private final Semaphore	semaphore;
		private final Bundle	bundle;

		BundleStarter(Bundle bundle, Semaphore semaphore) {
			this.bundle = bundle;
			this.semaphore = semaphore;
		}

		public void run() {
			try {
				semaphore.waitForSignal();
				bundle.start();
			}
			catch (Exception e) {
				log(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void testCustomizer() throws Exception {
		BundleContext context = getContext();
		Bundle tb;
		// 2.23.3 Testcase3 (tc3), ServiceTrackerCustomizer
		// Tb1 contains service: testservice1
		// Implement ServiceTrackerCustomizer

		// Create ServiceTracker object with testservice1
		// Call ServiceTracker.open()
		final boolean[] customizerCalled = new boolean[] {false, false, false};
		ServiceTracker st = new ServiceTracker(context, TestService1.class
				.getName(), new ServiceTrackerCustomizer() {
			public java.lang.Object addingService(ServiceReference reference) {
				synchronized (customizerCalled) {
					customizerCalled[0] = true;
				}
				Object obj = getContext().getService(reference);
				return obj;

			}

			public void modifiedService(ServiceReference reference,
					java.lang.Object service) {
				synchronized (customizerCalled) {
					customizerCalled[1] = true;
				}
			}

			public void removedService(ServiceReference reference,
					java.lang.Object service) {
				synchronized (customizerCalled) {
					customizerCalled[2] = true;
				}
				getContext().ungetService(reference);
			}
		});
		st.open();
		try {
			// Call ServiceTracker.size()
			assertEquals(
					"The number of Services being tracked by ServiceTracker 1 is: 0",
					0, st.size());
			// Install tb1
			tb = installBundle("tb1.jar");
			try {
				synchronized (customizerCalled) {
					assertTrue("addingService not called", customizerCalled[0]);
				}
				assertEquals(
						"The number of Services being tracked by ServiceTracker 1 is: 1",
						1, st.size());
				// Addingservice should do something

			}
			finally {
				// Uninstall tb1
				uninstallBundle(tb);
			}
			// RemovedService should do something
			synchronized (customizerCalled) {
				assertTrue("removedService not called", customizerCalled[2]);
			}
		}
		finally {
			// Call ServiceTracker.close()
			st.close();
		}

	}

	public void testRemove() throws Exception {
		BundleContext context = getContext();

		// 2.23.4 Testcase4 (tc4), tracking a classname
		// Tb1 contains service: testservice1, testservice2, testservice3
		// Tb2 contains service: testservice1
		// Tb3 contains service: testservice1
		// Tb4 uses testservice2
		// Creates ServiceTracker object with classname testservice1
		// Call ServiceTracker.open()
		Filter f = context.createFilter("(name=TestService1)");
		ServiceTracker st = new ServiceTracker(context, f, null);
		st.open();

		// Install tb1, tb2, tb3 and tb4
		Bundle tb1 = installBundle("tb1.jar");
		Bundle tb2 = installBundle("tb2.jar");
		Bundle tb3 = installBundle("tb3.jar");
		Bundle tb4 = installBundle("tb4.jar");

		try {
			// Call ServiceTracker.getServiceReferences()
			ServiceReference[] srs = st.getServiceReferences();
			assertNotNull("one bundle registered TestService1", srs);
			assertEquals("one bundle registered TestService1", 1, srs.length);
			assertEquals("tb1 registered TestService1", tb1.getBundleId(),
					srs[0].getBundle().getBundleId());
			// Call ServiceTracker.getServices()
			Object[] os = st.getServices();
			assertNotNull("one registered TestService1", os);
			assertEquals("one registered TestService1", 1, os.length);
			assertTrue("instanceof TestService1", os[0] instanceof TestService1);

			// Call ServiceTracker.size()
			assertEquals(
					"The number of Services being tracked by ServiceTracker 1 is: 1",
					1, st.size());

			ServiceReference sr = context
					.getServiceReference(TestService1.NAME);
			st.remove(sr);
			// Call ServiceTracker.getServiceReferences()
			// Should find tb1, tb3
			srs = st.getServiceReferences();
			assertNull("no TestService1", srs);

			// Call ServiceTracker.getServices()
			// Should find testservice1
			os = st.getServices();
			assertNull("no TestService1", os);

			// Call ServiceTracker.size()
			assertEquals(
					"The number of Services being tracked by ServiceTracker 1 is: 0",
					0, st.size());
		}
		finally {
			// Call ServiceTracker.close()
			st.close();

			uninstallBundle(tb4);
			uninstallBundle(tb3);
			uninstallBundle(tb2);
			uninstallBundle(tb1);
		}
	}

	public void testFilterWithPropertyChanges() throws Exception {
		BundleContext context = getContext();
		// 2.23.5 Testcase5 (tc5), filter match
		// Tb1 contains service: testservice1
		// Call BundleContext.BundleContext.createFilter(String) (testservice1)

		Filter f = context.createFilter("(name=TestService1)");
		// Creates ServiceTracker object with Filter
		ServiceTracker st = new ServiceTracker(context, f, null);
		// Call ServiceTracker.open()
		st.open();
		try {
			// Call ServiceTracker.size()
			// Should reply 0
			assertEquals(
					"The number of Services being tracked by ServiceTracker 1 is: 0",
					0, st.size());
			// Install tb1
			Bundle tb1 = installBundle("tb1.jar");
			try {
				// Call ServiceTracker.size()
				// Should reply 1
				assertEquals(
						"The number of Services being tracked by ServiceTracker 1 is: 1",
						1, st.size());
				// Call ServiceTracker.getServiceReferences()
				// Should find all
				// Call ServiceTracker.getServiceReferences()
				// Should find tb1, tb3
				ServiceReference[] srs = st.getServiceReferences();

				assertNotNull(
						"There were no ServiceReferences in this ServiceTracker.",
						srs);
				for (int i = 0; i < srs.length; i++) {
					assertEquals(
							"The ServiceReferences contains: TestService1",
							"TestService1", srs[i].getProperty("name"));
				}

				// Change property for TestService1 so that the filter doesn't
				// match
				// The only way to change property is to have the
				// ServiceRegistration,
				// i.e. reg a new TestService1
				Hashtable ts1Props = new Hashtable();
				ts1Props.put("name", "TestService1");
				ts1Props.put("version", new Float(1.0));
				ts1Props.put("compatible", new Float(1.0));
				ts1Props.put("description", "TestService 1 in tbc");

				ServiceRegistration tsr1 = context.registerService(
						TestService1.NAME, new TestService1() {
							// empty
						}, ts1Props);

				assertEquals(
						"The number of Services being tracked by ServiceTracker 1 is: 2",
						2, st.size());
				ts1Props.put("name", "TestService1a");
				tsr1.setProperties(ts1Props);
				// Check that the servicetracker doesn't find the TestService
				assertEquals(
						"The number of Services being tracked by ServiceTracker 1 is: 1",
						1, st.size());
				// Change property for tb1 so that the filter match again
				ts1Props.put("name", "TestService1");
				tsr1.setProperties(ts1Props);
				// Check that the servicetracker find TestService
				assertEquals(
						"The number of Services being tracked by ServiceTracker 1 is: 2",
						2, st.size());

				tsr1.unregister();
				assertEquals(
						"The number of Services being tracked by ServiceTracker 1 is: 1",
						1, st.size());
			}
			finally {
				uninstallBundle(tb1);
			}
		}
		finally {
			// Call ServiceTracker.close()
			st.close();
		}
	}

	public void testTrackingCount() throws Exception {
		BundleContext context = getContext();
		ServiceTracker st = new ServiceTracker(context, TestService3.NAME, null);
		assertEquals("ServiceTracker.getTrackingCount() == -1", -1, st
				.getTrackingCount());
		st.open();
		try {
			// Should be 0
			assertEquals("ServiceTracker.getTrackingCount() == 0", 0, st
					.getTrackingCount());

			ServiceRegistration sr = context.registerService(TestService3.NAME,
					new TestService3() {
						// empty
					}, null);
			// Should be 1
			assertEquals("ServiceTracker.getTrackingCount() == 1", 1, +st
					.getTrackingCount());

			sr.unregister();
			// Should be 2
			assertEquals("ServiceTracker.getTrackingCount() == 2", 2, st
					.getTrackingCount());
		}
		finally {
			st.close();
		}
		assertEquals("ServiceTracker.getTrackingCount() == -1", -1, st
				.getTrackingCount());
	}

	public void testServiceTracker01() {
		// simple ServiceTracker test
		Runnable runIt = new Runnable() {
			public void run() {
				// nothing
			}
		};
		Hashtable props = new Hashtable();
		props.put(getName(), Boolean.TRUE);
		ServiceRegistration reg = getContext().registerService(
				Runnable.class.getName(), runIt, props);
		ServiceTracker testTracker = null;
		try {
			final boolean[] results = new boolean[] {false, false, false};
			ServiceTrackerCustomizer testCustomizer = new ServiceTrackerCustomizer() {
				public Object addingService(ServiceReference reference) {
					results[0] = true;
					return reference;
				}

				public void modifiedService(ServiceReference reference,
						Object service) {
					results[1] = true;
				}

				public void removedService(ServiceReference reference,
						Object service) {
					results[2] = true;
				}
			};
			try {
				testTracker = new ServiceTracker(getContext(), FrameworkUtil
						.createFilter("(&(objectClass=java.lang.Runnable)("
								+ getName() + "=true))"), testCustomizer);
			}
			catch (InvalidSyntaxException e) {
				fail("filter error", e);
			}
			testTracker.open();
			assertTrue("Did not call addingService", results[0]);
			assertFalse("Did call modifiedService", results[1]);
			assertFalse("Did call removedService", results[2]);
			clearResults(results);

			// change props to still match
			props.put("testChangeProp", Boolean.FALSE);
			reg.setProperties(props);
			assertFalse("Did call addingService", results[0]);
			assertTrue("Did not call modifiedService", results[1]);
			assertFalse("Did call removedService", results[2]);
			clearResults(results);

			// change props to no longer match
			props.put(getName(), Boolean.FALSE);
			reg.setProperties(props);
			assertFalse("Did call addingService", results[0]);
			assertFalse("Did call modifiedService", results[1]);
			assertTrue("Did not call removedService", results[2]);
			clearResults(results);

			// change props to no longer match
			props.put("testChangeProp", Boolean.TRUE);
			reg.setProperties(props);
			assertFalse("Did call addingService", results[0]);
			assertFalse("Did call modifiedService", results[1]);
			assertFalse("Did call removedService", results[2]);
			clearResults(results);

			// change props back to match
			props.put(getName(), Boolean.TRUE);
			reg.setProperties(props);
			assertTrue("Did not call addingService", results[0]);
			assertFalse("Did call modifiedService", results[1]);
			assertFalse("Did call removedService", results[2]);
			clearResults(results);

		}
		finally {
			if (reg != null)
				reg.unregister();
			if (testTracker != null)
				testTracker.close();
		}
	}

	public void testServiceTracker02() {
		// simple ServiceTracker test
		Runnable runIt = new Runnable() {
			public void run() {
				// nothing
			}
		};
		Hashtable props = new Hashtable();
		props.put(getName(), Boolean.FALSE);
		ServiceRegistration reg = getContext().registerService(
				Runnable.class.getName(), runIt, props);
		ServiceTracker testTracker = null;
		try {
			final boolean[] results = new boolean[] {false, false, false};
			ServiceTrackerCustomizer testCustomizer = new ServiceTrackerCustomizer() {
				public Object addingService(ServiceReference reference) {
					results[0] = true;
					return reference;
				}

				public void modifiedService(ServiceReference reference,
						Object service) {
					results[1] = true;
				}

				public void removedService(ServiceReference reference,
						Object service) {
					results[2] = true;
				}
			};
			try {
				testTracker = new ServiceTracker(getContext(), FrameworkUtil
						.createFilter("(&(objectClass=java.lang.Runnable)("
								+ getName() + "=true))"), testCustomizer);
			}
			catch (InvalidSyntaxException e) {
				fail("filter error", e);
			}
			testTracker.open();
			assertFalse("Did call addingService", results[0]);
			assertFalse("Did call modifiedService", results[1]);
			assertFalse("Did call removedService", results[2]);
			clearResults(results);

			// change props to match
			props.put(getName(), Boolean.TRUE);
			reg.setProperties(props);
			assertTrue("Did not call addingService", results[0]);
			assertFalse("Did call modifiedService", results[1]);
			assertFalse("Did call removedService", results[2]);
			clearResults(results);

			// change props to still match
			props.put("testChangeProp", Boolean.TRUE);
			reg.setProperties(props);
			assertFalse("Did call addingService", results[0]);
			assertTrue("Did not call modifiedService", results[1]);
			assertFalse("Did call removedService", results[2]);
			clearResults(results);

			// change props to no longer match
			props.put(getName(), Boolean.FALSE);
			reg.setProperties(props);
			assertFalse("Did call addingService", results[0]);
			assertFalse("Did call modifiedService", results[1]);
			assertTrue("Did not call removedService", results[2]);
			clearResults(results);

			// change props to no longer match
			props.put("testChangeProp", Boolean.FALSE);
			reg.setProperties(props);
			assertFalse("Did call addingService", results[0]);
			assertFalse("Did call modifiedService", results[1]);
			assertFalse("Did call removedService", results[2]);
			clearResults(results);

		}
		finally {
			if (reg != null)
				reg.unregister();
			if (testTracker != null)
				testTracker.close();
		}
	}

	public void testServiceTracker03() {
		// simple ServiceTracker test
		Runnable runIt = new Runnable() {
			public void run() {
				// nothing
			}
		};
		Hashtable props = new Hashtable();
		props.put(getName(), Boolean.TRUE);
		ServiceRegistration reg = getContext().registerService(
				Runnable.class.getName(), runIt, props);
		ServiceTracker testTracker = null;
		try {
			final boolean[] results = new boolean[] {false, false, false};
			ServiceTrackerCustomizer testCustomizer = new ServiceTrackerCustomizer() {
				public Object addingService(ServiceReference reference) {
					results[0] = true;
					return reference;
				}

				public void modifiedService(ServiceReference reference,
						Object service) {
					results[1] = true;
				}

				public void removedService(ServiceReference reference,
						Object service) {
					results[2] = true;
				}
			};
			try {
				testTracker = new ServiceTracker(getContext(), FrameworkUtil
						.createFilter("(&(objectclass=java.lang.Runnable)("
								+ getName().toLowerCase() + "=true))"),
						testCustomizer);
			}
			catch (InvalidSyntaxException e) {
				fail("filter error", e);
			}
			testTracker.open();
			assertTrue("Did not call addingService", results[0]);
			assertFalse("Did call modifiedService", results[1]);
			assertFalse("Did call removedService", results[2]);
			clearResults(results);

			// change props to not match
			props.put(getName(), Boolean.FALSE);
			reg.setProperties(props);
			assertFalse("Did call addingService", results[0]);
			assertFalse("Did call modifiedService", results[1]);
			assertTrue("Did not call removedService", results[2]);
			clearResults(results);

			// change props to match
			props.put(getName(), Boolean.TRUE);
			reg.setProperties(props);
			assertTrue("Did not call addingService", results[0]);
			assertFalse("Did call modifiedService", results[1]);
			assertFalse("Did call removedService", results[2]);
			clearResults(results);

			// change props to still match
			props.put("testChangeProp", Boolean.FALSE);
			reg.setProperties(props);
			assertFalse("Did call addingService", results[0]);
			assertTrue("Did not call modifiedService", results[1]);
			assertFalse("Did call removedService", results[2]);
			clearResults(results);
		}
		finally {
			if (reg != null)
				reg.unregister();
			if (testTracker != null)
				testTracker.close();
		}
	}

	public void testModifiedRanking() {
		Runnable runIt = new Runnable() {
			public void run() {
				// nothing
			}
		};
		Hashtable props = new Hashtable();
		props.put(getName(), Boolean.TRUE);
		props.put(Constants.SERVICE_RANKING, new Integer(15));
		ServiceRegistration reg1 = getContext().registerService(
				Runnable.class.getName(), runIt, props);
		props.put(Constants.SERVICE_RANKING, new Integer(10));
		ServiceRegistration reg2 = getContext().registerService(
				Runnable.class.getName(), runIt, props);
		ServiceTracker testTracker = null;
		try {
			try {
				testTracker = new ServiceTracker(getContext(), FrameworkUtil
						.createFilter("(&(objectclass=java.lang.Runnable)("
								+ getName().toLowerCase() + "=true))"), null);
			}
			catch (InvalidSyntaxException e) {
				fail("filter error", e);
			}
			testTracker.open();
			assertEquals("wrong service reference", reg1.getReference(),
					testTracker.getServiceReference());

			props.put(Constants.SERVICE_RANKING, new Integer(20));
			reg2.setProperties(props);
			assertEquals("wrong service reference", reg2.getReference(),
					testTracker.getServiceReference());
		}
		finally {
			if (reg1 != null)
				reg1.unregister();
			if (reg2 != null)
				reg2.unregister();
			if (testTracker != null)
				testTracker.close();
		}
	}

	private void clearResults(boolean[] results) {
		for (int i = 0; i < results.length; i++)
			results[i] = false;
	}

}

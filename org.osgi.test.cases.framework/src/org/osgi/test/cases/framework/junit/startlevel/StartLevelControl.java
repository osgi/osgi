/*
 * Copyright (c) OSGi Alliance 2002.
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

package org.osgi.test.cases.framework.junit.startlevel;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.test.support.BundleEventCollector;
import org.osgi.test.support.FrameworkEventCollector;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.sleep.Sleep;

public class StartLevelControl extends DefaultTestBundleControl {
	private static final int ZERO = 0;
	private static final int NEGATIVE = -5;
	private FrameworkStartLevel		fsl;
	private int						ibsl;
	private int						origSl;
	private int						sl_4;
	private int						sl_5;
	private int						sl_6;
	private int						sl_10;
	private int						sl_15;
	private int						sl_20;

	private static int				SLEEP	= 2000;

	private static int				TIMEOUT	= 600000;	// 10 min.
	private FrameworkEventCollector	fec;
	private BundleEventCollector	bec;

	protected void setUp() throws Exception {
		String sleepTimeString = getProperty("osgi.tc.startlevel.sleeptime");
		int sleepTime = SLEEP;
		if (sleepTimeString != null) {
			try {
				sleepTime = Integer.parseInt(sleepTimeString);
			}
			catch (Exception e) {
				e.printStackTrace();
				log("Error while parsing sleep value! The default one will be used : "
						+ SLEEP);
			}
			if (sleepTime < 200) {
				log("The sleep value is too low : " + sleepTime
						+ " ! The default one will be used : " + SLEEP);
			}
			else {
				SLEEP = sleepTime;
			}
		}
		sleepTimeString = getProperty("osgi.tc.startlevel.timeout");
		if (sleepTimeString != null) {
			try {
				TIMEOUT = Integer.parseInt(sleepTimeString);
			}
			catch (Exception e) {
				e.printStackTrace();
				log("Error while parsing timeout value! The default one will be used : "
						+ TIMEOUT);
			}
		}
		fsl = getContext().getBundle(0).adapt(FrameworkStartLevel.class);
		ibsl = fsl.getInitialBundleStartLevel();
		origSl = fsl.getStartLevel();
		int min = ibsl > origSl ? ibsl : origSl;
		sl_4 = min + 4;
		sl_5 = min + 5;
		sl_6 = min + 6;
		sl_10 = min + 10;
		sl_15 = min + 15;
		sl_20 = min + 20;
		fec = new FrameworkEventCollector(FrameworkEvent.STARTLEVEL_CHANGED);
		getContext().addFrameworkListener(fec);
		bec = new BundleEventCollector(BundleEvent.STARTED
				| BundleEvent.STOPPED);
		getContext().addBundleListener(bec);
	}

	protected void tearDown() throws Exception {
		fec.clear();
		fsl.setInitialBundleStartLevel(ibsl);
		fsl.setStartLevel(origSl, (FrameworkListener[]) null);
		fec.getList(1, TIMEOUT);
		getContext().removeFrameworkListener(fec);
		getContext().removeBundleListener(bec);
		fsl = null;
	}

	public void testInitialBundleStartLevel() throws Exception {
		ArrayList<FrameworkEvent> expectedFrameworkEvents = new ArrayList<>();
		ArrayList<BundleEvent> expectedBundleStartEvents = new ArrayList<>();
		ArrayList<BundleEvent> expectedBundleStopEvents = new ArrayList<>();

		fsl.setInitialBundleStartLevel(sl_20);
		assertEquals("getInitialBundleStartLevel", sl_20, fsl
				.getInitialBundleStartLevel());

		fsl.setStartLevel(sl_10, (FrameworkListener[]) null);
		expectedFrameworkEvents.add(new FrameworkEvent(
				FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(0),
				null));
		assertEquals("Received start level changed event", fec.getComparator(),
				expectedFrameworkEvents, fec.getList(expectedFrameworkEvents
						.size(), TIMEOUT));

		Bundle tb1 = getContext().installBundle(getWebServer() + "startlevel.tb1.jar");
		Bundle tb2 = null;
		try {
			tb1.start();
			assertTrue("getState() = INSTALLED | RESOLVED", inState(tb1,
					Bundle.INSTALLED | Bundle.RESOLVED));

			fsl.setInitialBundleStartLevel(sl_10);
			tb2 = getContext().installBundle(getWebServer() + "startlevel.tb2.jar");
			tb2.start();
			expectedBundleStartEvents.add(new BundleEvent(BundleEvent.STARTED,
					tb2));
			assertEquals("Received bundle started event", bec.getComparator(),
					expectedBundleStartEvents, bec.getList(
							expectedBundleStartEvents.size(), TIMEOUT));
			assertTrue("getState() = ACTIVE", inState(tb2, Bundle.ACTIVE));
			tb1.uninstall();
			tb1 = null;
			tb2.stop();
			expectedBundleStopEvents.add(new BundleEvent(BundleEvent.STOPPED,
					tb2));
			assertEquals("Received bundle stopped event", bec.getComparator(),
					expectedBundleStopEvents, bec.getList(
							expectedBundleStopEvents.size(), TIMEOUT));
		}
		finally {
			if (tb1 != null) {
				tb1.uninstall();
			}
			if (tb2 != null) {
				tb2.uninstall();
			}
		}
	}

	public void testStartOrder() throws Exception {
		ArrayList<FrameworkEvent> expectedFrameworkEvents = new ArrayList<>();
		ArrayList<BundleEvent> expectedBundleStartEvents = new ArrayList<>();
		ArrayList<BundleEvent> expectedBundleStopEvents = new ArrayList<>();

		fsl.setInitialBundleStartLevel(sl_20);
		fsl.setStartLevel(sl_10, (FrameworkListener[]) null);
		expectedFrameworkEvents.add(new FrameworkEvent(
				FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(0),
				null));
		assertEquals("Received start level changed event", fec.getComparator(),
				expectedFrameworkEvents, fec.getList(expectedFrameworkEvents
						.size(), TIMEOUT));

		Bundle tb1 = getContext().installBundle(getWebServer() + "startlevel.tb1.jar");
		tb1.start();
		Bundle tb2 = getContext().installBundle(getWebServer() + "startlevel.tb2.jar");
		tb2.start();
		try {
			// start tb1 and tb2
			fsl.setStartLevel(sl_20, (FrameworkListener[]) null);
			assertEquals("Received start level changed event", fec
					.getComparator(), expectedFrameworkEvents, fec.getList(
					expectedFrameworkEvents.size(), TIMEOUT));
			expectedBundleStartEvents.add(new BundleEvent(BundleEvent.STARTED,
					tb1));
			expectedBundleStartEvents.add(new BundleEvent(BundleEvent.STARTED,
					tb2));
			assertEquals("Received bundle started event", bec.getComparator(),
					expectedBundleStartEvents, bec.getListSorted(
							expectedBundleStartEvents.size(), TIMEOUT));

			// stop tb2 and tb1
			fsl.setStartLevel(sl_10, (FrameworkListener[]) null);
			assertEquals("Received start level changed event", fec
					.getComparator(), expectedFrameworkEvents, fec.getList(
					expectedFrameworkEvents.size(), TIMEOUT));
			expectedBundleStopEvents.add(new BundleEvent(BundleEvent.STOPPED,
					tb1));
			expectedBundleStopEvents.add(new BundleEvent(BundleEvent.STOPPED,
					tb2));
			assertEquals("Received bundle stopped event", bec.getComparator(),
					expectedBundleStopEvents, bec.getListSorted(
							expectedBundleStopEvents.size(), TIMEOUT));

			// reverse the start order
			BundleStartLevel bsl = tb2.adapt(BundleStartLevel.class);
			bsl.setStartLevel(sl_15);
			Sleep.sleep(SLEEP);

			// start tb2 and tb1
			fsl.setStartLevel(sl_20, (FrameworkListener[]) null);
			assertEquals("Received start level changed event", fec
					.getComparator(), expectedFrameworkEvents, fec.getList(
					expectedFrameworkEvents.size(), TIMEOUT));
			expectedBundleStartEvents.clear();
			expectedBundleStartEvents.add(new BundleEvent(BundleEvent.STARTED,
					tb2));
			expectedBundleStartEvents.add(new BundleEvent(BundleEvent.STARTED,
					tb1));
			assertEquals("Received bundle started event", bec.getComparator(),
					expectedBundleStartEvents, bec.getList(
							expectedBundleStartEvents.size(), TIMEOUT));

			// stop tb1 and tb2
			fsl.setStartLevel(sl_10, (FrameworkListener[]) null);
			assertEquals("Received start level changed event", fec
					.getComparator(), expectedFrameworkEvents, fec.getList(
					expectedFrameworkEvents.size(), TIMEOUT));
			expectedBundleStopEvents.clear();
			expectedBundleStopEvents.add(new BundleEvent(BundleEvent.STOPPED,
					tb1));
			expectedBundleStopEvents.add(new BundleEvent(BundleEvent.STOPPED,
					tb2));
			assertEquals("Received bundle stopped event", bec.getComparator(),
					expectedBundleStopEvents, bec.getList(
							expectedBundleStopEvents.size(), TIMEOUT));
		}
		finally {
			tb1.uninstall();
			tb2.uninstall();
		}
	}

	public void testSetStartLevel() throws Exception {
		ArrayList<FrameworkEvent> expectedFrameworkEvents = new ArrayList<FrameworkEvent>();
		ArrayList<BundleEvent> expectedBundleStartEvents = new ArrayList<BundleEvent>();
		ArrayList<BundleEvent> expectedBundleStopEvents = new ArrayList<BundleEvent>();

		expectedFrameworkEvents.add(new FrameworkEvent(
				FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(0),
				null));

		FrameworkEventCollector[] testListeners = new FrameworkEventCollector[] {
				new FrameworkEventCollector(-1), new FrameworkEventCollector(-1), new FrameworkEventCollector(-1)
		};

		fsl.setInitialBundleStartLevel(sl_15);
		fsl.setStartLevel(sl_10, testListeners);
		checkFrameworkEvents(expectedFrameworkEvents, testListeners);

		Bundle tb1 = getContext().installBundle(getWebServer() + "startlevel.tb1.jar");

		expectedBundleStartEvents
				.add(new BundleEvent(BundleEvent.STARTED, tb1));
		expectedBundleStopEvents.add(new BundleEvent(BundleEvent.STOPPED, tb1));
		try {
			fsl.setStartLevel(sl_20, testListeners);
			checkFrameworkEvents(expectedFrameworkEvents, testListeners);
			assertTrue("getState() = INSTALLED | RESOLVED", inState(tb1,
					Bundle.INSTALLED | Bundle.RESOLVED));

			fsl.setStartLevel(sl_10, testListeners);
			checkFrameworkEvents(expectedFrameworkEvents, testListeners);
			assertTrue("getState() = INSTALLED | RESOLVED", inState(tb1,
					Bundle.INSTALLED | Bundle.RESOLVED));

			tb1.start();
			assertTrue("getState() = INSTALLED | RESOLVED", inState(tb1,
					Bundle.INSTALLED | Bundle.RESOLVED));

			fsl.setStartLevel(sl_20, testListeners);
			checkFrameworkEvents(expectedFrameworkEvents, testListeners);
			assertTrue("getState() = ACTIVE", inState(tb1, Bundle.ACTIVE));
			assertEquals("Received bundle started event", bec.getComparator(),
					expectedBundleStartEvents, bec.getList(
							expectedBundleStartEvents.size(), TIMEOUT));

			fsl.setStartLevel(sl_10, testListeners);
			checkFrameworkEvents(expectedFrameworkEvents, testListeners);
			assertTrue("getState() = INSTALLED | RESOLVED", inState(tb1,
					Bundle.INSTALLED | Bundle.RESOLVED));
			assertEquals("Received bundle stopped event", bec.getComparator(),
					expectedBundleStopEvents, bec.getList(
							expectedBundleStopEvents.size(), TIMEOUT));

			tb1.stop();
			fsl.setStartLevel(sl_20, testListeners);
			checkFrameworkEvents(expectedFrameworkEvents, testListeners);
			assertTrue("getState() = INSTALLED | RESOLVED", inState(tb1,
					Bundle.INSTALLED | Bundle.RESOLVED));
		}
		finally {
			tb1.uninstall();
		}
	}

	private void checkFrameworkEvents(List<FrameworkEvent> expectedFrameworkEvents, FrameworkEventCollector[] callerFrameworkEvents) {
		assertEquals("Received start level changed event", fec.getComparator(),
				expectedFrameworkEvents, fec.getList(expectedFrameworkEvents
						.size(), TIMEOUT));
		if (callerFrameworkEvents != null) {
			for (int i = 0; i < callerFrameworkEvents.length; i++) {
				assertEquals("Wrong events for caller listeners " + i,
						fec.getComparator(), expectedFrameworkEvents, callerFrameworkEvents[i].getList(expectedFrameworkEvents
								.size(), TIMEOUT));
			}
		}
	}

	public void testSetBundleStartLevel() throws Exception {
		ArrayList<FrameworkEvent> expectedFrameworkEvents = new ArrayList<>();
		ArrayList<BundleEvent> expectedBundleStartEvents = new ArrayList<>();
		ArrayList<BundleEvent> expectedBundleStopEvents = new ArrayList<>();

		expectedFrameworkEvents.add(new FrameworkEvent(
				FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(0),
				null));

		fsl.setInitialBundleStartLevel(sl_15);
		fsl.setStartLevel(sl_10, (FrameworkListener[]) null);
		assertEquals("Received start level changed event", fec.getComparator(),
				expectedFrameworkEvents, fec.getList(expectedFrameworkEvents
						.size(), TIMEOUT));

		Bundle tb1 = getContext().installBundle(getWebServer() + "startlevel.tb1.jar");

		expectedBundleStartEvents
				.add(new BundleEvent(BundleEvent.STARTED, tb1));
		expectedBundleStopEvents.add(new BundleEvent(BundleEvent.STOPPED, tb1));
		try {
			BundleStartLevel bsl = tb1.adapt(BundleStartLevel.class);
			bsl.setStartLevel(sl_5);
			Sleep.sleep(SLEEP);
			assertTrue("Startlevel 10/5 stop", inState(tb1, Bundle.INSTALLED
					| Bundle.RESOLVED));

			bsl.setStartLevel(sl_15);
			Sleep.sleep(SLEEP);
			assertTrue("StartLevel 10/15 stop", inState(tb1, Bundle.INSTALLED
					| Bundle.RESOLVED));

			tb1.start();
			assertTrue("StartLevel 10/15 start", inState(tb1, Bundle.INSTALLED
					| Bundle.RESOLVED));

			bsl.setStartLevel(sl_5);
			assertEquals("Received bundle started event", bec.getComparator(),
					expectedBundleStartEvents, bec.getList(
							expectedBundleStartEvents.size(), TIMEOUT));
			assertTrue("StartLevel 10/5 start", inState(tb1, Bundle.ACTIVE));

			bsl.setStartLevel(sl_15);
			assertEquals("Received bundle stopped event", bec.getComparator(),
					expectedBundleStopEvents, bec.getList(
							expectedBundleStopEvents.size(), TIMEOUT));
			assertTrue("StartLevel 10/15 start", inState(tb1, Bundle.INSTALLED
					| Bundle.RESOLVED));

			bsl.setStartLevel(sl_5);
			assertEquals("Received bundle started event", bec.getComparator(),
					expectedBundleStartEvents, bec.getList(
							expectedBundleStartEvents.size(), TIMEOUT));
			assertTrue("StartLevel 10/5 start", inState(tb1, Bundle.ACTIVE));

			tb1.stop();
			assertEquals("Received bundle stopped event", bec.getComparator(),
					expectedBundleStopEvents, bec.getList(
							expectedBundleStopEvents.size(), TIMEOUT));
			assertTrue("stop", inState(tb1, Bundle.INSTALLED | Bundle.RESOLVED));
		}
		finally {
			tb1.uninstall();
		}
	}

	public void testPersistentlyStarted() throws Exception {
		ArrayList<FrameworkEvent> expectedFrameworkEvents = new ArrayList<>();
		ArrayList<BundleEvent> expectedBundleStartEvents = new ArrayList<>();
		ArrayList<BundleEvent> expectedBundleStopEvents = new ArrayList<>();

		expectedFrameworkEvents.add(new FrameworkEvent(
				FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(0),
				null));

		fsl.setInitialBundleStartLevel(sl_15);
		assertEquals("setInitialBundleStartLevel", sl_15, fsl
				.getInitialBundleStartLevel());

		fsl.setStartLevel(sl_10, (FrameworkListener[]) null);
		assertEquals("Received start level changed event", fec.getComparator(),
				expectedFrameworkEvents, fec.getList(expectedFrameworkEvents
						.size(), TIMEOUT));

		Bundle tb1 = getContext().installBundle(getWebServer() + "startlevel.tb1.jar");

		expectedBundleStartEvents
				.add(new BundleEvent(BundleEvent.STARTED, tb1));
		expectedBundleStopEvents.add(new BundleEvent(BundleEvent.STOPPED, tb1));
		try {
			BundleStartLevel bsl = tb1.adapt(BundleStartLevel.class);
			assertEquals("isBundlePersistentlyStarted", false, bsl
					.isPersistentlyStarted());

			tb1.start();
			assertEquals("isBundlePersistentlyStarted", true, bsl
					.isPersistentlyStarted());

			fsl.setStartLevel(sl_20, (FrameworkListener[]) null);
			assertEquals("Received start level changed event", fec
					.getComparator(), expectedFrameworkEvents, fec.getList(
					expectedFrameworkEvents.size(), TIMEOUT));
			assertEquals("Received bundle started event", bec.getComparator(),
					expectedBundleStartEvents, bec.getList(
							expectedBundleStartEvents.size(), TIMEOUT));
			assertEquals("isBundlePersistentlyStarted", true, bsl
					.isPersistentlyStarted());

			fsl.setStartLevel(sl_10, (FrameworkListener[]) null);
			assertEquals("Received start level changed event", fec
					.getComparator(), expectedFrameworkEvents, fec.getList(
					expectedFrameworkEvents.size(), TIMEOUT));
			assertEquals("Received bundle stopped event", bec.getComparator(),
					expectedBundleStopEvents, bec.getList(
							expectedBundleStopEvents.size(), TIMEOUT));
			assertEquals("isBundlePersistentlyStarted", true, bsl
					.isPersistentlyStarted());

			bsl.setStartLevel(sl_5);
			assertEquals("Received bundle started event", bec.getComparator(),
					expectedBundleStartEvents, bec.getList(
							expectedBundleStartEvents.size(), TIMEOUT));
			assertEquals("isBundlePersistentlyStarted", true, bsl
					.isPersistentlyStarted());

			bsl.setStartLevel(sl_15);
			assertEquals("Received bundle stopped event", bec.getComparator(),
					expectedBundleStopEvents, bec.getList(
							expectedBundleStopEvents.size(), TIMEOUT));
			assertEquals("isBundlePersistentlyStarted", true, bsl
					.isPersistentlyStarted());
		}
		finally {
			tb1.uninstall();
		}
	}

	public void testSystemBundle() throws Exception {
		Bundle systemBundle = getContext().getBundle(0);
		BundleStartLevel bsl = systemBundle.adapt(BundleStartLevel.class);
		assertEquals("getBundleStartLevel", 0, bsl
				.getStartLevel());
		try {
			bsl.setStartLevel(42);
			fail("got no IllegalArgumentException");
		}
		catch (IllegalArgumentException iae) {
			// expected
		}
	}

	public void testExceptionInActivator() throws Exception {
		ArrayList<FrameworkEvent> expectedFrameworkEvents = new ArrayList<>();
		ArrayList<FrameworkEvent> expectedFrameworkError1 = new ArrayList<>();
		ArrayList<FrameworkEvent> expectedFrameworkError2 = new ArrayList<>();

		FrameworkEventCollector fec2 = new FrameworkEventCollector(
				FrameworkEvent.STARTLEVEL_CHANGED | FrameworkEvent.ERROR);
		getContext().addFrameworkListener(fec2);

		try {
			expectedFrameworkEvents.add(new FrameworkEvent(
					FrameworkEvent.STARTLEVEL_CHANGED, getContext()
							.getBundle(0), null));

			fsl.setInitialBundleStartLevel(sl_5);
			fsl.setStartLevel(sl_10, (FrameworkListener[]) null);
			assertEquals("Received start level changed event", fec2
					.getComparator(), expectedFrameworkEvents, fec2.getList(
					expectedFrameworkEvents.size(), TIMEOUT));

			Bundle tb5 = getContext().installBundle(getWebServer() + "startlevel.tb5.jar");
			expectedFrameworkError1.add(new FrameworkEvent(
					FrameworkEvent.ERROR, tb5, new BundleException("")));
			expectedFrameworkError2.add(new FrameworkEvent(
					FrameworkEvent.ERROR, tb5, new BundleException("")));
			expectedFrameworkError2.add(new FrameworkEvent(
					FrameworkEvent.STARTLEVEL_CHANGED, getContext()
							.getBundle(0), null));

			try {
				try {
					tb5.start();
					assertEquals("getState() = ACTIVE", true, inState(tb5,
							Bundle.ACTIVE));

				}
				catch (Exception e) {
					fail("Unexpected exception: " + e.getMessage());
				}

				// FrameworkEvent.ERROR due to active startlevel change
				fsl.setStartLevel(sl_4, (FrameworkListener[]) null);
				assertEquals("Received framework events", fec2.getComparator(),
						expectedFrameworkError2, fec2.getList(
								expectedFrameworkError2.size(), TIMEOUT));
				assertEquals("getState() = INSTALLED | RESOLVED", true,
						inState(tb5, Bundle.INSTALLED | Bundle.RESOLVED));

				// no FrameworkEvent.ERROR
				fsl.setStartLevel(sl_5, (FrameworkListener[]) null);
				assertEquals("Received start level changed event", fec2
						.getComparator(), expectedFrameworkEvents, fec2
						.getList(expectedFrameworkEvents.size(), TIMEOUT));
				assertEquals("getState() = ACTIVE", true, inState(tb5,
						Bundle.ACTIVE));

				// FrameworkEvent.ERROR due to bundle startlevel change
				BundleStartLevel bsl = tb5.adapt(BundleStartLevel.class);
				bsl.setStartLevel(sl_6);
				assertEquals("Received framework events", fec2.getComparator(),
						expectedFrameworkError1, fec2.getList(
								expectedFrameworkError1.size(), TIMEOUT));
				assertEquals("getState() = INSTALLED | RESOLVED", true,
						inState(tb5, Bundle.INSTALLED | Bundle.RESOLVED));
			}
			finally {
				tb5.uninstall();
			}
		}
		finally {
			getContext().removeFrameworkListener(fec2);
		}
	}

	public void testActivatorChangeBundleStartLevel() throws Exception {
		ArrayList<FrameworkEvent> expectedFrameworkEvents = new ArrayList<>();
		ArrayList<BundleEvent> expectedBundleEvents = new ArrayList<>();

		expectedFrameworkEvents.add(new FrameworkEvent(
				FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(0),
				null));

		fsl.setInitialBundleStartLevel(sl_5);
		fsl.setStartLevel(sl_10, (FrameworkListener[]) null);
		assertEquals("Received start level changed event", fec.getComparator(),
				expectedFrameworkEvents, fec.getList(expectedFrameworkEvents
						.size(), TIMEOUT));
		Bundle tb3 = getContext().installBundle(getWebServer() + "startlevel.tb3.jar");
		expectedBundleEvents.add(new BundleEvent(BundleEvent.STARTED, tb3));
		expectedBundleEvents.add(new BundleEvent(BundleEvent.STOPPED, tb3));
		try {
			tb3.start();
			BundleStartLevel bsl = tb3.adapt(BundleStartLevel.class);
			assertEquals("getBundleStartLevel", sl_15, bsl
					.getStartLevel());
			assertEquals("Received bundle events", bec.getComparator(),
					expectedBundleEvents, bec.getListSorted(
							expectedBundleEvents
							.size(), TIMEOUT));
		}
		finally {
			tb3.uninstall();
		}
	}

	public void testActivatorChangeStartLevel() throws Exception {
		ArrayList<FrameworkEvent> expectedFrameworkEvents = new ArrayList<>();
		ArrayList<BundleEvent> expectedBundleStartEvents = new ArrayList<>();
		ArrayList<BundleEvent> expectedBundleStopEvents = new ArrayList<>();

		expectedFrameworkEvents.add(new FrameworkEvent(
				FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(0),
				null));
		fsl.setInitialBundleStartLevel(sl_5);
		fsl.setStartLevel(sl_10, (FrameworkListener[]) null);
		assertEquals("Received start level changed event", fec.getComparator(),
				expectedFrameworkEvents, fec.getList(expectedFrameworkEvents
						.size(), TIMEOUT));
		Bundle tb4 = getContext().installBundle(getWebServer() + "startlevel.tb4.jar");
		expectedBundleStartEvents
				.add(new BundleEvent(BundleEvent.STARTED, tb4));
		expectedBundleStopEvents.add(new BundleEvent(BundleEvent.STOPPED, tb4));
		try {
			tb4.start();
			assertEquals("Received bundle events", bec.getComparator(),
					expectedBundleStartEvents, bec.getList(
							expectedBundleStartEvents.size(), TIMEOUT));
			assertEquals("Received start level changed event", fec
					.getComparator(), expectedFrameworkEvents, fec.getList(
					expectedFrameworkEvents.size(), TIMEOUT));
			assertEquals("getStartLevel", sl_15, fsl.getStartLevel());

			tb4.stop();
			assertEquals("Received bundle events", bec.getComparator(),
					expectedBundleStopEvents, bec.getList(
							expectedBundleStopEvents.size(), TIMEOUT));
			assertEquals("Received start level changed event", fec
					.getComparator(), expectedFrameworkEvents, fec.getList(
					expectedFrameworkEvents.size(), TIMEOUT));
			assertEquals("getStartLevel", sl_10, fsl.getStartLevel());
		}
		finally {
			tb4.uninstall();
		}
	}

	public void testExceptions() throws Exception {
		Bundle tb1 = getContext().installBundle(
				getWebServer() + "startlevel.tb1.jar");
		try {
			try {
				fsl.setInitialBundleStartLevel(ZERO);
				fail("Expected IllegalArgumentException.");
			} catch (IllegalArgumentException e) {
				// expected
			}
			try {
				fsl.setInitialBundleStartLevel(NEGATIVE);
				fail("Expected IllegalArgumentException.");
			} catch (IllegalArgumentException e) {
				// expected
			}
			try {
				fsl.setStartLevel(ZERO);
				fail("Expected IllegalArgumentException.");
			} catch (IllegalArgumentException e) {
				// expected
			}
			try {
				fsl.setStartLevel(NEGATIVE);
				fail("Expected IllegalArgumentException.");
			} catch (IllegalArgumentException e) {
				// expected
			}
			BundleStartLevel bsl = tb1.adapt(BundleStartLevel.class);
			try {
				bsl.setStartLevel(ZERO);
				fail("Expected IllegalArgumentException.");
			} catch (IllegalArgumentException e) {
				// expected
			}
			try {
				bsl.setStartLevel(NEGATIVE);
				fail("Expected IllegalArgumentException.");
			} catch (IllegalArgumentException e) {
				// expected
			}
		} finally {
			tb1.uninstall();
		}
	}

	private boolean inState(Bundle b, int stateMask) {
		return (b.getState() & stateMask) != 0;
	}
}

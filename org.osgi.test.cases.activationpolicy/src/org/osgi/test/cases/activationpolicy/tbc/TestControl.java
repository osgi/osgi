/*
 * $Header$
 * 
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.activationpolicy.tbc;

import org.osgi.framework.*;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;
import org.osgi.test.cases.util.DefaultTestBundleControl;

/**
 * This class contains tests related with the framework class loading policies.
 * 
 * @author left
 * @version $Revision$
 */
public class TestControl extends DefaultTestBundleControl {

	/**
	 * Creates a new instance of TestControl
	 */
	public TestControl() {

	}

	public void compareEvents(Object[] expectedEvents, Object[] actualEvents) {
		assertEquals("number of results", expectedEvents.length, actualEvents.length);
		for (int i = 0; i < actualEvents.length; i++) {
			if (expectedEvents[i] instanceof BundleEvent) {
				BundleEvent expected = (BundleEvent) expectedEvents[i];
				BundleEvent actual = (BundleEvent) actualEvents[i];
				assertEquals("Event Bundles", expected.getBundle(), actual.getBundle());
				assertEquals("Event Type", expected.getType(), actual.getType());
			} else if (expectedEvents[i] instanceof FrameworkEvent) {
				FrameworkEvent expected = (FrameworkEvent) expectedEvents[i];
				FrameworkEvent actual = (FrameworkEvent) expectedEvents[i];
				assertEquals("Event Bundles", expected.getSource(), actual.getSource());
				assertEquals("Event Type", expected.getType(), actual.getType());
			}
		}
	}
	
	/*
	 * Tests a simple lazy policy with no includes or excludes directives
	 */
	public void testActivationPolicy01() throws Exception {
		Bundle tblazy1 = getContext().installBundle(getWebServer() + "tblazy1.jar");
		Bundle tblazy2 = getContext().installBundle(getWebServer() + "tblazy2.jar");
		Bundle tblazy3 = getContext().installBundle(getWebServer() + "tblazy3.jar");
		Bundle tblazy4 = getContext().installBundle(getWebServer() + "tblazy4.jar");

		EventListenerTestResults resultsListener = new SyncEventListenerTestResults(BundleEvent.STARTED | BundleEvent.STOPPED);
		getContext().addBundleListener(resultsListener);
		try {
			tblazy1.loadClass("org.osgi.test.cases.activationpolicy.tblazy1.LazySimple").newInstance();
	
			// The bundle must have been activated now
			Object[] expectedEvents = new Object[1];
			expectedEvents[0] = new BundleEvent(BundleEvent.STARTED, tblazy2);
			Object[] actualEvents = resultsListener.getResults(1);
			compareEvents(expectedEvents, actualEvents);
		} finally {
			getContext().removeBundleListener(resultsListener);
			uninstallBundle(tblazy1);
			uninstallBundle(tblazy2);
			uninstallBundle(tblazy3);
			uninstallBundle(tblazy4);
		}
	}

	/*
	 * Tests a bundle with the lazy activation policy and an excludes directive
	 */
	public void testActivationPolicy02() throws Exception {
		Bundle tblazy1 = getContext().installBundle(getWebServer() + "tblazy1.jar");
		Bundle tblazy2 = getContext().installBundle(getWebServer() + "tblazy2.jar");
		Bundle tblazy3 = getContext().installBundle(getWebServer() + "tblazy3.jar");
		Bundle tblazy4 = getContext().installBundle(getWebServer() + "tblazy4.jar");

		EventListenerTestResults resultsListener = new SyncEventListenerTestResults(BundleEvent.STARTED | BundleEvent.STOPPED);
		getContext().addBundleListener(resultsListener);
		try {
			// First load a class that depends on a class included in an excludes package
			tblazy1.loadClass("org.osgi.test.cases.activationpolicy.tblazy1.LazyExclude1").newInstance();
			// this should result in no STARTED event
			Object[] expectedEvents = new Object[0];
			Object[] actualEvents = resultsListener.getResults(0);
			compareEvents(expectedEvents, actualEvents);

			// Now load a class that was not included in an excludes package
			tblazy1.loadClass("org.osgi.test.cases.activationpolicy.tblazy1.LazyExclude2").newInstance();
			// this should result in a STARTED event for tblazy3 bundle
			expectedEvents = new Object[1];
			expectedEvents[0] = new BundleEvent(BundleEvent.STARTED, tblazy3);
			actualEvents = resultsListener.getResults(1);
			compareEvents(expectedEvents, actualEvents);
		} finally {
			getContext().removeBundleListener(resultsListener);
			uninstallBundle(tblazy1);
			uninstallBundle(tblazy2);
			uninstallBundle(tblazy3);
			uninstallBundle(tblazy4);
		}
	}
	/*
	 * Tests a bundle with the lazy activation policy and an includes directive
	 */
	public void testActivationPolicy03() throws Exception {
		Bundle tblazy1 = getContext().installBundle(getWebServer() + "tblazy1.jar");
		Bundle tblazy2 = getContext().installBundle(getWebServer() + "tblazy2.jar");
		Bundle tblazy3 = getContext().installBundle(getWebServer() + "tblazy3.jar");
		Bundle tblazy4 = getContext().installBundle(getWebServer() + "tblazy4.jar");

		EventListenerTestResults resultsListener = new SyncEventListenerTestResults(BundleEvent.STARTED | BundleEvent.STOPPED);
		getContext().addBundleListener(resultsListener);
		try {
			// first load a class that depends on a class that was not included in an includes package
			tblazy1.loadClass("org.osgi.test.cases.activationpolicy.tblazy1.LazyInclude1").newInstance();
			// this should result in no STARTED event
			Object[] expectedEvents = new Object[0];
			Object[] actualEvents = resultsListener.getResults(0);
			compareEvents(expectedEvents, actualEvents);

			// now load a class that depends on a class that is included in an includes package
			tblazy1.loadClass("org.osgi.test.cases.activationpolicy.tblazy1.LazyInclude2").newInstance();
			// this should result in a STARTED event
			expectedEvents = new Object[1];
			expectedEvents[0] = new BundleEvent(BundleEvent.STARTED, tblazy4);
			actualEvents = resultsListener.getResults(1);
			compareEvents(expectedEvents, actualEvents);
		} finally {
			getContext().removeBundleListener(resultsListener);
			uninstallBundle(tblazy1);
			uninstallBundle(tblazy2);
			uninstallBundle(tblazy3);
			uninstallBundle(tblazy4);
		}
	}

	/*
	 * Tests the lazy activation policy in relation to the start-level service.
	 */
	public void testActivationPolicy04() throws Exception {
		PackageAdmin pa = (PackageAdmin) getService(PackageAdmin.class);
		StartLevel startLevel = (StartLevel) getService(StartLevel.class);
		int initialSL = startLevel.getStartLevel();
		int initialBSL = startLevel.getInitialBundleStartLevel();
		startLevel.setInitialBundleStartLevel(initialSL + 10);
		Bundle tblazy2 = getContext().installBundle(getWebServer() + "tblazy2.jar");
		if (pa != null)
			pa.resolveBundles(new Bundle[] {tblazy2});
		EventListenerTestResults resultsListener = new SyncEventListenerTestResults(BundleEvent.STARTED | BundleEvent.LAZY_ACTIVATION | BundleEvent.STARTING | BundleEvent.STOPPING | BundleEvent.STOPPED);
		getContext().addBundleListener(resultsListener);
		EventListenerTestResults startlevelListener = new EventListenerTestResults(FrameworkEvent.STARTLEVEL_CHANGED);
		getContext().addFrameworkListener(startlevelListener);
		try {
			// crank up the framework start-level.  This should result in no STARTED event
			startLevel.setStartLevel(startLevel.getStartLevel() + 15);
			Object[] expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(), null);
			Object[] actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);
		
			startLevel.setStartLevel(startLevel.getStartLevel() - 15);
			expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(), null);
			actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);

			// we expect a LAZY_ACTIVATION, STOPPING, STOPPED event to be sent here because we met the start-level,
			// but no STARTED event should be fired because nothing triggered the bundle to activate
			Object[] expectedEvents = new Object[3];
			expectedEvents[0] = new BundleEvent(BundleEvent.LAZY_ACTIVATION, tblazy2);
			expectedEvents[1] = new BundleEvent(BundleEvent.STOPPING, tblazy2);
			expectedEvents[2] = new BundleEvent(BundleEvent.STOPPED, tblazy2);
			Object[] actualEvents = resultsListener.getResults(3);
			compareEvents(expectedEvents, actualEvents);

			// now load a class from it before the start-level is met.  This should result in no STARTED event
			tblazy2.loadClass("org.osgi.test.cases.activationpolicy.tblazy2.ATest");
			expectedEvents = new Object[0];
			actualEvents = resultsListener.getResults(0);
			compareEvents(expectedEvents, actualEvents);

			startLevel.setStartLevel(startLevel.getStartLevel() + 15);
			expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(), null);
			actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);

			startLevel.setStartLevel(startLevel.getStartLevel() - 15);
			expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(), null);
			actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);

			// we expect a LAZY_ACTIVATION, STOPPING, STOPPED event to be sent here because we met the start-level,
			// but no STARTED event because the trigger for start happened before we the start-level was met
			expectedEvents = new Object[3];
			expectedEvents[0] = new BundleEvent(BundleEvent.LAZY_ACTIVATION, tblazy2);
			expectedEvents[1] = new BundleEvent(BundleEvent.STOPPING, tblazy2);
			expectedEvents[2] = new BundleEvent(BundleEvent.STOPPED, tblazy2);
			actualEvents = resultsListener.getResults(3);
			compareEvents(expectedEvents, actualEvents);

			// now load a class while start-level is met.
			startLevel.setStartLevel(startLevel.getStartLevel() + 15);
			expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(), null);
			actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);

			tblazy2.loadClass("org.osgi.test.cases.activationpolicy.tblazy2.ATest");
			// Check for the proper events, STARTED should be fired here because the start-level was met
			expectedEvents = new Object[3];
			expectedEvents[0] = new BundleEvent(BundleEvent.LAZY_ACTIVATION, tblazy2);
			expectedEvents[1] = new BundleEvent(BundleEvent.STARTING, tblazy2);
			expectedEvents[2] = new BundleEvent(BundleEvent.STARTED, tblazy2);
			actualEvents = resultsListener.getResults(3);
			compareEvents(expectedEvents, actualEvents);

			assertTrue("bundle is persistently started!!", !startLevel.isBundlePersistentlyStarted(tblazy2));

			startLevel.setStartLevel(startLevel.getStartLevel() - 15);
			expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(), null);
			actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);

			// make sure the bundle was stopped
			expectedEvents = new Object[2];
			expectedEvents[0] = new BundleEvent(BundleEvent.STOPPING, tblazy2);
			expectedEvents[1] = new BundleEvent(BundleEvent.STOPPED, tblazy2);
			actualEvents = resultsListener.getResults(2);
			compareEvents(expectedEvents, actualEvents);
		} finally {
			getContext().removeBundleListener(resultsListener);
			uninstallBundle(tblazy2);
			startLevel.setStartLevel(initialSL);
			startLevel.setInitialBundleStartLevel(initialBSL);
		}
	}

	/*
	 * Tests Bundle.start(START_TRANSIENT) in relation to the start-level service
	 */
	public void testActivationPolicy05() throws Exception {
		PackageAdmin pa = (PackageAdmin) getService(PackageAdmin.class);
		StartLevel startLevel = (StartLevel) getService(StartLevel.class);
		int initialSL = startLevel.getStartLevel();
		int initialBSL = startLevel.getInitialBundleStartLevel();
		startLevel.setInitialBundleStartLevel(initialSL + 10);
		Bundle tblazy2 = getContext().installBundle(getWebServer() + "tblazy2.jar");
		if (pa != null)
			pa.resolveBundles(new Bundle[] {tblazy2});
		EventListenerTestResults resultsListener = new SyncEventListenerTestResults(BundleEvent.STARTED | BundleEvent.LAZY_ACTIVATION | BundleEvent.STARTING | BundleEvent.STOPPING | BundleEvent.STOPPED);
		getContext().addBundleListener(resultsListener);
		EventListenerTestResults startlevelListener = new EventListenerTestResults(FrameworkEvent.STARTLEVEL_CHANGED);
		getContext().addFrameworkListener(startlevelListener);
		try {
			// crank up the framework start-level.  This should result in no STARTED event
			startLevel.setStartLevel(startLevel.getStartLevel() + 15);
			Object[] expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(), null);
			Object[] actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);
		
			startLevel.setStartLevel(startLevel.getStartLevel() - 15);
			expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(), null);
			actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);

			// we expect a LAZY_ACTIVATION, STOPPING, STOPPED event to be sent here because we met the start-level
			Object[] expectedEvents = new Object[3];
			expectedEvents[0] = new BundleEvent(BundleEvent.LAZY_ACTIVATION, tblazy2);
			expectedEvents[1] = new BundleEvent(BundleEvent.STOPPING, tblazy2);
			expectedEvents[2] = new BundleEvent(BundleEvent.STOPPED, tblazy2);
			Object[] actualEvents = resultsListener.getResults(3);
			compareEvents(expectedEvents, actualEvents);

			// now call start(START_TRANSIENT) before the start-level is met.  This should result in no STARTED event
			try {
				tblazy2.start(Bundle.START_TRANSIENT);
				assertTrue("Bundle is started!!", tblazy2.getState() != Bundle.ACTIVE);
			} catch (BundleException e) {
				// expected
			}
			assertTrue("bundle is persistently started!!", !startLevel.isBundlePersistentlyStarted(tblazy2));

			expectedEvents = new Object[0];
			actualEvents = resultsListener.getResults(0);
			compareEvents(expectedEvents, actualEvents);

			startLevel.setStartLevel(startLevel.getStartLevel() + 15);
			expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(), null);
			actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);

			startLevel.setStartLevel(startLevel.getStartLevel() - 15);
			expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(), null);
			actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);

			// we expect a LAZY_ACTIVATION, STOPPING, STOPPED event to be sent here because we met the start-level
			// but no STARTED event because the transient start was called before the start-level was met
			expectedEvents = new Object[3];
			expectedEvents[0] = new BundleEvent(BundleEvent.LAZY_ACTIVATION, tblazy2);
			expectedEvents[1] = new BundleEvent(BundleEvent.STOPPING, tblazy2);
			expectedEvents[2] = new BundleEvent(BundleEvent.STOPPED, tblazy2);
			actualEvents = resultsListener.getResults(3);
			compareEvents(expectedEvents, actualEvents);

			// now call start(START_TRANSIENT) while start-level is met.
			startLevel.setStartLevel(startLevel.getStartLevel() + 15);
			expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(), null);
			actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);

			tblazy2.start(Bundle.START_TRANSIENT);
			assertTrue("bundle is persistently started!!", !startLevel.isBundlePersistentlyStarted(tblazy2));

			// we expect a LAZY_ACTIVATION event here because the start-level was met before we called Bundle.start method.
			expectedEvents = new Object[3];
			expectedEvents[0] = new BundleEvent(BundleEvent.LAZY_ACTIVATION, tblazy2);
			expectedEvents[1] = new BundleEvent(BundleEvent.STARTING, tblazy2);
			expectedEvents[2] = new BundleEvent(BundleEvent.STARTED, tblazy2);
			actualEvents = resultsListener.getResults(3);
			compareEvents(expectedEvents, actualEvents);

			startLevel.setStartLevel(startLevel.getStartLevel() - 15);
			expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(), null);
			actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);

			expectedEvents = new Object[2];
			expectedEvents[0] = new BundleEvent(BundleEvent.STOPPING, tblazy2);
			expectedEvents[1] = new BundleEvent(BundleEvent.STOPPED, tblazy2);
			actualEvents = resultsListener.getResults(2);
			compareEvents(expectedEvents, actualEvents);
		} finally {
			getContext().removeBundleListener(resultsListener);
			uninstallBundle(tblazy2);
			startLevel.setStartLevel(initialSL);
			startLevel.setInitialBundleStartLevel(initialBSL);
		}
	}

	/*
	 * Tests Bundle.stop(STOP_TRANSIENT) in relation to the start-level service
	 */
	public void testActivationPolicy06() throws Exception {
		StartLevel startLevel = (StartLevel) getService(StartLevel.class);
		int initialSL = startLevel.getStartLevel();
		int initialBSL = startLevel.getInitialBundleStartLevel();
		startLevel.setInitialBundleStartLevel(initialSL + 10);
		Bundle tblazy2 = getContext().installBundle(getWebServer() + "tblazy2.jar");
		PackageAdmin pa = (PackageAdmin) getService(PackageAdmin.class);
		if (pa != null)
			pa.resolveBundles(new Bundle[] {tblazy2});
		EventListenerTestResults resultsListener = new SyncEventListenerTestResults(BundleEvent.STARTED | BundleEvent.LAZY_ACTIVATION | BundleEvent.STARTING | BundleEvent.STOPPING | BundleEvent.STOPPED);
		getContext().addBundleListener(resultsListener);
		EventListenerTestResults startlevelListener = new EventListenerTestResults(FrameworkEvent.STARTLEVEL_CHANGED);
		getContext().addFrameworkListener(startlevelListener);
		try {
			// persistently start the bundle
			tblazy2.start();
			assertTrue("bundle is not persistently started!!", startLevel.isBundlePersistentlyStarted(tblazy2));

			// test transient start Bundle.stop(START_TRANSIENT)
			startLevel.setStartLevel(startLevel.getStartLevel() + 15);
			Object[] expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(), null);
			Object[] actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);
		
			startLevel.setStartLevel(startLevel.getStartLevel() - 15);
			expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(), null);
			actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);

			// we expect a STARTING, STARTED, STOPPING, STOPPED event to be sent here because we met the start-level and we were persistently started
			// no LAZY_ACTIVATION event should be fired because this activation was not a result of a class load.
			Object[] expectedEvents = new Object[4];
			expectedEvents[0] = new BundleEvent(BundleEvent.STARTING,  tblazy2);
			expectedEvents[1] = new BundleEvent(BundleEvent.STARTED,  tblazy2);
			expectedEvents[2] = new BundleEvent(BundleEvent.STOPPING, tblazy2);
			expectedEvents[3] = new BundleEvent(BundleEvent.STOPPED, tblazy2);
			Object[] actualEvents = resultsListener.getResults(4);
			compareEvents(expectedEvents, actualEvents);

			// now call stop(STOP_TRANSIENT) while the start-level is met.
			startLevel.setStartLevel(startLevel.getStartLevel() + 15);
			expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(), null);
			actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);

			// we expect a STARTING, STARTED event to be sent here because we met the start-level
			// no LAZY_ACTIVATION event should be fired because this activation was not a result of a class load
			expectedEvents = new Object[2];
			expectedEvents[0] = new BundleEvent(BundleEvent.STARTING, tblazy2);
			expectedEvents[1] = new BundleEvent(BundleEvent.STARTED, tblazy2);
			actualEvents = resultsListener.getResults(2);
			compareEvents(expectedEvents, actualEvents);

			tblazy2.stop(Bundle.STOP_TRANSIENT);
			assertTrue("Bundle is not persistently started!!", startLevel.isBundlePersistentlyStarted(tblazy2));

			// we expect a STOPPING, STOPPED event to be sent here because we met the start-level
			expectedEvents = new Object[2];
			expectedEvents[0] = new BundleEvent(BundleEvent.STOPPING, tblazy2);
			expectedEvents[1] = new BundleEvent(BundleEvent.STOPPED, tblazy2);
			actualEvents = resultsListener.getResults(2);
			compareEvents(expectedEvents, actualEvents);

			startLevel.setStartLevel(startLevel.getStartLevel() - 15);
			expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(), null);
			actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);


			// now set the start-level back up and check that the bundle is started again because it is persistently started.
			startLevel.setStartLevel(startLevel.getStartLevel() + 15);
			expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(), null);
			actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);

			expectedEvents = new Object[2];
			expectedEvents[0] = new BundleEvent(BundleEvent.STARTING, tblazy2);
			expectedEvents[1] = new BundleEvent(BundleEvent.STARTED, tblazy2);
			actualEvents = resultsListener.getResults(2);
			compareEvents(expectedEvents, actualEvents);

			startLevel.setStartLevel(startLevel.getStartLevel() - 15);
			expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(), null);
			actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);

			expectedEvents = new Object[2];
			expectedEvents[0] = new BundleEvent(BundleEvent.STOPPING, tblazy2);
			expectedEvents[1] = new BundleEvent(BundleEvent.STOPPED, tblazy2);
			actualEvents = resultsListener.getResults(2);
			compareEvents(expectedEvents, actualEvents);
		} finally {
			getContext().removeBundleListener(resultsListener);
			uninstallBundle(tblazy2);
			startLevel.setStartLevel(initialSL);
			startLevel.setInitialBundleStartLevel(initialBSL);
		}
	}
	
	/*
	 * Tests a simple dependency chain and checks to see if lazy activation bundles got
	 * started in the correct order.
	 */
	public void testActivationPolicyChain01() throws Exception {
		Bundle tbchain1 = getContext().installBundle(getWebServer() + "tbchain1.jar");
		Bundle tbchain2 = getContext().installBundle(getWebServer() + "tbchain2.jar");
		Bundle tbchain3 = getContext().installBundle(getWebServer() + "tbchain3.jar");
		Bundle tbchain4 = getContext().installBundle(getWebServer() + "tbchain4.jar");
		Bundle tbchain5 = getContext().installBundle(getWebServer() + "tbchain5.jar");

		EventListenerTestResults resultsListener = new EventListenerTestResults(BundleEvent.STARTED | BundleEvent.STOPPED);
		getContext().addBundleListener(resultsListener);
		try {
			tbchain1.loadClass("org.osgi.test.cases.activationpolicy.tbchain1.SingleChainTest").newInstance();
	
			Object[] expectedEvents = new Object[3];
			expectedEvents[0] = new BundleEvent(BundleEvent.STARTED, tbchain3);
			expectedEvents[1] = new BundleEvent(BundleEvent.STARTED, tbchain2);
			expectedEvents[2] = new BundleEvent(BundleEvent.STARTED, tbchain1);
			Object[] actualEvents = resultsListener.getResults(3);
			assertEquals("number of results", expectedEvents.length, actualEvents.length);
			compareEvents(expectedEvents, actualEvents);
		} finally {
			getContext().removeBundleListener(resultsListener);
			uninstallBundle(tbchain1);
			uninstallBundle(tbchain2);
			uninstallBundle(tbchain3);
			uninstallBundle(tbchain4);
			uninstallBundle(tbchain5);
		}
	}

	/*
	 * More advanced chain test that contains multiple class hierachies
	 * NOTE there may be too much assumption on the order the VM verifier loads interface classes when more than one is implemented.
	 */
	public void testActivationPolicyChain02() throws Exception {
		Bundle tbchain1 = getContext().installBundle(getWebServer() + "tbchain1.jar");
		Bundle tbchain2 = getContext().installBundle(getWebServer() + "tbchain2.jar");
		Bundle tbchain3 = getContext().installBundle(getWebServer() + "tbchain3.jar");
		Bundle tbchain4 = getContext().installBundle(getWebServer() + "tbchain4.jar");
		Bundle tbchain5 = getContext().installBundle(getWebServer() + "tbchain5.jar");

		EventListenerTestResults resultsListener = new EventListenerTestResults(BundleEvent.STARTED | BundleEvent.STOPPED);
		getContext().addBundleListener(resultsListener);
		try {
			tbchain1.loadClass("org.osgi.test.cases.activationpolicy.tbchain1.TestMultiChain").newInstance();

			Object[] expectedEvents = new Object[5];
			expectedEvents[0] = new BundleEvent(BundleEvent.STARTED, tbchain5);
			expectedEvents[1] = new BundleEvent(BundleEvent.STARTED, tbchain3);
			expectedEvents[2] = new BundleEvent(BundleEvent.STARTED, tbchain4);
			expectedEvents[3] = new BundleEvent(BundleEvent.STARTED, tbchain2);
			expectedEvents[4] = new BundleEvent(BundleEvent.STARTED, tbchain1);
			Object[] actualEvents = resultsListener.getResults(5);
			assertEquals("number of results", expectedEvents.length, actualEvents.length);
			compareEvents(expectedEvents, actualEvents);
		} finally {
			getContext().removeBundleListener(resultsListener);
			uninstallBundle(tbchain1);
			uninstallBundle(tbchain2);
			uninstallBundle(tbchain3);
			uninstallBundle(tbchain4);
			uninstallBundle(tbchain5);
		}
	}
	
	/*
	 * This tests that a ClassCircularityError is not generated when the "trigger" class is loaded while a bundle
	 * is being activated as a result of a lazy activation policy
	 */
	public void testClassCircularity() throws Exception {
		Bundle tblazy5 = getContext().installBundle(getWebServer() + "tblazy5.jar");
		Bundle tblazy6 = getContext().installBundle(getWebServer() + "tblazy6.jar");

		EventListenerTestResults resultsListener = new EventListenerTestResults(BundleEvent.STARTED);
		getContext().addBundleListener(resultsListener);
		try {
			tblazy5.loadClass("org.osgi.test.cases.activationpolicy.tblazy5.CircularityErrorTest").newInstance();
			// The order of activation is reversed here because tblazy6's activator loads a class in tblazy5 that triggers it to be activated before
			// tblazy6 is started, therefore the STARTED event should be fired for tblazy5 first.
			// It is questionable whether this is required by the specification.  It may be better just to make sure both bundles are in the ACTIVE state 
			// after the classload is successful to determine that no errors occurred.
			Object[] expectedEvents = new Object[2];
			expectedEvents[0] = new BundleEvent(BundleEvent.STARTED, tblazy5);
			expectedEvents[1] = new BundleEvent(BundleEvent.STARTED, tblazy6);
			Object[] actualEvents = resultsListener.getResults(2);
			compareEvents(expectedEvents, actualEvents);
		} finally {
			getContext().removeBundleListener(resultsListener);
			uninstallBundle(tblazy5);
			uninstallBundle(tblazy6);
		}
	}
}
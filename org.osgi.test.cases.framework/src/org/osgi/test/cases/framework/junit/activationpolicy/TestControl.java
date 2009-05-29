/*
 * $Id$
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
package org.osgi.test.cases.framework.junit.activationpolicy;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.service.startlevel.StartLevel;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;


/**
 * This class contains tests related with the framework class loading policies.
 * 
 * @author left
 * @version $Revision$
 */
public class TestControl extends DefaultTestBundleControl {

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
				FrameworkEvent actual = (FrameworkEvent) actualEvents[i];
				assertEquals("Event Bundles", expected.getSource(), actual.getSource());
				assertEquals("Event Type", expected.getType(), actual.getType());
			}
		}
	}
	
	/*
	 * Tests a simple lazy policy with no includes or excludes directives
	 */
	public void testActivationPolicy01() throws Exception {
		Bundle tblazy1 = installBundle(getWebServer()
				+ "activationpolicy.tblazy1.jar", false);
		Bundle tblazy2 = installBundle(getWebServer()
				+ "activationpolicy.tblazy2.jar", false);
		Bundle tblazy3 = installBundle(getWebServer()
				+ "activationpolicy.tblazy3.jar", false);
		Bundle tblazy4 = installBundle(getWebServer()
				+ "activationpolicy.tblazy4.jar", false);

		tblazy1.start(Bundle.START_ACTIVATION_POLICY);
		tblazy2.start(Bundle.START_ACTIVATION_POLICY);
		tblazy3.start(Bundle.START_ACTIVATION_POLICY);
		tblazy4.start(Bundle.START_ACTIVATION_POLICY);
		// listen for STARTED, STOPPED and LAZY_ACTIVATION events
		// we should not get LAZY_ACTIVATION events because this is not a synchronous listener.
		EventListenerTestResults resultsListener = new EventListenerTestResults(BundleEvent.STARTED | BundleEvent.STOPPED | BundleEvent.LAZY_ACTIVATION);
		getContext().addBundleListener(resultsListener);
		try {
			tblazy1.loadClass("org.osgi.test.cases.framework.activationpolicy.tblazy1.LazySimple").newInstance();
	
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
		Bundle tblazy1 = installBundle(getWebServer()
				+ "activationpolicy.tblazy1.jar", false);
		Bundle tblazy2 = installBundle(getWebServer()
				+ "activationpolicy.tblazy2.jar", false);
		Bundle tblazy3 = installBundle(getWebServer()
				+ "activationpolicy.tblazy3.jar", false);
		Bundle tblazy4 = installBundle(getWebServer()
				+ "activationpolicy.tblazy4.jar", false);
		
		tblazy1.start(Bundle.START_ACTIVATION_POLICY);
		tblazy2.start(Bundle.START_ACTIVATION_POLICY);
		tblazy3.start(Bundle.START_ACTIVATION_POLICY);
		tblazy4.start(Bundle.START_ACTIVATION_POLICY);

		// listen for STARTED, STOPPED and LAZY_ACTIVATION evnets
		// we should not get LAZY_ACTIVATION events because this is not a synchronous listener.
		EventListenerTestResults resultsListener = new EventListenerTestResults(BundleEvent.STARTED | BundleEvent.STOPPED | BundleEvent.LAZY_ACTIVATION);
		getContext().addBundleListener(resultsListener);
		try {
			// First load a class that depends on a class included in an excludes package
			tblazy1.loadClass("org.osgi.test.cases.framework.activationpolicy.tblazy1.LazyExclude1").newInstance();
			// this should result in no STARTED event
			Object[] expectedEvents = new Object[0];
			Object[] actualEvents = resultsListener.getResults(0);
			compareEvents(expectedEvents, actualEvents);

			// Now load a class that was not included in an excludes package
			tblazy1.loadClass("org.osgi.test.cases.framework.activationpolicy.tblazy1.LazyExclude2").newInstance();
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
		Bundle tblazy1 = installBundle(getWebServer()
				+ "activationpolicy.tblazy1.jar", false);
		Bundle tblazy2 = installBundle(getWebServer()
				+ "activationpolicy.tblazy2.jar", false);
		Bundle tblazy3 = installBundle(getWebServer()
				+ "activationpolicy.tblazy3.jar", false);
		Bundle tblazy4 = installBundle(getWebServer()
				+ "activationpolicy.tblazy4.jar", false);

		tblazy1.start(Bundle.START_ACTIVATION_POLICY);
		tblazy2.start(Bundle.START_ACTIVATION_POLICY);
		tblazy3.start(Bundle.START_ACTIVATION_POLICY);
		tblazy4.start(Bundle.START_ACTIVATION_POLICY);
		// listen for STARTED, STOPPED and LAZY_ACTIVATION evnets
		// we should not get LAZY_ACTIVATION events because this is not a synchronous listener.
		EventListenerTestResults resultsListener = new EventListenerTestResults(BundleEvent.STARTED | BundleEvent.STOPPED | BundleEvent.LAZY_ACTIVATION);
		getContext().addBundleListener(resultsListener);
		try {
			// first load a class that depends on a class that was not included in an includes package
			tblazy1.loadClass("org.osgi.test.cases.framework.activationpolicy.tblazy1.LazyInclude1").newInstance();
			// this should result in no STARTED event
			Object[] expectedEvents = new Object[0];
			Object[] actualEvents = resultsListener.getResults(0);
			compareEvents(expectedEvents, actualEvents);

			// now load a class that depends on a class that is included in an includes package
			tblazy1.loadClass("org.osgi.test.cases.framework.activationpolicy.tblazy1.LazyInclude2").newInstance();
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
		StartLevel startLevel = (StartLevel) getService(StartLevel.class);
		int initialSL = startLevel.getStartLevel();
		int initialBSL = startLevel.getInitialBundleStartLevel();
		startLevel.setInitialBundleStartLevel(initialSL + 10);
		Bundle tblazy2 = installBundle(getWebServer()
				+ "activationpolicy.tblazy2.jar", false);
		tblazy2.start(Bundle.START_ACTIVATION_POLICY);
		// listen for STARTING, STARTED, STOPPING, STOPPED and LAZY_ACTIVATION events
		// we *should* get LAZY_ACTIVATION events because this *is* a synchronous listener.
		EventListenerTestResults resultsListener = new SyncEventListenerTestResults(BundleEvent.STARTED | BundleEvent.LAZY_ACTIVATION | BundleEvent.STARTING | BundleEvent.STOPPING | BundleEvent.STOPPED);
		getContext().addBundleListener(resultsListener);
		EventListenerTestResults startlevelListener = new EventListenerTestResults(FrameworkEvent.STARTLEVEL_CHANGED);
		getContext().addFrameworkListener(startlevelListener);
		try {
			// crank up the framework start-level.  This should result in no STARTED event
			startLevel.setStartLevel(startLevel.getStartLevel() + 15);
			Object[] expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(0), null);
			Object[] actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);
		
			startLevel.setStartLevel(startLevel.getStartLevel() - 15);
			expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(0), null);
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

			// now load a class from it before the start-level is met.  This should result in no events
			tblazy2.loadClass("org.osgi.test.cases.framework.activationpolicy.tblazy2.ATest");
			expectedEvents = new Object[0];
			actualEvents = resultsListener.getResults(0);
			compareEvents(expectedEvents, actualEvents);

			startLevel.setStartLevel(startLevel.getStartLevel() + 15);
			expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(0), null);
			actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);

			startLevel.setStartLevel(startLevel.getStartLevel() - 15);
			expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(0), null);
			actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);

            // Given that the start-level was met, then depending on the framework implementation we should
            // see either:
            //   * LAZY_ACTIVATION, STARTING, STARTED, STOPPING, STOPPED events or
            //   * LAZY_ACTIVATION, STOPPING, STOPPED events
			// The difference comes from whether the framework treats the trigger as a one-time trigger or not.
			actualEvents = resultsListener.getResults(5);
            // This is the case if the trigger is a one-time event.
            if (actualEvents.length == 5)
            {
                expectedEvents = new Object[5];
                expectedEvents[0] = new BundleEvent(BundleEvent.LAZY_ACTIVATION, tblazy2);
                expectedEvents[1] = new BundleEvent(BundleEvent.STARTING, tblazy2);
                expectedEvents[2] = new BundleEvent(BundleEvent.STARTED, tblazy2);
                expectedEvents[3] = new BundleEvent(BundleEvent.STOPPING, tblazy2);
                expectedEvents[4] = new BundleEvent(BundleEvent.STOPPED, tblazy2);
                compareEvents(expectedEvents, actualEvents);
            }
            // This is the case if the trigger is NOT a one-time event.
            else
            {
                expectedEvents = new Object[3];
                expectedEvents[0] = new BundleEvent(BundleEvent.LAZY_ACTIVATION, tblazy2);
                expectedEvents[1] = new BundleEvent(BundleEvent.STOPPING, tblazy2);
                expectedEvents[2] = new BundleEvent(BundleEvent.STOPPED, tblazy2);
                compareEvents(expectedEvents, actualEvents);

                // now load a class while start-level is met.
                startLevel.setStartLevel(startLevel.getStartLevel() + 15);
                expectedFrameworkEvents = new Object[1];
                expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(0), null);
                actualFrameworkEvents = startlevelListener.getResults(1);
                compareEvents(expectedFrameworkEvents, actualFrameworkEvents);

                tblazy2.loadClass("org.osgi.test.cases.framework.activationpolicy.tblazy2.ATest");

                startLevel.setStartLevel(startLevel.getStartLevel() - 15);
                expectedFrameworkEvents = new Object[1];
                expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(0), null);
                actualFrameworkEvents = startlevelListener.getResults(1);
                compareEvents(expectedFrameworkEvents, actualFrameworkEvents);

                // Check for the proper events, STARTED should be fired here because the start-level was met
                expectedEvents = new Object[5];
                expectedEvents[0] = new BundleEvent(BundleEvent.LAZY_ACTIVATION, tblazy2);
                expectedEvents[1] = new BundleEvent(BundleEvent.STARTING, tblazy2);
                expectedEvents[2] = new BundleEvent(BundleEvent.STARTED, tblazy2);
                expectedEvents[3] = new BundleEvent(BundleEvent.STOPPING, tblazy2);
                expectedEvents[4] = new BundleEvent(BundleEvent.STOPPED, tblazy2);
                actualEvents = resultsListener.getResults(5);
                compareEvents(expectedEvents, actualEvents);
            }
		} finally {
			getContext().removeBundleListener(resultsListener);
			uninstallBundle(tblazy2);
			startlevelListener = new EventListenerTestResults(FrameworkEvent.STARTLEVEL_CHANGED);
			getContext().addFrameworkListener(startlevelListener);
			startLevel.setStartLevel(initialSL);
			startlevelListener.getResults(1);
			startLevel.setInitialBundleStartLevel(initialBSL);
			getContext().removeFrameworkListener(startlevelListener);
		}
	}

	/*
	 * Tests Bundle.start(START_ACTIVATION_POLICY) in relation to the start-level service
	 */
	public void testActivationPolicy05() throws Exception {
		StartLevel startLevel = (StartLevel) getService(StartLevel.class);
		int initialSL = startLevel.getStartLevel();
		int initialBSL = startLevel.getInitialBundleStartLevel();
		startLevel.setInitialBundleStartLevel(initialSL + 10);
		Bundle tblazy2 = installBundle(getWebServer()
				+ "activationpolicy.tblazy2.jar", false);
		// make this a persistent start and ignore the activation policy;
		// this should not activate the bundle because the start-level is not met.
		tblazy2.start(0);
		assertTrue("bundle is persistently started.", startLevel.isBundlePersistentlyStarted(tblazy2));
		assertTrue("bundle is not using activation policy.", !startLevel.isBundleActivationPolicyUsed(tblazy2));
		// listen for STARTING, STARTED, STOPPING, STOPPED and LAZY_ACTIVATION events
		// we *should* get LAZY_ACTIVATION events because this *is* a synchronous listener.
		EventListenerTestResults resultsListener = new SyncEventListenerTestResults(BundleEvent.STARTED | BundleEvent.LAZY_ACTIVATION | BundleEvent.STARTING | BundleEvent.STOPPING | BundleEvent.STOPPED);
		getContext().addBundleListener(resultsListener);
		EventListenerTestResults startlevelListener = new EventListenerTestResults(FrameworkEvent.STARTLEVEL_CHANGED);
		getContext().addFrameworkListener(startlevelListener);
		try {
			// crank up the framework start-level.  This should result in a STARTED event because we are ingoring the activation policy
			startLevel.setStartLevel(startLevel.getStartLevel() + 15);
			Object[] expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(0), null);
			Object[] actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);
		
			startLevel.setStartLevel(startLevel.getStartLevel() - 15);
			expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(0), null);
			actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);

			// we expect a STARTING, STARTED, STOPPING, STOPPED event to be sent here because we met the start-level
			// and we are ignoring the lazy activation policy
			Object[] expectedEvents = new Object[4];
			expectedEvents[0] = new BundleEvent(BundleEvent.STARTING, tblazy2);
			expectedEvents[1] = new BundleEvent(BundleEvent.STARTED, tblazy2);
			expectedEvents[2] = new BundleEvent(BundleEvent.STOPPING, tblazy2);
			expectedEvents[3] = new BundleEvent(BundleEvent.STOPPED, tblazy2);
			Object[] actualEvents = resultsListener.getResults(4);
			compareEvents(expectedEvents, actualEvents);

			// now mark the bundle to use the activation policy
			tblazy2.start(Bundle.START_ACTIVATION_POLICY);
			assertTrue("bundle is using activation policy.", startLevel.isBundleActivationPolicyUsed(tblazy2));

			startLevel.setStartLevel(startLevel.getStartLevel() + 15);
			expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(0), null);
			actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);

			startLevel.setStartLevel(startLevel.getStartLevel() - 15);
			expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(0), null);
			actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);

			// we expect a LAZY_ACTIVATION, STOPPING, STOPPED event to be sent here because we met the start-level
			// but no STARTING or STARTED event because no trigger class was loaded.
			expectedEvents = new Object[3];
			expectedEvents[0] = new BundleEvent(BundleEvent.LAZY_ACTIVATION, tblazy2);
			expectedEvents[1] = new BundleEvent(BundleEvent.STOPPING, tblazy2);
			expectedEvents[2] = new BundleEvent(BundleEvent.STOPPED, tblazy2);
			actualEvents = resultsListener.getResults(3);
			compareEvents(expectedEvents, actualEvents);

			// persistently stop the bundle
			tblazy2.stop();
			// no events are expected because the bundle should already be stopped
			expectedEvents = new Object[0];
			actualEvents = resultsListener.getResults(0);
			compareEvents(expectedEvents, actualEvents);
			
			// now call start(START_TRANSIENT | START_ACTIVATION_POLICY) while start-level is met.
			startLevel.setStartLevel(startLevel.getStartLevel() + 15);
			expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(0), null);
			actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);

			tblazy2.start(Bundle.START_TRANSIENT | Bundle.START_ACTIVATION_POLICY);

			// we expect a LAZY_ACTIVATION event
			expectedEvents = new Object[1];
			expectedEvents[0] = new BundleEvent(BundleEvent.LAZY_ACTIVATION, tblazy2);
			actualEvents = resultsListener.getResults(1);
			compareEvents(expectedEvents, actualEvents);

			startLevel.setStartLevel(startLevel.getStartLevel() - 15);
			expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(0), null);
			actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);

			expectedEvents = new Object[2];
			expectedEvents[0] = new BundleEvent(BundleEvent.STOPPING, tblazy2);
			expectedEvents[1] = new BundleEvent(BundleEvent.STOPPED, tblazy2);
			actualEvents = resultsListener.getResults(2);
			compareEvents(expectedEvents, actualEvents);

			// make sure the bundle is not persistently started and is not using its activation policy
			assertTrue("bundle is not persistently started.", !startLevel.isBundlePersistentlyStarted(tblazy2));
			assertTrue("bundle is not using activation policy.", !startLevel.isBundleActivationPolicyUsed(tblazy2));
		} finally {
			getContext().removeBundleListener(resultsListener);
			uninstallBundle(tblazy2);
			startlevelListener = new EventListenerTestResults(FrameworkEvent.STARTLEVEL_CHANGED);
			getContext().addFrameworkListener(startlevelListener);
			startLevel.setStartLevel(initialSL);
			startlevelListener.getResults(1);
			startLevel.setInitialBundleStartLevel(initialBSL);
			getContext().removeFrameworkListener(startlevelListener);
		}
	}
	
	/*
	 * Tests Bundle.start(START_TRANSIENT) in relation to the start-level service
	 */
	public void testStartTransient01() throws Exception {
		StartLevel startLevel = (StartLevel) getService(StartLevel.class);
		int initialSL = startLevel.getStartLevel();
		int initialBSL = startLevel.getInitialBundleStartLevel();
		startLevel.setInitialBundleStartLevel(initialSL + 10);
		Bundle tblazy2 = installBundle(getWebServer()
				+ "activationpolicy.tblazy2.jar", false);
		tblazy2.start(Bundle.START_ACTIVATION_POLICY);
		// listen for STARTING, STARTED, STOPPING, STOPPED and LAZY_ACTIVATION events
		// we *should* get LAZY_ACTIVATION events because this *is* a synchronous listener.
		EventListenerTestResults resultsListener = new SyncEventListenerTestResults(BundleEvent.STARTED | BundleEvent.LAZY_ACTIVATION | BundleEvent.STARTING | BundleEvent.STOPPING | BundleEvent.STOPPED);
		getContext().addBundleListener(resultsListener);
		EventListenerTestResults startlevelListener = new EventListenerTestResults(FrameworkEvent.STARTLEVEL_CHANGED);
		getContext().addFrameworkListener(startlevelListener);
		try {
			// crank up the framework start-level.  This should result in no STARTED event
			startLevel.setStartLevel(startLevel.getStartLevel() + 15);
			Object[] expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(0), null);
			Object[] actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);
		
			startLevel.setStartLevel(startLevel.getStartLevel() - 15);
			expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(0), null);
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
				fail("expected a BundleException because start level is not met.");
			} catch (BundleException e) {
				// expected
			}

			expectedEvents = new Object[0];
			actualEvents = resultsListener.getResults(0);
			compareEvents(expectedEvents, actualEvents);

			startLevel.setStartLevel(startLevel.getStartLevel() + 15);
			expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(0), null);
			actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);

			startLevel.setStartLevel(startLevel.getStartLevel() - 15);
			expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(0), null);
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
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(0), null);
			actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);

			tblazy2.start(Bundle.START_TRANSIENT);

			// we expect a LAZY_ACTIVATION event here because the start-level was met before we called Bundle.start method.
			expectedEvents = new Object[3];
			expectedEvents[0] = new BundleEvent(BundleEvent.LAZY_ACTIVATION, tblazy2);
			expectedEvents[1] = new BundleEvent(BundleEvent.STARTING, tblazy2);
			expectedEvents[2] = new BundleEvent(BundleEvent.STARTED, tblazy2);
			actualEvents = resultsListener.getResults(3);
			compareEvents(expectedEvents, actualEvents);

			startLevel.setStartLevel(startLevel.getStartLevel() - 15);
			expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(0), null);
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
			startlevelListener = new EventListenerTestResults(FrameworkEvent.STARTLEVEL_CHANGED);
			getContext().addFrameworkListener(startlevelListener);
			startLevel.setStartLevel(initialSL);
			startlevelListener.getResults(1);
			startLevel.setInitialBundleStartLevel(initialBSL);
			getContext().removeFrameworkListener(startlevelListener);
		}
	}

	/*
	 * Tests Bundle.stop(STOP_TRANSIENT) in relation to the start-level service
	 */
	public void testStopTransient01() throws Exception {
		StartLevel startLevel = (StartLevel) getService(StartLevel.class);
		int initialSL = startLevel.getStartLevel();
		int initialBSL = startLevel.getInitialBundleStartLevel();
		startLevel.setInitialBundleStartLevel(initialSL + 10);
		Bundle tblazy2 = installBundle(getWebServer()
				+ "activationpolicy.tblazy2.jar", false);
		// listen for STARTING, STARTED, STOPPING, STOPPED and LAZY_ACTIVATION events
		// we *should* get LAZY_ACTIVATION events because this *is* a synchronous listener.
		EventListenerTestResults resultsListener = new SyncEventListenerTestResults(BundleEvent.STARTED | BundleEvent.LAZY_ACTIVATION | BundleEvent.STARTING | BundleEvent.STOPPING | BundleEvent.STOPPED);
		getContext().addBundleListener(resultsListener);
		EventListenerTestResults startlevelListener = new EventListenerTestResults(FrameworkEvent.STARTLEVEL_CHANGED);
		getContext().addFrameworkListener(startlevelListener);
		try {
			// persistently start the bundle
			tblazy2.start();
			assertTrue("bundle is persistently started.", startLevel.isBundlePersistentlyStarted(tblazy2));

			// test transient start Bundle.stop(START_TRANSIENT)
			startLevel.setStartLevel(startLevel.getStartLevel() + 15);
			Object[] expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(0), null);
			Object[] actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);
		
			startLevel.setStartLevel(startLevel.getStartLevel() - 15);
			expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(0), null);
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
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(0), null);
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
			assertTrue("Bundle is persistently started.", startLevel.isBundlePersistentlyStarted(tblazy2));

			// we expect a STOPPING, STOPPED event to be sent here because we met the start-level
			expectedEvents = new Object[2];
			expectedEvents[0] = new BundleEvent(BundleEvent.STOPPING, tblazy2);
			expectedEvents[1] = new BundleEvent(BundleEvent.STOPPED, tblazy2);
			actualEvents = resultsListener.getResults(2);
			compareEvents(expectedEvents, actualEvents);

			startLevel.setStartLevel(startLevel.getStartLevel() - 15);
			expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(0), null);
			actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);


			// now set the start-level back up and check that the bundle is started again because it is persistently started.
			startLevel.setStartLevel(startLevel.getStartLevel() + 15);
			expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(0), null);
			actualFrameworkEvents = startlevelListener.getResults(1);
			compareEvents(expectedFrameworkEvents, actualFrameworkEvents);

			expectedEvents = new Object[2];
			expectedEvents[0] = new BundleEvent(BundleEvent.STARTING, tblazy2);
			expectedEvents[1] = new BundleEvent(BundleEvent.STARTED, tblazy2);
			actualEvents = resultsListener.getResults(2);
			compareEvents(expectedEvents, actualEvents);

			startLevel.setStartLevel(startLevel.getStartLevel() - 15);
			expectedFrameworkEvents = new Object[1];
			expectedFrameworkEvents[0] = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(0), null);
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
			startlevelListener = new EventListenerTestResults(FrameworkEvent.STARTLEVEL_CHANGED);
			getContext().addFrameworkListener(startlevelListener);
			startLevel.setStartLevel(initialSL);
			startlevelListener.getResults(1);
			startLevel.setInitialBundleStartLevel(initialBSL);
			getContext().removeFrameworkListener(startlevelListener);
		}
	}
	
	/*
	 * Tests a simple dependency chain and checks to see if lazy activation bundles got
	 * started in the correct order.
	 */
	public void testActivationPolicyChain01() throws Exception {
		Bundle tbchain1 = installBundle(getWebServer()
				+ "activationpolicy.tbchain1.jar", false);
		Bundle tbchain2 = installBundle(getWebServer()
				+ "activationpolicy.tbchain2.jar", false);
		Bundle tbchain3 = installBundle(getWebServer()
				+ "activationpolicy.tbchain3.jar", false);
		Bundle tbchain4 = installBundle(getWebServer()
				+ "activationpolicy.tbchain4.jar", false);
		Bundle tbchain5 = installBundle(getWebServer()
				+ "activationpolicy.tbchain5.jar", false);

		tbchain1.start(Bundle.START_ACTIVATION_POLICY);
		tbchain2.start(Bundle.START_ACTIVATION_POLICY);
		tbchain3.start(Bundle.START_ACTIVATION_POLICY);
		tbchain4.start(Bundle.START_ACTIVATION_POLICY);
		tbchain5.start(Bundle.START_ACTIVATION_POLICY);

		EventListenerTestResults resultsListener = new EventListenerTestResults(BundleEvent.STARTED | BundleEvent.STOPPED);
		getContext().addBundleListener(resultsListener);
		try {
			tbchain1.loadClass("org.osgi.test.cases.framework.activationpolicy.tbchain1.SingleChainTest").newInstance();
	
			Object[] expectedEvents = new Object[3];
			expectedEvents[0] = new BundleEvent(BundleEvent.STARTED, tbchain3);
			expectedEvents[1] = new BundleEvent(BundleEvent.STARTED, tbchain2);
			expectedEvents[2] = new BundleEvent(BundleEvent.STARTED, tbchain1);
			Object[] actualEvents = resultsListener.getResults(3);
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
		Bundle tbchain1 = installBundle(getWebServer()
				+ "activationpolicy.tbchain1.jar", false);
		Bundle tbchain2 = installBundle(getWebServer()
				+ "activationpolicy.tbchain2.jar", false);
		Bundle tbchain3 = installBundle(getWebServer()
				+ "activationpolicy.tbchain3.jar", false);
		Bundle tbchain4 = installBundle(getWebServer()
				+ "activationpolicy.tbchain4.jar", false);
		Bundle tbchain5 = installBundle(getWebServer()
				+ "activationpolicy.tbchain5.jar", false);

		tbchain1.start(Bundle.START_ACTIVATION_POLICY);
		tbchain2.start(Bundle.START_ACTIVATION_POLICY);
		tbchain3.start(Bundle.START_ACTIVATION_POLICY);
		tbchain4.start(Bundle.START_ACTIVATION_POLICY);
		tbchain5.start(Bundle.START_ACTIVATION_POLICY);

		EventListenerTestResults resultsListener = new EventListenerTestResults(BundleEvent.STARTED | BundleEvent.STOPPED);
		getContext().addBundleListener(resultsListener);
		try {
			tbchain1.loadClass("org.osgi.test.cases.framework.activationpolicy.tbchain1.TestMultiChain").newInstance();

			Object[] expectedEvents = new Object[5];
			expectedEvents[0] = new BundleEvent(BundleEvent.STARTED, tbchain5);
			expectedEvents[1] = new BundleEvent(BundleEvent.STARTED, tbchain3);
			expectedEvents[2] = new BundleEvent(BundleEvent.STARTED, tbchain4);
			expectedEvents[3] = new BundleEvent(BundleEvent.STARTED, tbchain2);
			expectedEvents[4] = new BundleEvent(BundleEvent.STARTED, tbchain1);
			Object[] actualEvents = resultsListener.getResults(5);
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
		Bundle tblazy5 = installBundle(getWebServer()
				+ "activationpolicy.tblazy5.jar", false);
		Bundle tblazy6 = installBundle(getWebServer()
				+ "activationpolicy.tblazy6.jar", false);

		tblazy5.start(Bundle.START_ACTIVATION_POLICY);
		tblazy6.start(Bundle.START_ACTIVATION_POLICY);

		EventListenerTestResults resultsListener = new EventListenerTestResults(BundleEvent.STARTED);
		getContext().addBundleListener(resultsListener);
		try {
			tblazy5.loadClass("org.osgi.test.cases.framework.activationpolicy.tblazy5.CircularityErrorTest").newInstance();
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

	/*
	 * Tests that getBundleContext works as when LAZY_ACTIVATION event is fired.
	 */
	public void testGetBundleContext() throws Exception {
		Bundle tblazy2 = installBundle(getWebServer()
				+ "activationpolicy.tblazy2.jar", false);

		// listen for STARTED, STOPPED and LAZY_ACTIVATION evnets
		// we *should* get LAZY_ACTIVATION events because this *is* a synchronous listener.
		SyncEventListenerTestResults resultsListener = new SyncEventListenerTestResults(BundleEvent.STARTED | BundleEvent.STOPPED | BundleEvent.LAZY_ACTIVATION, true);
		getContext().addBundleListener(resultsListener);
		tblazy2.start(Bundle.START_ACTIVATION_POLICY);
		try {
			tblazy2.loadClass("org.osgi.test.cases.framework.activationpolicy.tblazy2.ATest");
	
			// The bundle must have been activated now
			Object[] expectedEvents = new Object[2];
			expectedEvents[0] = new BundleEvent(BundleEvent.LAZY_ACTIVATION, tblazy2);
			expectedEvents[1] = new BundleEvent(BundleEvent.STARTED, tblazy2);
			Object[] actualEvents = resultsListener.getResults(2);
			compareEvents(expectedEvents, actualEvents);
			BundleContext[] contexts = resultsListener.getContexts();
			assertEquals("number of contexts", 1, contexts.length);
			assertEquals("bundle context", tblazy2, contexts[0].getBundle());
		} finally {
			getContext().removeBundleListener(resultsListener);
			uninstallBundle(tblazy2);
		}
	}
}
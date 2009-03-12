/*
 * $Id$
 *
 * Copyright (c) The OSGi Alliance (2009). All Rights Reserved.
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

package org.osgi.test.cases.blueprint.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.test.cases.blueprint.services.AssertionService;

import junit.framework.TestCase;

/**
 * A test phase for managed by a test controller.  At test phase has
 * an initializtion/wait for events/validation/termination lifecycle.
 * A given test consists of one or more test phases that are run
 * in sequence.
 */
public class TestPhase {
    // our timeout period for this phase
    protected long timeout;

    // the test context we're running in...used for starting/stopping bundles, etc.
    protected BundleContext testContext;

    // our event set of interest;
    protected List events = new ArrayList();

    public TestPhase(BundleContext testContext, long timeout)
    {
        this.testContext = testContext;
        this.timeout = timeout;
    }

    /**
     * Create a TestPhase from a list of event sets.
     *
     * @param eventSet The lists of sets to add.
     */
    public TestPhase(List eventSet)
    {
        this.events.addAll(eventSet);
    }


    /**
     * Add an event set to the phase list.  An event set generally
     * manages events for a single bundle.
     *
     * @param eventSet The added event set.
     */
    public void addEventSet(EventSet eventSet) {
        this.events.add(eventSet);
    }

    /**
     * Retrieve an event set by index position.
     *
     * @param index  The index of the target set.
     *
     * @return The associated EventSet for that position.
     */
    public EventSet getEventSet(int index) {
        return (EventSet)events.get(index);
    }


    /**
     * Perform test startup operations.
     */
    public void start(BundleContext testContext) throws Exception {
        this.testContext = testContext;
        for (int i = 0; i < events.size(); i ++) {
            EventSet set = (EventSet)events.get(i);
            set.start(testContext);
        }
    }

    /**
     * Perform cleanup on this result set.  In particular, this
     * removes the event listener and stops the bundle.  Called
     * at the end of processing.
     */
    public void cleanup(BundleContext testContext) throws Exception {
        // make sure all of the event sets have a chance to
        // drive their terminators
        stopEventSets();
    }

    /**
     * Give each event set a chance to perform any test setup it requires.
     * Typically, this involves starting some sort of bundle.
     */
    protected void startEventSets() throws Exception {
        for (int i = 0; i < events.size(); i ++) {
            EventSet set = (EventSet)events.get(i);
            set.start(testContext);
        }
    }

    /**
     * Give each event set a chance to perform any test setup it requires.
     * Typically, this involves starting some sort of bundle.
     */
    protected void stopEventSets() throws Exception {
        for (int i = 0; i < events.size();i ++) {
            EventSet set = (EventSet)events.get(i);
            set.stop(testContext);
        }
    }


    /**
     * Give each event set a chance to perform any test setup it requires.
     * Typically, this involves starting some sort of bundle.
     */
    protected void checkEventResults() throws Exception {
        // we do this in multiple passes...first checking for failures,
        // then missing events, and finally validation failures

        // outright failures
        for (int i = 0; i < events.size(); i ++) {
            EventSet set = (EventSet)events.get(i);
            set.checkUnexpected();
        }

        // missing stuff
        for (int i = 0; i < events.size(); i ++) {
            EventSet set = (EventSet)events.get(i);
            set.checkMissing();
        }

        // nothing unexpected or missing, so validate
        // the state of things
        for (int i = 0; i < events.size(); i ++) {
            EventSet set = (EventSet)events.get(i);
            // the event set might need to use the context to
            // poke around at the state of things (e.g., see if
            // particular services are registered, check service
            // properties, etc.)
            set.checkEnvironment(testContext);
        }
    }

    /**
     * Run the tests using specific timeout intervals.
     *
     * @param timeout The timeout value (in milliseconds).
     */
    public synchronized void runTest() throws Exception {
        // ok, so any startup processing needed by our event processors.
        startEventSets();
        // now wait for either a failure event or receipt of all expected events.
        try {
            wait(timeout);
        } catch (InterruptedException e) {
            // we timed out...there should be unsatisfied dependencies
            // in our list if that's the case, which will raise an assertion
            // acception
        }
        // have the event set check its results
        checkEventResults();
        // have these event sets perform any necessary clean up
        stopEventSets();
    }

    /**
     * Handle an event in the event set.  Each event set gets a chance
     * to look at this to decide if it's of interest.  Once this is done,
     * then we check to see if all event sets are completely done with processing.
     *
     * @param event  The received TestEvent (already preprocessed).
     */
	public synchronized void handleEvent(TestEvent event) {
        for (int i = 0; i < events.size(); i ++) {
            EventSet set = (EventSet)events.get(i);
            set.handleEvent(testContext, event);
        }

        // if nothing is in error state, and we're still waiting for
        // some events, go listen for more events
        if ( haveErrors() || isComplete()) {
            // our expected results indicate everything is looking good,
            // so wake up the main processor and have it move along to the next step.
            notify();
        }
    }

    /**
     * Check to see if any of our event sets has received something
     * it perceives as an error condition.  An error condition will
     * terminate the phase wait, since the trigger error might prevent
     * all of our expected events from being generated.
     *
     * @return true if we have any error conditions, false for "we're still good"
     */
    protected boolean haveErrors() {
        // now see if everything is complete with this
        for (int i = 0; i < events.size(); i ++) {
            EventSet set = (EventSet)events.get(i);
            // if this test set has received errors, then
            // we can stop listening now.
            if (set.hasErrors()) {
                return true;
            }
        }
        return false;    // no errors yet
    }


    /**
     * Check to see if all of the event sets have received their
     * expected revents.
     *
     * @return true if all expected events in all sets have been received.  false
     *         if we still have unstatisfied events.
     */
    protected boolean isComplete() {
        // now see if everything is complete with this
        for (int i = 0; i < events.size(); i ++) {
            EventSet set = (EventSet)events.get(i);
            // if this test set is still waiting for stuff,
            // then we have to keep listening
            if (!set.isComplete()) {
                return false;
            }
        }

        return true;   // all done
    }


    /**
     * Override the default timeout value.
     *
     * @param timeout The new timeout length.
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }


    /**
     * Retrieve the timeout length.
     *
     * @return The current timeout value.
     */
    public long getTimeout() {
        return timeout;
    }
}


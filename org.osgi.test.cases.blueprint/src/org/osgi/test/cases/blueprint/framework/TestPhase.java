/*
 * Copyright (c) IBM Corporation (2009). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.test.cases.blueprint.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.osgi.framework.BundleContext;

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

    // the list of events we weren't expecting to see
    // make this synchronized so we can update on multiple event threads.
    protected List failures = new ArrayList();

    // our list of event sets that are still waiting for events.
    protected Set pending = new HashSet();

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
    public TestPhase(List eventSets)
    {
        for (int i = 0; i < eventSets.size(); i++) {
            EventSet set = (EventSet)eventSets.get(i);
            set.setTestPhase(this);
            events.add(set);
        }
    }


    /**
     * Add an event set to the phase list.  An event set generally
     * manages events for a single bundle.
     *
     * @param eventSet The added event set.
     */
    public void addEventSet(EventSet eventSet) {
        eventSet.setTestPhase(this);
        events.add(eventSet);
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
     * Wake up the phase controller when a failure occurs.
     *
     * @param event  The event that caused the failure.  This will be
     *               used to raise the failure condition.
     */
    public synchronized void handleFailure(TestEvent event) {
        failures.add(event);
        System.out.println("!!!!!!!!!");
        System.out.println("!!!!!!!!! Test phase ended with failure: " + event);
        System.out.println("!!!!!!!!!");
        notify();
    }

    /**
     * Process an event set indicating it has received all of
     * its expected events.
     *
     * @param set    The completed event set.
     */
    public synchronized void handleCompletion(EventSet set) {
        // remove this from the pending set.  If we've cleared
        // all of these out, then wake up the main thread to process
        // the results
        pending.remove(set);
        System.out.println("!!!!!!!!! handleCompletion() called for EventSet: " + set + " there are " + pending.size() + " still pending");
        if (pending.isEmpty()) {
            System.out.println("!!!!!!!!!");
            System.out.println("!!!!!!!!! Test phase normal completion");
            System.out.println("!!!!!!!!!");
            notify();
        }
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
     * Check the event results at the end of the test phase.
     *
     * @exception Exception
     */
    protected void checkEventResults() throws Exception {
        // we do this in multiple passes...first checking for failures,
        // then missing events, and finally validation failures

        // outright failures
        if (!failures.isEmpty()) {
            // get the first unexpected event and have it raise the exception
            TestEvent event = (TestEvent)failures.get(0);
            event.failUnexpected();
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
    public void runTest() throws Exception {
        // add all event sets to the completion list
        pending.addAll(events);

        // ok, so any startup processing needed by our event processors.
        startEventSets();
        // now wait for either a failure event or receipt of all expected events.
        try {
            synchronized (this) {
                // There's a race condition between the start phase and
                // this point.  As soon as we run the initializers, we start
                // fielding events.  It's entirely possible that we've received
                // a failure or all of the expected events before we
                // reach the point where we wait, so check the state before
                // we wait.

                if (!isComplete()) {
                    wait(timeout);
                }
            }
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
	public void handleEvent(TestEvent event) {
        for (int i = 0; i < events.size(); i ++) {
            EventSet set = (EventSet)events.get(i);
            set.handleEvent(testContext, event);
        }
    }


    /**
     * Check to see if all of the event sets have received their
     * expected revents.
     *
     * @return true if all expected events in all sets have been received.  false
     *         if we still have unstatisfied events.
     */
    protected boolean isComplete() {
        // the event sets notify us when they've received their last expected
        // event.  We're done when the last event set is removed from this.
        return pending.isEmpty();
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


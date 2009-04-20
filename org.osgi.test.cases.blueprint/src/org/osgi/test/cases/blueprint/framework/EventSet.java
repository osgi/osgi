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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * An expected event set attached to a TestPhase.  This holds all of the
 * expected results, as well as holding a list of events that we would
 * consider as test failures if received.  The EventSet also has
 * a set of phase initializers, terminators, and validators that
 * will be run once all expected events have been received.
 */
public class EventSet {
    // our test bundle (needed for adding some validators
    protected BundleContext testBundle;
    // the bundle we're interested in
    protected Bundle componentBundle;
    // the symbolic name of the bundle (used for some events)
    protected String bundleName;

    // the list of events we expect
    protected List expectedEvents = new LinkedList();
    // the list of events that are explicit failures if received
    protected List failureEvents = new LinkedList();
    // the list of events we weren't expecting to see
    protected List unexpectedEvents = new LinkedList();
    // post test validators
    protected List validators = new LinkedList();
    // pre-test setup stages
    protected List initializers = new LinkedList();
    // post-test cleanup phases
    protected List terminators = new LinkedList();

    /**
     * Create a test event set.
     *
     * @param testBundle The bundle context for the test currently running.
     * @param bundle     The bundle that this event set manages.
     */
    public EventSet(BundleContext testBundle, Bundle bundle) {
        this.testBundle = testBundle;
        this.componentBundle = bundle;
        this.bundleName = componentBundle.getSymbolicName();
    }

    /**
     * Perform any test phase initialization steps.
     *
     * @param testContext
     *               The test execution context.
     *
     * @exception Exception
     */
    public void start(BundleContext testContext) throws Exception {
        for (int i = 0; i < initializers.size(); i++) {
            TestInitializer v = (TestInitializer)initializers.get(i);
            v.start(testContext);
        }
    }

    /**
     * Perform any test phase termination steps.
     *
     * @param testContext
     *               The test case bundle context.
     *
     * @exception Exception
     */
    public void stop(BundleContext testContext) throws Exception {
        Iterator i = terminators.iterator();
        while (i.hasNext()) {
            TestCleanup v = (TestCleanup)i.next();
            // remove this from the termination list so we never
            // process this twise
            i.remove();
            v.cleanup(testContext);
        }
    }

    /**
     * Process a received event type by checking it against our
     * expected results and determining if we need to raise this as
     * a failing condition.
     *
     * @param event  The received test event type.
     */
    public void handleEvent(BundleContext testContext, TestEvent event) {
        // generally, an event set only handles events related to a single
        // bundle.  Filter those out here.
        if (!event.isForBundle(componentBundle)) {
            return;
        }

        TestEvent expected = locateEvent(event, expectedEvents);
        // if this was not in the expected list, check the type to
        // see if this is something we want to flag.
        if (expected != null) {
            // have the expected event validate the received event, since
            // the information in the event should be part of the test.
            TestEvent bad = expected.validate(event);
            if (bad != null) {
                // if we got something bad, add this to the unexpected failures list.
                unexpectedEvents.add(bad);
            }
            else {
                // see if this event is intended to trigger any actions
                expected.eventReceived(event);
            }
        }
        else
        {
            expected = locateEvent(event, failureEvents);
            if (expected != null) {
                unexpectedEvents.add(event);
            }

            // if this is a creation failure event that we weren't anticipating,
            // then handle this like an assertion failure
            else if (event.isError()) {
                // this also gets added to the "didn't expect to see this pile"
                unexpectedEvents.add(event);
            }
        }
    }

    /**
     * For some tests, we need to remove some of the
     * default events, either because we need to attach
     * an action to it or because for that specific test,
     * it no longer applies.
     *
     * @param target The target event to remove.
     *
     * @return The removed event (in case we wish to modifying
     *         and reregister).
     */
    public TestEvent removeEvent(TestEvent target) {
        // event equality is frequently based on the bundle
        // that the event is specific to.  We need to inject
        // this into the target so we get correct matching
        if (target instanceof BundleAware) {
            // set the component bundle into the event.
            ((BundleAware)target).setBundle(componentBundle);
        }
        return locateEvent(target, expectedEvents);
    }


    /**
     * Match a received event against the list of expected events.  If
     * a match is located, the matching event is removed from the
     * expected list.
     *
     * @param target The target event we're looking to match.
     * @param source The list of expected events we're searching (generally, either the Start
     *               phase list or the Stop phase list).
     *
     * @return The matched event or null if no match was found.
     */
    public TestEvent locateEvent(TestEvent target, List source) {
        Iterator i = source.iterator();
        while (i.hasNext()) {
            TestEvent current = (TestEvent)i.next();
            // the equal tests needs to be driven by the expected
            if (current.matches(target)) {
                // remove this element from the list and return the matched one
                i.remove();
                return current;
            }
        }
        return null;
    }

    /**
     * Check if an event set has detected any errors in the monitored
     * event set.
     *
     * @return True if we've received something that indicates a test failure,
     *         false if we're still clean.
     */
    public boolean hasErrors() {
        return !unexpectedEvents.isEmpty();
    }

    /**
     * Check to see if this event set has received all of it's expected
     * events.
     *
     * @return true if our expected set is now empty.  false if we're still
     * waiting for something.
     */
    public boolean isComplete() {
        return expectedEvents.isEmpty();
    }


    /**
     * Check our current set of results.  This will raise any needed
     * assertions, either because we've had test failures, or we've
     * not received all of the events we've expected.  If all of that
     * passes, then we run the end-of-phase validators to verify that
     * the environment is in the state we expect to see.
     *
     * @param testContext
     *               The current test context.
     *
     * @exception Exception
     */
    public void checkResults(BundleContext testContext) throws Exception {
        // look at and report unexpected events first.  These might be
        // the cause of any missing events.  This gives us a chance to report
        // the root cause of the failure
        checkUnexpected();
        checkMissing();       // now report any non-received events
        // now go validate any environmental conditions.
        checkEnvironment(testContext);
    }

    /**
     * Check that we've received all of the events we expected to see
     * as a result of starting the bundle.  Generally, this check only
     * occurs after a controller timeout.  We'll raise an exception for
     * any unsatisfied exceptions in our expected queue.
     */
    public void checkMissing() throws Exception {
        if (!expectedEvents.isEmpty()) {
            // give all of the expected event listeners a chance first, then
            // raise an assertion
            for (int i = 0; i < expectedEvents.size(); i++) {
                TestEvent event = (TestEvent)expectedEvents.get(i);
                event.eventNotReceived();
            }

            // get the first missing event and have it raise the exception
            TestEvent event = (TestEvent)expectedEvents.get(0);
            event.failExpected();
        }
    }


    /**
     * Check to see if we had any unexpected events show up.  This is
     * likely an error failure or perhaps some lifecycle event happened
     * more than once.
     */
    public void checkUnexpected() {
        if (!unexpectedEvents.isEmpty()) {
            // get the first unexpected event and have it raise the exception
            TestEvent event = (TestEvent)unexpectedEvents.get(0);
            event.failUnexpected();
        }
    }


    /**
     * Do any post-phase validation checks on the test.
     *
     * @param testContext
     *               The test bundle context.
     *
     * @exception Exception
     */
    public void checkEnvironment(BundleContext testContext) throws Exception {
        for (int i = 0; i < validators.size(); i++) {
            TestValidator v = (TestValidator)validators.get(i);
            v.validate(testContext);
        }
    }


    /**
     * Add an initializer to an event set.
     *
     * @param v      The new initializer to add.
     */
    public void addInitializer(TestInitializer v) {
        if (v instanceof BundleAware) {
            // set the component bundle into the event.
            ((BundleAware)v).setBundle(componentBundle);
        }
        initializers.add(v);
    }


    /**
     * Add a terminator to an event set.
     *
     * @param v      The new terminator to add.
     */
    public void addTerminator(TestCleanup v) {
        if (v instanceof BundleAware) {
            // set the component bundle into the event.
            ((BundleAware)v).setBundle(componentBundle);
        }
        terminators.add(v);
    }


    /**
     * Add a validator to an event set.
     *
     * @param v      The new validator to add.
     */
    public void addValidator(TestValidator v) {
        if (v instanceof BundleAware) {
            // set the component bundle into the event.
            ((BundleAware)v).setBundle(componentBundle);
        }
        validators.add(v);
    }


    /**
     * Add an event to the expected list.
     *
     * @param event  The expected test event.
     */
    public void addEvent(TestEvent event) {
        // set the component bundle into the event.
        event.setBundle(componentBundle);
        expectedEvents.add(event);
    }

    /**
     * Add an event to the failure list.
     *
     * @param event  An event that will cause a test failure if received.
     */
    public void addFailureEvent(TestEvent event) {
        // set the component bundle into the event.
        event.setBundle(componentBundle);
        failureEvents.add(event);
    }

    /**
     * Add a list of expected assertions to our tracking list.
     *
     * @param componentId
     *               The componentId sending the assertions.
     * @param types  The type of assertions to fail on.
     */
    public void addAssertions(String componentId, String[] types) {
        for (int i = 0; i < types.length; i++) {
            addAssertion(componentId, types[i]);
        }
    }

    /**
     * A series of convenience methods to make it easier to add
     * certain types of common events to the set.
     */
    public void addAssertion(String componentId, String type) {
        addEvent(new ComponentAssertion(componentId, type));
    }

    public void addPropertyAssertion(String componentId, String type, String property) {
        addEvent(new ComponentAssertion(componentId, type, property));
    }

    public void addBlueprintEvent(String type) {
        addEvent(new BlueprintEvent(type));
    }

    public void addBlueprintEvent(String type, Map props) {
        addEvent(new BlueprintEvent(type, props));
    }

    public void addBlueprintContextEvent(String type) {
        addEvent(new BlueprintContextEvent(type));
    }

    public void addBlueprintContextEvent(String type, Map props) {
        addEvent(new BlueprintContextEvent(type, props));
    }

    public void addBundleEvent(String type) {
        addEvent(new BundleTestEvent(type));
    }

    public void addBundleEvent(String type, Map props) {
        addEvent(new BundleTestEvent(type, props));
    }

    public void addFrameworkEvent(String type) {
        addEvent(new FrameworkTestEvent(type));
    }

    public void addFrameworkEvent(String type, Map props) {
        addEvent(new FrameworkTestEvent(type, props));
    }

    public void addServiceEvent(String type, String interfaceName, Map props) {
        addEvent(new ServiceTestEvent(type, interfaceName, props));
    }

    public void addServiceEvent(String type, String[] interfaces, Map props) {
        addEvent(new ServiceTestEvent(type, interfaces, props));
    }

    public void addServiceEvent(String type, String interfaceName) {
        addEvent(new ServiceTestEvent(type, interfaceName));
    }

    public void addServiceEvent(String type, String[] interfaces) {
        addEvent(new ServiceTestEvent(type, interfaces));
    }

    public void addServiceEvent(String type, Class interfaceName, Map props) {
        addEvent(new ServiceTestEvent(type, interfaceName, props));
    }

    public void addServiceEvent(String type, Class[] interfaces, Map props) {
        addEvent(new ServiceTestEvent(type, interfaces, props));
    }

    public void addServiceEvent(String type, Class interfaceName) {
        addEvent(new ServiceTestEvent(type, interfaceName));
    }

    public void addServiceEvent(String type, Class[] interfaces) {
        addEvent(new ServiceTestEvent(type, interfaces));
    }
}

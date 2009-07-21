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
import java.util.Map;
import java.util.HashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;

/**
 * Controller for a extender bundle lifecycle tests
 * where the stopping of the extender bundle controls
 * the action.
 */
public class ExtenderStopController extends BaseTestController {
    // the extender bundle we're working on
    protected Bundle extender;

    // this is our startup phase.  General test progression
    // is start the bundle, wait for all of our expected
    // events.
    protected TestPhase startPhase;
    // this is the endphase.  We stop the bundle, then
    // make sure we see all of the cleanup events.
    protected TestPhase endPhase;

    public ExtenderStopController(BundleContext testContext, Bundle extender) {
        super(testContext, DEFAULT_TIMEOUT);
        this.extender = extender;

        startPhase = new TestPhase(testContext, timeout);
        addTestPhase(startPhase);

        endPhase = new TestPhase(testContext, timeout);
        addTestPhase(endPhase);
    }


    /**
     * Add a bundle to this test phase.  This installs the bundle and
     * tracks it.
     *
     * @param bundleName The fully qualified bundle name.
     *
     * @exception Exception
     */
    public void addBundle(String bundleName) throws Exception {
        addBundle(bundleName, 0);
    }


    /**
     * Add a bundle to this test phase.  This installs the bundle and
     * tracks it.
     *
     * @param bundleName The fully qualified bundle name.
     * @param startOptions
     *                   The bundle startup options
     *
     * @exception Exception
     */
    public void addBundle(String bundleName, int startOptions) throws Exception {
        // first install this
        Bundle bundle = installBundle(bundleName);

        // A standard start/stop cycle test has a common set of events we look for
        // in each phase.  Add the events to each list
        EventSet startEvents = new EventSet(testContext, bundle);
        addStartEvents(bundle, startOptions, startEvents);
        startPhase.addEventSet(startEvents);

        EventSet endEvents = new EventSet(testContext, bundle);
        addStopEvents(bundle, endEvents);
        endPhase.addEventSet(endEvents);
    }


    /**
     * Add a standard set of bundle start events for this event type.
     *
     * @param bundle The bundle the event set is tracking.
     * @param startOptions
     *               The bundle start options.
     * @param events The created event set.
     */
    public void addStartEvents(Bundle bundle, int startOptions, EventSet events) {
        // we add an initializer to start our bundle when the test starts
        events.addInitializer(new TestBundleStarter(bundle, 0, startOptions));
        // we always expect to see a started bundle event
        events.addBundleEvent("STARTED");
        // now standard blueprint revents.
        events.addBlueprintEvent("CREATING");
        events.addBlueprintEvent("CREATED");
        events.addServiceEvent("REGISTERED", BlueprintContainer.class.getName());
    }


    /**
     * Add a standard set of bundle stop events for this event type.
     *
     * @param bundle The bundle the event set is tracking.
     * @param events The created event set.
     */
    public void addStopEvents(Bundle bundle, EventSet events) {
        // this is the same as the standard test set, except for the bundle stopper.  That
        // event is managed by stopping the extender bundle

        // we should see the module context unregistered during shutdown.
        events.addServiceEvent("UNREGISTERING", "org.osgi.service.blueprint.container.BlueprintContainer");
        // now standard blueprint revents.
        events.addBlueprintEvent("DESTROYING");
        events.addBlueprintEvent("DESTROYED");

        // at the end of everything, there should no longer be a module context associated with the
        // component bundle.
        events.addValidator(new NoBlueprintContainerValidator());
        // the bundle should still be in the started state when everything settles down
        events.addValidator(new BundleStateValidator(Bundle.ACTIVE));

        // make sure we uninstall this at termination time
        events.addTerminator(new TestBundleUninstaller(bundle));
    }


    /**
     * Perform any controller-related testcase cleanup steps.
     *
     * @param runner The test case running controlling the tests.
     */
    public void cleanup() throws Exception {
        super.cleanup();
        // restart the extender bundle
        extender.start();
    }

    public void setup() throws Exception {
        // get the first set of stop events and add an initializer to the stop phase to
        // shut down the extender bundle.
        EventSet events = getStopEvents(0);

        // we start this test phase out by stopping the bundle.  Everything else flows
        // from that.
        events.addInitializer(new TestBundleStopper(extender));
    }


    /**
     * Retrieve the different phases of this test.
     *
     * @return The starting test phase processor.
     */
    public TestPhase getStartPhase() {
        return startPhase;
    }


    /**
     * Retrieve the different phases of this test.
     *
     * @return The end test phase processor.
     */
    public TestPhase getEndPhase() {
        return endPhase;
    }


    /**
     * Retrieve the different phases of this test.
     *
     * @return The starting test event processor.
     */
    public EventSet getStartEvents() {
        return getStartEvents(0);
    }


    /**
     * Retrieve the different phases of this test.
     *
     * @return The starting test event processor.
     */
    public EventSet getStartEvents(int index) {
        return startPhase.getEventSet(index);
    }


    /**
     * Retrieve the different phases of this test.
     *
     * @return The end test phase processor.
     */
    public EventSet getStopEvents() {
        return getStopEvents(0);
    }


    /**
     * Retrieve the different phases of this test.
     *
     * @return The end test phase processor.
     */
    public EventSet getStopEvents(int index) {
        return endPhase.getEventSet(index);
    }
}



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

/**
 * Controller for a standard, single bundle test with involving
 * a start/stop cycle on a bundle.
 */
public class StandardTestController extends BaseTestController {
    // our list of managed bundles.  This stores the module metadata object associated with the bundle.
    protected Map bundleList = new HashMap();

    // this is our startup phase.  General test progression
    // is start the bundle, wait for all of our expected
    // events.
    protected TestPhase startPhase;
    // this is the endphase.  We stop the bundle, then
    // make sure we see all of the cleanup events.
    protected TestPhase endPhase;

    public StandardTestController(BundleContext testContext) {
        this(testContext, DEFAULT_TIMEOUT);
    }

    public StandardTestController(BundleContext testContext, long timeout) {
        super(testContext, timeout);

        startPhase = new TestPhase(testContext, timeout);
        addTestPhase(startPhase);

        endPhase = new TestPhase(testContext, timeout);
        addTestPhase(endPhase);
    }

    public StandardTestController(BundleContext testContext, String bundle1) throws Exception {
        this(testContext);
        addBundle(bundle1);
    }

    public StandardTestController(BundleContext testContext, String bundle1, String bundle2) throws Exception {
        this(testContext);
        addBundle(bundle1);
        addBundle(bundle2);
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
        // first install this
        Bundle testBundle = installBundle(bundleName);
        // add this to our managed list.
        ModuleMetadata moduleMetadata = new ModuleMetadata(testContext, testBundle);
        bundleList.put(bundleName, moduleMetadata);
        // add the bundle to the appropriate processing lists
        addBundle(testBundle, moduleMetadata);
    }


    /**
     * Add a bundle/metadata combo to the target test controller.
     *
     * @param bundle The installed bundle.
     * @param moduleMetadata
     *               The associated module metadata.
     */
    public void addBundle(Bundle bundle, ModuleMetadata moduleMetadata) {
        // A standard start/stop cycle test has a common set of events we look for
        // in each phase.  Add the events to each list
        EventSet startEvents = new MetadataEventSet(moduleMetadata, testContext, bundle);
        addStartEvents(bundle, moduleMetadata, startEvents);
        startPhase.addEventSet(startEvents);

        EventSet endEvents = new MetadataEventSet(moduleMetadata, testContext, bundle);
        addStopEvents(bundle, moduleMetadata, endEvents);
        endPhase.addEventSet(endEvents);
    }

    /**
     * Add a standard set of bundle start events for this event type.
     *
     * @param bundle The bundle the event set is tracking.
     * @param moduleMetadata
     *               The ModuleMetadata context for this event set.
     * @param events The created event set.
     */
    public void addStartEvents(Bundle bundle, ModuleMetadata moduleMetadata, EventSet events) {
        // we add an initializer to start our bundle when the test starts
        events.addInitializer(new TestBundleStarter(bundle));
        // we always expect to see a started bundle event
        events.addBundleEvent("STARTED");
        // now standard blueprint revents.
        events.addBlueprintEvent("CREATING");
        events.addBlueprintEvent("CREATED");
        events.addModuleContextEvent("CREATED");
        events.addServiceEvent("REGISTERED", "org.osgi.service.blueprint.context.ModuleContext");

        // this needs to be the first validator of the set, since
        // it initializes the module context.
        events.addValidator(moduleMetadata);
        // the bundle should be in the ACTIVE state when everything settles down
        events.addValidator(new BundleStateValidator(Bundle.ACTIVE));
    }

    /**
     * Add a standard set of bundle stop events for this event type.
     *
     * @param bundle The bundle the event set is tracking.
     * @param moduleMetadata
     *               The ModuleMetadata context for this event set.
     * @param events The created event set.
     */
    public void addStopEvents(Bundle bundle, ModuleMetadata moduleMetadata, EventSet events) {
        // we start this test phase out by stopping the bundle.  Everything else flows
        // from that.
        events.addInitializer(new TestBundleStopper(bundle));
        // we always expect to see a stopped bundle event at the end
        events.addBundleEvent("STOPPED");
        // we should see the module context unregistered during shutdown.
        events.addServiceEvent("UNREGISTERING", "org.osgi.service.blueprint.context.ModuleContext");
        // now standard blueprint revents.
        events.addBlueprintEvent("DESTROYING");
        events.addBlueprintEvent("DESTROYED");

        // at the end of everything, there should no longer be a module context associated with the
        // component bundle.
        events.addValidator(new NoModuleContextValidator());
        // the bundle should be in the STOPPED state when everything settles down
        events.addValidator(new BundleStateValidator(Bundle.RESOLVED));
        // this needs to perform some cleanup when everything is done,
        // so add it to the terminator list.
        events.addTerminator(moduleMetadata);
    }



    /**
     * Perform any controller-related testcase cleanup steps.
     *
     * @param runner The test case running controlling the tests.
     */
    public void cleanup() throws Exception {
        super.cleanup();
        Iterator i = bundleList.values().iterator();
        // have each of the module metadata objects uninstall the associated bundles.
        while (i.hasNext()) {
            ModuleMetadata moduleMetadata = (ModuleMetadata)i.next();
            moduleMetadata.cleanup(testContext);
        }
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
    public MetadataEventSet getStartEvents() {
        return getStartEvents(0);
    }


    /**
     * Retrieve the different phases of this test.
     *
     * @return The starting test event processor.
     */
    public MetadataEventSet getStartEvents(int index) {
        return (MetadataEventSet)startPhase.getEventSet(index);
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


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
 * Controller for a extender bundle lifecycle tests
 * where the stopping of the extender bundle controls
 * the action.
 */
public class ExtenderStopController extends StandardTestController {
    // the extender bundle we're working on
    protected Bundle extender;

    public ExtenderStopController(BundleContext testContext, Bundle extender) {
        super(testContext);
        this.extender = extender;
    }


    /**
     * Add a standard set of bundle stop events for this event type.
     *
     * @param bundle The bundle the event set is tracking.
     * @param blueprintMetadata
     *               The BlueprintMetadata context for this event set.
     * @param events The created event set.
     */
    public void addStopEvents(Bundle bundle, BlueprintMetadata blueprintMetadata, EventSet events) {
        // this is the same as the standard test set, except for the bundle stopper.  That
        // event is managed by stopping the extender bundle

        // we should see the module context unregistered during shutdown.
        events.addServiceEvent("UNREGISTERING", "org.osgi.service.blueprint.context.BlueprintContext");
        // now standard blueprint revents.
        events.addBlueprintEvent("DESTROYING");
        events.addBlueprintEvent("DESTROYED");

        // at the end of everything, there should no longer be a module context associated with the
        // component bundle.
        events.addValidator(new NoBlueprintContextValidator());
        // the bundle should still be in the started state when everything settles down
        events.addValidator(new BundleStateValidator(Bundle.ACTIVE));
        // this needs to perform some cleanup when everything is done,
        // so add it to the terminator list.
        events.addTerminator(blueprintMetadata);
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
}



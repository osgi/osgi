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
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * Controller for a standard, single bundle test with involving
 * a start/stop cycle on a bundle.
 */
public class ThreePhaseTestController extends StandardTestController {
    // this is our middle test phase.
    protected TestPhase middlePhase;

    public ThreePhaseTestController(BundleContext testContext) {
        super(testContext);
        middlePhase = new TestPhase(testContext, timeout);
        // this inserts this between the start and stop hases
        addTestPhase(1, middlePhase);
    }

    public ThreePhaseTestController(BundleContext testContext, String bundle1) throws Exception {
        this(testContext);
        addBundle(bundle1);
    }

    public ThreePhaseTestController(BundleContext testContext, String bundle1, String bundle2) throws Exception {
        this(testContext);
        addBundle(bundle1);
        addBundle(bundle2);
    }


    /**
     * Add a bundle/metadata combo to the target test controller.
     *
     * @param bundle The installed bundle.
     * @param startOptions
     *               The bundle start options
     * @param blueprintMetadata
     *               The associated module metadata.
     */
    public void addBundle(Bundle bundle, int startOptions, BlueprintMetadata blueprintMetadata) {
        // add the standard events, then our additions
        super.addBundle(bundle, startOptions, blueprintMetadata);

        // The middle event set has no standard set of items to add.  This is filled
        // in by the test creater.
        EventSet middleEvents = new MetadataEventSet(blueprintMetadata, testContext, bundle);
        addMiddleEvents(bundle, blueprintMetadata, middleEvents);
        middlePhase.addEventSet(middleEvents);
    }

    /**
     * Add a standard set of bundle middle events for this event type.
     *
     * @param bundle The bundle the event set is tracking.
     * @param blueprintMetadata
     *               The BlueprintMetadata context for this event set.
     * @param events The created event set.
     */
    public void addMiddleEvents(Bundle bundle, BlueprintMetadata blueprintMetadata, EventSet events) {
        // no standard set for these
    }


    /**
     * Retrieve the different phases of this test.
     *
     * @return The middle test phase processor.
     */
    public TestPhase getMiddlePhase() {
        return middlePhase;
    }


    /**
     * Retrieve the different phases of this test.
     *
     * @return The middle phase test event processor.
     */
    public MetadataEventSet getMiddleEvents() {
        return getMiddleEvents(0);
    }


    /**
     * Retrieve the different phases of this test.
     *
     * @return The middle phase test event processor.
     */
    public MetadataEventSet getMiddleEvents(int index) {
        return (MetadataEventSet)middlePhase.getEventSet(index);
    }
}


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
 * Controller for a standard error test.  This is a single-phase test
 * that will validate that the BlueprintContext is not created.
 */
public class StandardErrorTestController extends BaseTestController {
    // our stanard tests have just a single bundle use for
    // for the testing.
    protected Bundle bundle;
    // this is our startup phase.  General test progression
    // is start the bundle, wait for all of our expected
    // events.
    protected TestPhase testPhase;

    public StandardErrorTestController(BundleContext testContext, String testBundle) throws Exception {
        this(testContext, testBundle, DEFAULT_TIMEOUT);
    }

    public StandardErrorTestController(BundleContext testContext, String testBundle, long timeout) throws Exception {
        super(testContext, timeout);
        // we're responsible for the bundle install/uninstall.
        bundle = installBundle(testBundle);

        testPhase = new TestPhase(testContext, timeout);
        // A standard start/stop cycle test has a common set of events we look for
        // in each phase.
        testPhase.addEventSet(new StandardErrorEventSet(testContext, bundle));

        addTestPhase(testPhase);
    }


    /**
     * Retrieve the different phases of this test.
     *
     * @return The starting test phase processor.
     */
    public TestPhase getTestPhase() {
        return testPhase;
    }


    /**
     * Retrieve the different phases of this test.
     *
     * @return The starting test event processor.
     */
    public EventSet getTestEvents() {
        return testPhase.getEventSet(0);
    }


    /**
     * Perform any controller-related testcase cleanup steps.
     *
     * @param runner The test case running controlling the tests.
     */
    public void cleanup() throws Exception {
        super.cleanup();
        // uninstall the bundle on completion
        bundle.uninstall();
    }
}


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
 * A test initializer use to start a test bundle at the beginning of
 * a test phase.
 */
public class TestBundleStarter implements TestInitializer {
    // the bundle we manage
    protected Bundle bundle;
    // a delay to apply to the start (in milliseconds)
    protected long delay = 0;
    protected int startOptions = 0;

    public TestBundleStarter(Bundle bundle) {
        this.bundle = bundle;
    }

    public TestBundleStarter(Bundle bundle, long delay) {
        this(bundle, delay, 0);
    }

    public TestBundleStarter(Bundle bundle, long delay, int startOptions) {
        this.bundle = bundle;
        this.delay = delay;
        this.startOptions = startOptions;
    }


    /**
     * Perform any additional test phase cleanup actions.
     *
     * @param testContext
     *               The BundleContext for the test (used for inspecting the test
     *               environment).
     *
     * @exception Exception
     */
    public void start(BundleContext testContext) throws Exception {
        // we might want to delay starting this
        if (delay > 0) {
            try {
                // half a second should be sufficiently long, likely longer than is needed.
                Thread.sleep(delay);
            } catch (InterruptedException e) {
            }
        }

        System.out.println(">>>>>>>> starting bundle " + bundle);
        bundle.start(startOptions);
    }
}

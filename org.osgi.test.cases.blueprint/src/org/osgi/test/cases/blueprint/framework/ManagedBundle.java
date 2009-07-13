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

import java.io.InputStream;
import java.net.URL;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * A to manage the lifecycle of a test bundle that exists just
 * for noise purposes.  We're not interested in tracking any of the
 * events, but we need fine-grained control over when this bundle is started
 * and stopped.
 */
public class ManagedBundle implements TestCleanup, TestInitializer {
    // The ReplayListener instance we're interfacing with
    protected Bundle bundle;

    public ManagedBundle(BundleContext testContext, String bundleName) throws Exception {
        URL url = new URL(bundleName);
        InputStream in = url.openStream();
        bundle = testContext.installBundle(bundleName, in);
    }


    /**
     * Perform any additional test phase setup actions.
     *
     * @param testContext
     *               The BundleContext for the test (used for inspecting the test
     *               environment).
     *
     * @exception Exception
     */
    public void start(BundleContext testContext) throws Exception {
        bundle.start();
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
    public void cleanup(BundleContext testContext) throws Exception {
        bundle.stop();
        bundle.uninstall();
    }
}


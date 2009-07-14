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
 * uninstall a bundle at the end of a test phase.
 */
public class TestBundleInstaller implements TestInitializer, TestCleanup {
    // the bundle we manage
    protected String bundleName;
    // the installed bundle
    protected Bundle bundle;

    public TestBundleInstaller(String bundleName) {
        this.bundleName = bundleName;
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
        URL url = new URL(bundleName);
        InputStream in = url.openStream();

        System.out.println(">>>>>>> installing bundle " + bundleName);
        bundle = testContext.installBundle(bundleName, in);
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
        if (bundle != null) {
            System.out.println(">>>>>>>> uninstalling bundle " + bundle);
            // this will stop the bundle also.
            bundle.uninstall();
            bundle = null;
        }
    }
}


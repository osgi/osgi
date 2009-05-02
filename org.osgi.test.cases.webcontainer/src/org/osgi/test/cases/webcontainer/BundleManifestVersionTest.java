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
package org.osgi.test.cases.webcontainer;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.service.webcontainer.WebContainer;

/**
 * @version $Rev$ $Date$
 * 
 *          test Bundle-ManifestVersion manifest header processed correctly with
 *          various scenarios
 */
public class BundleManifestVersionTest extends ManifestHeadersTestBundleControl {
    private static final String MANIFESTVERSION1 = "2";
    private static final String MANIFESTVERSION2 = "4";
    private static final String MANIFESTVERSION3 = "1";

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * verify valid deployOptions overwrite original manifest
     * Bundle-ManifestVersion
     */
    public void testBundleManifestVersion001() throws Exception {
        generalManifestVersionTest(MANIFESTVERSION1, "/tw1", "tw1.war", false);
    }

    /*
     * verify valid deployOptions overwrite original manifest
     * Bundle-ManifestVersion
     */
    public void testBundleManifestVersion002() throws Exception {
        generalManifestVersionTest(MANIFESTVERSION1, "/tw2", "tw2.war", true);
    }

    /*
     * verify valid deployOptions overwrite original manifest
     * Bundle-ManifestVersion
     */
    public void testBundleManifestVersion003() throws Exception {
        generalManifestVersionTest(MANIFESTVERSION1, "/tw3", "tw3.war", true);
    }

    /*
     * verify valid deployOptions overwrite original manifest
     * Bundle-ManifestVersion
     */
    public void testBundleManifestVersion004() throws Exception {
        generalManifestVersionTest(MANIFESTVERSION1, "/tw4", "tw4.war", false);
    }

    /*
     * verify valid deployOptions overwrite original manifest
     * Bundle-ManifestVersion
     */
    public void testBundleManifestVersion005() throws Exception {
        generalManifestVersionTest(MANIFESTVERSION1, "/tw5", "tw5.war", false);
    }

    /*
     * verify Bundle-ManifestVersion being null in deployOptions
     */
    public void testBundleManifestVersion006() throws Exception {
        generalManifestVersionTest(null, "/tw1", "tw1.war", true);
    }

    /*
     * verify Bundle-ManifestVersion being null in deployOptions
     */
    public void testBundleManifestVersion007() throws Exception {
        generalManifestVersionTest(null, "/tw2", "tw2.war", false);
    }

    /*
     * verify Bundle-ManifestVersion being null in deployOptions
     */
    public void testBundleManifestVersion008() throws Exception {
        generalManifestVersionTest(null, "/tw3", "tw3.war", false);
    }

    /*
     * verify Bundle-ManifestVersion being null in deployOptions
     */
    public void testBundleManifestVersion009() throws Exception {
        generalManifestVersionTest(null, "/tw4", "tw4.war", false);
    }

    /*
     * verify Bundle-ManifestVersion being null in deployOptions
     */
    public void testBundleManifestVersion010() throws Exception {
        generalManifestVersionTest(null, "/tw5", "tw5.war", true);
    }

    /*
     * verify invalid Bundle-ManifestVersion in deployOptions
     */
    public void testBundleManifestVersion011() throws Exception {
        // specify install options
        final Map options = new HashMap();
        options.put(Constants.BUNDLE_VERSION, MANIFESTVERSION2);
        options.put(WebContainer.WEB_CONTEXT_PATH, "/tw2");
        // install the war file
        log("install war file: tw2.war at context path /tw2");
        try {
            this.b = installBundle(super.getWarURL("tw2.war", options), false);
            fail("bundle install should fail: " + Constants.BUNDLE_VERSION
                    + " = " + MANIFESTVERSION2);
        } catch (Exception e) {
            // expected unless the framework supports Bundle-ManifestVersion
            // greater than 2
            log("this is expected if the framework doesn't support the "
                    + Constants.BUNDLE_VERSION + " = " + MANIFESTVERSION2);
        }
        assertNull("Bundle b should be null", this.b);

        // test unable to access /tw2 yet as it is not started
        assertFalse("should not be able to access /tw2", super
                .ableAccessPath("/tw2/"));
    }

    /*
     * verify invalid Bundle-ManifestVersion in deployOptions
     */
    public void testBundleManifestVersion012() throws Exception {
        // specify install options
        final Map options = new HashMap();
        options.put(Constants.BUNDLE_VERSION, MANIFESTVERSION3);
        options.put(WebContainer.WEB_CONTEXT_PATH, "/tw3");
        // install the war file
        log("install and start war file: tw3.war at context path /tw3");
        try {
            this.b = installBundle(super.getWarURL("tw3.war", options), true);
            fail("bundle install should fail: " + Constants.BUNDLE_VERSION
                    + " = " + MANIFESTVERSION3);
        } catch (Exception e) {
            // expected;
        }
        assertNull("Bundle b should be null", this.b);

        // test unable to access /tw3 yet as it is not started
        assertFalse("should not be able to access /tw3", super
                .ableAccessPath("/tw3/"));
    }

    /*
     * generalManifestVersionTest to be used by non-error test
     */
    private Bundle generalManifestVersionTest(String version, String cp,
            String warName, boolean start) throws Exception {
        // specify install options
        final Map options = new HashMap();
        options.put(Constants.BUNDLE_MANIFESTVERSION, version);
        options.put(WebContainer.WEB_CONTEXT_PATH, cp);
        return super.generalHeadersTest(options, warName, start);
    }

    // TODO create war manifest that contains the Bundle-ManifestVersion header
    // and more
    // tests
}

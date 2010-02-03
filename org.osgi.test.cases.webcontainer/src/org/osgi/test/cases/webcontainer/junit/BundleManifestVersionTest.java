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
package org.osgi.test.cases.webcontainer.junit;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.test.cases.webcontainer.util.ManifestHeadersTestBundleControl;

/**
 * @version $Rev$ $Date$
 * 
 *          test Bundle-ManifestVersion manifest header processed correctly with
 *          various scenarios
 */
public class BundleManifestVersionTest extends ManifestHeadersTestBundleControl {

    private Map<String, Object> createOptions(String version, String cp) {
        final Map<String, Object> options = new HashMap<String, Object>();
        options.put(Constants.BUNDLE_MANIFESTVERSION, version);
        options.put(WEB_CONTEXT_PATH, cp);
        return options;
    }
    
    /*
     * verify valid deployOptions overwrite original manifest
     * Bundle-ManifestVersion
     */
    public void testBundleManifestVersion001() throws Exception {
        final Map<String, Object> options = createOptions(MANIFESTVERSION1, "/tw1");
        this.b = super.installWar(options, "tw1.war", false);
        generalHeadersTest(options, "tw1.war", false, this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest
     * Bundle-ManifestVersion
     */
    public void testBundleManifestVersion004() throws Exception {
        final Map<String, Object> options = createOptions(MANIFESTVERSION1, "/tw4");
        this.b = super.installWar(options, "tw4.war", false);
        super.generalHeadersTest(options, "tw4.war", false, this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest
     * Bundle-ManifestVersion
     */
    public void testBundleManifestVersion005() throws Exception {
        final Map<String, Object> options = createOptions(MANIFESTVERSION1, "/tw5");
        this.b = super.installWar(options, "wmtw5.war", false);
        generalHeadersTest(options, "wmtw5.war", false, this.b);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest
     * Bundle-ManifestVersion
     */
    public void testBundleManifestVersion006() throws Exception {
        final Map<String, Object> options = createOptions(MANIFESTVERSION1, "/tw1");
        this.b = super.installWar(options, "wmtw1.war", false);
        generalHeadersTest(options, "wmtw1.war", false, this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest
     * Bundle-ManifestVersion
     */
    public void testBundleManifestVersion009() throws Exception {
        final Map<String, Object> options = createOptions(MANIFESTVERSION1, "/tw4");
        this.b = super.installWar(options, "wmtw4.war", false);
        generalHeadersTest(options, "wmtw4.war", false, this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest
     * Bundle-ManifestVersion
     */
    public void testBundleManifestVersion010() throws Exception {
        final Map<String, Object> options = createOptions(MANIFESTVERSION1, "/tw5");
        this.b = super.installWar(options, "wmtw5.war", false);
        generalHeadersTest(options, "wmtw5.war", false, this.b);
    }
    
    /*
     * verify null bundle-manifestVersion
     */
    public void testBundleManifestVersion011() throws Exception {
        final Map<String, Object> options = createOptions(null, "/tw5");
        this.b = super.installWar(options, "wmtw5.war", false);
        generalHeadersTest(options, "wmtw5.war", false, this.b);
    }

    /*
     * verify invalid Bundle-ManifestVersion in deployOptions
     */
    public void testBundleManifestVersionError001() throws Exception {
        // specify install options
        final Map<String, Object> options = createOptions(MANIFESTVERSION3, "/tw1");
        // install the war file
        log("install war file: tw1.war at context path /tw1");
        try {
            this.b = installBundle(super.getWarURL("tw1.war", options), true);
            fail("bundle install should fail: " + Constants.BUNDLE_VERSION
                    + " = " + MANIFESTVERSION3);
        } catch (BundleException be) {
            // expected unless the framework supports Bundle-ManifestVersion
            // greater than 2
            log("this is expected if the framework doesn't support the "
                    + Constants.BUNDLE_VERSION + " = " + MANIFESTVERSION3);
        }
        assertNull("Bundle b should be null", this.b);

        // test unable to access /tw2 yet as it is not started
        assertFalse("should not be able to access /tw1", super
                .ableAccessPath("/tw1/"));
    }

    /*
     * verify invalid Bundle-ManifestVersion in deployOptions
     */
    public void testBundleManifestVersionError002() throws Exception {
        // specify install options
        final Map<String, Object> options = createOptions(MANIFESTVERSION3, "/tw4");
        // install the war file
        log("install and start war file: tw4.war at context path /tw4");
        try {
            this.b = installBundle(super.getWarURL("tw4.war", options), false);
            fail("bundle install should fail: " + Constants.BUNDLE_VERSION
                    + " = " + MANIFESTVERSION3);
        } catch (BundleException be) {
            // expected;
        }
        assertNull("Bundle b should be null", this.b);

        // test unable to access /tw3 yet as it is not started
        assertFalse("should not be able to access /tw4", super
                .ableAccessPath("/tw4/"));
    }
}

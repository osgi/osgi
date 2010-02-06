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
 *          test Bundle-Version manifest header processed correctly with various
 *          scenarios
 */
public class BundleVersionTest extends ManifestHeadersTestBundleControl {

    private Map<String, Object> createOptions(String version, String cp) {
        final Map<String, Object> options = new HashMap<String, Object>();
        options.put(Constants.BUNDLE_VERSION, version);
        options.put(WEB_CONTEXT_PATH, cp);
        return options;
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Bundle-Version
     */
    public void testBundleVersion001() throws Exception {
        final Map<String, Object> options = createOptions(VERSION1, "/tw1");
        this.b = super.installWar(options, "tw1.war", false);
        super.generalHeadersTest(options, "tw1.war", false, this.b);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Bundle-Version
     * test case insensitive
     */
    public void testBundleVersion001_1() throws Exception {
        final Map<String, Object> options = new HashMap<String, Object>();
        options.put("bundle-version", VERSION1);
        options.put(WEB_CONTEXT_PATH, "/tw1");
        this.b = super.installWar(options, "tw1.war", false);
        options.remove("bundle-version");
        options.put(Constants.BUNDLE_VERSION, VERSION1);
        super.generalHeadersTest(options, "tw1.war", false, this.b);
    }


    /*
     * verify valid deployOptions overwrite original manifest Bundle-Version
     */
    public void testBundleVersion004() throws Exception {
        final Map<String, Object> options = createOptions(VERSION3, "/tw4");
        this.b = super.installWar(options, "tw4.war", true);
        super.generalHeadersTest(options, "tw4.war", true, this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest Bundle-Version
     */
    public void testBundleVersion005() throws Exception {
        final Map<String, Object> options = createOptions(VERSION3, "/tw5");
        this.b = super.installWar(options, "tw5.war", false);
        super.generalHeadersTest(options, "tw5.war", false, this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest Bundle-Version
     */
    public void testBundleVersion006() throws Exception {
        final Map<String, Object> options = createOptions(VERSION1, "/tw1");
        try {
            this.b = super.installWar(options, "wmtw1.war", false);
        } catch (BundleException be) {
            // expected as Bundle-Version is specified as invalid url param for WAB
        }
    }

    /*
     * verify valid deployOptions overwrite original manifest Bundle-Version
     */
    public void testBundleVersion009() throws Exception {
        final Map<String, Object> options = createOptions(VERSION3, "/tw4");
        this.b = super.installWar(options, "wmtw4.war", true);
        super.generalHeadersTest(options, "wmtw4.war", true, this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest Bundle-Version
     */
    public void testBundleVersion010() throws Exception {
        final Map<String, Object> options = createOptions(VERSION3, "/tw5");
        this.b = super.installWar(options, "wmtw5.war", false);
        super.generalHeadersTest(options, "wmtw5.war", false, this.b);
    }
    
    /*
     * error case, when version specified by deployer is invalid
     */
    public void testBundleVersionError001() throws Exception {
        // specify install options
        final Map<String, Object> options = createOptions(VERSION4, "/tw4");
        // install the war file
        log("install war file: tw4.war at context path /tw4");
        // may not be able to installBundle correctly if version is specified
        // improperly??
        try {
            this.b = installBundle(super.getWarURL("tw4.war", options), false);
            fail("should be getting install BundleException as version format is invalid");
        } catch (BundleException be) {
            // expected
        }
        assertNull("Bundle b should be null", this.b);

        // test unable to access /tw4 yet as it is not installed
        assertFalse("should not be able to access /tw4", super
                .ableAccessPath("/tw4/"));
    }

    /*
     * error case, when version specified by deployer is invalid
     */
    public void testBundleVersionError002() throws Exception {
        // specify install options
        final Map<String, Object> options = createOptions(VERSION5, "/tw5");
        // install the war file
        log("install war file: tw5.war at context path /tw5");
        // may not be able to installBundle correctly if version is specified
        // improperly
        try {
            this.b = installBundle(super.getWarURL("tw5.war", options), false);
            fail("should be getting install BundleException as version format is invalid");
        } catch (BundleException be) {
            // expected
        }
        assertNull("Bundle b should be null", this.b);

        // test unable to access /tw5 yet as it is not installed
        assertFalse("should not be able to access /tw5", super
                .ableAccessPath("/tw5/"));
    }
}

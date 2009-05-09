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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.service.webcontainer.WebContainer;
import org.osgi.test.cases.webcontainer.ManifestHeadersTestBundleControl;

/**
 * @version $Rev$ $Date$
 * 
 *          test Bundle-Version manifest header processed correctly with various
 *          scenarios
 */
public class BundleVersionTest extends ManifestHeadersTestBundleControl {

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * verify valid deployOptions overwrite original manifest Bundle-Version
     */
    public void testBundleVersion001() throws Exception {
        this.b = generalVersionTest(VERSION1, "/tw1", "tw1.war", false);
    }

    /*
     * verify valid deployOptions overwrite original manifest Bundle-Version
     */
    public void testBundleVersion002() throws Exception {
        this.b = generalVersionTest(VERSION2, "/tw2", "tw2.war", false);
    }

    /*
     * verify valid deployOptions overwrite original manifest Bundle-Version
     */
    public void testBundleVersion003() throws Exception {
        this.b = generalVersionTest(VERSION3, "/tw3", "tw3.war", false);
    }

    /*
     * verify valid deployOptions overwrite original manifest Bundle-Version
     */
    public void testBundleVersion004() throws Exception {
        this.b = generalVersionTest(VERSION3, "/tw4", "tw4.war", true);
    }

    /*
     * verify valid deployOptions overwrite original manifest Bundle-Version
     */
    public void testBundleVersion005() throws Exception {
        this.b = generalVersionTest(VERSION3, "/tw5", "tw5.war", false);
    }

    /*
     * verify valid deployOptions overwrite original manifest Bundle-Version
     */
    public void testBundleVersion006() throws Exception {
        this.b = generalVersionTest(VERSION1, "/tw1", "wmtw1.war", false);
    }

    /*
     * verify valid deployOptions overwrite original manifest Bundle-Version
     */
    public void testBundleVersion007() throws Exception {
        this.b = generalVersionTest(VERSION2, "/tw2", "wmtw2.war", false);
    }

    /*
     * verify valid deployOptions overwrite original manifest Bundle-Version
     */
    public void testBundleVersion008() throws Exception {
        this.b = generalVersionTest(VERSION3, "/tw3", "wmtw3.war", false);
    }

    /*
     * verify valid deployOptions overwrite original manifest Bundle-Version
     */
    public void testBundleVersion009() throws Exception {
        this.b = generalVersionTest(VERSION3, "/tw4", "wmtw4.war", true);
    }

    /*
     * verify valid deployOptions overwrite original manifest Bundle-Version
     */
    public void testBundleVersion010() throws Exception {
        this.b = generalVersionTest(VERSION3, "/tw5", "wmtw5.war", false);
    }
    
    /*
     * error case, when version specified by deployer is invalid
     */
    public void testBundleVersionError001() throws Exception {
        // specify install options
        final Map options = new HashMap();
        options.put(Constants.BUNDLE_VERSION, VERSION4);
        options.put(WebContainer.WEB_CONTEXT_PATH, "/tw4");
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
        final Map options = new HashMap();
        options.put(Constants.BUNDLE_VERSION, VERSION5);
        options.put(WebContainer.WEB_CONTEXT_PATH, "/tw5");
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

    /*
     * generalVersionTest to be used by non-error test
     */
    private Bundle generalVersionTest(String version, String cp,
            String warName, boolean start) throws Exception {
        // specify install options
        final Map options = new HashMap();
        options.put(Constants.BUNDLE_VERSION, version);
        options.put(WebContainer.WEB_CONTEXT_PATH, cp);
        return super.generalHeadersTest(options, warName, start);
    }
}

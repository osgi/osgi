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
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.service.webcontainer.WebContainer;

/**
 * @version $Rev$ $Date$
 * 
 *          test Bundle-Classpath manifest header processed correctly with various
 *          scenarios
 */
public class BundleClasspathTest extends ManifestHeadersTestBundleControl {
    private static final String[] CLASSPATH1 = {"lib2/log.jar"}; 
    private static final String[] CLASSPATH2 = {"lib2/util.jar","lib2/log.jar"};
    private static final String[] CLASSPATH3 = {"lib2/util.jar","lib2/log.jar", "libs/utiljar"};

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * verify valid deployOptions overwrite original manifest Bundle-Classpath
     */
    public void testBundleClasspath001() throws Exception {
        this.b = generalClasspathTest(CLASSPATH1, "/tw1", "tw1.war", false);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Bundle-Classpath
     */
    public void testBundleClasspath002() throws Exception {
        this.b = generalClasspathTest(CLASSPATH2, "/tw2", "tw2.war", false);
    }

    /*
     * verify when Bundle-Classpath is not specified
     */
    public void testBundleClasspath006() throws Exception {
        this.b = generalClasspathTest(null, "/tw1", "tw5.war", false);
    }
    
    /*
     * verify when Bundle-Classpath is not specified
     */
    public void testBundlClasspath007() throws Exception {
        this.b = generalClasspathTest(null, "/tw5", "tw5.war", false);
    }
    
    /*
     * verify when Bundle-Classpath is not specified
     */
    public void testBundleClasspath008() throws Exception {
        this.b = generalClasspathTest(null, "/tw5", "tw5.war", false);
    }
    
    /*
     * verify when Bundle-Classpath is not specified
     */
    public void testBundleClasspath009() throws Exception {
        this.b = generalClasspathTest(null, "/tw5", "tw5.war", false);
    }
    
    /*
     * verify when Bundle-Classpath is not specified
     */
    public void testBundleClasspath010() throws Exception {
        this.b = generalClasspathTest(null, "/tw5", "tw5.war", false);
    }

    /*
     * error case, when Bundle-Classpath specified by deployer is valid but cannot locate the file
     * 
     */
    public void testBundleClasspath011() throws Exception {
        // specify install options
        final Map options = new HashMap();
        options.put(Constants.BUNDLE_CLASSPATH, CLASSPATH1);
        options.put(WebContainer.WEB_CONTEXT_PATH, "/tw4");
        // install the war file
        log("install war file: tw4.war at context path /tw4");
        // may not be able to installBundle correctly if version is specified
        // improperly??
        try {
            this.b = installBundle(super.getWarURL("tw4.war", options), false);
            fail("should be getting install BundleException as Bundle-Classpath contains jar that doesn't exist");
        } catch (BundleException e) {
            // expected
        }
        assertNull("Bundle b should be null", this.b);

        // test unable to access /tw4 yet as it is not installed
        assertFalse("should not be able to access /tw4", super
                .ableAccessPath("/tw4/"));
    }

    /*
     * error case, when Bundle-Classpath specified by deployer is invalid format
     */
    public void testBundleClasspath012() throws Exception {
        // specify install options
        final Map options = new HashMap();
        options.put(Constants.BUNDLE_CLASSPATH, CLASSPATH3);
        options.put(WebContainer.WEB_CONTEXT_PATH, "/tw5");
        // install the war file
        log("install war file: tw5.war at context path /tw5");
        // may not be able to installBundle correctly if version is specified
        // improperly
        try {
            this.b = installBundle(super.getWarURL("tw5.war", options), true);
            fail("should be getting install BundleException as Bundle-Classpath format is invalid");
        } catch (BundleException e) {
            // expected
        }
        assertNull("Bundle b should be null", this.b);

        // test unable to access /tw5 yet as it is not installed
        assertFalse("should not be able to access /tw5", super
                .ableAccessPath("/tw5/"));
    }

    /*
     * generalClasspathTest to be used by non-error test
     */
    private Bundle generalClasspathTest(String[] classpath, String cp,
            String warName, boolean start) throws Exception {
        // specify install options
        final Map options = new HashMap();
        options.put(Constants.BUNDLE_CLASSPATH, classpath);
        options.put(WebContainer.WEB_CONTEXT_PATH, cp);
        return super.generalHeadersTest(options, warName, start);
    }

    // TODO create war manifest that contains the Bundle-Version header and more
    // tests
}

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
import org.osgi.test.cases.webcontainer.ManifestHeadersTestBundleControl;

/**
 * @version $Rev$ $Date$
 * 
 *          test Bundle-Classpath manifest header processed correctly with various
 *          scenarios
 */
public class BundleClasspathTest extends ManifestHeadersTestBundleControl {
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
        this.b = generalClasspathTest(CLASSPATH3, "/tw1", "tw1.war", false);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Bundle-Classpath
     */
    public void testBundleClasspath002() throws Exception {
        this.b = generalClasspathTest(CLASSPATH3, "/tw2", "tw2.war", false);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Bundle-Classpath
     */
    public void testBundleClasspath003() throws Exception {
        this.b = generalClasspathTest(CLASSPATH1, "/tw5", "tw5.war", true);
        classpassServletTest(this.b);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Bundle-Classpath
     */
    public void testBundleClasspath004() throws Exception {
        this.b = generalClasspathTest(CLASSPATH2, "/tw5", "tw5.war", true);
        classpassServletTest(this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest Bundle-Classpath
     */
    public void testBundleClasspath005() throws Exception {
        this.b = generalClasspathTest(CLASSPATH1, "/tw5", "wm2tw5.war", true);
        classpassServletTest(this.b);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Bundle-Classpath
     */
    public void testBundleClasspath006() throws Exception {
        this.b = generalClasspathTest(CLASSPATH1, "/tw5", "wm2tw5.war", true);
        classpassServletTest(this.b);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Bundle-Classpath
     */
    public void testBundleClasspath007() throws Exception {
        this.b = generalClasspathTest(CLASSPATH2, "/tw5", "wm3tw5.war", true);
        classpassServletTest(this.b);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Bundle-Classpath
     */
    public void testBundleClasspath008() throws Exception {
        this.b = generalClasspathTest(CLASSPATH2, "/tw5", "wm3tw5.war", true);
        classpassServletTest(this.b);
    }

    /*
     * verify when Bundle-Classpath is not specified
     */
    public void testBundleClasspath019() throws Exception {
        this.b = generalClasspathTest(null, "/tw5", "wm2tw5.war", true);
        classpassServletTest(this.b);
    }
    
    /*
     * verify when Bundle-Classpath is not specified
     */
    public void testBundleClasspath020() throws Exception {
        this.b = generalClasspathTest(null, "/tw5", "wm3tw5.war", true);
        classpassServletTest(this.b);
    }


    /*
     * error case, when Bundle-Classpath specified by deployer is valid but cannot locate the file
     * 
     */
    public void testBundleClasspathError001() throws Exception {
        // specify install options
        final Map options = new HashMap();
        options.put(Constants.BUNDLE_CLASSPATH, CLASSPATH4);
        options.put(WEB_CONTEXT_PATH, "/tw4");
        // install the war file
        log("install war file: tw4.war at context path /tw4");
        // may not be able to installBundle correctly if Bundle-Classpath is specified
        // improperly??
        try {
            this.b = installBundle(super.getWarURL("tw4.war", options), false);
        } catch (BundleException be) {
            fail("should not be getting install BundleException as Bundle-Classpath contains jar that have valid format");
        }
        assertNotNull("Bundle b should be not null", this.b);
        assertEquals("Checking Bundle state is installed", b.getState(), Bundle.INSTALLED);
        
        try {
            this.b.start();
            fail("No exception thrown, Error!");
        } catch (BundleException be) {
            // expected
        }
        // test unable to access /tw4 yet as it is not installed
        assertFalse("should not be able to access /tw4", super
                .ableAccessPath("/tw4/"));
    }

    /*
     * error case, when Bundle-Classpath specified by deployer is invalid format
     */
    public void testBundleClasspathError002() throws Exception {
        // specify install options
        final Map options = new HashMap();
        options.put(Constants.BUNDLE_CLASSPATH, CLASSPATH5);
        options.put(WEB_CONTEXT_PATH, "/tw5");
        // install the war file
        log("install war file: tw5.war at context path /tw5");
        // may not be able to installBundle correctly if version is specified
        // improperly
        try {
            this.b = installBundle(super.getWarURL("tw5.war", options), false);
            fail("should be getting install BundleException as Bundle-Classpath format is invalid");
        } catch (BundleException be) {
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
        options.put(WEB_CONTEXT_PATH, cp);
        return super.generalHeadersTest(options, warName, start);
    }
}

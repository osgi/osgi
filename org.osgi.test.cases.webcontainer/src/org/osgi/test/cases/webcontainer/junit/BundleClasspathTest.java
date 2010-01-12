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

    private Map<String, Object> createOptions(String[] classpath, String cp) {
        final Map<String, Object> options = new HashMap<String, Object>();
        options.put(Constants.BUNDLE_CLASSPATH, classpath);
        options.put(WEB_CONTEXT_PATH, cp);
        return options;
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Bundle-Classpath
     */
    public void testBundleClasspath001() throws Exception {
        final Map<String, Object> options = createOptions(CLASSPATH3, "/tw1");
        this.b = super.installWar(options, "tw1.war", false);
        super.generalHeadersTest(options, "tw1.war", false, this.b);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Bundle-Classpath
     */
    public void testBundleClasspath002() throws Exception {
        final Map<String, Object> options = createOptions(CLASSPATH3, "/tw2");
        this.b = super.installWar(options, "tw2.war", false);
        super.generalHeadersTest(options, "tw2.war", false, this.b);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Bundle-Classpath
     */
    public void testBundleClasspath003() throws Exception {
        final Map<String, Object> options = createOptions(CLASSPATH1, "/tw5");
        this.b = super.installWar(options, "tw5.war", true);
        super.generalHeadersTest(options, "tw5.war", true, this.b);
        classpassServletTest(this.b);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Bundle-Classpath
     */
    public void testBundleClasspath004() throws Exception {
        final Map<String, Object> options = createOptions(CLASSPATH2, "/tw5");
        this.b = super.installWar(options, "tw5.war", true);
        super.generalHeadersTest(options, "tw5.war", true, this.b);
        classpassServletTest(this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest Bundle-Classpath
     */
    public void testBundleClasspath005() throws Exception {
        final Map<String, Object> options = createOptions(CLASSPATH1, "/tw5");
        this.b = super.installWar(options, "wm2tw5.war", true);
        super.generalHeadersTest(options, "wm2tw5.war", true, this.b);
        classpassServletTest(this.b);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Bundle-Classpath
     */
    public void testBundleClasspath006() throws Exception {
        final Map<String, Object> options = createOptions(CLASSPATH1, "/tw5");
        this.b = super.installWar(options, "wm2tw5.war", true);
        super.generalHeadersTest(options, "wm2tw5.war", true, this.b);
        classpassServletTest(this.b);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Bundle-Classpath
     */
    public void testBundleClasspath007() throws Exception {
        final Map<String, Object> options = createOptions(CLASSPATH2, "/tw5");
        this.b = super.installWar(options, "wm3tw5.war", true);
        super.generalHeadersTest(options, "wm3tw5.war", true, this.b);
        classpassServletTest(this.b);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Bundle-Classpath
     */
    public void testBundleClasspath008() throws Exception {
        final Map<String, Object> options = createOptions(CLASSPATH2, "/tw5");
        this.b = super.installWar(options, "wm3tw5.war", true);
        super.generalHeadersTest(options, "wm3tw5.war", true, this.b);
        classpassServletTest(this.b);
    }

    /*
     * verify when Bundle-Classpath is not specified
     */
    public void testBundleClasspath009() throws Exception {
        final Map<String, Object> options = createOptions(null, "/tw5");
        this.b = super.installWar(options, "wm2tw5.war", true);
        super.generalHeadersTest(options, "wm2tw5.war", true, this.b);
        classpassServletTest(this.b);
    }
    
    /*
     * verify when Bundle-Classpath is not specified
     */
    public void testBundleClasspath010() throws Exception {
        final Map<String, Object> options = createOptions(null, "/tw5");
        this.b = super.installWar(options, "wm3tw5.war", true);
        super.generalHeadersTest(options, "wm3tw5.war", true, this.b);
        classpassServletTest(this.b);
    }
}

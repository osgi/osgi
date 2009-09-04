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
 *          test Web-ContextPath manifest header processed correctly with various
 *          scenarios
 */
public class BundleWebContextPathTest extends ManifestHeadersTestBundleControl {

    private Map<String, Object> createOptions(String cp) {
        final Map<String, Object> options = new HashMap<String, Object>();
        options.put(WEB_CONTEXT_PATH, cp);
        return options;
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Web-ContextPath
     */
    public void testWebContextPath001() throws Exception {
        final Map<String, Object> options = createOptions(WEBCONTEXTPATH1);
        this.b = super.installWar(options, "tw1.war", false);
        super.generalHeadersTest(options, "tw1.war", false, this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest Web-ContextPath
     */
    public void testWebContextPath002() throws Exception {
        final Map<String, Object> options = createOptions(WEBCONTEXTPATH2);
        this.b = super.installWar(options, "tw2.war", false);
        super.generalHeadersTest(options, "tw2.war", false, this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest Web-ContextPath
     */
    public void testWebContextPath003() throws Exception {
        final Map<String, Object> options = createOptions(WEBCONTEXTPATH3);
        this.b = super.installWar(options, "tw3.war", true);
        super.generalHeadersTest(options, "tw3.war", true, this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest Web-ContextPath
     */
    public void testWebContextPath004() throws Exception {
        final Map<String, Object> options = createOptions(WEBCONTEXTPATH4);
        this.b = super.installWar(options, "tw4.war", true);
        super.generalHeadersTest(options, "tw4.war", true, this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest Web-ContextPath
     */
    public void testWebContextPath005() throws Exception {
        final Map<String, Object> options = createOptions(WEBCONTEXTPATH5);
        this.b = super.installWar(options, "tw5.war", false);
        super.generalHeadersTest(options, "tw5.war", false, this.b);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Web-ContextPath
     */
    public void testWebContextPath006() throws Exception {
        final Map<String, Object> options = createOptions(WEBCONTEXTPATH1);
        this.b = super.installWar(options, "wmtw1.war", false);
        super.generalHeadersTest(options, "wmtw1.war", false, this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest Web-ContextPath
     */
    public void testWebContextPath007() throws Exception {
        final Map<String, Object> options = createOptions(WEBCONTEXTPATH2);
        this.b = super.installWar(options, "wmtw2.war", false);
        super.generalHeadersTest(options, "wmtw2.war", false, this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest Web-ContextPath
     */
    public void testWebContextPath008() throws Exception {
        final Map<String, Object> options = createOptions(WEBCONTEXTPATH3);
        this.b = super.installWar(options, "wmtw3.war", true);
        super.generalHeadersTest(options, "wmtw3.war", true, this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest Web-ContextPath
     */
    public void testWebContextPath009() throws Exception {
        final Map<String, Object> options = createOptions(WEBCONTEXTPATH4);
        this.b = super.installWar(options, "wmtw4.war", true);
        super.generalHeadersTest(options, "wmtw4.war", true, this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest Web-ContextPath
     */
    public void testWebContextPath010() throws Exception {
        final Map<String, Object> options = createOptions(WEBCONTEXTPATH5);
        this.b = super.installWar(options, "wmtw5.war", false);
        super.generalHeadersTest(options, "wmtw5.war", false, this.b);
    }

    /*
     * verify long Web-ContextPath specified in deployOptions
     */
    public void testLongWebContextPath001() throws Exception {
        final Map<String, Object> options = createOptions(LONGWEBCONTEXTPATH);
        this.b = super.installWar(options, "tw1.war", false);
        super.generalHeadersTest(options, "tw1.war", false, this.b);
    }

    /*
     * verify long Web-ContextPath specified in deployOptions
     */
    public void testLongWebContextPath002() throws Exception {
        final Map<String, Object> options = createOptions(LONGWEBCONTEXTPATH2);
        this.b = super.installWar(options, "tw2.war", false);
        super.generalHeadersTest(options, "tw2.war", false, this.b);
    }

    /*
     * verify long Web-ContextPath specified in deployOptions
     */
    public void testLongWebContextPath003() throws Exception {
        final Map<String, Object> options = createOptions(LONGWEBCONTEXTPATH3);
        this.b = super.installWar(options, "tw3.war", true);
        super.generalHeadersTest(options, "tw3.war", true, this.b);
    }

    /*
     * verify long Web-ContextPath specified in deployOptions
     */
    public void testLongWebContextPath004() throws Exception {
        final Map<String, Object> options = createOptions(LONGWEBCONTEXTPATH);
        this.b = super.installWar(options, "wmtw1.war", true);
        super.generalHeadersTest(options, "wmtw1.war", true, this.b);
    }

    /*
     * verify long Web-ContextPath specified in deployOptions
     */
    public void testLongWebContextPath005() throws Exception {
        final Map<String, Object> options = createOptions(LONGWEBCONTEXTPATH2);
        this.b = super.installWar(options, "wmtw2.war", true);
        super.generalHeadersTest(options, "wmtw2.war", true, this.b);
    }

    /*
     * verify long Web-ContextPath specified in deployOptions
     */
    public void testLongWebContextPath006() throws Exception {
        final Map<String, Object> options = createOptions(LONGWEBCONTEXTPATH2);
        this.b = super.installWar(options, "wmtw3.war", false);
        super.generalHeadersTest(options, "wmtw3.war", true, this.b);
    }

    /*
     * verify Web-ContextPath has to be unique
     */
    public void testWebContextPathError001() throws Exception {
        Map<String, Object> options = createOptions(WEBCONTEXTPATH3);
        this.b = super.installWar(options, "tw3.war", true);
        super.generalHeadersTest(options, "tw3.war", true, this.b);

        // install the war file, reuse the same options as tw1
        log("attempt to install war file: tw3.war at context path /tw3");
        Bundle b2 = null;
        options = createOptions(WEBCONTEXTPATH3);
        try {
            b2 = installBundle(super.getWarURL("tw3.war", options), false);
            fail("bundle install should fail as "
                    + WEB_CONTEXT_PATH + "is not unique: "
                    + WEB_CONTEXT_PATH + " = " + WEBCONTEXTPATH3);
        } catch (BundleException e) {
            // expected
        }
        assertNull("Bundle b2 should be null as install failed", b2);

        options = createOptions(null);
        b2 = super.installWar(options, "tw3.war", true);
        super.generalHeadersTest(options, "tw3.war", true, b2);
        uninstallBundle(b2);
    }

    /*
     * verify install 10 web applications
     */
    public void testMultipleWebContextPath001() throws Exception {
        Map<String, Object> options = createOptions(WEBCONTEXTPATH1);
        this.b = super.installWar(options, "tw1.war", true);
        super.generalHeadersTest(options, "tw1.war", true, this.b);
        
        options = createOptions(WEBCONTEXTPATH2);
        Bundle b2 = super.installWar(options, "tw2.war", false);
        super.generalHeadersTest(options, "tw2.war", false, b2);

        options = createOptions(WEBCONTEXTPATH3);
        Bundle b3 = super.installWar(options, "tw3.war", false);
        super.generalHeadersTest(options, "tw3.war", false, b3);
        
        options = createOptions(WEBCONTEXTPATH4);
        Bundle b4 = super.installWar(options, "tw4.war", true);
        super.generalHeadersTest(options, "tw4.war", true, b4);
        
        options = createOptions(WEBCONTEXTPATH5);
        Bundle b5 = super.installWar(options, "tw5.war", false);
        super.generalHeadersTest(options, "tw5.war", false, b5);
        
        options = createOptions(LONGWEBCONTEXTPATH);
        Bundle b6 = super.installWar(options, "tw1.war", false);
        super.generalHeadersTest(options, "tw1.war", false, b6);
        
        options = createOptions(LONGWEBCONTEXTPATH2);
        Bundle b7 = super.installWar(options, "tw2.war", true);
        super.generalHeadersTest(options, "tw2.war", true, b6);
        
        options = createOptions(LONGWEBCONTEXTPATH3);
        Bundle b8 = super.installWar(options, "tw3.war", true);
        super.generalHeadersTest(options, "tw3.war", true, b8);
        
        options = createOptions(null);
        Bundle b9 = super.installWar(options, "tw4.war", false);
        super.generalHeadersTest(options, "tw4.war", false, b9);
        
        Bundle b10 = super.installWar(options, "tw5.war", true);
        super.generalHeadersTest(options, "tw5.war", true, b10);
        
        uninstallBundle(b2);
        uninstallBundle(b3);
        uninstallBundle(b4);
        uninstallBundle(b5);
        uninstallBundle(b6);
        uninstallBundle(b7);
        uninstallBundle(b8);
        uninstallBundle(b9);
        uninstallBundle(b10);
    }

    /*
     * verify install 100 web applications
     */
    public void testMultipleWebContextPath002() throws Exception {
        Bundle[] bundles = new Bundle[100];
        final Map<String, Object> options = new HashMap<String, Object>();
        for (int i = 0; i < 100; i++) {
            bundles[i] = super.installWar(options, "tw1.war", true);
            super.generalHeadersTest(options, "tw1.war", true, bundles[i]);
        }
        for (int i = 0; i < 100; i++) {
            uninstallBundle(bundles[i]);
        }
    }
}

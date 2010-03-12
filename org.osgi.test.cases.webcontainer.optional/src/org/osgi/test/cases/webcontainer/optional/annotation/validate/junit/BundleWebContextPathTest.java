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
package org.osgi.test.cases.webcontainer.optional.annotation.validate.junit;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.test.cases.webcontainer.util.ManifestHeadersTestBundleControl;

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
        try {
            b2 = super.installWar(options, "tw3.war", true);
            super.generalHeadersTest(options, "tw3.war", true, b2);       
        } finally {
            if (b2 != null) {
                uninstallBundle(b2);
            }
        }
    }

    /*
     * verify install 10 web applications
     */
    public void testMultipleWebContextPath001() throws Exception {
        Map<String, Object> options = createOptions(WEBCONTEXTPATH1);
        this.b = super.installWar(options, "tw1.war", true);
        super.generalHeadersTest(options, "tw1.war", true, this.b);
        
        Bundle[] bundles = new Bundle[9];
        try {
            options = createOptions(WEBCONTEXTPATH2);
            bundles[0] = super.installWar(options, "tw2.war", false);
            super.generalHeadersTest(options, "tw2.war", false, bundles[0]);
    
            options = createOptions(WEBCONTEXTPATH3);
            bundles[1] = super.installWar(options, "tw3.war", false);
            super.generalHeadersTest(options, "tw3.war", false, bundles[1]);
            
            options = createOptions(WEBCONTEXTPATH4);
            bundles[2] = super.installWar(options, "tw4.war", true);
            super.generalHeadersTest(options, "tw4.war", true, bundles[2]);
            
            options = createOptions(WEBCONTEXTPATH5);
            bundles[3] = super.installWar(options, "tw5.war", false);
            super.generalHeadersTest(options, "tw5.war", false, bundles[3]);
            
            options = createOptions(LONGWEBCONTEXTPATH);
            bundles[4] = super.installWar(options, "tw1.war", false);
            super.generalHeadersTest(options, "tw1.war", false, bundles[4]);
            
            options = createOptions(LONGWEBCONTEXTPATH2);
            bundles[5] = super.installWar(options, "tw2.war", true);
            super.generalHeadersTest(options, "tw2.war", true, bundles[5]);
            
            options = createOptions(LONGWEBCONTEXTPATH3);
            bundles[6] = super.installWar(options, "tw3.war", true);
            super.generalHeadersTest(options, "tw3.war", true, bundles[6]);
            
            options = createOptions(null);
            bundles[7] = super.installWar(options, "tw4.war", false);
            super.generalHeadersTest(options, "tw4.war", false, bundles[7]);
            
            bundles[8] = super.installWar(options, "tw5.war", true);
            super.generalHeadersTest(options, "tw5.war", true, bundles[8]);
        } finally {
            for (int i = 0; i < bundles.length; i++) {
                if (bundles[i] != null) {
                    uninstallBundle(bundles[i]);
                }
            }
        }
    }

}

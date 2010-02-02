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
    public void testWebContextPath001() throws Exception {
        final Map<String, Object> options = createOptions(WEBCONTEXTPATH1);
        this.b = super.installWar(options, "tw1.war", false);
        super.generalHeadersTest(options, "tw1.war", false, this.b);
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
        final Map<String, Object> options = createOptions(null);
        this.b = super.installWar(options, "wmtw1.war", false);
        super.generalHeadersTest(options, "wmtw1.war", false, this.b);
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
     * verify Web-ContextPath doesn't start w/ forward slash
     */
    public void testWebContextPath011() throws Exception {
        final Map<String, Object> options = createOptions(WEBCONTEXTPATH5.substring(1));
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
    public void testLongWebContextPath004() throws Exception {
        final Map<String, Object> options = createOptions(LONGWEBCONTEXTPATH);
        this.b = super.installWar(options, "wmtw1.war", true);
        super.generalHeadersTest(options, "wmtw1.war", true, this.b);
    }

    /*
     * verify Web-ContextPath has to be unique
     */
    public void testWebContextPathError001() throws Exception {
        Map<String, Object> options = createOptions(WEBCONTEXTPATH4);
        this.b = super.installWar(options, "tw1.war", true);
        super.generalHeadersTest(options, "tw1.war", true, this.b);

        // install the war file, reuse the same options as tw1
        log("attempt to install war file: tw4.war at context path " + WEBCONTEXTPATH4);
        Bundle b2 = null;
        options = createOptions(WEBCONTEXTPATH4);

        b2 = installBundle(super.getWarURL("tw4.war", options), true);
        // should only able to access TW1 home page, as web extender should emit a FAILED event 
        // when web context path is not unique for TW4
        String response = super.getResponse(WEBCONTEXTPATH4);
        super.checkTW1HomeResponse(response);
        uninstallBundle(b2);

        b2 = null;
        // give a unique web context path
        options = createOptions(WEBCONTEXTPATH4 + "tw4.war");
        try {
            b2 = super.installWar(options, "tw4.war", true);
            super.generalHeadersTest(options, "tw4.war", true, b2);       
        } finally {
            if (b2 != null) {
                uninstallBundle(b2);
            }
        }
    }
    
    /*
     * verify Web-ContextPath is available after when the war that uses the same Web-ContextPath is uninstalled
     */
    public void testWebContextPathError002() throws Exception {
        Map<String, Object> options = createOptions(WEBCONTEXTPATH4);
        this.b = super.installWar(options, "tw1.war", true);
        super.generalHeadersTest(options, "tw1.war", true, this.b);

        // install the war file, reuse the same options as tw1
        log("attempt to install war file: tw4.war at context path " + WEBCONTEXTPATH4);
        Bundle b2 = null;
        options = createOptions(WEBCONTEXTPATH4);

        b2 = installBundle(super.getWarURL("tw4.war", options), true);
        // should only able to access TW1 home page, as web extender should emit a FAILED event 
        // when web context path is not unique for TW4
        String response = super.getResponse(WEBCONTEXTPATH4);
        super.checkTW1HomeResponse(response);
        uninstallBundle(this.b);
        this.b = null;
        
        // try install tw4.war again after WEBCONTEXTPATH4 is avail
        // and this should succeed.
        try {
            b2 = super.installWar(options, "tw4.war", true);
            super.generalHeadersTest(options, "tw4.war", true, b2);       
        } finally {
            if (b2 != null) {
                uninstallBundle(b2);
            }
        }
        
        // install bundle b back and should succeed
        this.b = super.installWar(options, "tw1.war", true);
        super.generalHeadersTest(options, "tw1.war", true, this.b);
    }

    /*
     * verify install 6 web applications
     */
    public void testMultipleWebContextPath001() throws Exception {
        Map<String, Object> options = createOptions(WEBCONTEXTPATH1);
        this.b = super.installWar(options, "tw1.war", true);
        super.generalHeadersTest(options, "tw1.war", true, this.b);
        
        Bundle[] bundles = new Bundle[5];
        try {
            options = createOptions(WEBCONTEXTPATH4);
            bundles[0] = super.installWar(options, "tw4.war", true);
            super.generalHeadersTest(options, "tw4.war", true, bundles[0]);
            
            options = createOptions(WEBCONTEXTPATH5);
            bundles[1] = super.installWar(options, "tw5.war", false);
            super.generalHeadersTest(options, "tw5.war", false, bundles[1]);
            
            options = createOptions(LONGWEBCONTEXTPATH);
            bundles[2] = super.installWar(options, "wmtw1.war", false);
            super.generalHeadersTest(options, "wmtw1.war", false, bundles[2]);
            
            options = createOptions(null);
            bundles[3] = super.installWar(options, "wmtw4.war", false);
            super.generalHeadersTest(options, "wmtw4.war", false, bundles[3]);
            
            bundles[4] = super.installWar(options, "wmtw5.war", true);
            super.generalHeadersTest(options, "wmtw5.war", true, bundles[4]);
        } finally {
            for (int i = 0; i < bundles.length; i++) {
                if (bundles[i] != null) {
                    uninstallBundle(bundles[i]);
                }
            }
        }
    }

    /*
     * verify install 100 web applications  TODO: test is incorrect
     */
    public void testMultipleWebContextPath002() throws Exception {
        Bundle[] bundles = new Bundle[100];
        try {
            for (int i = 0; i < 100; i++) {
                Map<String, Object> options = createOptions(WEBCONTEXTPATH1 + "_" + i);
                bundles[i] = super.installWar(options, "tw1.war", true);
                super.generalHeadersTest(options, "tw1.war", true, bundles[i]);
            }
        } finally {
            for (int i = 0; i < 100; i++) {
                if (bundles[i] != null) {
                    uninstallBundle(bundles[i]);
                }
            }
        }
    }
}

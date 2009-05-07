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
import org.osgi.service.webcontainer.WebContainer;

/**
 * @version $Rev$ $Date$
 * 
 *          test Web-ContextPath manifest header processed correctly with various
 *          scenarios
 */
public class BundleWebContextPathTest extends ManifestHeadersTestBundleControl {
    private static final String WEBCONTEXTPATH1 = "/ct-testwar1";
    private static final String WEBCONTEXTPATH2 = "/ct-testwar2";
    private static final String WEBCONTEXTPATH3 = "/ct-testwar3";
    private static final String WEBCONTEXTPATH4 = "/ct-testwar4";
    private static final String WEBCONTEXTPATH5 = "/ct-testwar5";

    private static final String LONGWEBCONTEXTPATH = "/abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxy";
    private static final String LONGWEBCONTEXTPATH2 = "/abcdefghijk/lmnopqrstuvwxyzabcdefgh/ijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrs";
    private static final String LONGWEBCONTEXTPATH3 = "/abcde/fghijklmnopqrstuvwxyzabcdefghijk/lmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghi/12345678901233543523423424242342342";
    private Bundle b;

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        if (this.b != null && this.b.getState() != Bundle.UNINSTALLED) {
            this.b.uninstall();
        }
        this.b = null;
    }

    /*
     * verify valid deployOptions overwrite original manifest Web-ContextPath
     */
    public void testWebContextPath001() throws Exception {
        this.b = generalWebContextPathTest(WEBCONTEXTPATH1, "tw1.war", false);
    }

    /*
     * verify valid deployOptions overwrite original manifest Web-ContextPath
     */
    public void testWebContextPath002() throws Exception {
        this.b = generalWebContextPathTest(WEBCONTEXTPATH2, "tw2.war", false);
    }

    /*
     * verify valid deployOptions overwrite original manifest Web-ContextPath
     */
    public void testWebContextPath003() throws Exception {
        this.b = generalWebContextPathTest(WEBCONTEXTPATH3, "tw3.war", true);
    }

    /*
     * verify valid deployOptions overwrite original manifest Web-ContextPath
     */
    public void testWebContextPath004() throws Exception {
        this.b = generalWebContextPathTest(WEBCONTEXTPATH4, "tw4.war", true);
    }

    /*
     * verify valid deployOptions overwrite original manifest Web-ContextPath
     */
    public void testWebContextPath005() throws Exception {
        this.b = generalWebContextPathTest(WEBCONTEXTPATH5, "tw5.war", false);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Web-ContextPath
     */
    public void testWebContextPath006() throws Exception {
        this.b = generalWebContextPathTest(WEBCONTEXTPATH1, "wmtw1.war", false);
    }

    /*
     * verify valid deployOptions overwrite original manifest Web-ContextPath
     */
    public void testWebContextPath007() throws Exception {
        this.b = generalWebContextPathTest(WEBCONTEXTPATH2, "wmtw2.war", false);
    }

    /*
     * verify valid deployOptions overwrite original manifest Web-ContextPath
     */
    public void testWebContextPath008() throws Exception {
        this.b = generalWebContextPathTest(WEBCONTEXTPATH3, "wmtw3.war", true);
    }

    /*
     * verify valid deployOptions overwrite original manifest Web-ContextPath
     */
    public void testWebContextPath009() throws Exception {
        this.b = generalWebContextPathTest(WEBCONTEXTPATH4, "wmtw4.war", true);
    }

    /*
     * verify valid deployOptions overwrite original manifest Web-ContextPath
     */
    public void testWebContextPath010() throws Exception {
        this.b = generalWebContextPathTest(WEBCONTEXTPATH5, "wmtw5.war", false);
    }

    /*
     * verify null Web-ContextPath specified in deployOptions
     */
    public void testWebContextPath011() throws Exception {
        this.b = generalWebContextPathTest(null, "tw1.war", true);
    }

    /*
     * verify null Web-ContextPath specified in deployOptions
     */
    public void testWebContextPath012() throws Exception {
        this.b = generalWebContextPathTest(null, "tw2.war", true);
    }

    /*
     * verify null Web-ContextPath specified in deployOptions
     */
    public void testWebContextPath013() throws Exception {
        this.b = generalWebContextPathTest(null, "tw3.war", false);
    }

    /*
     * verify null Web-ContextPath specified in deployOptions
     */
    public void testWebContextPath014() throws Exception {
        this.b = generalWebContextPathTest(null, "tw4.war", false);
    }

    /*
     * verify null Web-ContextPath specified in deployOptions
     */
    public void testWebContextPath015() throws Exception {
        this.b = generalWebContextPathTest(null, "tw5.war", true);
    }

    /*
     * verify null Web-ContextPath specified in deployOptions
     */
    public void testWebContextPath016() throws Exception {
        this.b = generalWebContextPathTest(null, "wmtw1.war", true);
    }

    /*
     * verify null Web-ContextPath specified in deployOptions
     */
    public void testWebContextPath017() throws Exception {
        this.b = generalWebContextPathTest(null, "wmtw2.war", true);
    }

    /*
     * verify null Web-ContextPath specified in deployOptions
     */
    public void testWebContextPath018() throws Exception {
        this.b = generalWebContextPathTest(null, "wmtw3.war", false);
    }

    /*
     * verify null Web-ContextPath specified in deployOptions
     */
    public void testWebContextPath019() throws Exception {
        this.b = generalWebContextPathTest(null, "wmtw4.war", false);
    }

    /*
     * verify null Web-ContextPath specified in deployOptions
     */
    public void testWebContextPath020() throws Exception {
        this.b = generalWebContextPathTest(null, "wmtw5.war", true);
    }
    /*
     * verify long Web-ContextPath specified in deployOptions
     */
    public void testLongWebContextPath001() throws Exception {
        this.b = generalWebContextPathTest(LONGWEBCONTEXTPATH, "tw1.war", false);
    }

    /*
     * verify long Web-ContextPath specified in deployOptions
     */
    public void testLongWebContextPath002() throws Exception {
        this.b = generalWebContextPathTest(LONGWEBCONTEXTPATH2, "tw2.war", false);
    }

    /*
     * verify long Web-ContextPath specified in deployOptions
     */
    public void testLongWebContextPath003() throws Exception {
        this.b = generalWebContextPathTest(LONGWEBCONTEXTPATH3, "tw3.war", true);
    }

    /*
     * verify long Web-ContextPath specified in deployOptions
     */
    public void testLongWebContextPath004() throws Exception {
        this.b = generalWebContextPathTest(LONGWEBCONTEXTPATH, "wmtw1.war", true);
    }

    /*
     * verify long Web-ContextPath specified in deployOptions
     */
    public void testLongWebContextPath005() throws Exception {
        this.b = generalWebContextPathTest(LONGWEBCONTEXTPATH2, "wmtw2.war", true);
    }

    /*
     * verify long Web-ContextPath specified in deployOptions
     */
    public void testLongWebContextPath006() throws Exception {
        this.b = generalWebContextPathTest(LONGWEBCONTEXTPATH2, "wmtw3.war", true);
    }

    /*
     * verify Web-ContextPath has to be unique
     */
    public void testWebContextPathError001() throws Exception {
        this.b = generalWebContextPathTest(WEBCONTEXTPATH3, "tw3.war", true);

        // install the war file, reuse the same options as tw1
        log("attempt to install war file: tw3.war at context path /tw3");
        Bundle b2 = null;
        Map options = new HashMap();
        options.put(WebContainer.WEB_CONTEXT_PATH, WEBCONTEXTPATH3);
        try {
            b2 = installBundle(super.getWarURL("tw3.war", options), true);
            fail("bundle install should fail as "
                    + WebContainer.WEB_CONTEXT_PATH + "is not unique: "
                    + WebContainer.WEB_CONTEXT_PATH + " = " + WEBCONTEXTPATH3);
        } catch (Exception e) {
            // expected
        }
        assertNull("Bundle b2 should be null as install failed", b2);

        b2 = generalWebContextPathTest(null, "tw3.war", true);
        uninstallBundle(b2);
    }

    /*
     * verify install 10 web applications
     */
    public void testMultipleWebContextPath001() throws Exception {
        this.b = generalWebContextPathTest(WEBCONTEXTPATH1, "tw1.war", true);
        Bundle b2 = generalWebContextPathTest(WEBCONTEXTPATH2, "tw2.war", false);
        Bundle b3 = generalWebContextPathTest(WEBCONTEXTPATH3, "tw3.war", false);
        Bundle b4 = generalWebContextPathTest(WEBCONTEXTPATH4, "tw4.war", true);
        Bundle b5 = generalWebContextPathTest(WEBCONTEXTPATH5, "tw5.war", false);
        Bundle b6 = generalWebContextPathTest(LONGWEBCONTEXTPATH, "tw1.war",
                false);
        Bundle b7 = generalWebContextPathTest(LONGWEBCONTEXTPATH2, "tw2.war",
                true);
        Bundle b8 = generalWebContextPathTest(LONGWEBCONTEXTPATH3, "tw3.war",
                true);
        Bundle b9 = generalWebContextPathTest(null, "tw4.war", false);
        Bundle b10 = generalWebContextPathTest(null, "tw5.war", true);
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
        for (int i = 0; i < 100; i++) {
            bundles[i] = generalWebContextPathTest(null, "tw1.war", true);
        }
        for (int i = 0; i < 100; i++) {
            uninstallBundle(bundles[i]);
        }
    }

    /*
     * generalWebContextPathTest to be used by non-error test
     */
    private Bundle generalWebContextPathTest(String contextPath,
            String warName, boolean start) throws Exception {
        // specify install options
        final Map options = new HashMap();
        String cp = contextPath;
        options.put(WebContainer.WEB_CONTEXT_PATH, cp);
        return super.generalHeadersTest(options, warName, start);
    }
}

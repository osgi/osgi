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
import org.osgi.framework.Constants;
import org.osgi.test.cases.webcontainer.ManifestHeadersTestBundleControl;

/**
 * @version $Rev$ $Date$
 * 
 *          test various manifest headers processed correctly with various
 *          scenarios
 */
public class BundleManifestHeadersTest extends ManifestHeadersTestBundleControl {
    
    /*
     * verify valid deployOptions overwrite original manifest headers
     */
    public void testManifestHeaders001() throws Exception {
        final Map<String, Object> options = createOptions(VERSION1, MANIFESTVERSION1, SYMBOLICNAME1,
                CLASSPATH3, IMPORTS1, EXPORTS1, WEBCONTEXTPATH1,
                JSPEXTRACTLOAC1);
        this.b = super.installWar(options, "tw1.war", false);
        super.generalHeadersTest(options, "tw1.war", false, this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest headers
     */
    public void testManifestHeaders004() throws Exception {
        final Map<String, Object> options = createOptions(VERSION1, MANIFESTVERSION1, SYMBOLICNAME4,
                CLASSPATH3, IMPORTS4, EXPORTS4, WEBCONTEXTPATH4, null);
        this.b = super.installWar(options, "tw4.war", false);
        super.generalHeadersTest(options, "tw4.war", false, this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest headers
     */
    public void testManifestHeaders005() throws Exception {
        final Map<String, Object> options = createOptions(VERSION2, MANIFESTVERSION1, SYMBOLICNAME5,
                CLASSPATH1, IMPORTS5, EXPORTS5, WEBCONTEXTPATH5, null);
        this.b = super.installWar(options, "tw5.war", true);
        super.generalHeadersTest(options, "tw5.war", true, this.b);
        classpassServletTest(this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest headers
     */
    public void testManifestHeaders006() throws Exception {
        final Map<String, Object> options = createOptions(VERSION1, MANIFESTVERSION1, SYMBOLICNAME1,
                CLASSPATH3, IMPORTS1, EXPORTS1, WEBCONTEXTPATH1,
                JSPEXTRACTLOAC1);
        this.b = super.installWar(options, "wmtw1.war", false);
        super.generalHeadersTest(options, "wmtw1.war", false, this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest headers
     */
    public void testManifestHeaders009() throws Exception {
        final Map<String, Object> options = createOptions(VERSION1, MANIFESTVERSION1, SYMBOLICNAME4,
                CLASSPATH3, IMPORTS4, EXPORTS4, WEBCONTEXTPATH4, null);
        this.b = super.installWar(options, "wmtw4.war", false);
        super.generalHeadersTest(options, "wmtw4.war", false, this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest headers
     */
    public void testManifestHeaders010() throws Exception {
        final Map<String, Object> options = createOptions(VERSION2, MANIFESTVERSION1, SYMBOLICNAME5,
                CLASSPATH1, IMPORTS5, EXPORTS5, WEBCONTEXTPATH5, null);
        this.b = super.installWar(options, "wmtw5.war", true);
        super.generalHeadersTest(options, "wmtw5.war", true, this.b);
        classpassServletTest(this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest headers
     */
    public void testManifestHeaders011() throws Exception {
        final Map<String, Object> options = createOptions(VERSION2, MANIFESTVERSION1, SYMBOLICNAME5,
                CLASSPATH2, IMPORTS5, EXPORTS5, WEBCONTEXTPATH5, null);
        this.b = super.installWar(options, "wm2tw5.war", true);
        super.generalHeadersTest(options, "wm2tw5.war", true, this.b);
        classpassServletTest(this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest headers
     */
    public void testManifestHeaders012() throws Exception {
        final Map<String, Object> options = createOptions(VERSION2, MANIFESTVERSION1, SYMBOLICNAME5,
                CLASSPATH3, IMPORTS5, EXPORTS5, WEBCONTEXTPATH5, null);
        this.b = super.installWar(options, "wm3tw5.war", true);
        super.generalHeadersTest(options, "wm3tw5.war", true, this.b);
        classpassServletTest(this.b);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest headers
     */
    public void testManifestHeaders013() throws Exception {
        final Map<String, Object> options = createOptions(VERSION2, MANIFESTVERSION1, null,
                null, IMPORTS5, EXPORTS5, WEBCONTEXTPATH5, null);
        this.b = super.installWar(options, "wm2tw5.war", true);
        super.generalHeadersTest(options, "wm2tw5.war", true, this.b);
        classpassServletTest(this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest headers
     */
    public void testManifestHeaders014() throws Exception {
        final Map<String, Object> options = createOptions(null, null, SYMBOLICNAME5,
                CLASSPATH3, IMPORTS5, EXPORTS5, WEBCONTEXTPATH5, null);
        this.b = super.installWar(options, "wm3tw5.war", true);
        super.generalHeadersTest(options, "wm3tw5.war", true, this.b);
        classpassServletTest(this.b);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest headers
     */
    public void testManifestHeaders015() throws Exception {
        final Map<String, Object> options = createOptions(VERSION2, MANIFESTVERSION1, SYMBOLICNAME5,
                CLASSPATH3, IMPORTS5, EXPORTS5, null, null);
        this.b = super.installWar(options, "wm3tw5.war", true);
        super.generalHeadersTest(options, "wm3tw5.war", true, this.b);
        classpassServletTest(this.b);
    }
    

    /*
     * verify null deployOptions
     */
    public void testEmptyDeployOptions001() throws Exception {
        final Map<String, Object> options = new HashMap<String, Object>();
        this.b = super.installWar(options, "tw1.war", true);
        super.generalHeadersTest(new HashMap<String, Object>(), "tw1.war", true, this.b);
    }

    /*
     * verify null deployOptions
     */
    public void testEmptyDeployOptions004() throws Exception {
        final Map<String, Object> options = new HashMap<String, Object>();
        this.b = super.installWar(options, "tw4.war", false);
        super.generalHeadersTest(new HashMap<String, Object>(), "tw4.war", false, this.b);
    }

    /*
     * verify null deployOptions
     */
    public void testEmptyDeployOptions005() throws Exception {
        final Map<String, Object> options = new HashMap<String, Object>();
        this.b = super.installWar(options, "tw5.war", true);
        super.generalHeadersTest(new HashMap<String, Object>(), "tw5.war", true, this.b);
    }

    /*
     * verify null deployOptions
     */
    public void testEmptyDeployOptions006() throws Exception {
        final Map<String, Object> options = new HashMap<String, Object>();
        this.b = super.installWar(options, "wmtw1.war", false);
        super.generalHeadersTest(new HashMap<String, Object>(), "wmtw1.war", false, this.b);
    }

    /*
     * verify null deployOptions
     */
    public void testEmptyDeployOptions009() throws Exception {
        final Map<String, Object> options = new HashMap<String, Object>();
        this.b = super.installWar(options, "wmtw4.war", false);
        super.generalHeadersTest(new HashMap<String, Object>(), "wmtw4.war", false, this.b);
    }

    /*
     * verify null deployOptions
     */
    public void testEmptyDeployOptions010() throws Exception {
        final Map<String, Object> options = new HashMap<String, Object>();
        this.b = super.installWar(options, "wmtw5.war", true);
        super.generalHeadersTest(new HashMap<String, Object>(), "wmtw5.war", true, this.b);
    }

    /*
     * verify null deployOptions
     */
    public void testEmptyDeployOptions011() throws Exception {
        final Map<String, Object> options = new HashMap<String, Object>();
        this.b = super.installWar(options, "wm2tw5.war", true);
        super.generalHeadersTest(new HashMap<String, Object>(), "wm2tw5.war", true, this.b);
    }

    /*
     * verify null deployOptions
     */
    public void testEmptyDeployOptions012() throws Exception {
        final Map<String, Object> options = new HashMap<String, Object>();
        this.b = super.installWar(options, "wm3tw5.war", false);
        super.generalHeadersTest(new HashMap<String, Object>(), "wm3tw5.war", false, this.b);
    }
    
    /*
     * verify multiple WebApps manifest headers processing
     */
    public void testMultipleManifestHeaders001() throws Exception {
        final Map<String, Object> options = createOptions(null, MANIFESTVERSION1, null,
                CLASSPATH3, IMPORTS1, EXPORTS1, null,
                JSPEXTRACTLOAC1);
        final Map<String, Object> options2 = createOptions(null, MANIFESTVERSION1, null,
                CLASSPATH3, IMPORTS1, EXPORTS1, null,
                JSPEXTRACTLOAC1);
        Bundle b2 = null;
        try {
            this.b = super.installWar(options, "wmtw1.war", true);
            super.generalHeadersTest(options, "wmtw1.war", true, this.b);
            b2 = super.installWar(options2, "wmtw1.war", true);
            super.generalHeadersTest(options2, "wmtw1.war", true, b2);
            // should these two bundles be identical since they are installed from the same location string?
            assertEquals(this.b, b2);
        } finally {
            if (b2 != null) {
                uninstallBundle(b2);
            }
        }
        
    }

    /*
     * verify multiple WebApps manifest headers processing
     */
    public void testMultipleManifestHeaders002() throws Exception {
        final Map<String, Object> options = createOptions(null, MANIFESTVERSION1, null,
                CLASSPATH3, IMPORTS1, EXPORTS1, null,
                JSPEXTRACTLOAC1);
        final Map<String, Object> options2 = createOptions(null, MANIFESTVERSION1, null,
                CLASSPATH3, IMPORTS1, EXPORTS1, null,
                null);
        Bundle b2 = null;
        
        try {
            this.b = super.installWar(options, "tw1.war", true);
            super.generalHeadersTest(options, "tw1.war", true, this.b);
            b2 = super.installWar(options2, "wmtw1.war", true);
            super.generalHeadersTest(options2, "wmtw1.war", true, this.b);
            // should these two bundles be different since they are installed from the different location string?
            assertFalse(this.b == b2);
        } finally {
            if (b2 != null) {
                uninstallBundle(b2);
            }
        }
    }
    
    /*
     * verify multiple WebApps manifest headers processing
     */
    public void testMultipleManifestHeaders003() throws Exception {
        final Map<String, Object> options = createOptions(null, MANIFESTVERSION1, null,
                CLASSPATH3, IMPORTS1, EXPORTS1, null,
                JSPEXTRACTLOAC1);
        Bundle b2 = null;
        try {
            this.b = super.installWar(options, "wmtw1.war", true);
            super.generalHeadersTest(options, "wmtw1.war", true, this.b);
            b2 = super.installWar(new HashMap<String, Object>(), "wmtw1.war", true);
            super.generalHeadersTest(new HashMap<String, Object>(), "wmtw1.war", true, b2);
            // should these two bundles be identical since they are installed from the same location string?
            assertEquals(this.b, b2);
        } finally {
            if (b2 != null) {
                uninstallBundle(b2);
            }
        }
    }

    /*
     * verify multiple WebApps manifest headers processing
     */
    public void testMultipleManifestHeaders004() throws Exception {
        final Map<String, Object> options = createOptions(null, MANIFESTVERSION1, null,
                CLASSPATH3, IMPORTS1, EXPORTS1, null,
                JSPEXTRACTLOAC1);
        Bundle b2 = null;
        
        try {
            this.b = super.installWar(options, "tw1.war", true);
            super.generalHeadersTest(options, "tw1.war", true, this.b);
            b2 = super.installWar(new HashMap<String, Object>(), "wmtw1.war", true);
            super.generalHeadersTest(new HashMap<String, Object>(), "wmtw1.war", true, b2);
            // should these two bundles be different since they are installed from the different location string?
            assertFalse(this.b == b2);
        } finally {
            if (b2 != null) {
                uninstallBundle(b2);
            }
        }
    }
    
    /*
     * verify multiple WebApps manifest headers processing
     */
    public void testMultipleManifestHeaders005() throws Exception {
        final Map<String, Object> options = createOptions(VERSION1, MANIFESTVERSION1, SYMBOLICNAME1,
                CLASSPATH3, IMPORTS1, EXPORTS1, WEBCONTEXTPATH1,
                JSPEXTRACTLOAC1);
        final Map<String, Object> options2 = createOptions(VERSION2, MANIFESTVERSION1, SYMBOLICNAME2,
                CLASSPATH3, IMPORTS2, EXPORTS2, WEBCONTEXTPATH2, null);
        final Map<String, Object> options3 = createOptions(VERSION3, MANIFESTVERSION1, SYMBOLICNAME3,
                CLASSPATH3, IMPORTS3, EXPORTS3, WEBCONTEXTPATH3, null);
        final Map<String, Object> options4 = createOptions(VERSION1, MANIFESTVERSION1, SYMBOLICNAME4,
                CLASSPATH3, IMPORTS4, EXPORTS4, WEBCONTEXTPATH4, null);
        final Map<String, Object> options5 = createOptions(VERSION2, MANIFESTVERSION1, SYMBOLICNAME5,
                CLASSPATH1, IMPORTS5, EXPORTS5, WEBCONTEXTPATH5, null);
        
        Bundle b2 = null, b3 = null, b4 = null, b5 = null;
        
        try {
            this.b = super.installWar(options, "tw1.war", true);
            super.generalHeadersTest(options, "tw1.war", true, this.b);
            b2 = super.installWar(options2, "wmtw1.war", true);
            super.generalHeadersTest(options2, "wmtw1.war", true, b2);
            b3 = super.installWar(options3, "tw4.war", true);
            super.generalHeadersTest(options3, "tw4.war", true, b3);
            b4 = super.installWar(options4, "wmtw4.war", true);
            super.generalHeadersTest(options4, "wmtw4.war", true, b4);
            b5 = super.installWar(options5, "tw5.war", true);
            super.generalHeadersTest(options5, "tw5.war", true, b5);
            classpassServletTest(b5);
        } finally {
            if (b2 != null) {
                uninstallBundle(b2);
            }
            if (b3 != null) {
                uninstallBundle(b3);
            }
            if (b4 != null) {
                uninstallBundle(b4);
            }
            if (b5 != null) {
                uninstallBundle(b5);
            }
        }
    }   
    
    /*
     * verify multiple WebApps manifest headers processing
     */
    public void testMultipleManifestHeaders007() throws Exception {
        final Map<String, Object> options = createOptions(VERSION1, MANIFESTVERSION1, SYMBOLICNAME1,
                CLASSPATH3, IMPORTS1, EXPORTS1, WEBCONTEXTPATH1,
                JSPEXTRACTLOAC1);
        final Map<String, Object> options4 = createOptions(VERSION1, MANIFESTVERSION1, SYMBOLICNAME4,
                CLASSPATH3, IMPORTS4, EXPORTS4, WEBCONTEXTPATH4, null);
        final Map<String, Object> options5 = createOptions(VERSION2, MANIFESTVERSION1, SYMBOLICNAME5,
                CLASSPATH1, IMPORTS5, EXPORTS5, WEBCONTEXTPATH5, null);
        
        final Map<String, Object> options6 = createOptions(null, MANIFESTVERSION1, null,
                CLASSPATH3, IMPORTS1, EXPORTS1, null,
                null);
        final Map<String, Object> options9 = createOptions(null, MANIFESTVERSION1, null,
                CLASSPATH3, IMPORTS4, EXPORTS4, null, null);
        final Map<String, Object> options10 = createOptions(null, MANIFESTVERSION1, null,
                CLASSPATH1, IMPORTS5, EXPORTS5, null, null);
        Bundle[] bundles = new Bundle[5];

        try {
            this.b = super.installWar(options, "tw1.war", true);
            super.generalHeadersTest(options, "tw1.war", true, this.b);
            bundles[0] = super.installWar(options, "tw4.war", true);
            super.generalHeadersTest(options4, "tw4.war", true, bundles[0]);
            bundles[1] = super.installWar(options, "tw5.war", true);
            super.generalHeadersTest(options5, "tw5.war", true, bundles[1]);
            classpassServletTest(bundles[1]);
    
            bundles[2] = super.installWar(options6, "wmtw1.war", true);
            super.generalHeadersTest(options6, "wmtw1.war", true, bundles[2]);
            bundles[3] = super.installWar(options9, "wmtw4.war", true);
            super.generalHeadersTest(options9, "wmtw4.war", true, bundles[3]);
            bundles[4] = super.installWar(options10, "wmtw5.war", true);
            super.generalHeadersTest(options10, "wmtw5.war", true, bundles[4]);
            classpassServletTest(bundles[8]);
            assertFalse(this.b == bundles[2]);
            assertFalse(bundles[0] == bundles[3]);
            assertFalse(bundles[1] == bundles[4]);
        } finally {
            for (int i = 0; i < bundles.length; i++) {
                if (bundles[i] != null) {
                    uninstallBundle(bundles[i]);
                }
            }
        }
    }
    


    private Map<String, Object> createOptions(String version, String mVersion, String sName,
            String[] classpath, String[] imports, String[] exports, String cp,
            String jspLoc) throws Exception {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put(Constants.BUNDLE_VERSION, version);
        options.put(Constants.BUNDLE_MANIFESTVERSION, mVersion);
        options.put(Constants.BUNDLE_SYMBOLICNAME, sName);
        options.put(Constants.BUNDLE_CLASSPATH, classpath);
        options.put(Constants.IMPORT_PACKAGE, imports);
        options.put(Constants.EXPORT_PACKAGE, exports);
        options.put(WEB_CONTEXT_PATH, cp);
        options.put(WEB_JSP_EXTRACT_LOCATION, jspLoc);
        return options;
    }
}

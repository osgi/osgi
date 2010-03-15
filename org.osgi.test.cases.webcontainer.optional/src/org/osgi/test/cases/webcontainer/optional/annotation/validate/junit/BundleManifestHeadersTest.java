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
import org.osgi.framework.Constants;
import org.osgi.test.cases.webcontainer.optional.WebContainerOptionalTestBundleControl;
import org.osgi.test.cases.webcontainer.util.ManifestHeadersTestBundleControl;

/**
 * @version $Rev$ $Date$
 * 
 *          test various manifest headers processed correctly with various
 *          scenarios
 */
public class BundleManifestHeadersTest extends WebContainerOptionalTestBundleControl {

    /*
     * verify valid deployOptions overwrite original manifest headers
     */
    public void testManifestHeaders002() throws Exception {
        final Map<String, Object> options = createOptions(VERSION2, MANIFESTVERSION1, SYMBOLICNAME2,
                IMPORTS_ANNOTATION, WEBCONTEXTPATH2);
        this.b = super.installWar(options, "tw2.war", true);
        super.generalHeadersTest(options, "tw2.war", true, this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest headers
     */
    public void testManifestHeaders003() throws Exception {
        final Map<String, Object> options = createOptions(VERSION3, MANIFESTVERSION1, SYMBOLICNAME3,
                IMPORTS2, WEBCONTEXTPATH3);
        this.b = super.installWar(options, "tw3.war", true);
        super.generalHeadersTest(options, "tw3.war", true, this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest headers
     */
    public void testManifestHeaders007() throws Exception {
        final Map<String, Object> options = createOptions(VERSION2, MANIFESTVERSION1, SYMBOLICNAME2,
                IMPORTS2, WEBCONTEXTPATH2);
        try {
            this.b = super.installWar(options, "wmtw2.war", true);
            fail("install bundle should fail as it contains unsupported web url params");
        } catch (BundleException be) {
            // expected
        }
    }

    /*
     * verify valid deployOptions overwrite original manifest headers
     */
    public void testManifestHeaders008() throws Exception {
        final Map<String, Object> options = createOptions(VERSION3, MANIFESTVERSION1, SYMBOLICNAME3,
                IMPORTS3, WEBCONTEXTPATH3);
        try {
            this.b = super.installWar(options, "wmtw3.war", true);
            fail("install bundle should fail as it contains unsupported web url params");
        } catch (BundleException be) {
            // expected
        }
    }
    
    /*
     * verify able to install wab without web url handler
     */
    public void testManifestHeaders009() throws Exception {
        this.b = super.installBundle("wmtw2.war", true);
        super.generalHeadersTest(new HashMap<String, Object>(), "wmtw2.war", true, this.b);
    }

    /*
     * verify able to install wab without web url handler
     */
    public void testManifestHeaders010() throws Exception {
        this.b = super.installBundle("wmtw3.war", true);
        super.generalHeadersTest(new HashMap<String, Object>(), "wmtw3.war", true, this.b);

    }
    
    /*
     * verify deployOptions web-contextpath overwrite wab manifest
     */
    public void testManifestHeaders011() throws Exception {
        final Map<String, Object> options = new HashMap<String, Object>();
        options.put(WEB_CONTEXT_PATH, WEBCONTEXTPATH4);
        this.b = super.installWar(options, "wmtw2.war", true);
        super.generalHeadersTest(options, "wmtw2.war", true, this.b);
    }

    /*
     * verify deployOptions web-contextpath overwrite wab manifest
     */
    public void testManifestHeaders012() throws Exception {
        final Map<String, Object> options = new HashMap<String, Object>();
        options.put(WEB_CONTEXT_PATH, WEBCONTEXTPATH5);
        this.b = super.installWar(options, "wmtw3.war", true);
        super.generalHeadersTest(options, "wmtw3.war", true, this.b);
    }



    private Map<String, Object> createOptions(String version, String mVersion, String sName,
            String[] imports, String cp) throws Exception {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put(Constants.BUNDLE_VERSION, version);
        options.put(Constants.BUNDLE_MANIFESTVERSION, mVersion);
        options.put(Constants.BUNDLE_SYMBOLICNAME, sName);
        options.put(Constants.IMPORT_PACKAGE, imports);
        options.put(WEB_CONTEXT_PATH, cp);
        return options;
    }
}

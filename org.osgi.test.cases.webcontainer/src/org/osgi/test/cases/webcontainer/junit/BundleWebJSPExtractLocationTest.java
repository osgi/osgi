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
import org.osgi.service.webcontainer.WebContainer;
import org.osgi.test.cases.webcontainer.ManifestHeadersTestBundleControl;
import org.osgi.test.cases.webcontainer.util.ConstantsUtil;

/**
 * @version $Rev$ $Date$
 * 
 *          test Web-JSPExtractLocation manifest header processed correctly with various
 *          scenarios
 *          // TODO JSP extract location support is being discussed in rfc 66.
 *          not sure if this will be supported or if the directory must exist for the install to
 *          be successful - testWebContextPath003
 */
public class BundleWebJSPExtractLocationTest extends ManifestHeadersTestBundleControl {

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
     * verify valid deployOptions overwrite original manifest Web-JSPExtractLocation
     */
    public void testWebContextPath001() throws Exception {
        this.b = generalWebJSPExtractLocationTest(JSPEXTRACTLOAC1, "/tw1", "tw1.war", false);

    }

    /*
     * verify valid deployOptions overwrite original manifest Web-JSPExtractLocation
     */
    public void testWebContextPath002() throws Exception {
        this.b = generalWebJSPExtractLocationTest(JSPEXTRACTLOAC1, "/tw1", "tw1.war", true);

    }
    
    /*
     * verify valid deployOptions overwrite original manifest Web-JSPExtractLocation
     * use a directory that doesn't exist (need to be created)
     */
    public void testWebContextPath003() throws Exception {
        this.b = generalWebJSPExtractLocationTest(JSPEXTRACTLOAC2, "/tw1", "tw1.war", true);

    }
    
    /*
     * verify valid deployOptions overwrite original manifest Web-JSPExtractLocation
     * use user's current directory
     */
    public void testWebContextPath004() throws Exception {
        this.b = generalWebJSPExtractLocationTest(ConstantsUtil.getBaseDir(), "/tw1", "tw1.war", true);

    }

    /*
     * generalWebJSPExtractLocationTest to be used by non-error test
     */
    private Bundle generalWebJSPExtractLocationTest(String jspExtractLoc, String cp, 
            String warName, boolean start) throws Exception {
        // specify install options
        final Map options = new HashMap();
        options.put(WebContainer.WEB_JSP_EXTRACT_LOCATION, jspExtractLoc);
        options.put(WebContainer.WEB_CONTEXT_PATH, cp);
        return super.generalHeadersTest(options, warName, start);
    }

    // TODO create war manifest that contains the Web-JSPExtractLocation header and
    // more
    // tests
}

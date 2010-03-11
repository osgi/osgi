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

package org.osgi.test.cases.webcontainer.optional.annotation.junit;

import org.osgi.framework.Constants;
import org.osgi.test.cases.webcontainer.optional.WebContainerOptionalTestBundleControl;
import org.osgi.test.cases.webcontainer.optional.util.ConstantsUtil;

/**
 * @version $Rev$ $Date$
 */
public class MCResourceAnnotationTest extends WebContainerOptionalTestBundleControl {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        super.prepare("/tw3");
        this.options.put(Constants.IMPORT_PACKAGE, IMPORTS_ANNOTATION);

        // install + start the war file
        log("install war file: tw3.war at context path " + this.warContextPath);
        this.b = installBundle(super.getWarURL("tw3.war", this.options), true);
        assertTrue("should be able to see the servlet context associated with /tw3 web contextpath",
                super.checkServiceRegistered("/tw3"));
    }

    /*
     * Test @Resource field based annotation/injection with data type Integer,
     * String, Boolean, when the metadata-complete attribute is set to true.
     */
    public void testResource002() throws Exception {
        final String request = this.warContextPath + "/ResourceServlet2";
        String response = super.getResponse(request);

        // check if content of response is correct
        checkTW3ResourceServlet2Response(response);
    }

    /*
     * Test @Resource setter based injection with data type Integer, String,
     * Boolean, when the metadata-complete attribute is set to true.
     */
    public void testResource004() throws Exception {
        final String request = this.warContextPath + "/ResourceServlet4";
        String response = super.getResponse(request);

        // check if content of response is correct
        log("verify content of response is correct");
        if (debug) {
            log("response is " + response);
        }
        assertTrue(response.indexOf("ResourceServlet4") > 0);
        assertTrue(response.indexOf(ConstantsUtil.NULL + " "
                + ConstantsUtil.NULL) > 0);
        assertTrue(response.indexOf(ConstantsUtil.NULL + " + "
                + ConstantsUtil.NULL + " = " + ConstantsUtil.NULL + " that is "
                + ConstantsUtil.NULL) > 0);
    }

}

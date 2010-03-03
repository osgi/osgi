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
public class ResourceAnnotationTest extends WebContainerOptionalTestBundleControl {
  
    @Override
    public void setUp() throws Exception {
        super.setUp();
        super.prepare("/tw2");
        this.options.put(Constants.IMPORT_PACKAGE, IMPORTS_ANNOTATION);

        // install + start the war file
        log("install war file: tw2.war at context path " + this.warContextPath);
        this.b = installBundle(super.getWarURL("tw2.war", this.options), true);
        assertTrue("should be able to see the servlet context associated with /tw2 web contextpath",
                super.checkServiceRegistered("/tw2"));
    }

    /*
     * Test single @Resource class-level and field based annotation/injection
     * with data type javax.sql.DataSource
     */
    public void testResource001() throws Exception {
        final String request = this.warContextPath + "/ResourceServlet1";
        String response = super.getResponse(request);
        // check if content of response is correct
        log("verify content of response is correct");
        assertTrue(response.indexOf("ResourceServlet1") > 0);
        assertTrue(response
                .indexOf("Printing the injections in this ResourceServlet1 ...") > 0);
        assertTrue(response.indexOf("Done!") > 0);
        assertEquals(response.indexOf("null"), -1);
        assertEquals(response
                .indexOf("Error - unable to find name via @Resource"), -1);
    }

    /*
     * Test @Resource field based annotation/injection with data type Integer,
     * String, Boolean
     */
    public void testResource002() throws Exception {
        final String request = this.warContextPath + "/ResourceServlet2";
        String response = super.getResponse(request);
        // check if content of response is correct
        checkTW2ResourceServlet2Response(response);
    }

    /*
     * Test @Resources class-level annotation/injection
     * 
     * @Resource field based annotation/injection with data type
     * javax.sql.DataSource
     */
    public void testResource003() throws Exception {
        final String request = this.warContextPath + "/ResourceServlet3";
        String response = super.getResponse(request);
        // check if content of response is correct
        log("verify content of response is correct");
        assertTrue(response.indexOf("ResourceServlet3") > 0);
        assertTrue(response
                .indexOf("Printing the injections in this ResourceServlet3 ...") > 0);
        assertTrue(response.indexOf("Done!") > 0);
        assertEquals(response.indexOf("null"), -1);
        assertEquals(response
                .indexOf("Error - unable to find name via @Resource"), -1);
    }

    /*
     * Test @Resource setter based injection with data type Integer, String,
     * Boolean
     */
    public void testResource004() throws Exception {
        final String request = this.warContextPath + "/ResourceServlet4";
        String response = super.getResponse(request);
        // check if content of response is correct
        log("verify content of response is correct");
        assertTrue(response.indexOf("ResourceServlet4") > 0);
        assertTrue(response.indexOf(ConstantsUtil.WELCOMESTRINGVALUE) > 0);
        assertTrue(response.indexOf(ConstantsUtil.WELCOMESTATEMENTVALUE) > 0);
        assertEquals(response.indexOf("null"), -1);
    }

}

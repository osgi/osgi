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

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.test.cases.webcontainer.optional.WebContainerOptionalTestBundleControl;
import org.osgi.test.cases.webcontainer.optional.util.ConstantsUtil;

/**
 * @version $Rev$ $Date$
 */
public class PostConstructAnnotationTest extends WebContainerOptionalTestBundleControl {
    Bundle b;

    public void setUp() throws Exception {
        super.setUp();
        super.prepare("/tw2");
        this.options.put(Constants.IMPORT_PACKAGE, IMPORTS_ANNOTATION);

        super.cleanupPropertyFile();

        // install + start the war file
        log("install war file: tw2.war at context path " + this.warContextPath);
        this.b = installBundle(super.getWarURL("tw2.war", this.options), true);
        assertTrue("should be able to see the servlet context associated with /tw2 web contextpath",
                super.checkServiceRegistered("/tw2"));
    }

    private void uninstallWar() throws Exception {
        // uninstall the war file
        log("uninstall war file: tw2.war at context path "
                + this.warContextPath);
        uninstallBundle(this.b);
    }

    public void tearDown() throws Exception {
        uninstallWar();
    }

    /*
     * test @postConstruct annotated public method is called
     */
    public void testPostConstruct001() throws Exception {
        final String request = this.warContextPath
                + "/PostConstructPreDestroyServlet1";
        String response = super.getResponse(request);
        if (this.debug) {
            log(response);
        }
        // check if content of response is correct
        log("verify content of response is correct");
        assertTrue(response.indexOf("PostConstructPreDestroyServlet1") > 0);
        assertTrue(response
                .indexOf("PostConstructPreDestroyServlet1.printContext "
                        + ConstantsUtil.PRINTCONTEXT) > 0);
        assertEquals(response.indexOf("null"), -1);
        // check if the time stamp in response is after the beforeStart
        // time.
        assertTrue(this.timeUtil.getTimeFromResponse(response) > beforeInstall);
        log("verify annotated methods are invoked");
        assertTrue(this.timeUtil.getTimeFromLog(
                "PostConstructPreDestroyServlet1", ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
    }

    /*
     * test @postConstruct annotated private method is called
     */
    public void testPostConstruct002() throws Exception {
        final String request = this.warContextPath
                + "/PostConstructPreDestroyServlet2";
        String response = super.getResponse(request);
        // check if content of response is correct
        log("verify content of response is correct");
        if (debug) {
            log("response is " + response);
        }
        assertTrue(response.indexOf("PostConstructPreDestroyServlet2") > 0);
        assertTrue(response
                .indexOf("PostConstructPreDestroyServlet2.printContext "
                        + ConstantsUtil.PRINTCONTEXT) > 0);
        assertEquals(response.indexOf("null"), -1);
        // check if the time stamp in response is after the beforeStart
        // time.
        assertTrue(this.timeUtil.getTimeFromResponse(response) > beforeInstall);
        log("verify annotated methods are invoked");
        assertTrue(this.timeUtil.getTimeFromLog(
                "PostConstructPreDestroyServlet2", ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
    }

    /*
     * test @postConstruct annotated protected method is called
     */
    public void testPostConstruct003() throws Exception {
        final String request = this.warContextPath
                + "/PostConstructPreDestroyServlet3";
        String response = super.getResponse(request);
        // check if content of response is correct
        log("verify content of response is correct");
        if (debug) {
            log("response is " + response);
        }
        assertTrue(response.indexOf("PostConstructPreDestroyServlet3") > 0);
        assertTrue(response
                .indexOf("PostConstructPreDestroyServlet3.printContext "
                        + ConstantsUtil.PRINTCONTEXT) > 0);
        assertEquals(response.indexOf("null"), -1);
        // check if the time stamp in response is after the beforeStart
        // time.
        assertTrue(this.timeUtil.getTimeFromResponse(response) > beforeInstall);
        log("verify annotated methods are invoked");
        assertTrue(this.timeUtil.getTimeFromLog(
                "PostConstructPreDestroyServlet3", ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
    }
}

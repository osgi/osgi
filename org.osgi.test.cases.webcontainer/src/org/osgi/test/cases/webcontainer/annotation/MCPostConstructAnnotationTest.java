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

package org.osgi.test.cases.webcontainer.annotation;

import java.net.HttpURLConnection;
import java.net.URL;

import org.osgi.framework.Bundle;
import org.osgi.test.cases.webcontainer.WebContainerTestBundleControl;
import org.osgi.test.cases.webcontainer.util.ConstantsUtil;
import org.osgi.test.cases.webcontainer.util.Dispatcher;
import org.osgi.test.cases.webcontainer.util.TimeUtil;

/**
 * @version $Rev$ $Date$
 */
public class MCPostConstructAnnotationTest extends WebContainerTestBundleControl {
    String warContextPath;
    TimeUtil timeUtil;
    Bundle b;

    public void setUp() throws Exception {
        super.setUp();
        this.warContextPath = "/tw3";
        this.timeUtil = new TimeUtil(this.warContextPath);

        super.cleanupPropertyFile();

        // install + start the war file
        log("install war file: tw3.war at context path " + this.warContextPath);
        this.b = installBundle(getWebServer()
                + "tw3.war", true);
    }

    private void uninstallWar() throws Exception {
        // uninstall the war file
        log("uninstall war file: tw3.war at context path " + this.warContextPath);
        uninstallBundle(this.b);
    }

    public void tearDown() throws Exception {
        uninstallWar();
    }

    /*
     * test @postConstruct annotated public method is not called
     * when the metadata-complete attribute is set to true
     */
    public void testPostConstruct001() throws Exception {
        final String request = this.warContextPath
                + "/PostConstructPreDestroyServlet1";
        final URL url = Dispatcher.createURL(request, this.server);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            assertEquals(conn.getResponseCode(), 200);
            assertEquals(conn.getContentType(), "text/html");
            String response = Dispatcher.dispatch(conn);
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
            log("verify annotated methods are not invoked");
            assertEquals(this.timeUtil.getTimeFromLog(
                    "PostConstructPreDestroyServlet1", ConstantsUtil.POSTCONSTRUCT), 0);
        } finally {
            conn.disconnect();
        }
    }

    /*
     * test @postConstruct annotated private method is not called
     * when the metadata-complete attribute is set to true
     */
    public void testPostConstruct002() throws Exception {
        final String request = this.warContextPath
                + "/PostConstructPreDestroyServlet2";
        final URL url = Dispatcher.createURL(request, this.server);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            assertEquals(conn.getResponseCode(), 200);
            assertEquals(conn.getContentType(), "text/html");
            String response = Dispatcher.dispatch(conn);
            if (this.debug) {
                log(response);
            }
            // check if content of response is correct
            log("verify content of response is correct");
            assertTrue(response.indexOf("PostConstructPreDestroyServlet2") > 0);
            assertTrue(response
                    .indexOf("PostConstructPreDestroyServlet2.printContext "
                            + ConstantsUtil.PRINTCONTEXT) > 0);
            assertEquals(response.indexOf("null"), -1);
            // check if the time stamp in response is after the beforeStart
            // time.
            assertTrue(this.timeUtil.getTimeFromResponse(response) > beforeInstall);
            log("verify annotated methods are not invoked");
            assertEquals(this.timeUtil.getTimeFromLog(
                    "PostConstructPreDestroyServlet2", ConstantsUtil.POSTCONSTRUCT), 0);
        } finally {
            conn.disconnect();
        }
    }

    /*
     * test @postConstruct annotated protected method is not called
     * when the metadata-complete attribute is set to true
     */
    public void testPostConstruct003() throws Exception {
        final String request = this.warContextPath
                + "/PostConstructPreDestroyServlet3";
        final URL url = Dispatcher.createURL(request, this.server);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            assertEquals(conn.getResponseCode(), 200);
            assertEquals(conn.getContentType(), "text/html");
            String response = Dispatcher.dispatch(conn);
            if (this.debug) {
                log(response);
            }
            // check if content of response is correct
            log("verify content of response is correct");
            assertTrue(response.indexOf("PostConstructPreDestroyServlet3") > 0);
            assertTrue(response
                    .indexOf("PostConstructPreDestroyServlet3.printContext "
                            + ConstantsUtil.PRINTCONTEXT) > 0);
            assertEquals(response.indexOf("null"), -1);
            // check if the time stamp in response is after the beforeStart
            // time.
            assertTrue(this.timeUtil.getTimeFromResponse(response) > beforeInstall);
            log("verify annotated methods are not invoked");
            assertEquals(this.timeUtil.getTimeFromLog(
                    "PostConstructPreDestroyServlet3", ConstantsUtil.POSTCONSTRUCT), 0);
        } finally {
            conn.disconnect();
        }
    }
}

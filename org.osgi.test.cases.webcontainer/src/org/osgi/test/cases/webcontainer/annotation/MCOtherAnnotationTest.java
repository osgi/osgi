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

import org.osgi.test.cases.webcontainer.util.Constants;
import org.osgi.test.cases.webcontainer.util.Dispatcher;
import org.osgi.test.cases.webcontainer.util.Server;
import org.osgi.test.cases.webcontainer.util.TimeUtil;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * @version $Rev$ $Date$
 * 
 *          this test class is intended to test that annotations should not work
 *          in container managed classes that implement the following
 *          interfaces, in addition to the javax.servlet.Servlet interface:
 *          javax.servlet.Filter javax.servlet.ServletContextListener
 *          javax.servlet.ServletContextAttributeListener
 *          javax.servlet.ServletRequestListener
 *          javax.servlet.ServletRequestAttributeListener
 *          javax.servlet.http.HttpSessionListener
 *          javax.servlet.http.HttpSessionAttributeListener
 * 
 *          when the metadata-complete attribute is set to true.
 */
public class MCOtherAnnotationTest extends DefaultTestBundleControl {
    // this test case assume war files are already installed for now
    Server server;
    boolean debug;
    String warContextPath;
    long beforeInstall;
    TimeUtil timeUtil;

    public void setUp() throws Exception {
        // TODO if war file already exists, let's remove it first.

        this.server = new Server();
        this.debug = true;
        this.warContextPath = "/tw3";
        this.timeUtil = new TimeUtil(this.warContextPath);

        // capture a time before install
        // beforeInstall = System.currentTimeMillis();
        beforeInstall = 1;

        // clean up the property file.
        /*
         boolean success = ConstantsUtil.removeLogFile(); 
         if (!success) {
             log("Deleting File: " + ConstantsUtil.getLogFile() + " failed."); 
         }
         else { 
              log (ConstantsUtil.getLogFile() + " file is deleted."); 
         }*/

        // TODO install the war file
    }

    private void uninstallWar() throws Exception {
        // TODO uninstall the war file?

    }

    public void tearDown() throws Exception {
        uninstallWar();
    }

    /*
     * test annotated methods/fields in container manager class that implement
     * javax.servlet.Filter are not called when the metadata-complete attribute
     * is set to true.
     */
    public void testFilter() throws Exception {
        final String request = this.warContextPath
                + "/PostConstructPreDestroyServlet1";
        final URL url = Dispatcher.createURL(request, this.server);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            assertEquals(conn.getResponseCode(), 200);
            assertEquals(conn.getContentType(), "text/html");
            String response = Dispatcher.dispatch(conn);
            if (debug) {
                log(response);
            }
            // check if content of response is correct
            log("verify content of response is correct");
            assertTrue(response.indexOf("PostConstructPreDestroyServlet1") > 0);
            assertTrue(response
                    .indexOf("PostConstructPreDestroyServlet1.printContext "
                            + Constants.PRINTCONTEXT) > 0);
            assertEquals(response.indexOf("null"), -1);
            // check if the time stamp in response is after the beforeStart
            // time.
            log("check if the time stamp in response is after the beforeStart time.");
            assertTrue(this.timeUtil.getTimeFromResponse(response) > beforeInstall);

            log("verify annotated methods are not invoked");
            assertEquals(
                    this.timeUtil.getTimeFromLog(
                            "PostConstructPreDestroyServlet1",
                            Constants.POSTCONSTRUCT), 0);
            assertEquals(this.timeUtil.getTimeFromLog("TestFilter",
                    Constants.POSTCONSTRUCT), 0);

            log("verify non-annotated methods are still called");
            assertTrue(this.timeUtil.getTimeFromLog("TestFilter", "init") > beforeInstall);
            assertTrue(this.timeUtil.getTimeFromLog("TestFilter", "doFilter") > this.timeUtil
                    .getTimeFromLog("TestFilter", "init"));
            assertEquals(
                    this.timeUtil.getDespFromLog("TestFilter", "doFilter"),
                    Constants.NULL);
            assertEquals(this.timeUtil.getDespFromLog("TestFilter", "init"),
                    Constants.NULL);
            assertEquals(this.timeUtil.getDespFromLog(
                    "TestServletRequestListener", "requestInitialized"),
                    Constants.WELCOMESTRING + "-" + Constants.NULL);
        } finally {
            conn.disconnect();
        }
        assertEquals(this.timeUtil.getDespFromLog("TestServletRequestListener",
                "requestDestroyed"), "");
    }

    /*
     * test annotated methods/fields in container manager class that implement
     * javax.servlet.ServletContextListener or
     * javax.servlet.ServletContextAttributeListener are not called when the
     * metadata-complete attribute is set to true.
     */
    public void testServletContext() throws Exception {
        String request = this.warContextPath + "/ServletContextListenerServlet";
        URL url = Dispatcher.createURL(request, this.server);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // add attributes
        try {
            assertEquals(conn.getResponseCode(), 200);
            assertEquals(conn.getContentType(), "text/html");
            String response = Dispatcher.dispatch(conn);
            if (debug) {
                log(response);
            }
            // check if content of response is correct
            log("verify content of response is correct");
            assertTrue(response.indexOf("ServletContextListenerServlet") > 0);
            assertTrue(response.indexOf(Constants.EMAIL + "-"
                    + Constants.EMAILVALUE) > 0);
            assertTrue(response.indexOf(Constants.WELCOMESTRING + "-"
                    + Constants.NULL) > 0);
            assertTrue(response.indexOf(Constants.WELCOMESTATEMENT + "-"
                    + Constants.NULL) > 0);

            // check if annotated methods are not invoked
            log("verify annotated methods are not invoked");
            assertEquals(this.timeUtil.getTimeFromLog(
                    "ServletContextListenerServlet", Constants.POSTCONSTRUCT),
                    0);
            assertEquals(this.timeUtil.getTimeFromLog(
                    "TestServletContextListener", Constants.POSTCONSTRUCT), 0);
            assertEquals(this.timeUtil.getTimeFromLog(
                    "TestServletContextAttributeListener",
                    Constants.POSTCONSTRUCT), 0);
            log("verify non-annotated methods are still called");
            assertEquals(this.timeUtil.getDespFromLog(
                    "TestServletContextListener", "contextInitialized"),
                    Constants.EMAIL + "-" + Constants.EMAILVALUE);
            assertEquals(this.timeUtil.getDespFromLog(
                    "TestServletContextAttributeListener", "attributeAdded"),
                    Constants.EMAIL + "-" + Constants.EMAILVALUE);
        } finally {
            conn.disconnect();
        }
    }

    /*
     * test annotated methods/fields in container manager class that implement
     * javax.servlet.ServletRequestListener or
     * javax.servlet.ServletRequestAttributeListener are not called when the
     * metadata-complete attribute is set to true.
     */
    public void testServletRequest() throws Exception {
        String request = this.warContextPath + "/RequestListenerServlet";
        URL url = Dispatcher.createURL(request, this.server);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // add attributes
        try {
            assertEquals(conn.getResponseCode(), 200);
            assertEquals(conn.getContentType(), "text/html");
            String response = Dispatcher.dispatch(conn);
            if (debug) {
                log(response);
            }
            // check if content of response is correct
            log("verify content of response is correct");
            assertTrue(response.indexOf("RequestListenerServlet") > 0);
            assertTrue(response.indexOf(Constants.WELCOMESTRING + "-"
                    + Constants.NULL) > 0);
            assertTrue(response.indexOf(Constants.WELCOMESTATEMENT + "-"
                    + Constants.NULL) > 0);

            // check if annotated methods are not invoked
            log("verify annotated methods are not invoked");
            assertEquals(this.timeUtil.getTimeFromLog("RequestListenerServlet",
                    Constants.POSTCONSTRUCT), 0);
            assertEquals(this.timeUtil.getTimeFromLog(
                    "TestServletRequestListener", Constants.POSTCONSTRUCT), 0);
            assertEquals(this.timeUtil.getTimeFromLog(
                    "TestServletRequestAttributeListener",
                    Constants.POSTCONSTRUCT), 0);
            assertEquals(this.timeUtil.getDespFromLog(
                    "TestServletRequestListener", "requestInitialized"),
                    Constants.WELCOMESTRING + "-" + Constants.NULL);
        } finally {
            conn.disconnect();
        }
    }

    /*
     * test annotated methods/fields in container manager class that implement
     * javax.servlet.ServletRequestListener or
     * javax.servlet.ServletRequestAttributeListener are not called when the
     * metadata-complete attribute is set to true.
     */
    public void testHTTPSession() throws Exception {
        String request = this.warContextPath + "/HTTPSessionListenerServlet";
        URL url = Dispatcher.createURL(request, this.server);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // add attributes
        try {
            assertEquals(conn.getResponseCode(), 200);
            assertEquals(conn.getContentType(), "text/html");
            String response = Dispatcher.dispatch(conn);
            if (debug) {
                log(response);
            }
            // check if content of response is correct
            log("verify content of response is correct");
            assertTrue(response.indexOf("HTTPSessionListenerServlet") > 0);
            assertTrue(response.indexOf(Constants.WELCOMESTRING + "-"
                    + Constants.NULL) > 0);
            assertTrue(response.indexOf(Constants.WELCOMESTATEMENT + "-"
                    + Constants.NULL) > 0);

            // check if annotated methods are not invoked
            log("verify annotated methods are not invoked");
            assertEquals(this.timeUtil.getTimeFromLog(
                    "HTTPSessionListenerServlet", Constants.POSTCONSTRUCT), 0);
            assertEquals(this.timeUtil.getTimeFromLog("TestHttpSessionListener",
                    Constants.POSTCONSTRUCT), 0);
            assertEquals(this.timeUtil
                    .getTimeFromLog("TestHttpSessionAttributeListener",
                            Constants.POSTCONSTRUCT), 0);
            assertEquals(this.timeUtil.getDespFromLog(
                    "TestHttpSessionListener", "sessionCreated"),
                    Constants.WELCOMESTRING + "-"
                            + Constants.NULL);
        } finally {
            conn.disconnect();
        }
    }

    /*
     * test @PreDestroy annotated classes are not called in container manager
     * classes that implement the following interfaces: javax.servlet.Filter
     * javax.servlet.ServletContextListener
     * javax.servlet.ServletContextAttributeListener
     * javax.servlet.ServletRequestListener
     * javax.servlet.ServletRequestAttributeListener
     * javax.servlet.http.HttpSessionListener
     * javax.servlet.http.HttpSessionAttributeListener
     */
    public void testPreDestroyOther() throws Exception {
        uninstallWar();

        // check if annotated methods are not invoked
        log("verify annotated methods are not invoked");
        assertEquals(this.timeUtil.getTimeFromLog("TestFilter",
                Constants.PREDESTROY), 0);
        assertEquals(this.timeUtil.getTimeFromLog(
                "ServletContextListenerServlet", Constants.PREDESTROY), 0);
        assertEquals(this.timeUtil.getTimeFromLog("TestServletContextListener",
                Constants.PREDESTROY), 0);
        assertEquals(this.timeUtil.getTimeFromLog(
                "TestServletContextAttributeListener", Constants.PREDESTROY), 0);
        assertEquals(this.timeUtil.getTimeFromLog("RequestListenerServlet",
                Constants.PREDESTROY), 0);
        assertEquals(this.timeUtil.getTimeFromLog("TestServletRequestListener",
                Constants.PREDESTROY), 0);
        assertEquals(this.timeUtil.getTimeFromLog(
                "TestServletRequestAttributeListener", Constants.PREDESTROY), 0);
        assertEquals(this.timeUtil.getTimeFromLog("HTTPSessionListenerServlet",
                Constants.PREDESTROY), 0);
        assertEquals(this.timeUtil.getTimeFromLog("TestHttpSessionListener",
                Constants.PREDESTROY), 0);
        assertEquals(this.timeUtil.getTimeFromLog(
                "TestHttpSessionAttributeListener", Constants.PREDESTROY), 0);
    }
}

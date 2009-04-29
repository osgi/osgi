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
import java.util.jar.Manifest;

import org.osgi.framework.Bundle;
import org.osgi.test.cases.webcontainer.WebContainerTestBundleControl;
import org.osgi.test.cases.webcontainer.util.ConstantsUtil;
import org.osgi.test.cases.webcontainer.util.Dispatcher;
import org.osgi.test.cases.webcontainer.util.TimeUtil;
import org.osgi.test.cases.webcontainer.validate.BundleManifestValidator;

/**
 * @version $Rev$ $Date$
 * 
 *          this test class is intended to test that annotations should work in
 *          container managed classes that implement the following interfaces,
 *          in addition to the javax.servlet.Servlet interface:
 *          javax.servlet.Filter javax.servlet.ServletContextListener
 *          javax.servlet.ServletContextAttributeListener
 *          javax.servlet.ServletRequestListener
 *          javax.servlet.ServletRequestAttributeListener
 *          javax.servlet.http.HttpSessionListener
 *          javax.servlet.http.HttpSessionAttributeListener
 */
public class OtherAnnotationTest extends WebContainerTestBundleControl {
    String warContextPath;
    TimeUtil timeUtil;
    Bundle b;

    public void setUp() throws Exception {
        super.setUp();
        this.warContextPath = "/tw2";
        this.timeUtil = new TimeUtil(this.warContextPath);

        super.cleanupPropertyFile();
        // install + start the war file
        log("install war file: tw2.war at context path " + this.warContextPath);
        this.b = installBundle(getWebServer()
                + "tw2.war", true);
    }

    private void uninstallWar() throws Exception {
        // uninstall the war file
        log("uninstall war file: tw2.war at context path " + this.warContextPath);
        uninstallBundle(this.b);
    }

    public void tearDown() throws Exception {
        uninstallWar();
    }

    /*
     * set deployOptions to null to rely on the web container service to generate the manifest
     */
    public void testBundleManifest() throws Exception {
        Manifest originalManifest = super.getManifest("/resources/tw2/tw2.war");
        BundleManifestValidator validator = new BundleManifestValidator(this.b, originalManifest, null, this.debug);
        validator.validate();
    }
    
    /*
     * test annotated methods/fields in container manager class that implement
     * javax.servlet.Filter are called
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
            log("check if the time stamp in response is after the beforeStart time.");
            assertTrue(this.timeUtil.getTimeFromResponse(response) > beforeInstall);
            
            log("verify annotated methods are invoked");
            assertTrue(this.timeUtil.getTimeFromLog(
                    "PostConstructPreDestroyServlet1", ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
            assertTrue(this.timeUtil.getTimeFromLog("TestFilter",
                    ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
            
            log("verify other methods are invoked");
            assertTrue(this.timeUtil.getTimeFromLog("TestFilter", "init") > beforeInstall);
            assertTrue(this.timeUtil.getTimeFromLog("TestFilter", "doFilter") > this.timeUtil
                    .getTimeFromLog("TestFilter", "init"));
            assertEquals(
                    this.timeUtil.getDespFromLog("TestFilter", "doFilter"),
                    ConstantsUtil.WELCOMESTRINGVALUE);
            assertEquals(this.timeUtil.getDespFromLog("TestFilter", "init"),
                    ConstantsUtil.WELCOMESTATEMENTVALUE);
        } finally {
            conn.disconnect();
        }
    }

    /*
     * test annotated methods/fields in container manager class that implement
     * javax.servlet.ServletContextListener or
     * javax.servlet.ServletContextAttributeListener are called
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
            if (this.debug) {
                log(response);
            }
            // check if content of response is correct
            log("verify content of response is correct");
            assertTrue(response.indexOf("ServletContextListenerServlet") > 0);
            assertTrue(response.indexOf(ConstantsUtil.EMAIL + "-"
                    + ConstantsUtil.EMAILVALUE) > 0);
            assertTrue(response.indexOf(ConstantsUtil.WELCOMESTRING + "-"
                    + ConstantsUtil.WELCOMESTRINGVALUE) > 0);
            assertTrue(response.indexOf(ConstantsUtil.WELCOMESTATEMENT + "-"
                    + ConstantsUtil.WELCOMESTATEMENTVALUE) > 0);
            assertEquals(response.indexOf("null"), -1);

            // check if annotated methods are invoked
            log("verify annotated methods are invoked");
            assertTrue(this.timeUtil.getTimeFromLog(
                    "ServletContextListenerServlet", ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
            assertTrue(this.timeUtil.getTimeFromLog(
                    "TestServletContextListener", ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
            assertTrue(this.timeUtil.getTimeFromLog(
                    "TestServletContextAttributeListener",
                    ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
            log("verify other methods are invoked");
            assertEquals(this.timeUtil.getDespFromLog(
                    "TestServletContextListener", "contextInitialized"),
                    ConstantsUtil.EMAIL + "-" + ConstantsUtil.EMAILVALUE);
            assertEquals(this.timeUtil.getDespFromLog(
                    "TestServletContextAttributeListener", "attributeAdded"),
                    ConstantsUtil.WELCOMESTRING + "-"
                            + ConstantsUtil.WELCOMESTRINGVALUE);
        } finally {
            conn.disconnect();
        }

        request = this.warContextPath
                + "/ServletContextListenerServlet?modifyAttribute=true";
        url = Dispatcher.createURL(request, this.server);
        conn = (HttpURLConnection) url.openConnection();
        // modify attributes
        try {
            assertEquals(conn.getResponseCode(), 200);
            assertEquals(conn.getContentType(), "text/html");
            String response = Dispatcher.dispatch(conn);
            if (this.debug) {
                log(response);
            }
            // check if content of response is correct
            log("verify content of response is correct");
            assertTrue(response.indexOf("ServletContextListenerServlet") > 0);
            assertTrue(response.indexOf(ConstantsUtil.EMAIL + "-"
                    + ConstantsUtil.EMAILVALUE) > 0);
            assertTrue(response.indexOf(ConstantsUtil.WELCOMESTRING + "-" + "null") > 0);
            assertTrue(response.indexOf(ConstantsUtil.WELCOMESTATEMENT + "-"
                    + ConstantsUtil.WELCOMESTATEMENTVALUE2) > 0);

            // check if methods are invoked
            log("verify other methods are invoked");
            assertTrue(this.timeUtil.getTimeFromLog(
                    "TestServletContextAttributeListener", "attributeRemoved") > beforeInstall);
            assertEquals(
                    this.timeUtil.getDespFromLog(
                            "TestServletContextAttributeListener",
                            "attributeReplaced"), ConstantsUtil.WELCOMESTATEMENT
                            + "-" + ConstantsUtil.WELCOMESTATEMENTVALUE);
        } finally {
            conn.disconnect();
        }

        request = this.warContextPath
                + "/ServletContextListenerServlet?modifyAttribute=reset";
        url = Dispatcher.createURL(request, this.server);
        conn = (HttpURLConnection) url.openConnection();
        // reset attributes back to the initial values
        try {
            assertEquals(conn.getResponseCode(), 200);
            assertEquals(conn.getContentType(), "text/html");
            String response = Dispatcher.dispatch(conn);
            if (this.debug) {
                log(response);
            }
            // check if content of response is correct
            log("verify content of response is correct");
            assertTrue(response.indexOf("ServletContextListenerServlet") > 0);
            assertTrue(response.indexOf(ConstantsUtil.EMAIL + "-"
                    + ConstantsUtil.EMAILVALUE) > 0);
            assertTrue(response.indexOf(ConstantsUtil.WELCOMESTRING + "-"
                    + ConstantsUtil.WELCOMESTRINGVALUE) > 0);
            assertTrue(response.indexOf(ConstantsUtil.WELCOMESTATEMENT + "-"
                    + ConstantsUtil.WELCOMESTATEMENTVALUE) > 0);

            // check if methods are invoked
            log("verify other methods are invoked");
            assertEquals(this.timeUtil.getDespFromLog(
                    "TestServletContextAttributeListener", "attributeAdded"),
                    ConstantsUtil.WELCOMESTRING + "-"
                            + ConstantsUtil.WELCOMESTRINGVALUE);
            assertEquals(
                    this.timeUtil.getDespFromLog(
                            "TestServletContextAttributeListener",
                            "attributeReplaced"), ConstantsUtil.WELCOMESTATEMENT
                            + "-" + ConstantsUtil.WELCOMESTATEMENTVALUE2);
        } finally {
            conn.disconnect();
        }
    }

    /*
     * test annotated methods/fields in container manager class that implement
     * javax.servlet.ServletRequestListener or
     * javax.servlet.ServletRequestAttributeListener are called
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
            if (this.debug) {
                log(response);
            }
            // check if content of response is correct
            log ("verify content of response is correct");
            assertTrue(response.indexOf("RequestListenerServlet") > 0);
            assertTrue(response.indexOf(ConstantsUtil.WELCOMESTRING + "-"
                    + ConstantsUtil.WELCOMESTRINGVALUE) > 0);
            assertTrue(response.indexOf(ConstantsUtil.WELCOMESTATEMENT + "-"
                    + ConstantsUtil.WELCOMESTATEMENTVALUE) > 0);
            assertEquals(response.indexOf("null"), -1);

            // check if annotated methods are invoked
            log("verify annotated methods are invoked");
            assertTrue(this.timeUtil.getTimeFromLog("RequestListenerServlet",
                    ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
            assertTrue(this.timeUtil.getTimeFromLog(
                    "TestServletRequestListener", ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
            assertTrue(this.timeUtil.getTimeFromLog(
                    "TestServletRequestAttributeListener",
                    ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
            log("verify other methods are invoked");
            assertEquals(this.timeUtil.getDespFromLog(
                    "TestServletRequestListener", "requestInitialized"),
                    ConstantsUtil.WELCOMESTRING + "-"
                            + ConstantsUtil.WELCOMESTRINGVALUE);
            assertEquals(this.timeUtil.getDespFromLog(
                    "TestServletRequestAttributeListener", "attributeAdded"),
                    ConstantsUtil.WELCOMESTRING + "-"
                            + ConstantsUtil.WELCOMESTRINGVALUE);
            assertTrue(this.timeUtil.getTimeFromLog(
                    "TestServletRequestAttributeListener", "attributeRemoved") > beforeInstall);
            assertEquals(
                    this.timeUtil.getDespFromLog(
                            "TestServletRequestAttributeListener",
                            "attributeReplaced"), ConstantsUtil.WELCOMESTATEMENT
                            + "-" + ConstantsUtil.WELCOMESTATEMENTVALUE);
        } finally {
            conn.disconnect();
        }

        conn = (HttpURLConnection) url.openConnection();
        // modify attributes
        try {
            assertEquals(conn.getResponseCode(), 200);
            assertEquals(conn.getContentType(), "text/html");
            String response = Dispatcher.dispatch(conn);
            if (this.debug) {
                log(response);
            }
            // check if content of response is correct
            log("verify content of response is correct");
            assertTrue(response.indexOf("RequestListenerServlet") > 0);
            assertTrue(response.indexOf(ConstantsUtil.WELCOMESTRING + "-"
                    + ConstantsUtil.WELCOMESTRINGVALUE) > 0);
            assertTrue(response.indexOf(ConstantsUtil.WELCOMESTATEMENT + "-"
                    + ConstantsUtil.WELCOMESTATEMENTVALUE) > 0);
            assertEquals(response.indexOf("null"), -1);

            // check if annotated methods are invoked
            log("verify annotated methods are invoked");
            assertTrue(this.timeUtil.getTimeFromLog("RequestListenerServlet",
                    ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
            assertTrue(this.timeUtil.getTimeFromLog(
                    "TestServletRequestListener", ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
            assertTrue(this.timeUtil.getTimeFromLog(
                    "TestServletRequestAttributeListener",
                    ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
            log("check if other methods are invoked");
            assertEquals(this.timeUtil.getDespFromLog(
                    "TestServletRequestListener", "requestInitialized"),
                    ConstantsUtil.WELCOMESTRING + "-"
                            + ConstantsUtil.WELCOMESTRINGVALUE);
            assertEquals(this.timeUtil.getDespFromLog(
                    "TestServletRequestAttributeListener", "attributeAdded"),
                    ConstantsUtil.WELCOMESTRING + "-"
                            + ConstantsUtil.WELCOMESTRINGVALUE);
            assertTrue(this.timeUtil.getTimeFromLog(
                    "TestServletRequestAttributeListener", "attributeRemoved") > beforeInstall);
            assertEquals(
                    this.timeUtil.getDespFromLog(
                            "TestServletRequestAttributeListener",
                            "attributeReplaced"), ConstantsUtil.WELCOMESTATEMENT
                            + "-" + ConstantsUtil.WELCOMESTATEMENTVALUE);
        } finally {
            conn.disconnect();
        }
    }

    /*
     * test annotated methods/fields in container manager class that implement
     * javax.servlet.ServletRequestListener or
     * javax.servlet.ServletRequestAttributeListener are called
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
            if (this.debug) {
                log(response);
            }
            // check if content of response is correct
            log("verify content of response is correct");
            assertTrue(response.indexOf("HTTPSessionListenerServlet") > 0);
            assertTrue(response.indexOf(ConstantsUtil.WELCOMESTRING + "-"
                    + ConstantsUtil.WELCOMESTRINGVALUE) > 0);
            assertTrue(response.indexOf(ConstantsUtil.WELCOMESTATEMENT + "-"
                    + ConstantsUtil.WELCOMESTATEMENTVALUE) > 0);
            assertEquals(response.indexOf("null"), -1);

            // check if annotated methods are invoked
            log("verify annotated methods are invoked");
            assertTrue(this.timeUtil.getTimeFromLog(
                    "HTTPSessionListenerServlet", ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
            assertTrue(this.timeUtil.getTimeFromLog("TestHttpSessionListener",
                    ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
            assertTrue(this.timeUtil
                    .getTimeFromLog("TestHttpSessionAttributeListener",
                            ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
            log("verify other methods are invoked");
            assertEquals(this.timeUtil.getDespFromLog(
                    "TestHttpSessionListener", "sessionCreated"),
                    ConstantsUtil.WELCOMESTRING + "-"
                            + ConstantsUtil.WELCOMESTRINGVALUE);
            assertEquals(this.timeUtil.getDespFromLog(
                    "TestHttpSessionAttributeListener", "attributeAdded"),
                    ConstantsUtil.WELCOMESTRING + "-"
                            + ConstantsUtil.WELCOMESTRINGVALUE);
            assertTrue(this.timeUtil.getTimeFromLog(
                    "TestHttpSessionAttributeListener", "attributeRemoved") > beforeInstall);
            assertEquals(this.timeUtil.getDespFromLog(
                    "TestHttpSessionAttributeListener", "attributeReplaced"),
                    ConstantsUtil.WELCOMESTATEMENT + "-"
                            + ConstantsUtil.WELCOMESTATEMENTVALUE);
        } finally {
            conn.disconnect();
        }

        conn = (HttpURLConnection) url.openConnection();
        try {
            assertEquals(conn.getResponseCode(), 200);
            assertEquals(conn.getContentType(), "text/html");
            String response = Dispatcher.dispatch(conn);
            if (this.debug) {
                log(response);
            }
            // check if content of response is correct
            log("verify content of response is correct");
            assertTrue(response.indexOf("HTTPSessionListenerServlet") > 0);
            assertTrue(response.indexOf(ConstantsUtil.WELCOMESTRING + "-"
                    + ConstantsUtil.WELCOMESTRINGVALUE) > 0);
            assertTrue(response.indexOf(ConstantsUtil.WELCOMESTATEMENT + "-"
                    + ConstantsUtil.WELCOMESTATEMENTVALUE) > 0);
            assertEquals(response.indexOf("null"), -1);

            // check if annotated methods are invoked
            log("verify annotated methods are invoked");
            assertTrue(this.timeUtil.getTimeFromLog(
                    "HTTPSessionListenerServlet", ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
            assertTrue(this.timeUtil.getTimeFromLog("TestHttpSessionListener",
                    ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
            assertTrue(this.timeUtil
                    .getTimeFromLog("TestHttpSessionAttributeListener",
                            ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
            log("verify other methods are invoked");
            assertEquals(this.timeUtil.getDespFromLog(
                    "TestHttpSessionListener", "sessionCreated"),
                    ConstantsUtil.WELCOMESTRING + "-"
                            + ConstantsUtil.WELCOMESTRINGVALUE);
            assertEquals(this.timeUtil.getDespFromLog(
                    "TestHttpSessionAttributeListener", "attributeAdded"),
                    ConstantsUtil.WELCOMESTRING + "-"
                            + ConstantsUtil.WELCOMESTRINGVALUE);
            assertTrue(this.timeUtil.getTimeFromLog(
                    "TestHttpSessionAttributeListener", "attributeRemoved") > beforeInstall);
            assertEquals(this.timeUtil.getDespFromLog(
                    "TestHttpSessionAttributeListener", "attributeReplaced"),
                    ConstantsUtil.WELCOMESTATEMENT + "-"
                            + ConstantsUtil.WELCOMESTATEMENTVALUE);
        } finally {
            conn.disconnect();
        }
    }

    /*
     * test @PreDestroy annotated classes are called in container manager
     * classes that implement the following interfaces: javax.servlet.Filter
     * javax.servlet.ServletContextListener
     * javax.servlet.ServletContextAttributeListener
     * javax.servlet.ServletRequestListener
     * javax.servlet.ServletRequestAttributeListener
     * javax.servlet.http.HttpSessionListener
     * javax.servlet.http.HttpSessionAttributeListener
     */
    public void testPreDestroyOther() throws Exception {
        // capture the beforeUninstall time
        long beforeUninstall = System.currentTimeMillis();
        uninstallWar();

        log("verify annotated methods are invoked");
        assertTrue(this.timeUtil.getTimeFromLog("TestFilter",
                ConstantsUtil.PREDESTROY) > beforeUninstall);
        assertTrue(this.timeUtil.getTimeFromLog(
                "ServletContextListenerServlet", ConstantsUtil.PREDESTROY) > beforeUninstall);
        assertTrue(this.timeUtil.getTimeFromLog("TestServletContextListener",
                ConstantsUtil.PREDESTROY) > beforeUninstall);
        assertTrue(this.timeUtil.getTimeFromLog(
                "TestServletContextAttributeListener", ConstantsUtil.PREDESTROY) > beforeUninstall);
        assertTrue(this.timeUtil.getTimeFromLog("RequestListenerServlet",
                ConstantsUtil.PREDESTROY) > beforeUninstall);
        assertTrue(this.timeUtil.getTimeFromLog("TestServletRequestListener",
                ConstantsUtil.PREDESTROY) > beforeUninstall);
        assertTrue(this.timeUtil.getTimeFromLog(
                "TestServletRequestAttributeListener", ConstantsUtil.PREDESTROY) > beforeUninstall);
        assertTrue(this.timeUtil.getTimeFromLog("HTTPSessionListenerServlet",
                ConstantsUtil.PREDESTROY) > beforeUninstall);
        assertTrue(this.timeUtil.getTimeFromLog("TestHttpSessionListener",
                ConstantsUtil.PREDESTROY) > beforeUninstall);
        assertTrue(this.timeUtil.getTimeFromLog(
                "TestHttpSessionAttributeListener", ConstantsUtil.PREDESTROY) > beforeUninstall);
    }
}

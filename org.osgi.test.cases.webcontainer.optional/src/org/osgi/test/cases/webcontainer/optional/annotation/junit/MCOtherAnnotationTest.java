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

import java.util.jar.Manifest;

import org.osgi.framework.Constants;
import org.osgi.test.cases.webcontainer.optional.WebContainerOptionalTestBundleControl;
import org.osgi.test.cases.webcontainer.optional.util.ConstantsUtil;
import org.osgi.test.cases.webcontainer.util.validate.BundleManifestValidator;

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
public class MCOtherAnnotationTest extends WebContainerOptionalTestBundleControl {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        super.prepare("/tw3");
        this.options.put(Constants.IMPORT_PACKAGE, IMPORTS_ANNOTATION);

        super.cleanupPropertyFile();

        // install + start the war file
        log("install war file: tw3.war at context path " + this.warContextPath);
        this.b = installBundle(super.getWarURL("tw3.war", this.options), true);
        
        assertTrue("should be able to see the servlet context associated with /tw3 web contextpath",
                super.checkServiceRegistered("/tw3"));
    }


    /*
     * set deployOptions to null to rely on the web container service to
     * generate the manifest
     */
    public void testBundleManifest() throws Exception {
		Manifest originalManifest = super.getManifest("/tw3.war");
        BundleManifestValidator validator = new BundleManifestValidator(this.b,
                originalManifest, this.options, this.debug);
        validator.validate();
    }

    /*
     * test home page
     */
    public void testHome() throws Exception {
        final String request = this.warContextPath + "/";
        String response = super.getResponse(request);
        super.checkTW3HomeResponse(response);
    }
    
    /*
     * test annotated methods/fields in container manager class that implement
     * javax.servlet.Filter are not called when the metadata-complete attribute
     * is set to true.
     */
    public void testFilter() throws Exception {
        final String request = this.warContextPath
                + "/PostConstructPreDestroyServlet1";
        String response = super.getResponse(request);

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

        log("verify annotated methods are not invoked");
        assertEquals(
                this.timeUtil.getTimeFromLog("PostConstructPreDestroyServlet1",
                        ConstantsUtil.POSTCONSTRUCT), 0);
        assertEquals(this.timeUtil.getTimeFromLog("TestFilter",
                ConstantsUtil.POSTCONSTRUCT), 0);

        log("verify non-annotated methods are still called");
        assertTrue(this.timeUtil.getTimeFromLog("TestFilter", "init") + " should occur earlier than " + beforeInstall, 
                this.timeUtil.getTimeFromLog("TestFilter", "init") > beforeInstall);
        assertTrue(this.timeUtil.getTimeFromLog("TestFilter", "doFilter") > this.timeUtil
                .getTimeFromLog("TestFilter", "init"));
        assertEquals(this.timeUtil.getDespFromLog("TestFilter", "doFilter"),
                ConstantsUtil.NULL);
        assertEquals(this.timeUtil.getDespFromLog("TestFilter", "init"),
                ConstantsUtil.NULL);
        assertEquals(this.timeUtil.getDespFromLog("TestServletRequestListener",
                "requestInitialized"), ConstantsUtil.WELCOMESTRING + "-"
                + ConstantsUtil.NULL);

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
        String response = super.getResponse(request);

        // check if content of response is correct
        log("verify content of response is correct");
        assertTrue(response.indexOf("ServletContextListenerServlet") > 0);
        assertTrue(response.indexOf(ConstantsUtil.EMAIL + "-"
                + ConstantsUtil.EMAILVALUE) > 0);
        assertTrue(response.indexOf(ConstantsUtil.WELCOMESTRING + "-"
                + ConstantsUtil.NULL) > 0);
        assertTrue(response.indexOf(ConstantsUtil.WELCOMESTATEMENT + "-"
                + ConstantsUtil.NULL) > 0);

        // check if annotated methods are not invoked
        log("verify annotated methods are not invoked");
        assertEquals(this.timeUtil.getTimeFromLog(
                "ServletContextListenerServlet", ConstantsUtil.POSTCONSTRUCT),
                0);
        assertEquals(this.timeUtil.getTimeFromLog("TestServletContextListener",
                ConstantsUtil.POSTCONSTRUCT), 0);
        assertEquals(this.timeUtil.getTimeFromLog(
                "TestServletContextAttributeListener",
                ConstantsUtil.POSTCONSTRUCT), 0);
        log("verify non-annotated methods are still called");
        assertEquals(this.timeUtil.getDespFromLog("TestServletContextListener",
                "contextInitialized"), ConstantsUtil.EMAIL + "-"
                + ConstantsUtil.EMAILVALUE);
        assertEquals(this.timeUtil.getDespFromLog(
                "TestServletContextAttributeListener", "attributeAdded"),
                ConstantsUtil.EMAIL + "-" + ConstantsUtil.EMAILVALUE);
    }

    /*
     * test annotated methods/fields in container manager class that implement
     * javax.servlet.ServletRequestListener or
     * javax.servlet.ServletRequestAttributeListener are not called when the
     * metadata-complete attribute is set to true.
     */
    public void testServletRequest() throws Exception {
        String request = this.warContextPath + "/RequestListenerServlet";
        String response = super.getResponse(request);

        // check if content of response is correct
        log("verify content of response is correct");
        assertTrue(response.indexOf("RequestListenerServlet") > 0);
        assertTrue(response.indexOf(ConstantsUtil.WELCOMESTRING + "-"
                + ConstantsUtil.NULL) > 0);
        assertTrue(response.indexOf(ConstantsUtil.WELCOMESTATEMENT + "-"
                + ConstantsUtil.NULL) > 0);

        // check if annotated methods are not invoked
        log("verify annotated methods are not invoked");
        assertEquals(this.timeUtil.getTimeFromLog("RequestListenerServlet",
                ConstantsUtil.POSTCONSTRUCT), 0);
        assertEquals(this.timeUtil.getTimeFromLog("TestServletRequestListener",
                ConstantsUtil.POSTCONSTRUCT), 0);
        assertEquals(this.timeUtil.getTimeFromLog(
                "TestServletRequestAttributeListener",
                ConstantsUtil.POSTCONSTRUCT), 0);
        assertEquals(this.timeUtil.getDespFromLog("TestServletRequestListener",
                "requestInitialized"), ConstantsUtil.WELCOMESTRING + "-"
                + ConstantsUtil.NULL);
    }

    /*
     * test annotated methods/fields in container manager class that implement
     * javax.servlet.ServletRequestListener or
     * javax.servlet.ServletRequestAttributeListener are not called when the
     * metadata-complete attribute is set to true.
     */
    public void testHTTPSession() throws Exception {
        String request = this.warContextPath + "/HTTPSessionListenerServlet";
        String response = super.getResponse(request);

        // check if content of response is correct
        log("verify content of response is correct");
        assertTrue(response.indexOf("HTTPSessionListenerServlet") > 0);
        assertTrue(response.indexOf(ConstantsUtil.WELCOMESTRING + "-"
                + ConstantsUtil.NULL) > 0);
        assertTrue(response.indexOf(ConstantsUtil.WELCOMESTATEMENT + "-"
                + ConstantsUtil.NULL) > 0);

        // check if annotated methods are not invoked
        log("verify annotated methods are not invoked");
        assertEquals(this.timeUtil.getTimeFromLog("HTTPSessionListenerServlet",
                ConstantsUtil.POSTCONSTRUCT), 0);
        assertEquals(this.timeUtil.getTimeFromLog("TestHttpSessionListener",
                ConstantsUtil.POSTCONSTRUCT), 0);
        assertEquals(this.timeUtil
                .getTimeFromLog("TestHttpSessionAttributeListener",
                        ConstantsUtil.POSTCONSTRUCT), 0);
        assertEquals(this.timeUtil.getDespFromLog("TestHttpSessionListener",
                "sessionCreated"), ConstantsUtil.WELCOMESTRING + "-"
                + ConstantsUtil.NULL);
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
        super.tearDown();

        // check if annotated methods are not invoked
        log("verify annotated methods are not invoked");
        assertEquals(this.timeUtil.getTimeFromLog("TestFilter",
                ConstantsUtil.PREDESTROY), 0);
        assertEquals(this.timeUtil.getTimeFromLog(
                "ServletContextListenerServlet", ConstantsUtil.PREDESTROY), 0);
        assertEquals(this.timeUtil.getTimeFromLog("TestServletContextListener",
                ConstantsUtil.PREDESTROY), 0);
        assertEquals(this.timeUtil
                .getTimeFromLog("TestServletContextAttributeListener",
                        ConstantsUtil.PREDESTROY), 0);
        assertEquals(this.timeUtil.getTimeFromLog("RequestListenerServlet",
                ConstantsUtil.PREDESTROY), 0);
        assertEquals(this.timeUtil.getTimeFromLog("TestServletRequestListener",
                ConstantsUtil.PREDESTROY), 0);
        assertEquals(this.timeUtil
                .getTimeFromLog("TestServletRequestAttributeListener",
                        ConstantsUtil.PREDESTROY), 0);
        assertEquals(this.timeUtil.getTimeFromLog("HTTPSessionListenerServlet",
                ConstantsUtil.PREDESTROY), 0);
        assertEquals(this.timeUtil.getTimeFromLog("TestHttpSessionListener",
                ConstantsUtil.PREDESTROY), 0);
        assertEquals(this.timeUtil.getTimeFromLog(
                "TestHttpSessionAttributeListener", ConstantsUtil.PREDESTROY),
                0);
    }
}

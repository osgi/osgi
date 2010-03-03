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
public class OtherAnnotationTest extends WebContainerOptionalTestBundleControl {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        super.prepare("/tw2");
        this.options.put(Constants.IMPORT_PACKAGE, IMPORTS_ANNOTATION);

        super.cleanupPropertyFile();
        // install + start the war file
        log("install war file: tw2.war at context path " + this.warContextPath);
        this.b = installBundle(super.getWarURL("tw3.war", this.options), true);
        assertTrue("should be able to see the servlet context associated with /tw2 web contextpath",
                super.checkServiceRegistered("/tw2"));
    }

    /*
     * set deployOptions to null to rely on the web container service to
     * generate the manifest
     */
    public void testBundleManifest() throws Exception {
		Manifest originalManifest = super.getManifest("/tw2.war");
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
        super.checkTW2HomeResponse(response);
    }
    
    /*
     * test annotated methods/fields in container manager class that implement
     * javax.servlet.Filter are called
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

        log("verify annotated methods are invoked");
        assertTrue(this.timeUtil.getTimeFromLog(
                "PostConstructPreDestroyServlet1", ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
        assertTrue(this.timeUtil.getTimeFromLog("TestFilter",
                ConstantsUtil.POSTCONSTRUCT) > beforeInstall);

        log("verify other methods are invoked");
        assertTrue(this.timeUtil.getTimeFromLog("TestFilter", "init") > beforeInstall);
        assertTrue(this.timeUtil.getTimeFromLog("TestFilter", "doFilter") > this.timeUtil
                .getTimeFromLog("TestFilter", "init"));
        assertEquals(this.timeUtil.getDespFromLog("TestFilter", "doFilter"),
                ConstantsUtil.WELCOMESTRINGVALUE);
        assertEquals(this.timeUtil.getDespFromLog("TestFilter", "init"),
                ConstantsUtil.WELCOMESTATEMENTVALUE);
    }

    /*
     * test annotated methods/fields in container manager class that implement
     * javax.servlet.ServletContextListener or
     * javax.servlet.ServletContextAttributeListener are called
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
                + ConstantsUtil.WELCOMESTRINGVALUE) > 0);
        assertTrue(response.indexOf(ConstantsUtil.WELCOMESTATEMENT + "-"
                + ConstantsUtil.WELCOMESTATEMENTVALUE) > 0);
        assertEquals(response.indexOf("null"), -1);

        // check if annotated methods are invoked
        log("verify annotated methods are invoked");
        assertTrue(this.timeUtil.getTimeFromLog(
                "ServletContextListenerServlet", ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
        assertTrue(this.timeUtil.getTimeFromLog("TestServletContextListener",
                ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
        assertTrue(this.timeUtil.getTimeFromLog(
                "TestServletContextAttributeListener",
                ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
        log("verify other methods are invoked");
        assertEquals(this.timeUtil.getDespFromLog("TestServletContextListener",
                "contextInitialized"), ConstantsUtil.EMAIL + "-"
                + ConstantsUtil.EMAILVALUE);
        assertEquals(this.timeUtil.getDespFromLog(
                "TestServletContextAttributeListener", "attributeAdded"),
                ConstantsUtil.WELCOMESTRING + "-"
                        + ConstantsUtil.WELCOMESTRINGVALUE);

        request = this.warContextPath
                + "/ServletContextListenerServlet?modifyAttribute=true";
        response = super.getResponse(request);
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
        assertEquals(this.timeUtil.getDespFromLog(
                "TestServletContextAttributeListener", "attributeReplaced"),
                ConstantsUtil.WELCOMESTATEMENT + "-"
                        + ConstantsUtil.WELCOMESTATEMENTVALUE);

        request = this.warContextPath
                + "/ServletContextListenerServlet?modifyAttribute=reset";
        response = super.getResponse(request);
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
        assertEquals(this.timeUtil.getDespFromLog(
                "TestServletContextAttributeListener", "attributeReplaced"),
                ConstantsUtil.WELCOMESTATEMENT + "-"
                        + ConstantsUtil.WELCOMESTATEMENTVALUE2);
    }

    /*
     * test annotated methods/fields in container manager class that implement
     * javax.servlet.ServletRequestListener or
     * javax.servlet.ServletRequestAttributeListener are called
     */
    public void testServletRequest() throws Exception {
        String request = this.warContextPath + "/RequestListenerServlet";
        String response = super.getResponse(request);
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
        assertTrue(this.timeUtil.getTimeFromLog("TestServletRequestListener",
                ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
        assertTrue(this.timeUtil.getTimeFromLog(
                "TestServletRequestAttributeListener",
                ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
        log("verify other methods are invoked");
        assertEquals(this.timeUtil.getDespFromLog("TestServletRequestListener",
                "requestInitialized"), ConstantsUtil.WELCOMESTRING + "-"
                + ConstantsUtil.WELCOMESTRINGVALUE);
        assertEquals(this.timeUtil.getDespFromLog(
                "TestServletRequestAttributeListener", "attributeAdded"),
                ConstantsUtil.WELCOMESTRING + "-"
                        + ConstantsUtil.WELCOMESTRINGVALUE);
        assertTrue(this.timeUtil.getTimeFromLog(
                "TestServletRequestAttributeListener", "attributeRemoved") > beforeInstall);
        assertEquals(this.timeUtil.getDespFromLog(
                "TestServletRequestAttributeListener", "attributeReplaced"),
                ConstantsUtil.WELCOMESTATEMENT + "-"
                        + ConstantsUtil.WELCOMESTATEMENTVALUE);

        // modify attributes
        response = super.getResponse(request);
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
        assertTrue(this.timeUtil.getTimeFromLog("TestServletRequestListener",
                ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
        assertTrue(this.timeUtil.getTimeFromLog(
                "TestServletRequestAttributeListener",
                ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
        log("check if other methods are invoked");
        assertEquals(this.timeUtil.getDespFromLog("TestServletRequestListener",
                "requestInitialized"), ConstantsUtil.WELCOMESTRING + "-"
                + ConstantsUtil.WELCOMESTRINGVALUE);
        assertEquals(this.timeUtil.getDespFromLog(
                "TestServletRequestAttributeListener", "attributeAdded"),
                ConstantsUtil.WELCOMESTRING + "-"
                        + ConstantsUtil.WELCOMESTRINGVALUE);
        assertTrue(this.timeUtil.getTimeFromLog(
                "TestServletRequestAttributeListener", "attributeRemoved") > beforeInstall);
        assertEquals(this.timeUtil.getDespFromLog(
                "TestServletRequestAttributeListener", "attributeReplaced"),
                ConstantsUtil.WELCOMESTATEMENT + "-"
                        + ConstantsUtil.WELCOMESTATEMENTVALUE);
    }

    /*
     * test annotated methods/fields in container manager class that implement
     * javax.servlet.ServletRequestListener or
     * javax.servlet.ServletRequestAttributeListener are called
     */
    public void testHTTPSession() throws Exception {
        String request = this.warContextPath + "/HTTPSessionListenerServlet";
        String response = super.getResponse(request);
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
        assertTrue(this.timeUtil.getTimeFromLog("HTTPSessionListenerServlet",
                ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
        assertTrue(this.timeUtil.getTimeFromLog("TestHttpSessionListener",
                ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
        assertTrue(this.timeUtil
                .getTimeFromLog("TestHttpSessionAttributeListener",
                        ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
        log("verify other methods are invoked");
        assertEquals(this.timeUtil.getDespFromLog("TestHttpSessionListener",
                "sessionCreated"), ConstantsUtil.WELCOMESTRING + "-"
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

        response = super.getResponse(request);

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
        assertTrue(this.timeUtil.getTimeFromLog("HTTPSessionListenerServlet",
                ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
        assertTrue(this.timeUtil.getTimeFromLog("TestHttpSessionListener",
                ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
        assertTrue(this.timeUtil
                .getTimeFromLog("TestHttpSessionAttributeListener",
                        ConstantsUtil.POSTCONSTRUCT) > beforeInstall);
        log("verify other methods are invoked");
        assertEquals(this.timeUtil.getDespFromLog("TestHttpSessionListener",
                "sessionCreated"), ConstantsUtil.WELCOMESTRING + "-"
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
        super.tearDown();

        log("verify annotated methods are invoked");
        assertTrue(this.timeUtil.getTimeFromLog("TestFilter",
                ConstantsUtil.PREDESTROY) > beforeUninstall);
        assertTrue(this.timeUtil.getTimeFromLog(
                "ServletContextListenerServlet", ConstantsUtil.PREDESTROY) > beforeUninstall);
        assertTrue(this.timeUtil.getTimeFromLog("TestServletContextListener",
                ConstantsUtil.PREDESTROY) > beforeUninstall);
        assertTrue(this.timeUtil
                .getTimeFromLog("TestServletContextAttributeListener",
                        ConstantsUtil.PREDESTROY) > beforeUninstall);
        assertTrue(this.timeUtil.getTimeFromLog("RequestListenerServlet",
                ConstantsUtil.PREDESTROY) > beforeUninstall);
        assertTrue(this.timeUtil.getTimeFromLog("TestServletRequestListener",
                ConstantsUtil.PREDESTROY) > beforeUninstall);
        assertTrue(this.timeUtil
                .getTimeFromLog("TestServletRequestAttributeListener",
                        ConstantsUtil.PREDESTROY) > beforeUninstall);
        assertTrue(this.timeUtil.getTimeFromLog("HTTPSessionListenerServlet",
                ConstantsUtil.PREDESTROY) > beforeUninstall);
        assertTrue(this.timeUtil.getTimeFromLog("TestHttpSessionListener",
                ConstantsUtil.PREDESTROY) > beforeUninstall);
        assertTrue(this.timeUtil.getTimeFromLog(
                "TestHttpSessionAttributeListener", ConstantsUtil.PREDESTROY) > beforeUninstall);
    }
}

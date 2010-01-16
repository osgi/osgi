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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.osgi.test.cases.webcontainer.util.WebContainerTestBundleControl;
import org.osgi.test.cases.webcontainer.util.ConstantsUtil;
import org.osgi.test.cases.webcontainer.util.Dispatcher;

/**
 * @version $Rev$ $Date$
 */
public class PostConstructPreDestroyErrorTest extends
        WebContainerTestBundleControl {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        super.prepare("/tw2");

        super.cleanupPropertyFile();

        // install + start the war file
        log("install war file: tw2.war at context path " + this.warContextPath);
        this.b = installBundle(super.getWarURL("tw3.war", this.options), true);
    }

    /*
     * test @postConstruct annotated public method is not called when there are
     * multiple @postConstruct annotations
     */
    public void testPostConstructError001() throws Exception {
        final String request = this.warContextPath
                + "/PostConstructErrorServlet1";
        final URL url = Dispatcher.createURL(request, this.server);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            assertTrue(conn.getResponseCode() != 200);
            String response = Dispatcher.dispatch(conn);
            if (this.debug) {
                log(response);
            }
            fail("should be getting an exception");
        } catch (IOException e) {
            // ignore - this can be expected
        } finally {
            conn.disconnect();
        }
        log("verify annotated methods are not invoked when there are multiple @postConstruct annotations");
        assertEquals(this.timeUtil.getTimeFromLog("PostConstructErrorServlet1",
                "postConstruct1"), 0);
        assertEquals(this.timeUtil.getTimeFromLog("PostConstructErrorServlet1",
                "postConstruct2"), 0);
    }

    /*
     * test @postConstruct annotated public method is not called when
     * 
     * @postConstruct annotated method is static
     */
    public void testPostConstructError002() throws Exception {
        final String request = this.warContextPath
                + "/PostConstructErrorServlet2";
        final URL url = Dispatcher.createURL(request, this.server);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            assertTrue(conn.getResponseCode() != 200);
            String response = Dispatcher.dispatch(conn);
            if (this.debug) {
                log(response);
            }
            fail("should be getting an exception");
        } catch (IOException e) {
            // ignore - this can be expected
        } finally {
            conn.disconnect();
        }
        log("verify annotated methods are not invoked when @postConstruct annotated method is static");
        assertEquals(this.timeUtil.getTimeFromLog("PostConstructErrorServlet2",
                ConstantsUtil.POSTCONSTRUCT), 0);
    }

    /*
     * test @postConstruct annotated public method is not called when
     * 
     * @postConstruct annotated method throws Exception
     */
    public void testPostConstructError003() throws Exception {
        final String request = this.warContextPath
                + "/PostConstructErrorServlet3";
        final URL url = Dispatcher.createURL(request, this.server);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            assertTrue(conn.getResponseCode() != 200);
            String response = Dispatcher.dispatch(conn);
            if (this.debug) {
                log(response);
            }
            fail("should be getting an exception");
        } catch (IOException e) {
            // ignore - this can be expected
        } finally {
            conn.disconnect();
        }
        log("verify annotated methods are not invoked when @postConstruct annotated method throws Exception");
        assertEquals(this.timeUtil.getTimeFromLog("PostConstructErrorServlet3",
                ConstantsUtil.POSTCONSTRUCT), 0);
    }

    /*
     * test @preDestroy annotated public method is not called when there are
     * multiple @preDestroy annotations
     */
    public void testPreDestroyError001() throws Exception {
        final String request = this.warContextPath + "/PreDestroyErrorServlet1";
        String response = super.getResponse(request);
        // check if content of response is correct
        log("verify content of response is correct");
        assertTrue(response.indexOf("PreDestroyErrorServlet1") > 0);
        assertTrue(response.indexOf("PreDestroyErrorServlet1.printContext "
                + ConstantsUtil.PRINTCONTEXT) > 0);
        assertEquals(response.indexOf("null"), -1);
        // check if the time stamp in response is after the beforeStart
        // time.
        assertTrue(this.timeUtil.getTimeFromResponse(response) > beforeInstall);
        log("verify annotated methods are not invoked when there are multiple @preDestroy annotations");
        assertEquals(this.timeUtil.getTimeFromLog("PreDestroyErrorServlet1",
                ConstantsUtil.POSTCONSTRUCT), 0);
    }

    /*
     * test @preDestroy annotated public method is not called when @preDestroy
     * annotated method is static
     */
    public void testPreDestroyError002() throws Exception {
        final String request = this.warContextPath + "/PreDestroyErrorServlet2";
        String response = super.getResponse(request);
        // check if content of response is correct
        log("verify content of response is correct");
        assertTrue(response.indexOf("PreDestroyErrorServlet2") > 0);
        assertTrue(response.indexOf("PreDestroyErrorServlet2.printContext "
                + ConstantsUtil.PRINTCONTEXT) > 0);
        assertEquals(response.indexOf("null"), -1);
        // check if the time stamp in response is after the beforeStart
        // time.
        assertTrue(this.timeUtil.getTimeFromResponse(response) > beforeInstall);
        log("verify annotated methods are not invoked when @preDestroy annotated method is static");
        assertEquals(this.timeUtil.getTimeFromLog("PreDestroyErrorServlet2",
                ConstantsUtil.POSTCONSTRUCT), 0);
    }

    /*
     * test @preDestroy annotated public method is not called when @preDestroy
     * annotated method throws Exception
     */
    public void testPreDestroyError003() throws Exception {
        final String request = this.warContextPath + "/PreDestroyErrorServlet3";
        String response = super.getResponse(request);
        // check if content of response is correct
        log("verify content of response is correct");
        assertTrue(response.indexOf("PreDestroyErrorServlet3") > 0);
        assertTrue(response.indexOf("PreDestroyErrorServlet3.printContext "
                + ConstantsUtil.PRINTCONTEXT) > 0);
        assertEquals(response.indexOf("null"), -1);
        // check if the time stamp in response is after the beforeStart
        // time.
        assertTrue(this.timeUtil.getTimeFromResponse(response) > beforeInstall);
        log("verify annotated methods are not invoked when @preDestroy annotated method throws Exception");
        assertEquals(this.timeUtil.getTimeFromLog("PreDestroyErrorServlet2",
                ConstantsUtil.POSTCONSTRUCT), 0);
    }

    /*
     * test when war is uninstalled and servlets are destroyed, the pre-destroy
     * annotated methods are not called
     */
    public void testPreDestroyError004() throws Exception {
        testPreDestroyError001();
        testPreDestroyError002();
        testPreDestroyError003();
        super.tearDown();
        // TODO do we need to wait till the war is uninstalled properly?

        log("verify pre-destroy annotated methods are not called");
        assertEquals(this.timeUtil.getTimeFromLog("PreDestroyErrorServlet1",
                "cleanup1"), 0);
        assertEquals(this.timeUtil.getTimeFromLog("PreDestroyErrorServlet1",
                "cleanup2"), 0);
        assertEquals(this.timeUtil.getTimeFromLog("PreDestroyErrorServlet2",
                "cleanup"), 0);
        assertEquals(this.timeUtil.getTimeFromLog("PreDestroyErrorServlet3",
                "cleanup"), 0);

    }
}

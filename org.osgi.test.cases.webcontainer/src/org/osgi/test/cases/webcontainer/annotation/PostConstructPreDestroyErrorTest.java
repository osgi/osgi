/*
 * $Id$
 *
 * Copyright (c) The OSGi Alliance (2009). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.test.cases.webcontainer.annotation;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.osgi.test.cases.webcontainer.util.Constants;
import org.osgi.test.cases.webcontainer.util.Dispatcher;
import org.osgi.test.cases.webcontainer.util.Server;
import org.osgi.test.cases.webcontainer.util.TimeUtil;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class PostConstructPreDestroyErrorTest extends DefaultTestBundleControl {
    // this test case assume war files are already installed for now
    Server server;
    boolean debug;
    String warContextPath;
    long beforeInstall;
    TimeUtil timeUtil;

    public void setUp() throws Exception {
        // TODO if war file already exists, let's remove it first.

        this.server = new Server("localhost");
        this.debug = true;
        this.warContextPath = "/tw2";
        this.timeUtil = new TimeUtil(this.warContextPath);

        // capture a time before install
        beforeInstall = System.currentTimeMillis();

        // TODO install the war file

    }

    private void uninstallWar() throws Exception {
        // TODO uninstall the war file?

    }

    public void tearDown() throws Exception {
        uninstallWar();
    }

    /*
     * test @postConstruct annotated public method is not called
     * when there are multiple @postConstruct annotations
     */
    public void testPostConstructError001() throws Exception {
        final String request = this.warContextPath
                + "/PostConstructErrorServlet1";
        final URL url = Dispatcher.createURL(request, this.server);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            assertTrue(conn.getResponseCode() != 200);
            System.out.println("content type " + conn.getContentType());
            String response = Dispatcher.dispatch(conn);
            if (debug) {
                System.out.println(response);
            }
            // check if content of response contains some error/exception
            assertTrue(response.indexOf("Exception") > 0);
            assertTrue(this.timeUtil.getTimeFromLog("PostConstructErrorServlet1", "postConstruct1") == 0);
            assertTrue(this.timeUtil.getTimeFromLog("PostConstructErrorServlet1", "postConstruct2") == 0);
        } catch (IOException e) {
            // ingore - this can be expected
        } finally {
            conn.disconnect();
        }
    }
    
    /*
     * test @postConstruct annotated public method is not called
     * when @postConstruct annotated method is static
     */
    public void testPostConstructError002() throws Exception {
        final String request = this.warContextPath
                + "/PostConstructErrorServlet2";
        final URL url = Dispatcher.createURL(request, this.server);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            assertTrue(conn.getResponseCode() != 200);
            String response = Dispatcher.dispatch(conn);
            if (debug) {
                System.out.println(response);
            }
            // check if content of response contains some error/exception
            assertTrue(response.indexOf("Exception") > 0);
            assertTrue(this.timeUtil.getTimeFromLog("PostConstructErrorServlet2", "postConstruct") == 0);
        } catch (IOException e) {
            // ingore - this can be expected
        } finally {
            conn.disconnect();
        }
    }
    
    /*
     * test @postConstruct annotated public method is not called
     * when @postConstruct annotated method throws Exception
     */
    public void testPostConstructError003() throws Exception {
        final String request = this.warContextPath
                + "/PostConstructErrorServlet3";
        final URL url = Dispatcher.createURL(request, this.server);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            assertTrue(conn.getResponseCode() != 200);
            String response = Dispatcher.dispatch(conn);
            if (debug) {
                System.out.println(response);
            }
            // check if content of response contains some error/exception
            assertTrue(response.indexOf("Exception") > 0);
            assertTrue(this.timeUtil.getTimeFromLog("PostConstructErrorServlet3", "postConstruct") == 0);
        } catch (IOException e) {
            // ingore - this can be expected
        } finally {
            conn.disconnect();
        }
    }
    
    /*
     * test @preDestroy annotated public method is not called
     * when there are multiple @preDestroy annotations
     */
    public void testPreDestroyError001() throws Exception {
        final String request = this.warContextPath + "/PreDestroyErrorServlet1";
        final URL url = Dispatcher.createURL(request, this.server);
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        try {
            assertTrue(conn.getResponseCode() == 200);
            assertTrue(conn.getContentType().equals("text/html"));
            String response = Dispatcher.dispatch(conn);
            if (debug) {
                log(response);
            }
            // check if content of response is correct
            assertTrue(response.indexOf("PreDestroyErrorServlet1") > 0);
            assertTrue(response.indexOf("PreDestroyErrorServlet1.printContext " + Constants.PRINTCONTEXT) > 0);
            assertTrue(response.indexOf("null") == -1);
            // check if the time stamp in response is after the beforeStart time.
            assertTrue(this.timeUtil.getTimeFromResponse(response) > beforeInstall);
            assertTrue(this.timeUtil.getTimeFromLog("PreDestroyErrorServlet1", "postConstruct") == 0);
        } finally {
            conn.disconnect();
        }
    }
    
    /*
     * test @preDestroy annotated public method is not called
     * when @preDestroy annotated method is static
     */
    public void testPreDestroyError002() throws Exception {
        final String request = this.warContextPath + "/PreDestroyErrorServlet2";
        final URL url = Dispatcher.createURL(request, this.server);
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        try {
            assertTrue(conn.getResponseCode() == 200);
            assertTrue(conn.getContentType().equals("text/html"));
            String response = Dispatcher.dispatch(conn);
            if (debug) {
                log(response);
            }
            // check if content of response is correct
            assertTrue(response.indexOf("PreDestroyErrorServlet2") > 0);
            assertTrue(response.indexOf("PreDestroyErrorServlet2.printContext " + Constants.PRINTCONTEXT) > 0);
            assertTrue(response.indexOf("null") == -1);
            // check if the time stamp in response is after the beforeStart time.
            assertTrue(this.timeUtil.getTimeFromResponse(response) > beforeInstall);
            assertTrue(this.timeUtil.getTimeFromLog("PreDestroyErrorServlet2", "postConstruct") == 0);
        } finally {
            conn.disconnect();
        }
    }
    
    /*
     * test @preDestroy annotated public method is not called
     * when @preDestroy annotated method throws Exception
     */
    public void testPreDestroyError003() throws Exception {
        final String request = this.warContextPath + "/PreDestroyErrorServlet3";
        final URL url = Dispatcher.createURL(request, this.server);
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        try {
            assertTrue(conn.getResponseCode() == 200);
            assertTrue(conn.getContentType().equals("text/html"));
            String response = Dispatcher.dispatch(conn);
            if (debug) {
                log(response);
            }
            // check if content of response is correct
            assertTrue(response.indexOf("PreDestroyErrorServlet3") > 0);
            assertTrue(response.indexOf("PreDestroyErrorServlet3.printContext " + Constants.PRINTCONTEXT) > 0);
            assertTrue(response.indexOf("null") == -1);
            // check if the time stamp in response is after the beforeStart time.
            assertTrue(this.timeUtil.getTimeFromResponse(response) > beforeInstall);
            assertTrue(this.timeUtil.getTimeFromLog("PreDestroyErrorServlet2", "postConstruct") == 0);
        } finally {
            conn.disconnect();
        }
    }
    
    /*
     * test when war is uninstalled and servlets are destroyed, the pre-destroy
     * annotated methods are not called
     */
    public void testPreDestroyError004() throws Exception {
        testPreDestroyError001();
        testPreDestroyError002();
        testPreDestroyError003();
        uninstallWar();
        // TODO do we need to wait till the war is uninstalled properly?
        
        assertTrue(this.timeUtil.getTimeFromLog("PreDestroyErrorServlet1", "cleanup1") == 0);
        assertTrue(this.timeUtil.getTimeFromLog("PreDestroyErrorServlet1", "cleanup2") == 0);
        assertTrue(this.timeUtil.getTimeFromLog("PreDestroyErrorServlet2", "cleanup") == 0);
        assertTrue(this.timeUtil.getTimeFromLog("PreDestroyErrorServlet3", "cleanup") == 0);
        
    }
}

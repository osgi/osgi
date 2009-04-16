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

import java.net.HttpURLConnection;
import java.net.URL;

import org.osgi.test.cases.webcontainer.util.Constants;
import org.osgi.test.cases.webcontainer.util.Dispatcher;
import org.osgi.test.cases.webcontainer.util.Server;
import org.osgi.test.cases.webcontainer.util.TimeUtil;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class PostConstructAnnotationTest extends DefaultTestBundleControl {
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
        // beforeInstall = System.currentTimeMillis();
        beforeInstall = 0;
        // TODO install the war file

    }
    
    private void uninstallWar() throws Exception {
        // TODO uninstall the war file?
        
    }
    
    public void tearDown() throws Exception {
        uninstallWar();
    }
    
    
    /*
     * test @postConstruct annotated public method is called
     */
    public void testPostConstruct001() throws Exception {
        final String request = this.warContextPath + "/PostConstructPreDestroyServlet1";
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
            assertTrue(response.indexOf("PostConstructPreDestroyServlet1") > 0);
            assertTrue(response.indexOf("PostConstructPreDestroyServlet1.printContext " + Constants.PRINTCONTEXT) > 0);
            assertTrue(response.indexOf("null") == -1);
            // check if the time stamp in response is after the beforeStart time.
            assertTrue(this.timeUtil.getTimeFromResponse(response) > beforeInstall);
            assertTrue(this.timeUtil.getTimeFromLog("PostConstructPreDestroyServlet1", Constants.POSTCONSTRUCT) > beforeInstall);
        } finally {
            conn.disconnect();
        }
    }
    
    /*
     * test @postConstruct annotated private method is called
     */
    public void testPostConstruct002() throws Exception {
        final String request = this.warContextPath + "/PostConstructPreDestroyServlet2";
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
            assertTrue(response.indexOf("PostConstructPreDestroyServlet2") > 0);
            assertTrue(response.indexOf("PostConstructPreDestroyServlet2.printContext " + Constants.PRINTCONTEXT) > 0);
            assertTrue(response.indexOf("null") == -1);
            // check if the time stamp in response is after the beforeStart time.
            assertTrue(this.timeUtil.getTimeFromResponse(response) > beforeInstall);
            assertTrue(this.timeUtil.getTimeFromLog("PostConstructPreDestroyServlet2", Constants.POSTCONSTRUCT) > beforeInstall);
        } finally {
            conn.disconnect();
        }
    }
    
    /*
     * test @postConstruct annotated protected method is called
     */
    public void testPostConstruct003() throws Exception {
        final String request = this.warContextPath + "/PostConstructPreDestroyServlet3";
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
            assertTrue(response.indexOf("PostConstructPreDestroyServlet3") > 0);
            assertTrue(response.indexOf("PostConstructPreDestroyServlet3.printContext " + Constants.PRINTCONTEXT) > 0);
            assertTrue(response.indexOf("null") == -1);
            // check if the time stamp in response is after the beforeStart time.
            assertTrue(this.timeUtil.getTimeFromResponse(response) > beforeInstall);
            assertTrue(this.timeUtil.getTimeFromLog("PostConstructPreDestroyServlet3", Constants.POSTCONSTRUCT) > beforeInstall);
        } finally {
            conn.disconnect();
        }
    }
}

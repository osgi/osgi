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

import org.osgi.test.cases.webcontainer.util.Dispatcher;
import org.osgi.test.cases.webcontainer.util.Server;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class ResourceAnnotationTest extends DefaultTestBundleControl {
    // this test case assume war files are already installed for now
    Server server;
    boolean debug;
    String warContextPath;
    
    public void setUp() throws Exception {
        // install the war file?
        // can we get setUp and tearDown only run once through the test, instead of running each time before each test?
        this.server = new Server("localhost");
        this.debug = true;
        this.warContextPath = "/tw2";
        // TODO install war file
    }
    
    public void tearDown() throws Exception {
        // uninstall the war file?
    }
    
    /*
     *  Test single @Resource class-level and field based annotation/injection 
     *  with data type javax.sql.DataSource
     */
    public void testResource001() throws Exception {
        final String request = this.warContextPath  + "/ResourceServlet1";
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
            assertTrue(response.indexOf("ResourceServlet1") > 0);
            assertTrue(response.indexOf("Printing the injections in this ResourceServlet1 ...") > 0);
            assertTrue(response.indexOf("Done!") > 0);
            assertTrue(response.indexOf("null") == -1);
            assertTrue(response.indexOf("Error - unable to find name via @Resource") == -1);
        } finally {
            conn.disconnect();
        }
    }
    
    /*
     *  Test @Resource field based annotation/injection
     *  with data type Integer, String, Boolean
     */
    public void testResource002() throws Exception {
        final String request = this.warContextPath + "/ResourceServlet2";
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
            assertTrue(response.indexOf("ResourceServlet2") > 0);
            assertTrue(response.indexOf("Welcome String from env-entry!") > 0);
            assertTrue(response.indexOf("5 + 5 = 10 that is true") > 0);
            assertTrue(response.indexOf("null") == -1);
        } finally {
            conn.disconnect();
        }
    }
    
    /*
     *  Test @Resources class-level annotation/injection 
     *  @Resource field based annotation/injection
     *  with data type javax.sql.DataSource
     */
    public void testResource003() throws Exception {
        final String request = this.warContextPath + "/ResourceServlet3";
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
            assertTrue(response.indexOf("ResourceServlet3") > 0);
            assertTrue(response.indexOf("Printing the injections in this ResourceServlet3 ...") > 0);
            assertTrue(response.indexOf("Done!") > 0);
            assertTrue(response.indexOf("null") == -1);
            assertTrue(response.indexOf("Error - unable to find name via @Resource") == -1);
        } finally {
            conn.disconnect();
        }
    }
    
    /*
     *  Test @Resource setter based injection 
     *  with data type Integer, String, Boolean
     */
    public void testResource004() throws Exception {
        final String request = this.warContextPath + "/ResourceServlet4";
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
            assertTrue(response.indexOf("ResourceServlet4") > 0);
            assertTrue(response.indexOf("Welcome String from env-entry!") > 0);
            assertTrue(response.indexOf("5 + 5 = 10 that is true") > 0);
            assertTrue(response.indexOf("null") == -1);
        } finally {
            conn.disconnect();
        }
    }

}

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
import org.osgi.test.support.Base64Encoder;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/*
 * this test class is intended to test that annotations should work in container managed classes that
 * implement the following interfaces, in addition to the javax.servlet.Servlet interface:
 * javax.servlet.Filter 
 * javax.servlet.ServletContextListener 
 * javax.servlet.ServletContextAttributeListener 
 * javax.servlet.ServletRequestListener 
 * javax.servlet.ServletRequestAttributeListener 
 * javax.servlet.http.HttpSessionListener 
 * javax.servlet.http.HttpSessionAttributeListener
 */
public class SecurityAnnotationTest extends DefaultTestBundleControl {
    // this test case assume war files are already installed for now
    Server server;
    boolean debug;
    String warContextPath;
    long beforeInstall;

    public void setUp() throws Exception {
        // TODO if war file already exists, let's remove it first.

        this.server = new Server("localhost");
        this.debug = true;
        this.warContextPath = "/tw2";

        // capture a time before install
        // beforeInstall = System.currentTimeMillis();
        beforeInstall = 1;
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
     * javax.servlet.Filter are called, supplying the right username/password
     */
    public void testDeclareRolesAnnotation001() throws Exception {
        final String request = this.warContextPath + "/SecurityTestServlet";
        final URL url = Dispatcher.createURL(request, this.server);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        final String userName = "admin";
        final String password = "admin";

        try {
            if (conn.getResponseCode() == 401) {
                conn.disconnect();

                conn = (HttpURLConnection) url.openConnection();
                byte[] encodedPassword = (userName + ":" + password).getBytes();
                conn.setRequestProperty("Authorization", "Basic "
                        + new String(Base64Encoder.encode(encodedPassword)));
            }

            String response = Dispatcher.dispatch(conn);
            if (debug) {
                log(response);
            }
            assertTrue(conn.getResponseCode() == 200);
            assertTrue(conn.getContentType().equals("text/html"));

            // check if content of response is correct
            assertTrue(response.indexOf("SecurityTestServlet") > 0);
            assertTrue(response.indexOf(Constants.EMAIL + "-" + Constants.EMAILVALUE) > 0);
            assertTrue(response.indexOf(Constants.WELCOMESTRING + "-" + Constants.WELCOMESTRINGVALUE) > 0);
            assertTrue(response.indexOf(Constants.WELCOMESTATEMENT + "-" + Constants.WELCOMESTATEMENTVALUE) > 0);
            assertTrue(response.indexOf("null") == -1); 
        } finally {
            conn.disconnect();
        }
    }
    
    /*
     * test annotated methods/fields in container manager class that implement
     * javax.servlet.Filter are called, supplying the wrong password.
     */
    public void testDeclareRolesAnnotation002() throws Exception {
        final String request = this.warContextPath + "/SecurityTestServlet";
        final URL url = Dispatcher.createURL(request, this.server);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        final String userName = "admin";
        final String password = "admin1";

        try {
            if (conn.getResponseCode() == 401) {
                conn.disconnect();

                conn = (HttpURLConnection) url.openConnection();
                byte[] encodedPassword = (userName + ":" + password).getBytes();
                conn.setRequestProperty("Authorization", "Basic "
                        + new String(Base64Encoder.encode(encodedPassword)));
                assertTrue(conn.getResponseCode() == 401);

            }

            assertTrue(conn.getResponseCode() != 200); 
        } finally {
            conn.disconnect();
        }
    }
    
    /*
     * test annotated methods/fields in container manager class that implement
     * javax.servlet.Filter are called, supplying the wrong username and password.
     */
    public void testDeclareRolesAnnotation003() throws Exception {
        final String request = this.warContextPath + "/SecurityTestServlet";
        final URL url = Dispatcher.createURL(request, this.server);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        final String userName = "admin1";
        final String password = "admin1";

        try {
            if (conn.getResponseCode() == 401) {
                conn.disconnect();

                conn = (HttpURLConnection) url.openConnection();
                byte[] encodedPassword = (userName + ":" + password).getBytes();
                conn.setRequestProperty("Authorization", "Basic "
                        + new String(Base64Encoder.encode(encodedPassword)));
                assertTrue(conn.getResponseCode() == 401 );

            }

            assertTrue(conn.getResponseCode() != 200); 
        } finally {
            conn.disconnect();
        }
    }
    
    /*
     * test annotated methods/fields in container manager class that implement
     * javax.servlet.Filter are called, supplying the correct username and password, but inappropriate role
     */
    public void testDeclareRolesAnnotation004() throws Exception {
        final String request = this.warContextPath + "/SecurityTestServlet";
        final URL url = Dispatcher.createURL(request, this.server);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        final String userName = "tomcat";
        final String password = "tomcat";

        try {
            if (conn.getResponseCode() == 401) {
                conn.disconnect();

                conn = (HttpURLConnection) url.openConnection();
                byte[] encodedPassword = (userName + ":" + password).getBytes();
                conn.setRequestProperty("Authorization", "Basic "
                        + new String(Base64Encoder.encode(encodedPassword)));
                assertTrue(conn.getResponseCode() == 403);

            }

            assertTrue(conn.getResponseCode() != 200); 
        } finally {
            conn.disconnect();
        }
    }
    
    
}

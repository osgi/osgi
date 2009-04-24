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
import org.osgi.test.cases.webcontainer.util.Constants;
import org.osgi.test.cases.webcontainer.util.Dispatcher;
import org.osgi.test.cases.webcontainer.util.Server;
import org.osgi.test.support.Base64Encoder;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * @version $Rev$ $Date$
 */
public class SecurityAnnotationTest extends DefaultTestBundleControl {
    // this test case assume war files are already installed for now
    Server server;
    boolean debug;
    String warContextPath;
    long beforeInstall;
    Bundle b;

    public void setUp() throws Exception {
        // TODO if war file already exists, let's remove it first.

        this.server = new Server("localhost");
        this.debug = true;
        this.warContextPath = "/tw3";

        // capture a time before install
        // beforeInstall = System.currentTimeMillis();
        beforeInstall = 1;

        // install + start the war file
        log("install war file: tw2.war at context path " + this.warContextPath);
        b = installBundle(getWebServer()
                + "tw2.war", true);
    }

    private void uninstallWar() throws Exception {
        // uninstall the war file
        log("uninstall war file: tw2.war at context path " + this.warContextPath);
        uninstallBundle(b);
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
        // TODO allow user to specify username password at runtime or via
        // property file
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
            assertEquals(conn.getResponseCode(), 200);
            assertEquals(conn.getContentType(), "text/html");

            // check if content of response is correct
            log("verify content of response is correct");
            assertTrue(response.indexOf("SecurityTestServlet") > 0);
            assertTrue(response.indexOf(Constants.EMAIL + "-"
                    + Constants.EMAILVALUE) > 0);
            assertTrue(response.indexOf(Constants.WELCOMESTRING + "-"
                    + Constants.WELCOMESTRINGVALUE) > 0);
            assertTrue(response.indexOf(Constants.WELCOMESTATEMENT + "-"
                    + Constants.WELCOMESTATEMENTVALUE) > 0);
            assertEquals(response.indexOf("null"), -1);
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
                assertEquals(conn.getResponseCode(), 401);

            }

            assertTrue(conn.getResponseCode() != 200);
        } finally {
            conn.disconnect();
        }
    }

    /*
     * test annotated methods/fields in container manager class that implement
     * javax.servlet.Filter are called, supplying the wrong username and
     * password.
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
                assertEquals(conn.getResponseCode(), 401);

            }

            assertTrue(conn.getResponseCode() != 200);
        } finally {
            conn.disconnect();
        }
    }

    /*
     * test annotated methods/fields in container manager class that implement
     * javax.servlet.Filter are called, supplying the correct username and
     * password, but inappropriate role
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
                assertEquals(conn.getResponseCode(), 403);

            }

            assertTrue(conn.getResponseCode() != 200);
        } finally {
            conn.disconnect();
        }
    }

}

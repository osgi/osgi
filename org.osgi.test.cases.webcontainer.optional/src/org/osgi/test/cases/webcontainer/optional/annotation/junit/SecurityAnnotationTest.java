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

import java.net.HttpURLConnection;
import java.net.URL;

import org.osgi.framework.Constants;
import org.osgi.test.cases.webcontainer.optional.WebContainerOptionalTestBundleControl;
import org.osgi.test.cases.webcontainer.optional.util.ConstantsUtil;
import org.osgi.test.cases.webcontainer.util.Dispatcher;
import org.osgi.test.support.Base64Encoder;

/**
 * @version $Rev$ $Date$
 */
public class SecurityAnnotationTest extends WebContainerOptionalTestBundleControl {
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        super.prepare("/tw2");
        this.options.put(Constants.IMPORT_PACKAGE, IMPORTS_ANNOTATION);

        // install + start the war file
        log("install war file: tw2.war at context path " + this.warContextPath);
        this.b = installBundle(super.getWarURL("tw2.war", this.options), true);
        assertTrue("should be able to see the servlet context associated with /tw2 web contextpath",
                super.checkServiceRegistered("/tw2"));
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
            if (this.debug) {
                log(response);
            }
            assertEquals(conn.getResponseCode(), 200);
            assertEquals(conn.getContentType(), "text/html");

            // check if content of response is correct
            log("verify content of response is correct");
            assertTrue(response.indexOf("SecurityTestServlet") > 0);
            assertTrue(response.indexOf(ConstantsUtil.EMAIL + "-"
                    + ConstantsUtil.EMAILVALUE) > 0);
            assertTrue(response.indexOf(ConstantsUtil.WELCOMESTRING + "-"
                    + ConstantsUtil.WELCOMESTRINGVALUE) > 0);
            assertTrue(response.indexOf(ConstantsUtil.WELCOMESTATEMENT + "-"
                    + ConstantsUtil.WELCOMESTATEMENTVALUE) > 0);
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

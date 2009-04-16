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

import org.osgi.test.cases.webcontainer.util.Dispatcher;
import org.osgi.test.cases.webcontainer.util.Server;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * @version $Rev$ $Date$
 */
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

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
package org.osgi.test.cases.webcontainer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogReaderService;
import org.osgi.test.cases.webcontainer.util.Constants;
import org.osgi.test.cases.webcontainer.util.Dispatcher;
import org.osgi.test.cases.webcontainer.util.Server;
import org.osgi.test.cases.webcontainer.util.TimeUtil;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * @version $Rev$ $Date$
 * 
 *          test class for tw4
 */
public class TW4Test extends DefaultTestBundleControl {
    // this test case assume war files are already installed for now
    Server server;
    boolean debug;
    String warContextPath;
    long beforeInstall;
    TimeUtil timeUtil;

    public void setUp() throws Exception {
        // TODO if war file already exists, let's remove it first.

        this.server = new Server();
        this.debug = true;
        this.warContextPath = "/tw4";
        this.timeUtil = new TimeUtil(this.warContextPath);

        // TODO install the war file

    }

    private void uninstallWar() throws Exception {
        // TODO uninstall the war file

    }

    public void tearDown() throws Exception {
        uninstallWar();
    }

    /*
     * test empty doGet
     */
    public void testEmptyDoGet() throws Exception {
        final String request = this.warContextPath + "/TestServlet1";
        final URL url = Dispatcher.createURL(request, this.server);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            assertEquals(conn.getResponseCode(), 200);
            assertEquals(conn.getContentType(), null);
            String response = Dispatcher.dispatch(conn);
            if (debug) {
                log(response);
            }
            // check if content of response is empty
            assertEquals(response, "");
        } finally {
            conn.disconnect();
        }
    }

    /*
     * test long parameter values - borrow these two params from
     * org.osgi.test.cases.http
     */
    public void testLongParams() throws Exception {
        String param1 = "param1=value1";
        String param2 = "param2=abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";
        final String request = this.warContextPath
                + "/TestServlet1/TestServlet2?tc=1&" + param1 + "&" + param2;
        if (debug) {
            log(request);
        }
        final URL url = Dispatcher.createURL(request, this.server);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            assertEquals(conn.getResponseCode(), 200);
            assertEquals(conn.getContentType(), "text/html");
            String response = Dispatcher.dispatch(conn);
            if (debug) {
                log(response);
            }
            // check if content of response is correct
            assertEquals(response, Constants.TW4LONGPARAMS);
        } finally {
            conn.disconnect();
        }
    }

    /*
     * test a few special character param values - borrow these five params from
     * org.osgi.test.cases.http
     */
    public void testSpecialParams() throws Exception {
        String param1 = "param1=" + URLEncoder.encode("&", "UTF-8");
        String param2 = "param2=" + URLEncoder.encode("&&", "UTF-8");
        String param3 = "param3=" + URLEncoder.encode("%", "UTF-8");
        String param4 = "param4=" + URLEncoder.encode(" ", "UTF-8");
        String param5 = "param5=" + URLEncoder.encode("?", "UTF-8");
        final String request = this.warContextPath
                + "/TestServlet1/TestServlet2?tc=2&" + param1 + "&" + param2
                + "&" + param3 + "&" + param4 + "&" + param5;
        if (debug) {
            log(request);
        }
        final URL url = Dispatcher.createURL(request, this.server);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            assertEquals(conn.getResponseCode(), 200);
            assertEquals(conn.getContentType(), "text/html");
            String response = Dispatcher.dispatch(conn);
            if (debug) {
                log(response);
            }
            // check if content of response is correct
            assertEquals(response, Constants.TW4SPECPARAMS);
        } finally {
            conn.disconnect();
        }
    }

    /*
     * test large response 4M - borrow test from org.osgi.test.cases.http
     */
    public void testLargeResponse() throws Exception {
        final String request = this.warContextPath
                + "/TestServlet1/TestServlet2/TestServlet3";
        final URL url = Dispatcher.createURL(request, this.server);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            assertEquals(conn.getResponseCode(), 200);
            assertEquals(conn.getContentType(), "text/plain");
            BufferedReader in = null;
            int bufsize = 0;
            int len = 0;
            char[] buf = new char[1024];
            
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            assertNotNull(in);
            while ((len = in.read(buf, 0, buf.length)) > 0)
                bufsize += len;
            in.close();
            // check if content of response is correct
            log(bufsize + "");
            assertEquals(bufsize, 4194304);
        } finally {
            conn.disconnect();
        }
    }

    /*
     * test simple plain response
     */
    public void testPlainResponse() throws Exception {
        final String request = this.warContextPath
                + "/TestServlet1/TestServlet2/TestServlet3/TestServlet4?type=plain";
        final URL url = Dispatcher.createURL(request, this.server);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            assertEquals(conn.getResponseCode(), 200);
            assertEquals(conn.getContentType(), "text/plain");
            String response = Dispatcher.dispatch(conn);
            if (debug) {
                log(response);
            }
            // check if content of response is correct
            assertEquals(response, Constants.PLAINRESPONSE);
        } finally {
            conn.disconnect();
        }
    }

    /*
     * test simple html response
     */
    public void testHTMLResponse() throws Exception {
        final String request = this.warContextPath
                + "/TestServlet1/TestServlet2/TestServlet3/TestServlet4?type=html";
        final URL url = Dispatcher.createURL(request, this.server);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            assertEquals(conn.getResponseCode(), 200);
            assertEquals(conn.getContentType(), "text/html");
            String response = Dispatcher.dispatch(conn);
            if (debug) {
                log(response);
            }
            // check if content of response is correct
            assertEquals(response, Constants.HTMLRESPONSE);
        } finally {
            conn.disconnect();
        }
    }

    /*
     * test simple image/jpeg response
     */
    public void testJPGResponse() throws Exception {
        final String request = this.warContextPath
                + "/TestServlet1/TestServlet2/TestServlet3/TestServlet4?type=jpg";
        final URL url = Dispatcher.createURL(request, this.server);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            assertEquals(conn.getResponseCode(), 200);
            assertEquals(conn.getContentType(), "image/jpeg");
            String response = Dispatcher.dispatch(conn);
            if (debug) {
                log(response);
            }
        } finally {
            conn.disconnect();
        }
    }
}

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
package org.osgi.test.cases.webcontainer.junit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.jar.Manifest;

import org.osgi.framework.Bundle;
import org.osgi.test.cases.webcontainer.WebContainerTestBundleControl;
import org.osgi.test.cases.webcontainer.util.ConstantsUtil;
import org.osgi.test.cases.webcontainer.util.Dispatcher;
import org.osgi.test.cases.webcontainer.validate.BundleManifestValidator;

/**
 * @version $Rev$ $Date$
 * 
 *          test class for tw4
 */
public class TW4Test extends WebContainerTestBundleControl {
    Bundle b;
    
    public void setUp() throws Exception {
        super.setUp();
        super.prepare("/tw4");
        // install + start the war file
        log("install war file: tw4.war at context path " + this.warContextPath);
        String loc = super.getWarURL("tw4.war", this.options);
        if (this.debug) {
            log("bundleName to be passed into installBundle is " + loc);	
        }
        this.b = installBundle(loc, true);
    }

    private void uninstallWar() throws Exception {
        // uninstall the war file
        log("uninstall war file: tw4.war at context path "
                + this.warContextPath);
        uninstallBundle(this.b);
    }

    public void tearDown() throws Exception {
        uninstallWar();
    }

    /*
     * set deployOptions to null to rely on the web container service to
     * generate the manifest
     */
    public void testBundleManifest() throws Exception {
		Manifest originalManifest = super.getManifest("/tw4.war");
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
        super.checkTW4HomeResponse(response);
    }
    
    /*
     * test empty doGet
     */
    public void testEmptyDoGet() throws Exception {
        final String request = this.warContextPath + "/TestServlet1";
        final URL url = Dispatcher.createURL(request, this.server);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            assertEquals(200, conn.getResponseCode());
            assertEquals(null, conn.getContentType());
            String response = Dispatcher.dispatch(conn);
            if (this.debug) {
                log(response);
            }
            // check if content of response is empty
            log("verify content of response is correct");
            assertEquals("", response);
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
        String param2 = "param2=" + ConstantsUtil.PARAM2;
        final String request = this.warContextPath
                + "/TestServlet1/TestServlet2?tc=1&" + param1 + "&" + param2;
        String response = super.getResponse(request);
        // check if content of response is correct
        log("verify content of response is correct");
        assertEquals(ConstantsUtil.TW4LONGPARAMS, response);
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
        String response = super.getResponse(request);
        // check if content of response is correct
        log("verify content of response is correct");
        assertEquals(ConstantsUtil.TW4SPECPARAMS, response);
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
            assertEquals(200, conn.getResponseCode());
            assertEquals("text/plain", conn.getContentType());
            BufferedReader in = null;
            int bufsize = 0;
            int len = 0;
            char[] buf = new char[1024];

            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            assertNotNull(in);
            while ((len = in.read(buf, 0, buf.length)) > 0)
                bufsize += len;
            in.close();
            // check if content of response is correct
            log("verify content of response is correct");
            log(bufsize + "");
            assertEquals(4194304, bufsize);
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
            assertEquals(200, conn.getResponseCode());
            assertEquals("text/plain", conn.getContentType());
            String response = Dispatcher.dispatch(conn);
            if (this.debug) {
                log(response);
            }
            // check if content of response is correct
            log("verify content of response is correct");
            assertEquals(ConstantsUtil.PLAINRESPONSE, response);
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
        String response = super.getResponse(request);
        // check if content of response is correct
        log("verify content of response is correct");
        assertEquals(ConstantsUtil.HTMLRESPONSE, response);
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
            assertEquals(200, conn.getResponseCode());
            assertEquals("image/jpeg", conn.getContentType());
            String response = Dispatcher.dispatch(conn);
            if (this.debug) {
                log(response);
            }
        } finally {
            conn.disconnect();
        }
    }
}

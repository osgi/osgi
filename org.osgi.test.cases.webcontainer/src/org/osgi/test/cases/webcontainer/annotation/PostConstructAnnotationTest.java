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

import org.osgi.test.cases.webcontainer.util.Constants;
import org.osgi.test.cases.webcontainer.util.Dispatcher;
import org.osgi.test.cases.webcontainer.util.Server;
import org.osgi.test.cases.webcontainer.util.TimeUtil;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * @version $Rev$ $Date$
 */
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
            assertEquals(conn.getResponseCode(), 200);
            assertEquals(conn.getContentType(),"text/html");
            String response = Dispatcher.dispatch(conn);
            if (debug) {
                log(response);
            }
            // check if content of response is correct
            assertTrue(response.indexOf("PostConstructPreDestroyServlet1") > 0);
            assertTrue(response.indexOf("PostConstructPreDestroyServlet1.printContext " + Constants.PRINTCONTEXT) > 0);
            assertEquals(response.indexOf("null"), -1);
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
            assertEquals(conn.getResponseCode(), 200);
            assertEquals(conn.getContentType(),"text/html");
            String response = Dispatcher.dispatch(conn);
            if (debug) {
                log(response);
            }
            // check if content of response is correct
            assertTrue(response.indexOf("PostConstructPreDestroyServlet2") > 0);
            assertTrue(response.indexOf("PostConstructPreDestroyServlet2.printContext " + Constants.PRINTCONTEXT) > 0);
            assertEquals(response.indexOf("null"), -1);
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
            assertEquals(conn.getResponseCode(), 200);
            assertEquals(conn.getContentType(),"text/html");
            String response = Dispatcher.dispatch(conn);
            if (debug) {
                log(response);
            }
            // check if content of response is correct
            assertTrue(response.indexOf("PostConstructPreDestroyServlet3") > 0);
            assertTrue(response.indexOf("PostConstructPreDestroyServlet3.printContext " + Constants.PRINTCONTEXT) > 0);
            assertEquals(response.indexOf("null"), -1);
            // check if the time stamp in response is after the beforeStart time.
            assertTrue(this.timeUtil.getTimeFromResponse(response) > beforeInstall);
            assertTrue(this.timeUtil.getTimeFromLog("PostConstructPreDestroyServlet3", Constants.POSTCONSTRUCT) > beforeInstall);
        } finally {
            conn.disconnect();
        }
    }
}

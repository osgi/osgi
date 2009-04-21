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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

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
 * test class for tw1
 */
public class TW1Test extends DefaultTestBundleControl {
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
        this.warContextPath = "/tw1";
        this.timeUtil = new TimeUtil(this.warContextPath);
        
        // TODO install the war file
        
    }
    
    private void uninstallWar() throws Exception {
        // TODO uninstall the war file
        
    }
    
    public void tearDown() throws Exception {
        uninstallWar();
    }
    
    public void testBasic001() throws Exception {  
        final String request = this.warContextPath + "/";
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
            assertTrue(response.indexOf("TestWar1") > 0);
            assertTrue(response.indexOf("/BasicTest") > 0);
            assertTrue(response.indexOf("404.html (static link)") > 0);
            assertTrue(response.indexOf("404.html (through servlet.RequestDispatcher.forward())") > 0);
            assertTrue(response.indexOf("404.jsp (through servlet.RequestDispatcher.forward())") > 0);
            assertTrue(response.indexOf("Broken Link (for call ErrorPage)") > 0);
            assertTrue(response.indexOf("image.html") > 0);
        } finally {
            conn.disconnect();
        }
    }
    
    public void testBasic002() throws Exception {
        final String request = this.warContextPath + "/BasicTest";
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
            assertEquals(response, Constants.BASICTESTWAR1);
        } finally {
            conn.disconnect();
        }
    }
    
    public void testBasic003() throws Exception {
        final String request = this.warContextPath + "/404.html";
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
            assertEquals(response, Constants.ERROR404HTML);
        } finally {
            conn.disconnect();
        }
    }
    
    public void testBasic004() throws Exception {
        final String request = this.warContextPath + "/ErrorTest?target=html";
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
            assertEquals(response, Constants.ERROR404HTML);
        } finally {
            conn.disconnect();
        }
    }
    
    public void testBasic005() throws Exception {
        final String request = this.warContextPath + "/ErrorTest";
        final URL url = Dispatcher.createURL(request, this.server);
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        try {
            assertEquals(conn.getResponseCode(), 200);
            log(conn.getContentType());
            assertTrue(conn.getContentType().indexOf("text/html") > -1);
            String response = Dispatcher.dispatch(conn);
            if (debug) {
                log(response);
            }
            // check if content of response is correct
            assertEquals(response, Constants.ERROR404JSP);
        } finally {
            conn.disconnect();
        }
    }
    
    public void testBasic006() throws Exception {
        final String request = this.warContextPath + "/aaa";
        final URL url = Dispatcher.createURL(request, this.server);
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        try {
            assertEquals(conn.getResponseCode(), 404);
            assertTrue(conn.getContentType().indexOf("text/html") > -1);
            String response = Dispatcher.dispatch(conn);
            if (debug) {
                log(response);
            }
            fail("should be getting an exception");
        } catch (IOException e) {
            // ignore - this can be expected
        } finally {
            conn.disconnect();
        }
    }
    
    public void testBasic007() throws Exception {
        final String request = this.warContextPath + "/image.html";
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
            assertEquals(response, Constants.IMAGEHTML);
        } finally {
            conn.disconnect();
        }
    }
    
    public void testBasic008() throws Exception {
        final String request = this.warContextPath + "/images/osgi.gif";
        final URL url = Dispatcher.createURL(request, this.server);
        final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        try {
            if (debug) {
                log(conn.getContentType());
            }
            assertEquals(conn.getResponseCode(), 200);
            assertEquals(conn.getContentType(), "image/gif");
        } finally {
            conn.disconnect();
        }
    }
}

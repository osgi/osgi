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
import java.util.Properties;

import org.osgi.test.cases.webcontainer.util.Constants;
import org.osgi.test.cases.webcontainer.util.Dispatcher;
import org.osgi.test.cases.webcontainer.util.Server;
import org.osgi.test.cases.webcontainer.util.TimeUtil;
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
public class OtherAnnotationTest extends DefaultTestBundleControl {
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
        this.warContextPath = "/tw2";
        this.timeUtil = new TimeUtil(this.warContextPath);
        
        // capture a time before install
        //beforeInstall = System.currentTimeMillis();
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
     * javax.servlet.Filter are called
     */
    public void testFilter() throws Exception {
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
            assertTrue(this.timeUtil.getTimeFromLog("TestFilter", Constants.POSTCONSTRUCT) > beforeInstall);
            assertTrue(this.timeUtil.getTimeFromLog("TestFilter", "init") > beforeInstall);
            assertTrue(this.timeUtil.getTimeFromLog("TestFilter", "doFilter") > this.timeUtil.getTimeFromLog("TestFilter", "init") );
            assertTrue(this.timeUtil.getDespFromLog("TestFilter", "doFilter").equals(Constants.WELCOMESTRINGVALUE));
            assertTrue(this.timeUtil.getDespFromLog("TestFilter", "init").equals(Constants.WELCOMESTATEMENTVALUE));
            
            // TODO test preDestroy annotated method
        } finally {
            conn.disconnect();
        }
    }
    
    /*
     * test annotated methods/fields in container manager class that implement 
     * javax.servlet.ServletContextListener or
     * javax.servlet.ServletContextAttributeListener are called
     */
    public void testServletContext() throws Exception {
        String request = this.warContextPath + "/ServletContextListenerServlet";
        URL url = Dispatcher.createURL(request, this.server);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // add attributes
        try {
            assertTrue(conn.getResponseCode() == 200);
            assertTrue(conn.getContentType().equals("text/html"));
            String response = Dispatcher.dispatch(conn);
            if (debug) {
                log(response);
            }
            // check if content of response is correct
            assertTrue(response.indexOf("ServletContextListenerServlet") > 0);
            assertTrue(response.indexOf(Constants.EMAIL + "-" + Constants.EMAILVALUE) > 0);
            assertTrue(response.indexOf(Constants.WELCOMESTRING + "-" + Constants.WELCOMESTRINGVALUE) > 0);
            assertTrue(response.indexOf(Constants.WELCOMESTATEMENT + "-" + Constants.WELCOMESTATEMENTVALUE) > 0);
            assertTrue(response.indexOf("null") == -1);

            // check if annotated methods are invoked
            System.out.println("time is " + this.timeUtil.getTimeFromLog("ServletContextListenerServlet", Constants.POSTCONSTRUCT));
            assertTrue(this.timeUtil.getTimeFromLog("ServletContextListenerServlet", Constants.POSTCONSTRUCT) > beforeInstall);
            assertTrue(this.timeUtil.getTimeFromLog("TestServletContextListener", Constants.POSTCONSTRUCT) > beforeInstall);
            assertTrue(this.timeUtil.getTimeFromLog("TestServletContextAttributeListener", Constants.POSTCONSTRUCT) > beforeInstall);
            assertTrue(this.timeUtil.getDespFromLog("TestServletContextListener", "contextInitialized").equals(Constants.EMAIL + "-" + Constants.EMAILVALUE));
            assertTrue(this.timeUtil.getDespFromLog("TestServletContextAttributeListener", "attributeAdded").equals(Constants.WELCOMESTRING + "-" + Constants.WELCOMESTRINGVALUE));           
        } finally {
            conn.disconnect();
        }
        
        request = this.warContextPath + "/ServletContextListenerServlet?modifyAttribute=true";
        url = Dispatcher.createURL(request, this.server);
        conn = (HttpURLConnection)url.openConnection();
        // modify attributes
        try {
            assertTrue(conn.getResponseCode() == 200);
            assertTrue(conn.getContentType().equals("text/html"));
            String response = Dispatcher.dispatch(conn);
            if (debug) {
                log(response);
            }
            // check if content of response is correct
            assertTrue(response.indexOf("ServletContextListenerServlet") > 0);
            assertTrue(response.indexOf(Constants.EMAIL + "-" + Constants.EMAILVALUE) > 0);
            assertTrue(response.indexOf(Constants.WELCOMESTRING + "-" + "null") > 0);
            assertTrue(response.indexOf(Constants.WELCOMESTATEMENT + "-" + Constants.WELCOMESTATEMENTVALUE2) > 0);
            
            // check if methods are invoked
            assertTrue(this.timeUtil.getTimeFromLog("TestServletContextAttributeListener", "attributeRemoved") > beforeInstall);
            
            assertTrue(this.timeUtil.getDespFromLog("TestServletContextAttributeListener", "attributeReplaced").equals(Constants.WELCOMESTATEMENT + "-" + Constants.WELCOMESTATEMENTVALUE));           
        } finally {
            conn.disconnect();
        }
        
        request = this.warContextPath + "/ServletContextListenerServlet?modifyAttribute=reset";
        url = Dispatcher.createURL(request, this.server);
        conn = (HttpURLConnection)url.openConnection();
        // reset attributes back to the initial values
        try {
            assertTrue(conn.getResponseCode() == 200);
            assertTrue(conn.getContentType().equals("text/html"));
            String response = Dispatcher.dispatch(conn);
            if (debug) {
                log(response);
            }
            // check if content of response is correct
            assertTrue(response.indexOf("ServletContextListenerServlet") > 0);
            assertTrue(response.indexOf(Constants.EMAIL + "-" + Constants.EMAILVALUE) > 0);
            assertTrue(response.indexOf(Constants.WELCOMESTRING + "-" + Constants.WELCOMESTRINGVALUE) > 0);
            assertTrue(response.indexOf(Constants.WELCOMESTATEMENT + "-" + Constants.WELCOMESTATEMENTVALUE) > 0);
            
            // check if methods are invoked
            assertTrue(this.timeUtil.getDespFromLog("TestServletContextAttributeListener", "attributeAdded").equals(Constants.WELCOMESTRING + "-" + Constants.WELCOMESTRINGVALUE));           
            assertTrue(this.timeUtil.getDespFromLog("TestServletContextAttributeListener", "attributeReplaced").equals(Constants.WELCOMESTATEMENT + "-" + Constants.WELCOMESTATEMENTVALUE2));           
        } finally {
            conn.disconnect();
        }
    }
    
    /*
     * test annotated methods/fields in container manager class that implement 
     * javax.servlet.ServletRequestListener or
     * javax.servlet.ServletRequestAttributeListener are called
     */
    public void testServletRequest() throws Exception {
        String request = this.warContextPath + "/RequestListenerServlet";
        URL url = Dispatcher.createURL(request, this.server);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // add attributes
        try {
            assertTrue(conn.getResponseCode() == 200);
            assertTrue(conn.getContentType().equals("text/html"));
            String response = Dispatcher.dispatch(conn);
            if (debug) {
                log(response);
            }
            // check if content of response is correct
            assertTrue(response.indexOf("RequestListenerServlet") > 0);
            assertTrue(response.indexOf(Constants.WELCOMESTRING + "-" + Constants.WELCOMESTRINGVALUE) > 0);
            assertTrue(response.indexOf(Constants.WELCOMESTATEMENT + "-" + Constants.WELCOMESTATEMENTVALUE) > 0);
            assertTrue(response.indexOf("null") == -1);

            // check if annotated methods are invoked
            assertTrue(this.timeUtil.getTimeFromLog("RequestListenerServlet", Constants.POSTCONSTRUCT) > beforeInstall);
            assertTrue(this.timeUtil.getTimeFromLog("TestServletRequestListener", Constants.POSTCONSTRUCT) > beforeInstall);
            assertTrue(this.timeUtil.getTimeFromLog("TestServletRequestAttributeListener", Constants.POSTCONSTRUCT) > beforeInstall);
            assertTrue(this.timeUtil.getDespFromLog("TestServletRequestListener", "requestInitialized").equals(Constants.WELCOMESTRING + "-" + Constants.WELCOMESTRINGVALUE));
            assertTrue(this.timeUtil.getDespFromLog("TestServletRequestAttributeListener", "attributeAdded").equals(Constants.WELCOMESTRING + "-" + Constants.WELCOMESTRINGVALUE));    
            assertTrue(this.timeUtil.getTimeFromLog("TestServletRequestAttributeListener", "attributeRemoved") > beforeInstall); 
            assertTrue(this.timeUtil.getDespFromLog("TestServletRequestAttributeListener", "attributeReplaced").equals(Constants.WELCOMESTATEMENT + "-" + Constants.WELCOMESTATEMENTVALUE));    
        } finally {
            conn.disconnect();
        }
        
        conn = (HttpURLConnection)url.openConnection();
        // modify attributes
        try {
            assertTrue(conn.getResponseCode() == 200);
            assertTrue(conn.getContentType().equals("text/html"));
            String response = Dispatcher.dispatch(conn);
            if (debug) {
                log(response);
            }
            // check if content of response is correct
            assertTrue(response.indexOf("RequestListenerServlet") > 0);
            assertTrue(response.indexOf(Constants.WELCOMESTRING + "-" + Constants.WELCOMESTRINGVALUE) > 0);
            assertTrue(response.indexOf(Constants.WELCOMESTATEMENT + "-" + Constants.WELCOMESTATEMENTVALUE) > 0);
            assertTrue(response.indexOf("null") == -1);

            // check if annotated methods are invoked
            assertTrue(this.timeUtil.getTimeFromLog("RequestListenerServlet", Constants.POSTCONSTRUCT) > beforeInstall);
            assertTrue(this.timeUtil.getTimeFromLog("TestServletRequestListener", Constants.POSTCONSTRUCT) > beforeInstall);
            assertTrue(this.timeUtil.getTimeFromLog("TestServletRequestAttributeListener", Constants.POSTCONSTRUCT) > beforeInstall);
            assertTrue(this.timeUtil.getDespFromLog("TestServletRequestListener", "requestInitialized").equals(Constants.WELCOMESTRING + "-" + Constants.WELCOMESTRINGVALUE));
            assertTrue(this.timeUtil.getDespFromLog("TestServletRequestAttributeListener", "attributeAdded").equals(Constants.WELCOMESTRING + "-" + Constants.WELCOMESTRINGVALUE));    
            assertTrue(this.timeUtil.getTimeFromLog("TestServletRequestAttributeListener", "attributeRemoved") > beforeInstall); 
            assertTrue(this.timeUtil.getDespFromLog("TestServletRequestAttributeListener", "attributeReplaced").equals(Constants.WELCOMESTATEMENT + "-" + Constants.WELCOMESTATEMENTVALUE));    
        } finally {
            conn.disconnect();
        }
    }
    
    /*
     * test annotated methods/fields in container manager class that implement 
     * javax.servlet.ServletRequestListener or
     * javax.servlet.ServletRequestAttributeListener are called
     */
    public void testHTTPSession() throws Exception {
        String request = this.warContextPath + "/HTTPSessionListenerServlet";
        URL url = Dispatcher.createURL(request, this.server);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        
        // add attributes
        try {
            assertTrue(conn.getResponseCode() == 200);
            assertTrue(conn.getContentType().equals("text/html"));
            String response = Dispatcher.dispatch(conn);
            if (debug) {
                log(response);
            }
            // check if content of response is correct
            assertTrue(response.indexOf("HTTPSessionListenerServlet") > 0);
            assertTrue(response.indexOf(Constants.WELCOMESTRING + "-" + Constants.WELCOMESTRINGVALUE) > 0);
            assertTrue(response.indexOf(Constants.WELCOMESTATEMENT + "-" + Constants.WELCOMESTATEMENTVALUE) > 0);
            assertTrue(response.indexOf("null") == -1);

            // check if annotated methods are invoked
            assertTrue(this.timeUtil.getTimeFromLog("HTTPSessionListenerServlet", Constants.POSTCONSTRUCT) > beforeInstall);
            assertTrue(this.timeUtil.getTimeFromLog("TestHttpSessionListener", Constants.POSTCONSTRUCT) > beforeInstall);
            assertTrue(this.timeUtil.getTimeFromLog("TestHttpSessionAttributeListener", Constants.POSTCONSTRUCT) > beforeInstall);
            assertTrue(this.timeUtil.getDespFromLog("TestHttpSessionListener", "sessionCreated").equals(Constants.WELCOMESTRING + "-" + Constants.WELCOMESTRINGVALUE));
            assertTrue(this.timeUtil.getDespFromLog("TestHttpSessionAttributeListener", "attributeAdded").equals(Constants.WELCOMESTRING + "-" + Constants.WELCOMESTRINGVALUE));    
            assertTrue(this.timeUtil.getTimeFromLog("TestHttpSessionAttributeListener", "attributeRemoved") > beforeInstall); 
            assertTrue(this.timeUtil.getDespFromLog("TestHttpSessionAttributeListener", "attributeReplaced").equals(Constants.WELCOMESTATEMENT + "-" + Constants.WELCOMESTATEMENTVALUE));    
        } finally {
            conn.disconnect();
        }
        
        conn = (HttpURLConnection)url.openConnection();
        try {
            assertTrue(conn.getResponseCode() == 200);
            assertTrue(conn.getContentType().equals("text/html"));
            String response = Dispatcher.dispatch(conn);
            if (debug) {
                log(response);
            }
            // check if content of response is correct
            assertTrue(response.indexOf("HTTPSessionListenerServlet") > 0);
            assertTrue(response.indexOf(Constants.WELCOMESTRING + "-" + Constants.WELCOMESTRINGVALUE) > 0);
            assertTrue(response.indexOf(Constants.WELCOMESTATEMENT + "-" + Constants.WELCOMESTATEMENTVALUE) > 0);
            assertTrue(response.indexOf("null") == -1);

            // check if annotated methods are invoked
            assertTrue(this.timeUtil.getTimeFromLog("HTTPSessionListenerServlet", Constants.POSTCONSTRUCT) > beforeInstall);
            assertTrue(this.timeUtil.getTimeFromLog("TestHttpSessionListener", Constants.POSTCONSTRUCT) > beforeInstall);
            assertTrue(this.timeUtil.getTimeFromLog("TestHttpSessionAttributeListener", Constants.POSTCONSTRUCT) > beforeInstall);
            assertTrue(this.timeUtil.getDespFromLog("TestHttpSessionListener", "sessionCreated").equals(Constants.WELCOMESTRING + "-" + Constants.WELCOMESTRINGVALUE));
            assertTrue(this.timeUtil.getDespFromLog("TestHttpSessionAttributeListener", "attributeAdded").equals(Constants.WELCOMESTRING + "-" + Constants.WELCOMESTRINGVALUE));    
            assertTrue(this.timeUtil.getTimeFromLog("TestHttpSessionAttributeListener", "attributeRemoved") > beforeInstall); 
            assertTrue(this.timeUtil.getDespFromLog("TestHttpSessionAttributeListener", "attributeReplaced").equals(Constants.WELCOMESTATEMENT + "-" + Constants.WELCOMESTATEMENTVALUE)); 
        } finally {
            conn.disconnect();
        }
    }

    /*
     * test @PreDestroy annotated classes are called in container manager classes that implement the following interfaces:
     *  * javax.servlet.Filter 
     * javax.servlet.ServletContextListener 
     * javax.servlet.ServletContextAttributeListener 
     * javax.servlet.ServletRequestListener 
     * javax.servlet.ServletRequestAttributeListener 
     * javax.servlet.http.HttpSessionListener 
     * javax.servlet.http.HttpSessionAttributeListener
     */
    public void testPreDestroyOther() throws Exception {
        // capture the beforeUninstall time
        long beforeUninstall = System.currentTimeMillis();
        uninstallWar();
        
        assertTrue(this.timeUtil.getTimeFromLog("TestFilter", Constants.PREDESTROY) > beforeUninstall);
        assertTrue(this.timeUtil.getTimeFromLog("ServletContextListenerServlet", Constants.PREDESTROY) > beforeUninstall);
        assertTrue(this.timeUtil.getTimeFromLog("TestServletContextListener", Constants.PREDESTROY) > beforeUninstall);
        assertTrue(this.timeUtil.getTimeFromLog("TestServletContextAttributeListener", Constants.PREDESTROY) > beforeUninstall);
        assertTrue(this.timeUtil.getTimeFromLog("RequestListenerServlet", Constants.PREDESTROY) > beforeUninstall);
        assertTrue(this.timeUtil.getTimeFromLog("TestServletRequestListener", Constants.PREDESTROY) > beforeUninstall);
        assertTrue(this.timeUtil.getTimeFromLog("TestServletRequestAttributeListener", Constants.PREDESTROY) > beforeUninstall);
        assertTrue(this.timeUtil.getTimeFromLog("HTTPSessionListenerServlet", Constants.PREDESTROY) > beforeUninstall);
        assertTrue(this.timeUtil.getTimeFromLog("TestHttpSessionListener", Constants.PREDESTROY) > beforeUninstall);
        assertTrue(this.timeUtil.getTimeFromLog("TestHttpSessionAttributeListener", Constants.PREDESTROY) > beforeUninstall);
    }
}

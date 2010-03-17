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

import java.util.Enumeration;
import java.util.jar.Manifest;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;
import org.osgi.test.cases.webcontainer.util.ConstantsUtil;
import org.osgi.test.cases.webcontainer.util.WebContainerTestBundleControl;
import org.osgi.test.cases.webcontainer.util.validate.BundleManifestValidator;

/**
 * @version $Rev$ $Date$
 * 
 *          tw8 is same test war as tw5, except that tw8.war doesn't contain web.xml, 
 *          and the web.xml is provided by fragment instead
 */
public class TW8Test extends WebContainerTestBundleControl {
    LogReaderService logReaderService;
    private static final String TW8_SYMBOLIC_NAME = "org.osgi.test.cases.webcontainer.tw8";

    private Bundle fragmentBundle;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        super.prepare("/tw8");

        // install the war file.
        log("install war file: tw8.war at context path " + this.warContextPath);
        this.options.put(Constants.IMPORT_PACKAGE, IMPORTS_OSGI_FRAMEWORK);
        this.options.put(Constants.BUNDLE_SYMBOLICNAME, TW8_SYMBOLIC_NAME);
        String loc = super.getWarURL("tw8.war", this.options);
        if (this.debug) {
            log("bundleName to be passed into installBundle is " + loc);	
        }
        this.b = installBundle(loc, false);
        
        fragmentBundle = super.installBundle("fragment.tw8.jar", false);
        
        // start the war file
        this.b.start();
        
        ServiceReference logReaderServiceReference = getContext()
                .getServiceReference(LogReaderService.class.getName());
        this.logReaderService = (LogReaderService) getContext().getService(
                logReaderServiceReference);
        
        // make sure we don't run tests until the servletcontext is registered with service registry
        boolean register = super.checkServiceRegistered(this.warContextPath);
        assertTrue("the ServletContext should be registered", register);
    }

    public void tearDown() throws Exception {
        uninstallBundle(fragmentBundle);
        super.tearDown();
    }
    
    /*
     * set deployOptions to null to rely on the web container service to
     * generate the manifest
     */
    public void testBundleManifest() throws Exception {
		Manifest originalManifest = super.getManifest("/tw8.war");
        BundleManifestValidator validator = new BundleManifestValidator(this.b,
                originalManifest, this.options, this.debug);
        validator.validate();
    }
    
    public void testLog001() throws Exception {
        long beforeLog = System.currentTimeMillis();

        final String request = this.warContextPath
                + "/BundleContextTestServlet";
        String response = super.getResponse(request);
        // check if content of response is correct
        log("verify content of response is correct");
        assertTrue(response.indexOf("BundleContextTestServlet") > 0);
        assertTrue(response.indexOf(ConstantsUtil.TESTLOGMSG) > 0);
        assertEquals(-1, response.indexOf("null"));

        Enumeration e = logReaderService.getLog();
        
        // let's check all the logs in case there is some other code writes to the log
        boolean checked = false;
        while (e.hasMoreElements()) {
            LogEntry logentry = (LogEntry) e.nextElement();
            String message = logentry.getMessage();
            log("get log message: " + message);

            if (message.equals(ConstantsUtil.TESTLOGMSG)) {
                assertEquals(TW8_SYMBOLIC_NAME, logentry.getBundle().getSymbolicName());
                assertEquals(LogService.LOG_ERROR, logentry.getLevel());
                assertTrue(logentry.getTime() >= beforeLog);
                assertTrue(logentry.getTime() <= System.currentTimeMillis());
                checked = true;
                break;
            }

        }
        
        assertTrue("log entries are compared and checked", checked);
    }

    public void testLog002() throws Exception {
        long beforeLog = System.currentTimeMillis();

        final String request = this.warContextPath
                + "/BundleContextTestServlet?log=2";
        String response = super.getResponse(request);
        // check if content of response is correct
        log("verify content of response is correct");
        assertTrue(response.indexOf("BundleContextTestServlet") > 0);
        assertTrue(response.indexOf(ConstantsUtil.TESTLOGMSG2) > 0);
        assertEquals(-1, response.indexOf("null"));

        Enumeration e = logReaderService.getLog();
        // let's check all the logs in case there is some other code writes to the log
        boolean checked = false;
        while (e.hasMoreElements()) {
            LogEntry logentry = (LogEntry) e.nextElement();
            String message = logentry.getMessage();
            log("get log message: " + message);
            if (message.equals(ConstantsUtil.TESTLOGMSG2)) {
                assertEquals(TW8_SYMBOLIC_NAME, logentry.getBundle().getSymbolicName());
                assertEquals(LogService.LOG_WARNING, logentry.getLevel());
                assertTrue(logentry.getTime() >= beforeLog);
                assertTrue(logentry.getTime() <= System.currentTimeMillis());
                checked = true;
                break;
            }

        }
        
        assertTrue("log entries are compared and checked", checked);
    }

    public void testLog003() throws Exception {
        long beforeLog = System.currentTimeMillis();

        final String request = this.warContextPath
                + "/BundleContextTestServlet?log=3";
        String response = super.getResponse(request);

        // check if content of response is correct
        log("verify content of response is correct");
        assertTrue(response.indexOf("BundleContextTestServlet") > 0);
        assertTrue(response.indexOf(ConstantsUtil.TESTLOGMSG3) > 0);
        assertEquals(-1, response.indexOf("null"));

        Enumeration e = logReaderService.getLog();
        // let's check all the logs in case there is some other code writes to the log
        boolean checked = false;
        while (e.hasMoreElements()) {
            LogEntry logentry = (LogEntry) e.nextElement();
            String message = logentry.getMessage();
            log("get log message: " + message);
            if (message.equals(ConstantsUtil.TESTLOGMSG3)) {
                assertEquals(TW8_SYMBOLIC_NAME, logentry.getBundle().getSymbolicName());
                assertEquals(LogService.LOG_INFO, logentry.getLevel());
                assertTrue(logentry.getTime() >= beforeLog);
                assertTrue(logentry.getTime() <= System.currentTimeMillis());
                checked = true;
                break;
            }
        }
        
        assertTrue("log entries are compared and checked", checked);
    }

    public void testLog004() throws Exception {
        long beforeLog = System.currentTimeMillis();

        final String request = this.warContextPath
                + "/BundleContextTestServlet?log=4";
        String response = super.getResponse(request);

        // check if content of response is correct
        log("verify content of response is correct");
        assertTrue(response.indexOf("BundleContextTestServlet") > 0);
        assertTrue(response.indexOf(ConstantsUtil.TESTLOGMSG4) > 0);
        assertEquals(-1, response.indexOf("null"));

        Enumeration e = logReaderService.getLog();
        // let's check all the logs in case there is some other code writes to the log
        boolean checked = false;
        while (e.hasMoreElements()) {
            LogEntry logentry = (LogEntry) e.nextElement();
            String message = logentry.getMessage();
            log("get log message: " + message);
            if (message.equals(ConstantsUtil.TESTLOGMSG4)) {
                assertEquals(TW8_SYMBOLIC_NAME, logentry.getBundle().getSymbolicName());
                assertEquals(LogService.LOG_DEBUG, logentry.getLevel());
                assertTrue(logentry.getTime() >= beforeLog);
                assertTrue(logentry.getTime() <= System.currentTimeMillis());
                assertEquals(logentry.getException().toString(), new RuntimeException().toString());
                checked = true;
                break;
            }
        }
        
        assertTrue("log entries are compared and checked", checked);
    }
    
    /*
     * test ClasspathTestServlet
     */
    public void testClasspassServlet() throws Exception {
        final String request = this.warContextPath
        + "/ClasspathTestServlet";
        String response = super.getResponse(request);
        assertEquals("checking response content", "<html><head><title>ClasspathTestServlet</title></head><body>" 
                + ConstantsUtil.ABLEGETLOG + "<br/>" +  ConstantsUtil.ABLEGETSIMPLEHELLO + "<br/></body></html>", response);
    }
    
}

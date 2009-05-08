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

import java.util.Enumeration;
import java.util.jar.Manifest;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;
import org.osgi.test.cases.webcontainer.util.ConstantsUtil;
import org.osgi.test.cases.webcontainer.validate.BundleManifestValidator;

/**
 * @version $Rev$ $Date$
 * 
 *          test able to access BundleContext from ServletContext and use the
 *          OSGi log service.
 */
public class AccessBundleContextTest extends WebContainerTestBundleControl {
    Bundle b;
    LogReaderService logReaderService;

    public void setUp() throws Exception {
        super.setUp();
        super.prepare("/tw5");

        // install + start the war file
        log("install war file: tw5.war at context path " + this.warContextPath);
        this.b = installBundle(super.getWarURL("tw5.war", this.options), true);

        ServiceReference logReaderServiceReference = getContext()
                .getServiceReference(LogReaderService.class.getName());
        this.logReaderService = (LogReaderService) getContext().getService(
                logReaderServiceReference);
    }

    private void uninstallWar() throws Exception {
        // uninstall the war file
        log("uninstall war file: tw5.war at context path "
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
        Manifest originalManifest = super.getManifest("/resources/tw5/tw5.war");
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
        super.checkTW5HomeResponse(response);
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
        assertEquals(response.indexOf("null"), -1);

        Enumeration e = logReaderService.getLog();
        while (e.hasMoreElements()) {
            LogEntry logentry = (LogEntry) e.nextElement();
            log("get log message: " + logentry.getMessage());
            assertEquals(logentry.getMessage(), ConstantsUtil.TESTLOGMSG);
            assertEquals(logentry.getBundle(), this.b.getBundleContext());
            assertEquals(logentry.getLevel(), LogService.LOG_ERROR);
            assertTrue(logentry.getTime() >= beforeLog);
            assertTrue(logentry.getTime() <= System.currentTimeMillis());
            break;
        }
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
        assertEquals(response.indexOf("null"), -1);

        Enumeration e = logReaderService.getLog();
        while (e.hasMoreElements()) {
            LogEntry logentry = (LogEntry) e.nextElement();
            log("get log message: " + logentry.getMessage());
            assertEquals(logentry.getMessage(), ConstantsUtil.TESTLOGMSG2);
            assertEquals(logentry.getBundle(), this.b.getBundleContext());
            assertEquals(logentry.getLevel(), LogService.LOG_WARNING);
            assertTrue(logentry.getTime() >= beforeLog);
            assertTrue(logentry.getTime() <= System.currentTimeMillis());
            break;
        }
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
        assertEquals(response.indexOf("null"), -1);

        Enumeration e = logReaderService.getLog();
        while (e.hasMoreElements()) {
            LogEntry logentry = (LogEntry) e.nextElement();
            log("get log message: " + logentry.getMessage());
            assertEquals(logentry.getMessage(), ConstantsUtil.TESTLOGMSG3);
            assertEquals(logentry.getBundle(), this.b.getBundleContext());
            assertEquals(logentry.getLevel(), LogService.LOG_INFO);
            assertTrue(logentry.getTime() >= beforeLog);
            assertTrue(logentry.getTime() <= System.currentTimeMillis());
            break;
        }
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
        assertEquals(response.indexOf("null"), -1);

        Enumeration e = logReaderService.getLog();
        while (e.hasMoreElements()) {
            LogEntry logentry = (LogEntry) e.nextElement();
            log("get log message: " + logentry.getMessage());
            assertEquals(logentry.getMessage(), ConstantsUtil.TESTLOGMSG4);
            assertEquals(logentry.getBundle(), this.b.getBundleContext());
            assertEquals(logentry.getLevel(), LogService.LOG_DEBUG);
            assertTrue(logentry.getTime() >= beforeLog);
            assertTrue(logentry.getTime() <= System.currentTimeMillis());
            assertEquals(logentry.getException(), new RuntimeException());
            break;
        }
    }
    
    /*
     * test ClasspathTestServlet
     */
    public void testClasspassServlet() throws Exception {
        final String request = this.warContextPath
        + "/ClasspathTestServlet";
        String response = super.getResponse(request);
        assertEquals("checking response content", response,"<html><head><title>ClasspathTestServlet</title></head><body>" 
                + ConstantsUtil.ABLEGETLOG + "<br/>" +  ConstantsUtil.ABLEGETSIMPLEHELLO + "<br/></body></html>");
    }
}

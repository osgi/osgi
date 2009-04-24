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

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;
import org.osgi.test.cases.webcontainer.util.Constants;
import org.osgi.test.cases.webcontainer.util.Dispatcher;
import org.osgi.test.cases.webcontainer.util.Server;
import org.osgi.test.cases.webcontainer.util.TimeUtil;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * @version $Rev$ $Date$
 * 
 *          test able to access BundleContext from ServletContext and use the
 *          OSGi log service.
 */
public class AccessBundleContextTest extends DefaultTestBundleControl {
    // this test case assume war files are already installed for now
    Server server;
    boolean debug;
    String warContextPath;
    long beforeInstall;
    TimeUtil timeUtil;
    Bundle b;
    
    LogReaderService logReaderService;

    public void setUp() throws Exception {
        // TODO if war file already exists, let's remove it first.

        this.server = new Server();
        this.debug = true;
        this.warContextPath = "/tw5";
        this.timeUtil = new TimeUtil(this.warContextPath);

        // capture a time before install
        beforeInstall = System.currentTimeMillis();
        // install + start the war file
        log("install war file: tw5.war at context path " + this.warContextPath);
        b = installBundle(getWebServer()
                + "tw5.war", true);

        ServiceReference logReaderServiceReference = getContext()
                .getServiceReference(LogReaderService.class.getName());
        logReaderService = (LogReaderService) getContext().getService(
                logReaderServiceReference);
    }

    private void uninstallWar() throws Exception {
        // uninstall the war file
        log("uninstall war file: tw5.war at context path " + this.warContextPath);
        uninstallBundle(b);
    }

    public void tearDown() throws Exception {
        uninstallWar();
    }

    public void testLog001() throws Exception {
        long beforeLog = System.currentTimeMillis();

        final String request = this.warContextPath
                + "/BundleContextTestServlet";
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
            log("verify content of response is correct");
            assertTrue(response.indexOf("BundleContextTestServlet") > 0);
            assertTrue(response.indexOf(Constants.TESTLOGMSG) > 0);
            assertEquals(response.indexOf("null"), -1);
        } finally {
            conn.disconnect();
        }

        Enumeration e = logReaderService.getLog();
        while (e.hasMoreElements()) {
            LogEntry logentry = (LogEntry) e.nextElement();
            log("get log message: " + logentry.getMessage());
            assertEquals(logentry.getMessage(), Constants.TESTLOGMSG);
            // TODO get Bundle via OSGI RFC 66 WebApplication getBundle() API,
            // then use Bundle.getBundleContext() to get BundleContext
            assertEquals(logentry.getBundle(), getContext());
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
            log("verify content of response is correct");
            assertTrue(response.indexOf("BundleContextTestServlet") > 0);
            assertTrue(response.indexOf(Constants.TESTLOGMSG2) > 0);
            assertEquals(response.indexOf("null"), -1);
        } finally {
            conn.disconnect();
        }

        Enumeration e = logReaderService.getLog();
        while (e.hasMoreElements()) {
            LogEntry logentry = (LogEntry) e.nextElement();
            log("get log message: " + logentry.getMessage());
            assertEquals(logentry.getMessage(), Constants.TESTLOGMSG2);
            // TODO get Bundle via ISGI WebApplication getBundle(),
            // then use Bundle.getBundleContext() to get BundleContext
            assertEquals(logentry.getBundle(), getContext());
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
            log("verify content of response is correct");
            assertTrue(response.indexOf("BundleContextTestServlet") > 0);
            assertTrue(response.indexOf(Constants.TESTLOGMSG3) > 0);
            assertEquals(response.indexOf("null"), -1);
        } finally {
            conn.disconnect();
        }

        Enumeration e = logReaderService.getLog();
        while (e.hasMoreElements()) {
            LogEntry logentry = (LogEntry) e.nextElement();
            log("get log message: " + logentry.getMessage());
            assertEquals(logentry.getMessage(), Constants.TESTLOGMSG3);
            // TODO get Bundle via ISGI WebApplication getBundle(),
            // then use Bundle.getBundleContext() to get BundleContext
            assertEquals(logentry.getBundle(), getContext());
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
            log("verify content of response is correct");
            assertTrue(response.indexOf("BundleContextTestServlet") > 0);
            assertTrue(response.indexOf(Constants.TESTLOGMSG4) > 0);
            assertEquals(response.indexOf("null"), -1);
        } finally {
            conn.disconnect();
        }

        Enumeration e = logReaderService.getLog();
        while (e.hasMoreElements()) {
            LogEntry logentry = (LogEntry) e.nextElement();
            log("get log message: " + logentry.getMessage());
            assertEquals(logentry.getMessage(), Constants.TESTLOGMSG4);
            // TODO get Bundle via ISGI WebApplication getBundle(),
            // then use Bundle.getBundleContext() to get BundleContext
            assertEquals(logentry.getBundle(), getContext());
            assertEquals(logentry.getLevel(), LogService.LOG_DEBUG);
            assertTrue(logentry.getTime() >= beforeLog);
            assertTrue(logentry.getTime() <= System.currentTimeMillis());
            assertEquals(logentry.getException(), new RuntimeException());
            break;
        }
    }
}

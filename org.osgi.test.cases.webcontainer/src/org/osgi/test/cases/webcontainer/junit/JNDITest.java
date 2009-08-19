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

import javax.naming.InitialContext;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;
import org.osgi.test.cases.webcontainer.WebContainerTestBundleControl;
import org.osgi.test.cases.webcontainer.util.ConstantsUtil;

/**
 * @version $Rev$ $Date$
 * 
 *          JNDITest to verify integration between RFC 66 and RFC 142
 */
public class JNDITest extends WebContainerTestBundleControl {
    Bundle b;

    public void setUp() throws Exception {
        super.setUp();
        super.prepare("/tw5");

        // install + start the war file
        log("install war file: tw5.war at context path " + this.warContextPath);
        String loc = super.getWarURL("t51.war", this.options);
        if (this.debug) {
            log("bundleName to be passed into installBundle is " + loc);	
        }
        this.b = installBundle(loc, true);

        // verify JNDI in OSGi service is installed
        log("verify JNDI in OSGi service is installed.  The tests in this class require JNDI in OSGi being installed.");
        log("check if there is any JNDI service provider that is registered as an OSGi service under  the javax.naming.spi.InitialContextFactory interface");
        ServiceReference sr = getContext().getServiceReference(
                InitialContextFactory.class.getName());
        if (sr != null) {
            assertNotNull("InitialContextFactory should not be null",
                    (InitialContextFactory) getContext().getService(sr));
        } else {
            log("check if there is any JNDI service provider that is registered as an OSGi service under  the javax.naming.spi.InitialContextFactoryBuilder interface");
            sr = getContext().getServiceReference(
                    InitialContextFactoryBuilder.class.getName());
            assertNotNull("sr should not be null", sr);
            assertNotNull("InitialContextFactoryBuilder should not be null",
                    (InitialContextFactoryBuilder) getContext().getService(sr));
        }

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

    // simple JNDI lookup test with the need to have rfc 66 to 
    // make sure things works
    public void testLookupLogService() throws Exception {
        ClassLoader cl = getClass().getClassLoader();
        assertTrue(
                "Current ClassLoader should be implementing BundleReference",
                cl instanceof BundleReference);
        Thread.currentThread().setContextClassLoader(cl);
        InitialContext context = new InitialContext();
        LogService logService = (LogService) context
                .lookup("osgi:services/org.osgi.service.log");
        assertNotNull(logService);
        logService.log(LogService.LOG_INFO,
                "able to obtain logService via JNDI lookup");
    }

    public void testLookupLogServiceFromServlet() throws Exception {
        long beforeLog = System.currentTimeMillis();

        final String request = this.warContextPath + "/JNDITestServlet";
        String response = super.getResponse(request);
        // check if content of response is correct
        log("verify content of response is correct");
        assertTrue(response.indexOf("JNDITestServlet") > 0);
        assertTrue(response.indexOf(ConstantsUtil.TESTLOGMSG) > 0);
        assertEquals("should not get this", -1, response.indexOf("unable to lookup logService via JNDI"));

        ServiceReference logReaderServiceReference = getContext()
                .getServiceReference(LogReaderService.class.getName());
        LogReaderService logReaderService = (LogReaderService) getContext()
                .getService(logReaderServiceReference);
        Enumeration e = logReaderService.getLog();
        while (e.hasMoreElements()) {
            LogEntry logentry = (LogEntry) e.nextElement();
            log("get log message: " + logentry.getMessage());
            assertEquals(ConstantsUtil.TESTLOGMSG, logentry.getMessage());
            assertEquals(this.b.getBundleContext(), logentry.getBundle());
            assertEquals(LogService.LOG_ERROR, logentry.getLevel());
            assertTrue(logentry.getTime() >= beforeLog);
            assertTrue(logentry.getTime() <= System.currentTimeMillis());
            break;
        }
    }

}

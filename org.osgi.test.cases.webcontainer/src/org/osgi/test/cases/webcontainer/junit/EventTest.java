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

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.osgi.service.event.Event;
import org.osgi.test.cases.webcontainer.WebContainerTestBundleControl;
import org.osgi.test.cases.webcontainer.handler.ExtenderTestEventHandler;

/**
 * @version $Rev$ $Date$
 * 
 *          EventTest to verify Events are published by the web extender
 *          bundle at certain situations defined by rfc 66
 */
public class EventTest extends WebContainerTestBundleControl {
    Bundle b;
    Bundle eventhandler;
    private final int WAITCOUNT = 5;

    public void setUp() throws Exception {
        super.setUp();
        super.prepare("/tw1");
        this.options.put(Constants.BUNDLE_SYMBOLICNAME, "org.osgi.test.cases.webcontainer.tw1");

        // install the war file
        log("install war file: tw1.war at context path " + this.warContextPath);
        this.b = installBundle(super.getWarURL("tw1.war", this.options), false);
        this.eventhandler = getContext().installBundle(
                getWebServer() + "eventHandler.jar");
        this.eventhandler.start();
    }

    private void uninstallWar() throws Exception {
        // uninstall the war file
        log("uninstall war file: tw1.war at context path "
                + this.warContextPath);
        uninstallBundle(this.b);
    }

    public void tearDown() throws Exception {
        uninstallWar();
        this.eventhandler.uninstall();
    }
    
    public void testEvent001() throws Exception {
        ExtenderTestEventHandler.clearEvents();
        assertNull(ExtenderTestEventHandler.getPreviousEvent());
        assertNull(ExtenderTestEventHandler.getCurrentEvent());
        
        this.b.start();
        
        // expect emit of the following events:
        // org/osgi/service/web/STARTING Ð the web extender has spotted the web application bundle and is starting it. 
        // org/osgi/service/web/STARTED Ð the web extender has finished starting the web application bundle. Formatted: Bullets and Numbering 
        // wait a few seconds to make sure events are delivered.
        int count = 0;
        while(ExtenderTestEventHandler.getPreviousEvent() == null && count < WAITCOUNT) {
            Thread.sleep(1000);
            count++;
        }

        Event eventPrevious = ExtenderTestEventHandler.getPreviousEvent();
        Event eventCurrent = ExtenderTestEventHandler.getCurrentEvent();
        assertNotNull(eventPrevious);
        assertNotNull(eventCurrent);
        
        long startingTime = Long.parseLong((String)eventPrevious.getProperty("timestamp"));
        long startedTime = Long.parseLong((String)eventCurrent.getProperty("timestamp"));
        
        assertEquals(eventPrevious.getTopic(), "org/osgi/service/web/STARTING");
        assertEquals((String)eventPrevious.getProperty("bundle.symbolicName"), "org.osgi.test.cases.webcontainer.tw1");
        assertEquals(Long.parseLong((String) eventPrevious.getProperty("bundle.id")), this.b.getBundleId());
        assertEquals((Bundle)eventPrevious.getProperty("bundle"), this.b);
        assertEquals((Version)eventPrevious.getProperty("bundle.version"), this.b.getVersion());
        assertNotNull(startingTime);
        assertNotNull((Bundle)eventPrevious.getProperty("extender.bundle"));
        assertNotNull(Long.parseLong((String)eventPrevious.getProperty("extender.bundle.id")));
        assertNotNull((String)eventPrevious.getProperty("extender.bundle.symbolicName"));
        assertNotNull((Version)eventPrevious.getProperty("extender.bundle.version"));
        
        assertEquals(eventCurrent.getTopic(), "org/osgi/service/web/STARTED");
        assertEquals((String)eventCurrent.getProperty("bundle.symbolicName"), "org.osgi.test.cases.webcontainer.tw1");
        assertEquals(Long.parseLong((String) eventCurrent.getProperty("bundle.id")), this.b.getBundleId());
        assertEquals((Bundle)eventCurrent.getProperty("bundle"), this.b);
        assertEquals((Version)eventCurrent.getProperty("bundle.version"), this.b.getVersion());
        assertNotNull(startedTime);
        assertNotNull((Bundle)eventCurrent.getProperty("extender.bundle"));
        assertNotNull(Long.parseLong((String)eventCurrent.getProperty("extender.bundle.id")));
        assertNotNull((String)eventCurrent.getProperty("extender.bundle.symbolicName"));
        assertNotNull((Version)eventCurrent.getProperty("extender.bundle.version"));

        // the extender information should be the same

        assertTrue(startedTime >= startingTime);
        assertEquals(eventCurrent.getProperty("extender.bundle"), eventPrevious.getProperty("extender.bundle"));
        assertEquals(eventCurrent.getProperty("extender.bundle.id"), eventPrevious.getProperty("extender.bundle.id"));
        assertEquals(eventCurrent.getProperty("extender.bundle.symbolicName"), eventPrevious.getProperty("extender.bundle.symbolicName"));
        assertEquals(eventCurrent.getProperty("extender.bundle.version"), eventPrevious.getProperty("extender.bundle.version"));

        eventPrevious = null;
        eventCurrent = null;
        ExtenderTestEventHandler.clearEvents();
        
        
        this.b.stop();
        // emit the following events:
        // org/osgi/service/web/STOPPING Ð the web extender is stopping the web application bundle. 
        // org/osgi/service/web/STOPPED Ð a web extender has stopped the web application bundle. 
        // wait a few seconds to make sure events are delivered.
        count = 0;
        while(ExtenderTestEventHandler.getPreviousEvent() == null && count < WAITCOUNT) {
            Thread.sleep(1000);
            count++;
        }
        eventPrevious = ExtenderTestEventHandler.getPreviousEvent();
        eventCurrent = ExtenderTestEventHandler.getCurrentEvent();
        assertNotNull(eventPrevious);
        assertNotNull(eventCurrent);
        assertEquals(eventPrevious.getTopic(), "org/osgi/service/web/STOPPING");
        assertEquals(eventCurrent.getTopic(), "org/osgi/service/web/STOPPED");
        assertTrue(Long.parseLong((String)eventPrevious.getProperty("timestamp")) >= startedTime);
        assertTrue(Long.parseLong((String)eventCurrent.getProperty("timestamp")) >= Long.parseLong((String)eventPrevious.getProperty("timestamp")));
        
        ExtenderTestEventHandler.clearEvents();
    }
    
    public void testEvent002() throws Exception {
        // start the bundle again and try deploy another bundle that cause a failure
        this.b.start();
        // wait a few seconds to make sure events are delivered.
        int count = 0;
        while(ExtenderTestEventHandler.getPreviousEvent() == null && count < WAITCOUNT) {
            Thread.sleep(1000);
            count++;
        }
        
        ExtenderTestEventHandler.clearEvents();
        assertNull(ExtenderTestEventHandler.getPreviousEvent());
        assertNull(ExtenderTestEventHandler.getCurrentEvent());
        
        Map options = new HashMap();
        options.put(WEB_CONTEXT_PATH, "/tw1");
        options.put(Constants.BUNDLE_SYMBOLICNAME, "org.osgi.test.cases.webcontainer.tw2");
        
        // install the war file that uses the same WebContextPath
        log("install war file: tw2.war at context path /tw1");
        Bundle b2 = installBundle(super.getWarURL("tw2.war", options), false);
        
        b2.start();
        // emit the following events:
        // org/osgi/service/web/STARTING Ð the web extender has spotted the web application bundle and is starting it.
        // org/osgi/service/web/FAILED - a web extender cannot start the bundle, this will be fired after a STARTING 
        // event has been fired if the bundle cannot be started for any reason. 
        // wait a few seconds to make sure events are delivered.
        count = 0;
        while(ExtenderTestEventHandler.getPreviousEvent() == null && count < WAITCOUNT) {
            Thread.sleep(1000);
            count++;
        }
        Event eventPrevious = ExtenderTestEventHandler.getPreviousEvent();
        Event eventCurrent = ExtenderTestEventHandler.getCurrentEvent();
        assertNotNull(eventPrevious);
        assertNotNull(eventCurrent);
        
        long startingTime = Long.parseLong((String)eventPrevious.getProperty("timestamp"));
        long failedTime = Long.parseLong((String)eventCurrent.getProperty("timestamp"));
        
        assertEquals(eventPrevious.getTopic(), "org/osgi/service/web/STARTING");
        assertEquals((String)eventPrevious.getProperty("bundle.symbolicName"), "org.osgi.test.cases.webcontainer.tw1");
        assertEquals(Long.parseLong((String) eventPrevious.getProperty("bundle.id")), this.b.getBundleId());
        assertEquals((Bundle)eventPrevious.getProperty("bundle"), this.b);
        assertEquals((Version)eventPrevious.getProperty("bundle.version"), this.b.getVersion());
        assertNotNull(startingTime);
        assertNotNull((Bundle)eventPrevious.getProperty("extender.bundle"));
        assertNotNull(Long.parseLong((String)eventPrevious.getProperty("extender.bundle.id")));
        assertNotNull((String)eventPrevious.getProperty("extender.bundle.symbolicName"));
        assertNotNull((Version)eventPrevious.getProperty("extender.bundle.version"));
        
        assertEquals(eventCurrent.getTopic(), "org/osgi/service/web/FAILED");
        assertEquals((String)eventCurrent.getProperty("bundle.symbolicName"), "org.osgi.test.cases.webcontainer.tw1");
        assertEquals(Long.parseLong((String) eventCurrent.getProperty("bundle.id")), this.b.getBundleId());
        assertEquals((Bundle)eventCurrent.getProperty("bundle"), this.b);
        assertEquals((Version)eventCurrent.getProperty("bundle.version"), this.b.getVersion());
        assertNotNull(failedTime);
        assertNotNull((Bundle)eventCurrent.getProperty("extender.bundle"));
        assertNotNull(Long.parseLong((String)eventCurrent.getProperty("extender.bundle.id")));
        assertNotNull((String)eventCurrent.getProperty("extender.bundle.symbolicName"));
        assertNotNull((Version)eventCurrent.getProperty("extender.bundle.version"));
        assertNotNull((Throwable)eventCurrent.getProperty("exception"));
        
        // the extender information should be the same
        assertTrue(failedTime >= startingTime);
        assertEquals(eventCurrent.getProperty("extender.bundle"), eventPrevious.getProperty("extender.bundle"));
        assertEquals(eventCurrent.getProperty("extender.bundle.id"), eventPrevious.getProperty("extender.bundle.id"));
        assertEquals(eventCurrent.getProperty("extender.bundle.symbolicName"), eventPrevious.getProperty("extender.bundle.symbolicName"));
        assertEquals(eventCurrent.getProperty("extender.bundle.version"), eventPrevious.getProperty("extender.bundle.version"));

        ExtenderTestEventHandler.clearEvents();
        
        b2.uninstall();
        // should not emit any events
        assertNull(ExtenderTestEventHandler.getPreviousEvent());
        assertNull(ExtenderTestEventHandler.getCurrentEvent());
    }
}

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
package org.osgi.test.cases.webcontainer.handler;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.test.cases.webcontainer.util.EventFactory;

/**
 * @version $Rev$ $Date$
 * 
 *          TestEventHandler to verify Events are published by the web extender
 *          bundle at certain situations defined by rfc 66
 */
public class ExtenderTestEventHandler implements EventHandler, BundleActivator {

    final static String[] topics = new String[] {
            "org/osgi/service/web/DEPLOYING",
            "org/osgi/service/web/DEPLOYED",
            "org/osgi/service/web/UNDEPLOYING",
            "org/osgi/service/web/UNDEPLOYED",
            "org/osgi/service/web/FAILED" };

    public void start(BundleContext context) {
        Dictionary d = new Hashtable();
        d.put(EventConstants.EVENT_TOPIC, topics);
        d.put(EventConstants.EVENT_FILTER, "(bundle.symbolicName=org.osgi.test.cases.webcontainer.tw*)");
        context.registerService(EventHandler.class.getName(), this, d);
    }

    public void stop(BundleContext context) throws Exception {
    }

    public void handleEvent(Event event) {
        // let's record the event
    	EventFactory.registerEvent(event);
    	System.out.println("event factory size is " + EventFactory.getEventSize());
    	System.out.println("handled event is " + event.toString());
    }

}

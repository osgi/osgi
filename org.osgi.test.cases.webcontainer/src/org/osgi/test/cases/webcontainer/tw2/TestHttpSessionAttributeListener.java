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

package org.osgi.test.cases.webcontainer.tw2;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

import org.osgi.test.cases.webcontainer.util.Constants;
import org.osgi.test.cases.webcontainer.util.Event;
import org.osgi.test.cases.webcontainer.util.EventLogger;

/**
 * @version $Rev$ $Date$
 */
public class TestHttpSessionAttributeListener implements
        HttpSessionAttributeListener {

    @PostConstruct
    public void postConstruct() {
        EventLogger.logEvent(new Event(this.getClass().getName(), Constants.POSTCONSTRUCT, ""));
    }

    @PreDestroy
    public void preDestroy() {
        EventLogger.logEvent(new Event(this.getClass().getName(), Constants.PREDESTROY, ""));
    }
    
    public void attributeAdded(HttpSessionBindingEvent se) {
        EventLogger.logEvent(new Event(this.getClass().getName(), "attributeAdded", se.getName() + "-" + se.getValue()));
    }

    public void attributeRemoved(HttpSessionBindingEvent se) {
        EventLogger.logEvent(new Event(this.getClass().getName(), "attributeRemoved", se.getName()));
    }

    public void attributeReplaced(HttpSessionBindingEvent se) {
        EventLogger.logEvent(new Event(this.getClass().getName(), "attributeReplaced", se.getName() + "-" + se.getValue()));
    }

}

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
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;

import org.osgi.test.cases.webcontainer.util.ConstantsUtil;
import org.osgi.test.cases.webcontainer.util.Event;
import org.osgi.test.cases.webcontainer.util.EventLogger;

/**
 * @version $Rev$ $Date$
 */
public class TestServletContextAttributeListener implements
        ServletContextAttributeListener {

    @PostConstruct
    public void postConstruct() {
        EventLogger.logEvent(new Event(this.getClass().getName(),
                ConstantsUtil.POSTCONSTRUCT, ""));
    }

    @PreDestroy
    public void preDestroy() {
        EventLogger.logEvent(new Event(this.getClass().getName(),
                ConstantsUtil.PREDESTROY, ""));
    }

    public void attributeAdded(ServletContextAttributeEvent scab) {
        EventLogger.logEvent(new Event(this.getClass().getName(),
                "attributeAdded", scab.getName() + "-" + scab.getValue()));
    }

    public void attributeRemoved(ServletContextAttributeEvent scab) {
        EventLogger.logEvent(new Event(this.getClass().getName(),
                "attributeRemoved", scab.getName()));

    }

    /*
     * If the attribute was replaced, ServletContextAttributeEvent.getValue()
     * returns the old value of the attribute.
     */
    public void attributeReplaced(ServletContextAttributeEvent scab) {
        EventLogger.logEvent(new Event(this.getClass().getName(),
                "attributeReplaced", scab.getName() + "-" + scab.getValue()));
    }
}

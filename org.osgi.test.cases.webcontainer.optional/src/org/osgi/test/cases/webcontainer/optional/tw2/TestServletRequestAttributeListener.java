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

package org.osgi.test.cases.webcontainer.optional.tw2;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;

import org.osgi.test.cases.webcontainer.optional.util.ConstantsUtil;
import org.osgi.test.cases.webcontainer.optional.util.Event;
import org.osgi.test.cases.webcontainer.optional.util.EventLogger;

/**
 * @version $Rev$ $Date$
 */
public class TestServletRequestAttributeListener implements
        ServletRequestAttributeListener {

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

    public void attributeAdded(ServletRequestAttributeEvent srae) {
        EventLogger.logEvent(new Event(this.getClass().getName(),
                "attributeAdded", srae.getName() + "-" + srae.getValue()));
    }

    public void attributeRemoved(ServletRequestAttributeEvent srae) {
        EventLogger.logEvent(new Event(this.getClass().getName(),
                "attributeRemoved", srae.getName()));
    }

    public void attributeReplaced(ServletRequestAttributeEvent srae) {
        EventLogger.logEvent(new Event(this.getClass().getName(),
                "attributeReplaced", srae.getName() + "-" + srae.getValue()));
    }

}

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
import javax.annotation.Resource;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import org.osgi.test.cases.webcontainer.optional.util.ConstantsUtil;
import org.osgi.test.cases.webcontainer.optional.util.Event;
import org.osgi.test.cases.webcontainer.optional.util.EventLogger;

/**
 * @version $Rev$ $Date$
 */
public class TestServletRequestListener implements ServletRequestListener {

    @Resource(name = "someString1")
    private String someString1;

    @Resource(name = "someString2")
    private String someString2;

    @Resource(name = "someInteger1")
    private Integer someInteger1;

    @Resource(name = "someInteger2")
    private Integer someInteger2;

    @Resource(name = "someInteger3")
    private Integer someInteger3;

    @Resource(name = "someBoolean1")
    private Boolean someBoolean1;

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

    public void requestDestroyed(ServletRequestEvent sre) {
        EventLogger.logEvent(new Event(this.getClass().getName(),
                "requestDestroyed", ""));
    }

    public void requestInitialized(ServletRequestEvent sre) {
        if (someInteger1 == null && someInteger2 == null
                && someBoolean1 == null) {
            sre.getServletRequest().setAttribute(ConstantsUtil.WELCOMESTATEMENT,
                    null);
        } else {
            sre.getServletRequest().setAttribute(
                    ConstantsUtil.WELCOMESTATEMENT,
                    someInteger1 + " + " + someInteger2 + " = " + someInteger3
                            + " is " + someBoolean1);
        }
        if (someString1 == null && someString2 == null) {
            sre.getServletRequest().setAttribute(ConstantsUtil.WELCOMESTRING, null);
        } else {
            sre.getServletRequest().setAttribute(ConstantsUtil.WELCOMESTRING,
                    someString1 + " " + someString2);
        }
        EventLogger.logEvent(new Event(this.getClass().getName(),
                "requestInitialized", ConstantsUtil.WELCOMESTRING
                        + "-"
                        + sre.getServletRequest().getAttribute(
                                ConstantsUtil.WELCOMESTRING)));
    }
}

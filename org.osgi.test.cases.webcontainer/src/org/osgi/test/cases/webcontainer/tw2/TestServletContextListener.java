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
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.osgi.test.cases.webcontainer.util.Constants;
import org.osgi.test.cases.webcontainer.util.Event;
import org.osgi.test.cases.webcontainer.util.EventLogger;

/**
 * @version $Rev$ $Date$
 */
public class TestServletContextListener implements ServletContextListener {

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
                Constants.POSTCONSTRUCT, ""));
    }

    @PreDestroy
    public void preDestroy() {
        EventLogger.logEvent(new Event(this.getClass().getName(),
                Constants.PREDESTROY, ""));
    }

    public void contextDestroyed(ServletContextEvent sce) {
        EventLogger.logEvent(new Event(this.getClass().getName(),
                "contextDestroyed", ""));
    }

    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        String email = context.getInitParameter("Email");
        context.setAttribute(Constants.EMAIL, email);
        if (someInteger1 == null && someInteger2 == null
                && someBoolean1 == null) {
            context.setAttribute(Constants.WELCOMESTATEMENT, null);
        } else {
            context
                    .setAttribute(Constants.WELCOMESTATEMENT, someInteger1
                            + "+" + someInteger2 + "=" + someInteger3 + " is "
                            + someBoolean1);
        }
        if (someString1 == null && someString2 == null) {
            context.setAttribute(Constants.WELCOMESTRING, null);
        } else {
            context.setAttribute(Constants.WELCOMESTRING, someString1 + " "
                    + someString2);
        }
        EventLogger.logEvent(new Event(this.getClass().getName(),
                "contextInitialized", Constants.EMAIL + "-"
                        + context.getInitParameter("Email")));
    }

}

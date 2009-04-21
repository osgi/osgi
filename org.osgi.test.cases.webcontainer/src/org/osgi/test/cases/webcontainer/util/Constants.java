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

package org.osgi.test.cases.webcontainer.util;

/**
 * @version $Rev$ $Date$
 */
public class Constants {

    // tw2 related
    public static final String CLEANUPDESP = "PreDestroy annotated method cleanup is invoked";
    public static final String CLEANUPDESP2 = "PreDestroy annotated method cleanup2 is invoked";
    public static final String POSTCONSTRUCTDESP = "PostConstruct annotated method postConstruct is invoked";
    public static final String POSTCONSTRUCTDESP2 = "PostConstruct annotated method postConstruct2 is invoked";
    public static final String PRINTCONTEXT = "method is invoked";

    public static final String LOGFILE = "webcontainer-event.properties";

    public static final String EMAIL = "Email";
    public static final String WELCOMESTRING = "WelcomeString";
    public static final String WELCOMESTATEMENT = "WelcomeStatement";

    public static final String EMAILVALUE = "eeg@osgi.org";
    public static final String WELCOMESTRINGVALUE = "Welcome String from env-entry!";
    public static final String WELCOMESTATEMENTVALUE = "5+5=10 is true";
    public static final String WELCOMESTATEMENTVALUE2 = "5+5=10 is not false";

    public static final String PREDESTROY = "preDestroy";
    public static final String POSTCONSTRUCT = "postConstruct";

    // tw5 related
    public static final String OSGIBUNDLECONTEXT = "osgi-bundlecontext";
    public static final String TESTLOGMSG = "test log ERROR by BundleContextTestServlet";
    public static final String TESTLOGMSG2 = "test log WARNING by BundleContextTestServlet";
    public static final String TESTLOGMSG3 = "test log INFO by BundleContextTestServlet";
    public static final String TESTLOGMSG4 = "test log DEBUG by BundleContextTestServlet";

    // tw1 related
    public static final String BASICTESTWAR1 = "<html><head><title>Basic Test War</title></head>"
            + "<body><h1>Basic Test War</h1></body></html>";
    public static final String ERROR404HTML = "<HTML><HEAD><TITLE>404.html</TITLE></HEAD>"
            + "<BODY><P>Error: 404</P><P>Customized Error Page Test - HTML page</P></BODY></HTML>";
    public static final String ERROR404JSP = "<html><head><title>404.jsp</title></head><body>"
            + "<P>Error: 404</P><P>Customized Error Page Test - JSP page</P></body></html>";
    public static final String IMAGEHTML = "<html><head><title>TestWar1 Image</title></head>"
            + "<body>This is the OSGi Alliance logo <img src=\"images/osgi.gif\" alt=\"OSGi Alliance\"/></html>";

    // tw4 related
    public static final String TW4BASIC = "<html><head><title>TestWar4-TestServlet</title></head>";
    public static final String TW4LONGPARAMS = TW4BASIC
            + "<body>param1: value1<br/> "
            + "param2: abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz<br/>"
            + "</body></html>";
    public static final String TW4SPECPARAMS = TW4BASIC
            + "<body>param1: &<br/>param2: &&<br/>param3: %<br/>param4:  <br/>param5: ?<br/></body></html>";
    public static final String PLAINRESPONSE = "TestWar4-TestServlet type: text";
    public static final String HTMLRESPONSE = TW4BASIC
            + "<body>type: html</body></html>";
}

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
}

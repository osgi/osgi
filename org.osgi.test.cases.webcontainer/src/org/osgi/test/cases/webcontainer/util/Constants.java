/*
 * $Id$
 *
 * Copyright (c) The OSGi Alliance (2009). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.test.cases.webcontainer.util;

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

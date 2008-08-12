/*
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
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
 * 
 */

/*
 * REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 01/09/2005   Alexandre Santos
 * 153          [MEGTCK][APP] Implement OAT test cases
 * ===========  ==============================================================
 */
package org.osgi.test.cases.application.tbc;

import org.osgi.service.application.ApplicationAdminPermission;


public class ApplicationConstants {  
    
	public static final String INVALID = ";`´$.,@";
    public static final String ACTIONS_INVALID_FORMAT = ApplicationAdminPermission.LIFECYCLE_ACTION
    + "&"
    + ApplicationAdminPermission.LOCK_ACTION
    + "&"
    + ApplicationAdminPermission.SCHEDULE_ACTION;
    public static final String ACTIONS = ApplicationAdminPermission.LIFECYCLE_ACTION
    + "," + ApplicationAdminPermission.LOCK_ACTION;
    public static final String ACTIONS_COMPLETE = ApplicationAdminPermission.SCHEDULE_ACTION
    + "," + ApplicationAdminPermission.LOCK_ACTION;
    public static final String ACTIONS_DIFFERENT_ORDER = ApplicationAdminPermission.LOCK_ACTION
    + "," + ApplicationAdminPermission.LIFECYCLE_ACTION;    
    public static final String XML_INEXISTENT = "unknown";
    public static final String XML_APP = "app";
    public static final String XML_LOG = "log";
    public static final String XML_REG = "reg";
    public static final String XML_ACTIVATOR = "activator";
    public static final String APPLICATION_LOCATION = "application.location";
    
    public static final String APP_VERSION = "1.0.0";
    public static final String APP_ICON = "/TestIcon.gif";    
    public static String TEST_PID = "";
    public static final String TEST_PID2 = "MIDlet: TestApplication-2.0.0-Cesar test MIDlet";
    public static final int TIMEOUT;
    public static final int SHORT_TIMEOUT;
    public final static String TIMER_EVENT = "org/osgi/application/timer";
    public final static String TOPIC_EVENT = "org/cesar/topic";
    public static final String EVENT_FILTER = "(minute=*)";
    public static final String INVALID_FILTER = "&[minute=1;}";
    public static final String APPLICATION_NAME = "TestApplication";
    public static final String OSGI = System.getProperty("info.dmtree.osgi.root");
    public static final String OSGI_APPLICATION = OSGI + "/Application";
    public static final String OSGI_APPLICATION_VERSION = "1.0";   
    public static final String OSGI_APPLICATION_PACKAGE = "org.osgi.test.cases.application.tb1";
    public static String OSGI_APPLICATION_APPID = "";
    public static String OSGI_APPLICATION_APPID_VALID = "";
    public static String OSGI_APPLICATION_APPID_NAME = "";
    public static String OSGI_APPLICATION_APPID_APPLICATION_ID = "";
    public static String OSGI_APPLICATION_APPID_ICONURI = "";
    public static String OSGI_APPLICATION_APPID_VENDOR = "";
    public static String OSGI_APPLICATION_APPID_VERSION = "";
    public static String OSGI_APPLICATION_APPID_LOCKED = "";
    public static String OSGI_APPLICATION_APPID_CONTAINERID = "";
    public static String OSGI_APPLICATION_APPID_PACKAGEID = "";
    public static String OSGI_APPLICATION_APPID_LOCATION = "";
    public static String OSGI_APPLICATION_APPID_EXT = "";
    public static String OSGI_APPLICATION_APPID_OPERATIONS = "";
    public static String OSGI_APPLICATION_APPID_SCHEDULES = "";
    public static String OSGI_APPLICATION_APPID_OPERATIONS_LOCK = "";
    public static String OSGI_APPLICATION_APPID_OPERATIONS_UNLOCK = "";
    public static String OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH = "";
    public static String OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID = "";
    public static String OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT = "";
    public static String OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_INSTANCEID = "";
    public static String OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_STATUS = "";
    public static String OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_MESSAGE = "";
    public static String OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS = "";
    public static String OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS_ID = "";
    public static String OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS_ID_NAME = "";
    public static String OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS_ID_VALUE = "";
    public static String OSGI_APPLICATION_APPID_INSTANCES = "";
    public static String OSGI_APPLICATION_APPID_INSTANCES_ID = "";
    public static String OSGI_APPLICATION_APPID_INSTANCES_ID_STATE = "";
    public static String OSGI_APPLICATION_APPID_INSTANCES_ID_INSTANCEID = "";
    public static String OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS = "";
    public static String OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS_STOP = "";
    public static String OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS_EXT = "";
    public static String OSGI_APPLICATION_APPID_SCHEDULES_ID = "";
    public static String OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS = "";
    public static String OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS_ID = "";
    public static String OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS_ID_NAME = "";
    public static String OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS_ID_VALUE = "";
    public static String OSGI_APPLICATION_APPID_SCHEDULES_ID_ENABLED = "";
    public static String OSGI_APPLICATION_APPID_SCHEDULES_ID_TOPICFILTER = "";
    public static String OSGI_APPLICATION_APPID_SCHEDULES_ID_EVENTFILTER = "";
    public static String OSGI_APPLICATION_APPID_SCHEDULES_ID_RECURRING = "";
    
    public static final String SIGNER_FILTER = "CN=John Smith, O=ACME Inc, OU=ACME Cert Authority, L=Austin, S=Texas, C=US";
    public static final String SIGNER_FILTER2 = "CN=Different Cert, O=Test Inc, OU=Test Cert Authority, L=Recife, S=Pernambuco, C=BR";
    public static final String SIGNER_FILTER_WILDCARD = "CN=John*, O=ACME Inc, OU=ACME Cert Authority, L=Austin, S=Texas, C=US";
    public static final String SIGNER_FILTER_INVALID1 = "2=*.ORG.BR, TX=CESAR, R=MOTOROLA, L=RECIFE, C=BR";
	
	public static String APPLICATION_PERMISSION_FILTER1 = "(&(signer="+SIGNER_FILTER+")"+"(pid="+TEST_PID+"))";
	public static String APPLICATION_PERMISSION_FILTER2 = "(&(signer="+SIGNER_FILTER_WILDCARD+")"+"(pid="+TEST_PID+"))";
	public static String APPLICATION_PERMISSION_FILTER_DIFFERENT_PID = "(&(signer="+SIGNER_FILTER+")"+"(pid="+TEST_PID2+"))";
	public static String APPLICATION_PERMISSION_FILTER_DIFFERENT_SIGNER = "(&(signer="+SIGNER_FILTER2+")"+"(pid="+TEST_PID+"))";
	public static String APPLICATION_PERMISSION_FILTER_INVALID1 = "(&!!!dfs#"+SIGNER_FILTER_INVALID1+")"+"(pid="+TEST_PID+")]";
	
	
    static {
    	if (System.getProperty("SHORT_TIMEOUT")!=null) {
    		SHORT_TIMEOUT = Integer.parseInt(System.getProperty("SHORT_TIMEOUT"));
    	} else {
    		SHORT_TIMEOUT = 600;
    	}
    	if (System.getProperty("TIMEOUT")!=null) {
    		TIMEOUT = Integer.parseInt(System.getProperty("TIMEOUT"));
    	} else {
    		TIMEOUT = 60000;
    	}    	    	
    }	
}
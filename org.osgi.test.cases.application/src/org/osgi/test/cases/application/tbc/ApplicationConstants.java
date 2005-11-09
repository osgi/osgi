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
    public static final String ACTIONS_DIFFERENT_ORDER = ApplicationAdminPermission.LOCK_ACTION
    + "," + ApplicationAdminPermission.LIFECYCLE_ACTION;    
    public static final String XML_INEXISTENT = "unknown";
    public static final String XML_APP = "app";
    public static final String XML_LOG = "log";
    public static final String XML_ACTIVATOR = "activator";
    
    public static final String APP_VERSION = "1.0.0";
    public static final String APP_ICON = "/TestIcon.gif";    
    public static final String TEST_PID = "MIDlet: TestMidlet-1.0.0-Cesar test MIDlet";
    public static final String TEST_PID2 = "MIDlet: TestMidlet-2.0.0-Cesar test MIDlet";
    public static final int TIMEOUT = 60000;
    public static final int SHORT_TIMEOUT = 2000;
    public final static String TIMER_EVENT = "org/osgi/application/timer";
    public static final String EVENT_FILTER = "()";
    public static final String INVALID_FILTER = "&[second=1;}";
    public static final String APPLICATION_NAME = "TestMidlet";
    public static final String OSGI = System.getProperty("org.osgi.service.dmt.root");
    public static final String OSGI_APPLICATION = OSGI + "/Application";
    public static final String OSGI_APPLICATION_VERSION = "1.0";
    public static final String OSGI_APPLICATION_CONTAINER = "MidletContainer";
    public static final String OSGI_APPLICATION_PACKAGE = "org.osgi.test.cases.application.tb1";
    public static final String OSGI_APPLICATION_APPID = OSGI_APPLICATION + "/" + APPLICATION_NAME;
    public static final String OSGI_APPLICATION_APPID_NAME = OSGI_APPLICATION_APPID + "/Name";
    public static final String OSGI_APPLICATION_APPID_APPLICATION_ID = OSGI_APPLICATION_APPID + "/ApplicationID";
    public static final String OSGI_APPLICATION_APPID_ICONURI = OSGI_APPLICATION_APPID + "/IconURI";
    public static final String OSGI_APPLICATION_APPID_VENDOR = OSGI_APPLICATION_APPID + "/Vendor";
    public static final String OSGI_APPLICATION_APPID_VERSION = OSGI_APPLICATION_APPID + "/Version";
    public static final String OSGI_APPLICATION_APPID_LOCKED = OSGI_APPLICATION_APPID + "/Locked";
    public static final String OSGI_APPLICATION_APPID_CONTAINERID = OSGI_APPLICATION_APPID + "/ContainerID";
    public static final String OSGI_APPLICATION_APPID_PACKAGEID = OSGI_APPLICATION_APPID + "/PackageID";
    public static final String OSGI_APPLICATION_APPID_LOCATION = OSGI_APPLICATION_APPID + "/Location";
    public static final String OSGI_APPLICATION_APPID_EXT = OSGI_APPLICATION_APPID + "/Ext";    
    public static final String OSGI_APPLICATION_APPID_OPERATIONS = OSGI_APPLICATION_APPID + "/Operations";
    public static final String OSGI_APPLICATION_APPID_SCHEDULES = OSGI_APPLICATION_APPID + "/Schedules";
    public static final String OSGI_APPLICATION_APPID_OPERATIONS_LOCK = OSGI_APPLICATION_APPID_OPERATIONS + "/Lock";
    public static final String OSGI_APPLICATION_APPID_OPERATIONS_UNLOCK = OSGI_APPLICATION_APPID_OPERATIONS + "/Unlock";
    public static final String OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH = OSGI_APPLICATION_APPID_OPERATIONS + "/Launch";
    public static final String OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID = OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH + "/Cesar";
    public static final String OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT = OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID + "/Result";
    public static final String OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_INSTANCEID = OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT + "/InstanceID";
    public static final String OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_STATUS = OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT + "/Status";
    public static final String OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT_MESSAGE = OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_RESULT + "/Message";
    public static final String OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS = OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID + "/Arguments";
    public static final String OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS_ID = OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS + "/1";
    public static final String OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS_ID_NAME = OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS_ID + "/Name";
    public static final String OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS_ID_VALUE = OSGI_APPLICATION_APPID_OPERATIONS_LAUNCH_LAUNCHID_ARGUMENTS_ID + "/Value";
    public static final String OSGI_APPLICATION_APPID_INSTANCES = OSGI_APPLICATION_APPID + "/Instances";
    public static final String OSGI_APPLICATION_HANDLE_SERVICE_PID = APPLICATION_NAME + ":1";
    public static final String OSGI_APPLICATION_APPID_INSTANCES_ID = OSGI_APPLICATION_APPID_INSTANCES + "/" + OSGI_APPLICATION_HANDLE_SERVICE_PID;
    public static final String OSGI_APPLICATION_APPID_INSTANCES_ID_STATE = OSGI_APPLICATION_APPID_INSTANCES_ID + "/State";
    public static final String OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS = OSGI_APPLICATION_APPID_INSTANCES_ID + "/Operations";
    public static final String OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS_STOP = OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS + "/Stop";
    public static final String OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS_EXT = OSGI_APPLICATION_APPID_INSTANCES_ID_OPERATIONS + "/Ext";
    public static final String OSGI_APPLICATION_APPID_SCHEDULES_ID = OSGI_APPLICATION_APPID + "/Schedule/Cesar";
    public static final String OSGI_APPLICATION_APPID_SCHEDULES_ID_ARGUMENTS = OSGI_APPLICATION_APPID_SCHEDULES_ID + "Cesar/Arguments";
    public static final String OSGI_APPLICATION_APPID_SCHEDULES_ID_ENABLED = OSGI_APPLICATION_APPID_SCHEDULES_ID + "Cesar/Enabled";
    public static final String OSGI_APPLICATION_APPID_SCHEDULES_ID_TOPICFILTER = OSGI_APPLICATION_APPID_SCHEDULES_ID + "Cesar/TopicFilter";
    public static final String OSGI_APPLICATION_APPID_SCHEDULES_ID_EVENTFILTER = OSGI_APPLICATION_APPID_SCHEDULES_ID + "Cesar/EventFilter";
    public static final String OSGI_APPLICATION_APPID_SCHEDULES_ID_RECURRING = OSGI_APPLICATION_APPID_SCHEDULES_ID + "Cesar/Recurring";
 
	private static final String SIGNER_FILTER = "CN=CESAR.ORG.BR, OU=CESAR, O=MOTOROLA, L=RECIFE, C=BR";
	private static final String SIGNER_FILTER_WILDCARD = "CN=*.ORG.BR, OU=CESAR, O=MOTOROLA, L=RECIFE, C=BR";
	private static final String SIGNER_FILTER_INVALID1 = "CN=#@$.ORG.BR, OU=CESAR, O=MOTOROLA, L=RECIFE, C=BR";
	private static final String SIGNER_FILTER_INVALID2 = "2=*.ORG.BR, TX=CESAR, R=MOTOROLA, L=RECIFE, C=BR";

	public static final String APPLICATION_PERMISSION_FILTER1 = "(&(signer="+SIGNER_FILTER+")"+"(pid="+TEST_PID+"))";
	public static final String APPLICATION_PERMISSION_FILTER2 = "(&(signer="+SIGNER_FILTER_WILDCARD+")"+"(pid="+TEST_PID+"))";
	public static final String APPLICATION_PERMISSION_FILTER_DIFFERENT = "(&(signer="+SIGNER_FILTER+")"+"(pid="+TEST_PID2+"))";
	public static final String APPLICATION_PERMISSION_FILTER_INVALID1 = "(&(signer="+SIGNER_FILTER_INVALID1+")"+"(pid="+TEST_PID+"))";
	public static final String APPLICATION_PERMISSION_FILTER_INVALID2 = "(&(signer="+SIGNER_FILTER_INVALID2+")"+"(pid="+TEST_PID+"))";
   
}
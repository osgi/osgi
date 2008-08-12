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
 * 03/08/2005   Alexandre Santos
 * 164          [MEGTCK][MON] Move Test Control Constants to a diferent class
 * ===========  ==============================================================
 */
package org.osgi.test.cases.monitor.tbc;

/**
 * @author Alexandre Santos
 */
public class MonitorConstants {

	public static final String OSGI_ROOT = System.getProperty("info.dmtree.osgi.root");
	public static final String LONGID = "abcdefghjklmnoprstuvwyzabcdfghijk";
	public final static String INVALID_ID = ";/?:@&=+$,";
	public final static String SV_MONITORABLEID1 = "cesar";
	public final static String SV_MONITORABLEID2 = "cesar2";
	public final static String SV_MONITORABLEID3 = "testCesar";
	public final static int MONITORABLE_LENGTH = 1;
	public final static int SV_LENGTH = 2;
	public final static String SV_NAME1 = "test0";
	public final static String SV_NAME2 = "test1";
	public final static String INEXISTENT_MONITORABLE = "a23_-.";
	public final static String IDENTIFY_ID = "ID";
	public final static String DESCRIPTION = "description";
	public final static int INVALID_CM = -1;
	public final static int SV_INT_VALUE = 1;
	public final static float SV_FLOAT_VALUE = 0.0f;
	public final static boolean SV_BOOLEAN_VALUE = false;
	public final static String SV_STRING_VALUE = "value";
	public final static String INITIATOR = "initiator";
	public final static String[] SVS = { "cesar/test0", "cesar/test1" };
	public final static String[] SVS_NOT_SUPPORT_NOTIFICATION = { "cesar2/test0" };
	public final static String INEXISTENT_SVS = "cesar/tmp";
	public final static int SCHEDULE = 1;
	public final static int COUNT = 1;
	public final static int INVALID_SCHEDULE = -1;
	public final static int INVALID_COUNT = -1;
	public final static String INVALID_MONITORABLE_SV = "tmp";
	public static int EVENT_COUNT = 0;
	public static int SWITCH_EVENTS_COUNT = 0;
	public static final int TIMEOUT = 180000;
	public static final int SHORT_TIMEOUT = 2000;
	public static final String CONST_STATUSVARIABLE_NAME = "mon.statusvariable.name";
	public static final String CONST_STATUSVARIABLE_VALUE = "mon.statusvariable.value";
	public static final String CONST_MONITORABLE_PID = "mon.monitorable.pid";
	public static final String CONST_LISTENER_ID = "mon.listener.id";
	public static final String DMT_OSGI_MONITOR = OSGI_ROOT+"/Monitor";
	public final static String DMT_URI_MONITORABLE1 = DMT_OSGI_MONITOR + "/"+SV_MONITORABLEID1;
	public final static String DMT_URI_MONITORABLE2 = DMT_OSGI_MONITOR + "/"+SV_MONITORABLEID2;
	public final static String MONITOR_XML_MONITORABLE1_SV1 = "x-oma-trap:"+SV_MONITORABLEID1+"/"+SV_NAME1;
	public final static String DMT_URI_MONITORABLE1_SV1 = DMT_URI_MONITORABLE1+"/"+SV_NAME1;
	public final static String DMT_URI_MONITORABLE1_SV2 = DMT_URI_MONITORABLE1+"/"+SV_NAME2;
	public final static String DMT_URI_MONITORABLE2_SV1 = DMT_URI_MONITORABLE2+"/"+SV_NAME1;
	public final static String DMT_URI_MONITORABLE2_SV2 = DMT_URI_MONITORABLE2+"/"+SV_NAME2;
	public static final String DMT_URI_MONITORABLE_SV1_TRAPID = DMT_URI_MONITORABLE1_SV1 + "/TrapID";
	public static final String DMT_URI_MONITORABLE_SV1_CM = DMT_URI_MONITORABLE1_SV1 + "/CM";
	public static final String DMT_URI_MONITORABLE_SV1_RESULTS = DMT_URI_MONITORABLE1_SV1 + "/Results";
	public static final String DMT_URI_MONITORABLE_SV1_SERVER = DMT_URI_MONITORABLE1_SV1 + "/Server";
	public static final String REMOTE_SERVER = "remoteServer";
	public final static String[] DMT_URI_MONITORABLE1_PROPERTIES = {
		DMT_URI_MONITORABLE_SV1_SERVER+"/"+REMOTE_SERVER,
		DMT_URI_MONITORABLE_SV1_SERVER+"/"+REMOTE_SERVER+"/Reporting/Type",
		DMT_URI_MONITORABLE_SV1_SERVER+"/"+REMOTE_SERVER+"/Reporting/Value",
		DMT_URI_MONITORABLE_SV1_SERVER+"/"+REMOTE_SERVER+"/Enabled",
		DMT_URI_MONITORABLE_SV1_SERVER+"/"+REMOTE_SERVER+"/ServerID",
		DMT_URI_MONITORABLE_SV1_SERVER+"/"+REMOTE_SERVER+"/Reporting",
		DMT_URI_MONITORABLE_SV1_SERVER+"/"+REMOTE_SERVER+"/TrapRef",
		DMT_URI_MONITORABLE_SV1_SERVER+"/"+REMOTE_SERVER+"/TrapRef/testId",
		DMT_URI_MONITORABLE_SV1_SERVER+"/"+REMOTE_SERVER+"/TrapRef/testId/TrapRefID"
	};	
	
	

}

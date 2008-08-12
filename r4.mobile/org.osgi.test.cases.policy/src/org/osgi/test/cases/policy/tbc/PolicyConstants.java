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
 * Aug 3, 2005  Leonardo Barros
 * 33           Implement MEGTCK for the application RFC 
 * ===========  ==============================================================
 */
package org.osgi.test.cases.policy.tbc;

import info.dmtree.security.DmtPermission;

public class PolicyConstants {

	public static String LOCATION = "";

	public static final String ALL_ACTIONS = DmtPermission.ADD + ","
			+ DmtPermission.DELETE + "," + DmtPermission.EXEC + ","
			+ DmtPermission.GET + "," + DmtPermission.REPLACE;

	public static final String ALL_NODES = "./*";

	public static final String IMEI_VALID_CODE = "012345678912345";
	
	public static final String IMEI_VALID_CODE_WILDCARD = "01234567891234*";

	public static final String IMEI_INVALID_CODE = "0123456-8912345";

	public static final String IMEI_CHAR_CODE = "abcdefghijklmno";

	public static final String IMEI_LESS_DIGIT_CODE = "12345";

	public static final String IMEI_MORE_DIGIT_CODE = "1234567890123456";

	public static final String IMSI_VALID_CODE = "012345678912345";
	
	public static final String IMSI_VALID_CODE_WILDCARD = "01234567891234*";

	public static final String IMSI_CHAR_CODE = "abcdefghijklmno";

	public static final String IMSI_LESS_DIGIT_CODE = "12345";

	public static final String IMSI_INVALID_CODE = "0123456-8912345";

	public static final String IMSI_MORE_DIGIT_CODE = "1234567890123456";

	public static final String INVALID_CODE = "@#$%sA!&_";

	public static final String CONDITION_NAME = "conditionName";
	
	public static final String CONDITION_NAME_NODE = CONDITION_NAME + "/Name"; 

	public static final String PRINCIPAL = "www.cesar.org.br";

	public static final String OSGI_ROOT = System.getProperty("info.dmtree.osgi.root");

	public static final String TEST_NODE = "test";

	public static final String TEST_NODE_LOCATION = "test/Location";

	public static final String PRINCIPAL_LOCATION = PRINCIPAL + "/Principal";

	public static final String PRINCIPAL_PERMISSION = PRINCIPAL
			+ "/PermissionInfo";

	public static final String CONDITIONAL_PERMISSIONINFO = CONDITION_NAME
			+ "/PermissionInfo";

	public static final String CONDITIONAL_CONDITIONINFO = CONDITION_NAME
			+ "/ConditionInfo";

	public static final String TEST_NODE_PERMISSION = "test/PermissionInfo";

	public static final String POLICY_JAVA_NODE = OSGI_ROOT + "/Policy/Java";

	public static final String LOCATION_PERMISSION_NODE = POLICY_JAVA_NODE
			+ "/LocationPermission";

	public static final String LOCATIONS_NODE = LOCATION_PERMISSION_NODE
			+ "/Locations";

	public static final String DEFAULT_PERMISSION_NODE = LOCATION_PERMISSION_NODE
			+ "/Default";

	public static final String PRINCIPAL_PERMISSION_NODE = POLICY_JAVA_NODE
			+ "/DmtPrincipalPermission";

	public static final String CONDITIONAL_PERMISSION_NODE = POLICY_JAVA_NODE
			+ "/ConditionalPermission";

	public static final String INVALID_COST_LIMIT = "TEST";

	public static final String CATALOG_NAME = "com.provider.messages.userprompt";
	
	public static final String LEVEL_ONESHOT = "ONESHOT";

	public static final String LEVEL_SESSION = "SESSION";

	public static final String LEVEL_BLANKET = "BLANKET";
	
	public static final String ALL_LEVELS = LEVEL_ONESHOT + "," + LEVEL_SESSION + "," + LEVEL_BLANKET;

}

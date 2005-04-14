/*
 * ============================================================================
 * (c) Copyright 2005 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */

package org.osgi.impl.service.policy.integrationtests;

import java.security.AllPermission;
import java.security.PrivilegedExceptionAction;
import org.osgi.framework.AdminPermission;
import org.osgi.service.condpermadmin.BundleLocationCondition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.util.mobile.UserPromptCondition;

/**
 *
 * TODO Add Javadoc comment for this type.
 * 
 * @version $Revision$
 */
public class TestUserPrompt extends IntegratedTest {
	public static final ConditionInfo ALLOW_SOCKET = new ConditionInfo(UserPromptCondition.class.getName(),
			new String[] {"BLANKET","","org.osgi.impl.service.policy.integrationtests.userprompt","%MESSAGE_1"});
	public static final ConditionInfo INTEGRATIONTESTS_BUNDLE1_LOCATION_CONDITION =
		new ConditionInfo(BundleLocationCondition.class.getName(),new String[]{INTEGRATIONTESTS_BUNDLE1_JAR});
	public static final PermissionInfo ALL_PERMISSION = new PermissionInfo(AllPermission.class.getName(),"*","*");


	public void testBasic() throws Exception {
		startFramework(true);
		conditionalPermissionAdmin.addConditionalPermissionInfo(
				new ConditionInfo[]{INTEGRATIONTESTS_BUNDLE1_LOCATION_CONDITION,ALLOW_SOCKET},
				new PermissionInfo[]{ALL_PERMISSION});
		PrivilegedExceptionAction adminAction = (PrivilegedExceptionAction) new PrivilegedExceptionAction() {
			public Object run() throws Exception {
				System.getSecurityManager().checkPermission(new AdminPermission());
				return null;
			}
		};
		bundle1DoAction.invoke(null, new Object[]{adminAction});
	}

}

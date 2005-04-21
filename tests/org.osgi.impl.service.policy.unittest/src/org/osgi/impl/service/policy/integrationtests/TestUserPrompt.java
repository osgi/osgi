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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
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
	public static final ConditionInfo ADMINISTRATION_QUESTION = new ConditionInfo(UserPromptCondition.class.getName(),
			new String[] {"BLANKET","","org.osgi.impl.service.policy.integrationtests.messages.userprompt","%ADMIN_TASK"});
	public static final ConditionInfo ADMINISTRATION_SESSION_QUESTION = new ConditionInfo(UserPromptCondition.class.getName(),
			new String[] {"SESSION","","org.osgi.impl.service.policy.integrationtests.messages.userprompt","%ADMIN_TASK"});
	public static final ConditionInfo INTEGRATIONTESTS_BUNDLE1_LOCATION_CONDITION =
		new ConditionInfo(BundleLocationCondition.class.getName(),new String[]{INTEGRATIONTESTS_BUNDLE1_JAR});
	public static final PermissionInfo ALL_PERMISSION = new PermissionInfo(AllPermission.class.getName(),"*","*");
	public static final PermissionInfo ADMIN_PERMISSION = new PermissionInfo(AdminPermission.class.getName(),"*","*");

	// this is an extremely inefficient stdin wrapper :)
	InputStream originalStdin;
	PrintStream stdinPrinter;
	ByteArrayOutputStream stdinByteArray;
	int byteArrayPos;
	
	class MyInputStream extends InputStream {
		public int read() throws IOException {
			byte [] ba = stdinByteArray.toByteArray();
			int b;
			if (ba.length > byteArrayPos) {
				b = ba[byteArrayPos];
				byteArrayPos++;
			} else {
				b = -1;
			}
			return b;
		}
		
	}
	
	public void setUp() throws Exception {
		super.setUp();
		originalStdin = System.in;
		byteArrayPos = 0;
		stdinByteArray = new ByteArrayOutputStream();
		stdinPrinter = new PrintStream(stdinByteArray,true);
		System.setIn(new MyInputStream());
		
	}
	
	public void tearDown() throws Exception {
		System.setIn(originalStdin);
		originalStdin = null;
		super.tearDown();
	}
	
	public void testBasicAlways() throws Exception {
		startFramework(true);

		conditionalPermissionAdmin.addConditionalPermissionInfo(
				new ConditionInfo[]{INTEGRATIONTESTS_BUNDLE1_LOCATION_CONDITION,ADMINISTRATION_QUESTION},
				new PermissionInfo[]{ADMIN_PERMISSION});
		PrivilegedExceptionAction adminAction = (PrivilegedExceptionAction) new PrivilegedExceptionAction() {
			public Object run() throws Exception {
				System.getSecurityManager().checkPermission(new AdminPermission());
				return null;
			}
		};
		
		stdinPrinter.println("always");
		
		bundle1DoAction.invoke(null, new Object[]{adminAction});
	}

	public void testBasicNever() throws Exception {
		startFramework(true);

		conditionalPermissionAdmin.addConditionalPermissionInfo(
				new ConditionInfo[]{INTEGRATIONTESTS_BUNDLE1_LOCATION_CONDITION,ADMINISTRATION_QUESTION},
				new PermissionInfo[]{ADMIN_PERMISSION});

		PrivilegedExceptionAction adminAction = (PrivilegedExceptionAction) new PrivilegedExceptionAction() {
			public Object run() throws Exception {
				System.getSecurityManager().checkPermission(new AdminPermission());
				return null;
			}
		};

		stdinPrinter.println("never");
		
		try {
			bundle1DoAction.invoke(null, new Object[]{adminAction});
			fail();
		} catch (InvocationTargetException e) {}
	}
	
	public void testAlwaysMultiple() throws Exception {
		startFramework(true);

		conditionalPermissionAdmin.addConditionalPermissionInfo(
				new ConditionInfo[]{INTEGRATIONTESTS_BUNDLE1_LOCATION_CONDITION,ADMINISTRATION_QUESTION},
				new PermissionInfo[]{ADMIN_PERMISSION});
		PrivilegedExceptionAction adminAction = (PrivilegedExceptionAction) new PrivilegedExceptionAction() {
			public Object run() throws Exception {
				System.getSecurityManager().checkPermission(new AdminPermission());
				return null;
			}
		};
		
		stdinPrinter.println("always");
		
		bundle1DoAction.invoke(null, new Object[]{adminAction});
		
		bundle1DoAction.invoke(null, new Object[]{adminAction});
		
		stopFramework();
		startFramework(false);

		// should be stored
		bundle1DoAction.invoke(null, new Object[]{adminAction});
		
	}

	public void testNeverMultiple() throws Exception {
		startFramework(true);

		conditionalPermissionAdmin.addConditionalPermissionInfo(
				new ConditionInfo[]{INTEGRATIONTESTS_BUNDLE1_LOCATION_CONDITION,ADMINISTRATION_QUESTION},
				new PermissionInfo[]{ADMIN_PERMISSION});

		PrivilegedExceptionAction adminAction = (PrivilegedExceptionAction) new PrivilegedExceptionAction() {
			public Object run() throws Exception {
				System.getSecurityManager().checkPermission(new AdminPermission());
				return null;
			}
		};

		stdinPrinter.println("never");
		
		try {
			bundle1DoAction.invoke(null, new Object[]{adminAction});
			fail();
		} catch (InvocationTargetException e) {}

		try {
			bundle1DoAction.invoke(null, new Object[]{adminAction});
			fail();
		} catch (InvocationTargetException e) {}

		stopFramework();
		startFramework(false);

		try {
			bundle1DoAction.invoke(null, new Object[]{adminAction});
			fail();
		} catch (InvocationTargetException e) {}

		try {
			bundle1DoAction.invoke(null, new Object[]{adminAction});
			fail();
		} catch (InvocationTargetException e) {}
	}

	public void testBasicSession() throws Exception {
		startFramework(true);

		conditionalPermissionAdmin.addConditionalPermissionInfo(
				new ConditionInfo[]{INTEGRATIONTESTS_BUNDLE1_LOCATION_CONDITION,ADMINISTRATION_SESSION_QUESTION},
				new PermissionInfo[]{ADMIN_PERMISSION});

		PrivilegedExceptionAction adminAction = (PrivilegedExceptionAction) new PrivilegedExceptionAction() {
			public Object run() throws Exception {
				System.getSecurityManager().checkPermission(new AdminPermission());
				return null;
			}
		};

		stdinPrinter.println("session");
		
		bundle1DoAction.invoke(null, new Object[]{adminAction});
		bundle1DoAction.invoke(null, new Object[]{adminAction});

		stopFramework();
		startFramework(false);

		stdinPrinter.println("nosession");
		
		try {
			bundle1DoAction.invoke(null, new Object[]{adminAction});
			fail();
		} catch (InvocationTargetException e) {}

		try {
			bundle1DoAction.invoke(null, new Object[]{adminAction});
			fail();
		} catch (InvocationTargetException e) {}

		stopFramework();
		startFramework(false);

		stdinPrinter.println("session");
		
		bundle1DoAction.invoke(null, new Object[]{adminAction});
		bundle1DoAction.invoke(null, new Object[]{adminAction});

		stopFramework();
		startFramework(false);

		stdinPrinter.println("never");
		
		try {
			bundle1DoAction.invoke(null, new Object[]{adminAction});
			fail();
		} catch (InvocationTargetException e) {}
		
		stopFramework();
		startFramework(false);

		try {
			bundle1DoAction.invoke(null, new Object[]{adminAction});
			fail();
		} catch (InvocationTargetException e) {}
	}


}

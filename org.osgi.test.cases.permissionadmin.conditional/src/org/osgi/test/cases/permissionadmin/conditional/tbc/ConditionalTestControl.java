/*
 * $Header$
 * 
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
 */
package org.osgi.test.cases.permissionadmin.conditional.tbc;

import org.osgi.test.cases.util.DefaultTestBundleControl;

import org.osgi.framework.*;
import org.osgi.service.condpermadmin.*;
import org.osgi.service.permissionadmin.*;

import java.util.StringTokenizer;
import java.util.Enumeration;
import java.util.Vector;

import java.security.Permission;



/**
 * Contains the test methods of the conditional permission test case.
 * 
 * @author Petia Sotirova
 * @version 1.0
 */
public class ConditionalTestControl extends DefaultTestBundleControl {
	
	private String 						testBundleLocation;
	private Bundle						testBundle;
	
	private ConditionalPermissionAdmin	conditionalAdmin;
	private PermissionAdmin				permissionAdmin;
	private ConditionalTBCService		tbc;
	
	private ConditionalUtility			utility;
	
	private String						BUNDLE_LOCATION_CONDITION = BundleLocationCondition.class.getName();
	// TODO -> BundleSymbolicNameCondition.class.getName()
	private String						BUNDLE_SYMBOLIC_NAME_CONDITION = "BundleSymbolicNameCondition";
	// TODO -> BundleSignerCondition.class.getName()
	private String						BUNDLE_SIGNER_CONDITION = "BundleSignerCondition";
	
	
	
	/**
	 * <remove>Prepare for each run. It is important that a test run is properly
	 * initialized and that each case can run standalone. To save a lot
	 * of time in debugging, clean up all possible persistent remains
	 * before the test is run. Clean up is better don in the prepare
	 * because debugging sessions can easily cause the unprepare never
	 * to be called.</remove> 
	 */
	public void prepare() throws Exception {
		testBundle = installBundle("tb1.jar");
		testBundleLocation = testBundle.getLocation();
		
		permissionAdmin = (PermissionAdmin)getService(PermissionAdmin.class);
		conditionalAdmin = (ConditionalPermissionAdmin)getService(ConditionalPermissionAdmin.class);
		tbc = (ConditionalTBCService)getService(ConditionalTBCService.class);
			
		utility = new ConditionalUtility(this, tbc, permissionAdmin, conditionalAdmin);
	}

	/**
	 * <remove>Prepare for each method. It is important that each method can
	 * be executed independently of each other method. Do not keep
	 * state between methods, if possible. This method can be used
	 * to clean up any possible remaining state.</remove> 
	 */
	public void setState() {
		log("#before each method");
		
		Enumeration infos = conditionalAdmin.getConditionalPermissionInfos();
				
		while ((infos != null) && infos.hasMoreElements()) {
			((ConditionalPermissionInfo)infos.nextElement()).delete();
		}
		
		permissionAdmin.setPermissions(testBundleLocation, null);
	}

	
	/**
	 * Creates correct and incorrect ConditionInfo.
	 * Check if conditioninfos created from encoded are identical to the original
	 */
	public void testConditionInfo() throws Exception {
		String conditionType = BUNDLE_LOCATION_CONDITION;
		String location = "test.location";
				
		// Assert Equals
		ConditionInfo info1 = new ConditionInfo(conditionType, new String[]{location});
		ConditionInfo info2 = new ConditionInfo("[" + conditionType + " " +
												"\"" + location + "\"]");
		assertEquals("constructed from a string ", info1, info2);
		assertEquals("toString ", info2.getEncoded(), info1.toString());
		assertEquals("identical hashcodes ", info1.hashCode(), info2.hashCode());
		assertEquals("identical types ", info1.getType(), info2.getType());
		assertEquals("identical args ", info1.getArgs(), info2.getArgs());
		
		// Bad CondittionInfo
		utility.createBadConditionInfo("null type", null, new String[]{location}, 
								NullPointerException.class);
		utility.createBadConditionInfo("Missing type ", "[" + " " + " " +
				"\"" + location + "\"]", IllegalArgumentException.class);
		utility.createBadConditionInfo("Missing open square brace ", " " + conditionType + " " +
				"\"" + location + "\"]", IllegalArgumentException.class);
		utility.createBadConditionInfo("Missing closing square brace ", "[" + conditionType + " " +
				"\"" + location + "\" ", IllegalArgumentException.class);
		utility.createBadConditionInfo("Missing args quote ", "[" + conditionType + " " +
				location + "]", IllegalArgumentException.class);
		utility.createBadConditionInfo("Argument after closing square brace ", "[" + conditionType + " " +
				"\"" + location + "\"]" + "\"" + location + "\"", IllegalArgumentException.class);
		utility.createBadConditionInfo("Missing open args quote ", "[" + conditionType + " " +
				location + "\"]", IllegalArgumentException.class);
		utility.createBadConditionInfo("Missing closing args quote ", "[" + conditionType + " " +
				"\"" + location + "]", IllegalArgumentException.class);
		utility.createBadConditionInfo("Too much whitespace between type and args ", "[" + conditionType + "    " +
				"\"" + location + "]", IllegalArgumentException.class);
		utility.createBadConditionInfo("Comma separation between type and args ", "[" + conditionType + " ," +
				"\"" + location + "]", IllegalArgumentException.class);
	}
	
	/**
	 * Check if ConditionalPermissionInfos added in CoditionalPermissionAdmin
	 * are identical to the original.
	 */
	public void testCoditionalPermissionAdmin() {
		ConditionInfo cInfo1 = new ConditionInfo(BUNDLE_LOCATION_CONDITION, 
				new String[]{testBundleLocation});
		
		ConditionInfo cInfo2 = new ConditionInfo(BUNDLE_SIGNER_CONDITION,
				new String[]{ConditionResource.getString(ConditionalUtility.DN_S)});
		
		ConditionInfo cInfo3 = new ConditionInfo(BUNDLE_SYMBOLIC_NAME_CONDITION,
				new String[]{testBundle.getSymbolicName(), null});
		
		
		PermissionInfo pInfo = new PermissionInfo(AdminPermission.class.getName(), 
												  "*", "*");
		
		ConditionInfo[] conditions = new ConditionInfo[]{cInfo1, cInfo2, cInfo3};
		PermissionInfo[] permissions = new PermissionInfo[]{pInfo}; 
		
		ConditionalPermissionInfo cpInfo = conditionalAdmin.addConditionalPermissionInfo(
				conditions, permissions);
		
		assertEquals("ConditionInfos ", cpInfo.getConditionInfos(), conditions);
		assertEquals("PermissionInfos ", cpInfo.getPermissionInfos(), permissions);
		
		Enumeration infos = conditionalAdmin.getConditionalPermissionInfos();
		
		boolean addConditionalPermission = false;
		ConditionalPermissionInfo addedcpInfo = null;
		
		while (infos.hasMoreElements()) {
			addedcpInfo = (ConditionalPermissionInfo)infos.nextElement();
			if (addedcpInfo.equals(cpInfo)) {
				addConditionalPermission = true;
				break;
			}
		}
		
		assertTrue("addConditionalPermission ", addConditionalPermission);

		assertEquals("ConditionInfos ", cpInfo.getConditionInfos(), addedcpInfo.getConditionInfos());
		assertEquals("PermissionInfos ", cpInfo.getPermissionInfos(), addedcpInfo.getPermissionInfos());
	}
	
	
	/**
	 * Check if only the bundle with the appropriate location 
	 * has(only) corresponding permissions.
	 */
	public void testBundleLocationCondition() {
		ConditionInfo cInfo = null;
		AdminPermission permission = new AdminPermission("*", "resource");
		AdminPermission allPermissions = new AdminPermission("*", "*");
		
		
		Vector locations = utility.getWildcardString(testBundleLocation);
		for (int i = 0; i < locations.size(); ++i) {
			cInfo = new ConditionInfo(BUNDLE_LOCATION_CONDITION, 
				new String[]{(String)locations.elementAt(i)});
		
			utility.testPermissions(new ConditionInfo[]{cInfo}, permission, 
					new Permission[]{permission}, new Permission[]{allPermissions});
		}
		
		
		String bundleLocation = getContext().getBundle().getLocation();
		cInfo = new ConditionInfo(BUNDLE_LOCATION_CONDITION, 
								  new String[]{bundleLocation});

		utility.testPermissions(new ConditionInfo[]{cInfo}, permission, 
				new Permission[]{}, new Permission[]{permission, allPermissions});
		
	}

	/**
	 * Check if only the bundle with the appropriate symbolic name 
	 * has(only) corresponding permissions.
	 */
	public void testBundleSymbolicNameCondition() {
		ConditionInfo cInfo = null;
		AdminPermission permission = new AdminPermission("*", "resource");
		AdminPermission allPermissions = new AdminPermission("*", "*");

		
		String symbolicName = testBundle.getSymbolicName();
		String version = (String)testBundle.getHeaders().get(ConditionalUtility.BUNDLE_VERSION);
		String floor = ConditionResource.getString(ConditionalUtility.VERSION_FLOOR);
		String ceiling = ConditionResource.getString(ConditionalUtility.VERSION_CEILING);
		
		Vector names = utility.getWildcardString(symbolicName);
		Vector correctRanges = utility.getCorrectVersionRanges(version, floor, ceiling);
		Vector notCorrectRanges = utility.getNotCorrectVersionRanges(version, floor, ceiling);
		
		String name;
		for (int i = 0; i < names.size(); ++i) {
			name = (String)names.elementAt(i);

			cInfo = new ConditionInfo(BUNDLE_SYMBOLIC_NAME_CONDITION, 
					new String[]{name, null});
		
			utility.testPermissions(new ConditionInfo[]{cInfo}, permission, 
					new Permission[]{permission}, new Permission[]{allPermissions});
			
			
			for (int j = 0; j < correctRanges.size(); ++j) {
				cInfo = new ConditionInfo(BUNDLE_SYMBOLIC_NAME_CONDITION, 
						new String[]{name, (String)correctRanges.elementAt(j)});
				
				utility.testPermissions(new ConditionInfo[]{cInfo}, permission, 
						new Permission[]{permission}, new Permission[]{allPermissions});
			}
			
			for (int k = 0; k < notCorrectRanges.size(); ++k) {
				cInfo = new ConditionInfo(BUNDLE_SYMBOLIC_NAME_CONDITION, 
						new String[]{name, (String)notCorrectRanges.elementAt(k)});
				
				utility.testPermissions(new ConditionInfo[]{cInfo}, permission, 
						new Permission[]{}, new Permission[]{permission, allPermissions});

			}
		}
	}
	
	
	/**
	 * Check if only the bundle with the appropriate certificates 
	 * has(only) corresponding permissions.
	 */
	public void testBundleSignerCondition() {
		ConditionInfo cInfo = null;
		AdminPermission permission = new AdminPermission("*", "resource");
		AdminPermission allPermissions = new AdminPermission("*", "*");
		
		// test with appropriate certificates 
		String dn_s_value = ConditionResource.getString(ConditionalUtility.DN_S);
		Vector dn_s = utility.createWildcardDNs(dn_s_value);

		String element;
		for (int i = 0; i < dn_s.size(); ++i) {
			element = (String)dn_s.elementAt(i);

			cInfo = new ConditionInfo(BUNDLE_SIGNER_CONDITION, new String[]{element});
		
			utility.testPermissions(new ConditionInfo[]{cInfo}, permission, 
					new Permission[]{permission}, new Permission[]{allPermissions});
		}
		
		// test with inappropriate certificates 
		dn_s_value = ConditionResource.getString(ConditionalUtility.INAPPROPRIATE_DN_S);

		StringTokenizer st = new StringTokenizer(dn_s_value, ConditionalUtility.SEPARATOR);
		while (st.hasMoreTokens()) {
			cInfo = new ConditionInfo(BUNDLE_SIGNER_CONDITION, new String[]{st.nextToken()});
			utility.testPermissions(new ConditionInfo[]{cInfo}, permission, 
						new Permission[]{}, new Permission[]{permission, allPermissions});

	    }
	}
	
	
	/**
	 * Check different cases with satisfied, evaluated and mutable conditions.
	 */
	public void testMoreConditions() {
		AdminPermission permission = new AdminPermission("*", "resource");
		AdminPermission allPermissions = new AdminPermission("*", "*");
		
		// All bundles must have that 'permission'
		utility.testPermissions(new ConditionInfo[]{}, permission, 
			new Permission[]{permission}, new Permission[]{allPermissions});


		utility.testPermissions(
			new ConditionInfo[]{
				utility.createTestCInfo(true, true, false), // evaluated, satisfied, not mutable
				utility.createTestCInfo(false, true, false), // non evaluated, satisfied ? , not mutable		
				utility.createTestCInfo(false, true, true)	// non evaluated, satisfied ? , mutable	
			}, 
			permission, 
			new Permission[]{permission}, 
			new Permission[]{allPermissions});

		utility.testPermissions(
			new ConditionInfo[]{
				utility.createTestCInfo(true, false, false), // evaluated, not satisfied, not mutable
			}, 
			permission, 
			new Permission[]{permission}, 
			new Permission[]{allPermissions});

		utility.testPermissions(
			new ConditionInfo[]{
				utility.createTestCInfo(true, true, false), // evaluated, satisfied, not mutable
				utility.createTestCInfo(true, false, false), // evaluated, not satisfied, not mutable		
			}, 
			permission, 
			new Permission[]{}, 
			new Permission[]{permission, allPermissions});
	
		utility.testPermissions(
			new ConditionInfo[]{
				utility.createTestCInfo(false, false, true), // not evaluated, not satisfied, mutable		
				utility.createTestCInfo(true, true, false), // evaluated, satisfied, not mutable
			}, 
			permission, 
			new Permission[]{}, 
			new Permission[]{permission, allPermissions});
		
	}
	
	/**
	 * Tests interaction between ConditionalPermissionAdmin and PermissionAdmin.
	 */
	public void testConditionalPA_and_PA() throws Exception {
		AdminPermission pCPA = new AdminPermission("*", "lifecycle");
		AdminPermission pPA = new AdminPermission("*", "listener"); 
		AdminPermission def = new AdminPermission("*", "metadata");
		
		PermissionInfo[] defaultPermissions = permissionAdmin.getDefaultPermissions();
		
		permissionAdmin.setDefaultPermissions(new PermissionInfo[]{
			new PermissionInfo(def.getClass().getName(), def.getName(), def.getActions())});
		utility.allowed(def.toString(), def);
		
		utility.testPermissions(new ConditionInfo[]{}, pCPA, 
				new Permission[]{pCPA}, new Permission[]{pPA});
		
		utility.setPermissionsByPermissionAdmin(testBundleLocation, pPA);
		utility.testPermissions(new ConditionInfo[]{}, pCPA, 
				new Permission[]{}, new Permission[]{pCPA});
		
		utility.setPermissionsByCPermissionAdmin(new ConditionInfo[]{}, pCPA);
		utility.allowed("", pPA);
		
				
		permissionAdmin.setDefaultPermissions(defaultPermissions);
	}
	
	
	/**
	 * Tests permissions when exists file META-INF/permissions.perm  
	 */
	public void testBundlePermissionInformation() throws Exception {
		Permission pFromFile = utility.getPermission(ConditionalUtility.REQUIRED);
		Permission pCP = utility.getPermission(ConditionalUtility.CP_PERMISSION);
		Permission pCPIntersection = utility.getPermission(ConditionalUtility.REQUIRED_CP_PERMISSION);

		
		// ConditionalPermissionAdmin && permissions.perm
		utility.testPermissions(new ConditionInfo[]{}, pCP, 
				new Permission[]{pCPIntersection}, new Permission[]{pFromFile, pCP});

		
		
		Permission pAP = utility.getPermission(ConditionalUtility.P_PERMISSION);
		Permission pAPIntersection = utility.getPermission(ConditionalUtility.REQUIRED_P_PERMISSION);
		
		// PermissionAdmin && ConditionalPermissionAdmin && permissions.perm
		utility.setPermissionsByPermissionAdmin(testBundleLocation, pAP);
		utility.testPermissions(new ConditionInfo[]{}, pCP, 
				new Permission[]{}, new Permission[]{pFromFile, pCP, pCPIntersection});
		
		utility.setPermissionsByCPermissionAdmin(new ConditionInfo[]{}, pCP);
		utility.allowed("", pAPIntersection);
	}

	
	/**
	 * Clean up after each method. Notice that during debugging
	 * many times the unsetState is never reached.
	 */
	public void unsetState() {
		log("#after each method");
	}

	/**
	 * Clean up after a run. Notice that during debugging
	 * many times the unprepare is never reached.
	 */
	public void unprepare() {
		log("#after each run");
	}
	

}
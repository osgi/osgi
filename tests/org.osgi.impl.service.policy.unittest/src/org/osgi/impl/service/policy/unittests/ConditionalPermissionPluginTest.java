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
package org.osgi.impl.service.policy.unittests;

import java.util.Arrays;
import org.osgi.framework.AdminPermission;
import org.osgi.framework.PackagePermission;
import org.osgi.framework.ServicePermission;
import org.osgi.impl.service.policy.condpermadmin.ConditionalPermissionAdminPlugin;
import org.osgi.impl.service.policy.unittests.util.DmtPluginTestCase;
import org.osgi.service.condpermadmin.BundleLocationCondition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtMetaNode;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.http.HttpService;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.util.gsm.IMEICondition;

/**
 *
 * TODO Add Javadoc comment for this type.
 * 
 * @version $Revision$
 */
public class ConditionalPermissionPluginTest extends DmtPluginTestCase {
	
	/**
	 * the plugin currently tested
	 */
	public ConditionalPermissionAdminPlugin plugin;
	
	/**
	 * the DMT session opened during a testcase
	 */
	public DmtSession dmtSession;

	/**
	 * conditional permission admin simulator
	 */
	public DummyConditionalPermissionAdmin condPermAdmin;

	public DummyComponentContext context;
	
	public static final String ROOT = "./OSGi/Policies/Java/ConditionalPermission";
	
	public static final ConditionInfo LOC1CONDITION = new ConditionInfo(
			BundleLocationCondition.class.getName(),
			new String[] {"http://example.com/loc1"});
	
	public static final PermissionInfo HTTPREGISTERPERMISSION = new PermissionInfo(
			ServicePermission.class.getName(),
			HttpService.class.getName(),
			"register");
	
	public static final PermissionInfo OSGIIMPORTPERMISSION = new PermissionInfo(
			PackagePermission.class.getName(),
			"org.osgi.*",
			"IMPORT");

	/*
	 * This is the example written in the policy RFC.
	 */
	public static final String RFC_EXAMPLE_HASH = "WovYXjHL_EgRTOVWHgipOk82tt8";
	public static final ConditionInfo[] RFC_EXAMPLE_COND = new ConditionInfo[] {LOC1CONDITION};
	public static final String RFC_EXAMPLE_COND_STR = RFC_EXAMPLE_COND[0].getEncoded()+"\n";
	public static final PermissionInfo[] RFC_EXAMPLE_PERM = new PermissionInfo[] {HTTPREGISTERPERMISSION,OSGIIMPORTPERMISSION};
	public static final String RFC_EXAMPLE_PERM_STR = RFC_EXAMPLE_PERM[0].getEncoded()+"\n"+RFC_EXAMPLE_PERM[1].getEncoded()+"\n";

	/*
	 * this is an other conditional permission to be used
	 */
	public static final String CP1_HASH = "foo";
	public static final ConditionInfo[] CP1_COND = new ConditionInfo[] { 
			new ConditionInfo(BundleLocationCondition.class.getName(),new String[]{"http://location1"}),
			new ConditionInfo(IMEICondition.class.getName(),new String[]{"128471624213421"})
	};
	public static final String CP1_COND_STR = CP1_COND[0].getEncoded()+"\n"+CP1_COND[1].getEncoded()+"\n";
	public static final PermissionInfo[] CP1_PERM = new PermissionInfo[] {
			new PermissionInfo(AdminPermission.class.getName(),"*","*")};
	public static final String CP1_PERM_STR = CP1_PERM[0].getEncoded();
	
	
	public void setUp() throws Exception {
		super.setUp();
		condPermAdmin = new DummyConditionalPermissionAdmin();
		context = new DummyComponentContext();
		context.properties.put("dataRootURIs",ROOT);
		context.services.put("condPermAdmin",condPermAdmin);
		plugin = new ConditionalPermissionAdminPlugin();
		context.doActivate(plugin);
		addDataPlugin(ROOT,plugin);
	}
	
	public void tearDown() throws Exception {
		condPermAdmin = null;
		plugin = null;
		dmtSession = null;
		super.tearDown();
	}

	public void newSession() throws DmtException {
		dmtSession = dmtFactory.getSession(ROOT);
		assertNotNull(dmtSession);
	}

	public void newAtomicSession() throws DmtException {
		dmtSession = dmtFactory.getSession(ROOT,DmtSession.LOCK_TYPE_ATOMIC);
		assertNotNull(dmtSession);
	}
	
	public void testRegister() throws Exception {
		newSession();
	}

	public void testRootMetaNode() throws Exception {
		newSession();
		DmtMetaNode mn = dmtSession.getMetaNode(ROOT);
		assertNotNull(mn);
		assertEquals(false,mn.can(DmtMetaNode.CMD_DELETE));
	}
	
	public void testRFCHashExample() throws Exception {
		condPermAdmin.addConditionalPermissionInfo(RFC_EXAMPLE_COND,RFC_EXAMPLE_PERM);
		newSession();
		DmtData pis = dmtSession.getNodeValue(RFC_EXAMPLE_HASH+"/PermissionInfo");
		assertEquals(RFC_EXAMPLE_PERM_STR,pis.getString());
		DmtData cis = dmtSession.getNodeValue(RFC_EXAMPLE_HASH+"/ConditionInfo");
		assertEquals(RFC_EXAMPLE_COND_STR,cis.getString());
	}
	
	public void testListings() throws Exception {
		condPermAdmin.addConditionalPermissionInfo(RFC_EXAMPLE_COND,RFC_EXAMPLE_PERM);
		newSession();
		String ch[] = dmtSession.getChildNodeNames(ROOT);
		assertEquals(1,ch.length);
		assertEquals(RFC_EXAMPLE_HASH,ch[0]);
		ch = dmtSession.getChildNodeNames(RFC_EXAMPLE_HASH);
		Arrays.sort(ch);
		assertEquals(2,ch.length);
		assertEquals("ConditionInfo",ch[0]);
		assertEquals("PermissionInfo",ch[1]);
	}

	public void testDelete() throws Exception {
		condPermAdmin.addConditionalPermissionInfo(RFC_EXAMPLE_COND,RFC_EXAMPLE_PERM);
		newAtomicSession();
		dmtSession.deleteNode(RFC_EXAMPLE_HASH);
		dmtSession.close();
		assertEquals(0,condPermAdmin.size());
	}
	
	public void testAdd() throws Exception {
		DummyConditionalPermissionAdmin.PI cp1 = condPermAdmin.new PI(CP1_COND,CP1_PERM);
		assertFalse(condPermAdmin.contains(cp1));
		newAtomicSession();
		dmtSession.createInteriorNode("1");
		dmtSession.setNodeValue("1/PermissionInfo",new DmtData(CP1_PERM_STR));
		dmtSession.setNodeValue("1/ConditionInfo",new DmtData(CP1_COND_STR));
		dmtSession.close();
		assertTrue(condPermAdmin.contains(cp1));
	}
	
	public void testAddAndDelete() throws Exception {
		// we add one, and delete an other one
		DummyConditionalPermissionAdmin.PI cp1 = condPermAdmin.new PI(CP1_COND,CP1_PERM);
		ConditionalPermissionInfo rfc = condPermAdmin.addConditionalPermissionInfo(RFC_EXAMPLE_COND,RFC_EXAMPLE_PERM);
		assertFalse(condPermAdmin.contains(cp1));
		assertTrue(condPermAdmin.contains(rfc));
		newAtomicSession();
		dmtSession.createInteriorNode("1");
		dmtSession.setNodeValue("1/PermissionInfo",new DmtData(CP1_PERM_STR));
		dmtSession.setNodeValue("1/ConditionInfo",new DmtData(CP1_COND_STR));
		dmtSession.deleteNode(RFC_EXAMPLE_HASH);
		dmtSession.close();
		assertTrue(condPermAdmin.contains(cp1));
		assertFalse(condPermAdmin.contains(rfc));
	}
	
	public void testDeleteOneFromTwo() throws Exception {
		// have two conditionalpermissions, delete one of them
		ConditionalPermissionInfo cp1 = condPermAdmin.addConditionalPermissionInfo(CP1_COND,CP1_PERM);
		ConditionalPermissionInfo rfc = condPermAdmin.addConditionalPermissionInfo(RFC_EXAMPLE_COND,RFC_EXAMPLE_PERM);
		assertTrue(condPermAdmin.contains(cp1));
		assertTrue(condPermAdmin.contains(rfc));
		newAtomicSession();
		dmtSession.deleteNode(RFC_EXAMPLE_HASH);
		dmtSession.close();
		assertTrue(condPermAdmin.contains(cp1));
		assertFalse(condPermAdmin.contains(rfc));
	}

	public void modifyPermissionInfo() throws Exception {
		// replace the permission infos in the RFC example with something else
		ConditionalPermissionInfo expected_target = condPermAdmin.new PI(RFC_EXAMPLE_COND,CP1_PERM);
		ConditionalPermissionInfo rfc = condPermAdmin.addConditionalPermissionInfo(RFC_EXAMPLE_COND,RFC_EXAMPLE_PERM);
		assertFalse(condPermAdmin.contains(expected_target));
		assertTrue(condPermAdmin.contains(rfc));
		newAtomicSession();
		dmtSession.setNodeValue(RFC_EXAMPLE_HASH+"/PermissionInfo",new DmtData(CP1_PERM_STR));
		dmtSession.close();
		assertTrue(condPermAdmin.contains(expected_target));
		assertFalse(condPermAdmin.contains(rfc));
	}
	
	public void testRollBack() throws Exception {
		ConditionalPermissionInfo rfc = condPermAdmin.addConditionalPermissionInfo(RFC_EXAMPLE_COND,RFC_EXAMPLE_PERM);
		assertTrue(condPermAdmin.contains(rfc));
		newAtomicSession();
		dmtSession.deleteNode(RFC_EXAMPLE_HASH);
		dmtSession.rollback();
		assertTrue(condPermAdmin.contains(rfc));
	}
	
	public void testNonAtomicWrite() throws Exception {
		newSession();
		try {
			dmtSession.createInteriorNode("1");
			fail();
		} catch (DmtException e) {}
		
	}
}

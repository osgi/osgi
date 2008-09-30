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

import info.dmtree.DmtData;
import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import info.dmtree.MetaNode;

import java.util.Arrays;

import org.osgi.framework.AdminPermission;
import org.osgi.framework.PackagePermission;
import org.osgi.framework.ServicePermission;
import org.osgi.impl.service.policy.condpermadmin.PluginFactory;
import org.osgi.impl.service.policy.unittests.DummyConditionalPermissionAdmin.PI;
import org.osgi.impl.service.policy.unittests.util.DmtPluginTestCase;
import org.osgi.service.condpermadmin.BundleLocationCondition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;
import org.osgi.service.http.HttpService;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.util.gsm.IMEICondition;

public class ConditionalPermissionPluginTest extends DmtPluginTestCase {
	
	/**
	 * the plugin currently tested
	 */
	public PluginFactory plugin;
	
	/**
	 * the DMT session opened during a testcase
	 */
	public DmtSession dmtSession;

	/**
	 * conditional permission admin simulator
	 */
	public DummyConditionalPermissionAdmin condPermAdmin;

	
	public static final String ROOT = "./OSGi/Policy/Java/ConditionalPermission";
	
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
		context.properties.put("dataRootURIs",ROOT);
		context.services.put("condPermAdmin",condPermAdmin);
		plugin = new PluginFactory();
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
		dmtSession = dmtAdmin.getSession(ROOT);
		assertNotNull(dmtSession);
	}

	public void newAtomicSession() throws DmtException {
		dmtSession = dmtAdmin.getSession(ROOT,DmtSession.LOCK_TYPE_ATOMIC);
		assertNotNull(dmtSession);
	}
	
	public void testRegister() throws Exception {
		newSession();
	}

	public void testRootMetaNode() throws Exception {
		newSession();
		MetaNode mn = dmtSession.getMetaNode(ROOT);
		assertNotNull(mn);
		assertEquals(false,mn.can(MetaNode.CMD_DELETE));
	}
		
	public void testListings() throws Exception {
		condPermAdmin.setConditionalPermissionInfo("rfc",RFC_EXAMPLE_COND,RFC_EXAMPLE_PERM);
		newSession();
		String ch[] = dmtSession.getChildNodeNames(ROOT);
		assertEquals(1,ch.length);
		assertEquals("rfc",ch[0]);
		ch = dmtSession.getChildNodeNames("rfc");
		Arrays.sort(ch);
		assertEquals(3,ch.length);
		assertEquals("ConditionInfo",ch[0]);
		assertEquals("Name",ch[1]);
		assertEquals("PermissionInfo",ch[2]);
	}

	public void testDelete() throws Exception {
		condPermAdmin.setConditionalPermissionInfo("rfc",RFC_EXAMPLE_COND,RFC_EXAMPLE_PERM);
		newAtomicSession();
		dmtSession.deleteNode("rfc");
		dmtSession.close();
		assertEquals(0,condPermAdmin.size());
	}
	
	public void testAdd() throws Exception {
		assertFalse(condPermAdmin.containsKey("1"));
		newAtomicSession();
		dmtSession.createInteriorNode("1");
		dmtSession.setNodeValue("1/Name",new DmtData("1"));;
		dmtSession.setNodeValue("1/PermissionInfo",new DmtData(CP1_PERM_STR));
		dmtSession.setNodeValue("1/ConditionInfo",new DmtData(CP1_COND_STR));
		dmtSession.close();
		assertTrue(condPermAdmin.containsKey("1"));
	}
	
	public void testAddAndDelete() throws Exception {
		// we add one, and delete an other one
		ConditionalPermissionInfo rfc = condPermAdmin.setConditionalPermissionInfo("rfc",RFC_EXAMPLE_COND,RFC_EXAMPLE_PERM);
		assertFalse(condPermAdmin.containsKey("1"));
		assertTrue(condPermAdmin.containsKey("rfc"));
		newAtomicSession();
		dmtSession.createInteriorNode("1");
		dmtSession.setNodeValue("1/Name",new DmtData("1"));
		dmtSession.setNodeValue("1/PermissionInfo",new DmtData(CP1_PERM_STR));
		dmtSession.setNodeValue("1/ConditionInfo",new DmtData(CP1_COND_STR));
		dmtSession.deleteNode("rfc");
		dmtSession.close();
		assertTrue(condPermAdmin.containsKey("1"));
		assertFalse(condPermAdmin.containsKey("rfc"));
	}
	
	public void testDeleteOneFromTwo() throws Exception {
		// have two conditionalpermissions, delete one of them
		ConditionalPermissionInfo cp1 = condPermAdmin.setConditionalPermissionInfo("cp1",CP1_COND,CP1_PERM);
		ConditionalPermissionInfo rfc = condPermAdmin.setConditionalPermissionInfo("rfc",RFC_EXAMPLE_COND,RFC_EXAMPLE_PERM);
		assertTrue(condPermAdmin.containsKey("cp1"));
		assertTrue(condPermAdmin.containsKey("rfc"));
		newAtomicSession();
		dmtSession.deleteNode("rfc");
		dmtSession.close();
		assertTrue(condPermAdmin.containsKey("cp1"));
		assertFalse(condPermAdmin.containsKey("rfc"));
	}

	public void modifyPermissionInfo() throws Exception {
		// replace the permission infos in the RFC example with something else
		ConditionalPermissionInfo rfc = condPermAdmin.setConditionalPermissionInfo("1",RFC_EXAMPLE_COND,RFC_EXAMPLE_PERM);
		assertTrue(condPermAdmin.containsKey("1"));
		newAtomicSession();
		dmtSession.setNodeValue("1/PermissionInfo",new DmtData(CP1_PERM_STR));
		dmtSession.close();
		assertTrue(condPermAdmin.containsKey("1"));
		PI pi = (PI) condPermAdmin.get("1");
		assertEquals(pi.permissionInfo.length,CP1_PERM.length);
		for(int i=0;i<pi.permissionInfo.length;i++) assertEquals(pi.permissionInfo[i],CP1_PERM[i]);
	}
	
	public void testRollBack() throws Exception {
		ConditionalPermissionInfo rfc = condPermAdmin.setConditionalPermissionInfo("1",RFC_EXAMPLE_COND,RFC_EXAMPLE_PERM);
		assertTrue(condPermAdmin.containsKey("1"));
		newAtomicSession();
		dmtSession.deleteNode("1");
		dmtSession.rollback();
		assertTrue(condPermAdmin.containsKey("1"));
	}
	
	public void testNonAtomicWrite() throws Exception {
		newSession();
		try {
			dmtSession.createInteriorNode("1");
			fail();
		} catch (DmtException e) {}
		
	}
}

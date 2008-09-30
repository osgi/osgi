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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.AdminPermission;
import org.osgi.framework.PackagePermission;
import org.osgi.impl.service.dmt.export.DmtPrincipalPermissionAdmin;
import org.osgi.impl.service.policy.dmtprincipal.PluginFactory;
import org.osgi.impl.service.policy.unittests.util.DmtPluginTestCase;
import org.osgi.service.permissionadmin.PermissionInfo;

/**
 *
 * TODO Add Javadoc comment for this type.
 * 
 * @version $Revision$
 */
public class DmtPrincipalPluginTest extends DmtPluginTestCase implements DmtPrincipalPermissionAdmin {
	public static final String ROOT = "./OSGi/Policy/Java/DmtPrincipalPermission";
	public static final String PRINCIPAL1 = "principal1";
	public static final String PRINCIPAL2 = "principal2";
	public static final PermissionInfo ADMINPERMISSION = new PermissionInfo(AdminPermission.class.getName(),"*","*");
	public static final PermissionInfo IMPORTPERMISSION = new PermissionInfo(PackagePermission.class.getName(),"org.osgi.framework","IMPORT");
	
	/**
	 * the plugin currently tested
	 */
	public PluginFactory plugin;

	/**
	 * an opened DMT session to our subtree
	 */
	public DmtSession dmtSession;

	/**
	 * permission info table simulation for DmtPrincipal.
	 * It is String -> PermissionInfo[]
	 */
	Map principalPermissions;
	public Map getPrincipalPermissions() { return principalPermissions; }
	public void setPrincipalPermissions(Map permissions) { principalPermissions = permissions; }

	public void setUp() throws Exception {
		super.setUp();
		principalPermissions = new HashMap();
		context.properties.put("dataRootURIs",ROOT);
		context.services.put("dmtPrincipalPermissionAdmin",this);
		plugin = new PluginFactory();
		context.doActivate(plugin);
		addDataPlugin(ROOT,plugin);
	}

	public void tearDown() throws Exception {
		plugin = null;
		principalPermissions = null;
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

	public void testEmptyTable() throws Exception {
		newSession();
		String[] children = dmtSession.getChildNodeNames(ROOT);
		assertEquals(0,children.length);
	}

	public void testFilledTable() throws Exception {
		principalPermissions.put(PRINCIPAL1,new PermissionInfo[]{});
		principalPermissions.put(PRINCIPAL2,new PermissionInfo[]{});
		newSession();
		String[] children = dmtSession.getChildNodeNames(ROOT);
		assertEquals(2,children.length);
		Arrays.sort(children);
		assertEquals(PRINCIPAL1,children[0]);
		assertEquals(PRINCIPAL2,children[1]);
	}
	
	public void testCreateNew() throws Exception {
		newAtomicSession();
		dmtSession.createInteriorNode("1");
		dmtSession.setNodeValue("1/Principal",new DmtData(PRINCIPAL1));
		dmtSession.setNodeValue("1/PermissionInfo",new DmtData(ADMINPERMISSION.getEncoded()));
		dmtSession.close();
		PermissionInfo[] pi = (PermissionInfo[]) principalPermissions.get(PRINCIPAL1);
		assertEquals(1,pi.length);
		assertEquals(ADMINPERMISSION,pi[0]);
	}

	public void testDelete() throws Exception {
		principalPermissions.put(PRINCIPAL1,new PermissionInfo[]{});
		principalPermissions.put(PRINCIPAL2,new PermissionInfo[]{});
		newAtomicSession();
		dmtSession.deleteNode(PRINCIPAL1);
		dmtSession.close();
		assertNull(principalPermissions.get(PRINCIPAL1));
		assertNotNull(principalPermissions.get(PRINCIPAL2));
	}
	
	public void testDeleteSub() throws Exception {
		// you cannot delete the Principal and PermissionInfo nodes
		principalPermissions.put(PRINCIPAL1,new PermissionInfo[]{});
		newAtomicSession();
		try {
			dmtSession.deleteNode(PRINCIPAL1+"/Principal");
			fail();
		} catch (DmtException e) {}
		try {
			dmtSession.deleteNode(PRINCIPAL1+"/PermissionInfo");
			fail();
		} catch (DmtException e) {}
	}

	public void testPermissionChange() throws Exception {
		principalPermissions.put(PRINCIPAL1,new PermissionInfo[]{});
		newAtomicSession();
		dmtSession.setNodeValue(PRINCIPAL1+"/PermissionInfo",new DmtData(ADMINPERMISSION.getEncoded()));
		dmtSession.close();
		PermissionInfo[] pi = (PermissionInfo[]) principalPermissions.get(PRINCIPAL1);
		assertNotNull(pi);
		assertEquals(1,pi.length);
		assertEquals(ADMINPERMISSION,pi[0]);
	}
	
	public void testCreateLeafNodes() throws Exception {
		// you cannot create any leaf nodes at all. There's no such thing in first level,
		// they get automatically created on the second level.
		newAtomicSession();
		
		try {
			dmtSession.createLeafNode("Foo",new DmtData("Bar"));
			fail();
		} catch (DmtException e) {}
		
		dmtSession.createInteriorNode("1");
		
		// already exists
		try {
			dmtSession.createLeafNode("1/PermissionInfo",new DmtData(ADMINPERMISSION.getEncoded()));
			fail();
		} catch (DmtException e) {}
		
		// cannot create anything else, either
		try {
			dmtSession.createLeafNode("1/Foo",new DmtData("Bar"));
			fail();
		} catch (DmtException e) {}
	}
	
	public void testMultiplePermissionsSet() throws Exception {
		newAtomicSession();
		dmtSession.createInteriorNode("1");
		dmtSession.setNodeValue("1/Principal",new DmtData(PRINCIPAL1));
		dmtSession.setNodeValue("1/PermissionInfo",
				new DmtData(ADMINPERMISSION.getEncoded()+"\n"+IMPORTPERMISSION.getEncoded()+"\n"));
		dmtSession.close();
		PermissionInfo pi[] = (PermissionInfo[]) principalPermissions.get(PRINCIPAL1);
		assertEquals(2,pi.length);
		assertEquals(ADMINPERMISSION,pi[0]);
		assertEquals(IMPORTPERMISSION,pi[1]);
	}
	
	public void testGetValue() throws Exception {
		principalPermissions.put(PRINCIPAL1,new PermissionInfo[0]);
		principalPermissions.put(PRINCIPAL2,new PermissionInfo[]{ADMINPERMISSION});
		newSession();
		assertEquals(PRINCIPAL1,dmtSession.getNodeValue(PRINCIPAL1+"/Principal").getString());
		assertEquals("",dmtSession.getNodeValue(PRINCIPAL1+"/PermissionInfo").getString());
		assertEquals(PRINCIPAL2,dmtSession.getNodeValue(PRINCIPAL2+"/Principal").getString());
		assertEquals(ADMINPERMISSION.getEncoded()+"\n",dmtSession.getNodeValue(PRINCIPAL2+"/PermissionInfo").getString());
	}
	
	public void testMultiplePermissionGet() throws Exception {
		principalPermissions.put(PRINCIPAL1,new PermissionInfo[]{ADMINPERMISSION,IMPORTPERMISSION});
		newAtomicSession();
		String str = dmtSession.getNodeValue(PRINCIPAL1+"/PermissionInfo").getString();
		assertEquals(ADMINPERMISSION.getEncoded()+"\n"+IMPORTPERMISSION.getEncoded()+"\n",str);
	}

	public void testEmptyPrincipal() throws Exception {
		newAtomicSession();
		dmtSession.createInteriorNode("1");
		try {
			dmtSession.close();
			fail();
		} catch (DmtException e) {}
	}

	public void testMultiplePrincipals() throws Exception {
		newAtomicSession();
		dmtSession.createInteriorNode("1");
		dmtSession.setNodeValue("1/Principal",new DmtData(PRINCIPAL1));
		dmtSession.createInteriorNode("2");
		dmtSession.setNodeValue("2/Principal",new DmtData(PRINCIPAL1));
		try {
			dmtSession.close();
			fail();
		} catch (DmtException e) {}
	}

	public void testNonAtomicWrite() throws Exception {
		newSession();
		try {
			dmtSession.createInteriorNode("1");
			fail();
		} catch (DmtException e) {}
		
	}
}

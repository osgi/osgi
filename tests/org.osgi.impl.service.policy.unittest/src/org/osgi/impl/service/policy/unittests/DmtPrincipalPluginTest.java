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
import java.util.HashMap;
import java.util.Map;
import org.osgi.framework.AdminPermission;
import org.osgi.framework.PackagePermission;
import org.osgi.impl.service.dmt.api.DmtPrincipalPermissionAdmin;
import org.osgi.impl.service.policy.dmtprincipal.DmtPrincipalPlugin;
import org.osgi.impl.service.policy.unittests.util.DmtPluginTestCase;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.permissionadmin.PermissionInfo;

/**
 *
 * TODO Add Javadoc comment for this type.
 * 
 * @version $Revision$
 */
public class DmtPrincipalPluginTest extends DmtPluginTestCase implements DmtPrincipalPermissionAdmin {
	public static final String PRINCIPAL1 = "principal1";
	public static final String PRINCIPAL1_HASH = "zDcCo9K+A67rtQI3TQEDg6_LEIw";
	public static final String PRINCIPAL2 = "principal2";
	public static final String PRINCIPAL2_HASH = "aIUZn8KSmU04HKUyaecKhd0jWsY";
	public static final PermissionInfo ADMINPERMISSION = new PermissionInfo(AdminPermission.class.getName(),"*","*");
	public static final PermissionInfo IMPORTPERMISSION = new PermissionInfo(PackagePermission.class.getName(),"org.osgi.framework","IMPORT");
	
	/**
	 * the plugin currently tested
	 */
	public DmtPrincipalPlugin plugin;

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
		plugin = new DmtPrincipalPlugin(this);
		addDataPlugin(DmtPrincipalPlugin.dataRootURI,plugin);
	}

	public void tearDown() throws Exception {
		plugin = null;
		principalPermissions = null;
		dmtSession = null;
		super.tearDown();
	}

	public void newSession() throws DmtException {
		dmtSession = dmtFactory.getSession(DmtPrincipalPlugin.dataRootURI);
		assertNotNull(dmtSession);
	}

	public void newAtomicSession() throws DmtException {
		dmtSession = dmtFactory.getSession(DmtPrincipalPlugin.dataRootURI,DmtSession.LOCK_TYPE_ATOMIC);
		assertNotNull(dmtSession);
	}

	public void testEmptyTable() throws Exception {
		newSession();
		String[] children = dmtSession.getChildNodeNames(DmtPrincipalPlugin.dataRootURI);
		assertEquals(0,children.length);
	}

	public void testFilledTable() throws Exception {
		principalPermissions.put(PRINCIPAL1,new PermissionInfo[]{});
		principalPermissions.put(PRINCIPAL2,new PermissionInfo[]{});
		newSession();
		String[] children = dmtSession.getChildNodeNames(DmtPrincipalPlugin.dataRootURI);
		assertEquals(2,children.length);
		Arrays.sort(children);
		assertEquals(PRINCIPAL2_HASH,children[0]);
		assertEquals(PRINCIPAL1_HASH,children[1]);
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
		dmtSession.deleteNode(PRINCIPAL1_HASH);
		dmtSession.close();
		assertNull(principalPermissions.get(PRINCIPAL1));
		assertNotNull(principalPermissions.get(PRINCIPAL2));
	}
	
	public void testDeleteSub() throws Exception {
		// you cannot delete the Principal and PermissionInfo nodes
		principalPermissions.put(PRINCIPAL1,new PermissionInfo[]{});
		newAtomicSession();
		try {
			dmtSession.deleteNode(PRINCIPAL1_HASH+"/Principal");
			fail();
		} catch (DmtException e) {}
		try {
			dmtSession.deleteNode(PRINCIPAL1_HASH+"/PermissionInfo");
			fail();
		} catch (DmtException e) {}
	}

	public void testPermissionChange() throws Exception {
		principalPermissions.put(PRINCIPAL1,new PermissionInfo[]{});
		newAtomicSession();
		dmtSession.setNodeValue(PRINCIPAL1_HASH+"/PermissionInfo",new DmtData(ADMINPERMISSION.getEncoded()));
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
		assertEquals(PRINCIPAL1,dmtSession.getNodeValue(PRINCIPAL1_HASH+"/Principal").getString());
		assertEquals("",dmtSession.getNodeValue(PRINCIPAL1_HASH+"/PermissionInfo").getString());
		assertEquals(PRINCIPAL2,dmtSession.getNodeValue(PRINCIPAL2_HASH+"/Principal").getString());
		assertEquals(ADMINPERMISSION.getEncoded()+"\n",dmtSession.getNodeValue(PRINCIPAL2_HASH+"/PermissionInfo").getString());
	}
	
	public void testMultiplePermissionGet() throws Exception {
		principalPermissions.put(PRINCIPAL1,new PermissionInfo[]{ADMINPERMISSION,IMPORTPERMISSION});
		newAtomicSession();
		String str = dmtSession.getNodeValue(PRINCIPAL1_HASH+"/PermissionInfo").getString();
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

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
import org.osgi.impl.service.policy.permadmin.PluginFactory;
import org.osgi.impl.service.policy.unittests.util.DmtPluginTestCase;
import org.osgi.service.permissionadmin.PermissionInfo;

/**
 *
 * TODO Add Javadoc comment for this type.
 * 
 * @version $Revision$
 */
public class PermissionAdminPluginTest extends DmtPluginTestCase {
	public static final String ROOT = "./OSGi/Policy/Java/LocationPermission";

	public static final String LOCATION1 = "http://location1";
	public static final String LOCATION1_HASH = "http:\\/\\/location1";

	public static final String LOCATION2 = "http://location2";
	public static final String LOCATION2_HASH = "http:\\/\\/location2";

	public DummyPermissionAdmin	permAdmin;
	public PluginFactory	plugin;
	public static final PermissionInfo ADMINPERMISSION = new PermissionInfo(AdminPermission.class.getName(),"","");
	public static final PermissionInfo IMPORTFRAMEWORKPERMISSION 
		= new PermissionInfo(PackagePermission.class.getName(),"org.osgi.framework","IMPORT");

	private DmtSession	dmtSession;

	public void setUp() throws Exception {
		super.setUp();
		permAdmin = new DummyPermissionAdmin();
		context.properties.put("dataRootURIs",ROOT);
		context.services.put("permissionAdmin",permAdmin);
		plugin = new PluginFactory();
		context.doActivate(plugin);
		addDataPlugin(ROOT,plugin);
	}
	
	public void tearDown() throws Exception {
		permAdmin = null;
		plugin = null;
		dmtSession=null;
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
		MetaNode mn = dmtSession.getMetaNode(ROOT);
	}
	
	public void testEmptyTree() throws Exception {
		newSession();
		String[] childNames = dmtSession.getChildNodeNames(ROOT);
		assertEquals(1,childNames.length);
		assertEquals("Locations",childNames[0]);
	}

	public void testEmptyDefault() throws Exception {
		permAdmin.setDefaultPermissions(new PermissionInfo[] {});
		newSession();
		String[] childNames = dmtSession.getChildNodeNames(ROOT);
		assertEquals(2,childNames.length);
		Arrays.sort(childNames);
		assertEquals("Default",childNames[0]);
		assertEquals("Locations",childNames[1]);
	}

	public void testSimpleDefault() throws Exception {
		permAdmin.setDefaultPermissions(new PermissionInfo[] {ADMINPERMISSION});
		newSession();
		DmtData pinfo = dmtSession.getNodeValue(ROOT+"/Default");
		assertEquals(ADMINPERMISSION.getEncoded()+"\n",pinfo.getString());
	}
	
	public void testRenameDefault() throws Exception {
		permAdmin.setDefaultPermissions(new PermissionInfo[] {ADMINPERMISSION});
		newSession();
		try {
			dmtSession.renameNode(ROOT+"/Default","Foo");
			fail();
		} catch (DmtException e){}
	}
	
	public void testCreateAlreadyExistingDefault() throws Exception {
		permAdmin.setDefaultPermissions(new PermissionInfo[] {ADMINPERMISSION});
		newSession();
		try {
			dmtSession.createLeafNode(ROOT+"/Default");
			fail();
		} catch (DmtException e){};
	}
	
	public void testCreateDefault() throws Exception {
		newAtomicSession();
		dmtSession.createLeafNode(ROOT+"/Default");
		dmtSession.setNodeValue(ROOT+"/Default",new DmtData(ADMINPERMISSION.getEncoded()));
		dmtSession.close();
		PermissionInfo[] permissionInfo = permAdmin.getDefaultPermissions();
		assertEquals(1,permissionInfo.length);
	}

	public void testRemoveDefault() throws Exception {
		permAdmin.setDefaultPermissions(new PermissionInfo[] {ADMINPERMISSION});
		newAtomicSession();
		dmtSession.deleteNode(ROOT+"/Default");
		dmtSession.close();
		PermissionInfo[] permissionInfo = permAdmin.getDefaultPermissions();
		assertNull(permissionInfo);
	}
	
	public void testNonAtomicWrite() throws Exception {
		// changing the tree is only allowed if the session is atomic
		newSession();
		try {
			dmtSession.createInteriorNode("Location/1");
			fail();
		} catch (DmtException e) {}
	}
	
	public void testRollback1() throws Exception {
		permAdmin.setDefaultPermissions(new PermissionInfo[] {ADMINPERMISSION});
		newAtomicSession();
		dmtSession.deleteNode(ROOT+"/Default");
		dmtSession.rollback();
		PermissionInfo[] permissionInfo = permAdmin.getDefaultPermissions();
		assertEquals(1,permissionInfo.length);
	}
	
	public void testBasicPermissionRead() throws Exception {
		permAdmin.setPermissions(LOCATION1, new PermissionInfo[] {ADMINPERMISSION});
		newSession();
		String pis = dmtSession.getNodeValue(ROOT+"/Locations/"+LOCATION1_HASH+"/PermissionInfo").getString();
		assertEquals(ADMINPERMISSION.getEncoded()+"\n",pis);
	}
	
	public void testRootTreeChildren() throws Exception {
		permAdmin.setDefaultPermissions(new PermissionInfo[] {ADMINPERMISSION});
		permAdmin.setPermissions(LOCATION1, new PermissionInfo[] {ADMINPERMISSION});
		permAdmin.setPermissions(LOCATION2, new PermissionInfo[] {ADMINPERMISSION});
		newSession();
		String[] children = dmtSession.getChildNodeNames(ROOT);
		Arrays.sort(children);
		assertEquals(2,children.length);
		assertEquals("Default",children[0]);
		assertEquals("Locations",children[1]);
		children = dmtSession.getChildNodeNames("Locations");
		assertEquals(2,children.length);
		Arrays.sort(children);
		assertEquals(LOCATION1_HASH,children[0]);
		assertEquals(LOCATION2_HASH,children[1]);
	}
	
	public void testCreatePermission() throws Exception {
		newAtomicSession();
		dmtSession.createInteriorNode("Locations/1");
		dmtSession.setNodeValue("Locations/1/Location",new DmtData(LOCATION1));
		dmtSession.setNodeValue("Locations/1/PermissionInfo",new DmtData(ADMINPERMISSION.getEncoded()));
		dmtSession.close();
		PermissionInfo pi[] = permAdmin.getPermissions(LOCATION1);
		assertEquals(1,pi.length);
		assertEquals(ADMINPERMISSION,pi[0]);
	}
	
	public void testRemovePermission() throws Exception {
		permAdmin.setPermissions(LOCATION1,new PermissionInfo[]{ADMINPERMISSION});
		newAtomicSession();
		dmtSession.deleteNode("Locations/"+LOCATION1_HASH);
		dmtSession.close();
		PermissionInfo pi[] = permAdmin.getPermissions(LOCATION1);
		assertNull(pi);
	}

	public void testChangePermission() throws Exception {
		permAdmin.setPermissions(LOCATION1,new PermissionInfo[]{ADMINPERMISSION});
		newAtomicSession();
		dmtSession.setNodeValue("Locations/"+LOCATION1_HASH+"/PermissionInfo",new DmtData(IMPORTFRAMEWORKPERMISSION.getEncoded()));
		dmtSession.close();
		PermissionInfo pi[] = permAdmin.getPermissions(LOCATION1);
		assertEquals(1,pi.length);
		assertEquals(IMPORTFRAMEWORKPERMISSION,pi[0]);
	}
	
	public void testPermissionChildNodes() throws Exception {
		permAdmin.setPermissions(LOCATION1,new PermissionInfo[]{ADMINPERMISSION});
		newAtomicSession();
		String[] names = dmtSession.getChildNodeNames("Locations/"+LOCATION1_HASH);
		Arrays.sort(names);
		assertEquals(2,names.length);
		assertEquals("Location",names[0]);
		assertEquals("PermissionInfo",names[1]);
	}
}

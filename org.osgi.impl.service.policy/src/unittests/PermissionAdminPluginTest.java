/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
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
package unittests;

import java.util.Arrays;
import org.osgi.framework.AdminPermission;
import org.osgi.framework.PackagePermission;
import org.osgi.impl.service.policy.permadmin.PermissionAdminPlugin;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtMetaNode;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.permissionadmin.PermissionInfo;
import unittests.util.DmtPluginTestCase;

/**
 *
 * TODO Add Javadoc comment for this type.
 * 
 * @version $Revision$
 */
public class PermissionAdminPluginTest extends DmtPluginTestCase {
	public static final String ROOT = PermissionAdminPlugin.dataRootURI;

	public static final String LOCATION1 = "http://location1";
	public static final String LOCATION1_HASH = "W+7i9Qa7tsvxf7Z9COBtdKgvKhM";

	public static final String LOCATION2 = "http://location2";
	public static final String LOCATION2_HASH = "xQrRNwWiEbyK3UXtpqgTb36LdZk";

	public DummyPermissionAdmin	permAdmin;
	public PermissionAdminPlugin	plugin;
	public static final PermissionInfo ADMINPERMISSION = new PermissionInfo(AdminPermission.class.getName(),"","");
	public static final PermissionInfo IMPORTFRAMEWORKPERMISSION 
		= new PermissionInfo(PackagePermission.class.getName(),"org.osgi.framework","IMPORT");

	private DmtSession	dmtSession;

	public void setUp() throws Exception {
		super.setUp();
		permAdmin = new DummyPermissionAdmin();
		plugin = new PermissionAdminPlugin(permAdmin);
		addDataPlugin(PermissionAdminPlugin.dataRootURI,plugin);
	}
	
	public void tearDown() throws Exception {
		permAdmin = null;
		plugin = null;
		dmtSession=null;
		super.tearDown();
	}

	public void newSession() throws DmtException {
		dmtSession = dmtFactory.getTree(null,ROOT);
		assertNotNull(dmtSession);
	}

	public void newAtomicSession() throws DmtException {
		dmtSession = dmtFactory.getTree(null,ROOT,DmtSession.LOCK_TYPE_ATOMIC);
		assertNotNull(dmtSession);
	}
	
	public void testRegister() throws Exception {
		newSession();
		DmtMetaNode mn = dmtSession.getMetaNode(ROOT);
	}
	
	public void testEmptyTree() throws Exception {
		newSession();
		String[] childNames = dmtSession.getChildNodeNames(ROOT);
		assertEquals(0,childNames.length);
	}

	public void testEmptyDefault() throws Exception {
		permAdmin.setDefaultPermissions(new PermissionInfo[] {});
		newSession();
		String[] childNames = dmtSession.getChildNodeNames(ROOT);
		assertEquals(1,childNames.length);
		assertEquals("Default",childNames[0]);
	}

	public void testSimpleDefault() throws Exception {
		permAdmin.setDefaultPermissions(new PermissionInfo[] {ADMINPERMISSION});
		newSession();
		DmtData pinfo = dmtSession.getNodeValue(ROOT+"/Default/PermissionInfo");
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
			dmtSession.createInteriorNode(ROOT+"/Default");
			fail();
		} catch (DmtException e){};
	}
	
	public void testCreateDefault() throws Exception {
		newSession();
		dmtSession.createInteriorNode(ROOT+"/Default");
		dmtSession.setNodeValue(ROOT+"/Default/PermissionInfo",new DmtData(ADMINPERMISSION.getEncoded()));
		dmtSession.close();
		PermissionInfo[] permissionInfo = permAdmin.getDefaultPermissions();
		assertEquals(1,permissionInfo.length);
	}

	public void testRemoveDefault() throws Exception {
		permAdmin.setDefaultPermissions(new PermissionInfo[] {ADMINPERMISSION});
		newSession();
		dmtSession.deleteNode(ROOT+"/Default");
		dmtSession.close();
		PermissionInfo[] permissionInfo = permAdmin.getDefaultPermissions();
		assertNull(permissionInfo);
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
		String pis = dmtSession.getNodeValue(ROOT+"/"+LOCATION1_HASH+"/PermissionInfo").getString();
		assertEquals(ADMINPERMISSION.getEncoded()+"\n",pis);
	}
	
	public void testRootTreeChildren() throws Exception {
		permAdmin.setDefaultPermissions(new PermissionInfo[] {ADMINPERMISSION});
		permAdmin.setPermissions(LOCATION1, new PermissionInfo[] {ADMINPERMISSION});
		permAdmin.setPermissions(LOCATION2, new PermissionInfo[] {ADMINPERMISSION});
		newSession();
		String[] children = dmtSession.getChildNodeNames(ROOT);
		Arrays.sort(children);
		assertEquals(3,children.length);
		assertEquals("Default",children[0]);
		assertEquals(LOCATION1_HASH,children[1]);
		assertEquals(LOCATION2_HASH,children[2]);
	}
	
	public void testCreatePermission() throws Exception {
		newAtomicSession();
		dmtSession.createInteriorNode("1");
		dmtSession.setNodeValue("1/Location",new DmtData(LOCATION1));
		dmtSession.setNodeValue("1/PermissionInfo",new DmtData(ADMINPERMISSION.getEncoded()));
		dmtSession.close();
		PermissionInfo pi[] = permAdmin.getPermissions(LOCATION1);
		assertEquals(1,pi.length);
		assertEquals(ADMINPERMISSION,pi[0]);
	}
	
	public void testRemovePermission() throws Exception {
		permAdmin.setPermissions(LOCATION1,new PermissionInfo[]{ADMINPERMISSION});
		newAtomicSession();
		dmtSession.deleteNode(LOCATION1_HASH);
		dmtSession.close();
		PermissionInfo pi[] = permAdmin.getPermissions(LOCATION1);
		assertNull(pi);
	}

	public void testChangePermission() throws Exception {
		permAdmin.setPermissions(LOCATION1,new PermissionInfo[]{ADMINPERMISSION});
		newAtomicSession();
		dmtSession.setNodeValue(LOCATION1_HASH+"/PermissionInfo",new DmtData(IMPORTFRAMEWORKPERMISSION.getEncoded()));
		dmtSession.close();
		PermissionInfo pi[] = permAdmin.getPermissions(LOCATION1);
		assertEquals(1,pi.length);
		assertEquals(IMPORTFRAMEWORKPERMISSION,pi[0]);
	}
}

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

import org.osgi.framework.AdminPermission;
import org.osgi.impl.service.policy.permadmin.PermissionAdminPlugin;
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
	public DummyPermissionAdmin	permAdmin;
	public PermissionAdminPlugin	plugin;
	public static final PermissionInfo ADMINPERMISSION = new PermissionInfo(AdminPermission.class.getName(),"","");

	public void setUp() throws Exception {
		super.setUp();
		permAdmin = new DummyPermissionAdmin();
		plugin = new PermissionAdminPlugin(permAdmin);
		addDataPlugin(PermissionAdminPlugin.dataRootURI,plugin);
	}
	
	public void tearDown() throws Exception {
		permAdmin = null;
		plugin = null;
		super.tearDown();
	}

	public void testRegister() throws Exception {
		DmtSession dmtSession = dmtFactory.getTree(null,ROOT);
		DmtMetaNode mn = dmtSession.getMetaNode(ROOT);
	}
	
	public void testEmptyTree() throws Exception {
		DmtSession dmtSession = dmtFactory.getTree(null,ROOT);
		String[] childNames = dmtSession.getChildNodeNames(ROOT);
		assertEquals(0,childNames.length);
	}

	public void testEmptyDefault() throws Exception {
		permAdmin.setDefaultPermissions(new PermissionInfo[] {});
		DmtSession dmtSession = dmtFactory.getTree(null,ROOT);
		String[] childNames = dmtSession.getChildNodeNames(ROOT);
		assertEquals(1,childNames.length);
		assertEquals("Default",childNames[0]);
	}

}

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
import java.util.HashMap;
import java.util.Map;
import org.osgi.framework.AdminPermission;
import org.osgi.impl.service.dmt.api.DmtPrincipalPermissionAdmin;
import org.osgi.impl.service.policy.dmtprincipal.DmtPrincipalPlugin;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.permissionadmin.PermissionInfo;
import unittests.util.DmtPluginTestCase;

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
}

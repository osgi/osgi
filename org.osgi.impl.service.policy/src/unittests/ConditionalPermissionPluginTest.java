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

import org.osgi.framework.PackagePermission;
import org.osgi.framework.ServicePermission;
import org.osgi.impl.service.policy.condpermadmin.ConditionalPermissionAdminPlugin;
import org.osgi.service.condpermadmin.BundleLocationCondition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtMetaNode;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.http.HttpService;
import org.osgi.service.permissionadmin.PermissionInfo;
import unittests.util.DmtPluginTestCase;

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
	public static final String RFC_EXAMPLEHASH = "WovYXjHL_EgRTOVWHgipOk82tt8";
	public static final ConditionInfo[] RFC_EXAMPLECOND = new ConditionInfo[] {LOC1CONDITION};
	public static final PermissionInfo[] RFC_EXAMPLEPERM = new PermissionInfo[] {HTTPREGISTERPERMISSION,OSGIIMPORTPERMISSION};

	
	public void setUp() throws Exception {
		super.setUp();
		condPermAdmin = new DummyConditionalPermissionAdmin();
		plugin = new ConditionalPermissionAdminPlugin(condPermAdmin);
		addDataPlugin(ConditionalPermissionAdminPlugin.dataRootURI,plugin);
	}
	
	public void tearDown() throws Exception {
		condPermAdmin = null;
		plugin = null;
		dmtSession = null;
		super.tearDown();
	}

	public void newSession() throws DmtException {
		dmtSession = dmtFactory.getSession(ConditionalPermissionAdminPlugin.dataRootURI);
		assertNotNull(dmtSession);
	}

	public void newAtomicSession() throws DmtException {
		dmtSession = dmtFactory.getSession(ConditionalPermissionAdminPlugin.dataRootURI,DmtSession.LOCK_TYPE_ATOMIC);
		assertNotNull(dmtSession);
	}
	
	public void testRegister() throws Exception {
		newSession();
	}

	public void testRootMetaNode() throws Exception {
		newSession();
		DmtMetaNode mn = dmtSession.getMetaNode(ConditionalPermissionAdminPlugin.dataRootURI);
		assertNotNull(mn);
		assertEquals(false,mn.canDelete());
	}
	
	public void testRFCHashExample() throws Exception {
		condPermAdmin.addCollection(RFC_EXAMPLECOND,RFC_EXAMPLEPERM);
		newSession();
		DmtData pis = dmtSession.getNodeValue(RFC_EXAMPLEHASH+"/PermissionInfo");
		assertEquals(RFC_EXAMPLEPERM[0].getEncoded()+"\n"+RFC_EXAMPLEPERM[1].getEncoded()+"\n",pis.getString());
		DmtData cis = dmtSession.getNodeValue(RFC_EXAMPLEHASH+"/ConditionInfo");
		assertEquals(RFC_EXAMPLECOND[0].getEncoded()+"\n",cis.getString());
	}
}

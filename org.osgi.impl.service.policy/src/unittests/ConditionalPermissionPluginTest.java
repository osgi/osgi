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

import org.osgi.impl.service.policy.condpermadmin.ConditionalPermissionAdminPlugin;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtMetaNode;
import org.osgi.service.dmt.DmtSession;
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
}

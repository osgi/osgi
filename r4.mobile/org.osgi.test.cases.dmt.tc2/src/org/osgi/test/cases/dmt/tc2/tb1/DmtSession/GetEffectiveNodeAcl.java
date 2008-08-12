/* 
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

/*
 * REVISION HISTORY:
 * 
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Feb 28, 2005  Luiz Felipe Guimaraes
 * 1		     Updates 
 * ============  =================================================================
 */
package org.osgi.test.cases.dmt.tc2.tb1.DmtSession;

import info.dmtree.Acl;
import info.dmtree.DmtException;
import info.dmtree.security.DmtPermission;
import info.dmtree.security.DmtPrincipalPermission;
import info.dmtree.DmtSession;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.TestInterface;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * This Test Case Validates the implementation of <code>getEffectiveNodeAcl</code> method of DmtSession, 
 * according to MEG specification
 */

public class GetEffectiveNodeAcl implements TestInterface {
	private DmtTestControl tbc;

	public GetEffectiveNodeAcl(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
        prepare();
		testGetEffectiveNodeAcl001();
		testGetEffectiveNodeAcl002();
		testGetEffectiveNodeAcl003();
		testGetEffectiveNodeAcl004();
		testGetEffectiveNodeAcl005();
		testGetEffectiveNodeAcl006();
		testGetEffectiveNodeAcl007();
	}

    private void prepare() {
        tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
    }
	/**
	 *	Tests if when there is no ACL defined for the node, it
	 *  will be derived from the closest ancestor having an ACL
	 *  defined.
	 * 
	 * @spec DmtSession.getEffectiveNodeAcl(String)
	 */	
	private void testGetEffectiveNodeAcl001() {
		DmtSession session = null;
		try {
			tbc.log("#testGetEffectiveNodeAcl001");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			Acl acl = new Acl(DmtConstants.ACLSTR);

			session.setNodeAcl(TestExecPluginActivator.ROOT, acl);
			tbc.assertEquals("Asserting the effective node ", acl.toString(),
					session.getEffectiveNodeAcl(TestExecPluginActivator.CHILD_INTERIOR_NODE)
							.toString());
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session, TestExecPluginActivator.ROOT);			
		}

	}

	
	/**
	 *	Tests if when there is an ACL defined for the node, it
	 *  will be derived from the ACL defined
	 * 
	 * @spec DmtSession.getEffectiveNodeAcl(String)
	 */		
	private void testGetEffectiveNodeAcl002() {
		DmtSession session = null;
		try {
			tbc.log("#testGetEffectiveNodeAcl002");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			Acl acl = new Acl(DmtConstants.ACLSTR);
			session.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE, acl);
			tbc.assertEquals("Asserting the effective node ", acl.toString(),
					session.getEffectiveNodeAcl(TestExecPluginActivator.INTERIOR_NODE)
							.toString());
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session, TestExecPluginActivator.INTERIOR_NODE);
		}
	}

	/**
	 * This method asserts that DmtException.NODE_NOT_FOUND is thrown
	 * if nodeUri points to a non-existing node 
	 * 
	 * @spec DmtSession.getEffectiveNodeAcl(String)
	 */			
	private void testGetEffectiveNodeAcl003() {
		DmtSession session = null;
		try {
			tbc.log("#testGetEffectiveNodeAcl003");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);

			session.getEffectiveNodeAcl(TestExecPluginActivator.INEXISTENT_NODE);

			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException's code is NODE_NOT_FOUND",
					DmtException.NODE_NOT_FOUND, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}

	
	
	/**
	 * This method asserts that getEffectiveNodeAcl is executed when the right Acl is set (Remote)
	 * 
	 * @spec DmtSession.getEffectiveNodeAcl(String)
	 */
	private void testGetEffectiveNodeAcl004() {
		DmtSession session = null;
		try {
			tbc.log("#testGetEffectiveNodeAcl004");

            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.PRINCIPAL, Acl.GET );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,
					TestExecPluginActivator.INTERIOR_NODE, DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.getEffectiveNodeAcl(TestExecPluginActivator.INTERIOR_NODE);

			tbc.pass("getEffectiveNodeAcl was successfully executed");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);

		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session,TestExecPluginActivator.INTERIOR_NODE);
            
		}

	}
	
	/**
	 * This method asserts that getNodeType is executed when the right DmtPermission is set (Local)
	 * 
	 * @spec DmtSession.getEffectiveNodeAcl(String)
	 */
	private void testGetEffectiveNodeAcl005() {
		DmtSession session = null;
		try {
			tbc.log("#testGetEffectiveNodeAcl005");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtConstants.ALL_NODES, DmtPermission.GET));
			
			session.getEffectiveNodeAcl(DmtConstants.OSGi_LOG);
			
			tbc.pass("getEffectiveNodeAcl was successfully executed");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);

		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, null);
            
		}

	}
	
	/**
	 * This method asserts that relative URI works as described.
	 * 
	 * @spec DmtSession.getEffectiveNodeAcl(String)
	 */
	private void testGetEffectiveNodeAcl006() {
		DmtSession session = null;
		try {
			tbc.log("#testGetEffectiveNodeAcl006");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.getEffectiveNodeAcl(TestExecPluginActivator.INTERIOR_NODE_NAME);

			tbc.pass("A relative URI can be used with getEffectiveNodeAcl.");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}		
	/**
	 * This method asserts that an empty string as relative URI means the root 
	 * URI the session was opened with
	 * 
	 * @spec DmtSession.getEffectiveNodeAcl(String)
	 */
	private void testGetEffectiveNodeAcl007() {
		DmtSession session = null;
		try {
			tbc.log("#testGetEffectiveNodeAcl007");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.INTERIOR_NODE, DmtSession.LOCK_TYPE_ATOMIC);

			session.getEffectiveNodeAcl("");

			tbc.pass("Asserts that an empty string as relative URI means the root " +
					"URI the session was opened with");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}	
}

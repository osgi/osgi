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
package org.osgi.test.cases.dmt.main.tb1.DmtSession;

import org.osgi.service.dmt.DmtAcl;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtPermission;
import org.osgi.service.dmt.DmtPrincipalPermission;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.main.tbc.TestInterface;
import org.osgi.test.cases.dmt.main.tbc.Plugin.TestExecPluginActivator;

/**
 * @methodUnderTest org.osgi.service.dmt.DmtSession#getEffectiveNodeAcl
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>getEffectiveNodeAcl<code> method, according to MEG reference
 *                     documentation (rfc0085).
 * @author Luiz Felipe Guimaraes
 */

public class GetEffectiveNodeAcl implements TestInterface {
	private DmtTestControl tbc;

	public GetEffectiveNodeAcl(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetEffectiveNodeAcl001();
		testGetEffectiveNodeAcl002();
		testGetEffectiveNodeAcl003();
		testGetEffectiveNodeAcl004();
		testGetEffectiveNodeAcl005();
		testGetEffectiveNodeAcl006();
		testGetEffectiveNodeAcl007();
		testGetEffectiveNodeAcl008();
		testGetEffectiveNodeAcl009();
		testGetEffectiveNodeAcl010();
		testGetEffectiveNodeAcl011();
		testGetEffectiveNodeAcl012();
	}

	/**
	 * @testID testGetEffectiveNodeAcl001
	 * @testDescription Tests if when there is no ACL defined for the node, it
	 *                  will be derived from the closest ancestor having an ACL
	 *                  defined
	 */
	private void testGetEffectiveNodeAcl001() {
		DmtSession session = null;
		try {
			tbc.log("#testGetEffectiveNodeAcl001");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			DmtAcl acl = new DmtAcl(DmtTestControl.ACLSTR);

			session.setNodeAcl(TestExecPluginActivator.ROOT, acl);
			tbc.assertEquals("Asserting the effective node ", acl.toString(),
					session.getEffectiveNodeAcl(TestExecPluginActivator.CHILD_INTERIOR_NODE)
							.toString());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(session, null, TestExecPluginActivator.ROOT);			
		}

	}

	/**
	 * @testID testGetEffectiveNodeAcl002
	 * @testDescription Tests if when there is an ACL defined for the node, it
	 *                  will be derived from the ACL defined
	 */
	private void testGetEffectiveNodeAcl002() {
		DmtSession session = null;
		try {
			tbc.log("#testGetEffectiveNodeAcl002");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			DmtAcl acl = new DmtAcl(DmtTestControl.ACLSTR);
			session.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE, acl);
			tbc.assertEquals("Asserting the effective node ", acl.toString(),
					session.getEffectiveNodeAcl(TestExecPluginActivator.INTERIOR_NODE)
							.toString());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(session, null, TestExecPluginActivator.INTERIOR_NODE);
		}
	}

	/**
	 * @testID testGetEffectiveNodeAcl003
	 * @testDescription Tests if DmtException.NODE_NOT_FOUND is thrown correctly
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
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetEffectiveNodeAcl004
	 * @testDescription Tests if DmtException.URI_TOO_LONG is thrown correctly
	 */
	private void testGetEffectiveNodeAcl004() {
		DmtSession session = null;
		try {
			tbc.log("#testGetEffectiveNodeAcl004");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);
			session.getEffectiveNodeAcl(DmtTestControl.URI_LONG);

			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException's code is URI_TOO_LONG",
					DmtException.URI_TOO_LONG, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetEffectiveNodeAcl005
	 * @testDescription Tests if DmtException.INVALID_URI is thrown correctly
	 */
	private void testGetEffectiveNodeAcl005() {
		DmtSession session = null;
		try {
			tbc.log("#testGetEffectiveNodeAcl005");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);
			
			session.getEffectiveNodeAcl(DmtTestControl.INVALID_URI);
			tbc.failException("#", DmtException.class);

		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException's code is INVALID_URI",
					DmtException.INVALID_URI, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetEffectiveNodeAcl006
	 * @testDescription Tests if DmtException.OTHER_ERROR is thrown correctly
	 */
	private void testGetEffectiveNodeAcl006() {
		DmtSession session = null;
		try {
			tbc.log("#testGetEffectiveNodeAcl006");

			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.getEffectiveNodeAcl(DmtTestControl.OSGi_CFG);

			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException's code is OTHER_ERROR",
					DmtException.OTHER_ERROR, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	
	/**
	 * @testID testGetEffectiveNodeAcl007
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to PERMISSION_DENIED is thrown.
	 */
	private void testGetEffectiveNodeAcl007() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testGetEffectiveNodeAcl007");

			localSession = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE, new DmtAcl(
					new String[] { DmtTestControl.PRINCIPAL },
					new int[] { DmtAcl.EXEC }));
			localSession.close();

			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,
					TestExecPluginActivator.INTERIOR_NODE, DmtSession.LOCK_TYPE_EXCLUSIVE);
			remoteSession.getEffectiveNodeAcl(TestExecPluginActivator.INTERIOR_NODE);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is PERMISSION_DENIED",
					DmtException.PERMISSION_DENIED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(localSession, remoteSession, TestExecPluginActivator.INTERIOR_NODE);
		}

	}

	/**
	 * @testID testGetEffectiveNodeAcl008
	 * @testDescription Tests if an IllegalStateException is thrown
	 *                  when it tries to get the effective node acl after a
	 *                  close().
	 */
	private void testGetEffectiveNodeAcl008() {
		DmtSession session = null;
		try {
			tbc.log("#testGetEffectiveNodeAcl008");
			
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.close();
			
			session.getEffectiveNodeAcl(TestExecPluginActivator.INTERIOR_NODE);
			
			tbc.failException("#", IllegalStateException.class);
		} catch (IllegalStateException e) {
			tbc.pass("The Exception was IllegalStateException");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName()
					+ " but was " + e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}

	}

	/**
	 * @testID testGetEffectiveNodeAcl009
	 * @testDescription This method asserts that SecurityException is thrown
	 *                  when getEffectiveNodeAcl is called without the right
	 *                  permission
	 */
	private void testGetEffectiveNodeAcl009() {
		DmtSession session = null;
		try {
			tbc.log("#testGetEffectiveNodeAcl009");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtTestControl.ALL_NODES, DmtPermission.EXEC));
			
			session.getEffectiveNodeAcl(TestExecPluginActivator.INTERIOR_NODE);
			
			tbc.failException("", SecurityException.class);
		} catch (SecurityException e) {
			tbc.pass("The Exception was SecurityException");
		} catch (Exception e) {
			tbc.fail("Expected " + SecurityException.class.getName()
					+ " but was " + e.getClass().getName());
		} finally {
			tbc.cleanUp(session, null, null);
		}

	}
	
	/**
	 * @testID testGetEffectiveNodeAcl010
	 * @testDescription This method asserts that no exception is thrown
	 *                  when we have set the correct acl permissions.
	 */
	private void testGetEffectiveNodeAcl010() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testGetEffectiveNodeAcl010");

			localSession = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE, new DmtAcl(
					new String[] { DmtTestControl.PRINCIPAL },
					new int[] { DmtAcl.GET }));
			localSession.close();

			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,
					TestExecPluginActivator.INTERIOR_NODE, DmtSession.LOCK_TYPE_EXCLUSIVE);
			remoteSession.getEffectiveNodeAcl(TestExecPluginActivator.INTERIOR_NODE);

			tbc.pass("getEffectiveNodeAcl was successfully executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");

		} finally {
			tbc.cleanUp(localSession, remoteSession, TestExecPluginActivator.INTERIOR_NODE);
		}

	}
	
	/**
	 * @testID testGetEffectiveNodeAcl011
	 * @testDescription This method asserts that no SecurityException is thrown
	 *                  when getEffectiveNodeAcl is called with the right
	 *                  permission
	 */
	private void testGetEffectiveNodeAcl011() {
		DmtSession session = null;
		try {
			tbc.log("#testGetEffectiveNodeAcl011");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtTestControl.ALL_NODES, DmtPermission.GET));
			
			session.getEffectiveNodeAcl(DmtTestControl.OSGi_LOG);
			
			tbc.pass("getEffectiveNodeAcl was successfully executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");

		} finally {
			tbc.cleanUp(session, null, null);
		}

	}
	
	/**
	 * @testID testGetEffectiveNodeAcl012
	 * @testDescription This method asserts that relative URI works as described.
	 * 
	 */
	private void testGetEffectiveNodeAcl012() {
		DmtSession session = null;
		try {
			tbc.log("#testGetEffectiveNodeAcl012");
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtTestControl.ALL_NODES,DmtTestControl.ALL_ACTIONS));
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.getEffectiveNodeAcl(TestExecPluginActivator.INTERIOR_VALUE);

			tbc.pass("A relative URI can be used with getEffectiveNodeAcl.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}		

}

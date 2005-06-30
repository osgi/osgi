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
 * Jan 25, 2005  Andre Assad
 * CR 1          Implement MEG TCK
 * ============  ==============================================================
 * Feb 14, 2005  Alexandre Santos
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ===========  ==============================================================
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
 * @author Andre Assad
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtSession#deleteNode
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>deleteNode<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class DeleteNode implements TestInterface {
	private DmtTestControl tbc;

	public DeleteNode(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {		
		testDeleteNode001();
		testDeleteNode002();
		testDeleteNode003();
		testDeleteNode004();
		testDeleteNode005();
		testDeleteNode006();
		testDeleteNode007();
		testDeleteNode008();
		testDeleteNode009();
		testDeleteNode010();
		testDeleteNode011();

	}

		/**
	 * @testID testDeleteNode001
	 * @testDescription This method tests that a DmtException is thrown using
	 *                  the NODE_NOT_FOUND code whenever an invalid node is
	 *                  deleted.
	 */
	private void testDeleteNode001() {
		DmtSession session = null;
		try {
			tbc.log("#testDeleteNode001");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.deleteNode(TestExecPluginActivator.INEXISTENT_NODE);

			tbc.failException("", DmtException.class);
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
	 * @testID testDeleteNode002
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to OTHER_ERROR is thrown.
	 */
	private void testDeleteNode002() {
		DmtSession session = null;
		try {
			tbc.log("#testDeleteNode002");

			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.deleteNode(TestExecPluginActivator.INTERIOR_NODE);
			
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserting that DmtException code is OTHER_ERROR",
					DmtException.OTHER_ERROR, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testDeleteNode003
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to INVALID_URI is thrown.
	 */
	private void testDeleteNode003() {
		DmtSession session = null;
		try {
			tbc.log("#testDeleteNode003");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.deleteNode(DmtTestControl.INVALID_URI);
			
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserting that DmtException code is INVALID_URI",
					DmtException.INVALID_URI, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testDeleteNode004
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to URI_TOO_LONG is thrown.
	 */
	private void testDeleteNode004() {
		DmtSession session = null;
		try {
			tbc.log("#testDeleteNode004");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.deleteNode(DmtTestControl.URI_LONG);
			
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is URI_TOO_LONG",
					DmtException.URI_TOO_LONG, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testDeleteNode005
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to COMMAND_NOT_ALLOWED is thrown.
	 */
	private void testDeleteNode005() {
		DmtSession session = null;
		try {
			tbc.log("#testDeleteNode005");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.deleteNode(DmtTestControl.OSGi_ROOT);
			
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is COMMAND_NOT_ALLOWED",
					DmtException.COMMAND_NOT_ALLOWED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testDeleteNode006
	 * @testDescription This method asserts that deleteNode is
	 *                  successfully executed when the correct permission is
	 *                  assigned
	 */
	private void testDeleteNode006() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testDeleteNode006");

			localSession = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(
					TestExecPluginActivator.INTERIOR_NODE,
					new DmtAcl(new String[] { DmtTestControl.PRINCIPAL },
							new int[] { DmtAcl.DELETE }));
			localSession.close();
			
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL, TestExecPluginActivator.INTERIOR_NODE,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			remoteSession.deleteNode(TestExecPluginActivator.INTERIOR_NODE);
			
			tbc.pass("deleteNode was successfully executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(localSession, remoteSession, TestExecPluginActivator.INTERIOR_NODE);
		}

	}

	/**
	 * @testID testDeleteNode007
	 * @testDescription This method asserts that deleteNode is successfully executed 
	 * 					when the correct permission is assigned
	 */
	private void testDeleteNode007() {
		DmtSession session = null;
		try {
			tbc.log("#testDeleteNode007");

			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtTestControl.ALL_NODES, DmtPermission.DELETE));

			session.deleteNode(TestExecPluginActivator.INTERIOR_NODE);
			
			tbc.pass("deleteNode was successfully executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		
		} finally {
			tbc.cleanUp(session, null, null);
		}
	}
	/**
	 * @testID testDeleteNode008
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to PERMISSION_DENIED is thrown.
	 */
	private void testDeleteNode008() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testDeleteNode008");

			localSession = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(
					TestExecPluginActivator.INTERIOR_NODE,
					new DmtAcl(new String[] { DmtTestControl.PRINCIPAL },
							new int[] { DmtAcl.EXEC }));
			localSession.close();
			
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL, TestExecPluginActivator.INTERIOR_NODE,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			remoteSession.deleteNode(TestExecPluginActivator.INTERIOR_NODE);
			
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
	 * @testID testDeleteNode009
	 * @testDescription This method asserts that an SecurityException is thrown
	 *                  when deleteNode is called without the right permission
	 */
	private void testDeleteNode009() {
		DmtSession session = null;
		try {
			tbc.log("#testDeleteNode009");

			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtTestControl.ALL_NODES, DmtPermission.EXEC));

			session.deleteNode(TestExecPluginActivator.INTERIOR_NODE);
			
			tbc.failException("#", SecurityException.class);
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
	 * @testID testDeleteNode010
	 * @testDescription This method asserts that an IllegalStateException is
	 *                  thrown when attemps to delete a node using a closed
	 *                  session.
	 */
	private void testDeleteNode010() {
		DmtSession session = null;
		try {
			tbc.log("#testDeleteNode010");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.close();
			
			session.deleteNode(TestExecPluginActivator.INTERIOR_NODE);
			
			tbc.failException("", IllegalStateException.class);
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
	 * @testID testDeleteNode011
	 * @testDescription This method asserts that relative URI works as described.
	 * 
	 */
	private void testDeleteNode011() {
		DmtSession session = null;
		try {
			tbc.log("#testDeleteNode011");
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtTestControl.ALL_NODES,DmtTestControl.ALL_ACTIONS));
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.deleteNode(TestExecPluginActivator.INTERIOR_VALUE);

			tbc.pass("A relative URI can be used with deleteNode.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}	
}

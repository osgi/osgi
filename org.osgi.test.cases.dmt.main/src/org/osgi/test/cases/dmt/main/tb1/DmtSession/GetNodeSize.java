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
 * Jan 26, 2005  Andre Assad
 * CR 1          Implement MEG TCK
 * ============  ==============================================================
 * Feb 15, 2005  Alexandre Santos
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
 * @methodUnderTest org.osgi.service.dmt.DmtSession#getNodeSize
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>getNodeSize<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class GetNodeSize implements TestInterface {
	private DmtTestControl tbc;

	public GetNodeSize(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetNodeSize001();
		testGetNodeSize002();
		testGetNodeSize003();
		testGetNodeSize004();
		testGetNodeSize005();
		testGetNodeSize006();
		testGetNodeSize007();
		testGetNodeSize008();
		testGetNodeSize009();
		testGetNodeSize010();
		testGetNodeSize011();
	}

	/**
	 * @testID testGetNodeSize001
	 * @testDescription This method asserts that getNodeSize correctly throws
	 *                  DmtException when a inexistent node is used.
	 */
	private void testGetNodeSize001() {
		DmtSession session = null;
		try {
			tbc.log("#testGetNodeSize001");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);
			session.getNodeSize(TestExecPluginActivator.INEXISTENT_NODE);
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
	 * @testID testGetNodeSize002
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to URI_TOO_LONG is thrown.
	 */
	private void testGetNodeSize002() {
		DmtSession session = null;
		try {
			tbc.log("#testGetNodeSize002");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.getNodeSize(DmtTestControl.URI_LONG);
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
	 * @testID testGetNodeSize003
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to INVALID_URI is thrown.
	 */
	private void testGetNodeSize003() {
		DmtSession session = null;
		try {
			tbc.log("#testGetNodeSize003");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.getNodeSize(DmtTestControl.INVALID_URI);

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
	 * @testID testGetNodeSize004
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to OTHER_ERROR is thrown.
	 */
	private void testGetNodeSize004() {
		DmtSession session = null;
		try {
			tbc.log("#testGetNodeSize004");

			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_CFG,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.getNodeSize(TestExecPluginActivator.LEAF_NODE);

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
	 * @testID testGetNodeSize005
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to PERMISSION_DENIED is thrown.
	 */
	private void testGetNodeSize005() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testGetNodeSize005");

			localSession = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(TestExecPluginActivator.LEAF_NODE,
					new DmtAcl(new String[] { DmtTestControl.PRINCIPAL },
							new int[] { DmtAcl.EXEC }));
			localSession.close();

			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class
					.getName(), DmtTestControl.PRINCIPAL, "*"));
			remoteSession = tbc.getDmtAdmin().getSession(
					DmtTestControl.PRINCIPAL, TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			remoteSession.getNodeSize(TestExecPluginActivator.LEAF_NODE);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is PERMISSION_DENIED",
					DmtException.PERMISSION_DENIED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(localSession, remoteSession, TestExecPluginActivator.LEAF_NODE);
		}

	}

	/**
	 * @testID testGetNodeSize006
	 * @testDescription This method asserts that an SecurityException is thrown
	 *                  when getNodeSize is called without the right permission
	 */
	private void testGetNodeSize006() {
		DmtSession session = null;
		try {
			tbc.log("#testGetNodeSize006");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			tbc.setPermissions(new PermissionInfo(
					DmtPermission.class.getName(), DmtTestControl.ALL_NODES,
					DmtPermission.EXEC));

			session.getNodeSize(TestExecPluginActivator.LEAF_NODE);

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
	 * @testID testGetNodeSize007
	 * @testDescription This method asserts that an IllegalStateException is
	 *                  thrown when attemps to execute getNodeSize using a
	 *                  closed session.
	 */
	private void testGetNodeSize007() {
		DmtSession session = null;
		try {
			tbc.log("#testGetNodeSize007");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);
			session.close();
			session.getNodeSize(TestExecPluginActivator.LEAF_NODE);

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
	 * @testID testGetNodeSize008
	 * @testDescription This method asserts that no exception is thrown when we
	 *                  set the correct acl permission.
	 */
	private void testGetNodeSize008() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testGetNodeSize008");

			localSession = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(TestExecPluginActivator.LEAF_NODE,
					new DmtAcl(new String[] { DmtTestControl.PRINCIPAL },
							new int[] { DmtAcl.GET }));
			localSession.close();

			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class
					.getName(), DmtTestControl.PRINCIPAL, "*"));
			remoteSession = tbc.getDmtAdmin().getSession(
					DmtTestControl.PRINCIPAL, TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			remoteSession.getNodeSize(TestExecPluginActivator.LEAF_NODE);

			tbc.pass("getNodeSize correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(localSession, remoteSession, TestExecPluginActivator.LEAF_NODE);
		}

	}

	/**
	 * @testID testGetNodeSize009
	 * @testDescription This method asserts that getNodeSize is successfully
	 *                  executed when the correct permission is assigned
	 */
	private void testGetNodeSize009() {
		DmtSession session = null;
		try {
			tbc.log("#testGetNodeSize009");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			tbc.setPermissions(new PermissionInfo(
					DmtPermission.class.getName(), DmtTestControl.ALL_NODES,
					DmtPermission.GET));

			session.getNodeSize(TestExecPluginActivator.LEAF_NODE);

			tbc.pass("getNodeSize correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(session, null, null);			
		}
	}

	/**
	 * @testID testGetNodeSize010
	 * @testDescription This method call getNodeSize in an interior node, 
	 *                  so, a DmtException with error code COMMAND_NOT_ALLOWED
	 *                  will be raised.
	 */
	private void testGetNodeSize010() {
		DmtSession session = null;
		try {
			tbc.log("#testGetNodeSize010");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			tbc.setPermissions(new PermissionInfo(
					DmtPermission.class.getName(), DmtTestControl.ALL_NODES,
					DmtPermission.GET));

			session.getNodeSize(TestExecPluginActivator.INTERIOR_NODE);

			tbc.failException("#", DmtException.class);

		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is COMMAND_NOT_ALLOWED",
					DmtException.COMMAND_NOT_ALLOWED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(session, null, null);			
		}
	}
	
	/**
	 * @testID testGetNodeSize011
	 * @testDescription This method asserts that relative URI works as described.
	 * 
	 */
	private void testGetNodeSize011() {
		DmtSession session = null;
		try {
			tbc.log("#testGetNodeSize011");
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtTestControl.ALL_NODES,DmtTestControl.ALL_ACTIONS));
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.getNodeSize(TestExecPluginActivator.LEAF_VALUE);

			tbc.pass("A relative URI can be used with getNodeSize.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}		
}

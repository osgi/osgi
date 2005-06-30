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
 * Jan 24, 2005  Andre Assad
 * CR 1          Implement MEG TCK
 * ============  ==============================================================
 * Feb 14, 2005  Alexandre Santos
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ===========  ===============================================================
 * Feb 17, 2005  Leonardo Barros
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ===========  ===============================================================
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
 * @methodUnderTest org.osgi.service.dmt.DmtSession#copy
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>copy<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class Copy implements TestInterface {
	private DmtTestControl tbc;

	public Copy(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testCopy001();
		testCopy002();
		testCopy003();
		testCopy004();
		testCopy005();
		testCopy006();
		testCopy007();
		testCopy008();
		testCopy009();
		testCopy010();
		testCopy011();
		testCopy012();
		testCopy013();
	}

	/**
	 * @testID testCopy001
	 * @testDescription This method test if the code in dmtexception is
	 *                  OTHER_ERROR.
	 */
	private void testCopy001() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy001");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.copy(TestExecPluginActivator.INTERIOR_NODE,
					TestExecPluginActivator.INEXISTENT_NODE, true);
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
	 * @testID testCopy002
	 * @testDescription This method test if the code in dmtexception is
	 *                  COMMAND_NOT_ALLOWED.
	 */
	private void testCopy002() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy002");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.copy(DmtTestControl.OSGi_ROOT,
					TestExecPluginActivator.INEXISTENT_NODE, true);
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
	 * @testID testCopy003
	 * @testDescription This method test if the code in dmtexception is
	 *                  INVALID_URI.
	 */
	private void testCopy003() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy003");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.copy(DmtTestControl.INVALID_URI,
					TestExecPluginActivator.INEXISTENT_NODE, true);

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
	 * @testID testCopy004
	 * @testDescription This method test if the code in dmtexception is
	 *                  URI_TOO_LONG.
	 */
	private void testCopy004() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy004");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.copy(DmtTestControl.URI_LONG,
					TestExecPluginActivator.INEXISTENT_NODE, true);
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
	 * @testID testCopy005
	 * @testDescription This method test if the code in dmtexception is
	 *                  NODE_NOT_FOUND.
	 */
	private void testCopy005() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy005");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.copy(TestExecPluginActivator.INEXISTENT_NODE,
					TestExecPluginActivator.ROOT + "/other", true);
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is NODE_NOT_FOUND",
					DmtException.NODE_NOT_FOUND, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testCopy006
	 * @testDescription This method test if the code in dmtexception is
	 *                  NODE_ALREADY_EXISTS.
	 */
	private void testCopy006() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy006");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.copy(TestExecPluginActivator.INTERIOR_NODE,
					TestExecPluginActivator.INTERIOR_NODE2, false);
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is NODE_ALREADY_EXISTS",
					DmtException.NODE_ALREADY_EXISTS, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testCopy007
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to PERMISSION_DENIED is thrown.
	 */
	private void testCopy007() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testCopy007");
			localSession = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE,
					new DmtAcl(new String[] { DmtTestControl.PRINCIPAL },
							new int[] { DmtAcl.EXEC }));
			localSession.close();
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class
					.getName(), DmtTestControl.PRINCIPAL, "*"));

			remoteSession = tbc.getDmtAdmin().getSession(
					DmtTestControl.PRINCIPAL, DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			remoteSession.copy(TestExecPluginActivator.INTERIOR_NODE,
					TestExecPluginActivator.INEXISTENT_NODE, true);
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is PERMISSION_DENIED",
					DmtException.PERMISSION_DENIED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(localSession, remoteSession,
					TestExecPluginActivator.INTERIOR_NODE);
		}

	}

	/**
	 * @testID testCopy008
	 * @testDescription This method asserts that an SecurityException is thrown
	 *                  when copy is called without the right permission
	 */
	private void testCopy008() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy008");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.setPermissions(new PermissionInfo(
					DmtPermission.class.getName(), DmtTestControl.ALL_NODES,
					DmtPermission.DELETE));
			session.copy(TestExecPluginActivator.INTERIOR_NODE,
					TestExecPluginActivator.INEXISTENT_NODE, true);
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
	 * @testID testCopy009
	 * @testDescription This method asserts that an IllegalStateException is
	 *                  thrown when attemps to copy an uri using a closed
	 *                  session.
	 */
	private void testCopy009() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy009");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.close();
			session.copy(TestExecPluginActivator.INTERIOR_NODE,
					TestExecPluginActivator.INEXISTENT_NODE, true);
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
	 * @testID testCopy010
	 * @testDescription This method asserts that the method is called if it has
	 *                  the right DmtPermission (local)
	 */
	private void testCopy010() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy010");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.setPermissions(new PermissionInfo(
					DmtPermission.class.getName(), DmtTestControl.ALL_NODES,
					DmtPermission.ADD + "," + DmtPermission.GET));
			session.copy(TestExecPluginActivator.INTERIOR_NODE,
					TestExecPluginActivator.INEXISTENT_NODE, false);
			tbc.pass("A node could be copied with the right permission");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(session, null, null);
		}

	}

	/**
	 * @testID testCopy011
	 * @testDescription This method asserts that the method is called if it has
	 *                  the right DmtAcl (remote)
	 */
	private void testCopy011() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testCopy011");
			localSession = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE,
					new DmtAcl(new String[] { DmtTestControl.PRINCIPAL },
							new int[] { DmtAcl.GET | DmtAcl.ADD }));
			localSession.close();
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class
					.getName(), DmtTestControl.PRINCIPAL, "*"));

			remoteSession = tbc.getDmtAdmin().getSession(
					DmtTestControl.PRINCIPAL, DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);

			remoteSession.copy(TestExecPluginActivator.INTERIOR_NODE,
					TestExecPluginActivator.INEXISTENT_NODE, false);
			tbc
					.pass("This method asserts that the method is called if it has the right DmtAcl");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(localSession, remoteSession,
					TestExecPluginActivator.INTERIOR_NODE);
		}

	}

	/**
	 * @testID testCopy012
	 * @testDescription This method asserts that relative URI works as described
	 *                  in this method.
	 */
	private void testCopy012() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy012");
			tbc.setPermissions(new PermissionInfo(
					DmtPermission.class.getName(), DmtTestControl.ALL_NODES,
					DmtTestControl.ALL_ACTIONS));
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.copy(TestExecPluginActivator.INTERIOR_VALUE,
					TestExecPluginActivator.INEXISTENT_NODE_VALUE, false);

			tbc.pass("A relative URI can be used with Copy.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testCopy013
	 * @testDescription Asserts if COMMAND_NOT_ALLOWED is thrown if any of the
	 *                  implied Get or Add commands are not allowed
	 */
	private void testCopy013() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy013");

			tbc.setPermissions(new PermissionInfo(
					DmtPermission.class.getName(), DmtTestControl.ALL_NODES,
					DmtTestControl.ALL_ACTIONS));

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.copy(TestExecPluginActivator.INTERIOR_NODE_COPY,
					TestExecPluginActivator.INTERIOR_NODE2_COPY, false);
			tbc.failException("",DmtException.class);
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

}

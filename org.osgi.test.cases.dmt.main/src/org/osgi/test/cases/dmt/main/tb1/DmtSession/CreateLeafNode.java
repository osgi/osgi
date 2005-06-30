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
 * Jan 1, 2005   Andre Assad
 * CR 1          Implement MEG TCK
 * ============  ==============================================================
 * Feb 15, 2005  Alexandre Santos
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ===========  ===============================================================
 */
package org.osgi.test.cases.dmt.main.tb1.DmtSession;

import org.osgi.service.dmt.DmtAcl;
import org.osgi.service.dmt.DmtData;
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
 * @methodUnderTest org.osgi.service.dmt.DmtSession#createLeafNode
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>createLeafNode<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class CreateLeafNode implements TestInterface {
	private DmtTestControl tbc;

	public CreateLeafNode(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testCreateLeafNode001();
		testCreateLeafNode002();
		testCreateLeafNode003();
		testCreateLeafNode004();
		testCreateLeafNode005();
		testCreateLeafNode006();
		testCreateLeafNode007();
		testCreateLeafNode008();
		testCreateLeafNode009();
		testCreateLeafNode010();
		testCreateLeafNode011();
		testCreateLeafNode012();
		testCreateLeafNode013();
		testCreateLeafNode014();
		testCreateLeafNode015();
		testCreateLeafNode016();
		testCreateLeafNode017();
		testCreateLeafNode018();
		testCreateLeafNode019();
		testCreateLeafNode020();
		testCreateLeafNode021();
		testCreateLeafNode022();
		testCreateLeafNode023();
		testCreateLeafNode024();
		testCreateLeafNode025();
		testCreateLeafNode026();
		testCreateLeafNode027();
		testCreateLeafNode028();
		testCreateLeafNode029();
		testCreateLeafNode030();
		testCreateLeafNode031();
		testCreateLeafNode032();
		testCreateLeafNode033();
	}

	/**
	 * @testID testCreateLeafNode001
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to NODE_ALREADY_EXISTS is thrown using the
	 *                  constructor with only one argument.
	 */
	private void testCreateLeafNode001() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode001");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createLeafNode(TestExecPluginActivator.LEAF_NODE);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is NODE_ALREADY_EXISTS",
					DmtException.NODE_ALREADY_EXISTS, e.getCode());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}

	}

	/**
	 * @testID testCreateLeafNode002
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to NODE_ALREADY_EXISTS is thrown using the
	 *                  constructor with two parameters.
	 */
	private void testCreateLeafNode002() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode002");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.createLeafNode(TestExecPluginActivator.LEAF_NODE,
					new DmtData(10));

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is NODE_ALREADY_EXISTS",
					DmtException.NODE_ALREADY_EXISTS, e.getCode());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testCreateLeafNode003
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to NODE_ALREADY_EXISTS is thrown using the
	 *                  constructor with three parameters.
	 */
	private void testCreateLeafNode003() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode003");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createLeafNode(TestExecPluginActivator.LEAF_NODE,
					new DmtData(10), DmtTestControl.MIMETYPE);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is NODE_ALREADY_EXISTS",
					DmtException.NODE_ALREADY_EXISTS, e.getCode());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testCreateLeafNode004
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to OTHER_ERROR is thrown using the constructor
	 *                  with only one argument.
	 */
	private void testCreateLeafNode004() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode004");

			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_CFG,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session
					.createLeafNode(TestExecPluginActivator.INEXISTENT_LEAF_NODE);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserting that DmtException code is OTHER_ERROR",
					DmtException.OTHER_ERROR, e.getCode());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testCreateLeafNode005
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to OTHER_ERROR is thrown using the constructor
	 *                  with two parameters.
	 */
	private void testCreateLeafNode005() {
		DmtSession session = null;

		try {
			tbc.log("#testCreateLeafNode005");

			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_CFG,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createLeafNode(
					TestExecPluginActivator.INEXISTENT_LEAF_NODE, new DmtData(
							10));

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserting that DmtException code is OTHER_ERROR",
					DmtException.OTHER_ERROR, e.getCode());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testCreateLeafNode006
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to OTHER_ERROR is thrown using the constructor
	 *                  with three parameters.
	 */
	private void testCreateLeafNode006() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode006");

			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_CFG,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createLeafNode(
					TestExecPluginActivator.INEXISTENT_LEAF_NODE, new DmtData(
							10), DmtTestControl.MIMETYPE);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserting that DmtException code is OTHER_ERROR",
					DmtException.OTHER_ERROR, e.getCode());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testCreateLeafNode007
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to INVALID_URI is thrown using the constructor
	 *                  with only one argument.
	 */
	private void testCreateLeafNode007() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode007");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createLeafNode(DmtTestControl.INVALID_URI);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserting that DmtException code is INVALID_URI",
					DmtException.INVALID_URI, e.getCode());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testCreateLeafNode008
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to INVALID_URI is thrown using the constructor
	 *                  with two parameters.
	 */
	private void testCreateLeafNode008() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode008");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createLeafNode(DmtTestControl.INVALID_URI, new DmtData(10));

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserting that DmtException code is INVALID_URI",
					DmtException.INVALID_URI, e.getCode());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testCreateLeafNode009
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to INVALID_URI is thrown using the constructor
	 *                  with three parameters.
	 */
	private void testCreateLeafNode009() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode009");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createLeafNode(DmtTestControl.INVALID_URI, new DmtData(10),
					DmtTestControl.MIMETYPE);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserting that DmtException code is INVALID_URI",
					DmtException.INVALID_URI, e.getCode());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(session, new String[] { DmtTestControl.INVALID_URI });
		}
	}

	/**
	 * @testID testCreateLeafNode010
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to URI_TOO_LONG is thrown using the constructor
	 *                  with only one argument.
	 */
	private void testCreateLeafNode010() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode010");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createLeafNode(DmtTestControl.URI_LONG);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is URI_TOO_LONG",
					DmtException.URI_TOO_LONG, e.getCode());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(session, new String[] { DmtTestControl.URI_LONG });
		}
	}

	/**
	 * @testID testCreateLeafNode011
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to URI_TOO_LONG is thrown using the constructor
	 *                  with two parameters.
	 */
	private void testCreateLeafNode011() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode011");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createLeafNode(DmtTestControl.URI_LONG, new DmtData(10));

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is URI_TOO_LONG",
					DmtException.URI_TOO_LONG, e.getCode());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(session, new String[] { DmtTestControl.URI_LONG });
		}
	}

	/**
	 * @testID testCreateLeafNode012
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to URI_TOO_LONG is thrown using the constructor
	 *                  with three parameters.
	 */
	private void testCreateLeafNode012() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode012");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createLeafNode(DmtTestControl.URI_LONG, new DmtData(10),
					DmtTestControl.MIMETYPE);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is URI_TOO_LONG",
					DmtException.URI_TOO_LONG, e.getCode());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(session, new String[] { DmtTestControl.URI_LONG });
		}
	}

	/**
	 * @testID testCreateLeafNode013
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to COMMAND_FAILED is thrown using the constructor
	 *                  with two parameters.
	 */
	private void testCreateLeafNode013() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode013");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createLeafNode(
					TestExecPluginActivator.INEXISTENT_LEAF_NODE, null);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is COMMAND_FAILED",
					DmtException.COMMAND_FAILED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testCreateLeafNode014
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to COMMAND_FAILED is thrown using the constructor
	 *                  with three parameters.
	 */
	private void testCreateLeafNode014() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode014");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createLeafNode(
					TestExecPluginActivator.INEXISTENT_LEAF_NODE, null,
					DmtTestControl.MIMETYPE);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is COMMAND_FAILED",
					DmtException.COMMAND_FAILED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testCreateLeafNode015
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to PERMISSION_DENIED is thrown.
	 */
	private void testCreateLeafNode015() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode015");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeAcl(TestExecPluginActivator.ROOT, new DmtAcl(
					new String[] { DmtTestControl.PRINCIPAL },
					new int[] { DmtAcl.EXEC }));

			session.close();
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class
					.getName(), DmtTestControl.PRINCIPAL, "*"));
			session = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,
					TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session
					.createLeafNode(TestExecPluginActivator.INEXISTENT_LEAF_NODE);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is PERMISSION_DENIED",
					DmtException.PERMISSION_DENIED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(session, null, TestExecPluginActivator.ROOT);
		}

	}

	/**
	 * @testID testCreateLeafNode016
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to PERMISSION_DENIED is thrown using the
	 *                  constructor with two parameters.
	 */
	private void testCreateLeafNode016() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode016");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeAcl(TestExecPluginActivator.ROOT, new DmtAcl(
					new String[] { DmtTestControl.PRINCIPAL },
					new int[] { DmtAcl.EXEC }));
			session.close();

			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class
					.getName(), DmtTestControl.PRINCIPAL, "*"));
			session = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,
					TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createLeafNode(
					TestExecPluginActivator.INEXISTENT_LEAF_NODE,
					new DmtData(0));

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is PERMISSION_DENIED",
					DmtException.PERMISSION_DENIED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(session, null, TestExecPluginActivator.ROOT);
		}

	}

	/**
	 * @testID testCreateLeafNode017
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to PERMISSION_DENIED is thrown.
	 */
	private void testCreateLeafNode017() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode017");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeAcl(TestExecPluginActivator.ROOT, new DmtAcl(
					new String[] { DmtTestControl.PRINCIPAL },
					new int[] { DmtAcl.EXEC }));

			session.close();

			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class
					.getName(), DmtTestControl.PRINCIPAL, "*"));
			session = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,
					TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.createLeafNode(
					TestExecPluginActivator.INEXISTENT_LEAF_NODE, new DmtData(
							10), DmtTestControl.MIMETYPE);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is PERMISSION_DENIED",
					DmtException.PERMISSION_DENIED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(session, null, TestExecPluginActivator.ROOT);
		}
	}

	/**
	 * @testID testCreateLeafNode018
	 * @testDescription This method asserts that an IllegalStateException is
	 *                  thrown when attemps to create a leaf node using a closed
	 *                  session
	 */
	private void testCreateLeafNode018() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode018");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.close();
			session
					.createLeafNode(TestExecPluginActivator.INEXISTENT_LEAF_NODE);

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
	 * @testID testCreateLeafNode019
	 * @testDescription This method asserts that an IllegalStateException is
	 *                  thrown when attemps to create a leaf node using a closed
	 *                  session using the constructor with two parameters.
	 */
	private void testCreateLeafNode019() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode019");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.close();
			session.createLeafNode(
					TestExecPluginActivator.INEXISTENT_LEAF_NODE, new DmtData(
							10));

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
	 * @testID testCreateLeafNode020
	 * @testDescription This method asserts that an IllegalStateException is
	 *                  thrown when attemps to create a leaf node using a closed
	 *                  session using the constructor with three parameters.
	 */
	private void testCreateLeafNode020() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode020");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.close();
			session.createLeafNode(
					TestExecPluginActivator.INEXISTENT_LEAF_NODE, new DmtData(
							10), DmtTestControl.MIMETYPE);

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
	 * @testID testCreateLeafNode021
	 * @testDescription This method asserts that an SecurityException is thrown
	 *                  when createLeafNode is called without the right
	 *                  permission
	 */
	private void testCreateLeafNode021() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode021");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			tbc.setPermissions(new PermissionInfo(
					DmtPermission.class.getName(), DmtTestControl.ALL_NODES,
					DmtPermission.EXEC));

			session
					.createLeafNode(TestExecPluginActivator.INEXISTENT_LEAF_NODE);

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
	 * @testID testCreateLeafNode022
	 * @testDescription This method asserts that an SecurityException is thrown
	 *                  when createLeafNode is called without the right
	 *                  permission using the constructor with two parameters.
	 */
	private void testCreateLeafNode022() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode022");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			tbc.setPermissions(new PermissionInfo(
					DmtPermission.class.getName(), DmtTestControl.ALL_NODES,
					DmtPermission.EXEC));
			session.createLeafNode(
					TestExecPluginActivator.INEXISTENT_LEAF_NODE, new DmtData(
							10));

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
	 * @testID testCreateLeafNode023
	 * @testDescription This method asserts that an SecurityException is thrown
	 *                  when createLeafNode is called without the right
	 *                  permission
	 */
	private void testCreateLeafNode023() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode023");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			tbc.setPermissions(new PermissionInfo(
					DmtPermission.class.getName(), DmtTestControl.ALL_NODES,
					DmtPermission.EXEC));

			session.createLeafNode(
					TestExecPluginActivator.INEXISTENT_LEAF_NODE, new DmtData(
							10), DmtTestControl.MIMETYPE);

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
	 * @testID testCreateLeafNode024
	 * @testDescription This method asserts that we can create a leaf node
	 *                  without raise an exception using the constructor with
	 *                  one argument.
	 */
	private void testCreateLeafNode024() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode024");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			tbc.setPermissions(new PermissionInfo(
					DmtPermission.class.getName(), DmtTestControl.ALL_NODES,
					DmtPermission.ADD));

			session
					.createLeafNode(TestExecPluginActivator.INEXISTENT_LEAF_NODE);

			tbc.pass("createLeafNode was successfully executed");

		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(session, null, null);
		}
	}

	/**
	 * @testID testCreateLeafNode025
	 * @testDescription This method asserts that we can create a leaf node
	 *                  without raise an exception using the constructor with
	 *                  two arguments.
	 */
	private void testCreateLeafNode025() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode025");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			tbc.setPermissions(new PermissionInfo(
					DmtPermission.class.getName(), DmtTestControl.ALL_NODES,
					DmtPermission.ADD));

			session.createLeafNode(
					TestExecPluginActivator.INEXISTENT_LEAF_NODE, new DmtData(
							10));

			tbc.pass("createLeafNode was successfully executed");

		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(session, null, null);
		}
	}

	/**
	 * @testID testCreateLeafNode026
	 * @testDescription This method asserts that we can create a leaf node
	 *                  without raise an exception using the constructor with
	 *                  three arguments.
	 */
	private void testCreateLeafNode026() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode026");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			tbc.setPermissions(new PermissionInfo(
					DmtPermission.class.getName(), DmtTestControl.ALL_NODES,
					DmtPermission.ADD));

			session.createLeafNode(
					TestExecPluginActivator.INEXISTENT_LEAF_NODE, new DmtData(
							10), DmtTestControl.MIMETYPE);

			tbc.pass("createLeafNode was successfully executed");

		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(session, null, null);
		}
	}

	/**
	 * @testID testCreateLeafNode027
	 * @testDescription This method asserts that we can create a leaf node
	 *                  without raise an exception using the constructor with
	 *                  one argument.
	 */
	private void testCreateLeafNode027() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode027");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeAcl(TestExecPluginActivator.ROOT, new DmtAcl(
					new String[] { DmtTestControl.PRINCIPAL },
					new int[] { DmtAcl.ADD }));

			session.close();
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class
					.getName(), DmtTestControl.PRINCIPAL, "*"));
			session = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,
					TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session
					.createLeafNode(TestExecPluginActivator.INEXISTENT_LEAF_NODE);

			tbc.pass("createLeafNode was successfully executed");

		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(session, null, TestExecPluginActivator.ROOT);
		}

	}

	/**
	 * @testID testCreateLeafNode028
	 * @testDescription This method asserts that we can create a leaf node
	 *                  without raise an exception using the constructor with
	 *                  two arguments.
	 */
	private void testCreateLeafNode028() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode028");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeAcl(TestExecPluginActivator.ROOT, new DmtAcl(
					new String[] { DmtTestControl.PRINCIPAL },
					new int[] { DmtAcl.ADD }));
			session.close();

			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class
					.getName(), DmtTestControl.PRINCIPAL, "*"));
			session = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,
					TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createLeafNode(
					TestExecPluginActivator.INEXISTENT_LEAF_NODE,
					new DmtData(0));

			tbc.pass("createLeafNode was successfully executed");

		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(session, null, TestExecPluginActivator.ROOT);
		}

	}

	/**
	 * @testID testCreateLeafNode029
	 * @testDescription This method asserts that we can create a leaf node
	 *                  without raise an exception using the constructor with
	 *                  three arguments.
	 */
	private void testCreateLeafNode029() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode029");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeAcl(TestExecPluginActivator.ROOT, new DmtAcl(
					new String[] { DmtTestControl.PRINCIPAL },
					new int[] { DmtAcl.ADD }));

			session.close();

			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class
					.getName(), DmtTestControl.PRINCIPAL, "*"));
			session = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,
					TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.createLeafNode(
					TestExecPluginActivator.INEXISTENT_LEAF_NODE, new DmtData(
							10), DmtTestControl.MIMETYPE);

			tbc.pass("createLeafNode was successfully executed");

		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(session, null, TestExecPluginActivator.ROOT);
		}
	}

	/**
	 * @testID testCreateLeafNode030
	 * @testDescription This method asserts that we can create a leaf node
	 *                  without raise an exception using the constructor with
	 *                  three arguments.
	 */
	private void testCreateLeafNode030() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode030");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeAcl(TestExecPluginActivator.ROOT, new DmtAcl(
					new String[] { DmtTestControl.PRINCIPAL },
					new int[] { DmtAcl.ADD }));

			session.close();

			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class
					.getName(), DmtTestControl.PRINCIPAL, "*"));
			session = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,
					TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.createLeafNode(
					TestExecPluginActivator.INEXISTENT_LEAF_NODE, new DmtData(
							10), null);

			tbc.pass("createLeafNode was successfully executed");

		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(session, null, TestExecPluginActivator.ROOT);
		}
	}

	/**
	 * @testID testCreateLeafNode031
	 * @testDescription This method asserts that relative URI works as described
	 *                  in this method using the method with one arguments.
	 * 
	 */
	private void testCreateLeafNode031() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode031");
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtTestControl.ALL_NODES,DmtTestControl.ALL_ACTIONS));
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.createLeafNode(TestExecPluginActivator.INEXISTENT_LEAF_VALUE);

			tbc.pass("A relative URI can be used with CreateLeafNode.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testCreateLeafNode032
	 * @testDescription This method asserts that relative URI works as described
	 *                  in this method using the method with two arguments.
	 * 
	 */
	private void testCreateLeafNode032() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode032");
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtTestControl.ALL_NODES,DmtTestControl.ALL_ACTIONS));
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.createLeafNode(TestExecPluginActivator.INEXISTENT_LEAF_VALUE,
					new DmtData(10));

			tbc.pass("A relative URI can be used with CreateLeafNode.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testCreateLeafNode033
	 * @testDescription This method asserts that relative URI works as described
	 *                  in this method using the method with three arguments.
	 * 
	 */
	private void testCreateLeafNode033() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateLeafNode033");
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtTestControl.ALL_NODES,DmtTestControl.ALL_ACTIONS));
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.createLeafNode(TestExecPluginActivator.INEXISTENT_LEAF_VALUE,
					new DmtData(10), null);

			tbc.pass("A relative URI can be used with CreateLeafNode.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}

}
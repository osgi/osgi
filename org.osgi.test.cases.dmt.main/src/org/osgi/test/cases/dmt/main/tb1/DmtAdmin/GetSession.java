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
 * 
 */

/*
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Jan 31, 2005  Andre Assad
 * 1             Implement MEG TCK
 * ============  ==============================================================
 * Feb 11, 2005  Alexandre Santos
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-002)
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.main.tb1.DmtAdmin;

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
 * @methodUnderTest org.osgi.service.dmt.DmtAdmin#getSession
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>getSession<code> constructor, according to MEG reference
 *                     documentation (rfc0085).
 */
public class GetSession implements TestInterface {
	private DmtTestControl tbc;

	public GetSession(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetSession001();
		testGetSession002();
		testGetSession003();
		testGetSession004();
		testGetSession005();
		testGetSession006();
		testGetSession007();
		testGetSession008();
		testGetSession009();
		testGetSession011();
		testGetSession012();
		testGetSession013();
		testGetSession014();
		testGetSession015();
		testGetSession016();
		testGetSession017();
		testGetSession018();
		testGetSession019();
		testGetSession020();
		testGetSession021();
		testGetSession022();
		testGetSession023();

	}

	/**
	 * @testID testGetSession001
	 * @testDescription This method asserts that a session is opened with the
	 *                  specified subtree, lock type (the default is
	 *                  LOCK_TYPE_EXCLUSIVE) and principal.
	 */
	private void testGetSession001() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSession001");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG);
			tbc.assertEquals("Asserting subtree", DmtTestControl.OSGi_LOG,
					session.getRootUri());
			tbc.assertEquals("Asserting lock type",
					DmtSession.LOCK_TYPE_EXCLUSIVE, session.getLockType());
			tbc.assertNull("Asserting principal", session.getPrincipal());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");

		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetSession002
	 * @testDescription This method asserts that "." on the parameter subtree
	 *                  gives access to the whole subtree
	 */
	private void testGetSession002() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSession002");
			session = tbc.getDmtAdmin().getSession(".");
			tbc.assertEquals("Asserting subtree", ".", session.getRootUri());
			tbc.assertEquals("Asserting lock type",
					DmtSession.LOCK_TYPE_EXCLUSIVE, session.getLockType());
			tbc.assertNull("Asserting principal", session.getPrincipal());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");

		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetSession003
	 * @testDescription This method asserts that null on the parameter subtree
	 *                  gives access to the whole subtree
	 */
	private void testGetSession003() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSession003");
			session = tbc.getDmtAdmin().getSession(null);
			tbc.assertEquals("Asserting subtree", ".", session.getRootUri());
			tbc.assertEquals("Asserting lock type",
					DmtSession.LOCK_TYPE_EXCLUSIVE, session.getLockType());
			tbc.assertNull("Asserting principal", session.getPrincipal());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");

		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetSession004
	 * @testDescription This method asserts if an invalid node causes an
	 *                  exception with NODE_NOT_FOUND code
	 */
	private void testGetSession004() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSession004");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.INVALID_NODE);
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting if the exception's code is NODE_NOT_FOUND.",
					DmtException.NODE_NOT_FOUND, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetSession005
	 * @testDescription This method asserts if a too long uri causes an
	 *                  exception with URI_TOO_LONG code
	 */
	private void testGetSession005() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSession005");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.URI_LONG);
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting if the exception's code is URI_TOO_LONG.",
					DmtException.URI_TOO_LONG, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetSession006
	 * @testDescription This method asserts if a node using invalid characters
	 *                  causes an exception with INVALID_URI code
	 */
	private void testGetSession006() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSession006");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.INVALID_URI);
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting if the exception's code is INVALID_URI.",
					DmtException.INVALID_URI, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetSession007
	 * @testDescription This method asserts that a session is opened with the
	 *                  specified subtree, lock type and principal.
	 */
	private void testGetSession007() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSession007");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_SHARED);
			tbc.assertEquals("Asserting subtree", DmtTestControl.OSGi_LOG,
					session.getRootUri());
			tbc.assertEquals("Asserting lock type",
					DmtSession.LOCK_TYPE_SHARED, session.getLockType());
			tbc.assertNull("Asserting principal", session.getPrincipal());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");

		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetSession008
	 * @testDescription This method asserts that "." on the parameter subtree
	 *                  gives access to the whole subtree
	 */
	private void testGetSession008() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSession008");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_ATOMIC);
			tbc.assertEquals("Asserting subtree", ".", session.getRootUri());
			tbc.assertEquals("Asserting lock type",
					DmtSession.LOCK_TYPE_ATOMIC, session.getLockType());
			tbc.assertNull("Asserting principal", session.getPrincipal());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");

		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetSession009
	 * @testDescription This method asserts that null on the parameter subtree
	 *                  gives access to the whole subtree
	 */
	private void testGetSession009() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSession009");
			session = tbc.getDmtAdmin().getSession(null,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.assertEquals("Asserting subtree", ".", session.getRootUri());
			tbc.assertEquals("Asserting lock type",
					DmtSession.LOCK_TYPE_EXCLUSIVE, session.getLockType());
			tbc.assertNull("Asserting principal", session.getPrincipal());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");

		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetSession011
	 * @testDescription This method asserts if an invalid node causes an
	 *                  exception with NODE_NOT_FOUND code
	 */
	private void testGetSession011() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSession011");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.INVALID_NODE,
					DmtSession.LOCK_TYPE_SHARED);
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting if the exception's code is NODE_NOT_FOUND.",
					DmtException.NODE_NOT_FOUND, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetSession012
	 * @testDescription This method asserts if a too long uri causes an
	 *                  exception with URI_TOO_LONG code
	 */
	private void testGetSession012() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSession012");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.URI_LONG,
					DmtSession.LOCK_TYPE_SHARED);
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting if the exception's code is URI_TOO_LONG.",
					DmtException.URI_TOO_LONG, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetSession013
	 * @testDescription This method asserts if a invalid lock mode causes an
	 *                  exception with OTHER_ERROR code
	 */
	private void testGetSession013() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSession013");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,
					DmtTestControl.INVALID_LOCKMODE);
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting if the exception's code is OTHER_ERROR.",
					DmtException.OTHER_ERROR, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetSession014
	 * @testDescription This method asserts if a node using invalid characters
	 *                  causes an exception with INVALID_URI code
	 */
	private void testGetSession014() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSession014");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.INVALID_URI,
					DmtSession.LOCK_TYPE_SHARED);
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting if the exception's code is INVALID_URI.",
					DmtException.INVALID_URI, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetSession015
	 * @testDescription This method asserts that a session is opened with the
	 *                  specified subtree, lock type and principal. This
	 *                  constructor use by default LOCK_TYPE_ATOMIC
	 * 
	 */
	private void testGetSession015() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSession015");
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class
					.getName(), DmtTestControl.PRINCIPAL, "*"));
			session = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,
					DmtTestControl.OSGi_LOG, DmtSession.LOCK_TYPE_ATOMIC);
			tbc.assertEquals("Asserting subtree", DmtTestControl.OSGi_LOG,
					session.getRootUri());
			tbc.assertEquals("Asserting lock type",
					DmtSession.LOCK_TYPE_ATOMIC, session.getLockType());
			tbc.assertEquals("Asserting principal", DmtTestControl.PRINCIPAL,
					session.getPrincipal());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");

		} finally {
			tbc.closeSession(session);
			tbc.setPermissions(new PermissionInfo(
					DmtPermission.class.getName(), DmtTestControl.ALL_NODES,
					DmtTestControl.ALL_ACTIONS));
		}
	}

	/**
	 * @testID testGetSession016
	 * @testDescription This method asserts that "." on the parameter subtree
	 *                  gives access to the whole subtree
	 * 
	 */
	private void testGetSession016() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSession016");
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class
					.getName(), DmtTestControl.PRINCIPAL, "*"));
			session = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,
					".", DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.assertEquals("Asserting subtree", ".", session.getRootUri());
			tbc.assertEquals("Asserting lock type",
					DmtSession.LOCK_TYPE_EXCLUSIVE, session.getLockType());
			tbc.assertEquals("Asserting principal", DmtTestControl.PRINCIPAL,
					session.getPrincipal());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
			tbc.setPermissions(new PermissionInfo(
					DmtPermission.class.getName(), DmtTestControl.ALL_NODES,
					DmtTestControl.ALL_ACTIONS));
		}
	}

	/**
	 * @testID testGetSession017
	 * @testDescription This method asserts that null on the parameter subtree
	 *                  gives access to the whole subtree
	 * 
	 */
	private void testGetSession017() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSession017");
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class
					.getName(), DmtTestControl.PRINCIPAL, "*"));
			session = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,
					null, DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.assertEquals("Asserting subtree", ".", session.getRootUri());
			tbc.assertEquals("Asserting lock type",
					DmtSession.LOCK_TYPE_EXCLUSIVE, session.getLockType());
			tbc.assertEquals("Asserting principal", DmtTestControl.PRINCIPAL,
					session.getPrincipal());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
			tbc.setPermissions(new PermissionInfo(
					DmtPermission.class.getName(), DmtTestControl.ALL_NODES,
					DmtTestControl.ALL_ACTIONS));
		}
	}

	/**
	 * @testID testGetSession018
	 * @testDescription This method asserts if an invalid node causes an
	 *                  exception with NODE_NOT_FOUND code
	 */
	private void testGetSession018() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSession018");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,
					DmtTestControl.INVALID_NODE, DmtSession.LOCK_TYPE_SHARED);
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting if the exception's code is NODE_NOT_FOUND.",
					DmtException.NODE_NOT_FOUND, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetSession019
	 * @testDescription This method asserts if a too long uri causes an
	 *                  exception with URI_TOO_LONG code
	 */
	private void testGetSession019() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSession019");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,
					DmtTestControl.URI_LONG, DmtSession.LOCK_TYPE_SHARED);
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting if the exception's code is URI_TOO_LONG.",
					DmtException.URI_TOO_LONG, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetSession020
	 * @testDescription This method asserts if a invalid lock mode causes an
	 *                  exception with OTHER_ERROR code
	 */
	private void testGetSession020() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSession020");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,
					DmtTestControl.OSGi_LOG, DmtTestControl.INVALID_LOCKMODE);
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting if the exception's code is OTHER_ERROR.",
					DmtException.OTHER_ERROR, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetSession021
	 * @testDescription This method asserts if a node using invalid characters
	 *                  causes an exception with INVALID_URI code
	 */
	private void testGetSession021() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSession021");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,
					DmtTestControl.INVALID_URI, DmtSession.LOCK_TYPE_SHARED);
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting if the exception's code is INVALID_URI.",
					DmtException.INVALID_URI, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetSession022
	 * @testDescription This method asserts if SecurityException is thrown when
	 *                  the caller does not have the required
	 *                  DmtPrincipalPermission
	 */
	private void testGetSession022() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSession022");
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class
					.getName(), DmtTestControl.PRINCIPAL_2, "*"));
			session = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,
					DmtTestControl.OSGi_LOG, DmtSession.LOCK_TYPE_SHARED);
			tbc.failException("#", SecurityException.class);
		} catch (SecurityException e) {
			tbc.pass("SecurityException was thrown.");
		} catch (Exception e) {
			tbc.fail("Expected " + SecurityException.class.getName()
					+ " but was " + e.getClass().getName());
		} finally {
			tbc.closeSession(session);
			tbc.setPermissions(new PermissionInfo(
					DmtPermission.class.getName(), DmtTestControl.ALL_NODES,
					DmtTestControl.ALL_ACTIONS));
		}
	}

	/**
	 * @testID testGetSession023
	 * @testDescription This method asserts if a leaf node can be opened as
	 *                  session root 
	 */
	private void testGetSession023() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSession023");

			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.LEAF_NODE,
					DmtSession.LOCK_TYPE_ATOMIC);
			tbc.pass("A leaf node could be opened as session root.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);

		}
	}

}

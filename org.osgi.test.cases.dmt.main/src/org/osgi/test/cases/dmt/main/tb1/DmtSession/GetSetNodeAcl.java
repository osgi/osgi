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
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 21/JAN/2005  Andre Assad
 * CR 1         Implement MEG TCK
 * ===========  ==============================================================
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
 * @methodUnderTest org.osgi.service.dmt.DmtSession#getNodeAcl,setNodeAcl
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>getNodeAcl, and setNodeAcl<code> methods, according to MEG reference
 *                     documentation.
 */
public class GetSetNodeAcl implements TestInterface {

	private DmtTestControl tbc;

	public GetSetNodeAcl(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetSetNodeAcl001();
		testGetSetNodeAcl002();
		testGetSetNodeAcl003();
		testGetSetNodeAcl004();
		testGetSetNodeAcl005();
		testGetSetNodeAcl006();
		testGetSetNodeAcl007();
		testGetSetNodeAcl008();
		testGetSetNodeAcl009();
		testGetSetNodeAcl010();
		testGetSetNodeAcl011();
		testGetSetNodeAcl012();
		testGetSetNodeAcl013();
		testGetSetNodeAcl014();
		testGetSetNodeAcl015();
		testGetSetNodeAcl016();
		testGetSetNodeAcl017();
		testGetSetNodeAcl018();
		testGetSetNodeAcl019();
		testGetSetNodeAcl020();
		testGetSetNodeAcl021();
		testGetSetNodeAcl022();
	}

	/**
	 * @testID testGetSetNodeAcl001
	 * @testDescription This method asserts that the DmtAcl is correctly set for
	 *                  a given session node on the tree.
	 */
	private void testGetSetNodeAcl001() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl001");
			DmtAcl acl = new DmtAcl(DmtTestControl.ACLSTR);
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeAcl(DmtTestControl.OSGi_LOG, acl);

			tbc.assertEquals("Asserting node Acl", acl.toString(), session
					.getNodeAcl(DmtTestControl.OSGi_LOG).toString());

		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(session, null, DmtTestControl.OSGi_LOG);
		}

	}

	/**
	 * @testID testGetSetNodeAcl002
	 * @testDescription This method asserts that a DmtException is thrown for an
	 *                  invalid node of the tree.
	 */
	private void testGetSetNodeAcl002() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl002");
			DmtAcl acl = new DmtAcl(DmtTestControl.ACLSTR);

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeAcl(TestExecPluginActivator.INEXISTENT_NODE, acl);
			tbc.failException("", DmtException.class);
			
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code was NODE_NOT_FOUND.",
					DmtException.NODE_NOT_FOUND, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName()
					+ " but was " + e.getClass().getName());
		} finally {
			tbc.cleanUp(session, null, TestExecPluginActivator.INEXISTENT_NODE);
		}
	}

	/**
	 * @testID testGetSetNodeAcl003
	 * @testDescription This method asserts that a DmtException is thrown for an
	 *                  uri too long of the tree.
	 */
	private void testGetSetNodeAcl003() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl003");
			DmtAcl acl = new DmtAcl(DmtTestControl.ACLSTR);

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeAcl(DmtTestControl.URI_LONG, acl);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code was URI_TOO_LONG.",
					DmtException.URI_TOO_LONG, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(session, null, DmtTestControl.URI_LONG);
		}
	}

	/**
	 * @testID testGetSetNodeAcl004
	 * @testDescription This method asserts that a DmtException is thrown for an
	 *                  invalid uri of the tree.
	 */
	private void testGetSetNodeAcl004() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl004");
			DmtAcl acl = new DmtAcl(DmtTestControl.ACLSTR);

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeAcl(DmtTestControl.INVALID_URI, acl);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code was INVALID_URI.",
					DmtException.INVALID_URI, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(session, null, DmtTestControl.INVALID_URI);
		}
	}

	/**
	 * @testID testGetSetNodeAcl005
	 * @testDescription This method asserts that a DmtException is thrown for an
	 *                  other error of the tree.
	 */
	private void testGetSetNodeAcl005() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl005");
			DmtAcl acl = new DmtAcl(DmtTestControl.ACLSTR);

			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeAcl(DmtTestControl.OSGi_CFG, acl);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code was OTHER_ERROR.",
					DmtException.OTHER_ERROR, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(session, null, DmtTestControl.OSGi_CFG);
		}
	}

	/**
	 * @testID testGetSetNodeAcl006
	 * @testDescription This method asserts that IllegalStateException
	 *                  is thrown when it closes the session and then try to set
	 *                  an acl
	 */
	private void testGetSetNodeAcl006() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl006");
			DmtAcl acl = new DmtAcl(DmtTestControl.ACLSTR);

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.close();
			session.setNodeAcl(DmtTestControl.OSGi_LOG, acl);

			tbc.failException("", IllegalStateException.class);
		} catch (IllegalStateException e) {
			tbc.pass("The Exception was IllegalStateException");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName()
					+ " but was " + e.getClass().getName());
		} finally {
			tbc.cleanUp(session, null, DmtTestControl.OSGi_CFG);
		}
	}

	/**
	 * @testID testGetSetNodeAcl007
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to PERMISSION_DENIED is thrown.
	 */
	private void testGetSetNodeAcl007() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testGetSetNodeAcl007");
			
			localSession = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(DmtTestControl.OSGi_LOG, new DmtAcl(
					new String[] { DmtTestControl.PRINCIPAL },
					new int[] { DmtAcl.GET }));
			localSession.close();

			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,
					DmtTestControl.OSGi_LOG, DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			remoteSession.setNodeAcl(DmtTestControl.OSGi_LOG, new DmtAcl(
					new String[] { DmtTestControl.PRINCIPAL_2 },
					new int[] { DmtAcl.ADD }));

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is PERMISSION_DENIED",
					DmtException.PERMISSION_DENIED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(localSession, remoteSession, DmtTestControl.OSGi_CFG);
		}
	}

	/**
	 * @testID testGetSetNodeAcl008
	 * @testDescription This method asserts that SecurityException is thrown
	 *                  when setNodeAcl is called without the right permission
	 */
	private void testGetSetNodeAcl008() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl008");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtTestControl.ALL_NODES, DmtPermission.EXEC));

			session.setNodeAcl(DmtTestControl.OSGi_LOG, new DmtAcl(
					new String[] { DmtTestControl.PRINCIPAL },
					new int[] { DmtAcl.ADD }));

			tbc.failException("", SecurityException.class);
		} catch (SecurityException e) {
			tbc.pass("The Exception was SecurityException");
		} catch (Exception e) {
			tbc.fail("Expected " + SecurityException.class.getName()
					+ " but was " + e.getClass().getName());
		} finally {
			tbc.cleanUp(session, null, DmtTestControl.OSGi_CFG);
		}

	}

	/**
	 * @testID testGetSetNodeAcl009
	 * @testDescription This method asserts that it setNodeAcl successfully executed 
	 * 					when the correct permission is assigned
	 */
	private void testGetSetNodeAcl009() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testGetSetNodeAcl009");

			localSession = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(DmtTestControl.OSGi_LOG, new DmtAcl(
					new String[] { DmtTestControl.PRINCIPAL },
					new int[] { DmtAcl.REPLACE }));
			localSession.close();

			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,
					DmtTestControl.OSGi_LOG, DmtSession.LOCK_TYPE_EXCLUSIVE);

			remoteSession.setNodeAcl(DmtTestControl.OSGi_LOG, new DmtAcl(
					new String[] { DmtTestControl.PRINCIPAL },
					new int[] { DmtAcl.ADD }));

			tbc.pass("setNodeAcl correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(localSession, remoteSession, DmtTestControl.OSGi_CFG);
		}
	}

	/**
	 * @testID testGetSetNodeAcl010
	 * @testDescription This method asserts that it setNodeAcl successfully executed 
	 * 					when the correct permission is assigned
	 */
	private void testGetSetNodeAcl010() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl010");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtTestControl.ALL_NODES, DmtPermission.REPLACE));

			session.setNodeAcl(DmtTestControl.OSGi_LOG, new DmtAcl(
					new String[] { DmtTestControl.PRINCIPAL },
					new int[] { DmtAcl.ADD }));

			tbc.pass("setNodeAcl correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(session, null, DmtTestControl.OSGi_CFG);			
		}

	}
	/**
	 * @testID testGetSetNodeAcl011
	 * @testDescription This method asserts that a DmtException is thrown when
	 *                  the session attempts to get an ACL of an invalid node
	 *                  for an invalid node of the tree.
	 */
	private void testGetSetNodeAcl011() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl011");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.getNodeAcl(TestExecPluginActivator.INEXISTENT_NODE);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code was NODE_NOT_FOUND.",
					DmtException.NODE_NOT_FOUND, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetSetNodeAcl012
	 * @testDescription This method asserts that a DmtException is thrown for an
	 *                  uri too long of the tree.
	 */
	private void testGetSetNodeAcl012() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl012");
			DmtAcl acl = new DmtAcl(DmtTestControl.ACLSTR);

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.getNodeAcl(DmtTestControl.URI_LONG);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code was URI_TOO_LONG.",
					DmtException.URI_TOO_LONG, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetSetNodeAcl013
	 * @testDescription This method asserts that a DmtException is thrown for an
	 *                  invalid uri of the tree.
	 */
	private void testGetSetNodeAcl013() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl013");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.getNodeAcl(DmtTestControl.INVALID_URI);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code was INVALID_URI.",
					DmtException.INVALID_URI, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetSetNodeAcl014
	 * @testDescription This method asserts that a DmtException is thrown for an
	 *                  other error of the tree.
	 */
	private void testGetSetNodeAcl014() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl014");

			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.getNodeAcl(DmtTestControl.OSGi_CFG);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code was OTHER_ERROR.",
					DmtException.OTHER_ERROR, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetSetNodeAcl015
	 * @testDescription This method asserts that getNodeAcl returns null if no acl is defined
	 */
	private void testGetSetNodeAcl015() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl015");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			tbc.assertNull("Asserting that it returns null if no acl is defined", session
					.getNodeAcl(TestExecPluginActivator.INTERIOR_NODE));

		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetSetNodeAcl016
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to PERMISSION_DENIED is thrown.
	 */
	private void testGetSetNodeAcl016() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testGetSetNodeAcl016");

			localSession = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(
					DmtTestControl.OSGi_LOG,
					new DmtAcl(new String[] { DmtTestControl.PRINCIPAL },
							new int[] { DmtAcl.EXEC }));
			localSession.close();

			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL, DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			remoteSession.getNodeAcl(DmtTestControl.OSGi_LOG);
			
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is PERMISSION_DENIED",
					DmtException.PERMISSION_DENIED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(localSession, remoteSession, DmtTestControl.OSGi_CFG);
		}

	}

	/**
	 * @testID testGetSetNodeAcl017
	 * @testDescription This method asserts that IllegalStateException
	 *                  is thrown when it closes the session and then try to get
	 *                  an acl
	 */
	private void testGetSetNodeAcl017() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl017");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);
			session.close();
			session.getNodeAcl(DmtTestControl.OSGi_LOG);
			
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
	 * @testID testGetSetNodeAcl018
	 * @testDescription This method asserts that SecurityException is thrown
	 *                  when getNodeAcl is called without the right permission
	 */
	private void testGetSetNodeAcl018() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl018");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtTestControl.ALL_NODES, DmtPermission.EXEC));
			
			session.getNodeAcl(DmtTestControl.OSGi_LOG);
			
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
	 * @testID testGetSetNodeAcl019
	 * @testDescription This method asserts that it getNodeAcl successfully executed 
	 * 					when the correct permission is assigned
	 */
	private void testGetSetNodeAcl019() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testGetSetNodeAcl019");

			localSession = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(DmtTestControl.OSGi_LOG, new DmtAcl(
					new String[] { DmtTestControl.PRINCIPAL },
					new int[] { DmtAcl.GET }));
			localSession.close();

			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,
					DmtTestControl.OSGi_LOG, DmtSession.LOCK_TYPE_EXCLUSIVE);

			remoteSession.getNodeAcl(DmtTestControl.OSGi_LOG);

			tbc.pass("getNodeAcl correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(localSession, remoteSession, DmtTestControl.OSGi_CFG);
		}
	}

	/**
	 * @testID testGetSetNodeAcl020
	 * @testDescription This method asserts that it getNodeAcl successfully executed 
	 * 					when the correct permission is assigned
	 */
	private void testGetSetNodeAcl020() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl020");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtTestControl.ALL_NODES, DmtPermission.GET));

			session.getNodeAcl(DmtTestControl.OSGi_LOG);

			tbc.pass("getNodeAcl correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(session, null, null);			
		}

	}
	
	/**
	 * @testID testGetSetNodeAcl021
	 * @testDescription This method asserts that relative URI works as described.
	 * 
	 */
	private void testGetSetNodeAcl021() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl021");
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtTestControl.ALL_NODES,DmtTestControl.ALL_ACTIONS));
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.getNodeAcl(TestExecPluginActivator.LEAF_VALUE);

			tbc.pass("A relative URI can be used with getNodeAcl.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}	
	
	/**
	 * @testID testGetSetNodeAcl022
	 * @testDescription This method asserts that relative URI works as described.
	 * 
	 */
	private void testGetSetNodeAcl022() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl021");
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtTestControl.ALL_NODES,DmtTestControl.ALL_ACTIONS));
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.setNodeAcl(TestExecPluginActivator.LEAF_VALUE, new DmtAcl(DmtTestControl.ACLSTR));

			tbc.pass("A relative URI can be used with setNodeAcl.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}		
}

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
 * ===========  ===============================================================
 */

package org.osgi.test.cases.dmt.main.tb1.DmtSession;

import org.osgi.service.dmt.DmtAcl;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtMetaNode;
import org.osgi.service.dmt.DmtPermission;
import org.osgi.service.dmt.DmtPrincipalPermission;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.main.tbc.TestInterface;
import org.osgi.test.cases.dmt.main.tbc.Plugin.TestExecPluginActivator;
import org.osgi.test.cases.dmt.main.tbc.Plugin.TestPluginMetaNode;

/**
 * @author Andre Assad
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtSession#getMetaNode
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>getMetaNode<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class GetMetaNode implements TestInterface {
	private DmtTestControl tbc;

	public GetMetaNode(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetMetaNode001();
		testGetMetaNode002();
		testGetMetaNode003();
		testGetMetaNode004();
		testGetMetaNode005();
		testGetMetaNode006();
		testGetMetaNode007();
		testGetMetaNode008();
		testGetMetaNode009();
		testGetMetaNode010();
		testGetMetaNode011();
	}
	/**
	 * @testID testGetMetaNode001
	 * @testDescription Asserts that the metanode was correctly returned.
	 */
	private void testGetMetaNode001() {
		DmtSession session = null;
		try {
			tbc.log("#testGetMetaNode001");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);

			DmtMetaNode metaNode = session.getMetaNode(TestExecPluginActivator.INTERIOR_NODE);
			tbc.assertTrue("Asserts that the metanode was correctly returned.",
					metaNode instanceof TestPluginMetaNode);
			
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * @testID testGetMetaNode002
	 * @testDescription Assert if a DmtException is thrown when a node not found
	 *                  is passed.
	 */
	private void testGetMetaNode002() {
		DmtSession session = null;
		try {
			tbc.log("#testGetMetaNode002");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);

			session.getMetaNode(TestExecPluginActivator.INEXISTENT_NODE);

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
	 * @testID testGetMetaNode003
	 * @testDescription Assert if a DmtException is thrown when an uri too long
	 *                  is passed.
	 */
	private void testGetMetaNode003() {
		DmtSession session = null;
		try {
			tbc.log("#testGetMetaNode003");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);

			session.getMetaNode(DmtTestControl.URI_LONG);

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
	 * @testID testGetMetaNode004
	 * @testDescription Assert if a DmtException is thrown when an invalid uri
	 *                  is passed.
	 */
	private void testGetMetaNode004() {
		DmtSession session = null;
		try {
			tbc.log("#testGetMetaNode004");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);

			session.getMetaNode(DmtTestControl.INVALID_URI);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is INVALID_URI",
					DmtException.INVALID_URI, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetMetaNode005
	 * @testDescription Assert if a DmtException is thrown when a node that
	 *                  can't be accessed is passed.
	 */
	private void testGetMetaNode005() {
		DmtSession session = null;
		try {
			tbc.log("#testGetMetaNode005");

			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_SHARED);

			session.getMetaNode(TestExecPluginActivator.INTERIOR_NODE);
			tbc.failException("", DmtException.class);
			
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is OTHER_ERROR",
					DmtException.OTHER_ERROR, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetMetaNode006
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to PERMISSION_DENIED is thrown.
	 */
	private void testGetMetaNode006() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testGetMetaNode006");

			localSession = tbc.getDmtAdmin().getSession(TestExecPluginActivator.INTERIOR_NODE,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			localSession.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE, new DmtAcl(
					new String[] { DmtTestControl.PRINCIPAL },
					new int[] { DmtAcl.EXEC }));

			localSession.close();
			
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,
					TestExecPluginActivator.INTERIOR_NODE, DmtSession.LOCK_TYPE_EXCLUSIVE);

			remoteSession.getMetaNode(TestExecPluginActivator.INTERIOR_NODE);

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
	 * @testID testGetMetaNode007
	 * @testDescription This method asserts that an SecurityException is thrown
	 *                  when getNodeSize is called without the right permission
	 */
	private void testGetMetaNode007() {
		DmtSession session = null;
		try {
			tbc.log("#testGetMetaNode007");

			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtTestControl.ALL_NODES, DmtPermission.EXEC));

			session.getMetaNode(DmtTestControl.OSGi_LOG);

			tbc.failException("", SecurityException.class);
		} catch (DmtException e) {
			tbc.fail("Expected " + SecurityException.class.getName()
					+ " but was " + e.getClass().getName());
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
	 * @testID testGetMetaNode008
	 * @testDescription This method asserts that an IllegalStateException is
	 *                  thrown when attemps to get a meta node of a closed
	 *                  session
	 */
	private void testGetMetaNode008() {
		DmtSession session = null;
		try {
			tbc.log("#testGetMetaNode008");

			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_SHARED);
			session.close();

			session.getMetaNode(DmtTestControl.OSGi_LOG);
			
			tbc.failException("", IllegalStateException.class);
		} catch (IllegalStateException e) {
			tbc.pass("The Exception was IllegalStateException when use a closed session");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName()
					+ " but was " + e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}

	}

	/**
	 * @testID testGetMetaNode009 
	 * @testDescription This method asserts that no exception is thrown
	 *                  when we set the correct acl permission.
	 */
	private void testGetMetaNode009() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testGetMetaNode009");

			localSession = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			localSession.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE, new DmtAcl(
					new String[] { DmtTestControl.PRINCIPAL },
					new int[] { DmtAcl.GET }));

			localSession.close();
			
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,
					TestExecPluginActivator.INTERIOR_NODE, DmtSession.LOCK_TYPE_EXCLUSIVE);

			remoteSession.getMetaNode(TestExecPluginActivator.INTERIOR_NODE);

			tbc.pass("getMetaNode was successfully executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(localSession, remoteSession, TestExecPluginActivator.INTERIOR_NODE);
		}
	}

	/**
	 * @testID testGetMetaNode010
	 * @testDescription This method asserts that no SecurityException is thrown
	 *                  when getNodeSize is called with the right permission
	 */
	private void testGetMetaNode010() {
		DmtSession session = null;
		try {
			tbc.log("#testGetMetaNode010");

			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtTestControl.ALL_NODES, DmtPermission.GET));

			session.getMetaNode(DmtTestControl.OSGi_LOG);

			tbc.pass("getMetaNode was successfully executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(session, null, null);
		}
	}
	
	/**
	 * @testID testGetMetaNode011
	 * @testDescription This method asserts that relative URI works as described.
	 * 
	 */
	private void testGetMetaNode011() {
		DmtSession session = null;
		try {
			tbc.log("#testGetMetaNode011");
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtTestControl.ALL_NODES,DmtTestControl.ALL_ACTIONS));
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.getMetaNode(TestExecPluginActivator.INTERIOR_VALUE);

			tbc.pass("A relative URI can be used with getMetaNode.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}		
}

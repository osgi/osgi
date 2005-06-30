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
 * Jan 31, 2005  Andre Assad
 * 1             Implement MEG TCK
 * ============  ==============================================================
 * Feb 15, 2005  Andre Assad
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ============  ==============================================================
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
 * @methodUnderTest org.osgi.service.dmt.DmtSession#getNodeTimestamp
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>getNodeTimestamp<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class GetNodeTimestamp implements TestInterface {

	private DmtTestControl tbc;

	public GetNodeTimestamp(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		/*testGetNodeTimestamp001();
		testGetNodeTimestamp002();
		testGetNodeTimestamp003();
		testGetNodeTimestamp004();
		testGetNodeTimestamp005();
		testGetNodeTimestamp006();
		testGetNodeTimestamp007();
		testGetNodeTimestamp008();
		testGetNodeTimestamp009();*/
		testGetNodeTimestamp010();
	}

	/**
	 * @testID testGetNodeTimestamp001
	 * @testDescription This method simulates a NODE_NOT_FOUND exception.
	 */
	private void testGetNodeTimestamp001() {
		DmtSession session = null;
		try {
			tbc.log("#testGetNodeTimestamp001");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);
			session.getNodeTimestamp(TestExecPluginActivator.INEXISTENT_NODE);

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
	 * @testID testGetNodeTimestamp002
	 * @testDescription Simulates a URI_TOO_LONG exception.
	 */
	private void testGetNodeTimestamp002() {
		DmtSession session = null;
		try {
			tbc.log("#testGetNodeTimestamp002");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);
			session.getNodeTimestamp(DmtTestControl.URI_LONG);

			tbc.failException("", DmtException.class);
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
	 * @testID testGetNodeTimestamp003
	 * @testDescription Simulates a INVALID_URI exception.
	 */
	private void testGetNodeTimestamp003() {
		DmtSession session = null;
		try {
			tbc.log("#testGetNodeTimestamp003");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);

			session.getNodeTimestamp(DmtTestControl.INVALID_URI);

			tbc.failException("", DmtException.class);
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
	 * @testID testGetNodeTimestamp004
	 * @testDescription Simulates a OTHER_ERROR exception.
	 */
	private void testGetNodeTimestamp004() {
		DmtSession session = null;
		try {
			tbc.log("#testGetNodeTimestamp004");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_CFG,
					DmtSession.LOCK_TYPE_SHARED);
			session.getNodeTimestamp(TestExecPluginActivator.INTERIOR_NODE);

			tbc.failException("", DmtException.class);
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
	 * @testID testGetNodeTimestamp005
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to PERMISSION_DENIED is thrown.
	 */
	private void testGetNodeTimestamp005() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testGetNodeTimestamp005");

			localSession = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE, new DmtAcl(
					new String[] { DmtTestControl.PRINCIPAL },
					new int[] { DmtAcl.EXEC }));
			localSession.close();
			
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_EXCLUSIVE);

			remoteSession.getNodeTimestamp(TestExecPluginActivator.INTERIOR_NODE);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException's code is PERMISSION_DENIED",
					DmtException.PERMISSION_DENIED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(localSession, remoteSession, TestExecPluginActivator.INTERIOR_NODE);
		}

	}

	/**
	 * @testID testGetNodeTimestamp006
	 * @testDescription This method asserts that an SecurityException is thrown
	 *                  when getNodeTimestamp is called without the right
	 *                  permission
	 */
	private void testGetNodeTimestamp006() {
		DmtSession session = null;
		try {
			tbc.log("#testGetNodeTimestamp006");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtTestControl.ALL_NODES, DmtPermission.EXEC));
			
			session.getNodeTimestamp(TestExecPluginActivator.INTERIOR_NODE);
			
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
	 * @testID testGetNodeTimestamp007
	 * @testDescription Simulates a IllegalStateException, on the close session
	 *                  case.
	 */
	private void testGetNodeTimestamp007() {
		DmtSession session = null;
		try {
			tbc.log("#testGetNodeTimestamp007");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.close();
			session.getNodeTimestamp(DmtTestControl.OSGi_LOG);
			
			tbc.failException("", IllegalStateException.class);
		} catch (IllegalStateException e) {
			tbc.pass("IllegalStateException properly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName()
					+ " but was " + e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * @testID testGetNodeTimestamp008
	 * @testDescription This method asserts that it is successfully executed 
	 * 					when the correct permission is assigned
	 */
	private void testGetNodeTimestamp008() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testGetNodeTimestamp008");

			localSession = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE, new DmtAcl(
					new String[] { DmtTestControl.PRINCIPAL },
					new int[] { DmtAcl.GET }));
			localSession.close();
			
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_EXCLUSIVE);

			remoteSession.getNodeTimestamp(TestExecPluginActivator.INTERIOR_NODE);

			tbc.pass("getNodeTimestamp correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(localSession, remoteSession, TestExecPluginActivator.INTERIOR_NODE);
		}

	}

	/**
	 * @testID testGetNodeTimestamp009
	 * @testDescription This method asserts that it is successfully executed 
	 * 					when the correct permission is assigned
	 */
	private void testGetNodeTimestamp009() {
		DmtSession session = null;
		try {
			tbc.log("#testGetNodeTimestamp009");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtTestControl.ALL_NODES, DmtPermission.GET));
			
			session.getNodeTimestamp(TestExecPluginActivator.INTERIOR_NODE);
			
			tbc.pass("getNodeTimestamp correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
			
		} finally {
			tbc.cleanUp(session, null, null);			
		}
	}
	
	/**
	 * @testID testGetNodeTimestamp010
	 * @testDescription This method asserts that relative URI works as described.
	 * 
	 */
	private void testGetNodeTimestamp010() {
		DmtSession session = null;
		try {
			tbc.log("#testGetNodeTimestamp010");
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtTestControl.ALL_NODES,DmtTestControl.ALL_ACTIONS));
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.getNodeTimestamp(TestExecPluginActivator.INTERIOR_VALUE);

			tbc.pass("A relative URI can be used with getNodeTimestamp.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}		

}

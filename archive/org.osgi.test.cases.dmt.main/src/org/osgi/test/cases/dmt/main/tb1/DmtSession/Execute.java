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
 * @methodUnderTest org.osgi.service.dmt.DmtSession#execute
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>execute<code> method, according to MEG reference
 *                     documentation.
 */
public class Execute implements TestInterface {
	private DmtTestControl tbc;

	public Execute(DmtTestControl tbc) {
		this.tbc = tbc;

	}

	public void run() {
		//TODO missing test case to the method that has correlator as argument. ( three parameters ).
		testExecute001();
		testExecute002();
		testExecute003();
		testExecute004();
		testExecute005();
		testExecute006();
		testExecute007();
		testExecute008();
		testExecute009();
		testExecute010();
		testExecute011();
		testExecute012();
	}

	/**
	 * @testID testExecute001
	 * @testDescription This method asserts that execute correctly throws
	 *                  DmtException when a inexistent node is executed
	 */
	private void testExecute001() {
		DmtSession session = null;
		try {
			tbc.log("#testExecute001");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.execute(DmtTestControl.INVALID_NODE, null);

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
	 * @testID testExecute002
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to URI_TOO_LONG is thrown.
	 */
	private void testExecute002() {
		DmtSession session = null;
		try {
			tbc.log("#testExecute002");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.execute(DmtTestControl.URI_LONG, null);

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
	 * @testID testExecute003
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to INVALID_URI is thrown.
	 */
	private void testExecute003() {
		DmtSession session = null;
		try {
			tbc.log("#testExecute003");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.execute(DmtTestControl.INVALID_URI, null);

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
	 * @testID testExecute004
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to OTHER_ERROR is thrown.
	 */
	private void testExecute004() {
		DmtSession session = null;
		try {
			tbc.log("#testExecute004");

			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			session.execute(TestExecPluginActivator.INTERIOR_NODE, null);

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
	 * @testID testExecute005
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to COMMAND_NOT_ALLOWED is thrown.
	 */
	private void testExecute005() {
		DmtSession session = null;
		try {
			tbc.log("#testExecute005");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.execute(DmtTestControl.OSGi_ROOT, null);

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
	 * @testID testExecute006
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to PERMISSION_DENIED is thrown.
	 */
	private void testExecute006() {
		DmtSession session = null;
		try {
			tbc.log("#testExecute006");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE, new DmtAcl(
					new String[] { DmtTestControl.PRINCIPAL },
					new int[] { DmtAcl.GET }));
			session.close();

			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.execute(TestExecPluginActivator.INTERIOR_NODE, null);
			
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is PERMISSION_DENIED",
					DmtException.PERMISSION_DENIED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(session, null, TestExecPluginActivator.INTERIOR_NODE);
		}

	}

	/**
	 * @testID testExecute007
	 * @testDescription This method asserts that an IllegalStateException is
	 *                  thrown when attemps to run the execute using a closed
	 *                  session.
	 */
	private void testExecute007() {
		DmtSession session = null;
		try {
			tbc.log("#testExecute007");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.close();
			session.execute(DmtTestControl.OSGi_LOG, null);
			
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
	 * @testID testExecute008
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to COMMAND_FAILED is thrown if no DmtExecPlugin
	 *                  is associated with the node.
	 */
	private void testExecute008() {
		DmtSession session = null;
		try {
			tbc.log("#testExecute008");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.execute(DmtTestControl.OSGi_CFG, null);
			
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException's code is COMMAND_FAILED",
					DmtException.COMMAND_FAILED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testExecute009
	 * @testDescription This method asserts that SecurityException is thrown
	 *                  when execute is called without the right permission
	 */
	private void testExecute009() {
		DmtSession session = null;	
		try {
			tbc.log("#testExecute009");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtTestControl.ALL_NODES, DmtPermission.ADD));
			
			session.execute(DmtTestControl.OSGi_LOG, null);
			
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
	 * @testID testExecute010
	 * @testDescription This method asserts that execute is
	 *                  successfully executed when the correct permission is
	 *                  assigned
	 */
	private void testExecute010() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testExecute010");

			localSession = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE, new DmtAcl(
					new String[] { DmtTestControl.PRINCIPAL },
					new int[] { DmtAcl.EXEC }));
			localSession.close();

			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,
					TestExecPluginActivator.INTERIOR_NODE, DmtSession.LOCK_TYPE_EXCLUSIVE);

			remoteSession.execute(TestExecPluginActivator.INTERIOR_NODE, null);
			
			tbc.pass("execute was successfully executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(localSession, remoteSession, TestExecPluginActivator.INTERIOR_NODE);
		}

	}
	/**
	 * @testID testExecute011
	 * @testDescription This method asserts that execute is
	 *                  successfully executed when the correct permission is
	 *                  assigned
	 */
	private void testExecute011() {
		DmtSession session = null;	
		try {
			tbc.log("#testExecute011");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtTestControl.ALL_NODES, DmtPermission.EXEC));
			
			session.execute(TestExecPluginActivator.INTERIOR_NODE, null);
			
			tbc.pass("execute was successfully executed");
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
	 * @testID testExecute012
	 * @testDescription This method asserts that relative URI works as described.
	 * 
	 */
	private void testExecute012() {
		DmtSession session = null;
		try {
			tbc.log("#testExecute012");
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtTestControl.ALL_NODES,DmtTestControl.ALL_ACTIONS));
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.execute(TestExecPluginActivator.INTERIOR_VALUE, null);

			tbc.pass("A relative URI can be used with execute.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}	
}

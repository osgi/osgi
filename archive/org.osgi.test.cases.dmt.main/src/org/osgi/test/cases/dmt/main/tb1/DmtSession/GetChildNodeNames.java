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

/* REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ============  ==============================================================
 * 26/01/2005    Andre Assad
 * 1             Implement TCK
 * ============  ==============================================================
 * 15/02/2005    Leonardo Barros
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-002)
 * ============  ==============================================================
 * Abr 04, 2005  Luiz Felipe Guimaraes
 * 34            [MEGTCK][DMT] CVS update 
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
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtReadOnly#getChildNodeNames
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>getChildNodeNames<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class GetChildNodeNames implements TestInterface {
	private DmtTestControl tbc;

	public GetChildNodeNames(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetChildNodeNames001();
		testGetChildNodeNames002();
		testGetChildNodeNames003();
		testGetChildNodeNames004();
		testGetChildNodeNames005();
		testGetChildNodeNames006();
		testGetChildNodeNames007();
		testGetChildNodeNames008();
		testGetChildNodeNames009();
		testGetChildNodeNames010();
		testGetChildNodeNames011();
		testGetChildNodeNames012();
	}

	/**
	 * @testID testGetChildNodeNames001
	 * @testDescription This method asserts that a DmtException is thrown
	 *                  whenever a tbc.getSession() tries to retrieve the child
	 *                  node names of an invalid node
	 */
	private void testGetChildNodeNames001() {
		DmtSession session = null;
		try {
			tbc.log("#testGetChildNodeNames001");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);

			session.getChildNodeNames(TestExecPluginActivator.INEXISTENT_NODE);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts DmtException.getCode",
					DmtException.NODE_NOT_FOUND, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	
	/**
	 * @testID testGetChildNodeNames002
	 * @testDescription This method asserts if getChildNodeNames throws
	 *                  DmtException with the correct code
	 */
	private void testGetChildNodeNames002() {
		DmtSession session = null;
		try {
			tbc.log("#testGetChildNodeNames002");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);
			session.getChildNodeNames(DmtTestControl.INVALID_URI);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts DmtException.getCode",
					DmtException.INVALID_URI, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetChildNodeNames003
	 * @testDescription This method asserts if getChildNodeNames throws
	 *                  DmtException with the correct code
	 */
	private void testGetChildNodeNames003() {
		DmtSession session = null;
		try {
			tbc.log("#testGetChildNodeNames003");

			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_SHARED);
			session.getChildNodeNames(DmtTestControl.OSGi_CFG);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts DmtException.getCode",
					DmtException.OTHER_ERROR, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetChildNodeNames004
	 * @testDescription This method asserts that a DmtException is thrown
	 *                  whenr it tries to get the child
	 *                  node names of an uri too long
	 */
	private void testGetChildNodeNames004() {
		DmtSession session = null;
		try {
			tbc.log("#testGetChildNodeNames004");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);

			session.getChildNodeNames(DmtTestControl.URI_LONG);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts DmtException.getCode",
					DmtException.URI_TOO_LONG, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetChildNodeNames005
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to PERMISSION_DENIED is thrown.
	 */
	private void testGetChildNodeNames005() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testGetChildNodeNames005");

			localSession = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE, new DmtAcl(
					new String[] { DmtTestControl.PRINCIPAL },
					new int[] { DmtAcl.EXEC }));

			localSession.close();
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,TestExecPluginActivator.INTERIOR_NODE,DmtSession.LOCK_TYPE_SHARED);

			remoteSession.getChildNodeNames(TestExecPluginActivator.INTERIOR_NODE);
			
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
	 * @testID testGetChildNodeNames006
	 * @testDescription This method asserts that an SecurityException is thrown
	 *                  when getChildNodeNames is called without the right
	 *                  permission
	 */
	private void testGetChildNodeNames006() {
		DmtSession session = null;
		try {
			tbc.log("#testGetChildNodeNames006");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtTestControl.ALL_NODES, DmtPermission.EXEC));
			
			session.getChildNodeNames(DmtTestControl.OSGi_LOG);
			
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
	 * @testID testGetChildNodeNames007
	 * @testDescription This method asserts if IllegalStateException is
	 *                  correctly thrown
	 */
	private void testGetChildNodeNames007() {
		DmtSession session = null;
		try {
			tbc.log("#testGetChildNodeNames007");
			
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.close();
			session.getChildNodeNames(DmtTestControl.OSGi_LOG);
			
			tbc.failException("", IllegalStateException.class);;
		} catch (IllegalStateException e) {
			tbc.pass("IllegalStateException correctly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName()
					+ " but was " + e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * @testID testGetChildNodeNames008
	 * @testDescription This method asserts that getChildNodeNames is successfully executed 
	 * 					when the correct permission is assigned
	 */
	private void testGetChildNodeNames008() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testGetChildNodeNames008");

			localSession = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE, new DmtAcl(
					new String[] { DmtTestControl.PRINCIPAL },
					new int[] { DmtAcl.GET }));

			localSession.close();
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,
					TestExecPluginActivator.INTERIOR_NODE, DmtSession.LOCK_TYPE_SHARED);

			remoteSession.getChildNodeNames(TestExecPluginActivator.INTERIOR_NODE);
			tbc.pass("getChildNodeNames correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(localSession, remoteSession, TestExecPluginActivator.INTERIOR_NODE);
		}

	}

	/**
	 * @testID testGetChildNodeNames009
	 * @testDescription This method asserts that getChildNodeNames is successfully executed 
	 * 					when the correct permission is assigned
	 */
	private void testGetChildNodeNames009() {
		DmtSession session = null;
		try {
			tbc.log("#testGetChildNodeNames009");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtTestControl.ALL_NODES, DmtPermission.GET));
			
			session.getChildNodeNames(TestExecPluginActivator.INTERIOR_NODE);
			
			tbc.pass("getChildNodeNames correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
			
		} finally {
			tbc.cleanUp(session, null, null);
		}
	}
	
	/**
	 * @testID testGetChildNodeNames010
	 * @testDescription This method asserts that an empty array is returned by DmtAdmin 
	 * 					when the plugin returns null.
	 */
	private void testGetChildNodeNames010() {
		DmtSession session = null;
		try {
			tbc.log("#testGetChildNodeNames010");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtTestControl.ALL_NODES, DmtPermission.GET));
			
			String[] childs = session.getChildNodeNames(TestExecPluginActivator.ROOT);
			
			tbc.assertTrue("Asserting if an empty array was returned by DmtAdmin.", childs == null ? false : (childs.length == 0)); 
			
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
			
		} finally {
			tbc.cleanUp(session, null, null);
		}
	}	
	
	/**
	 * @testID testGetChildNodeNames011
	 * @testDescription This method asserts that DmtAdmin remove the null 
	 * 					entries when our plugin return an array of string
	 * 					that contains null entries.
	 * 
	 */
	private void testGetChildNodeNames011() {
		DmtSession session = null;
		try {
			tbc.log("#testGetChildNodeNames011");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtTestControl.ALL_NODES, DmtPermission.GET));
			
			String[] childs = session.getChildNodeNames(TestExecPluginActivator.INTERIOR_NODE_WITH_NULL_VALUES);
			
			tbc.assertTrue("Asserting if an array with two elements was returned.", (childs.length == 2));
			
			
			boolean hasNull = false;
			for (int i=0; i<childs.length; i++) {
				if (childs[i] == null) {
					hasNull = true;
					break;
				}
			}
			
			tbc.assertTrue("Asserting if no null entries exists as elements in the returned array.", !hasNull);
			
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
			
		} finally {
			tbc.cleanUp(session, null, null);
		}
	}		
	
	/**
	 * @testID testGetChildNodeNames012
	 * @testDescription This method asserts that relative URI works as described.
	 * 
	 */
	private void testGetChildNodeNames012() {
		DmtSession session = null;
		try {
			tbc.log("#testGetChildNodeNames012");
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtTestControl.ALL_NODES,DmtTestControl.ALL_ACTIONS));
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.getChildNodeNames(TestExecPluginActivator.INTERIOR_VALUE);

			tbc.pass("A relative URI can be used with getChildNodeNames.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}	
	
	
}
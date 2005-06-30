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
 * @methodUnderTest org.osgi.service.dmt.DmtSession#getNodeTitle,setNodeTitle
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>getNodeTitle, setNodeTitle<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class GetSetNodeTitle implements TestInterface {
	private DmtTestControl tbc;

	public GetSetNodeTitle(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetSetNodeTitle001();
		testGetSetNodeTitle002();
		testGetSetNodeTitle003();
		testGetSetNodeTitle004();
		testGetSetNodeTitle005();
		testGetSetNodeTitle006();
		testGetSetNodeTitle007();
		testGetSetNodeTitle008();
		testGetSetNodeTitle009();
		testGetSetNodeTitle010();
		testGetSetNodeTitle011();
		testGetSetNodeTitle012();
		testGetSetNodeTitle013();
		testGetSetNodeTitle014();
		testGetSetNodeTitle015();
		testGetSetNodeTitle016();
		testGetSetNodeTitle017();
		testGetSetNodeTitle018();
		testGetSetNodeTitle019();
		testGetSetNodeTitle020();
	}

	
	/**
	 * @testID testGetSetNodeTitle001
	 * @testDescription This method simulates a NODE_NOT_FOUND exception.
	 */
	private void testGetSetNodeTitle001() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeTitle001");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeTitle(TestExecPluginActivator.INEXISTENT_NODE, DmtTestControl.TITLE);
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
	 * @testID testGetSetNodeTitle002
	 * @testDescription This method simulates a NODE_NOT_FOUND exception.
	 */
	private void testGetSetNodeTitle002() {	
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeTitle002");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);			
			session.getNodeTitle(TestExecPluginActivator.INEXISTENT_NODE);
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
	 * @testID testGetSetNodeTitle003
	 * @testDescription Simulates a URI_TOO_LONG exception.
	 */
	private void testGetSetNodeTitle003() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeTitle003");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeTitle(DmtTestControl.URI_LONG, DmtTestControl.TITLE);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is URI_TOO_LONG",
					DmtException.URI_TOO_LONG, e.getCode());		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}	
	/**
	 * @testID testGetSetNodeTitle004
	 * @testDescription Simulates a URI_TOO_LONG exception.
	 */
	private void testGetSetNodeTitle004() {
		DmtSession session = null;		
		try {
			tbc.log("#testGetSetNodeTitle004");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);		
			session.getNodeTitle(DmtTestControl.URI_LONG);
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
	 * @testID testGetSetNodeTitle005
	 * @testDescription Simulates a INVALID_URI exception.
	 */
	private void testGetSetNodeTitle005() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeTitle005");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeTitle(DmtTestControl.INVALID_URI,
					DmtTestControl.TITLE);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is INVALID_URI",
					DmtException.INVALID_URI, e.getCode());		
			} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was " + e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * @testID testGetSetNodeTitle006
	 * @testDescription Simulates a INVALID_URI exception.
	 */
	private void testGetSetNodeTitle006() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeTitle006");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);		
			session.getNodeTitle(DmtTestControl.INVALID_URI);
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
	 * @testID testGetSetNodeTitle007
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to PERMISSION_DENIED is thrown.
	 */
	private void testGetSetNodeTitle007() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testGetSetNodeTitle007");
			localSession = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE,new DmtAcl(new String[] { DmtTestControl.PRINCIPAL },new int[] { DmtAcl.EXEC } ));
			localSession.close();
			
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,TestExecPluginActivator.INTERIOR_NODE,DmtSession.LOCK_TYPE_ATOMIC);
			remoteSession.getNodeTitle(TestExecPluginActivator.INTERIOR_NODE);
			tbc.failException("", DmtException.class);
		}  catch (DmtException e) {
			tbc.assertEquals(
				"Asserting that DmtException code is PERMISSION_DENIED",
				DmtException.PERMISSION_DENIED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}  finally {
			tbc.cleanUp(localSession, remoteSession, TestExecPluginActivator.INTERIOR_NODE);
		}
	}
	/**
	 * @testID testGetSetNodeTitle008
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to PERMISSION_DENIED is thrown.
	 */
	private void testGetSetNodeTitle008() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testGetSetNodeTitle008");
			localSession = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE,new DmtAcl(new String[] { DmtTestControl.PRINCIPAL },new int[] { DmtAcl.EXEC } ));
			localSession.close();

			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,TestExecPluginActivator.INTERIOR_NODE,DmtSession.LOCK_TYPE_ATOMIC);
			remoteSession.setNodeTitle(TestExecPluginActivator.INTERIOR_NODE,DmtTestControl.TITLE);
			tbc.failException("", DmtException.class);
		}  catch (DmtException e) {
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
	 * @testID testGetSetNodeTitle009
	 * @testDescription This method asserts that getNodeTitle successfully executed 
	 * 					when the correct permission is assigned
	 */
	private void testGetSetNodeTitle009() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testGetSetNodeTitle009");
			localSession = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE,new DmtAcl(new String[] { DmtTestControl.PRINCIPAL },new int[] { DmtAcl.GET } ));
			localSession.close();
			
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,TestExecPluginActivator.INTERIOR_NODE,DmtSession.LOCK_TYPE_ATOMIC);
			remoteSession.getNodeTitle(TestExecPluginActivator.INTERIOR_NODE);
			tbc.pass("getNodeTitle correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		}  finally {
			tbc.cleanUp(localSession, remoteSession, TestExecPluginActivator.INTERIOR_NODE);			
		}
	}
	/**
	 * @testID testGetSetNodeTitle010
	 * @testDescription This method asserts that setNodeTitle is successfully executed 
	 * 					when the correct permission is assigned
	 */
	private void testGetSetNodeTitle010() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testGetSetNodeTitle010");
			localSession = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE,new DmtAcl(new String[] { DmtTestControl.PRINCIPAL },new int[] { DmtAcl.REPLACE } ));
			localSession.close();

			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,TestExecPluginActivator.INTERIOR_NODE,DmtSession.LOCK_TYPE_ATOMIC);
			remoteSession.setNodeTitle(TestExecPluginActivator.INTERIOR_NODE,DmtTestControl.TITLE);
			tbc.pass("setNodeTitle correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(localSession, remoteSession, TestExecPluginActivator.INTERIOR_NODE);
		}

	}

	/**
	 * @testID testGetSetNodeTitle011
	 * @testDescription Simulates a OTHER_ERROR exception.
	 */
	private void testGetSetNodeTitle011() {
		DmtSession session = null;
		tbc.log("#testGetSetNodeTitle011");
		try {
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG);
			session.setNodeTitle(TestExecPluginActivator.INTERIOR_NODE, DmtTestControl.TITLE);
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
	 * @testID testGetSetNodeTitle012
	 * @testDescription Simulates a OTHER_ERROR exception.
	 */
	private void testGetSetNodeTitle012() {
		DmtSession session = null;
		tbc.log("#testGetSetNodeTitle012");
			
		try {
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.getNodeTitle(TestExecPluginActivator.INTERIOR_NODE);
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
	 * @testID testGetSetNodeTitle013
	 * @testDescription This method asserts that an SecurityException 
	 *                  is thrown when setNodeTitle is called without the right permission
	 */
	private void testGetSetNodeTitle013() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeTitle013");
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtTestControl.ALL_NODES,DmtPermission.EXEC));
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeTitle(TestExecPluginActivator.INTERIOR_NODE, DmtTestControl.TITLE);
			tbc.failException("#",SecurityException.class);
		} catch (SecurityException e) {
			tbc.pass("The Exception was SecurityException");
		} catch (Exception e) {
			tbc.fail("Expected " + SecurityException.class.getName() + " but was " + e.getClass().getName());
		} finally {
			tbc.cleanUp(session, null, null);			
		}
	}
	/**
	 * @testID testGetSetNodeTitle014
	 * @testDescription This method asserts that an SecurityException 
	 *                  is thrown when setNodeTitle is called without the right permission
	 */
	private void testGetSetNodeTitle014() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeTitle014");
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtTestControl.ALL_NODES,DmtPermission.ADD));
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.getNodeTitle(TestExecPluginActivator.INTERIOR_NODE);
			tbc.failException("#",SecurityException.class);
		} catch (SecurityException e) {
			tbc.pass("The Exception was SecurityException");
		} catch (Exception e) {
			tbc.fail("Expected " + SecurityException.class.getName() + " but was " + e.getClass().getName());
		} finally {
			tbc.cleanUp(session, null, null);			
		}
	}
	
	/**
	 * @testID testGetSetNodeTitle015
	 * @testDescription This method asserts that it is successfully executed 
	 * 					when the correct permission is assigned
	 */
	private void testGetSetNodeTitle015() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeTitle015");
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtTestControl.ALL_NODES,DmtPermission.REPLACE));
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeTitle(TestExecPluginActivator.INTERIOR_NODE, DmtTestControl.TITLE);
			tbc.pass("setNodeTitle correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(session, null, null);
			
		}
	}
	/**
	 * @testID testGetSetNodeTitle016
	 * @testDescription This method asserts that it is successfully executed 
	 * 					when the correct permission is assigned
	 */
	private void testGetSetNodeTitle016() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeTitle016");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtTestControl.ALL_NODES,DmtPermission.GET));
			session.getNodeTitle(TestExecPluginActivator.INTERIOR_NODE);
			tbc.pass("getNodeTitle correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
			
		} finally {
			tbc.cleanUp(session, null, null);
		}
	}
	
	/**
	 * @testID testGetSetNodeTitle017
	 * @testDescription Simulates a IllegalStateException, on the
	 *                  close session case.
	 */
	private void testGetSetNodeTitle017() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeTitle017");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.close();
			session.setNodeTitle(TestExecPluginActivator.INTERIOR_NODE, DmtTestControl.TITLE);
			tbc.failException("#", IllegalStateException.class);
		} catch (IllegalStateException e) {
			tbc.pass("IllegalStateException properly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName()
					+ " but was " + e.getClass().getName());
		}
	}
	/**
	 * @testID testGetSetNodeTitle018
	 * @testDescription Simulates a IllegalStateException, on the
	 *                  close session case.
	 */
	private void testGetSetNodeTitle018() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeTitle018");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.close();
			session.getNodeTitle(TestExecPluginActivator.INTERIOR_NODE);
			tbc.failException("#", IllegalStateException.class);
		} catch (IllegalStateException e) {
			tbc.pass("IllegalStateException properly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName()
					+ " but was " + e.getClass().getName());
		}
	}
	
	/**
	 * @testID testGetSetNodeTitle019
	 * @testDescription This method asserts that relative URI works as described.
	 * 
	 */
	private void testGetSetNodeTitle019() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeTitle019");
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtTestControl.ALL_NODES,DmtTestControl.ALL_ACTIONS));
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.getNodeTitle(TestExecPluginActivator.LEAF_VALUE);

			tbc.pass("A relative URI can be used with getNodeTitle.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}	
	
	/**
	 * @testID testGetSetNodeTitle020
	 * @testDescription This method asserts that relative URI works as described.
	 * 
	 */
	private void testGetSetNodeTitle020() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeTitle020");
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtTestControl.ALL_NODES,DmtTestControl.ALL_ACTIONS));
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.setNodeTitle(TestExecPluginActivator.LEAF_VALUE, "temp");

			tbc.pass("A relative URI can be used with setNodeTitle.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}			
	
}

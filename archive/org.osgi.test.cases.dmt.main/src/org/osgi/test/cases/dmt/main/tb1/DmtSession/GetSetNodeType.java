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
 * @methodUnderTest org.osgi.service.dmt.DmtSession#getNodeType, setNodeType
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>getNodeType, setNodeType<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class GetSetNodeType implements TestInterface {
	private DmtTestControl tbc;

	private String xmlStr = "<?xml version=\"1.0\"?><data><name>data name</name><value>data value</value></data>";

	public GetSetNodeType(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		//014 setNodeType with an invalid type doesn't throw any exception

		testGetSetNodeType001();
		testGetSetNodeType002();
		testGetSetNodeType003();
		testGetSetNodeType004();
		testGetSetNodeType005();
		testGetSetNodeType006();
		testGetSetNodeType007();
		testGetSetNodeType008();
		testGetSetNodeType009();
		testGetSetNodeType010();
		testGetSetNodeType011();
		testGetSetNodeType012();
		testGetSetNodeType013();
		testGetSetNodeType014();
		testGetSetNodeType015();
		testGetSetNodeType016();
		testGetSetNodeType017();
		testGetSetNodeType018();
		testGetSetNodeType019();
		testGetSetNodeType020();
		testGetSetNodeType021();
		testGetSetNodeType022();

	}

	/**
	 * @testID testGetSetNodeType001
	 * @testDescription This method simulates a NODE_NOT_FOUND exception.
	 */
	private void testGetSetNodeType001() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeType001");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeType(TestExecPluginActivator.INEXISTENT_NODE, "text/xml");
			tbc.failException("#",DmtException.class);
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
	 * @testID testGetSetNodeType002
	 * @testDescription This method simulates a NODE_NOT_FOUND exception.
	 */
	private void testGetSetNodeType002() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeType002");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.getNodeType(TestExecPluginActivator.INEXISTENT_NODE);
			tbc.failException("#",DmtException.class);
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
	 * @testID testGetSetNodeType003
	 * @testDescription Simulates a URI_TOO_LONG exception.
	 */
	private void testGetSetNodeType003() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeType003");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeType(DmtTestControl.URI_LONG,
					"text/xml");
			tbc.failException("#",DmtException.class);
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
	 * @testID testGetSetNodeType004
	 * @testDescription Simulates a URI_TOO_LONG exception.
	 */
	private void testGetSetNodeType004() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeType004");	
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.getNodeType(DmtTestControl.URI_LONG);
			tbc.failException("#",DmtException.class);
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
	 * @testID testGetSetNodeType005
	 * @testDescription Simulates a INVALID_URI exception.
	 */
	private void testGetSetNodeType005() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeType005");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeType(DmtTestControl.INVALID_URI,
					"text/xml");
			tbc.failException("#",DmtException.class);
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
	 * @testID testGetSetNodeType006
	 * @testDescription Simulates a INVALID_URI exception.
	 */
	private void testGetSetNodeType006() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeType006");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.getNodeType(DmtTestControl.INVALID_URI);
			tbc.failException("#",DmtException.class);
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
	 * @testID testGetSetNodeType007
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to PERMISSION_DENIED is thrown.
	 */
	private void testGetSetNodeType007() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testGetSetNodeType007");
			localSession = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE,new DmtAcl(new String[] { DmtTestControl.PRINCIPAL },new int[] { DmtAcl.EXEC } ));
			localSession.close();
			
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,TestExecPluginActivator.INTERIOR_NODE,DmtSession.LOCK_TYPE_ATOMIC);
			remoteSession.getNodeType(TestExecPluginActivator.INTERIOR_NODE);
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
	 * @testID testGetSetNodeType008
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to PERMISSION_DENIED is thrown.
	 */
	private void testGetSetNodeType008() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testGetSetNodeType008");
			localSession = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE,new DmtAcl(new String[] { DmtTestControl.PRINCIPAL },new int[] { DmtAcl.EXEC } ));
			localSession.close();
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,TestExecPluginActivator.INTERIOR_NODE,DmtSession.LOCK_TYPE_ATOMIC);
			
			remoteSession.setNodeType(TestExecPluginActivator.INTERIOR_NODE,DmtTestControl.DDF);
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
	 * @testID testGetSetNodeType009
	 * @testDescription Simulates a OTHER_ERROR exception.
	 */
	private void testGetSetNodeType009() {
		DmtSession session = null;
		tbc.log("#testGetSetNodeType009");
		try {
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_CFG,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.setNodeType(TestExecPluginActivator.INTERIOR_NODE,
					"text/xml");
			tbc.failException("#",DmtException.class);
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
	 * @testID testGetSetNodeType010
	 * @testDescription Simulates a OTHER_ERROR exception.
	 */
	private void testGetSetNodeType010() {
		DmtSession session = null;
		tbc.log("#testGetSetNodeType010");
		try {
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_CFG,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.getNodeType(TestExecPluginActivator.INTERIOR_NODE);
			tbc.failException("#",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is OTHER_ERROR",
					DmtException.OTHER_ERROR, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}  finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetSetNodeType011
	 * @testDescription This method asserts that an SecurityException 
	 *                  is thrown when either setNodeType is called without the right permission
	 */
	private void testGetSetNodeType011() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeType011");
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtTestControl.ALL_NODES,DmtPermission.EXEC));
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.setNodeType(TestExecPluginActivator.INTERIOR_NODE,"text/xml");
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
	 * @testID testGetSetNodeType012
	 * @testDescription This method asserts that an SecurityException 
	 *                  is thrown when either getNodeType is called without the right permission
	 */
	private void testGetSetNodeType012() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeType012");	
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtTestControl.ALL_NODES,DmtPermission.EXEC));
			session.getNodeType(TestExecPluginActivator.INTERIOR_NODE);
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
	 * @testID testGetSetNodeType013
	 * @testDescription Simulates a COMMAND_FAILED exception in case of null type.
	 */
	private void testGetSetNodeType013() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeType013");
			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.INTERIOR_NODE,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.setNodeType(TestExecPluginActivator.INTERIOR_NODE,null);
			tbc.failException("#",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is COMMAND_FAILED",
					DmtException.COMMAND_FAILED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was " + e.getClass().getName());
		}  finally {
			tbc.closeSession(session);
		}
		
	}	
	/**
	 * @testID testGetSetNodeType014
	 * @testDescription Simulates a COMMAND_FAILED exception in case of invalid type.
	 */
	private void testGetSetNodeType014() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeType014");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.setNodeType(TestExecPluginActivator.INTERIOR_NODE,DmtTestControl.INVALID);
			tbc.failException("#",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is COMMAND_FAILED",
					DmtException.COMMAND_FAILED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was " + e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
		
	}	
	
	/**
	 * @testID testGetSetNodeType015
	 * @testDescription Simulates a IllegalStateException, on the
	 *                  close session case.
	 */
	private void testGetSetNodeType015() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeType015");
			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.INTERIOR_NODE,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.close();
			session.setNodeType(TestExecPluginActivator.INTERIOR_NODE, "text/xml");
			tbc.failException("#", IllegalStateException.class);
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
	 * @testID testGetSetNodeType016
	 * @testDescription Simulates a IllegalStateException, on the
	 *                  close session case.
	 */
	private void testGetSetNodeType016() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeType016");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.close();
			session.getNodeType(TestExecPluginActivator.INTERIOR_NODE);
			tbc.failException("#", IllegalStateException.class);
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
	 * @testID testGetSetNodeType017
	 * @testDescription This method asserts that it is successfully executed 
	 * 					when the correct permission is assigned
	 */
	private void testGetSetNodeType017() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testGetSetNodeType017");
			localSession = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE,new DmtAcl(new String[] { DmtTestControl.PRINCIPAL },new int[] { DmtAcl.GET } ));
			localSession.close();
			
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,TestExecPluginActivator.INTERIOR_NODE,DmtSession.LOCK_TYPE_ATOMIC);
			remoteSession.getNodeType(TestExecPluginActivator.INTERIOR_NODE);
			tbc.pass("getNodeType correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(localSession, remoteSession, TestExecPluginActivator.INTERIOR_NODE);
		}
	}
	/**
	 * @testID testGetSetNodeType018
	 * @testDescription This method asserts that it is successfully executed 
	 * 					when the correct permission is assigned
	 */
	private void testGetSetNodeType018() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testGetSetNodeType018");
			localSession = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE,new DmtAcl(new String[] { DmtTestControl.PRINCIPAL },new int[] { DmtAcl.REPLACE } ));
			localSession.close();
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,TestExecPluginActivator.INTERIOR_NODE,DmtSession.LOCK_TYPE_ATOMIC);
			
			remoteSession.setNodeType(TestExecPluginActivator.INTERIOR_NODE,DmtTestControl.DDF);
			tbc.pass("setNodeType correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		}  finally {
			tbc.cleanUp(localSession, remoteSession, TestExecPluginActivator.INTERIOR_NODE);			
		}

	}

	/**
	 * @testID testGetSetNodeType019
	 * @testDescription This method asserts that it is successfully executed 
	 * 					when the correct permission is assigned
	 */
	private void testGetSetNodeType019() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeType019");
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtTestControl.ALL_NODES,DmtPermission.REPLACE));
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.setNodeType(TestExecPluginActivator.INTERIOR_NODE,"text/xml");
			tbc.pass("setNodeType correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(session, null, null);
		}
	}
	/**
	 * @testID testGetSetNodeType020
	 * @testDescription This method asserts that it is successfully executed 
	 * 					when the correct permission is assigned
	 */
	private void testGetSetNodeType020() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeType020");	
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtTestControl.ALL_NODES,DmtPermission.GET));
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.getNodeType(TestExecPluginActivator.INTERIOR_NODE);
			tbc.pass("getNodeType correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(session, null, null);
		}
	}
	
	/**
	 * @testID testGetSetNodeType021
	 * @testDescription This method asserts that relative URI works as described.
	 * 
	 */
	private void testGetSetNodeType021() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeType021");
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtTestControl.ALL_NODES,DmtTestControl.ALL_ACTIONS));
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.getNodeType(TestExecPluginActivator.LEAF_VALUE);

			tbc.pass("A relative URI can be used with getNodeType.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}	
	
	/**
	 * @testID testGetSetNodeType022
	 * @testDescription This method asserts that relative URI works as described.
	 * 
	 */
	private void testGetSetNodeType022() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeType022");
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtTestControl.ALL_NODES,DmtTestControl.ALL_ACTIONS));
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.setNodeType(TestExecPluginActivator.LEAF_VALUE, "text/xml");

			tbc.pass("A relative URI can be used with setNodeType.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}			
}

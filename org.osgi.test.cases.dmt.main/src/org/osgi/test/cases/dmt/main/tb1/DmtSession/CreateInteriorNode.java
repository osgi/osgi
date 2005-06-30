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
 * ===========  ==============================================================
 * 25/01/2005   André Assad
 * 1            Implement TCK
 * ===========  ==============================================================
 * 14/02/2005   Leonardo Barros
 * 1            Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-002)
 * ===========  ==============================================================
 * 28/03/2005   Luiz Felipe Guimaraes
 * 1            Updates 
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
 * @methodUnderTest org.osgi.service.dmt.Dmt#createInteriorNode
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>createInteriorNode<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class CreateInteriorNode implements TestInterface {

	private DmtTestControl tbc;

	public CreateInteriorNode(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testCreateInteriorNode001();
		testCreateInteriorNode002();
		testCreateInteriorNode003();
		testCreateInteriorNode004();
		testCreateInteriorNode005();
		testCreateInteriorNode006();
		testCreateInteriorNode007();
		testCreateInteriorNode008();
		testCreateInteriorNode009();
		testCreateInteriorNode010();
		testCreateInteriorNode011();
		testCreateInteriorNode012();
		testCreateInteriorNode013();
		testCreateInteriorNode014();
		testCreateInteriorNode015();
		testCreateInteriorNode016();
		testCreateInteriorNode017();
		testCreateInteriorNode018();
		testCreateInteriorNode019();
		testCreateInteriorNode020();
		testCreateInteriorNode021();
		testCreateInteriorNode022();
	}

	/**
	 * @testID testCreateInteriorNode001
	 * @testDescription This method test if the code in DmtException is
	 *                  URI_LONG.
	 */
	private void testCreateInteriorNode001() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateInteriorNode001");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtTestControl.URI_LONG);
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts DmtException.getCode",
					DmtException.URI_TOO_LONG, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(session, new String[] { DmtTestControl.URI_LONG });
		}
	}

	/**
	 * @testID testCreateInteriorNode002
	 * @testDescription This method test if the code in DmtException is
	 *                  INVALID_URI.
	 */
	private void testCreateInteriorNode002() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateInteriorNode002");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtTestControl.INVALID_URI);
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
	 * @testID testCreateInteriorNode003
	 * @testDescription This method test if the code in DmtException is
	 *                  NODE_ALREADY_EXISTS.
	 */
	private void testCreateInteriorNode003() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateInteriorNode003");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(TestExecPluginActivator.INTERIOR_NODE);
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts DmtException.getCode",
					DmtException.NODE_ALREADY_EXISTS, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testCreateInteriorNode004
	 * @testDescription This method test if the code in DmtException is
	 *                  OTHER_ERROR.
	 */
	private void testCreateInteriorNode004() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateInteriorNode004");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_CFG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(TestExecPluginActivator.INTERIOR_NODE);
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
	 * @testID testCreateInteriorNode005
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to PERMISSION_DENIED is thrown.
	 */
	private void testCreateInteriorNode005() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testCreateInteriorNode005");
			localSession = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(
					TestExecPluginActivator.ROOT,
					new DmtAcl(new String[] { DmtTestControl.PRINCIPAL },
							new int[] { DmtAcl.EXEC }));
			localSession.close();
			
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL, TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			remoteSession.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE);
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is PERMISSION_DENIED",
					DmtException.PERMISSION_DENIED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(localSession, remoteSession, TestExecPluginActivator.ROOT);
		}
	}

	/**
	 * @testID testCreateInteriorNode006
	 * @testDescription This method asserts that createInteriorNode is
	 *                  successfully executed when the correct ACL is assigned
	 */
	private void testCreateInteriorNode006() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testCreateInteriorNode006");
			localSession = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(
					TestExecPluginActivator.ROOT,
					new DmtAcl(new String[] { DmtTestControl.PRINCIPAL },
							new int[] { DmtAcl.ADD }));
			localSession.close();
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL, TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			remoteSession.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE);
			tbc.pass("createInteriorNode was successfully executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(localSession, remoteSession, TestExecPluginActivator.ROOT);
		}
	}

	/**
	 * @testID testCreateInteriorNode007
	 * @testDescription This method asserts that an SecurityException is thrown
	 *                  when the method is called without the right
	 *                  permission
	 */
	private void testCreateInteriorNode007() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateInteriorNode007");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtTestControl.ALL_NODES,DmtPermission.DELETE));
			session.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE);
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
	 * @testID testCreateInteriorNode008
	 * @testDescription This method asserts that createInteriorNode is
	 *                  successfully executed when the correct permission is
	 *                  assigned
	 */
	private void testCreateInteriorNode008() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateInteriorNode008");
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtTestControl.ALL_NODES,DmtPermission.ADD));
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE);
			tbc.pass("createInteriorNode was successfully executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(session, null, null);
		}
	}

	/**
	 * @testID testCreateInteriorNode009
	 * @testDescription Asserts if DmtException with COMMAND_FAILED code is
	 *                  thrown when null type is passed as parameter.
	 */
	private void testCreateInteriorNode009() {
		DmtSession session = null;
		tbc.log("#testCreateInteriorNode009");
		try {
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE, null);
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is COMMAND_FAILED",
					DmtException.COMMAND_FAILED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testCreateInteriorNode010
	 * @testDescription Asserts if DmtException with COMMAND_FAILED code is
	 *                  thrown when an invalid type is passed as parameter.
	 */
	private void testCreateInteriorNode010() {
		DmtSession session = null;
		tbc.log("#testCreateInteriorNode010");
		try {
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE, DmtTestControl.INVALID);
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is COMMAND_FAILED",
					DmtException.COMMAND_FAILED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * @testID testCreateInteriorNode011
	 * @testDescription Asserts if DmtException with URI_TOO_LONG code is
     *                  thrown.
	 */
	private void testCreateInteriorNode011() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateInteriorNode011");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtTestControl.URI_LONG,
					DmtTestControl.MIMETYPE);
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts DmtException.getCode",
					DmtException.URI_TOO_LONG, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(session, new String[] { DmtTestControl.URI_LONG });
		}
	}

	/**
	 * @testID testCreateInteriorNode012
	 * @testDescription This method test if the code in DmtException is
	 *                  INVALID_URI.
	 */
	private void testCreateInteriorNode012() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateInteriorNode012");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(DmtTestControl.INVALID_URI,
					DmtTestControl.MIMETYPE);
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
	 * @testID testCreateInteriorNode013
	 * @testDescription This method test if the code in DmtException is
	 *                  NODE_ALREADY_EXISTS.
	 */
	private void testCreateInteriorNode013() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateInteriorNode013");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(TestExecPluginActivator.INTERIOR_NODE,
					DmtTestControl.MIMETYPE);
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts DmtException.getCode",
					DmtException.NODE_ALREADY_EXISTS, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);			
		}
	}

	/**
	 * @testID testCreateInteriorNode014
	 * @testDescription This method test if the code in DmtException is
	 *                  OTHER_ERROR.
	 */
	private void testCreateInteriorNode014() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateInteriorNode014");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG, DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE,
							DmtTestControl.MIMETYPE);
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
	 * @testID testCreateInteriorNode015
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to PERMISSION_DENIED is thrown.
	 */
	private void testCreateInteriorNode015() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testCreateInteriorNode015");
			localSession = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(
					TestExecPluginActivator.ROOT,
					new DmtAcl(new String[] { DmtTestControl.PRINCIPAL },
							new int[] { DmtAcl.EXEC }));
			localSession.close();
			
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL, TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			remoteSession.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE,
					DmtTestControl.MIMETYPE);
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is PERMISSION_DENIED",
					DmtException.PERMISSION_DENIED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(localSession, remoteSession, TestExecPluginActivator.ROOT);
		}
	}

	/**
	 * @testID testCreateInteriorNode016
	 * @testDescription This method asserts that createInteriorNode is
	 *                  successfully executed when the correct ACL is assigned
	 */
	private void testCreateInteriorNode016() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testCreateInteriorNode016");
			localSession = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(
					TestExecPluginActivator.ROOT,
					new DmtAcl(new String[] { DmtTestControl.PRINCIPAL },
							new int[] { DmtAcl.ADD }));
			localSession.close();
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL, TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			remoteSession.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE,
					DmtTestControl.MIMETYPE);
			tbc.pass("createInteriorNode was successfully executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(localSession, remoteSession, TestExecPluginActivator.ROOT);
		}
	}

	/**
	 * @testID testCreateInteriorNode017
	 * @testDescription This method asserts that an SecurityException is thrown
	 *                  when createInteriorNode is called without the right
	 *                  permission
	 */
	private void testCreateInteriorNode017() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateInteriorNode017");
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtTestControl.ALL_NODES,DmtPermission.DELETE));
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE,
					DmtTestControl.MIMETYPE);
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
	 * @testID testCreateInteriorNode018
	 * @testDescription This method asserts that createInteriorNode is
	 *                  successfully executed when the correct permission is
	 *                  assigned
	 */
	private void testCreateInteriorNode018() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateInteriorNode018");
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtTestControl.ALL_NODES,DmtPermission.ADD));
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE,
					DmtTestControl.MIMETYPE);
			tbc.pass("createInteriorNode was successfully executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(session, null, null);
		}
	}

	/**
	 * @testID testCreateInteriorNode019
	 * @testDescription This method asserts if IllegalStateException is thrown
	 *                  if this method is called when session is closed.
	 */
	private void testCreateInteriorNode019() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateInteriorNode019");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.close();
			session.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE);
			tbc.failException("", IllegalStateException.class);
		} catch (IllegalStateException e) {
			tbc.pass("IllegalStateException correctly thrown");
		} catch (Exception e) {
			tbc.failException("", IllegalStateException.class);
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testCreateInteriorNode020
	 * @testDescription This method asserts if IllegalStateException is thrown
	 *                  if this method is called when session is closed.
	 */
	private void testCreateInteriorNode020() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateInteriorNode020");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.close();
			session.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE,
					DmtTestControl.MIMETYPE);
			tbc.failException("", IllegalStateException.class);
		} catch (IllegalStateException e) {
			tbc.pass("IllegalStateException correctly thrown");
		} catch (Exception e) {
			tbc.failException("", IllegalStateException.class);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * @testID testCreateInteriorNode021
	 * @testDescription This method asserts that relative URI works as described in this method
	 * 					using the method with only one argument.
	 */
	private void testCreateInteriorNode021() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateInteriorNode021");
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtTestControl.ALL_NODES,DmtTestControl.ALL_ACTIONS));
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);
			
			session.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE_VALUE);
			
			tbc
					.pass("A relative URI can be used with CreateInteriorNode.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}		
	}	
	
	/**
	 * @testID testCreateInteriorNode022
	 * @testDescription This method asserts that relative URI works as described in this method
	 * 					using the method with two arguments.
	 */
	private void testCreateInteriorNode022() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateInteriorNode022");
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtTestControl.ALL_NODES,DmtTestControl.ALL_ACTIONS));
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);
			
			session.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE_VALUE, DmtTestControl.MIMETYPE);
			
			tbc
					.pass("A relative URI can be used with CreateInteriorNode.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}		
	}	

}

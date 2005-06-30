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
 * Feb 11, 2005  Andre Assad
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ============  ==============================================================
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
 * @methodUnderTest org.osgi.service.dmt.DmtSession#getNodeValue, setNodeValue
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>getNodeValue, setNodeValue<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class GetSetNodeValue implements TestInterface {
	private DmtTestControl tbc;

	public GetSetNodeValue(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetSetNodeValue001();
		testGetSetNodeValue002();
		testGetSetNodeValue003();
		testGetSetNodeValue004();
		testGetSetNodeValue005();
		testGetSetNodeValue006();
		testGetSetNodeValue007();
		testGetSetNodeValue008();
		testGetSetNodeValue009();
		testGetSetNodeValue010();
		testGetSetNodeValue011();
		testGetSetNodeValue012();
		testGetSetNodeValue013();
		testGetSetNodeValue014();
		testGetSetNodeValue015();
		testGetSetNodeValue016();
		testGetSetNodeValue017();
		testGetSetNodeValue018();
		testGetSetNodeValue019();
		testGetSetNodeValue020();
		testGetSetNodeValue021();
		testGetSetNodeValue022();
		testGetSetNodeValue023();
	}

	/**
	 * @testID testGetSetNodeValue001
	 * @testDescription Simulates a NODE_NOT_FOUND exception.
	 */
	private void testGetSetNodeValue001() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeValue001");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeValue(TestExecPluginActivator.INEXISTENT_NODE,
					new DmtData("value"));
			tbc.failException("#", DmtException.class);
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
	 * @testID testGetSetNodeValue002
	 * @testDescription Simulates a NODE_NOT_FOUND exception.
	 */
	private void testGetSetNodeValue002() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeValue002");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.getNodeValue(TestExecPluginActivator.INEXISTENT_NODE);
			tbc.failException("#", DmtException.class);
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
	 * @testID testGetSetNodeValue003
	 * @testDescription Simulates a COMMAND_NOT_ALLOWED exception.
	 */
	private void testGetSetNodeValue003() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeValue003");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeValue(TestExecPluginActivator.INTERIOR_NODE, new DmtData("value"));
			tbc.failException("#", DmtException.class);
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
	 * @testID testGetSetNodeValue004
	 * @testDescription Simulates a COMMAND_NOT_ALLOWED exception.
	 */
	private void testGetSetNodeValue004() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeValue004");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.getNodeValue(TestExecPluginActivator.INTERIOR_NODE);
			tbc.failException("#", DmtException.class);
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
	 * @testID testGetSetNodeValue005
	 * @testDescription Simulates a URI_TOO_LONG exception.
	 */
	private void testGetSetNodeValue005() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeValue005");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeValue(DmtTestControl.URI_LONG,
					new DmtData("value"));
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is URI_TOO_LONG",
					DmtException.URI_TOO_LONG, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}
	}
	/**
	 * @testID testGetSetNodeValue006
	 * @testDescription Simulates a URI_TOO_LONG exception.
	 */
	private void testGetSetNodeValue006() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeValue006");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.getNodeValue(DmtTestControl.URI_LONG);
			tbc.failException("#", DmtException.class);
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
	 * @testID testGetSetNodeValue007
	 * @testDescription Simulates an INVALID_URI exception.
	 */
	private void testGetSetNodeValue007() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeValue007");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeValue(DmtTestControl.INVALID_URI,
					new DmtData("value"));
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]", DmtException.INVALID_URI, e
					.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * @testID testGetSetNodeValue008
	 * @testDescription Simulates an INVALID_URI exception.
	 */
	private void testGetSetNodeValue008() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeValue008");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.getNodeValue(DmtTestControl.INVALID_URI);
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("[Exception Code]", DmtException.INVALID_URI, e
					.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testGetSetNodeValue009
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to PERMISSION_DENIED is thrown.
	 */
	private void testGetSetNodeValue009() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testGetSetNodeValue009");
			localSession = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(TestExecPluginActivator.LEAF_NODE,new DmtAcl(new String[] { DmtTestControl.PRINCIPAL },new int[] { DmtAcl.EXEC } ));
			tbc.closeSession(localSession);
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,TestExecPluginActivator.LEAF_NODE,DmtSession.LOCK_TYPE_ATOMIC);
			remoteSession.getNodeValue(TestExecPluginActivator.LEAF_NODE);
			tbc.failException("", DmtException.class);
		}  catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is PERMISSION_DENIED",
					DmtException.PERMISSION_DENIED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(localSession, remoteSession, TestExecPluginActivator.LEAF_NODE);
		}
	}
	/**
	 * @testID testGetSetNodeValue010
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to PERMISSION_DENIED is thrown.
	 */
	private void testGetSetNodeValue010() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testGetSetNodeValue010");
			localSession = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(TestExecPluginActivator.LEAF_NODE,new DmtAcl(new String[] { DmtTestControl.PRINCIPAL },new int[] { DmtAcl.EXEC } ));
			tbc.closeSession(localSession);
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,TestExecPluginActivator.LEAF_NODE,DmtSession.LOCK_TYPE_ATOMIC);
			remoteSession.setNodeValue(TestExecPluginActivator.LEAF_NODE,new DmtData("a"));
			tbc.failException("", DmtException.class);
		}  catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is PERMISSION_DENIED",
					DmtException.PERMISSION_DENIED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(localSession, remoteSession, TestExecPluginActivator.LEAF_NODE);			
		}
	}

	/**
	 * @testID testGetSetNodeValue011
	 * @testDescription Simulates an OTHER_ERROR exception.
	 */
	private void testGetSetNodeValue011() {
		DmtSession session = null;
		tbc.log("#testGetSetNodeValue011");
		try {
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_CFG,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.setNodeValue(TestExecPluginActivator.LEAF_NODE, new DmtData(10));
			tbc.failException("#", DmtException.class);
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
	 * @testID testGetSetNodeValue012
	 * @testDescription Simulates an OTHER_ERROR exception.
	 */
	private void testGetSetNodeValue012() {
		DmtSession session = null;
		tbc.log("#testGetSetNodeValue012");
		try {
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_CFG,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.getNodeValue(TestExecPluginActivator.LEAF_NODE);
			tbc.failException("#", DmtException.class);
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
	 * @testID testGetSetNodeValue013
	 * @testDescription This method asserts that an SecurityException 
	 *                  is thrown when setNodeValue is called without the right permission
	 */
	private void testGetSetNodeValue013() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeValue013");
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtTestControl.ALL_NODES,DmtPermission.EXEC));
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.setNodeValue(TestExecPluginActivator.LEAF_NODE, new DmtData(10));
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
	 * @testID testGetSetNodeValue014
	 * @testDescription This method asserts that an SecurityException 
	 *                  is thrown when getNodeValue is called without the right permission
	 */
	private void testGetSetNodeValue014() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeValue014");
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtTestControl.ALL_NODES,DmtPermission.EXEC));
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			
			session.getNodeValue(TestExecPluginActivator.LEAF_NODE);
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
	 * @testID testGetSetNodeValue015
	 * @testDescription Simulates an IllegalStateException for the
	 *                  cases in which the session is closed.
	 */
	private void testGetSetNodeValue015() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeValue015");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.close();
			session.setNodeValue(TestExecPluginActivator.LEAF_NODE,new DmtData(11));
			tbc.failException("",IllegalStateException.class);
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
	 * @testID testGetSetNodeValue016
	 * @testDescription Simulates an IllegalStateException for the
	 *                  cases in which the session is closed.
	 */
	private void testGetSetNodeValue016() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeValue016");	
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.close();
			session.getNodeValue(TestExecPluginActivator.LEAF_NODE);
			tbc.failException("",IllegalStateException.class);
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
	 * @testID testGetSetNodeValue017
	 * @testDescription Simulates an COMMAND_FAILED exception.
	 */
	private void testGetSetNodeValue017() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeValue017");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.setNodeValue(TestExecPluginActivator.LEAF_NODE, null);

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
	 * @testID testGetSetNodeValue018
	 * @testDescription This method asserts that it is successfully executed 
	 * 					when the correct permission is assigned
	 */
	private void testGetSetNodeValue018() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testGetSetNodeValue018");
			localSession = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(TestExecPluginActivator.LEAF_NODE,new DmtAcl(new String[] { DmtTestControl.PRINCIPAL },new int[] { DmtAcl.GET } ));
			tbc.closeSession(localSession);
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,TestExecPluginActivator.LEAF_NODE,DmtSession.LOCK_TYPE_ATOMIC);
			remoteSession.getNodeValue(TestExecPluginActivator.LEAF_NODE);
			tbc.pass("getNodeValue correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(localSession, remoteSession, TestExecPluginActivator.LEAF_NODE);
		}
	}
	/**
	 * @testID testGetSetNodeValue019
	 * @testDescription This method asserts that it is successfully executed 
	 * 					when the correct permission is assigned
	 */
	private void testGetSetNodeValue019() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testGetSetNodeValue019");
			localSession = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(TestExecPluginActivator.LEAF_NODE,new DmtAcl(new String[] { DmtTestControl.PRINCIPAL },new int[] { DmtAcl.REPLACE } ));
			tbc.closeSession(localSession);
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,TestExecPluginActivator.LEAF_NODE,DmtSession.LOCK_TYPE_ATOMIC);
			remoteSession.setNodeValue(TestExecPluginActivator.LEAF_NODE,new DmtData("a"));
			tbc.pass("setNodeValue correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(localSession, remoteSession, TestExecPluginActivator.LEAF_NODE);
		}
	}
	/**
	 * @testID testGetSetNodeValue020
	 * @testDescription This method asserts that it is successfully executed 
	 * 					when the correct permission is assigned
	 */
	private void testGetSetNodeValue020() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeValue020");
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtTestControl.ALL_NODES,DmtPermission.REPLACE));
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.setNodeValue(TestExecPluginActivator.LEAF_NODE, new DmtData(10));
			tbc.pass("setNodeValue correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(session, null, null);
		}
	}
	/**
	 * @testID testGetSetNodeValue021
	 * @testDescription This method asserts that it is successfully executed 
	 * 					when the correct permission is assigned
	 */
	private void testGetSetNodeValue021() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeValue021");
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtTestControl.ALL_NODES,DmtPermission.GET));
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			
			session.getNodeValue(TestExecPluginActivator.LEAF_NODE);
			tbc.pass("getNodeValue correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(session, null, null);
		}
	}
	
	/**
	 * @testID testGetSetNodeValue022
	 * @testDescription This method asserts that relative URI works as described.
	 * 
	 */
	private void testGetSetNodeValue022() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeValue022");
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtTestControl.ALL_NODES,DmtTestControl.ALL_ACTIONS));
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.getNodeValue(TestExecPluginActivator.LEAF_VALUE);

			tbc.pass("A relative URI can be used with getNodeValue.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}	
	
	/**
	 * @testID testGetSetNodeValue023
	 * @testDescription This method asserts that relative URI works as described.
	 * 
	 */
	private void testGetSetNodeValue023() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeValue023");
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtTestControl.ALL_NODES,DmtTestControl.ALL_ACTIONS));
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.setNodeValue(TestExecPluginActivator.LEAF_VALUE, new DmtData("a"));

			tbc.pass("A relative URI can be used with setNodeValue.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}				
}

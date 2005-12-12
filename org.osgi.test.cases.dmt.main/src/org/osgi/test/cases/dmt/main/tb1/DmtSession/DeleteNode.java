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
 * Jan 25, 2005  Andre Assad
 * CR 1          Implement MEG TCK
 * ============  ==============================================================
 * Feb 14, 2005  Alexandre Santos
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ===========  ==============================================================
 */

package org.osgi.test.cases.dmt.main.tb1.DmtSession;

import org.osgi.service.dmt.Acl;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.security.DmtPermission;
import org.osgi.service.dmt.security.DmtPrincipalPermission;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.main.tbc.DmtConstants;
import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.main.tbc.TestInterface;
import org.osgi.test.cases.dmt.main.tbc.Plugin.ExecPlugin.TestExecPluginActivator;
import org.osgi.test.cases.dmt.main.tbc.Plugin.NonAtomic.TestNonAtomicPluginActivator;
import org.osgi.test.cases.dmt.main.tbc.Plugin.ReadOnly.TestReadOnlyPluginActivator;

/**
 * This Test Case Validates the implementation of <code>deleteNode<code> 
 * method of DmtSession, according to MEG specification
 */
public class DeleteNode implements TestInterface {
	private DmtTestControl tbc;

	public DeleteNode(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {	
        prepare();
		testDeleteNode001();
		testDeleteNode002();
		testDeleteNode003();
		testDeleteNode004();
		testDeleteNode005();
		testDeleteNode006();
		testDeleteNode007();
		testDeleteNode008();
		testDeleteNode009();
		testDeleteNode010();
		testDeleteNode011();
	}
	
    private void prepare() {
        tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
    }
	/**
	 * This method asserts that DmtException.NODE_NOT_FOUND is thrown
	 * if nodeUri points to a non-existing node 
	 * 
	 * @spec DmtSession.deleteNode(String)
	 */
	private void testDeleteNode001() {
		DmtSession session = null;
		try {
			tbc.log("#testDeleteNode001");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.deleteNode(TestExecPluginActivator.INEXISTENT_NODE);

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
	 * This method asserts if IllegalStateException is thrown if this method is called 
	 * when the session is LOCK_TYPE_SHARED
	 * 
	 * @spec DmtSession.deleteNode(String)
	 */
	private void testDeleteNode002() {
		DmtSession session = null;
		try {
			tbc.log("#testDeleteNode002");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_SHARED);
			session.deleteNode(TestExecPluginActivator.INTERIOR_NODE);
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
	 * This method asserts that deleteNode is executed when the right Acl is set (Remote)
	 * 
	 * @spec DmtSession.deleteNode(String)
	 */
	private void testDeleteNode003() {
		DmtSession session = null;
		try {
			tbc.log("#testDeleteNode003");
            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.PRINCIPAL, Acl.DELETE );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL, TestExecPluginActivator.INTERIOR_NODE,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.deleteNode(TestExecPluginActivator.INTERIOR_NODE);
			
			tbc.pass("deleteNode was successfully executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, TestExecPluginActivator.INTERIOR_NODE);
            
		}

	}

	/**
	 * This method asserts that deleteNode is executed when the right DmtPermission is set (Local)
	 * 
	 * @spec DmtSession.deleteNode(String)
	 */
	private void testDeleteNode004() {
		DmtSession session = null;
		try {
			tbc.log("#testDeleteNode004");
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtPermission.DELETE));
			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.deleteNode(TestExecPluginActivator.INTERIOR_NODE);
			
			tbc.pass("deleteNode was successfully executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, null);
            
		}
	}
	
	
	/**
	 * This method asserts that relative URI works as described.
	 * 
	 * @spec DmtSession.deleteNode(String)
	 * 
	 */
	private void testDeleteNode005() {
		DmtSession session = null;
		try {
			tbc.log("#testDeleteNode005");
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.deleteNode(TestExecPluginActivator.INTERIOR_NODE_NAME);

			tbc.pass("A relative URI can be used with deleteNode.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}
	
	
	/**
	 * Asserts that DmtException with COMMAND_NOT_ALLOWED code is thrown when 
	 * the target node is the root of the tree 

	 * 
	 * @spec DmtSession.deleteNode(String)
	 */
	private void testDeleteNode006() {
		DmtSession session = null;
		tbc.log("#testDeleteNode006");
		try {
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.deleteNode(".");
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
	 * Asserts that DmtException with COMMAND_NOT_ALLOWED code is thrown 
	 * if the session is non-atomic (LOCK_TYPE_EXCLUSIVE) 
	 * and the plugin does not support non-atomic writing
	 * 
	 * @spec DmtSession.deleteNode(String)
	 */
	private void testDeleteNode007() {
		DmtSession session = null;
		tbc.log("#testDeleteNode007");
		try {
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.deleteNode(TestNonAtomicPluginActivator.INTERIOR_NODE);
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
	 * Asserts that DmtException with COMMAND_NOT_ALLOWED code is thrown 
	 * if the session is non-atomic (LOCK_TYPE_EXCLUSIVE) 
	 * and the plugin is read-only
	 * 
	 * @spec DmtSession.deleteNode(String)
	 */
	private void testDeleteNode008() {
		DmtSession session = null;
		tbc.log("#testDeleteNode008");
		try {
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.deleteNode(TestReadOnlyPluginActivator.INTERIOR_NODE);
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
	 * This method asserts that DmtException.TRANSACTION_ERROR is thrown 
	 * if the session is atomic and the plugin is read-only 
	 * 
	 * @spec DmtSession.deleteNode(String)
	 */
	private void testDeleteNode009() {
		DmtSession session = null;
		try {
			tbc.log("#testDeleteNode009");
			session = tbc.getDmtAdmin().getSession(".",
			    DmtSession.LOCK_TYPE_ATOMIC);
			
			session.deleteNode(TestReadOnlyPluginActivator.INTERIOR_NODE);
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserting that DmtException code is TRANSACTION_ERROR",
					DmtException.TRANSACTION_ERROR, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts that DmtException.TRANSACTION_ERROR is thrown 
	 * if the session is atomic and the plugin does not support non-atomic writing
	 * 
	 * @spec DmtSession.deleteNode(String)
	 */
	private void testDeleteNode010() {
		DmtSession session = null;
		try {
			tbc.log("#testDeleteNode010");
			session = tbc.getDmtAdmin().getSession(".",
			    DmtSession.LOCK_TYPE_ATOMIC);
			
			session.deleteNode(TestNonAtomicPluginActivator.INTERIOR_NODE);
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserting that DmtException code is TRANSACTION_ERROR",
					DmtException.TRANSACTION_ERROR, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * This method asserts that an empty string as relative URI means the root 
	 * URI the session was opened with
	 * 
	 * @spec DmtSession.deleteNode(String)
	 * 
	 */
	private void testDeleteNode011() {
		DmtSession session = null;
		try {
			tbc.log("#testDeleteNode011");
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.INTERIOR_NODE, DmtSession.LOCK_TYPE_ATOMIC);

			session.deleteNode("");

			tbc.pass("Asserts that an empty string as relative URI means the root" +
					" URI the session was opened with");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}
	
}

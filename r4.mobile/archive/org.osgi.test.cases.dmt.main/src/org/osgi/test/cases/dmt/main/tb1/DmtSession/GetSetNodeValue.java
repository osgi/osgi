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

import org.osgi.service.dmt.Acl;
import org.osgi.service.dmt.DmtData;
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
 * @author Andre Assad
 * 
 * This test case validates the implementation of <code>getNodeValue, setNodeValue</code> 
 * method of DmtSession, according to MEG specification.
 */
public class GetSetNodeValue implements TestInterface {
	private DmtTestControl tbc;
	
	private final DmtData dmtData = new DmtData(10);
	
	public GetSetNodeValue(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
        prepare();
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
	}
    
    private void prepare() {
        tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
    }
	/**
	 * This method asserts that DmtException.NODE_NOT_FOUND is thrown
	 * if nodeUri points to a non-existing node 
	 * 
	 * @spec DmtSession.setNodeValue(String,DmtData)
	 */
	private void testGetSetNodeValue001() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeValue001");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeValue(TestExecPluginActivator.INEXISTENT_NODE,dmtData);
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
	 * This method asserts that DmtException.NODE_NOT_FOUND is thrown
	 * if nodeUri points to a non-existing node 
	 * 
	 * @spec DmtSession.getNodeValue(String)
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
	 * This method asserts that DmtException.NODE_NOT_FOUND is thrown
	 * if nodeUri is not a leaf node 
	 * 
	 * @spec DmtSession.setNodeValue(String,DmtData)
	 */
	private void testGetSetNodeValue003() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeValue003");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeValue(TestExecPluginActivator.INTERIOR_NODE, dmtData);
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
	 * This method asserts that DmtException.COMMAND_NOT_ALLOWED is thrown
	 * if nodeUri is not a leaf node 
	 * 
	 * @spec DmtSession.getNodeValue(String)
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
	 * This method asserts that it is successfully executed when the correct Acl is set (Remote)
	 * 
	 * @spec DmtSession.getNodeValue(String)
	 */
	private void testGetSetNodeValue005() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeValue005");

            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.LEAF_NODE, DmtConstants.PRINCIPAL, Acl.GET );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
            
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,TestExecPluginActivator.LEAF_NODE,DmtSession.LOCK_TYPE_ATOMIC);
			session.getNodeValue(TestExecPluginActivator.LEAF_NODE);
			tbc.pass("getNodeValue correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, TestExecPluginActivator.LEAF_NODE);
            
		}
	}
	/**
	 * This method asserts that it is successfully executed when the correct Acl is set (Remote)
	 * 
	 * @spec DmtSession.setNodeValue(String,DmtData)
	 */
	private void testGetSetNodeValue006() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeValue006");

            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.LEAF_NODE, DmtConstants.PRINCIPAL, Acl.REPLACE );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,TestExecPluginActivator.LEAF_NODE,DmtSession.LOCK_TYPE_ATOMIC);
			session.setNodeValue(TestExecPluginActivator.LEAF_NODE,dmtData);
			tbc.pass("setNodeValue correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session,TestExecPluginActivator.LEAF_NODE);
            
		}
	}
	/**
	 * This method asserts that it is successfully executed  when the correct DmtPermission is set (Local)
	 * 
	 * @spec DmtSession.setNodeValue(String,DmtData)
	 */
	private void testGetSetNodeValue007() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeValue007");
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtConstants.ALL_NODES,DmtPermission.REPLACE));
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.setNodeValue(TestExecPluginActivator.LEAF_NODE, dmtData);
			tbc.pass("setNodeValue correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, null);
            
		}
	}
	/**
	 * This method asserts that it is successfully executed when the correct permission is assigned
	 * 
	 * @spec DmtSession.getNodeValue(String)
	 */
	private void testGetSetNodeValue008() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeValue008");
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtConstants.ALL_NODES,DmtPermission.GET));
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			
			session.getNodeValue(TestExecPluginActivator.LEAF_NODE);
			tbc.pass("getNodeValue correctly executed");
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
	 * @spec DmtSession.getNodeValue(String)
	 */
	private void testGetSetNodeValue009() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeValue009");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.getNodeValue(TestExecPluginActivator.LEAF_RELATIVE);

			tbc.pass("A relative URI can be used with getNodeValue.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}	
	
	/**
	 * This method asserts that relative URI works as described.
	 * 
	 * @spec DmtSession.setNodeValue(String,DmtData)
	 */
	private void testGetSetNodeValue010() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeValue010");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.setNodeValue(TestExecPluginActivator.LEAF_RELATIVE, dmtData);

			tbc.pass("A relative URI can be used with setNodeValue.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}
	
	
	
	/**
	 * This method asserts if IllegalStateException is thrown if this method is called 
	 * when the session is LOCK_TYPE_SHARED
	 * 
	 * @spec DmtSession.setNodeValue(String,DmtData)
	 */
	private void testGetSetNodeValue011() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeValue011");
			session = tbc.getDmtAdmin().getSession(
				TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_SHARED);
			session.setNodeValue(TestExecPluginActivator.LEAF_RELATIVE, dmtData);
			tbc.failException("", IllegalStateException.class);
		} catch (IllegalStateException e) {
			tbc.pass("IllegalStateException correctly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName() + " but was "
				+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

    


	/**
	 * This method asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * if the session is non-atomic (LOCK_TYPE_EXCLUSIVE) and the plugin is read-only
	 *
	 * @spec DmtSession.setNodeValue(String,DmtData)
	 */
    private void testGetSetNodeValue012() {
        DmtSession session = null;
        try {
            tbc.log("#testGetSetNodeValue012");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeValue(TestReadOnlyPluginActivator.LEAF_NODE,dmtData);
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
	 * This method asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * if the session is non-atomic (LOCK_TYPE_EXCLUSIVE) and the plugin 
	 * does not support non-atomic writing
	 *
	 * @spec DmtSession.setNodeValue(String,DmtData)
	 */
    private void testGetSetNodeValue013() {
        DmtSession session = null;
        try {
            tbc.log("#testGetSetNodeValue013");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeValue(TestNonAtomicPluginActivator.LEAF_NODE,dmtData);
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
     * This method asserts that DmtException.TRANSACTION_ERROR is thrown 
     * if the session is atomic and the plugin is read-only 
     * 
     * @spec DmtSession.setNodeValue(String,DmtData)
     */
    private void testGetSetNodeValue014() {
        DmtSession session = null;
        try {
            tbc.log("#testGetSetNodeValue014");
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_ATOMIC);
            
            session.setNodeValue(TestReadOnlyPluginActivator.LEAF_NODE,dmtData);
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
     * @spec DmtSession.setNodeValue(String,DmtData)
     */
    private void testGetSetNodeValue015() {
        DmtSession session = null;
        try {
            tbc.log("#testGetSetNodeValue015");
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_ATOMIC);
            
            session.setNodeValue(TestNonAtomicPluginActivator.LEAF_NODE,dmtData);
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
	 * @spec DmtSession.getNodeValue(String)
	 */
	private void testGetSetNodeValue016() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeValue016");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.LEAF_NODE, DmtSession.LOCK_TYPE_ATOMIC);

			session.getNodeValue("");

			tbc.pass("Asserts that an empty string as relative URI means the root " +
			"URI the session was opened with");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}	
	
	/**
	 * This method asserts that an empty string as relative URI means the root 
	 * URI the session was opened with
	 * 
	 * @spec DmtSession.setNodeValue(String,DmtData)
	 */
	private void testGetSetNodeValue017() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeValue017");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.LEAF_NODE, DmtSession.LOCK_TYPE_ATOMIC);

			session.setNodeValue("", dmtData);

			tbc.pass("Asserts that an empty string as relative URI means the root " +
				"URI the session was opened with");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}
}

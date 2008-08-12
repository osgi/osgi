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
 * @author Andre Assad
 * 
 * This test case validates the implementation of <code>getNodeTitle, setNodeTitle</code> method, 
 * according to MEG specification
 */
public class GetSetNodeTitle implements TestInterface {
	private DmtTestControl tbc;

	public GetSetNodeTitle(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
        prepare();
        if (DmtConstants.SUPPORTS_NODE_TIMESTAMP) {
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
        } else {
            testGetSetNodeTitleFeatureNotSupported001();
            testGetSetNodeTitleFeatureNotSupported002();
        }
	}

    private void prepare() {
        tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
    }
	/**
	 * This method asserts that DmtException.NODE_NOT_FOUND is thrown
	 * if nodeUri points to a non-existing node 
	 * 
	 * @spec DmtSession.setNodeTitle(String,String)
	 */
	private void testGetSetNodeTitle001() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeTitle001");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeTitle(TestExecPluginActivator.INEXISTENT_NODE, DmtConstants.TITLE);
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
	 * This method asserts that DmtException.NODE_NOT_FOUND is thrown
	 * if nodeUri points to a non-existing node 
	 * 
	 * @spec DmtSession.getNodeTitle(String)
	 */
	private void testGetSetNodeTitle002() {	
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeTitle002");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);			
			session.getNodeTitle(TestExecPluginActivator.INEXISTENT_NODE);
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
	 * This method asserts that getNodeTitle is executed when the right Acl is set (Remote)
	 * 
	 * @spec DmtSession.getNodeTitle(String)
	 */
	private void testGetSetNodeTitle003() {

		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeTitle003");
            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.PRINCIPAL, Acl.GET );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,TestExecPluginActivator.INTERIOR_NODE,DmtSession.LOCK_TYPE_ATOMIC);
			session.getNodeTitle(TestExecPluginActivator.INTERIOR_NODE);
			tbc.pass("getNodeTitle correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		}  finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session,TestExecPluginActivator.INTERIOR_NODE);	
            
		}
	}
	/**
	 * This method asserts that setNodeTitle is executed when the right Acl is set (Remote)
	 * 
	 * @spec DmtSession.setNodeTitle(String,String)
	 */
	private void testGetSetNodeTitle004() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeTitle004");
            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.PRINCIPAL, Acl.REPLACE );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,TestExecPluginActivator.INTERIOR_NODE,DmtSession.LOCK_TYPE_ATOMIC);
			session.setNodeTitle(TestExecPluginActivator.INTERIOR_NODE,DmtConstants.TITLE);
			tbc.pass("setNodeTitle correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session,TestExecPluginActivator.INTERIOR_NODE);
            
		}

	}

	
	/**
	 * This method asserts that setNodeTitle is executed when the right DmtPermission is set (Local)
	 * 
	 * @spec DmtSession.setNodeTitle(String,String)
	 */
	private void testGetSetNodeTitle005() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeTitle005");
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtConstants.ALL_NODES,DmtPermission.REPLACE));
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeTitle(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.TITLE);
			tbc.pass("setNodeTitle correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, null);
            
			
		}
	}
	/**
	 * This method asserts that getNodeTitle is executed when the right DmtPermission is set (Local)
	 * 
	 * @spec DmtSession.getNodeTitle(String)
	 */
	private void testGetSetNodeTitle006() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeTitle006");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtConstants.ALL_NODES,DmtPermission.GET));
			session.getNodeTitle(TestExecPluginActivator.INTERIOR_NODE);
			tbc.pass("getNodeTitle correctly executed");
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
	 * @spec DmtSession.getNodeTitle(String)
	 * 
	 */
	private void testGetSetNodeTitle007() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeTitle007");
			
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.getNodeTitle(TestExecPluginActivator.LEAF_RELATIVE);

			tbc.pass("A relative URI can be used with getNodeTitle.");
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
	 * @spec DmtSession.setNodeTitle(String,String)
	 */
	private void testGetSetNodeTitle008() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeTitle008");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.setNodeTitle(TestExecPluginActivator.LEAF_RELATIVE, "temp");

			tbc.pass("A relative URI can be used with setNodeTitle.");
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
	 * @spec DmtSession.setNodeTitle(String,String)
	 */
	private void testGetSetNodeTitle009() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeTitle009");
			session = tbc.getDmtAdmin().getSession(
				TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_SHARED);
			session.setNodeTitle(TestExecPluginActivator.LEAF_RELATIVE, "temp");
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
	 * This method asserts that DmtException.TRANSACTION_ERROR is thrown 
	 * if the session is atomic and the plugin is read-only
	 * 
	 * @spec DmtSession.setNodeTitle(String,DmtData)
	 */
	private void testGetSetNodeTitle010() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeTitle010");
			session = tbc.getDmtAdmin().getSession(".",
			    DmtSession.LOCK_TYPE_ATOMIC);
			
			session.setNodeTitle(TestReadOnlyPluginActivator.LEAF_NODE,DmtConstants.TITLE);
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
	 * @spec DmtSession.setNodeTitle(String,DmtData)
	 */
	private void testGetSetNodeTitle011() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeTitle011");
			session = tbc.getDmtAdmin().getSession(".",
			    DmtSession.LOCK_TYPE_ATOMIC);
			
			session.setNodeTitle(TestNonAtomicPluginActivator.LEAF_NODE,DmtConstants.TITLE);
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
	 * This method asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * if the session is non-atomic (LOCK_TYPE_EXCLUSIVE) and the plugin 
	 * does not support non-atomic writing
	 * 
	 * @spec DmtSession.setNodeTitle(String,DmtData)
	 */
	private void testGetSetNodeTitle012() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeTitle012");
			session = tbc.getDmtAdmin().getSession(".",
			    DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			session.setNodeTitle(TestNonAtomicPluginActivator.LEAF_NODE,DmtConstants.TITLE);
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserting that DmtException code is COMMAND_NOT_ALLOWED",
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
	 * is read-only 
	 * 
	 * @spec DmtSession.setNodeTitle(String,DmtData)
	 */
	private void testGetSetNodeTitle013() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeTitle013");
			session = tbc.getDmtAdmin().getSession(".",
			    DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			session.setNodeTitle(TestReadOnlyPluginActivator.LEAF_NODE,DmtConstants.TITLE);
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserting that DmtException code is COMMAND_NOT_ALLOWED",
					DmtException.COMMAND_NOT_ALLOWED, e.getCode());
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
	 * @spec DmtSession.getNodeTitle(String)
	 * 
	 */
	private void testGetSetNodeTitle014() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeTitle014");
			
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.LEAF_NODE, DmtSession.LOCK_TYPE_ATOMIC);

			session.getNodeTitle("");

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
	 * @spec DmtSession.setNodeTitle(String,String)
	 */
	private void testGetSetNodeTitle015() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeTitle015");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.LEAF_NODE, DmtSession.LOCK_TYPE_ATOMIC);

			session.setNodeTitle("", "temp");

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
     * Asserts that if the DmtAdmin service implementation does not support this method,
     * DmtException.FEATURE_NOT_SUPPORTED is thrown
     * 
     * @spec DmtSession.getNodeTitle(String)
     */
    private void testGetSetNodeTitleFeatureNotSupported001() { 
        DmtSession session = null;
        try {
            tbc.log("#testGetSetNodeTitleFeatureNotSupported001");
            session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
                    DmtSession.LOCK_TYPE_EXCLUSIVE);            
            session.getNodeTitle(TestExecPluginActivator.INTERIOR_NODE);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals(
                    "Asserting that DmtException code is FEATURE_NOT_SUPPORTED",
                    DmtException.FEATURE_NOT_SUPPORTED, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "
                    + e.getClass().getName());
        } finally {
            tbc.closeSession(session);
        }
    }
    
    /**
     * Asserts that if the DmtAdmin service implementation does not support this method,
     * DmtException.FEATURE_NOT_SUPPORTED is thrown
     * 
     * @spec DmtSession.setNodeTitle(String,String)
     */
    private void testGetSetNodeTitleFeatureNotSupported002() { 
        DmtSession session = null;
        try {
            tbc.log("#testGetSetNodeTitleFeatureNotSupported002");
            session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
                    DmtSession.LOCK_TYPE_EXCLUSIVE);            
            session.setNodeTitle(TestExecPluginActivator.INTERIOR_NODE,DmtConstants.TITLE);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals(
                    "Asserting that DmtException code is FEATURE_NOT_SUPPORTED",
                    DmtException.FEATURE_NOT_SUPPORTED, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "
                    + e.getClass().getName());
        } finally {
            tbc.closeSession(session);
        }
    }
}

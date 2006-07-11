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
 * Jun 20, 2005  Luiz Felipe Guimaraes
 * 1             Implement MEG TCK
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tb1.DmtSession;

import info.dmtree.*;
import info.dmtree.security.DmtPermission;
import info.dmtree.security.DmtPrincipalPermission;

import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.*;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.NonAtomic.TestNonAtomicPluginActivator;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ReadOnly.TestReadOnlyPluginActivator;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * This test case validates the implementation of <code>setDefaultNodeValue</code> method of DmtSession, 
 * according to MEG specification.
 */
public class SetDefaultNodeValue implements TestInterface {
	private DmtTestControl tbc;

	public SetDefaultNodeValue(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
	    //DmtException.METADATA_MISMATCH is tested in org.osgi.test.cases.dmt.plugins.tbc.MetaNode.MetaData.MetaData.java
	    //DmtException.INVALID_URI,URI_TOO_LONG,PERMISSION_DENIED are tested in org.osgi.test.cases.dmt.main.tb1.DmtSession.TestExceptions.java
	    //DmtIllegalStateException (timeout or closed session) is tested in org.osgi.test.cases.dmt.main.tb1.TestExceptions.java
	    //SecurityException is tested in org.osgi.test.cases.dmt.main.tb1.DmtSession.TestExceptions.java
        prepare();
		testSetDefaultNodeValue001();
		testSetDefaultNodeValue002();
		testSetDefaultNodeValue003();
		testSetDefaultNodeValue004();
		testSetDefaultNodeValue005();
		testSetDefaultNodeValue006();
		testSetDefaultNodeValue007();
		testSetDefaultNodeValue008();
		testSetDefaultNodeValue009();
		testSetDefaultNodeValue010();
		testSetDefaultNodeValue011();
	}
	
    private void prepare() {
        tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
    }
	/**
	 * This method asserts that DmtException.NODE_NOT_FOUND is thrown 
	 * if nodeUri points to a non-existing node 
	 *
	 * @spec DmtSession.setDefaultNodeValue(String)
	 */
	private void testSetDefaultNodeValue001() {
		DmtSession session = null;
		try {
			tbc.log("#testSetDefaultNodeValue001");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setDefaultNodeValue(TestExecPluginActivator.INEXISTENT_NODE);
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is NODE_NOT_FOUND",
					DmtException.NODE_NOT_FOUND, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * This method asserts that no exception is thrown 
	 * if the specified node is an interior node 
	 *
	 * @spec DmtSession.setDefaultNodeValue(String)
	 */
	private void testSetDefaultNodeValue002() {
		DmtSession session = null;
		try {
			tbc.log("#testSetDefaultNodeValue002");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setDefaultNodeValue(TestExecPluginActivator.INTERIOR_NODE);
			tbc.pass("Asserts that no Exception is thrown if nodeUri is an interior node and DmtSession.setDefaultNodeValue(String) is called");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);		
		} finally {
			tbc.closeSession(session);
		}
	}
	

    
	/**
	 * This method asserts that setDefaultNodeValue is executed when the right Acl is set (Remote) 
	 *
	 * @spec DmtSession.setDefaultNodeValue(String)
	 */
	private void testSetDefaultNodeValue003() {
		DmtSession session = null;
		try {
			tbc.log("#testSetDefaultNodeValue003");

            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.LEAF_NODE, DmtConstants.PRINCIPAL, Acl.REPLACE | Acl.GET );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,TestExecPluginActivator.LEAF_NODE,DmtSession.LOCK_TYPE_ATOMIC);
			session.setDefaultNodeValue(TestExecPluginActivator.LEAF_NODE);
			tbc.pass("setDefaultNodeValue correctly executed");
			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session,TestExecPluginActivator.LEAF_NODE);
            
		}
	}
	/**
	 * This method asserts that setDefaultNodeValue is executed when the right DmtPermission is set 
	 *
	 * @spec DmtSession.setDefaultNodeValue(String)
	 */
	private void testSetDefaultNodeValue004() {
		DmtSession session = null;
		try {
			tbc.log("#testSetDefaultNodeValue004");
            tbc.setPermissions(new PermissionInfo[] {
                    new PermissionInfo(DmtPermission.class.getName(), DmtConstants.OSGi_ROOT, DmtPermission.GET),
                    new PermissionInfo(DmtPermission.class.getName(),DmtConstants.ALL_NODES,DmtPermission.REPLACE)});
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.setDefaultNodeValue(TestExecPluginActivator.LEAF_NODE);
			tbc.pass("setDefaultNodeValue correctly executed");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
			tbc.cleanUp(session, null);
		}
	}
	
	/**
	 * This method asserts that a relative URI can be used with setDefaultNodeValue
	 *
	 * @spec DmtSession.setDefaultNodeValue(String)
	 */
	private void testSetDefaultNodeValue005() {
		DmtSession session = null;
		try {
			tbc.log("#testSetDefaultNodeValue005");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.setDefaultNodeValue(TestExecPluginActivator.LEAF_RELATIVE);

			tbc.pass("A relative URI can be used with setDefaultNodeValue.");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * This method asserts if DmtIllegalStateException is thrown if this method is called 
	 * when the session is LOCK_TYPE_SHARED
	 * 
	 * @spec DmtSession.setDefaultNodeValue(String)
	 */
	private void testSetDefaultNodeValue006() {
		DmtSession session = null;
		try {
			tbc.log("#testSetDefaultNodeValue006");
			session = tbc.getDmtAdmin().getSession(
				TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_SHARED);
			session.setDefaultNodeValue(TestExecPluginActivator.LEAF_NODE);
			tbc.failException("", DmtIllegalStateException.class);
		} catch (DmtIllegalStateException e) {
			tbc.pass("DmtIllegalStateException correctly thrown");
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtIllegalStateException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * This method asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * if the session is non-atomic (LOCK_TYPE_EXCLUSIVE) and the plugin is read-only
	 *
	 * @spec DmtSession.setDefaultNodeValue(String)
	 */
	private void testSetDefaultNodeValue007() {
		DmtSession session = null;
		try {
			tbc.log("#testSetDefaultNodeValue007");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setDefaultNodeValue(TestReadOnlyPluginActivator.LEAF_NODE);
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is COMMAND_NOT_ALLOWED",
					DmtException.COMMAND_NOT_ALLOWED, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * This method asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * if the session is non-atomic (LOCK_TYPE_EXCLUSIVE) and the plugin 
	 * does not support non-atomic writing
	 *
	 * @spec DmtSession.setDefaultNodeValue(String)
	 */
	private void testSetDefaultNodeValue008() {
		DmtSession session = null;
		try {
			tbc.log("#testSetDefaultNodeValue008");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setDefaultNodeValue(TestNonAtomicPluginActivator.LEAF_NODE);
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is COMMAND_NOT_ALLOWED",
					DmtException.COMMAND_NOT_ALLOWED, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
    /**
     * This method asserts that DmtException.TRANSACTION_ERROR is thrown 
     * if the session is atomic and the plugin is read-only 
     * 
     * @spec DmtSession.setDefaultNodeValue(String)
     */
    private void testSetDefaultNodeValue009() {
        DmtSession session = null;
        try {
            tbc.log("#testRenameNode009");
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_ATOMIC);
            
            session.setDefaultNodeValue(TestReadOnlyPluginActivator.LEAF_NODE);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is TRANSACTION_ERROR",
                    DmtException.TRANSACTION_ERROR, e.getCode());
        } catch (Exception e) {
        	tbc.failExpectedOtherException(DmtException.class, e);
        } finally {
            tbc.closeSession(session);
        }
    }
    /**
     * This method asserts that DmtException.TRANSACTION_ERROR is thrown 
     * if the session is atomic and the plugin does not support non-atomic writing
     * 
     * @spec DmtSession.setDefaultNodeValue(String)
     */
    private void testSetDefaultNodeValue010() {
        DmtSession session = null;
        try {
            tbc.log("#testRenameNode010");
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_ATOMIC);
            
            session.setDefaultNodeValue(TestNonAtomicPluginActivator.LEAF_NODE);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is TRANSACTION_ERROR",
                    DmtException.TRANSACTION_ERROR, e.getCode());
        } catch (Exception e) {
        	tbc.failExpectedOtherException(DmtException.class, e);
        } finally {
            tbc.closeSession(session);
        }
    }
	
	/**
	 * This method asserts that an empty string as relative URI means the root 
	 * URI the session was opened with
	 *
	 * @spec DmtSession.setDefaultNodeValue(String)
	 */
	private void testSetDefaultNodeValue011() {
		DmtSession session = null;
		try {
			tbc.log("#testSetDefaultNodeValue011");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.LEAF_NODE, DmtSession.LOCK_TYPE_ATOMIC);

			session.setDefaultNodeValue("");

			tbc.pass("Asserts that an empty string as relative URI means the root " +
				"URI the session was opened with");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}	
}

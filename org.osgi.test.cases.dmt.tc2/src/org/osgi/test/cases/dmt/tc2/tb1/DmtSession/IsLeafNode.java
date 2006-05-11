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
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 21/JAN/2005  Andre Assad
 * CR 1         Implement MEG TCK
 * ===========  ==============================================================
 * Feb 11, 2005 Alexandre Santos
 * 1            Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ===========  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tb1.DmtSession;

import info.dmtree.Acl;
import info.dmtree.DmtException;
import info.dmtree.security.DmtPermission;
import info.dmtree.security.DmtPrincipalPermission;
import info.dmtree.DmtSession;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.TestInterface;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;

/**
 * @author Andre Assad
 * 
 * This test case validates the implementation of <code>isLeafNode</code> method of DmtSession, 
 * according to MEG specification
 */
public class IsLeafNode implements TestInterface  {
	private DmtTestControl tbc;

	public IsLeafNode(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
        prepare();
		testIsLeafNode001();
		testIsLeafNode002();
        testIsLeafNode003();
		testIsLeafNode004();
		testIsLeafNode005();
        testIsLeafNode006();
        testIsLeafNode007();
	}
    private void prepare() {
        tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
    }
	/**
	 * Asserts that isLeafNode returns 'true' when the node is a leaf node
	 *  
	 * @spec DmtSession.isLeafNode(String)
	 */

	private void testIsLeafNode001() {
	    DmtSession session = null;
		try {
			tbc.log("#testIsLeafNode001");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
	        tbc.assertTrue("Asserting that isLeafNode() returns true when a leaf node is passed.", session.isLeafNode(TestExecPluginActivator.LEAF_NODE));
	    } catch (Exception e) {
	    	tbc.failUnexpectedException(e);       
	    } finally {
	        tbc.closeSession(session);	        
	    }
	}

	/**
	 * Asserts that isLeafNode returns 'false' when the node is an interior node
	 * 
	 * @spec DmtSession.isLeafNode(String)
	 */
	private void testIsLeafNode002() {
	    DmtSession session = null;
        try {
			tbc.log("#testIsLeafNode002");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.assertTrue("Asserting that isLeafNode() returns false when an interior node is passed.", 
					!session.isLeafNode(TestExecPluginActivator.INTERIOR_NODE));
        } catch (Exception e) {
        	tbc.failUnexpectedException(e);
        } finally {
        	tbc.closeSession(session);
        }
	}

   /**
	 * This method asserts that DmtException.NODE_NOT_FOUND is thrown
	 * if nodeUri points to a non-existing node
	 * 
	 * @spec DmtSession.isLeafNode(String) 
     */
    private void testIsLeafNode003() {
	    DmtSession session = null;
        try {
			tbc.log("#testIsLeafNode003");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
        	session.isLeafNode(TestExecPluginActivator.INEXISTENT_LEAF_NODE);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is NODE_NOT_FOUND", DmtException.NODE_NOT_FOUND, e.getCode());
        } catch (Exception e) {
        	tbc.failExpectedOtherException(DmtException.class, e);
        } finally {
        	tbc.closeSession(session);
        }
        
    }	
        
    /**
	 * This method asserts that isLeafNode is executed when the right Acl is set (Remote)
	 * 
	 * @spec DmtSession.isLeafNode(String)
	 */
	private void testIsLeafNode004() {
	    DmtSession session = null;
		try {
			tbc.log("#testIsLeafNode004");

            tbc.openSessionAndSetNodeAcl(DmtConstants.OSGi_LOG, DmtConstants.PRINCIPAL, Acl.GET );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,DmtConstants.OSGi_LOG,DmtSession.LOCK_TYPE_ATOMIC);
			session.isLeafNode(DmtConstants.OSGi_LOG);
			tbc.pass("isLeafNode correctly executed");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session,DmtConstants.OSGi_LOG);
            
		}

	}
	
    /**
     * This method asserts that isLeafNode is executed when the right DmtPermission is set (Local)
     * 
     * @spec DmtSession.isLeafNode(String)
     */
    private void testIsLeafNode005() {
        DmtSession session = null;
    	try {
			tbc.log("#testIsLeafNode005");
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtConstants.ALL_NODES,DmtPermission.GET));
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.isLeafNode(DmtConstants.OSGi_LOG);
			tbc.pass("isLeafNode correctly executed");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, null);
            
		}
        
    }
    
	/**
	 * This method asserts that relative URI works as described.
	 * 
	 * @spec DmtSession.isLeafNode(String)
	 * 
	 */
	private void testIsLeafNode006() {
		DmtSession session = null;
		try {
			tbc.log("#testIsLeafNode006");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.isLeafNode(TestExecPluginActivator.LEAF_RELATIVE);

			tbc.pass("A relative URI can be used with isLeafNode.");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * This method asserts that an empty string as relative URI means the root 
	 * URI the session was opened with
	 * 
	 * @spec DmtSession.isLeafNode(String)
	 * 
	 */
	private void testIsLeafNode007() {
		DmtSession session = null;
		try {
			tbc.log("#testIsLeafNode007");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.LEAF_NODE, DmtSession.LOCK_TYPE_ATOMIC);

			session.isLeafNode("");

			tbc.pass("Asserts that an empty string as relative URI means the root " +
					"URI the session was opened with");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
}

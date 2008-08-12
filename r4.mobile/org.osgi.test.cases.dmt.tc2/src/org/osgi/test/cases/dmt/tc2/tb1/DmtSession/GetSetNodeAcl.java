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
 * Feb 14, 2005  Alexandre Santos
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ===========  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tb1.DmtSession;

import info.dmtree.*;
import info.dmtree.security.DmtPermission;
import info.dmtree.security.DmtPrincipalPermission;

import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.*;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;

/**
 * @author Andre Assad
 * 
 * This test case validates the implementation of <code>getNodeAcl, setNodeAcl</code> methods of DmtSession, 
 * according to MEG specification
 */
public class GetSetNodeAcl implements TestInterface {

	private DmtTestControl tbc;

	public GetSetNodeAcl(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
        prepare();
		testGetSetNodeAcl001();
		testGetSetNodeAcl002();
		testGetSetNodeAcl003();
		testGetSetNodeAcl004();
		testGetSetNodeAcl005();
		testGetSetNodeAcl006();
		testGetSetNodeAcl007();
		testGetSetNodeAcl008();
		testGetSetNodeAcl009();
		testGetSetNodeAcl010();
		testGetSetNodeAcl011();
		testGetSetNodeAcl012();
		testGetSetNodeAcl013();
		testGetSetNodeAcl014();

	}
    private void prepare() {
        tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
    }
	/**
	 * This method asserts that a Acl is correctly set for a given node in the tree.
	 * 
	 * @spec DmtSession.setNodeAcl(String,Acl)
	 */
	private void testGetSetNodeAcl001() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl001");
			Acl acl = new Acl(DmtConstants.ACLSTR);
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeAcl(DmtConstants.OSGi_LOG, acl);

			tbc.assertEquals("Asserting node Acl", acl.toString(), session
					.getNodeAcl(DmtConstants.OSGi_LOG).toString());

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session, DmtConstants.OSGi_LOG);
		}

	}

	/**
	 * This method asserts that DmtException.NODE_NOT_FOUND is thrown
	 * if nodeUri points to a non-existing node 
	 * 
	 * @spec DmtSession.setNodeAcl(String,Acl)
	 */
	private void testGetSetNodeAcl002() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl002");
			Acl acl = new Acl(DmtConstants.ACLSTR);

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeAcl(TestExecPluginActivator.INEXISTENT_NODE, acl);
			tbc.failException("", DmtException.class);
			
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code was NODE_NOT_FOUND.",
					DmtException.NODE_NOT_FOUND, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.cleanUp(session, TestExecPluginActivator.INEXISTENT_NODE);
		}
	}

	

	/**
	 * This method asserts that setNodeAcl is executed when the right Acl is set (Remote)
	 * 
	 * @spec DmtSession.setNodeAcl(String,Acl)
	 */
	private void testGetSetNodeAcl003() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl003");

            tbc.openSessionAndSetNodeAcl(DmtConstants.OSGi_LOG, DmtConstants.PRINCIPAL, Acl.REPLACE | Acl.GET );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,
					DmtConstants.OSGi_LOG, DmtSession.LOCK_TYPE_EXCLUSIVE);
			Acl acl = new Acl(
                new String[] { DmtConstants.PRINCIPAL },
                new int[] { Acl.ADD });
            
			session.setNodeAcl(DmtConstants.OSGi_LOG, acl);
            session.close();
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_EXCLUSIVE);

			tbc.assertEquals("Asserts that setNodeAcl really sets the Acl of a node",acl,session.getNodeAcl(DmtConstants.OSGi_LOG));
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session,DmtConstants.OSGi_LOG);
            
		}
	}

	/**
	 * This method asserts that setNodeAcl is executed when the right DmtPermission is set (Local)
	 * 
	 * @spec DmtSession.setNodeAcl(String,Acl)
	 */
	private void testGetSetNodeAcl004() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl004");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtConstants.ALL_NODES, DmtPermission.REPLACE));

            Acl acl =  new Acl(
                new String[] { DmtConstants.PRINCIPAL },
                new int[] { Acl.ADD });
			session.setNodeAcl(DmtConstants.OSGi_LOG,acl);
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
			
            tbc.assertEquals("Asserts that setNodeAcl really sets the Acl of a node",acl,session.getNodeAcl(DmtConstants.OSGi_LOG));
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, DmtConstants.OSGi_LOG);		
            
		}

	}
	/**
	 * This method asserts that DmtException.NODE_NOT_FOUND is thrown
	 * if nodeUri points to a non-existing node 
	 * 
	 * @spec DmtSession.getNodeAcl(String)
	 */
	private void testGetSetNodeAcl005() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl005");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.getNodeAcl(TestExecPluginActivator.INEXISTENT_NODE);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code was NODE_NOT_FOUND.",
					DmtException.NODE_NOT_FOUND, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}

	

	/**
	 * This method asserts that getNodeAcl returns null if no acl is defined
	 * 
	 * @spec DmtSession.getNodeAcl(String)
	 */
	private void testGetSetNodeAcl006() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl006");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			tbc.assertNull("Asserting that it returns null if no acl is defined", session
					.getNodeAcl(TestExecPluginActivator.INTERIOR_NODE));

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}

	
	/**
	 * This method asserts that getNodeAcl is executed when the right Acl is set (Remote)
	 * 
	 * @spec DmtSession.getNodeAcl(String)
	 */
	private void testGetSetNodeAcl007() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl007");

            tbc.openSessionAndSetNodeAcl(DmtConstants.OSGi_LOG, DmtConstants.PRINCIPAL, Acl.GET );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,
					DmtConstants.OSGi_LOG, DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.getNodeAcl(DmtConstants.OSGi_LOG);

			tbc.pass("getNodeAcl correctly executed");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session,DmtConstants.OSGi_LOG);
            
		}
	}

	/**
	 * This method asserts that getNodeAcl is executed when the right DmtPermission is set (Local)
	 * 
	 * @spec DmtSession.getNodeAcl(String)
	 */
	private void testGetSetNodeAcl008() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl008");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtConstants.ALL_NODES, DmtPermission.GET));

			session.getNodeAcl(DmtConstants.OSGi_LOG);

			tbc.pass("getNodeAcl correctly executed");
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
	 * @spec DmtSession.getNodeAcl(String)
	 */
	private void testGetSetNodeAcl009() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl009");
			
		
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.getNodeAcl(TestExecPluginActivator.LEAF_RELATIVE);

			tbc.pass("A relative URI can be used with getNodeAcl.");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}	
	
	/**
	 * This method asserts that relative URI works as described.
	 * 
	 * @spec DmtSession.setNodeAcl(String,Acl)
	 */
	private void testGetSetNodeAcl010() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl010");
			
		
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);
			Acl acl =  new Acl(DmtConstants.ACLSTR);
			session.setNodeAcl(TestExecPluginActivator.LEAF_RELATIVE,acl);
			
            tbc.assertEquals("A relative URI can be used with setNodeAcl.",acl,session.getNodeAcl(TestExecPluginActivator.LEAF_NODE));
			
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
	 * @spec DmtSession.setNodeAcl(String,Acl)
	 */
	private void testGetSetNodeAcl011() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl011");
			session = tbc.getDmtAdmin().getSession(
				TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_SHARED);
			session.setNodeAcl(TestExecPluginActivator.LEAF_RELATIVE, new Acl(DmtConstants.ACLSTR));
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
	 * if the command attempts to set the ACL of the root node not to include 
	 * Add rights for all principals 
	 * 
	 * @spec DmtSession.setNodeAcl(String,Acl)
	 */
	private void testGetSetNodeAcl012() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl012");
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), ".",DmtConstants.ALL_ACTIONS));
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeAcl(".",new Acl("Add=www.cesar.org.br"));

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code was COMMAND_NOT_ALLOWED.",
					DmtException.COMMAND_NOT_ALLOWED, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.closeSession(session);
		}
	}
	
	
	
	/**
	 * This method asserts that an empty string as relative URI means the root 
	 * URI the session was opened with
	 * 
	 * @spec DmtSession.getNodeAcl(String)
	 */
	private void testGetSetNodeAcl013() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl013");
			
		
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.LEAF_NODE, DmtSession.LOCK_TYPE_ATOMIC);

			session.getNodeAcl("");


			tbc.pass("Asserts that an empty string as relative URI means the root " +
					"URI the session was opened with");
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
	 * @spec DmtSession.setNodeAcl(String,Acl)
	 */
	private void testGetSetNodeAcl014() {
		DmtSession session = null;
		try {
			tbc.log("#testGetSetNodeAcl014");
			
		
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.LEAF_NODE, DmtSession.LOCK_TYPE_ATOMIC);
			Acl acl =  new Acl(DmtConstants.ACLSTR);
			session.setNodeAcl("",acl);
			
            tbc.assertEquals("Asserts that an empty string as relative URI means the root " +
					"URI the session was opened with",acl,session.getNodeAcl(TestExecPluginActivator.LEAF_NODE));

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
}

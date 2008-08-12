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
 * Aug 18, 2005  Luiz Felipe Guimaraes
 * 23            Update test cases according to changes in the Acl API
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.main.tbc.Acl;

import java.lang.reflect.Modifier;

import org.osgi.service.dmt.Acl;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.security.DmtPrincipalPermission;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.main.tbc.DmtConstants;
import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.main.tbc.Plugin.ExecPlugin.TestExecPlugin;
import org.osgi.test.cases.dmt.main.tbc.Plugin.ExecPlugin.TestExecPluginActivator;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * This class validates the constraints of Acl, according to MEG specification
 * 
 */
public class AclConstraints {
	private DmtTestControl tbc;

	public AclConstraints(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testAclConstraints001();
		testAclConstraints002();
		testAclConstraints003();
		testAclConstraints004();
		testAclConstraints005();
		testAclConstraints006();
		testAclConstraints007();
		testAclConstraints008();
        testAclConstraints009();
        testAclConstraints010();
        testAclConstraints011();
        testAclConstraints012();

	}

	/**
	 * This test asserts that white space between tokens is not allowed.
	 * 
	 * @spec 117.7 Access Control Lists
	 */
	private void testAclConstraints001() {

		try {
			tbc.log("#testAclConstraints001");
			
            new org.osgi.service.dmt.Acl("Add=test&Exec=test &Get=*");
			
            tbc.failException("",IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass("White space between tokens of a Acl is not allowed.");			
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalArgumentException.class.getName() + " but was "
					+ e.getClass().getName());
		}
	}
	
	/**
	 * This test asserts that if the root node ACL is not explicitly set, it should be set to Add=*&Get=*&Replace=*.
	 * 
	 * @spec 117.7 Access Control Lists
	 */
	private void testAclConstraints002() {
		DmtSession session = null;
		try {
			tbc.log("#testAclConstraints002");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			String expectedRootAcl = "Add=*&Get=*&Replace=*";
			String rootAcl = session.getNodeAcl(".").toString();
			tbc.assertEquals("This test asserts that if the root node ACL is not explicitly set, it should be set to Add=*&Get=*&Replace=*.",expectedRootAcl,rootAcl);
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * This test asserts that the root node of DMT must always have an ACL associated with it
	 * 
	 * @spec 117.7 Access Control Lists
	 */
	private void testAclConstraints003() {
		DmtSession session = null;
		try {
			tbc.log("#testAclConstraints003");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeAcl(".",null);
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {	
			tbc.assertEquals("Asserts that the root node of DMT must always have an ACL associated with it",
			    DmtException.COMMAND_NOT_ALLOWED,e.getCode());
			
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
				+ e.getClass().getName());
		} finally {
			try {
				session.setNodeAcl(".",new org.osgi.service.dmt.Acl("Add=*&Get=*&Replace=*"));
			} catch (Exception e) {
				tbc.fail("Unexpected Exception: " + e.getClass().getName()
						+ " [Message: " + e.getMessage() + "]");
			} finally {
				tbc.closeSession(session);
			}
		}
	}
	
	/**
	 * This test asserts that the root's ACL can be changed
	 * 
	 * @spec 117.7 Access Control Lists
	 */
	private void testAclConstraints004() {
		DmtSession session = null;
		try {
			tbc.log("#testAclConstraints004");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			String expectedRootAcl = "Add=*&Exec=*&Replace=*";
			session.setNodeAcl(".",new org.osgi.service.dmt.Acl(expectedRootAcl));
			String rootAcl = session.getNodeAcl(".").toString();
			tbc.assertEquals("Asserts that the root's ACL can be changed.",expectedRootAcl,rootAcl);
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			try {
				session.setNodeAcl(".",new org.osgi.service.dmt.Acl("Add=*&Get=*&Replace=*"));
			} catch (Exception e) {
				tbc.fail("Unexpected Exception: " + e.getClass().getName()
						+ " [Message: " + e.getMessage() + "]");
			} finally {
				tbc.closeSession(session);
			}
		}
	}
	
	/**
	 * The Dmt Admin service synchronizes the ACLs with any change 
	 * in the DMT that is made through its service interface.
	 * This test case we test if the deleteNode method deletes also the ACL of that node.
	 * 
	 * @spec 117.7.2 Ghost Acls
	 */
	private void testAclConstraints005() {
		DmtSession session = null;
		try {
			tbc.log("#testAclConstraints005");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE,
					new org.osgi.service.dmt.Acl("Replace=*"));

			session.deleteNode(TestExecPluginActivator.INTERIOR_NODE);

			tbc.assertNull("This test asserts that the Dmt Admin service synchronizes the ACLs with any change "
							+ "in the DMT that is made through its service interface",
							session.getNodeAcl(TestExecPluginActivator.INTERIOR_NODE));
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This test case we test if the rename method renames also the ACL of that node.
	 * 
	 * @spec 117.7.2 Ghost Acls
	 */
	
	private void testAclConstraints006() {
		DmtSession session = null;
		try {
			tbc.log("#testAclConstraints006");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			String expectedRootAcl = "Add=*";
			session.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE,
					new org.osgi.service.dmt.Acl(expectedRootAcl));

			session.renameNode(TestExecPluginActivator.INTERIOR_NODE,TestExecPluginActivator.RENAMED_NODE_NAME);
			TestExecPlugin.setAllUriIsExistent(true);
			tbc.assertNull("Asserts that the method rename deletes the ACL of the source.",session.getNodeAcl(TestExecPluginActivator.INTERIOR_NODE));
			
			tbc.assertEquals("Asserts that the method rename moves the ACL from the source to the destiny.",
					expectedRootAcl,session.getNodeAcl(TestExecPluginActivator.RENAMED_NODE).toString());
			
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(session,TestExecPluginActivator.RENAMED_NODE);
			TestExecPlugin.setAllUriIsExistent(false);
		}
	}
	
	/**
	 * This test asserts that if a principal has Replace access to a node, 
	 * the principal is permitted to change the ACL of all its child nodes, 
	 * regardless of the ACLs that are set on the child nodes.
	 * 
	 * @spec 117.7 Access Control Lists
	 */
	private void testAclConstraints007() {
		DmtSession session = null;
		try {
			tbc.log("#testAclConstraints007");
            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.LEAF_NODE, DmtConstants.PRINCIPAL_2, Acl.GET );
            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.PRINCIPAL, Acl.REPLACE );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,TestExecPluginActivator.ROOT,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeAcl(TestExecPluginActivator.LEAF_NODE,new org.osgi.service.dmt.Acl("Get=*"));
			
			tbc.pass("If a principal has Replace access to a node, the principal is permitted to change the ACL of all its child nodes");
			
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(session,TestExecPluginActivator.LEAF_NODE);
			tbc.cleanAcl(TestExecPluginActivator.INTERIOR_NODE);
			
		}
	}
	/**
	 * This test asserts that Replace access on a leaf node does not allow changing the ACL property itself.
	 * 
	 * @spec 117.7 Access Control Lists
	 */
	private void testAclConstraints008() {
		DmtSession session = null;
		try {
			tbc.log("#testAclConstraints008");
            //We need to set that a parent of the node does not have Replace else the acl of the root "." is gotten
            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.PRINCIPAL, Acl.DELETE );
            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.LEAF_NODE, DmtConstants.PRINCIPAL, Acl.REPLACE );

            tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
            session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,TestExecPluginActivator.LEAF_NODE,DmtSession.LOCK_TYPE_EXCLUSIVE);
            session.setNodeAcl(TestExecPluginActivator.LEAF_NODE,new org.osgi.service.dmt.Acl("Get=*"));
			tbc.failException("",DmtException.class);
		} catch (DmtException e) {	
			tbc.assertEquals("Asserts that Replace access on a leaf node does not allow changing the ACL property itself.",
			    DmtException.PERMISSION_DENIED,e.getCode());
			
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.cleanUp(session,TestExecPluginActivator.LEAF_NODE);
            tbc.cleanAcl(TestExecPluginActivator.INTERIOR_NODE);
			
		}
	}
    
    /**
     * Asserts that ACLs must only be verified by the Dmt Admin service when the session 
     * has an associated principal.
     * 
     * @spec 117.7 Access Control Lists
     */
    private void testAclConstraints009() {
        DmtSession session = null;
        try {
            tbc.log("#testAclConstraints009");
            session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.ROOT,
                    DmtSession.LOCK_TYPE_EXCLUSIVE);

            session.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE,
                    new org.osgi.service.dmt.Acl("Replace=*"));

            session.deleteNode(TestExecPluginActivator.INTERIOR_NODE);

            tbc.pass("ACLs is only verified by the Dmt Admin service when the session has an associated principal.");
        } catch (Exception e) {
            tbc.fail("Unexpected Exception: " + e.getClass().getName()
                    + " [Message: " + e.getMessage() + "]");
        } finally {
            tbc.closeSession(session);
        }
    }
    
    /**
     * This method asserts that the copy methods does not copy the Acl.
     * It also tests that the copied nodes inherit the access rights 
     * from the parent of the destination node.
     * 
     * @spec 117.4.10 Copying Nodes
     */
    private void testAclConstraints010() {
        DmtSession session = null;
        try {
            tbc.log("#testAclConstraints010");
            tbc.cleanAcl(TestExecPluginActivator.INEXISTENT_NODE);
            
            session = tbc.getDmtAdmin().getSession(".",
                    DmtSession.LOCK_TYPE_EXCLUSIVE);
            Acl aclParent = new Acl(new String[] { DmtConstants.PRINCIPAL },new int[] { Acl.REPLACE });
            session.setNodeAcl(TestExecPluginActivator.ROOT,
                    aclParent);
            
            session.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE,
                    new Acl(new String[] { DmtConstants.PRINCIPAL },
                            new int[] { Acl.EXEC }));
            session.copy(TestExecPluginActivator.INTERIOR_NODE,
                    TestExecPluginActivator.INEXISTENT_NODE, true);
            TestExecPlugin.setAllUriIsExistent(true);
            tbc.assertTrue("Asserts that the copied nodes inherit the access rights from the parent of the destination node.",
                aclParent.equals(session.getEffectiveNodeAcl(TestExecPluginActivator.INEXISTENT_NODE)));
            
        } catch (Exception e) {
            tbc.fail("Unexpected Exception: " + e.getClass().getName()
                + " [Message: " + e.getMessage() + "]");
        } finally {
            tbc.cleanUp(session, TestExecPluginActivator.INTERIOR_NODE);
            tbc.cleanAcl(TestExecPluginActivator.ROOT);
            TestExecPlugin.setAllUriIsExistent(false);
        }
    }
    
    /**
     * This method asserts that when copy method is called if the calling principal does not 
     * have Replace rights for the parent, the destiny node must be set with an Acl having 
     * Add, Delete and Replace permissions
     * 
     * @spec 117.4.10 Copying Nodes
     */
    private void testAclConstraints011() {
        DmtSession session = null;
        try {
            tbc.log("#testAclConstraints011");
            
            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.PRINCIPAL, Acl.GET | Acl.ADD );
            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.ROOT, DmtConstants.PRINCIPAL, Acl.ADD );

            Acl aclExpected = new Acl(new String[] { DmtConstants.PRINCIPAL },new int[] { Acl.ADD | Acl.DELETE | Acl.REPLACE });

            tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class
					.getName(), DmtConstants.PRINCIPAL, "*"));
			
            session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL, ".",
                    DmtSession.LOCK_TYPE_EXCLUSIVE);
            
            session.copy(TestExecPluginActivator.INTERIOR_NODE,
                    TestExecPluginActivator.INEXISTENT_NODE, true);
            TestExecPlugin.setAllUriIsExistent(true);

            session.close();
            session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
            
            tbc.assertTrue("Asserts that if the calling principal does not have Replace rights for the parent, " +
            		"the destiny node must be set with an Acl having Add, Delete and Replace permissions.",
            		aclExpected.equals(session.getNodeAcl(TestExecPluginActivator.INEXISTENT_NODE)));
            
        } catch (Exception e) {
            tbc.fail("Unexpected Exception: " + e.getClass().getName()
                + " [Message: " + e.getMessage() + "]");
        } finally {
            tbc.cleanUp(session, TestExecPluginActivator.INTERIOR_NODE);
            tbc.cleanAcl(TestExecPluginActivator.ROOT);
            tbc.cleanAcl(TestExecPluginActivator.INEXISTENT_NODE);
            TestExecPlugin.setAllUriIsExistent(false);
        }
    }
    
    /**
     * Asserts that Acl is a public final class
     * 
     * @spec 117.12.2 Acl
     */
    private void testAclConstraints012() {
        try {
            int aclModifiers = Acl.class.getModifiers();
            tbc.assertTrue("Asserts that Acl is a public final class", aclModifiers == (Modifier.FINAL | Modifier.PUBLIC));
            
        } catch (Exception e) {
            tbc.fail("Unexpected Exception: " + e.getClass().getName()
                + " [Message: " + e.getMessage() + "]");
        }
    }
}

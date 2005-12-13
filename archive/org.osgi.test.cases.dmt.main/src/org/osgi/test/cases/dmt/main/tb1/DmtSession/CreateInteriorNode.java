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

import org.osgi.service.dmt.Acl;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.security.DmtPermission;
import org.osgi.service.dmt.security.DmtPrincipalPermission;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.main.tbc.DmtConstants;
import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.main.tbc.TestInterface;
import org.osgi.test.cases.dmt.main.tbc.Plugin.ExecPlugin.TestExecPlugin;
import org.osgi.test.cases.dmt.main.tbc.Plugin.ExecPlugin.TestExecPluginActivator;
import org.osgi.test.cases.dmt.main.tbc.Plugin.NonAtomic.TestNonAtomicPluginActivator;
import org.osgi.test.cases.dmt.main.tbc.Plugin.ReadOnly.TestReadOnlyPluginActivator;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * This test class validates the implementation of <code>createInteriorNode</code> method of DmtSession, 
 * according to MEG specification
 */
public class CreateInteriorNode implements TestInterface {

	private DmtTestControl tbc;

	public CreateInteriorNode(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
        prepare();
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
        testCreateInteriorNode023();
        testCreateInteriorNode024();
	}
    private void prepare() {
        tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
    }
	/**
	 * This method asserts that DmtException.NODE_ALREADY_EXISTS is thrown if is tried to create an existing interior node
	 * 
	 * @spec DmtSession.createInteriorNode(String)
	 */
	private void testCreateInteriorNode001() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateInteriorNode001");
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
	 * This method asserts that createInteriorNode is successfully executed when the correct ACL is assigned
	 * 
	 * @spec DmtSession.createInteriorNode(String)
	 */
	private void testCreateInteriorNode002() {

		DmtSession session = null;
		try {
			tbc.log("#testCreateInteriorNode002");
			
            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.ROOT, DmtConstants.PRINCIPAL, Acl.ADD );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL, TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE);
			tbc.pass("createInteriorNode was successfully executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session,TestExecPluginActivator.ROOT);
            
		}
	}

	/**
	 * This method asserts that createInteriorNode is successfully executed 
	 * when the correct permission is assigned
	 * 
	 * @spec DmtSession.createInteriorNode(String)
	 */
	private void testCreateInteriorNode003() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateInteriorNode003");
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtConstants.ALL_NODES,DmtPermission.ADD));
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE);
			tbc.pass("createInteriorNode was successfully executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, null);
            
		}
	}

    /**
     * Asserts that if the principal does not have Replace access rights on the 
     * parent of the new node then the session must automatically set the 
     * ACL of the new node so that the creating server has Add, Delete and 
     * Replace rights on the new node.
     * 
     * @spec DmtSession.createInteriorNode(String,String)
     */
    private void testCreateInteriorNode004() {
        DmtSession session = null;
        try {
            tbc.log("#testCreateInteriorNode004");

            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.ROOT, DmtConstants.PRINCIPAL, Acl.ADD );
            tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
            session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL, TestExecPluginActivator.ROOT,
                    DmtSession.LOCK_TYPE_ATOMIC);
            session.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE);
            TestExecPlugin.setAllUriIsExistent(true);
            session.close();
            
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.ROOT,DmtSession.LOCK_TYPE_EXCLUSIVE);
            Acl acl = session.getNodeAcl(TestExecPluginActivator.INEXISTENT_NODE);
            tbc.assertTrue("Asserts that if the principal does not have Replace access rights on the parent " +
                    "of the new node then the session must automatically set the ACL of the new node so that " +
                    "the creating server has Add, Delete and Replace rights on the new node. ",
                    acl.isPermitted(DmtConstants.PRINCIPAL,Acl.ADD | Acl.DELETE | Acl.REPLACE) && 
                    !acl.isPermitted(DmtConstants.PRINCIPAL,Acl.GET | Acl.EXEC));
            
            
        } catch (Exception e) {
            tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
        } finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session,TestExecPluginActivator.ROOT);
            TestExecPlugin.setAllUriIsExistent(false);
            
        }
    }

	/**
	 * This method asserts that DmtException.NODE_ALREADY_EXISTS is thrown if is tried to create an existing interior node
	 * 
	 * @spec DmtSession.createInteriorNode(String,String)
	 */
	private void testCreateInteriorNode005() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateInteriorNode005");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(TestExecPluginActivator.INTERIOR_NODE,
					DmtConstants.MIMETYPE);
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
	 * This method asserts that createInteriorNode is successfully executed when the correct ACL is assigned
	 * 
	 * @spec DmtSession.createInteriorNode(String,String)
	 */
	private void testCreateInteriorNode006() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateInteriorNode006");

            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.ROOT, DmtConstants.PRINCIPAL, Acl.ADD );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL, TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE,
					DmtConstants.MIMETYPE);
			tbc.pass("createInteriorNode was successfully executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session,TestExecPluginActivator.ROOT);
            
		}
	}

	

	/**
	 * This method asserts that createInteriorNode is successfully executed when the correct permission is assigned
	 * 
	 * @spec DmtSession.createInteriorNode(String,String)
	 */
	private void testCreateInteriorNode007() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateInteriorNode007");
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtConstants.ALL_NODES,DmtPermission.ADD));
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE,
					DmtConstants.MIMETYPE);
			tbc.pass("createInteriorNode was successfully executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, null);
		}
	}

	
	
	/**
	 * This method asserts that relative URI works as described in this method using the method with only one argument.
	 * 
	 * @spec DmtSession.createInteriorNode(String)
	 */
	private void testCreateInteriorNode008() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateInteriorNode008");
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);
			
			session.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE_NAME);
			
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
	 * This method asserts that relative URI works as described in this method using the method with two arguments.
	 * 
	 * @spec DmtSession.createInteriorNode(String,String)
	 */
	private void testCreateInteriorNode009() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateInteriorNode009");
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);
			
			session.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE_NAME, DmtConstants.MIMETYPE);
			
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
	 * This method asserts if IllegalStateException is thrown if this method is called 
	 * when the session is LOCK_TYPE_SHARED
	 * 
	 * @spec DmtSession.createInteriorNode(String,String)
	 */
	private void testCreateInteriorNode010() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateInteriorNode010");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_SHARED);
			session.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE);
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
	 * Asserts that DmtException with COMMAND_NOT_ALLOWED code is thrown when the parent
	 * is not an interior node.
	 * 
	 * @spec DmtSession.createInteriorNode(String,String)
	 */
	private void testCreateInteriorNode011() {
		DmtSession session = null;
		tbc.log("#testCreateInteriorNode011");
		try {
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(TestExecPluginActivator.LEAF_NODE +"/test", null);
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
	 * Asserts that DmtException with COMMAND_NOT_ALLOWED code is thrown when the parent
	 * is not an interior node.
	 * 
	 * @spec DmtSession.createInteriorNode(String)
	 */
	private void testCreateInteriorNode012() {
		DmtSession session = null;
		tbc.log("#testCreateInteriorNode012");
		try {
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(TestExecPluginActivator.LEAF_NODE +"/test");
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
	 * if the session is non-atomic (LOCK_TYPE_EXCLUSIVE) and the plugin is read-only 
	 * 
	 * @spec DmtSession.createInteriorNode(String)
	 */
	private void testCreateInteriorNode013() {
		DmtSession session = null;
		tbc.log("#testCreateInteriorNode013");
		try {
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.createInteriorNode(TestReadOnlyPluginActivator.INEXISTENT_NODE);
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
     * Asserts that if the parent node does not exist, it is created automatically,  
     * as if createInteriorNode(String) were called for the parent URI using the method with three parameters.
     * 
     * @spec DmtSession.createInteriorNode(String)
     */
    private void testCreateInteriorNode014() {
        DmtSession session = null;
        try {
            tbc.log("#testCreateInteriorNode014");
            TestExecPlugin.resetCount();
            session = tbc.getDmtAdmin().getSession(".",
                    DmtSession.LOCK_TYPE_EXCLUSIVE);

            session.createInteriorNode(TestExecPluginActivator.INEXISTENT_INTERIOR_NODES);

           
            tbc.assertTrue("Asserts that if the parent node does not exist, it is created automatically, " +
                    "as if createInteriorNode(String) were called for the parent URI", 
                    TestExecPlugin.getCreateInteriorNodeCount()==2);
        } catch (Exception e) {
            tbc.fail("Unexpected Exception: " + e.getClass().getName()
                    + " [Message: " + e.getMessage() + "]");
        } finally {
            tbc.cleanUp(session, null);
            
        }
    }
    /**
     * Asserts that if the parent node does not exist, it is created automatically,  
     * as if createInteriorNode(String) were called for the parent URI using the method with three parameters.
     * 
     * @spec DmtSession.createInteriorNode(String,String)
     */
    private void testCreateInteriorNode015() {
        DmtSession session = null;
        try {
            tbc.log("#testCreateInteriorNode015");
            TestExecPlugin.resetCount();
            session = tbc.getDmtAdmin().getSession(".",
                    DmtSession.LOCK_TYPE_EXCLUSIVE);

            session.createInteriorNode(TestExecPluginActivator.INEXISTENT_INTERIOR_NODES,DmtConstants.MIMETYPE);

           
            tbc.assertTrue("Asserts that if the parent node does not exist, it is created automatically, " +
                    "as if createInteriorNode(String) were called for the parent URI", 
                    TestExecPlugin.getCreateInteriorNodeCount()==2);
        } catch (Exception e) {
            tbc.fail("Unexpected Exception: " + e.getClass().getName()
                    + " [Message: " + e.getMessage() + "]");
        } finally {
            tbc.cleanUp(session, null);
            
        }
    }
    
    /**
     * Asserts that any exceptions encountered while creating the ancestors are propagated to 
     * the caller of this method
     * 
     * @spec DmtSession.createInteriorNode(String)
     */
    private void testCreateInteriorNode016() {
        DmtSession session = null;
        try {
            tbc.log("#testCreateInteriorNode016");
            TestExecPlugin.setExceptionAtCreateInteriorNode(true);
            session = tbc.getDmtAdmin().getSession(".",
                    DmtSession.LOCK_TYPE_EXCLUSIVE);

            session.createInteriorNode(TestExecPluginActivator.INEXISTENT_INTERIOR_NODES);
            
            tbc.failException("", IllegalStateException.class);
        } catch (IllegalStateException e) {
            tbc.pass("Asserts that any exceptions encountered while creating the ancestors are propagated to the caller of createLeafNode");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalStateException.class.getName() + " but was "
                + e.getClass().getName());
        } finally {
            tbc.cleanUp(session, null);
            TestExecPlugin.setExceptionAtCreateInteriorNode(false);
            
        }
    }
    
    /**
     * Asserts that any exceptions encountered while creating the ancestors are propagated to 
     * the caller of this method
     * 
     * @spec DmtSession.createInteriorNode(String,String)
     */
    private void testCreateInteriorNode017() {
        DmtSession session = null;
        try {
            tbc.log("#testCreateInteriorNode017");
            TestExecPlugin.setExceptionAtCreateInteriorNode(true);
            session = tbc.getDmtAdmin().getSession(".",
                    DmtSession.LOCK_TYPE_EXCLUSIVE);

            session.createInteriorNode(TestExecPluginActivator.INEXISTENT_INTERIOR_NODES,DmtConstants.MIMETYPE);
            
            tbc.failException("", IllegalStateException.class);
        } catch (IllegalStateException e) {
            tbc.pass("Asserts that any exceptions encountered while creating the ancestors are propagated to the caller of createLeafNode");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalStateException.class.getName() + " but was "
                + e.getClass().getName());
        } finally {
            tbc.cleanUp(session, null);
            TestExecPlugin.setExceptionAtCreateInteriorNode(false);
            
        }
    }
    
    /**
     * This method asserts that DmtException.TRANSACTION_ERROR 
     * if the session is atomic and the plugin is read-only
     * 
     * @spec DmtSession.createInteriorNode(String)
     */
    private void testCreateInteriorNode018() {
        DmtSession session = null;
        try {
            tbc.log("#testCreateInteriorNode018");
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_ATOMIC);
            
            session.createInteriorNode(TestReadOnlyPluginActivator.INEXISTENT_NODE);

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
     * if the session is atomic and the plugin is read-only
     * 
     * @spec DmtSession.createInteriorNode(String,String)
     */
    private void testCreateInteriorNode019() {
        DmtSession session = null;
        try {
            tbc.log("#testCreateInteriorNode019");
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_ATOMIC);
            
            session.createInteriorNode(TestReadOnlyPluginActivator.INEXISTENT_NODE,DmtConstants.MIMETYPE);

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
     * This method asserts that DmtException.TRANSACTION_ERROR 
     * if the session is atomic and the plugin does not support non-atomic writing
     * 
     * @spec DmtSession.createInteriorNode(String)
     */
    private void testCreateInteriorNode020() {
        DmtSession session = null;
        try {
            tbc.log("#testCreateInteriorNode020");
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_ATOMIC);
            
            session.createInteriorNode(TestNonAtomicPluginActivator.INEXISTENT_NODE);

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
     * @spec DmtSession.createInteriorNode(String,String)
     */
    private void testCreateInteriorNode021() {
        DmtSession session = null;
        try {
            tbc.log("#testCreateInteriorNode021");
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_ATOMIC);
            
            session.createInteriorNode(TestNonAtomicPluginActivator.INEXISTENT_NODE,DmtConstants.MIMETYPE);

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
     * Asserts that DmtException with COMMAND_NOT_ALLOWED code is thrown  
     * if the session is non-atomic (in this case LOCK_TYPE_EXCLUSIVE) and the plugin 
     * does not support non-atomic writing
     * 
     * @spec DmtSession.createInteriorNode(String)
     */
    private void testCreateInteriorNode022() {
        DmtSession session = null;
        tbc.log("#testCreateInteriorNode022");
        try {
            session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
            session.createInteriorNode(TestNonAtomicPluginActivator.INEXISTENT_NODE);
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
	 * This method asserts that an empty string as relative URI means the root 
	 * URI the session was opened with (it throws DmtException.NODE_ALREADY_EXISTS)
	 * using the method with one parameter
	 * 
	 * @spec DmtSession.createInteriorNode(String)
	 */
	private void testCreateInteriorNode023() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateInteriorNode023");
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.INTERIOR_NODE, DmtSession.LOCK_TYPE_ATOMIC);
			
			session.createInteriorNode("");
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is NODE_ALREADY_EXISTS",
					DmtException.NODE_ALREADY_EXISTS, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}  finally {
			tbc.closeSession(session);
		}		
	}	
	
	/**
	 * This method asserts that an empty string as relative URI means the root 
	 * URI the session was opened with (it throws DmtException.NODE_ALREADY_EXISTS)
	 * using the method with two parameters
	 * 
	 * @spec DmtSession.createInteriorNode(String,String)
	 */
	private void testCreateInteriorNode024() {
		DmtSession session = null;
		try {
			tbc.log("#testCreateInteriorNode024");
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.INTERIOR_NODE, DmtSession.LOCK_TYPE_ATOMIC);
			
			session.createInteriorNode("",DmtConstants.MIMETYPE);
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is NODE_ALREADY_EXISTS",
					DmtException.NODE_ALREADY_EXISTS, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		}  finally {
			tbc.closeSession(session);
		}		
	}	
    
}

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
 * @author Andre Assad
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtSession#isLeafNode
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>isLeafNode<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class IsLeafNode implements TestInterface  {
	private DmtTestControl tbc;

	public IsLeafNode(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testIsLeafNode001();
		testIsLeafNode002();
        testIsLeafNode003();
		testIsLeafNode004();
		testIsLeafNode005();
        testIsLeafNode006();
		testIsLeafNode007();
		testIsLeafNode008();
		testIsLeafNode009();
		testIsLeafNode010();
		testIsLeafNode011();
		testIsLeafNode012();
	}

	/**
	 * @testID testIsLeafNode001
	 * @testDescription This method creates a new leaf node and test wheather
	 *                  the node is a leaf or an interior node.
	 */

	private void testIsLeafNode001() {
	    DmtSession session = null;
		try {
			tbc.log("#testIsLeafNode001");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
	        tbc.assertTrue("Asserting true for leaf node", session.isLeafNode(TestExecPluginActivator.LEAF_NODE));
	    } catch (DmtException e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");         
	    } finally {
	        tbc.closeSession(session);	        
	    }
	}

	/**
	 * @testID testIsLeafNode002
	 * @testDescription This method tests wheather the OSGi_ROOT node is a leaf
	 *                  or an interior node.
	 */
	private void testIsLeafNode002() {
	    DmtSession session = null;
        try {
			tbc.log("#testIsLeafNode002");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.assertTrue("Asserting true if isLeafNode() returns false when an interior node is passed.", 
					!session.isLeafNode(TestExecPluginActivator.INTERIOR_NODE));
        } catch (DmtException e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]"); 
        } finally {
        	tbc.closeSession(session);
        }
	}

   /**
     * @testID testIsLeafNode003
     * @testDescription This method asserts that a DmtException with error code equals to 
     *                  NODE_NOT_FOUND is thrown using the constructor with only one argument.
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
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        } finally {
        	tbc.closeSession(session);
        }
        
    }	
     
    
    /**
     * @testID testIsLeafNode004
     * @testDescription This method asserts that a DmtException with error code equals to 
     *                  OTHER_ERROR is thrown using the constructor with only one argument.
     */
    private void testIsLeafNode004() {
	    DmtSession session = null;
        try {
			tbc.log("#testIsLeafNode004");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
        	session.isLeafNode(DmtTestControl.OSGi_CFG);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is OTHER_ERROR", DmtException.OTHER_ERROR, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        } finally {
        	tbc.closeSession(session);
        }
        
    }  
	/**
	 * @testID testIsLeafNode005
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to PERMISSION_DENIED is thrown.
	 */
	private void testIsLeafNode005() {
	    DmtSession localSession = null;
	    DmtSession remoteSession = null;
		try {
			tbc.log("#testIsLeafNode005");
			localSession = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(DmtTestControl.OSGi_LOG,new DmtAcl(new String[] { DmtTestControl.PRINCIPAL },new int[] { DmtAcl.EXEC } ));
			localSession.close();
			
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_ATOMIC);
			remoteSession.isLeafNode(DmtTestControl.OSGi_LOG);
			tbc.failException("", DmtException.class);
		}  catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is PERMISSION_DENIED",
					DmtException.PERMISSION_DENIED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(localSession, remoteSession, DmtTestControl.OSGi_LOG);
		}

	}
	
    /**
     * @testID testIsLeafNode006
     * @testDescription This method asserts that a DmtException with error code equals to 
     *                  URI_TOO_LONG is thrown using the constructor with only one argument.
     */
    private void testIsLeafNode006() {
    	DmtSession session = null;
        try {
			tbc.log("#testIsLeafNode006");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
        	session.isLeafNode(DmtTestControl.URI_LONG);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is URI_TOO_LONG", DmtException.URI_TOO_LONG, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        } finally {
        	tbc.closeSession(session);
        }
        
    }    
    
    /**
     * @testID testIsLeafNode007
     * @testDescription This method asserts that a DmtException with error code equals to 
     *                  INVALID_URI is thrown using the constructor with only one argument.
     */
    private void testIsLeafNode007() {
    	DmtSession session = null;
        try {
			tbc.log("#testIsLeafNode007");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
        	session.isLeafNode(DmtTestControl.INVALID_URI);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is INVALID_URI", DmtException.INVALID_URI, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        } finally {
        	tbc.closeSession(session);
        }
        
    } 	
    /**
     * @testID testIsLeafNode008
     * @testDescription This method asserts that an IllegalStateException is thrown when attemps to get a sessionId of a closed session.
     */
    private void testIsLeafNode008() {
        DmtSession session = null;
        try {
			tbc.log("#testIsLeafNode008");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
            session.close();
            session.isLeafNode(DmtTestControl.OSGi_ROOT);
            tbc.failException("", IllegalStateException.class);
        } catch (IllegalStateException e) {
            tbc.pass("The Exception was IllegalStateException");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalStateException.class.getName() + " but was "+e.getClass().getName());
        } finally {
        	tbc.closeSession(session);
        }
        
    }
    
    /**
     * @testID testIsLeafNode009
     * @testDescription This method asserts that SecurityException 
	 *                  is thrown when isLeafNode is called without the right permission
     */
    private void testIsLeafNode009() {
        DmtSession session = null;
    	try {
			tbc.log("#testIsLeafNode009");
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtTestControl.ALL_NODES,DmtPermission.EXEC));
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.isLeafNode(DmtTestControl.OSGi_LOG);
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
	 * @testID testIsLeafNode010
	 * @testDescription This method asserts that it is successfully executed 
	 * 					when the correct permission is assigned
	 */
	private void testIsLeafNode010() {
	    DmtSession localSession = null;
	    DmtSession remoteSession = null;
		try {
			tbc.log("#testIsLeafNode010");
			localSession = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(DmtTestControl.OSGi_LOG,new DmtAcl(new String[] { DmtTestControl.PRINCIPAL },new int[] { DmtAcl.GET } ));
			localSession.close();
			
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_ATOMIC);
			remoteSession.isLeafNode(DmtTestControl.OSGi_LOG);
			tbc.pass("getNodeTimestamp correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(localSession, remoteSession, DmtTestControl.OSGi_LOG);
		}

	}
	
    /**
     * @testID testIsLeafNode011
     * @testDescription This method asserts that it is successfully executed 
	 * 					when the correct permission is assigned
     */
    private void testIsLeafNode011() {
        DmtSession session = null;
    	try {
			tbc.log("#testIsLeafNode011");
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtTestControl.ALL_NODES,DmtPermission.GET));
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.isLeafNode(DmtTestControl.OSGi_LOG);
			tbc.pass("isLeafNode correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(session, null, null);
		}
        
    }
    
	/**
	 * @testID testIsLeafNode012
	 * @testDescription This method asserts that relative URI works as described.
	 * 
	 */
	private void testIsLeafNode012() {
		DmtSession session = null;
		try {
			tbc.log("#testIsLeafNode012");
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtTestControl.ALL_NODES,DmtTestControl.ALL_ACTIONS));
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.isLeafNode(TestExecPluginActivator.LEAF_VALUE);

			tbc.pass("A relative URI can be used with isLeafNode.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}    
}

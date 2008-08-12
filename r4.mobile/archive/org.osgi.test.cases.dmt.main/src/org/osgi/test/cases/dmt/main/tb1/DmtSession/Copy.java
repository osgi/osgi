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
 * Jan 24, 2005  Andre Assad
 * CR 1          Implement MEG TCK
 * ============  ==============================================================
 * Feb 14, 2005  Alexandre Santos
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ===========  ===============================================================
 * Feb 17, 2005  Leonardo Barros
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ===========  ===============================================================
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
 * @author Andre Assad
 * 
 * This Test Case Validates the implementation of <code>copy</code> method of DmtSession, 
 * according to MEG specification
 */
public class Copy implements TestInterface {
	private DmtTestControl tbc;

	public Copy(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
        prepare();
        testCopy001();
		testCopy002();
		testCopy003();
		testCopy004();
		testCopy005();
		testCopy006();
		testCopy007();
		testCopy008();
		testCopy009();
		testCopy010();
		testCopy011();
		testCopy012();
		testCopy013();
		testCopy014();
		testCopy015();
		testCopy016();
		testCopy017();
		testCopy018();
		testCopy019();
		testCopy020();
		testCopy021();
        testCopy022();
        testCopy023();
        testCopy024();
        testCopy025();
        testCopy026();
        testCopy027();
        testCopy028();
	}

	private void prepare() {
        tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
    }

	/**
	 * This method asserts that DmtException.COMMAND_NOT_ALLOWED is thrown when it tries to copy 
     * from an ancestor of newNodeUri
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy001() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy001");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.copy(DmtConstants.OSGi_ROOT,
					TestExecPluginActivator.INEXISTENT_NODE, true);
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
	 * This method asserts that DmtException.NODE_NOT_FOUND is thrown 
	 * if nodeUri points to a non-existing node
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy002() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy002");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.copy(TestExecPluginActivator.INEXISTENT_NODE,
					TestExecPluginActivator.ROOT + "/other", true);
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
	 * This method asserts that DmtException.NODE_ALREADY_EXISTS is thrown 
	 * if newNodeUri points to a node that already exists 
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy003() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy003");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.copy(TestExecPluginActivator.INTERIOR_NODE,
					TestExecPluginActivator.INTERIOR_NODE2, false);
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is NODE_ALREADY_EXISTS",
					DmtException.NODE_ALREADY_EXISTS, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	
	/**
	 * This method asserts that the method is called if it has the right DmtPermission (local)
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy004() {
        DmtSession session = null;
        try {
            tbc.log("#testCopy004");
            session = tbc.getDmtAdmin().getSession(".",
                DmtSession.LOCK_TYPE_EXCLUSIVE);
            
            tbc.setPermissions(new PermissionInfo[]{
                new PermissionInfo(DmtPermission.class.getName(),
                    TestExecPluginActivator.ROOT, DmtPermission.ADD),
                new PermissionInfo(DmtPermission.class.getName(),
                    TestExecPluginActivator.INTERIOR_NODE, DmtPermission.GET)});
            
            session.copy(TestExecPluginActivator.INTERIOR_NODE,
                TestExecPluginActivator.INEXISTENT_NODE, false);
            tbc.pass("A node could be copied with the right permission");
        } catch (Exception e) {
            tbc.fail("Unexpected Exception: " + e.getClass().getName()
                + " [Message: " + e.getMessage() + "]");
        } finally {
            tbc.setPermissions(new PermissionInfo(
                DmtPermission.class.getName(),
                DmtConstants.ALL_NODES, DmtConstants.ALL_ACTIONS));

            tbc.cleanUp(session, null);
        }
    }

	/**
	 * This method asserts that the method is called if it has the right Acl (remote)
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy005() {

		DmtSession session = null;
		try {
			tbc.log("#testCopy005");
            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.PRINCIPAL, Acl.GET | Acl.ADD );
            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.ROOT, DmtConstants.PRINCIPAL, Acl.ADD );
            
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class
					.getName(), DmtConstants.PRINCIPAL, "*"));

			session = tbc.getDmtAdmin().getSession(
					DmtConstants.PRINCIPAL, DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);

			session.copy(TestExecPluginActivator.INTERIOR_NODE,
					TestExecPluginActivator.INEXISTENT_NODE, false);
			tbc
					.pass("This method asserts that the method is called if it has the right Acl");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session,TestExecPluginActivator.INTERIOR_NODE);
			tbc.cleanAcl(TestExecPluginActivator.ROOT);
            
		}

	}

	/**
	 * This method asserts that relative URI works as described in this method.
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy006() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy006");
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.copy(TestExecPluginActivator.INTERIOR_NODE_NAME,
					TestExecPluginActivator.INEXISTENT_NODE_NAME, false);

			tbc.pass("A relative URI can be used with Copy.");
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
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy007() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy007");
			session = tbc.getDmtAdmin().getSession(
				TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_SHARED);

			session.copy(TestExecPluginActivator.INTERIOR_NODE_NAME,
				TestExecPluginActivator.INEXISTENT_NODE_NAME, false);
			
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
	 * This method asserts that DmtException.PERMISSION_DENIED is thrown if the session is 
	 * associated with a principal and the ACL of the copied node(s) does not allow the Get operation
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy008() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy008");
            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.PRINCIPAL, Acl.EXEC );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class
					.getName(), DmtConstants.PRINCIPAL, "*"));
			
			session = tbc.getDmtAdmin().getSession(
					DmtConstants.PRINCIPAL, DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.copy(TestExecPluginActivator.INTERIOR_NODE,
					TestExecPluginActivator.INEXISTENT_NODE, true);
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is PERMISSION_DENIED",
					DmtException.PERMISSION_DENIED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
			tbc.cleanUp(session,TestExecPluginActivator.INTERIOR_NODE);
		}

	}
	
	/**
	 * This method asserts that DmtException.PERMISSION_DENIED is thrown if the session is 
	 * associated with a principal and the ACL of the parent of the target node does not allow 
	 * the Add operation for the associated principal 

	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy009() {
		
		DmtSession session = null;
		try {
			tbc.log("#testCopy009");

			tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.ROOT, DmtConstants.PRINCIPAL, Acl.GET);
			//The copied node allows the Get operation			
            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.PRINCIPAL, Acl.GET);
            
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class
					.getName(), DmtConstants.PRINCIPAL, "*"));

			session = tbc.getDmtAdmin().getSession(
					DmtConstants.PRINCIPAL, DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.copy(TestExecPluginActivator.INTERIOR_NODE,
					TestExecPluginActivator.INEXISTENT_NODE, true);
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is PERMISSION_DENIED",
					DmtException.PERMISSION_DENIED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session,TestExecPluginActivator.INTERIOR_NODE);
            tbc.cleanAcl(TestExecPluginActivator.ROOT);
            
		}

	}
	
	/**
	 * This method asserts that an SecurityException is thrown if the caller does not 
	 * have DmtPermission for the copied node(s) with the Get action present
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy010() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy010");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.setPermissions(new PermissionInfo(
					DmtPermission.class.getName(), TestExecPluginActivator.INEXISTENT_NODE,
					DmtPermission.ADD));
			session.copy(TestExecPluginActivator.INTERIOR_NODE,
					TestExecPluginActivator.INEXISTENT_NODE, true);
			tbc.failException("#", SecurityException.class);
		} catch (SecurityException e) {
			tbc.pass("The Exception was SecurityException");
		} catch (Exception e) {
			tbc.fail("Expected " + SecurityException.class.getName()
					+ " but was " + e.getClass().getName());
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, null);
            
		}
	}
	
	/**
	 * This method asserts that an SecurityException is thrown if the caller does not 
	 * have DmtPermission for the parent of the target node with the Add action
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy011() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy011");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			//The copied node allows Get
			tbc.setPermissions(new PermissionInfo(
					DmtPermission.class.getName(), TestExecPluginActivator.INTERIOR_NODE,
					DmtPermission.GET));
			session.copy(TestExecPluginActivator.INTERIOR_NODE,
					TestExecPluginActivator.INEXISTENT_NODE, true);
			tbc.failException("#", SecurityException.class);
		} catch (SecurityException e) {
			tbc.pass("The Exception was SecurityException");
		} catch (Exception e) {
			tbc.fail("Expected " + SecurityException.class.getName()
					+ " but was " + e.getClass().getName());
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, null);
		}
	}
	/**
	 * This method asserts that DmtException.INVALID_URI is thrown when  
	 * newNodeUri is null 
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy012() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy012");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.copy(TestExecPluginActivator.INTERIOR_NODE,null, true);
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is INVALID_URI",
					DmtException.INVALID_URI, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * This method asserts that an empty string as newNodeUri means the root 
	 * URI the session was opened with  (DmtException.NODE_ALREADY_EXISTS is thrown 
	 * because the root already exist)
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy013() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy013");
			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.INTERIOR_NODE);
			session.copy(TestExecPluginActivator.INTERIOR_NODE,"", true);
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is NODE_ALREADY_EXISTS",
					DmtException.NODE_ALREADY_EXISTS, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * This method asserts that DmtException.INVALID_URI is thrown when  
	 * newNodeUri is syntactically invalid (node name ends with the '/' character)
)
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy014() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy014");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.copy(TestExecPluginActivator.INTERIOR_NODE,TestExecPluginActivator.INEXISTENT_NODE + "/", true);
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is INVALID_URI",
					DmtException.INVALID_URI, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * This method asserts that DmtException.INVALID_URI is thrown when  
	 * newNodeUri is syntactically invalid (node name ends with the '\' character)
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy015() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy015");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.copy(TestExecPluginActivator.INTERIOR_NODE,TestExecPluginActivator.INEXISTENT_NODE + "\\", true);
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is INVALID_URI",
					DmtException.INVALID_URI, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	} 
	/**
	 * This method asserts that DmtException.INVALID_URI is thrown when  
	 * newNodeUri is syntactically invalid (URI contains the segment "." at 
	 * a position other than the beginning of the URI)
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy016() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy016");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.copy(TestExecPluginActivator.INTERIOR_NODE,TestExecPluginActivator.ROOT + "/./"+ TestExecPluginActivator.INEXISTENT_NODE_NAME, true);
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is INVALID_URI",
					DmtException.INVALID_URI, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	} 
	/**
	 * This method asserts that DmtException.INVALID_URI is thrown when  
	 * newNodeUri is syntactically invalid (node name is ".." or the URI contains such a segment)
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy017() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy017");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.copy(TestExecPluginActivator.INTERIOR_NODE,TestExecPluginActivator.ROOT + "/../"+ TestExecPluginActivator.INEXISTENT_NODE_NAME, true);
			tbc.failException("", DmtException.class);
			
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is INVALID_URI",
					DmtException.INVALID_URI, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * This method asserts that DmtException.URI_TOO_LONG is thrown when  
	 * the length of nodeUri is too long 
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy018() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy018");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			if (DmtConstants.MAXIMUM_NODE_LENGTH>0) {
			    String uriTooLong = DmtTestControl.getSegmentTooLong(TestExecPluginActivator.ROOT);
			    session.copy(TestExecPluginActivator.INTERIOR_NODE,uriTooLong, true);
			    tbc.failException("", DmtException.class);
			} else {
		        tbc.log("#There is no maximum node length , " +
        		"DmtException.URI_TOO_LONG in this case will not be tested");
			}
			
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
	 * This method asserts that DmtException.URI_TOO_LONG is thrown when  
	 * the segment number exceeds the limit
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy019() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy019");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			if (DmtConstants.MAXIMUM_NODE_SEGMENTS>0) {
			    String uriTooLong = DmtTestControl.getExcedingSegmentsUri(TestExecPluginActivator.ROOT);
				session.copy(TestExecPluginActivator.INTERIOR_NODE,uriTooLong, true);
				tbc.failException("", DmtException.class);
				
			} else {
		        tbc.log("#There is no maximum node segments, " +
        		"DmtException.URI_TOO_LONG in this case will not be tested");
			}
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
	 * This method asserts that DmtException.TRANSACTION_ERROR is thrown 
	 * if the session is atomic and the plugin is read-only
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy020() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy020");
			session = tbc.getDmtAdmin().getSession(".",
			    DmtSession.LOCK_TYPE_ATOMIC);
			
			session.copy(TestReadOnlyPluginActivator.INTERIOR_NODE,
			    TestReadOnlyPluginActivator.INEXISTENT_NODE, true);

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
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy021() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy021");
			session = tbc.getDmtAdmin().getSession(".",
			    DmtSession.LOCK_TYPE_ATOMIC);
			
			session.copy(TestNonAtomicPluginActivator.INTERIOR_NODE,
			    TestNonAtomicPluginActivator.INEXISTENT_NODE, true);

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
	 * This method asserts that no exception is thrown if the caller has DmtPermission for the target node 
	 * with the Replace action and the node has a title 
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy022() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy022");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
            tbc.setPermissions(new PermissionInfo[]{
                    new PermissionInfo(DmtPermission.class.getName(),
                        TestExecPluginActivator.ROOT, DmtPermission.ADD),
                    new PermissionInfo(DmtPermission.class.getName(),
                        TestExecPluginActivator.INTERIOR_NODE, DmtPermission.GET),
                    new PermissionInfo(DmtPermission.class.getName(),
                                TestExecPluginActivator.INEXISTENT_NODE, DmtPermission.REPLACE)});

            //When doing this, getNodeTitle returns a value intead of null
            TestExecPlugin.setDefaultNodeTitle(DmtConstants.TITLE);
            
			session.copy(TestExecPluginActivator.INTERIOR_NODE,
					TestExecPluginActivator.INEXISTENT_NODE, true);
			tbc.pass("Copy method could called when the caller has DmtPermission for the " +
					"target node with the Replace action and the node has a title ");
		} catch (Exception e) {
            tbc.fail("Unexpected Exception: " + e.getClass().getName()
                    + " [Message: " + e.getMessage() + "]");

		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, null);
            TestExecPlugin.setDefaultNodeTitle(null);
		}
	}
	
	/**
	 * This method asserts that an SecurityException is thrown if the caller does not 
	 * have DmtPermission for the target node with the Replace action and the
	 * node has a title (in this case is necessary Replace)
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy023() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy023");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
            tbc.setPermissions(new PermissionInfo[]{
                    new PermissionInfo(DmtPermission.class.getName(),
                        TestExecPluginActivator.ROOT, DmtPermission.ADD),
                    new PermissionInfo(DmtPermission.class.getName(),
                        TestExecPluginActivator.INTERIOR_NODE, DmtPermission.GET)});

            //When doing this, getNodeTitle returns a value intead of null
            TestExecPlugin.setDefaultNodeTitle(DmtConstants.TITLE);
            
			session.copy(TestExecPluginActivator.INTERIOR_NODE,
					TestExecPluginActivator.INEXISTENT_NODE, true);
			tbc.failException("#", SecurityException.class);
		} catch (SecurityException e) {
			tbc.pass("The Exception was SecurityException");
		} catch (Exception e) {
			tbc.fail("Expected " + SecurityException.class.getName()
					+ " but was " + e.getClass().getName());
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, null);
            TestExecPlugin.setDefaultNodeTitle(null);
		}
	}
	
	/**
	 * This method asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * if any of the implied retrieval or update operations are not allowed.
	 * In this test a leaf node is copied (DmtAdmin calls DmtSession.createLeafNode(String)
	 * that throws this DmtException because it is opened in a non-atomic session and 
	 * the underlying plugin is read-only)
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy024() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy024");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.copy(TestReadOnlyPluginActivator.LEAF_NODE,
					TestReadOnlyPluginActivator.INEXISTENT_LEAF_NODE, true);
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
	 * This method asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * if any of the implied retrieval or update operations are not allowed.
	 * In this test a leaf node is copied (DmtAdmin calls DmtSession.createLeafNode(String)
	 * that throws this DmtException because it is opened in a non-atomic session and 
	 * the underlying plugin does not support non-atomic writing)
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy025() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy025");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.copy(TestNonAtomicPluginActivator.LEAF_NODE,
					TestNonAtomicPluginActivator.INEXISTENT_LEAF_NODE, true);
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
	 * This method asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * if any of the implied retrieval or update operations are not allowed.
	 * In this test an interior node is copied (DmtAdmin calls DmtSession.createInteriorNode(String)
	 * that throws this DmtException because it is opened in a non-atomic session and 
	 * the underlying plugin is read-only)
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy026() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy026");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.copy(TestReadOnlyPluginActivator.INTERIOR_NODE,
					TestReadOnlyPluginActivator.INEXISTENT_NODE, true);
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
	 * This method asserts that DmtException.COMMAND_NOT_ALLOWED is thrown 
	 * if any of the implied retrieval or update operations are not allowed.
	 * In this test an interior node is copied (DmtAdmin calls DmtSession.createInteriorNode(String)
	 * that throws this DmtException because it is opened in a non-atomic session and 
	 * the underlying plugin does not support non-atomic writing)
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy027() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy027");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.copy(TestNonAtomicPluginActivator.INTERIOR_NODE,
					TestNonAtomicPluginActivator.INEXISTENT_NODE, true);
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
	 * URI the session was opened with (it throws DmtException.COMMAND_FAILED 
	 * because they arent in the same session's subtree)
	 * 
	 * @spec DmtSession.copy(String,String,boolean)
	 */
	private void testCopy028() {
		DmtSession session = null;
		try {
			tbc.log("#testCopy028");
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.INTERIOR_NODE, DmtSession.LOCK_TYPE_ATOMIC);

			session.copy("",
					TestExecPluginActivator.INEXISTENT_NODE, false);
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is COMMAND_FAILED",
					DmtException.COMMAND_FAILED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}
}

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
 * CR 1          Implement MEG TCK
 * ============  ==============================================================
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
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ReadOnly.TestReadOnlyPluginActivator;

/**
 * @author Andre Assad
 * 
 * This test case validates the implementation of <code>execute<code> method of DmtSession, 
 * according to MEG specification.
 * 
 */
public class Execute implements TestInterface {
	private DmtTestControl tbc;
	private final String DATA = "data";
	private final String CORRELATOR = "correlator";
	
	public Execute(DmtTestControl tbc) {
		this.tbc = tbc;

	}

	public void run() {
        prepare();
		testExecute001();
		testExecute002();
		testExecute003();
		testExecute004();
		testExecute005();
		testExecute006();
		testExecute007();
		testExecute008();
		testExecute009();
		testExecute010();
		testExecute011();
		testExecute012();
		testExecute013();
		testExecute014();
		testExecute015();
		testExecute016();
		
	}
    private void prepare() {
        tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
    }
	/**
	 * This method asserts that DmtException.NODE_NOT_FOUND is thrown
	 * if nodeUri points to a non-existing node
	 *  
	 * @spec DmtSession.execute(String,String)
	 */
	private void testExecute001() {
		DmtSession session = null;
		try {
			tbc.log("#testExecute001");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.execute(TestExecPluginActivator.INEXISTENT_NODE, DATA);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException's code is NODE_NOT_FOUND",
					DmtException.NODE_NOT_FOUND, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}

	
	/**
	 * This method asserts that DmtException.COMMAND_FAILED is thrown 
	 * if no DmtExecPlugin is associated with the node
	 * 
	 * @spec DmtSession.execute(String,String)
	 */
	private void testExecute002() {
		DmtSession session = null;
		try {
			tbc.log("#testExecute002");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.execute(TestReadOnlyPluginActivator.INTERIOR_NODE, DATA);
			
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException's code is COMMAND_FAILED",
					DmtException.COMMAND_FAILED, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}

	
	/**
	 * This method asserts that execute is executed when the right Acl is set (Remote)
	 * 
	 * @spec DmtSession.execute(String,String)
	 */
	private void testExecute003() {
		DmtSession session = null;
		try {
			tbc.log("#testExecute003");
            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.PRINCIPAL, Acl.EXEC | Acl.GET );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,
					TestExecPluginActivator.INTERIOR_NODE, DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.execute(TestExecPluginActivator.INTERIOR_NODE, DATA);
			
			tbc.pass("execute was successfully executed");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, TestExecPluginActivator.INTERIOR_NODE);
            
            
		}

	}
	/**
	 * This method asserts that execute is executed when the right DmtPermission is set (Local)
	 * 
	 * @spec DmtSession.execute(String,String)
	 */
	private void testExecute004() {
		DmtSession session = null;	
		try {
			tbc.log("#testExecute004");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtConstants.ALL_NODES, DmtPermission.EXEC));
			
			session.execute(TestExecPluginActivator.INTERIOR_NODE, DATA);
			
			tbc.pass("execute was successfully executed");
		} catch (SecurityException e) {
			tbc.pass("The Exception was SecurityException");
		} catch (Exception e) {
			tbc.failExpectedOtherException(SecurityException.class, e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, null);
			
		}

	}
	

	/**
	 * This method asserts that relative URI works as described.
	 * 
	 * @spec DmtSession.execute(String,String)
	 */
	private void testExecute005() {
		DmtSession session = null;
		try {
			tbc.log("#testExecute005");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.execute(TestExecPluginActivator.INTERIOR_NODE_NAME, DATA);

			tbc.pass("A relative URI can be used with execute.");
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
	 * @spec DmtSession.execute(String,String,String)
	 */
	private void testExecute006() {
		DmtSession session = null;
		try {
			tbc.log("#testExecute006");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.execute(TestExecPluginActivator.INEXISTENT_NODE, CORRELATOR,null);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException's code is NODE_NOT_FOUND",
					DmtException.NODE_NOT_FOUND, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}

	

	
	/**
	 * This method asserts that DmtException.COMMAND_FAILED is thrown 
	 * if no DmtExecPlugin is associated with the node
	 * 
	 * @spec DmtSession.execute(String,String,String)
	 */
	private void testExecute007() {
		DmtSession session = null;
		try {
			tbc.log("#testExecute007");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.execute(TestReadOnlyPluginActivator.INTERIOR_NODE,CORRELATOR, null);
			
			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException's code is COMMAND_FAILED",
					DmtException.COMMAND_FAILED, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}

	
	/**
	 * This method asserts that execute is executed when the right Acl is set (Remote)
	 * 
	 * @spec DmtSession.execute(String,String,String)
	 */
	private void testExecute008() {
		DmtSession session = null;
		try {
			tbc.log("#testExecute008");


            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.PRINCIPAL, Acl.EXEC | Acl.GET );

			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,
					TestExecPluginActivator.INTERIOR_NODE, DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.execute(TestExecPluginActivator.INTERIOR_NODE,CORRELATOR, null);
			
			tbc.pass("execute was successfully executed");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session,TestExecPluginActivator.INTERIOR_NODE);
            
		}

	}
	/**
	 * This method asserts that execute is executed when the right DmtPermission is set (Local)
	 * 
	 * @spec DmtSession.execute(String,String,String)
	 */
	private void testExecute009() {
		DmtSession session = null;	
		try {
			tbc.log("#testExecute009");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtConstants.ALL_NODES, DmtPermission.EXEC));
			
			session.execute(TestExecPluginActivator.INTERIOR_NODE,CORRELATOR, null);
			
			tbc.pass("execute was successfully executed");
		} catch (SecurityException e) {
			tbc.pass("The Exception was SecurityException");
		} catch (Exception e) {
			tbc.failExpectedOtherException(SecurityException.class, e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.closeSession(session);
			
		}

	}
	

	/**
	 * This method asserts that relative URI works as described.
	 * 
	 * @spec DmtSession.execute(String,String,String)
	 */
	private void testExecute010() {
		DmtSession session = null;
		try {
			tbc.log("#testExecute010");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.execute(TestExecPluginActivator.INTERIOR_NODE_NAME,CORRELATOR, null);

			tbc.pass("A relative URI can be used with execute.");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}	
	
	/**
	 * This method asserts that null can be passed on data parameter
	 * 
	 * @spec DmtSession.execute(String,String,String)
	 */
	private void testExecute011() {
		DmtSession session = null;
		try {
			tbc.log("#testExecute011");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.execute(TestExecPluginActivator.INTERIOR_NODE_NAME,null);

			tbc.pass("Null can be passed on data parameter");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * This method asserts that null can be passed on correlator parameter
	 * 
	 * @spec DmtSession.execute(String,String,String)
	 */
	private void testExecute012() {
		DmtSession session = null;
		try {
			tbc.log("#testExecute012");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.execute(TestExecPluginActivator.INTERIOR_NODE_NAME,null, DATA);

			tbc.pass("Null can be passed on correlator parameter");
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
	 * @spec DmtSession.execute(String,String)
	 */
	private void testExecute013() {
		DmtSession session = null;
		try {
			tbc.log("#testExecute013");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.INTERIOR_NODE, DmtSession.LOCK_TYPE_ATOMIC);

			session.execute("", DATA);

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
	 * @spec DmtSession.execute(String,String)
	 */
	private void testExecute014() {
		DmtSession session = null;
		try {
			tbc.log("#testExecute014");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.INTERIOR_NODE, DmtSession.LOCK_TYPE_ATOMIC);

			session.execute("", CORRELATOR,null);

			tbc.pass("Asserts that an empty string as relative URI means the root " +
					"URI the session was opened with");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * This method asserts that DmtIllegalStateException is thrown
	 * if the session was opened using the LOCK_TYPE_SHARED lock type
	 *  
	 * @spec DmtSession.execute(String,String)
	 */
	private void testExecute015() {
		DmtSession session = null;
		try {
			tbc.log("#testExecute015");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);

			session.execute(TestExecPluginActivator.INTERIOR_NODE, DATA);

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
	 * This method asserts that DmtIllegalStateException is thrown
	 * if the session was opened using the LOCK_TYPE_SHARED lock type
	 *  
	 * @spec DmtSession.execute(String,String,String)
	 */
	private void testExecute016() {
		DmtSession session = null;
		try {
			tbc.log("#testExecute016");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);

			session.execute(TestExecPluginActivator.INTERIOR_NODE_NAME,CORRELATOR, DATA);

			tbc.failException("", DmtIllegalStateException.class);
		} catch (DmtIllegalStateException e) {
			tbc.pass("DmtIllegalStateException correctly thrown");
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtIllegalStateException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
}

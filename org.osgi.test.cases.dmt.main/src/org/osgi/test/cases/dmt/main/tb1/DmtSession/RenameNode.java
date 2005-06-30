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
 * Jan 25, 2005  Andre Assad
 * 1             Implement MEG TCK
 * ============  ==============================================================
 * Feb 15, 2005  Luiz Felipe Guimaraes
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-002)
 * ============  ==============================================================
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
 * @methodUnderTest org.osgi.service.dmt.DmtSession#renameNode
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>renameNode<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class RenameNode implements TestInterface {
	private DmtTestControl tbc;
	
	public RenameNode(DmtTestControl tbc) {
		this.tbc = tbc;
	}
	public void run() {
		testRenameNode001();
		testRenameNode002();
		testRenameNode003();
		testRenameNode004();
		testRenameNode005();
		testRenameNode006();
		testRenameNode007();
		testRenameNode008();
		testRenameNode009();
		testRenameNode010();
		testRenameNode011();
		
	}

	/**
	 * @testID testRenameNode001
	 * @testDescription This method tests if a DmtException.NODE_NOT_FOUND  
	 *                  is thrown correctly
	 */
	private void testRenameNode001() {
		DmtSession session = null;
		try {
			tbc.log("#testRenameNode001");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.renameNode(TestExecPluginActivator.INEXISTENT_NODE, "newName");
			tbc.failException("#",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserting that DmtException code is NODE_NOT_FOUND",DmtException.NODE_NOT_FOUND,e.getCode());
		} catch (Exception e) { 
			tbc.fail("Expected " + DmtException.class.getName() + " but was " + e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}	
	/**
	 * @testID testRenameNode002
	 * @testDescription This method tests if a DmtException.COMMAND_FAILED  
	 *                  is thrown correctly
	 */
	private void testRenameNode002() {
		DmtSession session = null;
		try {
			tbc.log("#testRenameNode002");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.renameNode(TestExecPluginActivator.INTERIOR_NODE, DmtTestControl.INVALID);
			tbc.failException("#",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserting that DmtException code is COMMAND_FAILED",DmtException.COMMAND_FAILED,e.getCode());
		} catch (Exception e) { 
			tbc.fail("Expected " + DmtException.class.getName() + " but was " + e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}	
	
	/**
	 * @testID testRenameNode003
	 * @testDescription This method tests if a DmtException.OTHER_ERROR 
	 *                  is thrown correctly
	 */
	private void testRenameNode003() {
		DmtSession session = null;

		try {
			tbc.log("#testRenameNode003");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG,DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.renameNode(TestExecPluginActivator.INTERIOR_NODE, "newName" );
			tbc.failException("#",DmtException.class);	
		} catch (DmtException e) {
			tbc.assertEquals("Asserting that DmtException code is OTHER_ERROR",DmtException.OTHER_ERROR,e.getCode());
		} catch (Exception e) { 
			tbc.fail("Expected " + DmtException.class.getName() + " but was " + e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}	
	
	/**
	 * @testID testRenameNode004
	 * @testDescription This method tests if a DmtException.URI_TOO_LONG  
	 *                  is thrown correctly
	 */
	private void testRenameNode004()  {
		DmtSession session = null;
		try {
			tbc.log("#testRenameNode004");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			session.renameNode(DmtTestControl.URI_LONG, DmtTestControl.LONG_NAME );
			tbc.failException("#",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserting that DmtException code is URI_TOO_LONG",DmtException.URI_TOO_LONG,e.getCode());
		} catch (Exception e) { 
			tbc.fail("Expected " + DmtException.class.getName() + " but was " + e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}

	}
	
	/**
	 * @testID testRenameNode005
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to PERMISSION_DENIED is thrown.
	 */
	private void testRenameNode005() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testRenameNode005");
			localSession = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE,new DmtAcl(new String[] { DmtTestControl.PRINCIPAL },new int[] { DmtAcl.EXEC } ));
			localSession.close();
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,TestExecPluginActivator.INTERIOR_NODE,DmtSession.LOCK_TYPE_ATOMIC);
			remoteSession.renameNode(TestExecPluginActivator.INTERIOR_NODE, "newName" );
			tbc.failException("", DmtException.class);
		}  catch (DmtException e) {
			tbc.assertEquals(
					"Asserting that DmtException code is PERMISSION_DENIED",
					DmtException.PERMISSION_DENIED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(localSession, remoteSession, TestExecPluginActivator.INTERIOR_NODE);
		}

	}   
	/**
	 * @testID testRenameNode006
	 * @testDescription This method asserts that an SecurityException 
	 *                  is thrown when renameNode is called without the right permission
	 */
	private void testRenameNode006() {
		DmtSession session = null;
		try {
			tbc.log("#testRenameNode006");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtTestControl.ALL_NODES,DmtPermission.EXEC));
			session.renameNode(TestExecPluginActivator.INTERIOR_NODE, "newName" );
			tbc.failException("#",SecurityException.class);
		} catch (SecurityException e) {
			tbc.pass("The Exception was SecurityException");
		} catch (Exception e) {
			tbc.fail("Expected " + SecurityException.class.getName() + " but was " + e.getClass().getName());
		} finally {
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtTestControl.ALL_NODES,DmtTestControl.ALL_ACTIONS));
		}
	} 	
	
	/**
	 * @testID testRenameNode007
	 * @testDescription This method tests if a DmtException.INVALID_URI
	 *                  is thrown correctly
	 */
	private void testRenameNode007()  {
		DmtSession session = null;
		try {
			tbc.log("#testRenameNode007");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.renameNode(DmtTestControl.INVALID_URI, "newName");
			tbc.failException("#",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserting that DmtException code is INVALID_URI",DmtException.INVALID_URI,e.getCode());
		} catch (Exception e) { 
			tbc.fail("Expected " + DmtException.class.getName() + " but was " + e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}

	}	
	/**
	 * @testID testRenameNode008
	 * @testDescription This method tests if a IllegalStateException   
	 *                  is thrown correctly when a session is already closed
	 */
	private void testRenameNode008() {
		DmtSession session = null;
		try {
			tbc.log("#testRenameNode008");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.close();
			session.renameNode(TestExecPluginActivator.INTERIOR_NODE, "newName" );			
			tbc.failException("#",IllegalStateException.class);
        } catch (IllegalStateException e) {
            tbc.pass("The exception was IllegalStateException");
        } catch (Exception e) {
        	tbc.fail("Expected " + IllegalStateException.class.getName() + " but was " + e.getClass().getName());
        } finally {
        	tbc.closeSession(session);
        }
	}
	/**
	 * @testID testRenameNode009
	 * @testDescription This method asserts that no exception is thrown we
	 *                  we set the correct Acl permission for the principal.
	 */
	private void testRenameNode009() {
		DmtSession localSession = null;
		DmtSession remoteSession = null;
		try {
			tbc.log("#testRenameNode009");
			localSession = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			localSession.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE,new DmtAcl(new String[] { DmtTestControl.PRINCIPAL },new int[] { DmtAcl.REPLACE } ));
			localSession.close();
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtTestControl.PRINCIPAL,"*"));
			remoteSession = tbc.getDmtAdmin().getSession(DmtTestControl.PRINCIPAL,TestExecPluginActivator.INTERIOR_NODE,DmtSession.LOCK_TYPE_ATOMIC);
			remoteSession.renameNode(TestExecPluginActivator.INTERIOR_NODE, "newName" );
			tbc.pass("renameNode correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.cleanUp(localSession, remoteSession, TestExecPluginActivator.INTERIOR_NODE);
		}

	}   
	/**
	 * @testID testRenameNode010
	 * @testDescription This method asserts that no exception is thrown 
	 *                  when we set the correct permisisons for the caller.
	 */
	private void testRenameNode010() {
		DmtSession session = null;
		try {
			tbc.log("#testRenameNode010");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtTestControl.ALL_NODES,DmtPermission.REPLACE));
			session.renameNode(TestExecPluginActivator.INTERIOR_NODE, "newName" );
			tbc.pass("renameNode correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(),DmtTestControl.ALL_NODES,DmtTestControl.ALL_ACTIONS));
		}
	} 	
	
	/**
	 * @testID testRenameNode011
	 * @testDescription This method asserts that relative URI works as described
	 *                  in this method.
	 */
	private void testRenameNode011() {
		DmtSession session = null;
		try {
			tbc.log("#testRenameNode011");
			tbc.setPermissions(new PermissionInfo(
					DmtPermission.class.getName(), DmtTestControl.ALL_NODES,
					DmtTestControl.ALL_ACTIONS));
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.renameNode(TestExecPluginActivator.INTERIOR_VALUE,
					TestExecPluginActivator.INEXISTENT_NODE_VALUE);

			tbc.pass("A relative URI can be used with renameNode.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}	
}

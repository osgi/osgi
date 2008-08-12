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
 * ============  ==============================================================
 * 26/01/2005    Andre Assad
 * 1             Implement TCK
 * ============  ==============================================================
 * 15/02/2005    Leonardo Barros
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-002)
 * ============  ==============================================================
 * Abr 04, 2005  Luiz Felipe Guimaraes
 * 34            [MEGTCK][DMT] CVS update 
 */

package org.osgi.test.cases.dmt.main.tb1.DmtSession;

import org.osgi.service.dmt.Acl;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.security.DmtPermission;
import org.osgi.service.dmt.security.DmtPrincipalPermission;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.main.tbc.DmtConstants;
import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.main.tbc.TestInterface;
import org.osgi.test.cases.dmt.main.tbc.Plugin.ExecPlugin.TestExecPluginActivator;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * This test case validates the implementation of <code>getChildNodeNames</code> method of DmtSession, 
 * according to MEG specification
 */
public class GetChildNodeNames implements TestInterface {
	private DmtTestControl tbc;

	public GetChildNodeNames(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
        prepare();
		testGetChildNodeNames001();
		testGetChildNodeNames002();
		testGetChildNodeNames003();
		testGetChildNodeNames004();
		testGetChildNodeNames005();
		testGetChildNodeNames006();
		testGetChildNodeNames007();
		testGetChildNodeNames008();
	}

    private void prepare() {
        tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
    }
	/**
	 * This method asserts that DmtException.NODE_NOT_FOUND is thrown
	 * if nodeUri points to a non-existing node 
	 * 
	 * @spec DmtSession.getChildNodeNames(String)
	 */
	private void testGetChildNodeNames001() {
		DmtSession session = null;
		try {
			tbc.log("#testGetChildNodeNames001");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);

			session.getChildNodeNames(TestExecPluginActivator.INEXISTENT_NODE);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts DmtException.getCode",
					DmtException.NODE_NOT_FOUND, e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts that getChildNodeNames is executed when the right Acl is set (Remote)
	 * 
	 * @spec DmtSession.getChildNodeNames(String)
	 */
	private void testGetChildNodeNames002() {
		DmtSession session = null;
		try {
			tbc.log("#testGetChildNodeNames002");

            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.PRINCIPAL, Acl.GET );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,
					TestExecPluginActivator.INTERIOR_NODE, DmtSession.LOCK_TYPE_SHARED);

			session.getChildNodeNames(TestExecPluginActivator.INTERIOR_NODE);
			tbc.pass("getChildNodeNames correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session,TestExecPluginActivator.INTERIOR_NODE);
            
		}

	}

	/**
	 * This method asserts that getChildNodeNames is executed when the right DmtPermission is set (Local)
	 * 
	 * @spec DmtSession.getChildNodeNames(String)
	 */
	private void testGetChildNodeNames003() {
		DmtSession session = null;
		try {
			tbc.log("#testGetChildNodeNames003");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtConstants.ALL_NODES, DmtPermission.GET));
			
			session.getChildNodeNames(TestExecPluginActivator.INTERIOR_NODE);
			
			tbc.pass("getChildNodeNames correctly executed");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
			
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, null);
            
		}
	}
	
	/**
	 * This method asserts that an empty array is returned by DmtAdmin when the plugin returns null.
	 * 
	 * @spec DmtSession.getChildNodeNames(String)
	 */
	private void testGetChildNodeNames004() {
		DmtSession session = null;
		try {
			tbc.log("#testGetChildNodeNames004");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtConstants.ALL_NODES, DmtPermission.GET));
			
			String[] childs = session.getChildNodeNames(TestExecPluginActivator.ROOT);
			
			tbc.assertTrue("Asserting if an empty array was returned by DmtAdmin.", childs == null ? false : (childs.length == 0)); 
			
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
			
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, null);
            
		}
	}	
	
	/**
	 * This method asserts that DmtAdmin remove the null entries when our plugin return an array of string that contains null entries.
	 * 
	 * @spec DmtSession.getChildNodeNames(String)
	 * 
	 */
	private void testGetChildNodeNames005() {
		DmtSession session = null;
		try {
			tbc.log("#testGetChildNodeNames005");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtConstants.ALL_NODES, DmtPermission.GET));
			
			String[] childs = session.getChildNodeNames(TestExecPluginActivator.INTERIOR_NODE_WITH_NULL_VALUES);
			
			tbc.assertTrue("Asserting if an array with two elements was returned.", (childs.length == 2));
			
			
			boolean hasNull = false;
			for (int i=0; i<childs.length; i++) {
				if (childs[i] == null) {
					hasNull = true;
					break;
				}
			}
			
			tbc.assertTrue("Asserting if no null entries exists as elements in the returned array.", !hasNull);
			
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
	 * @spec DmtSession.getChildNodeNames(String)
	 */
	private void testGetChildNodeNames006() {
		DmtSession session = null;
		try {
			tbc.log("#testGetChildNodeNames006");
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.getChildNodeNames(TestExecPluginActivator.INTERIOR_NODE_NAME);

			tbc.pass("A relative URI can be used with getChildNodeNames.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}	
	

	/**
	 * This method asserts that DmtException.COMMAND_NOT_ALLOWED is thrown when is tried to get 
	 * a node that is not an interior node
	 *  
	 * @spec DmtSession.getChildNodeNames(String)
	 */
	private void testGetChildNodeNames007() {
		DmtSession session = null;
		try {
			tbc.log("#testGetChildNodeNames007");

			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_SHARED);
			session.getChildNodeNames(TestExecPluginActivator.LEAF_NODE);

			tbc.failException("", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts DmtException.getCode",
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
	 * @spec DmtSession.getChildNodeNames(String)
	 */
	private void testGetChildNodeNames008() {
		DmtSession session = null;
		try {
			tbc.log("#testGetChildNodeNames008");
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.INTERIOR_NODE, DmtSession.LOCK_TYPE_ATOMIC);

			session.getChildNodeNames("");

			tbc.pass("Asserts that an empty string as relative URI means the root" +
					" URI the session was opened with");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}	
}

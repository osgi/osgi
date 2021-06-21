/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

/*
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline	
 * ============  ==============================================================
 * Jan 25, 2005  Andre Assad
 * CR 1          Implement MEG TCK
 * ============  ==============================================================
 * Feb 14, 2005  Alexandre Santos
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ===========  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tb1.DmtSession;

import org.osgi.service.dmt.Acl;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtIllegalStateException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.security.DmtPermission;
import org.osgi.service.dmt.security.DmtPrincipalPermission;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.TestInterface;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.NonAtomic.TestNonAtomicPluginActivator;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ReadOnly.TestReadOnlyPluginActivator;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * This Test Case Validates the implementation of <code>deleteNode<code> 
 * method of DmtSession, according to MEG specification
 */
public class DeleteNode implements TestInterface {
	private DmtTestControl tbc;

	public DeleteNode(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	@Override
	public void run() {	
        prepare();
		testDeleteNode001();
		testDeleteNode002();
		testDeleteNode003();
		testDeleteNode004();
		testDeleteNode005();
		testDeleteNode006();
		testDeleteNode007();
		testDeleteNode008();
		testDeleteNode009();
		testDeleteNode010();
		testDeleteNode011();
	}
	
    private void prepare() {
        tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
    }
	/**
	 * This method asserts that DmtException.NODE_NOT_FOUND is thrown
	 * if nodeUri points to a non-existing node 
	 * 
	 * @spec DmtSession.deleteNode(String)
	 */
	private void testDeleteNode001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testDeleteNode001");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.deleteNode(TestExecPluginActivator.INEXISTENT_NODE);

			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting that DmtException's code is NODE_NOT_FOUND",
					DmtException.NODE_NOT_FOUND, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}



	/**
	 * This method asserts if DmtIllegalStateException is thrown if this method is called 
	 * when the session is LOCK_TYPE_SHARED
	 * 
	 * @spec DmtSession.deleteNode(String)
	 */
	private void testDeleteNode002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testDeleteNode002");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_SHARED);
			session.deleteNode(TestExecPluginActivator.INTERIOR_NODE);
			DefaultTestBundleControl.failException("", DmtIllegalStateException.class);
		} catch (DmtIllegalStateException e) {
			DefaultTestBundleControl.pass("DmtIllegalStateException correctly thrown");
		} catch (Exception e) {
			DefaultTestBundleControl.failException("", DmtIllegalStateException.class);
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts that deleteNode is executed when the right Acl is set (Remote)
	 * 
	 * @spec DmtSession.deleteNode(String)
	 */
	private void testDeleteNode003() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testDeleteNode003");
            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.PRINCIPAL, Acl.DELETE );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL, TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.deleteNode(TestExecPluginActivator.INTERIOR_NODE);
			
			DefaultTestBundleControl.pass("deleteNode was successfully executed");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session, TestExecPluginActivator.INTERIOR_NODE);
            
		}

	}

	/**
	 * This method asserts that deleteNode is executed when the right DmtPermission is set (Local)
	 * 
	 * @spec DmtSession.deleteNode(String)
	 */
	private void testDeleteNode004() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testDeleteNode004");
            tbc.setPermissions(new PermissionInfo[] {
                    new PermissionInfo(DmtPermission.class.getName(), TestExecPluginActivator.ROOT, DmtPermission.GET),
                    new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtPermission.DELETE)});
			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.deleteNode(TestExecPluginActivator.INTERIOR_NODE);
			
			DefaultTestBundleControl.pass("deleteNode was successfully executed");
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
	 * @spec DmtSession.deleteNode(String)
	 * 
	 */
	private void testDeleteNode005() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testDeleteNode005");
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.deleteNode(TestExecPluginActivator.INTERIOR_NODE_NAME);

			DefaultTestBundleControl.pass("A relative URI can be used with deleteNode.");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	
	/**
	 * Asserts that DmtException with COMMAND_NOT_ALLOWED code is thrown when 
	 * the target node is the root of the tree 

	 * 
	 * @spec DmtSession.deleteNode(String)
	 */
	private void testDeleteNode006() {
		DmtSession session = null;
		DefaultTestBundleControl.log("#testDeleteNode006");
		try {
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.deleteNode(".");
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting that DmtException code is COMMAND_NOT_ALLOWED",
					DmtException.COMMAND_NOT_ALLOWED, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * Asserts that DmtException with COMMAND_NOT_ALLOWED code is thrown 
	 * if the session is non-atomic (LOCK_TYPE_EXCLUSIVE) 
	 * and the plugin does not support non-atomic writing
	 * 
	 * @spec DmtSession.deleteNode(String)
	 */
	private void testDeleteNode007() {
		DmtSession session = null;
		DefaultTestBundleControl.log("#testDeleteNode007");
		try {
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.deleteNode(TestNonAtomicPluginActivator.INTERIOR_NODE);
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserting that DmtException code is COMMAND_NOT_ALLOWED",
					DmtException.COMMAND_NOT_ALLOWED, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
	/**
	 * Asserts that DmtException with COMMAND_NOT_ALLOWED code is thrown 
	 * if the session is non-atomic (LOCK_TYPE_EXCLUSIVE) 
	 * and the plugin is read-only
	 * 
	 * @spec DmtSession.deleteNode(String)
	 */
	private void testDeleteNode008() {
		DmtSession session = null;
		DefaultTestBundleControl.log("#testDeleteNode008");
		try {
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.deleteNode(TestReadOnlyPluginActivator.INTERIOR_NODE);
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
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
	 * @spec DmtSession.deleteNode(String)
	 */
	private void testDeleteNode009() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testDeleteNode009");
			session = tbc.getDmtAdmin().getSession(".",
			    DmtSession.LOCK_TYPE_ATOMIC);
			
			session.deleteNode(TestReadOnlyPluginActivator.INTERIOR_NODE);
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserting that DmtException code is TRANSACTION_ERROR",
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
	 * @spec DmtSession.deleteNode(String)
	 */
	private void testDeleteNode010() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testDeleteNode010");
			session = tbc.getDmtAdmin().getSession(".",
			    DmtSession.LOCK_TYPE_ATOMIC);
			
			session.deleteNode(TestNonAtomicPluginActivator.INTERIOR_NODE);
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserting that DmtException code is TRANSACTION_ERROR",
					DmtException.TRANSACTION_ERROR, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * This method asserts that DmtException.COMMAND_NOT_ALLOWED is thrown
	 * if the target node is the root of the session
	 * 
	 * @spec DmtSession.deleteNode(String)
	 */
	private void testDeleteNode011() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testDeleteNode011");
			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.INTERIOR_NODE,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.deleteNode(TestExecPluginActivator.INTERIOR_NODE);
			
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserting that DmtException code is COMMAND_NOT_ALLOWED",
					DmtException.COMMAND_NOT_ALLOWED, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
		} finally {
			tbc.closeSession(session);
            
		}

	}
	
}

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
 * Feb 28, 2005  Luiz Felipe Guimaraes
 * 1		     Updates 
 * ============  =================================================================
 */
package org.osgi.test.cases.dmt.tc2.tb1.DmtSession;

import org.osgi.service.dmt.Acl;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.security.DmtPermission;
import org.osgi.service.dmt.security.DmtPrincipalPermission;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.TestInterface;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * This Test Case Validates the implementation of <code>getEffectiveNodeAcl</code> method of DmtSession, 
 * according to MEG specification
 */

public class GetEffectiveNodeAcl implements TestInterface {
	private DmtTestControl tbc;

	public GetEffectiveNodeAcl(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	@Override
	public void run() {
        prepare();
		testGetEffectiveNodeAcl001();
		testGetEffectiveNodeAcl002();
		testGetEffectiveNodeAcl003();
		testGetEffectiveNodeAcl004();
		testGetEffectiveNodeAcl005();
		testGetEffectiveNodeAcl006();
		testGetEffectiveNodeAcl007();
	}

    private void prepare() {
        tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
    }
	/**
	 *	Tests if when there is no ACL defined for the node, it
	 *  will be derived from the closest ancestor having an ACL
	 *  defined.
	 * 
	 * @spec DmtSession.getEffectiveNodeAcl(String)
	 */	
	private void testGetEffectiveNodeAcl001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetEffectiveNodeAcl001");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			Acl acl = new Acl(DmtConstants.ACLSTR);

			session.setNodeAcl(TestExecPluginActivator.ROOT, acl);
			TestCase.assertEquals("Asserting the effective node ", acl.toString(),
					session.getEffectiveNodeAcl(TestExecPluginActivator.CHILD_INTERIOR_NODE)
							.toString());
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session, TestExecPluginActivator.ROOT);			
		}

	}

	
	/**
	 *	Tests if when there is an ACL defined for the node, it
	 *  will be derived from the ACL defined
	 * 
	 * @spec DmtSession.getEffectiveNodeAcl(String)
	 */		
	private void testGetEffectiveNodeAcl002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetEffectiveNodeAcl002");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			Acl acl = new Acl(DmtConstants.ACLSTR);
			session.setNodeAcl(TestExecPluginActivator.INTERIOR_NODE, acl);
			TestCase.assertEquals("Asserting the effective node ", acl.toString(),
					session.getEffectiveNodeAcl(TestExecPluginActivator.INTERIOR_NODE)
							.toString());
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session, TestExecPluginActivator.INTERIOR_NODE);
		}
	}

	/**
	 * This method asserts that DmtException.NODE_NOT_FOUND is thrown
	 * if nodeUri points to a non-existing node 
	 * 
	 * @spec DmtSession.getEffectiveNodeAcl(String)
	 */			
	private void testGetEffectiveNodeAcl003() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetEffectiveNodeAcl003");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);

			session.getEffectiveNodeAcl(TestExecPluginActivator.INEXISTENT_NODE);

			DefaultTestBundleControl.failException("#", DmtException.class);
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
	 * This method asserts that getEffectiveNodeAcl is executed when the right Acl is set (Remote)
	 * 
	 * @spec DmtSession.getEffectiveNodeAcl(String)
	 */
	private void testGetEffectiveNodeAcl004() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetEffectiveNodeAcl004");

            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.PRINCIPAL, Acl.GET );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,
					TestExecPluginActivator.INTERIOR_NODE, DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.getEffectiveNodeAcl(TestExecPluginActivator.INTERIOR_NODE);

			DefaultTestBundleControl.pass("getEffectiveNodeAcl was successfully executed");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);

		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session,TestExecPluginActivator.INTERIOR_NODE);
            
		}

	}
	
	/**
	 * This method asserts that getNodeType is executed when the right DmtPermission is set (Local)
	 * 
	 * @spec DmtSession.getEffectiveNodeAcl(String)
	 */
	private void testGetEffectiveNodeAcl005() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetEffectiveNodeAcl005");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtConstants.ALL_NODES, DmtPermission.GET));
			
			session.getEffectiveNodeAcl(DmtConstants.OSGi_LOG);
			
			DefaultTestBundleControl.pass("getEffectiveNodeAcl was successfully executed");
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
	 * @spec DmtSession.getEffectiveNodeAcl(String)
	 */
	private void testGetEffectiveNodeAcl006() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetEffectiveNodeAcl006");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.getEffectiveNodeAcl(TestExecPluginActivator.INTERIOR_NODE_NAME);

			DefaultTestBundleControl.pass("A relative URI can be used with getEffectiveNodeAcl.");
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
	 * @spec DmtSession.getEffectiveNodeAcl(String)
	 */
	private void testGetEffectiveNodeAcl007() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetEffectiveNodeAcl007");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.INTERIOR_NODE, DmtSession.LOCK_TYPE_ATOMIC);

			session.getEffectiveNodeAcl("");

			DefaultTestBundleControl.pass("Asserts that an empty string as relative URI means the root " +
					"URI the session was opened with");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}	
}

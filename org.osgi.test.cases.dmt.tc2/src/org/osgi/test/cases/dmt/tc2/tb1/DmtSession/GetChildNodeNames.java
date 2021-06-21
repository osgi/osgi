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

package org.osgi.test.cases.dmt.tc2.tb1.DmtSession;

import org.osgi.service.dmt.Acl;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.security.DmtPermission;
import org.osgi.service.dmt.security.DmtPrincipalPermission;
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
 * This test case validates the implementation of <code>getChildNodeNames</code> method of DmtSession, 
 * according to MEG specification
 */
public class GetChildNodeNames implements TestInterface {
	private DmtTestControl tbc;

	public GetChildNodeNames(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	@Override
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
			DefaultTestBundleControl.log("#testGetChildNodeNames001");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);

			session.getChildNodeNames(TestExecPluginActivator.INEXISTENT_NODE);

			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts DmtException.getCode",
					DmtException.NODE_NOT_FOUND, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
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
			DefaultTestBundleControl.log("#testGetChildNodeNames002");

            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.PRINCIPAL, Acl.GET );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,
					TestExecPluginActivator.INTERIOR_NODE, DmtSession.LOCK_TYPE_SHARED);

			session.getChildNodeNames(TestExecPluginActivator.INTERIOR_NODE);
			DefaultTestBundleControl.pass("getChildNodeNames correctly executed");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
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
			DefaultTestBundleControl.log("#testGetChildNodeNames003");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtConstants.ALL_NODES, DmtPermission.GET));
			
			session.getChildNodeNames(TestExecPluginActivator.INTERIOR_NODE);
			
			DefaultTestBundleControl.pass("getChildNodeNames correctly executed");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
			
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
			DefaultTestBundleControl.log("#testGetChildNodeNames004");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtConstants.ALL_NODES, DmtPermission.GET));
			
			String[] childs = session.getChildNodeNames(TestExecPluginActivator.ROOT);
			
			TestCase.assertTrue("Asserting if an empty array was returned by DmtAdmin.", childs == null ? false : (childs.length == 0)); 
			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
			
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
			DefaultTestBundleControl.log("#testGetChildNodeNames005");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtConstants.ALL_NODES, DmtPermission.GET));
			
			String[] childs = session.getChildNodeNames(TestExecPluginActivator.INTERIOR_NODE_WITH_NULL_VALUES);
			
			TestCase.assertTrue("Asserting if an array with two elements was returned.", (childs.length == 2));
			
			
			boolean hasNull = false;
			for (int i=0; i<childs.length; i++) {
				if (childs[i] == null) {
					hasNull = true;
					break;
				}
			}
			
			TestCase.assertTrue("Asserting if no null entries exists as elements in the returned array.", !hasNull);
			
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
	 * @spec DmtSession.getChildNodeNames(String)
	 */
	private void testGetChildNodeNames006() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetChildNodeNames006");
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.getChildNodeNames(TestExecPluginActivator.INTERIOR_NODE_NAME);

			DefaultTestBundleControl.pass("A relative URI can be used with getChildNodeNames.");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
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
			DefaultTestBundleControl.log("#testGetChildNodeNames007");

			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_SHARED);
			session.getChildNodeNames(TestExecPluginActivator.LEAF_NODE);

			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts DmtException.getCode",
					DmtException.COMMAND_NOT_ALLOWED, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);
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
			DefaultTestBundleControl.log("#testGetChildNodeNames008");
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.INTERIOR_NODE, DmtSession.LOCK_TYPE_ATOMIC);

			session.getChildNodeNames("");

			DefaultTestBundleControl.pass("Asserts that an empty string as relative URI means the root" +
					" URI the session was opened with");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}	
}

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
 * Jan 26, 2005  Andre Assad
 * CR 1          Implement MEG TCK
 * ============  ==============================================================
 * Feb 15, 2005  Alexandre Santos
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ===========  ===============================================================
 */

package org.osgi.test.cases.dmt.tc2.tb1.DmtSession;

import org.osgi.service.dmt.Acl;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.security.DmtPermission;
import org.osgi.service.dmt.security.DmtPrincipalPermission;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.TestInterface;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestMetaNode;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.NonAtomic.TestNonAtomicPluginActivator;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * @author Andre Assad
 * 
 * This test case validates the implementation of <code>getMetaNode<code> method of DmtSession, 
 * according to MEG specification
 */
public class GetMetaNode implements TestInterface {
	private DmtTestControl tbc;

	public GetMetaNode(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	@Override
	public void run() {
        prepare();
		testGetMetaNode001();
		testGetMetaNode002();
		testGetMetaNode003();
		testGetMetaNode004();
		testGetMetaNode005();
		testGetMetaNode006();
		testGetMetaNode007();
	}
    
    private void prepare() {
        tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
    }
	/**
	 * Asserts that the metanode is correctly returned.
	 * 
	 * DmtSession.getMetaNode(String)
	 */
	private void testGetMetaNode001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetMetaNode001");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);

			MetaNode metaNode = session.getMetaNode(TestExecPluginActivator.INTERIOR_NODE);
			TestCase.assertTrue("Asserts that the metanode was correctly returned.",
					metaNode instanceof TestMetaNode);
			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * This method asserts that getMetaNode is executed when the right Acl is set (Remote)
	 * 
	 * @spec DmtSession.getMetaNode(String)
	 */
	private void testGetMetaNode002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetMetaNode002");

            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.PRINCIPAL, Acl.GET );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,
					TestExecPluginActivator.INTERIOR_NODE, DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.getMetaNode(TestExecPluginActivator.INTERIOR_NODE);

			DefaultTestBundleControl.pass("getMetaNode was successfully executed");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session,TestExecPluginActivator.INTERIOR_NODE);
            
		}
	}

	/**
	 * This method asserts that getMetaNode is executed when the right DmtPermission is set (Local)
	 * 
	 * @spec DmtSession.getMetaNode(String)
	 */
	private void testGetMetaNode003() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetMetaNode003");

			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG,
					DmtSession.LOCK_TYPE_EXCLUSIVE);

			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtConstants.ALL_NODES, DmtPermission.GET));

			session.getMetaNode(DmtConstants.OSGi_LOG);

			DefaultTestBundleControl.pass("getMetaNode was successfully executed");
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
	 * @spec DmtSession.getMetaNode(String)
	 */
	private void testGetMetaNode004() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetMetaNode004");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.getMetaNode(TestExecPluginActivator.INTERIOR_NODE_NAME);

			DefaultTestBundleControl.pass("A relative URI can be used with getMetaNode.");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}	
	
    /**
     * Asserts that DmtSession.getMetaNode returns null if there is no meta data available for the given node
     * 
     * @spec DmtSession.getMetaNode(String)
     */
	private void testGetMetaNode005() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetMetaNode005");
            
            session = tbc.getDmtAdmin().getSession(TestNonAtomicPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

            TestCase.assertNull("Asserts that DmtSession.getMetaNode returns null if there is no meta data " +
                    "available for the given node",session.getMetaNode(TestNonAtomicPluginActivator.INTERIOR_NODE));
            
        } catch (Exception e) {
        	tbc.failUnexpectedException(e);
        } finally {
            tbc.closeSession(session);
        }
    }
	
    /**
     * Asserts that a node does not have to exist for having meta-data associated with it
     * 
     * @spec DmtSession.getMetaNode(String)
     */
	private void testGetMetaNode006() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetMetaNode006");
            
            session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

            TestCase.assertNotNull("Asserts that a node does not have to exist for having meta-data associated with it",session.getMetaNode(TestExecPluginActivator.INEXISTENT_LEAF_NODE));
            
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
	 * @spec DmtSession.getMetaNode(String)
	 */
	private void testGetMetaNode007() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetMetaNode007");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.INTERIOR_NODE, DmtSession.LOCK_TYPE_ATOMIC);

			session.getMetaNode("");

			DefaultTestBundleControl.pass("Asserts that an empty string as relative URI means the root " +
					"URI the session was opened with");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}	
}

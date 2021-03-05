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
 * Jan 31, 2005  Andre Assad
 * 1             Implement MEG TCK
 * ============  ==============================================================
 * Feb 15, 2005  Andre Assad
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ============  ==============================================================
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
 * @author Andre Assad
 * 
 * This test case validates the implementation of <code>getNodeTimestamp</code> method of DmtSession, 
 * according to MEG specification.
 */
public class GetNodeTimestamp implements TestInterface {

	private DmtTestControl tbc;

	public GetNodeTimestamp(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	@Override
	public void run() {
        prepare();
        if (DmtConstants.SUPPORTS_NODE_TIMESTAMP) {
    		testGetNodeTimestamp001();
    		testGetNodeTimestamp002();
    		testGetNodeTimestamp003();
    		testGetNodeTimestamp004();
    		testGetNodeTimestamp005();
        } else {
            testGetNodeTimestampFeatureNotSupported001();
        }
	}
    private void prepare() {
        tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
    }
	/**
	 * This method asserts that DmtException.NODE_NOT_FOUND is thrown
	 * if nodeUri points to a non-existing node 
	 * 
	 * @spec DmtSession.getNodeTimestamp(String)
	 */
	private void testGetNodeTimestamp001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetNodeTimestamp001");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);
			session.getNodeTimestamp(TestExecPluginActivator.INEXISTENT_NODE);

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
	 * This method asserts that getNodeTimestamp is executed when the right Acl is set (Remote)
	 * 
	 * @spec DmtSession.getNodeTimestamp(String)
	 */
	private void testGetNodeTimestamp002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetNodeTimestamp002");

            tbc.openSessionAndSetNodeAcl(TestExecPluginActivator.INTERIOR_NODE, DmtConstants.PRINCIPAL, Acl.GET );
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_EXCLUSIVE);

			session.getNodeTimestamp(TestExecPluginActivator.INTERIOR_NODE);

			DefaultTestBundleControl.pass("getNodeTimestamp correctly executed");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
            tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
            tbc.cleanUp(session,TestExecPluginActivator.INTERIOR_NODE);
            
		}

	}

	/**
	 * This method asserts that getNodeVersion is executed when the right DmtPermission is set (Local)
	 * 
	 * @spec DmtSession.getNodeTimestamp(String)
	 */
	private void testGetNodeTimestamp003() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetNodeTimestamp003");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class
					.getName(), DmtConstants.ALL_NODES, DmtPermission.GET));
			
			session.getNodeTimestamp(TestExecPluginActivator.INTERIOR_NODE);
			
			DefaultTestBundleControl.pass("getNodeTimestamp correctly executed");
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
	 * @spec DmtSession.getNodeTimestamp(String)
	 */
	private void testGetNodeTimestamp004() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetNodeTimestamp004");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.getNodeTimestamp(TestExecPluginActivator.INTERIOR_NODE_NAME);

			DefaultTestBundleControl.pass("A relative URI can be used with getNodeTimestamp.");
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
	 * @spec DmtSession.getNodeTimestamp(String)
	 */
	private void testGetNodeTimestamp005() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetNodeTimestamp005");
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.INTERIOR_NODE, DmtSession.LOCK_TYPE_ATOMIC);

			session.getNodeTimestamp("");

			DefaultTestBundleControl.pass("Asserts that an empty string as relative URI means the root " +
				"URI the session was opened with");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
    /**
     * Asserts that if the DmtAdmin service implementation does not support this method,
     * DmtException.FEATURE_NOT_SUPPORTED is thrown
     * 
     * @spec DmtSession.getNodeTimestamp(String)
     */
    private void testGetNodeTimestampFeatureNotSupported001() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testGetNodeTimestampFeatureNotSupported001");

            session = tbc.getDmtAdmin().getSession(".",
                    DmtSession.LOCK_TYPE_SHARED);
            session.getNodeTimestamp(TestExecPluginActivator.INTERIOR_NODE);

            DefaultTestBundleControl.failException("", DmtException.class);
        } catch (DmtException e) {
            TestCase.assertEquals(
                    "Asserting that DmtException's code is FEATURE_NOT_SUPPORTED",
                    DmtException.FEATURE_NOT_SUPPORTED, e.getCode());
        } catch (Exception e) {
        	tbc.failExpectedOtherException(DmtException.class, e);
        } finally {
            tbc.closeSession(session);
        }
    }	
}

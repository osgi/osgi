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
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 21/JAN/2005  Andre Assad
 * CR 1         Implement MEG TCK
 * ===========  ==============================================================
 * Feb 11, 2005  Alexandre Santos
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ===========  ==============================================================
 * Feb 14, 2005  Leonardo Barros
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ===========  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tb1.DmtSession;

import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.security.DmtPermission;

import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.TestInterface;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * This test case validates the implementation of <code>getRootUri</code> method of DmtSession, 
 * according to MEG specification
 */

public class GetRootUri implements TestInterface {
	private DmtTestControl tbc;

	public GetRootUri(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	@Override
	public void run() {
        prepare();
		testGetRootUri001();
		testGetRootUri002();
		testGetRootUri003();
	}
    private void prepare() {
        tbc.setPermissions(new PermissionInfo[] {
                new PermissionInfo(DmtPermission.class.getName(), "*", DmtPermission.GET)});
    }
	/**
	 * Asserts that the complete URI of the root node is returned.
	 * 
	 * @spec DmtSession.getRootUri()
	 */
	private void testGetRootUri001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetRootUri001");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_ROOT,
					DmtSession.LOCK_TYPE_ATOMIC);
			TestCase.assertEquals("Asserting root uri", DmtConstants.OSGi_ROOT,
					session.getRootUri());
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * Asserts that "." is returned when the session is created without specifying a root
	 * 
	 * @spec DmtSession.getRootUri()
	 */
	private void testGetRootUri002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetRootUri002");
			session = tbc.getDmtAdmin().getSession(null,
					DmtSession.LOCK_TYPE_ATOMIC);
			TestCase.assertEquals("Asserting root uri", ".", session.getRootUri());
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * Asserts that the complete URI of the root node is returned when the root is a leaf node.
	 * 
	 * @spec DmtSession.getRootUri()
	 */
	private void testGetRootUri003() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetRootUri003");
			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.LEAF_NODE,
					DmtSession.LOCK_TYPE_ATOMIC);
			TestCase.assertEquals("Asserting root uri", TestExecPluginActivator.LEAF_NODE, session.getRootUri());
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}	
}

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
 */

package org.osgi.test.cases.dmt.tc2.tb1.DmtSession;

import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.security.DmtPermission;
import org.osgi.service.dmt.security.DmtPrincipalPermission;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.TestInterface;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * @author Andre Assad
 * 
 * This test case validates the implementation of <code>getPrincipal</code> method, 
 * according to MEG specification.
 */
public class GetPrincipal implements TestInterface {
	private DmtTestControl tbc;

	public GetPrincipal(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	@Override
	public void run() {
        prepare();
		testGetPrincipal001();
		testGetPrincipal002();
	}
    private void prepare() {
        tbc.setPermissions(new PermissionInfo[0]);
    }
	/**
	 * Asserts that getPrincipal returns the principal passed in the DmtSession's constructor
	 *  
	 * @spec DmtSession.getPrincipal()
	 */
	private void testGetPrincipal001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetPrincipal001");
			tbc.setPermissions(new PermissionInfo(DmtPrincipalPermission.class.getName(),DmtConstants.PRINCIPAL,"*"));
			session = tbc.getDmtAdmin().getSession(DmtConstants.PRINCIPAL,
					".", DmtSession.LOCK_TYPE_ATOMIC);

			TestCase.assertEquals("Asserts that getPrincipal returns the principal passed in the DmtSession's constructor",
					DmtConstants.PRINCIPAL, session.getPrincipal());
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.cleanUp(session, null);
		}
	}

	/**
	 * Asserts that getPrincipal returns null when the session is local
	 * 
	 * @spec DmtSession.getPrincipal()
	 */
	private void testGetPrincipal002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetPrincipal002");
            tbc.setPermissions(new PermissionInfo[] {
                    new PermissionInfo(DmtPermission.class.getName(), ".", DmtPermission.GET)});

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_ATOMIC);
			TestCase.assertNull("Asserts that getPrincipal returns null when the session is local", session
					.getPrincipal());
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
}

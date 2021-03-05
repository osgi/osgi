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
 * Feb 11, 2005 Alexandre Santos
 * 1            Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ===========  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tb1.DmtSession;

import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.security.DmtPermission;

import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.TestInterface;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * @author Andre Assad
 * 
 * This test case validates the implementation of <code>getSessionId</code> method of DmtSession, 
 * according to MEG specification
 */
public class GetSessionId implements TestInterface {
	private DmtTestControl tbc;

	public GetSessionId(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	@Override
	public void run() {
        prepare();
		testGetSessionId001();
	}
    private void prepare() {
        tbc.setPermissions(new PermissionInfo[] {
                new PermissionInfo(DmtPermission.class.getName(), ".", DmtPermission.GET)});
    }
	/**
	 * This method asserts that two different sessions have differents ids
	 * 
	 * @spec DmtSession.getSessionId()
	 */
	private void testGetSessionId001() {
		DmtSession session1 = null;
		DmtSession session2 = null;
		try {
			DefaultTestBundleControl.log("#testGetSessionId001");
			session1 = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);
			session2 = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);
			TestCase.assertTrue(
					"Asserting that session1 and session2 has differents ids.",
					(session1.getSessionId() != session2.getSessionId()));
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session1);
            tbc.closeSession(session2);
		}

	}
}

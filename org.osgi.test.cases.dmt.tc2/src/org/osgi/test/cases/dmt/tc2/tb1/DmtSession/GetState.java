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
 * 1             Implement MEG TCK
 * ============  ==============================================================
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
 * @author Luiz Felipe Guimaraes
 * 
 * This Test Case Validates the implementation of <code>getState</code> method of DmtSession, 
 * according to MEG specification
 */
public class GetState implements TestInterface {
	private DmtTestControl tbc;

	public GetState(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	@Override
	public void run() {
        prepare();
	    //STATE_INVALID is tested in org.osgi.test.cases.dmt.plugins.tbc.Others.UseCases
		testGetState001();
		testGetState002();
	}
    private void prepare() {
        tbc.setPermissions(new PermissionInfo[] {
                new PermissionInfo(DmtPermission.class.getName(), ".", DmtPermission.GET)});
    }
	/**
	 * Asserts that getState() returns STATE_OPEN after opening a DmtSession
	 * 
	 * @spec DmtSession.getState()
	 */
	private void testGetState001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetState001");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);

			TestCase.assertEquals("Asserting that the session is opened ",
					DmtSession.STATE_OPEN, session.getState());
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * Asserts that getState() returns STATE_CLOSED after closing a DmtSession
	 * 
	 * @spec DmtSession.getState()
	 */
	private void testGetState002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testGetState002");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);
			session.close();
			TestCase.assertEquals("Asserting that the session is closed ",
					DmtSession.STATE_CLOSED, session.getState());
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}

	
}

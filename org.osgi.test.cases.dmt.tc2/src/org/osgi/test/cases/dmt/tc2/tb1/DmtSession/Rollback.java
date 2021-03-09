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
 * ===========   ==============================================================
 * Jan 21, 2005  Andre Assad
 * 1             Implement MEG TCK
 * ===========   ==============================================================
 * Feb 11, 2005  Luiz Felipe Guimaraes
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-002)
 * ===========   ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tb1.DmtSession;

import org.osgi.service.dmt.DmtIllegalStateException;
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
 * This test case validates the implementation of <code>rollback</code> method of DmtSession, 
 * according to MEG specification
 */
public class Rollback implements TestInterface {
	private DmtTestControl tbc;

	public Rollback(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	@Override
	public void run() {
        prepare();
		testRollback001();
		testRollback002();
		testRollback003();
	}
    private void prepare() {
        tbc.setPermissions(new PermissionInfo[] {
                new PermissionInfo(DmtPermission.class.getName(), ".", DmtPermission.GET)});
    }
	/**
	 * This method asserts that DmtIllegalStateException is thrown 
	 * if rollback() is called when the session is opened using 
	 * the LOCK_TYPE_EXCLUSIVE lock type
	 * 
	 * @spec DmtSession.rollback()
	 */
	private void testRollback001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testRollback001");
			session = tbc.getDmtAdmin().getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.rollback();
			DefaultTestBundleControl.failException("#",DmtIllegalStateException.class);
		} catch (DmtIllegalStateException e) {
			DefaultTestBundleControl.pass("DmtIllegalStateException correctly thrown");
		} catch (Exception e) {
            tbc.failExpectedOtherException(DmtIllegalStateException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * This method asserts that DmtIllegalStateException is thrown 
	 * if rollback() is called when the session is opened using 
	 * the LOCK_TYPE_SHARED lock type
	 * 
	 * @spec DmtSession.rollback()
	 */
	private void testRollback002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testRollback002");
			session = tbc.getDmtAdmin().getSession(".", DmtSession.LOCK_TYPE_SHARED);
			session.rollback();
			DefaultTestBundleControl.failException("#",DmtIllegalStateException.class);
		} catch (DmtIllegalStateException e) {
			DefaultTestBundleControl.pass("DmtIllegalStateException correctly thrown");
		} catch (Exception e) {
            tbc.failExpectedOtherException(DmtIllegalStateException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}

		
    /**
     * This method asserts that after a rollback() the session is not closed.
     * 
     * @spec DmtSession.rollback()
     */
    private void testRollback003() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testRollback003");
            session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_ATOMIC);
            session.rollback();
            TestCase.assertEquals("Asserting that after a rollback(), the session is not closed.", session.getState(), DmtSession.STATE_OPEN);
        } catch (Exception e) {
        	tbc.failUnexpectedException(e);
        } finally {
            tbc.closeSession(session);
        }
    }
    
}

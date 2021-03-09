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
 * Mar 15, 2005  Luiz Felipe Guimaraes
 * 1             Implement MEG TCK
 * ===========   ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tb1.DmtSession;

import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.DmtIllegalStateException;
import org.osgi.service.dmt.security.DmtPermission;

import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.TestInterface;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;
/**
 * @author Luiz Felipe Guimaraes
 * 
 * This test case validates the implementation of <code>commit</code> method of DmtSession, 
 * according to MEG specification.
 * 
 */
public class Commit implements TestInterface {
	private DmtTestControl tbc;

	public Commit(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	@Override
	public void run() {
        prepare();
		testCommit001();
		testCommit002();
		testCommit003();
        testCommit004();
	}
    private void prepare() {
        tbc.setPermissions(new PermissionInfo[] {
                new PermissionInfo(DmtPermission.class.getName(), ".", DmtPermission.GET)
        });
    }
	/**
	 * This method asserts that whenever a DmtSession with LOCK_TYPE_EXCLUSIVE 
	 * lock is created, the commit operation is not supported.
	 * 
	 * @spec DmtSession.commit()
	 */
	private void testCommit001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCommit001");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.commit();
			DefaultTestBundleControl.failException("#", DmtIllegalStateException.class);
		} catch (DmtIllegalStateException e) {
			DefaultTestBundleControl.pass("DmtIllegalStateException is thrown if the session is tried to commit a non-atomic session");
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtIllegalStateException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts that whenever a DmtSession with LOCK_TYPE_SHARED lock is created, 
	 * the commit operation is not supported (second case).
	 * 
	 * @spec DmtSession.commit()
	 */
	private void testCommit002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testCommit002");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_SHARED);
			session.commit();
			DefaultTestBundleControl.failException("#", DmtIllegalStateException.class);
		} catch (DmtIllegalStateException e) {
			DefaultTestBundleControl.pass("DmtIllegalStateException is thrown if the session is tried to commit a non-atomic session");
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtIllegalStateException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}

    
    /**
     * This method asserts that commit() passes correctly.
     * 
     * @spec DmtSession.commit()
     */
    private void testCommit003() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testCommit003");
            session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_ATOMIC);
            session.commit();
            session.close();
            DefaultTestBundleControl.pass("Asserting that session was committed correctly");
        } catch (Exception e) {
            tbc.failUnexpectedException(e);
        } finally {
            tbc.closeSession(session);
        }
    }
    
    /**
     * This method asserts that after a commit() the session is not closed.
     * 
     * @spec DmtSession.commit()
     */
    private void testCommit004() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testCommit004");
            session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_ATOMIC);
            session.commit();
            TestCase.assertEquals("Asserting that after a commit(), the session is not closed.", session.getState(), DmtSession.STATE_OPEN);
        } catch (Exception e) {
        	tbc.failUnexpectedException(e);
        } finally {
            tbc.closeSession(session);
        }
    }
    
}

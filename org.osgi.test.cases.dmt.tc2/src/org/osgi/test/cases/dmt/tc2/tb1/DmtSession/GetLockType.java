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

import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.TestInterface;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * @author Andre Assad
 * 
 * This test case validates the implementation of <code>getLockType<code> method of DmtSession, 
 * according to MEG specification
 */
public class GetLockType implements TestInterface {
	private DmtTestControl tbc;

	public GetLockType(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	@Override
	public void run() {
        prepare();
		testGetLockType001();
		testGetLockType002();
		testGetLockType003();
	}

    private void prepare() {
        tbc.setPermissions( new PermissionInfo(DmtPermission.class.getName(), ".", DmtPermission.GET));

    }
	/**
	 * This method asserts that DmtSession.LOCK_TYPE_ATOMIC is returned when
	 * getLockType is called in an atomic session 
	 * 
	 * @spec DmtSession.getLockType()
	 */
	private void testGetLockType001() {
		DmtSession session = null;
	    try {
			DefaultTestBundleControl.log("#testGetLockType001");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_ATOMIC);
	        TestCase.assertEquals("Asserting lock type", DmtSession.LOCK_TYPE_ATOMIC,
	                session.getLockType());
	    } catch (Exception e) {
	    	tbc.failUnexpectedException(e);
	    }  finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts that DmtSession.LOCK_TYPE_EXCLUSIVE is returned when
	 * getLockType is called in an exclusive session 
	 * 
	 * @spec DmtSession.getLockType()
	 */
	private void testGetLockType002() {
		DmtSession session = null;
	    try {
			DefaultTestBundleControl.log("#testGetLockType002");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
	        TestCase.assertEquals("Asserting lock type", DmtSession.LOCK_TYPE_EXCLUSIVE,
	                session.getLockType());
	    } catch (Exception e) {
	    	tbc.failUnexpectedException(e);
	    } finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts that DmtSession.LOCK_TYPE_SHARED is returned when
	 * getLockType is called in a read only session 
	 * 
	 * @spec DmtSession.getLockType()
	 */
	private void testGetLockType003() {
		DmtSession session = null;
	    try {
			DefaultTestBundleControl.log("#testGetLockType003");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);

	        TestCase.assertEquals("Asserting lock type", DmtSession.LOCK_TYPE_SHARED,
	                session.getLockType());
	    } catch (Exception e) {
	    	tbc.failUnexpectedException(e);
	    } finally {
			tbc.closeSession(session);
		}       
	}
    
}

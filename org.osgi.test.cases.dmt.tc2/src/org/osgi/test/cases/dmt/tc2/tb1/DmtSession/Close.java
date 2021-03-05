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
 * Feb 16, 2005  Alexandre Santos
 * 1		     Implement MEG TCK
 * ============  =================================================================
 * Mar 15, 2005  Alexandre Santos
 * 28		     Implement test cases for DmtSession.close()
 * ============  =================================================================
 * Jun 17, 2005  Alexandre Alves
 * 28            [MEGTCK][DMT] Implement test cases for DmtSession.close()
 * ============  ==============================================================
 */
package org.osgi.test.cases.dmt.tc2.tb1.DmtSession;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.security.DmtPermission;

import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.TestInterface;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ReadOnly.TestReadOnlyPlugin;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ReadOnly.TestReadOnlyPluginActivator;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * This test case validates the implementation of <code>close</code> method of DmtSession, 
 * according to MEG specification
 * 
 * @author Alexandre Santos
 */

public class Close implements TestInterface  {
	private DmtTestControl tbc;
	
	public Close(DmtTestControl tbc) {
		this.tbc = tbc;
	}
	@Override
	public void run() {
        prepare();
		testClose001();
        testClose002();
	}
    private void prepare() {
        //No DmtPermission is needed. 
        tbc.setPermissions(new PermissionInfo[] {
                new PermissionInfo(DmtPermission.class.getName(), DmtConstants.OSGi_ROOT+"/*", DmtPermission.GET)
        });
    }
	/**
	 * Asserts that after a close, the state is really set to STATE_CLOSED.
	 * 
	 * @spec DmtSession.close()
	 */
	private void testClose001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testClose001");
			session = tbc.getDmtAdmin().getSession(DmtConstants.OSGi_LOG, DmtSession.LOCK_TYPE_ATOMIC);
			session.close();
			TestCase.assertEquals("Asserting if the state after a close is really STATE_CLOSED.", DmtSession.STATE_CLOSED, session.getState());
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
		    tbc.closeSession(session);
        }
		
	}
    
    /**
     * Asserts that the session becomes STATE_INVALID if the close operation completed unsuccessfully
     * 
     * @spec DmtSession.close()
     */
    private void testClose002() {
        DmtSession session = null;
        try {
            DefaultTestBundleControl.log("#testClose002");
            session = tbc.getDmtAdmin().getSession(TestReadOnlyPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);
            TestReadOnlyPlugin.setExceptionAtClose(true);
            session.close();
            DefaultTestBundleControl.failException("",DmtException.class);
        } catch (DmtException e) {
            TestCase.assertEquals("Asserts that the session becomes STATE_INVALID if the close operation completed unsuccessfully", DmtSession.STATE_INVALID, session.getState());
        } catch (Exception e) {
        	tbc.failExpectedOtherException(DmtException.class, e);
        } finally {
            tbc.closeSession(session);
            TestReadOnlyPlugin.setExceptionAtClose(false);
        }
        
    }
	
	
}

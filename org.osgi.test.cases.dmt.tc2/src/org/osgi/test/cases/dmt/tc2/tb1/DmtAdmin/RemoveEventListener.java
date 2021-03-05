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
 * Jul 12, 2005  Luiz Felipe Guimaraes
 * 118           Implement MEG TCK
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tb1.DmtAdmin;

import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.security.DmtPermission;
import org.osgi.service.dmt.security.DmtPrincipalPermission;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tb1.DmtEvent.DmtEventListenerImpl;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.TestInterface;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * @author Luiz Felipe Guimaraes
 * This test case validates the implementation of <code>removeEventListener</code> method of DmtAdmin, 
 * according to MEG specification
 * 
 */

public class RemoveEventListener implements TestInterface {
	private DmtTestControl tbc;
	
	public RemoveEventListener(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	@Override
	public void run() {
        prepare();
        testRemoveEventListener001();
        testRemoveEventListener002();
	}
	
    private void prepare() {
        tbc.setPermissions(
                new PermissionInfo[] {
                new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS),
                new PermissionInfo(DmtPrincipalPermission.class.getName(), DmtConstants.PRINCIPAL, "*") }
                );
    }
	/**
	 * Asserts that NullPointerException is thrown if the listener parameter is null
	 * 
	 * @spec DmtAdmin.removeEventListener(DmtEventListener) 

	 */
	private void testRemoveEventListener001() {
		try {
			DefaultTestBundleControl.log("#testRemoveEventListener001");
			// Does not compile anymore
			// tbc.getDmtAdmin().removeEventListener(null);
			DefaultTestBundleControl.failException("", NullPointerException.class);
		} catch (NullPointerException e) {
			DefaultTestBundleControl.pass("NullPointerException correctly thrown");
		} catch (Exception e) {
			tbc.failExpectedOtherException(NullPointerException.class, e);
		}
	}
	
	/**
	 * Asserts that DmtAdmin.removeEventListener removes a previously registered listener. 
	 * After this call, the listener will not receive change notifications
	 * 
	 * @spec DmtAdmin.removeEventListener(DmtEventListener) 
	 */
	private void testRemoveEventListener002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testRemoveEventListener002");
			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.ROOT,
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			
			@SuppressWarnings("unused")
			DmtEventListenerImpl event = new DmtEventListenerImpl();
			// Does not compile anymore
			
//			tbc.getDmtAdmin().addEventListener(DmtConstants.ALL_DMT_EVENTS,
//					TestExecPluginActivator.ROOT,event);
//			
//			tbc.getDmtAdmin().removeEventListener(event);
//			
//			session.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE);
//			
//			synchronized (tbc) {
//				tbc.wait(DmtConstants.WAITING_TIME);
//			}
//			
//			tbc.assertEquals("Asserts that the listener does not receive change notifications after DmtAdmin.removeEventListener() is called",0,event.getCount());

			TestCase.fail("API changed - pkriens");
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	}
}


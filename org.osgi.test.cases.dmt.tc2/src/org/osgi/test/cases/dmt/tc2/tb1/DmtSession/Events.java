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
 * Jun 29, 2005  Luiz Felipe Guimaraes
 * 1             Implement MEG TCK
 * ============  ==============================================================
 */
package org.osgi.test.cases.dmt.tc2.tb1.DmtSession;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.security.DmtPermission;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.TestInterface;
import org.osgi.test.cases.dmt.tc2.tbc.Activators.EventHandlerImpl;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * This test case validates the events of Dmt, according to MEG specification
 * 
 * @author Luiz Felipe Guimaraes
 */

public class Events implements TestInterface  {
	private DmtTestControl tbc;
	
	public Events(DmtTestControl tbc) {
		this.tbc = tbc;
	}
	@Override
	public void run() {
        prepare();
	    testEvents001();
	    testEvents002();
	}
    private void prepare() {
        tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
    }
	/**
	 * It asserts that when the commit method is called, the correct Dmt 
	 * Events are sent, that the nodes are the expected and that the 
	 * order is the expected and if the session is atomic, no events are
	 * sent before commit. The thread waits until all events 
	 * expected are sent or for the timeout.
	 * 
	 * @spec 117.10 Events
	 */	
	private void testEvents001() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testEvents001");
			// waits events sent by previous test methods before doing this test
			synchronized (tbc) {
				tbc.wait(DmtConstants.WAITING_TIME);
			}
			
			//Reset previous events
			EventHandlerImpl.reset();
			
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE);
			session.setNodeValue(TestExecPluginActivator.LEAF_NODE,new DmtData("B"));
			session.copy(TestExecPluginActivator.INTERIOR_NODE,TestExecPluginActivator.INEXISTENT_NODE,true);
			session.renameNode(TestExecPluginActivator.INTERIOR_NODE,TestExecPluginActivator.RENAMED_NODE_NAME);
			session.deleteNode(TestExecPluginActivator.INTERIOR_NODE);
			TestCase.assertEquals("Asserts that if the session is atomic, no event is sent before commit.",0,EventHandlerImpl.getEventCount());
			session.commit();
			synchronized (tbc) {
				tbc.wait(DmtConstants.WAITING_TIME);
			}
			TestCase.assertTrue("Asserts that the property session.id contains the same value as DmtSession.getSessionId()",EventHandlerImpl.getSessionId() == session.getSessionId());
			TestCase.assertTrue("Asserts if the events have the correct properties.", EventHandlerImpl.isAllProperties());
			// RFC-141: There is no pre-defined order of events anymore (see https://www.osgi.org/members/bugzilla/show_bug.cgi?id=1794)
//			tbc.assertEquals("Asserts that the number of events are correct",5,EventHandlerImpl.getEventCount());
			TestCase.assertTrue("Asserts that all of the node names are in the correct event.",EventHandlerImpl.passed());
//			tbc.assertTrue("Asserts that the order of the sent events is the expected.",EventHandlerImpl.isOrderedAtomic());
			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
		
	}	

	/**
	 * It asserts that if the session is exclusive, the events are sent immediatly, instead of waiting for commit. 
	 * 
	 * @spec 117.10 Events
	 */	
	private void testEvents002() {
		DmtSession session = null;
		try {
			DefaultTestBundleControl.log("#testEvents002");
			//Reset previous events
			EventHandlerImpl.reset();
			
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeValue(TestExecPluginActivator.LEAF_NODE,new DmtData("B"));
			session.copy(TestExecPluginActivator.INTERIOR_NODE,TestExecPluginActivator.INEXISTENT_NODE,true);
			session.renameNode(TestExecPluginActivator.INTERIOR_NODE,TestExecPluginActivator.RENAMED_NODE_NAME);
            session.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE);
			session.deleteNode(TestExecPluginActivator.INTERIOR_NODE);

			synchronized (tbc) {
				tbc.wait(DmtConstants.WAITING_TIME);
			}
			TestCase.assertTrue("Asserts if the events have the correct properties.", EventHandlerImpl.isAllProperties());
			TestCase.assertEquals("Asserts that the number of events are correct",5,EventHandlerImpl.getEventCount());
			TestCase.assertTrue("Asserts that all of the node names are in the correct event.",EventHandlerImpl.passed());
            TestCase.assertTrue("Asserts that the events are sent immediately in an exclusive session, following no order (as an atomic session).",
                EventHandlerImpl.isOrderedExclusive());
			TestCase.assertTrue("Asserts that the property session.id contains the same value as DmtSession.getSessionId()",EventHandlerImpl.getSessionId() == session.getSessionId());
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
		
	}
	
	
	
}

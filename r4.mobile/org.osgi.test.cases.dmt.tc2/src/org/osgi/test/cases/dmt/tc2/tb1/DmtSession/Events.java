/* 
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */ 

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

import info.dmtree.DmtData;
import info.dmtree.DmtSession;
import info.dmtree.security.DmtPermission;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.TestInterface;
import org.osgi.test.cases.dmt.tc2.tbc.Activators.EventHandlerImpl;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;

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
			tbc.log("#testEvents001");
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
			tbc.assertEquals("Asserts that if the session is atomic, no event is sent before commit.",0,EventHandlerImpl.getEventCount());
			session.commit();
			synchronized (tbc) {
				tbc.wait(DmtConstants.WAITING_TIME);
			}
			tbc.assertTrue("Asserts that the property session.id contains the same value as DmtSession.getSessionId()",EventHandlerImpl.getSessionId() == session.getSessionId());
			tbc.assertTrue("Asserts if the events have the correct properties.", EventHandlerImpl.isAllProperties());
			tbc.assertEquals("Asserts that the number of events are correct",5,EventHandlerImpl.getEventCount());
			tbc.assertTrue("Asserts that all of the node names are in the correct event.",EventHandlerImpl.passed());
			tbc.assertTrue("Asserts that the order of the sent events is the expected.",EventHandlerImpl.isOrderedAtomic());
			
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
			tbc.log("#testEvents002");
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
			tbc.assertTrue("Asserts if the events have the correct properties.", EventHandlerImpl.isAllProperties());
			tbc.assertEquals("Asserts that the number of events are correct",5,EventHandlerImpl.getEventCount());
			tbc.assertTrue("Asserts that all of the node names are in the correct event.",EventHandlerImpl.passed());
            tbc.assertTrue("Asserts that the events are sent immediately in an exclusive session, following no order (as an atomic session).",
                EventHandlerImpl.isOrderedExclusive());
			tbc.assertTrue("Asserts that the property session.id contains the same value as DmtSession.getSessionId()",EventHandlerImpl.getSessionId() == session.getSessionId());
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
		
	}
	
	
	
}

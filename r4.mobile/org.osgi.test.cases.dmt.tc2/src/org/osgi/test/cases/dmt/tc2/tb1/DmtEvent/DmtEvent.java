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
 * Apr 17, 2006  Luiz Felipe Guimaraes
 * 283           [MEGTCK][DMT] DMT API changes
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tb1.DmtEvent;

import info.dmtree.DmtData;
import info.dmtree.DmtSession;
import info.dmtree.security.DmtPermission;
import info.dmtree.security.DmtPrincipalPermission;

import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.TestInterface;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;



/**
 * @author Luiz Felipe Guimaraes
 * 
 * This Class Validates the implementation of <code>DmtEvent<code> constants, 
 * according to MEG specification
 */
public class DmtEvent implements TestInterface {
	private DmtTestControl tbc;
	
	public DmtEvent(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		prepare();
		testDmtEvent001();
		testDmtEvent002();
		testDmtEvent003();
	}
	
    private void prepare() {
        tbc.setPermissions(
            new PermissionInfo[] {
            new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS),
            new PermissionInfo(DmtPrincipalPermission.class.getName(), DmtConstants.PRINCIPAL, "*") }
            );
    }
	/**
	 * Asserts all the DmtEvent methods when the event is successfully sent 
	 * 
	 * @spec 117.13.5
	 */
	private void testDmtEvent001() {
		DmtSession session = null;
		try {
			tbc.log("#testDmtEvent001");
			
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_ATOMIC);
			
            synchronized (tbc) {
                tbc.wait(DmtConstants.WAITING_TIME);
            }

            DmtEventListenerImpl event = new DmtEventListenerImpl();
			tbc.getDmtAdmin().addEventListener(DmtConstants.ALL_DMT_EVENTS,
					TestExecPluginActivator.ROOT,event);
			
			session.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE);
			session.setNodeValue(TestExecPluginActivator.LEAF_NODE,new DmtData("B"));
			session.copy(TestExecPluginActivator.INTERIOR_NODE,TestExecPluginActivator.INEXISTENT_NODE,true);
			session.renameNode(TestExecPluginActivator.INTERIOR_NODE,TestExecPluginActivator.RENAMED_NODE_NAME);
			session.deleteNode(TestExecPluginActivator.INTERIOR_NODE);
			tbc.assertEquals("Asserts that if the session is atomic, no event is sent before commit.",0,event.getCount());
			session.commit();
			synchronized (tbc) {
				tbc.wait(DmtConstants.WAITING_TIME);
			}
			tbc.assertEquals("Asserts that the number of events are correct",5,event.getCount());
			tbc.assertTrue("Asserts that the order of the sent events is the expected.",event.isOrdered());
			
			
			info.dmtree.DmtEvent[] dmtEvents = event.getDmtEvents();
			assertEvent(dmtEvents[0], session,info.dmtree.DmtEvent.ADDED,new String[] { TestExecPluginActivator.INEXISTENT_NODE},null);
			assertEvent(dmtEvents[1], session,info.dmtree.DmtEvent.DELETED,new String[] { TestExecPluginActivator.INTERIOR_NODE},null);
			assertEvent(dmtEvents[2], session,info.dmtree.DmtEvent.REPLACED,new String[] { TestExecPluginActivator.LEAF_NODE},null);
			assertEvent(dmtEvents[3], session,info.dmtree.DmtEvent.RENAMED,new String[] { TestExecPluginActivator.INTERIOR_NODE},new String[] { TestExecPluginActivator.RENAMED_NODE} );
			assertEvent(dmtEvents[4], session,info.dmtree.DmtEvent.COPIED,new String[] { TestExecPluginActivator.INTERIOR_NODE},new String[] { TestExecPluginActivator.INEXISTENT_NODE } );
			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	
	}
		
	private void assertEvent(info.dmtree.DmtEvent event,DmtSession session,int expectedType,String[] expectedNodes,String[] expectedNewNodes) {
		tbc.assertEquals("Asserts that DmtEvent.getType() returns the correct event",
				expectedType,event.getType());
		tbc.assertTrue("Asserts that DmtEvent.getSessionId() returns the session Id",
				event.getSessionId() == session.getSessionId());
		
		if (null==expectedNodes) {
			tbc.assertNull("Asserts that DmtEvent.getNodes() returns null",
					event.getNodes());
		} else {
			tbc.assertTrue("Asserts that DmtEvent.getNodes() returns all nodes expected in this event",
					event.getNodes().length==expectedNodes.length);
			for (int i = 0; i < expectedNodes.length; i++) {
				tbc.assertTrue("Asserts that DmtEvent.getNodes() returns the expected nodes of this event",
						event.getNodes()[i].equals(expectedNodes[i]));
			}
		}
		if (null==expectedNewNodes) {
			tbc.assertNull("Asserts that DmtEvent.getNewNodes() returns null",
					event.getNewNodes());
		} else {
			tbc.assertTrue("Asserts that DmtEvent.getNewNodes() returns all nodes expected in this event",
					event.getNewNodes().length==expectedNewNodes.length);
			for (int i = 0; i < expectedNewNodes.length; i++) {
				tbc.assertTrue("Asserts that DmtEvent.getNewNodes() returns the expected nodes of this event",
						event.getNewNodes()[i].equals(expectedNewNodes[i]));
			}
		}
		
	}
	
	
	/**
	 * Asserts that DmtEvent.getNodes() and DmtEvent.getNewNodes() must return null for 
	 * DmtEvent.SESSION_OPENED and DmtEvent.SESSION_CLOSED  
	 * 
	 * @spec 117.13.5
	 */
	private void testDmtEvent002() {
		DmtSession session = null;
		try {
			tbc.log("#testDmtEvent002");
			
			DmtEventListenerImpl event = new DmtEventListenerImpl();
			tbc.getDmtAdmin().addEventListener(DmtConstants.ALL_DMT_EVENTS,
					TestExecPluginActivator.INTERIOR_NODE,event);
			
			session = tbc.getDmtAdmin().getSession(TestExecPluginActivator.ROOT,DmtSession.LOCK_TYPE_ATOMIC);
			session.close();
			synchronized (tbc) {
				tbc.wait(DmtConstants.WAITING_TIME);
			}
			tbc.assertEquals("Asserts that the number of events are correct",2,event.getCount());
			
			info.dmtree.DmtEvent[] dmtEvents = event.getDmtEvents();
			assertEvent(dmtEvents[0], session,info.dmtree.DmtEvent.SESSION_OPENED,null,null);
			assertEvent(dmtEvents[1], session,info.dmtree.DmtEvent.SESSION_CLOSED,null,null);

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	
	}
	
	/**
	 * Asserts all the DmtEvent methods when the event is successfully sent before DmtSession.close()
	 * is called (in case of DmtSession.LOCK_TYPE_EXCLUSIVE)
	 * 
	 * @spec 117.13.5
	 */
	private void testDmtEvent003() {
		DmtSession session = null;
		try {
			tbc.log("#testDmtEvent003");
			
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			
            synchronized (tbc) {
                tbc.wait(DmtConstants.WAITING_TIME);
            }

            DmtEventListenerImpl event = new DmtEventListenerImpl();
			tbc.getDmtAdmin().addEventListener(DmtConstants.ALL_DMT_EVENTS,
					TestExecPluginActivator.ROOT,event);
			
			session.setNodeValue(TestExecPluginActivator.LEAF_NODE,new DmtData("B"));
			session.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE);

			synchronized (tbc) {
				tbc.wait(DmtConstants.WAITING_TIME);
			}
			
			tbc.assertEquals("Asserts that if the session is exclusive, events are sent before close.",2,event.getCount());

			info.dmtree.DmtEvent[] dmtEvents = event.getDmtEvents();
			//The first one must be the DmtEvent.REPLACED and the second one DmtEvent.ADDED 
			assertEvent(dmtEvents[0], session,info.dmtree.DmtEvent.REPLACED,new String[] { TestExecPluginActivator.LEAF_NODE},null);
			assertEvent(dmtEvents[1], session,info.dmtree.DmtEvent.ADDED,new String[] { TestExecPluginActivator.INEXISTENT_NODE},null);
			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		} finally {
			tbc.closeSession(session);
		}
	
	}
	
	
		
}


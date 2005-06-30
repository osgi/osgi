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
package org.osgi.test.cases.dmt.main.tb1.DmtSession;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.main.tbc.DmtHandlerImpl;
import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.main.tbc.TestInterface;
import org.osgi.test.cases.dmt.main.tbc.Plugin.TestExecPluginActivator;

/**
 * @methodUnderTest org.osgi.service.dmt.DmtSession#close
 * @generalDescription This Test Case tests all Close methods
 *                     according to MEG specification (rfc0085)
 * @author Alexandre Santos
 */

public class Close implements TestInterface  {
	private DmtTestControl tbc;
	
	public Close(DmtTestControl tbc) {
		this.tbc = tbc;
	}
	public void run() {
		testClose001();
		testClose002();
		testClose003();
	}
	
	/**
	 * @testID testClose001
	 * @testDescription It asserts that when the close method is called, the correct Dmt 
	 * 					Events are sent, that the nodes are the expected and that the 
	 * 					order is the expected. The thread waits until all of the events 
	 * 					expected are sent or TIMEOUT.
	 * 					 
	 */	
	private synchronized void testClose001() {
		DmtSession session = null;
		try {
			tbc.log("#testClose001");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_ATOMIC);
			session.createInteriorNode(TestExecPluginActivator.INEXISTENT_NODE);
			session.setNodeValue(TestExecPluginActivator.LEAF_NODE,new DmtData("B"));
			session.copy(TestExecPluginActivator.INTERIOR_NODE,TestExecPluginActivator.INEXISTENT_NODE,true);
			session.renameNode(TestExecPluginActivator.INTERIOR_NODE,TestExecPluginActivator.RENAMED_VALUE);
			session.deleteNode(TestExecPluginActivator.INTERIOR_NODE);
			session.close();
			wait(DmtTestControl.TIMEOUT);
			tbc.assertTrue("Asserts if the events has the correct properties.", DmtHandlerImpl.isAllProperties());
			tbc.assertEquals("Asserts that the number of events is correct",5,DmtHandlerImpl.getEventCount());
			tbc.assertTrue("Asserts that all of the node names are in the correct event.",DmtHandlerImpl.passed());
			tbc.assertTrue("Asserts that the order of sent events is the expected.",DmtHandlerImpl.isOrdered());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.closeSession(session);
		}
		
	}	

	/**
	 * @testID testClose002
	 * @testDescription Asserts that after a close, the state is really setted
	 *                  to STATE_CLOSED.
	 */
	private void testClose002() {
		DmtSession session = null;
		try {
			tbc.log("#testClose002");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG, DmtSession.LOCK_TYPE_ATOMIC);
			session.close();
			tbc.assertEquals("Asserting if the state after a close is really STATE_CLOSED.", DmtSession.STATE_CLOSED, session.getState());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		}
		
	}
	
	
	/**
	 * @testID testClose003
	 * @testDescription Asserts that an exception is thrown
	 *                  when try to close a session that was already closed.
	 */
	private void testClose003() {
		DmtSession session = null;
		try {
			tbc.log("#testClose003");
			session = tbc.getDmtAdmin().getSession(DmtTestControl.OSGi_LOG, DmtSession.LOCK_TYPE_ATOMIC);
			session.close();
			session.close();
			tbc.failException("#",IllegalStateException.class);
		} catch (IllegalStateException e) {
			tbc.pass("IllegalStateException was thrown.");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName() + " but was " + e.getClass().getName());
		}		
	}		
}

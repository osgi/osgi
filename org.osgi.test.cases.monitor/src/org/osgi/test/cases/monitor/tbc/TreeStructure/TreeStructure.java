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
 * 
 */

/*
 * REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 30/03/2005   Alexandre Alves
 * 32           Implement DMT Structure validation for monitoring
 * ===========  ==============================================================
 */
package org.osgi.test.cases.monitor.tbc.TreeStructure;

import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @generalDescription Tests the DMT Structure
 */
public class TreeStructure {
	private MonitorTestControl tbc;

	public TreeStructure(MonitorTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testTreeStructure001();
		testTreeStructure002();
		testTreeStructure003();
		testTreeStructure004();
	}

	/**
	 * @testID testTreeStructure001
	 * @testDescription This method asserts if the dmt structure of the monitorable 
	 *                  is according to RFC-87 specify. If node exists and its stores the correct value.
	 */
	public void testTreeStructure001() {
		DmtSession session = null;
		try {
			tbc.log("#testTreeStructure001");
			session = tbc.getDmtAdmin().getSession(".");
			boolean passed = true;
			passed = session.isNodeUri(MonitorTestControl.DMT_URI_MONITORABLE1_SV1) && passed;
			
			passed = (session.getNodeValue(MonitorTestControl.DMT_URI_MONITORABLE1_SV1+"/CM").getString() == "CC") && passed;
			tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE,
					new String[] { "dmt structure of the monitorable is according to RFC-87 Specify." }), passed);
							
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.closeSession(session);		
		}
	}

	/**
	 * @testID testTreeStructure002
	 * @testDescription This method asserts if the dmt structure of the monitorable 
	 *                  is according to RFC-87 specify. If node exists and its stores the correct value.
	 */
	public void testTreeStructure002() {
		DmtSession session = null;
		try {
			tbc.log("#testTreeStructure002");
			session = tbc.getDmtAdmin().getSession(".");
			boolean passed = true;
			passed = session.isNodeUri(MonitorTestControl.DMT_URI_MONITORABLE1_SV2) && passed;
			
			passed = (session.getNodeValue(MonitorTestControl.DMT_URI_MONITORABLE1_SV2+"/CM").getString() == "DER") && passed;
			tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE,
					new String[] { "dmt structure of the monitorable is according to RFC-87 Specify." }), passed);
							
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.closeSession(session);		
		}
	}

	
	/**
	 * @testID testTreeStructure003
	 * @testDescription This method asserts if the dmt structure of the monitorable 
	 *                  is according to RFC-87 specify. If node exists and its stores the correct value.
	 */
	public void testTreeStructure003() {
		DmtSession session = null;
		try {
			tbc.log("#testTreeStructure003");
			session = tbc.getDmtAdmin().getSession(".");
			boolean passed = true;
			passed = session.isNodeUri(MonitorTestControl.DMT_URI_MONITORABLE2_SV1) && passed;
			
			passed = (session.getNodeValue(MonitorTestControl.DMT_URI_MONITORABLE2_SV1+"/CM").getString() == "CC") && passed;
			tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE,
					new String[] { "dmt structure of the monitorable is according to RFC-87 Specify." }), passed);
							
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.closeSession(session);		
		}

	}

	/**
	 * @testID testTreeStructure004
	 * @testDescription This method asserts if the dmt structure of the monitorable 
	 *                  is according to RFC-87 specify. If node exists and its stores the correct value.
	 */
	public void testTreeStructure004() {
		DmtSession session = null;
		try {
			tbc.log("#testTreeStructure004");
			session = tbc.getDmtAdmin().getSession(".");
			boolean passed = true;
			passed = session.isNodeUri(MonitorTestControl.DMT_URI_MONITORABLE2_SV2) && passed;
			
			passed = (session.getNodeValue(MonitorTestControl.DMT_URI_MONITORABLE2_SV2+"/CM").getString() == "DER") && passed;
			tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE,
					new String[] { "dmt structure of the monitorable is according to RFC-87 Specify." }), passed);
							
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.closeSession(session);		
		}

	}	

}

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
 * 24/05/2005  	Alexandre Santos
 * 38           Implement MEGTCK for the application RFC 
 * ===========  ==============================================================
 */
package org.osgi.test.cases.application.tbc.TreeStructure;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;

/**
 * @generalDescription This Test Class Validates the TreeStructure described
 *                     in RF-87.
 *                     
 */
public class TreeStructure {
	private ApplicationTestControl tbc;
	/**
	 * @param tbc
	 */
	public TreeStructure(ApplicationTestControl tbc) {
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
	 * @testDescription Tests nodes for each installed application
	 * 					according to RFC-87
	 *                  
	 */
	public void testTreeStructure001() {
		try {
			tbc.log("#testTreeStructure001");
			DmtSession session = tbc.getDmtAdmin().getSession(".");			
			
			boolean passed = false;
			passed = session.isNodeUri(ApplicationTestControl.APPS_URI);
			
			tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE, new String[] { "the node for installed applications already exist." }), passed);
			
			passed = session.isNodeUri(ApplicationTestControl.APPS_URI+"/"+ApplicationTestControl.TEST1_PID);
			
			tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE, new String[] { "the node for our installed application already exist." }), passed);
					
		} catch (DmtException e) {
			tbc.fail("error getting a session.");
		}
		
	}
	
	/**
	 * @testID testTreeStructure002
	 * @testDescription Tests all leaf nodes for an application installation 
	 *                  according to RFC-87.
	 */
	public void testTreeStructure002() {
		tbc.log("#testTreeStructure002");
		String str = null;
		boolean bool = false;
		DmtSession session = null;
			try {
				session = tbc.getDmtAdmin().getSession(".");
			} catch (DmtException e) {
				tbc.fail("error getting the session. DmtException: " + e.getMessage());
			}
			
			boolean passed = false;
			try {
				passed = session.isLeafNode(ApplicationTestControl.APPS_APP_NAME);
			} catch (DmtException e1) {
				tbc.fail("DmtException: " + e1.getMessage());
			}			
			tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE, new String[] { ApplicationTestControl.APPS_APP_NAME + "is a leaf." }), passed);
			
			try {
				str = session.getNodeValue(ApplicationTestControl.APPS_APP_NAME).getString();
			} catch (DmtException e2) {
				tbc.fail("DmtException: " + e2.getMessage());
			}
			
			tbc.assertEquals("Asserting if the application.name stored in dmt is correct.", ApplicationTestControl.TEST1_NAME_EN, str);
			
			try {
				passed = session.isLeafNode(ApplicationTestControl.APPS_APP_VERSION);
			} catch (DmtException e3) {
				tbc.fail("DmtException: " + e3.getMessage());
			}			
			tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE, new String[] {  ApplicationTestControl.APPS_APP_VERSION + "is a leaf." }), passed);
			
			try {
				str = session.getNodeValue(ApplicationTestControl.APPS_APP_VERSION).getString();
			} catch (DmtException e4) {
				tbc.fail("DmtException: " + e4.getMessage());
			}
			
			tbc.assertEquals("Asserting if the application.version stored in dmt is correct.", ApplicationTestControl.TEST1_VERSION, str);
			
			
			try {
				passed = session.isLeafNode(ApplicationTestControl.APPS_APP_VENDOR);
			} catch (DmtException e5) {
				tbc.fail("DmtException: " + e5.getMessage());
			}			
			tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE, new String[] {  ApplicationTestControl.APPS_APP_VENDOR + "is a leaf." }), passed);
			
			try {
				str = session.getNodeValue(ApplicationTestControl.APPS_APP_VENDOR).getString();
			} catch (DmtException e6) {
				tbc.fail("DmtException: " + e6.getMessage());
			}
			
			tbc.assertEquals("Asserting if the application.vendor stored in dmt is correct.", ApplicationTestControl.TEST1_VENDOR, str);
			
			
			try {
				passed = session.isLeafNode(ApplicationTestControl.APPS_APP_AUTOSTART);
			} catch (DmtException e7) {
				tbc.fail("DmtException: " + e7.getMessage());
			}			
			tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE, new String[] {  ApplicationTestControl.APPS_APP_AUTOSTART + "is a leaf." }), passed);
			
			try {
				bool = session.getNodeValue(ApplicationTestControl.APPS_APP_AUTOSTART).getBoolean();
			} catch (DmtException e8) {
				tbc.fail("DmtException: " + e8.getMessage());
			}
			
			tbc.assertTrue("Asserting if the application.autostart stored in dmt is correct.", bool);
			
			try {
				passed = session.isLeafNode(ApplicationTestControl.APPS_APP_LOCKED);
			} catch (DmtException e9) {
				tbc.fail("DmtException: " + e9.getMessage());
			}			
			tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE, new String[] {  ApplicationTestControl.APPS_APP_LOCKED + "is a leaf." }), passed);
			
			try {
				bool = session.getNodeValue(ApplicationTestControl.APPS_APP_LOCKED).getBoolean();
			} catch (DmtException e10) {
				tbc.fail("DmtException: " + e10.getMessage());
			}
			
			tbc.assertTrue("Asserting if the application.locked stored in dmt is correct.", !bool);	
			
			try {
				passed = session.isLeafNode(ApplicationTestControl.APPS_APP_SINGLETON);
			} catch (DmtException e11) {
				tbc.fail("DmtException: " + e11.getMessage());
			}			
			tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE, new String[] {  ApplicationTestControl.APPS_APP_SINGLETON.toString() + "is a leaf." }), passed);
			
			try {
				str = session.getNodeValue(ApplicationTestControl.APPS_APP_SINGLETON).getString();
			} catch (DmtException e12) {
				tbc.fail("DmtException: " + e12.getMessage());
			}
			
			tbc.assertEquals("Asserting if the application.singleton stored in dmt is correct.", ApplicationTestControl.TEST1_SINGLETON.toString(), str);				

			
			try {
				passed = session.isLeafNode(ApplicationTestControl.APPS_APP_BUNDLE_ID);
			} catch (DmtException e13) {
				tbc.fail("DmtException: " + e13.getMessage());
			}			
			tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE, new String[] {  ApplicationTestControl.APPS_APP_BUNDLE_ID + "is a leaf." }), passed);
			
			try {
				str = session.getNodeValue(ApplicationTestControl.APPS_APP_BUNDLE_ID).getString();
			} catch (DmtException e14) {
				tbc.fail("DmtException: " + e14.getMessage());
			}
			
			tbc.assertEquals("Asserting if the application.bundle_id stored in dmt is correct.", tbc.getBundleMeglet().getBundleId()+"", str);			
		
	}	
	
	/**
	 * @testID testTreeStructure003
	 * @testDescription Tests the interior nodes for application instances
	 *                  according to RFC-87.
	 */
	public void testTreeStructure003() {
		try {
			tbc.log("#testTreeStructure003");
			DmtSession session = tbc.getDmtAdmin().getSession(".");
			boolean passed = false;
			passed = session.isNodeUri(ApplicationTestControl.APPS_INSTANCE_URI);
			
			tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE, new String[] { "the node for the launched applications already exist." }), passed);
			
			passed = session.isNodeUri(ApplicationTestControl.APPS_INSTANCE_URI+"/"+tbc.getAppHandle().getInstanceID());
			
			tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE, new String[] { "the node for our launched application already exist." }), passed);
					
		} catch (DmtException e) {
			tbc.fail("error getting a session.");
		}
		
	}	
	
	/**
	 * @testID testTreeStructure004
	 * @testDescription Tests all leaf nodes for application instances
	 *                  according to RFC-87.
	 */
	public void testTreeStructure004() {
		try {
			tbc.log("#testTreeStructure004");
			DmtSession session = tbc.getDmtAdmin().getSession(".");
			
			boolean passed = false;
			
			passed = session.isLeafNode(ApplicationTestControl.APPS_INSTANCE_URI + "/" + 
						tbc.getAppHandle().getInstanceID()  + ApplicationTestControl.APPS_INSTANCE_STATE);			
			tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE, new String[] { ApplicationTestControl.APPS_INSTANCE_URI + "/" + 
					tbc.getAppHandle().getInstanceID()  + ApplicationTestControl.APPS_INSTANCE_STATE + "is a leaf." }), passed);
			
			String str = session.getNodeValue(ApplicationTestControl.APPS_INSTANCE_URI + "/" + 
					tbc.getAppHandle().getInstanceID()  + ApplicationTestControl.APPS_INSTANCE_STATE).toString();
			
			tbc.assertEquals("Asserting if the state of the handle stored in dmt is correct.", tbc.getAppHandle().getState(), str);
			
			passed = session.isLeafNode(ApplicationTestControl.APPS_INSTANCE_URI + "/" + 
					tbc.getAppHandle().getInstanceID()  + ApplicationTestControl.APPS_INSTANCE_TYPE);			
			tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE, new String[] { ApplicationTestControl.APPS_INSTANCE_URI + "/" + 
					tbc.getAppHandle().getInstanceID()  + ApplicationTestControl.APPS_INSTANCE_TYPE + "is a leaf." }), passed);
			
			str = session.getNodeValue(ApplicationTestControl.APPS_INSTANCE_URI + "/" + 
					tbc.getAppHandle().getInstanceID()  + ApplicationTestControl.APPS_INSTANCE_TYPE).toString();
			
			tbc.assertEquals("Asserting if the instanceID of the handle stored in dmt is correct.", ApplicationTestControl.TEST1_PID, str);					
			
		} catch (DmtException e) {
			tbc.fail("error getting a session.");
		}
		
	}		
	
}

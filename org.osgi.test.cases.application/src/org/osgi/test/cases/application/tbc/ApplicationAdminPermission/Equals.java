/*
 *  Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
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
 * 13/10/2005   Alexandre Santos
 * 153          Implement OAT test cases  
 * ===========  ==============================================================
 */

package org.osgi.test.cases.application.tbc.ApplicationAdminPermission;

import org.osgi.test.cases.application.tbc.ApplicationConstants;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;

/**
 * @author Alexandre Santos
 * 
 * This test class validates the implementation of
 * <code>equals</code> method, according to MEG reference
 * documentation.
 */
public class Equals {
	private ApplicationTestControl tbc;

	/**
	 * @param tbc
	 */
	public Equals(ApplicationTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testEquals001();
		testEquals002();
		testEquals003();
		testEquals004();
		testEquals005();
	}

    /**
     * This method asserts if two equals ApplicationAdminPermission returns
     * true to the equals method.
     * 
     * @spec ApplicationAdminPermission.equals(Object)
     */         
	private void testEquals001() {
		try {
			tbc.log("#testEquals001");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationConstants.ACTIONS);
			org.osgi.service.application.ApplicationAdminPermission app2 = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationConstants.ACTIONS);
			tbc.assertTrue("Asserting if two equals permissions returns true to the equals method.", app.equals(app2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}
	
    /**
     * This method asserts if two different ApplicationAdminPermission returns
     * false to the equals method.
     * 
     * @spec ApplicationAdminPermission.equals(Object)
     */         
	private void testEquals002() {
		try {
			tbc.log("#testEquals002");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationConstants.ACTIONS);
			org.osgi.service.application.ApplicationAdminPermission app2 = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER2, ApplicationConstants.ACTIONS);
			tbc.assertTrue("Asserting if two different permissions returns false to the equals method.", !app.equals(app2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}
	
    /**
     * This method asserts if two equals ApplicationAdminPermission returns
     * true in an equals call using the actions in a different order.
     * 
     * @spec ApplicationAdminPermission.equals(Object)
     */         
	private void testEquals003() {
		try {
			tbc.log("#testEquals003");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationConstants.ACTIONS);
			org.osgi.service.application.ApplicationAdminPermission app2 = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationConstants.ACTIONS_DIFFERENT_ORDER);
			tbc.assertTrue("Asserting if two equals permissions returns true even if the actions are in a different order.", app.equals(app2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}	
	
    /**
     * This method asserts if two equals ApplicationAdminPermission with different pid
     * returns false in an equals call.
     * 
     * @spec ApplicationAdminPermission.equals(Object)
     */         
	private void testEquals004() {
		try {
			tbc.log("#testEquals004");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationConstants.ACTIONS);
			org.osgi.service.application.ApplicationAdminPermission app2 = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER_DIFFERENT_PID, ApplicationConstants.ACTIONS);
			tbc.assertTrue("Asserting if two equals permissions with different pid returns false in an equals call.", !app.equals(app2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}	
	
    /**
     * This method asserts if two equals ApplicationAdminPermission with different signer
     * returns false in an equals call.
     * 
     * @spec ApplicationAdminPermission.equals(Object)
     */         
	private void testEquals005() {
		try {
			tbc.log("#testEquals005");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationConstants.ACTIONS);
			org.osgi.service.application.ApplicationAdminPermission app2 = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER_DIFFERENT_SIGNER, ApplicationConstants.ACTIONS);
			tbc.assertTrue("Asserting if two equals permissions with different signer returns false in an equals call.", !app.equals(app2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}	
	
}

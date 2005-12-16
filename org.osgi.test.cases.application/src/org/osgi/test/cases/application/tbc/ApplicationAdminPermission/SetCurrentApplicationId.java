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
 * <code>setCurrentApplicationId</code> method, according to MEG reference
 * documentation.
 */
public class SetCurrentApplicationId {
	
	private ApplicationTestControl tbc;

	/**
	 * @param tbc
	 */
	public SetCurrentApplicationId(ApplicationTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testSetCurrentApplicationId001();
		testSetCurrentApplicationId002();
		testSetCurrentApplicationId003();
		testSetCurrentApplicationId004();
	}

    /**
     * This method asserts that no exceptions is thrown when
     * null is passed as parameter.
     * 
     * @spec ApplicationAdminPermission.setCurrentApplicationId(String)
     */         
	private void testSetCurrentApplicationId001() {
		try {
			tbc.log("#testSetCurrentApplicationId001");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationConstants.ACTIONS);
			app.setCurrentApplicationId(null);
			tbc.pass("No exception was thrown.");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}
	
    /**
     * This method asserts if no exception is thrown when
     * invalid characters is passed as parameter.
     * 
     * @spec ApplicationAdminPermission.setCurrentApplicationId(String)
     */         
	private void testSetCurrentApplicationId002() {
		try {
			tbc.log("#testSetCurrentApplicationId002");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationConstants.ACTIONS);
			app.setCurrentApplicationId(ApplicationConstants.INVALID);
			tbc.pass("No exception was thrown.");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}
	
    /**
     * This method asserts that no exceptions is thrown when
     * null is passed as parameter using the constructor that receives
     * an ApplicationDescriptor as parameter.
     * 
     * @spec ApplicationAdminPermission.setCurrentApplicationId(String)
     */         
	private void testSetCurrentApplicationId003() {
		try {
			tbc.log("#testSetCurrentApplicationId001");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					tbc.getAppDescriptor(), ApplicationConstants.ACTIONS);
			app.setCurrentApplicationId(null);
			tbc.pass("No exception was thrown.");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}
	
    /**
     * This method asserts if no exception is thrown when
     * invalid characters is passed as parameter using the 
     * constructor that receives an ApplicationDescriptor as parameter.
     * 
     * @spec ApplicationAdminPermission.setCurrentApplicationId(String)
     */         
	private void testSetCurrentApplicationId004() {
		try {
			tbc.log("#testSetCurrentApplicationId002");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					tbc.getAppDescriptor(), ApplicationConstants.ACTIONS);
			app.setCurrentApplicationId(ApplicationConstants.INVALID);
			tbc.pass("No exception was thrown.");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}	
	

}

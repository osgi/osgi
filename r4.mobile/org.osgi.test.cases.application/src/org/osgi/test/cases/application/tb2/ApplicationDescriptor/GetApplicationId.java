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
 * 14/09/2005   Alexandre Santos
 * 153          Implement OAT test cases  
 * ===========  ==============================================================
 */
package org.osgi.test.cases.application.tb2.ApplicationDescriptor;

import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.TestInterface;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;

/**
 * @author Alexandre Alves
 * 
 * This Test Class Validates the implementation of
 * <code>getApplicationId</code> method, according to MEG reference
 * documentation.
 */
public class GetApplicationId implements TestInterface {
	private ApplicationTestControl tbc;

	public GetApplicationId(ApplicationTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetApplicationId001();
		testGetApplicationId002();
		testGetApplicationId003();
	}

	/**
	 * This method asserts that no exception is thrown
	 * when we try to get the application id.
	 * 
	 * @spec ApplicationDescriptor.getApplicationId()
	 */
	private void testGetApplicationId001() {
		tbc.log("#testGetApplicationId001");
		PermissionInfo[] infos = null;		
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());			
			tbc.setDefaultPermission();
			tbc.getAppDescriptor().getApplicationId();
			tbc.pass("Asserting that no exception was thrown.");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.cleanUp(infos);
		}
	}
	
	/**
	 * This method asserts that the getApplicationId returns
	 * a value equal to SERVICE_PID value.
	 * 
	 * @spec ApplicationDescriptor.getApplicationId()
	 */
	private void testGetApplicationId002() {
		PermissionInfo[] infos = null;
		tbc.log("#testGetApplicationId002");
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());
			tbc.setDefaultPermission();
			String value = tbc.getAppDescriptor().getApplicationId();
			tbc.assertEquals("Asserting if the getApplicationId returns a value equal to SERVICE_PID value.", tbc.getServicePid(), value);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.cleanUp(infos);
		}
	}	
	
	/**
	 * This method asserts that even if the application descriptor is unregistered
	 * no exception will be thrown.
	 * 
	 * @spec ApplicationDescriptor.getApplicationId()
	 */
	private void testGetApplicationId003() {
		PermissionInfo[] infos = null;		
		try {
			tbc.log("#testGetApplicationId003");
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());			
			tbc.setDefaultPermission();
			tbc.unregisterDescriptor();
			tbc.getAppDescriptor().getApplicationId();
			tbc.pass("No exception was thrown.");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.installDescriptor();
			tbc.cleanUp(infos);
		}
	}	

}

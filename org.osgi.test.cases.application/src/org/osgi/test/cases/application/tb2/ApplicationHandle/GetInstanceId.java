/*
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved. Implementation
 * of certain elements of the OSGi Specification may be subject to third party
 * intellectual property rights, including without limitation, patent rights
 * (such a third party may or may not be a member of the OSGi Alliance). The
 * OSGi Alliance is not responsible and shall not be held responsible in any
 * manner for identifying or failing to identify any or all such third party
 * intellectual property rights. This document and the information contained
 * herein are provided on an "AS IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL
 * WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO ANY WARRANTY
 * THAT THE USE OF THE INFORMATION HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY
 * IMPLIED WARRANTIES OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN
 * NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF
 * BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT,
 * INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL
 * DAMAGES OF ANY KIND IN CONNECTION WITH THIS DOCUMENT OR THE INFORMATION
 * CONTAINED HEREIN, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
/*
 * REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 24/08/2005   Alexandre Santos
 * 153          Implement OAT test cases  
 * ===========  ==============================================================
 */

package org.osgi.test.cases.application.tb2.ApplicationHandle;

import org.osgi.service.application.ApplicationAdminPermission;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.application.tbc.ApplicationConstants;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.TestInterface;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;

/**
 * @author Alexandre Alves
 * 
 * This Test Class Validates the implementation of
 * <code>getInstanceId</code> method, according to MEG reference
 * documentation.
 */
public class GetInstanceId implements TestInterface {

	private ApplicationTestControl	tbc;

	public GetInstanceId(ApplicationTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetInstanceId001();
		testGetInstanceId002();
	}

	/**
	 * This method asserts if the instanceID is equals to Application PID.
	 * 
	 * @spec ApplicationHandle.getInstanceId()
	 */
	public void testGetInstanceId001() {
		tbc.log("#testGetInstanceID001");
		ApplicationHandle handle = null;
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
				tbc.getTb2Location());
			
			tbc.setLocalPermission(new PermissionInfo(
				ApplicationAdminPermission.class.getName(),
				ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.LIFECYCLE_ACTION));	

			handle = tbc.getAppDescriptor().launch(null);
			
			tbc.setDefaultPermission();
			
			tbc
				.assertTrue("Asserting if the instanceID is the same used in xml.",
					handle.getInstanceId().indexOf(ApplicationConstants.TEST_PID) != -1);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + " " + e.getClass().getName());			
		} finally {
			tbc.cleanUp(handle, infos);
		}
	}

	/**
	 * This method asserts that if the ApplicationHandle is unregistered
	 * no exception will be thrown.
	 * 
	 * @spec ApplicationHandle.getInstanceId()
	 */
	public void testGetInstanceId002() {		
		ApplicationHandle handle = null;
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(
					new PermissionInfo(ApplicationAdminPermission.class.getName(), ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.LIFECYCLE_ACTION)
			);		
				
			handle = tbc.getAppDescriptor().launch(null);
			
			handle.destroy();
			
			tbc
			.assertTrue("Asserting if the instanceID is the same used in xml.",
				handle.getInstanceId().indexOf(ApplicationConstants.TEST_PID) != -1);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + " " + e.getClass().getName());			
		} finally {
			tbc.cleanUp(handle, infos);
		}
	}
}

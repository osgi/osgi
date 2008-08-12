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
 * 25/08/2005   Alexandre Santos
 * 153          Implement OAT test cases  
 * ===========  ==============================================================
 */
package org.osgi.test.cases.application.tb2.ScheduledApplication;

import org.osgi.service.application.ApplicationAdminPermission;
import org.osgi.service.application.ScheduledApplication;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.application.tbc.ApplicationConstants;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.TestInterface;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;

/**
 * @author Alexandre Alves
 *  
 * This Test Class Validates the implementation of
 * <code>isRecurring</code> method, according to MEG reference
 * documentation.
 */
public class IsRecurring implements TestInterface {
	private ApplicationTestControl tbc;

	public IsRecurring(ApplicationTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testIsRecurring001();
		testIsRecurring002();
		testIsRecurring003();
	}

    /**
     * This method asserts if the isRecurring method returns false when an
     * ApplicationScheduled was not started in recurring mode.
     * 
     * @spec ScheduleApplication.isRecurring()
     */       
	public void testIsRecurring001() {
		tbc.log("#testIsRecurring001");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

            tbc.setLocalPermission(
                new PermissionInfo(ApplicationAdminPermission.class.getName(), ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.SCHEDULE_ACTION)
            );

			sa = tbc.getAppDescriptor().schedule(null, null, "TestingPurposes*", null, false);

			tbc.setDefaultPermission();
			
			tbc.assertTrue("Asserting if isRecurring method returns false", !sa
					.isRecurring());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.cleanUp(sa, infos);
		}
	}

    /**
     * This method asserts if the isRecurring method returns true when an
     * ApplicationScheduled was started in recurring mode.
     * 
     * @spec ScheduleApplication.isRecurring()
     */           
	public void testIsRecurring002() {
		tbc.log("#testIsRecurring002");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

            tbc.setLocalPermission(
                new PermissionInfo(ApplicationAdminPermission.class.getName(), ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.SCHEDULE_ACTION)
            );

            sa = tbc.getAppDescriptor().schedule(null, null, "TestingPurposes*", null, true);
			
			tbc.setDefaultPermission();
			
			tbc.assertTrue("Asserting if isRecurring method returns true", sa
					.isRecurring());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.cleanUp(sa, infos);
		}
	}

    /**
     * This method asserts that if the ScheduledApplication is unregistered
     * IllegalStateException will be thrown.
     * 
     * @spec ScheduleApplication.isRecurring()
     */      
	public void testIsRecurring003() {
		tbc.log("#testIsRecurring003");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

            tbc.setLocalPermission(
                new PermissionInfo(ApplicationAdminPermission.class.getName(), ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.SCHEDULE_ACTION)
            );

			sa = tbc.getAppDescriptor().schedule(null, null, "*", null, false);

			sa.remove();
			
			sa.isRecurring();

			tbc.failException("", IllegalStateException.class);
		} catch (IllegalStateException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalStateException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalStateException.class.getName(),
							e.getClass().getName() }));
		} finally {
			tbc.cleanUp(sa, infos);
		}
	}
	
}

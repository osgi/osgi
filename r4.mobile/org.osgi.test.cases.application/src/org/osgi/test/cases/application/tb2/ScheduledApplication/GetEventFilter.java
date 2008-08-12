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
 * <code>getEventFilter</code> method, according to MEG reference
 * documentation.
 */
public class GetEventFilter implements TestInterface {
	private ApplicationTestControl tbc;

	public GetEventFilter(ApplicationTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetEventFilter001();
		testGetEventFilter002();
		testGetEventFilter003();
	}

    /**
     * This method asserts if null is returned when we
     * have passed null as event filter parameter.
     * 
     * @spec ScheduleApplication.getEventFilter()
     */    
	public void testGetEventFilter001() {
		tbc.log("#testGetEventFilter001");
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
        
        tbc
                .assertNull(
                        "Asserts if the arguments is correctly returned.",
                        sa.getEventFilter());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.cleanUp(sa, infos);
		}
	}

    /**
     * This method asserts if filter passed as parameter
     * is returned.
     * 
     * @spec ScheduleApplication.getEventFilter()
     */    
	public void testGetEventFilter002() {
		tbc.log("#testGetEventFilter002");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
            infos = tbc.getPermissionAdmin().getPermissions(
                tbc.getTb2Location());

        tbc.setLocalPermission(
            new PermissionInfo(ApplicationAdminPermission.class.getName(), ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.SCHEDULE_ACTION)
        );

        sa = tbc.getAppDescriptor().schedule(null, null, ApplicationConstants.TIMER_EVENT, ApplicationConstants.EVENT_FILTER, true);

        tbc.setDefaultPermission();
        
		tbc.assertEquals("Asserting if the filter passed as parameter is returned.", ApplicationConstants.EVENT_FILTER, sa.getEventFilter());
        
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
     * @spec ScheduleApplication.getEventFilter()
     */     
    public void testGetEventFilter003() {
        tbc.log("#testGetEventFilter003");
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
            
            sa.getEventFilter();

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

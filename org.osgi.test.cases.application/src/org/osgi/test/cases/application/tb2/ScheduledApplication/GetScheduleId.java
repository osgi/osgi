package org.osgi.test.cases.application.tb2.ScheduledApplication;

import org.osgi.service.application.ApplicationAdminPermission;
import org.osgi.service.application.ScheduledApplication;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.application.tbc.ApplicationConstants;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.TestInterface;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;

public class GetScheduleId implements TestInterface {
	private ApplicationTestControl tbc;

	public GetScheduleId(ApplicationTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
    //cleanup of registered schedules
    ScheduledApplication sa = tbc.getScheduledApplication();
    while (sa != null) {
      sa.remove();
      sa = tbc.getScheduledApplication();
    }

		testGetScheduleId001();	
		testGetScheduleId002();
		testGetScheduleId003();
	}
	
    /**
     * This method asserts if a valid id is returned 
     * when we have passed null as id.
     * 
     * @spec ScheduleApplication.getScheduleId()
     */    
	private void testGetScheduleId001() {
		tbc.log("#testGetScheduleId001");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
            infos = tbc.getPermissionAdmin().getPermissions(tbc.getTb2Location());

            tbc.setLocalPermission(new PermissionInfo(ApplicationAdminPermission.class.getName(), 
        		                       ApplicationConstants.APPLICATION_PERMISSION_FILTER1, 
        		                       ApplicationAdminPermission.SCHEDULE_ACTION));

            sa = tbc.getAppDescriptor().schedule(null, null, "TestingPurposes*", null, true);
            tbc.setDefaultPermission();        
            tbc.assertNotNull("This method asserts if a valid id is returned when we have passed null as id.",
            			sa.getScheduleId());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.cleanUp(sa, infos);
		}
	}
	
    /**
     * This method asserts if the id passed as parameter
     * is returned correctly.
     * 
     * @spec ScheduleApplication.getScheduleId()
     */    
	private void testGetScheduleId002() {
		tbc.log("#testGetScheduleId002");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		String id = "scheduleId";
		try {
            infos = tbc.getPermissionAdmin().getPermissions(tbc.getTb2Location());

            tbc.setLocalPermission(new PermissionInfo(ApplicationAdminPermission.class.getName(), 
        		                       ApplicationConstants.APPLICATION_PERMISSION_FILTER1, 
        		                       ApplicationAdminPermission.SCHEDULE_ACTION));
            
            sa = tbc.getAppDescriptor().schedule(id, null, "TestingPurposes*", null, true);
            tbc.setDefaultPermission();        
            tbc.assertEquals("This method asserts if the passed id is returned correctly by getScheduledId().",
            		id, sa.getScheduleId());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.cleanUp(sa, infos);
		}
	}
	
    /**
     * This method asserts that if the ScheduledApplication is unregistered
     * no exception is thrown.
     * 
     * @spec ScheduleApplication.getScheduleId()
     */     
    public void testGetScheduleId003() {
        tbc.log("#testGetScheduleId003");
        PermissionInfo[] infos = null;
        ScheduledApplication sa = null;
        try {
            infos = tbc.getPermissionAdmin().getPermissions(
                    tbc.getTb2Location());

            tbc.setLocalPermission(
                new PermissionInfo(ApplicationAdminPermission.class.getName(), ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.SCHEDULE_ACTION)
            );

            sa = tbc.getAppDescriptor().schedule(null, null, "TestingPurposes*", null, true);

            sa.remove();
            
            sa.getScheduleId();

            tbc.pass("No exception was thrown.");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
        } finally {
            tbc.cleanUp(sa, infos);
        }
    }

}

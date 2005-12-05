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
 * 23/02/2005   Leonardo Barros
 * 14           Implement MEG TCK
 * ===========  ==============================================================
 * 23/03/2005   Eduardo Oliveira
 * 14           Updates after formal inspection (JSTD-MEGTCK-CODE-INSP011)
 * ===========  ==============================================================
 * 24/03/2005   Alexandre Alves
 * 14           Updates after formal inspection (JSTD-MEGTCK-CODE-INSP011)
 * ===========  ==============================================================
 * May 20, 2005	Alexandre Alves
 * 92           Make changes according to monitor RFC updates
 * ===========  ==============================================================
 **/
package org.osgi.test.cases.monitor.tb1.MonitorAdmin;

import org.osgi.service.monitor.MonitoringJob;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.monitor.tbc.MonitorConstants;
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.TestInterface;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @author Alexandre Alves
 * 
 * This Test Class Validates the implementation of
 * <code>getRunningJobs</code> method, according to MEG reference
 * documentation.
 */
public class GetRunningJobs implements TestInterface {
	private MonitorTestControl tbc;

	public GetRunningJobs(MonitorTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetRunningJobs001();
		testGetRunningJobs002();
		testGetRunningJobs003();
		testGetRunningJobs004();
		testGetRunningJobs005();
		testGetRunningJobs006();
	}

	/**
	 * This method asserts if when we stop all running monitoring jobs
	 * and start a change based job, this method returns only one Monitoring Job.
	 * 
	 * @spec MonitorAdmin.getRunningJobs()
	 */
	private void testGetRunningJobs001() {
		tbc.log("#testGetRunningJobs001");
		MonitoringJob mj = null;
		MonitoringJob[] mjs = null;
		PermissionInfo[] infos = null;		
		try {
						
			
			tbc.stopRunningJobs();
			
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());
			
			tbc.setLocalPermission(new PermissionInfo[] {
					new PermissionInfo(org.osgi.service.monitor.MonitorPermission.class.getName(), MonitorConstants.SVS[0], org.osgi.service.monitor.MonitorPermission.STARTJOB),	
					new PermissionInfo(org.osgi.service.monitor.MonitorPermission.class.getName(), MonitorConstants.SVS[1], org.osgi.service.monitor.MonitorPermission.STARTJOB)
			});

			mj = tbc.getMonitorAdmin().startJob(MonitorConstants.INITIATOR,
					MonitorConstants.SVS, MonitorConstants.COUNT);

			mjs = tbc.getMonitorAdmin().getRunningJobs();
		
			tbc.assertNotNull("Asserting if a non-null value is returned by getRunningJobs().", mjs);
			
			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "number of running jobs", 1+"" }),
					1, mjs.length);
			
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[] { "the returned MonitoringJob" }), mjs[0]);
			
			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "initiator", MonitorConstants.INITIATOR }),
					MonitorConstants.INITIATOR, mjs[0].getInitiator());

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "length of the statusvariable names", MonitorConstants.SV_LENGTH+"" }),
					MonitorConstants.SV_LENGTH, mjs[0].getStatusVariableNames().length);

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "first statusvariable name", MonitorConstants.SVS[0] }),					
					MonitorConstants.SVS[0], mjs[0].getStatusVariableNames()[0]);

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "second statusvariable name", MonitorConstants.SVS[1] }),
					MonitorConstants.SVS[1], mjs[0].getStatusVariableNames()[1]);

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "count", MonitorConstants.COUNT+""
							 }), MonitorConstants.COUNT, mjs[0].getReportCount());			
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			mj.stop();		
			tbc.setTb1Permission(infos);
		}
	}

	/**
	 * This method asserts if when we stop all running monitoring jobs
	 * and start a time based job, this method returns only one Monitoring Job.
	 * 
	 * @spec MonitorAdmin.getRunningJobs()
	 */
	private void testGetRunningJobs002() {
		tbc.log("#testGetRunningJobs002");
		MonitoringJob mj = null;
		MonitoringJob[] mjs = null;
		PermissionInfo[] infos = null;	
		try {
			tbc.stopRunningJobs();
			
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());			

			tbc.setLocalPermission(new PermissionInfo[] {
					new PermissionInfo(org.osgi.service.monitor.MonitorPermission.class.getName(), MonitorConstants.SVS[0], org.osgi.service.monitor.MonitorPermission.STARTJOB),	
					new PermissionInfo(org.osgi.service.monitor.MonitorPermission.class.getName(), MonitorConstants.SVS[1], org.osgi.service.monitor.MonitorPermission.STARTJOB)
			});
			
			
			mj = tbc.getMonitorAdmin().startScheduledJob(
					MonitorConstants.INITIATOR, MonitorConstants.SVS,
					MonitorConstants.SCHEDULE, MonitorConstants.COUNT);


			mjs = tbc.getMonitorAdmin().getRunningJobs();
		
			tbc.assertNotNull("Asserting if a non-null value is returned by getRunningJobs().", mjs);
			
			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "number of running jobs", 1+""}),
					1, mjs.length);
			
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[] { "the returned MonitoringJob" }), mjs[0]);
			
			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "initiator", MonitorConstants.INITIATOR }),
					MonitorConstants.INITIATOR, mjs[0].getInitiator());

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "length of statusvariable names", MonitorConstants.SV_LENGTH+"" }),
					MonitorConstants.SV_LENGTH, mjs[0].getStatusVariableNames().length);

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "first statusvariable name", MonitorConstants.SVS[0] }),					
					MonitorConstants.SVS[0], mjs[0].getStatusVariableNames()[0]);

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "second statusvariable name", MonitorConstants.SVS[1] }),
					MonitorConstants.SVS[1], mjs[0].getStatusVariableNames()[1]);

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "schedule", MonitorConstants.SCHEDULE+"" }), MonitorConstants.SCHEDULE, mjs[0].getSchedule());
			
			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "count", MonitorConstants.COUNT+"" }),
					MonitorConstants.COUNT, mjs[0].getReportCount());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.setTb1Permission(infos);
			mj.stop();
		}
	}

	/**
	 * This method asserts if when we stop all running monitoring jobs
	 * and start a time based job and a change based job, this method returns two Monitoring Job.
	 * 
	 * @spec MonitorAdmin.getRunningJobs()
	 */
	private void testGetRunningJobs003() {
		tbc.log("#testGetRunningJobs003");
		MonitoringJob mj = null;
		MonitoringJob mj2 = null;
		MonitoringJob[] mjs = null;
		PermissionInfo[] infos = null;	
		try {	
			tbc.stopRunningJobs();
			
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());			

			tbc.setLocalPermission(new PermissionInfo[] {
					new PermissionInfo(org.osgi.service.monitor.MonitorPermission.class.getName(), MonitorConstants.SVS[0], org.osgi.service.monitor.MonitorPermission.STARTJOB),	
					new PermissionInfo(org.osgi.service.monitor.MonitorPermission.class.getName(), MonitorConstants.SVS[1], org.osgi.service.monitor.MonitorPermission.STARTJOB)
			});		

			mj = tbc.getMonitorAdmin().startScheduledJob(
					MonitorConstants.INITIATOR, MonitorConstants.SVS,
					MonitorConstants.SCHEDULE, MonitorConstants.COUNT);

			mj2 = tbc.getMonitorAdmin().startJob(MonitorConstants.INITIATOR,
					MonitorConstants.SVS, MonitorConstants.COUNT);

			mjs = tbc.getMonitorAdmin().getRunningJobs();
			
			tbc.assertNotNull("Asserting if a non-null value is returned by getRunningJobs().", mjs);		

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "number of running jobs", 2+"" }),
					2, mjs.length);
			
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[] { "the returned MonitoringJob" }), mjs[0]);
			
			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "initiator", MonitorConstants.INITIATOR }),
					MonitorConstants.INITIATOR, mjs[0].getInitiator());

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "length of statusvariable names", MonitorConstants.SV_LENGTH+"" }),	MonitorConstants.SV_LENGTH, mjs[0].getStatusVariableNames().length);

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "first statusvariable name", MonitorConstants.SVS[0] }),					
					MonitorConstants.SVS[0], mjs[0].getStatusVariableNames()[0]);

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "second statusvariable name", MonitorConstants.SVS[1] }),
					MonitorConstants.SVS[1], mjs[0].getStatusVariableNames()[1]);

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "schedule", MonitorConstants.SCHEDULE+"" }),
					MonitorConstants.SCHEDULE, mjs[0].getSchedule());
			
			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "count", MonitorConstants.COUNT+"" }),
					MonitorConstants.COUNT, mjs[0].getReportCount());			
			

			
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[] { "the returned MonitoringJob" }), mjs[1]);
			
			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "initiator", MonitorConstants.INITIATOR }),
					MonitorConstants.INITIATOR, mjs[1].getInitiator());

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "length of statusvariable names", MonitorConstants.SV_LENGTH+""}),
					MonitorConstants.SV_LENGTH, mjs[1].getStatusVariableNames().length);

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "first statusvariable name", MonitorConstants.SVS[0] }),					
					MonitorConstants.SVS[0], mjs[1].getStatusVariableNames()[0]);

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "second statusvariable name", MonitorConstants.SVS[1] }),
					MonitorConstants.SVS[1], mjs[1].getStatusVariableNames()[1]);

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "count", MonitorConstants.COUNT+"" }),
					MonitorConstants.COUNT, mjs[1].getReportCount());	
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {			
			tbc.setTb1Permission(infos);	
			mj.stop();
			mj2.stop();
		}
	}
	
	/**
	 * This method asserts that an empty array is returned if 
	 * there are no running jobs at the time of the call.
	 * 
	 * @spec MonitorAdmin.getRunningJobs()
	 */
	private void testGetRunningJobs004() {
		tbc.log("#testGetRunningJobs004");
		MonitoringJob[] mjs = null;
		PermissionInfo[] infos = null;		
		try {						
			
			tbc.stopRunningJobs();
			
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());
			
			mjs = tbc.getMonitorAdmin().getRunningJobs();
			
			tbc.assertNotNull("Asserting if a non-null value is returned by getRunningJobs().", mjs);
				
			tbc.assertTrue("Asserting if an empty array is returned when no MonitoringJob is running",
					(mjs.length==0));
					
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.setTb1Permission(infos);				
		}
	}
	
	/**
	 * This method asserts if the caller does not have
	 * MonitorPermission with the proper startjob action for all
	 * the Status Variables monitored by a job, then that job
	 * will be silently omitted from the results. This method uses
	 * a change based job.
	 * 
	 * @spec MonitorAdmin.getRunningJobs()
	 */
	private void testGetRunningJobs005() {
		tbc.log("#testGetRunningJobs005");
		MonitoringJob mj = null;
		MonitoringJob[] mjs = null;
		PermissionInfo[] infos = null;		
		try {									
			tbc.stopRunningJobs();
			
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());
			
			tbc.setLocalPermission(new PermissionInfo[] {					
					new PermissionInfo(org.osgi.service.monitor.MonitorPermission.class.getName(), MonitorConstants.SVS[1], org.osgi.service.monitor.MonitorPermission.STARTJOB),
					new PermissionInfo(org.osgi.service.monitor.MonitorPermission.class.getName(), MonitorConstants.SVS[0], org.osgi.service.monitor.MonitorPermission.STARTJOB)
			});

			mj = tbc.getMonitorAdmin().startJob(MonitorConstants.INITIATOR,
					MonitorConstants.SVS, MonitorConstants.COUNT);

			tbc.setLocalPermission(new PermissionInfo[] {					
					new PermissionInfo(org.osgi.service.monitor.MonitorPermission.class.getName(), MonitorConstants.SVS[0], org.osgi.service.monitor.MonitorPermission.STARTJOB)
			});		
			
			mjs = tbc.getMonitorAdmin().getRunningJobs();
		
			tbc.assertNotNull("Asserting if a non-null value is returned by getRunningJobs().", mjs);					
			
			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "number of running jobs", 0+"" }),
					0, mjs.length);
						
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			mj.stop();		
			tbc.setTb1Permission(infos);
		}
	}
	
	/**
	 * This method asserts if the caller does not have
	 * MonitorPermission with the proper startjob action for all
	 * the Status Variables monitored by a job, then that job
	 * will be silently omitted from the results. This method uses
	 * a time based job.
	 * 
	 * @spec MonitorAdmin.getRunningJobs()
	 */
	private void testGetRunningJobs006() {
		tbc.log("#testGetRunningJobs006");
		MonitoringJob mj = null;
		MonitoringJob[] mjs = null;
		PermissionInfo[] infos = null;	
		try {
			tbc.stopRunningJobs();
			
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());			

			tbc.setLocalPermission(new PermissionInfo[] {					
					new PermissionInfo(org.osgi.service.monitor.MonitorPermission.class.getName(), MonitorConstants.SVS[1], org.osgi.service.monitor.MonitorPermission.STARTJOB),
					new PermissionInfo(org.osgi.service.monitor.MonitorPermission.class.getName(), MonitorConstants.SVS[0], org.osgi.service.monitor.MonitorPermission.STARTJOB)
			});		
			
			mj = tbc.getMonitorAdmin().startScheduledJob(
					MonitorConstants.INITIATOR, MonitorConstants.SVS,
					MonitorConstants.SCHEDULE, MonitorConstants.COUNT);

			tbc.setLocalPermission(new PermissionInfo[] {
					new PermissionInfo(org.osgi.service.monitor.MonitorPermission.class.getName(), MonitorConstants.SVS[1], org.osgi.service.monitor.MonitorPermission.STARTJOB)
			});		

			mjs = tbc.getMonitorAdmin().getRunningJobs();
		
			tbc.assertNotNull("Asserting if a non-null value is returned by getRunningJobs().", mjs);
			
			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "number of running jobs", 0+""}),
					0, mjs.length);
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.setTb1Permission(infos);
			mj.stop();
		}
	}
	

}

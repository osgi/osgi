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
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.TestInterface;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @methodUnderTest org.osgi.service.monitor.MonitorAdmin#getRunningJobs
 * @generalDescription This class tests getRunningJobs method according with MEG
 *                     specification (rfc0084)
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
	}

	/**
	 * @testID testGetRunningJobs001
	 * @testDescription Tests if the correct list of running jobs is returned.
	 */
	public void testGetRunningJobs001() {
		tbc.log("#testGetRunningJobs001");
		MonitoringJob mj = null;
		MonitoringJob[] mjs = null;
		PermissionInfo[] infos = null;		
		try {
						
			
			tbc.stopRunningJobs();
			
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());
			
			tbc.setLocalPermission(new PermissionInfo[] {
					new PermissionInfo(org.osgi.service.monitor.MonitorPermission.class.getName(), MonitorTestControl.SVS[0], org.osgi.service.monitor.MonitorPermission.STARTJOB),	
					new PermissionInfo(org.osgi.service.monitor.MonitorPermission.class.getName(), MonitorTestControl.SVS[1], org.osgi.service.monitor.MonitorPermission.STARTJOB)
			});

			mj = tbc.getMonitorAdmin().startJob(MonitorTestControl.INITIATOR,
					MonitorTestControl.SVS, MonitorTestControl.COUNT);

			mjs = tbc.getMonitorAdmin().getRunningJobs();
		
			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "number of running jobs", 1+"" }),
					1, mjs.length);
			
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[] { "the returned MonitoringJob" }), mj);
			
			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "initiator", MonitorTestControl.INITIATOR }),
					MonitorTestControl.INITIATOR, mj.getInitiator());

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "length of the statusvariable names", MonitorTestControl.SV_LENGTH+"" }),
					MonitorTestControl.SV_LENGTH, mj.getStatusVariableNames().length);

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "first statusvariable name", MonitorTestControl.SVS[0] }),					
					MonitorTestControl.SVS[0], mj.getStatusVariableNames()[0]);

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "second statusvariable name", MonitorTestControl.SVS[1] }),
					MonitorTestControl.SVS[1], mj.getStatusVariableNames()[1]);

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "count", MonitorTestControl.COUNT+""
							 }), MonitorTestControl.COUNT, mj.getReportCount());			
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.getPermissionAdmin().setPermissions(
					tbc.getTb1Location(), infos);			
			mj.stop();			
		}
	}

	/**
	 * @testID testGetRunningJobs002
	 * @testDescription Tests if the correct list of running jobs is returned
	 */
	public void testGetRunningJobs002() {
		tbc.log("#testGetRunningJobs002");
		MonitoringJob mj = null;
		MonitoringJob[] mjs = null;
		PermissionInfo[] infos = null;	
		try {
			tbc.stopRunningJobs();
			
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());			

			tbc.setLocalPermission(new PermissionInfo[] {
					new PermissionInfo(org.osgi.service.monitor.MonitorPermission.class.getName(), MonitorTestControl.SVS[0], org.osgi.service.monitor.MonitorPermission.STARTJOB),	
					new PermissionInfo(org.osgi.service.monitor.MonitorPermission.class.getName(), MonitorTestControl.SVS[1], org.osgi.service.monitor.MonitorPermission.STARTJOB)
			});
			
			
			mj = tbc.getMonitorAdmin().startScheduledJob(
					MonitorTestControl.INITIATOR, MonitorTestControl.SVS,
					MonitorTestControl.SCHEDULE, MonitorTestControl.COUNT);


			mjs = tbc.getMonitorAdmin().getRunningJobs();
		
			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "number of running jobs", 1+""}),
					1, mjs.length);
			
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[] { "the returned MonitoringJob" }), mj);
			
			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "initiator", MonitorTestControl.INITIATOR }),
					MonitorTestControl.INITIATOR, mj.getInitiator());

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "length of statusvariable names", MonitorTestControl.SV_LENGTH+"" }),
					MonitorTestControl.SV_LENGTH, mj.getStatusVariableNames().length);

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "first statusvariable name", MonitorTestControl.SVS[0] }),					
					MonitorTestControl.SVS[0], mj.getStatusVariableNames()[0]);

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "second statusvariable name", MonitorTestControl.SVS[1] }),
					MonitorTestControl.SVS[1], mj.getStatusVariableNames()[1]);

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "schedule", MonitorTestControl.SCHEDULE+"" }), MonitorTestControl.SCHEDULE, mj.getSchedule());
			
			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "count", MonitorTestControl.COUNT+"" }),
					MonitorTestControl.COUNT, mj.getReportCount());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.getPermissionAdmin().setPermissions(
					tbc.getTb1Location(), infos);				
			mj.stop();
		}
	}

	/**
	 * @testID testGetRunningJobs003
	 * @testDescription Tests if the correct list of running jobs is returned
	 */
	public void testGetRunningJobs003() {
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
					new PermissionInfo(org.osgi.service.monitor.MonitorPermission.class.getName(), MonitorTestControl.SVS[0], org.osgi.service.monitor.MonitorPermission.STARTJOB),	
					new PermissionInfo(org.osgi.service.monitor.MonitorPermission.class.getName(), MonitorTestControl.SVS[1], org.osgi.service.monitor.MonitorPermission.STARTJOB)
			});
		

			mj = tbc.getMonitorAdmin().startScheduledJob(
					MonitorTestControl.INITIATOR, MonitorTestControl.SVS,
					MonitorTestControl.SCHEDULE, MonitorTestControl.COUNT);

			mj2 = tbc.getMonitorAdmin().startJob(MonitorTestControl.INITIATOR,
					MonitorTestControl.SVS, MonitorTestControl.COUNT);

			mjs = tbc.getMonitorAdmin().getRunningJobs();

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "number of running jobs", 2+"" }),
					2, mjs.length);
			
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[] { "the returned MonitoringJob" }), mj);
			
			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "initiator", MonitorTestControl.INITIATOR }),
					MonitorTestControl.INITIATOR, mj.getInitiator());

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "length of statusvariable names", MonitorTestControl.SV_LENGTH+"" }),	MonitorTestControl.SV_LENGTH, mj.getStatusVariableNames().length);

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "first statusvariable name", MonitorTestControl.SVS[0] }),					
					MonitorTestControl.SVS[0], mj.getStatusVariableNames()[0]);

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "second statusvariable name", MonitorTestControl.SVS[1] }),
					MonitorTestControl.SVS[1], mj.getStatusVariableNames()[1]);

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "schedule", MonitorTestControl.SCHEDULE+"" }),
					MonitorTestControl.SCHEDULE, mj.getSchedule());
			
			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "count", MonitorTestControl.COUNT+"" }),
					MonitorTestControl.COUNT, mj.getReportCount());			
			

			
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NOT_NULL, new String[] { "the returned MonitoringJob" }), mj2);
			
			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "initiator", MonitorTestControl.INITIATOR }),
					MonitorTestControl.INITIATOR, mj2.getInitiator());

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "length of statusvariable names", MonitorTestControl.SV_LENGTH+""}),
					MonitorTestControl.SV_LENGTH, mj2.getStatusVariableNames().length);

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "first statusvariable name", MonitorTestControl.SVS[0] }),					
					MonitorTestControl.SVS[0], mj2.getStatusVariableNames()[0]);

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "second statusvariable name", MonitorTestControl.SVS[1] }),
					MonitorTestControl.SVS[1], mj2.getStatusVariableNames()[1]);

			tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "count", MonitorTestControl.COUNT+"" }),
					MonitorTestControl.COUNT, mj2.getReportCount());	
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {			
			tbc.getPermissionAdmin().setPermissions(
					tbc.getTb1Location(), infos);			
			mj.stop();
			mj2.stop();
		}
	}
	
	/**
	 * @testID testGetRunningJobs004
	 * @testDescription Tests if an empty array is returned when no 
	 * 					MonitoringJob is running.
	 */
	public void testGetRunningJobs004() {
		tbc.log("#testGetRunningJobs004");
		MonitoringJob mj = null;
		MonitoringJob[] mjs = null;
		PermissionInfo[] infos = null;		
		try {						
			
			tbc.stopRunningJobs();
			
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());
			
			tbc.setLocalPermission(new PermissionInfo[] {
					new PermissionInfo(org.osgi.service.monitor.MonitorPermission.class.getName(), MonitorTestControl.SVS[0], org.osgi.service.monitor.MonitorPermission.STARTJOB),	
					new PermissionInfo(org.osgi.service.monitor.MonitorPermission.class.getName(), MonitorTestControl.SVS[1], org.osgi.service.monitor.MonitorPermission.STARTJOB)
			});

			mjs = tbc.getMonitorAdmin().getRunningJobs();
				
			tbc.assertTrue("Asserting if an empty array is returned when no MonitoringJob is running",
					(mjs.length==0));
					
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.getPermissionAdmin().setPermissions(
					tbc.getTb1Location(), infos);					
		}
	}	

}
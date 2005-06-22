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
 * 10/03/2005   Leonardo Barros
 * 14           Implement MEG TCK
 * ===========  ==============================================================
 * 22/03/2005   Eduardo Oliveira
 * 14           Updates after formal inspection (JSTD-MEGTCK-CODE-INSP011)
 * ===========  ==============================================================
 * 29/03/2005   Alexandre Santos
 * 14           Updates after formal inspection (JSTD-MEGTCK-CODE-INSP011)
 * ===========  ==============================================================
 * May 20, 2005	Alexandre Alves
 * 92           Make changes according to monitor RFC updates
 * ===========  ==============================================================
 * 14/06/2005	Alexandre Alves
 * 92           Make changes according to monitor RFC updates
 * ===========  ============================================================== * 
 */
package org.osgi.test.cases.monitor.tb1.MonitorAdmin;

import org.osgi.service.monitor.MonitoringJob;
import org.osgi.service.monitor.StatusVariable;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.SetStatusVariableInterface;
import org.osgi.test.cases.monitor.tbc.TestInterface;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @methodUnderTest org.osgi.service.monitor.MonitorAdmin#startScheduledJob
 * @generalDescription This class tests startScheduledJob method according with
 *                     MEG specification (rfc0084)
 */
public class StartScheduledJob implements TestInterface {

	private MonitorTestControl tbc;

	public StartScheduledJob(MonitorTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testStartScheduledJob001();
		testStartScheduledJob002();
		testStartScheduledJob003();
		testStartScheduledJob004();
		testStartScheduledJob005();
		testStartScheduledJob006();
		testStartScheduledJob007();
		testStartScheduledJob008();
		testStartScheduledJob009();
		testStartScheduledJob010();
		testStartScheduledJob011();
		testStartScheduledJob012();
	}

	/**
	 * @testID testStartScheduledJob001
	 * @testDescription Tests if a IllegalArgumentException is thrown if the
	 *                  list of StatusVariables is null
	 */
	public void testStartScheduledJob001() {
		tbc.log("#testStartScheduledJob001");
		try {
			tbc.stopRunningJobs();

			MonitoringJob job = tbc.getMonitorAdmin().startScheduledJob(
					MonitorTestControl.INITIATOR, null,
					MonitorTestControl.SCHEDULE, MonitorTestControl.COUNT);

			job.stop();

			tbc.failException("", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testStartScheduledJob002
	 * @testDescription Tests if a IllegalArgumentException is thrown if the
	 *                  list of StatusVariables is empty
	 */
	public void testStartScheduledJob002() {
		tbc.log("#testStartScheduledJob002");
		try {
			tbc.stopRunningJobs();

			MonitoringJob[] jobs = tbc.getMonitorAdmin().getRunningJobs();

			MonitoringJob job = tbc.getMonitorAdmin().startScheduledJob(
					MonitorTestControl.INITIATOR, new String[] {},
					MonitorTestControl.SCHEDULE, MonitorTestControl.COUNT);

			job.stop();

			tbc.failException("", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testStartScheduledJob003
	 * @testDescription Tests if a IllegalArgumentException is thrown if count
	 *                  parameter is invalid
	 */
	public void testStartScheduledJob003() {
		tbc.log("#testStartScheduledJob003");
		try {
			tbc.stopRunningJobs();

			MonitoringJob job = tbc.getMonitorAdmin().startScheduledJob(
					MonitorTestControl.INITIATOR, MonitorTestControl.SVS,
					MonitorTestControl.SCHEDULE,
					MonitorTestControl.INVALID_COUNT);

			job.stop();

			tbc.failException("", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testStartScheduledJob004
	 * @testDescription Tests if SecurityException is not thrown when the entity
	 *                  that start the job has the MonitorPermission with
	 *                  startjob action.
	 */
	public void testStartScheduledJob004() {
		tbc.log("#testStartScheduledJob004");
		PermissionInfo[] infos = null;
		try {
			tbc.stopRunningJobs();

			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc
					.setLocalPermission(new PermissionInfo[] {
							new PermissionInfo(
									org.osgi.service.monitor.MonitorPermission.class
											.getName(),
									MonitorTestControl.SVS[0],
									org.osgi.service.monitor.MonitorPermission.STARTJOB),
							new PermissionInfo(
									org.osgi.service.monitor.MonitorPermission.class
											.getName(),
									MonitorTestControl.SVS[1],
									org.osgi.service.monitor.MonitorPermission.STARTJOB) });

			MonitoringJob mj = tbc.getMonitorAdmin().startScheduledJob(
					MonitorTestControl.INITIATOR, MonitorTestControl.SVS,
					MonitorTestControl.SCHEDULE, MonitorTestControl.COUNT);

			tbc.assertNotNull(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_NOT_NULL,
					new String[] { "the returned MonitoringJob" }), mj);

			String init = mj.getInitiator();
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"initiator", MonitorTestControl.INITIATOR }),
					MonitorTestControl.INITIATOR, init);

			int varNamesLen = mj.getStatusVariableNames().length;
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"length of statusvariable names",
							MonitorTestControl.SV_LENGTH + "" }),
					MonitorTestControl.SV_LENGTH, varNamesLen);

			String varName0 = mj.getStatusVariableNames()[0];
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"first statusvariable name",
							MonitorTestControl.SVS[0] }),
					MonitorTestControl.SVS[0], varName0);

			String varName1 = mj.getStatusVariableNames()[1];
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"second statusvariable name",
							MonitorTestControl.SVS[1] }),
					MonitorTestControl.SVS[1], varName1);

			long schec = mj.getSchedule();
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] { "schedule",
							MonitorTestControl.SCHEDULE + "" }),
					MonitorTestControl.SCHEDULE, schec);

			int repCount = mj.getReportCount();
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] { "count",
							MonitorTestControl.COUNT + "" }),
					MonitorTestControl.COUNT, repCount);

			mj.stop();
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		} finally {
			tbc.getPermissionAdmin()
					.setPermissions(tbc.getTb1Location(), infos);
		}
	}

	/**
	 * @testID testStartScheduledJob005
	 * @testDescription Tests if SecurityException is thrown when the entity has
	 *                  no permission to start a job
	 */
	public void testStartScheduledJob005() {
		tbc.log("#testStartScheduledJob005");
		PermissionInfo[] infos = null;
		try {
			tbc.stopRunningJobs();

			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc
					.setLocalPermission(new PermissionInfo[] {
							new PermissionInfo(
									org.osgi.service.monitor.MonitorPermission.class
											.getName(),
									MonitorTestControl.SVS[0],
									org.osgi.service.monitor.MonitorPermission.STARTJOB),
							new PermissionInfo(
									org.osgi.service.monitor.MonitorPermission.class
											.getName(),
									MonitorTestControl.SVS[1],
									org.osgi.service.monitor.MonitorPermission.PUBLISH) });

			MonitoringJob mj = tbc.getMonitorAdmin().startScheduledJob(
					MonitorTestControl.INITIATOR, MonitorTestControl.SVS,
					MonitorTestControl.SCHEDULE, MonitorTestControl.COUNT);

			mj.stop();

			tbc.failException("", IllegalArgumentException.class);

		} catch (SecurityException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { SecurityException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							SecurityException.class.getName(),
							e.getClass().getName() }));
		} finally {
			tbc.getPermissionAdmin()
					.setPermissions(tbc.getTb1Location(), infos);
		}
	}

	/**
	 * @testID testStartScheduledJob006
	 * @testDescription Tests if SecurityException is thrown when the entity has
	 *                  no permission to start a job for any StatusVariable
	 *                  passed as parameter
	 */
	public void testStartScheduledJob006() {
		tbc.log("#testStartScheduledJob006");
		PermissionInfo[] infos = null;
		try {
			tbc.stopRunningJobs();

			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc
					.setLocalPermission(new PermissionInfo[] {
							new PermissionInfo(
									org.osgi.service.monitor.MonitorPermission.class
											.getName(),
									MonitorTestControl.SVS[0], null),
							new PermissionInfo(
									org.osgi.service.monitor.MonitorPermission.class
											.getName(),
									MonitorTestControl.SVS[1], null) });

			MonitoringJob mj = tbc.getMonitorAdmin().startScheduledJob(
					MonitorTestControl.INITIATOR, MonitorTestControl.SVS,
					MonitorTestControl.SCHEDULE, MonitorTestControl.COUNT);

			mj.stop();

			tbc.failException("", IllegalArgumentException.class);

		} catch (SecurityException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { SecurityException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							SecurityException.class.getName(),
							e.getClass().getName() }));
		} finally {
			tbc.getPermissionAdmin()
					.setPermissions(tbc.getTb1Location(), infos);
		}
	}

	/**
	 * @testID testStartScheduledJob007
	 * @testDescription Tests if a IllegalArgumentException is thrown if
	 *                  schedule parameter is invalid
	 */
	public void testStartScheduledJob007() {
		tbc.log("#testStartScheduledJob007");
		try {
			tbc.stopRunningJobs();

			MonitoringJob job = tbc.getMonitorAdmin().startScheduledJob(
					MonitorTestControl.INITIATOR, MonitorTestControl.SVS,
					MonitorTestControl.INVALID_SCHEDULE,
					MonitorTestControl.COUNT);

			job.stop();

			tbc.failException("", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testStartScheduledJob008
	 * @testDescription Tests if monitoring job notifies the listener in correct
	 *                  time and if it stops after two measurements.
	 */
	public synchronized void testStartScheduledJob008() {
		tbc.log("#testStartScheduledJob008");
		PermissionInfo[] infos = null;
		MonitoringJob mj = null;
		try {
			tbc.stopRunningJobs();

			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc
					.setLocalPermission(new PermissionInfo[] {
							new PermissionInfo(
									org.osgi.service.monitor.MonitorPermission.class
											.getName(),
									MonitorTestControl.SVS[0],
									org.osgi.service.monitor.MonitorPermission.STARTJOB),
							new PermissionInfo(
									org.osgi.service.monitor.MonitorPermission.class
											.getName(),
									MonitorTestControl.SVS[1],
									org.osgi.service.monitor.MonitorPermission.STARTJOB) });

			MonitoringJob[] job = tbc.getMonitorAdmin().getRunningJobs();

			MonitorTestControl.EVENT_COUNT = 0;

			mj = tbc.getMonitorAdmin().startScheduledJob(
					MonitorTestControl.INITIATOR, MonitorTestControl.SVS,
					MonitorTestControl.SCHEDULE + 4,
					MonitorTestControl.COUNT + 1); // schedule for 5 seconds and count for 2 measurements 

			wait(MonitorTestControl.TIMEOUT); // wait for 2 seconds

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"variable of event modification", 0 + "" }), 0,
					MonitorTestControl.EVENT_COUNT); // assert that no events has been fired.

			wait(MonitorTestControl.TIMEOUT*3); // wait for 6 seconds
			
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"variable of event modification", 2 + "" }), 2,
					MonitorTestControl.EVENT_COUNT); // assert if two events was fired ( because it has two StatusVariable )

			
			// asserting event properties.
			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "event properties correspond to the properties specified in rfc." }),
							tbc.isMonitorablePid()
									&& tbc.isStatusVariableName()
									&& tbc.isStatusVariableValue()
									&& tbc.isListenerId());

			tbc
					.assertEquals(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_EQUALS,
											new String[] {
													"the value in "
															+ MonitorTestControl.CONST_STATUSVARIABLE_NAME,
													MonitorTestControl.SV_NAME2 }),
							MonitorTestControl.SV_NAME2, tbc
									.getStatusVariableName());

			tbc
					.assertEquals(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_EQUALS,
											new String[] {
													"the value in "
															+ MonitorTestControl.CONST_STATUSVARIABLE_VALUE,
													"test" }), "test", tbc
									.getStatusVariableValue());

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"the value in "
									+ MonitorTestControl.CONST_MONITORABLE_PID,
							MonitorTestControl.SV_MONITORABLEID1 }),
					MonitorTestControl.SV_MONITORABLEID1, tbc
							.getMonitorablePid());

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"the value in "
									+ MonitorTestControl.CONST_LISTENER_ID,
							MonitorTestControl.INITIATOR }),
					MonitorTestControl.INITIATOR, tbc.getListenerId());
			
			SetStatusVariableInterface monitorable = tbc.getStatusVariableInterface();
			StatusVariable sv = new StatusVariable(MonitorTestControl.SV_NAME2,
					StatusVariable.CM_DER, "test1");
			monitorable.setStatusVariable(sv); // update the second statusvariable value to test1, to check it later. 
	
			wait(MonitorTestControl.TIMEOUT * 2); // wait 4 seconds, after that will be 12 seconds in total.

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"variable of event modification", 4 + "" }), 4,
					MonitorTestControl.EVENT_COUNT); // assert if two more events was fired.

			
			// assert the event properties
			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "event properties correspond to the properties specified in rfc." }),
							tbc.isMonitorablePid()
									&& tbc.isStatusVariableName()
									&& tbc.isStatusVariableValue()
									&& tbc.isListenerId());

			tbc
					.assertEquals(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_EQUALS,
											new String[] {
													"the value in "
															+ MonitorTestControl.CONST_STATUSVARIABLE_NAME,
													MonitorTestControl.SV_NAME2 }),
							MonitorTestControl.SV_NAME2, tbc
									.getStatusVariableName());

			tbc
					.assertEquals(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_EQUALS,
											new String[] {
													"the value in "
															+ MonitorTestControl.CONST_STATUSVARIABLE_VALUE,
													"test1" }), "test1", tbc
									.getStatusVariableValue()); // check if the updated value was set.

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"the value in "
									+ MonitorTestControl.CONST_MONITORABLE_PID,
							MonitorTestControl.SV_MONITORABLEID1 }),
					MonitorTestControl.SV_MONITORABLEID1, tbc
							.getMonitorablePid());

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"the value in "
									+ MonitorTestControl.CONST_LISTENER_ID,
							MonitorTestControl.INITIATOR }),
					MonitorTestControl.INITIATOR, tbc.getListenerId());

			wait(MonitorTestControl.TIMEOUT * 3); // wait 6 seconds, after that will be 18 seconds.

			tbc.assertEquals("Asserting if the monitoringjob stops the reporting. So, we expect the same value.", 4,
					MonitorTestControl.EVENT_COUNT); // I have set only two measurements, so, the event count must be 4, if it is bigger than 4, then
														// the monitoring job was not stopped automatically.

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		} finally {
			mj.stop();
			tbc.getPermissionAdmin()
					.setPermissions(tbc.getTb1Location(), infos);
		}
	}

	/**
	 * @testID testStartScheduledJob009
	 * @testDescription Tests if a IllegalArgumentException is thrown if the
	 *                  initiator is null
	 */
	public void testStartScheduledJob009() {
		tbc.log("#testStartScheduledJob009");
		try {
			tbc.stopRunningJobs();

			MonitoringJob job = tbc.getMonitorAdmin().startScheduledJob(null,
					MonitorTestControl.SVS, MonitorTestControl.SCHEDULE,
					MonitorTestControl.COUNT);

			job.stop();

			tbc.failException("", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testStartScheduledJob010
	 * @testDescription Tests if a IllegalArgumentException is thrown if the
	 *                  initiator is empty
	 */
	public void testStartScheduledJob010() {
		tbc.log("#testStartScheduledJob010");
		try {
			tbc.stopRunningJobs();

			MonitoringJob job = tbc.getMonitorAdmin().startScheduledJob("",
					MonitorTestControl.SVS, MonitorTestControl.SCHEDULE,
					MonitorTestControl.COUNT);

			job.stop();

			tbc.failException("", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testStartScheduledJob011
	 * @testDescription Tests if a IllegalArgumentException is thrown if the
	 *                  initiator is empty
	 */
	public void testStartScheduledJob011() {
		tbc.log("#testStartScheduledJob011");
		try {
			tbc.stopRunningJobs();

			MonitoringJob job = tbc.getMonitorAdmin().startScheduledJob(
					MonitorTestControl.INITIATOR,
					new String[] { "cesar/" + MonitorTestControl.INVALID_ID },
					MonitorTestControl.SCHEDULE, MonitorTestControl.COUNT);

			job.stop();

			tbc.failException("", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		}
	}
	
	/**
	 * @testID testStartScheduledJob012
	 * @testDescription Tests if SecurityException is thrown when the entity has
	 *                  to use at minimum two as scheduled value.
	 */
	public void testStartScheduledJob012() {
		tbc.log("#testStartScheduledJob012");
		PermissionInfo[] infos = null;
		try {
			tbc.stopRunningJobs();

			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc
					.setLocalPermission(new PermissionInfo[] {
							new PermissionInfo(
									org.osgi.service.monitor.MonitorPermission.class
											.getName(),
									MonitorTestControl.SVS[0],
									org.osgi.service.monitor.MonitorPermission.STARTJOB+":2"),
							new PermissionInfo(
									org.osgi.service.monitor.MonitorPermission.class
											.getName(),
									MonitorTestControl.SVS[1],
									org.osgi.service.monitor.MonitorPermission.PUBLISH) });

			MonitoringJob mj = tbc.getMonitorAdmin().startScheduledJob(
					MonitorTestControl.INITIATOR, MonitorTestControl.SVS,
					MonitorTestControl.SCHEDULE, MonitorTestControl.COUNT);

			mj.stop();

			tbc.failException("", IllegalArgumentException.class);

		} catch (SecurityException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { SecurityException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							SecurityException.class.getName(),
							e.getClass().getName() }));
		} finally {
			tbc.getPermissionAdmin()
					.setPermissions(tbc.getTb1Location(), infos);
		}
	}	

}

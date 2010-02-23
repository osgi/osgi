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

import org.osgi.service.monitor.MonitorPermission;
import org.osgi.service.monitor.MonitoringJob;
import org.osgi.service.monitor.StatusVariable;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.monitor.tbc.MonitorConstants;
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.TestInterface;
import org.osgi.test.cases.monitor.tbc.TestingMonitorable;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @author Alexandre Alves
 * 
 * This Test Class Validates the implementation of
 * <code>startScheduledJob</code> method, according to MEG reference
 * documentation.
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
		testStartScheduledJob013();
		testStartScheduledJob014();
	}

	/**
	 * This method asserts if IllegalArgumentException is thrown
	 * when we pass null to the list of StatusVariable.
	 * 
	 * @spec MonitorAdmin.startScheduledJob(String,String[],int,int)
	 */
	private void testStartScheduledJob001() {
		tbc.log("#testStartScheduledJob001");
		MonitoringJob job = null;
		try {

			job = tbc.getMonitorAdmin().startScheduledJob(
					MonitorConstants.INITIATOR, null,
					MonitorConstants.SCHEDULE, MonitorConstants.COUNT);

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
		} finally {
			tbc.cleanUp(job);
		}
	}

	/**
	 * This method asserts if IllegalArgumentException is thrown
	 * when we pass a empty list of StatusVariable.
	 * 
	 * @spec MonitorAdmin.startScheduledJob(String,String[],int,int)
	 */
	private void testStartScheduledJob002() {
		tbc.log("#testStartScheduledJob002");
		MonitoringJob job = null;
		try {
			job = tbc.getMonitorAdmin().startScheduledJob(
					MonitorConstants.INITIATOR, new String[] {},
					MonitorConstants.SCHEDULE, MonitorConstants.COUNT);			

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
		} finally {
			tbc.cleanUp(job);
		}
	}

	/**
	 * This method asserts if IllegalArgumentException is thrown
	 * when we pass an invalid count as parameter.
	 * 
	 * @spec MonitorAdmin.startScheduledJob(String,String[],int,int)
	 */
	private void testStartScheduledJob003() {
		tbc.log("#testStartScheduledJob003");
		MonitoringJob job = null;
		try {

			job = tbc.getMonitorAdmin().startScheduledJob(
					MonitorConstants.INITIATOR, MonitorConstants.SVS,
					MonitorConstants.SCHEDULE,
					MonitorConstants.INVALID_COUNT);

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
		} finally {
			tbc.cleanUp(job);
		}
	}

	/**
	 * This method tests the get methods for MonitoringJob.
	 * It asserts the values passed in StartScheduledJob.
	 * 
	 * @spec MonitorAdmin.startScheduledJob(String,String[],int,int)
	 */
	private void testStartScheduledJob004() {
		tbc.log("#testStartScheduledJob004");
		PermissionInfo[] infos = null;
		MonitoringJob mj = null;
		try {

			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc
					.setLocalPermission(new PermissionInfo[] {
							new PermissionInfo(
									org.osgi.service.monitor.MonitorPermission.class
											.getName(),
									MonitorConstants.SVS[0],
									org.osgi.service.monitor.MonitorPermission.STARTJOB),
							new PermissionInfo(
									org.osgi.service.monitor.MonitorPermission.class
											.getName(),
									MonitorConstants.SVS[1],
									org.osgi.service.monitor.MonitorPermission.STARTJOB) });

			mj = tbc.getMonitorAdmin().startScheduledJob(
					MonitorConstants.INITIATOR, MonitorConstants.SVS,
					MonitorConstants.SCHEDULE*4, MonitorConstants.COUNT);

			tbc.assertNotNull(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_NOT_NULL,
					new String[] { "the returned MonitoringJob" }), mj);

			String init = mj.getInitiator();
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"initiator", MonitorConstants.INITIATOR }),
					MonitorConstants.INITIATOR, init);

			int varNamesLen = mj.getStatusVariableNames().length;
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"length of statusvariable names",
							MonitorConstants.SV_LENGTH + "" }),
					MonitorConstants.SV_LENGTH, varNamesLen);

			String varName0 = mj.getStatusVariableNames()[0];
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"first statusvariable name",
							MonitorConstants.SVS[0] }),
					MonitorConstants.SVS[0], varName0);

			String varName1 = mj.getStatusVariableNames()[1];
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"second statusvariable name",
							MonitorConstants.SVS[1] }),
					MonitorConstants.SVS[1], varName1);

			long sched = mj.getSchedule();
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] { "schedule",
							MonitorConstants.SCHEDULE*4 + "" }),
							MonitorConstants.SCHEDULE*4, sched);

			int repCount = mj.getReportCount();
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] { "count",
							MonitorConstants.COUNT + "" }),
					MonitorConstants.COUNT, repCount);
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(mj);
			tbc.setTb1Permission(infos);
		}
	}

	/**
	 * This method asserts that a SecurityException is thrown when
	 * one of the StatusVariables does not have StartJob permission.
	 * 
	 * @spec MonitorAdmin.startScheduledJob(String,String[],int,int)
	 */
	private void testStartScheduledJob005() {
		tbc.log("#testStartScheduledJob005");
		PermissionInfo[] infos = null;
		MonitoringJob mj = null;
		try {

			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc
					.setLocalPermission(new PermissionInfo[] {
							new PermissionInfo(
									org.osgi.service.monitor.MonitorPermission.class
											.getName(),
									MonitorConstants.SVS[0],
									org.osgi.service.monitor.MonitorPermission.STARTJOB),
							new PermissionInfo(
									org.osgi.service.monitor.MonitorPermission.class
											.getName(),
									MonitorConstants.SVS[1],
									org.osgi.service.monitor.MonitorPermission.PUBLISH) });

			mj = tbc.getMonitorAdmin().startScheduledJob(
					MonitorConstants.INITIATOR, MonitorConstants.SVS,
					MonitorConstants.SCHEDULE, MonitorConstants.COUNT);

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
			tbc.cleanUp(mj);
			tbc.setTb1Permission(infos);
		}
	}

	/**
	 * This method asserts that a SecurityException is thrown when
	 * the StatusVariables does not have permission.
	 * 
	 * @spec MonitorAdmin.startScheduledJob(String,String[],int,int)
	 */
	private void testStartScheduledJob006() {
		tbc.log("#testStartScheduledJob006");
		PermissionInfo[] infos = null;
		MonitoringJob mj = null;
		try {

			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc
					.setLocalPermission(new PermissionInfo[] {
							new PermissionInfo(
									org.osgi.service.monitor.MonitorPermission.class
											.getName(),
									MonitorConstants.SVS[0], ""),
							new PermissionInfo(
									org.osgi.service.monitor.MonitorPermission.class
											.getName(),
									MonitorConstants.SVS[1], "") });

			mj = tbc.getMonitorAdmin().startScheduledJob(
					MonitorConstants.INITIATOR, MonitorConstants.SVS,
					MonitorConstants.SCHEDULE, MonitorConstants.COUNT);

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
			tbc.cleanUp(mj);
			tbc.setTb1Permission(infos);
		}
	}

	/**
	 * This method asserts if IllegalArgumentException is thrown
	 * when we pass an invalid schedule as parameter.
	 * 
	 * @spec MonitorAdmin.startScheduledJob(String,String[],int,int)
	 */
	private void testStartScheduledJob007() {
		tbc.log("#testStartScheduledJob007");
		MonitoringJob job = null;
		try {

			job = tbc.getMonitorAdmin().startScheduledJob(
					MonitorConstants.INITIATOR, MonitorConstants.SVS,
					MonitorConstants.INVALID_SCHEDULE,
					MonitorConstants.COUNT);

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
		} finally {
			tbc.cleanUp(job);
		}
	}

	/**
	 * This method tests the schedule and count parameters, asserting the time in seconds
	 * between two measurements and the number of measurements to be taken.
	 * 
	 * @spec MonitorAdmin.startScheduledJob(String,String[],int,int)
	 */
	private void testStartScheduledJob008() {
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
									MonitorConstants.SVS[0],
									org.osgi.service.monitor.MonitorPermission.STARTJOB),
							new PermissionInfo(
									org.osgi.service.monitor.MonitorPermission.class
											.getName(),
									MonitorConstants.SVS[1],
									org.osgi.service.monitor.MonitorPermission.STARTJOB) });

			MonitorConstants.EVENT_COUNT = 0;
			
			tbc.resetEvent();

			mj = tbc.getMonitorAdmin().startScheduledJob(
					MonitorConstants.INITIATOR, MonitorConstants.SVS,
					MonitorConstants.SCHEDULE + 4,
					MonitorConstants.COUNT + 1); // schedule for 5 seconds and count for 2 measurements 

			tbc.sleep0(MonitorConstants.SHORT_TIMEOUT); // wait for 2 seconds

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"variable of event modification", 0 + "" }), 0,
					MonitorConstants.EVENT_COUNT); // assert that no events has been fired.

			tbc.sleep0(MonitorConstants.SHORT_TIMEOUT*3); // wait for 6 seconds
			
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"variable of event modification", 2 + "" }), 2,
					MonitorConstants.EVENT_COUNT); // assert if two events was fired ( because it has two StatusVariable )

			
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
															+ MonitorConstants.CONST_STATUSVARIABLE_NAME,
													MonitorConstants.SV_NAME2 }),
							MonitorConstants.SV_NAME2, tbc
									.getStatusVariableName());

			tbc
					.assertEquals(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_EQUALS,
											new String[] {
													"the value in "
															+ MonitorConstants.CONST_STATUSVARIABLE_VALUE,
													"test" }), "test", tbc
									.getStatusVariableValue());

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"the value in "
									+ MonitorConstants.CONST_MONITORABLE_PID,
							MonitorConstants.SV_MONITORABLEID1 }),
					MonitorConstants.SV_MONITORABLEID1, tbc
							.getMonitorablePid());

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"the value in "
									+ MonitorConstants.CONST_LISTENER_ID,
							MonitorConstants.INITIATOR }),
					MonitorConstants.INITIATOR, tbc.getListenerId());
			
			TestingMonitorable monitorable = tbc.getMonitorableInterface();
			StatusVariable sv = new StatusVariable(MonitorConstants.SV_NAME2,
					StatusVariable.CM_DER, "test1");
			monitorable.setStatusVariable(sv); // update the second statusvariable value to test1, to check it later. 
	
			tbc.sleep0(MonitorConstants.SHORT_TIMEOUT * 2); // wait 4 seconds, after that will be 12 seconds in total.

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"variable of event modification", 4 + "" }), 4,
					MonitorConstants.EVENT_COUNT); // assert if two more events was fired.

			
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
															+ MonitorConstants.CONST_STATUSVARIABLE_NAME,
													MonitorConstants.SV_NAME2 }),
							MonitorConstants.SV_NAME2, tbc
									.getStatusVariableName());

			tbc
					.assertEquals(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_EQUALS,
											new String[] {
													"the value in "
															+ MonitorConstants.CONST_STATUSVARIABLE_VALUE,
													"test1" }), "test1", tbc
									.getStatusVariableValue()); // check if the updated value was set.

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"the value in "
									+ MonitorConstants.CONST_MONITORABLE_PID,
							MonitorConstants.SV_MONITORABLEID1 }),
					MonitorConstants.SV_MONITORABLEID1, tbc
							.getMonitorablePid());

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"the value in "
									+ MonitorConstants.CONST_LISTENER_ID,
							MonitorConstants.INITIATOR }),
					MonitorConstants.INITIATOR, tbc.getListenerId());

			tbc.sleep0(MonitorConstants.SHORT_TIMEOUT * 3); // wait 6 seconds, after that will be 18 seconds.

			tbc.assertEquals("Asserting if the monitoringjob stops the reporting. So, we expect the same value.", 4,
					MonitorConstants.EVENT_COUNT); // I have set only two measurements, so, the event count must be 4, if it is bigger than 4, then
														// the monitoring job was not stopped automatically.

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(mj);
			tbc.setTb1Permission(infos);
			//Monitorable was changed, restore its state 
			tbc.reinstallMonitorable1();
		}
	}

	/**
	 * This method asserts if IllegalArgumentException is thrown
	 * when we pass null as initiator parameter.
	 * 
	 * @spec MonitorAdmin.startScheduledJob(String,String[],int,int)
	 */
	private void testStartScheduledJob009() {
		tbc.log("#testStartScheduledJob009");
		MonitoringJob job = null;
		try {

			job = tbc.getMonitorAdmin().startScheduledJob(null,
					MonitorConstants.SVS, MonitorConstants.SCHEDULE,
					MonitorConstants.COUNT);

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
		} finally {
			tbc.cleanUp(job);
		}
	}

	/**
	 * This method asserts if IllegalArgumentException is thrown
	 * when we pass an empty string as initiator parameter.
	 * 
	 * @spec MonitorAdmin.startScheduledJob(String,String[],int,int)
	 */
	private void testStartScheduledJob010() {
		tbc.log("#testStartScheduledJob010");
		MonitoringJob job = null;
		try {
			job = tbc.getMonitorAdmin().startScheduledJob("",
					MonitorConstants.SVS, MonitorConstants.SCHEDULE,
					MonitorConstants.COUNT);

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
		} finally {
			tbc.cleanUp(job);
		}
	}

	/**
	 * This method asserts if IllegalArgumentException is thrown
	 * when we pass invalid characters as path to a StatusVariable.
	 * 
	 * @spec MonitorAdmin.startScheduledJob(String,String[],int,int)
	 */
	private void testStartScheduledJob011() {
		tbc.log("#testStartScheduledJob011");
		MonitoringJob job = null;
		try {

			job = tbc.getMonitorAdmin().startScheduledJob(
					MonitorConstants.INITIATOR,
					new String[] { "cesar/" + MonitorConstants.INVALID_ID },
					MonitorConstants.SCHEDULE, MonitorConstants.COUNT);

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
		} finally {
			tbc.cleanUp(job);
		}
	}
	
	/**
	 * This method asserts that a SecurityException is thrown
	 * if the permission does not allow starting the job with the given frequency.
	 * 
	 * @spec MonitorAdmin.startScheduledJob(String,String[],int,int)
	 */
	private void testStartScheduledJob012() {
		tbc.log("#testStartScheduledJob012");
		PermissionInfo[] infos = null;
		MonitoringJob mj = null;
		try {

			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc
					.setLocalPermission(new PermissionInfo[] {
							new PermissionInfo(
									org.osgi.service.monitor.MonitorPermission.class
											.getName(),
									MonitorConstants.SVS[0],
									org.osgi.service.monitor.MonitorPermission.STARTJOB+":2"),
							new PermissionInfo(
									org.osgi.service.monitor.MonitorPermission.class
											.getName(),
									MonitorConstants.SVS[1],
									org.osgi.service.monitor.MonitorPermission.PUBLISH) });

			mj = tbc.getMonitorAdmin().startScheduledJob(
					MonitorConstants.INITIATOR, MonitorConstants.SVS,
					MonitorConstants.SCHEDULE, MonitorConstants.COUNT);

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
			tbc.cleanUp(mj);
			tbc.setTb1Permission(infos);
		}
	}
	
	/**
	 * This method asserts if when we start a measurement to run indefinitely
	 * it will run more than two times.
	 * 
	 * @spec MonitorAdmin.startScheduledJob(String,String[],int,int)
	 */	
	private void testStartScheduledJob013() {
		tbc.log("#testStartScheduledJob013");
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
									MonitorConstants.SVS[0],
									org.osgi.service.monitor.MonitorPermission.STARTJOB),
							new PermissionInfo(
									org.osgi.service.monitor.MonitorPermission.class
											.getName(),
									MonitorConstants.SVS[1],
									org.osgi.service.monitor.MonitorPermission.STARTJOB) });

			MonitorConstants.EVENT_COUNT = 0;
			
			mj = tbc.getMonitorAdmin().startScheduledJob(
					MonitorConstants.INITIATOR, MonitorConstants.SVS,
					MonitorConstants.SCHEDULE + 3,
					0); // to run indefinitely

			tbc.sleep0(MonitorConstants.SHORT_TIMEOUT*5); // wait for 10 seconds

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"variable of event modification", 4 + "" }), 4,
					MonitorConstants.EVENT_COUNT); // assert that 4 events has been fired ( two for each statusvariable ).

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(mj);
			tbc.setTb1Permission(infos);
		}
	}
	
	/**
	 * This method asserts if IllegalArgumentException is thrown when
	 * we pass a path to a non-existing StatusVariable.
	 * 
	 * @spec MonitorAdmin.startScheduledJob(String,String[],int,int)
	 */	
	private void testStartScheduledJob014() {
		tbc.log("#testStartScheduledJob014");
		PermissionInfo[] infos = null;
		MonitoringJob job = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorConstants.INEXISTENT_SVS, MonitorPermission.STARTJOB));

			job = tbc.getMonitorAdmin().startScheduledJob(
					MonitorConstants.INITIATOR, new String[] { MonitorConstants.INEXISTENT_SVS },
					MonitorConstants.SCHEDULE,
					MonitorConstants.COUNT);

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
		} finally {
			tbc.cleanUp(job);
			tbc.setTb1Permission(infos);
		}
	}	

}

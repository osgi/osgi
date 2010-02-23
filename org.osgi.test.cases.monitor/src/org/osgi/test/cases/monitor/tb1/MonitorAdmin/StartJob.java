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
 * 22/03/2005   Eduardo Oliveira
 * 14           Updates after formal inspection (JSTD-MEGTCK-CODE-INSP011)
 * ===========  ==============================================================
 * 29/03/2005   Alexandre Santos
 * 14           Updates after formal inspection (JSTD-MEGTCK-CODE-INSP011)
 * ===========  ==============================================================
 * May 20, 2005	Alexandre Alves
 * 92           Make changes according to monitor RFC updates
 * ===========  ==============================================================
 */
package org.osgi.test.cases.monitor.tb1.MonitorAdmin;

import org.osgi.service.monitor.MonitorPermission;
import org.osgi.service.monitor.MonitoringJob;
import org.osgi.service.monitor.StatusVariable;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.monitor.tbc.MonitorConstants;
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.TestInterface;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @author Alexandre Alves
 * 
 * This Test Class Validates the implementation of
 * <code>startJob</code> method, according to MEG reference
 * documentation.
 */
public class StartJob implements TestInterface {

	private MonitorTestControl tbc;

	public StartJob(MonitorTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testStartJob001();
		testStartJob002();
		testStartJob003();
		testStartJob004();
		testStartJob005();
		testStartJob006();
		testStartJob007();
		testStartJob008();
		testStartJob009();
		testStartJob010();
		testStartJob011();
	}

	/**
	 * This method asserts if IllegalArgumentException is thrown
	 * when we pass invalid characters as parameter.
	 * 
	 * @spec MonitorAdmin.startJob(String,String[],int)
	 */
	private void testStartJob001() {
		tbc.log("#testStartJob001");
		MonitoringJob job = null;
		try {

			job = tbc.getMonitorAdmin().startJob(
					MonitorConstants.INITIATOR, null,
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
	 * when we pass a empty list of StatusVariable.
	 * 
	 * @spec MonitorAdmin.startJob(String,String[],int)
	 */
	private void testStartJob002() {
		tbc.log("#testStartJob002");
		MonitoringJob job = null;
		try {

			job = tbc.getMonitorAdmin().startJob(
					MonitorConstants.INITIATOR, new String[] {},
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
	 * when we pass an invalid count as parameter.
	 * 
	 * @spec MonitorAdmin.startJob(String,String[],int)
	 */
	private void testStartJob003() {
		tbc.log("#testStartJob003");
		MonitoringJob job = null;
		try {

			job = tbc.getMonitorAdmin().startJob(
					MonitorConstants.INITIATOR, MonitorConstants.SVS,
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
	 * It asserts the values passed in StartJob.
	 * 
	 * @spec MonitorAdmin.startJob(String,String[],int)
	 */
	private void testStartJob004() {
		tbc.log("#testStartJob004");
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

			mj = tbc.getMonitorAdmin().startJob(
					MonitorConstants.INITIATOR, MonitorConstants.SVS,
					MonitorConstants.COUNT);

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

			int repCount = mj.getReportCount();
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] { "count",
							MonitorConstants.COUNT + "" }),
					MonitorConstants.COUNT, repCount);
			
			long schedule = mj.getSchedule();
			tbc.assertEquals("Asserting that 0 is returned when we have started a based job.", 0, schedule);

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
	 * @spec MonitorAdmin.startJob(String,String[],int)
	 */
	private void testStartJob005() {
		tbc.log("#testStartJob005");
		PermissionInfo[] infos = null;
		MonitoringJob job = null;
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

			job = tbc.getMonitorAdmin().startJob(
					MonitorConstants.INITIATOR, MonitorConstants.SVS,
					MonitorConstants.COUNT);

			tbc.failException("", SecurityException.class);

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
			tbc.cleanUp(job);			
			tbc.setTb1Permission(infos);
		}
	}

	/**
	 * This method asserts that a SecurityException is thrown when
	 * the StatusVariables does not have permission.
	 * 
	 * @spec MonitorAdmin.startJob(String,String[],int)
	 */
	private void testStartJob006() {
		tbc.log("#testStartJob006");
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

			mj = tbc.getMonitorAdmin().startJob(
					MonitorConstants.INITIATOR, MonitorConstants.SVS,
					MonitorConstants.COUNT);

			tbc.failException("", SecurityException.class);

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
	 * This method asserts that an IllegalArgumentException is thrown when
	 * one of the StatusVariables does not support notifications.
	 * 
	 * @spec MonitorAdmin.startJob(String,String[],int)
	 */
	private void testStartJob007() {
		tbc.log("#testStartJob007");
		PermissionInfo[] infos = null;
		MonitoringJob job = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorConstants.SVS_NOT_SUPPORT_NOTIFICATION[0], MonitorPermission.STARTJOB));

			job = tbc.getMonitorAdmin().startJob(
					MonitorConstants.INITIATOR,
					MonitorConstants.SVS_NOT_SUPPORT_NOTIFICATION,
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

	/**
	 * This method tests the count parameter, asserting the number of changes that 
	 * happen to a StatusVariable before a new notification is sent. We are using
	 * 2 as count parameter.
	 * 
	 * @spec MonitorAdmin.startJob(String,String[],int)
	 */
	private synchronized void testStartJob008() {
		tbc.log("#testStartJob008");
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
			
			tbc.setBroadcast(false); // listen only for events with listenerId			

			mj = tbc.getMonitorAdmin().startJob(
					MonitorConstants.INITIATOR, MonitorConstants.SVS,
					MonitorConstants.COUNT + 1);

			tbc.getMonitorListener().updated(
					MonitorConstants.SV_MONITORABLEID1,
					new StatusVariable(MonitorConstants.SV_NAME1,
							StatusVariable.CM_CC, "test1"));
			
			tbc.waitForStatusVariable();
			
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"variable of event modification", 0 + "" }), 0,
					MonitorConstants.EVENT_COUNT);

			
     tbc.resetEvent();

			tbc.getMonitorListener().updated(
					MonitorConstants.SV_MONITORABLEID1,
					new StatusVariable(MonitorConstants.SV_NAME1,
							StatusVariable.CM_CC, "test1"));

			tbc.waitForStatusVariable();
			
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"variable of event modification", 1 + "" }), 1,
					MonitorConstants.EVENT_COUNT);

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
													MonitorConstants.SV_NAME1 }),
							MonitorConstants.SV_NAME1, tbc
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
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + " "
					+ e.getClass().getName() + " " + e.getMessage());
		} finally {
			tbc.cleanUp(mj);
			tbc.setTb1Permission(infos);
		}
	}

	/**
	 * This method asserts if IllegalArgumentException is thrown
	 * when we pass null as parameter.
	 * 
	 * @spec MonitorAdmin.startJob(String,String[],int)
	 */
	private void testStartJob009() {
		tbc.log("#testStartJob009");
		MonitoringJob job = null;
		try {

			job = tbc.getMonitorAdmin().startJob(null,
					MonitorConstants.SVS, MonitorConstants.COUNT);

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
	 * when we pass an empty initiator as parameter.
	 * 
	 * @spec MonitorAdmin.startJob(String,String[],int)
	 */
	private void testStartJob010() {
		tbc.log("#testStartJob010");
		MonitoringJob job = null;
		try {

			job = tbc.getMonitorAdmin().startJob("",
					MonitorConstants.SVS, MonitorConstants.COUNT);

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
	 * when we pass invalid characters as StatusVariable parameter.
	 * 
	 * @spec MonitorAdmin.startJob(String,String[],int)
	 */
	private void testStartJob011() {
		tbc.log("#testStartJob011");
		MonitoringJob job = null;
		try {

			job = tbc.getMonitorAdmin().startJob(
					MonitorConstants.INITIATOR,
					new String[] { "cesar/" + MonitorConstants.INVALID_ID },
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

}

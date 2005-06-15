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

import org.osgi.service.monitor.MonitoringJob;
import org.osgi.service.monitor.StatusVariable;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.TestInterface;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @methodUnderTest org.osgi.service.monitor.MonitorAdmin#startJob
 * @generalDescription This class tests startJob method according with MEG
 *                     specification (rfc0084)
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
	 * @testID testStartJob001
	 * @testDescription Tests if a IllegalArgumentException is thrown if the
	 *                  list of StatusVariables is null
	 */
	public void testStartJob001() {
		tbc.log("#testStartJob001");
		try {
			tbc.stopRunningJobs();
						
			MonitoringJob job = tbc.getMonitorAdmin().startJob(MonitorTestControl.INITIATOR, null,
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
	 * @testID testStartJob002
	 * @testDescription Tests if a IllegalArgumentException is thrown if the
	 *                  list of StatusVariables is empty
	 */
	public void testStartJob002() {
		tbc.log("#testStartJob002");
		try {
			tbc.stopRunningJobs();
			
			MonitoringJob job = tbc.getMonitorAdmin().startJob(MonitorTestControl.INITIATOR,
					new String[] {}, MonitorTestControl.COUNT);
			
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
	 * @testID testStartJob003
	 * @testDescription Tests if a IllegalArgumentException is thrown if count
	 *                  parameter is invalid
	 */
	public void testStartJob003() {
		tbc.log("#testStartJob003");
		try {
			tbc.stopRunningJobs();
			
			MonitoringJob job = tbc.getMonitorAdmin().startJob(MonitorTestControl.INITIATOR,
					MonitorTestControl.SVS, MonitorTestControl.INVALID_COUNT);
			
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
	 * @testID testStartJob004
	 * @testDescription Tests if SecurityException is not thrown when the entity
	 *                  that start the job has the MonitorPermission with
	 *                  startjob action and validate all parameters.
	 */
	public void testStartJob004() {
		tbc.log("#testStartJob004");
		PermissionInfo[] infos = null;
		try {
			tbc.stopRunningJobs();
			
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo[] {
					new PermissionInfo(org.osgi.service.monitor.MonitorPermission.class.getName(), MonitorTestControl.SVS[0], org.osgi.service.monitor.MonitorPermission.STARTJOB),	
					new PermissionInfo(org.osgi.service.monitor.MonitorPermission.class.getName(), MonitorTestControl.SVS[1], org.osgi.service.monitor.MonitorPermission.STARTJOB)
			});

			MonitoringJob mj = tbc.getMonitorAdmin().startJob(
					MonitorTestControl.INITIATOR, MonitorTestControl.SVS,
					MonitorTestControl.COUNT);

			tbc.assertNotNull(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_NOT_NULL,
					new String[] { "the returned MonitoringJob" }), mj);

			String init = mj.getInitiator();
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] { "initiator",
							MonitorTestControl.INITIATOR }),
					MonitorTestControl.INITIATOR, init);

			int varNamesLen = mj.getStatusVariableNames().length;
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] { "length of statusvariable names",
							MonitorTestControl.SV_LENGTH+"" }),
					MonitorTestControl.SV_LENGTH, varNamesLen);

			String varName0 = mj.getStatusVariableNames()[0];
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] { "first statusvariable name",
							MonitorTestControl.SVS[0] }),
					MonitorTestControl.SVS[0], varName0);

			String varName1 = mj.getStatusVariableNames()[1];
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] { "second statusvariable name",
							MonitorTestControl.SVS[1] }),
					MonitorTestControl.SVS[1], varName1);

			int repCount = mj.getReportCount();
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] { "count",
							MonitorTestControl.COUNT+"" }), MonitorTestControl.COUNT,
					repCount);

			mj.stop();
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.getPermissionAdmin().setPermissions(
					tbc.getTb1Location(), infos);
		}
	}

	/**
	 * @testID testStartJob005
	 * @testDescription Tests if SecurityException is thrown when the entity has
	 *                  no permission to start a job
	 */
	public void testStartJob005() {
		tbc.log("#testStartJob005");
		PermissionInfo[] infos = null;
		try {
			tbc.stopRunningJobs();
			
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo[] {
					new PermissionInfo(org.osgi.service.monitor.MonitorPermission.class.getName(), MonitorTestControl.SVS[0], org.osgi.service.monitor.MonitorPermission.STARTJOB),	
					new PermissionInfo(org.osgi.service.monitor.MonitorPermission.class.getName(), MonitorTestControl.SVS[1], org.osgi.service.monitor.MonitorPermission.PUBLISH)
			});

			MonitoringJob job = tbc.getMonitorAdmin().startJob(MonitorTestControl.INITIATOR,
					MonitorTestControl.SVS, MonitorTestControl.COUNT);
			
			job.stop();

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
			tbc.getPermissionAdmin().setPermissions(
					tbc.getTb1Location(), infos);
		}
	}

	/**
	 * @testID testStartJob006
	 * @testDescription Tests if SecurityException is thrown when the entity has
	 *                  no permission to start a job for any StatusVariable
	 *                  passed as parameter
	 */
	public void testStartJob006() {
		tbc.log("#testStartJob006");
		PermissionInfo[] infos = null;
		try {
			tbc.stopRunningJobs();
			
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo[] {
					new PermissionInfo(org.osgi.service.monitor.MonitorPermission.class.getName(), MonitorTestControl.SVS[0], null),	
					new PermissionInfo(org.osgi.service.monitor.MonitorPermission.class.getName(), MonitorTestControl.SVS[1], null)
			});

			MonitoringJob mj = tbc.getMonitorAdmin().startJob(MonitorTestControl.INITIATOR,
					MonitorTestControl.SVS, MonitorTestControl.COUNT);

			mj.stop();	
			
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
			tbc.getPermissionAdmin().setPermissions(
					tbc.getTb1Location(), infos);
		}
	}

	/**
	 * @testID testStartJob007
	 * @testDescription Tests if a IllegalArgumentException is thrown if the
	 *                  list of StatusVariables does not supports notification
	 */
	public void testStartJob007() {
		tbc.log("#testStartJob007");
		try {		
			tbc.stopRunningJobs();
			
			MonitoringJob job = tbc.getMonitorAdmin().startJob(MonitorTestControl.INITIATOR,
					MonitorTestControl.SVS_NOT_SUPPORT_NOTIFICATION,
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
	 * @testID testStartJob008
	 * @testDescription Tests if monitoring job notifies the listener after two
	 *                  updates.
	 */
	public synchronized void testStartJob008() {
		tbc.log("#testStartJob008");
		PermissionInfo[] infos = null;
		try {
			tbc.stopRunningJobs();
			
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo[] {
					new PermissionInfo(org.osgi.service.monitor.MonitorPermission.class.getName(), MonitorTestControl.SVS[0], org.osgi.service.monitor.MonitorPermission.STARTJOB),	
					new PermissionInfo(org.osgi.service.monitor.MonitorPermission.class.getName(), MonitorTestControl.SVS[1], org.osgi.service.monitor.MonitorPermission.STARTJOB)
			});
				
			MonitorTestControl.EVENT_COUNT = 0;

			MonitoringJob mj = tbc.getMonitorAdmin().startJob(
					MonitorTestControl.INITIATOR, MonitorTestControl.SVS,
					MonitorTestControl.COUNT + 1);

			tbc.getMonitorListener().updated(
					MonitorTestControl.SV_MONITORABLEID1,
					new StatusVariable(
							MonitorTestControl.SV_NAME1, StatusVariable.CM_CC,
							"test1"));
			wait(MonitorTestControl.TIMEOUT);
			
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] { "variable of event modification",
							0+"" }), 0,
					MonitorTestControl.EVENT_COUNT);

			tbc.getMonitorListener().updated(
					MonitorTestControl.SV_MONITORABLEID1,
					new StatusVariable(
							MonitorTestControl.SV_NAME1, StatusVariable.CM_CC,
							"test1"));
			
			wait(MonitorTestControl.TIMEOUT);
			
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] { "variable of event modification",
							1+"" }), 1,
					MonitorTestControl.EVENT_COUNT);

			mj.stop();
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + " " + e.getClass().getName() + " " + e.getMessage());
		} finally {
			tbc.getPermissionAdmin().setPermissions(
					tbc.getTb1Location(), infos);
		}
	}
	
	/**
	 * @testID testStartJob009
	 * @testDescription Tests if a IllegalArgumentException is thrown if the
	 *                  initiator is null.
	 */
	public void testStartJob009() {
		tbc.log("#testStartJob009");
		try {
			tbc.stopRunningJobs();
			
			MonitoringJob job = tbc.getMonitorAdmin().startJob(null, MonitorTestControl.SVS,
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
	 * @testID testStartJob010
	 * @testDescription Tests if a IllegalArgumentException is thrown if the
	 *                  initiator is empty.
	 */
	public void testStartJob010() {
		tbc.log("#testStartJob010");
		try {
			tbc.stopRunningJobs();
			
			MonitoringJob job = tbc.getMonitorAdmin().startJob("", MonitorTestControl.SVS,
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
	 * @testID testStartJob011
	 * @testDescription Tests if a IllegalArgumentException is thrown if the
	 *                  StatusVariable name is invalid.
	 */
	public void testStartJob011() {
		tbc.log("#testStartJob011");
		try {
			tbc.stopRunningJobs();
			
			MonitoringJob job = tbc.getMonitorAdmin().startJob(MonitorTestControl.INITIATOR, new String[] { "cesar/"+MonitorTestControl.INVALID_ID },
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

}

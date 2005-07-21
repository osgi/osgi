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
 * 11/03/2005   Alexandre Santos
 * 14           Implement MEG TCK
 * ===========  ==============================================================
 * 22/03/2005   Eduardo Oliveira
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
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.TestInterface;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @author Alexandre Alves
 * 
 * This Test Class Validates the implementation of
 * <code>switchEvents<code> method, according to MEG reference
 * documentation.
 */
public class SwitchEvents implements TestInterface {
	private MonitorTestControl tbc;

	public SwitchEvents(MonitorTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testSwitchEvents001();
		testSwitchEvents002();
		testSwitchEvents003();
		testSwitchEvents004();
		testSwitchEvents005();
		testSwitchEvents006();
		testSwitchEvents007();
		testSwitchEvents008();
		testSwitchEvents009();
		testSwitchEvents010();
		testSwitchEvents011();
	}

	/**
	 * This method asserts if IllegalArgumentException is thrown
	 * when we pass invalid characters as path to a statusvariable.
	 * 
	 * @spec MonitorAdmin.switchEvents(String,boolean)
	 */
	private void testSwitchEvents001() {
		tbc.log("#testSwitchEvents001");
		try {

			tbc.getMonitorAdmin().switchEvents(MonitorTestControl.INVALID_ID,
					false);
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
	 * This method asserts if IllegalArgumentException is thrown
	 * when we pass a path that points to a non-existing StatusVariable.
	 * 
	 * @spec MonitorAdmin.switchEvents(String,boolean)
	 */
	private void testSwitchEvents002() {
		tbc.log("#testSwitchEvents002");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorTestControl.INEXISTENT_SVS, MonitorPermission.SWITCHEVENTS));
			
			tbc.getMonitorAdmin().switchEvents(
					MonitorTestControl.INEXISTENT_SVS, false);
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
			tbc.getPermissionAdmin().setPermissions(tbc.getTb1Location(), infos);
		}
	}

	/**
	 * This method asserts that a SecurityException is thrown when
	 * the statusvariable has other action permission.
	 * 
	 * @spec MonitorAdmin.switchEvents(String,boolean)
	 */
	private void testSwitchEvents003() {
		tbc.log("#testSwitchEvents003");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class
					.getName(), MonitorTestControl.SVS[0],
					MonitorPermission.PUBLISH));

			tbc.getMonitorAdmin()
					.switchEvents(MonitorTestControl.SVS[0], false);

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
			tbc.getPermissionAdmin()
					.setPermissions(tbc.getTb1Location(), infos);
		}
	}

	/**
	 * This method asserts that a SecurityException is thrown when
	 * the statusvariable does not have permission.
	 * 
	 * @spec MonitorAdmin.switchEvents(String,boolean)
	 */
	private void testSwitchEvents004() {
		tbc.log("#testSwitchEvents004");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class
					.getName(), MonitorTestControl.SVS[0], null));

			tbc.getMonitorAdmin()
					.switchEvents(MonitorTestControl.SVS[0], false);

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
			tbc.getPermissionAdmin()
					.setPermissions(tbc.getTb1Location(), infos);
		}
	}

	/**
	 * This method asserts if the switchevents is ignored when a
	 * monitoringjob exist with the passed statusvariable.
	 * 
	 * @spec MonitorAdmin.switchEvents(String,boolean)
	 */
	private synchronized void testSwitchEvents005() {
		tbc.log("#testSwitchEvents005");
		PermissionInfo[] infos = null;
		try {
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
									org.osgi.service.monitor.MonitorPermission.STARTJOB),
							new PermissionInfo(MonitorPermission.class
									.getName(), MonitorTestControl.SVS[0],
									MonitorPermission.SWITCHEVENTS) });

			MonitorTestControl.EVENT_COUNT = 0;

			tbc.resetEvent();
			
			tbc.setEventClassCode(0); // listen only for events with listenerId

			MonitoringJob mj = tbc.getMonitorAdmin().startJob(
					MonitorTestControl.INITIATOR, MonitorTestControl.SVS,
					MonitorTestControl.COUNT);			

			tbc.getMonitorAdmin()
					.switchEvents(MonitorTestControl.SVS[0], false);

			tbc.getMonitorListener().updated(
					MonitorTestControl.SV_MONITORABLEID1,
					new StatusVariable(MonitorTestControl.SV_NAME1,
							StatusVariable.CM_CC, "test1"));

			synchronized (tbc) {
				tbc.wait(MonitorTestControl.TIMEOUT);
			}

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"variable of event modification", 1 + "" }), 1,
					MonitorTestControl.EVENT_COUNT);

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
	 * This method asserts if an IllegalArgumentException is thrown
	 * when we pass an empty string as statusvariable.
	 * 
	 * @spec MonitorAdmin.switchEvents(String,boolean)
	 */
	private void testSwitchEvents006() {
		tbc.log("#testSwitchEvents006");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class
					.getName(), MonitorTestControl.SVS[0],
					MonitorPermission.PUBLISH));

			tbc.getMonitorAdmin().switchEvents("", false);
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
			tbc.getPermissionAdmin()
					.setPermissions(tbc.getTb1Location(), infos);
		}
	}

	/**
	 * This method asserts that a SecurityException is thrown when
	 * the caller has permission for other statusvariable.
	 * 
	 * @spec MonitorAdmin.switchEvents(String,boolean)
	 */
	private void testSwitchEvents007() {
		tbc.log("#testSwitchEvents007");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class
					.getName(), MonitorTestControl.SVS[1],
					MonitorPermission.SWITCHEVENTS));

			tbc.getMonitorAdmin()
					.switchEvents(MonitorTestControl.SVS[0], false);

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
			tbc.getPermissionAdmin()
					.setPermissions(tbc.getTb1Location(), infos);
		}
	}

	/**
	 * This method asserts that wildcards are acceptable normally.
	 * 
	 * @spec MonitorAdmin.switchEvents(String,boolean)
	 */
	private synchronized void testSwitchEvents008() {
		tbc.log("#testSwitchEvents008");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo[] { new PermissionInfo(
					MonitorPermission.class.getName(), "*/*",
					MonitorPermission.SWITCHEVENTS) });

			tbc.stopRunningJobs();
			
			MonitorTestControl.SWITCH_EVENTS_COUNT = 0;
			
			tbc.setEventClassCode(1); // listen only for broadcast events

			tbc.getMonitorAdmin().switchEvents("cesar/test*", false);

			tbc.getMonitorListener().updated(
					MonitorTestControl.SV_MONITORABLEID1,
					new StatusVariable(MonitorTestControl.SV_NAME1,
							StatusVariable.CM_CC, "test1"));

			wait(MonitorTestControl.SHORT_TIMEOUT);

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"variable of event modification", 0 + "" }), 0,
					MonitorTestControl.SWITCH_EVENTS_COUNT);

			tbc.getMonitorListener().updated(
					MonitorTestControl.SV_MONITORABLEID2,
					new StatusVariable(MonitorTestControl.SV_NAME1,
							StatusVariable.CM_CC, "test1"));

			synchronized (tbc) {
				tbc.wait(MonitorTestControl.TIMEOUT);
			}

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"variable of event modification", 1 + "" }), 1,
					MonitorTestControl.SWITCH_EVENTS_COUNT);

			tbc.getMonitorAdmin().switchEvents("cesa*/test*", true);

			tbc.getMonitorListener().updated(
					MonitorTestControl.SV_MONITORABLEID1,
					new StatusVariable(MonitorTestControl.SV_NAME1,
							StatusVariable.CM_DER, "test1"));

			synchronized (tbc) {
				tbc.wait(MonitorTestControl.TIMEOUT);
			}

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"variable of event modification", 2 + "" }), 2,
					MonitorTestControl.SWITCH_EVENTS_COUNT);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		} finally {
			tbc.getPermissionAdmin()
					.setPermissions(tbc.getTb1Location(), infos);
		}
	}

	/**
	 * This method asserts that when a StatusVariable is registered, its
	 * event sending state is ON by default. Here, we reinstall the monitorable.
	 * 
	 * @spec MonitorAdmin.switchEvents(String,boolean)
	 */	
	private synchronized void testSwitchEvents009() {
		tbc.log("#testSwitchEvents009");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo[] { new PermissionInfo(
					MonitorPermission.class.getName(), "*/*",
					MonitorPermission.SWITCHEVENTS) });

			tbc.stopRunningJobs();

			tbc.getMonitorAdmin().switchEvents("*/*", false);

			// stop all the monitorables
			tbc.reinstallMonitorable1();

			MonitorTestControl.SWITCH_EVENTS_COUNT = 0;
			
			tbc.setEventClassCode(1); // listen only for broadcast events

			tbc.getMonitorListener().updated(
					MonitorTestControl.SV_MONITORABLEID1,
					new StatusVariable(MonitorTestControl.SV_NAME1,
							StatusVariable.CM_CC, "test1"));

			synchronized (tbc) {
				tbc.wait(MonitorTestControl.TIMEOUT);
			}

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"variable of event modification", 1 + "" }), 1,
					MonitorTestControl.SWITCH_EVENTS_COUNT);

			tbc.getMonitorListener().updated(
					MonitorTestControl.SV_MONITORABLEID2,
					new StatusVariable(MonitorTestControl.SV_NAME1,
							StatusVariable.CM_CC, "test1"));
			
			wait(MonitorTestControl.SHORT_TIMEOUT);

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"variable of event modification", 1 + "" }), 1,
					MonitorTestControl.SWITCH_EVENTS_COUNT);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		} finally {
			tbc.getPermissionAdmin()
					.setPermissions(tbc.getTb1Location(), infos);
		}
	}

	/**
	 * This method asserts if IllegalArgumentException is thrown
	 * when we pass an invalid target.
	 * 
	 * @spec MonitorAdmin.switchEvents(String,boolean)
	 */	
	private void testSwitchEvents010() {
		tbc.log("#testSwitchEvents010");
		try {
			tbc.setLocalPermission(new PermissionInfo[] { new PermissionInfo(
					MonitorPermission.class.getName(), "*/*",
					MonitorPermission.SWITCHEVENTS) });

			tbc.getMonitorAdmin().switchEvents("*esar/*", false);
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
	 * This method asserts that when a StatusVariable is registered, its
	 * event sending state is ON by default. Here, we uninstall, call switchevents
	 * and then install the monitorables.
	 * 
	 * @spec MonitorAdmin.switchEvents(String,boolean)
	 */	
	private synchronized void testSwitchEvents011() {
		tbc.log("#testSwitchEvents011");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo[] { new PermissionInfo(
					MonitorPermission.class.getName(), "*/*",
					MonitorPermission.SWITCHEVENTS) });

			tbc.stopRunningJobs();

			tbc.stopMonitorables();

			tbc.getMonitorAdmin().switchEvents("cesa*/*", false);

			tbc.installMonitorables();

			MonitorTestControl.SWITCH_EVENTS_COUNT = 0;
			
			tbc.setEventClassCode(1); // listen only for broadcast events

			tbc.getMonitorListener().updated(
					MonitorTestControl.SV_MONITORABLEID1,
					new StatusVariable(MonitorTestControl.SV_NAME1,
							StatusVariable.CM_CC, "test1"));

			synchronized (tbc) {
				tbc.wait(MonitorTestControl.TIMEOUT);
			}

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"variable of event modification", 1 + "" }), 1,
					MonitorTestControl.SWITCH_EVENTS_COUNT);

			tbc.getMonitorListener().updated(
					MonitorTestControl.SV_MONITORABLEID2,
					new StatusVariable(MonitorTestControl.SV_NAME1,
							StatusVariable.CM_CC, "test1"));
			
			synchronized (tbc) {
				tbc.wait(MonitorTestControl.TIMEOUT);
			}			

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"variable of event modification", 2 + "" }), 2,
					MonitorTestControl.SWITCH_EVENTS_COUNT);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		} finally {
			tbc.getPermissionAdmin()
					.setPermissions(tbc.getTb1Location(), infos);
		}
	}

}
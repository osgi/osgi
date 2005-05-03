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
 */
package org.osgi.test.cases.monitor.tb1.MonitorAdmin;

import org.osgi.service.monitor.MonitorPermission;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.TestInterface;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @methodUnderTest org.osgi.service.monitor.MonitorAdmin#switchEvents
 * @generalDescription This class tests switchEvents method according with MEG
 *                     specification (rfc0084)
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
	}

	/**
	 * @testID testSwitchEvents001
	 * @testDescription Asserts if IllegalArgumentException is thrown when use
	 *                  invalid characters.
	 */
	public void testSwitchEvents001() {
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
	 * @testID testSwitchEvents002
	 * @testDescription Tests if a IllegalArgumentException is thrown when use a
	 *                  StatusVariable that doesn't exist.
	 */
	public void testSwitchEvents002() {
		try {
			tbc.getMonitorAdmin().switchEvents(
					MonitorTestControl.SVS_DONT_EXIST, false);
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
	 * @testID testSwitchEvents003
	 * @testDescription Asserts if a SecurityException is thrown when the caller
	 *                  has no switchevents permission
	 */
	public void testSwitchEvents003() {
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorTestControl.SVS[0], MonitorPermission.PUBLISH));
			
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
			tbc.getPermissionAdmin().setPermissions(
					tbc.getTb1Location(), infos);
		}
	}

	/**
	 * @testID testSwitchEvents004
	 * @testDescription Asserts if a SecurityException is thrown when the caller
	 *                  has no switchevents permission
	 */
	public void testSwitchEvents004() {
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorTestControl.SVS[0], null));

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
			tbc.getPermissionAdmin().setPermissions(
					tbc.getTb1Location(), infos);
		}
	}

	/**
	 * @testID testSwitchEvents005
	 * @testDescription Asserts if a SecurityException is not thrown when the
	 *                  caller has switchevents permission
	 */
	public void testSwitchEvents005() {
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorTestControl.SVS[0], MonitorPermission.SWITCHEVENTS));

			tbc.getMonitorAdmin()
					.switchEvents(MonitorTestControl.SVS[0], false);

			tbc.pass("SwitchEvents correctly executed.");

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		} finally {
			tbc.getPermissionAdmin().setPermissions(
					tbc.getTb1Location(), infos);
		}
	}

}

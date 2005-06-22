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
 * 11/03/2005   Eduardo Oliveira
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

import org.osgi.service.monitor.MonitorPermission;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.TestInterface;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @methodUnderTest org.osgi.service.monitor.MonitorAdmin#getMonitorableNames
 * @generalDescription This class tests GetMonitorableNames method according
 *                     with MEG specification (rfc0084)
 */
public class GetMonitorableNames implements TestInterface {
	private MonitorTestControl tbc;

	public GetMonitorableNames(MonitorTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetMonitorableNames001();
		testGetMonitorableNames002();
		testGetMonitorableNames003();
		testGetMonitorableNames004();
		testGetMonitorableNames005();
		testGetMonitorableNames006();
	}

	/**
	 * @testID testGetMonitorableNames001
	 * @testDescription Asserts if a SecurityException is thrown when the caller
	 *                  has no discover permission
	 */
	public void testGetMonitorableNames001() {
		tbc.log("#testGetMonitorableNames001");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),"*/*", MonitorPermission.RESET));

			tbc.getMonitorAdmin().getMonitorableNames();

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
	 * @testID testGetMonitorableNames002
	 * @testDescription Tests if the MonitorAdmin returns the monitorables that
	 *                  was registered.
	 */
	public void testGetMonitorableNames002() {
		tbc.log("#testGetMonitorableNames002");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),"*/*", MonitorTestControl.DISCOVER));

			String[] monitorableNames = tbc.getMonitorAdmin()
					.getMonitorableNames();

			boolean hasMon1 = false;
			boolean hasMon2 = false;
			for (int i = 0; i < monitorableNames.length
					&& !(hasMon1 && hasMon2); i++) {
				if (monitorableNames[i]
						.equals(MonitorTestControl.SV_MONITORABLEID1)) {
					hasMon1 = true;
				} else if (monitorableNames[i]
						.equals(MonitorTestControl.SV_MONITORABLEID2)) {
					hasMon2 = true;
				}
			}

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "MonitorAdmin returns the monitorables that was registered." }),
							hasMon1 && hasMon2);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.getPermissionAdmin().setPermissions(
					tbc.getTb1Location(), infos);
		}
	}

	/**
	 * @testID testGetMonitorableNames003
	 * @testDescription Tests the alphabetical order of the returned monitorable
	 *                  names.
	 */
	public void testGetMonitorableNames003() {
		tbc.log("#testGetMonitorableNames003");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),"*/*", MonitorTestControl.DISCOVER));

			String[] monitorableNames = tbc.getMonitorAdmin()
					.getMonitorableNames();

			boolean passed = true;
			for (int i = 0; i < monitorableNames.length - 1; i++) {
				if (monitorableNames[i].compareTo(monitorableNames[i + 1]) > 0) {
					passed = false;
				}
			}

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "The returned monitorable names is in alphabetical order." }),
							passed);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.getPermissionAdmin().setPermissions(
					tbc.getTb1Location(), infos);
		}
	}
	
	/**
	 * @testID testGetMonitorableNames004
	 * @testDescription Asserts if a SecurityException is thrown when the caller
	 *                  has no discover permission
	 */	
	public void testGetMonitorableNames004() {
		tbc.log("#testGetMonitorableNames004");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),"*/*", null));

			tbc.getMonitorAdmin().getMonitorableNames();
			
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
	 * @testID testGetMonitorableNames005
	 * @testDescription Tests if the returned monitorables name list is empty.
	 */
	public void testGetMonitorableNames005() {
		tbc.log("#testGetMonitorableNames005");
		PermissionInfo[] infos = null;
		tbc.stopMonitorables();
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),"*/*", MonitorTestControl.DISCOVER));

			String[] monitorableNames = tbc.getMonitorAdmin()
					.getMonitorableNames();
			
			tbc.assertTrue("Asserting if the returned monitorables names list is empty. ", (monitorableNames.length==0));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.installMonitorables();
			tbc.getPermissionAdmin().setPermissions(
					tbc.getTb1Location(), infos);
		}
	}
	
	/**
	 * @testID testGetMonitorableNames006
	 * @testDescription Asserts if a SecurityException is thrown when the caller
	 *                  has discover permission but does not use the correct target
	 */
	public void testGetMonitorableNames006() {
		tbc.log("#testGetMonitorableNames006");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorTestControl.SVS[0], MonitorTestControl.DISCOVER));

			tbc.getMonitorAdmin().getMonitorableNames();

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
	
}
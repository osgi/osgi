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
 * 24/03/2005   Alexandre Alves
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
 * @methodUnderTest org.osgi.service.monitor.MonitorAdmin#getStatusVariableNames
 * @generalDescription This class tests getStatusVariableNames method according with MEG
 *                     specification (rfc0084)
 */
public class GetStatusVariableNames implements TestInterface {
	private MonitorTestControl tbc;

	public GetStatusVariableNames(MonitorTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetStatusVariableNames001();
		testGetStatusVariableNames002();
		testGetStatusVariableNames003();
		testGetStatusVariableNames004();
		testGetStatusVariableNames005();
		testGetStatusVariableNames006();
		testGetStatusVariableNames007();
		testGetStatusVariableNames008();
		testGetStatusVariableNames009();
	}

	/**
	 * @testID testGetStatusVariableNames001
	 * @testDescription Tests if IllegalArgumentException is thrown when
	 *                  monitorableId is null
	 */
	public void testGetStatusVariableNames001() {
		try {
			tbc.getMonitorAdmin().getStatusVariableNames(null);

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
	 * @testID testGetStatusVariableNames002
	 * @testDescription Tests if IllegalArgumentException is thrown when
	 *                  monitorableId is invalid
	 */
	public void testGetStatusVariableNames002() {
		try {
			tbc.getMonitorAdmin().getStatusVariableNames(
					MonitorTestControl.INVALID_ID);

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
	 * @testID testGetStatusVariableNames003
	 * @testDescription Tests if IllegalArgumentException is thrown when
	 *                  monitorableId points to a non-existing id
	 */
	public void testGetStatusVariableNames003() {
		try {
			tbc.getMonitorAdmin().getStatusVariableNames(
					MonitorTestControl.INVALID_MONITORABLE_SV);

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
	 * @testID testGetStatusVariableNames004
	 * @testDescription Tests if monitorable_id parameter is valid
	 */
	public void testGetStatusVariableNames004() {
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),"*/*", MonitorPermission.DISCOVER));

			String[] sv = tbc.getMonitorAdmin().getStatusVariableNames(
					MonitorTestControl.SV_MONITORABLEID1);

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"the first statusvariable name",
							MonitorTestControl.SV_NAME1 }),
					MonitorTestControl.SV_NAME1, sv[0]);

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] { "second statusvariable name",
							MonitorTestControl.SV_NAME2 }),
					MonitorTestControl.SV_NAME2, sv[1]);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		} finally {
			tbc.getPermissionAdmin().setPermissions(
					tbc.getTb1Location(), infos);
		}
	}

	/**
	 * @testID testGetStatusVariableNames005
	 * @testDescription Tests if status variables names array has the same size
	 *                  of status variables defined for monitorable id cesar.
	 */
	public void testGetStatusVariableNames005() {
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),"*/*", MonitorPermission.DISCOVER));

			String[] sv = tbc.getMonitorAdmin().getStatusVariableNames(
					MonitorTestControl.SV_MONITORABLEID1);

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"length of statusvariable names", MonitorTestControl.SV_LENGTH+"" }),
					MonitorTestControl.SV_LENGTH, sv.length);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		} finally {
			tbc.getPermissionAdmin().setPermissions(
					tbc.getTb1Location(), infos);
		}
	}

	/**
	 * @testID testGetStatusVariableNames006
	 * @testDescription Tests if the MonitorAdmin returns the statusvariables
	 *                  that was registered in monitorable implementation.
	 */
	public void testGetStatusVariableNames006() {
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),"*/*", MonitorPermission.DISCOVER));
			
			String[] sv = tbc.getMonitorAdmin().getStatusVariableNames(
					MonitorTestControl.SV_MONITORABLEID1);

			boolean passed = true;
			for (int i = 0; i < sv.length; i++) {
				if ((!sv[i].equals(MonitorTestControl.SV_NAME1))
						&& (!sv[i].equals(MonitorTestControl.SV_NAME2))) {
					passed = false;
				}
			}

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "MonitorAdmin returns the statusVariables that was registered in monitorable implementation." }),
							passed);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		} finally {
			tbc.getPermissionAdmin().setPermissions(
					tbc.getTb1Location(), infos);
		}
	}

	/**
	 * @testID testGetStatusVariableNames007
	 * @testDescription Tests the alphabetical order of the returned
	 *                  statusVariables names.
	 */
	public void testGetStatusVariableNames007() {
		PermissionInfo[] infos = null;
		try {

			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),"*/*", MonitorPermission.DISCOVER));

			String[] sv = tbc.getMonitorAdmin().getStatusVariableNames(
					MonitorTestControl.SV_MONITORABLEID1);

			boolean passed = true;
			for (int i = 0; i < sv.length - 1; i++) {
				if (sv[i].compareTo(sv[i + 1]) > 0) {
					passed = false;
				}
			}

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "The returned statusVariables names is in alphabetical order." }),
							passed);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		} finally {
			tbc.getPermissionAdmin().setPermissions(
					tbc.getTb1Location(), infos);
		}
	}

	/**
	 * @testID testGetStatusVariableNames008
	 * @testDescription Asserts if a SecurityException is thrown when the caller
	 *                  has no discover permission
	 */
	public void testGetStatusVariableNames008() {
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),"*/*", MonitorPermission.PUBLISH));

			tbc.getMonitorAdmin().getStatusVariableNames(
					MonitorTestControl.SV_MONITORABLEID1);

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
	 * @testID testGetStatusVariableNames009
	 * @testDescription Asserts if a SecurityException is thrown when the caller
	 *                  has no discover permission
	 */
	public void testGetStatusVariableNames009() {
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),"*/*", null));

			tbc.getMonitorAdmin().getStatusVariableNames(
					MonitorTestControl.SV_MONITORABLEID1);

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

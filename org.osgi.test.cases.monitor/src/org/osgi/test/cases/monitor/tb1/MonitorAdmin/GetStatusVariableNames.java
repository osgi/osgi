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
 * May 20, 2005	Alexandre Alves
 * 92           Make changes according to monitor RFC updates
 * ===========  ==============================================================
 */
package org.osgi.test.cases.monitor.tb1.MonitorAdmin;

import org.osgi.service.monitor.MonitorPermission;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.TestInterface;
import org.osgi.test.cases.monitor.tbc.Activators.MonitorableActivatorInvalid;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @author Alexandre Alves
 * 
 * This Test Class Validates the implementation of
 * <code>getStatusVariableNames<code> method, according to MEG reference
 * documentation.
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
	 * This method asserts that an IllegalArgumentException is thrown
	 * when we pass null as parameter.
	 * 
	 * @spec MonitorAdmin.getStatusVariableNames(string)
	 */
	private void testGetStatusVariableNames001() {
		tbc.log("#testGetStatusVariableNames001");
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
	 * This method asserts that an IllegalArgumentException is thrown
	 * when we pass invalid characters as parameter.
	 * 
	 * @spec MonitorAdmin.getStatusVariableNames(string)
	 */
	private void testGetStatusVariableNames002() {
		tbc.log("#testGetStatusVariableNames002");
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
	 * This method asserts that an IllegalArgumentException is thrown
	 * when we pass a path that points to a non-existing monitorable.
	 * 
	 * @spec MonitorAdmin.getStatusVariableNames(string)
	 */
	private void testGetStatusVariableNames003() {
		tbc.log("#testGetStatusVariableNames003");
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
	 * This method asserts that all monitorables registered
	 * is returned in alphabetical order.
	 * 
	 * @spec MonitorAdmin.getStatusVariableNames(string)
	 */
	private void testGetStatusVariableNames004() {
		tbc.log("#testGetStatusVariableNames004");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class
					.getName(), "*/*", MonitorPermission.READ));

			String[] sv = tbc.getMonitorAdmin().getStatusVariableNames(
					MonitorTestControl.SV_MONITORABLEID1);

			tbc.assertTrue(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"length of statusvariable names",
							MonitorTestControl.SV_LENGTH + "" }),
					(MonitorTestControl.SV_LENGTH == sv.length));

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"the first statusvariable name",
							MonitorTestControl.SV_NAME1 }),
					MonitorTestControl.SV_NAME1, sv[0]);

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"second statusvariable name",
							MonitorTestControl.SV_NAME2 }),
					MonitorTestControl.SV_NAME2, sv[1]);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		} finally {
			tbc.getPermissionAdmin()
					.setPermissions(tbc.getTb1Location(), infos);
		}
	}

	/**
	 * This method asserts that no statusvariable names is 
	 * returned when the caller has no read permission.
	 * 
	 * @spec MonitorAdmin.getStatusVariableNames(string)
	 */
	private void testGetStatusVariableNames005() {
		tbc.log("#testGetStatusVariableNames005");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class
					.getName(), "*/*", null));

			String[] sv = tbc.getMonitorAdmin().getStatusVariableNames(
					MonitorTestControl.SV_MONITORABLEID1);

			tbc
					.assertEquals(
							"Asserting if an empty vector of StatusVariable names is returned.",
							0, sv.length);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		} finally {
			tbc.getPermissionAdmin()
					.setPermissions(tbc.getTb1Location(), infos);
		}
	}

	/**
	 * This method asserts that no statusvariable names is 
	 * returned when the caller has read permission for other monitorable.
	 * 
	 * @spec MonitorAdmin.getStatusVariableNames(string)
	 */
	private void testGetStatusVariableNames006() {
		tbc.log("#testGetStatusVariableNames006");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class
					.getName(), MonitorTestControl.SV_MONITORABLEID2 + "/*",
					MonitorPermission.READ));

			String[] sv = tbc.getMonitorAdmin().getStatusVariableNames(
					MonitorTestControl.SV_MONITORABLEID1);

			tbc.assertNotNull("Asserting that a non-null value is returned by getStatusVariableNames().", sv);
			
			tbc
					.assertEquals(
							"Asserting if an empty vector of StatusVariable names is returned when the caller has read permission for other monitorable.",
							0, sv.length);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		} finally {
			tbc.getPermissionAdmin()
					.setPermissions(tbc.getTb1Location(), infos);
		}
	}

	/**
	 * This method asserts that only the statusvariable that 
	 * we have set permission is returned.
	 * 
	 * @spec MonitorAdmin.getStatusVariableNames(string)
	 */
	private void testGetStatusVariableNames007() {
		tbc.log("#testGetStatusVariableNames007");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class
					.getName(), MonitorTestControl.SVS[0],
					MonitorPermission.READ));

			String[] sv = tbc.getMonitorAdmin().getStatusVariableNames(
					MonitorTestControl.SV_MONITORABLEID1);

			tbc
					.assertEquals(
							"Asserting if only the specified statusvariable is returned.",
							1, sv.length);

			tbc
					.assertEquals(
							"Asserting if the returned statusvariable has the expected name.",
							MonitorTestControl.SV_NAME1, sv[0]);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		} finally {
			tbc.getPermissionAdmin()
					.setPermissions(tbc.getTb1Location(), infos);
		}
	}

	/**
	 * This method asserts that only the statusvariable that 
	 * has publish action permission set by its monitorable is returned
	 * even if all statusvariable has read action permission.
	 * 
	 * @spec MonitorAdmin.getStatusVariableNames(string)
	 */
	private void testGetStatusVariableNames008() {
		tbc.log("#testGetStatusVariableNames008");
		PermissionInfo[] infosTb1 = null;
		PermissionInfo[] infosTb3 = null;
		try {
			infosTb1 = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			infosTb3 = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb3Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class
					.getName(), "*/*", MonitorPermission.READ));

			tbc.setLocalPermission("cesar2/test0",
					org.osgi.service.monitor.MonitorPermission.PUBLISH);

			String[] statusVariables = tbc.getMonitorAdmin()
					.getStatusVariableNames(
							MonitorTestControl.SV_MONITORABLEID2);

			tbc.assertTrue("Asserting if only one StatusVariable is returned.",
					(statusVariables.length == 1));

			tbc
					.assertEquals(
							"Asserting if the MonitorAdmin returns the StatusVariable that we expect.",
							MonitorTestControl.SV_NAME1, statusVariables[0]);

			tbc.setLocalPermission(MonitorTestControl.SV_MONITORABLEID1 + "/*",
					org.osgi.service.monitor.MonitorPermission.PUBLISH);

			statusVariables = tbc.getMonitorAdmin()
					.getStatusVariableNames(
							MonitorTestControl.SV_MONITORABLEID1);

			tbc.assertTrue("Asserting if two StatusVariables are returned.",
					(statusVariables.length == 2));

			tbc
					.assertEquals(
							"Asserting if the MonitorAdmin returns the StatusVariable that we expect.",
							MonitorTestControl.SV_NAME1, statusVariables[0]);
			
			tbc
			.assertEquals(
					"Asserting if the MonitorAdmin returns the StatusVariable that we expect.",
					MonitorTestControl.SV_NAME2, statusVariables[1]);			

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		} finally {
			tbc.getPermissionAdmin().setPermissions(tbc.getTb1Location(),
					infosTb1);
			tbc.getPermissionAdmin().setPermissions(tbc.getTb3Location(),
					infosTb3);
		}
	}
	
	/**
	 * This method asserts that if our monitorable
	 * returns null, MonitorAdmin must returns a empty array instead of throws
	 * NullPointerException.
	 * 
	 * @spec MonitorAdmin.getStatusVariableNames(string)
	 */
	private void testGetStatusVariableNames009() {
		tbc.log("#testGetStatusVariableNames009");
		PermissionInfo[] infos = null;
		MonitorableActivatorInvalid monitorableActivator = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class
					.getName(), "*/*",
					MonitorPermission.READ));
			
			monitorableActivator = new MonitorableActivatorInvalid(tbc, MonitorTestControl.INVALID_MONITORABLE_SV);
			monitorableActivator.start(tbc.getContext());
			

			String[] sv = tbc.getMonitorAdmin().getStatusVariableNames(
					MonitorTestControl.INVALID_MONITORABLE_SV);

			tbc
					.assertNotNull("Asserting that a non-null value was returned by MonitorAdmin.", sv);
			
			tbc
			.assertTrue("Asserting that an empty array was returned by MonitorAdmin.", (sv.length==0));			

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		} finally {
			try {
				monitorableActivator.stop(tbc.getContext());
			} catch (Exception e1) {
				tbc.log("error on stopping the monitorable.");
			}
			tbc.getPermissionAdmin()
					.setPermissions(tbc.getTb1Location(), infos);
		}
	}	

}

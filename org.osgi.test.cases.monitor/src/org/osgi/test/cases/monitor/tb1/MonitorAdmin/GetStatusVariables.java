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
 * 24/03/2005   Alexandre Alves
 * 14           Updates after formal inspection (JSTD-MEGTCK-CODE-INSP011)
 * ===========  ==============================================================
 * May 20, 2005	Alexandre Alves
 * 92           Make changes according to monitor RFC updates
 * ===========  ==============================================================
 */
package org.osgi.test.cases.monitor.tb1.MonitorAdmin;

import org.osgi.service.monitor.MonitorPermission;
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
 * <code>getStatusVariables</code> method, according to MEG reference
 * documentation.
 */
public class GetStatusVariables implements TestInterface {
	private MonitorTestControl tbc;

	public GetStatusVariables(MonitorTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetStatusVariables001();
		testGetStatusVariables002();
		testGetStatusVariables003();
		testGetStatusVariables004();
		testGetStatusVariables005();
		testGetStatusVariables006();
		testGetStatusVariables007();
		testGetStatusVariables008();
	}

	/**
	 * This method asserts that all StatusVariable of the
	 * specific Monitorable is returned.
	 * 
	 * @spec MonitorAdmin.getStatusVariables(string)
	 */
	private void testGetStatusVariables001() {
		tbc.log("#testGetStatusVariables001");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class
					.getName(), MonitorConstants.SV_MONITORABLEID1 + "/*",
					MonitorPermission.READ));

			StatusVariable[] statusVariables = tbc.getMonitorAdmin()
					.getStatusVariables(MonitorConstants.SV_MONITORABLEID1);

			tbc.assertEquals("Asserting if two StatusVariables was returned.",
					MonitorConstants.SV_LENGTH, statusVariables.length);

			tbc
					.assertTrue(
							"Asserting the name of the first StatusVariable.",
							((statusVariables[0].getID()
									.equals(MonitorConstants.SV_NAME1)) || (statusVariables[0].getID()
									.equals(MonitorConstants.SV_NAME2))));
			
			tbc
			.assertTrue(
					"Asserting the name of the second StatusVariable.",
					((statusVariables[1].getID()
							.equals(MonitorConstants.SV_NAME1)) || (statusVariables[1].getID()
							.equals(MonitorConstants.SV_NAME2))));			

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		} finally {
			tbc.setTb1Permission(infos);
		}
	}

	/**
	 * This method asserts if IllegalArgumentException is thrown
	 * when we pass null as parameter.
	 * 
	 * @spec MonitorAdmin.getStatusVariables(string)
	 */
	private void testGetStatusVariables002() {
		tbc.log("#testGetStatusVariables002");
		try {
			tbc.getMonitorAdmin().getStatusVariables(null);

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
	 * when we pass invalid characters as parameter.
	 * 
	 * @spec MonitorAdmin.getStatusVariables(string)
	 */
	private void testGetStatusVariables003() {
		tbc.log("#testGetStatusVariables003");
		try {
			tbc.getMonitorAdmin().getStatusVariables(
					MonitorConstants.INVALID_ID);

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
	 * when we pass a path that points to a non-existing Monitorable.
	 * 
	 * @spec MonitorAdmin.getStatusVariables(string)
	 */
	private void testGetStatusVariables004() {
		tbc.log("#testGetStatusVariables004");
		try {
			tbc.getMonitorAdmin().getStatusVariables(
					MonitorConstants.INEXISTENT_MONITORABLE);

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
	 * This method asserts that no StatusVariable is 
	 * returned when the caller has no read permission.
	 * 
	 * @spec MonitorAdmin.getStatusVariables(string)
	 */
	private void testGetStatusVariables005() {
		tbc.log("#testGetStatusVariables005");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class
					.getName(), "*/*", ""));

			StatusVariable[] statusVariables = tbc.getMonitorAdmin()
					.getStatusVariables(MonitorConstants.SV_MONITORABLEID1);

			tbc
					.assertTrue(
							"Asserting if an empty array is returned when the caller has no read permission.",
							(statusVariables.length == 0));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		} finally {
			tbc.setTb1Permission(infos);
		}
	}

	/**
	 * This method asserts that no StatusVariable is 
	 * returned when the caller has read permission for other Monitorable.
	 * 
	 * @spec MonitorAdmin.getStatusVariables(string)
	 */
	private void testGetStatusVariables006() {
		tbc.log("#testGetStatusVariables006");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class
					.getName(), MonitorConstants.SV_MONITORABLEID2 + "/*",
					MonitorPermission.READ));

			StatusVariable[] statusVariables = tbc.getMonitorAdmin()
					.getStatusVariables(MonitorConstants.SV_MONITORABLEID1);

			tbc
					.assertTrue(
							"Asserting if an empty array is returned when the caller has read permission for other monitorable.",
							(statusVariables.length == 0));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		} finally {
			tbc.setTb1Permission(infos);
		}
	}

	/**
	 * This method asserts if the only StatusVariable that
	 * we have set read permission is returned.
	 * 
	 * @spec MonitorAdmin.getStatusVariables(string)
	 */
	private void testGetStatusVariables007() {
		tbc.log("#testGetStatusVariables007");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class
					.getName(), MonitorConstants.SVS[0],
					MonitorPermission.READ));

			StatusVariable[] statusVariables = tbc.getMonitorAdmin()
					.getStatusVariables(MonitorConstants.SV_MONITORABLEID1);

			tbc.assertTrue("Asserting if only one StatusVariable is returned.",
					(statusVariables.length == 1));

			tbc
					.assertEquals(
							"Asserting if the MonitorAdmin returns the StatusVariable that we expect.",
							MonitorConstants.SV_NAME1,
							statusVariables[0].getID());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		} finally {
			tbc.setTb1Permission(infos);
		}
	}
	
	/**
	 * This method asserts if the only StatusVariable that
	 * has publish permission for the Monitorable is returned.
	 * 
	 * @spec MonitorAdmin.getStatusVariables(string)
	 */
	private void testGetStatusVariables008() {
		tbc.log("#testGetStatusVariables008");
		PermissionInfo[] infosTb1 = null;
		PermissionInfo[] infosTb3 = null;
		try {
			infosTb1 = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());
			
			infosTb3 = tbc.getPermissionAdmin().getPermissions(tbc.getTb3Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class
					.getName(), "*/*",
					MonitorPermission.READ));
			
			tbc.setLocalPermission("cesar2/test0", org.osgi.service.monitor.MonitorPermission.PUBLISH);

			StatusVariable[] statusVariables = tbc.getMonitorAdmin()
					.getStatusVariables(MonitorConstants.SV_MONITORABLEID2);

			tbc.assertTrue("Asserting if only one StatusVariable is returned.",
					(statusVariables.length == 1));

			tbc
					.assertEquals(
							"Asserting if the MonitorAdmin returns the StatusVariable that we expect.",
							MonitorConstants.SV_NAME1,
							statusVariables[0].getID());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		} finally {
			tbc.setTb1Permission(infosTb1);
			tbc.setTb3Permission(infosTb3);
		}
	}	

}

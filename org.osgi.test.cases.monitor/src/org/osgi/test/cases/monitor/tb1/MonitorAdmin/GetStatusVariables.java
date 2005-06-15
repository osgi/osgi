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
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.TestInterface;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @methodUnderTest org.osgi.service.monitor.MonitorAdmin#getStatusVariables
 * @generalDescription This class tests getStatusVariables method according with
 *                     MEG specification (rfc0084)
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
		testGetStatusVariables009();
	}

	/**
	 * @testID testGetStatusVariables001
	 * @testDescription Tests the length of the returned statusvariables.
	 */
	public void testGetStatusVariables001() {
		tbc.log("#testGetStatusVariables001");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());
			
			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorTestControl.SV_MONITORABLEID1+"/*", MonitorPermission.READ));

			StatusVariable[] statusVariables = tbc.getMonitorAdmin()
					.getStatusVariables(MonitorTestControl.SV_MONITORABLEID1);

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"length of statusvariables",
							MonitorTestControl.SV_LENGTH+"" }),
					MonitorTestControl.SV_LENGTH, statusVariables.length);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.getPermissionAdmin().setPermissions(
					tbc.getTb1Location(), infos);
		}
	}

	/**
	 * @testID testGetStatusVariables002
	 * @testDescription Tests if the StatusVariables returned was the expected.
	 */
	public void testGetStatusVariables002() {
		tbc.log("#testGetStatusVariables002");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorTestControl.SV_MONITORABLEID1+"/*", MonitorPermission.READ));

			StatusVariable[] statusVariables = tbc.getMonitorAdmin()
					.getStatusVariables(MonitorTestControl.SV_MONITORABLEID1);

			boolean hasN1 = false;
			boolean hasN2 = false;
			for (int i = 0; i < statusVariables.length && !(hasN1 && hasN2); i++) {
				if (statusVariables[i].getID().equals(
						MonitorTestControl.SV_NAME1)) {
					hasN1 = true;
				} else if (statusVariables[i].getID().equals(
						MonitorTestControl.SV_NAME2)) {
					hasN2 = true;
				}
			}

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "all the StatusVariable names is in the StatusVariable list returned by monitorable." }),
							hasN1 && hasN2);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.getPermissionAdmin().setPermissions(
					tbc.getTb1Location(), infos);
		}
	}

	/**
	 * @testID testGetStatusVariables003
	 * @testDescription Tests if IllegalArgumentException is thrown when null is
	 *                  passed as parameter
	 */
	public void testGetStatusVariables003() {
		tbc.log("#testGetStatusVariables003");
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
	 * @testID testGetStatusVariables004
	 * @testDescription Tests if IllegalArgumentException is thrown when an
	 *                  invalid id is passed as parameter
	 */
	public void testGetStatusVariables004() {
		tbc.log("#testGetStatusVariables004");
		try {
			tbc.getMonitorAdmin().getStatusVariables(
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
	 * @testID testGetStatusVariables005
	 * @testDescription Tests if IllegalArgumentException is thrown when n path
	 *                  that doesn't exist is passed as parameter
	 */
	public void testGetStatusVariables005() {
		tbc.log("#testGetStatusVariables005");
		try {
			tbc.getMonitorAdmin().getStatusVariables(
					MonitorTestControl.VALID_ID);

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
	 * @testID testGetStatusVariables006
	 * @testDescription Asserts if a SecurityException is thrown when the caller
	 *                  has no read permission
	 */
	public void testGetStatusVariables006() {
		tbc.log("#testGetStatusVariables006");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorTestControl.SV_MONITORABLEID1+"/*", MonitorPermission.PUBLISH));

			tbc.getMonitorAdmin().getStatusVariables(MonitorTestControl.SV_MONITORABLEID1);

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
	 * @testID testGetStatusVariables007
	 * @testDescription Asserts if a SecurityException is thrown when the caller
	 *                  has no read permission
	 */
	public void testGetStatusVariables007() {
		tbc.log("#testGetStatusVariables007");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorTestControl.SV_MONITORABLEID1+"/*", null));

			tbc.getMonitorAdmin().getStatusVariables(MonitorTestControl.SV_MONITORABLEID1);

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
	 * @testID testGetStatusVariables008
	 * @testDescription Tests if IllegalArgumentException is thrown when an empty
	 *                  string is passed as parameter.
	 */
	public void testGetStatusVariables008() {
		tbc.log("#testGetStatusVariables008");
		try {
			tbc.getMonitorAdmin().getStatusVariables("");

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
	 * @testID testGetStatusVariables009
	 * @testDescription Asserts if a SecurityException is thrown when the caller
	 *                  has read permission for only one of two statusvariables.
	 */
	public void testGetStatusVariables009() {
		tbc.log("#testGetStatusVariables009");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorTestControl.SVS[0], null));

			tbc.getMonitorAdmin().getStatusVariables(MonitorTestControl.SV_MONITORABLEID1);

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

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
 * May 20, 2005	Alexandre Alves
 * 92           Make changes according to monitor RFC updates
 * ===========  ==============================================================
 */
package org.osgi.test.cases.monitor.tb1.MonitorAdmin;

import junit.framework.*;

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
 * <code>getStatusVariable</code> method, according to MEG reference
 * documentation.
 */
public class GetStatusVariable implements TestInterface {
	private MonitorTestControl tbc;

	public GetStatusVariable(MonitorTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetStatusVariable001();
		testGetStatusVariable002();
		testGetStatusVariable003();
		testGetStatusVariable004();
		testGetStatusVariable005();
		testGetStatusVariable006();
		testGetStatusVariable007();
	}

	/**
	 * This method asserts that an IllegalArgumentException is thrown
	 * when we pass null as parameter.
	 * 
	 * @spec MonitorAdmin.getStatusVariable(string)
	 */
	private void testGetStatusVariable001() {
		tbc.log("#testGetStatusVariable001");
		try {
			tbc.getMonitorAdmin().getStatusVariable(null);
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
	 * when we pass invalid characteres as parameter.
	 * 
	 * @spec MonitorAdmin.getStatusVariable(string)
	 */
	private void testGetStatusVariable002() {
		tbc.log("#testGetStatusVariable002");
		try {
			tbc.getMonitorAdmin().getStatusVariable(
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
	 * This method asserts that an IllegalArgumentException is thrown
	 * when we pass a path that points to a non-existing StatusVariable.
	 * 
	 * @spec MonitorAdmin.getStatusVariable(string)
	 */
	private void testGetStatusVariable003() {
		tbc.log("#testGetStatusVariable003");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorConstants.INEXISTENT_SVS, MonitorPermission.READ));
			
			tbc.getMonitorAdmin().getStatusVariable(MonitorConstants.INEXISTENT_SVS);

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
			tbc.setTb1Permission(infos);
		}
	}

	/**
	 * This method asserts that the returned StatusVariable correspond to
	 * the path passed.
	 * 
	 * @spec MonitorAdmin.getStatusVariable(string)
	 */
	private void testGetStatusVariable004() {
		tbc.log("#testGetStatusVariable004");
		PermissionInfo[] infos = null;
		try {
			
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorConstants.SVS[0], MonitorPermission.READ));

			StatusVariable sv = tbc.getMonitorAdmin().getStatusVariable(
					MonitorConstants.SVS[0]);

			tbc.assertNotNull(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_NOT_NULL,
					new String[] { "returned StatusVariables" }), sv);

			tbc
					.assertEquals(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_EQUALS,
											new String[] { "ID", 
													MonitorConstants.SVS[0]
															.substring(MonitorConstants.SVS[0]
																	.indexOf("/") + 1) }),
							MonitorConstants.SV_NAME1, sv.getID());

		} finally {
			tbc.setTb1Permission(infos);
		}
	}

	/**
	 * This method asserts that a SecurityException is thrown when
	 * we have set no permission to the StatusVariable that we are using.
	 * 
	 * @spec MonitorAdmin.getStatusVariable(string)
	 */
	private void testGetStatusVariable005() {
		tbc.log("#testGetStatusVariable005");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorConstants.SVS[0], ""));

			tbc.getMonitorAdmin().getStatusVariable(MonitorConstants.SVS[0]);

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
			tbc.setTb1Permission(infos);
		}
	}

	/**
	 * This method asserts that a SecurityException is thrown when
	 * we have set other action permission to the StatusVariable that we are using.
	 * 
	 * @spec MonitorAdmin.getStatusVariable(string)
	 */
	private void testGetStatusVariable006() {
		tbc.log("#testGetStatusVariable006");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorConstants.SVS[0], MonitorPermission.PUBLISH));

			tbc.getMonitorAdmin().getStatusVariable(MonitorConstants.SVS[0]);

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
			tbc.setTb1Permission(infos);
		}
	}
	
	/**
	 * This method asserts that a SecurityException is thrown when
	 * we have set read action permission to other StatusVariable.
	 * 
	 * @spec MonitorAdmin.getStatusVariable(string)
	 */
	private void testGetStatusVariable007() {
		tbc.log("#testGetStatusVariable007");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorConstants.SVS[1], MonitorPermission.READ));

			tbc.getMonitorAdmin().getStatusVariable(MonitorConstants.SVS[0]);

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
			tbc.setTb1Permission(infos);
		}
	}

}

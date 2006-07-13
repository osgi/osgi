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
 * 22/03/2005   Eduardo Oliveira
 * 14           Updates after formal inspection (JSTD-MEGTCK-CODE-INSP011)
 * ===========  ==============================================================
 * 14/04/2005   Alexandre Santos
 * 14           Updates for SecurityException tests
 * ===========  ==============================================================
 * May 20, 2005	Alexandre Alves
 * 92           Make changes according to monitor RFC updates
 * ===========  ==============================================================
 */
package org.osgi.test.cases.monitor.tb1.MonitorAdmin;

import org.osgi.service.monitor.MonitorPermission;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.monitor.tbc.MonitorConstants;
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.TestInterface;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @author Alexandre Alves
 * 
 * This Test Class Validates the implementation of
 * <code>resetStatusVariable</code> method, according to MEG reference
 * documentation.
 */
public class ResetStatusVariable implements TestInterface {

	private MonitorTestControl tbc;

	public ResetStatusVariable(MonitorTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testResetStatusVariable001();
		testResetStatusVariable002();
		testResetStatusVariable003();
		testResetStatusVariable004();
		testResetStatusVariable005();
		testResetStatusVariable006();
		testResetStatusVariable007();
	}

	/**
	 * This method asserts if IllegalArgumentException is thrown
	 * when we pass invalid characters as parameter.
	 * 
	 * @spec MonitorAdmin.resetStatusVariable(string)
	 */
	private void testResetStatusVariable001() {
		tbc.log("#testResetStatusVariable001");
		try {
			tbc.getMonitorAdmin().resetStatusVariable(
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
	 * when we pass null as parameter.
	 * 
	 * @spec MonitorAdmin.resetStatusVariable(string)
	 */
	private void testResetStatusVariable002() {
		tbc.log("#testResetStatusVariable002");
		try {
			tbc.getMonitorAdmin().resetStatusVariable(null);
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
	 * @spec MonitorAdmin.resetStatusVariable(string)
	 */
	private void testResetStatusVariable003() {		
		tbc.log("#testResetStatusVariable003");
		PermissionInfo[] infos = null;		
		try {
			infos = tbc.getPermissionAdmin().getPermissions(tbc.getTb1Location());
			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorConstants.INEXISTENT_SVS, MonitorPermission.RESET));			
			
			tbc.getMonitorAdmin()
					.resetStatusVariable(MonitorConstants.INEXISTENT_SVS);
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
	 * This method asserts if MonitorAdmin returns the value
	 * returned by our Monitorable implementation.
	 * 
	 * @spec MonitorAdmin.resetStatusVariable(string)
	 */
	private void testResetStatusVariable004() {
		tbc.log("#testResetStatusVariable004");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorConstants.SVS[0], MonitorPermission.RESET));

			boolean result = tbc.getMonitorAdmin().resetStatusVariable(
					MonitorConstants.SVS[0]);

			tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE,
					new String[] { "resetStatusVariable was called in correct implementation returning false as result. " }), result);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.setTb1Permission(infos);
		}
	}

	/**
	 * This method asserts that a SecurityException is thrown when
	 * we have set other action permission to the StatusVariable that we are using.
	 * 
	 * @spec MonitorAdmin.resetStatusVariable(string)
	 */
	private void testResetStatusVariable005() {
		tbc.log("#testResetStatusVariable005");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorConstants.SVS[0], MonitorPermission.PUBLISH));

			tbc.getMonitorAdmin().resetStatusVariable(
					MonitorConstants.SVS[0]);

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
	 * we have no action permission to the StatusVariable that we are using.
	 * 
	 * @spec MonitorAdmin.resetStatusVariable(string)
	 */
	private void testResetStatusVariable006() {
		tbc.log("#testResetStatusVariable006");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorConstants.SVS[1], ""));

			tbc.getMonitorAdmin().resetStatusVariable(
					MonitorConstants.SVS[1]);

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
	 * we have reset permission to other StatusVariable.
	 * 
	 * @spec MonitorAdmin.resetStatusVariable(string)
	 */
	private void testResetStatusVariable007() {
		tbc.log("#testResetStatusVariable007");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorConstants.SVS[1], MonitorPermission.RESET));

			tbc.getMonitorAdmin().resetStatusVariable(
					MonitorConstants.SVS[0]);

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

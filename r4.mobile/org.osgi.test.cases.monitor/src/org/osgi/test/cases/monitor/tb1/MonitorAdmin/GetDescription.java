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
 * Mar 29, 2005	Leonardo Barros
 * 14           Implement MEG TCK
 * ===========  ==============================================================
 * May 20, 2005	Alexandre Alves
 * 92           Make changes according to monitor RFC updates
 * ===========  ==============================================================
 * Jun 28, 2005	Andre Assad
 * 92           Changes after face to face meeting
 * ===========  ==============================================================
 **/
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
 * <code>getDescription</code> method, according to MEG reference
 * documentation.
 */
public class GetDescription implements TestInterface {
	private MonitorTestControl tbc;

	/**
	 * @param tbc
	 */
	public GetDescription(MonitorTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetDescription001();
		testGetDescription002();
		testGetDescription003();
		testGetDescription004();
		testGetDescription005();
		testGetDescription006();
		testGetDescription007();
	}

	/**
	 * This method asserts if a IllegalArgumentException is thrown
	 * when we pass null as parameter.
	 * 
	 * @spec MonitorAdmin.getDescription(String)
	 */
	private void testGetDescription001() {
		try { 
			tbc.log("#testGetDescription001");
			tbc.getMonitorAdmin().getDescription(null);
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
	 * This method asserts if a IllegalArgumentException is thrown
	 * when we pass an invalid characters as parameter.
	 * 
	 * @spec MonitorAdmin.getDescription(String)
	 */
	private void testGetDescription002() {
		try {
			tbc.log("#testGetDescription002");
			tbc.getMonitorAdmin().getDescription(MonitorConstants.INVALID_ID);

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
	 * This method asserts if a IllegalArgumentException is thrown
	 * when we pass a path to a inexistent StatusVariable.
	 * 
	 * @spec MonitorAdmin.getDescription(String)
	 */
	private void testGetDescription003() {
		PermissionInfo[] infos = null;
		try {
			tbc.log("#testGetDescription003");
			infos = tbc.getPermissionAdmin().getPermissions(tbc.getTb1Location());
			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorConstants.INEXISTENT_SVS, MonitorPermission.READ));
			
			tbc.getMonitorAdmin().getDescription(MonitorConstants.INEXISTENT_SVS);

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
	 * This method asserts if the MonitorAdmin returns the value (null)
	 * returned by our Monitorable.
	 * 
	 * @spec MonitorAdmin.getDescription(String)
	 */
	private void testGetDescription004() {
		tbc.log("#testGetDescription004");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(tbc.getTb1Location());
			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorConstants.SVS[0], MonitorPermission.READ));

			String desc = tbc.getMonitorAdmin().getDescription(MonitorConstants.SVS[0]);
			
			tbc.assertNull("The description was correctly returned by MonitorAdmin", desc);
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.setTb1Permission(infos);
		}
	}

	/**
	 * This method asserts that a SecurityException is thrown when
	 * we have set no permission to the StatusVariable that we are using.
	 * 
	 * @spec MonitorAdmin.getDescription(String)
	 */
	private void testGetDescription005() {
		tbc.log("#testGetDescription005");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorConstants.SVS[0], ""));

			tbc.getMonitorAdmin().getDescription(MonitorConstants.SVS[0]);

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
	 * @spec MonitorAdmin.getDescription(String)
	 */
	private void testGetDescription006() {
		tbc.log("#testGetDescription006");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorConstants.SVS[0], MonitorPermission.PUBLISH));

			tbc.getMonitorAdmin().getDescription(MonitorConstants.SVS[0]);

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
	 * @spec MonitorAdmin.getDescription(String)
	 */
	private void testGetDescription007() {
		tbc.log("#testGetDescription007");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorConstants.SVS[1], MonitorPermission.READ));

			tbc.getMonitorAdmin().getDescription(MonitorConstants.SVS[0]);

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

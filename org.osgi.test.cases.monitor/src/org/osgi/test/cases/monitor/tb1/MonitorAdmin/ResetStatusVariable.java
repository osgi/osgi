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
 */
package org.osgi.test.cases.monitor.tb1.MonitorAdmin;

import org.osgi.service.monitor.MonitorPermission;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.TestInterface;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @methodUnderTest org.osgi.service.monitor.MonitorAdmin#resetStatusVariable
 * @generalDescription This class tests resetStatusVariable method according
 *                     with MEG specification (rfc0084)
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
	}

	/**
	 * @testID testResetStatusVariable001
	 * @testDescription Tests if an invalid characters throws an IllegalArgumentException
	 */
	public void testResetStatusVariable001() {
		try {
			tbc.getMonitorAdmin().resetStatusVariable(
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
	 * @testID testResetStatusVariable002
	 * @testDescription Tests if null is passed as parameter will throws an 
	 * 					IllegalArgumentException
	 */
	public void testResetStatusVariable002() {
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
	 * @testID testResetStatusVariable003
	 * @testDescription Tests if a statusvariable that doesn't exists
	 *					throws an IllegalArgumentException 					
	 */
	public void testResetStatusVariable003() {
		try {
			tbc.getMonitorAdmin()
					.resetStatusVariable(MonitorTestControl.SVS_DONT_EXIST);
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
	 * @testID testResetStatusVariable004
	 * @testDescription Tests if the valid ID is reseted using long as type.
	 */
	public void testResetStatusVariable004() {
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorTestControl.SVS[0], MonitorPermission.RESET));

			boolean result = tbc.getMonitorAdmin().resetStatusVariable(
					MonitorTestControl.SVS[0]);

			tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE,
					new String[] { "resetStatusVariable was called in correct implementation returning false as result. " }), !result);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		} finally {
			tbc.getPermissionAdmin().setPermissions(
					tbc.getTb1Location(), infos);
		}
	}

	/**
	 * @testID testResetStatusVariable005
	 * @testDescription Asserts if a SecurityException is thrown when the caller
	 *                  has no reset permission
	 */
	public void testResetStatusVariable005() {
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorTestControl.SVS[0], MonitorPermission.PUBLISH));

			tbc.getMonitorAdmin().resetStatusVariable(
					MonitorTestControl.SVS[0]);

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
	 * @testID testResetStatusVariable006
	 * @testDescription Asserts if a SecurityException is thrown when the caller
	 *                  has no reset permission
	 */
	public void testResetStatusVariable006() {
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb1Location());

			tbc.setLocalPermission(new PermissionInfo(MonitorPermission.class.getName(),MonitorTestControl.SVS[1], null));

			tbc.getMonitorAdmin().resetStatusVariable(
					MonitorTestControl.SVS[1]);

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

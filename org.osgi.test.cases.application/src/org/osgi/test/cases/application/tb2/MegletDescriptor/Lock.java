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
 * 03/05/2005   Leonardo Barros
 * 38           Implement MEGTCK for the application RFC 
 * ===========  ==============================================================
 * 12/05/2005   Alexandre Santos
 * 38           Update to fix errors 
 * ===========  ==============================================================
 */
package org.osgi.test.cases.application.tb2.MegletDescriptor;

import org.osgi.service.application.ApplicationAdminPermission;
import org.osgi.service.application.meglet.MegletHandle;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.TestInterface;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;

/**
 * @methodUnderTest org.osgi.service.application.ApplicationDescriptor#lock
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>lock<code> method, according to MEG reference
 *                     documentation.
 */
public class Lock implements TestInterface {
	private ApplicationTestControl tbc;

	public Lock(ApplicationTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testLock001();
		testLock002();
		testLock003();
	}

	/**
	 * @testID testLock001
	 * @testDescription Asserts if SecurityException is thrown when the caller
	 *                  does not have "lock" ApplicationAdminPermission for the
	 *                  application.
	 */
	public void testLock001() {
		tbc.log("#testLock001");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setDefaultPermission();

			tbc.getAppDescriptor1().lock();

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
			tbc.getPermissionAdmin()
					.setPermissions(tbc.getTb2Location(), infos);
		}
	}

	/**
	 * @testID testLock002
	 * @testDescription Asserts if an application is locked avoiding to launch a
	 *                  new instance
	 */
	public void testLock002() {
		tbc.log("#testLock002");
		PermissionInfo[] infos = null;
		MegletHandle meglet = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo[] {
					new PermissionInfo(ApplicationAdminPermission.class
							.getName(), ApplicationTestControl.TEST1_PID,
							ApplicationAdminPermission.LOCK),
					new PermissionInfo(ApplicationAdminPermission.class
							.getName(), ApplicationTestControl.TEST1_PID,
							ApplicationAdminPermission.LIFECYCLE) });

			tbc.getAppDescriptor1().lock();

			meglet = (MegletHandle) tbc.getAppDescriptor1().launch(null);
			tbc.assertNull("Asserting if null was returned when we try to launch a application locked.", meglet);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.cleanUp(meglet, infos);
			tbc.getAppDescriptor1().unlock();
		}
	}

	/**
	 * @testID testLock003
	 * @testDescription Asserts if IllegalStateException is thrown when
	 *                  application descriptor is unregistered
	 */
	public void testLock003() {
		tbc.log("#testLock003");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationTestControl.TEST1_PID,
					ApplicationAdminPermission.LOCK));

			tbc.stopServices();
			
			tbc.getAppDescriptor1().lock();

			tbc.failException("", IllegalStateException.class);
		} catch (IllegalStateException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalStateException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalStateException.class.getName(),
							e.getClass().getName() }));
		} finally {
			tbc.getPermissionAdmin()
					.setPermissions(tbc.getTb2Location(), infos);
			tbc.installBundleMeglet();
		}
	}

}

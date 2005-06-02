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
 * 04/05/2005   Leonardo Barros
 * 38           Implement MEGTCK for the application RFC 
 * ===========  ==============================================================
 * 13/05/2005   Alexandre Santos
 * 38           Update to fix errors 
 * ===========  ==============================================================
 */
package org.osgi.test.cases.application.tb2.MegletDescriptor;

import org.osgi.service.application.ApplicationAdminPermission;
import org.osgi.service.application.ApplicationDescriptor;
import org.osgi.service.application.SingletonException;
import org.osgi.service.application.meglet.MegletHandle;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.TestInterface;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;

/**
 * @methodUnderTest org.osgi.service.application.ApplicationDescriptor#launch
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>launch<code> method, according to MEG reference
 *                     documentation.
 */
public class Launch implements TestInterface {
	private ApplicationTestControl tbc;

	public Launch(ApplicationTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testLaunch001();
		testLaunch002();
		testLaunch003();
		testLaunch004();
		testLaunch005();
	}

	/**
	 * @testID testLaunch001
	 * @testDescription Asserts if SecurityException is thrown when the caller
	 *                  does not have "launch" ApplicationAdminPermission for
	 *                  the application.
	 */
	public void testLaunch001() {
		tbc.log("#testLaunch001");
		MegletHandle meglet = null;
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setDefaultPermission();

			meglet = (MegletHandle) tbc.getAppDescriptor2().launch(tbc.getMeg2Properties());

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
			tbc.cleanUp(meglet, infos);			
		}
	}

	/**
	 * @testID testLaunch002
	 * @testDescription Asserts if SingletonException is thrown when attempts to
	 *                  launch a second instance of a singleton application
	 */
	public void testLaunch002() {
		tbc.log("#testLaunch002");
		PermissionInfo[] infos = null;
		MegletHandle meglet = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationTestControl.TEST1_PID,
					ApplicationAdminPermission.LIFECYCLE));

			// TODO which is the action that must be assigned to the permission?

			tbc.getAppDescriptor1().unlock();
			meglet = (MegletHandle) tbc.getAppDescriptor1().launch(tbc.getMeg1Properties());

			tbc.failException("", SingletonException.class);
		} catch (SingletonException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { SingletonException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							SingletonException.class.getName(),
							e.getClass().getName() }));
		} finally {
			tbc.cleanUp(meglet, infos);	
		}
	}

	/**
	 * @testID testLaunch003
	 * @testDescription Asserts if IllegalStateException is thrown when
	 *                  application descriptor is unregistered
	 */
	public void testLaunch003() {
		tbc.log("#testLaunch003");
		MegletHandle meglet = null;
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationTestControl.TEST2_PID,
					ApplicationAdminPermission.LIFECYCLE));

			tbc.stopServices();
			
			meglet = (MegletHandle) tbc.getAppDescriptor2().launch(tbc.getMeg2Properties());

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
			tbc.cleanUp(meglet, infos);	
			tbc.installBundleMeglet();				
		}
	}

	/**
	 * @testID testLaunch004
	 * @testDescription Asserts if null is returned when the application is
	 *                  locked
	 */
	public void testLaunch004() {
		tbc.log("#testLaunch004");
		PermissionInfo[] infos = null;
		MegletHandle meglet = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationTestControl.TEST2_PID,
					ApplicationAdminPermission.LIFECYCLE));

			tbc.getAppDescriptor2().lock();
			
			meglet = (MegletHandle)tbc.getAppDescriptor2().launch(tbc.getMeg2Properties());
					
			tbc.assertNull(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_NULL,
					new String[] { "the returned value of launch method" }),
					meglet);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.cleanUp(meglet, infos);	
		}
	}

	/**
	 * @testID testLaunch005
	 * @testDescription Asserts if the application is launched
	 */
	public void testLaunch005() {
		tbc.log("#testLaunch005");
		PermissionInfo[] infos = null;
		MegletHandle meglet = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationTestControl.TEST2_PID,
					ApplicationAdminPermission.LIFECYCLE));

			tbc.getAppDescriptor2().unlock();

			meglet = (MegletHandle) tbc.getAppDescriptor2().launch(
					tbc.getMeg2Properties());

			tbc.assertEquals("Asserts if the application is launched corretly",
					ApplicationTestControl.TEST2_PID, meglet
							.getApplicationDescriptor().getProperties(null)
							.get(ApplicationDescriptor.APPLICATION_PID));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.cleanUp(meglet, infos);	
		}
	}

}

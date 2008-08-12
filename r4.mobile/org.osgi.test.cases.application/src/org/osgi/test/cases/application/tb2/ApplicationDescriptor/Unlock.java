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
 * 24/08/2005   Alexandre Santos
 * 153          Implement OAT test cases  
 * ===========  ==============================================================
 */
package org.osgi.test.cases.application.tb2.ApplicationDescriptor;

import org.osgi.service.application.ApplicationAdminPermission;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.application.tbc.ApplicationConstants;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.TestInterface;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;

/**
 * @author Alexandre Alves
 * 
 * This Test Class Validates the implementation of
 * <code>unlock</code> method, according to MEG reference
 * documentation.
 */
public class Unlock implements TestInterface {
	private ApplicationTestControl tbc;

	public Unlock(ApplicationTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testUnlock001();
		testUnlock002();
		testUnlock003();
		testUnlock004();
	}

	/**
	/**
	 * This method asserts that SecurityException is thrown when
	 * the caller doesn't have "lock" ApplicationAdminPermission
	 * for the application.
	 * 
	 * @spec ApplicationDescriptor.unlock()
	 */
	public void testUnlock001() {
		tbc.log("#testUnlock001");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setDefaultPermission();

			tbc.getAppDescriptor().unlock();

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
			tbc.cleanUp(infos);
		}
	}

	/**
	 * This method asserts that a unlock call after other unlock call
	 * will not thrown exception.
	 * 
	 * @spec ApplicationDescriptor.unlock()
	 */
	public void testUnlock002() {
		tbc.log("#testUnlock002");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(
				new PermissionInfo(ApplicationAdminPermission.class.getName(), ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.LOCK_ACTION)
			);

			tbc.getAppDescriptor().unlock();
			tbc.getAppDescriptor().unlock();

			tbc.pass("No Exception was thrown.");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.cleanUp(infos);
		}
	}	

	/**
	 * This method asserts that if the application descriptor is unregistered
	 * IllegalStateException will be thrown.
	 * 
	 * @spec ApplicationDescriptor.unlock()
	 */
	public void testUnlock003() {
		tbc.log("#testUnlock003");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(
				new PermissionInfo(ApplicationAdminPermission.class.getName(), ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.LOCK_ACTION)
			);

			tbc.unregisterDescriptor();

			tbc.getAppDescriptor().unlock();

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
			tbc.installDescriptor();
			tbc.cleanUp(infos);			
		}
	}

	/**
	 * This method asserts if unlock is called after a lock call,
	 * an application can be started without exceptions.
	 * 
	 * @spec ApplicationDescriptor.unlock()
	 */
	public void testUnlock004() {
		tbc.log("#testUnlock004");
		ApplicationHandle handle = null;
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.LIFECYCLE_ACTION+","+ApplicationAdminPermission.LOCK_ACTION));

			tbc.getAppDescriptor().lock();
			tbc.getAppDescriptor().unlock();

			handle = (ApplicationHandle) tbc.getAppDescriptor().launch(null);

			tbc.pass("No exception was thrown.");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.cleanUp(handle, infos);
		}	
	}
	
}

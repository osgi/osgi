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
 * 05/05/2005   Leonardo Barros
 * 38           Implement MEGTCK for the application RFC 
 * ===========  ==============================================================
 * 17/05/2005   Alexandre Alves
 * 38           Update changes/Fix errors 
 * ===========  ==============================================================
 * 24/05/2005   Alexandre Alves
 * 38           Update changes after inspection 
 * ===========  ==============================================================
 */
package org.osgi.test.cases.application.tb2.ScheduledApplication;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.application.ApplicationAdminPermission;
import org.osgi.service.application.ScheduledApplication;
import org.osgi.service.application.meglet.MegletHandle;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.TestInterface;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;

/**
 * @methodUnderTest org.osgi.service.application.ScheduledApplication#remove
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>remove<code> method, according to MEG reference
 *                     documentation.
 */
public class Remove implements TestInterface {
	private ApplicationTestControl tbc;

	public Remove(ApplicationTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testRemove001();
		testRemove002();
		testRemove003();
	}

	/**
	 * @testID testRemove001
	 * @testDescription Asserts if the SecurityException is thrown when the
	 *                  caller does not have the correct
	 *                  ApplicationAdminPermission action
	 */
	public void testRemove001() {
		tbc.log("#testRemove001");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationTestControl.TEST2_PID,
					ApplicationAdminPermission.LOCK+","+ApplicationAdminPermission.SCHEDULE));

			tbc.getAppDescriptor2().unlock();

			sa = tbc.getAppDescriptor2().schedule(null, "*", null, false);

			tbc.setDefaultPermission();

			sa.remove();

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
			tbc.cleanUpRemove(infos);
		}
	}

	/**
	 * @testID testRemove002
	 * @testDescription Asserts if IllegalStateException is thrown when
	 *                  application descriptor is unregistered
	 */
	public void testRemove002() {
		tbc.log("#testRemove002");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationTestControl.TEST2_PID,
					ApplicationAdminPermission.LOCK+","+ApplicationAdminPermission.SCHEDULE));

			tbc.getAppDescriptor2().unlock();

			sa = tbc.getAppDescriptor2().schedule(null, "*", null, false);

			tbc.stopServices();
			
			sa.remove();

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
			tbc.cleanUpRemove(infos);
			tbc.installBundleMeglet();
		}
	}

	/**
	 * @testID testRemove003
	 * @testDescription Asserts if the application is unregistered after remove
	 *                  method call
	 */
	public void testRemove003() {
		tbc.log("#testRemove003");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationTestControl.TEST2_PID,
					ApplicationAdminPermission.LOCK+","+ApplicationAdminPermission.SCHEDULE));

			tbc.getAppDescriptor2().unlock();
			
			closeMeglets();

			sa = tbc.getAppDescriptor2().schedule(null, "org/osgi/framework/ServiceEvent/UNREGISTERED", null, true);

			MegletHandle meglet = (MegletHandle) tbc.getAppDescriptor2().launch(tbc.getMeg2Properties());
			
			meglet.destroy();
			
			tbc.assertTrue("Asserting if a meglet was started by ScheduledApplication", !isHandleStopped());

			sa.remove();
			
			closeMeglets();
			
			meglet = (MegletHandle) tbc.getAppDescriptor2().launch(tbc.getMeg2Properties());
			
			meglet.destroy();
			
			tbc.assertTrue("Asserting if a meglet was started by ScheduledApplication", isHandleStopped());
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.cleanUpRemove(infos);
		}
	}
	
	private void closeMeglets() {
		MegletHandle meglet = tbc.getAppHandle(ApplicationTestControl.TEST2_PID);
		while (meglet != null) {
			try {
				meglet.destroy();
			} catch (Exception e) {
				tbc.log("#Cant destroy the meglet");
			}
			meglet = tbc.getAppHandle(ApplicationTestControl.TEST2_PID);				
		}			
	}
	
	private boolean isHandleStopped() {
		try {
			ServiceReference[] appDescRefs = tbc.getContext()
			.getServiceReferences(
					"org.osgi.service.application.ApplicationHandle",
					"(application.descriptor="
							+ ApplicationTestControl.TEST2_PID + ")");
			if (appDescRefs == null) {
				return true;
			} else {
				return false;
			}
		} catch (InvalidSyntaxException e) {
			return false;
		}
		
		
	}	
	
}

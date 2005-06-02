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

import org.osgi.framework.ServiceReference;
import org.osgi.service.application.ApplicationAdminPermission;
import org.osgi.service.application.ScheduledApplication;
import org.osgi.service.application.meglet.MegletHandle;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.TestInterface;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;

/**
 * @methodUnderTest org.osgi.service.application.ApplicationDescriptor#schedule
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>schedule<code> method, according to MEG reference
 *                     documentation.
 */
public class Schedule implements TestInterface {
	private ApplicationTestControl tbc;

	public Schedule(ApplicationTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testSchedule001();
		testSchedule002();
		testSchedule003();
		testSchedule004();
		testSchedule005();
		testSchedule006();
		testSchedule007();
	}

	/**
	 * @testID testSchedule001
	 * @testDescription Asserts if SecurityException is thrown when the caller
	 *                  does not have "schedule" ApplicationAdminPermission for
	 *                  the application.
	 */
	public void testSchedule001() {
		tbc.log("#testSchedule001");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setDefaultPermission();

			sa = tbc.getAppDescriptor2().schedule(null, "*", null, false);

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
			tbc.cleanUp(sa, infos);
		}
	}

	/**
	 * @testID testSchedule002
	 * @testDescription Asserts if IllegalStateException is thrown when
	 *                  application descriptor is unregistered
	 */
	public void testSchedule002() {
		tbc.log("#testSchedule002");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationTestControl.TEST2_PID,
					ApplicationAdminPermission.SCHEDULE));

			tbc.stopServices();

			tbc.getAppDescriptor2().schedule(tbc.getMeg2Properties(), "*",
					null, false);

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

	/**
	 * @testID testSchedule003
	 * @testDescription Asserts if NullPointerException is thrown when the topic
	 *                  parameter is null
	 */
	public void testSchedule003() {
		tbc.log("#testSchedule003");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationTestControl.TEST2_PID,
					ApplicationAdminPermission.SCHEDULE));

			tbc.getAppDescriptor2().schedule(tbc.getMeg2Properties(), null,
					"(second=0)", false);

			tbc.failException("", NullPointerException.class);
		} catch (NullPointerException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { NullPointerException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							NullPointerException.class.getName(),
							e.getClass().getName() }));
		} finally {
			tbc.getPermissionAdmin()
					.setPermissions(tbc.getTb2Location(), infos);
		}
	}

	/**
	 * @testID testSchedule004
	 * @testDescription Asserts if the application is scheduled when the caller
	 *                  has the correct permission
	 */
	public void testSchedule004() {
		tbc.log("#testSchedule004");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationTestControl.TEST2_PID,
					ApplicationAdminPermission.SCHEDULE));

			tbc.getAppDescriptor2().unlock();

			ServiceReference[] appDescRefs = tbc
					.getContext()
					.getServiceReferences(
							"org.osgi.service.application.ScheduledApplication",
							null);

			int count = (appDescRefs != null ? appDescRefs.length : 0);

			sa = tbc.getAppDescriptor2().schedule(tbc.getMeg2Properties(),
					ApplicationTestControl.TIMER_EVENT_TOPIC, "(second=0)",
					false);

			tbc.assertNotNull(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_NOT_NULL,
					new String[] { "the returned value of schedule method" }),
					sa);

			appDescRefs = tbc.getContext().getServiceReferences(
					"org.osgi.service.application.ScheduledApplication", null);

			tbc.assertEquals("Asserts if the application is scheduled",
					count + 1, appDescRefs.length);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(sa, infos);
		}
	}

	/**
	 * @testID testSchedule005
	 * @testDescription Asserts if the application is scheduled when the caller
	 *                  has the correct permission, and that the firing event
	 *                  launches one more application.
	 */
	public synchronized void testSchedule005() {
		tbc.log("#testSchedule005");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		MegletHandle meglet = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationTestControl.TEST2_PID,
					ApplicationAdminPermission.SCHEDULE));

			tbc.getAppDescriptor2().unlock();

			ServiceReference[] appDescRefs = tbc
					.getContext()
					.getServiceReferences(
							"org.osgi.service.application.ScheduledApplication",
							null);

			int count = (appDescRefs != null ? appDescRefs.length : 0);

			sa = tbc.getAppDescriptor2().schedule(tbc.getMeg2Properties(),
					"org/osgi/framework/ServiceEvent/UNREGISTERED", null, true);

			tbc.assertNotNull(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_NOT_NULL,
					new String[] { "the returned value of schedule method" }),
					sa);

			meglet = (MegletHandle) tbc.getAppDescriptor2().launch(
					tbc.getMeg2Properties());
			meglet.destroy(); // fire unregistered event

			appDescRefs = tbc.getContext().getServiceReferences(
					"org.osgi.service.application.ApplicationHandle",
					"(application.descriptor="
							+ ApplicationTestControl.TEST2_PID + ")");

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "an application is scheduled when the caller has the correct permission." }),
							((appDescRefs != null) && (appDescRefs.length == 1)));

			appDescRefs = tbc.getContext().getServiceReferences(
					"org.osgi.service.application.ScheduledApplication", null);

			tbc.assertEquals("Asserts if the application is scheduled",
					count + 1, appDescRefs.length);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		} finally {
			tbc.cleanUp(sa, infos);
		}
	}

	/**
	 * @testID testSchedule006
	 * @testDescription Asserts if the application is scheduled when the caller
	 *                  has the null as arguments parameter.
	 */
	public void testSchedule006() {
		tbc.log("#testSchedule006");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationTestControl.TEST2_PID,
					ApplicationAdminPermission.SCHEDULE));

			tbc.getAppDescriptor2().unlock();

			ServiceReference[] appDescRefs = tbc
					.getContext()
					.getServiceReferences(
							"org.osgi.service.application.ScheduledApplication",
							null);

			int count = (appDescRefs != null ? appDescRefs.length : 0);

			sa = tbc.getAppDescriptor2().schedule(null,
					ApplicationTestControl.TIMER_EVENT_TOPIC, "(second=0)",
					false);

			tbc.assertNotNull(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_NOT_NULL,
					new String[] { "the returned value of schedule method" }),
					sa);

			appDescRefs = tbc.getContext().getServiceReferences(
					"org.osgi.service.application.ScheduledApplication", null);

			tbc.assertEquals("Asserts if the application is scheduled",
					count + 1, appDescRefs.length);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(sa, infos);
		}
	}
	
	/**
	 * @testID testSchedule007
	 * @testDescription Asserts if the org.osgi.triggeringevent 
	 *                  works as described.
	 */
	public synchronized void testSchedule007() {
		tbc.log("#testSchedule007");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			tbc.setOurMegletActivated(false);
			
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationTestControl.TEST2_PID,
					ApplicationAdminPermission.SCHEDULE+","+ApplicationAdminPermission.LOCK));

			tbc.getAppDescriptor2().unlock();

			ServiceReference[] appDescRefs = tbc
					.getContext()
					.getServiceReferences(
							"org.osgi.service.application.ScheduledApplication",
							null);

			int count = (appDescRefs != null ? appDescRefs.length : 0);

			sa = tbc.getAppDescriptor2().schedule(tbc.getMeg2PropertiesTest(),
					ApplicationTestControl.TIMER_EVENT_TOPIC, "(second=0)",
					false);

			tbc.assertNotNull(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_NOT_NULL,
					new String[] { "the returned value of schedule method" }),
					sa);
			
			wait(ApplicationTestControl.TIME_OUT);
			
			tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE, new String[] { "our triggerevent was fired." } ), tbc.isOurMegletActivated());

			appDescRefs = tbc.getContext().getServiceReferences(
					"org.osgi.service.application.ScheduledApplication", null);

			tbc.assertEquals("Asserts if the application is scheduled",
					count + 1, appDescRefs.length);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(sa, infos);
		}
	}	

}

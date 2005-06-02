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
 * 19/05/2005   Alexandre Alves
 * 38           Implement MEGTCK for the application RFC 
 * ===========  ==============================================================
 */
package org.osgi.test.cases.application.tbc.UseCases;

import org.osgi.service.application.ApplicationAdminPermission;
import org.osgi.service.application.meglet.MegletHandle;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.TestInterface;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;

/**
 * @generalDescription This test class validates lifecycle states changes of an
 *                     application
 */
public class LifecycleStates implements TestInterface {
	private ApplicationTestControl tbc;

	public LifecycleStates(ApplicationTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testLifecycleStates001();
		testLifecycleStates002();
		testLifecycleStates003();
		testLifecycleStates004();
		testLifecycleStates005();
	}

	/**
	 * @testID testLifecycleStates001
	 * @testDescription Asserts if the REGISTERED event is dispatch
	 * 					correctly                 
	 */
	public synchronized void testLifecycleStates001() {
		tbc.log("#testLifecycleStates001");
		PermissionInfo[] infos = null;
		MegletHandle ah = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationTestControl.TEST2_PID,
					ApplicationAdminPermission.LIFECYCLE));

			tbc.getAppDescriptor2().unlock();

			ah = (MegletHandle) tbc.getAppDescriptor2().launch(tbc.getMeg2Properties());

			wait(ApplicationTestControl.TIME_OUT);
			
			tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE, new String[] { "the system has received a register event after launch an app." }),
					tbc.isRegistered() && !tbc.isModified() && !tbc.isUnregistered());
			
			ah.destroy();

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.getAppDescriptor2().lock();
			tbc.getPermissionAdmin()
					.setPermissions(tbc.getTb2Location(), infos);
		}
	}
	
	/**
	 * @testID testLifecycleStates002
	 * @testDescription Asserts if the MODIFIED event is dispatch   
	 *                  correctly
	 */
	public synchronized void testLifecycleStates002() {
		tbc.log("#testLifecycleStates002");
		PermissionInfo[] infos = null;
		MegletHandle ah = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationTestControl.TEST2_PID,
					ApplicationAdminPermission.LIFECYCLE));

			tbc.getAppDescriptor2().unlock();

			ah = (MegletHandle) tbc.getAppDescriptor2().launch(tbc.getMeg2Properties());
			
			ah.suspend();

			wait(ApplicationTestControl.TIME_OUT);
			
			tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE, new String[] { "the system has received a modified event after suspend an app." }),
					!tbc.isRegistered() && tbc.isModified() && !tbc.isUnregistered());

			ah.destroy();

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.getAppDescriptor2().lock();
			tbc.getPermissionAdmin()
					.setPermissions(tbc.getTb2Location(), infos);
		}
	}	
	
	/**
	 * @testID testLifecycleStates003
	 * @testDescription Asserts if the MODIFIED event is dispatch   
	 *                  correctly
	 */
	public synchronized void testLifecycleStates003() {
		tbc.log("#testLifecycleStates003");
		PermissionInfo[] infos = null;
		MegletHandle ah = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationTestControl.TEST2_PID,
					ApplicationAdminPermission.LIFECYCLE));

			tbc.getAppDescriptor2().unlock();

			ah = (MegletHandle) tbc.getAppDescriptor2().launch(tbc.getMeg2Properties());
			
			ah.suspend();
			
			ah.resume();

			wait(ApplicationTestControl.TIME_OUT);
			
			tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE, new String[] { "the system has received a modified event after resume an app." }),
					!tbc.isRegistered() && tbc.isModified() && !tbc.isUnregistered());

			ah.destroy();

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.getAppDescriptor2().lock();
			tbc.getPermissionAdmin()
					.setPermissions(tbc.getTb2Location(), infos);
		}
	}	
	
	/**
	 * @testID testLifecycleStates004
	 * @testDescription Asserts if the UNREGISTERED event is dispatch   
	 *                  correctly
	 */
	public synchronized void testLifecycleStates004() {
		tbc.log("#testLifecycleStates004");
		PermissionInfo[] infos = null;
		MegletHandle ah = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationTestControl.TEST2_PID,
					ApplicationAdminPermission.LIFECYCLE));

			tbc.getAppDescriptor2().unlock();

			ah = (MegletHandle) tbc.getAppDescriptor2().launch(tbc.getMeg2Properties());
			
			ah.destroy();

			wait(ApplicationTestControl.TIME_OUT);
			
			tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE, new String[] { "the system has received a unregistered event after destroy an app." }),
					!tbc.isRegistered() && !tbc.isModified() && tbc.isUnregistered());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.getAppDescriptor2().lock();
			tbc.getPermissionAdmin()
					.setPermissions(tbc.getTb2Location(), infos);
		}
	}		
	
	/**
	 * @testID testLifecycleStates005
	 * @testDescription Asserts if the UNREGISTERED event is dispatch 
	 *                  when we destroy a suspended meglet.
	 */
	public synchronized void testLifecycleStates005() {
		tbc.log("#testLifecycleStates005");
		PermissionInfo[] infos = null;
		MegletHandle ah = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationTestControl.TEST2_PID,
					ApplicationAdminPermission.LIFECYCLE));

			tbc.getAppDescriptor2().unlock();

			ah = (MegletHandle) tbc.getAppDescriptor2().launch(tbc.getMeg2Properties());
			
			ah.suspend();
			
			ah.destroy();

			wait(ApplicationTestControl.TIME_OUT);
			
			tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE, new String[] { "the system has received a unregistered event after destroy a suspended application." }),
					!tbc.isRegistered() && !tbc.isModified() && tbc.isUnregistered());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.getAppDescriptor2().lock();
			tbc.getPermissionAdmin()
					.setPermissions(tbc.getTb2Location(), infos);
		}
	}		
}

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
 * 23/08/2005   Alexandre Santos
 * 153          Implement OAT test cases  
 * ===========  ==============================================================
 */
package org.osgi.test.cases.application.tb2.ApplicationDescriptor;

import java.util.HashMap;

import org.osgi.service.application.ApplicationAdminPermission;
import org.osgi.service.application.ScheduledApplication;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.application.tbc.ApplicationConstants;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.TestInterface;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;

/**
 * @author Alexandre Alves
 * 
 * This Test Class Validates the implementation of
 * <code>schedule</code> method, according to MEG reference
 * documentation.
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
		testSchedule008();
		testSchedule009();
	}
	
	/**
	 * This method asserts if null can be passed as eventFilter parameter.
	 * 
	 * @spec ApplicationDescriptor.schedule(Map,String,String,boolean)
	 */	
	private void testSchedule001() {
		tbc.log("#testSchedule001");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(
				new PermissionInfo(ApplicationAdminPermission.class.getName(), ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.SCHEDULE_ACTION)
			);

			HashMap map = new HashMap();
			sa = tbc.getAppDescriptor().schedule(map, "*", null, false);
			
			tbc.installTestBundle();
			
			tbc.assertNotNull("Asserting that a ScheduledApplication was returned when we pass null as eventFilter parameter.", sa);
			
			synchronized (tbc) {
				tbc.wait(ApplicationConstants.SHORT_TIMEOUT);
			}
			
			tbc.assertEquals("Asserting that a ApplicationHandle was registered.", 1, tbc.getNumberAppHandle());
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.cleanUp(sa, infos);
			tbc.uninstallTestBundle();
		}
	}
	
	/**
	 * This method asserts if null can be passed as arguments parameter.
	 * 
	 * @spec ApplicationDescriptor.schedule(Map,String,String,boolean)
	 */	
	private void testSchedule002() {
		tbc.log("#testSchedule002");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(
				new PermissionInfo(ApplicationAdminPermission.class.getName(), ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.SCHEDULE_ACTION)
			);


			sa = tbc.getAppDescriptor().schedule(null, "*", null, false);
			
			tbc.installTestBundle();
			
			tbc.assertNotNull("Asserting that a ScheduledApplication was returned when we pass null as arguments parameter.", sa);
			
			synchronized (tbc) {
				tbc.wait(ApplicationConstants.SHORT_TIMEOUT);
			}
			
			tbc.assertEquals("Asserting that a ApplicationHandle was registered.", 1, tbc.getNumberAppHandle());
								
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.cleanUp(sa, infos);
			tbc.uninstallTestBundle();
		}
	}	

	/**
	 * This method asserts if SecurityException is thrown when the caller
	 * does not have "schedule" ApplicationAdminPermission for the application.
	 * 
	 * @spec ApplicationDescriptor.schedule(Map,String,String,boolean)
	 */	
	private void testSchedule003() {
		tbc.log("#testSchedule003");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setDefaultPermission();

			sa = tbc.getAppDescriptor().schedule(null, "*", null, false);

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
	 * This method asserts that if the application descriptor is unregistered
	 * IllegalStateException will be thrown.
	 * 
	 * @spec ApplicationDescriptor.schedule(Map,String,String,boolean)
	 */
	private void testSchedule004() {
		tbc.log("#testSchedule004");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(
				new PermissionInfo(ApplicationAdminPermission.class.getName(), ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.SCHEDULE_ACTION)
			);
			
			tbc.unregisterDescriptor();

			sa = tbc.getAppDescriptor().schedule(null, "*",
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
			tbc.installDescriptor();
			tbc.cleanUp(sa, infos);
		}
	}

	/**
	 * This method asserts that NullPointerException is thrown when the topic 
	 * parameter is null.
	 * 
	 * @spec ApplicationDescriptor.schedule(Map,String,String,boolean)
	 */	
	private void testSchedule005() {
		tbc.log("#testSchedule005");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(
				new PermissionInfo(ApplicationAdminPermission.class.getName(), ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.SCHEDULE_ACTION)
			);

			sa = tbc.getAppDescriptor().schedule(null, null,
					null, false);

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
			tbc.cleanUp(sa, infos);
		}
	}	
	
	/**
	 * This method asserts if an empty string works as a wildcard (*) 
	 * passed in topic parameter.
	 * 
	 * @spec ApplicationDescriptor.schedule(Map,String,String,boolean)
	 */	
	private void testSchedule006() {
		tbc.log("#testSchedule006");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(
				new PermissionInfo(ApplicationAdminPermission.class.getName(), ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.SCHEDULE_ACTION)
			);


			sa = tbc.getAppDescriptor().schedule(null, "", null, false);
			
			tbc.installTestBundle();
			
			tbc.assertNotNull("Asserting that an empty string works as a wildcard(*) in topic parameter.", sa);
			
			synchronized (tbc) {
				tbc.wait(ApplicationConstants.SHORT_TIMEOUT);
			}
			
			tbc.assertEquals("Asserting that a ApplicationHandle was registered.", 1, tbc.getNumberAppHandle());
								
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.cleanUp(sa, infos);
			tbc.uninstallTestBundle();
		}
	}

	
	/**
	 * This method asserts if the topic/eventFilter really works
	 * as a filter for the events an the application will be started
	 * only once.
	 * 
	 * @spec ApplicationDescriptor.schedule(Map,String,String,boolean)
	 */	
	private void testSchedule007() {
		tbc.log("#testSchedule007");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(
				new PermissionInfo(ApplicationAdminPermission.class.getName(), ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.SCHEDULE_ACTION)
			);

			sa = tbc.getAppDescriptor().schedule(null, ApplicationConstants.TIMER_EVENT, ApplicationConstants.EVENT_FILTER, false);
			
			tbc.assertNotNull("Asserting that a ScheduledApplication was returned according to the used filter.", sa);
			
			synchronized (tbc) {
				tbc.wait(ApplicationConstants.SHORT_TIMEOUT);
			}
					
			tbc.assertEquals("Asserting that a ApplicationHandle was registered.", 1, tbc.getNumberAppHandle());
						
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.cleanUp(sa, infos);
		}
	}	
	
	/**
	 * This method asserts if when recurring is true, the scheduling
	 * will take place for every event occurrence.
	 * 
	 * @spec ApplicationDescriptor.schedule(Map,String,String,boolean)
	 */	
	private void testSchedule008() {
		tbc.log("#testSchedule008");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(
				new PermissionInfo(ApplicationAdminPermission.class.getName(), ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.SCHEDULE_ACTION)
			);
			
			sa = tbc.getAppDescriptor().schedule(null, ApplicationConstants.TIMER_EVENT, ApplicationConstants.EVENT_FILTER, true);
			
			tbc.assertNotNull("Asserting that a ScheduledApplication was returned according to the used filter.", sa);
			
			long ticks = System.currentTimeMillis();
			
			for( int counter = 0; counter != 10; counter++ ) {
				try {
					Thread.sleep( 200 );
				}catch( InterruptedException e ) {};
				
				if( tbc.getNumberAppHandle() == 2 )
					break;
			}
			
			long outTicks = System.currentTimeMillis();
		
			if( outTicks - ticks < 1000 || outTicks - ticks > 2100 )
				tbc.fail("Timer too fast!");
					
			tbc.assertEquals("Asserting that two ApplicationHandles were registered.", 2, tbc.getNumberAppHandle());
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.cleanUp(sa, infos);
		}
	}
	
	/**
	 * This method asserts that InvalidSyntaxException is thrown when 
	 * the the specified eventFilter is not syntactically correct . 
	 * 
	 * @spec ApplicationDescriptor.schedule(Map,String,String,boolean)
	 */	
	private void testSchedule009() {
		tbc.log("#testSchedule009");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(
				new PermissionInfo(ApplicationAdminPermission.class.getName(), ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.SCHEDULE_ACTION)
			);

			sa = tbc.getAppDescriptor().schedule(null, ApplicationConstants.TIMER_EVENT, ApplicationConstants.INVALID_FILTER, false);

			tbc.failException("", org.osgi.framework.InvalidSyntaxException.class);
		} catch (org.osgi.framework.InvalidSyntaxException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { org.osgi.framework.InvalidSyntaxException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							org.osgi.framework.InvalidSyntaxException.class.getName(),
							e.getClass().getName() }));
		} finally {
			tbc.cleanUp(sa, infos);
		}
	}	

}

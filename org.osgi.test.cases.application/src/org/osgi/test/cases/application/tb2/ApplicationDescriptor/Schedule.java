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
import org.osgi.service.application.ApplicationException;
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
		testSchedule010();
		testSchedule011();
		testSchedule012();
		testSchedule013();
		testSchedule014();
		testSchedule015();
		testSchedule016();
		testSchedule017();
		testSchedule018();
		testSchedule019();
	}
	
	/**
	 * This method asserts if null can be passed as eventFilter parameter.
	 * 
	 * @spec ApplicationDescriptor.schedule(String,Map,String,String,boolean)
	 */	
	private void testSchedule001() {
		tbc.log("#testSchedule001");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			//remove all previously launched handles
			tbc.destroyHandles();
			
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(
				new PermissionInfo(ApplicationAdminPermission.class.getName(), ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.SCHEDULE_ACTION)
			);

      HashMap map = new HashMap();
			sa = tbc.getAppDescriptor().schedule(null, map, "*", null, false);
			
			tbc.installTestBundle();
			
			tbc.assertNotNull("Asserting that a ScheduledApplication was returned when we pass null as eventFilter parameter.", sa);
			
		  Thread.sleep(ApplicationConstants.SHORT_TIMEOUT);
			
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
	 * @spec ApplicationDescriptor.schedule(String,Map,String,String,boolean)
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


			sa = tbc.getAppDescriptor().schedule(null, null, "*", null, false);
			
			tbc.installTestBundle();
			
			tbc.assertNotNull("Asserting that a ScheduledApplication was returned when we pass null as arguments parameter.", sa);
			
      Thread.sleep(ApplicationConstants.SHORT_TIMEOUT);
			
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
	 * @spec ApplicationDescriptor.schedule(String,Map,String,String,boolean)
	 */	
	private void testSchedule003() {
		tbc.log("#testSchedule003");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setDefaultPermission();

			sa = tbc.getAppDescriptor().schedule(null, null, "*", null, false);

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
	 * @spec ApplicationDescriptor.schedule(String,Map,String,String,boolean)
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

			sa = tbc.getAppDescriptor().schedule(null, null, "*",
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
	 * @spec ApplicationDescriptor.schedule(String,Map,String,String,boolean)
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

			sa = tbc.getAppDescriptor().schedule(null, null, null,
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
	 * @spec ApplicationDescriptor.schedule(String,Map,String,String,boolean)
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


			sa = tbc.getAppDescriptor().schedule(null, null, "", null, false);
			
			tbc.installTestBundle();
			
			tbc.assertNotNull("Asserting that an empty string works as a wildcard(*) in topic parameter.", sa);
			
      Thread.sleep(ApplicationConstants.SHORT_TIMEOUT);
			
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
	 * in the right time.
	 * 
	 * @spec ApplicationDescriptor.schedule(String,Map,String,String,boolean)
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

			sa = tbc.getAppDescriptor().schedule(null, null, ApplicationConstants.TIMER_EVENT, ApplicationConstants.EVENT_FILTER, false);
			
			tbc.assertNotNull("Asserting that a ScheduledApplication was returned according to the used filter.", sa);
			
      int handles = tbc.getNumberAppHandle();
      int counter = 0;
      //cycle until the handle appears or a timeout of 1+ minute passes
      while (handles < 1 && counter < 62) {
        synchronized (this) {
          this.wait(1000);
        }
        handles = tbc.getNumberAppHandle();
        counter++;
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
	 * @spec ApplicationDescriptor.schedule(String,Map,String,String,boolean)
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
			
			sa = tbc.getAppDescriptor().schedule(null, null, ApplicationConstants.TOPIC_EVENT, null, true);
			
			tbc.assertNotNull("Asserting that a ScheduledApplication was returned according to the used filter.", sa);
			
			tbc.sendEvent(ApplicationConstants.TOPIC_EVENT);
			tbc.sendEvent(ApplicationConstants.TOPIC_EVENT);
								
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
	 * @spec ApplicationDescriptor.schedule(String,Map,String,String,boolean)
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

			sa = tbc.getAppDescriptor().schedule(null, null, ApplicationConstants.TIMER_EVENT, ApplicationConstants.INVALID_FILTER, false);

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
	
	/**
	 * This method asserts that IllegalArgumentException is thrown when 
	 * with have passed an empty string as key for the map. 
	 * 
	 * @spec ApplicationDescriptor.schedule(String,Map,String,String,boolean)
	 */	
	private void testSchedule010() {
		tbc.log("#testSchedule010");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(
				new PermissionInfo(ApplicationAdminPermission.class.getName(), ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.SCHEDULE_ACTION)
			);

			HashMap map = new HashMap();
			map.put("", "Rejected");
			sa = tbc.getAppDescriptor().schedule(null, map, ApplicationConstants.TIMER_EVENT, ApplicationConstants.EVENT_FILTER, true);

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
			tbc.cleanUp(sa, infos);
		}
	}	
	
	/**
	 * This method asserts that no exception is thrown
	 * when we have passed null as map value. 
	 * 
	 * @spec ApplicationDescriptor.schedule(String,Map,String,String,boolean)
	 */	
	private void testSchedule011() {
		tbc.log("#testSchedule011");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(
				new PermissionInfo(ApplicationAdminPermission.class.getName(), ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.SCHEDULE_ACTION)
			);

			HashMap map = new HashMap();
			map.put("Test", null);
			sa = tbc.getAppDescriptor().schedule(null, map, ApplicationConstants.TIMER_EVENT, ApplicationConstants.EVENT_FILTER, true);

			tbc.pass("No exception was thrown.");
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
			tbc.cleanUp(sa, infos);
		}
	}
	
	/**
	 * This method asserts that IllegalArgumentException
	 * is thrown when we have passed null as mapKey.
	 * 
	 * @spec ApplicationDescriptor.schedule(String,Map,String,String,boolean)
	 */	
	private void testSchedule012() {
		tbc.log("#testSchedule012");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(
				new PermissionInfo(ApplicationAdminPermission.class.getName(), ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.SCHEDULE_ACTION)
			);

			HashMap map = new HashMap();
			map.put(null, "Rejected");
			sa = tbc.getAppDescriptor().schedule(null, map, ApplicationConstants.TIMER_EVENT, ApplicationConstants.EVENT_FILTER, true);

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
			tbc.cleanUp(sa, infos);
		}
	}	
	
	/**
	 * This method asserts that IllegalArgumentException
	 * is thrown when we have passed an Integer as mapKey.
	 * 
	 * @spec ApplicationDescriptor.schedule(String,Map,String,String,boolean)
	 */	
	private void testSchedule013() {
		tbc.log("#testSchedule013");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(
				new PermissionInfo(ApplicationAdminPermission.class.getName(), ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.SCHEDULE_ACTION)
			);

			HashMap map = new HashMap();
			map.put(new Integer(2), "Rejected");
			sa = tbc.getAppDescriptor().schedule(null, map, ApplicationConstants.TIMER_EVENT, ApplicationConstants.EVENT_FILTER, true);

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
			tbc.cleanUp(sa, infos);
		}
	}	
	
	/**
	 * This method asserts that IllegalArgumentException
	 * is thrown when we have passed an Object as mapKey.
	 * 
	 * @spec ApplicationDescriptor.schedule(String,Map,String,String,boolean)
	 */	
	private void testSchedule014() {
		tbc.log("#testSchedule014");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(
				new PermissionInfo(ApplicationAdminPermission.class.getName(), ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.SCHEDULE_ACTION)
			);

			HashMap map = new HashMap();
			map.put(new Object(), "Rejected");
			sa = tbc.getAppDescriptor().schedule(null, map, ApplicationConstants.TIMER_EVENT, ApplicationConstants.EVENT_FILTER, true);

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
			tbc.cleanUp(sa, infos);
		}
	}	
	
	/**
	 * This method asserts that IllegalArgumentException
	 * is thrown when we have passed a Boolean as mapKey.
	 * 
	 * @spec ApplicationDescriptor.schedule(String,Map,String,String,boolean)
	 */	
	private void testSchedule015() {
		tbc.log("#testSchedule015");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(
				new PermissionInfo(ApplicationAdminPermission.class.getName(), ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.SCHEDULE_ACTION)
			);

			HashMap map = new HashMap();
			map.put(new Boolean(true), "Rejected");
			sa = tbc.getAppDescriptor().schedule(null, map, ApplicationConstants.TIMER_EVENT, ApplicationConstants.EVENT_FILTER, true);

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
			tbc.cleanUp(sa, infos);
		}
	}
	
	/**
	 * This method asserts that IllegalArgumentException
	 * is thrown when we have passed a Float as mapKey.
	 * 
	 * @spec ApplicationDescriptor.schedule(String,Map,String,String,boolean)
	 */	
	private void testSchedule016() {
		tbc.log("#testSchedule016");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(
				new PermissionInfo(ApplicationAdminPermission.class.getName(), ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.SCHEDULE_ACTION)
			);

			HashMap map = new HashMap();
			map.put(new Float(2.3f), "Rejected");
			sa = tbc.getAppDescriptor().schedule(null, map, ApplicationConstants.TIMER_EVENT, ApplicationConstants.EVENT_FILTER, true);

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
			tbc.cleanUp(sa, infos);
		}
	}
	
	/**
	 * This method asserts that IllegalArgumentException
	 * is thrown when we have passed a Byte as mapKey.
	 * 
	 * @spec ApplicationDescriptor.schedule(String,Map,String,String,boolean)
	 */	
	private void testSchedule017() {
		tbc.log("#testSchedule017");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(
				new PermissionInfo(ApplicationAdminPermission.class.getName(), ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.SCHEDULE_ACTION)
			);

			HashMap map = new HashMap();
			map.put(new Byte("a"), "Rejected");
			sa = tbc.getAppDescriptor().schedule(null, map, ApplicationConstants.TIMER_EVENT, ApplicationConstants.EVENT_FILTER, true);

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
			tbc.cleanUp(sa, infos);
		}
	}
	
	/**
	 * This method asserts that IllegalArgumentException
	 * is thrown when we have passed a Double as mapKey.
	 * 
	 * @spec ApplicationDescriptor.schedule(String,Map,String,String,boolean)
	 */	
	private void testSchedule018() {
		tbc.log("#testSchedule018");
		PermissionInfo[] infos = null;
		ScheduledApplication sa = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(
				new PermissionInfo(ApplicationAdminPermission.class.getName(), ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.SCHEDULE_ACTION)
			);

			HashMap map = new HashMap();
			map.put(new Double(0.2), "Rejected");
			sa = tbc.getAppDescriptor().schedule(null, map, ApplicationConstants.TIMER_EVENT, ApplicationConstants.EVENT_FILTER, true);

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
			tbc.cleanUp(sa, infos);
		}
	}	
	
    /**
     * This method asserts if ApplicationException is thrown when two ScheduledApplication
     * is created with the same id.  
     * 
     * @spec ApplicationDescriptor.schedule(String,Map,String,String,boolean)
     */    
	private void testSchedule019() {
		tbc.log("#testSchedule019");
		PermissionInfo[] infos = null;
		ScheduledApplication sa1 = null, sa2 = null;
		String id = "scheduleId";
		try {
            infos = tbc.getPermissionAdmin().getPermissions(tbc.getTb2Location());

            tbc.setLocalPermission(new PermissionInfo(ApplicationAdminPermission.class.getName(), 
        		                       ApplicationConstants.APPLICATION_PERMISSION_FILTER1, 
        		                       ApplicationAdminPermission.SCHEDULE_ACTION));
            
            sa1 = tbc.getAppDescriptor().schedule(id, null, "TestingPurposes*", null, true);
            sa2 = tbc.getAppDescriptor().schedule(id, null, "TestingPurposes*", null, true);            
            tbc.failException("", ApplicationException.class);
		} catch (ApplicationException e) {
			tbc.assertEquals("Asserting if the error code is APPLICATION_DUPLICATE_SCHEDULED_ID.", ApplicationException.APPLICATION_DUPLICATE_SCHEDULE_ID, e.getErrorCode());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							ApplicationException.class.getName(),
							e.getClass().getName() }));
		} finally {
			tbc.cleanUp(sa1, infos);
			tbc.cleanUp(sa2, infos);
		}
	}	
	

}
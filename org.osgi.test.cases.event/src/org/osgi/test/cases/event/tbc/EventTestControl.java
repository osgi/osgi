/*
 * $Header$
 * 
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
 */
package org.osgi.test.cases.event.tbc;

import java.util.Hashtable;

import org.osgi.framework.*;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.util.DefaultTestBundleControl;
import org.osgi.service.event.*;

/**
 * <remove>The TemplateControl controls is downloaded in the target and will control the
 * test run. The description of this test cases should contain the overall
 * execution of the run. This description is usuall quite minimal because the
 * main description is in the TemplateTestCase.</remove>
 * 
 * TODO Add Javadoc comment for this.
 * 
 * @version $Revision$
 */
public class EventTestControl extends DefaultTestBundleControl {
	
	private ServiceReference eventAdminSR;
  private EventAdmin eventAdmin;
  private Bundle tb1;
  private Bundle tb2;
  private static String[] methods = new String[] {"testIstallation", // "TC1"
                                                  "testSetPermissions", // "TC2"
                                                  "testEventConstruction", // "TC3"
                                                  "testPostEventNotification", // "TC4"
                                                  };

  /**
   * Returns a list containing the names of the testmethods that
   * should be called. Overriden.
   */
  protected String[] getMethods() {
    return methods;
  }
  
  /**
	 * <remove>Prepare for each run. It is important that a test run is properly
	 * initialized and that each case can run standalone. To save a lot
	 * of time in debugging, clean up all possible persistent remains
	 * before the test is run. Clean up is better don in the prepare
	 * because debugging sessions can easily cause the unprepare never
	 * to be called.</remove> 
   * @throws Exception
	 */
	public void prepare() throws Exception {
		log("#before each run");
    tb1 = installBundle("tb1.jar");
    tb2 = installBundle("tb2.jar");
	}

	/**
	 * <remove>Prepare for each method. It is important that each method can
	 * be executed independently of each other method. Do not keep
	 * state between methods, if possible. This method can be used
	 * to clean up any possible remaining state.</remove> 
	 */
	public void setState() {
		log("#before each method");
	}
  
  public boolean checkPrerequisites() {
    log("#checkPrerequisites");
    eventAdminSR = getContext().getServiceReference(EventAdmin.class.getName());
    if (eventAdminSR == null) return false;
    eventAdmin = (EventAdmin) getContext().getService(eventAdminSR);
    if (eventAdmin == null) return false;
    return true;
  }
  
  /**
   * System bundle exports system services.
   * 
   * Verify that the System bundle exists and exports the
   * system services: PackageAdmin, PermissionAdmin.
   * @throws Exception
   *
   * @specification     org.osgi.framework
   * @specificationSection    system.bundle
   * @specificationVersion  3
   */
  public void testIstallation() throws Exception {
    assertBundle(TBCService.class.getName(), tb1);
    assertBundle(EventHandler.class.getName(), tb1);
    
    assertBundle(TBCService.class.getName(), tb2);
    assertBundle(EventHandler.class.getName(), tb2);
    
    //Bundle system = getContext().getBundle(0);
    //assertBundle(PackageAdmin.class.getName(), system);
  }


	/**
	 * Tests if the permissions are set correctly and the exceptions 
   * that are thrown if they are not.
	 */
	public void testSetPermissions() {
    PermissionAdmin permissionAdmin = (PermissionAdmin)getRegistry().getService(PermissionAdmin.class);
    //try to send event without PUBLISH TopicPermission
    Hashtable properties = new Hashtable();
    Hashtable ht = new Hashtable();
    ht.put("topic", "org/osgi/test/cases/event");
    Event event1 = new Event("org/osgi/test/cases/event/ACTION1", properties);
    String message = "The caller does not have TopicPermission[topic,PUBLISH] for the topic: [";
    try {
      eventAdmin.sendEvent(event1);
      failException(message + event1.getTopic() + "]", SecurityException.class);
    } catch (Throwable e) {
      assertException(message + event1.getTopic() + "]", SecurityException.class, e);
    }
    //try to send event with PUBLISH TopicPermission
    PermissionInfo permInfo = new PermissionInfo(ServicePermission.class.getName(), 
                                                 "org.osgi.service.event.EventAdmin", 
                                                 ServicePermission.GET);
    permissionAdmin.setPermissions(getContext().getBundle().getLocation(), 
                                   new PermissionInfo[]{permInfo});
    try {
      eventAdmin.sendEvent(event1);
      failException(message + event1.getTopic() + "]", SecurityException.class);
    } catch (Throwable e) {
      assertException(message + event1.getTopic() + "]", SecurityException.class, e);
    }
    
    PermissionInfo regInfo = new PermissionInfo(ServicePermission.class.getName(), 
                                                "org.osgi.service.event.EventHandler", 
                                                ServicePermission.REGISTER);
    //set permissions to tb1
    PermissionInfo topInfo1 = new PermissionInfo(TopicPermission.class.getName(), 
                                                 "org/*", 
                                                 TopicPermission.SUBSCRIBE);
    permissionAdmin.setPermissions(tb1.getLocation(), new PermissionInfo[]{regInfo, topInfo1});
    //set permissions to tb2
    PermissionInfo topInfo2 = new PermissionInfo(TopicPermission.class.getName(), 
                                                 "org/osgi/*", 
                                                 TopicPermission.SUBSCRIBE);
    permissionAdmin.setPermissions(tb2.getLocation(), new PermissionInfo[]{regInfo, topInfo2});   
		
	}
  
  /**
   * Tests the event construction and the exceptions 
   * that are thrown if event topic doesn't conform to the following grammar: 
   * topic := token ( "/" token )*
   */
  public void testEventConstruction() {
    String[] illegalTopics = new String[] {"", "*", "/error_topic", "//error_topic1", "1/error_topic2", "topic/error_topic3/", "error_topic&"};
    String[]   legalTopics = new String[] {"ACTION0", "org/osgi/test/cases/event/ACTION1", "org/osgi/test/cases/event/ACTION2"};
    Hashtable properties = new Hashtable();
    String message = "Exception in event construction with topic:[";
    String topic;
    //illigal topics tested
    for (int i = 0; i < illegalTopics.length; i++) {
      topic = illegalTopics[i];
      try {
        new Event(topic, properties);
        failException(message + topic + "]" , IllegalArgumentException.class);
      } catch (Throwable e) {
        assertException(message + topic + "]", IllegalArgumentException.class, e);
      }      
    }
    //legal topics tested
    for (int i = 0; i < legalTopics.length; i++) {
      topic = legalTopics[i];
      try {
        new Event(topic, properties);
        pass("Event constructed with topic: " + topic);
      } catch (Throwable e) {
        fail(message + topic + "]");
      }
    }
  }
  
  /**
   * Tests the notification for events after posting 
   * (if they match of the listeners).
   */
  public void testPostEventNotification() {
    TBCService tbcService1 = (TBCService) tb1;
    TBCService tbcService2 = (TBCService) tb2;
    
    String[] topics;
    topics = new String[] {"org/osgi/test/*", "org/osgi/newtest1/*", "org/osgi1/*", "Event1"};
    tbcService1.setTopics(topics);
    topics = new String[] {"org/osgi/test/*", "org/osgi/newtest1/newtest2/*", "org/osgi2/*"};
    tbcService2.setTopics(topics);
    
    String[] events = new String[] {"org/osgi/test/Event0", "Event1", "org/osgi1/Event2", "org/osgi1/test/Event3", 
                                    "org/osgi/newtest1/Event4", "org/osgi/newtest2/Event5", "org/osgi2/test/Event6"};
    Boolean[] eventsMap1 = new Boolean[] {Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, 
                                          Boolean.TRUE, Boolean.FALSE, Boolean.FALSE};
    Boolean[] eventsMap2 = new Boolean[] {Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, 
                                          Boolean.FALSE, Boolean.FALSE, Boolean.TRUE};
    
    Event event;
    for (int i = 0; i < events.length; i++) {
      event = new Event(events[i], new Hashtable());
      eventAdmin.postEvent(event);
      assertEvent(event, tb1, tbcService1, eventsMap1[i].booleanValue());
      assertEvent(event, tb2, tbcService2, eventsMap2[i].booleanValue());
    }
  }

	
	/**
	 * Verify that the service with name is exported by the bundle b.
	 * 
	 * @param name		fqn of the service, e.g. com.acme.foo.Foo
   * @param b       the bundle to be asserted
	 */
	private void assertBundle(String name, Bundle b) {
		ServiceReference	ref = getContext().getServiceReference(name);
		assertNotNull(name + "  service must be registered ", ref);
		assertEquals("Invalid exporter for " + name, b, ref.getBundle());
	}
  
  private void assertEvent(Event eventPassed, Bundle bundle, TBCService tbcService, boolean recieved) {
    pass("                Passed event: " + eventPassed);
    pass("Bundle's event handler topic: " + arrayToString(tbcService.getTopics()));
    Event eventReceived = tbcService.getLastReceivedEvent();
    assertEquals("In [" + bundle.getSymbolicName() + "] received event [" + eventReceived + "]",
                 recieved, eventReceived != null);    
    if (eventReceived != null) {
      String[] properties = eventReceived.getPropertyNames();
      String property;
      for (int i = 0; i < properties.length; i++) {
        property = properties[i];
        Object value = eventReceived.getProperty(property);
      }
    }
  }
	
	/**
	 * Clean up after each method. Notice that during debugging
	 * many times the unsetState is never reached.
	 */
	public void unsetState() {
		log("#after each method");
	}

	/**
	 * Clean up after a run. Notice that during debugging
	 * many times the unprepare is never reached.
	 * @throws Exception
	 */
	public void unprepare() throws Exception {
		log("#after each run");
    uninstallBundle(tb1);
    uninstallBundle(tb2);
	}  
}
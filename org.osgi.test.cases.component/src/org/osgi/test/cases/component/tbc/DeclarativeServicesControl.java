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
package org.osgi.test.cases.component.tbc;

import java.util.Dictionary;

import org.osgi.framework.*;
import org.osgi.test.cases.util.DefaultTestBundleControl;
import org.osgi.util.tracker.ServiceTracker;

/**
 * This is the bundle initially installed and started by the TestCase.
 * It performs the test methods of the declarative services test case.
 * 
 * @version $Revision$
 */
public class DeclarativeServicesControl extends DefaultTestBundleControl {
  
  private Bundle tb1, tb2, tb3;
  private static String[] methods = new String[] {
	  "testRegistration",     // "TC1"
	  "testGetServiceDirect", // "TC2"
	  "testGetServiceLookup", // "TC3"
	  "testGetServiceEvent",  // "TC4"
	  "testGetProperties",    // "TC5"
	  "testFactory",          // "TC6"
  };
  
  /**
   * Returns a list containing the names of the test methods in the order they should be called.
   */
  protected String[] getMethods() {
    return methods;
  }
  
	public void prepare() throws Exception {
    tb1 = installBundle("tb1.jar");
    tb1.start();
    tb2 = installBundle("tb2.jar");
    tb2.start();
    tb3 = installBundle("tb3.jar");
    tb3.start();
	}

	public void setState() {
	}

  /**
   * Tests registering / unregistering of the component bundles and their provided services.
   */
  public void testRegistration() throws Exception {
    TBCService serviceProvider, serviceConsumerLookup, serviceConsumerEvent;
    ServiceTracker trackerProvider = new ServiceTracker(getContext(), "org.osgi.test.cases.component.tb1.ServiceProvider", null);
    trackerProvider.open();
    ServiceTracker trackerConsumerLookup = new ServiceTracker(getContext(), "org.osgi.test.cases.component.tb2.ServiceConsumerLookup", null);
    trackerConsumerLookup.open();
    ServiceTracker trackerConsumerEvent = new ServiceTracker(getContext(), "org.osgi.test.cases.component.tb3.ServiceConsumerEvent", null);
    trackerConsumerEvent.open();

    assertEquals("ServiceProvider bundle should be in active state", Bundle.ACTIVE, tb1.getState());
    serviceProvider = (TBCService) trackerProvider.getService();
    assertNotNull("ServiceProvider service should be registered", serviceProvider);

    assertEquals("ServiceConsumerLookup bundle should be in active state", Bundle.ACTIVE, tb2.getState());
    serviceConsumerLookup = (TBCService) trackerConsumerLookup.getService();
    assertNotNull("ServiceConsumerLookup service should be registered", serviceConsumerLookup);

    assertEquals("ServiceConsumerEvent bundle should be in active state", Bundle.ACTIVE, tb3.getState());
    serviceConsumerEvent = (TBCService) trackerConsumerEvent.getService();
    assertNotNull("ServiceConsumerEvent service should be registered", serviceConsumerEvent);
    
    tb1.uninstall();
    serviceProvider = (TBCService) trackerProvider.getService();
    assertNull("ServiceProvider service should not be registered", serviceProvider);

    tb1 = installBundle("tb1.jar");
    assertEquals("ServiceProvider bundle should be in resolved state", Bundle.RESOLVED, tb1.getState());
    serviceProvider = (TBCService) trackerProvider.getService();
    assertNull("ServiceProvider service should not be registered", serviceProvider);

    tb1.start();
    assertEquals("ServiceProvider bundle should be in active state", Bundle.ACTIVE, tb1.getState());
    serviceProvider = (TBCService) trackerProvider.getService();
    assertNotNull("ServiceProvider service should be registered", serviceProvider);

    trackerProvider.close();
    trackerConsumerLookup.close();
    trackerConsumerEvent.close();
  }

  /**
   * Tests getting of the provided service from the first component bundle directly.
   */
	public void testGetServiceDirect() {
    ServiceTracker tracker = new ServiceTracker(getContext(), "org.osgi.test.cases.component.tb1.ServiceProvider", null);
    tracker.open();
    TBCService serviceProvider = (TBCService) tracker.getService();
    TestObject testObject = serviceProvider.getTestObject();
    assertEquals("The return value should be an instance of " + TestObject.class.getName(),
    		TestObject.class.getName(), testObject.getClass().getName());
    tracker.close();
	}

  /**
   * Tests getting of the provided service of the second component bundle through the lookup method.
   */
  public void testGetServiceLookup() {
    ServiceTracker tracker = new ServiceTracker(getContext(), "org.osgi.test.cases.component.tb2.ServiceConsumerLookup", null);
    tracker.open();
    TBCService serviceConsumerLookup = (TBCService) tracker.getService();
    TestObject testObject = serviceConsumerLookup.getTestObject();
    assertEquals("The return value should be an instance of " + TestObject.class.getName(),
    		TestObject.class.getName(), testObject.getClass().getName());
    tracker.close();
  }

  /**
   * Tests getting of the provided service of the third component bundle through the event method.
   */
  public void testGetServiceEvent() {
    ServiceTracker tracker = new ServiceTracker(getContext(), "org.osgi.test.cases.component.tb3.ServiceConsumerEvent", null);
    tracker.open();
    TBCService serviceConsumerEvent = (TBCService) tracker.getService();
    TestObject testObject = serviceConsumerEvent.getTestObject();
    assertEquals("The return value should be an instance of " + TestObject.class.getName(),
    		TestObject.class.getName(), testObject.getClass().getName());
    tracker.close();
  }

  /**
   * Tests getting of the component properties from the second component bundle
   * set through property and properties elements in XML.
   */
  public void testGetProperties() {
    ServiceTracker tracker = new ServiceTracker(getContext(), "org.osgi.test.cases.component.tb2.ServiceConsumerLookup", null);
    tracker.open();
    TBCService serviceConsumerLookup = (TBCService) tracker.getService();
    Dictionary properties = serviceConsumerLookup.getProperties();
    assertNotNull("Properties should not be empty", properties);
    assertTrue("The size of properties should be at least 2", properties.size() >= 2);
    assertEquals("Value of test.property.int should be equal to '123'", properties.get("test.property.int"), "123");
    String[] array = (String[]) properties.get("test.property.string");
    assertEquals("The size of test.property.string array should be 3", array.length, 3);
    for (int i = 0; i < 3; i++) {
      assertEquals("Element [" + i + "] of test.property.string array should be 'Value " + (i + 1) + "'", array[i], "Value " + (i + 1));
    }
    tracker.close();
  }

  /**
   * TODO
   */
  public void testFactory() {
  }

	/**
	 * Clean up after each method. Notice that during debugging
	 * many times the unsetState is never reached.
	 */
	public void unsetState() {
	}

	/**
	 * Clean up after a run. Notice that during debugging
	 * many times the unprepare is never reached.
	 */
	public void unprepare() throws Exception {
    tb1.stop();
    uninstallBundle(tb1);
    tb2.stop();
    uninstallBundle(tb2);
    tb3.stop();
    uninstallBundle(tb3);
	}
}

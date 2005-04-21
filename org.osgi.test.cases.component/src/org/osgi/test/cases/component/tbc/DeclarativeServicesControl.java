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
import java.util.Hashtable;

import org.osgi.framework.*;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;
import org.osgi.test.cases.util.DefaultTestBundleControl;
import org.osgi.util.tracker.ServiceTracker;

/**
 * This is the bundle initially installed and started by the TestCase.
 * It performs the test methods of the declarative services test case.
 *
 * @version $Revision$
 */
public class DeclarativeServicesControl extends DefaultTestBundleControl implements FrameworkListener {

  private Bundle tb1, tb2, tb3;
  
  private static String[] methods = new String[] {
    "testRegistration",     // "TC1"
    "testGetServiceDirect", // "TC2"
    "testGetServiceLookup", // "TC3"
    "testGetServiceEvent",  // "TC4"
    "testGetProperties",    // "TC5"
    "testComponentFactory", // "TC6"
    "testBadComponents"     // TC7
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
    Thread.yield();
    tb2 = installBundle("tb2.jar");
    tb2.start();
    Thread.yield();
    tb3 = installBundle("tb3.jar");
    tb3.start();
    Thread.yield();
  }

  public void setState() {
  }

  /**
   * Tests registering / unregistering of the component bundles and their provided services.
   *
   * @throws Exception can be thrown at any time
   */
  public void testRegistration() throws Exception {
    TBCService serviceProvider, serviceConsumerLookup, serviceConsumerEvent;

    ServiceTracker trackerProvider = new ServiceTracker(getContext(), "org.osgi.test.cases.component.tb1.ServiceProvider", null);
    trackerProvider.open();

    ServiceTracker trackerConsumerLookup = new ServiceTracker(getContext(), "org.osgi.test.cases.component.tb2.ServiceConsumerLookup", null);
    trackerConsumerLookup.open();

    ServiceTracker trackerConsumerEvent = new ServiceTracker(getContext(), "org.osgi.test.cases.component.tb3.ServiceConsumerEvent", null);
    trackerConsumerEvent.open();

    serviceProvider = (TBCService) trackerProvider.getService();
    assertTrue("ServiceProvider bundle should be in active state", tb1.getState() == Bundle.ACTIVE);
    assertNotNull("ServiceProvider service should be registered", serviceProvider);

    serviceConsumerLookup = (TBCService) trackerConsumerLookup.getService();
    assertTrue("ServiceConsumerLookup bundle should be in active state", tb2.getState() == Bundle.ACTIVE);
    assertNotNull("ServiceConsumerLookup service should be registered", serviceConsumerLookup);

    serviceConsumerEvent = (TBCService) trackerConsumerEvent.getService();
    assertTrue("ServiceConsumerEvent bundle should be in active state", tb3.getState() == Bundle.ACTIVE);
    assertNotNull("ServiceConsumerEvent service should be registered", serviceConsumerEvent);

    tb1.uninstall();
    serviceProvider = (TBCService) trackerProvider.getService();
    assertNull("ServiceProvider service should not be registered", serviceProvider);

    tb1 = installBundle("tb1.jar");
    tb1.stop();
    Thread.yield();
    serviceProvider = (TBCService) trackerProvider.getService();
    assertTrue("ServiceProvider bundle should be in resolved state", tb1.getState() == Bundle.RESOLVED);
    assertNull("ServiceProvider service should not be registered", serviceProvider);

    tb1.start();
    Thread.yield();
    serviceProvider = (TBCService) trackerProvider.getService();
    assertTrue("ServiceProvider bundle should be in active state", tb1.getState() == Bundle.ACTIVE);
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
    assertEquals("The return value should be an instance of TestObject", TestObject.class, testObject.getClass());
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
    assertEquals("The return value should be an instance of TestObject", TestObject.class, testObject.getClass());
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
    assertEquals("The return value should be an instance of TestObject", TestObject.class, testObject.getClass());
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
    for (int i = 0; i < 3; i++ ) {
      assertEquals("Element [" + i + "] of test.property.string array should be 'Value " + (i + 1) + "'", array[i], "Value " + (i + 1));
    }
    tracker.close();
  }

  /**
   * This method will check the correct behaviour of component factories
   * registrations. It will perform the following checks:
   *
   * <pre>
   *
   *  + install a component factory bundle
   *   - make sure that the component factory is registered as service
   *  + create a new component (using a component factory)
   *   - make sure that the component is registered as service
   *   - make sure that service is registered with correct properties
   *   - make sure that the service receives the correct properties in the activate method.
   *  + stop the component factory bundle
   *   - make sure that all components created by this factory are also disposed
   *
   * </pre>
   *
   * @throws Exception
   */
  public void testComponentFactory() throws Exception {
    BundleContext bc = getContext();

    Filter filter = bc.createFilter("(&(" + ComponentConstants.COMPONENT_FACTORY
                                    + "=org.osgi.test.cases.component.tb4.NamedService)("
                                    + Constants.OBJECTCLASS + '='
                                    + ComponentFactory.class.getName() + "))");
    ServiceTracker componentFactoryTracker = new ServiceTracker(bc, filter, null);
    ServiceTracker instancesTracker = new ServiceTracker(bc, "org.osgi.test.cases.component.tb4.NamedService", null);

    Bundle bundle = installBundle("tb4.jar");
    bundle.start();
    Thread.yield();

    componentFactoryTracker.open();
    instancesTracker.open();
    Hashtable props;
    try {
      int oldCount = instancesTracker.getTrackingCount();
      ComponentFactory factory = (ComponentFactory) componentFactoryTracker.getService();

      // create the first service
      props = new Hashtable();
      props.put("name", "hello");
      factory.newInstance(props);

      assertEquals("The instances tracking count should be increased by one", oldCount + 1, instancesTracker.getTrackingCount());
      boolean serviceFound = false;
      // find the service, the reference must have the properties set correctly
      ServiceReference refs[] = instancesTracker.getServiceReferences();
      for (int i = 0; refs != null && i < refs.length; i++) {
        ServiceReference current = refs[i];
        String name = (String) current.getProperty("name");
        if ("hello".equals(name)) {
          serviceFound = true;
          Object service = instancesTracker.getService(current);
          assertEquals("The service must have the same name", "hello", service.toString());
          break;
        }
      }
      // fail if the service was not found
      assertTrue("A service should be registered", serviceFound);

      // create the second service
      props = new Hashtable();
      props.put("name", "world");
      ComponentInstance worldInstance = factory.newInstance(props);

      // make sure that the new service is registered
      assertEquals("The instances tracking count should be increased by two", oldCount + 2, instancesTracker.getTrackingCount());

      worldInstance.dispose();
      assertTrue("The service should be unregistered if the instance is disposed", instancesTracker.getServices().length == (oldCount + 1));

      // stop the bundle in order to check if some services remained registered
      bundle.stop();
      Thread.yield();

      assertNull("The component factory must be unregistered", componentFactoryTracker.getService());
      assertNull("All component instances, created by the factory should be unregistered", instancesTracker.getService());
    } finally {
      componentFactoryTracker.close();
      instancesTracker.close();
    }
  }

  Bundle errorBundle;

  /**
   * This method tests the behaviour of the service component runtime against bad component definitions.
   *
   * It will perform the following checks:
   *
   * <pre>
   *
   *  + install bundle with illegal component definition
   *   verify the following illegal conditions:
   *   - more that one implementation definition
   *   - both factory &amp; serviceFactory are specified
   *   - reference has a bind method but doesn't have unbind and reverse
   *  + check if framework error is posted
   *
   * </pre>
   *
   * @throws Exception can be thrown at any time
   */
  public void testBadComponents() throws Exception {
    BundleContext bc = getContext();

    tb1.uninstall();

    // clear any previously reported 'error' bundles
    errorBundle = null;
    bc.addFrameworkListener(this);

    // the bundle contains some illegal definitions
    tb1 = installBundle("tb1.jar");
    tb1.start();
    Thread.yield();

    bc.removeFrameworkListener(this);

    // make sure that SCR reports some errors for tb1
    assertSame("The Service Component Runtime should post a framework error for tb1", tb1, errorBundle);

    // make sure that the services are not registered
    ServiceReference ref;
    ref = bc.getServiceReference("org.osgi.test.cases.component.tb1.BadService1");
    assertNull("The BadService1 shouldn't be registered because the XML contains two implementation classes", ref);

    ref = bc.getServiceReference("org.osgi.test.cases.component.tb1.BadService2");
    assertNull("The BadService2 shouldn't be registered because component & service factories are incompatible", ref);

    ref = bc.getServiceReference("org.osgi.test.cases.component.tb3.MissingBindMethods");
    assertNull("The MissingBindMethods shouldn't be registered because doesn't have both bind & unbind methods", ref);
  }

  /**
   * Clean up after each method. Notice that during debugging many times the unsetState is never reached.
   */
  public void unsetState() {
  }

  /**
   * Clean up after a run. Notice that during debugging many times the unprepare is never reached.
   */
  public void unprepare() throws Exception {
    tb1.stop();
    Thread.yield();
    uninstallBundle(tb1);
    tb2.stop();
    Thread.yield();
    uninstallBundle(tb2);
    tb3.stop();
    Thread.yield();
    uninstallBundle(tb3);
  }

  public void frameworkEvent(FrameworkEvent event) {
    if (event.getType() == FrameworkEvent.ERROR) {
      errorBundle = event.getBundle();
    }
  }
}

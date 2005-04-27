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
 * This is the bundle initially installed and started by the TestCase. It
 * performs the test methods of the declarative services test case.
 * 
 * @version $Revision$
 */
public class DeclarativeServicesControl extends DefaultTestBundleControl
		implements FrameworkListener {

	private Bundle			tb1, tb2, tb3;

	private static String[]	methods	= new String[] {"testRegistration", // "TC1"
									"testGetServiceDirect", // "TC2"
									"testGetServiceLookup", // "TC3"
									"testGetServiceEvent", // "TC4"
									"testGetProperties", // "TC5"
									"testStartStopSCR", // "TC7"
									"testComponentFactory", // "TC8"
									"testBadComponents", // TC9
									"testDynamicBind", // TC10
									};

	private ServiceTracker	trackerProvider;
	private ServiceTracker	trackerConsumerLookup;
	private ServiceTracker	trackerConsumerEvent;
	private ServiceTracker	trackerNamedService;
	private ServiceTracker	trackerNamedServiceFactory;

	/**
	 * Returns a list containing the names of the test methods in the order they
	 * should be called.
	 * 
	 * @see org.osgi.test.cases.util.DefaultTestBundleControl#getMethods()
	 */
	protected String[] getMethods() {
		return methods;
	}

	public void prepare() throws Exception {
		// install test cases
		tb1 = installBundle("tb1.jar");
		tb2 = installBundle("tb2.jar");
		tb3 = installBundle("tb3.jar");

		// start them
		tb1.start();
		tb2.start();
		tb3.start();
		Thread.yield();

		// init trackers
		BundleContext bc = getContext();

		trackerProvider = new ServiceTracker(bc,
				"org.osgi.test.cases.component.tb1.ServiceProvider", null);
		trackerConsumerLookup = new ServiceTracker(bc,
				"org.osgi.test.cases.component.tb2.ServiceConsumerLookup", null);
		trackerConsumerEvent = new ServiceTracker(bc,
				"org.osgi.test.cases.component.tb3.ServiceConsumerEvent", null);
		trackerNamedService = new ServiceTracker(bc,
				"org.osgi.test.cases.component.tb4.NamedService", null);
		Filter filter = bc.createFilter("(&("
				+ ComponentConstants.COMPONENT_FACTORY
				+ "=org.osgi.test.cases.component.tb4.NamedService)("
				+ Constants.OBJECTCLASS + '='
				+ ComponentFactory.class.getName() + "))");
		trackerNamedServiceFactory = new ServiceTracker(bc, filter, null);

		// start listening
		trackerProvider.open();
		trackerConsumerLookup.open();
		trackerConsumerEvent.open();
		trackerNamedService.open();
		trackerNamedServiceFactory.open();
	}

	/**
	 * Clean up after a run. Notice that during debugging many times the
	 * unprepare is never reached.
	 * 
	 * @throws Exception is the bundles are not installed
	 * @see org.osgi.test.cases.util.DefaultTestBundleControl#unprepare()
	 */
	public void unprepare() throws Exception {
		uninstallBundle(tb1);
		uninstallBundle(tb2);
		uninstallBundle(tb3);
		Thread.yield();
		// stop listening

		trackerProvider.close();
		trackerConsumerLookup.close();
		trackerConsumerEvent.close();
		trackerNamedService.close();
		trackerNamedServiceFactory.close();
	}

	/**
	 * Tests registering / unregistering of the component bundles and their
	 * provided services.
	 * 
	 * @throws Exception can be thrown at any time
	 */
	public void testRegistration() throws Exception {
		TBCService serviceProvider, serviceConsumerLookup, serviceConsumerEvent;

		serviceProvider = (TBCService) trackerProvider.getService();
		assertEquals("ServiceProvider bundle should be in active state",
				Bundle.ACTIVE, tb1.getState());
		assertNotNull("ServiceProvider service should be registered",
				serviceProvider);

		serviceConsumerLookup = (TBCService) trackerConsumerLookup.getService();
		assertEquals("ServiceConsumerLookup bundle should be in active state",
				Bundle.ACTIVE, tb2.getState());
		assertNotNull("ServiceConsumerLookup service should be registered",
				serviceConsumerLookup);

		serviceConsumerEvent = (TBCService) trackerConsumerEvent.getService();
		assertEquals("ServiceConsumerEvent bundle should be in active state",
				Bundle.ACTIVE, tb3.getState());
		assertNotNull("ServiceConsumerEvent service should be registered",
				serviceConsumerEvent);

		tb1.uninstall();
		serviceProvider = (TBCService) trackerProvider.getService();
		assertNull("ServiceProvider service should not be registered",
				serviceProvider);

		tb1 = installBundle("tb1.jar");
		tb1.stop();
		Thread.yield();
		serviceProvider = (TBCService) trackerProvider.getService();
		assertEquals("ServiceProvider bundle should be in resolved state",
				Bundle.RESOLVED, tb1.getState());
		assertNull("ServiceProvider service should not be registered",
				serviceProvider);

		tb1.start();
		Thread.yield();
		serviceProvider = (TBCService) trackerProvider.getService();
		assertEquals("ServiceProvider bundle should be in active state",
				Bundle.ACTIVE, tb1.getState());
		assertNotNull("ServiceProvider service should be registered",
				serviceProvider);
	}

	/**
	 * Tests getting of the provided service from the first component bundle
	 * directly.
	 */
	public void testGetServiceDirect() {
		TBCService serviceProvider = (TBCService) trackerProvider.getService();
		TestObject testObject = serviceProvider.getTestObject();
		assertEquals("The return value should be an instance of TestObject",
				TestObject.class, testObject.getClass());
	}

	/**
	 * Tests getting of the provided service of the second component bundle
	 * through the lookup method.
	 */
	public void testGetServiceLookup() {
		TBCService serviceConsumerLookup = (TBCService) trackerConsumerLookup
				.getService();
		TestObject testObject = serviceConsumerLookup.getTestObject();
		assertEquals("The return value should be an instance of TestObject",
				TestObject.class, testObject.getClass());
	}

	/**
	 * Tests getting of the provided service of the third component bundle
	 * through the event method.
	 */
	public void testGetServiceEvent() {
		TBCService serviceConsumerEvent = (TBCService) trackerConsumerEvent
				.getService();
		TestObject testObject = serviceConsumerEvent.getTestObject();
		assertEquals("The return value should be an instance of TestObject",
				TestObject.class, testObject.getClass());
	}

	/**
	 * Tests getting of the component properties from the second component
	 * bundle set through property and properties elements in XML.
	 */
	public void testGetProperties() {
		TBCService serviceConsumerLookup = (TBCService) trackerConsumerLookup
				.getService();
		Dictionary properties = serviceConsumerLookup.getProperties();
		assertNotNull("Properties should not be empty", properties);
		assertTrue("The size of properties should be at least 2", properties
				.size() >= 2);
		assertEquals("Value of test.property.int should be equal to '123'",
				"123", properties.get("test.property.int"));
		String[] array = (String[]) properties.get("test.property.string");
		assertEquals("The size of test.property.string array should be 3", 3,
				array.length);
		for (int i = 0; i < 3; i++) {
			assertEquals("Element [" + i
					+ "] of test.property.string array should be 'Value "
					+ (i + 1) + "'", array[i], "Value " + (i + 1));
		}
	}

	/**
	 * This method will check the correct behaviour of component factories
	 * registrations. It will perform the following checks:
	 * 
	 * <code> 
	 * + install a component factory bundle 
	 *  - make sure that the component factory is registered as service 
	 * + create a new component (using a component factory) 
	 *  - make sure that the component is registered as service 
	 *  - make sure that service is registered with correct properties 
	 *  - make sure that the service receives the correct properties in the
	 *    activate method. 
	 * + stop the component factory bundle 
	 *  - make sure that all components created by this factory are also disposed
	 * </code>
	 * 
	 * @throws Exception
	 */
	public void testComponentFactory() throws Exception {
		Bundle bundle = installBundle("tb4.jar");
		bundle.start();
		Thread.yield();

		Hashtable props;
		int oldCount = trackerNamedService.getTrackingCount();
		ComponentFactory factory = (ComponentFactory) trackerNamedServiceFactory
				.getService();

		// create the first service
		props = new Hashtable();
		props.put("name", "hello");
		factory.newInstance(props);

		assertEquals("The instances tracking count should be increased by one",
				oldCount + 1, trackerNamedService.getTrackingCount());
		boolean serviceFound = false;
		// find the service, the reference must have the properties set
		// correctly
		ServiceReference refs[] = trackerNamedService.getServiceReferences();
		for (int i = 0; refs != null && i < refs.length; i++) {
			ServiceReference current = refs[i];
			String name = (String) current.getProperty("name");
			if ("hello".equals(name)) {
				serviceFound = true;
				Object service = trackerNamedService.getService(current);
				assertEquals("The service must have the same name", "hello",
						service.toString());
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
		assertEquals("The instances tracking count should be increased by two",
				oldCount + 2, trackerNamedService.getTrackingCount());

		worldInstance.dispose();
		assertEquals(
				"The service should be unregistered if the instance is disposed",
				oldCount + 1, trackerNamedService.getServices().length);

		// stop the bundle in order to check if some services remained
		// registered
		bundle.uninstall();
		Thread.yield();

		assertNull("The component factory must be unregistered",
				trackerNamedServiceFactory.getService());
		assertNull(
				"All component instances, created by the factory should be unregistered",
				trackerNamedService.getService());
	}

	Bundle	errorBundle;

	/**
	 * This method tests the behaviour of the service component runtime against
	 * bad component definitions.
	 * 
	 * It will perform the following checks:
	 * 
	 * <code>
	 * + install bundle with illegal component definition
	 *   verify the following illegal conditions:
	 *  - more that one implementation definition
	 *  - both factory &amp; serviceFactory are specified
	 *  - reference has a bind method but doesn't have unbind and reverse
	 * + check if framework error is posted
	 * </code>
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
		assertSame(
				"The Service Component Runtime should post a framework error for tb1",
				tb1, errorBundle);

		// make sure that the services are not registered
		ServiceReference ref;
		ref = bc
				.getServiceReference("org.osgi.test.cases.component.tb1.BadService1");
		assertNull(
				"The BadService1 shouldn't be registered because the XML contains two implementation classes",
				ref);

		ref = bc
				.getServiceReference("org.osgi.test.cases.component.tb1.BadService2");
		assertNull(
				"The BadService2 shouldn't be registered because component & service factories are incompatible",
				ref);

		ref = bc
				.getServiceReference("org.osgi.test.cases.component.tb3.MissingBindMethods");
		assertNull(
				"The MissingBindMethods shouldn't be registered because doesn't have both bind & unbind methods",
				ref);
	}

	/**
	 * Clean up after each method. Notice that during debugging many times the
	 * unsetState is never reached.
	 */
	public void unsetState() {
	}

	/**
	 * This test check if the SCR will unregister all components when it is
	 * stopped!
	 * 
	 * @throws Exception
	 */
	public void testStartStopSCR() throws Exception {
		Bundle scr = getSCRBundle();
		if (scr != null) {
			ServiceReference refs[];
			BundleContext bc = getContext();
			String filter = "(" + ComponentConstants.COMPONENT_NAME + "=*)";

			// preserve the count of the registered components
			// after SRC is started again, the same number of components
			// must be registered
			refs = bc.getServiceReferences(null, filter);
			int count = refs.length;

			scr.stop();
			Thread.yield();

			refs = bc.getServiceReferences(null, filter);
			assertNull(
					"The Service Component Runtime must stop all services if SCR is stopped",
					refs);

			// make sure that after SCR is being started it will activate
			// the components which bundles are active
			scr.start();
			Thread.yield();

			refs = bc.getServiceReferences(null, filter);
			assertEquals(
					"The Service Component Runtime must start all components that are installed prior it",
					count, refs.length);
		}
		else {
			log("testStartStopSCR test was ignored, please set the property named 'scr.bundle.name'");
		}
	}

	private Bundle getSCRBundle() {
		String bundleName = System.getProperty("scr.bundle.name");
		if (bundleName != null) {
			Bundle[] bundles = getContext().getBundles();
			for (int i = 0; i < bundles.length; i++) {
				Bundle current = bundles[i];
				Dictionary headers = current.getHeaders(null);
				String name = (String) headers.get(Constants.BUNDLE_NAME);
				if (bundleName.equals(name)) {
					return current;
				}
			}
		}
		return null;
	}

	public void frameworkEvent(FrameworkEvent e) {
		if (e.getType() == FrameworkEvent.ERROR) {
			errorBundle = e.getBundle();
		}
	}

	// helper for testDynamicBind
	private int getCount() {
		TBCService serviceConsumerEvent = (TBCService) trackerConsumerEvent
				.getService();
		Dictionary props = serviceConsumerEvent.getProperties();
		Integer x = (Integer) props.get("count");
		return x.intValue();
	}

	// tb3 service has dynamic bind
	public void testDynamicBind() throws Exception {
		assertEquals("The number of dynamically bound service should be zero",
				0, getCount());

		Bundle bundle = installBundle("tb4.jar");
		bundle.start();
		Thread.yield();

		assertEquals("No instance created yet, so the bind count must be zero",
				0, getCount());

		ComponentFactory factory = (ComponentFactory) trackerNamedServiceFactory
				.getService();

		// create the first service!
		Hashtable props = new Hashtable();
		props.put("name", "hello");
		ComponentInstance instance1 = factory.newInstance(props);

		assertEquals("The should be bound to the first instance", 1, getCount());

		props = new Hashtable();
		props.put("name", "world");
		// ComponentInstance instance2 =
		factory.newInstance(props);
		assertEquals("The should be bound to the second instance too", 2,
				getCount());

		instance1.dispose();
		assertEquals("The should be unbound from the first instance", 1,
				getCount());

		bundle.uninstall(); // must also dispose the second instance!
		assertEquals("The should be unbound from the all component instances",
				0, getCount());
	}

	// TODO:
	// 1. verify component enable/disable
	// 2. verify CM properties - instances should be different after change
	// 3. test that component factory will register the service (if is provider)
	// and will not register it if is not provider

}

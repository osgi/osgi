/*
 * Copyright (c) OSGi Alliance (2004, 2012). All Rights Reserved.
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
package org.osgi.test.cases.component.junit;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;
import org.osgi.test.cases.component.service.BaseService;
import org.osgi.test.cases.component.service.ComponentContextExposer;
import org.osgi.test.cases.component.service.ComponentEnabler;
import org.osgi.test.cases.component.service.TBCService;
import org.osgi.test.cases.component.service.TestObject;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.sleep.Sleep;
import org.osgi.util.tracker.ServiceTracker;

/**
 * This is the bundle initially installed and started by the TestCase. It
 * performs the test methods of the declarative services test case.
 *
 * @version $Id$
 */
public class DeclarativeServicesControl extends DefaultTestBundleControl
		implements LogListener {

	private static final String	TEST_CASE_ROOT		= "org.osgi.test.cases.component";
	private static final String	PROVIDER_CLASS		= TEST_CASE_ROOT
															+ ".service.ServiceProvider";
	private static final String	LOOKUP_CLASS		= TEST_CASE_ROOT
															+ ".tb2.ServiceConsumerLookup";
	private static final String	DYN_CLASS			= TEST_CASE_ROOT
															+ ".tb2.DynService";
	private static final String	EVENT_CLASS			= TEST_CASE_ROOT
															+ ".tb3.ServiceConsumerEvent";
	private static final String	NAMED_CLASS			= TEST_CASE_ROOT
															+ ".tb4.NamedService";
	private static final String	COMP_OPTIONAL_100	= TEST_CASE_ROOT
															+ ".tb5.optionalNS100";
	private static final String	COMP_OPTIONAL_110	= TEST_CASE_ROOT
															+ ".tb5.optionalNS110";
	private static final String	COMP_REQUIRE_100	= TEST_CASE_ROOT
															+ ".tb5.requireNS100";
	private static final String	COMP_REQUIRE_110	= TEST_CASE_ROOT
															+ ".tb5.requireNS110";
	private static final String	COMP_IGNORE_100		= TEST_CASE_ROOT
															+ ".tb5.ignoreNS100";
	private static final String	COMP_IGNORE_110		= TEST_CASE_ROOT
															+ ".tb5.ignoreNS110";
	private static final String	COMP_NOTSET_100		= TEST_CASE_ROOT
															+ ".tb5.notsetNS100";
	private static final String	COMP_NOTSET_110		= TEST_CASE_ROOT
															+ ".tb5.notsetNS110";
	private static final String	MOD_NOTSET_NS100	= TEST_CASE_ROOT
															+ ".tb13.notsetNS100";
	private static final String	MOD_NOTSET_NS110	= TEST_CASE_ROOT
															+ ".tb13.notsetNS110";
	private static final String	MOD_NOARGS_NS100	= TEST_CASE_ROOT
															+ ".tb13.NoArgs100";
	private static final String	MOD_NOARGS_NS110	= TEST_CASE_ROOT
															+ ".tb13.NoArgs110";
	private static final String	MOD_CC_NS100		= TEST_CASE_ROOT
															+ ".tb13.CcNS100";
	private static final String	MOD_CC_NS110		= TEST_CASE_ROOT
															+ ".tb13.CcNS110";
	private static final String	MOD_BC_NS100		= TEST_CASE_ROOT
															+ ".tb13.BcNS100";
	private static final String	MOD_BC_NS110		= TEST_CASE_ROOT
															+ ".tb13.BcNS110";
	private static final String	MOD_MAP_NS100		= TEST_CASE_ROOT
															+ ".tb13.MapNS100";
	private static final String	MOD_MAP_NS110		= TEST_CASE_ROOT
															+ ".tb13.MapNS110";
	private static final String	MOD_CC_BC_MAP_NS100	= TEST_CASE_ROOT
															+ ".tb13.CcBcMapNS100";
	private static final String	MOD_CC_BC_MAP_NS110	= TEST_CASE_ROOT
															+ ".tb13.CcBcMapNS110";
	private static final String	MOD_NOT_EXIST_NS110	= TEST_CASE_ROOT
															+ ".tb13.NotExistNS110";
	private static final String	MOD_THROW_EX_NS110	= TEST_CASE_ROOT
															+ ".tb13.ThrowExNS110";

	private static int			SLEEP			= 1000;
	private boolean synchronousBuild = false;

	private Bundle				tb1, tb2, tb3;

	private ServiceTracker		trackerProvider;
	private ServiceTracker		trackerConsumerLookup;
	private ServiceTracker		trackerDyn;
	private ServiceTracker		trackerConsumerEvent;
	private ServiceTracker		trackerNamedService;
	private ServiceTracker		trackerNamedServiceFactory;
	private ServiceTracker		trackerCM;
	private ServiceTracker    	trackerBaseService;

	protected void setUp() throws Exception {
		String sleepTimeString = System
				.getProperty("osgi.tc.component.sleeptime");
		int sleepTime = SLEEP;
		if (sleepTimeString != null) {
			try {
				sleepTime = Integer.parseInt(sleepTimeString);
			}
			catch (Exception e) {
				e.printStackTrace();
				System.out
						.println("Error while parsing sleep value! The default one will be used : "
								+ SLEEP);
			}
			if (sleepTime < 100) {
				System.out.println("The sleep value is too low : " + sleepTime
						+ " ! The default one will be used : " + SLEEP);
			}
			else {
				SLEEP = sleepTime;
			}
		}

		BundleContext bc = getContext();
		trackerCM = new ServiceTracker(bc, ConfigurationAdmin.class.getName(),
				null);
		trackerCM.open();
	    // clear component configurations
 	    clearConfigurations();


		// install test cases
		tb1 = installBundle("tb1.jar", false);
		tb2 = installBundle("tb2.jar", false);
		tb3 = installBundle("tb3.jar", false);

		// start them
		tb1.start();
		tb2.start();
		tb3.start();

		// init trackers
		trackerProvider = new ServiceTracker(bc, PROVIDER_CLASS, null);
		trackerConsumerLookup = new ServiceTracker(bc, LOOKUP_CLASS, null);
		trackerDyn = new ServiceTracker(bc, DYN_CLASS, null);
		trackerConsumerEvent = new ServiceTracker(bc, EVENT_CLASS, null);
		trackerNamedService = new ServiceTracker(bc, NAMED_CLASS, null);
		Filter filter = bc.createFilter("(&("
				+ ComponentConstants.COMPONENT_FACTORY + '=' + NAMED_CLASS
				+ ")(" + Constants.OBJECTCLASS + '='
				+ ComponentFactory.class.getName() + "))");
		trackerNamedServiceFactory = new ServiceTracker(bc, filter, null);
		trackerBaseService = new ServiceTracker(bc, BaseService.class.getName(), null);

		// start listening
		trackerProvider.open(true);
		trackerConsumerLookup.open();
		trackerDyn.open();
		trackerConsumerEvent.open();
		trackerNamedService.open();
		trackerNamedServiceFactory.open();
		trackerBaseService.open();

		Sleep.sleep(SLEEP);
	}

	/**
	 * Clean up after a run. Notice that during debugging many times the
	 * unprepare is never reached.
	 *
	 * @throws Exception is the bundles are not installed
	 * @see org.osgi.test.cases.util.DefaultTestBundleControl#unprepare()
	 */
	protected void tearDown() throws Exception {
		uninstallBundle(tb1);
		uninstallBundle(tb2);
		uninstallBundle(tb3);

		trackerProvider.close();
		trackerConsumerLookup.close();
		trackerDyn.close();
		trackerConsumerEvent.close();
		trackerNamedService.close();
		trackerNamedServiceFactory.close();
		trackerBaseService.close();

		clearConfigurations();
		trackerCM.close();
	}

	/**
	 * This methods takes care of the configurations related to this test
	 *
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 */
	private void clearConfigurations() throws Exception {
		ConfigurationAdmin cm = (ConfigurationAdmin) trackerCM.getService();
		assertNotNull("The ConfigurationAdmin should be available", cm);
		// clean configurations from previous tests
		// clean factory configs for named service
		clearConfiguration(cm, "(service.factoryPid=" + TEST_CASE_ROOT + "*)");
		// clean configs for named service
		clearConfiguration(cm, "(service.pid=" + TEST_CASE_ROOT + "*)");

		Sleep.sleep(SLEEP * 2);
	}

	private void clearConfiguration(ConfigurationAdmin cm, String filter)
			throws Exception {
		Configuration[] configs = cm.listConfigurations(filter);
		for (int i = 0; configs != null && i < configs.length; i++) {
			Configuration configuration = configs[i];
			configuration.delete();
		}
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
		Sleep.sleep(SLEEP);
		serviceProvider = (TBCService) trackerProvider.getService();
		assertNull("ServiceProvider service should not be registered",
				serviceProvider);

		tb1 = installBundle("tb1.jar", false);
		Sleep.sleep(SLEEP);
		serviceProvider = (TBCService) trackerProvider.getService();
		assertTrue("ServiceProvider bundle should be in resolved state", (tb1
				.getState() & (Bundle.RESOLVED | Bundle.INSTALLED)) != 0);
		assertNull("ServiceProvider service should not be registered",
				serviceProvider);

		tb1.start();
		Sleep.sleep(SLEEP);
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
		assertTrue("test.property.string array should be", Arrays.equals(
				new String[] {"Value 1", "Value 2", "Value 3"}, array));
	}

	/**
	 * This test check if the SCR will unregister all components when it is
	 * stopped!
	 *
	 * @throws Exception
	 */
	public void testStartStopSCR() throws Exception {
		Bundle scr = getSCRBundle();
		if (scr == null) {
			fail("testStartStopSCR test cannot execute: Please set the system property 'scr.bundle.name' to the Bundle-SymbolicName of the SCR implementation bundle being tested.");
		}

		ServiceReference refs[];
		BundleContext bc = getContext();
		String filter = "(" + ComponentConstants.COMPONENT_NAME + "=*)";

		// preserve the count of the registered components
		// after SRC is started again, the same number of components
		// must be registered
		refs = bc.getServiceReferences(null, filter);
		int count = (refs == null) ? 0 : refs.length;

		scr.stop();
		Sleep.sleep(SLEEP * 2);

		try {
			refs = bc.getServiceReferences(null, filter);
			assertNull(
					"The Service Component Runtime must stop all services if SCR is stopped",
					refs);
		}
		finally {
			// make sure that after SCR is being started it will activate
			// the components which bundles are active
			scr.start();
			Sleep.sleep(SLEEP * 2);
		}

		refs = bc.getServiceReferences(null, filter);
		assertEquals(
				"The Service Component Runtime must start all components that are installed prior it",
				count, (refs == null) ? 0 : refs.length);
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
		Bundle bundle = installBundle("tb4.jar", false);
		bundle.start();
		Sleep.sleep(SLEEP);

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
		Sleep.sleep(SLEEP);

		// make sure that the new service is registered
		assertEquals("The instances tracking count should be increased by two",
				oldCount + 2, trackerNamedService.getTrackingCount());

		worldInstance.dispose();
		Sleep.sleep(SLEEP);
		assertEquals(
				"The service should be unregistered if the instance is disposed",
				oldCount + 1, trackerNamedService.getServices().length);

		// stop the bundle in order to check if some services remained
		// registered
		bundle.uninstall();
		Sleep.sleep(SLEEP);

		assertNull("The component factory must be unregistered",
				trackerNamedServiceFactory.getService());
		assertNull(
				"All component instances, created by the factory should be unregistered",
				trackerNamedService.getService());
	}

	boolean	errorLog	= false;

	public void logged(LogEntry e) {
		if (e.getLevel() == LogService.LOG_ERROR) {
			errorLog = true;
		}
	}

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
		errorLog = false;
		LogReaderService logService = (LogReaderService) getService(LogReaderService.class);
		logService.addLogListener(this);

		// the bundle contains some illegal definitions
		tb1 = installBundle("tb1.jar", false);
		tb1.start();
		Sleep.sleep(SLEEP * 2); // log and scr have asynchronous processing

		logService.removeLogListener(this);

		// make sure that SCR reports some errors for tb1
		assertTrue(
				"The Service Component Runtime should post a log error for tb1",
				errorLog);

		// make sure that the services are not registered
		ServiceReference ref;
		ref = bc.getServiceReference(TEST_CASE_ROOT + ".tb1.BadService1");
		assertNull(
				"The BadService1 shouldn't be registered because the XML contains two implementation classes",
				ref);

		ref = bc.getServiceReference(TEST_CASE_ROOT + ".tb1.BadService2");
		assertNull(
				"The BadService2 shouldn't be registered because component & service factories are incompatible",
				ref);
	}

	private Bundle getSCRBundle() {
		String bundleName = System.getProperty("scr.bundle.name");
		if (bundleName != null) {
			Bundle[] bundles = getContext().getBundles();
			for (int i = 0; i < bundles.length; i++) {
				Bundle current = bundles[i];
				String name = current.getSymbolicName();
				if (bundleName.equals(name)) {
					return current;
				}
			}
		}
		return null;
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

		Bundle bundle = installBundle("tb4.jar", false);
		bundle.start();
		Sleep.sleep(SLEEP);

		assertEquals("No instance created yet, so the bind count must be zero",
				0, getCount());

		ComponentFactory factory = (ComponentFactory) trackerNamedServiceFactory
				.getService();

		// create the first service!
		Hashtable props = new Hashtable();
		props.put("name", "hello");
		ComponentInstance instance1 = factory.newInstance(props);
		Sleep.sleep(SLEEP);

		assertEquals("The component should be bound to the first instance", 1,
				getCount());

		props = new Hashtable();
		props.put("name", "world");
		// ComponentInstance instance2 =
		factory.newInstance(props);
		Sleep.sleep(SLEEP);
		assertEquals(
				"The component should be bound to the second instance too", 2,
				getCount());

		instance1.dispose();
		Sleep.sleep(SLEEP);
		assertEquals("The component should be unbound from the first instance",
				1, getCount());

		bundle.uninstall(); // must also dispose the second instance!
		Sleep.sleep(SLEEP);
		assertEquals(
				"The component should be unbound from the all component instances",
				0, getCount());
	}

	public void testCMUpdate() throws Exception {
		ConfigurationAdmin cm = (ConfigurationAdmin) trackerCM.getService();
		assertNotNull("The ConfigurationAdmin should be available", cm);
		ServiceReference ref;
		Object service;
		String cmprop;

		// verify that the current service is loaded with default values from
		// the XML file
		ref = trackerConsumerLookup.getServiceReference();
		cmprop = (String) ref.getProperty("cmprop");
		service = trackerConsumerLookup.getService();
		assertEquals(
				"The service should be registered from the property defined in the XML file",
				"setFromXML", cmprop);

		// update the properties
		Hashtable props = new Hashtable(10);
		props.put("cmprop", "setFromCM");
		// the line below will create the configuration if it doesn't exists!
		// see CM api for details
		Configuration config = cm.getConfiguration(LOOKUP_CLASS, null);
		config.update(props);

		// let SCR & CM to complete it's job
		Sleep.sleep(SLEEP * 2);

		// verify that the service is updated
		assertNotSame("The service shoud be restarted with a new instance",
				service, trackerConsumerLookup.getService());
		service = trackerConsumerLookup.getService();
		// verify that the property is overriden with the CM value
		ref = trackerConsumerLookup.getServiceReference();
		cmprop = (String) ref.getProperty("cmprop");
		assertEquals(
				"The property 'cmprop' should be overriden with the value from CM",
				"setFromCM", cmprop);
		Object otherProp = ref.getProperty("test.property.string");
		assertNotNull(
				"The CM update shouldn't delete the other properties defined in XML file",
				otherProp);

		// make sure that the SCR handles delete events!
		config.delete();
		// wait to complete - slow becase CM may use threads for notify
		Sleep.sleep(SLEEP * 2);

		assertNotSame(
				"After delete the service shoud be restarted with a new instance",
				service, trackerConsumerLookup.getService());
		ref = trackerConsumerLookup.getServiceReference();
		cmprop = (String) ref.getProperty("cmprop");
		assertEquals(
				"After delete the service should be registered from the property defined in the XML file",
				"setFromXML", cmprop);
	}

	// Notice: "serviceFactory" attribute means "an OSGi Service Factory"
	// which is quite different from the "Managed Service Factory" which is
	// CM related.
	public void testManagedServiceFactory() throws Exception {
		// check the service is instantiated without properties!
		int length = trackerConsumerEvent.getServices().length;
		Object service = trackerConsumerEvent.getService();
		assertEquals(
				"The event service should be registered even if no properties are set!",
				1, length);

		ConfigurationAdmin cm = (ConfigurationAdmin) trackerCM.getService();
		assertNotNull("The ConfigurationAdmin should be available", cm);
		Configuration config1;
		Configuration config2;
		Hashtable props;

		// create a new configuration
		config1 = cm.createFactoryConfiguration(EVENT_CLASS, null);
		props = new Hashtable(2);
		props.put("instance", "1");
		config1.update(props);
		Sleep.sleep(SLEEP * 2); // let it finish update, CM + SCR

		// verify the result
		length = trackerConsumerEvent.getServices().length;
		assertEquals("The service count shouldn't be changed!", 1, length);
		assertNotSame("The service should be updated with new instance",
				service, trackerConsumerEvent.getService());

		// create second configuration
		config2 = cm.createFactoryConfiguration(EVENT_CLASS, null);
		props = new Hashtable(2);
		props.put("instance", "2");
		config2.update(props);
		Sleep.sleep(SLEEP * 2); // let it finish update, CM + SCR

		// verify the result
		length = trackerConsumerEvent.getServices().length;
		assertEquals("For each configuration should be a service instance", 2,
				length);

		config1.delete();
		config2.delete();
	}

	public void testComponentEnableAndDisable() throws Exception {
		int count;

		// initially the DynService shouldn't be register because it's disabled
		count = trackerDyn.getServices() == null ? 0
				: trackerDyn.getServices().length;
		assertEquals(
				"The DynService shouldn't be instantiated because it is disabled",
				0, count);

		// enable the all services (actually only dyn service)
		ComponentEnabler lookupService = (ComponentEnabler) trackerConsumerLookup
				.getService();
		lookupService.enableComponent(null, true);
		Sleep.sleep(SLEEP); // let SCR complete

		// verify that it is instantiate
		count = trackerDyn.getServices() == null ? 0
				: trackerDyn.getServices().length;
		assertEquals("The DynService must be enabled and instantiated", 1,
				count);

		// disable dyn service
		lookupService.enableComponent(DYN_CLASS, false);
		Sleep.sleep(SLEEP); // let SCR complete

		// verify that DYN services is not available
		count = trackerDyn.getServices() == null ? 0
				: trackerDyn.getServices().length;
		assertEquals("The DynService must be disabled & removed again", 0,
				count);
	}

	public void testConfigurationPolicy() throws Exception {
		ConfigurationAdmin cm = (ConfigurationAdmin) trackerCM.getService();
		assertNotNull("The ConfigurationAdmin should be available", cm);

		Bundle tb5 = installBundle("tb5.jar");
		tb5.start();
		waitBundleStart();

		Hashtable props = new Hashtable(10);
		props.put("config.base.data", new Integer(1));

		// component notsetNS100 - property not set by Configuration Admin; XML
		// NS
		// 1.0.0
		assertEquals(
				"Configuration data should not be available for notsetNS100",
				0, getBaseConfigData(COMP_NOTSET_100));
		// component notsetNS100 - property set by Configuration Admin; XML NS
		// 1.0.0
		Configuration config = cm.getConfiguration(COMP_NOTSET_100, null);
		config.update(props);
		Sleep.sleep(SLEEP * 3);
		assertEquals(
				"Configuration data should be available for notsetNS100 and equal to 1",
				1, getBaseConfigData(COMP_NOTSET_100));

		// component notsetNS110 - property not set by Configuration Admin; XML
		// NS
		// 1.1.0
		assertEquals(
				"Configuration data should not be available for notsetNS110",
				0, getBaseConfigData(COMP_NOTSET_110));
		// component notsetNS110 - property set by Configuration Admin; XML NS
		// 1.1.0
		config = cm.getConfiguration(COMP_NOTSET_110, null);
		config.update(props);
		Sleep.sleep(SLEEP * 3);
		assertEquals(
				"Configuration data should be available for notsetNS110 and equal to 1",
				1, getBaseConfigData(COMP_NOTSET_110));

		// component optionalNS100 - property not set by Configuration Admin;
		// XML NS
		// 1.0.0 - INVALID COMPONENT
		assertEquals("Component optionalNS100 should not be activated", -1,
				getBaseConfigData(COMP_OPTIONAL_100));
		// component optionalNS100 - property set by Configuration Admin; XML NS
		// 1.0.0 - INVALID COMPONENT
		config = cm.getConfiguration(COMP_OPTIONAL_100, null);
		config.update(props);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Component optionalNS100 should not be activated", -1,
				getBaseConfigData(COMP_OPTIONAL_100));

		// component optionalNS110 - property not set by Configuration Admin;
		// XML NS
		// 1.1.0
		assertEquals(
				"Configuration data should not be available for optionalNS110",
				0, getBaseConfigData(COMP_OPTIONAL_110));
		// component optionalNS110 - property set by Configuration Admin; XML NS
		// 1.1.0
		config = cm.getConfiguration(COMP_OPTIONAL_110, null);
		config.update(props);
		Sleep.sleep(SLEEP * 3);
		assertEquals(
				"Configuration data should be available for optionalNS110 and equal to 1",
				1, getBaseConfigData(COMP_OPTIONAL_110));

		// component requireNS100 - property not set by Configuration Admin; XML
		// NS
		// 1.0.0 - INVALID COMPONENT
		assertEquals("Component requireNS100 should not be activated", -1,
				getBaseConfigData(COMP_REQUIRE_100));
		// component requireNS100 - property set by Configuration Admin; XML NS
		// 1.0.0 - INVALID COMPONENT
		config = cm.getConfiguration(COMP_REQUIRE_100, null);
		config.update(props);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Component requireNS100 should not be activated", -1,
				getBaseConfigData(COMP_REQUIRE_100));

		// component requireNS110 - property not set by Configuration Admin; XML
		// NS
		// 1.1.0
		assertEquals(
				"Configuration data should not be available for requireNS110, and it should not be satisfied",
				-1, getBaseConfigData(COMP_REQUIRE_110));
		// component requireNS110 - property set by Configuration Admin; XML NS
		// 1.1.0
		config = cm.getConfiguration(COMP_REQUIRE_110, null);
		config.update(props);
		Sleep.sleep(SLEEP * 3);
		assertEquals(
				"Configuration data should be available for requireNS110 and equal to 1",
				1, getBaseConfigData(COMP_REQUIRE_110));

		// component ignoreNS100 - property not set by Configuration Admin; XML
		// NS
		// 1.0.0 - INVALID COMPONENT
		assertEquals("Component ignoreNS100 should not be activated", -1,
				getBaseConfigData(COMP_IGNORE_100));
		// component ignoreNS100 - property set by Configuration Admin; XML NS
		// 1.0.0
		// - INVALID COMPONENT
		config = cm.getConfiguration(COMP_IGNORE_100, null);
		config.update(props);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Component ignoreNS100 should not be activated", -1,
				getBaseConfigData(COMP_IGNORE_100));

		// component ignoreNS110 - property not set by Configuration Admin; XML
		// NS
		// 1.1.0
		assertEquals(
				"Configuration data should not be available for ignoreNS110, but it should be satisfied",
				0, getBaseConfigData(COMP_IGNORE_110));
		// component ignoreNS110 - property set by Configuration Admin; XML NS
		// 1.1.0
		config = cm.getConfiguration(COMP_IGNORE_110, null);
		config.update(props);
		Sleep.sleep(SLEEP * 3);
		assertEquals(
				"Configuration data should not be available for ignoreNS110, but it should be satisfied",
				0, getBaseConfigData(COMP_IGNORE_110));

		uninstallBundle(tb5);
	}

	// tests configuration-policy for factory configuration objects
	public void testConfigurationPolicyFactoryConf() throws Exception {
		ConfigurationAdmin cm = (ConfigurationAdmin) trackerCM.getService();
		assertNotNull("The ConfigurationAdmin should be available", cm);

		Bundle tb5 = installBundle("tb5.jar");
		tb5.start();
		waitBundleStart();

		Hashtable props = new Hashtable(10);
		props.put("config.base.data", new Integer(1));

		// component notsetNS100 - property not set by Configuration Admin; XML
		// NS
		// 1.0.0
		assertEquals(
				"Configuration data should not be available for notsetNS100",
				0, getBaseConfigData(COMP_NOTSET_100));
		// component notsetNS100 - property set by Configuration Admin; XML NS
		// 1.0.0
		Configuration config = cm.createFactoryConfiguration(COMP_NOTSET_100, null);
		config.update(props);
		Sleep.sleep(SLEEP * 3);
		assertEquals(
				"Configuration data should be available for notsetNS100 and equal to 1",
				1, getBaseConfigData(COMP_NOTSET_100));

		// component notsetNS110 - property not set by Configuration Admin; XML
		// NS
		// 1.1.0
		assertEquals(
				"Configuration data should not be available for notsetNS110",
				0, getBaseConfigData(COMP_NOTSET_110));
		// component notsetNS110 - property set by Configuration Admin; XML NS
		// 1.1.0
		config = cm.createFactoryConfiguration(COMP_NOTSET_110, null);
		config.update(props);
		Sleep.sleep(SLEEP * 3);
		assertEquals(
				"Configuration data should be available for notsetNS110 and equal to 1",
				1, getBaseConfigData(COMP_NOTSET_110));

		// component optionalNS100 - property not set by Configuration Admin;
		// XML NS
		// 1.0.0 - INVALID COMPONENT
		assertEquals("Component optionalNS100 should not be activated", -1,
				getBaseConfigData(COMP_OPTIONAL_100));
		// component optionalNS100 - property set by Configuration Admin; XML NS
		// 1.0.0 - INVALID COMPONENT
		config = cm.createFactoryConfiguration(COMP_OPTIONAL_100, null);
		config.update(props);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Component optionalNS100 should not be activated", -1,
				getBaseConfigData(COMP_OPTIONAL_100));

		// component optionalNS110 - property not set by Configuration Admin;
		// XML NS
		// 1.1.0
		assertEquals(
				"Configuration data should not be available for optionalNS110",
				0, getBaseConfigData(COMP_OPTIONAL_110));
		// component optionalNS110 - property set by Configuration Admin; XML NS
		// 1.1.0
		config = cm.createFactoryConfiguration(COMP_OPTIONAL_110, null);
		config.update(props);
		Sleep.sleep(SLEEP * 3);
		assertEquals(
				"Configuration data should be available for optionalNS110 and equal to 1",
				1, getBaseConfigData(COMP_OPTIONAL_110));

		// component requireNS100 - property not set by Configuration Admin; XML
		// NS
		// 1.0.0 - INVALID COMPONENT
		assertEquals("Component requireNS100 should not be activated", -1,
				getBaseConfigData(COMP_REQUIRE_100));
		// component requireNS100 - property set by Configuration Admin; XML NS
		// 1.0.0 - INVALID COMPONENT
		config = cm.createFactoryConfiguration(COMP_REQUIRE_100, null);
		config.update(props);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Component requireNS100 should not be activated", -1,
				getBaseConfigData(COMP_REQUIRE_100));

		// component requireNS110 - property not set by Configuration Admin; XML
		// NS
		// 1.1.0
		assertEquals(
				"Configuration data should not be available for requireNS110, and it should not be satisfied",
				-1, getBaseConfigData(COMP_REQUIRE_110));
		// component requireNS110 - property set by Configuration Admin; XML NS
		// 1.1.0
		config = cm.createFactoryConfiguration(COMP_REQUIRE_110, null);
		config.update(props);
		Sleep.sleep(SLEEP * 3);
		assertEquals(
				"Configuration data should be available for requireNS110 and equal to 1",
				1, getBaseConfigData(COMP_REQUIRE_110));

		// component ignoreNS100 - property not set by Configuration Admin; XML
		// NS
		// 1.0.0 - INVALID COMPONENT
		assertEquals("Component ignoreNS100 should not be activated", -1,
				getBaseConfigData(COMP_IGNORE_100));
		// component ignoreNS100 - property set by Configuration Admin; XML NS
		// 1.0.0
		// - INVALID COMPONENT
		config = cm.createFactoryConfiguration(COMP_IGNORE_100, null);
		config.update(props);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Component ignoreNS100 should not be activated", -1,
				getBaseConfigData(COMP_IGNORE_100));

		// component ignoreNS110 - property not set by Configuration Admin; XML
		// NS
		// 1.1.0
		assertEquals(
				"Configuration data should not be available for ignoreNS110, but it should be satisfied",
				0, getBaseConfigData(COMP_IGNORE_110));
		// component ignoreNS110 - property set by Configuration Admin; XML NS
		// 1.1.0
		config = cm.createFactoryConfiguration(COMP_IGNORE_110, null);
		config.update(props);
		Sleep.sleep(SLEEP * 3);
		assertEquals(
				"Configuration data should not be available for ignoreNS110, but it should be satisfied",
				0, getBaseConfigData(COMP_IGNORE_110));

		uninstallBundle(tb5);
	}

	public void testActivateDeactivate() throws Exception {
		Bundle tb6 = installBundle("tb6.jar");
		tb6.start();
		Sleep.sleep(SLEEP * 3);

		final String NOTSET_NS100 = TEST_CASE_ROOT + ".tb6.notsetNS100";
		final String NOTSET_NS110 = TEST_CASE_ROOT + ".tb6.notsetNS110";
		final String NOARGS_NS100 = TEST_CASE_ROOT + ".tb6.NoArgsNS100";
		final String NOARGS_NS110 = TEST_CASE_ROOT + ".tb6.NoArgsNS110";
		final String CC_NS100 = TEST_CASE_ROOT + ".tb6.CcNS100";
		final String CC_NS110 = TEST_CASE_ROOT + ".tb6.CcNS110";
		final String BC_NS100 = TEST_CASE_ROOT + ".tb6.BcNS100";
		final String BC_NS110 = TEST_CASE_ROOT + ".tb6.BcNS110";
		final String MAP_NS100 = TEST_CASE_ROOT + ".tb6.MapNS100";
		final String MAP_NS110 = TEST_CASE_ROOT + ".tb6.MapNS110";
		final String CC_BC_MAP_NS100 = TEST_CASE_ROOT + ".tb6.CcBcMapNS100";
		final String CC_BC_MAP_NS110 = TEST_CASE_ROOT + ".tb6.CcBcMapNS110";
		final String INT_NS110 = TEST_CASE_ROOT + ".tb6.IntNS110";
		final String CC_BC_MAP_INT_NS110 = TEST_CASE_ROOT
				+ ".tb6.CcBcMapIntNS110";

		BaseService bs = getBaseService(NOTSET_NS100);
		ComponentContext cc = (bs instanceof ComponentContextExposer) ? ((ComponentContextExposer) bs)
				.getComponentContext()
				: null;
		assertNotNull("Component context should be available", cc);

		assertEquals(
				"Activate method of " + NOTSET_NS100 + " should be called",
				1 << 0, (1 << 0) & getBaseConfigData(bs));
		cc.disableComponent(NOTSET_NS100);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Deactivate method of " + NOTSET_NS100
				+ " should be called", 1 << 1, (1 << 1) & getBaseConfigData(bs));

		bs = getBaseService(NOTSET_NS110);
		assertNotNull("bs should not be null", bs);
		assertEquals(
				"Activate method of " + NOTSET_NS110 + " should be called",
				1 << 0, (1 << 0) & getBaseConfigData(bs));
		cc.disableComponent(NOTSET_NS110);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Deactivate method of " + NOTSET_NS110
				+ " should be called", 1 << 1, (1 << 1) & getBaseConfigData(bs));

		bs = getBaseService(NOARGS_NS100); // INVALID COMPONENT FOR XML NS 1.0.0
		assertEquals("Component " + NOARGS_NS100 + " should not be activated",
				-1, getBaseConfigData(bs));

		bs = getBaseService(NOARGS_NS110);
		assertNotNull("bs should not be null", bs);
		assertEquals(
				"Activate method of " + NOARGS_NS110 + " should be called",
				1 << 2, (1 << 2) & getBaseConfigData(bs));
		cc.disableComponent(NOARGS_NS110);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Deactivate method of " + NOARGS_NS110
				+ " should be called", 1 << 3, (1 << 3) & getBaseConfigData(bs));

		bs = getBaseService(CC_NS100); // INVALID COMPONENT FOR XML NS 1.0.0
		assertEquals("Component " + CC_NS100 + " should not be activated", -1,
				getBaseConfigData(bs));

		bs = getBaseService(CC_NS110);
		assertNotNull("bs should not be null", bs);
		assertEquals("Activate method of " + CC_NS110 + " should be called",
				1 << 4, (1 << 4) & getBaseConfigData(bs));
		cc.disableComponent(CC_NS110);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Deactivate method of " + CC_NS110 + " should be called",
				1 << 5, (1 << 5) & getBaseConfigData(bs));

		bs = getBaseService(BC_NS100); // INVALID COMPONENT FOR XML NS 1.0.0
		assertEquals("Component " + BC_NS100 + " should not be activated", -1,
				getBaseConfigData(bs));

		bs = getBaseService(BC_NS110);
		assertNotNull("bs should not be null", bs);
		assertEquals("Activate method of " + BC_NS110 + " should be called",
				1 << 6, (1 << 6) & getBaseConfigData(bs));
		cc.disableComponent(BC_NS110);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Deactivate method of " + BC_NS110 + " should be called",
				1 << 7, (1 << 7) & getBaseConfigData(bs));

		bs = getBaseService(MAP_NS100); // INVALID COMPONENT FOR XML NS 1.0.0
		assertEquals("Component " + MAP_NS100 + " should not be activated", -1,
				getBaseConfigData(bs));

		bs = getBaseService(MAP_NS110);
		assertNotNull("bs should not be null", bs);
		assertEquals("Activate method of " + MAP_NS110 + " should be called",
				1 << 8, (1 << 8) & getBaseConfigData(bs));
		cc.disableComponent(MAP_NS110);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Deactivate method of " + MAP_NS110 + " should be called",
				1 << 9, (1 << 9) & getBaseConfigData(bs));

		bs = getBaseService(CC_BC_MAP_NS100); // INVALID COMPONENT FOR XML NS
												// 1.0.0
		assertEquals("Component " + CC_BC_MAP_NS100
				+ " should not be activated", -1, getBaseConfigData(bs));

		bs = getBaseService(CC_BC_MAP_NS110);
		assertNotNull("bs should not be null", bs);
		assertEquals("Activate method of " + CC_BC_MAP_NS110
				+ " should be called", 1 << 10, (1 << 10)
				& getBaseConfigData(bs));
		cc.disableComponent(CC_BC_MAP_NS110);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Deactivate method of " + CC_BC_MAP_NS110
				+ " should be called", 1 << 11, (1 << 11)
				& getBaseConfigData(bs));

		bs = getBaseService(INT_NS110);
		assertNotNull("bs should not be null", bs);
		cc.disableComponent(INT_NS110);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Deactivate method of " + INT_NS110 + " should be called",
				1 << 12, (1 << 12) & getBaseConfigData(bs));

		bs = getBaseService(CC_BC_MAP_INT_NS110);
		assertNotNull("bs should not be null", bs);
		cc.disableComponent(CC_BC_MAP_INT_NS110);
		Sleep.sleep(SLEEP * 3);
		int data = getBaseConfigData(bs);
		assertEquals("Deactivate method of " + CC_BC_MAP_INT_NS110
				+ " should be called", 1 << 13, (1 << 13) & data);

		// // Testing Deactivation reasons ////
		assertEquals(
				"Deactivation reason shall be DEACTIVATION_REASON_DISABLED", 1,
				0xFF & (data >> 16));

		final String CONT_EXP = TEST_CASE_ROOT + ".tb6.ContExp";

		cc.enableComponent(CC_BC_MAP_INT_NS110);
		Sleep.sleep(SLEEP * 3);
		bs = getBaseService(CC_BC_MAP_INT_NS110);
		assertNotNull("bs should not be null", bs);
		cc.disableComponent(CONT_EXP);
		Sleep.sleep(SLEEP * 3);
		assertEquals(
				"Deactivation reason shall be DEACTIVATION_REASON_REFERENCE",
				2, 0xFF & (getBaseConfigData(bs) >> 16));

		cc.enableComponent(CONT_EXP);
		Sleep.sleep(SLEEP * 3);

		bs = getBaseService(CC_BC_MAP_INT_NS110);
		assertNotNull("bs should not be null", bs);
		ConfigurationAdmin cm = (ConfigurationAdmin) trackerCM.getService();
		assertNotNull("The ConfigurationAdmin should be available", cm);
		Configuration config = cm.getConfiguration(CC_BC_MAP_INT_NS110, null);
		Dictionary properties = config.getProperties();
		if (properties == null) {
			properties = new Hashtable();
		}
		properties.put("configuration.dummy", "dummy");
		config.update(properties);
		Sleep.sleep(SLEEP * 3);
		assertEquals(
				"Deactivation reason shall be DEACTIVATION_REASON_CONFIGURATION_MODIFIED",
				3, 0xFF & (getBaseConfigData(bs) >> 16));

		cc.enableComponent(CC_BC_MAP_INT_NS110);
		Sleep.sleep(SLEEP * 3);

		bs = getBaseService(CC_BC_MAP_INT_NS110);
		assertNotNull("bs should not be null", bs);
		config = cm.getConfiguration(CC_BC_MAP_INT_NS110, null);
		config.delete();
		Sleep.sleep(SLEEP * 3);
		assertEquals(
				"Deactivation reason shall be DEACTIVATION_REASON_CONFIGURATION_DELETED",
				4, 0xFF & (getBaseConfigData(bs) >> 16));

		cc.enableComponent(CC_BC_MAP_INT_NS110);
		Sleep.sleep(SLEEP * 3);

		cc.enableComponent(INT_NS110);
		Sleep.sleep(SLEEP * 3);

		bs = getBaseService(INT_NS110);
		assertNotNull("bs should not be null", bs);
		ComponentContext ccIntNS110 = (bs instanceof ComponentContextExposer) ? ((ComponentContextExposer) bs)
				.getComponentContext()
				: null;
		assertNotNull("Component context should be available for " + INT_NS110,
				ccIntNS110);
		ccIntNS110.getComponentInstance().dispose();
		Sleep.sleep(SLEEP * 3);
		assertEquals(
				"Deactivation reason shall be DEACTIVATION_REASON_DISPOSED", 5,
				0xFF & (getBaseConfigData(bs) >> 16));

		bs = getBaseService(CC_BC_MAP_INT_NS110);
		assertNotNull("bs should not be null", bs);
		tb6.stop();
		Sleep.sleep(SLEEP * 3);
		assertEquals(
				"Deactivation reason shall be DEACTIVATION_REASON_BUNDLE_STOPPED",
				6, 0xFF & (getBaseConfigData(bs) >> 16));

		uninstallBundle(tb6);
	}

	public void testBindUnbindParams() throws Exception {
		Bundle tb7 = installBundle("tb7.jar");
		tb7.start();
		Sleep.sleep(SLEEP * 3);

		final String SR_NS100 = TEST_CASE_ROOT + ".tb7.SrNS100";
		final String SR_NS110 = TEST_CASE_ROOT + ".tb7.SrNS110";
		final String CE_NS100 = TEST_CASE_ROOT + ".tb7.CeNS100";
		final String CE_NS110 = TEST_CASE_ROOT + ".tb7.CeNS110";
		final String CE_MAP_NS100 = TEST_CASE_ROOT + ".tb7.CeMapNS100";
		final String CE_MAP_NS110 = TEST_CASE_ROOT + ".tb7.CeMapNS110";

		ServiceReference ref = getContext().getServiceReference(
				ComponentEnabler.class.getName());
		assertNotNull(
				"Component Enabler Service Reference should be available", ref);
		ComponentEnabler enabler = (ComponentEnabler) getContext().getService(
				ref);
		assertNotNull("Component Enabler Service should be available", enabler);

		BaseService bs = getBaseService(SR_NS100);
		assertNotNull("Component " + SR_NS100 + " should be activated", bs);
		assertEquals("Bind method of " + SR_NS100 + " should be called",
				1 << 0, (1 << 0) & getBaseConfigData(bs));
		enabler.enableComponent(SR_NS100, false);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Unbind method of " + SR_NS100 + " should be called",
				1 << 1, (1 << 1) & getBaseConfigData(bs));

		bs = getBaseService(SR_NS110);
		assertNotNull("Component " + SR_NS110 + " should be activated", bs);
		assertEquals("Bind method of " + SR_NS110 + " should be called",
				1 << 0, (1 << 0) & getBaseConfigData(bs));
		enabler.enableComponent(SR_NS110, false);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Unbind method of " + SR_NS110 + " should be called",
				1 << 1, (1 << 1) & getBaseConfigData(bs));

		bs = getBaseService(CE_NS100);
		assertNotNull("Component " + CE_NS100 + " should be activated", bs);
		assertEquals("Bind method of " + CE_NS100 + " should be called",
				1 << 2, (1 << 2) & getBaseConfigData(bs));
		enabler.enableComponent(CE_NS100, false);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Unbind method of " + CE_NS100 + " should be called",
				1 << 3, (1 << 3) & getBaseConfigData(bs));

		bs = getBaseService(CE_NS110);
		assertNotNull("Component " + CE_NS110 + " should be activated", bs);
		assertEquals("Bind method of " + CE_NS110 + " should be called",
				1 << 2, (1 << 2) & getBaseConfigData(bs));
		enabler.enableComponent(CE_NS110, false);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Unbind method of " + CE_NS110 + " should be called",
				1 << 3, (1 << 3) & getBaseConfigData(bs));

		bs = getBaseService(CE_MAP_NS100);
		assertNotNull("Component " + CE_MAP_NS100 + " should be activated", bs);
		assertEquals("Bind method of " + CE_MAP_NS100 + " should not be called",
			0, getBaseConfigData(bs));

		enabler.enableComponent(CE_MAP_NS100, false);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Unbind method of " + CE_MAP_NS100 + " should not be called",
			0, getBaseConfigData(bs));
		assertNull("Component " + CE_MAP_NS100 + " should not be activated", bs);

		bs = getBaseService(CE_MAP_NS110);
		assertNotNull("Component " + CE_MAP_NS110 + " should be activated", bs);
		assertEquals("Bind method of " + CE_MAP_NS110 + " should be called",
				1 << 4, (1 << 4) & getBaseConfigData(bs));
		enabler.enableComponent(CE_MAP_NS110, false);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Unbind method of " + CE_MAP_NS110 + " should be called",
				1 << 5, (1 << 5) & getBaseConfigData(bs));

		getContext().ungetService(ref);
		uninstallBundle(tb7);
	}

	public void testOptionalNames() throws Exception {
		Bundle tb8 = installBundle("tb8.jar");
		tb8.start();
		Sleep.sleep(SLEEP * 3);
		BaseService bs;

		final String OPT_NAME_100 = TEST_CASE_ROOT + ".tb8.OptionalNames";
		final String OPT_NAME_110 = TEST_CASE_ROOT + ".tb8.OptionalNames2";
		final String OPT_REF_100 = TEST_CASE_ROOT + ".tb8.OptRef100";
		final String OPT_REF_110 = TEST_CASE_ROOT + ".tb8.OptRef110";

		assertNull("Component " + OPT_NAME_100 + " should not be activated",
				getBaseService(OPT_NAME_100));
		assertNotNull("Component " + OPT_NAME_110 + " should be activated",
				getBaseService(OPT_NAME_110));

		assertNull("Component " + OPT_REF_100 + " should not be activated",
				getBaseService(OPT_REF_100));
		assertNotNull("Component " + OPT_REF_110 + " should be activated",
				bs = getBaseService(OPT_REF_110));

		ComponentContext cc = (bs instanceof ComponentContextExposer) ? ((ComponentContextExposer) bs)
				.getComponentContext()
				: null;
		assertNotNull("Component context should be available", cc);
		assertNotNull(
				"Optional reference name should be set to interface attribute",
				cc.locateService(ComponentContextExposer.class.getName()));

		uninstallBundle(tb8);
	}


	  // tests wildcard handling in mf (e.g. Service-Component: OSGI-INF/*.xml)
	  public void testWildcardHandling() throws Exception {
	    Bundle tb9 = installBundle("tb9.jar");
	    tb9.start();
	    waitBundleStart();

		final String WILD = TEST_CASE_ROOT + ".tb9.Wildcard";

	    // check that the both components are available
	    assertTrue("The first Wildcard component should be available", checkAvailability(WILD + "1"));
	    assertTrue("The second Wildcard component should be available", checkAvailability(WILD + "2"));

	    uninstallBundle(tb9);
	  }

	public void testDisposingMultipleDependencies() throws Exception {
		Bundle tb10 = installBundle("tb10.jar");
		tb10.start();
		waitBundleStart();

		final String C1 = TEST_CASE_ROOT + ".tb10.Component1";
		final String C2 = TEST_CASE_ROOT + ".tb10.Component2";
		final String C3 = TEST_CASE_ROOT + ".tb10.Component3";

		BaseService serviceC1 = getBaseService(C1);
		assertNotNull("Component " + C1 + " should be activated", serviceC1);
		BaseService serviceC2 = getBaseService(C2);
		assertNotNull("Component " + C2 + " should be activated", serviceC2);
		BaseService serviceC3 = getBaseService(C3);
		assertNotNull("Component " + C3 + " should be activated", serviceC3);

		ComponentContext cc = (serviceC1 instanceof ComponentContextExposer) ? ((ComponentContextExposer) serviceC1)
				.getComponentContext()
				: null;
		assertNotNull("Component context should be available", cc);

		cc.disableComponent(C1);
		Sleep.sleep(SLEEP * 3);

		assertEquals("Component " + C3 + " should be deactivated first", 0,
				getBaseConfigData(serviceC3));
		assertEquals("Component " + C2 + " should be deactivated second", 1,
				getBaseConfigData(serviceC2));
		assertEquals("Component " + C1 + " should be deactivated third", 2,
				getBaseConfigData(serviceC1));

		uninstallBundle(tb10);
	}

	public void testReferenceTargetProperty() throws Exception {
		Bundle tb11 = installBundle("tb11.jar");
		tb11.start();
		waitBundleStart();

		final String EXPOSER = TEST_CASE_ROOT + ".tb11.Exposer";
		final String C1 = TEST_CASE_ROOT + ".tb11.C1";
		final String C2 = TEST_CASE_ROOT + ".tb11.C2";

		BaseService bs = getBaseService(EXPOSER);
		ComponentContext cc = (bs instanceof ComponentContextExposer) ? ((ComponentContextExposer) bs)
				.getComponentContext()
				: null;
		assertNotNull("Component context should be available", cc);

		BaseService serviceC2 = getBaseService(C2);
		// target property of referenced service of component Component2 should
		// not
		// be satisfied
		assertNull("Component " + C2
				+ " should not be activated because of unsatisfied reference",
				serviceC2);

		cc.enableComponent(C1);
		Sleep.sleep(SLEEP * 3);
		assertNotNull("Component " + C1 + " should be available",
				getBaseService(C1));

		serviceC2 = getBaseService(C2);
		// target property of referenced service of component Component2 should
		// now
		// be satisfied
		assertNotNull("Component " + C2 + " should be activated", serviceC2);

		uninstallBundle(tb11);
	}

	public void testLazyBundles() throws Exception {
		Bundle tb12 = installBundle("tb12.jar");
		// lazy bundle
		tb12.start(Bundle.START_ACTIVATION_POLICY);
		waitBundleStart();

		final String COMP = TEST_CASE_ROOT + ".tb12.component";

		Sleep.sleep(SLEEP);
		assertTrue("Provided service of Component " + COMP
				+ " should be available.", trackerBaseService.size() > 0);

		uninstallBundle(tb12);
	}

	// Testing modified attribute for XML NS 1.0.0
	public void testModified100() throws Exception {
		ConfigurationAdmin cm = (ConfigurationAdmin) trackerCM.getService();
		assertNotNull("The ConfigurationAdmin should be available", cm);

		Bundle tb13 = installBundle("tb13.jar");

		Hashtable props = new Hashtable(10);
		props.put("config.dummy.data", new Integer(1));
		cm.getConfiguration(MOD_NOTSET_NS100, null).update(props);
		cm.getConfiguration(MOD_NOARGS_NS100, null).update(props);
		cm.getConfiguration(MOD_CC_NS100, null).update(props);
		cm.getConfiguration(MOD_BC_NS100, null).update(props);
		cm.getConfiguration(MOD_MAP_NS100, null).update(props);
		cm.getConfiguration(MOD_CC_BC_MAP_NS100, null).update(props);

		Sleep.sleep(SLEEP * 3);

		tb13.start();
		waitBundleStart();

		props.put("config.dummy.data", new Integer(2));
		Hashtable unsatisfyingProps = new Hashtable(10);
		unsatisfyingProps.put("ref.target", "(component.name=" + TEST_CASE_ROOT
				+ ".tb13.unexisting.provider)");

		BaseService bs = getBaseService(MOD_NOTSET_NS100);
		assertNotNull(bs);
		cm.getConfiguration(MOD_NOTSET_NS100, null).update(props);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Modified method of " + MOD_NOTSET_NS100
				+ " should not be called", 0, (1 << 0) & getBaseConfigData(bs));
		assertEquals("Deactivate method of " + MOD_NOTSET_NS100
				+ " should be called", 1 << 7, (1 << 7) & getBaseConfigData(bs));
		bs = getBaseService(MOD_NOTSET_NS100);
		cm.getConfiguration(MOD_NOTSET_NS100, null).update(unsatisfyingProps);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Modified method of " + MOD_NOTSET_NS100
				+ " should not be called", 0, (1 << 0) & getBaseConfigData(bs));
		assertEquals("Deactivate method of " + MOD_NOTSET_NS100
				+ " should be called", 1 << 7, (1 << 7) & getBaseConfigData(bs));

		// INVALID COMPONENTS for XML NS 1.0.0 - modified attribute is set
		bs = getBaseService(MOD_NOARGS_NS100);
		assertEquals("Component " + MOD_NOARGS_NS100
				+ " should not be activated", null, bs);
		bs = getBaseService(MOD_CC_NS100);
		assertEquals("Component " + MOD_CC_NS100 + " should not be activated",
				null, bs);
		bs = getBaseService(MOD_BC_NS100);
		assertEquals("Component " + MOD_BC_NS100 + " should not be activated",
				null, bs);
		bs = getBaseService(MOD_MAP_NS100);
		assertEquals("Component " + MOD_MAP_NS100 + " should not be activated",
				null, bs);
		bs = getBaseService(MOD_CC_BC_MAP_NS100);
		assertEquals("Component " + MOD_CC_BC_MAP_NS100
				+ " should not be activated", null, bs);

		uninstallBundle(tb13);
	}

	// Testing modified attribute for XML NS 1.1.0
	public void testModified110() throws Exception {
		ConfigurationAdmin cm = (ConfigurationAdmin) trackerCM.getService();
		assertNotNull("The ConfigurationAdmin should be available", cm);

		Bundle tb13a = installBundle("tb13a.jar");

		Hashtable props = new Hashtable(10);
		props.put("config.dummy.data", new Integer(1));
		cm.getConfiguration(MOD_NOTSET_NS110, null).update(props);
		cm.getConfiguration(MOD_NOARGS_NS110, null).update(props);
		cm.getConfiguration(MOD_CC_NS110, null).update(props);
		cm.getConfiguration(MOD_BC_NS110, null).update(props);
		cm.getConfiguration(MOD_MAP_NS110, null).update(props);
		cm.getConfiguration(MOD_CC_BC_MAP_NS110, null).update(props);

		Sleep.sleep(SLEEP * 3);

		tb13a.start();
		waitBundleStart();

		props.put("config.dummy.data", new Integer(2));
		Hashtable unsatisfyingProps = new Hashtable(10);
		unsatisfyingProps.put("ref.target", "(component.name=" + TEST_CASE_ROOT
				+ ".tb13.unexisting.provider)");

		BaseService bs = getBaseService(MOD_NOTSET_NS110);
		cm.getConfiguration(MOD_NOTSET_NS110, null).update(props);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Modified method of " + MOD_NOTSET_NS110
				+ " should not be called", 0, (1 << 0) & getBaseConfigData(bs));
		assertEquals("Deactivate method of " + MOD_NOTSET_NS110
				+ " should be called", 1 << 7, (1 << 7) & getBaseConfigData(bs));
		bs = getBaseService(MOD_NOTSET_NS110);
		cm.getConfiguration(MOD_NOTSET_NS110, null).update(unsatisfyingProps);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Modified method of " + MOD_NOTSET_NS110
				+ " should not be called", 0, (1 << 0) & getBaseConfigData(bs));
		assertEquals("Deactivate method of " + MOD_NOTSET_NS110
				+ " should be called", 1 << 7, (1 << 7) & getBaseConfigData(bs));
		// Re-activating
		bs = getBaseService(MOD_NOTSET_NS110);
		assertEquals("Activate method of " + MOD_NOTSET_NS110
				+ " should be called", 1 << 6, (1 << 6) & getBaseConfigData(bs));

		bs = getBaseService(MOD_NOARGS_NS110);
		cm.getConfiguration(MOD_NOARGS_NS110, null).update(props);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Modified method of " + MOD_NOARGS_NS110
				+ " should be called", 1 << 1, (1 << 1) & getBaseConfigData(bs));
		assertEquals("Deactivate method of " + MOD_NOARGS_NS110
				+ " should not be called", 0, (1 << 7) & getBaseConfigData(bs));
		cm.getConfiguration(MOD_NOARGS_NS110, null).update(unsatisfyingProps);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Deactivate method of " + MOD_NOARGS_NS110
				+ " should be called", 1 << 7, (1 << 7) & getBaseConfigData(bs));
		// Re-activating
		bs = getBaseService(MOD_NOARGS_NS110);
		assertEquals("Activate method of " + MOD_NOARGS_NS110
				+ " should be called", 1 << 6, (1 << 6) & getBaseConfigData(bs));

		bs = getBaseService(MOD_CC_NS110);
		cm.getConfiguration(MOD_CC_NS110, null).update(props);
		Sleep.sleep(SLEEP * 3);
		assertEquals(
				"Modified method of " + MOD_CC_NS110 + " should be called",
				1 << 2, (1 << 2) & getBaseConfigData(bs));
		assertEquals("Deactivate method of " + MOD_CC_NS110
				+ " should not be called", 0, (1 << 7) & getBaseConfigData(bs));
		cm.getConfiguration(MOD_CC_NS110, null).update(unsatisfyingProps);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Deactivate method of " + MOD_CC_NS110
				+ " should be called", 1 << 7, (1 << 7) & getBaseConfigData(bs));
		// Re-activating
		bs = getBaseService(MOD_CC_NS110);
		assertEquals(
				"Activate method of " + MOD_CC_NS110 + " should be called",
				1 << 6, (1 << 6) & getBaseConfigData(bs));

		bs = getBaseService(MOD_BC_NS110);
		cm.getConfiguration(MOD_BC_NS110, null).update(props);
		Sleep.sleep(SLEEP * 3);
		assertEquals(
				"Modified method of " + MOD_BC_NS110 + " should be called",
				1 << 3, (1 << 3) & getBaseConfigData(bs));
		assertEquals("Deactivate method of " + MOD_BC_NS110
				+ " should not be called", 0, (1 << 7) & getBaseConfigData(bs));
		cm.getConfiguration(MOD_BC_NS110, null).update(unsatisfyingProps);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Deactivate method of " + MOD_BC_NS110
				+ " should be called", 1 << 7, (1 << 7) & getBaseConfigData(bs));
		// Re-activating
		bs = getBaseService(MOD_BC_NS110);
		assertEquals(
				"Activate method of " + MOD_BC_NS110 + " should be called",
				1 << 6, (1 << 6) & getBaseConfigData(bs));

		bs = getBaseService(MOD_MAP_NS110);
		cm.getConfiguration(MOD_MAP_NS110, null).update(props);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Modified method of " + MOD_MAP_NS110
				+ " should be called", 1 << 4, (1 << 4) & getBaseConfigData(bs));
		assertEquals("Deactivate method of " + MOD_MAP_NS110
				+ " should not be called", 0, (1 << 7) & getBaseConfigData(bs));
		cm.getConfiguration(MOD_MAP_NS110, null).update(unsatisfyingProps);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Deactivate method of " + MOD_MAP_NS110
				+ " should be called", 1 << 7, (1 << 7) & getBaseConfigData(bs));
		// Re-activating
		bs = getBaseService(MOD_MAP_NS110);
		assertEquals("Activate method of " + MOD_MAP_NS110
				+ " should be called", 1 << 6, (1 << 6) & getBaseConfigData(bs));

		bs = getBaseService(MOD_CC_BC_MAP_NS110);
		cm.getConfiguration(MOD_CC_BC_MAP_NS110, null).update(props);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Modified method of " + MOD_CC_BC_MAP_NS110
				+ " should be called", 1 << 5, (1 << 5) & getBaseConfigData(bs));
		assertEquals("Deactivate method of " + MOD_CC_BC_MAP_NS110
				+ " should not be called", 0, (1 << 7) & getBaseConfigData(bs));
		cm.getConfiguration(MOD_CC_BC_MAP_NS110, null)
				.update(unsatisfyingProps);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Deactivate method of " + MOD_CC_BC_MAP_NS110
				+ " should be called", 1 << 7, (1 << 7) & getBaseConfigData(bs));
		// Re-activating
		bs = getBaseService(MOD_CC_BC_MAP_NS110);
		assertEquals("Activate method of " + MOD_CC_BC_MAP_NS110
				+ " should be called", 1 << 6, (1 << 6) & getBaseConfigData(bs));

		uninstallBundle(tb13a);
	}

	// Testing modified attribute - special cases
	public void testModifiedSpecialCases() throws Exception {
		ConfigurationAdmin cm = (ConfigurationAdmin) trackerCM.getService();
		assertNotNull("The ConfigurationAdmin should be available", cm);

		Bundle tb13a = installBundle("tb13a.jar");

		Hashtable props = new Hashtable(10);
		props.put("config.dummy.data", new Integer(1));
		cm.getConfiguration(MOD_CC_NS110, null).update(props);
		cm.getConfiguration(MOD_NOT_EXIST_NS110, null).update(props);
		cm.getConfiguration(MOD_THROW_EX_NS110, null).update(props);
		cm.getConfiguration(MOD_BC_NS110, null).update(props);
		Sleep.sleep(SLEEP * 3);

		tb13a.start();
		waitBundleStart();

		// Verifying correctness of updated component properties
		BaseService bs = getBaseService(MOD_CC_NS110);
		props.put("config.dummy.data", new Integer(2));
		cm.getConfiguration(MOD_CC_NS110, null).update(props);
		Sleep.sleep(SLEEP * 3);
		Object val = ((ComponentContextExposer) bs).getComponentContext()
				.getProperties().get("config.dummy.data");
		assertEquals(
				"Modified method of " + MOD_CC_NS110 + " should be called",
				1 << 2, (1 << 2) & getBaseConfigData(bs));
		assertTrue("Component properties should be updated properly for "
				+ MOD_CC_NS110, (new Integer(2)).equals(val));

		// Specified modified method doesn't exist, deactivate() should be
		// called
		// instead of modified
		bs = getBaseService(MOD_NOT_EXIST_NS110);
		cm.getConfiguration(MOD_NOT_EXIST_NS110, null).update(props);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Deactivate method of " + MOD_NOT_EXIST_NS110
				+ " should be called", 1 << 7, (1 << 7) & getBaseConfigData(bs));
		// Re-activating
		bs = getBaseService(MOD_NOT_EXIST_NS110);
		assertEquals("Activate method of " + MOD_NOT_EXIST_NS110
				+ " should be called", 1 << 6, (1 << 6) & getBaseConfigData(bs));

		// Specified modified method throws exception. Normal workflow should
		// continue, deactivate() should not be called
		bs = getBaseService(MOD_THROW_EX_NS110);
		cm.getConfiguration(MOD_THROW_EX_NS110, null).update(props);
		Sleep.sleep(SLEEP * 3);
		assertEquals("Deactivate method of " + MOD_THROW_EX_NS110
				+ " should not be called", 0, (1 << 7) & getBaseConfigData(bs));

		// Deleting component configuration
		bs = getBaseService(MOD_BC_NS110);
		cm.getConfiguration(MOD_BC_NS110, null).delete();
		Sleep.sleep(SLEEP * 3);
		assertEquals("Modified method of " + MOD_BC_NS110
				+ " should not be called", 0, (1 << 5) & getBaseConfigData(bs));
		assertEquals("Deactivate method of " + MOD_BC_NS110
				+ " should be called", 1 << 7, (1 << 7) & getBaseConfigData(bs));
		// Re-activating
		bs = getBaseService(MOD_BC_NS110);
		assertEquals(
				"Activate method of " + MOD_BC_NS110 + " should be called",
				1 << 6, (1 << 6) & getBaseConfigData(bs));

		uninstallBundle(tb13a);
	}

	public void testPrivateProperties() throws Exception {
		Bundle tb14 = installBundle("tb14.jar");
		tb14.start();
		waitBundleStart();

		final String COMP = TEST_CASE_ROOT + ".tb14.component";

		ServiceReference ref = trackerBaseService.getServiceReference();
		assertNotNull("Provided service of " + COMP + " should be available",
				ref);
		String[] keys = ref.getPropertyKeys();
		for (int i = 0; i < keys.length; i++) {
			assertTrue("Private properties should not be propagated", !keys[i]
					.startsWith("."));
		}

		uninstallBundle(tb14);
	}

	public void testUpdatedReference() throws Exception {
		Bundle tb15 = installBundle("tb15.jar");
		try {
			tb15.start();
			waitBundleStart();

			final String KEY = TEST_CASE_ROOT + ".tb15.serviceproperty";
			final String TB15_SVCMAP = TEST_CASE_ROOT + ".tb15.updatedSvcMap";
			final String TB15_SVC = TEST_CASE_ROOT + ".tb15.updatedSvc";
			final String TB15_SR = TEST_CASE_ROOT + ".tb15.updatedSr";
			final String TB15_OVERLOADED1 = TEST_CASE_ROOT
					+ ".tb15.updatedOverloaded1";
			final String TB15_OVERLOADED2 = TEST_CASE_ROOT
					+ ".tb15.updatedOverloaded2";
			final String TB15_BADSIG = TEST_CASE_ROOT + ".tb15.updatedBadSig";
			final String TB15_NOTSET = TEST_CASE_ROOT + ".tb15.updatedNotSet";
			final String TB15_110 = TEST_CASE_ROOT + ".tb15.updated110";
			final String TB15_100 = TEST_CASE_ROOT + ".tb15.updated100";

			BaseService bsSvcMap = getBaseService(TB15_SVCMAP);
			BaseService bsSvc = getBaseService(TB15_SVC);
			BaseService bsSr = getBaseService(TB15_SR);
			BaseService bsOverloaded1 = getBaseService(TB15_OVERLOADED1);
			BaseService bsOverloaded2 = getBaseService(TB15_OVERLOADED2);
			BaseService bsBadSig = getBaseService(TB15_BADSIG);
			BaseService bsNotSet = getBaseService(TB15_NOTSET);
			BaseService bs110 = getBaseService(TB15_110);
			BaseService bs100 = getBaseService(TB15_100);
			assertNotNull("Provided service of " + TB15_SVCMAP
					+ " should be available", bsSvcMap);
			assertNotNull("Provided service of " + TB15_SVC
					+ " should be available", bsSvc);
			assertNotNull("Provided service of " + TB15_SR
					+ " should be available", bsSr);
			assertNotNull("Provided service of " + TB15_OVERLOADED1
					+ " should be available", bsOverloaded1);
			assertNotNull("Provided service of " + TB15_OVERLOADED2
					+ " should be available", bsOverloaded2);
			assertNotNull("Provided service of " + TB15_BADSIG
					+ " should be available", bsBadSig);
			assertNotNull("Provided service of " + TB15_NOTSET
					+ " should be available", bsNotSet);
			assertNull("Provided service of " + TB15_110
					+ " should be available", bs110);
			assertNull("Provided service of " + TB15_100
					+ " should be available", bs100);

			assertNull("service property not null", bsSvcMap.getProperties()
					.get(KEY));
			assertNull("service property not null",
					bsSvc.getProperties().get(KEY));
			assertNull("service property not null",
					bsSr.getProperties().get(KEY));
			assertNull("service property not null", bsOverloaded1
					.getProperties().get(KEY));
			assertNull("service property not null", bsOverloaded2
					.getProperties().get(KEY));
			assertNull("service property not null", bsBadSig.getProperties()
					.get(KEY));
			assertNull("service property not null", bsNotSet.getProperties()
					.get(KEY));

			TestObject service = new TestObject();
			Dictionary props = new Hashtable();
			props.put(KEY, "1");
			ServiceRegistration reg = getContext().registerService(
					TestObject.class.getName(), service, props);
			try {
				Sleep.sleep(SLEEP * 3);
				assertEquals("service property incorrect", "bind1", bsSvcMap
						.getProperties().get(KEY));
				assertEquals("service property incorrect", "bind1", bsSvc
						.getProperties().get(KEY));
				assertEquals("service property incorrect", "bind1", bsSr
						.getProperties().get(KEY));
				assertEquals("service property incorrect", "bind1",
						bsOverloaded1.getProperties().get(KEY));
				assertEquals("service property incorrect", "bind1",
						bsOverloaded2.getProperties().get(KEY));
				assertEquals("service property incorrect", "bind1", bsNotSet
						.getProperties().get(KEY));
				assertEquals("service property incorrect", "bind1", bsBadSig
						.getProperties().get(KEY));

				props.put(KEY, "2");
				reg.setProperties(props);
				Sleep.sleep(SLEEP * 3);
				assertEquals("service property incorrect", "updatedSvcMap2",
						bsSvcMap.getProperties().get(KEY));
				assertEquals("service property incorrect", "updatedSvc", bsSvc
						.getProperties().get(KEY));
				assertEquals("service property incorrect", "updatedSr2", bsSr
						.getProperties().get(KEY));
				assertEquals("service property incorrect",
						"updatedOverloaded1Sr2", bsOverloaded1.getProperties()
								.get(KEY));
				assertEquals("service property incorrect",
						"updatedOverloaded2Svc", bsOverloaded2.getProperties()
								.get(KEY));
				assertEquals("service property incorrect", "bind1", bsBadSig
						.getProperties().get(KEY));
				assertEquals("service property incorrect", "bind1", bsNotSet
						.getProperties().get(KEY));

				reg.unregister();
				reg = null;
				Sleep.sleep(SLEEP * 3);
				assertEquals("service property incorrect", "unbind2", bsSvcMap
						.getProperties().get(KEY));
				assertEquals("service property incorrect", "unbind2", bsSvc
						.getProperties().get(KEY));
				assertEquals("service property incorrect", "unbind2", bsSr
						.getProperties().get(KEY));
				assertEquals("service property incorrect", "unbind2",
						bsOverloaded1.getProperties().get(KEY));
				assertEquals("service property incorrect", "unbind2",
						bsOverloaded2.getProperties().get(KEY));
				assertEquals("service property incorrect", "unbind2", bsBadSig
						.getProperties().get(KEY));
				assertEquals("service property incorrect", "unbind2", bsNotSet
						.getProperties().get(KEY));
			}
			finally {
				if (reg != null)
					reg.unregister();
			}
		}
		finally {
			uninstallBundle(tb15);
		}
	}

	public void testConfigurationPID() throws Exception {
		ConfigurationAdmin cm = (ConfigurationAdmin) trackerCM.getService();
		assertNotNull("The ConfigurationAdmin should be available", cm);

		final String PID = TEST_CASE_ROOT + ".tb16.pid1";
		final String KEY = TEST_CASE_ROOT + ".tb16.configproperty";
		final String TB16_REQUIRED = TEST_CASE_ROOT
				+ ".tb16.configurationRequired";
		final String TB16_NOTPRESENT = TEST_CASE_ROOT
				+ ".tb16.configurationNotPresent";
		final String TB16_IGNORED = TEST_CASE_ROOT
				+ ".tb16.configurationIgnored";
		final String TB16_110 = TEST_CASE_ROOT + ".tb16.configuration110";
		final String TB16_100 = TEST_CASE_ROOT + ".tb16.configuration100";

		Configuration config = cm.getConfiguration(PID, null);
		Dictionary props = new Hashtable();
		props.put(KEY, "config1");
		config.update(props);
		config = cm.getConfiguration(TB16_REQUIRED, null);
		props = new Hashtable();
		props.put(KEY, "bad1");
		config.update(props);
		config = cm.getConfiguration(TB16_NOTPRESENT, null);
		props = new Hashtable();
		props.put(KEY, "bad2");
		config.update(props);

		Bundle tb16 = installBundle("tb16.jar");
		try {
			tb16.start();
			waitBundleStart();


			BaseService bsRequired = getBaseService(TB16_REQUIRED);
			BaseService bsNotPesent = getBaseService(TB16_NOTPRESENT);
			BaseService bsIgnored = getBaseService(TB16_IGNORED);
			BaseService bs110 = getBaseService(TB16_110);
			BaseService bs100 = getBaseService(TB16_100);
			assertNotNull("Provided service of " + TB16_REQUIRED
					+ " should be available", bsRequired);
			assertNotNull("Provided service of " + TB16_NOTPRESENT
					+ " should be available", bsNotPesent);
			assertNotNull("Provided service of " + TB16_IGNORED
					+ " should be available", bsIgnored);
			assertNull("Provided service of " + TB16_110
					+ " should be available", bs110);
			assertNull("Provided service of " + TB16_100
					+ " should be available", bs100);

			assertEquals("component property wrong", "config1", bsRequired
					.getProperties().get(KEY));
			assertEquals("component property wrong", "xml2", bsNotPesent
					.getProperties().get(KEY));
			assertEquals("component property wrong", "xml3", bsIgnored
					.getProperties().get(KEY));

		}
		finally {
			uninstallBundle(tb16);
		}
	}

	public void testReferencePolicyOptionStaticReluctant() throws Exception {
		final String KEY = TEST_CASE_ROOT + ".tb17.serviceproperty";
		final String IDENTITY = TEST_CASE_ROOT + ".tb17.identity";
		final String TB17_SR01 = TEST_CASE_ROOT + ".tb17.SR01";
		final String TB17_SR11 = TEST_CASE_ROOT + ".tb17.SR11";
		final String TB17_SR0N = TEST_CASE_ROOT + ".tb17.SR0N";
		final String TB17_SR1N = TEST_CASE_ROOT + ".tb17.SR1N";

		TestObject service = new TestObject();
		Dictionary serviceProps = new Hashtable();
		serviceProps.put(KEY, "initial");
		ServiceRegistration regInitial = getContext().registerService(
				TestObject.class.getName(), service, serviceProps);
		ServiceRegistration regHigher = null;

		Bundle tb17 = installBundle("tb17.jar");
		try {
			tb17.start();
			waitBundleStart();

			BaseService bsSR01 = getBaseService(TB17_SR01);
			BaseService bsSR11 = getBaseService(TB17_SR11);
			BaseService bsSR0N = getBaseService(TB17_SR0N);
			BaseService bsSR1N = getBaseService(TB17_SR1N);
			assertNotNull("Provided service of " + TB17_SR01
					+ " should be available", bsSR01);
			assertNotNull("Provided service of " + TB17_SR11
					+ " should be available", bsSR11);
			assertNotNull("Provided service of " + TB17_SR0N
					+ " should be available", bsSR0N);
			assertNotNull("Provided service of " + TB17_SR1N
					+ " should be available", bsSR1N);

			Object idSR01 = bsSR01.getProperties().get(IDENTITY);
			Object idSR11 = bsSR11.getProperties().get(IDENTITY);
			Object idSR0N = bsSR0N.getProperties().get(IDENTITY);
			Object idSR1N = bsSR1N.getProperties().get(IDENTITY);

			assertEquals("service property incorrect", "bindSR01/initial",
					bsSR01.getProperties().get(KEY));
			assertEquals("service property incorrect", "bindSR11/initial",
					bsSR11.getProperties().get(KEY));
			assertEquals("service property incorrect", "bindSR0N/initial",
					bsSR0N.getProperties().get(KEY));
			assertEquals("service property incorrect", "bindSR1N/initial",
					bsSR1N.getProperties().get(KEY));

			serviceProps.put(KEY, "higher");
			serviceProps.put(Constants.SERVICE_RANKING, new Integer(100));
			regHigher = getContext().registerService(
					TestObject.class.getName(), service, serviceProps);
			Sleep.sleep(SLEEP * 3);

			/* reacquire since they may have been reactivated */
			bsSR01 = getBaseService(TB17_SR01);
			bsSR11 = getBaseService(TB17_SR11);
			bsSR0N = getBaseService(TB17_SR0N);
			bsSR1N = getBaseService(TB17_SR1N);

			assertEquals("component reactivated", idSR01, bsSR01
					.getProperties().get(IDENTITY));
			assertEquals("service property incorrect", "bindSR01/initial",
					bsSR01.getProperties().get(KEY));
			assertEquals("component reactivated", idSR11, bsSR11
					.getProperties().get(IDENTITY));
			assertEquals("service property incorrect", "bindSR11/initial",
					bsSR11.getProperties().get(KEY));
			assertEquals("component reactivated", idSR0N, bsSR0N
					.getProperties().get(IDENTITY));
			assertEquals("service property incorrect", "bindSR0N/initial",
					bsSR0N.getProperties().get(KEY));
			assertEquals("component reactivated", idSR1N, bsSR1N
					.getProperties().get(IDENTITY));
			assertEquals("service property incorrect", "bindSR1N/initial",
					bsSR1N.getProperties().get(KEY));

		}
		finally {
			uninstallBundle(tb17);
			if (regHigher != null) {
				regHigher.unregister();
			}
			if (regInitial != null) {
				regInitial.unregister();
			}
		}
	}

	public void testReferencePolicyOptionStaticGreedy() throws Exception {
		final String KEY = TEST_CASE_ROOT + ".tb17.serviceproperty";
		final String IDENTITY = TEST_CASE_ROOT + ".tb17.identity";
		final String TB17_SG01 = TEST_CASE_ROOT + ".tb17.SG01";
		final String TB17_SG11 = TEST_CASE_ROOT + ".tb17.SG11";
		final String TB17_SG0N = TEST_CASE_ROOT + ".tb17.SG0N";
		final String TB17_SG1N = TEST_CASE_ROOT + ".tb17.SG1N";

		TestObject service = new TestObject();
		Dictionary serviceProps = new Hashtable();
		serviceProps.put(KEY, "initial");
		ServiceRegistration regInitial = getContext().registerService(
				TestObject.class.getName(), service, serviceProps);
		ServiceRegistration regHigher = null;

		Bundle tb17 = installBundle("tb17.jar");
		try {
			tb17.start();
			waitBundleStart();

			BaseService bsSG01 = getBaseService(TB17_SG01);
			BaseService bsSG11 = getBaseService(TB17_SG11);
			BaseService bsSG0N = getBaseService(TB17_SG0N);
			BaseService bsSG1N = getBaseService(TB17_SG1N);
			assertNotNull("Provided service of " + TB17_SG01
					+ " should be available", bsSG01);
			assertNotNull("Provided service of " + TB17_SG11
					+ " should be available", bsSG11);
			assertNotNull("Provided service of " + TB17_SG0N
					+ " should be available", bsSG0N);
			assertNotNull("Provided service of " + TB17_SG1N
					+ " should be available", bsSG1N);

			Object idSG01 = bsSG01.getProperties().get(IDENTITY);
			Object idSG11 = bsSG11.getProperties().get(IDENTITY);
			Object idSG0N = bsSG0N.getProperties().get(IDENTITY);
			Object idSG1N = bsSG1N.getProperties().get(IDENTITY);

			assertEquals("service property incorrect", "bindSG01/initial",
					bsSG01.getProperties().get(KEY));
			assertEquals("service property incorrect", "bindSG11/initial",
					bsSG11.getProperties().get(KEY));
			assertEquals("service property incorrect", "bindSG0N/initial",
					bsSG0N.getProperties().get(KEY));
			assertEquals("service property incorrect", "bindSG1N/initial",
					bsSG1N.getProperties().get(KEY));

			serviceProps.put(KEY, "higher");
			serviceProps.put(Constants.SERVICE_RANKING, new Integer(100));
			regHigher = getContext().registerService(
					TestObject.class.getName(), service, serviceProps);
			Sleep.sleep(SLEEP * 3);

			/* reacquire since they may have been reactivated */
			bsSG01 = getBaseService(TB17_SG01);
			bsSG11 = getBaseService(TB17_SG11);
			bsSG0N = getBaseService(TB17_SG0N);
			bsSG1N = getBaseService(TB17_SG1N);

			assertFalse("component not reactivated",
					idSG01.equals(bsSG01.getProperties().get(IDENTITY)));
			assertEquals("service property incorrect", "bindSG01/higher",
					bsSG01.getProperties().get(KEY));
			assertFalse("component not reactivated",
					idSG11.equals(bsSG11.getProperties().get(IDENTITY)));
			assertEquals("service property incorrect", "bindSG11/higher",
					bsSG11.getProperties().get(KEY));
			assertFalse("component not reactivated",
					idSG0N.equals(bsSG0N.getProperties().get(IDENTITY)));
			assertEquals("service property incorrect",
					"bindSG0N/higher/initial", bsSG0N.getProperties().get(KEY));
			assertFalse("component not reactivated",
					idSG1N.equals(bsSG1N.getProperties().get(IDENTITY)));
			assertEquals("service property incorrect",
					"bindSG1N/higher/initial", bsSG1N.getProperties().get(KEY));

		}
		finally {
			uninstallBundle(tb17);
			if (regHigher != null) {
				regHigher.unregister();
			}
			if (regInitial != null) {
				regInitial.unregister();
			}
		}
	}

	public void testReferencePolicyOptionDynamicReluctant() throws Exception {
		final String KEY = TEST_CASE_ROOT + ".tb17.serviceproperty";
		final String IDENTITY = TEST_CASE_ROOT + ".tb17.identity";
		final String TB17_DR01 = TEST_CASE_ROOT + ".tb17.DR01";
		final String TB17_DR11 = TEST_CASE_ROOT + ".tb17.DR11";
		final String TB17_DR0N = TEST_CASE_ROOT + ".tb17.DR0N";
		final String TB17_DR1N = TEST_CASE_ROOT + ".tb17.DR1N";

		TestObject service = new TestObject();
		Dictionary serviceProps = new Hashtable();
		serviceProps.put(KEY, "initial");
		ServiceRegistration regInitial = getContext().registerService(
				TestObject.class.getName(), service, serviceProps);
		ServiceRegistration regHigher = null;

		Bundle tb17 = installBundle("tb17.jar");
		try {
			tb17.start();
			waitBundleStart();

			BaseService bsDR01 = getBaseService(TB17_DR01);
			BaseService bsDR11 = getBaseService(TB17_DR11);
			BaseService bsDR0N = getBaseService(TB17_DR0N);
			BaseService bsDR1N = getBaseService(TB17_DR1N);
			assertNotNull("Provided service of " + TB17_DR01
					+ " should be available", bsDR01);
			assertNotNull("Provided service of " + TB17_DR11
					+ " should be available", bsDR11);
			assertNotNull("Provided service of " + TB17_DR0N
					+ " should be available", bsDR0N);
			assertNotNull("Provided service of " + TB17_DR1N
					+ " should be available", bsDR1N);

			Object idDR01 = bsDR01.getProperties().get(IDENTITY);
			Object idDR11 = bsDR11.getProperties().get(IDENTITY);
			Object idDR0N = bsDR0N.getProperties().get(IDENTITY);
			Object idDR1N = bsDR1N.getProperties().get(IDENTITY);

			assertEquals("service property incorrect", "bindDR01/initial",
					bsDR01.getProperties().get(KEY));
			assertEquals("service property incorrect", "bindDR11/initial",
					bsDR11.getProperties().get(KEY));
			assertEquals("service property incorrect", "bindDR0N/initial",
					bsDR0N.getProperties().get(KEY));
			assertEquals("service property incorrect", "bindDR1N/initial",
					bsDR1N.getProperties().get(KEY));

			serviceProps.put(KEY, "higher");
			serviceProps.put(Constants.SERVICE_RANKING, new Integer(100));
			regHigher = getContext().registerService(
					TestObject.class.getName(), service, serviceProps);
			Sleep.sleep(SLEEP * 3);

			/* reacquire since they may have been reactivated */
			bsDR01 = getBaseService(TB17_DR01);
			bsDR11 = getBaseService(TB17_DR11);
			bsDR0N = getBaseService(TB17_DR0N);
			bsDR1N = getBaseService(TB17_DR1N);

			assertEquals("component reactivated", idDR01, bsDR01
					.getProperties().get(IDENTITY));
			assertEquals("service property incorrect", "bindDR01/initial",
					bsDR01.getProperties().get(KEY));
			assertEquals("component reactivated", idDR11, bsDR11
					.getProperties().get(IDENTITY));
			assertEquals("service property incorrect", "bindDR11/initial",
					bsDR11.getProperties().get(KEY));
			assertEquals("component reactivated", idDR0N, bsDR0N
					.getProperties().get(IDENTITY));
			assertEquals("service property incorrect",
					"bindDR0N/higher/initial", bsDR0N.getProperties().get(KEY));
			assertEquals("component reactivated", idDR1N, bsDR1N
					.getProperties().get(IDENTITY));
			assertEquals("service property incorrect",
					"bindDR1N/higher/initial", bsDR1N.getProperties().get(KEY));

		}
		finally {
			uninstallBundle(tb17);
			if (regHigher != null) {
				regHigher.unregister();
			}
			if (regInitial != null) {
				regInitial.unregister();
			}
		}
	}

	public void testReferencePolicyOptionDynamicGreedy() throws Exception {
		final String KEY = TEST_CASE_ROOT + ".tb17.serviceproperty";
		final String IDENTITY = TEST_CASE_ROOT + ".tb17.identity";
		final String TB17_DG01 = TEST_CASE_ROOT + ".tb17.DG01";
		final String TB17_DG11 = TEST_CASE_ROOT + ".tb17.DG11";
		final String TB17_DG0N = TEST_CASE_ROOT + ".tb17.DG0N";
		final String TB17_DG1N = TEST_CASE_ROOT + ".tb17.DG1N";

		TestObject service = new TestObject();
		Dictionary serviceProps = new Hashtable();
		serviceProps.put(KEY, "initial");
		ServiceRegistration regInitial = getContext().registerService(
				TestObject.class.getName(), service, serviceProps);
		ServiceRegistration regHigher = null;

		Bundle tb17 = installBundle("tb17.jar");
		try {
			tb17.start();
			waitBundleStart();

			BaseService bsDG01 = getBaseService(TB17_DG01);
			BaseService bsDG11 = getBaseService(TB17_DG11);
			BaseService bsDG0N = getBaseService(TB17_DG0N);
			BaseService bsDG1N = getBaseService(TB17_DG1N);
			assertNotNull("Provided service of " + TB17_DG01
					+ " should be available", bsDG01);
			assertNotNull("Provided service of " + TB17_DG11
					+ " should be available", bsDG11);
			assertNotNull("Provided service of " + TB17_DG0N
					+ " should be available", bsDG0N);
			assertNotNull("Provided service of " + TB17_DG1N
					+ " should be available", bsDG1N);

			Object idDG01 = bsDG01.getProperties().get(IDENTITY);
			Object idDG11 = bsDG11.getProperties().get(IDENTITY);
			Object idDG0N = bsDG0N.getProperties().get(IDENTITY);
			Object idDG1N = bsDG1N.getProperties().get(IDENTITY);

			assertEquals("service property incorrect", "bindDG01/initial",
					bsDG01.getProperties().get(KEY));
			assertEquals("service property incorrect", "bindDG11/initial",
					bsDG11.getProperties().get(KEY));
			assertEquals("service property incorrect", "bindDG0N/initial",
					bsDG0N.getProperties().get(KEY));
			assertEquals("service property incorrect", "bindDG1N/initial",
					bsDG1N.getProperties().get(KEY));

			serviceProps.put(KEY, "higher");
			serviceProps.put(Constants.SERVICE_RANKING, new Integer(100));
			regHigher = getContext().registerService(
					TestObject.class.getName(), service, serviceProps);
			Sleep.sleep(SLEEP * 3);

			/* reacquire since they may have been reactivated */
			bsDG01 = getBaseService(TB17_DG01);
			bsDG11 = getBaseService(TB17_DG11);
			bsDG0N = getBaseService(TB17_DG0N);
			bsDG1N = getBaseService(TB17_DG1N);

			assertEquals("component reactivated", idDG01, bsDG01
					.getProperties().get(IDENTITY));
			assertEquals("service property incorrect", "unbindDG01/higher",
					bsDG01.getProperties().get(KEY));
			assertEquals("component reactivated", idDG11, bsDG11
					.getProperties().get(IDENTITY));
			assertEquals("service property incorrect", "unbindDG11/higher",
					bsDG11.getProperties().get(KEY));
			assertEquals("component reactivated", idDG0N, bsDG0N
					.getProperties().get(IDENTITY));
			assertEquals("service property incorrect",
					"bindDG0N/higher/initial", bsDG0N.getProperties().get(KEY));
			assertEquals("component reactivated", idDG1N, bsDG1N
					.getProperties().get(IDENTITY));
			assertEquals("service property incorrect",
					"bindDG1N/higher/initial", bsDG1N.getProperties().get(KEY));

		}
		finally {
			uninstallBundle(tb17);
			if (regHigher != null) {
				regHigher.unregister();
			}
			if (regInitial != null) {
				regInitial.unregister();
			}
		}
	}

	/**
	 * Searches for component with name componentName which provides
	 * BaseService. Returns value of its "config.base.data" property.
	 *
	 * @param componentName
	 *            - the name of the component to get data
	 * @return the value of property "config.base.data", provided by
	 *         BaseService.getProperties(). Returned value is -1 when component
	 *         which provides BaseService and has specified name is not
	 *         activated. Returned value is 0 when component with specified name
	 *         is active but doesn't have property "config.base.data".
	 */
	private int getBaseConfigData(String componentName) {
		BaseService s = getBaseService(componentName);
		return getBaseConfigData(s);
	}

	private int getBaseConfigData(BaseService s) {
		Dictionary props = null;
		int value = -1;
		if (s != null) {
			value = 0;
			if ((props = s.getProperties()) != null) {
				Object prop = props.get("config.base.data");
				if (prop instanceof Integer) {
					value = ((Integer) prop).intValue();
				}
			}
		}
		return value;
	}

	private BaseService getBaseService(String componentName) {
		Object[] services = trackerBaseService.getServices();
		if (services == null) {
			return null;
		}
		for (int i = 0; i < services.length; i++) {
			if (services[i] instanceof BaseService) {
				BaseService s = (BaseService) services[i];
				Dictionary props = s.getProperties();
				if (props != null
						&& ((String) props
								.get(ComponentConstants.COMPONENT_NAME))
								.equals(componentName)) {
					return s;
				}
			}
		}
		return null;
	}

	private boolean checkAvailability(String service) {
		BundleContext bc = getContext();
		ServiceReference ref = bc.getServiceReference(service);
		return ref != null;
	}

	public void sleep0(long millisToSleep) {
		long start = System.currentTimeMillis();
		do {
			try {
				Sleep.sleep(50);
			} catch (InterruptedException e) {
			}
		} while (System.currentTimeMillis() - start < millisToSleep);
	}

	private void waitBundleStart() {
		if (!synchronousBuild) {
			sleep0(2 * SLEEP);
		}
	}

}

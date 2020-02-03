/*
 * Copyright (c) OSGi Alliance (2004, 2019). All Rights Reserved.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.osgi.test.cases.component.junit.DTOUtil.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.PrototypeServiceFactory;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.osgi.framework.dto.ServiceReferenceDTO;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.namespace.extender.ExtenderNamespace;
import org.osgi.namespace.service.ServiceNamespace;
import org.osgi.resource.Namespace;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentConfigurationDTO;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;
import org.osgi.service.component.runtime.dto.ReferenceDTO;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;
import org.osgi.test.cases.component.service.BaseService;
import org.osgi.test.cases.component.service.ComponentContextExposer;
import org.osgi.test.cases.component.service.ComponentEnabler;
import org.osgi.test.cases.component.service.DSTestConstants;
import org.osgi.test.cases.component.service.MultipleFieldTestService;
import org.osgi.test.cases.component.service.ScalarFieldTestService;
import org.osgi.test.cases.component.service.ServiceProvider;
import org.osgi.test.cases.component.service.ServiceReceiver;
import org.osgi.test.cases.component.service.TBCService;
import org.osgi.test.cases.component.service.TestIdentitySet;
import org.osgi.test.cases.component.service.TestList;
import org.osgi.test.cases.component.service.TestObject;
import org.osgi.test.cases.component.service.TestSet;
import org.osgi.test.cases.component.tb13.ModifyRegistrator;
import org.osgi.test.cases.component.tb13a.ModifyRegistrator2;
import org.osgi.test.cases.component.tb6.ActDeactComponent;
import org.osgi.test.support.MockFactory;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.test.support.sleep.Sleep;
import org.osgi.util.promise.Promise;
import org.osgi.util.tracker.ServiceTracker;

/**
 * This is the bundle initially installed and started by the TestCase. It
 * performs the test methods of the declarative services test case.
 *
 * @author $Id$
 */
public class DeclarativeServicesControl extends DefaultTestBundleControl
		implements LogListener {

	private static final String	TEST_CASE_ROOT		= "org.osgi.test.cases.component";
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

	private ServiceTracker<ServiceProvider,ServiceProvider>					trackerProvider;
	private ServiceTracker<Object,Object>										trackerConsumerLookup;
	private ServiceTracker<Object,Object>										trackerDyn;
	private ServiceTracker<Object,Object>										trackerConsumerEvent;
	private ServiceTracker<Object,Object>										trackerNamedService;
	private ServiceTracker<ComponentFactory<Object>,ComponentFactory<Object>>	trackerNamedServiceFactory;
	private ServiceTracker<ConfigurationAdmin, ConfigurationAdmin>	trackerCM;
	private ServiceTracker<BaseService, BaseService>	trackerBaseService;
	private ServiceTracker<ServiceComponentRuntime, ServiceComponentRuntime>	scrTracker;

	protected void setUp() throws Exception {
		String sleepTimeString = getProperty("osgi.tc.component.sleeptime");
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
		trackerCM = new ServiceTracker<ConfigurationAdmin, ConfigurationAdmin>(bc, ConfigurationAdmin.class,
				null);
		trackerCM.open();
	    // clear component configurations
 	    clearConfigurations();

		scrTracker = new ServiceTracker<ServiceComponentRuntime, ServiceComponentRuntime>(bc,
				ServiceComponentRuntime.class, null);
		scrTracker.open();

		// install test cases
		tb1 = installBundle("tb1.jar", false);
		tb2 = installBundle("tb2.jar", false);
		tb3 = installBundle("tb3.jar", false);

		// start them
		tb1.start();
		tb2.start();
		tb3.start();

		// init trackers
		trackerProvider = new ServiceTracker<>(bc, ServiceProvider.class, null);
		trackerConsumerLookup = new ServiceTracker<>(bc, LOOKUP_CLASS, null);
		trackerDyn = new ServiceTracker<>(bc, DYN_CLASS, null);
		trackerConsumerEvent = new ServiceTracker<>(bc, EVENT_CLASS, null);
		trackerNamedService = new ServiceTracker<>(bc, NAMED_CLASS, null);
		Filter filter = bc.createFilter("(&("
				+ ComponentConstants.COMPONENT_FACTORY + '=' + NAMED_CLASS
				+ ")(" + Constants.OBJECTCLASS + '='
				+ ComponentFactory.class.getName() + "))");
		trackerNamedServiceFactory = new ServiceTracker<>(bc, filter, null);
		trackerBaseService = new ServiceTracker<BaseService, BaseService>(bc, BaseService.class, null);

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
		scrTracker.close();
	}

	/**
	 * This methods takes care of the configurations related to this test
	 *
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 */
	private void clearConfigurations() throws Exception {
		ConfigurationAdmin cm = trackerCM.getService();
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

		serviceProvider = trackerProvider.getService();
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
		serviceProvider = trackerProvider.getService();
		assertNull("ServiceProvider service should not be registered",
				serviceProvider);

		tb1 = installBundle("tb1.jar", false);
		Sleep.sleep(SLEEP);
		serviceProvider = trackerProvider.getService();
		assertTrue("ServiceProvider bundle should be in resolved state", (tb1
				.getState() & (Bundle.RESOLVED | Bundle.INSTALLED)) != 0);
		assertNull("ServiceProvider service should not be registered",
				serviceProvider);

		tb1.start();
		Sleep.sleep(SLEEP);
		serviceProvider = trackerProvider.getService();
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
		TBCService serviceProvider = trackerProvider.getService();
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
		Dictionary<String,Object> properties = serviceConsumerLookup
				.getProperties();
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
		assertNotNull(
				"Please set the system property 'scr.bundle.name' to the Bundle-SymbolicName of the SCR implementation bundle being tested.",
				scr);

		ServiceReference< ? > refs[];
		BundleContext bc = getContext();
		String filter = "(" + ComponentConstants.COMPONENT_NAME + "=*)";

		// preserve the count of the registered components
		// after SRC is started again, the same number of components
		// must be registered
		refs = bc.getServiceReferences((String) null, filter);
		int count = (refs == null) ? 0 : refs.length;

		scr.stop();
		Sleep.sleep(SLEEP * 2);

		try {
			refs = bc.getServiceReferences((String) null, filter);
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

		refs = bc.getServiceReferences((String) null, filter);
		assertEquals(
				"The Service Component Runtime must start all components that are installed prior it",
				count, (refs == null) ? 0 : refs.length);
	}

	public void testSCRExtenderCapability() throws Exception {
		Bundle scr = getSCRBundle();
		assertNotNull(
				"Please set the system property 'scr.bundle.name' to the Bundle-SymbolicName of the SCR implementation bundle being tested.",
				scr);
		BundleWiring wiring = scr.adapt(BundleWiring.class);
		assertNotNull("No BundleWiring available for SCR bundle", wiring);
		boolean found = false;
		List<BundleCapability> extenders = wiring
				.getCapabilities(ExtenderNamespace.EXTENDER_NAMESPACE);
		for (BundleCapability extender : extenders) {
			if ("osgi.component".equals(extender.getAttributes().get(
					ExtenderNamespace.EXTENDER_NAMESPACE))) {
				found = true;
				assertEquals(
						"osgi.extender capability version wrong",
						new Version(1, 4, 0),
						extender.getAttributes()
								.get(
								ExtenderNamespace.CAPABILITY_VERSION_ATTRIBUTE));
				String uses = extender.getDirectives().get(
						Namespace.CAPABILITY_USES_DIRECTIVE);
				assertNotNull(
						"osgi.extender capability uses directive missing", uses);
				boolean usefound = false;
				for (String use : uses.split("\\s*,\\s*")) {
					if ("org.osgi.service.component".equals(use)) {
						usefound = true;
						break;
					}
				}
				assertTrue("osgi.extender capability missing package in uses",
						usefound);
				break;
			}
		}
		assertTrue("missing osgi.extender capability for osgi.component", found);
	}

	public void testSCRServiceCapability() throws Exception {
		Bundle scr = getSCRBundle();
		assertNotNull(
				"Please set the system property 'scr.bundle.name' to the Bundle-SymbolicName of the SCR implementation bundle being tested.",
				scr);
		BundleWiring wiring = scr.adapt(BundleWiring.class);
		assertNotNull("No BundleWiring available for SCR bundle", wiring);
		boolean found = false;
		List<BundleCapability> services = wiring
				.getCapabilities(ServiceNamespace.SERVICE_NAMESPACE);
		for (BundleCapability service : services) {
			@SuppressWarnings("unchecked")
			List<String> objectClass = (List<String>) service.getAttributes()
					.get(ServiceNamespace.CAPABILITY_OBJECTCLASS_ATTRIBUTE);
			if (objectClass.contains(ServiceComponentRuntime.class.getName())) {
				found = true;
				String uses = service.getDirectives().get(
						Namespace.CAPABILITY_USES_DIRECTIVE);
				assertNotNull("osgi.service capability uses directive missing",
						uses);
				boolean usefound = false;
				for (String use : uses.split("\\s*,\\s*")) {
					if ("org.osgi.service.component.runtime".equals(use)) {
						usefound = true;
						break;
					}
				}
				assertTrue("osgi.service capability missing package in uses",
						usefound);
				break;
			}
		}
		assertTrue("missing osgi.service capability for "
				+ ServiceComponentRuntime.class.getName(), found);
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

		Hashtable<String,Object> props;
		int oldCount = trackerNamedService.getTrackingCount();
		ComponentFactory<Object> factory = trackerNamedServiceFactory
				.getService();

		// create the first service
		props = new Hashtable<>();
		props.put("name", "hello");
		factory.newInstance(props);

		assertEquals("The instances tracking count should be increased by one",
				oldCount + 1, trackerNamedService.getTrackingCount());
		boolean serviceFound = false;
		// find the service, the reference must have the properties set
		// correctly
		ServiceReference<Object> refs[] = trackerNamedService
				.getServiceReferences();
		for (int i = 0; refs != null && i < refs.length; i++) {
			ServiceReference<Object> current = refs[i];
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
		props = new Hashtable<>();
		props.put("name", "world");
		ComponentInstance<Object> worldInstance = factory.newInstance(props);
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

	@SuppressWarnings("deprecation")
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
		LogReaderService logService = getService(LogReaderService.class);
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
		ServiceReference< ? > ref;
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
		String bundleName = getProperty("scr.bundle.name");
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
		Dictionary<String,Object> props = serviceConsumerEvent.getProperties();
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

		ComponentFactory<Object> factory = trackerNamedServiceFactory
				.getService();

		// create the first service!
		Hashtable<String,Object> props = new Hashtable<>();
		props.put("name", "hello");
		ComponentInstance<Object> instance1 = factory.newInstance(props);
		Sleep.sleep(SLEEP);

		assertEquals("The component should be bound to the first instance", 1,
				getCount());

		props = new Hashtable<>();
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
		ConfigurationAdmin cm = trackerCM.getService();
		assertNotNull("The ConfigurationAdmin should be available", cm);
		ServiceReference<Object> ref;
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
		Hashtable<String,Object> props = new Hashtable<>(10);
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

		ConfigurationAdmin cm = trackerCM.getService();
		assertNotNull("The ConfigurationAdmin should be available", cm);
		Configuration config1;
		Configuration config2;
		Hashtable<String,Object> props;

		// create a new configuration
		config1 = cm.createFactoryConfiguration(EVENT_CLASS, null);
		props = new Hashtable<>(2);
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
		props = new Hashtable<>(2);
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
		ConfigurationAdmin cm = trackerCM.getService();
		assertNotNull("The ConfigurationAdmin should be available", cm);

		Bundle tb5 = installBundle("tb5.jar", false);
		tb5.start();
		waitBundleStart();

		Hashtable<String,Object> props = new Hashtable<>(10);
		props.put("config.base.data", Integer.valueOf(1));

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
		ConfigurationAdmin cm = trackerCM.getService();
		assertNotNull("The ConfigurationAdmin should be available", cm);

		Bundle tb5 = installBundle("tb5.jar", false);
		tb5.start();
		waitBundleStart();

		Hashtable<String,Object> props = new Hashtable<>(10);
		props.put("config.base.data", Integer.valueOf(1));

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
		log("install tb6");
		Bundle tb6 = installBundle("tb6.jar", false);
		log("installed tb6");
		log("start tb6");
		tb6.start();
		log("started tb6");
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
		final String CONT_EXP = TEST_CASE_ROOT + ".tb6.ContExp";

		BaseService bs = getBaseService(NOTSET_NS100);
		ComponentContext cc = (bs instanceof ComponentContextExposer) ? ((ComponentContextExposer) bs)
				.getComponentContext()
				: null;
		assertNotNull("Component context should be available", cc);

		assertTrue(
				"Activate method of " + NOTSET_NS100 + " should be called",
				checkDataBits(ActDeactComponent.ACTIVATE_CC,
						getBaseConfigData(bs)));
		log("disable " + NOTSET_NS100);
		cc.disableComponent(NOTSET_NS100);
		log("disabled " + NOTSET_NS100);
		Sleep.sleep(SLEEP * 3);
		assertTrue(
				"Deactivate method of " + NOTSET_NS100 + " should be called",
				checkDataBits(ActDeactComponent.DEACTIVATE_CC,
						getBaseConfigData(bs)));

		bs = getBaseService(NOTSET_NS110);
		assertNotNull("bs should not be null", bs);
		assertTrue(
				"Activate method of " + NOTSET_NS110 + " should be called",
				checkDataBits(ActDeactComponent.ACTIVATE_CC,
						getBaseConfigData(bs)));
		log("disable " + NOTSET_NS110);
		cc.disableComponent(NOTSET_NS110);
		log("disabled " + NOTSET_NS110);
		Sleep.sleep(SLEEP * 3);
		assertTrue(
				"Deactivate method of " + NOTSET_NS110 + " should be called",
				checkDataBits(ActDeactComponent.DEACTIVATE_CC,
						getBaseConfigData(bs)));

		bs = getBaseService(NOARGS_NS100); // INVALID COMPONENT FOR XML NS 1.0.0
		assertEquals("Component " + NOARGS_NS100 + " should not be activated",
				-1, getBaseConfigData(bs));

		bs = getBaseService(NOARGS_NS110);
		assertNotNull("bs should not be null", bs);
		assertTrue(
				"Activate method of " + NOARGS_NS110 + " should be called",
				checkDataBits(ActDeactComponent.ACT, getBaseConfigData(bs)));
		log("disable " + NOARGS_NS110);
		cc.disableComponent(NOARGS_NS110);
		log("disabled " + NOARGS_NS110);
		Sleep.sleep(SLEEP * 3);
		assertTrue(
				"Deactivate method of " + NOARGS_NS110 + " should be called",
				checkDataBits(ActDeactComponent.DEACT, getBaseConfigData(bs)));

		bs = getBaseService(CC_NS100); // INVALID COMPONENT FOR XML NS 1.0.0
		assertEquals("Component " + CC_NS100 + " should not be activated", -1,
				getBaseConfigData(bs));

		bs = getBaseService(CC_NS110);
		assertNotNull("bs should not be null", bs);
		assertTrue("Activate method of " + CC_NS110 + " should be called",
				checkDataBits(ActDeactComponent.ACT_CC, getBaseConfigData(bs)));
		log("disable " + CC_NS110);
		cc.disableComponent(CC_NS110);
		log("disabled " + CC_NS110);
		Sleep.sleep(SLEEP * 3);
		assertTrue("Deactivate method of " + CC_NS110 + " should be called",
				checkDataBits(ActDeactComponent.DEACT_CC, getBaseConfigData(bs)));

		bs = getBaseService(BC_NS100); // INVALID COMPONENT FOR XML NS 1.0.0
		assertEquals("Component " + BC_NS100 + " should not be activated", -1,
				getBaseConfigData(bs));

		bs = getBaseService(BC_NS110);
		assertNotNull("bs should not be null", bs);
		assertTrue("Activate method of " + BC_NS110 + " should be called",
				checkDataBits(ActDeactComponent.ACT_BC, getBaseConfigData(bs)));
		log("disable " + BC_NS110);
		cc.disableComponent(BC_NS110);
		log("disabled " + BC_NS110);
		Sleep.sleep(SLEEP * 3);
		assertTrue("Deactivate method of " + BC_NS110 + " should be called",
				checkDataBits(ActDeactComponent.DEACT_BC, getBaseConfigData(bs)));

		bs = getBaseService(MAP_NS100); // INVALID COMPONENT FOR XML NS 1.0.0
		assertEquals("Component " + MAP_NS100 + " should not be activated", -1,
				getBaseConfigData(bs));

		bs = getBaseService(MAP_NS110);
		assertNotNull("bs should not be null", bs);
		assertTrue("Activate method of " + MAP_NS110 + " should be called",
				checkDataBits(ActDeactComponent.ACT_MAP, getBaseConfigData(bs)));
		log("disable " + MAP_NS110);
		cc.disableComponent(MAP_NS110);
		log("disabled " + MAP_NS110);
		Sleep.sleep(SLEEP * 3);
		assertTrue("Deactivate method of " + MAP_NS110 + " should be called",
				checkDataBits(ActDeactComponent.DEACT_MAP,
						getBaseConfigData(bs)));

		bs = getBaseService(CC_BC_MAP_NS100); // INVALID COMPONENT FOR XML NS
												// 1.0.0
		assertEquals("Component " + CC_BC_MAP_NS100
				+ " should not be activated", -1, getBaseConfigData(bs));

		bs = getBaseService(CC_BC_MAP_NS110);
		assertNotNull("bs should not be null", bs);
		assertTrue("Activate method of " + CC_BC_MAP_NS110
				+ " should be called",
				checkDataBits(ActDeactComponent.ACT_CC_BC_MAP,
						getBaseConfigData(bs)));
		log("disable " + CC_BC_MAP_NS110);
		cc.disableComponent(CC_BC_MAP_NS110);
		log("disabled " + CC_BC_MAP_NS110);
		Sleep.sleep(SLEEP * 3);
		assertTrue("Deactivate method of " + CC_BC_MAP_NS110
				+ " should be called",
				checkDataBits(ActDeactComponent.DEACT_CC_BC_MAP,
						getBaseConfigData(bs)));

		bs = getBaseService(INT_NS110);
		assertNotNull("bs should not be null", bs);
		log("disable " + INT_NS110);
		cc.disableComponent(INT_NS110);
		log("disabled " + INT_NS110);
		Sleep.sleep(SLEEP * 3);
		assertTrue("Deactivate method of " + INT_NS110 + " should be called",
				checkDataBits(ActDeactComponent.DEACT_INT,
						getBaseConfigData(bs)));

		bs = getBaseService(CC_BC_MAP_INT_NS110);
		assertNotNull("bs should not be null", bs);
		log("disable " + CC_BC_MAP_INT_NS110);
		cc.disableComponent(CC_BC_MAP_INT_NS110);
		log("disabled " + CC_BC_MAP_INT_NS110);
		Sleep.sleep(SLEEP * 3);
		long data = getBaseConfigData(bs);
		assertTrue("Deactivate method of " + CC_BC_MAP_INT_NS110
				+ " should be called",
				checkDataBits(ActDeactComponent.DEACT_CC_BC_MAP_INT, data));

		// // Testing Deactivation reasons ////
		assertEquals(
				"Deactivation reason shall be DEACTIVATION_REASON_DISABLED",
				ComponentConstants.DEACTIVATION_REASON_DISABLED,
				0xFF & (data >> 16));

		log("enable " + CC_BC_MAP_INT_NS110);
		cc.enableComponent(CC_BC_MAP_INT_NS110);
		log("enabled " + CC_BC_MAP_INT_NS110);
		Sleep.sleep(SLEEP * 3);
		bs = getBaseService(CC_BC_MAP_INT_NS110);
		assertNotNull("bs should not be null", bs);
		log("disable " + CONT_EXP);
		cc.disableComponent(CONT_EXP);
		log("disabled " + CONT_EXP);
		Sleep.sleep(SLEEP * 3);
		assertEquals(
				"Deactivation reason shall be DEACTIVATION_REASON_REFERENCE",
				ComponentConstants.DEACTIVATION_REASON_REFERENCE,
				0xFF & (getBaseConfigData(bs) >> 16));

		log("enable " + CONT_EXP);
		cc.enableComponent(CONT_EXP);
		log("enabled " + CONT_EXP);
		Sleep.sleep(SLEEP * 3);

		bs = getBaseService(CC_BC_MAP_INT_NS110);
		assertNotNull("bs should not be null", bs);
		ConfigurationAdmin cm = trackerCM.getService();
		assertNotNull("The ConfigurationAdmin should be available", cm);
		Configuration config = cm.getConfiguration(CC_BC_MAP_INT_NS110, null);
		Dictionary<String,Object> properties = config.getProperties();
		if (properties == null) {
			properties = new Hashtable<>();
		}
		properties.put("configuration.dummy", "dummy");
		log("configuration update " + CC_BC_MAP_INT_NS110);
		config.update(properties);
		log("configuration updated " + CC_BC_MAP_INT_NS110);
		Sleep.sleep(SLEEP * 3);
		assertEquals(
				"Deactivation reason shall be DEACTIVATION_REASON_CONFIGURATION_MODIFIED",
				ComponentConstants.DEACTIVATION_REASON_CONFIGURATION_MODIFIED,
				0xFF & (getBaseConfigData(bs) >> 16));

		log("enable " + CC_BC_MAP_INT_NS110);
		cc.enableComponent(CC_BC_MAP_INT_NS110);
		log("enabled " + CC_BC_MAP_INT_NS110);
		Sleep.sleep(SLEEP * 3);

		bs = getBaseService(CC_BC_MAP_INT_NS110);
		assertNotNull("bs should not be null", bs);
		config = cm.getConfiguration(CC_BC_MAP_INT_NS110, null);
		log("configuration delete " + CC_BC_MAP_INT_NS110);
		config.delete();
		log("configuration deleted " + CC_BC_MAP_INT_NS110);
		Sleep.sleep(SLEEP * 3);
		assertEquals(
				"Deactivation reason shall be DEACTIVATION_REASON_CONFIGURATION_DELETED",
				ComponentConstants.DEACTIVATION_REASON_CONFIGURATION_DELETED,
				0xFF & (getBaseConfigData(bs) >> 16));

		log("enable " + CC_BC_MAP_INT_NS110);
		cc.enableComponent(CC_BC_MAP_INT_NS110);
		log("enabled " + CC_BC_MAP_INT_NS110);
		Sleep.sleep(SLEEP * 3);

		log("enable " + INT_NS110);
		cc.enableComponent(INT_NS110);
		log("enabled " + INT_NS110);
		Sleep.sleep(SLEEP * 3);

		bs = getBaseService(INT_NS110);
		assertNotNull("bs should not be null", bs);
		ComponentContext ccIntNS110 = (bs instanceof ComponentContextExposer) ? ((ComponentContextExposer) bs)
				.getComponentContext()
				: null;
		assertNotNull("Component context should be available for " + INT_NS110,
				ccIntNS110);
		log("dispose " + INT_NS110);
		ccIntNS110.getComponentInstance().dispose();
		log("disposed " + INT_NS110);
		Sleep.sleep(SLEEP * 3);
		assertEquals(
				"Deactivation reason shall be DEACTIVATION_REASON_DISPOSED",
				ComponentConstants.DEACTIVATION_REASON_DISPOSED,
				0xFF & (getBaseConfigData(bs) >> 16));

		bs = getBaseService(CONT_EXP);
		assertNotNull("bs should not be null", bs);
		log("stop tb6");
		tb6.stop();
		log("stopped tb6");
		Sleep.sleep(SLEEP * 3);
		assertEquals(
				"Deactivation reason shall be DEACTIVATION_REASON_BUNDLE_STOPPED",
				ComponentConstants.DEACTIVATION_REASON_BUNDLE_STOPPED,
				0xFF & (getBaseConfigData(bs) >> 16));

		log("uninstall tb6");
		uninstallBundle(tb6);
		log("uninstalled tb6");
	}

	public void testEventMethods110() throws Exception {
		final String SR_NS100 = TEST_CASE_ROOT + ".tb7.SrNS100";
		final String SR_NS110 = TEST_CASE_ROOT + ".tb7.SrNS110";
		final String CE_NS100 = TEST_CASE_ROOT + ".tb7.CeNS100";
		final String CE_NS110 = TEST_CASE_ROOT + ".tb7.CeNS110";
		final String CE_MAP_NS100 = TEST_CASE_ROOT + ".tb7.CeMapNS100";
		final String CE_MAP_NS110 = TEST_CASE_ROOT + ".tb7.CeMapNS110";

		Bundle tb7 = installBundle("tb7.jar", false);
		assertNotNull("tb7 failed to install", tb7);

		try {
			tb7.start();
			Sleep.sleep(SLEEP * 3);

			ServiceReference<ComponentEnabler> ref = getContext()
					.getServiceReference(ComponentEnabler.class);
			assertNotNull(
					"Component Enabler Service Reference should be available",
					ref);
			ComponentEnabler enabler = getContext()
					.getService(ref);
			assertNotNull("Component Enabler Service should be available",
					enabler);

			try {
				BaseService bs = getBaseService(SR_NS100);
				assertNotNull("Component " + SR_NS100 + " should be activated",
						bs);
				assertTrue(
						"Bind method of " + SR_NS100 + " should be called",
						checkDataBits(DSTestConstants.BIND_1,
								getBaseConfigData(bs)));
				enabler.enableComponent(SR_NS100, false);
				Sleep.sleep(SLEEP * 3);
				assertTrue(
						"Unbind method of " + SR_NS100 + " should be called",
						checkDataBits(DSTestConstants.UNBIND_1,
								getBaseConfigData(bs)));

				bs = getBaseService(SR_NS110);
				assertNotNull("Component " + SR_NS110 + " should be activated",
						bs);
				assertTrue(
						"Bind method of " + SR_NS110 + " should be called",
						checkDataBits(DSTestConstants.BIND_1,
								getBaseConfigData(bs)));
				enabler.enableComponent(SR_NS110, false);
				Sleep.sleep(SLEEP * 3);
				assertTrue(
						"Unbind method of " + SR_NS110 + " should be called",
						checkDataBits(DSTestConstants.UNBIND_1,
								getBaseConfigData(bs)));

				bs = getBaseService(CE_NS100);
				assertNotNull("Component " + CE_NS100 + " should be activated",
						bs);
				assertTrue(
						"Bind method of " + CE_NS100 + " should be called",
						checkDataBits(DSTestConstants.BIND_2,
								getBaseConfigData(bs)));
				enabler.enableComponent(CE_NS100, false);
				Sleep.sleep(SLEEP * 3);
				assertTrue(
						"Unbind method of " + CE_NS100 + " should be called",
						checkDataBits(DSTestConstants.UNBIND_2,
								getBaseConfigData(bs)));

				bs = getBaseService(CE_NS110);
				assertNotNull("Component " + CE_NS110 + " should be activated",
						bs);
				assertTrue(
						"Bind method of " + CE_NS110 + " should be called",
						checkDataBits(DSTestConstants.BIND_2,
								getBaseConfigData(bs)));
				enabler.enableComponent(CE_NS110, false);
				Sleep.sleep(SLEEP * 3);
				assertTrue(
						"Unbind method of " + CE_NS110 + " should be called",
						checkDataBits(DSTestConstants.UNBIND_2,
								getBaseConfigData(bs)));

				bs = getBaseService(CE_MAP_NS100);
				assertNotNull("Component " + CE_MAP_NS100
						+ " should be activated", bs);
				assertFalse(
						"Bind method of " + CE_MAP_NS100
								+ " should not be called",
						checkDataBits(DSTestConstants.BIND_3,
								getBaseConfigData(bs)));

				enabler.enableComponent(CE_MAP_NS100, false);
				Sleep.sleep(SLEEP * 3);
				assertFalse(
						"Unbind method of " + CE_MAP_NS100
								+ " should not be called",
						checkDataBits(DSTestConstants.UNBIND_3,
								getBaseConfigData(bs)));

				bs = getBaseService(CE_MAP_NS110);
				assertNotNull("Component " + CE_MAP_NS110
						+ " should be activated", bs);
				assertTrue(
						"Bind method of " + CE_MAP_NS110 + " should be called",
						checkDataBits(DSTestConstants.BIND_3,
								getBaseConfigData(bs)));
				enabler.enableComponent(CE_MAP_NS110, false);
				Sleep.sleep(SLEEP * 3);
				assertTrue(
						"Unbind method of " + CE_MAP_NS110
								+ " should be called",
						checkDataBits(DSTestConstants.UNBIND_3,
								getBaseConfigData(bs)));
			}
			finally {
				getContext().ungetService(ref);
			}
		}
		finally {
			uninstallBundle(tb7);
		}
	}

	public void testEventMethods130() throws Exception {

		Bundle tb18 = installBundle("tb18.jar", false);
		assertNotNull("tb18 failed to install", tb18);
		
		try {
			tb18.start();
			Sleep.sleep(SLEEP * 3);

			ServiceReference<ComponentEnabler> ref = getContext()
					.getServiceReference(ComponentEnabler.class);
			assertNotNull(
					"Component Enabler Service Reference should be available",
					ref);
			ComponentEnabler enabler = getContext()
					.getService(ref);
			assertNotNull("Component Enabler Service should be available",
					enabler);

			try {
				final String COMPONENT_1 = TEST_CASE_ROOT + ".tb18.1";
				BaseService bs = getBaseService(COMPONENT_1);
				assertNotNull("Component " + COMPONENT_1 + " should be activated", bs);
				assertFalse(
						"Wrong bind method of " + COMPONENT_1 + " called",
						checkDataBits(DSTestConstants.ERROR_1,
								getBaseConfigData(bs)));
				assertTrue(
						"Bind method of " + COMPONENT_1 + " should be called",
						checkDataBits(DSTestConstants.BIND_1,
								getBaseConfigData(bs)));
				enabler.enableComponent(COMPONENT_1, false);
				Sleep.sleep(SLEEP * 3);
				assertFalse(
						"Wrong unbind method of " + COMPONENT_1 + " called",
						checkDataBits(DSTestConstants.ERROR_1,
								getBaseConfigData(bs)));
				assertTrue(
						"Unbind method of " + COMPONENT_1 + " should be called",
						checkDataBits(DSTestConstants.UNBIND_1,
								getBaseConfigData(bs)));

				final String COMPONENT_2 = TEST_CASE_ROOT + ".tb18.2";
				bs = getBaseService(COMPONENT_2);
				assertNotNull("Component " + COMPONENT_2 + " should be activated", bs);
				assertFalse(
						"Wrong bind method of " + COMPONENT_2 + " called",
						checkDataBits(DSTestConstants.ERROR_2,
								getBaseConfigData(bs)));
				assertTrue(
						"Bind method of " + COMPONENT_2 + " should be called",
						checkDataBits(DSTestConstants.BIND_2,
								getBaseConfigData(bs)));
				enabler.enableComponent(COMPONENT_2, false);
				Sleep.sleep(SLEEP * 3);
				assertFalse(
						"Wrong unbind method of " + COMPONENT_2 + " called",
						checkDataBits(DSTestConstants.ERROR_2,
								getBaseConfigData(bs)));
				assertTrue(
						"Unbind method of " + COMPONENT_2 + " should be called",
						checkDataBits(DSTestConstants.UNBIND_2,
								getBaseConfigData(bs)));

				final String COMPONENT_3 = TEST_CASE_ROOT + ".tb18.3";
				bs = getBaseService(COMPONENT_3);
				assertNotNull("Component " + COMPONENT_3 + " should be activated",
						bs);
				assertTrue(
						"Bind method of " + COMPONENT_3 + " should be called",
						checkDataBits(DSTestConstants.BIND_3,
								getBaseConfigData(bs)));
				enabler.enableComponent(COMPONENT_3, false);
				Sleep.sleep(SLEEP * 3);
				assertTrue(
						"Unbind method of " + COMPONENT_3 + " should be called",
						checkDataBits(DSTestConstants.UNBIND_3,
								getBaseConfigData(bs)));

				final String COMPONENT_4 = TEST_CASE_ROOT + ".tb18.4";
				bs = getBaseService(COMPONENT_4);
				assertNotNull("Component " + COMPONENT_4
						+ " should be activated", bs);
				assertTrue(
						"Bind method of " + COMPONENT_4 + " should be called",
						checkDataBits(DSTestConstants.BIND_4,
								getBaseConfigData(bs)));
				enabler.enableComponent(COMPONENT_4, false);
				Sleep.sleep(SLEEP * 3);
				assertTrue(
						"Unbind method of " + COMPONENT_4 + " should be called",
						checkDataBits(DSTestConstants.UNBIND_4,
								getBaseConfigData(bs)));

				final String COMPONENT_5 = TEST_CASE_ROOT + ".tb18.5";
				bs = getBaseService(COMPONENT_5);
				assertNotNull("Component " + COMPONENT_5
						+ " should be activated", bs);
				assertFalse(
						"Wrong bind method of " + COMPONENT_5 + " called",
						checkDataBits(DSTestConstants.ERROR_5,
								getBaseConfigData(bs)));
				assertTrue(
						"Bind method of " + COMPONENT_5 + " should be called",
						checkDataBits(DSTestConstants.BIND_5,
								getBaseConfigData(bs)));
				enabler.enableComponent(COMPONENT_5, false);
				Sleep.sleep(SLEEP * 3);
				assertFalse(
						"Wrong bind method of " + COMPONENT_5 + " called",
						checkDataBits(DSTestConstants.ERROR_5,
								getBaseConfigData(bs)));
				assertTrue(
						"Unbind method of " + COMPONENT_5 + " should be called",
						checkDataBits(DSTestConstants.UNBIND_5,
								getBaseConfigData(bs)));

				final String COMPONENT_6 = TEST_CASE_ROOT + ".tb18.6";
				bs = getBaseService(COMPONENT_6);
				assertNotNull("Component " + COMPONENT_6
						+ " should be activated", bs);
				assertTrue(
						"Bind method of " + COMPONENT_6 + " should be called",
						checkDataBits(DSTestConstants.BIND_6,
								getBaseConfigData(bs)));
				enabler.enableComponent(COMPONENT_6, false);
				Sleep.sleep(SLEEP * 3);
				assertTrue(
						"Unbind method of " + COMPONENT_6 + " should be called",
						checkDataBits(DSTestConstants.UNBIND_6,
								getBaseConfigData(bs)));

				final String COMPONENT_7 = TEST_CASE_ROOT + ".tb18.7";
				bs = getBaseService(COMPONENT_7);
				assertNotNull("Component " + COMPONENT_7
						+ " should be activated", bs);
				assertFalse(
						"Wrong bind method of " + COMPONENT_7 + " called",
						checkDataBits(DSTestConstants.ERROR_7,
								getBaseConfigData(bs)));
				assertTrue(
						"Bind method of " + COMPONENT_7 + " should be called",
						checkDataBits(DSTestConstants.BIND_7,
								getBaseConfigData(bs)));
				enabler.enableComponent(COMPONENT_7, false);
				Sleep.sleep(SLEEP * 3);
				assertFalse(
						"Wrong unbind method of " + COMPONENT_7 + " called",
						checkDataBits(DSTestConstants.ERROR_7,
								getBaseConfigData(bs)));
				assertTrue(
						"Unbind method of " + COMPONENT_7 + " should be called",
						checkDataBits(DSTestConstants.UNBIND_7,
								getBaseConfigData(bs)));

				final String COMPONENT_8 = TEST_CASE_ROOT + ".tb18.8";
				bs = getBaseService(COMPONENT_8);
				assertNotNull("Component " + COMPONENT_8
						+ " should be activated", bs);
				assertFalse(
						"Wrong bind method of " + COMPONENT_8 + " called",
						checkDataBits(DSTestConstants.ERROR_8,
								getBaseConfigData(bs)));
				assertTrue(
						"Bind method of " + COMPONENT_8 + " should be called",
						checkDataBits(DSTestConstants.BIND_8,
								getBaseConfigData(bs)));
				enabler.enableComponent(COMPONENT_8, false);
				Sleep.sleep(SLEEP * 3);
				assertFalse(
						"Wrong unbind method of " + COMPONENT_8 + " called",
						checkDataBits(DSTestConstants.ERROR_8,
								getBaseConfigData(bs)));
				assertTrue(
						"Unbind method of " + COMPONENT_8 + " should be called",
						checkDataBits(DSTestConstants.UNBIND_8,
								getBaseConfigData(bs)));
			}
			finally {
				getContext().ungetService(ref);
			}
		}
		finally {
			uninstallBundle(tb18);
		}
	}
	
	public void testOptionalNames() throws Exception {
		Bundle tb8 = installBundle("tb8.jar", false);
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
		Bundle tb9 = installBundle("tb9.jar", false);
	    tb9.start();
	    waitBundleStart();

		final String WILD = TEST_CASE_ROOT + ".tb9.Wildcard";

	    // check that the both components are available
	    assertTrue("The first Wildcard component should be available", checkAvailability(WILD + "1"));
	    assertTrue("The second Wildcard component should be available", checkAvailability(WILD + "2"));

	    uninstallBundle(tb9);
	  }

	public void testDisposingMultipleDependencies() throws Exception {
		Bundle tb10 = installBundle("tb10.jar", false);
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
		Bundle tb11 = installBundle("tb11.jar", false);
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
		Bundle tb12 = installBundle("tb12.jar", false);
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
		ConfigurationAdmin cm = trackerCM.getService();
		assertNotNull("The ConfigurationAdmin should be available", cm);

		Bundle tb13 = installBundle("tb13.jar", false);

		Hashtable<String,Object> props = new Hashtable<>(10);
		props.put("config.dummy.data", Integer.valueOf(1));
		cm.getConfiguration(MOD_NOTSET_NS100, null).update(props);
		cm.getConfiguration(MOD_NOARGS_NS100, null).update(props);
		cm.getConfiguration(MOD_CC_NS100, null).update(props);
		cm.getConfiguration(MOD_BC_NS100, null).update(props);
		cm.getConfiguration(MOD_MAP_NS100, null).update(props);
		cm.getConfiguration(MOD_CC_BC_MAP_NS100, null).update(props);

		Sleep.sleep(SLEEP * 3);

		tb13.start();
		waitBundleStart();

		props.put("config.dummy.data", Integer.valueOf(2));
		Hashtable<String,Object> unsatisfyingProps = new Hashtable<>(10);
		unsatisfyingProps.put("ref.target", "(component.name=" + TEST_CASE_ROOT
				+ ".tb13.unexisting.provider)");

		BaseService bs = getBaseService(MOD_NOTSET_NS100);
		assertNotNull(bs);
		cm.getConfiguration(MOD_NOTSET_NS100, null).update(props);
		Sleep.sleep(SLEEP * 3);
		assertFalse("Modified method of " + MOD_NOTSET_NS100
				+ " should not be called",
				checkDataBits(ModifyRegistrator.MODIFIED, getBaseConfigData(bs)));
		assertTrue("Deactivate method of " + MOD_NOTSET_NS100
				+ " should be called",
				checkDataBits(ModifyRegistrator.DEACTIVATE,
						getBaseConfigData(bs)));
		bs = getBaseService(MOD_NOTSET_NS100);
		cm.getConfiguration(MOD_NOTSET_NS100, null).update(unsatisfyingProps);
		Sleep.sleep(SLEEP * 3);
		assertFalse("Modified method of " + MOD_NOTSET_NS100
				+ " should not be called",
				checkDataBits(ModifyRegistrator.MODIFIED, getBaseConfigData(bs)));
		assertTrue("Deactivate method of " + MOD_NOTSET_NS100
				+ " should be called",
				checkDataBits(ModifyRegistrator.DEACTIVATE,
						getBaseConfigData(bs)));

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
		ConfigurationAdmin cm = trackerCM.getService();
		assertNotNull("The ConfigurationAdmin should be available", cm);

		Bundle tb13a = installBundle("tb13a.jar", false);

		Hashtable<String,Object> props = new Hashtable<>(10);
		props.put("config.dummy.data", Integer.valueOf(1));
		cm.getConfiguration(MOD_NOTSET_NS110, null).update(props);
		cm.getConfiguration(MOD_NOARGS_NS110, null).update(props);
		cm.getConfiguration(MOD_CC_NS110, null).update(props);
		cm.getConfiguration(MOD_BC_NS110, null).update(props);
		cm.getConfiguration(MOD_MAP_NS110, null).update(props);
		cm.getConfiguration(MOD_CC_BC_MAP_NS110, null).update(props);

		Sleep.sleep(SLEEP * 3);

		tb13a.start();
		waitBundleStart();

		props.put("config.dummy.data", Integer.valueOf(2));
		Hashtable<String,Object> unsatisfyingProps = new Hashtable<>(10);
		unsatisfyingProps.put("ref.target", "(component.name=" + TEST_CASE_ROOT
				+ ".tb13.unexisting.provider)");

		BaseService bs = getBaseService(MOD_NOTSET_NS110);
		cm.getConfiguration(MOD_NOTSET_NS110, null).update(props);
		Sleep.sleep(SLEEP * 3);
		assertFalse("Modified method of " + MOD_NOTSET_NS110
				+ " should not be called",
				checkDataBits(ModifyRegistrator2.MODIFIED,
						getBaseConfigData(bs)));
		assertTrue("Deactivate method of " + MOD_NOTSET_NS110
				+ " should be called",
				checkDataBits(ModifyRegistrator2.DEACTIVATE,
						getBaseConfigData(bs)));
		bs = getBaseService(MOD_NOTSET_NS110);
		cm.getConfiguration(MOD_NOTSET_NS110, null).update(unsatisfyingProps);
		Sleep.sleep(SLEEP * 3);
		assertFalse("Modified method of " + MOD_NOTSET_NS110
				+ " should not be called",
				checkDataBits(ModifyRegistrator2.MODIFIED,
						getBaseConfigData(bs)));
		assertTrue("Deactivate method of " + MOD_NOTSET_NS110
				+ " should be called",
				checkDataBits(ModifyRegistrator2.DEACTIVATE,
						getBaseConfigData(bs)));
		// Re-activating
		bs = getBaseService(MOD_NOTSET_NS110);
		assertTrue("Activate method of " + MOD_NOTSET_NS110
				+ " should be called",
				checkDataBits(ModifyRegistrator2.ACTIVATE,
						getBaseConfigData(bs)));

		bs = getBaseService(MOD_NOARGS_NS110);
		cm.getConfiguration(MOD_NOARGS_NS110, null).update(props);
		Sleep.sleep(SLEEP * 3);
		assertTrue("Modified method of " + MOD_NOARGS_NS110
				+ " should be called",
				checkDataBits(ModifyRegistrator2.MOD, getBaseConfigData(bs)));
		assertFalse("Deactivate method of " + MOD_NOARGS_NS110
				+ " should not be called",
				checkDataBits(ModifyRegistrator2.DEACTIVATE,
						getBaseConfigData(bs)));
		cm.getConfiguration(MOD_NOARGS_NS110, null).update(unsatisfyingProps);
		Sleep.sleep(SLEEP * 3);
		assertTrue("Deactivate method of " + MOD_NOARGS_NS110
				+ " should be called",
				checkDataBits(ModifyRegistrator2.DEACTIVATE,
						getBaseConfigData(bs)));
		// Re-activating
		bs = getBaseService(MOD_NOARGS_NS110);
		assertTrue("Activate method of " + MOD_NOARGS_NS110
				+ " should be called",
				checkDataBits(ModifyRegistrator2.ACTIVATE,
						getBaseConfigData(bs)));

		bs = getBaseService(MOD_CC_NS110);
		cm.getConfiguration(MOD_CC_NS110, null).update(props);
		Sleep.sleep(SLEEP * 3);
		assertTrue(
				"Modified method of " + MOD_CC_NS110 + " should be called",
				checkDataBits(ModifyRegistrator2.MOD_CC, getBaseConfigData(bs)));
		assertFalse("Deactivate method of " + MOD_CC_NS110
				+ " should not be called",
				checkDataBits(ModifyRegistrator2.DEACTIVATE,
						getBaseConfigData(bs)));
		cm.getConfiguration(MOD_CC_NS110, null).update(unsatisfyingProps);
		Sleep.sleep(SLEEP * 3);
		assertTrue(
				"Deactivate method of " + MOD_CC_NS110 + " should be called",
				checkDataBits(ModifyRegistrator2.DEACTIVATE,
						getBaseConfigData(bs)));
		// Re-activating
		bs = getBaseService(MOD_CC_NS110);
		assertTrue(
				"Activate method of " + MOD_CC_NS110 + " should be called",
				checkDataBits(ModifyRegistrator2.ACTIVATE,
						getBaseConfigData(bs)));

		bs = getBaseService(MOD_BC_NS110);
		cm.getConfiguration(MOD_BC_NS110, null).update(props);
		Sleep.sleep(SLEEP * 3);
		assertTrue(
				"Modified method of " + MOD_BC_NS110 + " should be called",
				checkDataBits(ModifyRegistrator2.MOD_BC, getBaseConfigData(bs)));
		assertFalse("Deactivate method of " + MOD_BC_NS110
				+ " should not be called",
				checkDataBits(ModifyRegistrator2.DEACTIVATE,
						getBaseConfigData(bs)));
		cm.getConfiguration(MOD_BC_NS110, null).update(unsatisfyingProps);
		Sleep.sleep(SLEEP * 3);
		assertTrue(
				"Deactivate method of " + MOD_BC_NS110 + " should be called",
				checkDataBits(ModifyRegistrator2.DEACTIVATE,
						getBaseConfigData(bs)));
		// Re-activating
		bs = getBaseService(MOD_BC_NS110);
		assertTrue(
				"Activate method of " + MOD_BC_NS110 + " should be called",
				checkDataBits(ModifyRegistrator2.ACTIVATE,
						getBaseConfigData(bs)));

		bs = getBaseService(MOD_MAP_NS110);
		cm.getConfiguration(MOD_MAP_NS110, null).update(props);
		Sleep.sleep(SLEEP * 3);
		assertTrue("Modified method of " + MOD_MAP_NS110 + " should be called",
				checkDataBits(ModifyRegistrator2.MOD_MAP, getBaseConfigData(bs)));
		assertFalse("Deactivate method of " + MOD_MAP_NS110
				+ " should not be called",
				checkDataBits(ModifyRegistrator2.DEACTIVATE,
						getBaseConfigData(bs)));
		cm.getConfiguration(MOD_MAP_NS110, null).update(unsatisfyingProps);
		Sleep.sleep(SLEEP * 3);
		assertTrue("Deactivate method of " + MOD_MAP_NS110
				+ " should be called",
				checkDataBits(ModifyRegistrator2.DEACTIVATE,
						getBaseConfigData(bs)));
		// Re-activating
		bs = getBaseService(MOD_MAP_NS110);
		assertTrue("Activate method of " + MOD_MAP_NS110 + " should be called",
				checkDataBits(ModifyRegistrator2.ACTIVATE,
						getBaseConfigData(bs)));

		bs = getBaseService(MOD_CC_BC_MAP_NS110);
		cm.getConfiguration(MOD_CC_BC_MAP_NS110, null).update(props);
		Sleep.sleep(SLEEP * 3);
		assertTrue("Modified method of " + MOD_CC_BC_MAP_NS110
				+ " should be called",
				checkDataBits(ModifyRegistrator2.MOD_CC_BC_MAP,
						getBaseConfigData(bs)));
		assertFalse("Deactivate method of " + MOD_CC_BC_MAP_NS110
				+ " should not be called",
				checkDataBits(ModifyRegistrator2.DEACTIVATE,
						getBaseConfigData(bs)));
		cm.getConfiguration(MOD_CC_BC_MAP_NS110, null)
				.update(unsatisfyingProps);
		Sleep.sleep(SLEEP * 3);
		assertTrue("Deactivate method of " + MOD_CC_BC_MAP_NS110
				+ " should be called",
				checkDataBits(ModifyRegistrator2.DEACTIVATE,
						getBaseConfigData(bs)));
		// Re-activating
		bs = getBaseService(MOD_CC_BC_MAP_NS110);
		assertTrue("Activate method of " + MOD_CC_BC_MAP_NS110
				+ " should be called",
				checkDataBits(ModifyRegistrator2.ACTIVATE,
						getBaseConfigData(bs)));

		uninstallBundle(tb13a);
	}

	// Testing modified attribute - special cases
	public void testModifiedSpecialCases() throws Exception {
		ConfigurationAdmin cm = trackerCM.getService();
		assertNotNull("The ConfigurationAdmin should be available", cm);

		Bundle tb13a = installBundle("tb13a.jar", false);

		Hashtable<String,Object> props = new Hashtable<>(10);
		props.put("config.dummy.data", Integer.valueOf(1));
		cm.getConfiguration(MOD_CC_NS110, null).update(props);
		cm.getConfiguration(MOD_NOT_EXIST_NS110, null).update(props);
		cm.getConfiguration(MOD_THROW_EX_NS110, null).update(props);
		cm.getConfiguration(MOD_BC_NS110, null).update(props);
		Sleep.sleep(SLEEP * 3);

		log("\ntb13a starting");
		tb13a.start();
		waitBundleStart();

		// Verifying correctness of updated component properties
		BaseService bs = getBaseService(MOD_CC_NS110);
		props.put("config.dummy.data", Integer.valueOf(2));
		log("\n" + MOD_CC_NS110 + " config update");
		cm.getConfiguration(MOD_CC_NS110, null).update(props);
		Sleep.sleep(SLEEP * 3);
		Object val = ((ComponentContextExposer) bs).getComponentContext()
				.getProperties().get("config.dummy.data");
		assertTrue(
				"Modified method of " + MOD_CC_NS110 + " should be called",
				checkDataBits(ModifyRegistrator2.MOD_CC, getBaseConfigData(bs)));
		assertTrue("Component properties should be updated properly for "
				+ MOD_CC_NS110, (Integer.valueOf(2)).equals(val));

		// Specified modified method doesn't exist, deactivate() should be
		// called
		// instead of modified
		bs = getBaseService(MOD_NOT_EXIST_NS110);
		log("\n" + MOD_NOT_EXIST_NS110 + " config update");
		cm.getConfiguration(MOD_NOT_EXIST_NS110, null).update(props);
		Sleep.sleep(SLEEP * 3);
		assertTrue("Deactivate method of " + MOD_NOT_EXIST_NS110
				+ " should be called",
				checkDataBits(ModifyRegistrator2.DEACTIVATE,
						getBaseConfigData(bs)));
		// Re-activating
		bs = getBaseService(MOD_NOT_EXIST_NS110);
		assertTrue("Activate method of " + MOD_NOT_EXIST_NS110
				+ " should be called",
				checkDataBits(ModifyRegistrator2.ACTIVATE,
						getBaseConfigData(bs)));

		// Specified modified method throws exception. Normal workflow should
		// continue, deactivate() should not be called
		bs = getBaseService(MOD_THROW_EX_NS110);
		log("\n" + MOD_THROW_EX_NS110 + " config update");
		cm.getConfiguration(MOD_THROW_EX_NS110, null).update(props);
		Sleep.sleep(SLEEP * 3);
		assertFalse("Deactivate method of " + MOD_THROW_EX_NS110
				+ " should not be called",
				checkDataBits(ModifyRegistrator2.DEACTIVATE,
						getBaseConfigData(bs)));

		// Deleting component configuration
		bs = getBaseService(MOD_BC_NS110);
		log("\n" + MOD_BC_NS110 + " config delete");
		cm.getConfiguration(MOD_BC_NS110, null).delete();
		Sleep.sleep(SLEEP * 3);
		assertFalse("Modified method of " + MOD_BC_NS110
				+ " should not be called",
				checkDataBits(ModifyRegistrator2.MOD_BC, getBaseConfigData(bs)));
		assertTrue(
				"Deactivate method of " + MOD_BC_NS110 + " should be called",
				checkDataBits(ModifyRegistrator2.DEACTIVATE,
						getBaseConfigData(bs)));
		// Re-activating
		bs = getBaseService(MOD_BC_NS110);
		assertTrue(
				"Activate method of " + MOD_BC_NS110 + " should be called",
				checkDataBits(ModifyRegistrator2.ACTIVATE,
						getBaseConfigData(bs)));

		log("\ntb13a stopping");
		uninstallBundle(tb13a);
	}

	public void testPrivateProperties() throws Exception {
		Bundle tb14 = installBundle("tb14.jar", false);
		tb14.start();
		waitBundleStart();

		final String COMP = TEST_CASE_ROOT + ".tb14.component";

		ServiceReference<BaseService> ref = trackerBaseService
				.getServiceReference();
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
		Bundle tb15 = installBundle("tb15.jar", false);
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
			Dictionary<String,Object> props = new Hashtable<>();
			props.put(KEY, "1");
			ServiceRegistration<TestObject> reg = getContext()
					.registerService(TestObject.class, service, props);
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
		ConfigurationAdmin cm = trackerCM.getService();
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
		Dictionary<String,Object> props = new Hashtable<>();
		props.put(KEY, "config1");
		config.update(props);
		config = cm.getConfiguration(TB16_REQUIRED, null);
		props = new Hashtable<>();
		props.put(KEY, "bad1");
		config.update(props);
		config = cm.getConfiguration(TB16_NOTPRESENT, null);
		props = new Hashtable<>();
		props.put(KEY, "bad2");
		config.update(props);

		Bundle tb16 = installBundle("tb16.jar", false);
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
			assertEquals("service.pid property wrong", PID, bsRequired.getProperties().get(Constants.SERVICE_PID));

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
		Dictionary<String,Object> serviceProps = new Hashtable<>();
		serviceProps.put(KEY, "initial");
		ServiceRegistration<TestObject> regInitial = getContext()
				.registerService(TestObject.class, service, serviceProps);
		ServiceRegistration<TestObject> regHigher = null;

		Bundle tb17 = installBundle("tb17.jar", false);
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
			serviceProps.put(Constants.SERVICE_RANKING, Integer.valueOf(100));
			regHigher = getContext().registerService(
					TestObject.class, service, serviceProps);
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
		Dictionary<String,Object> serviceProps = new Hashtable<>();
		serviceProps.put(KEY, "initial");
		ServiceRegistration<TestObject> regInitial = getContext()
				.registerService(TestObject.class, service, serviceProps);
		ServiceRegistration<TestObject> regHigher = null;

		Bundle tb17 = installBundle("tb17.jar", false);
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
			serviceProps.put(Constants.SERVICE_RANKING, Integer.valueOf(100));
			regHigher = getContext().registerService(
					TestObject.class, service, serviceProps);
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
		Dictionary<String,Object> serviceProps = new Hashtable<>();
		serviceProps.put(KEY, "initial");
		ServiceRegistration<TestObject> regInitial = getContext()
				.registerService(TestObject.class, service, serviceProps);
		ServiceRegistration<TestObject> regHigher = null;

		Bundle tb17 = installBundle("tb17.jar", false);
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
			serviceProps.put(Constants.SERVICE_RANKING, Integer.valueOf(100));
			regHigher = getContext().registerService(
					TestObject.class, service, serviceProps);
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
		Dictionary<String,Object> serviceProps = new Hashtable<>();
		serviceProps.put(KEY, "initial");
		ServiceRegistration<TestObject> regInitial = getContext()
				.registerService(TestObject.class, service, serviceProps);
		ServiceRegistration<TestObject> regHigher = null;

		Bundle tb17 = installBundle("tb17.jar", false);
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
			serviceProps.put(Constants.SERVICE_RANKING, Integer.valueOf(100));
			regHigher = getContext().registerService(
					TestObject.class, service, serviceProps);
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
	private long getBaseConfigData(String componentName) {
		BaseService s = getBaseService(componentName);
		return getBaseConfigData(s);
	}

	private long getBaseConfigData(BaseService s) {
		Dictionary<String,Object> props = null;
		long value = -1L;
		if (s != null) {
			value = 0;
			if ((props = s.getProperties()) != null) {
				Object prop = props.get("config.base.data");
				if (prop instanceof Integer) {
					value = ((Integer) prop).longValue();
				}
				else
					if (prop instanceof Long) {
						value = ((Long) prop).longValue();

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
				Dictionary<String,Object> props = s.getProperties();
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
		ServiceReference< ? > ref = bc.getServiceReference(service);
		return ref != null;
	}

	private static boolean checkDataBits(long bits, long data) {
		return bits == (bits & data);
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

	public void testScopedServiceSingleton130() throws Exception {
		Bundle tb19 = installBundle("tb19.jar", false);
		assertNotNull("tb19 failed to install", tb19);
		
		try {
			tb19.start();

			Filter baseFilter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + BaseService.class.getName()
					+ ")(" + ComponentConstants.COMPONENT_NAME + "=org.osgi.test.cases.component.tb19.Singleton))");
			Filter receiverFilter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ServiceReceiver.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb19.SingletonReceiver))");
			ServiceTracker<BaseService, BaseService> baseTracker = new ServiceTracker<BaseService, BaseService>(
					getContext(), baseFilter, null);
			ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>> receiverTracker = new ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>>(
					getContext(), receiverFilter, null);
			try {
				baseTracker.open();
				receiverTracker.open();
				BaseService s = baseTracker.waitForService(SLEEP * 3);
				assertNotNull("missing service", s);
				ServiceReceiver<BaseService> r = receiverTracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver", r);
				assertSame("singleton service is not the same object", s, r.getServices().get(0));
			}
			finally {
				baseTracker.close();
				receiverTracker.close();
			}
		}
		finally {
			uninstallBundle(tb19);
		}
	}

	public void testScopedServiceBundle130() throws Exception {
		Bundle tb19 = installBundle("tb19.jar", false);
		assertNotNull("tb19 failed to install", tb19);

		try {
			tb19.start();

			Filter baseFilter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + BaseService.class.getName()
					+ ")(" + ComponentConstants.COMPONENT_NAME + "=org.osgi.test.cases.component.tb19.Bundle))");
			Filter receiverFilter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ServiceReceiver.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb19.BundleReceiver))");
			ServiceTracker<BaseService, BaseService> baseTracker = new ServiceTracker<BaseService, BaseService>(
					getContext(), baseFilter, null);
			ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>> receiverTracker = new ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>>(
					getContext(), receiverFilter, null);
			try {
				baseTracker.open();
				receiverTracker.open();
				BaseService s = baseTracker.waitForService(SLEEP * 3);
				assertNotNull("missing service", s);
				ServiceReceiver<BaseService> r = receiverTracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver", r);
				assertNotSame("bundle service is the same object", s, r.getServices().get(0));
				assertEquals("scope not bundle", Constants.SCOPE_BUNDLE,
						r.getServicesProperies().get(0).get(Constants.SERVICE_SCOPE));
				assertEquals("scope not bundle", Constants.SCOPE_BUNDLE,
						baseTracker.getServiceReference().getProperty(Constants.SERVICE_SCOPE));
			}
			finally {
				baseTracker.close();
				receiverTracker.close();
			}
		}
		finally {
			uninstallBundle(tb19);
		}
	}

	public void testScopedServicePrototype130() throws Exception {
		Bundle tb19 = installBundle("tb19.jar", false);
		assertNotNull("tb19 failed to install", tb19);

		try {
			tb19.start();

			Filter baseFilter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + BaseService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=org.osgi.test.cases.component.tb19.Prototype))");
			Filter receiverFilter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ServiceReceiver.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb19.PrototypeReceiver))");
			ServiceTracker<BaseService, BaseService> baseTracker = new ServiceTracker<BaseService, BaseService>(
					getContext(), baseFilter, null);
			ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>> receiverTracker = new ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>>(
					getContext(), receiverFilter, null);
			try {
				baseTracker.open();
				receiverTracker.open();
				BaseService s = baseTracker.waitForService(SLEEP * 3);
				assertNotNull("missing service", s);
				ServiceReceiver<BaseService> r = receiverTracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver", r);
				assertNotSame("bundle service is the same object", s, r.getServices().get(0));
				assertEquals("scope not prototype", Constants.SCOPE_PROTOTYPE,
						r.getServicesProperies().get(0).get(Constants.SERVICE_SCOPE));
				assertEquals("scope not prototype", Constants.SCOPE_PROTOTYPE,
						baseTracker.getServiceReference().getProperty(Constants.SERVICE_SCOPE));
				ServiceObjects<BaseService> so = getContext().getServiceObjects(baseTracker.getServiceReference());
				List<BaseService> list = new ArrayList<BaseService>(20);
				for (int i = 0; i < 20; i++) {
					BaseService o = so.getService();
					assertNotSame("prototype same as tracked service", s, o);
					assertNotSame("prototype same as bound service", r.getServices().get(0), o);
					for (int j = 0; j < list.size(); j++) {
						assertNotSame("duplicate prototype service object", list.get(j), o);
					}
					list.add(o);
				}
			}
			finally {
				baseTracker.close();
				receiverTracker.close();
			}
		}
		finally {
			uninstallBundle(tb19);
		}
	}

	public void testScopedReferenceBundle130() throws Exception {
		Bundle tb20 = installBundle("tb20.jar", false);
		assertNotNull("tb20 failed to install", tb20);

		try {
			tb20.start();

			Filter receiver1Filter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ServiceReceiver.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb20.BundleReceiverPrototype1))");
			Filter receiver2Filter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ServiceReceiver.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb20.BundleReceiverPrototype2))");
			ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>> receiver1Tracker = new ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>>(
					getContext(), receiver1Filter, null);
			ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>> receiver2Tracker = new ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>>(
					getContext(), receiver2Filter, null);
			try {
				receiver1Tracker.open();
				receiver2Tracker.open();
				ServiceReceiver<BaseService> r1 = receiver1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver1", r1);
				ServiceReceiver<BaseService> r2 = receiver2Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver2", r2);
				assertNotSame(r1, r2);
				BaseService s = r1.getServices().get(0);
				for (BaseService o : r1.getServices()) {
					assertSame("service not same", s, o);
				}
				for (BaseService o : r2.getServices()) {
					assertSame("service not same", s, o);
				}
			}
			finally {
				receiver1Tracker.close();
				receiver2Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb20);
		}
	}

	public void testScopedReferencePrototypeBundle130() throws Exception {
		Bundle tb20 = installBundle("tb20.jar", false);
		assertNotNull("tb20 failed to install", tb20);

		try {
			tb20.start();

			Filter receiver1Filter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ServiceReceiver.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb20.PrototypeReceiverBundle1))");
			Filter receiver2Filter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ServiceReceiver.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb20.PrototypeReceiverBundle2))");
			ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>> receiver1Tracker = new ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>>(
					getContext(), receiver1Filter, null);
			ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>> receiver2Tracker = new ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>>(
					getContext(), receiver2Filter, null);
			try {
				receiver1Tracker.open();
				receiver2Tracker.open();
				ServiceReceiver<BaseService> r1 = receiver1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver1", r1);
				ServiceReceiver<BaseService> r2 = receiver2Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver2", r2);
				assertNotSame(r1, r2);
				BaseService s = r1.getServices().get(0);
				for (BaseService o : r1.getServices()) {
					assertSame("service not same", s, o);
				}
				for (BaseService o : r2.getServices()) {
					assertSame("service not same", s, o);
				}
			}
			finally {
				receiver1Tracker.close();
				receiver2Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb20);
		}
	}

	public void testScopedReferencePrototypePrototype130() throws Exception {
		Bundle tb20 = installBundle("tb20.jar", false);
		assertNotNull("tb20 failed to install", tb20);

		try {
			tb20.start();

			Filter receiver1Filter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ServiceReceiver.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb20.PrototypeReceiverPrototype1))");
			Filter receiver2Filter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ServiceReceiver.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb20.PrototypeReceiverPrototype2))");
			ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>> receiver1Tracker = new ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>>(
					getContext(), receiver1Filter, null);
			ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>> receiver2Tracker = new ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>>(
					getContext(), receiver2Filter, null);
			try {
				receiver1Tracker.open();
				receiver2Tracker.open();
				ServiceReceiver<BaseService> r1 = receiver1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver1", r1);
				ServiceReceiver<BaseService> r2 = receiver2Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver2", r2);
				assertNotSame(r1, r2);
				BaseService s1 = r1.getServices().get(0);
				BaseService s2 = r2.getServices().get(0);
				assertNotSame("two components share same instance", s1, s2);
				for (BaseService o : r1.getServices()) {
					assertSame("component does not share same instance", s1, o);
				}
				for (BaseService o : r2.getServices()) {
					assertSame("component does not share same instance", s2, o);
				}
			}
			finally {
				receiver1Tracker.close();
				receiver2Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb20);
		}
	}

	public void testScopedReferencePrototypeRequiredPrototype130() throws Exception {
		Bundle tb20 = installBundle("tb20.jar", false);
		assertNotNull("tb20 failed to install", tb20);

		try {
			tb20.start();

			Filter receiver1Filter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ServiceReceiver.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb20.PrototypeRequiredReceiverPrototype1))");
			Filter receiver2Filter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ServiceReceiver.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb20.PrototypeRequiredReceiverPrototype2))");
			ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>> receiver1Tracker = new ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>>(
					getContext(), receiver1Filter, null);
			ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>> receiver2Tracker = new ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>>(
					getContext(), receiver2Filter, null);
			try {
				receiver1Tracker.open();
				receiver2Tracker.open();
				ServiceReceiver<BaseService> r1 = receiver1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver1", r1);
				ServiceReceiver<BaseService> r2 = receiver2Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver2", r2);
				assertNotSame(r1, r2);
				BaseService s1 = r1.getServices().get(0);
				BaseService s2 = r2.getServices().get(0);
				assertNotSame("two components share same instance", s1, s2);
				for (BaseService o : r1.getServices()) {
					assertSame("component does not share same instance", s1, o);
				}
				for (BaseService o : r2.getServices()) {
					assertSame("component does not share same instance", s2, o);
				}
			}
			finally {
				receiver1Tracker.close();
				receiver2Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb20);
		}
	}

	public void testScopedReferencePrototypeRequiredSingleton130() throws Exception {
		Bundle tb20 = installBundle("tb20.jar", false);
		assertNotNull("tb20 failed to install", tb20);

		try {
			tb20.start();

			Filter receiverFilter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ServiceReceiver.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb20.PrototypeRequiredReceiverSingleton))");
			ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>> receiverTracker = new ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>>(
					getContext(), receiverFilter, null);
			try {
				receiverTracker.open();
				ServiceReceiver<BaseService> r = receiverTracker.waitForService(SLEEP * 3);
				assertNull("receiver should not have been registered", r);
			}
			finally {
				receiverTracker.close();
			}
		}
		finally {
			uninstallBundle(tb20);
		}
	}

	public void testScopedReferencePrototypeRequiredBundle130() throws Exception {
		Bundle tb20 = installBundle("tb20.jar", false);
		assertNotNull("tb20 failed to install", tb20);

		try {
			tb20.start();

			Filter receiverFilter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ServiceReceiver.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb20.PrototypeRequiredReceiverBundle))");
			ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>> receiverTracker = new ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>>(
					getContext(), receiverFilter, null);
			try {
				receiverTracker.open();
				ServiceReceiver<BaseService> r = receiverTracker.waitForService(SLEEP * 3);
				assertNull("receiver should not have been registered", r);
			}
			finally {
				receiverTracker.close();
			}
		}
		finally {
			uninstallBundle(tb20);
		}
	}

	public void testServiceComponentRuntimeDescription() throws Exception {
		ServiceComponentRuntime scr = scrTracker.getService();
		assertNotNull("failed to find ServiceComponentRuntime service", scr);
		Collection<ComponentDescriptionDTO> descriptions = scr.getComponentDescriptionDTOs(tb1, tb2, tb3);
		assertNotNull("null descriptions", descriptions);
		assertFalse("descriptions empty", descriptions.isEmpty());
		assertEquals("no args result size mismatch", descriptions.size(), scr.getComponentDescriptionDTOs().size());
		ComponentDescriptionDTO description1 = scr.getComponentDescriptionDTO(tb1,
				"org.osgi.test.cases.component.tb1.impl.ServiceProviderImpl");
		assertNotNull("null description1", description1);
		ComponentDescriptionDTO expected1 = newComponentDescriptionDTO(
				"org.osgi.test.cases.component.tb1.impl.ServiceProviderImpl",
				newBundleDTO(tb1), null, "bundle",
				"org.osgi.test.cases.component.tb1.impl.ServiceProviderImpl", true, false,
				new String[] {
						"org.osgi.test.cases.component.service.ServiceProvider"
				}, Collections.<String, Object> emptyMap(),
				new ReferenceDTO[] {}, "activate", "deactivate", null, "optional",
				new String[] {
						"org.osgi.test.cases.component.tb1.impl.ServiceProviderImpl"
				}, null, new String[0], 0);
		assertThat(description1)
				.usingRecursiveComparison()
				.isEqualTo(expected1);
		boolean found = false;
		for (ComponentDescriptionDTO d : descriptions) {
			if (d.bundle.id != description1.bundle.id)
				continue;
			if (!d.name.equals(description1.name))
				continue;
			assertThat(d)
					.usingRecursiveComparison()
					.isEqualTo(description1);
			found = true;
			break;
		}
		assertTrue("description1 not found", found);

		ComponentDescriptionDTO description3 = scr.getComponentDescriptionDTO(tb3,
				"org.osgi.test.cases.component.tb3.ServiceConsumerEvent");
		assertNotNull("null description3", description3);
		Map<String, Object> properties3 = new HashMap<String, Object>();
		properties3.put("serviceProvider.target",
				"(component.name=org.osgi.test.cases.component.tb1.impl.ServiceProviderImpl)");
		ComponentDescriptionDTO expected3 = newComponentDescriptionDTO(
				"org.osgi.test.cases.component.tb3.ServiceConsumerEvent",
				newBundleDTO(tb3), null, "singleton",
				"org.osgi.test.cases.component.tb3.impl.ServiceConsumerEventImpl", true, false,
				new String[] {"org.osgi.test.cases.component.tb3.ServiceConsumerEvent"}, properties3,
				new ReferenceDTO[] {
						newReferenceDTO("serviceProvider",
								"org.osgi.test.cases.component.service.ServiceProvider",
								"1..1", "static", "reluctant",
								"(component.name=org.osgi.test.cases.component.tb1.impl.ServiceProviderImpl)",
								"bindServiceProvider", "unbindServiceProvider",
								null, null, null, "bundle", null, null),
						newReferenceDTO("namedService",
								"org.osgi.test.cases.component.tb4.NamedService",
								"0..n",
								"dynamic", "reluctant", null, "bindObject", "unbindObject", null, null, null,
								"bundle", null, null)
				},
				"activate", "deactivate", null, "optional",
				new String[] {
						"org.osgi.test.cases.component.tb3.ServiceConsumerEvent"
				}, null, new String[0], 0);
		assertThat(description3)
				.usingRecursiveComparison()
				.isEqualTo(expected3);
		found = false;
		for (ComponentDescriptionDTO d : descriptions) {
			if (d.bundle.id != description3.bundle.id)
				continue;
			if (!d.name.equals(description3.name))
				continue;
			assertThat(d)
					.usingRecursiveComparison()
					.isEqualTo(description3);
			found = true;
			break;
		}
		assertTrue("description1 not found", found);
	}

	public void testServiceComponentRuntimeConfiguration() throws Exception {
		ServiceComponentRuntime scr = scrTracker.getService();
		assertNotNull("failed to find ServiceComponentRuntime service", scr);
		ComponentDescriptionDTO description1 = scr.getComponentDescriptionDTO(tb1,
				"org.osgi.test.cases.component.tb1.impl.ServiceProviderImpl");
		assertNotNull("null description1", description1);
		Collection<ComponentConfigurationDTO> configurations1 = scr.getComponentConfigurationDTOs(description1);
		assertNotNull("null configurations1", configurations1);
		assertEquals("wrong number of configurations", 1, configurations1.size());
		ComponentConfigurationDTO configuration1 = configurations1.iterator().next();
		assertNotNull("null configuration", configuration1);
		assertThat(configuration1.description)
				.usingRecursiveComparison()
				.isEqualTo(description1);
		assertEquals("wrong state in configuration", ComponentConfigurationDTO.ACTIVE, configuration1.state);
		assertEquals("wrong number of satisfiedReferences", 0, configuration1.satisfiedReferences.length);
		assertEquals("wrong number of unsatisfiedReferences", 0, configuration1.unsatisfiedReferences.length);
		assertEquals("wrong component name", description1.name,
				configuration1.properties.get(ComponentConstants.COMPONENT_NAME));
		assertTrue("configuration missing component.id property",
				configuration1.properties.containsKey(ComponentConstants.COMPONENT_ID));
		assertNull(configuration1.failure);
		ServiceReferenceDTO[] tb1SRs = tb1.adapt(ServiceReferenceDTO[].class);
		assertThat(tb1SRs).hasSize(1);
		assertThat(configuration1.service)
				.usingRecursiveComparison()
				.isEqualTo(tb1SRs[0]);

		ComponentDescriptionDTO description3 = scr.getComponentDescriptionDTO(tb3,
				"org.osgi.test.cases.component.tb3.ServiceConsumerEvent");
		assertNotNull("null description3", description3);
		Collection<ComponentConfigurationDTO> configurations3 = scr.getComponentConfigurationDTOs(description3);
		assertNotNull("null configurations3", configurations3);
		assertEquals("wrong number of configurations", 1, configurations3.size());
		ComponentConfigurationDTO configuration3 = configurations3.iterator().next();
		assertNotNull("null configuration", configuration3);
		assertThat(configuration3.description)
				.usingRecursiveComparison()
				.isEqualTo(description3);
		assertEquals("wrong state in configuration", ComponentConfigurationDTO.ACTIVE, configuration3.state);
		assertEquals("wrong number of satisfiedReferences", 2, configuration3.satisfiedReferences.length);
		assertEquals("wrong number of unsatisfiedReferences", 0, configuration3.unsatisfiedReferences.length);
		assertEquals("wrong component name", description3.name,
				configuration3.properties.get(ComponentConstants.COMPONENT_NAME));
		assertTrue("configuration missing component.id property",
				configuration3.properties.containsKey(ComponentConstants.COMPONENT_ID));
		assertNull(configuration3.failure);
		ServiceReferenceDTO[] tb3SRs = tb3.adapt(ServiceReferenceDTO[].class);
		assertThat(tb3SRs).hasSize(1);
		assertThat(configuration3.service)
				.usingRecursiveComparison()
				.isEqualTo(tb3SRs[0]);
	}

	public void testServiceComponentRuntimeEnablement() throws Exception {
		ServiceComponentRuntime scr = scrTracker.getService();
		assertNotNull("failed to find ServiceComponentRuntime service", scr);
		ComponentDescriptionDTO description1 = scr.getComponentDescriptionDTO(tb1,
				"org.osgi.test.cases.component.tb1.impl.ServiceProviderImpl");
		assertNotNull("null description1", description1);

		Collection<ComponentConfigurationDTO> configurations1 = scr.getComponentConfigurationDTOs(description1);
		assertNotNull("null configurations1", configurations1);
		assertEquals("wrong number of configurations", 1, configurations1.size());
		ComponentConfigurationDTO configuration1 = configurations1.iterator().next();
		assertNotNull("null configuration", configuration1);
		assertEquals("wrong state in configuration", ComponentConfigurationDTO.ACTIVE, configuration1.state);

		assertTrue("description is not enabled", scr.isComponentEnabled(description1));
		Promise<Void> p = scr.disableComponent(description1);
		p.getValue(); // wait for state change to complete
		assertFalse("description is enabled", scr.isComponentEnabled(description1));
		configurations1 = scr.getComponentConfigurationDTOs(description1);
		assertNotNull("null configurations1", configurations1);
		assertEquals("wrong number of configurations", 0, configurations1.size());

		p = scr.enableComponent(description1);
		p.getValue(); // wait for state change to complete
		assertTrue("description is not enabled", scr.isComponentEnabled(description1));
		configurations1 = scr.getComponentConfigurationDTOs(description1);
		assertNotNull("null configurations1", configurations1);
		assertEquals("wrong number of configurations", 1, configurations1.size());
		assertEquals("wrong state in configuration", ComponentConfigurationDTO.ACTIVE, configuration1.state);
	}

	public void testMinimumCardinality01130() throws Exception {
		ServiceComponentRuntime scr = scrTracker.getService();
		assertNotNull("failed to find ServiceComponentRuntime service", scr);
		Bundle tb21 = installBundle("tb21.jar", false);
		assertNotNull("tb21 failed to install", tb21);

		try {
			tb21.start();

			ComponentDescriptionDTO description1 = scr.getComponentDescriptionDTO(tb21,
					"org.osgi.test.cases.component.tb21.ServiceComponent1");
			assertNotNull("null description1", description1);
			assertFalse("description1 is enabled", scr.isComponentEnabled(description1));
			ComponentDescriptionDTO description2 = scr.getComponentDescriptionDTO(tb21,
					"org.osgi.test.cases.component.tb21.ServiceComponent2");
			assertNotNull("null description2", description2);
			assertFalse("description2 is enabled", scr.isComponentEnabled(description2));

			Filter receiver1Filter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ServiceReceiver.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb21.Receiver01NoMinimum))");
			Filter receiver2Filter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ServiceReceiver.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb21.Receiver01Minimum1))");
			Filter receiver3Filter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ServiceReceiver.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb21.Receiver01Minimum2))");
			ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>> receiver1Tracker = new ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>>(
					getContext(), receiver1Filter, null);
			ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>> receiver2Tracker = new ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>>(
					getContext(), receiver2Filter, null);
			ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>> receiver3Tracker = new ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>>(
					getContext(), receiver3Filter, null);
			try {
				receiver1Tracker.open();
				receiver2Tracker.open();
				receiver3Tracker.open();
				ServiceReceiver<BaseService> r1 = receiver1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver1", r1);
				assertTrue("receiver1 has services", r1.getServices().isEmpty());
				ServiceReceiver<BaseService> r2 = receiver2Tracker.waitForService(SLEEP * 3);
				assertNull("receiver2 available", r2);
				ServiceReceiver<BaseService> r3 = receiver3Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver3", r3);
				assertTrue("receiver3 has services", r3.getServices().isEmpty());

				Promise<Void> p = scr.enableComponent(description1);
				p.getValue(); // wait for state change to complete
				assertTrue("description1 is not enabled", scr.isComponentEnabled(description1));

				r1 = receiver2Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver1", r1);
				assertFalse("receiver1 has no services", r1.getServices().isEmpty());
				assertEquals("receiver1 does not have exactly 1 service", 1, r1.getServices().size());
				r2 = receiver2Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver2", r2);
				assertFalse("receiver2 has no services", r2.getServices().isEmpty());
				assertEquals("receiver2 does not have exactly 1 service", 1, r2.getServices().size());
				r3 = receiver3Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver3", r3);
				assertFalse("receiver3 has no services", r3.getServices().isEmpty());
				assertEquals("receiver3 does not have exactly 1 service", 1, r3.getServices().size());

				p = scr.enableComponent(description2);
				p.getValue(); // wait for state change to complete
				assertTrue("description2 is not enabled", scr.isComponentEnabled(description2));

				r1 = receiver2Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver1", r1);
				assertFalse("receiver1 has no services", r1.getServices().isEmpty());
				assertEquals("receiver1 does not have exactly 1 service", 1, r1.getServices().size());
				r2 = receiver2Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver2", r2);
				assertFalse("receiver2 has no services", r2.getServices().isEmpty());
				assertEquals("receiver2 does not have exactly 1 service", 1, r2.getServices().size());
				r3 = receiver3Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver3", r3);
				assertFalse("receiver3 has no services", r3.getServices().isEmpty());
				assertEquals("receiver3 does not have exactly 1 service", 1, r3.getServices().size());

				p = scr.disableComponent(description2);
				p.getValue(); // wait for state change to complete
				assertFalse("description2 is enabled", scr.isComponentEnabled(description2));

				r1 = receiver2Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver1", r1);
				assertFalse("receiver1 has no services", r1.getServices().isEmpty());
				assertEquals("receiver1 does not have exactly 1 service", 1, r1.getServices().size());
				r2 = receiver2Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver2", r2);
				assertFalse("receiver2 has no services", r2.getServices().isEmpty());
				assertEquals("receiver2 does not have exactly 1 service", 1, r2.getServices().size());
				r3 = receiver3Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver3", r3);
				assertFalse("receiver3 has no services", r3.getServices().isEmpty());
				assertEquals("receiver3 does not have exactly 1 service", 1, r3.getServices().size());

				p = scr.disableComponent(description1);
				p.getValue(); // wait for state change to complete
				assertFalse("description1 is enabled", scr.isComponentEnabled(description1));

				r1 = receiver1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver1", r1);
				assertTrue("receiver1 has services", r1.getServices().isEmpty());
				r2 = receiver2Tracker.waitForService(SLEEP * 3);
				assertNull("receiver2 available", r2);
				r3 = receiver3Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver3", r3);
				assertTrue("receiver3 has services", r3.getServices().isEmpty());
			}
			finally {
				receiver1Tracker.close();
				receiver2Tracker.close();
				receiver3Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb21);
		}
	}

	public void testMinimumCardinality0n130() throws Exception {
		ServiceComponentRuntime scr = scrTracker.getService();
		assertNotNull("failed to find ServiceComponentRuntime service", scr);
		Bundle tb21 = installBundle("tb21.jar", false);
		assertNotNull("tb21 failed to install", tb21);

		try {
			tb21.start();

			ComponentDescriptionDTO description1 = scr.getComponentDescriptionDTO(tb21,
					"org.osgi.test.cases.component.tb21.ServiceComponent1");
			assertNotNull("null description1", description1);
			assertFalse("description1 is enabled", scr.isComponentEnabled(description1));
			ComponentDescriptionDTO description2 = scr.getComponentDescriptionDTO(tb21,
					"org.osgi.test.cases.component.tb21.ServiceComponent2");
			assertNotNull("null description2", description2);
			assertFalse("description2 is enabled", scr.isComponentEnabled(description2));

			Filter receiver1Filter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ServiceReceiver.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb21.Receiver0nNoMinimum))");
			Filter receiver2Filter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ServiceReceiver.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb21.Receiver0nMinimum1))");
			Filter receiver3Filter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ServiceReceiver.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb21.Receiver0nMinimum2))");
			ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>> receiver1Tracker = new ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>>(
					getContext(), receiver1Filter, null);
			ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>> receiver2Tracker = new ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>>(
					getContext(), receiver2Filter, null);
			ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>> receiver3Tracker = new ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>>(
					getContext(), receiver3Filter, null);
			try {
				receiver1Tracker.open();
				receiver2Tracker.open();
				receiver3Tracker.open();
				ServiceReceiver<BaseService> r1 = receiver1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver1", r1);
				assertTrue("receiver1 has services", r1.getServices().isEmpty());
				ServiceReceiver<BaseService> r2 = receiver2Tracker.waitForService(SLEEP * 3);
				assertNull("receiver2 available", r2);
				ServiceReceiver<BaseService> r3 = receiver3Tracker.waitForService(SLEEP * 3);
				assertNull("receiver3 available", r3);

				Promise<Void> p = scr.enableComponent(description1);
				p.getValue(); // wait for state change to complete
				assertTrue("description1 is not enabled", scr.isComponentEnabled(description1));

				r1 = receiver1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver1", r1);
				assertFalse("receiver1 has no services", r1.getServices().isEmpty());
				assertEquals("receiver1 does not have exactly 1 service", 1, r1.getServices().size());
				r2 = receiver2Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver2", r2);
				assertFalse("receiver2 has no services", r2.getServices().isEmpty());
				assertEquals("receiver2 does not have exactly 1 service", 1, r2.getServices().size());
				r3 = receiver3Tracker.waitForService(SLEEP * 3);
				assertNull("receiver3 available", r3);

				p = scr.enableComponent(description2);
				p.getValue(); // wait for state change to complete
				assertTrue("description2 is not enabled", scr.isComponentEnabled(description2));

				r1 = receiver1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver1", r1);
				assertFalse("receiver1 has noservices", r1.getServices().isEmpty());
				assertEquals("receiver1 does not have exactly 2 service", 2, r1.getServices().size());
				r2 = receiver2Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver2", r2);
				assertFalse("receiver2 has no services", r2.getServices().isEmpty());
				assertEquals("receiver2 does not have exactly 2 service", 2, r2.getServices().size());
				r3 = receiver3Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver3", r3);
				assertFalse("receiver3 has no services", r3.getServices().isEmpty());
				assertEquals("receiver3 does not have exactly 2 service", 2, r3.getServices().size());

				p = scr.disableComponent(description2);
				p.getValue(); // wait for state change to complete
				assertFalse("description2 is enabled", scr.isComponentEnabled(description2));

				r1 = receiver1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver1", r1);
				assertFalse("receiver1 has noservices", r1.getServices().isEmpty());
				assertEquals("receiver1 does not have exactly 1 service", 1, r1.getServices().size());
				r2 = receiver2Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver2", r2);
				assertFalse("receiver2 has no services", r2.getServices().isEmpty());
				assertEquals("receiver2 does not have exactly 1 service", 1, r2.getServices().size());
				r3 = receiver3Tracker.waitForService(SLEEP * 3);
				assertNull("receiver3 available", r3);

			}
			finally {
				receiver1Tracker.close();
				receiver2Tracker.close();
				receiver3Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb21);
		}
	}

	public void testMinimumCardinality11130() throws Exception {
		ServiceComponentRuntime scr = scrTracker.getService();
		assertNotNull("failed to find ServiceComponentRuntime service", scr);
		Bundle tb21 = installBundle("tb21.jar", false);
		assertNotNull("tb21 failed to install", tb21);

		try {
			tb21.start();

			ComponentDescriptionDTO description1 = scr.getComponentDescriptionDTO(tb21,
					"org.osgi.test.cases.component.tb21.ServiceComponent1");
			assertNotNull("null description1", description1);
			assertFalse("description1 is enabled", scr.isComponentEnabled(description1));
			ComponentDescriptionDTO description2 = scr.getComponentDescriptionDTO(tb21,
					"org.osgi.test.cases.component.tb21.ServiceComponent2");
			assertNotNull("null description2", description2);
			assertFalse("description2 is enabled", scr.isComponentEnabled(description2));

			Filter receiver1Filter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ServiceReceiver.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb21.Receiver11NoMinimum))");
			Filter receiver2Filter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ServiceReceiver.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb21.Receiver11Minimum0))");
			Filter receiver3Filter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ServiceReceiver.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb21.Receiver11Minimum1))");
			Filter receiver4Filter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ServiceReceiver.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb21.Receiver11Minimum2))");
			ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>> receiver1Tracker = new ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>>(
					getContext(), receiver1Filter, null);
			ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>> receiver2Tracker = new ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>>(
					getContext(), receiver2Filter, null);
			ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>> receiver3Tracker = new ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>>(
					getContext(), receiver3Filter, null);
			ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>> receiver4Tracker = new ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>>(
					getContext(), receiver4Filter, null);
			try {
				receiver1Tracker.open();
				receiver2Tracker.open();
				receiver3Tracker.open();
				receiver4Tracker.open();
				ServiceReceiver<BaseService> r1 = receiver1Tracker.waitForService(SLEEP * 3);
				assertNull("receiver1 available", r1);
				ServiceReceiver<BaseService> r2 = receiver2Tracker.waitForService(SLEEP * 3);
				assertNull("receiver2 available", r2);
				ServiceReceiver<BaseService> r3 = receiver3Tracker.waitForService(SLEEP * 3);
				assertNull("receiver3 available", r3);
				ServiceReceiver<BaseService> r4 = receiver4Tracker.waitForService(SLEEP * 3);
				assertNull("receiver4 available", r4);

				Promise<Void> p = scr.enableComponent(description1);
				p.getValue(); // wait for state change to complete
				assertTrue("description1 is not enabled", scr.isComponentEnabled(description1));

				r1 = receiver1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver1", r1);
				assertFalse("receiver1 has no services", r1.getServices().isEmpty());
				assertEquals("receiver1 does not have exactly 1 service", 1, r1.getServices().size());
				r2 = receiver2Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver2", r2);
				assertFalse("receiver2 has no services", r2.getServices().isEmpty());
				assertEquals("receiver2 does not have exactly 1 service", 1, r2.getServices().size());
				r3 = receiver3Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver3", r3);
				assertFalse("receiver3 has no services", r3.getServices().isEmpty());
				assertEquals("receiver3 does not have exactly 1 service", 1, r3.getServices().size());
				r4 = receiver4Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver4", r4);
				assertFalse("receiver4 has no services", r4.getServices().isEmpty());
				assertEquals("receiver4 does not have exactly 1 service", 1, r4.getServices().size());

				p = scr.enableComponent(description2);
				p.getValue(); // wait for state change to complete
				assertTrue("description2 is not enabled", scr.isComponentEnabled(description2));
				r4 = receiver4Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver4", r4);
				assertFalse("receiver4 has no services", r4.getServices().isEmpty());
				assertEquals("receiver4 does not have exactly 1 service", 1, r4.getServices().size());
			}
			finally {
				receiver1Tracker.close();
				receiver2Tracker.close();
				receiver3Tracker.close();
				receiver4Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb21);
		}
	}

	public void testMinimumCardinality1n130() throws Exception {
		ServiceComponentRuntime scr = scrTracker.getService();
		assertNotNull("failed to find ServiceComponentRuntime service", scr);
		Bundle tb21 = installBundle("tb21.jar", false);
		assertNotNull("tb21 failed to install", tb21);

		try {
			tb21.start();

			ComponentDescriptionDTO description1 = scr.getComponentDescriptionDTO(tb21,
					"org.osgi.test.cases.component.tb21.ServiceComponent1");
			assertNotNull("null description1", description1);
			assertFalse("description1 is enabled", scr.isComponentEnabled(description1));
			ComponentDescriptionDTO description2 = scr.getComponentDescriptionDTO(tb21,
					"org.osgi.test.cases.component.tb21.ServiceComponent2");
			assertNotNull("null description2", description2);
			assertFalse("description2 is enabled", scr.isComponentEnabled(description2));

			Filter receiver1Filter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ServiceReceiver.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb21.Receiver1nNoMinimum))");
			Filter receiver2Filter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ServiceReceiver.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb21.Receiver1nMinimum0))");
			Filter receiver3Filter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ServiceReceiver.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb21.Receiver1nMinimum1))");
			Filter receiver4Filter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ServiceReceiver.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb21.Receiver1nMinimum2))");
			ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>> receiver1Tracker = new ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>>(
					getContext(), receiver1Filter, null);
			ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>> receiver2Tracker = new ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>>(
					getContext(), receiver2Filter, null);
			ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>> receiver3Tracker = new ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>>(
					getContext(), receiver3Filter, null);
			ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>> receiver4Tracker = new ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>>(
					getContext(), receiver4Filter, null);
			try {
				receiver1Tracker.open();
				receiver2Tracker.open();
				receiver3Tracker.open();
				receiver4Tracker.open();
				ServiceReceiver<BaseService> r1 = receiver1Tracker.waitForService(SLEEP * 3);
				assertNull("receiver1 available", r1);
				ServiceReceiver<BaseService> r2 = receiver2Tracker.waitForService(SLEEP * 3);
				assertNull("receiver2 available", r2);
				ServiceReceiver<BaseService> r3 = receiver3Tracker.waitForService(SLEEP * 3);
				assertNull("receiver3 available", r3);
				ServiceReceiver<BaseService> r4 = receiver4Tracker.waitForService(SLEEP * 3);
				assertNull("receiver4 available", r4);

				Promise<Void> p = scr.enableComponent(description1);
				p.getValue(); // wait for state change to complete
				assertTrue("description1 is not enabled", scr.isComponentEnabled(description1));

				r1 = receiver1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver1", r1);
				assertFalse("receiver1 has no services", r1.getServices().isEmpty());
				assertEquals("receiver1 does not have exactly 1 service", 1, r1.getServices().size());
				r2 = receiver2Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver2", r2);
				assertFalse("receiver2 has no services", r2.getServices().isEmpty());
				assertEquals("receiver2 does not have exactly 1 service", 1, r2.getServices().size());
				r3 = receiver3Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver3", r3);
				assertFalse("receiver3 has no services", r3.getServices().isEmpty());
				assertEquals("receiver3 does not have exactly 1 service", 1, r3.getServices().size());
				r4 = receiver4Tracker.waitForService(SLEEP * 3);
				assertNull("receiver4 available", r4);

				p = scr.enableComponent(description2);
				p.getValue(); // wait for state change to complete
				assertTrue("description2 is not enabled", scr.isComponentEnabled(description2));

				r1 = receiver1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver1", r1);
				assertFalse("receiver1 has no services", r1.getServices().isEmpty());
				assertEquals("receiver1 does not have exactly 2 service", 2, r1.getServices().size());
				r2 = receiver2Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver2", r2);
				assertFalse("receiver2 has no services", r2.getServices().isEmpty());
				assertEquals("receiver2 does not have exactly 2 service", 2, r2.getServices().size());
				r3 = receiver3Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver3", r3);
				assertFalse("receiver3 has no services", r3.getServices().isEmpty());
				assertEquals("receiver3 does not have exactly 2 service", 2, r3.getServices().size());
				r4 = receiver4Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver4", r4);
				assertFalse("receiver4 has no services", r4.getServices().isEmpty());
				assertEquals("receiver4 does not have exactly 2 service", 2, r4.getServices().size());

				p = scr.disableComponent(description2);
				p.getValue(); // wait for state change to complete
				assertFalse("description2 is enabled", scr.isComponentEnabled(description2));

				r1 = receiver1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver1", r1);
				assertFalse("receiver1 has no services", r1.getServices().isEmpty());
				assertEquals("receiver1 does not have exactly 1 service", 1, r1.getServices().size());
				r2 = receiver2Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver2", r2);
				assertFalse("receiver2 has no services", r2.getServices().isEmpty());
				assertEquals("receiver2 does not have exactly 1 service", 1, r2.getServices().size());
				r3 = receiver3Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver3", r3);
				assertFalse("receiver3 has no services", r3.getServices().isEmpty());
				assertEquals("receiver3 does not have exactly 1 service", 1, r3.getServices().size());
				r4 = receiver4Tracker.waitForService(SLEEP * 3);
				assertNull("receiver4 available", r4);
			}
			finally {
				receiver1Tracker.close();
				receiver2Tracker.close();
				receiver3Tracker.close();
				receiver4Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb21);
		}
	}

	public void testMinimumCardinality110() throws Exception {
		ServiceComponentRuntime scr = scrTracker.getService();
		assertNotNull("failed to find ServiceComponentRuntime service", scr);
		Bundle tb21 = installBundle("tb21.jar", false);
		assertNotNull("tb21 failed to install", tb21);

		try {
			tb21.start();

			ComponentDescriptionDTO description1 = scr.getComponentDescriptionDTO(tb21,
					"org.osgi.test.cases.component.tb21.ServiceComponent1");
			assertNotNull("null description1", description1);
			assertFalse("description1 is enabled", scr.isComponentEnabled(description1));
			ComponentDescriptionDTO description2 = scr.getComponentDescriptionDTO(tb21,
					"org.osgi.test.cases.component.tb21.ServiceComponent2");
			assertNotNull("null description2", description2);
			assertFalse("description2 is enabled", scr.isComponentEnabled(description2));

			Filter receiver1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + ServiceReceiver.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=org.osgi.test.cases.component.tb21.Receiverv11))");
			ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>> receiver1Tracker = new ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>>(
					getContext(), receiver1Filter, null);
			try {
				receiver1Tracker.open();
				ServiceReceiver<BaseService> r1 = receiver1Tracker.waitForService(SLEEP * 3);
				assertNull("receiver1 available", r1);

				Promise<Void> p = scr.enableComponent(description1);
				p.getValue(); // wait for state change to complete
				assertTrue("description1 is not enabled", scr.isComponentEnabled(description1));

				r1 = receiver1Tracker.waitForService(SLEEP * 3);
				assertNull("receiver1 available", r1);

				p = scr.enableComponent(description2);
				p.getValue(); // wait for state change to complete
				assertTrue("description2 is not enabled", scr.isComponentEnabled(description2));

				r1 = receiver1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver1", r1);
				assertFalse("receiver1 has no services", r1.getServices().isEmpty());
				assertEquals("receiver1 does not have exactly 2 service", 2, r1.getServices().size());

				p = scr.disableComponent(description2);
				p.getValue(); // wait for state change to complete
				assertFalse("description2 is enabled", scr.isComponentEnabled(description2));

				r1 = receiver1Tracker.waitForService(SLEEP * 3);
				assertNull("receiver4 available", r1);
			}
			finally {
				receiver1Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb21);
		}
	}

	public void testComparableMap130() throws Exception {
		ServiceComponentRuntime scr = scrTracker.getService();
		assertNotNull("failed to find ServiceComponentRuntime service", scr);
		Bundle tb22 = installBundle("tb22.jar", false);
		assertNotNull("tb22 failed to install", tb22);

		try {
			tb22.start();

			ComponentDescriptionDTO description1 = scr.getComponentDescriptionDTO(tb22,
					"org.osgi.test.cases.component.tb22.Ranking10");
			assertNotNull("null description1", description1);
			assertFalse("description1 is enabled", scr.isComponentEnabled(description1));

			Filter receiver1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + ServiceReceiver.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=org.osgi.test.cases.component.tb22.MapReceiver))");
			ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>> receiver1Tracker = new ServiceTracker<ServiceReceiver<BaseService>, ServiceReceiver<BaseService>>(
					getContext(), receiver1Filter, null);
			try {
				receiver1Tracker.open();
				ServiceReceiver<BaseService> r1 = receiver1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver1", r1);
				List<Map<String, Object>> props = r1.getServicesProperies();
				assertFalse("props empty", props.isEmpty());
				assertEquals("props size not 1", 1, props.size());
				Map<String, Object> p1 = props.get(0);
				assertNotNull("p1 null", p1);
				assertTrue("p1 not Comparable", p1 instanceof Comparable);
				assertEquals("wrong ranking", Integer.valueOf(1), p1.get(Constants.SERVICE_RANKING));

				Promise<Void> p = scr.enableComponent(description1);
				p.getValue(); // wait for state change to complete
				assertTrue("description1 is not enabled", scr.isComponentEnabled(description1));

				r1 = receiver1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver1", r1);
				props = r1.getServicesProperies();
				assertFalse("props empty", props.isEmpty());
				assertEquals("props size not 2", 2, props.size());
				p1 = props.get(0);
				assertNotNull("p1 null", p1);
				assertTrue("p1 not Comparable", p1 instanceof Comparable);
				assertEquals("wrong ranking", Integer.valueOf(10), p1.get(Constants.SERVICE_RANKING));
				Map<String, Object> p2 = props.get(1);
				assertNotNull("p2 null", p2);
				assertTrue("p2 not Comparable", p2 instanceof Comparable);
				assertEquals("wrong ranking", Integer.valueOf(1), p2.get(Constants.SERVICE_RANKING));
			}
			finally {
				receiver1Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb22);
		}
	}

	public void testConfigurationSinglePID130() throws Exception {
		ConfigurationAdmin cm = trackerCM.getService();
		assertNotNull("The ConfigurationAdmin should be available", cm);

		final String PID_ROOT = TEST_CASE_ROOT + ".tb23";
		final String PID = PID_ROOT + ".SinglePID";

		Configuration config = cm.getConfiguration(PID, null);
		Dictionary<String, Object> props = new Hashtable<String, Object>();
		props.put(PID_ROOT, "config");
		config.update(props);

		Bundle tb23 = installBundle("tb23.jar", false);
		assertNotNull("tb23 failed to install", tb23);

		try {
			tb23.start();

			Filter base1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + BaseService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=org.osgi.test.cases.component.tb23.SinglePID))");
			ServiceTracker<BaseService, BaseService> base1Tracker = new ServiceTracker<BaseService, BaseService>(
					getContext(), base1Filter, null);
			try {
				base1Tracker.open();
				BaseService b1 = base1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing base1", b1);
				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "config", props.get(PID_ROOT));
				assertEquals("wrong service.pid", PID, props.get(Constants.SERVICE_PID));
			}
			finally {
				base1Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb23);
		}
	}

	public void testConfigurationSinglePIDFactory130() throws Exception {
		ConfigurationAdmin cm = trackerCM.getService();
		assertNotNull("The ConfigurationAdmin should be available", cm);

		final String PID_ROOT = TEST_CASE_ROOT + ".tb23";
		final String PID1 = PID_ROOT + ".SinglePID";
		final String PID2 = PID_ROOT + ".SinglePIDFactory";

		Configuration config = cm.getConfiguration(PID1, null);
		Dictionary<String, Object> props = new Hashtable<String, Object>();
		props.put(PID_ROOT, "config1");
		props.put(PID1, "config1");
		config.update(props);

		Bundle tb23 = installBundle("tb23.jar", false);
		assertNotNull("tb23 failed to install", tb23);

		try {
			tb23.start();

			Filter base1Filter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ComponentFactory.class.getName() + ")(" + ComponentConstants.COMPONENT_FACTORY
					+ "=org.osgi.test.cases.component.tb23.SinglePIDFactory))");
			ServiceTracker<ComponentFactory<BaseService>,ComponentFactory<BaseService>> base1Tracker = new ServiceTracker<>(
					getContext(), base1Filter, null);
			try {
				base1Tracker.open();
				ComponentFactory<BaseService> f1 = base1Tracker
						.waitForService(SLEEP * 3);
				assertNotNull("missing factory1", f1);
				props = new Hashtable<String, Object>();
				props.put(PID_ROOT, "factory");
				props.put(PID2, "factory");
				ComponentInstance<BaseService> i1 = f1.newInstance(props);
				assertNotNull("missing ComponentInstance", i1);
				BaseService b1 = i1.getInstance();
				assertNotNull("missing base1", b1);
				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "factory", props.get(PID_ROOT));
				assertEquals("configurations not merged", "config1", props.get(PID1));
				assertEquals("configurations not merged", "factory", props.get(PID2));
				assertEquals("wrong service.pid", PID1, props.get(Constants.SERVICE_PID));
			}
			finally {
				base1Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb23);
		}
	}

	public void testConfigurationMultiplePIDs130() throws Exception {
		ConfigurationAdmin cm = trackerCM.getService();
		assertNotNull("The ConfigurationAdmin should be available", cm);

		final String PID_ROOT = TEST_CASE_ROOT + ".tb23";
		final String PID1 = PID_ROOT + ".MultiplePID1";
		final String PID2 = PID_ROOT + ".MultiplePID2";

		Configuration config = cm.getConfiguration(PID1, null);
		Dictionary<String, Object> props = new Hashtable<String, Object>();
		props.put(PID_ROOT, "config1");
		props.put(PID1, "config1");
		config.update(props);
		config = cm.getConfiguration(PID2, null);
		props = new Hashtable<String, Object>();
		props.put(PID_ROOT, "config2");
		props.put(PID2, "config2");
		config.update(props);

		Bundle tb23 = installBundle("tb23.jar", false);
		assertNotNull("tb23 failed to install", tb23);

		try {
			tb23.start();

			Filter base1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + BaseService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=org.osgi.test.cases.component.tb23.MultiplePIDs))");
			ServiceTracker<BaseService, BaseService> base1Tracker = new ServiceTracker<BaseService, BaseService>(
					getContext(), base1Filter, null);
			try {
				base1Tracker.open();
				BaseService b1 = base1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing base1", b1);
				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "config2", props.get(PID_ROOT));
				assertEquals("configurations not merged", "config1", props.get(PID1));
				assertEquals("configurations not merged", "config2", props.get(PID2));
				@SuppressWarnings("unchecked")
				Collection<String> pids = (Collection<String>) props.get(Constants.SERVICE_PID);
				assertEquals("pids collection wrong", Arrays.asList(PID1, PID2), pids);
			}
			finally {
				base1Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb23);
		}
	}

	public void testConfigurationMultiplePIDsFactory130() throws Exception {
		ConfigurationAdmin cm = trackerCM.getService();
		assertNotNull("The ConfigurationAdmin should be available", cm);

		final String PID_ROOT = TEST_CASE_ROOT + ".tb23";
		final String PID1 = PID_ROOT + ".MultiplePID1";
		final String PID2 = PID_ROOT + ".MultiplePID2";
		final String PID3 = PID_ROOT + ".MultiplePIDFactory";

		Configuration config = cm.getConfiguration(PID1, null);
		Dictionary<String, Object> props = new Hashtable<String, Object>();
		props.put(PID_ROOT, "config1");
		props.put(PID1, "config1");
		config.update(props);
		config = cm.getConfiguration(PID2, null);
		props = new Hashtable<String, Object>();
		props.put(PID_ROOT, "config2");
		props.put(PID2, "config2");
		config.update(props);

		Bundle tb23 = installBundle("tb23.jar", false);
		assertNotNull("tb23 failed to install", tb23);

		try {
			tb23.start();

			Filter base1Filter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ComponentFactory.class.getName() + ")(" + ComponentConstants.COMPONENT_FACTORY
					+ "=org.osgi.test.cases.component.tb23.MultiplePIDsFactory))");
			ServiceTracker<ComponentFactory<BaseService>,ComponentFactory<BaseService>> base1Tracker = new ServiceTracker<>(
					getContext(), base1Filter, null);
			try {
				base1Tracker.open();
				ComponentFactory<BaseService> f1 = base1Tracker
						.waitForService(SLEEP * 3);
				assertNotNull("missing factory1", f1);
				props = new Hashtable<String, Object>();
				props.put(PID_ROOT, "factory");
				props.put(PID3, "factory");
				ComponentInstance<BaseService> i1 = f1.newInstance(props);
				assertNotNull("missing ComponentInstance", i1);
				BaseService b1 = i1.getInstance();
				assertNotNull("missing base1", b1);
				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "factory", props.get(PID_ROOT));
				assertEquals("configurations not merged", "config1", props.get(PID1));
				assertEquals("configurations not merged", "config2", props.get(PID2));
				assertEquals("configurations not merged", "factory", props.get(PID3));
				@SuppressWarnings("unchecked")
				Collection<String> pids = (Collection<String>) props.get(Constants.SERVICE_PID);
				assertEquals("pids collection wrong", Arrays.asList(PID1, PID2), pids);
			}
			finally {
				base1Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb23);
		}
	}

	public void testConfigurationTargetedPIDRequiredNoModified130() throws Exception {
		ConfigurationAdmin cm = trackerCM.getService();
		assertNotNull("The ConfigurationAdmin should be available", cm);

		Bundle tb23 = installBundle("tb23.jar", false);
		assertNotNull("tb23 failed to install", tb23);

		final String bsn = tb23.getSymbolicName();
		final String version = tb23.getVersion().toString();
		final String location = tb23.getLocation();

		final String PID_ROOT = TEST_CASE_ROOT + ".tb23";
		final String NAME = PID_ROOT + ".TargetedPIDRequiredNoModified";
		final String PID1 = NAME;
		final String PID2 = String.format("%s|%s", PID1, bsn);
		final String PID3 = String.format("%s|%s|%s", PID1, bsn, version);
		final String PID4 = String.format("%s|%s|%s|%s", PID1, bsn, version, location);

		try {
			tb23.start();

			Filter base1Filter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ BaseService.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME + "=" + NAME + "))");
			ServiceTracker<BaseService, BaseService> base1Tracker = new ServiceTracker<BaseService, BaseService>(
					getContext(), base1Filter, null);
			try {
				Dictionary<String, Object> props;
				base1Tracker.open();
				BaseService b1;
				b1 = base1Tracker.waitForService(SLEEP * 3);
				assertNull("base1 available", b1);

				Configuration config1 = cm.getConfiguration(PID1, null);
				props = new Hashtable<String, Object>();
				props.put(PID_ROOT, "config1");
				props.put(PID1, "config1");
				config1.update(props);
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				b1 = base1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing base1", b1);
				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "config1", props.get(PID_ROOT));
				assertEquals("wrong service.pid", PID1, props.get(Constants.SERVICE_PID));

				Configuration config2 = cm.getConfiguration(PID2, null);
				props = new Hashtable<String, Object>();
				props.put(PID_ROOT, "config2");
				props.put(PID2, "config2");
				config2.update(props);
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				b1 = base1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing base1", b1);
				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "config2", props.get(PID_ROOT));
				assertEquals("wrong service.pid", PID2, props.get(Constants.SERVICE_PID));

				Configuration config3 = cm.getConfiguration(PID3, null);
				props = new Hashtable<String, Object>();
				props.put(PID_ROOT, "config3");
				props.put(PID3, "config3");
				config3.update(props);
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				b1 = base1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing base1", b1);
				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "config3", props.get(PID_ROOT));
				assertEquals("wrong service.pid", PID3, props.get(Constants.SERVICE_PID));

				Configuration config4 = cm.getConfiguration(PID4, null);
				props = new Hashtable<String, Object>();
				props.put(PID_ROOT, "config4");
				props.put(PID4, "config4");
				config4.update(props);
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				b1 = base1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing base1", b1);
				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "config4", props.get(PID_ROOT));
				assertEquals("wrong service.pid", PID4, props.get(Constants.SERVICE_PID));

				config4.delete();
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				b1 = base1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing base1", b1);
				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "config3", props.get(PID_ROOT));
				assertEquals("wrong service.pid", PID3, props.get(Constants.SERVICE_PID));

				config3.delete();
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				b1 = base1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing base1", b1);
				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "config2", props.get(PID_ROOT));
				assertEquals("wrong service.pid", PID2, props.get(Constants.SERVICE_PID));

				config2.delete();
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				b1 = base1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing base1", b1);
				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "config1", props.get(PID_ROOT));
				assertEquals("wrong service.pid", PID1, props.get(Constants.SERVICE_PID));

				config1.delete();
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				b1 = base1Tracker.waitForService(SLEEP * 3);
				assertNull("base1 available", b1);
			}
			finally {
				base1Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb23);
		}
	}

	public void testConfigurationTargetedPIDRequiredModified130() throws Exception {
		ConfigurationAdmin cm = trackerCM.getService();
		assertNotNull("The ConfigurationAdmin should be available", cm);

		Bundle tb23 = installBundle("tb23.jar", false);
		assertNotNull("tb23 failed to install", tb23);

		final String bsn = tb23.getSymbolicName();
		final String version = tb23.getVersion().toString();
		final String location = tb23.getLocation();

		final String PID_ROOT = TEST_CASE_ROOT + ".tb23";
		final String NAME = PID_ROOT + ".TargetedPIDRequiredModified";
		final String PID1 = NAME;
		final String PID2 = String.format("%s|%s", PID1, bsn);
		final String PID3 = String.format("%s|%s|%s", PID1, bsn, version);
		final String PID4 = String.format("%s|%s|%s|%s", PID1, bsn, version, location);

		try {
			tb23.start();

			Filter base1Filter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ BaseService.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME + "=" + NAME + "))");
			ServiceTracker<BaseService, BaseService> base1Tracker = new ServiceTracker<BaseService, BaseService>(
					getContext(), base1Filter, null);
			try {
				Dictionary<String, Object> props;
				base1Tracker.open();
				BaseService b1;
				b1 = base1Tracker.waitForService(SLEEP * 3);
				assertNull("base1 available", b1);

				Configuration config1 = cm.getConfiguration(PID1, null);
				props = new Hashtable<String, Object>();
				props.put(PID_ROOT, "config1");
				props.put(PID1, "config1");
				config1.update(props);
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				b1 = base1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing base1", b1);
				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "config1", props.get(PID_ROOT));
				assertEquals("wrong service.pid", PID1, props.get(Constants.SERVICE_PID));

				Configuration config2 = cm.getConfiguration(PID2, null);
				props = new Hashtable<String, Object>();
				props.put(PID_ROOT, "config2");
				props.put(PID2, "config2");
				config2.update(props);
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "config2", props.get(PID_ROOT));
				assertEquals("wrong service.pid", PID2, props.get(Constants.SERVICE_PID));

				Configuration config3 = cm.getConfiguration(PID3, null);
				props = new Hashtable<String, Object>();
				props.put(PID_ROOT, "config3");
				props.put(PID3, "config3");
				config3.update(props);
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "config3", props.get(PID_ROOT));
				assertEquals("wrong service.pid", PID3, props.get(Constants.SERVICE_PID));

				Configuration config4 = cm.getConfiguration(PID4, null);
				props = new Hashtable<String, Object>();
				props.put(PID_ROOT, "config4");
				props.put(PID4, "config4");
				config4.update(props);
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "config4", props.get(PID_ROOT));
				assertEquals("wrong service.pid", PID4, props.get(Constants.SERVICE_PID));

				config4.delete();
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "config3", props.get(PID_ROOT));
				assertEquals("wrong service.pid", PID3, props.get(Constants.SERVICE_PID));

				config3.delete();
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "config2", props.get(PID_ROOT));
				assertEquals("wrong service.pid", PID2, props.get(Constants.SERVICE_PID));

				config2.delete();
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "config1", props.get(PID_ROOT));
				assertEquals("wrong service.pid", PID1, props.get(Constants.SERVICE_PID));

				config1.delete();
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				b1 = base1Tracker.waitForService(SLEEP * 3);
				assertNull("base1 available", b1);
			}
			finally {
				base1Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb23);
		}
	}

	public void testConfigurationTargetedPIDOptionalNoModified130() throws Exception {
		ConfigurationAdmin cm = trackerCM.getService();
		assertNotNull("The ConfigurationAdmin should be available", cm);

		Bundle tb23 = installBundle("tb23.jar", false);
		assertNotNull("tb23 failed to install", tb23);

		final String bsn = tb23.getSymbolicName();
		final String version = tb23.getVersion().toString();
		final String location = tb23.getLocation();

		final String PID_ROOT = TEST_CASE_ROOT + ".tb23";
		final String NAME = PID_ROOT + ".TargetedPIDOptionalNoModified";
		final String PID1 = NAME;
		final String PID2 = String.format("%s|%s", PID1, bsn);
		final String PID3 = String.format("%s|%s|%s", PID1, bsn, version);
		final String PID4 = String.format("%s|%s|%s|%s", PID1, bsn, version, location);

		try {
			tb23.start();

			Filter base1Filter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ BaseService.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME + "=" + NAME + "))");
			ServiceTracker<BaseService, BaseService> base1Tracker = new ServiceTracker<BaseService, BaseService>(
					getContext(), base1Filter, null);
			try {
				Dictionary<String, Object> props;
				base1Tracker.open();
				BaseService b1;
				b1 = base1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing base1", b1);
				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "xml", props.get(PID_ROOT));
				assertNull("wrong service.pid", props.get(Constants.SERVICE_PID));

				Configuration config1 = cm.getConfiguration(PID1, null);
				props = new Hashtable<String, Object>();
				props.put(PID_ROOT, "config1");
				props.put(PID1, "config1");
				config1.update(props);
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				b1 = base1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing base1", b1);
				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "config1", props.get(PID_ROOT));
				assertEquals("wrong service.pid", PID1, props.get(Constants.SERVICE_PID));

				Configuration config2 = cm.getConfiguration(PID2, null);
				props = new Hashtable<String, Object>();
				props.put(PID_ROOT, "config2");
				props.put(PID2, "config2");
				config2.update(props);
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				b1 = base1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing base1", b1);
				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "config2", props.get(PID_ROOT));
				assertEquals("wrong service.pid", PID2, props.get(Constants.SERVICE_PID));

				Configuration config3 = cm.getConfiguration(PID3, null);
				props = new Hashtable<String, Object>();
				props.put(PID_ROOT, "config3");
				props.put(PID3, "config3");
				config3.update(props);
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				b1 = base1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing base1", b1);
				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "config3", props.get(PID_ROOT));
				assertEquals("wrong service.pid", PID3, props.get(Constants.SERVICE_PID));

				Configuration config4 = cm.getConfiguration(PID4, null);
				props = new Hashtable<String, Object>();
				props.put(PID_ROOT, "config4");
				props.put(PID4, "config4");
				config4.update(props);
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				b1 = base1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing base1", b1);
				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "config4", props.get(PID_ROOT));
				assertEquals("wrong service.pid", PID4, props.get(Constants.SERVICE_PID));

				config4.delete();
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				b1 = base1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing base1", b1);
				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "config3", props.get(PID_ROOT));
				assertEquals("wrong service.pid", PID3, props.get(Constants.SERVICE_PID));

				config3.delete();
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				b1 = base1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing base1", b1);
				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "config2", props.get(PID_ROOT));
				assertEquals("wrong service.pid", PID2, props.get(Constants.SERVICE_PID));

				config2.delete();
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				b1 = base1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing base1", b1);
				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "config1", props.get(PID_ROOT));
				assertEquals("wrong service.pid", PID1, props.get(Constants.SERVICE_PID));

				config1.delete();
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				b1 = base1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing base1", b1);
				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "xml", props.get(PID_ROOT));
				assertNull("wrong service.pid", props.get(Constants.SERVICE_PID));
			}
			finally {
				base1Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb23);
		}
	}

	public void testConfigurationTargetedPIDOptionalModified130() throws Exception {
		ConfigurationAdmin cm = trackerCM.getService();
		assertNotNull("The ConfigurationAdmin should be available", cm);

		Bundle tb23 = installBundle("tb23.jar", false);
		assertNotNull("tb23 failed to install", tb23);

		final String bsn = tb23.getSymbolicName();
		final String version = tb23.getVersion().toString();
		final String location = tb23.getLocation();

		final String PID_ROOT = TEST_CASE_ROOT + ".tb23";
		final String NAME = PID_ROOT + ".TargetedPIDOptionalModified";
		final String PID1 = NAME;
		final String PID2 = String.format("%s|%s", PID1, bsn);
		final String PID3 = String.format("%s|%s|%s", PID1, bsn, version);
		final String PID4 = String.format("%s|%s|%s|%s", PID1, bsn, version, location);

		try {
			tb23.start();

			Filter base1Filter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ BaseService.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME + "=" + NAME + "))");
			ServiceTracker<BaseService, BaseService> base1Tracker = new ServiceTracker<BaseService, BaseService>(
					getContext(), base1Filter, null);
			try {
				Dictionary<String, Object> props;
				base1Tracker.open();
				BaseService b1;
				b1 = base1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing base1", b1);
				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "xml", props.get(PID_ROOT));
				assertNull("wrong service.pid", props.get(Constants.SERVICE_PID));

				Configuration config1 = cm.getConfiguration(PID1, null);
				props = new Hashtable<String, Object>();
				props.put(PID_ROOT, "config1");
				props.put(PID1, "config1");
				config1.update(props);
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "config1", props.get(PID_ROOT));
				assertEquals("wrong service.pid", PID1, props.get(Constants.SERVICE_PID));

				Configuration config2 = cm.getConfiguration(PID2, null);
				props = new Hashtable<String, Object>();
				props.put(PID_ROOT, "config2");
				props.put(PID2, "config2");
				config2.update(props);
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "config2", props.get(PID_ROOT));
				assertEquals("wrong service.pid", PID2, props.get(Constants.SERVICE_PID));

				Configuration config3 = cm.getConfiguration(PID3, null);
				props = new Hashtable<String, Object>();
				props.put(PID_ROOT, "config3");
				props.put(PID3, "config3");
				config3.update(props);
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "config3", props.get(PID_ROOT));
				assertEquals("wrong service.pid", PID3, props.get(Constants.SERVICE_PID));

				Configuration config4 = cm.getConfiguration(PID4, null);
				props = new Hashtable<String, Object>();
				props.put(PID_ROOT, "config4");
				props.put(PID4, "config4");
				config4.update(props);
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "config4", props.get(PID_ROOT));
				assertEquals("wrong service.pid", PID4, props.get(Constants.SERVICE_PID));

				config4.delete();
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "config3", props.get(PID_ROOT));
				assertEquals("wrong service.pid", PID3, props.get(Constants.SERVICE_PID));

				config3.delete();
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "config2", props.get(PID_ROOT));
				assertEquals("wrong service.pid", PID2, props.get(Constants.SERVICE_PID));

				config2.delete();
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "config1", props.get(PID_ROOT));
				assertEquals("wrong service.pid", PID1, props.get(Constants.SERVICE_PID));

				config1.delete();
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				props = b1.getProperties();
				assertNotNull("props null", props);
				assertEquals("wrong configuration precedence", "xml", props.get(PID_ROOT));
				assertNull("wrong service.pid", props.get(Constants.SERVICE_PID));
			}
			finally {
				base1Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb23);
		}
	}

	public void testStaticScalarFieldReference130() throws Exception {
		Bundle tb24 = installBundle("tb24.jar", false);
		assertNotNull("tb24 failed to install", tb24);

		final String NAME = TEST_CASE_ROOT + ".tb24.StaticScalarFieldReceiver";

		try {
			tb24.start();

			Filter comp1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + BaseService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=org.osgi.test.cases.component.tb24.Ranking1))");
			Filter test1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + ScalarFieldTestService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=" + NAME + "))");
			ServiceTracker<BaseService, BaseService> comp1Tracker = new ServiceTracker<BaseService, BaseService>(
					getContext(), comp1Filter, null);
			ServiceTracker<ScalarFieldTestService<BaseService>, ScalarFieldTestService<BaseService>> test1Tracker = new ServiceTracker<ScalarFieldTestService<BaseService>, ScalarFieldTestService<BaseService>>(
					getContext(), test1Filter, null);
			try {
				comp1Tracker.open();
				test1Tracker.open();
				BaseService c1 = comp1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing comp1", c1);
				ScalarFieldTestService<BaseService> t1 = test1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing test1", t1);
				assertEquals("wrong activation count", 1, t1.getActivationCount());
				assertEquals("wrong modification count", 0, t1.getModificationCount());
				assertEquals("wrong deactivation count", 0, t1.getDeactivationCount());

				assertNotNull("no service injected", t1.getService());
				assertSame("wrong service injected", c1, t1.getService());
				assertNotNull("no service injected", t1.getAssignable());
				assertSame("wrong service injected", c1, t1.getAssignable());
				assertNotNull("no reference injected", t1.getReference());
				assertEquals("wrong reference injected", comp1Tracker.getServiceReference(), t1.getReference());
				assertNotNull("no serviceobjects injected", t1.getServiceObjects());
				assertEquals("wrong serviceobjects injected", comp1Tracker.getServiceReference(),
						t1.getServiceObjects().getServiceReference());
				assertSame("serviceobjects produced wrong service", c1, t1.getServiceObjects().getService());
				Map<String, Object> properties = t1.getProperties();
				assertNotNull("no properties injected", properties);
				assertEquals("wrong properties injected", c1.getProperties().get(ComponentConstants.COMPONENT_ID),
						properties.get(ComponentConstants.COMPONENT_ID));
				assertTrue("properties not Comparable", properties instanceof Comparable);
				try {
					properties.remove(ComponentConstants.COMPONENT_ID);
					failException("properties is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				try {
					properties.put("foo", "bar");
					failException("properties is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				assertNotNull("no tuple injected", t1.getTuple());
				Map<String, Object> key = t1.getTuple().getKey();
				assertNotNull("tuple key null", key);
				assertEquals("tuple key wrong", properties.get(ComponentConstants.COMPONENT_ID),
						key.get(ComponentConstants.COMPONENT_ID));
				assertTrue("tuple not Comparable", t1.getTuple() instanceof Comparable);
				assertTrue("tuple key not Comparable", key instanceof Comparable);
				try {
					key.remove(ComponentConstants.COMPONENT_ID);
					failException("tuple key is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				try {
					key.put("foo", "bar");
					failException("tuple key is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				assertEquals("properties not equal to tuple key", 0,
						asComparable(properties).compareTo(key));
				assertEquals("tuple keys not equal to properties", 0,
						asComparable(key).compareTo(properties));
				assertNotNull("tuple value null", t1.getTuple().getValue());
				assertSame("tuple value wrong", t1.getService(), t1.getTuple().getValue());
			}
			finally {
				comp1Tracker.close();
				test1Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb24);
		}
	}

	@SuppressWarnings("unchecked")
	private static <C> Comparable<C> asComparable(C o) {
		return (Comparable<C>) o;
	}

	public void testStaticScalarFieldReferenceModified130() throws Exception {
		ConfigurationAdmin cm = trackerCM.getService();
		assertNotNull("The ConfigurationAdmin should be available", cm);

		final String PID_ROOT = TEST_CASE_ROOT + ".tb24";
		Bundle tb24 = installBundle("tb24.jar", false);
		assertNotNull("tb24 failed to install", tb24);

		final String NAME = TEST_CASE_ROOT + ".tb24.StaticScalarFieldReceiver";

		try {
			tb24.start();

			Filter comp1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + BaseService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=org.osgi.test.cases.component.tb24.Ranking1))");
			Filter test1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + ScalarFieldTestService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=" + NAME + "))");
			ServiceTracker<BaseService, BaseService> comp1Tracker = new ServiceTracker<BaseService, BaseService>(
					getContext(), comp1Filter, null);
			ServiceTracker<ScalarFieldTestService<BaseService>, ScalarFieldTestService<BaseService>> test1Tracker = new ServiceTracker<ScalarFieldTestService<BaseService>, ScalarFieldTestService<BaseService>>(
					getContext(), test1Filter, null);
			try {
				comp1Tracker.open();
				test1Tracker.open();
				BaseService c1 = comp1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing comp1", c1);
				assertEquals("wrong service property value", "xml", c1.getProperties().get(PID_ROOT));
				ScalarFieldTestService<BaseService> t1 = test1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing test1", t1);
				assertEquals("wrong activation count", 1, t1.getActivationCount());
				assertEquals("wrong modification count", 0, t1.getModificationCount());
				assertEquals("wrong deactivation count", 0, t1.getDeactivationCount());

				assertNotNull("no reference injected", t1.getReference());
				assertEquals("wrong property value", "xml", t1.getReference().getProperty(PID_ROOT));
				assertNotNull("no properties injected", t1.getProperties());
				assertEquals("wrong property value", "xml", t1.getProperties().get(PID_ROOT));
				assertNotNull("no tuple injected", t1.getTuple());
				Map<String, Object> key = t1.getTuple().getKey();
				assertNotNull("tuple key null", key);
				assertEquals("wrong property value", "xml", key.get(PID_ROOT));

				Configuration config = cm.getConfiguration("org.osgi.test.cases.component.tb24.Ranking1", null);
				Dictionary<String, Object> props = new Hashtable<String, Object>();
				props.put(PID_ROOT, "config");
				config.update(props);
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				assertEquals("wrong service property value", "config", c1.getProperties().get(PID_ROOT));
				ScalarFieldTestService<BaseService> t2 = test1Tracker.waitForService(SLEEP * 3);
				assertNotSame("test service not reactivated", t1, t2);
				assertEquals("wrong activation count", 2, t2.getActivationCount());
				assertEquals("wrong modification count", 0, t2.getModificationCount());
				assertEquals("wrong deactivation count", 1, t2.getDeactivationCount());
				assertNotNull("no reference injected", t2.getReference());
				assertEquals("wrong property value", "config", t2.getReference().getProperty(PID_ROOT));
				assertNotNull("no properties injected", t2.getProperties());
				assertEquals("wrong property value", "config", t2.getProperties().get(PID_ROOT));
				assertNotNull("no tuple injected", t2.getTuple());
				key = t2.getTuple().getKey();
				assertNotNull("tuple key null", key);
				assertEquals("wrong property value", "config", key.get(PID_ROOT));
			}
			finally {
				comp1Tracker.close();
				test1Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb24);
		}
	}

	public void testDynamicScalarFieldReferenceModified130() throws Exception {
		ConfigurationAdmin cm = trackerCM.getService();
		assertNotNull("The ConfigurationAdmin should be available", cm);

		final String PID_ROOT = TEST_CASE_ROOT + ".tb24";
		Bundle tb24 = installBundle("tb24.jar", false);
		assertNotNull("tb24 failed to install", tb24);

		final String NAME = TEST_CASE_ROOT + ".tb24.DynamicScalarFieldReceiver";

		try {
			tb24.start();

			Filter comp1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + BaseService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=org.osgi.test.cases.component.tb24.Ranking1))");
			Filter test1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + ScalarFieldTestService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=" + NAME + "))");
			ServiceTracker<BaseService, BaseService> comp1Tracker = new ServiceTracker<BaseService, BaseService>(
					getContext(), comp1Filter, null);
			ServiceTracker<ScalarFieldTestService<BaseService>, ScalarFieldTestService<BaseService>> test1Tracker = new ServiceTracker<ScalarFieldTestService<BaseService>, ScalarFieldTestService<BaseService>>(
					getContext(), test1Filter, null);
			try {
				comp1Tracker.open();
				test1Tracker.open();
				BaseService c1 = comp1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing comp1", c1);
				assertEquals("wrong service property value", "xml", c1.getProperties().get(PID_ROOT));
				ScalarFieldTestService<BaseService> t1 = test1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing test1", t1);
				assertEquals("wrong activation count", 1, t1.getActivationCount());
				assertEquals("wrong modification count", 0, t1.getModificationCount());
				assertEquals("wrong deactivation count", 0, t1.getDeactivationCount());

				assertNotNull("no reference injected", t1.getReference());
				assertEquals("wrong property value", "xml", t1.getReference().getProperty(PID_ROOT));
				assertNotNull("no properties injected", t1.getProperties());
				assertEquals("wrong property value", "xml", t1.getProperties().get(PID_ROOT));
				assertNotNull("no tuple injected", t1.getTuple());
				Map<String, Object> key = t1.getTuple().getKey();
				assertNotNull("tuple key null", key);
				assertEquals("wrong property value", "xml", key.get(PID_ROOT));

				Configuration config = cm.getConfiguration("org.osgi.test.cases.component.tb24.Ranking1", null);
				Dictionary<String, Object> props = new Hashtable<String, Object>();
				props.put(PID_ROOT, "config");
				config.update(props);
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				assertEquals("wrong service property value", "config", c1.getProperties().get(PID_ROOT));
				assertSame("test service reactivated", t1, test1Tracker.waitForService(SLEEP * 3));
				assertEquals("wrong activation count", 1, t1.getActivationCount());
				assertEquals("wrong modification count", 0, t1.getModificationCount());
				assertEquals("wrong deactivation count", 0, t1.getDeactivationCount());
				assertNotNull("no reference injected", t1.getReference());
				assertEquals("wrong property value", "config", t1.getReference().getProperty(PID_ROOT));
				assertNotNull("no properties injected", t1.getProperties());
				assertEquals("wrong property value", "config", t1.getProperties().get(PID_ROOT));
				assertNotNull("no tuple injected", t1.getTuple());
				key = t1.getTuple().getKey();
				assertNotNull("tuple key null", key);
				assertEquals("wrong property value", "config", key.get(PID_ROOT));
			}
			finally {
				comp1Tracker.close();
				test1Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb24);
		}
	}

	public void testDynamicNonVoltaileFieldReference130() throws Exception {
		Bundle tb24 = installBundle("tb24.jar", false);
		assertNotNull("tb24 failed to install", tb24);

		final String NAME = TEST_CASE_ROOT + ".tb24.DynamicNonVolatileFieldReceiver";

		try {
			tb24.start();

			Filter comp1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + BaseService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=org.osgi.test.cases.component.tb24.Ranking1))");
			Filter test1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + ScalarFieldTestService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=" + NAME + "))");
			ServiceTracker<BaseService, BaseService> comp1Tracker = new ServiceTracker<BaseService, BaseService>(
					getContext(), comp1Filter, null);
			ServiceTracker<ScalarFieldTestService<BaseService>, ScalarFieldTestService<BaseService>> test1Tracker = new ServiceTracker<ScalarFieldTestService<BaseService>, ScalarFieldTestService<BaseService>>(
					getContext(), test1Filter, null);
			try {
				comp1Tracker.open();
				test1Tracker.open();
				BaseService c1 = comp1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing comp1", c1);
				ScalarFieldTestService<BaseService> s1 = test1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing test1", s1);
				assertEquals("wrong activation count", 1, s1.getActivationCount());
				assertEquals("wrong modification count", 0, s1.getModificationCount());
				assertEquals("wrong deactivation count", 0, s1.getDeactivationCount());

				assertNull("service injected", s1.getService());
				assertNull("service injected", s1.getAssignable());
				assertNull("reference injected", s1.getReference());
				assertNull("serviceobjects injected", s1.getServiceObjects());
				assertNull("properties injected", s1.getProperties());
				assertNull("tuple injected", s1.getTuple());

				@SuppressWarnings("unchecked")
				MultipleFieldTestService<BaseService> t1 = (MultipleFieldTestService<BaseService>) s1;
				Collection<BaseService> cs1 = t1.getCollectionService();
				assertNotNull("no service collection", cs1);
				assertEquals("wrong number of elements in service collection", 0, cs1.size());
				assertTrue("service collection replaced", cs1 instanceof TestList);
				Collection<ServiceReference<BaseService>> cr1 = t1.getCollectionReference();
				assertNotNull("no service reference collection", cr1);
				assertEquals("wrong number of elements in service reference collection", 0, cr1.size());
				assertTrue("service reference collection replaced", cr1 instanceof TestList);
				Collection<ComponentServiceObjects<BaseService>> co1 = t1.getCollectionServiceObjects();
				assertNotNull("no service objects collection", co1);
				assertEquals("wrong number of elements in service objects collection", 0, co1.size());
				assertTrue("service objects collection replaced", co1 instanceof TestList);
				Collection<Map<String, Object>> cp1 = t1.getCollectionProperties();
				assertNotNull("no service properties collection", cp1);
				assertEquals("wrong number of elements in service properties collection", 0, cp1.size());
				assertTrue("service properties collection replaced", cp1 instanceof TestList);
				Collection<Entry<Map<String, Object>, BaseService>> ct1 = t1.getCollectionTuple();
				assertNotNull("no service tuple collection", ct1);
				assertEquals("wrong number of elements in service tuple collection", 0, ct1.size());
				assertTrue("service tuple collection replaced", ct1 instanceof TestList);

				List<BaseService> ls1 = t1.getListService();
				assertNotNull("no service list", ls1);
				assertEquals("wrong number of elements in service list", 0, ls1.size());
				assertTrue("service list replaced", ls1 instanceof TestList);
				List<ServiceReference<BaseService>> lr1 = t1.getListReference();
				assertNotNull("no service reference list", lr1);
				assertEquals("wrong number of elements in service reference list", 0, lr1.size());
				assertTrue("service reference list replaced", lr1 instanceof TestList);
				List<ComponentServiceObjects<BaseService>> lo1 = t1.getListServiceObjects();
				assertNotNull("no service objects list", lo1);
				assertEquals("wrong number of elements in service objects list", 0, lo1.size());
				assertTrue("service objects list replaced", lo1 instanceof TestList);
				List<Map<String, Object>> lp1 = t1.getListProperties();
				assertNotNull("no service properties list", lp1);
				assertEquals("wrong number of elements in service properties list", 0, lp1.size());
				assertTrue("service properties list replaced", lp1 instanceof TestList);
				List<Entry<Map<String, Object>, BaseService>> lt1 = t1.getListTuple();
				assertNotNull("no service tuple list", lt1);
				assertEquals("wrong number of elements in service tuple list", 0, lt1.size());
				assertTrue("service tuple list replaced", lt1 instanceof TestList);

			}
			finally {
				comp1Tracker.close();
				test1Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb24);
		}
	}

	public void testFinalFieldReference130() throws Exception {
		Bundle tb24 = installBundle("tb24.jar", false);
		assertNotNull("tb24 failed to install", tb24);

		final String NAME = TEST_CASE_ROOT + ".tb24.FinalFieldReceiver";

		try {
			tb24.start();

			Filter comp1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + BaseService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=org.osgi.test.cases.component.tb24.Ranking1))");
			Filter test1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + ScalarFieldTestService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=" + NAME + "))");
			ServiceTracker<BaseService, BaseService> comp1Tracker = new ServiceTracker<BaseService, BaseService>(
					getContext(), comp1Filter, null);
			ServiceTracker<ScalarFieldTestService<BaseService>, ScalarFieldTestService<BaseService>> test1Tracker = new ServiceTracker<ScalarFieldTestService<BaseService>, ScalarFieldTestService<BaseService>>(
					getContext(), test1Filter, null);
			try {
				comp1Tracker.open();
				test1Tracker.open();
				BaseService c1 = comp1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing comp1", c1);
				ScalarFieldTestService<BaseService> s1 = test1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing test1", s1);
				assertEquals("wrong activation count", 1, s1.getActivationCount());
				assertEquals("wrong modification count", 0, s1.getModificationCount());
				assertEquals("wrong deactivation count", 0, s1.getDeactivationCount());

				assertNull("service injected", s1.getService());
				assertNull("service injected", s1.getAssignable());
				assertNull("reference injected", s1.getReference());
				assertNull("serviceobjects injected", s1.getServiceObjects());
				assertNull("properties injected", s1.getProperties());
				assertNull("tuple injected", s1.getTuple());

				@SuppressWarnings("unchecked")
				MultipleFieldTestService<BaseService> t1 = (MultipleFieldTestService<BaseService>) s1;
				Collection<BaseService> cs1 = t1.getCollectionService();
				assertNotNull("no service collection", cs1);
				assertEquals("wrong number of elements in service collection", 0, cs1.size());
				assertTrue("service collection replaced", cs1 instanceof TestList);
				Collection<ServiceReference<BaseService>> cr1 = t1.getCollectionReference();
				assertNotNull("no service reference collection", cr1);
				assertEquals("wrong number of elements in service reference collection", 0, cr1.size());
				assertTrue("service reference collection replaced", cr1 instanceof TestList);
				Collection<ComponentServiceObjects<BaseService>> co1 = t1.getCollectionServiceObjects();
				assertNotNull("no service objects collection", co1);
				assertEquals("wrong number of elements in service objects collection", 0, co1.size());
				assertTrue("service objects collection replaced", co1 instanceof TestList);
				Collection<Map<String, Object>> cp1 = t1.getCollectionProperties();
				assertNotNull("no service properties collection", cp1);
				assertEquals("wrong number of elements in service properties collection", 0, cp1.size());
				assertTrue("service properties collection replaced", cp1 instanceof TestList);
				Collection<Entry<Map<String, Object>, BaseService>> ct1 = t1.getCollectionTuple();
				assertNotNull("no service tuple collection", ct1);
				assertEquals("wrong number of elements in service tuple collection", 0, ct1.size());
				assertTrue("service tuple collection replaced", ct1 instanceof TestList);

				List<BaseService> ls1 = t1.getListService();
				assertNotNull("no service list", ls1);
				assertEquals("wrong number of elements in service list", 0, ls1.size());
				assertTrue("service list replaced", ls1 instanceof TestList);
				List<ServiceReference<BaseService>> lr1 = t1.getListReference();
				assertNotNull("no service reference list", lr1);
				assertEquals("wrong number of elements in service reference list", 0, lr1.size());
				assertTrue("service reference list replaced", lr1 instanceof TestList);
				List<ComponentServiceObjects<BaseService>> lo1 = t1.getListServiceObjects();
				assertNotNull("no service objects list", lo1);
				assertEquals("wrong number of elements in service objects list", 0, lo1.size());
				assertTrue("service objects list replaced", lo1 instanceof TestList);
				List<Map<String, Object>> lp1 = t1.getListProperties();
				assertNotNull("no service properties list", lp1);
				assertEquals("wrong number of elements in service properties list", 0, lp1.size());
				assertTrue("service properties list replaced", lp1 instanceof TestList);
				List<Entry<Map<String, Object>, BaseService>> lt1 = t1.getListTuple();
				assertNotNull("no service tuple list", lt1);
				assertEquals("wrong number of elements in service tuple list", 0, lt1.size());
				assertTrue("service tuple list replaced", lt1 instanceof TestList);
			}
			finally {
				comp1Tracker.close();
				test1Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb24);
		}
	}

	public void testOptionalScalarFieldReference130() throws Exception {
		Bundle tb24 = installBundle("tb24.jar", false);
		assertNotNull("tb24 failed to install", tb24);

		final String NAME = TEST_CASE_ROOT + ".tb24.OptionalScalarFieldReceiver";

		try {
			tb24.start();

			Filter comp1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + BaseService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=org.osgi.test.cases.component.tb24.Ranking1))");
			Filter test1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + ScalarFieldTestService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=" + NAME + "))");
			ServiceTracker<BaseService, BaseService> comp1Tracker = new ServiceTracker<BaseService, BaseService>(
					getContext(), comp1Filter, null);
			ServiceTracker<ScalarFieldTestService<BaseService>, ScalarFieldTestService<BaseService>> test1Tracker = new ServiceTracker<ScalarFieldTestService<BaseService>, ScalarFieldTestService<BaseService>>(
					getContext(), test1Filter, null);
			try {
				comp1Tracker.open();
				test1Tracker.open();
				BaseService c1 = comp1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing comp1", c1);
				ScalarFieldTestService<BaseService> t1 = test1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing test1", t1);
				assertEquals("wrong activation count", 1, t1.getActivationCount());
				assertEquals("wrong modification count", 0, t1.getModificationCount());
				assertEquals("wrong deactivation count", 0, t1.getDeactivationCount());

				assertNotNull("no service injected", t1.getService());
				assertSame("wrong service injected", c1, t1.getService());
				assertNotNull("no service injected", t1.getAssignable());
				assertSame("wrong service injected", c1, t1.getAssignable());

				assertNull("field not nulled", t1.getReference());
				assertNull("field not nulled", t1.getServiceObjects());
			}
			finally {
				comp1Tracker.close();
				test1Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb24);
		}
	}

	public void testDynamicScalarFieldReference130() throws Exception {
		ServiceComponentRuntime scr = scrTracker.getService();
		assertNotNull("failed to find ServiceComponentRuntime service", scr);
		Bundle tb24 = installBundle("tb24.jar", false);
		assertNotNull("tb24 failed to install", tb24);

		final String NAME = TEST_CASE_ROOT + ".tb24.DynamicScalarFieldReceiver";

		try {
			tb24.start();

			ComponentDescriptionDTO description1 = scr.getComponentDescriptionDTO(tb24,
					"org.osgi.test.cases.component.tb24.Ranking10");
			assertNotNull("null description1", description1);
			assertFalse("description1 is enabled", scr.isComponentEnabled(description1));

			Filter comp1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + BaseService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=org.osgi.test.cases.component.tb24.Ranking*))");
			Filter test1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + ScalarFieldTestService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=" + NAME + "))");
			ServiceTracker<BaseService, BaseService> comp1Tracker = new ServiceTracker<BaseService, BaseService>(
					getContext(), comp1Filter, null);
			ServiceTracker<ScalarFieldTestService<BaseService>, ScalarFieldTestService<BaseService>> test1Tracker = new ServiceTracker<ScalarFieldTestService<BaseService>, ScalarFieldTestService<BaseService>>(
					getContext(), test1Filter, null);
			try {
				comp1Tracker.open();
				test1Tracker.open();
				BaseService c1 = comp1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing comp1", c1);
				ScalarFieldTestService<BaseService> t1 = test1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing test1", t1);
				assertEquals("wrong activation count", 1, t1.getActivationCount());
				assertEquals("wrong modification count", 0, t1.getModificationCount());
				assertEquals("wrong deactivation count", 0, t1.getDeactivationCount());

				assertNotNull("no service injected", t1.getService());
				assertSame("wrong service injected", c1, t1.getService());
				assertNotNull("no service injected", t1.getAssignable());
				assertSame("wrong service injected", c1, t1.getAssignable());
				assertNotNull("no reference injected", t1.getReference());
				assertEquals("wrong reference injected", comp1Tracker.getServiceReference(), t1.getReference());
				assertNotNull("no serviceobjects injected", t1.getServiceObjects());
				assertEquals("wrong serviceobjects injected", comp1Tracker.getServiceReference(),
						t1.getServiceObjects().getServiceReference());
				assertSame("serviceobjects produced wrong service", c1, t1.getServiceObjects().getService());
				Map<String, Object> properties = t1.getProperties();
				assertNotNull("no properties injected", properties);
				assertEquals("wrong properties injected", c1.getProperties().get(ComponentConstants.COMPONENT_ID),
						properties.get(ComponentConstants.COMPONENT_ID));
				assertTrue("properties not Comparable", properties instanceof Comparable);
				try {
					properties.remove(ComponentConstants.COMPONENT_ID);
					failException("properties is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				try {
					properties.put("foo", "bar");
					failException("properties is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				assertNotNull("no tuple injected", t1.getTuple());
				Map<String, Object> key = t1.getTuple().getKey();
				assertNotNull("tuple key null", key);
				assertEquals("tuple key wrong", properties.get(ComponentConstants.COMPONENT_ID),
						key.get(ComponentConstants.COMPONENT_ID));
				assertTrue("tuple not Comparable", t1.getTuple() instanceof Comparable);
				assertTrue("tuple key not Comparable", key instanceof Comparable);
				try {
					key.remove(ComponentConstants.COMPONENT_ID);
					failException("tuple key is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				try {
					key.put("foo", "bar");
					failException("tuple key is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				assertEquals("properties not equal to tuple key", 0,
						asComparable(properties).compareTo(key));
				assertEquals("tuple keys not equal to properties", 0,
						asComparable(key).compareTo(properties));
				assertNotNull("tuple value null", t1.getTuple().getValue());
				assertSame("tuple value wrong", t1.getService(), t1.getTuple().getValue());

				Promise<Void> p = scr.enableComponent(description1);
				p.getValue(); // wait for state change to complete
				assertTrue("description1 is not enabled", scr.isComponentEnabled(description1));
				BaseService c10 = comp1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing comp10", c10);
				assertNotSame("did not get higher ranked service", c1, c10);
				assertEquals("wrong activation count", 1, t1.getActivationCount());
				assertEquals("wrong modification count", 0, t1.getModificationCount());
				assertEquals("wrong deactivation count", 0, t1.getDeactivationCount());

				assertNotNull("no service injected", t1.getService());
				assertSame("wrong service injected", c10, t1.getService());
				assertNotNull("no service injected", t1.getAssignable());
				assertSame("wrong service injected", c10, t1.getAssignable());
				assertNotNull("no reference injected", t1.getReference());
				assertEquals("wrong reference injected", comp1Tracker.getServiceReference(), t1.getReference());
				assertNotNull("no serviceobjects injected", t1.getServiceObjects());
				assertEquals("wrong serviceobjects injected", comp1Tracker.getServiceReference(),
						t1.getServiceObjects().getServiceReference());
				assertSame("serviceobjects produced wrong service", c10, t1.getServiceObjects().getService());
				properties = t1.getProperties();
				assertNotNull("no properties injected", properties);
				assertEquals("wrong properties injected", c10.getProperties().get(ComponentConstants.COMPONENT_ID),
						properties.get(ComponentConstants.COMPONENT_ID));
				assertTrue("properties not Comparable", properties instanceof Comparable);
				try {
					properties.remove(ComponentConstants.COMPONENT_ID);
					failException("properties is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				try {
					properties.put("foo", "bar");
					failException("properties is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				assertNotNull("no tuple injected", t1.getTuple());
				key = t1.getTuple().getKey();
				assertNotNull("tuple key null", key);
				assertEquals("tuple key wrong", properties.get(ComponentConstants.COMPONENT_ID),
						key.get(ComponentConstants.COMPONENT_ID));
				assertTrue("tuple not Comparable", t1.getTuple() instanceof Comparable);
				assertTrue("tuple key not Comparable", key instanceof Comparable);
				try {
					key.remove(ComponentConstants.COMPONENT_ID);
					failException("tuple key is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				try {
					key.put("foo", "bar");
					failException("tuple key is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				assertEquals("properties not equal to tuple key", 0,
						asComparable(properties).compareTo(key));
				assertEquals("tuple keys not equal to properties", 0,
						asComparable(key).compareTo(properties));
				assertNotNull("tuple value null", t1.getTuple().getValue());
				assertSame("tuple value wrong", t1.getService(), t1.getTuple().getValue());

				p = scr.disableComponent(description1);
				p.getValue(); // wait for state change to complete
				assertFalse("description1 is not enabled", scr.isComponentEnabled(description1));
				assertSame("not back to comp1", c1, comp1Tracker.waitForService(SLEEP * 3));

				assertNotNull("no service injected", t1.getService());
				assertSame("wrong service injected", c1, t1.getService());
				assertNotNull("no service injected", t1.getAssignable());
				assertSame("wrong service injected", c1, t1.getAssignable());
				assertNotNull("no reference injected", t1.getReference());
				assertEquals("wrong reference injected", comp1Tracker.getServiceReference(), t1.getReference());
				assertNotNull("no serviceobjects injected", t1.getServiceObjects());
				assertEquals("wrong serviceobjects injected", comp1Tracker.getServiceReference(),
						t1.getServiceObjects().getServiceReference());
				assertSame("serviceobjects produced wrong service", c1, t1.getServiceObjects().getService());
				properties = t1.getProperties();
				assertNotNull("no properties injected", properties);
				assertEquals("wrong properties injected", c1.getProperties().get(ComponentConstants.COMPONENT_ID),
						properties.get(ComponentConstants.COMPONENT_ID));
				assertTrue("properties not Comparable", properties instanceof Comparable);
				try {
					properties.remove(ComponentConstants.COMPONENT_ID);
					failException("properties is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				try {
					properties.put("foo", "bar");
					failException("properties is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				assertNotNull("no tuple injected", t1.getTuple());
				key = t1.getTuple().getKey();
				assertNotNull("tuple key null", key);
				assertEquals("tuple key wrong", properties.get(ComponentConstants.COMPONENT_ID),
						key.get(ComponentConstants.COMPONENT_ID));
				assertTrue("tuple not Comparable", t1.getTuple() instanceof Comparable);
				assertTrue("tuple key not Comparable", key instanceof Comparable);
				try {
					key.remove(ComponentConstants.COMPONENT_ID);
					failException("tuple key is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				try {
					key.put("foo", "bar");
					failException("tuple key is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				assertEquals("properties not equal to tuple key", 0,
						asComparable(properties).compareTo(key));
				assertEquals("tuple keys not equal to properties", 0,
						asComparable(key).compareTo(properties));
				assertNotNull("tuple value null", t1.getTuple().getValue());
				assertSame("tuple value wrong", t1.getService(), t1.getTuple().getValue());

			}
			finally {
				comp1Tracker.close();
				test1Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb24);
		}
	}

	public void testStaticMultipleFieldReference130() throws Exception {
		Bundle tb24 = installBundle("tb24.jar", false);
		assertNotNull("tb24 failed to install", tb24);

		final String NAME = TEST_CASE_ROOT + ".tb24.StaticMultipleFieldReceiver";

		try {
			tb24.start();

			Filter comp1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + BaseService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=org.osgi.test.cases.component.tb24.Ranking1))");
			Filter test1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + MultipleFieldTestService.class.getName()
							+ ")("
							+ ComponentConstants.COMPONENT_NAME + "=" + NAME + "))");
			ServiceTracker<BaseService, BaseService> comp1Tracker = new ServiceTracker<BaseService, BaseService>(
					getContext(), comp1Filter, null);
			ServiceTracker<MultipleFieldTestService<BaseService>, MultipleFieldTestService<BaseService>> test1Tracker = new ServiceTracker<MultipleFieldTestService<BaseService>, MultipleFieldTestService<BaseService>>(
					getContext(), test1Filter, null);
			try {
				comp1Tracker.open();
				test1Tracker.open();
				BaseService c1 = comp1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing comp1", c1);
				MultipleFieldTestService<BaseService> t1 = test1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing test1", t1);
				assertEquals("wrong activation count", 1, t1.getActivationCount());
				assertEquals("wrong modification count", 0, t1.getModificationCount());
				assertEquals("wrong deactivation count", 0, t1.getDeactivationCount());

				Collection<BaseService> cs1 = t1.getCollectionService();
				assertNotNull("no service collection", cs1);
				assertFalse("service collection not replaced", cs1 instanceof TestList);
				assertEquals("wrong number of elements in service collection", 1, cs1.size());
				assertSame("wrong service in service collection", c1, cs1.iterator().next());
				assertTrue("service collection not mutable", cs1.remove(cs1.iterator().next()));
				Collection<ServiceReference<BaseService>> cr1 = t1.getCollectionReference();
				assertNotNull("no service reference collection", cr1);
				assertFalse("service reference collection not replaced", cr1 instanceof TestList);
				assertEquals("wrong number of elements in service reference collection", 1, cr1.size());
				assertEquals("wrong service in service reference collection", comp1Tracker.getServiceReference(),
						cr1.iterator().next());
				assertTrue("service reference collection not mutable", cr1.remove(cr1.iterator().next()));
				Collection<ComponentServiceObjects<BaseService>> co1 = t1.getCollectionServiceObjects();
				assertNotNull("no service objects collection", co1);
				assertFalse("service objects collection not replaced", co1 instanceof TestList);
				assertEquals("wrong number of elements in service objects collection", 1, co1.size());
				assertEquals("wrong service in service objects collection", comp1Tracker.getServiceReference(),
						co1.iterator().next().getServiceReference());
				assertTrue("service objects collection not mutable", co1.remove(co1.iterator().next()));
				Collection<Map<String, Object>> cp1 = t1.getCollectionProperties();
				assertNotNull("no service properties collection", cp1);
				assertFalse("service properties collection not replaced", cp1 instanceof TestList);
				assertEquals("wrong number of elements in service properties collection", 1, cp1.size());
				Map<String, Object> properties = cp1.iterator().next();
				assertTrue("service properties collection not mutable", cp1.remove(properties));
				assertNotNull("no properties injected", properties);
				assertEquals("wrong properties injected", c1.getProperties().get(ComponentConstants.COMPONENT_ID),
						properties.get(ComponentConstants.COMPONENT_ID));
				assertTrue("properties not Comparable", properties instanceof Comparable);
				try {
					properties.remove(ComponentConstants.COMPONENT_ID);
					failException("properties is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				try {
					properties.put("foo", "bar");
					failException("properties is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				Collection<Entry<Map<String, Object>, BaseService>> ct1 = t1.getCollectionTuple();
				assertNotNull("no service tuple collection", ct1);
				assertFalse("service tuple collection not replaced", ct1 instanceof TestList);
				assertEquals("wrong number of elements in service tuple collection", 1, ct1.size());
				Entry<Map<String, Object>, BaseService> tuple = ct1.iterator().next();
				assertTrue("service tuple collection not mutable", ct1.remove(tuple));
				Map<String, Object> key = tuple.getKey();
				assertNotNull("no tuple injected", key);
				assertEquals("wrong tuple injected", c1.getProperties().get(ComponentConstants.COMPONENT_ID),
						key.get(ComponentConstants.COMPONENT_ID));
				assertTrue("tuple not Comparable", tuple instanceof Comparable);
				assertTrue("tuple key not Comparable", key instanceof Comparable);
				try {
					key.remove(ComponentConstants.COMPONENT_ID);
					failException("tuple key is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				try {
					key.put("foo", "bar");
					failException("tuple key is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				assertEquals("properties not equal to tuple key", 0,
						asComparable(properties).compareTo(key));
				assertEquals("tuple keys not equal to properties", 0,
						asComparable(key).compareTo(properties));
				assertNotNull("tuple value null", tuple.getValue());
				assertSame("tuple value wrong", c1, tuple.getValue());

				List<BaseService> ls1 = t1.getListService();
				assertNotNull("no service list", ls1);
				assertFalse("service list not replaced", ls1 instanceof TestList);
				assertEquals("wrong number of elements in service list", 1, ls1.size());
				assertSame("wrong service in service list", c1, ls1.get(0));
				assertTrue("service list not mutable", ls1.remove(ls1.get(0)));
				List<ServiceReference<BaseService>> lr1 = t1.getListReference();
				assertNotNull("no service reference list", lr1);
				assertFalse("service reference list not replaced", lr1 instanceof TestList);
				assertEquals("wrong number of elements in service reference list", 1, lr1.size());
				assertEquals("wrong service in service reference list", comp1Tracker.getServiceReference(), lr1.get(0));
				assertTrue("service reference list not mutable", lr1.remove(lr1.get(0)));
				List<ComponentServiceObjects<BaseService>> lo1 = t1.getListServiceObjects();
				assertNotNull("no service objects list", lo1);
				assertFalse("service objects list not replaced", lo1 instanceof TestList);
				assertEquals("wrong number of elements in service objects list", 1, lo1.size());
				assertEquals("wrong service in service objects list", comp1Tracker.getServiceReference(),
						lo1.get(0).getServiceReference());
				assertSame("serviceobjects produced wrong service", c1, lo1.get(0).getService());
				assertTrue("service objects list not mutable", lo1.remove(lo1.get(0)));
				List<Map<String, Object>> lp1 = t1.getListProperties();
				assertNotNull("no service properties list", lp1);
				assertFalse("service properties list not replaced", lp1 instanceof TestList);
				assertEquals("wrong number of elements in service properties list", 1, lp1.size());
				properties = lp1.get(0);
				assertTrue("service properties list not mutable", lp1.remove(properties));
				assertNotNull("no properties injected", properties);
				assertEquals("wrong properties injected", c1.getProperties().get(ComponentConstants.COMPONENT_ID),
						properties.get(ComponentConstants.COMPONENT_ID));
				assertTrue("properties not Comparable", properties instanceof Comparable);
				try {
					properties.remove(ComponentConstants.COMPONENT_ID);
					failException("properties is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				try {
					properties.put("foo", "bar");
					failException("properties is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				List<Entry<Map<String, Object>, BaseService>> lt1 = t1.getListTuple();
				assertNotNull("no service tuple list", lt1);
				assertFalse("service tuple list not replaced", lt1 instanceof TestList);
				assertEquals("wrong number of elements in service tuple list", 1, lt1.size());
				assertTrue("tuple not Comparable", lt1.get(0) instanceof Comparable);
				assertEquals("tuples not equal", 0,
						asComparable(lt1.get(0)).compareTo(tuple));
				assertEquals("tuples not equal", 0,
						asComparable(tuple).compareTo(lt1.get(0)));
				tuple = lt1.get(0);
				assertTrue("service tuple list not mutable", lt1.remove(tuple));
				key = tuple.getKey();
				assertNotNull("no tuple injected", key);
				assertEquals("wrong tuple injected", c1.getProperties().get(ComponentConstants.COMPONENT_ID),
						key.get(ComponentConstants.COMPONENT_ID));
				assertTrue("tuple key not Comparable", key instanceof Comparable);
				try {
					key.remove(ComponentConstants.COMPONENT_ID);
					failException("tuple key is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				try {
					key.put("foo", "bar");
					failException("tuple key is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				assertEquals("properties not equal to tuple key", 0,
						asComparable(properties).compareTo(key));
				assertEquals("tuple keys not equal to properties", 0,
						asComparable(key).compareTo(properties));
				assertNotNull("tuple value null", tuple.getValue());
				assertSame("tuple value wrong", c1, tuple.getValue());
			}
			finally {
				comp1Tracker.close();
				test1Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb24);
		}
	}

	public void testStaticMultipleFieldReferenceModified130() throws Exception {
		ConfigurationAdmin cm = trackerCM.getService();
		assertNotNull("The ConfigurationAdmin should be available", cm);
		final String PID_ROOT = TEST_CASE_ROOT + ".tb24";

		Bundle tb24 = installBundle("tb24.jar", false);
		assertNotNull("tb24 failed to install", tb24);

		final String NAME = TEST_CASE_ROOT + ".tb24.StaticMultipleFieldReceiver";

		try {
			tb24.start();

			Filter comp1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + BaseService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=org.osgi.test.cases.component.tb24.Ranking1))");
			Filter test1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + MultipleFieldTestService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=" + NAME + "))");
			ServiceTracker<BaseService, BaseService> comp1Tracker = new ServiceTracker<BaseService, BaseService>(
					getContext(), comp1Filter, null);
			ServiceTracker<MultipleFieldTestService<BaseService>, MultipleFieldTestService<BaseService>> test1Tracker = new ServiceTracker<MultipleFieldTestService<BaseService>, MultipleFieldTestService<BaseService>>(
					getContext(), test1Filter, null);
			try {
				comp1Tracker.open();
				test1Tracker.open();
				BaseService c1 = comp1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing comp1", c1);
				MultipleFieldTestService<BaseService> t1 = test1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing test1", t1);
				assertEquals("wrong activation count", 1, t1.getActivationCount());
				assertEquals("wrong modification count", 0, t1.getModificationCount());
				assertEquals("wrong deactivation count", 0, t1.getDeactivationCount());

				Collection<BaseService> cs1 = t1.getCollectionService();
				assertNotNull("no service collection", cs1);
				assertEquals("wrong number of elements in service collection", 1, cs1.size());
				assertSame("wrong service in service collection", c1, cs1.iterator().next());
				Collection<ServiceReference<BaseService>> cr1 = t1.getCollectionReference();
				assertNotNull("no service reference collection", cr1);
				assertEquals("wrong number of elements in service reference collection", 1, cr1.size());
				assertEquals("wrong property value", "xml", cr1.iterator().next().getProperty(PID_ROOT));
				Collection<ComponentServiceObjects<BaseService>> co1 = t1.getCollectionServiceObjects();
				assertNotNull("no service objects collection", co1);
				assertEquals("wrong number of elements in service objects collection", 1, co1.size());
				assertEquals("wrong property value", "xml",
						co1.iterator().next().getServiceReference().getProperty(PID_ROOT));
				Collection<Map<String, Object>> cp1 = t1.getCollectionProperties();
				assertNotNull("no service properties collection", cp1);
				assertEquals("wrong number of elements in service properties collection", 1, cp1.size());
				Map<String, Object> properties = cp1.iterator().next();
				assertNotNull("no properties injected", properties);
				assertEquals("wrong property value", "xml", properties.get(PID_ROOT));
				Collection<Entry<Map<String, Object>, BaseService>> ct1 = t1.getCollectionTuple();
				assertNotNull("no service tuple collection", ct1);
				assertEquals("wrong number of elements in service tuple collection", 1, ct1.size());
				Map<String, Object> key = ct1.iterator().next().getKey();
				assertNotNull("no tuple injected", key);
				assertEquals("wrong property value", "xml", key.get(PID_ROOT));

				List<BaseService> ls1 = t1.getListService();
				assertNotNull("no service list", ls1);
				assertEquals("wrong number of elements in service list", 1, ls1.size());
				assertSame("wrong service in service list", c1, ls1.get(0));
				List<ServiceReference<BaseService>> lr1 = t1.getListReference();
				assertNotNull("no service reference list", lr1);
				assertEquals("wrong number of elements in service reference list", 1, lr1.size());
				assertEquals("wrong property value", "xml", lr1.iterator().next().getProperty(PID_ROOT));
				List<ComponentServiceObjects<BaseService>> lo1 = t1.getListServiceObjects();
				assertNotNull("no service objects list", lo1);
				assertEquals("wrong number of elements in service objects list", 1, lo1.size());
				assertEquals("wrong property value", "xml",
						lo1.iterator().next().getServiceReference().getProperty(PID_ROOT));
				List<Map<String, Object>> lp1 = t1.getListProperties();
				assertNotNull("no service properties list", lp1);
				assertEquals("wrong number of elements in service properties list", 1, lp1.size());
				properties = lp1.get(0);
				assertNotNull("no properties injected", properties);
				assertEquals("wrong property value", "xml", properties.get(PID_ROOT));
				List<Entry<Map<String, Object>, BaseService>> lt1 = t1.getListTuple();
				assertNotNull("no service tuple list", lt1);
				assertEquals("wrong number of elements in service tuple list", 1, lt1.size());
				key = lt1.get(0).getKey();
				assertNotNull("no tuple injected", key);
				assertEquals("wrong property value", "xml", key.get(PID_ROOT));

				Configuration config = cm.getConfiguration("org.osgi.test.cases.component.tb24.Ranking1", null);
				Dictionary<String, Object> props = new Hashtable<String, Object>();
				props.put(PID_ROOT, "config");
				config.update(props);
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				assertEquals("wrong service property value", "config", c1.getProperties().get(PID_ROOT));
				MultipleFieldTestService<BaseService> t2 = test1Tracker.waitForService(SLEEP * 3);
				assertNotSame("test service not reactivated", t1, t2);
				assertEquals("wrong activation count", 2, t2.getActivationCount());
				assertEquals("wrong modification count", 0, t2.getModificationCount());
				assertEquals("wrong deactivation count", 1, t2.getDeactivationCount());

				Collection<BaseService> cs2 = t2.getCollectionService();
				assertNotNull("no service collection", cs2);
				assertEquals("wrong number of elements in service collection", 1, cs2.size());
				assertSame("wrong service in service collection", c1, cs2.iterator().next());
				Collection<ServiceReference<BaseService>> cr2 = t2.getCollectionReference();
				assertNotNull("no service reference collection", cr2);
				assertEquals("wrong number of elements in service reference collection", 1, cr2.size());
				assertEquals("wrong property value", "config", cr2.iterator().next().getProperty(PID_ROOT));
				Collection<ComponentServiceObjects<BaseService>> co2 = t2.getCollectionServiceObjects();
				assertNotNull("no service objects collection", co2);
				assertEquals("wrong number of elements in service objects collection", 1, co2.size());
				assertEquals("wrong property value", "config",
						co2.iterator().next().getServiceReference().getProperty(PID_ROOT));
				Collection<Map<String, Object>> cp2 = t2.getCollectionProperties();
				assertNotNull("no service properties collection", cp2);
				assertNotSame("service properties collection not replaced", cp1, cp2);
				assertEquals("wrong number of elements in service properties collection", 1, cp2.size());
				properties = cp2.iterator().next();
				assertNotNull("no properties injected", properties);
				assertEquals("wrong property value", "config", properties.get(PID_ROOT));
				Collection<Entry<Map<String, Object>, BaseService>> ct2 = t2.getCollectionTuple();
				assertNotNull("no service tuple collection", ct2);
				assertNotSame("service tuple collection not replaced", ct1, ct2);
				assertEquals("wrong number of elements in service tuple collection", 1, ct2.size());
				key = ct2.iterator().next().getKey();
				assertNotNull("no tuple injected", key);
				assertEquals("wrong property value", "config", key.get(PID_ROOT));

				List<BaseService> ls2 = t2.getListService();
				assertNotNull("no service list", ls2);
				assertEquals("wrong number of elements in service list", 1, ls2.size());
				assertSame("wrong service in service list", c1, ls2.get(0));
				List<ServiceReference<BaseService>> lr2 = t2.getListReference();
				assertNotNull("no service reference list", lr2);
				assertEquals("wrong number of elements in service reference list", 1, lr2.size());
				assertEquals("wrong property value", "config", lr2.iterator().next().getProperty(PID_ROOT));
				List<ComponentServiceObjects<BaseService>> lo2 = t2.getListServiceObjects();
				assertNotNull("no service objects list", lo2);
				assertEquals("wrong number of elements in service objects list", 1, lo2.size());
				assertEquals("wrong property value", "config",
						lo2.iterator().next().getServiceReference().getProperty(PID_ROOT));
				List<Map<String, Object>> lp2 = t2.getListProperties();
				assertNotNull("no service properties list", lp2);
				assertNotSame("service properties list not replaced", lp1, lp2);
				assertEquals("wrong number of elements in service properties list", 1, lp2.size());
				properties = lp2.get(0);
				assertNotNull("no properties injected", properties);
				assertEquals("wrong property value", "config", properties.get(PID_ROOT));
				List<Entry<Map<String, Object>, BaseService>> lt2 = t2.getListTuple();
				assertNotNull("no service tuple list", lt2);
				assertNotSame("service tuple list not replaced", lt1, lt2);
				assertEquals("wrong number of elements in service tuple list", 1, lt2.size());
				key = lt2.get(0).getKey();
				assertNotNull("no tuple injected", key);
				assertEquals("wrong property value", "config", key.get(PID_ROOT));

			}
			finally {
				comp1Tracker.close();
				test1Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb24);
		}
	}

	public void testUpdateStaticMultipleFieldReference130() throws Exception {
		Bundle tb24 = installBundle("tb24.jar", false);
		assertNotNull("tb24 failed to install", tb24);

		final String NAME = TEST_CASE_ROOT + ".tb24.UpdateStaticMultipleFieldReceiver";

		try {
			tb24.start();

			Filter comp1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + BaseService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=org.osgi.test.cases.component.tb24.Ranking1))");
			Filter test1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + MultipleFieldTestService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=" + NAME + "))");
			ServiceTracker<BaseService, BaseService> comp1Tracker = new ServiceTracker<BaseService, BaseService>(
					getContext(), comp1Filter, null);
			ServiceTracker<MultipleFieldTestService<BaseService>, MultipleFieldTestService<BaseService>> test1Tracker = new ServiceTracker<MultipleFieldTestService<BaseService>, MultipleFieldTestService<BaseService>>(
					getContext(), test1Filter, null);
			try {
				comp1Tracker.open();
				test1Tracker.open();
				BaseService c1 = comp1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing comp1", c1);
				MultipleFieldTestService<BaseService> t1 = test1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing test1", t1);
				assertEquals("wrong activation count", 1, t1.getActivationCount());
				assertEquals("wrong modification count", 0, t1.getModificationCount());
				assertEquals("wrong deactivation count", 0, t1.getDeactivationCount());

				Collection<BaseService> cs1 = t1.getCollectionService();
				assertNotNull("no service collection", cs1);
				assertEquals("wrong number of elements in service collection", 0, cs1.size());
				assertTrue("service collection replaced", cs1 instanceof TestList);
				Collection<ServiceReference<BaseService>> cr1 = t1.getCollectionReference();
				assertNotNull("no service reference collection", cr1);
				assertEquals("wrong number of elements in service reference collection", 0, cr1.size());
				assertTrue("service reference collection replaced", cr1 instanceof TestList);
				Collection<ComponentServiceObjects<BaseService>> co1 = t1.getCollectionServiceObjects();
				assertNotNull("no service objects collection", co1);
				assertEquals("wrong number of elements in service objects collection", 0, co1.size());
				assertTrue("service objects collection replaced", co1 instanceof TestList);
				Collection<Map<String, Object>> cp1 = t1.getCollectionProperties();
				assertNotNull("no service properties collection", cp1);
				assertEquals("wrong number of elements in service properties collection", 0, cp1.size());
				assertTrue("service properties collection replaced", cp1 instanceof TestList);
				Collection<Entry<Map<String, Object>, BaseService>> ct1 = t1.getCollectionTuple();
				assertNotNull("no service tuple collection", ct1);
				assertEquals("wrong number of elements in service tuple collection", 0, ct1.size());
				assertTrue("service tuple collection replaced", ct1 instanceof TestList);

				List<BaseService> ls1 = t1.getListService();
				assertNotNull("no service list", ls1);
				assertEquals("wrong number of elements in service list", 0, ls1.size());
				assertTrue("service list replaced", ls1 instanceof TestList);
				List<ServiceReference<BaseService>> lr1 = t1.getListReference();
				assertNotNull("no service reference list", lr1);
				assertEquals("wrong number of elements in service reference list", 0, lr1.size());
				assertTrue("service reference list replaced", lr1 instanceof TestList);
				List<ComponentServiceObjects<BaseService>> lo1 = t1.getListServiceObjects();
				assertNotNull("no service objects list", lo1);
				assertEquals("wrong number of elements in service objects list", 0, lo1.size());
				assertTrue("service objects list replaced", lo1 instanceof TestList);
				List<Map<String, Object>> lp1 = t1.getListProperties();
				assertNotNull("no service properties list", lp1);
				assertEquals("wrong number of elements in service properties list", 0, lp1.size());
				assertTrue("service properties list replaced", lp1 instanceof TestList);
				List<Entry<Map<String, Object>, BaseService>> lt1 = t1.getListTuple();
				assertNotNull("no service tuple list", lt1);
				assertEquals("wrong number of elements in service tuple list", 0, lt1.size());
				assertTrue("service tuple list replaced", lt1 instanceof TestList);

			}
			finally {
				comp1Tracker.close();
				test1Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb24);
		}
	}

	@SuppressWarnings("unchecked")
	public void testOptionalStaticMultipleFieldReference130() throws Exception {
		ServiceComponentRuntime scr = scrTracker.getService();
		assertNotNull("failed to find ServiceComponentRuntime service", scr);
		Bundle tb24 = installBundle("tb24.jar", false);
		assertNotNull("tb24 failed to install", tb24);

		final String NAME = TEST_CASE_ROOT + ".tb24.OptionalStaticMultipleFieldReceiver";

		try {
			tb24.start();

			ComponentDescriptionDTO description1 = scr.getComponentDescriptionDTO(tb24,
					"org.osgi.test.cases.component.tb24.Ranking10");
			assertNotNull("null description1", description1);
			assertFalse("description1 is enabled", scr.isComponentEnabled(description1));

			Filter comp1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + BaseService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=org.osgi.test.cases.component.tb24.Ranking10))");
			Filter test1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + MultipleFieldTestService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=" + NAME + "))");
			ServiceTracker<BaseService, BaseService> comp1Tracker = new ServiceTracker<BaseService, BaseService>(
					getContext(), comp1Filter, null);
			ServiceTracker<MultipleFieldTestService<BaseService>, MultipleFieldTestService<BaseService>> test1Tracker = new ServiceTracker<MultipleFieldTestService<BaseService>, MultipleFieldTestService<BaseService>>(
					getContext(), test1Filter, null);
			try {
				comp1Tracker.open();
				test1Tracker.open();
				BaseService c1 = comp1Tracker.waitForService(SLEEP * 3);
				assertNull("found comp1", c1);
				MultipleFieldTestService<BaseService> t1 = test1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing test1", t1);
				assertEquals("wrong activation count", 1, t1.getActivationCount());
				assertEquals("wrong modification count", 0, t1.getModificationCount());
				assertEquals("wrong deactivation count", 0, t1.getDeactivationCount());

				Collection<BaseService> cs1 = t1.getCollectionService();
				assertNotNull("no service collection", cs1);
				assertEquals("wrong number of elements in service collection", 0, cs1.size());
				assertFalse("service collection not replaced", cs1 instanceof TestList);
				assertTrue("service collection not mutable", cs1.add(MockFactory.newMock(BaseService.class, null)));
				Collection<ServiceReference<BaseService>> cr1 = t1.getCollectionReference();
				assertNotNull("no service reference collection", cr1);
				assertEquals("wrong number of elements in service reference collection", 0, cr1.size());
				assertFalse("service reference collection not replaced", cr1 instanceof TestList);
				assertTrue("service reference collection not mutable", cr1.add(comp1Tracker.getServiceReference()));
				Collection<ComponentServiceObjects<BaseService>> co1 = t1.getCollectionServiceObjects();
				assertNotNull("no service objects collection", co1);
				assertEquals("wrong number of elements in service objects collection", 0, co1.size());
				assertFalse("service objects collection not replaced", co1 instanceof TestList);
				assertTrue("service objects collection not mutable",
						co1.add(MockFactory.newMock(ComponentServiceObjects.class, null)));
				Collection<Map<String, Object>> cp1 = t1.getCollectionProperties();
				assertNotNull("no service properties collection", cp1);
				assertEquals("wrong number of elements in service properties collection", 0, cp1.size());
				assertFalse("service properties collection not replaced", cp1 instanceof TestList);
				assertTrue("service properties collection not mutable", cp1.add(MockFactory.newMock(Map.class, null)));
				Collection<Entry<Map<String, Object>, BaseService>> ct1 = t1.getCollectionTuple();
				assertNotNull("no service tuple collection", ct1);
				assertEquals("wrong number of elements in service tuple collection", 0, ct1.size());
				assertFalse("service tuple collection not replaced", ct1 instanceof TestList);
				assertTrue("service tuple collection not mutable", ct1.add(MockFactory.newMock(Entry.class, null)));

				List<BaseService> ls1 = t1.getListService();
				assertNotNull("no service list", ls1);
				assertEquals("wrong number of elements in service list", 0, ls1.size());
				assertFalse("service list not replaced", ls1 instanceof TestList);
				assertTrue("service list not mutable", ls1.add(MockFactory.newMock(BaseService.class, null)));
				List<ServiceReference<BaseService>> lr1 = t1.getListReference();
				assertNotNull("no service reference list", lr1);
				assertEquals("wrong number of elements in service reference list", 0, lr1.size());
				assertFalse("service reference list not replaced", lr1 instanceof TestList);
				assertTrue("service reference list not mutable", lr1.add(comp1Tracker.getServiceReference()));
				List<ComponentServiceObjects<BaseService>> lo1 = t1.getListServiceObjects();
				assertNotNull("no service objects list", lo1);
				assertEquals("wrong number of elements in service objects list", 0, lo1.size());
				assertFalse("service objects list not replaced", lo1 instanceof TestList);
				assertTrue("service objects list not mutable",
						lo1.add(MockFactory.newMock(ComponentServiceObjects.class, null)));
				List<Map<String, Object>> lp1 = t1.getListProperties();
				assertNotNull("no service properties list", lp1);
				assertEquals("wrong number of elements in service properties list", 0, lp1.size());
				assertFalse("service properties list not replaced", lp1 instanceof TestList);
				assertTrue("service properties list not mutable", lp1.add(MockFactory.newMock(Map.class, null)));
				List<Entry<Map<String, Object>, BaseService>> lt1 = t1.getListTuple();
				assertNotNull("no service tuple list", lt1);
				assertEquals("wrong number of elements in service tuple list", 0, lt1.size());
				assertFalse("service tuple list not replaced", lt1 instanceof TestList);
				assertTrue("service tuple list not mutable", lt1.add(MockFactory.newMock(Entry.class, null)));

				Promise<Void> p = scr.enableComponent(description1);
				p.getValue(); // wait for state change to complete
				assertTrue("description1 is not enabled", scr.isComponentEnabled(description1));

				c1 = comp1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing comp1", c1);
				MultipleFieldTestService<BaseService> t2 = test1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing test1", t2);
				assertNotSame("not reactivated", t2, t1);
				assertEquals("wrong activation count", 2, t2.getActivationCount());
				assertEquals("wrong modification count", 0, t2.getModificationCount());
				assertEquals("wrong deactivation count", 1, t2.getDeactivationCount());

				Collection<BaseService> cs2 = t2.getCollectionService();
				assertNotNull("no service collection", cs2);
				assertNotSame("service collection not replaced", cs1, cs2);
				assertEquals("wrong number of elements in service collection", 1, cs2.size());
				assertFalse("service collection not replaced", cs2 instanceof TestList);
				assertSame("wrong service in service collection", c1, cs2.iterator().next());
				Collection<ServiceReference<BaseService>> cr2 = t2.getCollectionReference();
				assertNotNull("no service reference collection", cr2);
				assertNotSame("service reference collection not replaced", cr1, cr2);
				assertEquals("wrong number of elements in service reference collection", 1, cr2.size());
				assertFalse("service reference collection not replaced", cr2 instanceof TestList);
				Collection<ComponentServiceObjects<BaseService>> co2 = t2.getCollectionServiceObjects();
				assertNotNull("no service objects collection", co2);
				assertNotSame("service objects collection not replaced", co1, co2);
				assertEquals("wrong number of elements in service objects collection", 1, co2.size());
				assertFalse("service objects collection not replaced", co2 instanceof TestList);
				Collection<Map<String, Object>> cp2 = t2.getCollectionProperties();
				assertNotNull("no service properties collection", cp2);
				assertNotSame("service properties collection not replaced", cp1, cp2);
				assertEquals("wrong number of elements in service properties collection", 1, cp2.size());
				assertFalse("service properties collection not replaced", cp2 instanceof TestList);
				Map<String, Object> properties = cp2.iterator().next();
				assertNotNull("no properties injected", properties);
				Collection<Entry<Map<String, Object>, BaseService>> ct2 = t2.getCollectionTuple();
				assertNotSame("service tuple collection not replaced", ct1, ct2);
				assertNotNull("no service tuple collection", ct2);
				assertEquals("wrong number of elements in service tuple collection", 1, ct2.size());
				assertFalse("service tuple collection not replaced", ct2 instanceof TestList);
				Map<String, Object> key = ct2.iterator().next().getKey();
				assertNotNull("no tuple injected", key);

				List<BaseService> ls2 = t2.getListService();
				assertNotNull("no service list", ls2);
				assertNotSame("service list not replaced", ls1, ls2);
				assertEquals("wrong number of elements in service list", 1, ls2.size());
				assertFalse("service list not replaced", ls2 instanceof TestList);
				assertSame("wrong service in service list", c1, ls2.get(0));
				List<ServiceReference<BaseService>> lr2 = t2.getListReference();
				assertNotNull("no service reference list", lr2);
				assertNotSame("service reference list not replaced", lr1, lr2);
				assertEquals("wrong number of elements in service reference list", 1, lr2.size());
				assertFalse("service reference list not replaced", lr2 instanceof TestList);
				List<ComponentServiceObjects<BaseService>> lo2 = t2.getListServiceObjects();
				assertNotNull("no service objects list", lo2);
				assertNotSame("service objects list not replaced", lo1, lo2);
				assertEquals("wrong number of elements in service objects list", 1, lo2.size());
				assertFalse("service objects list not replaced", lo2 instanceof TestList);
				List<Map<String, Object>> lp2 = t2.getListProperties();
				assertNotNull("no service properties list", lp2);
				assertNotSame("service properties list not replaced", lp1, lp2);
				assertEquals("wrong number of elements in service properties list", 1, lp2.size());
				assertFalse("service properties list not replaced", lp2 instanceof TestList);
				properties = lp2.get(0);
				assertNotNull("no properties injected", properties);
				List<Entry<Map<String, Object>, BaseService>> lt2 = t2.getListTuple();
				assertNotNull("no service tuple list", lt2);
				assertNotSame("service tuple list not replaced", lt1, lt2);
				assertEquals("wrong number of elements in service tuple list", 1, lt2.size());
				assertFalse("service tuple list not replaced", lt2 instanceof TestList);
				key = lt2.get(0).getKey();
				assertNotNull("no tuple injected", key);

				p = scr.disableComponent(description1);
				p.getValue(); // wait for state change to complete
				assertFalse("description1 is not enabled", scr.isComponentEnabled(description1));

				c1 = comp1Tracker.waitForService(SLEEP * 3);
				assertNull("found comp1", c1);
				MultipleFieldTestService<BaseService> t3 = test1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing test3", t3);
				assertNotSame("not reactivated", t3, t1);
				assertNotSame("not reactivated", t3, t2);
				assertEquals("wrong activation count", 3, t3.getActivationCount());
				assertEquals("wrong modification count", 0, t3.getModificationCount());
				assertEquals("wrong deactivation count", 2, t3.getDeactivationCount());

				Collection<BaseService> cs3 = t3.getCollectionService();
				assertNotNull("no service collection", cs3);
				assertEquals("wrong number of elements in service collection", 0, cs3.size());
				assertFalse("service collection not replaced", cs3 instanceof TestList);
				assertNotSame("service collection not replaced", cs1, cs3);
				assertNotSame("service collection not replaced", cs2, cs3);
				Collection<ServiceReference<BaseService>> cr3 = t3.getCollectionReference();
				assertNotNull("no service reference collection", cr3);
				assertEquals("wrong number of elements in service reference collection", 0, cr3.size());
				assertFalse("service reference collection not replaced", cr3 instanceof TestList);
				assertNotSame("service reference collection not replaced", cr1, cr3);
				assertNotSame("service reference collection not replaced", cr2, cr3);
				Collection<ComponentServiceObjects<BaseService>> co3 = t3.getCollectionServiceObjects();
				assertNotNull("no service objects collection", co3);
				assertEquals("wrong number of elements in service objects collection", 0, co3.size());
				assertFalse("service objects collection not replaced", co3 instanceof TestList);
				assertNotSame("service objects collection not replaced", co1, co3);
				assertNotSame("service objects collection not replaced", co2, co3);
				Collection<Map<String, Object>> cp3 = t3.getCollectionProperties();
				assertNotNull("no service properties collection", cp3);
				assertEquals("wrong number of elements in service properties collection", 0, cp3.size());
				assertFalse("service properties collection not replaced", cp3 instanceof TestList);
				assertNotSame("service properties collection not replaced", cp1, cp3);
				assertNotSame("service properties collection not replaced", cp2, cp3);
				Collection<Entry<Map<String, Object>, BaseService>> ct3 = t3.getCollectionTuple();
				assertNotNull("no service tuple collection", ct3);
				assertEquals("wrong number of elements in service tuple collection", 0, ct3.size());
				assertFalse("service tuple collection not replaced", ct3 instanceof TestList);
				assertNotSame("service tuple collection not replaced", ct1, ct3);
				assertNotSame("service tuple collection not replaced", ct2, ct3);

				List<BaseService> ls3 = t3.getListService();
				assertNotNull("no service list", ls3);
				assertEquals("wrong number of elements in service list", 0, ls3.size());
				assertFalse("service list not replaced", ls3 instanceof TestList);
				assertNotSame("service list not replaced", ls1, ls3);
				assertNotSame("service list not replaced", ls2, ls3);
				List<ServiceReference<BaseService>> lr3 = t3.getListReference();
				assertNotNull("no service reference list", lr3);
				assertEquals("wrong number of elements in service reference list", 0, lr3.size());
				assertFalse("service reference list not replaced", lr3 instanceof TestList);
				assertNotSame("service reference list not replaced", lr1, lr3);
				assertNotSame("service reference list not replaced", lr2, lr3);
				List<ComponentServiceObjects<BaseService>> lo3 = t3.getListServiceObjects();
				assertNotNull("no service objects list", lo3);
				assertEquals("wrong number of elements in service objects list", 0, lo3.size());
				assertFalse("service objects list not replaced", lo3 instanceof TestList);
				assertNotSame("service objects list not replaced", lo1, lo3);
				assertNotSame("service objects list not replaced", lo2, lo3);
				List<Map<String, Object>> lp3 = t3.getListProperties();
				assertNotNull("no service properties list", lp3);
				assertEquals("wrong number of elements in service properties list", 0, lp3.size());
				assertFalse("service properties list not replaced", lp3 instanceof TestList);
				assertNotSame("service properties list not replaced", lp1, lp3);
				assertNotSame("service properties list not replaced", lp2, lp3);
				List<Entry<Map<String, Object>, BaseService>> lt3 = t3.getListTuple();
				assertNotNull("no service tuple list", lt3);
				assertEquals("wrong number of elements in service tuple list", 0, lt3.size());
				assertFalse("service tuple list not replaced", lt3 instanceof TestList);
				assertNotSame("service tuple list not replaced", lt1, lt3);
				assertNotSame("service tuple list not replaced", lt2, lt3);

			}
			finally {
				comp1Tracker.close();
				test1Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb24);
		}
	}

	public void testSortingStaticMultipleFieldReference130() throws Exception {
		ServiceComponentRuntime scr = scrTracker.getService();
		assertNotNull("failed to find ServiceComponentRuntime service", scr);
		Bundle tb24 = installBundle("tb24.jar", false);
		assertNotNull("tb24 failed to install", tb24);

		final String NAME = TEST_CASE_ROOT + ".tb24.StaticMultipleFieldReceiver";

		try {
			tb24.start();

			ComponentDescriptionDTO description1 = scr.getComponentDescriptionDTO(tb24,
					"org.osgi.test.cases.component.tb24.Ranking10");
			assertNotNull("null description1", description1);
			assertFalse("description1 is enabled", scr.isComponentEnabled(description1));

			Filter comp1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + BaseService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=org.osgi.test.cases.component.tb24.Ranking1))");
			Filter comp10Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + BaseService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=org.osgi.test.cases.component.tb24.Ranking10))");
			Filter test1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + MultipleFieldTestService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=" + NAME + "))");
			ServiceTracker<BaseService, BaseService> comp1Tracker = new ServiceTracker<BaseService, BaseService>(
					getContext(), comp1Filter, null);
			ServiceTracker<BaseService, BaseService> comp10Tracker = new ServiceTracker<BaseService, BaseService>(
					getContext(), comp10Filter, null);
			ServiceTracker<MultipleFieldTestService<BaseService>, MultipleFieldTestService<BaseService>> test1Tracker = new ServiceTracker<MultipleFieldTestService<BaseService>, MultipleFieldTestService<BaseService>>(
					getContext(), test1Filter, null);
			try {
				comp1Tracker.open();
				comp10Tracker.open();
				test1Tracker.open();
				BaseService c1 = comp1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing comp1", c1);
				BaseService c10 = comp10Tracker.waitForService(SLEEP * 3);
				assertNull("found comp10", c10);
				MultipleFieldTestService<BaseService> t1 = test1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing test1", t1);
				assertEquals("wrong activation count", 1, t1.getActivationCount());
				assertEquals("wrong modification count", 0, t1.getModificationCount());
				assertEquals("wrong deactivation count", 0, t1.getDeactivationCount());

				Collection<BaseService> cs1 = t1.getCollectionService();
				assertNotNull("no service collection", cs1);
				assertEquals("wrong number of elements in service collection", 1, cs1.size());
				Collection<ServiceReference<BaseService>> cr1 = t1.getCollectionReference();
				assertNotNull("no service reference collection", cr1);
				assertEquals("wrong number of elements in service reference collection", 1, cr1.size());
				Collection<ComponentServiceObjects<BaseService>> co1 = t1.getCollectionServiceObjects();
				assertNotNull("no service objects collection", co1);
				assertEquals("wrong number of elements in service objects collection", 1, co1.size());
				Collection<Map<String, Object>> cp1 = t1.getCollectionProperties();
				assertNotNull("no service properties collection", cp1);
				assertEquals("wrong number of elements in service properties collection", 1, cp1.size());
				Collection<Entry<Map<String, Object>, BaseService>> ct1 = t1.getCollectionTuple();
				assertNotNull("no service tuple collection", ct1);
				assertEquals("wrong number of elements in service tuple collection", 1, ct1.size());

				List<BaseService> ls1 = t1.getListService();
				assertNotNull("no service list", ls1);
				assertEquals("wrong number of elements in service list", 1, ls1.size());
				List<ServiceReference<BaseService>> lr1 = t1.getListReference();
				assertNotNull("no service reference list", lr1);
				assertEquals("wrong number of elements in service reference list", 1, lr1.size());
				List<ComponentServiceObjects<BaseService>> lo1 = t1.getListServiceObjects();
				assertNotNull("no service objects list", lo1);
				assertEquals("wrong number of elements in service objects list", 1, lo1.size());
				List<Map<String, Object>> lp1 = t1.getListProperties();
				assertNotNull("no service properties list", lp1);
				assertEquals("wrong number of elements in service properties list", 1, lp1.size());
				List<Entry<Map<String, Object>, BaseService>> lt1 = t1.getListTuple();
				assertNotNull("no service tuple list", lt1);
				assertEquals("wrong number of elements in service tuple list", 1, lt1.size());

				Promise<Void> p = scr.enableComponent(description1);
				p.getValue(); // wait for state change to complete
				assertTrue("description1 is not enabled", scr.isComponentEnabled(description1));

				c10 = comp10Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing comp10", c10);
				MultipleFieldTestService<BaseService> t2 = test1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing test1", t2);
				assertNotSame("not reactivated", t2, t1);
				assertEquals("wrong activation count", 2, t2.getActivationCount());
				assertEquals("wrong modification count", 0, t2.getModificationCount());
				assertEquals("wrong deactivation count", 1, t2.getDeactivationCount());

				Collection<BaseService> cs2 = t2.getCollectionService();
				assertNotNull("no service collection", cs2);
				assertNotSame("service collection not replaced", cs1, cs2);
				assertEquals("wrong number of elements in service collection", 2, cs2.size());
				assertEquals("wrong order in service collection", Arrays.asList(c1, c10), cs2);
				Collection<ServiceReference<BaseService>> cr2 = t2.getCollectionReference();
				assertNotNull("no service reference collection", cr2);
				assertNotSame("service reference collection not replaced", cr1, cr2);
				assertEquals("wrong number of elements in service reference collection", 2, cr2.size());
				assertEquals("wrong order in service reference collection",
						Arrays.asList(comp1Tracker.getServiceReference(), comp10Tracker.getServiceReference()), cr2);
				Collection<ComponentServiceObjects<BaseService>> co2 = t2.getCollectionServiceObjects();
				assertNotNull("no service objects collection", co2);
				assertNotSame("service objects collection not replaced", co1, co2);
				assertEquals("wrong number of elements in service objects collection", 2, co2.size());
				Iterator<ComponentServiceObjects<BaseService>> co2i = co2.iterator();
				assertEquals("wrong order in service objects collection",
						Arrays.asList(comp1Tracker.getServiceReference(), comp10Tracker.getServiceReference()),
						Arrays.asList(co2i.next().getServiceReference(), co2i.next().getServiceReference()));
				Collection<Map<String, Object>> cp2 = t2.getCollectionProperties();
				assertNotNull("no service properties collection", cp2);
				assertNotSame("service properties collection not replaced", cp1, cp2);
				assertEquals("wrong number of elements in service properties collection", 2, cp2.size());
				Iterator<Map<String, Object>> cp2i = cp2.iterator();
				assertTrue("wrong order in service properties collection",
						asComparable(cp2i.next()).compareTo(cp2i.next()) < 0);
				Collection<Entry<Map<String, Object>, BaseService>> ct2 = t2.getCollectionTuple();
				assertNotNull("no service tuple collection", ct2);
				assertNotSame("service tuple collection not replaced", ct1, ct2);
				assertEquals("wrong number of elements in service tuple collection", 2, ct2.size());
				Iterator<Entry<Map<String, Object>, BaseService>> ct2i = ct2.iterator();
				assertTrue("wrong order in service tuple collection",
						asComparable(ct2i.next()).compareTo(ct2i.next()) < 0);

				List<BaseService> ls2 = t2.getListService();
				assertNotNull("no service list", ls2);
				assertNotSame("service list not replaced", ls1, ls2);
				assertEquals("wrong number of elements in service list", 2, ls2.size());
				assertEquals("wrong order in service list", Arrays.asList(c1, c10), ls2);
				List<ServiceReference<BaseService>> lr2 = t2.getListReference();
				assertNotNull("no service reference list", lr2);
				assertNotSame("service reference list not replaced", lr1, lr2);
				assertEquals("wrong number of elements in service reference list", 2, lr2.size());
				assertEquals("wrong order in service reference list",
						Arrays.asList(comp1Tracker.getServiceReference(), comp10Tracker.getServiceReference()), lr2);
				List<ComponentServiceObjects<BaseService>> lo2 = t2.getListServiceObjects();
				assertNotNull("no service objects list", lo2);
				assertNotSame("service objects list not replaced", lo1, lo2);
				assertEquals("wrong number of elements in service objects list", 2, lo2.size());
				assertEquals("wrong order in service objects list",
						Arrays.asList(comp1Tracker.getServiceReference(), comp10Tracker.getServiceReference()),
						Arrays.asList(lo2.get(0).getServiceReference(), lo2.get(1).getServiceReference()));
				List<Map<String, Object>> lp2 = t2.getListProperties();
				assertNotNull("no service properties list", lp2);
				assertNotSame("service properties list not replaced", lp1, lp2);
				assertEquals("wrong number of elements in service properties list", 2, lp2.size());
				assertTrue("wrong order in service properties list",
						asComparable(lp2.get(0)).compareTo(lp2.get(1)) < 0);
				List<Entry<Map<String, Object>, BaseService>> lt2 = t2.getListTuple();
				assertNotNull("no service tuple list", lt2);
				assertNotSame("service tuple list not replaced", lt1, lt2);
				assertEquals("wrong number of elements in service tuple list", 2, lt2.size());
				assertTrue("wrong order in service tuple list",
						asComparable(lt2.get(0)).compareTo(lt2.get(1)) < 0);
			}
			finally {
				comp1Tracker.close();
				comp10Tracker.close();
				test1Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb24);
		}
	}

	public void testNonInstanceFieldReference130() throws Exception {
		Bundle tb24 = installBundle("tb24.jar", false);
		assertNotNull("tb24 failed to install", tb24);

		final String NAME = TEST_CASE_ROOT + ".tb24.NonInstanceFieldReceiver";

		try {
			tb24.start();

			Filter comp1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + BaseService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=org.osgi.test.cases.component.tb24.Ranking1))");
			Filter test1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + ScalarFieldTestService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=" + NAME + "))");
			ServiceTracker<BaseService, BaseService> comp1Tracker = new ServiceTracker<BaseService, BaseService>(
					getContext(), comp1Filter, null);
			ServiceTracker<ScalarFieldTestService<BaseService>, ScalarFieldTestService<BaseService>> test1Tracker = new ServiceTracker<ScalarFieldTestService<BaseService>, ScalarFieldTestService<BaseService>>(
					getContext(), test1Filter, null);
			try {
				comp1Tracker.open();
				test1Tracker.open();
				BaseService c1 = comp1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing comp1", c1);
				ScalarFieldTestService<BaseService> s1 = test1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing test1", s1);
				assertEquals("wrong activation count", 1, s1.getActivationCount());
				assertEquals("wrong modification count", 0, s1.getModificationCount());
				assertEquals("wrong deactivation count", 0, s1.getDeactivationCount());

				assertNull("service injected", s1.getService());
				assertNull("service injected", s1.getAssignable());
				assertNull("reference injected", s1.getReference());
				assertNull("serviceobjects injected", s1.getServiceObjects());
				assertNull("properties injected", s1.getProperties());
				assertNull("tuple injected", s1.getTuple());

				@SuppressWarnings("unchecked")
				MultipleFieldTestService<BaseService> t1 = (MultipleFieldTestService<BaseService>) s1;
				Collection<BaseService> cs1 = t1.getCollectionService();
				assertNotNull("no service collection", cs1);
				assertEquals("wrong number of elements in service collection", 0, cs1.size());
				assertTrue("service collection replaced", cs1 instanceof TestList);
				Collection<ServiceReference<BaseService>> cr1 = t1.getCollectionReference();
				assertNotNull("no service reference collection", cr1);
				assertEquals("wrong number of elements in service reference collection", 0, cr1.size());
				assertTrue("service reference collection replaced", cr1 instanceof TestList);
				Collection<ComponentServiceObjects<BaseService>> co1 = t1.getCollectionServiceObjects();
				assertNotNull("no service objects collection", co1);
				assertEquals("wrong number of elements in service objects collection", 0, co1.size());
				assertTrue("service objects collection replaced", co1 instanceof TestList);
				Collection<Map<String, Object>> cp1 = t1.getCollectionProperties();
				assertNotNull("no service properties collection", cp1);
				assertEquals("wrong number of elements in service properties collection", 0, cp1.size());
				assertTrue("service properties collection replaced", cp1 instanceof TestList);
				Collection<Entry<Map<String, Object>, BaseService>> ct1 = t1.getCollectionTuple();
				assertNotNull("no service tuple collection", ct1);
				assertEquals("wrong number of elements in service tuple collection", 0, ct1.size());
				assertTrue("service tuple collection replaced", ct1 instanceof TestList);

				List<BaseService> ls1 = t1.getListService();
				assertNotNull("no service list", ls1);
				assertEquals("wrong number of elements in service list", 0, ls1.size());
				assertTrue("service list replaced", ls1 instanceof TestList);
				List<ServiceReference<BaseService>> lr1 = t1.getListReference();
				assertNotNull("no service reference list", lr1);
				assertEquals("wrong number of elements in service reference list", 0, lr1.size());
				assertTrue("service reference list replaced", lr1 instanceof TestList);
				List<ComponentServiceObjects<BaseService>> lo1 = t1.getListServiceObjects();
				assertNotNull("no service objects list", lo1);
				assertEquals("wrong number of elements in service objects list", 0, lo1.size());
				assertTrue("service objects list replaced", lo1 instanceof TestList);
				List<Map<String, Object>> lp1 = t1.getListProperties();
				assertNotNull("no service properties list", lp1);
				assertEquals("wrong number of elements in service properties list", 0, lp1.size());
				assertTrue("service properties list replaced", lp1 instanceof TestList);
				List<Entry<Map<String, Object>, BaseService>> lt1 = t1.getListTuple();
				assertNotNull("no service tuple list", lt1);
				assertEquals("wrong number of elements in service tuple list", 0, lt1.size());
				assertTrue("service tuple list replaced", lt1 instanceof TestList);

				Collection<BaseService> ss1 = t1.getCollectionSubtypeService();
				assertNotNull("no service collection subtype", ss1);
				assertEquals("wrong number of elements in service collection subtype", 0, ss1.size());
				assertTrue("service collection subtype replaced", ss1 instanceof TestSet);
				Collection<ServiceReference<BaseService>> sr1 = t1.getCollectionSubtypeReference();
				assertNotNull("no service reference collection subtype", sr1);
				assertEquals("wrong number of elements in service reference collection subtype", 0, sr1.size());
				Collection<ComponentServiceObjects<BaseService>> so1 = t1.getCollectionSubtypeServiceObjects();
				assertNotNull("no service objects collection subtype", so1);
				assertEquals("wrong number of elements in service objects collection subtype", 0, so1.size());
				Collection<Map<String, Object>> sp1 = t1.getCollectionSubtypeProperties();
				assertNotNull("no service properties collection subtype", sp1);
				assertEquals("wrong number of elements in service properties collection subtype", 0, sp1.size());
				assertTrue("service properties collection subtype replaced", sp1 instanceof TestIdentitySet);
				Collection<Entry<Map<String, Object>, BaseService>> st1 = t1.getCollectionSubtypeTuple();
				assertNotNull("no service tuple collection subtype", st1);
				assertEquals("wrong number of elements in service tuple collection subtype", 0, st1.size());
				assertTrue("service tuple collection subtype replaced", st1 instanceof TestIdentitySet);

			}
			finally {
				comp1Tracker.close();
				test1Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb24);
		}
	}

	public void testBadFieldReference130() throws Exception {
		Bundle tb24 = installBundle("tb24.jar", false);
		assertNotNull("tb24 failed to install", tb24);

		final String NAME = TEST_CASE_ROOT + ".tb24.BadFieldReceiver";

		try {
			tb24.start();

			Filter comp1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + BaseService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=org.osgi.test.cases.component.tb24.Ranking1))");
			Filter test1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + ScalarFieldTestService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=" + NAME + "))");
			ServiceTracker<BaseService, BaseService> comp1Tracker = new ServiceTracker<BaseService, BaseService>(
					getContext(), comp1Filter, null);
			ServiceTracker<ScalarFieldTestService<BaseService>, ScalarFieldTestService<BaseService>> test1Tracker = new ServiceTracker<ScalarFieldTestService<BaseService>, ScalarFieldTestService<BaseService>>(
					getContext(), test1Filter, null);
			try {
				comp1Tracker.open();
				test1Tracker.open();
				BaseService c1 = comp1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing comp1", c1);
				ScalarFieldTestService<BaseService> s1 = test1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing test1", s1);
				assertEquals("wrong activation count", 1, s1.getActivationCount());
				assertEquals("wrong modification count", 0, s1.getModificationCount());
				assertEquals("wrong deactivation count", 0, s1.getDeactivationCount());

				assertNull("service injected", s1.getAssignable());

				@SuppressWarnings("unchecked")
				MultipleFieldTestService<BaseService> t1 = (MultipleFieldTestService<BaseService>) s1;
				Collection<BaseService> cs1 = t1.getCollectionService();
				assertNull("service collection injected", cs1);
				Collection<ServiceReference<BaseService>> cr1 = t1.getCollectionReference();
				assertNull("service reference collection injected", cr1);
				Collection<ComponentServiceObjects<BaseService>> co1 = t1.getCollectionServiceObjects();
				assertNull("service objects collection injected", co1);
				Collection<Map<String, Object>> cp1 = t1.getCollectionProperties();
				assertNull("service properties collection injected", cp1);
				Collection<Entry<Map<String, Object>, BaseService>> ct1 = t1.getCollectionTuple();
				assertNull("service tuple collection injected", ct1);
				Collection<BaseService> ss1 = t1.getCollectionSubtypeService();
				assertNull("service collection subtype should not be modified", ss1);
			}
			finally {
				comp1Tracker.close();
				test1Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb24);
		}
	}

	public void testDynamicMultipleFieldReference130() throws Exception {
		Bundle tb24 = installBundle("tb24.jar", false);
		assertNotNull("tb24 failed to install", tb24);

		final String NAME = TEST_CASE_ROOT + ".tb24.DynamicMultipleFieldReceiver";

		try {
			tb24.start();

			Filter comp1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + BaseService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=org.osgi.test.cases.component.tb24.Ranking1))");
			Filter test1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + MultipleFieldTestService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=" + NAME + "))");
			ServiceTracker<BaseService, BaseService> comp1Tracker = new ServiceTracker<BaseService, BaseService>(
					getContext(), comp1Filter, null);
			ServiceTracker<MultipleFieldTestService<BaseService>, MultipleFieldTestService<BaseService>> test1Tracker = new ServiceTracker<MultipleFieldTestService<BaseService>, MultipleFieldTestService<BaseService>>(
					getContext(), test1Filter, null);
			try {
				comp1Tracker.open();
				test1Tracker.open();
				BaseService c1 = comp1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing comp1", c1);
				MultipleFieldTestService<BaseService> t1 = test1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing test1", t1);
				assertEquals("wrong activation count", 1, t1.getActivationCount());
				assertEquals("wrong modification count", 0, t1.getModificationCount());
				assertEquals("wrong deactivation count", 0, t1.getDeactivationCount());

				Collection<BaseService> cs1 = t1.getCollectionService();
				assertNotNull("no service collection", cs1);
				assertFalse("service collection not replaced", cs1 instanceof TestList);
				assertEquals("wrong number of elements in service collection", 1, cs1.size());
				assertSame("wrong service in service collection", c1, cs1.iterator().next());
				assertTrue("service collection not mutable", cs1.remove(cs1.iterator().next()));
				Collection<ServiceReference<BaseService>> cr1 = t1.getCollectionReference();
				assertNotNull("no service reference collection", cr1);
				assertFalse("service reference collection not replaced", cr1 instanceof TestList);
				assertEquals("wrong number of elements in service reference collection", 1, cr1.size());
				assertEquals("wrong service in service reference collection", comp1Tracker.getServiceReference(),
						cr1.iterator().next());
				assertTrue("service reference collection not mutable", cr1.remove(cr1.iterator().next()));
				Collection<ComponentServiceObjects<BaseService>> co1 = t1.getCollectionServiceObjects();
				assertNotNull("no service objects collection", co1);
				assertFalse("service objects collection not replaced", co1 instanceof TestList);
				assertEquals("wrong number of elements in service objects collection", 1, co1.size());
				assertEquals("wrong service in service objects collection", comp1Tracker.getServiceReference(),
						co1.iterator().next().getServiceReference());
				assertTrue("service objects collection not mutable", co1.remove(co1.iterator().next()));
				Collection<Map<String, Object>> cp1 = t1.getCollectionProperties();
				assertNotNull("no service properties collection", cp1);
				assertFalse("service properties collection not replaced", cp1 instanceof TestList);
				assertEquals("wrong number of elements in service properties collection", 1, cp1.size());
				Map<String, Object> properties = cp1.iterator().next();
				assertTrue("service properties collection not mutable", cp1.remove(properties));
				assertNotNull("no properties injected", properties);
				assertEquals("wrong properties injected", c1.getProperties().get(ComponentConstants.COMPONENT_ID),
						properties.get(ComponentConstants.COMPONENT_ID));
				assertTrue("properties not Comparable", properties instanceof Comparable);
				try {
					properties.remove(ComponentConstants.COMPONENT_ID);
					failException("properties is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				try {
					properties.put("foo", "bar");
					failException("properties is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				Collection<Entry<Map<String, Object>, BaseService>> ct1 = t1.getCollectionTuple();
				assertNotNull("no service tuple collection", ct1);
				assertFalse("service tuple collection not replaced", ct1 instanceof TestList);
				assertEquals("wrong number of elements in service tuple collection", 1, ct1.size());
				Entry<Map<String, Object>, BaseService> tuple = ct1.iterator().next();
				assertTrue("service tuple collection not mutable", ct1.remove(tuple));
				Map<String, Object> key = tuple.getKey();
				assertNotNull("no tuple injected", key);
				assertEquals("wrong tuple injected", c1.getProperties().get(ComponentConstants.COMPONENT_ID),
						key.get(ComponentConstants.COMPONENT_ID));
				assertTrue("tuple not Comparable", tuple instanceof Comparable);
				assertTrue("tuple key not Comparable", key instanceof Comparable);
				try {
					key.remove(ComponentConstants.COMPONENT_ID);
					failException("tuple key is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				try {
					key.put("foo", "bar");
					failException("tuple key is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				assertEquals("properties not equal to tuple key", 0,
						asComparable(properties).compareTo(key));
				assertEquals("tuple keys not equal to properties", 0,
						asComparable(key).compareTo(properties));
				assertNotNull("tuple value null", tuple.getValue());
				assertSame("tuple value wrong", c1, tuple.getValue());

				List<BaseService> ls1 = t1.getListService();
				assertNotNull("no service list", ls1);
				assertFalse("service list not replaced", ls1 instanceof TestList);
				assertEquals("wrong number of elements in service list", 1, ls1.size());
				assertSame("wrong service in service list", c1, ls1.get(0));
				assertTrue("service list not mutable", ls1.remove(ls1.get(0)));
				List<ServiceReference<BaseService>> lr1 = t1.getListReference();
				assertNotNull("no service reference list", lr1);
				assertFalse("service reference list not replaced", lr1 instanceof TestList);
				assertEquals("wrong number of elements in service reference list", 1, lr1.size());
				assertEquals("wrong service in service reference list", comp1Tracker.getServiceReference(), lr1.get(0));
				assertTrue("service reference list not mutable", lr1.remove(lr1.get(0)));
				List<ComponentServiceObjects<BaseService>> lo1 = t1.getListServiceObjects();
				assertNotNull("no service objects list", lo1);
				assertFalse("service objects list not replaced", lo1 instanceof TestList);
				assertEquals("wrong number of elements in service objects list", 1, lo1.size());
				assertEquals("wrong service in service objects list", comp1Tracker.getServiceReference(),
						lo1.get(0).getServiceReference());
				assertSame("serviceobjects produced wrong service", c1, lo1.get(0).getService());
				assertTrue("service objects list not mutable", lo1.remove(lo1.get(0)));
				List<Map<String, Object>> lp1 = t1.getListProperties();
				assertNotNull("no service properties list", lp1);
				assertFalse("service properties list not replaced", lp1 instanceof TestList);
				assertEquals("wrong number of elements in service properties list", 1, lp1.size());
				properties = lp1.get(0);
				assertTrue("service properties list not mutable", lp1.remove(properties));
				assertNotNull("no properties injected", properties);
				assertEquals("wrong properties injected", c1.getProperties().get(ComponentConstants.COMPONENT_ID),
						properties.get(ComponentConstants.COMPONENT_ID));
				assertTrue("properties not Comparable", properties instanceof Comparable);
				try {
					properties.remove(ComponentConstants.COMPONENT_ID);
					failException("properties is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				try {
					properties.put("foo", "bar");
					failException("properties is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				List<Entry<Map<String, Object>, BaseService>> lt1 = t1.getListTuple();
				assertNotNull("no service tuple list", lt1);
				assertFalse("service tuple list not replaced", lt1 instanceof TestList);
				assertEquals("wrong number of elements in service tuple list", 1, lt1.size());
				assertTrue("tuple not Comparable", lt1.get(0) instanceof Comparable);
				assertEquals("tuples not equal", 0,
						asComparable(lt1.get(0)).compareTo(tuple));
				assertEquals("tuples not equal", 0,
						asComparable(tuple).compareTo(lt1.get(0)));
				tuple = lt1.get(0);
				assertTrue("service tuple list not mutable", lt1.remove(tuple));
				key = tuple.getKey();
				assertNotNull("no tuple injected", key);
				assertEquals("wrong tuple injected", c1.getProperties().get(ComponentConstants.COMPONENT_ID),
						key.get(ComponentConstants.COMPONENT_ID));
				assertTrue("tuple key not Comparable", key instanceof Comparable);
				try {
					key.remove(ComponentConstants.COMPONENT_ID);
					failException("tuple key is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				try {
					key.put("foo", "bar");
					failException("tuple key is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				assertEquals("properties not equal to tuple key", 0,
						asComparable(properties).compareTo(key));
				assertEquals("tuple keys not equal to properties", 0,
						asComparable(key).compareTo(properties));
				assertNotNull("tuple value null", tuple.getValue());
				assertSame("tuple value wrong", c1, tuple.getValue());

				Collection<BaseService> ss1 = t1.getCollectionSubtypeService();
				assertNotNull("no service collection subtype", ss1);
				assertTrue("service collection subtype replaced", ss1 instanceof TestSet);
				assertEquals("wrong number of elements in service collection subtype", 1, ss1.size());
				assertSame("wrong service in service collection subtype", c1, ss1.iterator().next());
				Collection<ServiceReference<BaseService>> sr1 = t1.getCollectionSubtypeReference();
				assertNotNull("no service reference collection subtype", sr1);
				assertEquals("wrong number of elements in service reference collection subtype", 1, sr1.size());
				assertEquals("wrong service in service reference collection subtype",
						comp1Tracker.getServiceReference(), sr1.iterator().next());
				Collection<ComponentServiceObjects<BaseService>> so1 = t1.getCollectionSubtypeServiceObjects();
				assertNotNull("no service objects collection subtype", so1);
				assertEquals("wrong number of elements in service objects collection subtype", 1, so1.size());
				assertEquals("wrong service in service objects collection subtype", comp1Tracker.getServiceReference(),
						so1.iterator().next().getServiceReference());
				Collection<Map<String, Object>> sp1 = t1.getCollectionSubtypeProperties();
				assertNotNull("no service properties collection subtype", sp1);
				assertTrue("service properties collection subtype not replaced", sp1 instanceof TestIdentitySet);
				assertEquals("wrong number of elements in service properties collection subtype", 1, sp1.size());
				properties = sp1.iterator().next();
				assertNotNull("no properties injected", properties);
				assertEquals("wrong properties injected", c1.getProperties().get(ComponentConstants.COMPONENT_ID),
						properties.get(ComponentConstants.COMPONENT_ID));
				assertTrue("properties not Comparable", properties instanceof Comparable);
				try {
					properties.remove(ComponentConstants.COMPONENT_ID);
					failException("properties is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				try {
					properties.put("foo", "bar");
					failException("properties is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				Collection<Entry<Map<String, Object>, BaseService>> st1 = t1.getCollectionSubtypeTuple();
				assertNotNull("no service tuple collection subtype", st1);
				assertTrue("service tuple collection subtype not replaced", st1 instanceof TestIdentitySet);
				assertEquals("wrong number of elements in service tuple collection subtype", 1, st1.size());
				tuple = st1.iterator().next();
				key = tuple.getKey();
				assertNotNull("no tuple injected", key);
				assertEquals("wrong tuple injected", c1.getProperties().get(ComponentConstants.COMPONENT_ID),
						key.get(ComponentConstants.COMPONENT_ID));
				assertTrue("tuple not Comparable", tuple instanceof Comparable);
				assertTrue("tuple key not Comparable", key instanceof Comparable);
				try {
					key.remove(ComponentConstants.COMPONENT_ID);
					failException("tuple key is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				try {
					key.put("foo", "bar");
					failException("tuple key is not unmodifiable", UnsupportedOperationException.class);
				}
				catch (UnsupportedOperationException expected) {
					// map must be unmodifiable
				}
				assertEquals("properties not equal to tuple key", 0,
						asComparable(properties).compareTo(key));
				assertEquals("tuple keys not equal to properties", 0,
						asComparable(key).compareTo(properties));
				assertNotNull("tuple value null", tuple.getValue());
				assertSame("tuple value wrong", c1, tuple.getValue());

			}
			finally {
				comp1Tracker.close();
				test1Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb24);
		}
	}

	public void testOptionalDynamicMultipleFieldReference130() throws Exception {
		ServiceComponentRuntime scr = scrTracker.getService();
		assertNotNull("failed to find ServiceComponentRuntime service", scr);
		Bundle tb24 = installBundle("tb24.jar", false);
		assertNotNull("tb24 failed to install", tb24);

		final String NAME = TEST_CASE_ROOT + ".tb24.OptionalDynamicMultipleFieldReceiver";

		try {
			tb24.start();

			ComponentDescriptionDTO description1 = scr.getComponentDescriptionDTO(tb24,
					"org.osgi.test.cases.component.tb24.Ranking10");
			assertNotNull("null description1", description1);
			assertFalse("description1 is enabled", scr.isComponentEnabled(description1));

			Filter comp1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + BaseService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=org.osgi.test.cases.component.tb24.Ranking10))");
			Filter test1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + MultipleFieldTestService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=" + NAME + "))");
			ServiceTracker<BaseService, BaseService> comp1Tracker = new ServiceTracker<BaseService, BaseService>(
					getContext(), comp1Filter, null);
			ServiceTracker<MultipleFieldTestService<BaseService>, MultipleFieldTestService<BaseService>> test1Tracker = new ServiceTracker<MultipleFieldTestService<BaseService>, MultipleFieldTestService<BaseService>>(
					getContext(), test1Filter, null);
			try {
				comp1Tracker.open();
				test1Tracker.open();
				BaseService c1 = comp1Tracker.waitForService(SLEEP * 3);
				assertNull("found comp1", c1);
				MultipleFieldTestService<BaseService> t1 = test1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing test1", t1);
				assertEquals("wrong activation count", 1, t1.getActivationCount());
				assertEquals("wrong modification count", 0, t1.getModificationCount());
				assertEquals("wrong deactivation count", 0, t1.getDeactivationCount());

				Collection<BaseService> cs1 = t1.getCollectionService();
				assertNotNull("no service collection", cs1);
				assertFalse("service collection not replaced", cs1 instanceof TestList);
				assertEquals("wrong number of elements in service collection", 0, cs1.size());
				Collection<ServiceReference<BaseService>> cr1 = t1.getCollectionReference();
				assertNotNull("no service reference collection", cr1);
				assertFalse("service reference collection not replaced", cr1 instanceof TestList);
				assertEquals("wrong number of elements in service reference collection", 0, cr1.size());
				Collection<ComponentServiceObjects<BaseService>> co1 = t1.getCollectionServiceObjects();
				assertNotNull("no service objects collection", co1);
				assertFalse("service objects collection not replaced", co1 instanceof TestList);
				assertEquals("wrong number of elements in service objects collection", 0, co1.size());
				Collection<Map<String, Object>> cp1 = t1.getCollectionProperties();
				assertNotNull("no service properties collection", cp1);
				assertFalse("service properties collection not replaced", cp1 instanceof TestList);
				assertEquals("wrong number of elements in service properties collection", 0, cp1.size());
				Collection<Entry<Map<String, Object>, BaseService>> ct1 = t1.getCollectionTuple();
				assertNotNull("no service tuple collection", ct1);
				assertFalse("service tuple collection not replaced", ct1 instanceof TestList);
				assertEquals("wrong number of elements in service tuple collection", 0, ct1.size());

				List<BaseService> ls1 = t1.getListService();
				assertNotNull("no service list", ls1);
				assertFalse("service list not replaced", ls1 instanceof TestList);
				assertEquals("wrong number of elements in service list", 0, ls1.size());
				List<ServiceReference<BaseService>> lr1 = t1.getListReference();
				assertNotNull("no service reference list", lr1);
				assertFalse("service reference list not replaced", lr1 instanceof TestList);
				assertEquals("wrong number of elements in service reference list", 0, lr1.size());
				List<ComponentServiceObjects<BaseService>> lo1 = t1.getListServiceObjects();
				assertNotNull("no service objects list", lo1);
				assertFalse("service objects list not replaced", lo1 instanceof TestList);
				assertEquals("wrong number of elements in service objects list", 0, lo1.size());
				List<Map<String, Object>> lp1 = t1.getListProperties();
				assertNotNull("no service properties list", lp1);
				assertFalse("service properties list not replaced", lp1 instanceof TestList);
				assertEquals("wrong number of elements in service properties list", 0, lp1.size());
				List<Entry<Map<String, Object>, BaseService>> lt1 = t1.getListTuple();
				assertNotNull("no service tuple list", lt1);
				assertFalse("service tuple list not replaced", lt1 instanceof TestList);
				assertEquals("wrong number of elements in service tuple list", 0, lt1.size());

				Collection<BaseService> ss1 = t1.getCollectionSubtypeService();
				assertNotNull("no service collection subtype", ss1);
				assertTrue("service collection subtype replaced", ss1 instanceof TestSet);
				assertEquals("wrong number of elements in service collection subtype", 0, ss1.size());
				Collection<ServiceReference<BaseService>> sr1 = t1.getCollectionSubtypeReference();
				assertNotNull("no service reference collection subtype", sr1);
				assertEquals("wrong number of elements in service reference collection subtype", 0, sr1.size());
				Collection<ComponentServiceObjects<BaseService>> so1 = t1.getCollectionSubtypeServiceObjects();
				assertNotNull("no service objects collection subtype", so1);
				assertEquals("wrong number of elements in service objects collection subtype", 0, so1.size());
				Collection<Map<String, Object>> sp1 = t1.getCollectionSubtypeProperties();
				assertNotNull("no service properties collection subtype", sp1);
				assertTrue("service properties collection subtype not replaced", sp1 instanceof TestIdentitySet);
				assertEquals("wrong number of elements in service properties collection subtype", 0, sp1.size());
				Collection<Entry<Map<String, Object>, BaseService>> st1 = t1.getCollectionSubtypeTuple();
				assertNotNull("no service tuple collection subtype", st1);
				assertTrue("service tuple collection subtype not replaced", st1 instanceof TestIdentitySet);
				assertEquals("wrong number of elements in service tuple collection subtype", 0, st1.size());

				Promise<Void> p = scr.enableComponent(description1);
				p.getValue(); // wait for state change to complete
				assertTrue("description1 is not enabled", scr.isComponentEnabled(description1));

				c1 = comp1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing comp1", c1);
				MultipleFieldTestService<BaseService> t2 = test1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing test1", t2);
				assertSame("reactivated", t1, t2);
				assertEquals("wrong activation count", 1, t1.getActivationCount());
				assertEquals("wrong modification count", 0, t1.getModificationCount());
				assertEquals("wrong deactivation count", 0, t1.getDeactivationCount());

				Collection<BaseService> cs2 = t1.getCollectionService();
				assertNotNull("no service collection", cs2);
				assertNotSame("service collection same", cs1, cs2);
				assertEquals("wrong number of elements in service collection", 1, cs2.size());
				Collection<ServiceReference<BaseService>> cr2 = t1.getCollectionReference();
				assertNotNull("no service reference collection", cr2);
				assertNotSame("service reference collection same", cr1, cr2);
				assertEquals("wrong number of elements in service reference collection", 1, cr2.size());
				Collection<ComponentServiceObjects<BaseService>> co2 = t1.getCollectionServiceObjects();
				assertNotNull("no service objects collection", co2);
				assertNotSame("service objects collection same", co1, co2);
				assertEquals("wrong number of elements in service objects collection", 1, co2.size());
				Collection<Map<String, Object>> cp2 = t1.getCollectionProperties();
				assertNotNull("no service properties collection", cp2);
				assertNotSame("service properties collection same", cp1, cp2);
				assertEquals("wrong number of elements in service properties collection", 1, cp2.size());
				Collection<Entry<Map<String, Object>, BaseService>> ct2 = t1.getCollectionTuple();
				assertNotNull("no service tuple collection", ct2);
				assertNotSame("service tuple collection same", ct1, ct2);
				assertEquals("wrong number of elements in service tuple collection", 1, ct2.size());

				List<BaseService> ls2 = t1.getListService();
				assertNotNull("no service list", ls2);
				assertNotSame("service list same", ls1, ls2);
				assertEquals("wrong number of elements in service list", 1, ls2.size());
				List<ServiceReference<BaseService>> lr2 = t1.getListReference();
				assertNotNull("no service reference list", lr2);
				assertNotSame("service reference list same", lr1, lr2);
				assertEquals("wrong number of elements in service reference list", 1, lr2.size());
				List<ComponentServiceObjects<BaseService>> lo2 = t1.getListServiceObjects();
				assertNotNull("no service objects list", lo2);
				assertNotSame("service objects list same", lo1, lo2);
				assertEquals("wrong number of elements in service objects list", 1, lo2.size());
				List<Map<String, Object>> lp2 = t1.getListProperties();
				assertNotNull("no service properties list", lp2);
				assertNotSame("service properties list same", lp1, lp2);
				assertEquals("wrong number of elements in service properties list", 1, lp2.size());
				List<Entry<Map<String, Object>, BaseService>> lt2 = t1.getListTuple();
				assertNotNull("no service tuple list", lt2);
				assertNotSame("service tuple list same", lt1, lt2);
				assertEquals("wrong number of elements in service tuple list", 1, lt2.size());

				Collection<BaseService> ss2 = t1.getCollectionSubtypeService();
				assertNotNull("no service collection subtype", ss2);
				assertSame("service collection subtype not same", ss1, ss2);
				assertEquals("wrong number of elements in service collection subtype", 1, ss2.size());
				Collection<ServiceReference<BaseService>> sr2 = t1.getCollectionSubtypeReference();
				assertNotNull("no service reference collection subtype", sr2);
				assertSame("service reference collection subtype not same", sr1, sr2);
				assertEquals("wrong number of elements in service reference collection subtype", 1, sr2.size());
				Collection<ComponentServiceObjects<BaseService>> so2 = t1.getCollectionSubtypeServiceObjects();
				assertNotNull("no service objects collection subtype", so2);
				assertSame("service objects collection subtype not same", so1, so2);
				assertEquals("wrong number of elements in service objects collection subtype", 1, so2.size());
				Collection<Map<String, Object>> sp2 = t1.getCollectionSubtypeProperties();
				assertNotNull("no service properties collection subtype", sp2);
				assertSame("service properties collection subtype not same", sp1, sp2);
				assertEquals("wrong number of elements in service properties collection subtype", 1, sp2.size());
				Collection<Entry<Map<String, Object>, BaseService>> st2 = t1.getCollectionSubtypeTuple();
				assertNotNull("no service tuple collection subtype", st2);
				assertSame("service tuple collection subtype not same", st1, st2);
				assertEquals("wrong number of elements in service tuple collection subtype", 1, st2.size());

				p = scr.disableComponent(description1);
				p.getValue(); // wait for state change to complete
				assertFalse("description1 is enabled", scr.isComponentEnabled(description1));

				c1 = comp1Tracker.waitForService(SLEEP * 3);
				assertNull("found comp1", c1);
				MultipleFieldTestService<BaseService> t3 = test1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing test1", t3);
				assertSame("reactivated", t1, t3);
				assertEquals("wrong activation count", 1, t1.getActivationCount());
				assertEquals("wrong modification count", 0, t1.getModificationCount());
				assertEquals("wrong deactivation count", 0, t1.getDeactivationCount());

				Collection<BaseService> cs3 = t1.getCollectionService();
				assertNotNull("no service collection", cs3);
				assertNotSame("service collection same", cs1, cs3);
				assertNotSame("service collection same", cs2, cs3);
				assertEquals("wrong number of elements in service collection", 0, cs3.size());
				Collection<ServiceReference<BaseService>> cr3 = t1.getCollectionReference();
				assertNotNull("no service reference collection", cr3);
				assertNotSame("service reference collection same", cr1, cr3);
				assertNotSame("service reference collection same", cr2, cr3);
				assertEquals("wrong number of elements in service reference collection", 0, cr3.size());
				Collection<ComponentServiceObjects<BaseService>> co3 = t1.getCollectionServiceObjects();
				assertNotNull("no service objects collection", co3);
				assertNotSame("service objects collection same", co1, co3);
				assertNotSame("service objects collection same", co2, co3);
				assertEquals("wrong number of elements in service objects collection", 0, co3.size());
				Collection<Map<String, Object>> cp3 = t1.getCollectionProperties();
				assertNotNull("no service properties collection", cp3);
				assertNotSame("service properties collection same", cp1, cp3);
				assertNotSame("service properties collection same", cp2, cp3);
				assertEquals("wrong number of elements in service properties collection", 0, cp3.size());
				Collection<Entry<Map<String, Object>, BaseService>> ct3 = t1.getCollectionTuple();
				assertNotNull("no service tuple collection", ct3);
				assertNotSame("service tuple collection same", ct1, ct3);
				assertNotSame("service tuple collection same", ct2, ct3);
				assertEquals("wrong number of elements in service tuple collection", 0, ct3.size());

				List<BaseService> ls3 = t1.getListService();
				assertNotNull("no service list", ls3);
				assertNotSame("service list same", ls1, ls3);
				assertNotSame("service list same", ls2, ls3);
				assertEquals("wrong number of elements in service list", 0, ls3.size());
				List<ServiceReference<BaseService>> lr3 = t1.getListReference();
				assertNotNull("no service reference list", lr3);
				assertNotSame("service reference list same", lr1, lr3);
				assertNotSame("service reference list same", lr2, lr3);
				assertEquals("wrong number of elements in service reference list", 0, lr3.size());
				List<ComponentServiceObjects<BaseService>> lo3 = t1.getListServiceObjects();
				assertNotNull("no service objects list", lo3);
				assertNotSame("service objects list same", lo1, lo3);
				assertNotSame("service objects list same", lo2, lo3);
				assertEquals("wrong number of elements in service objects list", 0, lo3.size());
				List<Map<String, Object>> lp3 = t1.getListProperties();
				assertNotNull("no service properties list", lp3);
				assertNotSame("service properties list same", lp1, lp3);
				assertNotSame("service properties list same", lp2, lp3);
				assertEquals("wrong number of elements in service properties list", 0, lp3.size());
				List<Entry<Map<String, Object>, BaseService>> lt3 = t1.getListTuple();
				assertNotNull("no service tuple list", lt3);
				assertNotSame("service tuple list same", lt1, lt3);
				assertNotSame("service tuple list same", lt2, lt3);
				assertEquals("wrong number of elements in service tuple list", 0, lt3.size());

				Collection<BaseService> ss3 = t1.getCollectionSubtypeService();
				assertNotNull("no service collection subtype", ss3);
				assertSame("service collection subtype not same", ss1, ss3);
				assertEquals("wrong number of elements in service collection subtype", 0, ss3.size());
				Collection<ServiceReference<BaseService>> sr3 = t1.getCollectionSubtypeReference();
				assertNotNull("no service reference collection subtype", sr3);
				assertSame("service reference collection subtype not same", sr1, sr3);
				assertEquals("wrong number of elements in service reference collection subtype", 0, sr3.size());
				Collection<ComponentServiceObjects<BaseService>> so3 = t1.getCollectionSubtypeServiceObjects();
				assertNotNull("no service objects collection subtype", so3);
				assertSame("service objects collection subtype not same", so1, so3);
				assertEquals("wrong number of elements in service objects collection subtype", 0, so3.size());
				Collection<Map<String, Object>> sp3 = t1.getCollectionSubtypeProperties();
				assertNotNull("no service properties collection subtype", sp3);
				assertSame("service properties collection subtype not same", sp1, sp3);
				assertEquals("wrong number of elements in service properties collection subtype", 0, sp3.size());
				Collection<Entry<Map<String, Object>, BaseService>> st3 = t1.getCollectionSubtypeTuple();
				assertNotNull("no service tuple collection subtype", st3);
				assertSame("service tuple collection subtype not same", st1, st3);
				assertEquals("wrong number of elements in service tuple collection subtype", 0, st3.size());

			}
			finally {
				comp1Tracker.close();
				test1Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb24);
		}
	}

	public void testDynamicMultipleFieldReferenceModified130() throws Exception {
		ConfigurationAdmin cm = trackerCM.getService();
		assertNotNull("The ConfigurationAdmin should be available", cm);
		final String PID_ROOT = TEST_CASE_ROOT + ".tb24";

		Bundle tb24 = installBundle("tb24.jar", false);
		assertNotNull("tb24 failed to install", tb24);

		final String NAME = TEST_CASE_ROOT + ".tb24.DynamicMultipleFieldReceiver";

		try {
			tb24.start();

			Filter comp1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + BaseService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=org.osgi.test.cases.component.tb24.Ranking1))");
			Filter test1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + MultipleFieldTestService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=" + NAME + "))");
			ServiceTracker<BaseService, BaseService> comp1Tracker = new ServiceTracker<BaseService, BaseService>(
					getContext(), comp1Filter, null);
			ServiceTracker<MultipleFieldTestService<BaseService>, MultipleFieldTestService<BaseService>> test1Tracker = new ServiceTracker<MultipleFieldTestService<BaseService>, MultipleFieldTestService<BaseService>>(
					getContext(), test1Filter, null);
			try {
				comp1Tracker.open();
				test1Tracker.open();
				BaseService c1 = comp1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing comp1", c1);
				MultipleFieldTestService<BaseService> t1 = test1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing test1", t1);
				assertEquals("wrong activation count", 1, t1.getActivationCount());
				assertEquals("wrong modification count", 0, t1.getModificationCount());
				assertEquals("wrong deactivation count", 0, t1.getDeactivationCount());

				Collection<BaseService> cs1 = t1.getCollectionService();
				assertNotNull("no service collection", cs1);
				assertEquals("wrong number of elements in service collection", 1, cs1.size());
				assertSame("wrong service in service collection", c1, cs1.iterator().next());
				Collection<ServiceReference<BaseService>> cr1 = t1.getCollectionReference();
				assertNotNull("no service reference collection", cr1);
				assertEquals("wrong number of elements in service reference collection", 1, cr1.size());
				assertEquals("wrong property value", "xml", cr1.iterator().next().getProperty(PID_ROOT));
				Collection<ComponentServiceObjects<BaseService>> co1 = t1.getCollectionServiceObjects();
				assertNotNull("no service objects collection", co1);
				assertEquals("wrong number of elements in service objects collection", 1, co1.size());
				assertEquals("wrong property value", "xml",
						co1.iterator().next().getServiceReference().getProperty(PID_ROOT));
				Collection<Map<String, Object>> cp1 = t1.getCollectionProperties();
				assertNotNull("no service properties collection", cp1);
				assertEquals("wrong number of elements in service properties collection", 1, cp1.size());
				Map<String, Object> properties = cp1.iterator().next();
				assertNotNull("no properties injected", properties);
				assertEquals("wrong property value", "xml", properties.get(PID_ROOT));
				Collection<Entry<Map<String, Object>, BaseService>> ct1 = t1.getCollectionTuple();
				assertNotNull("no service tuple collection", ct1);
				assertEquals("wrong number of elements in service tuple collection", 1, ct1.size());
				Map<String, Object> key = ct1.iterator().next().getKey();
				assertNotNull("no tuple injected", key);
				assertEquals("wrong property value", "xml", key.get(PID_ROOT));

				List<BaseService> ls1 = t1.getListService();
				assertNotNull("no service list", ls1);
				assertEquals("wrong number of elements in service list", 1, ls1.size());
				assertSame("wrong service in service list", c1, ls1.get(0));
				List<ServiceReference<BaseService>> lr1 = t1.getListReference();
				assertNotNull("no service reference list", lr1);
				assertEquals("wrong number of elements in service reference list", 1, lr1.size());
				assertEquals("wrong property value", "xml", lr1.iterator().next().getProperty(PID_ROOT));
				List<ComponentServiceObjects<BaseService>> lo1 = t1.getListServiceObjects();
				assertNotNull("no service objects list", lo1);
				assertEquals("wrong number of elements in service objects list", 1, lo1.size());
				assertEquals("wrong property value", "xml",
						lo1.iterator().next().getServiceReference().getProperty(PID_ROOT));
				List<Map<String, Object>> lp1 = t1.getListProperties();
				assertNotNull("no service properties list", lp1);
				assertEquals("wrong number of elements in service properties list", 1, lp1.size());
				properties = lp1.get(0);
				assertNotNull("no properties injected", properties);
				assertEquals("wrong property value", "xml", properties.get(PID_ROOT));
				List<Entry<Map<String, Object>, BaseService>> lt1 = t1.getListTuple();
				assertNotNull("no service tuple list", lt1);
				assertEquals("wrong number of elements in service tuple list", 1, lt1.size());
				key = lt1.get(0).getKey();
				assertNotNull("no tuple injected", key);
				assertEquals("wrong property value", "xml", key.get(PID_ROOT));

				Collection<BaseService> ss1 = t1.getCollectionSubtypeService();
				assertNotNull("no service collection subtype", ss1);
				assertEquals("wrong number of elements in service collection subtype", 1, ss1.size());
				assertSame("wrong service in service collection subtype", c1, ss1.iterator().next());
				Collection<ServiceReference<BaseService>> sr1 = t1.getCollectionSubtypeReference();
				assertNotNull("no service reference collection subtype", sr1);
				assertEquals("wrong number of elements in service reference collection subtype", 1, sr1.size());
				assertEquals("wrong property value", "xml", sr1.iterator().next().getProperty(PID_ROOT));
				Collection<ComponentServiceObjects<BaseService>> so1 = t1.getCollectionSubtypeServiceObjects();
				assertNotNull("no service objects collection subtype", so1);
				assertEquals("wrong number of elements in service objects collection subtype", 1, so1.size());
				assertEquals("wrong property value", "xml",
						so1.iterator().next().getServiceReference().getProperty(PID_ROOT));
				Collection<Map<String, Object>> sp1 = t1.getCollectionSubtypeProperties();
				assertNotNull("no service properties collection subtype", sp1);
				assertEquals("wrong number of elements in service properties collection subtype", 1, sp1.size());
				properties = sp1.iterator().next();
				assertNotNull("no properties injected", properties);
				assertEquals("wrong property value", "xml", properties.get(PID_ROOT));
				Collection<Entry<Map<String, Object>, BaseService>> st1 = t1.getCollectionSubtypeTuple();
				assertNotNull("no service tuple collection subtype", st1);
				assertEquals("wrong number of elements in service tuple collection subtype", 1, st1.size());
				key = st1.iterator().next().getKey();
				assertNotNull("no tuple injected", key);
				assertEquals("wrong property value", "xml", key.get(PID_ROOT));

				Configuration config = cm.getConfiguration("org.osgi.test.cases.component.tb24.Ranking1", null);
				Dictionary<String, Object> props = new Hashtable<String, Object>();
				props.put(PID_ROOT, "config");
				config.update(props);
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change

				assertEquals("wrong service property value", "config", c1.getProperties().get(PID_ROOT));
				MultipleFieldTestService<BaseService> t2 = test1Tracker.waitForService(SLEEP * 3);
				assertSame("test service reactivated", t1, t2);
				assertEquals("wrong activation count", 1, t1.getActivationCount());
				assertEquals("wrong modification count", 0, t1.getModificationCount());
				assertEquals("wrong deactivation count", 0, t1.getDeactivationCount());

				Collection<BaseService> cs2 = t2.getCollectionService();
				assertNotNull("no service collection", cs2);
				assertEquals("wrong number of elements in service collection", 1, cs2.size());
				assertSame("wrong service in service collection", c1, cs2.iterator().next());
				Collection<ServiceReference<BaseService>> cr2 = t2.getCollectionReference();
				assertNotNull("no service reference collection", cr2);
				assertEquals("wrong number of elements in service reference collection", 1, cr2.size());
				assertEquals("wrong property value", "config", cr2.iterator().next().getProperty(PID_ROOT));
				Collection<ComponentServiceObjects<BaseService>> co2 = t2.getCollectionServiceObjects();
				assertNotNull("no service objects collection", co2);
				assertEquals("wrong number of elements in service objects collection", 1, co2.size());
				assertEquals("wrong property value", "config",
						co2.iterator().next().getServiceReference().getProperty(PID_ROOT));
				Collection<Map<String, Object>> cp2 = t2.getCollectionProperties();
				assertNotNull("no service properties collection", cp2);
				assertNotSame("service properties collection not replaced", sp1, cp2);
				assertEquals("wrong number of elements in service properties collection", 1, cp2.size());
				properties = cp2.iterator().next();
				assertNotNull("no properties injected", properties);
				assertEquals("wrong property value", "config", properties.get(PID_ROOT));
				Collection<Entry<Map<String, Object>, BaseService>> ct2 = t2.getCollectionTuple();
				assertNotNull("no service tuple collection", ct2);
				assertNotSame("service tuple collection not replaced", st1, ct2);
				assertEquals("wrong number of elements in service tuple collection", 1, ct2.size());
				key = ct2.iterator().next().getKey();
				assertNotNull("no tuple injected", key);
				assertEquals("wrong property value", "config", key.get(PID_ROOT));

				List<BaseService> ls2 = t2.getListService();
				assertNotNull("no service list", ls2);
				assertEquals("wrong number of elements in service list", 1, ls2.size());
				assertSame("wrong service in service list", c1, ls2.get(0));
				List<ServiceReference<BaseService>> lr2 = t2.getListReference();
				assertNotNull("no service reference list", lr2);
				assertEquals("wrong number of elements in service reference list", 1, lr2.size());
				assertEquals("wrong property value", "config", lr2.iterator().next().getProperty(PID_ROOT));
				List<ComponentServiceObjects<BaseService>> lo2 = t2.getListServiceObjects();
				assertNotNull("no service objects list", lo2);
				assertEquals("wrong number of elements in service objects list", 1, lo2.size());
				assertEquals("wrong property value", "config",
						lo2.iterator().next().getServiceReference().getProperty(PID_ROOT));
				List<Map<String, Object>> lp2 = t2.getListProperties();
				assertNotNull("no service properties list", lp2);
				assertNotSame("service properties list not replaced", lp1, lp2);
				assertEquals("wrong number of elements in service properties list", 1, lp2.size());
				properties = lp2.get(0);
				assertNotNull("no properties injected", properties);
				assertEquals("wrong property value", "config", properties.get(PID_ROOT));
				List<Entry<Map<String, Object>, BaseService>> lt2 = t2.getListTuple();
				assertNotNull("no service tuple list", lt2);
				assertNotSame("service tuple list not replaced", lt1, lt2);
				assertEquals("wrong number of elements in service tuple list", 1, lt2.size());
				key = lt2.get(0).getKey();
				assertNotNull("no tuple injected", key);
				assertEquals("wrong property value", "config", key.get(PID_ROOT));

				Collection<BaseService> ss2 = t2.getCollectionSubtypeService();
				assertNotNull("no service collection subtype", ss2);
				assertSame("service collection subtype replaced", ss1, ss2);
				assertEquals("wrong number of elements in service collection subtype", 1, ss2.size());
				assertEquals("wrong collection operations on service collection subtype", "add",
						((TestSet<BaseService>) ss2).getOps().toString());
				assertSame("wrong service in service collection subtype", c1, ss2.iterator().next());
				Collection<ServiceReference<BaseService>> sr2 = t2.getCollectionSubtypeReference();
				assertNotNull("no service reference collection subtype", sr2);
				assertSame("service reference collection subtype replaced", sr1, sr2);
				assertEquals("wrong number of elements in service reference collection subtype", 1, sr2.size());
				assertEquals("wrong property value", "config", sr2.iterator().next().getProperty(PID_ROOT));
				Collection<ComponentServiceObjects<BaseService>> so2 = t2.getCollectionSubtypeServiceObjects();
				assertNotNull("no service objects collection subtype", so2);
				assertSame("service objects collection subtype replaced", so1, so2);
				assertEquals("wrong number of elements in service objects collection subtype", 1, so2.size());
				assertEquals("wrong property value", "config",
						so2.iterator().next().getServiceReference().getProperty(PID_ROOT));
				Collection<Map<String, Object>> sp2 = t2.getCollectionSubtypeProperties();
				assertNotNull("no service properties collection subtype", sp2);
				assertSame("service properties collection subtype replaced", sp1, sp2);
				assertEquals("wrong number of elements in service properties collection subtype", 1, sp2.size());
				assertEquals("wrong collection operations on service properties collection subtype", "addaddremove",
						((TestIdentitySet<Map<String, Object>>) sp2).getOps().toString());
				properties = sp2.iterator().next();
				assertNotNull("no properties injected", properties);
				assertEquals("wrong property value", "config", properties.get(PID_ROOT));
				Collection<Entry<Map<String, Object>, BaseService>> st2 = t2.getCollectionSubtypeTuple();
				assertNotNull("no service tuple collection subtype", st2);
				assertSame("service tuple collection subtype replaced", st1, st2);
				assertEquals("wrong number of elements in service tuple collection subtype", 1, st2.size());
				assertEquals("wrong collection operations on service tuple collection subtype", "addaddremove",
						((TestIdentitySet<Entry<Map<String, Object>, BaseService>>) st2).getOps().toString());
				key = st2.iterator().next().getKey();
				assertNotNull("no tuple injected", key);
				assertEquals("wrong property value", "config", key.get(PID_ROOT));

			}
			finally {
				comp1Tracker.close();
				test1Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb24);
		}
	}

	public void testSortingDynamicMultipleFieldReference130() throws Exception {
		ServiceComponentRuntime scr = scrTracker.getService();
		assertNotNull("failed to find ServiceComponentRuntime service", scr);
		Bundle tb24 = installBundle("tb24.jar", false);
		assertNotNull("tb24 failed to install", tb24);

		final String NAME = TEST_CASE_ROOT + ".tb24.DynamicMultipleFieldReceiver";

		try {
			tb24.start();

			ComponentDescriptionDTO description1 = scr.getComponentDescriptionDTO(tb24,
					"org.osgi.test.cases.component.tb24.Ranking10");
			assertNotNull("null description1", description1);
			assertFalse("description1 is enabled", scr.isComponentEnabled(description1));

			Filter comp1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + BaseService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=org.osgi.test.cases.component.tb24.Ranking1))");
			Filter comp10Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + BaseService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=org.osgi.test.cases.component.tb24.Ranking10))");
			Filter test1Filter = getContext()
					.createFilter("(&(" + Constants.OBJECTCLASS + "=" + MultipleFieldTestService.class.getName() + ")("
							+ ComponentConstants.COMPONENT_NAME + "=" + NAME + "))");
			ServiceTracker<BaseService, BaseService> comp1Tracker = new ServiceTracker<BaseService, BaseService>(
					getContext(), comp1Filter, null);
			ServiceTracker<BaseService, BaseService> comp10Tracker = new ServiceTracker<BaseService, BaseService>(
					getContext(), comp10Filter, null);
			ServiceTracker<MultipleFieldTestService<BaseService>, MultipleFieldTestService<BaseService>> test1Tracker = new ServiceTracker<MultipleFieldTestService<BaseService>, MultipleFieldTestService<BaseService>>(
					getContext(), test1Filter, null);
			try {
				comp1Tracker.open();
				comp10Tracker.open();
				test1Tracker.open();
				BaseService c1 = comp1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing comp1", c1);
				BaseService c10 = comp10Tracker.waitForService(SLEEP * 3);
				assertNull("found comp10", c10);
				MultipleFieldTestService<BaseService> t1 = test1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing test1", t1);
				assertEquals("wrong activation count", 1, t1.getActivationCount());
				assertEquals("wrong modification count", 0, t1.getModificationCount());
				assertEquals("wrong deactivation count", 0, t1.getDeactivationCount());

				Collection<BaseService> cs1 = t1.getCollectionService();
				assertNotNull("no service collection", cs1);
				assertEquals("wrong number of elements in service collection", 1, cs1.size());
				Collection<ServiceReference<BaseService>> cr1 = t1.getCollectionReference();
				assertNotNull("no service reference collection", cr1);
				assertEquals("wrong number of elements in service reference collection", 1, cr1.size());
				Collection<ComponentServiceObjects<BaseService>> co1 = t1.getCollectionServiceObjects();
				assertNotNull("no service objects collection", co1);
				assertEquals("wrong number of elements in service objects collection", 1, co1.size());
				Collection<Map<String, Object>> cp1 = t1.getCollectionProperties();
				assertNotNull("no service properties collection", cp1);
				assertEquals("wrong number of elements in service properties collection", 1, cp1.size());
				Collection<Entry<Map<String, Object>, BaseService>> ct1 = t1.getCollectionTuple();
				assertNotNull("no service tuple collection", ct1);
				assertEquals("wrong number of elements in service tuple collection", 1, ct1.size());

				List<BaseService> ls1 = t1.getListService();
				assertNotNull("no service list", ls1);
				assertEquals("wrong number of elements in service list", 1, ls1.size());
				List<ServiceReference<BaseService>> lr1 = t1.getListReference();
				assertNotNull("no service reference list", lr1);
				assertEquals("wrong number of elements in service reference list", 1, lr1.size());
				List<ComponentServiceObjects<BaseService>> lo1 = t1.getListServiceObjects();
				assertNotNull("no service objects list", lo1);
				assertEquals("wrong number of elements in service objects list", 1, lo1.size());
				List<Map<String, Object>> lp1 = t1.getListProperties();
				assertNotNull("no service properties list", lp1);
				assertEquals("wrong number of elements in service properties list", 1, lp1.size());
				List<Entry<Map<String, Object>, BaseService>> lt1 = t1.getListTuple();
				assertNotNull("no service tuple list", lt1);
				assertEquals("wrong number of elements in service tuple list", 1, lt1.size());

				Promise<Void> p = scr.enableComponent(description1);
				p.getValue(); // wait for state change to complete
				assertTrue("description1 is not enabled", scr.isComponentEnabled(description1));

				c10 = comp10Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing comp10", c10);
				MultipleFieldTestService<BaseService> t2 = test1Tracker.waitForService(SLEEP * 3);
				assertNotNull("missing test1", t2);
				assertSame("reactivated", t1, t2);
				assertEquals("wrong activation count", 1, t1.getActivationCount());
				assertEquals("wrong modification count", 0, t1.getModificationCount());
				assertEquals("wrong deactivation count", 0, t1.getDeactivationCount());

				Collection<BaseService> cs2 = t1.getCollectionService();
				assertNotNull("no service collection", cs2);
				assertNotSame("service collection not replaced", cs1, cs2);
				assertEquals("wrong number of elements in service collection", 2, cs2.size());
				assertEquals("wrong order in service collection", Arrays.asList(c1, c10), cs2);
				Collection<ServiceReference<BaseService>> cr2 = t1.getCollectionReference();
				assertNotNull("no service reference collection", cr2);
				assertNotSame("service reference collection not replaced", cr1, cr2);
				assertEquals("wrong number of elements in service reference collection", 2, cr2.size());
				assertEquals("wrong order in service reference collection",
						Arrays.asList(comp1Tracker.getServiceReference(), comp10Tracker.getServiceReference()), cr2);
				Collection<ComponentServiceObjects<BaseService>> co2 = t1.getCollectionServiceObjects();
				assertNotNull("no service objects collection", co2);
				assertNotSame("service objects collection not replaced", co1, co2);
				assertEquals("wrong number of elements in service objects collection", 2, co2.size());
				Iterator<ComponentServiceObjects<BaseService>> co2i = co2.iterator();
				assertEquals("wrong order in service objects collection",
						Arrays.asList(comp1Tracker.getServiceReference(), comp10Tracker.getServiceReference()),
						Arrays.asList(co2i.next().getServiceReference(), co2i.next().getServiceReference()));
				Collection<Map<String, Object>> cp2 = t1.getCollectionProperties();
				assertNotNull("no service properties collection", cp2);
				assertNotSame("service properties collection not replaced", cp1, cp2);
				assertEquals("wrong number of elements in service properties collection", 2, cp2.size());
				Iterator<Map<String, Object>> cp2i = cp2.iterator();
				assertTrue("wrong order in service properties collection",
						asComparable(cp2i.next()).compareTo(cp2i.next()) < 0);
				Collection<Entry<Map<String, Object>, BaseService>> ct2 = t1.getCollectionTuple();
				assertNotNull("no service tuple collection", ct2);
				assertNotSame("service tuple collection not replaced", ct1, ct2);
				assertEquals("wrong number of elements in service tuple collection", 2, ct2.size());
				Iterator<Entry<Map<String, Object>, BaseService>> ct2i = ct2.iterator();
				assertTrue("wrong order in service tuple collection",
						asComparable(ct2i.next()).compareTo(ct2i.next()) < 0);

				List<BaseService> ls2 = t1.getListService();
				assertNotNull("no service list", ls2);
				assertNotSame("service list not replaced", ls1, ls2);
				assertEquals("wrong number of elements in service list", 2, ls2.size());
				assertEquals("wrong order in service list", Arrays.asList(c1, c10), ls2);
				List<ServiceReference<BaseService>> lr2 = t1.getListReference();
				assertNotNull("no service reference list", lr2);
				assertNotSame("service reference list not replaced", lr1, lr2);
				assertEquals("wrong number of elements in service reference list", 2, lr2.size());
				assertEquals("wrong order in service reference list",
						Arrays.asList(comp1Tracker.getServiceReference(), comp10Tracker.getServiceReference()), lr2);
				List<ComponentServiceObjects<BaseService>> lo2 = t1.getListServiceObjects();
				assertNotNull("no service objects list", lo2);
				assertNotSame("service objects list not replaced", lo1, lo2);
				assertEquals("wrong number of elements in service objects list", 2, lo2.size());
				assertEquals("wrong order in service objects list",
						Arrays.asList(comp1Tracker.getServiceReference(), comp10Tracker.getServiceReference()),
						Arrays.asList(lo2.get(0).getServiceReference(), lo2.get(1).getServiceReference()));
				List<Map<String, Object>> lp2 = t1.getListProperties();
				assertNotNull("no service properties list", lp2);
				assertNotSame("service properties list not replaced", lp1, lp2);
				assertEquals("wrong number of elements in service properties list", 2, lp2.size());
				assertTrue("wrong order in service properties list",
						asComparable(lp2.get(0)).compareTo(lp2.get(1)) < 0);
				List<Entry<Map<String, Object>, BaseService>> lt2 = t1.getListTuple();
				assertNotNull("no service tuple list", lt2);
				assertNotSame("service tuple list not replaced", lt1, lt2);
				assertEquals("wrong number of elements in service tuple list", 2, lt2.size());
				assertTrue("wrong order in service tuple list",
						asComparable(lt2.get(0)).compareTo(lt2.get(1)) < 0);
			}
			finally {
				comp1Tracker.close();
				comp10Tracker.close();
				test1Tracker.close();
			}
		}
		finally {
			uninstallBundle(tb24);
		}
	}

	static class BaseServiceImpl implements BaseService {
		public Dictionary<String, Object> getProperties() {
			return null;
		}
	}

	public void testComponentServiceObjectsSingleton130() throws Exception {
		trackerBaseService.close(); // Don't mess up BaseService count!
		ServiceComponentRuntime scr = scrTracker.getService();
		assertNotNull("failed to find ServiceComponentRuntime service", scr);
		Bundle tb25 = installBundle("tb25.jar", false);
		assertNotNull("tb25 failed to install", tb25);

		Dictionary<String, Object> baseProps = new Hashtable<String, Object>();
		baseProps.put(ComponentConstants.COMPONENT_NAME, "org.osgi.test.cases.component.tb25.BaseService");
		BaseService b1 = new BaseServiceImpl();
		ServiceRegistration<BaseService> baseReg = getContext().registerService(BaseService.class, b1, baseProps);
		try {
			tb25.start();

			ComponentDescriptionDTO receiverDesc = scr.getComponentDescriptionDTO(tb25,
					"org.osgi.test.cases.component.tb25.ComponentServiceObjectsReceiver");
			assertNotNull("null receiverDesc", receiverDesc);
			assertTrue("receiverDesc is not enabled", scr.isComponentEnabled(receiverDesc));

			Filter receiverFilter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ServiceReceiver.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb25.ComponentServiceObjectsReceiver))");
			ServiceTracker<ServiceReceiver<ComponentServiceObjects<BaseService>>, ServiceReceiver<ComponentServiceObjects<BaseService>>> receiverTracker = new ServiceTracker<ServiceReceiver<ComponentServiceObjects<BaseService>>, ServiceReceiver<ComponentServiceObjects<BaseService>>>(
					getContext(), receiverFilter, null);
			try {
				assertNull("wrong number of services", baseReg.getReference().getUsingBundles());

				receiverTracker.open();
				ServiceReceiver<ComponentServiceObjects<BaseService>> r = receiverTracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver", r);
				ComponentServiceObjects<BaseService> cso = r.getServices().get(0);
				assertNotNull("missing ComponentServiceObjects", cso);
				assertEquals("wrong service reference", baseReg.getReference(), cso.getServiceReference());

				assertNotNull("missing base", b1);
				for (int i = 0; i < 12; i++) {
					assertSame("wrong service", b1, cso.getService());
				}
				assertEquals("wrong number of services", 1, baseReg.getReference().getUsingBundles().length);

				try {
					cso.ungetService(MockFactory.newMock(BaseService.class, new Object()));
					failException("unget of invalid service succeeded", IllegalArgumentException.class);
				}
				catch (IllegalArgumentException e) {
					// expected
				}
				for (int i = 0; i < 12; i++) {
					cso.ungetService(b1);
				}
				assertNull("wrong number of services", baseReg.getReference().getUsingBundles());
				b1 = cso.getService();
				assertNotNull("missing base", b1);
				for (int i = 0; i < 11; i++) {
					assertSame("wrong service", b1, cso.getService());
				}
				assertEquals("wrong number of services", 1, baseReg.getReference().getUsingBundles().length);

				baseProps.put(ComponentConstants.COMPONENT_NAME, "unbind");
				baseReg.setProperties(baseProps);
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change
				assertNull("base not unbound", r.getServices().get(0));
				assertNull("wrong number of services", baseReg.getReference().getUsingBundles());

				assertNull("returned service object for unbound service", cso.getService());

				Promise<Void> p = scr.disableComponent(receiverDesc);
				p.getValue(); // wait for state change to complete
				assertFalse("receiverDesc is enabled", scr.isComponentEnabled(receiverDesc));
				try {
					cso.getService();
					failException("get on deactivated component succeeded", IllegalStateException.class);
				}
				catch (IllegalStateException e) {
					// expected
				}
			}
			finally {
				receiverTracker.close();
			}
		}
		finally {
			uninstallBundle(tb25);
			baseReg.unregister();
		}
	}

	static class BaseServiceServiceFactory implements ServiceFactory<BaseService> {
		final AtomicInteger counter;

		public BaseServiceServiceFactory(AtomicInteger counter) {
			this.counter = counter;
		}

		public BaseService getService(Bundle bundle, ServiceRegistration<BaseService> registration) {
			counter.incrementAndGet();
			BaseService service = new BaseServiceImpl();
			System.out.printf("getService: %s[%X]\n",
					registration.getReference().getProperty(ComponentConstants.COMPONENT_NAME),
					System.identityHashCode(service));
			return service;
		}

		public void ungetService(Bundle bundle, ServiceRegistration<BaseService> registration, BaseService service) {
			if (service instanceof BaseServiceImpl) {
				System.out.printf("ungetService: %s[%X]\n",
						registration.getReference().getProperty(ComponentConstants.COMPONENT_NAME),
						System.identityHashCode(service));
				counter.decrementAndGet();
			}
		}
	}

	public void testComponentServiceObjectsBundle130() throws Exception {
		trackerBaseService.close(); // Don't mess up BaseService count!
		ServiceComponentRuntime scr = scrTracker.getService();
		assertNotNull("failed to find ServiceComponentRuntime service", scr);
		Bundle tb25 = installBundle("tb25.jar", false);
		assertNotNull("tb25 failed to install", tb25);

		Dictionary<String, Object> baseProps = new Hashtable<String, Object>();
		baseProps.put(ComponentConstants.COMPONENT_NAME, "org.osgi.test.cases.component.tb25.BaseService");
		AtomicInteger counter = new AtomicInteger();
		BaseServiceServiceFactory f1 = new BaseServiceServiceFactory(counter);
		ServiceRegistration<BaseService> baseReg = getContext().registerService(BaseService.class, f1, baseProps);
		try {
			tb25.start();

			ComponentDescriptionDTO receiverDesc = scr.getComponentDescriptionDTO(tb25,
					"org.osgi.test.cases.component.tb25.ComponentServiceObjectsReceiver");
			assertNotNull("null receiverDesc", receiverDesc);
			assertTrue("receiverDesc is not enabled", scr.isComponentEnabled(receiverDesc));

			Filter receiverFilter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ServiceReceiver.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb25.ComponentServiceObjectsReceiver))");
			ServiceTracker<ServiceReceiver<ComponentServiceObjects<BaseService>>, ServiceReceiver<ComponentServiceObjects<BaseService>>> receiverTracker = new ServiceTracker<ServiceReceiver<ComponentServiceObjects<BaseService>>, ServiceReceiver<ComponentServiceObjects<BaseService>>>(
					getContext(), receiverFilter, null);
			try {
				assertEquals("wrong number of services", 0, counter.get());

				receiverTracker.open();
				ServiceReceiver<ComponentServiceObjects<BaseService>> r = receiverTracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver", r);
				ComponentServiceObjects<BaseService> cso = r.getServices().get(0);
				assertNotNull("missing ComponentServiceObjects", cso);
				assertEquals("wrong service reference", baseReg.getReference(), cso.getServiceReference());

				BaseService b1 = cso.getService();
				assertNotNull("missing base", b1);
				for (int i = 0; i < 11; i++) {
					assertSame("wrong service", b1, cso.getService());
				}
				assertEquals("wrong number of services", 1, counter.get());
				try {
					cso.ungetService(MockFactory.newMock(BaseService.class, new Object()));
					failException("unget of invalid service succeeded", IllegalArgumentException.class);
				}
				catch (IllegalArgumentException e) {
					// expected
				}
				for (int i = 0; i < 12; i++) {
					cso.ungetService(b1);
				}

				assertEquals("wrong number of services", 0, counter.get());
				b1 = cso.getService();
				assertNotNull("missing base", b1);
				for (int i = 0; i < 11; i++) {
					assertSame("wrong service", b1, cso.getService());
				}
				assertEquals("wrong number of services", 1, counter.get());

				baseProps.put(ComponentConstants.COMPONENT_NAME, "unbind");
				baseReg.setProperties(baseProps);
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change
				assertNull("base not unbound", r.getServices().get(0));
				assertEquals("wrong number of services", 0, counter.get());

				assertNull("returned service object for unbound service", cso.getService());

				Promise<Void> p = scr.disableComponent(receiverDesc);
				p.getValue(); // wait for state change to complete
				assertFalse("receiverDesc is enabled", scr.isComponentEnabled(receiverDesc));

				try {
					cso.getService();
					failException("get on deactivated component succeeded", IllegalStateException.class);
				}
				catch (IllegalStateException e) {
					// expected
				}
			}
			finally {
				receiverTracker.close();
			}
		}
		finally {
			uninstallBundle(tb25);
			baseReg.unregister();
		}
	}

	static class BaseServicePrototypeServiceFactory extends BaseServiceServiceFactory
			implements PrototypeServiceFactory<BaseService> {
		public BaseServicePrototypeServiceFactory(AtomicInteger counter) {
			super(counter);
		}
	}

	public void testComponentServiceObjectsPrototype130() throws Exception {
		trackerBaseService.close(); // Don't mess up BaseService count!
		ServiceComponentRuntime scr = scrTracker.getService();
		assertNotNull("failed to find ServiceComponentRuntime service", scr);
		Bundle tb25 = installBundle("tb25.jar", false);
		assertNotNull("tb25 failed to install", tb25);

		Dictionary<String, Object> baseProps = new Hashtable<String, Object>();
		baseProps.put(ComponentConstants.COMPONENT_NAME, "org.osgi.test.cases.component.tb25.BaseService");
		AtomicInteger counter = new AtomicInteger();
		BaseServicePrototypeServiceFactory f1 = new BaseServicePrototypeServiceFactory(counter);
		ServiceRegistration<BaseService> baseReg = getContext().registerService(BaseService.class, f1, baseProps);
		try {
			tb25.start();

			ComponentDescriptionDTO receiverDesc = scr.getComponentDescriptionDTO(tb25,
					"org.osgi.test.cases.component.tb25.ComponentServiceObjectsReceiver");
			assertNotNull("null receiverDesc", receiverDesc);
			assertTrue("receiverDesc is not enabled", scr.isComponentEnabled(receiverDesc));

			Filter receiverFilter = getContext().createFilter("(&(" + Constants.OBJECTCLASS + "="
					+ ServiceReceiver.class.getName() + ")(" + ComponentConstants.COMPONENT_NAME
					+ "=org.osgi.test.cases.component.tb25.ComponentServiceObjectsReceiver))");
			ServiceTracker<ServiceReceiver<ComponentServiceObjects<BaseService>>, ServiceReceiver<ComponentServiceObjects<BaseService>>> receiverTracker = new ServiceTracker<ServiceReceiver<ComponentServiceObjects<BaseService>>, ServiceReceiver<ComponentServiceObjects<BaseService>>>(
					getContext(), receiverFilter, null);
			try {
				assertEquals("wrong number of services", 0, counter.get());

				receiverTracker.open();
				ServiceReceiver<ComponentServiceObjects<BaseService>> r = receiverTracker.waitForService(SLEEP * 3);
				assertNotNull("missing receiver", r);
				ComponentServiceObjects<BaseService> cso = r.getServices().get(0);
				assertNotNull("missing ComponentServiceObjects", cso);
				assertEquals("wrong service reference", baseReg.getReference(), cso.getServiceReference());

				List<BaseService> services = new ArrayList<BaseService>(12);
				for (int i = 0; i < 12; i++) {
					BaseService b1 = cso.getService();
					assertNotNull("missing base", b1);
					assertFalse("wrong service", services.contains(b1));
					services.add(b1);
				}
				assertEquals("wrong number of services", 12, counter.get());
				try {
					cso.ungetService(MockFactory.newMock(BaseService.class, new Object()));
					failException("unget of invalid service succeeded", IllegalArgumentException.class);
				}
				catch (IllegalArgumentException e) {
					// expected
				}
				for (BaseService b1 : services) {
					cso.ungetService(b1);
				}

				assertEquals("wrong number of services", 0, counter.get());
				services = new ArrayList<BaseService>(12);
				for (int i = 0; i < 12; i++) {
					BaseService b1 = cso.getService();
					assertNotNull("missing base", b1);
					assertFalse("wrong service", services.contains(b1));
					services.add(b1);
				}
				assertEquals("wrong number of services", 12, counter.get());

				baseProps.put(ComponentConstants.COMPONENT_NAME, "unbind");
				baseReg.setProperties(baseProps);
				Sleep.sleep(SLEEP * 3); // wait for SCR to react to change
				assertNull("base not unbound", r.getServices().get(0));
				assertEquals("wrong number of services", 0, counter.get());

				assertNull("returned service object for unbound service", cso.getService());

				Promise<Void> p = scr.disableComponent(receiverDesc);
				p.getValue(); // wait for state change to complete
				assertFalse("receiverDesc is enabled", scr.isComponentEnabled(receiverDesc));

				try {
					cso.getService();
					failException("get on deactivated component succeeded", IllegalStateException.class);
				}
				catch (IllegalStateException e) {
					// expected
				}
			}
			finally {
				receiverTracker.close();
			}
		}
		finally {
			uninstallBundle(tb25);
			baseReg.unregister();
		}
	}

}

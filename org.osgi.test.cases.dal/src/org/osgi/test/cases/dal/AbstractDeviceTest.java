/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.test.cases.dal;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dal.Device;
import org.osgi.service.dal.Function;
import org.osgi.test.cases.step.TestStep;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Common class for all Device Abstraction Layer TCs. It contains some helper
 * methods.
 */
public abstract class AbstractDeviceTest extends DefaultTestBundleControl {

	/**
	 * A service listener, which can track {@code Device} and {@code Function}
	 * services.
	 */
	protected final DeviceServiceListener	deviceServiceListener;

	/**
	 * The service tracker tracks the test stepper service.
	 */
	private final ServiceTracker			testStepTracker;

	/**
	 * The constructor initializes the device listener and the stepper tracker.
	 */
	public AbstractDeviceTest() {
		this.testStepTracker = new ServiceTracker(super.getContext(), TestStep.class.getName(), null);
		this.deviceServiceListener = new DeviceServiceListener(super.getContext());
	}

	/**
	 * Registers the listeners and opens the trackers.
	 * 
	 * @see org.osgi.test.support.compatibility.DefaultTestBundleControl#setUp()
	 */
	protected void setUp() throws Exception {
		this.testStepTracker.open();
		this.deviceServiceListener.register();
	}

	/**
	 * Unregisters the listeners and closes the trackers.
	 * 
	 * @see org.osgi.test.support.compatibility.DefaultTestBundleControl#tearDown()
	 */
	protected void tearDown() throws Exception {
		this.testStepTracker.close();
		this.deviceServiceListener.unregister();
	}

	/**
	 * Returns the device with the given identifier.
	 * 
	 * @param deviceUID The device identifier.
	 * 
	 * @return The device with the given identifier.
	 * 
	 * @throws InvalidSyntaxException If the device identifier cannot build
	 *         valid LDAP filter.
	 */
	protected Device getDevice(final String deviceUID) throws InvalidSyntaxException {
		BundleContext bc = super.getContext();
		ServiceReference[] deviceSRefs = bc.getServiceReferences(
				Device.class.getName(), '(' + Device.SERVICE_UID + '=' + deviceUID + ')');
		assertEquals("One device with the given UID is expected.", 1, deviceSRefs.length);
		Device device = (Device) bc.getService(deviceSRefs[0]);
		assertNotNull("The device service is missing.", device);
		return device;
	}

	/**
	 * Returns the function with the specified property name and property value.
	 * Each argument can be {@code null}.
	 * 
	 * @param propName Specifies the property name, can be {@code null}.
	 * @param propValue Specifies the property value, can be {@code null}. That
	 *        means any value.
	 * 
	 * @return The functions according to the specified arguments.
	 * 
	 * @throws InvalidSyntaxException If invalid filter is built with the
	 *         specified arguments.
	 */
	protected Function[] getFunctions(String propName, String propValue) throws InvalidSyntaxException {
		String filter = null;
		if (null != propName) {
			if (null == propValue) {
				propValue = "*";
			}
			filter = '(' + propName + '=' + propValue + ')';
		}
		BundleContext bc = super.getContext();
		ServiceReference[] functionSRefs = bc.getServiceReferences(null, filter);
		assertNotNull("There is no function with property: " + propName, functionSRefs);
		Function[] functions = new Function[functionSRefs.length];
		for (int i = 0; i < functions.length; i++) {
			functions[i] = (Function) bc.getService(functionSRefs[i]);
			assertNotNull(
					"The function service is missing with UID: " + functionSRefs[i].getProperty(Device.SERVICE_UID),
					functions[i]);
		}
		return functions;
	}

	/**
	 * Checks that the given set of properties are available in the service
	 * reference.
	 * 
	 * @param serviceRef The reference to check.
	 * @param propNames The property names to check.
	 */
	protected void checkRequiredProperties(ServiceReference serviceRef, String[] propNames) {
		for (int i = 0; i < propNames.length; i++) {
			assertNotNull(propNames[i] + " property is missing",
					serviceRef.getProperty(propNames[i]));
		}
	}

	/**
	 * Returns the test stepper service.
	 * 
	 * @return The test stepper service.
	 */
	protected TestStep getTestStep() {
		TestStep testStep = (TestStep) this.testStepTracker.getService();
		assertNotNull("The test step service is misisng.", testStep);
		return testStep;
	}

}

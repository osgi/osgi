/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.osgi.test.cases.dal;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dal.Device;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.functions.data.BooleanData;
import org.osgi.test.cases.step.TestStep;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Common class for all Device Abstraction Layer TCs. It contains some helper
 * methods.
 */
public abstract class AbstractDeviceTest extends DefaultTestBundleControl {

	/**
	 * A service listener, which can track <code>Device</code> and
	 * <code>Function</code> services.
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
	 * Returns the function with the specified class name, property name and
	 * property value. Each argument can be <code>null</code>.
	 * 
	 * @param functionClassName Specifies the function class name, can be
	 *        <code>null</code>.
	 * @param propName Specifies the property name, can be <code>null</code>.
	 * @param propValue Specifies the property value, can be <code>null</code>.
	 *        That means any value.
	 * 
	 * @return The functions according to the specified arguments.
	 * 
	 * @throws InvalidSyntaxException If invalid filter is built with the
	 *         specified arguments.
	 */
	protected Function[] getFunctions(String functionClassName, final String propName, String propValue) throws InvalidSyntaxException {
		String filter = null;
		if (null != propName) {
			if (null == propValue) {
				propValue = "*";
			}
			filter = '(' + propName + '=' + propValue + ')';
		}
		BundleContext bc = super.getContext();
		ServiceReference[] functionSRefs = bc.getServiceReferences(functionClassName, filter);
		assertNotNull("There is no function with property: " + propName + " and class: " + functionClassName, functionSRefs);
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
		TestStep userInteraction = (TestStep) this.testStepTracker.getService();
		assertNotNull("The test step service is misisng. Test case must finish.", userInteraction);
		return userInteraction;
	}

	/**
	 * Asserts that the given <code>BooleanData</code> value is equivalent to
	 * the expected value.
	 * 
	 * @param expectedValue Exception value.
	 * @param actualData Actual value.
	 */
	protected void assertEquals(boolean expectedValue, BooleanData actualData) {
		assertNotNull("The boolean data is missing!", actualData);
		assertEquals("The boolean value is not correct!", expectedValue, actualData.getValue());
		assertTrue("The boolean data timestamp is not correct!",
				System.currentTimeMillis() >= actualData.getTimestamp());
	}

}

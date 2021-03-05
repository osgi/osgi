/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.test.cases.dal;

import java.util.Collection;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dal.Device;
import org.osgi.service.dal.Function;
import org.osgi.test.cases.dal.step.TestStepDeviceProxy;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * Common class for all Device Abstraction Layer TCs. It contains some helper
 * methods.
 */
public abstract class AbstractDeviceTest extends DefaultTestBundleControl {

	/**
	 * A service listener, which can track {@code Device} services.
	 */
	protected TestServiceListener	deviceServiceListener;

	/**
	 * The manual test steps are sent to the test step proxy.
	 */
	protected TestStepDeviceProxy	testStepProxy;

	/**
	 * initializes the listeners and the test step proxy.
	 * 
	 * @see org.osgi.test.support.compatibility.DefaultTestBundleControl#setUp()
	 */
	protected void setUp() throws Exception { // NOPMD
		super.setUp();
		this.testStepProxy = new TestStepDeviceProxy(super.getContext());
		this.deviceServiceListener = new TestServiceListener(
				super.getContext(), TestServiceListener.DEVICE_FILTER);
	}

	/**
	 * Unregisters the listeners and closes the test step proxy.
	 * 
	 * @see org.osgi.test.support.compatibility.DefaultTestBundleControl#tearDown()
	 */
	protected void tearDown() throws Exception { // //NOPMD
		this.testStepProxy.close();
		this.deviceServiceListener.unregister();
		super.tearDown();
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
	protected Device getDevice(String deviceUID) throws InvalidSyntaxException {
		BundleContext bc = super.getContext();
		Collection<ServiceReference<Device>> deviceSRefs = bc
				.getServiceReferences(Device.class,
						'(' + Device.SERVICE_UID + '=' + deviceUID + ')');
		assertEquals("One device with the given UID is expected.", 1,
				deviceSRefs.size());
		Device device = bc.getService(deviceSRefs.iterator().next());
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
		ServiceReference< ? >[] functionSRefs = bc
				.getServiceReferences((String) null, filter);
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
	protected void checkRequiredProperties(ServiceReference< ? > serviceRef,
			String[] propNames) {
		for (int i = 0; i < propNames.length; i++) {
			assertNotNull(propNames[i] + " property is missing",
					serviceRef.getProperty(propNames[i]));
		}
	}
}

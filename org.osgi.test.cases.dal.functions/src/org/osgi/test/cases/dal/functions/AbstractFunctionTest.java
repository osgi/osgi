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

package org.osgi.test.cases.dal.functions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dal.Device;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.FunctionData;
import org.osgi.service.dal.FunctionEvent;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.service.dal.functions.data.BooleanData;
import org.osgi.service.dal.functions.data.LevelData;
import org.osgi.test.cases.dal.step.TestStepDeviceProxy;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * Common class for all Device Abstraction Layer function TCs. It contains some
 * helper methods.
 */
public abstract class AbstractFunctionTest extends DefaultTestBundleControl {

	/**
	 * The manual test steps are sent to the test step proxy.
	 */
	protected TestStepDeviceProxy	testStepProxy;

	/**
	 * Initializes the test step proxy.
	 * 
	 * @see org.osgi.test.support.compatibility.DefaultTestBundleControl#setUp()
	 */
	protected void setUp() throws Exception { // NOPMD
		super.setUp();
		this.testStepProxy = new TestStepDeviceProxy(super.getContext());
	}

	/**
	 * Closes the test step proxy.
	 * 
	 * @see org.osgi.test.support.compatibility.DefaultTestBundleControl#tearDown()
	 */
	protected void tearDown() throws Exception { // NOPMD
		this.testStepProxy.close();
		super.tearDown();
	}

	/**
	 * Returns the function with the specified class name.
	 * 
	 * @param functionClassName Specifies the function class name, can be
	 *        {@code null}.
	 * 
	 * @return The functions according to the specified arguments.
	 */
	protected Function[] getFunctions(String functionClassName) {
		BundleContext bc = super.getContext();
		ServiceReference< ? >[] functionSRefs = null;
		try {
			functionSRefs = bc.getServiceReferences(functionClassName, null);
		} catch (InvalidSyntaxException e) {
			// not possible, the filter is not used
		}
		assertNotNull("At least one function is expected of: " + functionClassName, functionSRefs);
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
	 * Asserts that the given {@code BooleanData} value is equivalent to the
	 * expected value.
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

	/**
	 * Asserts that the given {@code LevelData} level is equivalent to the
	 * expected level. If metadata is available, the metadata consistency is
	 * checked too.
	 * 
	 * @param expectedMetadata The metadata to check.
	 * @param expectedValue THe expected value.
	 * @param actualData The actual value.
	 */
	protected void assertEquals(PropertyMetadata expectedMetadata, BigDecimal expectedValue, LevelData actualData) {
		assertNotNull("The level data is missing!", actualData);
		assertEquals("The level value is not correct!", expectedValue, actualData.getLevel());
		assertTrue("The level data timestamp is not correct!",
				System.currentTimeMillis() >= actualData.getTimestamp());
		String valueUnit = actualData.getUnit();
		if ((null == expectedMetadata) || (null == valueUnit)) {
			return;
		}
		Map<String, ? > additionalMetadata = expectedMetadata.getMetadata(null);
		if (null == additionalMetadata) {
			return;
		}
		String[] units = (String[]) additionalMetadata.get(PropertyMetadata.UNITS);
		if (null == units) {
			return;
		}
		for (int i = 0; i < units.length; i++) {
			if (valueUnit.equals(units[i])) {
				return;
			}
		}
		fail("The level data unit is not availale in metadata: " + valueUnit);
	}

	/**
	 * Asserts the function data field values.
	 * 
	 * @param timestamp The timestamp to check.
	 * @param metadata The metadata to check.
	 * @param actualData The actual data.
	 */
	protected void assertFunctionDataFields(long timestamp,
			Map<String, ? > metadata, FunctionData actualData) {
		// time stamp
		assertEquals(
				"The timestamp is not correct!",
				timestamp,
				actualData.getTimestamp());

		// metadata
		assertEquals(
				"The metadata is not correct!",
				metadata,
				actualData.getMetadata());
	}

	/**
	 * Validates the function event with a manual test step.
	 * 
	 * @param functionClassName The function class name.
	 * @param propName The function property name.
	 * @param stepId The test step identifier.
	 * @param stepMessage The test step message.
	 */
	protected void checkPropertyEvent(String functionClassName, String propName, String stepId, String stepMessage) {
		Function[] functions = getFunctions(
				functionClassName, PropertyMetadata.ACCESS_EVENTABLE);
		String functionUID = (String) functions[0].getServiceProperty(Function.SERVICE_UID);
		FunctionEventHandler eventHandler = new FunctionEventHandler(super.getContext());
		eventHandler.register(functionUID, propName);
		FunctionEvent functionEvent;
		try {
			this.testStepProxy.execute(stepId, stepMessage);
			functionEvent = eventHandler.getEvents(1)[0];
		} finally {
			eventHandler.unregister();
		}
		assertNotNull("No data in the alarm event.", functionEvent.getFunctionPropertyValue());
		assertEquals(
				"The event function identifier is not correct!",
				functionUID,
				functionEvent.getFunctionUID());
		assertEquals(
				"The property name is not correct!",
				propName,
				functionEvent.getFunctionPropertyName());
	}

	/**
	 * Returns all functions with the given class and at least one property with
	 * the desired access.
	 * 
	 * @param functionClass The function class.
	 * @param propertyAccess The functions must have at least one property with
	 *        the given access.
	 * 
	 * @return All functions with the given class and at least one property with
	 *         the desired access.
	 */
	protected Function[] getFunctions(String functionClass, int propertyAccess) {
		try {
			ServiceReference< ? >[] functionSRefs = super.getContext()
					.getServiceReferences(
					functionClass, '(' + Function.SERVICE_PROPERTY_NAMES + "=*)");
			assertNotNull(
					"There are no functions of type: " + functionClass + " and property access: " + propertyAccess,
					functionSRefs);
			List<Function> result = new ArrayList<>(functionSRefs.length);
			for (int i = 0; i < functionSRefs.length; i++) {
				Function function = (Function) super.getContext().getService(functionSRefs[i]);
				if (null == function) {
					continue;
				}
				String[] propertyNames = (String[]) function.getServiceProperty(
						Function.SERVICE_PROPERTY_NAMES);
				for (int ii = 0; ii < propertyNames.length; ii++) {
					if (isPropertyAccessValid(function, propertyNames[ii], propertyAccess)) {
						result.add(function);
						break;
					}
				}
			}
			assertFalse(
					"There is no function, which contains a proeprty with an access: " + propertyAccess,
					result.isEmpty());
			return result.toArray(new Function[0]);
		} catch (InvalidSyntaxException e) {
			// the filter is valid
		}
		return null;
	}

	private static boolean isPropertyAccessValid(
			Function function, String propertyName, int propertyAccess) {
		PropertyMetadata propertyMetadata = function.getPropertyMetadata(propertyName);
		if (null == propertyMetadata) {
			return false;
		}
		Map<String, ? > metadata = propertyMetadata.getMetadata(null);
		if (null == metadata) {
			return false;
		}
		Integer accessType = (Integer) metadata.get(PropertyMetadata.ACCESS);
		return (null != accessType) && (propertyAccess == (accessType.intValue() & propertyAccess));
	}
}

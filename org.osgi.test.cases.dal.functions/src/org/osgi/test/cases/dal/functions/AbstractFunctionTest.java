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


package org.osgi.test.cases.dal.functions;

import java.math.BigDecimal;
import java.util.Map;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dal.Device;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.FunctionData;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.service.dal.functions.data.BooleanData;
import org.osgi.service.dal.functions.data.LevelData;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * Common class for all Device Abstraction Layer function TCs. It contains some
 * helper methods.
 */
public abstract class AbstractFunctionTest extends DefaultTestBundleControl {

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

	/**
	 * Asserts that the given <code>LevelData</code> level is equivalent to the
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
		Map additionalMetadata = expectedMetadata.getMetadata(null);
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
	protected void assertFunctionDataFields(long timestamp, Map metadata, FunctionData actualData) {
		// timestamp
		assertEquals(
				"The timestamp field is not correct!",
				timestamp,
				actualData.timestamp);
		assertEquals(
				"The timestamp is not correct!",
				timestamp,
				actualData.getTimestamp());

		// metadata
		assertEquals(
				"The metadata field is not correct!",
				metadata,
				actualData.metadata);
		assertEquals(
				"The metadata is not correct!",
				metadata,
				actualData.getMetadata());
	}

}

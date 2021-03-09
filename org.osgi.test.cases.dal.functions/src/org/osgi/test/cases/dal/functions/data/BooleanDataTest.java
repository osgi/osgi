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

package org.osgi.test.cases.dal.functions.data;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.dal.FunctionData;
import org.osgi.service.dal.functions.data.BooleanData;
import org.osgi.test.cases.dal.functions.AbstractFunctionTest;

/**
 * Validates the {@code BooleanData} data structure.
 */
public final class BooleanDataTest extends AbstractFunctionTest {

	/**
	 * Checks {@link BooleanData#equals(Object)} method.
	 */
	public void testEquals() {
		// check without metadata
		BooleanData data = new BooleanData(Long.MIN_VALUE, null, true);
		assertEquals("The boolean data comparison is wrong!",
				data,
				data);
		assertEquals("The boolean data comparison is wrong!",
				data,
				new BooleanData(Long.MIN_VALUE, null, true));

		// check with metadata
		Map<String,Object> metadata = new HashMap<>();
		metadata.put(FunctionData.DESCRIPTION, "test-description");
		data = new BooleanData(Long.MIN_VALUE, metadata, false);
		assertEquals("The boolean data comparison is wrong!",
				data,
				data);
		assertEquals("The boolean data comparison is wrong!",
				data,
				new BooleanData(Long.MIN_VALUE, metadata, false));

		// check with fields map
		Map<String,Object> fields = new HashMap<>();
		fields.put(BooleanData.FIELD_VALUE, Boolean.FALSE);
		fields.put(FunctionData.FIELD_TIMESTAMP, Long.valueOf(Long.MIN_VALUE));
		fields.put(FunctionData.FIELD_METADATA, metadata);
		data = new BooleanData(fields);
		assertEquals("The boolean data comparison is wrong!",
				data,
				data);
		assertEquals("The boolean data comparison is wrong!",
				data,
				new BooleanData(Long.MIN_VALUE, metadata, false));
	}

	/**
	 * Checks {@link BooleanData#compareTo(Object)} with {@code BooleanData}.
	 */
	public void testComparison() {
		// check without metadata
		BooleanData data = new BooleanData(Long.MIN_VALUE, null, true);
		assertEquals(
				"The boolean data comparison is wrong!",
				0, data.compareTo(data));
		assertEquals(
				"The boolean data comparison is wrong!",
				0, data.compareTo(new BooleanData(Long.MIN_VALUE, null, true)));

		// check with metadata
		Map<String,Object> metadata = new HashMap<>();
		metadata.put(FunctionData.DESCRIPTION, "test-description");
		data = new BooleanData(Long.MIN_VALUE, metadata, false);
		assertEquals(
				"The boolean data comparison is wrong!",
				0, data.compareTo(data));
		assertEquals(
				"The boolean data comparison is wrong!",
				0, data.compareTo(new BooleanData(Long.MIN_VALUE, metadata, false)));

		// check with fields map
		Map<String,Object> fields = new HashMap<>();
		fields.put(BooleanData.FIELD_VALUE, Boolean.FALSE);
		fields.put(FunctionData.FIELD_TIMESTAMP, Long.valueOf(Long.MIN_VALUE));
		fields.put(FunctionData.FIELD_METADATA, metadata);
		data = new BooleanData(fields);
		assertEquals(
				"The boolean data comparison is wrong!",
				0, data.compareTo(data));
		assertEquals("The boolean data comparison is wrong!",
				0, data.compareTo(new BooleanData(Long.MIN_VALUE, metadata, false)));
	}

	/**
	 * Checks {@link BooleanData#hashCode()}.
	 */
	public void testHashCode() {
		// check without metadata
		BooleanData data = new BooleanData(Long.MIN_VALUE, null, true);
		assertEquals("The boolean data hash code is wrong!",
				data.hashCode(),
				data.hashCode());
		assertEquals("The boolean data hash code is wrong!",
				data.hashCode(),
				(new BooleanData(Long.MIN_VALUE, null, true)).hashCode());

		// check with metadata
		Map<String,Object> metadata = new HashMap<>();
		metadata.put(FunctionData.DESCRIPTION, "test-description");
		data = new BooleanData(Long.MIN_VALUE, metadata, false);
		assertEquals("The boolean data hash code is wrong!",
				data.hashCode(),
				data.hashCode());
		assertEquals("The boolean data hash code is wrong!",
				data.hashCode(),
				(new BooleanData(Long.MIN_VALUE, metadata, false)).hashCode());

		// check with fields map
		Map<String,Object> fields = new HashMap<>();
		fields.put(BooleanData.FIELD_VALUE, Boolean.FALSE);
		fields.put(FunctionData.FIELD_TIMESTAMP, Long.valueOf(Long.MIN_VALUE));
		fields.put(FunctionData.FIELD_METADATA, metadata);
		data = new BooleanData(fields);
		assertEquals("The boolean data hash code is wrong!",
				data.hashCode(),
				data.hashCode());
		assertEquals("The boolean data hash code is wrong!",
				data.hashCode(),
				(new BooleanData(Long.MIN_VALUE, metadata, false)).hashCode());
	}

	/**
	 * Checks {@code BooleanData} field values.
	 */
	public void testFields() {
		// check without metadata
		BooleanData data = new BooleanData(Long.MIN_VALUE, null, true);
		checkBooleanDataFields(Long.MIN_VALUE, null, true, data);

		// check with metadata
		Map<String,Object> metadata = new HashMap<>();
		metadata.put(FunctionData.DESCRIPTION, "test-description");
		data = new BooleanData(Long.MIN_VALUE, metadata, false);
		checkBooleanDataFields(Long.MIN_VALUE, metadata, false, data);

		// check with fields map
		Map<String,Object> fields = new HashMap<>();
		fields.put(BooleanData.FIELD_VALUE, Boolean.FALSE);
		fields.put(FunctionData.FIELD_TIMESTAMP, Long.valueOf(Long.MIN_VALUE));
		fields.put(FunctionData.FIELD_METADATA, metadata);
		data = new BooleanData(fields);
		checkBooleanDataFields(Long.MIN_VALUE, metadata, false, data);
	}

	/**
	 * Checks the {@code BooleanData} construction with an invalid fields.
	 */
	public void testInvalidFields() {
		Map<String,Object> fields = new HashMap<>();
		fields.put(BooleanData.FIELD_VALUE, "wrong-type");
		checkInvalidFieldType(fields);

		fields.clear();
		fields.put(BooleanData.FIELD_VALUE, Boolean.TRUE);
		fields.put(BooleanData.FIELD_METADATA, Boolean.TRUE);
		checkInvalidFieldType(fields);

		fields.clear();
		fields.put(BooleanData.FIELD_VALUE, Boolean.TRUE);
		fields.put(BooleanData.FIELD_TIMESTAMP, Boolean.TRUE);
		checkInvalidFieldType(fields);

		fields.clear();
		try {
			new BooleanData(fields);
			fail("The boolean data is built with empty fields.");
		} catch (IllegalArgumentException iae) {
			// go ahead, it's expected
		}

		try {
			new BooleanData(null);
			fail("The boolean data is built with null fields");
		} catch (NullPointerException npe) { // NOPMD
			// go ahead, it's expected
		}
	}

	/**
	 * Checks {@link BooleanData#toString()}.
	 */
	public void testToString() {
		BooleanData booleanData = new BooleanData(
				System.currentTimeMillis(),
				new HashMap<>(),
				true);
		assertNotNull("There is no string representation of the boolean data.", booleanData.toString());
		booleanData = new BooleanData(
				Long.MIN_VALUE,
				null,
				false);
		assertNotNull("There is no string representation of the boolean data.", booleanData.toString());
	}

	private void checkInvalidFieldType(Map<String, ? > fields) {
		try {
			new BooleanData(fields);
			fail("The boolean data is built with invalid fields: " + fields);
		} catch (ClassCastException cce) {
			// go ahead, it's expected
		}
	}

	private void checkBooleanDataFields(long timestamp,
			Map<String, ? > metadata, boolean value, BooleanData actualData) {
		super.assertFunctionDataFields(timestamp, metadata, actualData);
		assertEquals(
				"The value is not correct!",
				value,
				actualData.getValue());
	}
}

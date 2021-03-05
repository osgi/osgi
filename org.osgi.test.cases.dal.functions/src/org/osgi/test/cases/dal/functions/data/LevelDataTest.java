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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.dal.FunctionData;
import org.osgi.service.dal.SIUnits;
import org.osgi.service.dal.functions.data.LevelData;
import org.osgi.test.cases.dal.functions.AbstractFunctionTest;

/**
 * Validates the {@code LevelData} data structure.
 */
public final class LevelDataTest extends AbstractFunctionTest {

	private static final BigDecimal	TEST_VALUE	= new BigDecimal("1.00001");
	private static final String		TEST_UNIT	= "test-unit";

	/**
	 * Checks {@link LevelData#equals(Object)} method.
	 */
	public void testEquals() {
		// check without metadata
		LevelData data = new LevelData(Long.MIN_VALUE, null, TEST_VALUE, null);
		assertEquals("The level data comparison is wrong!",
				data,
				data);
		assertEquals("The level data comparison is wrong!",
				data,
				new LevelData(Long.MIN_VALUE, null, TEST_VALUE, null));

		// check with metadata
		Map<String,Object> metadata = new HashMap<>();
		metadata.put(FunctionData.DESCRIPTION, "test-description");
		data = new LevelData(Long.MIN_VALUE, metadata, TEST_VALUE, TEST_UNIT);
		assertEquals("The level data comparison is wrong!",
				data,
				data);
		assertEquals("The level data comparison is wrong!",
				data,
				new LevelData(Long.MIN_VALUE, metadata, TEST_VALUE, TEST_UNIT));

		// check with fields map
		Map<String,Object> fields = new HashMap<>();
		fields.put(LevelData.FIELD_LEVEL, TEST_VALUE);
		fields.put(FunctionData.FIELD_TIMESTAMP, Long.valueOf(Long.MIN_VALUE));
		fields.put(FunctionData.FIELD_METADATA, metadata);
		fields.put(LevelData.FIELD_UNIT, TEST_UNIT);
		data = new LevelData(fields);
		assertEquals("The level data comparison is wrong!",
				data,
				data);
		assertEquals("The level data comparison is wrong!",
				data,
				new LevelData(Long.MIN_VALUE, metadata, TEST_VALUE, TEST_UNIT));
	}

	/**
	 * Checks {@link LevelData#compareTo(Object)} with {@code LevelData}.
	 */
	public void testComparison() {
		// check without metadata
		LevelData data = new LevelData(Long.MIN_VALUE, null, TEST_VALUE, null);
		assertEquals(
				"The level data comparison is wrong!",
				0, data.compareTo(data));
		assertEquals(
				"The level data comparison is wrong!",
				0, data.compareTo(new LevelData(Long.MIN_VALUE, null, TEST_VALUE, null)));

		// check with metadata
		Map<String,Object> metadata = new HashMap<>();
		metadata.put(FunctionData.DESCRIPTION, "test-description");
		data = new LevelData(Long.MIN_VALUE, metadata, TEST_VALUE, TEST_UNIT);
		assertEquals(
				"The level data comparison is wrong!",
				0, data.compareTo(data));
		assertEquals(
				"The level data comparison is wrong!",
				0, data.compareTo(new LevelData(Long.MIN_VALUE, metadata, TEST_VALUE, TEST_UNIT)));

		// check with fields map
		Map<String,Object> fields = new HashMap<>();
		fields.put(LevelData.FIELD_LEVEL, TEST_VALUE);
		fields.put(FunctionData.FIELD_TIMESTAMP, Long.valueOf(Long.MIN_VALUE));
		fields.put(FunctionData.FIELD_METADATA, metadata);
		fields.put(LevelData.FIELD_UNIT, TEST_UNIT);
		data = new LevelData(fields);
		assertEquals(
				"The level data comparison is wrong!",
				0, data.compareTo(data));
		assertEquals("The level data comparison is wrong!",
				0, data.compareTo(new LevelData(Long.MIN_VALUE, metadata, TEST_VALUE, TEST_UNIT)));
	}

	/**
	 * Checks {@link LevelData#hashCode()}.
	 */
	public void testHashCode() {
		// check without metadata
		LevelData data = new LevelData(Long.MIN_VALUE, null, TEST_VALUE, null);
		assertEquals("The level data hash code is wrong!",
				data.hashCode(),
				data.hashCode());
		assertEquals("The level data hash code is wrong!",
				data.hashCode(),
				(new LevelData(Long.MIN_VALUE, null, TEST_VALUE, null)).hashCode());

		// check with metadata
		Map<String,Object> metadata = new HashMap<>();
		metadata.put(FunctionData.DESCRIPTION, "test-description");
		data = new LevelData(Long.MIN_VALUE, metadata, TEST_VALUE, TEST_UNIT);
		assertEquals("The level data hash code is wrong!",
				data.hashCode(),
				data.hashCode());
		assertEquals("The level data hash code is wrong!",
				data.hashCode(),
				(new LevelData(Long.MIN_VALUE, metadata, TEST_VALUE, TEST_UNIT)).hashCode());

		// check with fields map
		Map<String,Object> fields = new HashMap<>();
		fields.put(LevelData.FIELD_LEVEL, TEST_VALUE);
		fields.put(FunctionData.FIELD_TIMESTAMP, Long.valueOf(Long.MIN_VALUE));
		fields.put(FunctionData.FIELD_METADATA, metadata);
		fields.put(LevelData.FIELD_UNIT, TEST_UNIT);
		data = new LevelData(fields);
		assertEquals("The level data hash code is wrong!",
				data.hashCode(),
				data.hashCode());
		assertEquals("The level data hash code is wrong!",
				data.hashCode(),
				(new LevelData(Long.MIN_VALUE, metadata, TEST_VALUE, TEST_UNIT)).hashCode());
	}

	/**
	 * Checks {@code LevelData} field values.
	 */
	public void testFields() {
		// check without metadata
		LevelData data = new LevelData(Long.MIN_VALUE, null, TEST_VALUE, null);
		checkLevelDataFields(Long.MIN_VALUE, null, TEST_VALUE, null, data);

		// check with metadata
		Map<String,Object> metadata = new HashMap<>();
		metadata.put(FunctionData.DESCRIPTION, "test-description");
		data = new LevelData(Long.MIN_VALUE, metadata, TEST_VALUE, TEST_UNIT);
		checkLevelDataFields(Long.MIN_VALUE, metadata, TEST_VALUE, TEST_UNIT, data);

		// check with fields map
		Map<String,Object> fields = new HashMap<>();
		fields.put(LevelData.FIELD_LEVEL, TEST_VALUE);
		fields.put(FunctionData.FIELD_TIMESTAMP, Long.valueOf(Long.MIN_VALUE));
		fields.put(FunctionData.FIELD_METADATA, metadata);
		fields.put(LevelData.FIELD_UNIT, TEST_UNIT);
		data = new LevelData(fields);
		checkLevelDataFields(Long.MIN_VALUE, metadata, TEST_VALUE, TEST_UNIT, data);
	}

	/**
	 * Checks the {@code LevelData} construction with an invalid fields.
	 */
	public void testInvalidFields() {
		Map<String,Object> fields = new HashMap<>();
		fields.put(LevelData.FIELD_LEVEL, "wrong-type");
		checkInvalidFieldType(fields);

		BigDecimal testValue = new BigDecimal("1.0001");
		fields.clear();
		fields.put(LevelData.FIELD_LEVEL, testValue);
		fields.put(LevelData.FIELD_UNIT, Boolean.TRUE);
		checkInvalidFieldType(fields);

		fields.clear();
		fields.put(LevelData.FIELD_LEVEL, testValue);
		fields.put(LevelData.FIELD_METADATA, Boolean.TRUE);
		checkInvalidFieldType(fields);

		fields.clear();
		fields.put(LevelData.FIELD_LEVEL, testValue);
		fields.put(LevelData.FIELD_TIMESTAMP, Boolean.TRUE);
		checkInvalidFieldType(fields);

		fields.clear();
		try {
			new LevelData(fields);
			fail("The level data is built with empty fields.");
		} catch (IllegalArgumentException iae) {
			// go ahead, it's expected
		}

		try {
			new LevelData(null);
			fail("The level data is built with null fields");
		} catch (NullPointerException npe) { // NOPMD
			// go ahead, it's expected
		}

		try {
			new LevelData(System.currentTimeMillis(), new HashMap<>(), null,
					SIUnits.AMPERE);
			fail("The level data is built with null level.");
		} catch (NullPointerException npe) { // NOPMD
			// go ahead, it's expected
		}
	}

	/**
	 * Checks {@link LevelData#toString()}.
	 */
	public void testToString() {
		LevelData levelData = new LevelData(
				System.currentTimeMillis(),
				new HashMap<>(),
				new BigDecimal("1.00001"),
				SIUnits.AMPERE);
		assertNotNull("There is no string representation of the level data.", levelData.toString());
		levelData = new LevelData(
				System.currentTimeMillis(),
				null,
				new BigDecimal("1.00001"),
				null);
		assertNotNull("There is no string representation of the level data.", levelData.toString());
	}

	private void checkInvalidFieldType(Map<String, ? > fields) {
		try {
			new LevelData(fields);
			fail("The level data is built with invalid fields: " + fields);
		} catch (ClassCastException cce) {
			// go ahead, it's expected
		}
	}

	private void checkLevelDataFields(long timestamp, Map<String, ? > metadata,
			BigDecimal level, String unit, LevelData actualData) {
		super.assertFunctionDataFields(timestamp, metadata, actualData);
		// unit
		assertEquals(
				"The metadata is not correct!",
				unit,
				actualData.getUnit());

		// value
		assertEquals(
				"The level is not correct!",
				level,
				actualData.getLevel());
	}
}

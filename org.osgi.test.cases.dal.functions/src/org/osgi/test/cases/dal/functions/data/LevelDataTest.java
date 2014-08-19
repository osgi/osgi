/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.test.cases.dal.functions.data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.osgi.service.dal.FunctionData;
import org.osgi.service.dal.functions.data.LevelData;
import org.osgi.test.cases.dal.functions.AbstractFunctionTest;

/**
 * Validates the {@code LevelData} data structure.
 */
public final class LevelDataTest extends AbstractFunctionTest {

	private static final BigDecimal	TEST_VALUE	= new BigDecimal(1.00001);
	private static final String		TEST_UNIT	= "test-unit";

	/**
	 * Checks {@link LevelData#equals(Object)} method.
	 */
	public void testEquals() {
		// check without metadata
		LevelData data = new LevelData(Long.MIN_VALUE, null, null, TEST_VALUE);
		assertEquals("The level data comparison is wrong!",
				data,
				data);
		assertEquals("The level data comparison is wrong!",
				data,
				new LevelData(Long.MIN_VALUE, null, null, TEST_VALUE));

		// check with metadata
		Map metadata = new HashMap();
		metadata.put(FunctionData.META_INFO_DESCRIPTION, "test-description");
		data = new LevelData(Long.MIN_VALUE, metadata, TEST_UNIT, TEST_VALUE);
		assertEquals("The level data comparison is wrong!",
				data,
				data);
		assertEquals("The level data comparison is wrong!",
				data,
				new LevelData(Long.MIN_VALUE, metadata, TEST_UNIT, TEST_VALUE));

		// check with fields map
		Map fields = new HashMap();
		fields.put(LevelData.FIELD_LEVEL, TEST_VALUE);
		fields.put(FunctionData.FIELD_TIMESTAMP, new Long(Long.MIN_VALUE));
		fields.put(FunctionData.FIELD_METADATA, metadata);
		fields.put(LevelData.FIELD_UNIT, TEST_UNIT);
		data = new LevelData(fields);
		assertEquals("The level data comparison is wrong!",
				data,
				data);
		assertEquals("The level data comparison is wrong!",
				data,
				new LevelData(Long.MIN_VALUE, metadata, TEST_UNIT, TEST_VALUE));
	}

	/**
	 * Checks {@link LevelData#compareTo(Object)} with {@code LevelData}.
	 */
	public void testLevelDataComparison() {
		// check without metadata
		LevelData data = new LevelData(Long.MIN_VALUE, null, null, TEST_VALUE);
		assertEquals(
				"The level data comparison is wrong!",
				0, data.compareTo(data));
		assertEquals(
				"The level data comparison is wrong!",
				0, data.compareTo(new LevelData(Long.MIN_VALUE, null, null, TEST_VALUE)));

		// check with metadata
		Map metadata = new HashMap();
		metadata.put(FunctionData.META_INFO_DESCRIPTION, "test-description");
		data = new LevelData(Long.MIN_VALUE, metadata, TEST_UNIT, TEST_VALUE);
		assertEquals(
				"The level data comparison is wrong!",
				0, data.compareTo(data));
		assertEquals(
				"The level data comparison is wrong!",
				0, data.compareTo(new LevelData(Long.MIN_VALUE, metadata, TEST_UNIT, TEST_VALUE)));

		// check with fields map
		Map fields = new HashMap();
		fields.put(LevelData.FIELD_LEVEL, TEST_VALUE);
		fields.put(FunctionData.FIELD_TIMESTAMP, new Long(Long.MIN_VALUE));
		fields.put(FunctionData.FIELD_METADATA, metadata);
		fields.put(LevelData.FIELD_UNIT, TEST_UNIT);
		data = new LevelData(fields);
		assertEquals(
				"The level data comparison is wrong!",
				0, data.compareTo(data));
		assertEquals("The level data comparison is wrong!",
				0, data.compareTo(new LevelData(Long.MIN_VALUE, metadata, TEST_UNIT, TEST_VALUE)));
	}

	/**
	 * Checks {@link LevelData#compareTo(Object)} with {@code BigDecimal}.
	 */
	public void testBigDecimalComparison() {
		// check without metadata
		LevelData data = new LevelData(Long.MIN_VALUE, null, null, TEST_VALUE);
		assertEquals(
				"The boolean comparison is wrong!",
				0, data.compareTo(TEST_VALUE));

		// check with metadata
		Map metadata = new HashMap();
		metadata.put(FunctionData.META_INFO_DESCRIPTION, "test-description");
		data = new LevelData(Long.MIN_VALUE, metadata, TEST_UNIT, TEST_VALUE);
		assertEquals(
				"The level data comparison is wrong!",
				0, data.compareTo(TEST_VALUE));

		// check with fields map
		Map fields = new HashMap();
		fields.put(LevelData.FIELD_LEVEL, TEST_VALUE);
		fields.put(FunctionData.FIELD_TIMESTAMP, new Long(Long.MIN_VALUE));
		fields.put(FunctionData.FIELD_METADATA, metadata);
		fields.put(LevelData.FIELD_UNIT, TEST_UNIT);
		data = new LevelData(fields);
		assertEquals(
				"The level data comparison is wrong!",
				0, data.compareTo(TEST_VALUE));
	}

	/**
	 * Checks {@link LevelData#compareTo(Object)} with {@code Map}.
	 */
	public void testMapComparison() {
		// check without metadata
		LevelData data = new LevelData(Long.MIN_VALUE, null, null, TEST_VALUE);
		Map dataMap = new HashMap();
		dataMap.put("level", TEST_VALUE);
		dataMap.put("timestamp", new Long(Long.MIN_VALUE));
		assertEquals(
				"The boolean comparison is wrong!",
				0, data.compareTo(dataMap));

		// check with metadata
		Map metadata = new HashMap();
		metadata.put(FunctionData.META_INFO_DESCRIPTION, "test-description");
		dataMap.put(LevelData.FIELD_LEVEL, TEST_VALUE);
		dataMap.put(FunctionData.FIELD_TIMESTAMP, new Long(Long.MIN_VALUE));
		dataMap.put(FunctionData.FIELD_METADATA, metadata);
		dataMap.put(LevelData.FIELD_UNIT, TEST_UNIT);
		data = new LevelData(Long.MIN_VALUE, metadata, TEST_UNIT, TEST_VALUE);
		assertEquals(
				"The level data comparison is wrong!",
				0, data.compareTo(dataMap));

		// check with fields map
		Map fields = new HashMap();
		fields.put(LevelData.FIELD_LEVEL, TEST_VALUE);
		fields.put(FunctionData.FIELD_TIMESTAMP, new Long(Long.MIN_VALUE));
		fields.put(FunctionData.FIELD_METADATA, metadata);
		fields.put(LevelData.FIELD_UNIT, TEST_UNIT);
		data = new LevelData(fields);
		assertEquals(
				"The level data comparison is wrong!",
				0, data.compareTo(fields));
	}

	/**
	 * Checks {@link LevelData#hashCode()}.
	 */
	public void testHashCode() {
		// check without metadata
		LevelData data = new LevelData(Long.MIN_VALUE, null, null, TEST_VALUE);
		assertEquals("The level data hash code is wrong!",
				data.hashCode(),
				data.hashCode());
		assertEquals("The level data hash code is wrong!",
				data.hashCode(),
				(new LevelData(Long.MIN_VALUE, null, null, TEST_VALUE)).hashCode());

		// check with metadata
		Map metadata = new HashMap();
		metadata.put(FunctionData.META_INFO_DESCRIPTION, "test-description");
		data = new LevelData(Long.MIN_VALUE, metadata, TEST_UNIT, TEST_VALUE);
		assertEquals("The level data hash code is wrong!",
				data.hashCode(),
				data.hashCode());
		assertEquals("The level data hash code is wrong!",
				data.hashCode(),
				(new LevelData(Long.MIN_VALUE, metadata, TEST_UNIT, TEST_VALUE)).hashCode());

		// check with fields map
		Map fields = new HashMap();
		fields.put(LevelData.FIELD_LEVEL, TEST_VALUE);
		fields.put(FunctionData.FIELD_TIMESTAMP, new Long(Long.MIN_VALUE));
		fields.put(FunctionData.FIELD_METADATA, metadata);
		fields.put(LevelData.FIELD_UNIT, TEST_UNIT);
		data = new LevelData(fields);
		assertEquals("The level data hash code is wrong!",
				data.hashCode(),
				data.hashCode());
		assertEquals("The level data hash code is wrong!",
				data.hashCode(),
				(new LevelData(Long.MIN_VALUE, metadata, TEST_UNIT, TEST_VALUE)).hashCode());
	}

	/**
	 * Checks {@code LevelData} field values.
	 */
	public void testLevelDataFields() {
		// check without metadata
		LevelData data = new LevelData(Long.MIN_VALUE, null, null, TEST_VALUE);
		checkLevelDataFields(Long.MIN_VALUE, null, null, TEST_VALUE, data);

		// check with metadata
		Map metadata = new HashMap();
		metadata.put(FunctionData.META_INFO_DESCRIPTION, "test-description");
		data = new LevelData(Long.MIN_VALUE, metadata, TEST_UNIT, TEST_VALUE);
		checkLevelDataFields(Long.MIN_VALUE, metadata, TEST_UNIT, TEST_VALUE, data);

		// check with fields map
		Map fields = new HashMap();
		fields.put(LevelData.FIELD_LEVEL, TEST_VALUE);
		fields.put(FunctionData.FIELD_TIMESTAMP, new Long(Long.MIN_VALUE));
		fields.put(FunctionData.FIELD_METADATA, metadata);
		fields.put(LevelData.FIELD_UNIT, TEST_UNIT);
		data = new LevelData(fields);
		checkLevelDataFields(Long.MIN_VALUE, metadata, TEST_UNIT, TEST_VALUE, data);
	}

	/**
	 * Checks the {@code LevelData} construction with an invalid fields.
	 */
	public void testInvalidFields() {
		final Map fields = new HashMap();
		fields.put(LevelData.FIELD_LEVEL, "wrong-type");
		checkInvalidFieldType(fields);

		final BigDecimal testValue = new BigDecimal(1.0001);
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
		} catch (NullPointerException npe) {
			// go ahead, it's expected
		}
	}

	private void checkInvalidFieldType(Map fields) {
		try {
			new LevelData(fields);
			fail("The level data is built with invalid fields: " + fields);
		} catch (ClassCastException cce) {
			// go ahead, it's expected
		}
	}

	private void checkLevelDataFields(long timestamp, Map metadata, String unit, BigDecimal level, LevelData actualData) {
		super.assertFunctionDataFields(timestamp, metadata, actualData);
		// unit
		assertEquals(
				"The metadata field is not correct!",
				unit,
				actualData.unit);
		assertEquals(
				"The metadata is not correct!",
				unit,
				actualData.getUnit());

		// value
		assertEquals(
				"The level field is not correct!",
				level,
				actualData.level);
		assertEquals(
				"The level is not correct!",
				level,
				actualData.getLevel());
	}

}

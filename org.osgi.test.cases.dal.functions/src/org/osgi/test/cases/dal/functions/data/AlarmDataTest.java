/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.test.cases.dal.functions.data;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.dal.FunctionData;
import org.osgi.service.dal.functions.data.AlarmData;
import org.osgi.test.cases.dal.functions.AbstractFunctionTest;

/**
 * Validates the {@code AlarmData} data structure.
 */
public final class AlarmDataTest extends AbstractFunctionTest {

	/**
	 * Checks {@link AlarmData#equals(Object)} method.
	 */
	public void testEquals() {
		// check without metadata
		AlarmData data = new AlarmData(Long.MIN_VALUE, null, AlarmData.SEVERITY_UNDEFINED, AlarmData.TYPE_COLD);
		assertEquals("The alarm data comparison is wrong!",
				data,
				data);
		assertEquals("The alarm data comparison is wrong!",
				data,
				new AlarmData(Long.MIN_VALUE, null, AlarmData.SEVERITY_UNDEFINED, AlarmData.TYPE_COLD));

		// check with metadata
		Map<String,Object> metadata = new HashMap<>();
		metadata.put(FunctionData.DESCRIPTION, "test-description");
		data = new AlarmData(Long.MIN_VALUE, metadata, AlarmData.SEVERITY_UNDEFINED, AlarmData.TYPE_COLD);
		assertEquals("The alarm data comparison is wrong!",
				data,
				data);
		assertEquals("The alarm data comparison is wrong!",
				data,
				new AlarmData(Long.MIN_VALUE, metadata, AlarmData.SEVERITY_UNDEFINED, AlarmData.TYPE_COLD));

		// check with fields map
		Map<String,Object> fields = new HashMap<>();
		fields.put(AlarmData.FIELD_SEVERITY, Integer.valueOf(AlarmData.SEVERITY_UNDEFINED));
		fields.put(AlarmData.FIELD_TYPE, Integer.valueOf(AlarmData.TYPE_COLD));
		fields.put(FunctionData.FIELD_METADATA, metadata);
		fields.put(FunctionData.FIELD_TIMESTAMP, Long.valueOf(Long.MIN_VALUE));
		data = new AlarmData(fields);
		assertEquals("The alarm data comparison is wrong!",
				data,
				data);
		assertEquals("The alarm data comparison is wrong!",
				data,
				new AlarmData(Long.MIN_VALUE, metadata, AlarmData.SEVERITY_UNDEFINED, AlarmData.TYPE_COLD));
	}

	/**
	 * Checks {@link AlarmData#compareTo(Object)} with {@code AlarmData}.
	 */
	public void testComparison() {
		// check without metadata
		AlarmData data = new AlarmData(Long.MIN_VALUE, null, AlarmData.SEVERITY_UNDEFINED, AlarmData.TYPE_COLD);
		assertEquals(
				"The alarm data comparison is wrong!",
				0, data.compareTo(data));
		assertEquals(
				"The alarm data comparison is wrong!",
				0, data.compareTo(new AlarmData(Long.MIN_VALUE, null, AlarmData.SEVERITY_UNDEFINED, AlarmData.TYPE_COLD)));

		// check with metadata
		Map<String,Object> metadata = new HashMap<>();
		metadata.put(FunctionData.DESCRIPTION, "test-description");
		data = new AlarmData(Long.MIN_VALUE, metadata, AlarmData.SEVERITY_UNDEFINED, AlarmData.TYPE_COLD);
		assertEquals(
				"The alarm data comparison is wrong!",
				0, data.compareTo(data));
		assertEquals(
				"The alarm data comparison is wrong!",
				0, data.compareTo(new AlarmData(Long.MIN_VALUE, metadata, AlarmData.SEVERITY_UNDEFINED, AlarmData.TYPE_COLD)));

		// check with fields map
		Map<String,Object> fields = new HashMap<>();
		fields.put(AlarmData.FIELD_SEVERITY, Integer.valueOf(AlarmData.SEVERITY_UNDEFINED));
		fields.put(AlarmData.FIELD_TYPE, Integer.valueOf(AlarmData.TYPE_COLD));
		fields.put(FunctionData.FIELD_METADATA, metadata);
		fields.put(FunctionData.FIELD_TIMESTAMP, Long.valueOf(Long.MIN_VALUE));
		data = new AlarmData(fields);
		assertEquals(
				"The alarm data comparison is wrong!",
				0, data.compareTo(data));
		assertEquals("The alarm data comparison is wrong!",
				0, data.compareTo(new AlarmData(Long.MIN_VALUE, metadata, AlarmData.SEVERITY_UNDEFINED, AlarmData.TYPE_COLD)));
	}

	/**
	 * Checks {@link AlarmData#hashCode()}.
	 */
	public void testHashCode() {
		// check without metadata
		AlarmData data = new AlarmData(Long.MIN_VALUE, null, AlarmData.SEVERITY_UNDEFINED, AlarmData.TYPE_COLD);
		assertEquals("The alarm data hash code is wrong!",
				data.hashCode(),
				data.hashCode());
		assertEquals("The alarm data hash code is wrong!",
				data.hashCode(),
				(new AlarmData(Long.MIN_VALUE, null, AlarmData.SEVERITY_UNDEFINED, AlarmData.TYPE_COLD)).hashCode());

		// check with metadata
		Map<String,Object> metadata = new HashMap<>();
		metadata.put(FunctionData.DESCRIPTION, "test-description");
		data = new AlarmData(Long.MIN_VALUE, metadata, AlarmData.SEVERITY_UNDEFINED, AlarmData.TYPE_COLD);
		assertEquals("The alarm data hash code is wrong!",
				data.hashCode(),
				data.hashCode());
		assertEquals("The alarm data hash code is wrong!",
				data.hashCode(),
				(new AlarmData(Long.MIN_VALUE, metadata, AlarmData.SEVERITY_UNDEFINED, AlarmData.TYPE_COLD)).hashCode());

		// check with fields map
		Map<String,Object> fields = new HashMap<>();
		fields.put(AlarmData.FIELD_SEVERITY, Integer.valueOf(AlarmData.SEVERITY_UNDEFINED));
		fields.put(AlarmData.FIELD_TYPE, Integer.valueOf(AlarmData.TYPE_COLD));
		fields.put(FunctionData.FIELD_TIMESTAMP, Long.valueOf(Long.MIN_VALUE));
		fields.put(FunctionData.FIELD_METADATA, metadata);
		data = new AlarmData(fields);
		assertEquals("The alarm data hash code is wrong!",
				data.hashCode(),
				data.hashCode());
		assertEquals("The alarm data hash code is wrong!",
				data.hashCode(),
				(new AlarmData(Long.MIN_VALUE, metadata, AlarmData.SEVERITY_UNDEFINED, AlarmData.TYPE_COLD)).hashCode());
	}

	/**
	 * Checks {@code AlarmData} field values.
	 */
	public void testFields() {
		// check without metadata
		AlarmData data = new AlarmData(Long.MIN_VALUE, null, AlarmData.SEVERITY_UNDEFINED, AlarmData.TYPE_COLD);
		checkAlarmDataFields(Long.MIN_VALUE, null, AlarmData.SEVERITY_UNDEFINED, AlarmData.TYPE_COLD, data);

		// check with metadata
		Map<String,Object> metadata = new HashMap<>();
		metadata.put(FunctionData.DESCRIPTION, "test-description");
		data = new AlarmData(Long.MIN_VALUE, metadata, AlarmData.SEVERITY_UNDEFINED, AlarmData.TYPE_COLD);
		checkAlarmDataFields(Long.MIN_VALUE, metadata, AlarmData.SEVERITY_UNDEFINED, AlarmData.TYPE_COLD, data);

		// check with fields map
		Map<String,Object> fields = new HashMap<>();
		fields.put(AlarmData.FIELD_SEVERITY, Integer.valueOf(AlarmData.SEVERITY_UNDEFINED));
		fields.put(AlarmData.FIELD_TYPE, Integer.valueOf(AlarmData.TYPE_COLD));
		fields.put(FunctionData.FIELD_TIMESTAMP, Long.valueOf(Long.MIN_VALUE));
		fields.put(FunctionData.FIELD_METADATA, metadata);
		data = new AlarmData(fields);
		checkAlarmDataFields(Long.MIN_VALUE, metadata, AlarmData.SEVERITY_UNDEFINED, AlarmData.TYPE_COLD, data);

		fields.clear();
		data = new AlarmData(fields);
		checkAlarmDataFields(Long.MIN_VALUE, null, AlarmData.SEVERITY_UNDEFINED, AlarmData.TYPE_UNDEFINED, data);
	}

	/**
	 * Checks the {@code AlarmData} construction with an invalid fields.
	 */
	public void testInvalidFields() {
		Map<String,Object> fields = new HashMap<>();
		fields.put(AlarmData.FIELD_SEVERITY, "invalid-severity");
		fields.put(AlarmData.FIELD_TYPE, Integer.valueOf(AlarmData.TYPE_COLD));
		checkInvalidFieldType(fields);

		fields.clear();
		fields.put(AlarmData.FIELD_SEVERITY, Integer.valueOf(AlarmData.SEVERITY_UNDEFINED));
		fields.put(AlarmData.FIELD_TYPE, "cold");
		checkInvalidFieldType(fields);

		fields.clear();
		fields.put(AlarmData.FIELD_SEVERITY, Integer.valueOf(AlarmData.SEVERITY_UNDEFINED));
		fields.put(AlarmData.FIELD_TYPE, Integer.valueOf(AlarmData.TYPE_COLD));
		fields.put(FunctionData.FIELD_METADATA, Boolean.TRUE);
		checkInvalidFieldType(fields);

		fields.clear();
		fields.put(AlarmData.FIELD_SEVERITY, Integer.valueOf(AlarmData.SEVERITY_UNDEFINED));
		fields.put(AlarmData.FIELD_TYPE, Integer.valueOf(AlarmData.TYPE_COLD));
		fields.put(FunctionData.FIELD_TIMESTAMP, Boolean.TRUE);
		checkInvalidFieldType(fields);

		try {
			new AlarmData(null);
			fail("The alarm data is built with null fields");
		} catch (NullPointerException npe) { // NOPMD
			// go ahead, it's expected
		}

		try {
			new AlarmData(
					System.currentTimeMillis(),
					new HashMap<>(),
					Integer.MIN_VALUE,
					AlarmData.TYPE_ACCESS_CONTROL);
			fail("Alarm data is built with invalid severity.");
		} catch (IllegalArgumentException iae) {
			// go ahead, it's expected
		}
	}

	/**
	 * Checks {@link AlarmData#toString()}.
	 */
	public void testToString() {
		AlarmData alarmData = new AlarmData(
				System.currentTimeMillis(),
				new HashMap<>(),
				AlarmData.SEVERITY_UNDEFINED,
				AlarmData.TYPE_ACCESS_CONTROL);
		assertNotNull("There is no string representation of the alarm data.", alarmData.toString());
		alarmData = new AlarmData(
				Long.MIN_VALUE,
				null,
				AlarmData.SEVERITY_MAJOR,
				Integer.MIN_VALUE);
		assertNotNull("There is no string representation of the alarm data.", alarmData.toString());
	}

	private void checkInvalidFieldType(Map<String, ? > fields) {
		try {
			new AlarmData(fields);
			fail("The alarm data is built with invalid fields: " + fields);
		} catch (ClassCastException cce) {
			// go ahead, it's expected
		}
	}

	private void checkAlarmDataFields(long timestamp, Map<String, ? > metadata,
			int severity, int type, AlarmData actualData) {
		super.assertFunctionDataFields(timestamp, metadata, actualData);
		assertEquals(
				"The severity is not correct!",
				severity,
				actualData.getSeverity());
		assertEquals(
				"The alarm is not correct!",
				type,
				actualData.getType());
	}
}

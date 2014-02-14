/*
 * Copyright (c) OSGi Alliance (2014). All Rights Reserved.
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


package org.osgi.test.cases.dal.functions.data;

import java.util.HashMap;
import java.util.Map;
import org.osgi.service.dal.DeviceFunctionData;
import org.osgi.service.dal.functions.data.AlarmData;
import org.osgi.test.cases.dal.AbstractDeviceTest;

/**
 * Validates the <code>AlarmData</code> data structure.
 */
public final class AlarmDataTest extends AbstractDeviceTest {

	/**
	 * Checks {@link AlarmData#equals(Object)} method.
	 */
	public void testEquals() {
		// check without metadata
		AlarmData data = new AlarmData(Long.MIN_VALUE, null, AlarmData.SEVERITY_NONE, AlarmData.TYPE_COLD);
		assertEquals("The alarm data comparison is wrong!",
				data,
				data);
		assertEquals("The alarm data comparison is wrong!",
				data,
				new AlarmData(Long.MIN_VALUE, null, AlarmData.SEVERITY_NONE, AlarmData.TYPE_COLD));

		// check with metadata
		Map metadata = new HashMap();
		metadata.put(DeviceFunctionData.META_INFO_DESCRIPTION, "test-description");
		data = new AlarmData(Long.MIN_VALUE, metadata, AlarmData.SEVERITY_NONE, AlarmData.TYPE_COLD);
		assertEquals("The alarm data comparison is wrong!",
				data,
				data);
		assertEquals("The alarm data comparison is wrong!",
				data,
				new AlarmData(Long.MIN_VALUE, metadata, AlarmData.SEVERITY_NONE, AlarmData.TYPE_COLD));

		// check with fields map
		Map fields = new HashMap();
		fields.put(AlarmData.FIELD_SEVERITY, new Integer(AlarmData.SEVERITY_NONE));
		fields.put(AlarmData.FIELD_TYPE, new Integer(AlarmData.TYPE_COLD));
		fields.put(DeviceFunctionData.FIELD_METADATA, metadata);
		fields.put(DeviceFunctionData.FIELD_TIMESTAMP, new Long(Long.MIN_VALUE));
		data = new AlarmData(fields);
		assertEquals("The alarm data comparison is wrong!",
				data,
				data);
		assertEquals("The alarm data comparison is wrong!",
				data,
				new AlarmData(Long.MIN_VALUE, metadata, AlarmData.SEVERITY_NONE, AlarmData.TYPE_COLD));
	}

	/**
	 * Checks {@link AlarmData#compareTo(Object)} with <code>AlarmData</code>.
	 */
	public void testAlarmDataComparison() {
		// check without metadata
		AlarmData data = new AlarmData(Long.MIN_VALUE, null, AlarmData.SEVERITY_NONE, AlarmData.TYPE_COLD);
		assertEquals(
				"The alarm data comparison is wrong!",
				0, data.compareTo(data));
		assertEquals(
				"The alarm data comparison is wrong!",
				0, data.compareTo(new AlarmData(Long.MIN_VALUE, null, AlarmData.SEVERITY_NONE, AlarmData.TYPE_COLD)));

		// check with metadata
		Map metadata = new HashMap();
		metadata.put(DeviceFunctionData.META_INFO_DESCRIPTION, "test-description");
		data = new AlarmData(Long.MIN_VALUE, metadata, AlarmData.SEVERITY_NONE, AlarmData.TYPE_COLD);
		assertEquals(
				"The alarm data comparison is wrong!",
				0, data.compareTo(data));
		assertEquals(
				"The alarm data comparison is wrong!",
				0, data.compareTo(new AlarmData(Long.MIN_VALUE, metadata, AlarmData.SEVERITY_NONE, AlarmData.TYPE_COLD)));

		// check with fields map
		Map fields = new HashMap();
		fields.put(AlarmData.FIELD_SEVERITY, new Integer(AlarmData.SEVERITY_NONE));
		fields.put(AlarmData.FIELD_TYPE, new Integer(AlarmData.TYPE_COLD));
		fields.put(DeviceFunctionData.FIELD_METADATA, metadata);
		fields.put(DeviceFunctionData.FIELD_TIMESTAMP, new Long(Long.MIN_VALUE));
		data = new AlarmData(fields);
		assertEquals(
				"The alarm data comparison is wrong!",
				0, data.compareTo(data));
		assertEquals("The alarm data comparison is wrong!",
				0, data.compareTo(new AlarmData(Long.MIN_VALUE, metadata, AlarmData.SEVERITY_NONE, AlarmData.TYPE_COLD)));
	}

	/**
	 * Checks {@link AlarmData#compareTo(Object)} with <code>Map</code>.
	 */
	public void testMapComparison() {
		// check without metadata
		AlarmData data = new AlarmData(Long.MIN_VALUE, null, AlarmData.SEVERITY_NONE, AlarmData.TYPE_COLD);
		Map fields = new HashMap();
		fields.put(AlarmData.FIELD_SEVERITY, new Integer(AlarmData.SEVERITY_NONE));
		fields.put(AlarmData.FIELD_TYPE, new Integer(AlarmData.TYPE_COLD));
		fields.put(DeviceFunctionData.FIELD_TIMESTAMP, new Long(Long.MIN_VALUE));
		assertEquals(
				"The boolean comparison is wrong!",
				0, data.compareTo(fields));

		// check with metadata
		Map metadata = new HashMap();
		metadata.put(DeviceFunctionData.META_INFO_DESCRIPTION, "test-description");
		fields.put(DeviceFunctionData.FIELD_METADATA, metadata);
		data = new AlarmData(Long.MIN_VALUE, metadata, AlarmData.SEVERITY_NONE, AlarmData.TYPE_COLD);
		assertEquals(
				"The alarm data comparison is wrong!",
				0, data.compareTo(fields));

		// check with fields map
		data = new AlarmData(fields);
		assertEquals(
				"The alarm data comparison is wrong!",
				0, data.compareTo(fields));
	}

	/**
	 * Checks {@link AlarmData#hashCode()}.
	 */
	public void testHashCode() {
		// check without metadata
		AlarmData data = new AlarmData(Long.MIN_VALUE, null, AlarmData.SEVERITY_NONE, AlarmData.TYPE_COLD);
		assertEquals("The alarm data hash code is wrong!",
				data.hashCode(),
				data.hashCode());
		assertEquals("The alarm data hash code is wrong!",
				data.hashCode(),
				(new AlarmData(Long.MIN_VALUE, null, AlarmData.SEVERITY_NONE, AlarmData.TYPE_COLD)).hashCode());

		// check with metadata
		Map metadata = new HashMap();
		metadata.put(DeviceFunctionData.META_INFO_DESCRIPTION, "test-description");
		data = new AlarmData(Long.MIN_VALUE, metadata, AlarmData.SEVERITY_NONE, AlarmData.TYPE_COLD);
		assertEquals("The alarm data hash code is wrong!",
				data.hashCode(),
				data.hashCode());
		assertEquals("The alarm data hash code is wrong!",
				data.hashCode(),
				(new AlarmData(Long.MIN_VALUE, metadata, AlarmData.SEVERITY_NONE, AlarmData.TYPE_COLD)).hashCode());

		// check with fields map
		Map fields = new HashMap();
		fields.put(AlarmData.FIELD_SEVERITY, new Integer(AlarmData.SEVERITY_NONE));
		fields.put(AlarmData.FIELD_TYPE, new Integer(AlarmData.TYPE_COLD));
		fields.put(DeviceFunctionData.FIELD_TIMESTAMP, new Long(Long.MIN_VALUE));
		fields.put(DeviceFunctionData.FIELD_METADATA, metadata);
		data = new AlarmData(fields);
		assertEquals("The alarm data hash code is wrong!",
				data.hashCode(),
				data.hashCode());
		assertEquals("The alarm data hash code is wrong!",
				data.hashCode(),
				(new AlarmData(Long.MIN_VALUE, metadata, AlarmData.SEVERITY_NONE, AlarmData.TYPE_COLD)).hashCode());
	}

	/**
	 * Checks <code>AlarmData</code> field values.
	 */
	public void testAlarmDataFields() {
		// check without metadata
		AlarmData data = new AlarmData(Long.MIN_VALUE, null, AlarmData.SEVERITY_NONE, AlarmData.TYPE_COLD);
		checkAlarmDataFields(Long.MIN_VALUE, null, AlarmData.SEVERITY_NONE, AlarmData.TYPE_COLD, data);

		// check with metadata
		Map metadata = new HashMap();
		metadata.put(DeviceFunctionData.META_INFO_DESCRIPTION, "test-description");
		data = new AlarmData(Long.MIN_VALUE, metadata, AlarmData.SEVERITY_NONE, AlarmData.TYPE_COLD);
		checkAlarmDataFields(Long.MIN_VALUE, metadata, AlarmData.SEVERITY_NONE, AlarmData.TYPE_COLD, data);

		// check with fields map
		Map fields = new HashMap();
		fields.put(AlarmData.FIELD_SEVERITY, new Integer(AlarmData.SEVERITY_NONE));
		fields.put(AlarmData.FIELD_TYPE, new Integer(AlarmData.TYPE_COLD));
		fields.put(DeviceFunctionData.FIELD_TIMESTAMP, new Long(Long.MIN_VALUE));
		fields.put(DeviceFunctionData.FIELD_METADATA, metadata);
		data = new AlarmData(fields);
		checkAlarmDataFields(Long.MIN_VALUE, metadata, AlarmData.SEVERITY_NONE, AlarmData.TYPE_COLD, data);
	}

	/**
	 * Checks the <code>AlarmData</code> construction with an invalid fields.
	 */
	public void testInvalidFields() {
		final Map fields = new HashMap();
		fields.put(AlarmData.FIELD_SEVERITY, "invalid-severity");
		fields.put(AlarmData.FIELD_TYPE, new Integer(AlarmData.TYPE_COLD));
		checkInvalidFieldType(fields);

		fields.clear();
		fields.put(AlarmData.FIELD_SEVERITY, new Integer(AlarmData.SEVERITY_NONE));
		fields.put(AlarmData.FIELD_TYPE, "cold");
		checkInvalidFieldType(fields);

		fields.clear();
		fields.put(AlarmData.FIELD_SEVERITY, new Integer(AlarmData.SEVERITY_NONE));
		fields.put(AlarmData.FIELD_TYPE, new Integer(AlarmData.TYPE_COLD));
		fields.put(DeviceFunctionData.FIELD_METADATA, Boolean.TRUE);
		checkInvalidFieldType(fields);

		fields.clear();
		fields.put(AlarmData.FIELD_SEVERITY, new Integer(AlarmData.SEVERITY_NONE));
		fields.put(AlarmData.FIELD_TYPE, new Integer(AlarmData.TYPE_COLD));
		fields.put(DeviceFunctionData.FIELD_TIMESTAMP, Boolean.TRUE);
		checkInvalidFieldType(fields);

		fields.clear();
		try {
			new AlarmData(fields);
			fail("The alarm data is built with empty fields.");
		} catch (IllegalArgumentException iae) {
			// go ahead, it's expected
		}

		try {
			new AlarmData(null);
			fail("The alarm data is built with null fields");
		} catch (NullPointerException npe) {
			// go ahead, it's expected
		}
	}

	private void checkInvalidFieldType(Map fields) {
		try {
			new AlarmData(fields);
			fail("The alarm data is built with invalid fields: " + fields);
		} catch (ClassCastException cce) {
			// expected, go ahead
		}
	}

	private void checkAlarmDataFields(long timestamp, Map metadata, int severity, int type, AlarmData actualData) {
		super.assertDeviceFunctionDataFields(timestamp, metadata, actualData);
		// severity
		assertEquals(
				"The severity field is not correct!",
				severity,
				actualData.severity);
		assertEquals(
				"The severity is not correct!",
				severity,
				actualData.getSeverity());

		// type
		assertEquals(
				"The alarm field is not correct!",
				type,
				actualData.type);
		assertEquals(
				"The alarm is not correct!",
				type,
				actualData.getType());
	}

}

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
import org.osgi.service.dal.functions.data.BooleanData;
import org.osgi.test.cases.dal.AbstractDeviceTest;

/**
 * Validates the <code>BooleanData</code> data structure.
 */
public final class BooleanDataTest extends AbstractDeviceTest {

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
		Map metadata = new HashMap();
		metadata.put(DeviceFunctionData.META_INFO_DESCRIPTION, "test-description");
		data = new BooleanData(Long.MIN_VALUE, metadata, false);
		assertEquals("The boolean data comparison is wrong!",
				data,
				data);
		assertEquals("The boolean data comparison is wrong!",
				data,
				new BooleanData(Long.MIN_VALUE, metadata, false));

		// check with fields map
		Map fields = new HashMap();
		fields.put(BooleanData.FIELD_VALUE, Boolean.FALSE);
		fields.put(DeviceFunctionData.FIELD_TIMESTAMP, new Long(Long.MIN_VALUE));
		fields.put(DeviceFunctionData.FIELD_METADATA, metadata);
		data = new BooleanData(fields);
		assertEquals("The boolean data comparison is wrong!",
				data,
				data);
		assertEquals("The boolean data comparison is wrong!",
				data,
				new BooleanData(Long.MIN_VALUE, metadata, false));
	}

	/**
	 * Checks {@link BooleanData#compareTo(Object)} with
	 * <code>BooleanData</code>.
	 */
	public void testBooleanDataComparison() {
		// check without metadata
		BooleanData data = new BooleanData(Long.MIN_VALUE, null, true);
		assertEquals(
				"The boolean data comparison is wrong!",
				0, data.compareTo(data));
		assertEquals(
				"The boolean data comparison is wrong!",
				0, data.compareTo(new BooleanData(Long.MIN_VALUE, null, true)));

		// check with metadata
		Map metadata = new HashMap();
		metadata.put(DeviceFunctionData.META_INFO_DESCRIPTION, "test-description");
		data = new BooleanData(Long.MIN_VALUE, metadata, false);
		assertEquals(
				"The boolean data comparison is wrong!",
				0, data.compareTo(data));
		assertEquals(
				"The boolean data comparison is wrong!",
				0, data.compareTo(new BooleanData(Long.MIN_VALUE, metadata, false)));

		// check with fields map
		Map fields = new HashMap();
		fields.put(BooleanData.FIELD_VALUE, Boolean.FALSE);
		fields.put(DeviceFunctionData.FIELD_TIMESTAMP, new Long(Long.MIN_VALUE));
		fields.put(DeviceFunctionData.FIELD_METADATA, metadata);
		data = new BooleanData(fields);
		assertEquals(
				"The boolean data comparison is wrong!",
				0, data.compareTo(data));
		assertEquals("The boolean data comparison is wrong!",
				0, data.compareTo(new BooleanData(Long.MIN_VALUE, metadata, false)));
	}

	/**
	 * Checks {@link BooleanData#compareTo(Object)} with <code>Boolean</code>.
	 */
	public void testBooleanComparison() {
		// check without metadata
		BooleanData data = new BooleanData(Long.MIN_VALUE, null, true);
		assertEquals(
				"The boolean comparison is wrong!",
				0, data.compareTo(Boolean.TRUE));

		// check with metadata
		Map metadata = new HashMap();
		metadata.put(DeviceFunctionData.META_INFO_DESCRIPTION, "test-description");
		data = new BooleanData(Long.MIN_VALUE, metadata, false);
		assertEquals(
				"The boolean data comparison is wrong!",
				0, data.compareTo(Boolean.FALSE));

		// check with fields map
		Map fields = new HashMap();
		fields.put(BooleanData.FIELD_VALUE, Boolean.FALSE);
		fields.put(DeviceFunctionData.FIELD_TIMESTAMP, new Long(Long.MIN_VALUE));
		fields.put(DeviceFunctionData.FIELD_METADATA, metadata);
		data = new BooleanData(fields);
		assertEquals(
				"The boolean data comparison is wrong!",
				0, data.compareTo(Boolean.FALSE));
	}

	/**
	 * Checks {@link BooleanData#compareTo(Object)} with <code>Map</code>.
	 */
	public void testMapComparison() {
		// check without metadata
		BooleanData data = new BooleanData(Long.MIN_VALUE, null, true);
		Map dataMap = new HashMap();
		dataMap.put(BooleanData.FIELD_VALUE, Boolean.TRUE);
		dataMap.put(DeviceFunctionData.FIELD_TIMESTAMP, new Long(Long.MIN_VALUE));
		assertEquals(
				"The boolean comparison is wrong!",
				0, data.compareTo(dataMap));

		// check with metadata
		Map metadata = new HashMap();
		metadata.put(DeviceFunctionData.META_INFO_DESCRIPTION, "test-description");
		dataMap.put(BooleanData.FIELD_VALUE, Boolean.FALSE);
		dataMap.put(DeviceFunctionData.FIELD_TIMESTAMP, new Long(Long.MIN_VALUE));
		dataMap.put(DeviceFunctionData.FIELD_METADATA, metadata);
		data = new BooleanData(Long.MIN_VALUE, metadata, false);
		assertEquals(
				"The boolean data comparison is wrong!",
				0, data.compareTo(dataMap));

		// check with fields map
		Map fields = new HashMap();
		fields.put(BooleanData.FIELD_VALUE, Boolean.FALSE);
		fields.put(DeviceFunctionData.FIELD_TIMESTAMP, new Long(Long.MIN_VALUE));
		fields.put(DeviceFunctionData.FIELD_METADATA, metadata);
		data = new BooleanData(fields);
		assertEquals(
				"The boolean data comparison is wrong!",
				0, data.compareTo(fields));
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
		Map metadata = new HashMap();
		metadata.put(DeviceFunctionData.META_INFO_DESCRIPTION, "test-description");
		data = new BooleanData(Long.MIN_VALUE, metadata, false);
		assertEquals("The boolean data hash code is wrong!",
				data.hashCode(),
				data.hashCode());
		assertEquals("The boolean data hash code is wrong!",
				data.hashCode(),
				(new BooleanData(Long.MIN_VALUE, metadata, false)).hashCode());

		// check with fields map
		Map fields = new HashMap();
		fields.put(BooleanData.FIELD_VALUE, Boolean.FALSE);
		fields.put(DeviceFunctionData.FIELD_TIMESTAMP, new Long(Long.MIN_VALUE));
		fields.put(DeviceFunctionData.FIELD_METADATA, metadata);
		data = new BooleanData(fields);
		assertEquals("The boolean data hash code is wrong!",
				data.hashCode(),
				data.hashCode());
		assertEquals("The boolean data hash code is wrong!",
				data.hashCode(),
				(new BooleanData(Long.MIN_VALUE, metadata, false)).hashCode());
	}

	/**
	 * Checks <code>BooleanData</code> field values.
	 */
	public void testBooleanDataFields() {
		// check without metadata
		BooleanData data = new BooleanData(Long.MIN_VALUE, null, true);
		checkBooleanDataFields(Long.MIN_VALUE, null, true, data);

		// check with metadata
		Map metadata = new HashMap();
		metadata.put(DeviceFunctionData.META_INFO_DESCRIPTION, "test-description");
		data = new BooleanData(Long.MIN_VALUE, metadata, false);
		checkBooleanDataFields(Long.MIN_VALUE, metadata, false, data);

		// check with fields map
		Map fields = new HashMap();
		fields.put(BooleanData.FIELD_VALUE, Boolean.FALSE);
		fields.put(DeviceFunctionData.FIELD_TIMESTAMP, new Long(Long.MIN_VALUE));
		fields.put(DeviceFunctionData.FIELD_METADATA, metadata);
		data = new BooleanData(fields);
		checkBooleanDataFields(Long.MIN_VALUE, metadata, false, data);
	}

	/**
	 * Checks the <code>BooleanData</code> construction with an invalid fields.
	 */
	public void testInvalidFields() {
		final Map fields = new HashMap();
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
		} catch (NullPointerException npe) {
			// go ahead, it's expected
		}
	}

	private void checkInvalidFieldType(Map fields) {
		try {
			new BooleanData(fields);
			fail("The boolean data is built with invalid fields: " + fields);
		} catch (ClassCastException cce) {
			// expected, go ahead
		}
	}

	private void checkBooleanDataFields(long timestamp, Map metadata, boolean value, BooleanData actualData) {
		super.assertDeviceFunctionDataFields(timestamp, metadata, actualData);
		// value
		assertEquals(
				"The value field is not correct!",
				value,
				actualData.value);
		assertEquals(
				"The value is not correct!",
				value,
				actualData.getValue());
	}

}

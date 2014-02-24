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
import org.osgi.service.dal.FunctionData;
import org.osgi.service.dal.functions.data.KeypadData;
import org.osgi.test.cases.dal.functions.AbstractFunctionTest;

/**
 * Validates the <code>KeypadData</code> data structure.
 */
public final class KeypadDataTest extends AbstractFunctionTest {

	private static final int	KEY_CODE	= 8;
	private static final String	KEY_NAME	= "Backspace";

	/**
	 * Checks {@link KeypadData#equals(Object)} method.
	 */
	public void testEquals() {
		// check without metadata
		KeypadData data = new KeypadData(Long.MIN_VALUE, null, KeypadData.EVENT_TYPE_PRESSED_DOUBLE, KEY_CODE, KEY_NAME);
		assertEquals("The keypad data comparison is wrong!",
				data,
				data);
		assertEquals("The keypad data comparison is wrong!",
				data,
				new KeypadData(Long.MIN_VALUE, null, KeypadData.EVENT_TYPE_PRESSED_DOUBLE, KEY_CODE, KEY_NAME));

		// check with metadata
		Map metadata = new HashMap();
		metadata.put(FunctionData.META_INFO_DESCRIPTION, "test-description");
		data = new KeypadData(Long.MIN_VALUE, metadata, KeypadData.EVENT_TYPE_PRESSED_DOUBLE, KEY_CODE, KEY_NAME);
		assertEquals("The keypad data comparison is wrong!",
				data,
				data);
		assertEquals("The keypad data comparison is wrong!",
				data,
				new KeypadData(Long.MIN_VALUE, metadata, KeypadData.EVENT_TYPE_PRESSED_DOUBLE, KEY_CODE, KEY_NAME));

		// check with fields map
		Map fields = new HashMap();
		fields.put(KeypadData.FIELD_EVENT_TYPE, new Integer(KeypadData.EVENT_TYPE_PRESSED_DOUBLE));
		fields.put(KeypadData.FIELD_KEY_CODE, new Integer(KEY_CODE));
		fields.put(KeypadData.FIELD_KEY_NAME, KEY_NAME);
		fields.put(FunctionData.FIELD_METADATA, metadata);
		fields.put(FunctionData.FIELD_TIMESTAMP, new Long(Long.MIN_VALUE));
		data = new KeypadData(fields);
		assertEquals("The keypad data comparison is wrong!",
				data,
				data);
		assertEquals("The keypad data comparison is wrong!",
				data,
				new KeypadData(Long.MIN_VALUE, metadata, KeypadData.EVENT_TYPE_PRESSED_DOUBLE, KEY_CODE, KEY_NAME));
	}

	/**
	 * Checks {@link KeypadData#compareTo(Object)} with <code>KeypadData</code>.
	 */
	public void testKeypadDataComparison() {
		// check without metadata
		KeypadData data = new KeypadData(Long.MIN_VALUE, null, KeypadData.EVENT_TYPE_PRESSED_DOUBLE, KEY_CODE, KEY_NAME);
		assertEquals(
				"The keypad data comparison is wrong!",
				0, data.compareTo(data));
		assertEquals(
				"The keypad data comparison is wrong!",
				0, data.compareTo(new KeypadData(Long.MIN_VALUE, null, KeypadData.EVENT_TYPE_PRESSED_DOUBLE, KEY_CODE, KEY_NAME)));

		// check with metadata
		Map metadata = new HashMap();
		metadata.put(FunctionData.META_INFO_DESCRIPTION, "test-description");
		data = new KeypadData(Long.MIN_VALUE, metadata, KeypadData.EVENT_TYPE_PRESSED_DOUBLE, KEY_CODE, KEY_NAME);
		assertEquals(
				"The keypad data comparison is wrong!",
				0, data.compareTo(data));
		assertEquals(
				"The keypad data comparison is wrong!",
				0, data.compareTo(new KeypadData(Long.MIN_VALUE, metadata, KeypadData.EVENT_TYPE_PRESSED_DOUBLE, KEY_CODE, KEY_NAME)));

		// check with fields map
		Map fields = new HashMap();
		fields.put(KeypadData.FIELD_EVENT_TYPE, new Integer(KeypadData.EVENT_TYPE_PRESSED_DOUBLE));
		fields.put(KeypadData.FIELD_KEY_CODE, new Integer(KEY_CODE));
		fields.put(KeypadData.FIELD_KEY_NAME, KEY_NAME);
		fields.put(FunctionData.FIELD_METADATA, metadata);
		fields.put(FunctionData.FIELD_TIMESTAMP, new Long(Long.MIN_VALUE));
		data = new KeypadData(fields);
		assertEquals(
				"The keypad data comparison is wrong!",
				0, data.compareTo(data));
		assertEquals("The keypad data comparison is wrong!",
				0, data.compareTo(new KeypadData(Long.MIN_VALUE, metadata, KeypadData.EVENT_TYPE_PRESSED_DOUBLE, KEY_CODE, KEY_NAME)));
	}

	/**
	 * Checks {@link KeypadData#compareTo(Object)} with <code>Map</code>.
	 */
	public void testMapComparison() {
		// check without metadata
		KeypadData data = new KeypadData(Long.MIN_VALUE, null, KeypadData.EVENT_TYPE_PRESSED_DOUBLE, KEY_CODE, KEY_NAME);
		Map fields = new HashMap();
		fields.put(KeypadData.FIELD_EVENT_TYPE, new Integer(KeypadData.EVENT_TYPE_PRESSED_DOUBLE));
		fields.put(KeypadData.FIELD_KEY_CODE, new Integer(KEY_CODE));
		fields.put(KeypadData.FIELD_KEY_NAME, KEY_NAME);
		fields.put(FunctionData.FIELD_TIMESTAMP, new Long(Long.MIN_VALUE));
		assertEquals(
				"The boolean comparison is wrong!",
				0, data.compareTo(fields));

		// check with metadata
		Map metadata = new HashMap();
		metadata.put(FunctionData.META_INFO_DESCRIPTION, "test-description");
		fields.put(FunctionData.FIELD_METADATA, metadata);
		data = new KeypadData(Long.MIN_VALUE, metadata, KeypadData.EVENT_TYPE_PRESSED_DOUBLE, KEY_CODE, KEY_NAME);
		assertEquals(
				"The keypad data comparison is wrong!",
				0, data.compareTo(fields));

		// check with fields map
		data = new KeypadData(fields);
		assertEquals(
				"The keypad data comparison is wrong!",
				0, data.compareTo(fields));
	}

	/**
	 * Checks {@link KeypadData#hashCode()}.
	 */
	public void testHashCode() {
		// check without metadata
		KeypadData data = new KeypadData(Long.MIN_VALUE, null, KeypadData.EVENT_TYPE_PRESSED_DOUBLE, KEY_CODE, KEY_NAME);
		assertEquals("The keypad data hash code is wrong!",
				data.hashCode(),
				data.hashCode());
		assertEquals("The keypad data hash code is wrong!",
				data.hashCode(),
				(new KeypadData(Long.MIN_VALUE, null, KeypadData.EVENT_TYPE_PRESSED_DOUBLE, KEY_CODE, KEY_NAME)).hashCode());

		// check with metadata
		Map metadata = new HashMap();
		metadata.put(FunctionData.META_INFO_DESCRIPTION, "test-description");
		data = new KeypadData(Long.MIN_VALUE, metadata, KeypadData.EVENT_TYPE_PRESSED_DOUBLE, KEY_CODE, KEY_NAME);
		assertEquals("The keypad data hash code is wrong!",
				data.hashCode(),
				data.hashCode());
		assertEquals("The keypad data hash code is wrong!",
				data.hashCode(),
				(new KeypadData(Long.MIN_VALUE, metadata, KeypadData.EVENT_TYPE_PRESSED_DOUBLE, KEY_CODE, KEY_NAME)).hashCode());

		// check with fields map
		Map fields = new HashMap();
		fields.put(KeypadData.FIELD_EVENT_TYPE, new Integer(KeypadData.EVENT_TYPE_PRESSED_DOUBLE));
		fields.put(KeypadData.FIELD_KEY_CODE, new Integer(KEY_CODE));
		fields.put(KeypadData.FIELD_KEY_NAME, KEY_NAME);
		fields.put(FunctionData.FIELD_METADATA, metadata);
		fields.put(FunctionData.FIELD_TIMESTAMP, new Long(Long.MIN_VALUE));
		data = new KeypadData(fields);
		assertEquals("The keypad data hash code is wrong!",
				data.hashCode(),
				data.hashCode());
		assertEquals("The keypad data hash code is wrong!",
				data.hashCode(),
				(new KeypadData(Long.MIN_VALUE, metadata, KeypadData.EVENT_TYPE_PRESSED_DOUBLE, KEY_CODE, KEY_NAME)).hashCode());
	}

	/**
	 * Checks <code>KeypadData</code> field values.
	 */
	public void testKeypadDataFields() {
		// check without metadata
		KeypadData data = new KeypadData(Long.MIN_VALUE, null, KeypadData.EVENT_TYPE_PRESSED_DOUBLE, KEY_CODE, KEY_NAME);
		checkKeypadDataFields(Long.MIN_VALUE, null, KeypadData.EVENT_TYPE_PRESSED_DOUBLE, KEY_CODE, KEY_NAME, data);

		// check with metadata
		Map metadata = new HashMap();
		metadata.put(FunctionData.META_INFO_DESCRIPTION, "test-description");
		data = new KeypadData(Long.MIN_VALUE, metadata, KeypadData.EVENT_TYPE_PRESSED_DOUBLE, KEY_CODE, KEY_NAME);
		checkKeypadDataFields(Long.MIN_VALUE, metadata, KeypadData.EVENT_TYPE_PRESSED_DOUBLE, KEY_CODE, KEY_NAME, data);

		// check with fields map
		Map fields = new HashMap();
		fields.put(KeypadData.FIELD_EVENT_TYPE, new Integer(KeypadData.EVENT_TYPE_PRESSED_DOUBLE));
		fields.put(KeypadData.FIELD_KEY_CODE, new Integer(KEY_CODE));
		fields.put(KeypadData.FIELD_KEY_NAME, KEY_NAME);
		fields.put(FunctionData.FIELD_METADATA, metadata);
		fields.put(FunctionData.FIELD_TIMESTAMP, new Long(Long.MIN_VALUE));
		data = new KeypadData(fields);
		checkKeypadDataFields(Long.MIN_VALUE, metadata, KeypadData.EVENT_TYPE_PRESSED_DOUBLE, KEY_CODE, KEY_NAME, data);
	}

	/**
	 * Checks the <code>KeypadData</code> construction with an invalid fields.
	 */
	public void testInvalidFields() {
		final Map fields = new HashMap();
		fields.put(KeypadData.FIELD_EVENT_TYPE, "invalid-event-type");
		fields.put(KeypadData.FIELD_KEY_CODE, new Integer(KEY_CODE));
		checkInvalidFieldType(fields);

		fields.clear();
		fields.put(KeypadData.FIELD_EVENT_TYPE, new Integer(KeypadData.EVENT_TYPE_PRESSED_DOUBLE_LONG));
		fields.put(KeypadData.FIELD_KEY_CODE, "invalid-key-code");
		checkInvalidFieldType(fields);

		fields.clear();
		fields.put(KeypadData.FIELD_EVENT_TYPE, new Integer(KeypadData.EVENT_TYPE_PRESSED_DOUBLE_LONG));
		fields.put(KeypadData.FIELD_KEY_CODE, new Integer(KEY_CODE));
		fields.put(KeypadData.FIELD_KEY_NAME, new Integer(Integer.MAX_VALUE));
		checkInvalidFieldType(fields);

		fields.clear();
		fields.put(KeypadData.FIELD_EVENT_TYPE, new Integer(KeypadData.EVENT_TYPE_PRESSED_DOUBLE_LONG));
		fields.put(KeypadData.FIELD_KEY_CODE, new Integer(KEY_CODE));
		fields.put(FunctionData.FIELD_METADATA, Boolean.TRUE);
		checkInvalidFieldType(fields);

		fields.clear();
		fields.put(KeypadData.FIELD_EVENT_TYPE, new Integer(KeypadData.EVENT_TYPE_PRESSED_DOUBLE_LONG));
		fields.put(KeypadData.FIELD_KEY_CODE, new Integer(KEY_CODE));
		fields.put(FunctionData.FIELD_TIMESTAMP, Boolean.TRUE);
		checkInvalidFieldType(fields);

		fields.clear();
		try {
			new KeypadData(fields);
			fail("The keypad data is built with empty fields.");
		} catch (IllegalArgumentException iae) {
			// go ahead, it's expected
		}

		try {
			new KeypadData(null);
			fail("The keypad data is built with null fields");
		} catch (NullPointerException npe) {
			// go ahead, it's expected
		}
	}

	private void checkInvalidFieldType(Map fields) {
		try {
			new KeypadData(fields);
			fail("The keypad data is built with invalid fields: " + fields);
		} catch (ClassCastException cce) {
			// expected, go ahead
		}
	}

	private void checkKeypadDataFields(long timestamp, Map metadata, int eventType, int keyCode, String keyName, KeypadData actualData) {
		super.assertFunctionDataFields(timestamp, metadata, actualData);
		// event type
		assertEquals(
				"The event type field is not correct!",
				eventType,
				actualData.eventType);
		assertEquals(
				"The event type is not correct!",
				eventType,
				actualData.getEventType());

		// key code
		assertEquals(
				"The key code field is not correct!",
				keyCode,
				actualData.keyCode);
		assertEquals(
				"The key code is not correct!",
				keyCode,
				actualData.getKeyCode());

		// key name
		assertEquals(
				"The key name field is not correct!",
				keyName,
				actualData.keyName);
		assertEquals(
				"The key name is not correct!",
				keyName,
				actualData.getKeyName());
	}

}

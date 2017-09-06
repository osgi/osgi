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
import org.osgi.service.dal.functions.data.KeypadData;
import org.osgi.test.cases.dal.functions.AbstractFunctionTest;

/**
 * Validates the {@code KeypadData} data structure.
 */
public final class KeypadDataTest extends AbstractFunctionTest {

	private static final int	KEY_CODE	= 8;
	private static final String	KEY_NAME	= "Backspace";

	/**
	 * Checks {@link KeypadData#equals(Object)} method.
	 */
	public void testEquals() {
		// check without metadata
		KeypadData data = new KeypadData(
				Long.MIN_VALUE,
				null,
				KeypadData.TYPE_PRESSED,
				KeypadData.SUB_TYPE_PRESSED_NORMAL,
				KEY_CODE,
				KEY_NAME);
		assertEquals("The keypad data comparison is wrong!",
				data,
				data);
		assertEquals("The keypad data comparison is wrong!",
				data,
				new KeypadData(
						Long.MIN_VALUE,
						null,
						KeypadData.TYPE_PRESSED,
						KeypadData.SUB_TYPE_PRESSED_NORMAL,
						KEY_CODE,
						KEY_NAME));

		// check with metadata
		Map metadata = new HashMap();
		metadata.put(FunctionData.DESCRIPTION, "test-description");
		data = new KeypadData(
				Long.MIN_VALUE,
				metadata,
				KeypadData.TYPE_PRESSED,
				KeypadData.SUB_TYPE_PRESSED_NORMAL,
				KEY_CODE,
				KEY_NAME);
		assertEquals("The keypad data comparison is wrong!",
				data,
				data);
		assertEquals("The keypad data comparison is wrong!",
				data,
				new KeypadData(
						Long.MIN_VALUE,
						metadata,
						KeypadData.TYPE_PRESSED,
						KeypadData.SUB_TYPE_PRESSED_NORMAL,
						KEY_CODE,
						KEY_NAME));

		// check with fields map
		Map fields = new HashMap();
		fields.put(KeypadData.FIELD_TYPE, Integer.valueOf(KeypadData.TYPE_PRESSED));
		fields.put(KeypadData.FIELD_SUB_TYPE, Integer.valueOf(KeypadData.SUB_TYPE_PRESSED_NORMAL));
		fields.put(KeypadData.FIELD_KEY_CODE, Integer.valueOf(KEY_CODE));
		fields.put(KeypadData.FIELD_KEY_NAME, KEY_NAME);
		fields.put(FunctionData.FIELD_METADATA, metadata);
		fields.put(FunctionData.FIELD_TIMESTAMP, Long.valueOf(Long.MIN_VALUE));
		data = new KeypadData(fields);
		assertEquals("The keypad data comparison is wrong!",
				data,
				data);
		assertEquals("The keypad data comparison is wrong!",
				data,
				new KeypadData(
						Long.MIN_VALUE,
						metadata,
						KeypadData.TYPE_PRESSED,
						KeypadData.SUB_TYPE_PRESSED_NORMAL,
						KEY_CODE,
						KEY_NAME));
	}

	/**
	 * Checks {@link KeypadData#compareTo(Object)} with {@code KeypadData}.
	 */
	public void testComparison() {
		// check without metadata
		KeypadData data = new KeypadData(
				Long.MIN_VALUE,
				null,
				KeypadData.TYPE_PRESSED,
				KeypadData.SUB_TYPE_PRESSED_NORMAL,
				KEY_CODE,
				KEY_NAME);
		assertEquals(
				"The keypad data comparison is wrong!",
				0, data.compareTo(data));
		assertEquals(
				"The keypad data comparison is wrong!",
				0, data.compareTo(
						new KeypadData(
								Long.MIN_VALUE,
								null,
								KeypadData.TYPE_PRESSED,
								KeypadData.SUB_TYPE_PRESSED_NORMAL,
								KEY_CODE,
								KEY_NAME)));

		// check with metadata
		Map metadata = new HashMap();
		metadata.put(FunctionData.DESCRIPTION, "test-description");
		data = new KeypadData(
				Long.MIN_VALUE,
				metadata,
				KeypadData.TYPE_PRESSED,
				KeypadData.SUB_TYPE_PRESSED_NORMAL,
				KEY_CODE,
				KEY_NAME);
		assertEquals(
				"The keypad data comparison is wrong!",
				0, data.compareTo(data));
		assertEquals(
				"The keypad data comparison is wrong!",
				0, data.compareTo(
						new KeypadData(
								Long.MIN_VALUE,
								metadata,
								KeypadData.TYPE_PRESSED,
								KeypadData.SUB_TYPE_PRESSED_NORMAL,
								KEY_CODE,
								KEY_NAME)));

		// check with fields map
		Map fields = new HashMap();
		fields.put(KeypadData.FIELD_TYPE, Integer.valueOf(KeypadData.TYPE_PRESSED));
		fields.put(KeypadData.FIELD_SUB_TYPE, Integer.valueOf(KeypadData.SUB_TYPE_PRESSED_NORMAL));
		fields.put(KeypadData.FIELD_KEY_CODE, Integer.valueOf(KEY_CODE));
		fields.put(KeypadData.FIELD_KEY_NAME, KEY_NAME);
		fields.put(FunctionData.FIELD_METADATA, metadata);
		fields.put(FunctionData.FIELD_TIMESTAMP, Long.valueOf(Long.MIN_VALUE));
		data = new KeypadData(fields);
		assertEquals(
				"The keypad data comparison is wrong!",
				0, data.compareTo(data));
		assertEquals("The keypad data comparison is wrong!",
				0, data.compareTo(
						new KeypadData(
								Long.MIN_VALUE,
								metadata,
								KeypadData.TYPE_PRESSED,
								KeypadData.SUB_TYPE_PRESSED_NORMAL,
								KEY_CODE,
								KEY_NAME)));
	}

	/**
	 * Checks {@link KeypadData#hashCode()}.
	 */
	public void testHashCode() {
		// check without metadata
		KeypadData data = new KeypadData(
				Long.MIN_VALUE,
				null,
				KeypadData.TYPE_PRESSED,
				KeypadData.SUB_TYPE_PRESSED_NORMAL,
				KEY_CODE,
				KEY_NAME);
		assertEquals("The keypad data hash code is wrong!",
				data.hashCode(),
				data.hashCode());
		assertEquals("The keypad data hash code is wrong!",
				data.hashCode(),
				(new KeypadData(
						Long.MIN_VALUE,
						null,
						KeypadData.TYPE_PRESSED,
						KeypadData.SUB_TYPE_PRESSED_NORMAL,
						KEY_CODE,
						KEY_NAME)).hashCode());

		// check with metadata
		Map metadata = new HashMap();
		metadata.put(FunctionData.DESCRIPTION, "test-description");
		data = new KeypadData(
				Long.MIN_VALUE,
				metadata,
				KeypadData.TYPE_PRESSED,
				KeypadData.SUB_TYPE_PRESSED_NORMAL,
				KEY_CODE,
				KEY_NAME);
		assertEquals("The keypad data hash code is wrong!",
				data.hashCode(),
				data.hashCode());
		assertEquals("The keypad data hash code is wrong!",
				data.hashCode(),
				(new KeypadData(
						Long.MIN_VALUE,
						metadata,
						KeypadData.TYPE_PRESSED,
						KeypadData.SUB_TYPE_PRESSED_NORMAL,
						KEY_CODE,
						KEY_NAME)).hashCode());

		// check with fields map
		Map fields = new HashMap();
		fields.put(KeypadData.FIELD_TYPE, Integer.valueOf(KeypadData.TYPE_PRESSED));
		fields.put(KeypadData.FIELD_TYPE, Integer.valueOf(KeypadData.SUB_TYPE_PRESSED_NORMAL));
		fields.put(KeypadData.FIELD_KEY_CODE, Integer.valueOf(KEY_CODE));
		fields.put(KeypadData.FIELD_KEY_NAME, KEY_NAME);
		fields.put(FunctionData.FIELD_METADATA, metadata);
		fields.put(FunctionData.FIELD_TIMESTAMP, Long.valueOf(Long.MIN_VALUE));
		data = new KeypadData(fields);
		assertEquals("The keypad data hash code is wrong!",
				data.hashCode(),
				data.hashCode());
		assertEquals("The keypad data hash code is wrong!",
				data.hashCode(),
				(new KeypadData(
						Long.MIN_VALUE,
						metadata,
						KeypadData.TYPE_PRESSED,
						KeypadData.SUB_TYPE_PRESSED_NORMAL,
						KEY_CODE,
						KEY_NAME)).hashCode());
	}

	/**
	 * Checks {@code KeypadData} field values.
	 */
	public void testFields() {
		// check without metadata
		KeypadData data = new KeypadData(
				Long.MIN_VALUE,
				null,
				KeypadData.TYPE_PRESSED,
				KeypadData.SUB_TYPE_PRESSED_NORMAL,
				KEY_CODE,
				KEY_NAME);
		checkKeypadDataFields(
				Long.MIN_VALUE,
				null,
				KeypadData.TYPE_PRESSED,
				KeypadData.SUB_TYPE_PRESSED_NORMAL,
				KEY_CODE,
				KEY_NAME,
				data);

		// check with metadata
		Map metadata = new HashMap();
		metadata.put(FunctionData.DESCRIPTION, "test-description");
		data = new KeypadData(
				Long.MIN_VALUE,
				metadata,
				KeypadData.TYPE_PRESSED,
				KeypadData.SUB_TYPE_PRESSED_NORMAL,
				KEY_CODE,
				KEY_NAME);
		checkKeypadDataFields(
				Long.MIN_VALUE,
				metadata,
				KeypadData.TYPE_PRESSED,
				KeypadData.SUB_TYPE_PRESSED_NORMAL,
				KEY_CODE,
				KEY_NAME,
				data);

		// check with fields map
		Map fields = new HashMap();
		fields.put(KeypadData.FIELD_TYPE, Integer.valueOf(KeypadData.TYPE_PRESSED));
		fields.put(KeypadData.FIELD_SUB_TYPE, Integer.valueOf(KeypadData.SUB_TYPE_PRESSED_NORMAL));
		fields.put(KeypadData.FIELD_KEY_CODE, Integer.valueOf(KEY_CODE));
		fields.put(KeypadData.FIELD_KEY_NAME, KEY_NAME);
		fields.put(FunctionData.FIELD_METADATA, metadata);
		fields.put(FunctionData.FIELD_TIMESTAMP, Long.valueOf(Long.MIN_VALUE));
		data = new KeypadData(fields);
		checkKeypadDataFields(
				Long.MIN_VALUE,
				metadata,
				KeypadData.TYPE_PRESSED,
				KeypadData.SUB_TYPE_PRESSED_NORMAL,
				KEY_CODE,
				KEY_NAME,
				data);
	}

	/**
	 * Checks the {@code KeypadData} construction with invalid fields.
	 */
	public void testInvalidFields() {
		Map fields = new HashMap();
		fields.put(KeypadData.FIELD_TYPE, "invalid-event-type");
		fields.put(KeypadData.FIELD_KEY_CODE, Integer.valueOf(KEY_CODE));
		checkInvalidFieldType(fields);

		fields.clear();
		fields.put(KeypadData.FIELD_TYPE, Integer.valueOf(KeypadData.TYPE_PRESSED));
		fields.put(KeypadData.FIELD_KEY_CODE, Integer.valueOf(KEY_CODE));
		fields.put(KeypadData.FIELD_SUB_TYPE, "invalid-event-sub-type");
		checkInvalidFieldType(fields);

		fields.clear();
		fields.put(KeypadData.FIELD_TYPE, Integer.valueOf(KeypadData.TYPE_PRESSED));
		fields.put(KeypadData.FIELD_KEY_CODE, "invalid-key-code");
		checkInvalidFieldType(fields);

		fields.clear();
		fields.put(KeypadData.FIELD_TYPE, Integer.valueOf(KeypadData.TYPE_PRESSED));
		fields.put(KeypadData.FIELD_KEY_CODE, Integer.valueOf(KEY_CODE));
		fields.put(KeypadData.FIELD_KEY_NAME, Integer.valueOf(Integer.MAX_VALUE));
		checkInvalidFieldType(fields);

		fields.clear();
		fields.put(KeypadData.FIELD_TYPE, Integer.valueOf(KeypadData.TYPE_PRESSED));
		fields.put(KeypadData.FIELD_KEY_CODE, Integer.valueOf(KEY_CODE));
		fields.put(FunctionData.FIELD_METADATA, Boolean.TRUE);
		checkInvalidFieldType(fields);

		fields.clear();
		fields.put(KeypadData.FIELD_TYPE, Integer.valueOf(KeypadData.TYPE_PRESSED));
		fields.put(KeypadData.FIELD_KEY_CODE, Integer.valueOf(KEY_CODE));
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
		} catch (NullPointerException npe) { // NOPMD
			// go ahead, it's expected
		}
	}

	/**
	 * Checks the {@code KeypadData} construction with invalid sub-types.
	 */
	public void testInvalidSubtypes() {
		checkInvalidSubtype(KeypadData.TYPE_RELEASED, KeypadData.SUB_TYPE_PRESSED_NORMAL);
		checkInvalidSubtype(KeypadData.TYPE_RELEASED, KeypadData.SUB_TYPE_PRESSED_LONG);
		checkInvalidSubtype(KeypadData.TYPE_RELEASED, KeypadData.SUB_TYPE_PRESSED_DOUBLE);
		checkInvalidSubtype(KeypadData.TYPE_RELEASED, KeypadData.SUB_TYPE_PRESSED_DOUBLE_LONG);
	}

	/**
	 * Checks {@link KeypadData#toString()}.
	 */
	public void testToString() {
		KeypadData keypadData = new KeypadData(
				System.currentTimeMillis(),
				new HashMap(),
				KeypadData.TYPE_PRESSED,
				KeypadData.SUB_TYPE_PRESSED_LONG,
				Integer.MIN_VALUE,
				"test-key-name");
		assertNotNull("There is no string representation of the keypad data.", keypadData.toString());
		keypadData = new KeypadData(
				Long.MIN_VALUE,
				null,
				KeypadData.TYPE_PRESSED,
				Integer.MIN_VALUE,
				Integer.MIN_VALUE,
				null);
		assertNotNull("There is no string representation of the keypad data.", keypadData.toString());
	}

	private void checkInvalidSubtype(int type, int subType) {
		try {
			new KeypadData(
					Long.MIN_VALUE,
					null,
					type,
					subType,
					KEY_CODE,
					KEY_NAME);
			fail("KeypadData has been built with invalid combination of type: " + type +
					" and sub-type: " + subType);
		} catch (IllegalArgumentException iae) {
			// expected, go ahead
		}
	}

	private void checkInvalidFieldType(Map fields) {
		try {
			new KeypadData(fields);
			fail("The keypad data is built with invalid fields: " + fields);
		} catch (ClassCastException cce) {
			// go ahead, it's expected
		}
	}

	private void checkKeypadDataFields(
			long timestamp,
			Map metadata,
			int type,
			int subType,
			int keyCode,
			String keyName,
			KeypadData actualData) {
		super.assertFunctionDataFields(timestamp, metadata, actualData);
		// event type
		assertEquals(
				"The event type is not correct!",
				type,
				actualData.getType());

		// event sub-type
		assertEquals(
				"The event sub-type is not correct!",
				subType,
				actualData.getSubType());

		// key code
		assertEquals(
				"The key code is not correct!",
				keyCode,
				actualData.getKeyCode());

		// key name
		assertEquals(
				"The key name is not correct!",
				keyName,
				actualData.getKeyName());
	}
}

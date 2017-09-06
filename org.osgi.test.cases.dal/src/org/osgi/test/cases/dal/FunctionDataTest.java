/*
 * Copyright (c) 2014 ProSyst Software GmbH. All Rights Reserved.
 *
 * This CODE is owned by ProSyst Software GmbH,
 * and is being distributed to OSGi PARTICIPANTS as MATERIALS
 * under the terms of section 1 of the OSGi Alliance Inc. Intellectual Property Rights Policy,
 * Amended and Restated as of May 23, 2011.
 */

package org.osgi.test.cases.dal;

import java.util.HashMap;
import java.util.Map;
import org.osgi.service.dal.FunctionData;

/**
 * Test class validates the function data.
 */
public class FunctionDataTest extends AbstractDeviceTest {

	/**
	 * Validates {@link FunctionData#equals(Object)} method.
	 */
	public void testEqualsAndHashCode() {
		// test without metadata
		checkEqualsAndHashCode(null);

		// test with empty metadata
		checkEqualsAndHashCode(new HashMap());

		// test with comparable values
		Map metadata = new HashMap();
		metadata.put("test-string", "test");
		metadata.put("test-boolean", Boolean.TRUE);
		metadata.put("test-int", Integer.valueOf(Integer.MAX_VALUE));
		checkEqualsAndHashCode(metadata);

		// test with array
		int[] testArray = new int[] {0, 1, 2, 3, 4, 5};
		metadata.put("test-array", testArray);
		checkEqualsAndHashCode(metadata);

		// test with Map
		Map testMap = new HashMap();
		testMap.put("test-map-array", testArray);
		checkEqualsAndHashCode(metadata);

		metadata.clear();
		metadata.put("test-value", Boolean.TRUE);
		Map otherMetadata = new HashMap();
		otherMetadata.put("test-value", "true");
		assertFalse("The function data instances are equal for different metadata.",
				(new TestFunctionData(Long.MIN_VALUE, metadata)).equals(
						new TestFunctionData(Long.MIN_VALUE, otherMetadata)));

		metadata.clear();
		otherMetadata.clear();
		metadata.put("test-value", new int[] {1, 2, 3});
		otherMetadata.put("test-value", new int[] {1, 2, 4});
		assertFalse("The function data instances are equal for different metadata.",
				(new TestFunctionData(Long.MIN_VALUE, metadata)).equals(
						new TestFunctionData(Long.MIN_VALUE, otherMetadata)));

		metadata.clear();
		otherMetadata.clear();
		metadata.put("test-value", metadata);
		otherMetadata.put("test-value", otherMetadata);
		assertFalse("The function data instances are equal for self-contained metadata.",
				(new TestFunctionData(Long.MIN_VALUE, metadata)).equals(
						new TestFunctionData(Long.MIN_VALUE, otherMetadata)));
	}

	/**
	 * Validates {@link FunctionData#compareTo(Object)} method.
	 */
	public void testComparison() {
		Map metadata = new HashMap();
		metadata.put("test-value", Integer.valueOf(1));

		Map otherMetadata = new HashMap();
		otherMetadata.put("test-value", Integer.valueOf(2));
		checkComparison(metadata, otherMetadata);

		metadata.clear();
		otherMetadata.clear();
		metadata.put("test-value", new int[] {1, 2, 3});
		otherMetadata.put("test-value", new int[] {1, 2, 4});
		checkComparison(metadata, otherMetadata);

		metadata.clear();
		otherMetadata.clear();
		Map metadataValue = new HashMap();
		metadataValue.put("test-value", Integer.valueOf(1));
		metadata.put("test-value", metadataValue);
		Map otherMetadataValue = new HashMap();
		otherMetadataValue.put("test-value", Integer.valueOf(2));
		otherMetadata.put("test-value", otherMetadataValue);
	}

	/**
	 * Validates {@link FunctionData#FunctionData(long, java.util.Map)}.
	 */
	public void testFields() {
		long currentTime = System.currentTimeMillis();
		// check with null metadata
		FunctionData data = new TestFunctionData(currentTime, null);
		assertEquals("The function data timestamp is not correct.",
				currentTime, data.getTimestamp());
		assertNull("There is function data metadata.", data.getMetadata());

		// check with metadata
		Map metadata = new HashMap();
		data = new TestFunctionData(currentTime, metadata);
		assertSame("The function data metadata is not correct.",
				metadata, data.getMetadata());

		// check with map constructor
		Map fields = new HashMap();
		fields.put(FunctionData.FIELD_TIMESTAMP, Long.valueOf(currentTime));
		fields.put(FunctionData.FIELD_METADATA, metadata);
		data = new TestFunctionData(fields);
		assertEquals("The function data timestamp is not correct.",
				currentTime, data.getTimestamp());
		assertSame("The function data metadata is not correct.",
				metadata, data.getMetadata());
	}

	/**
	 * Validates {@link FunctionData#FunctionData(long, java.util.Map)} with
	 * invalid fields.
	 */
	public void testInvalidFields() {
		Map fields = new HashMap();
		fields.put(FunctionData.FIELD_TIMESTAMP, "1");
		checkInvalidFieldType(fields);

		fields.clear();
		fields.put(FunctionData.FIELD_TIMESTAMP, Long.valueOf(System.currentTimeMillis()));
		fields.put(FunctionData.FIELD_METADATA, "test");
		checkInvalidFieldType(fields);
	}

	private void checkInvalidFieldType(Map fields) {
		try {
			new TestFunctionData(fields);
			fail("The function data is built with invalid fields: " + fields);
		} catch (ClassCastException cce) {
			// go ahead, it's expected
		}
	}

	private void checkComparison(Map lessMetadata, Map greaterMetadata) {
		long currentTime = System.currentTimeMillis();
		long otherTime = currentTime - 1;

		FunctionData data = new TestFunctionData(currentTime, lessMetadata);
		assertTrue("Function data timestamp is not compared with higher priority.",
				data.compareTo(new TestFunctionData(otherTime, greaterMetadata)) > 0);

		assertTrue("Function data metadata comparison is not correct.",
				data.compareTo(new TestFunctionData(currentTime, greaterMetadata)) < 0);

		assertEquals("Function data comparison is not correct.",
				0, data.compareTo(new TestFunctionData(currentTime, lessMetadata)));
	}

	private void checkEqualsAndHashCode(Map metadata) {
		TestFunctionData testData = new TestFunctionData(Long.MIN_VALUE, metadata);
		assertFalse("The function data is equal to null.", testData.equals(null)); // NOPMD
		assertFalse("The function data is equal to java.lang.Object instance.", testData.equals(new Object()));
		assertEquals("The function data is not equal to itself.", testData, testData);
		TestFunctionData otherTestData = new TestFunctionData(Long.MIN_VALUE, metadata);
		assertEquals("The function data is not equal to a copy.",
				testData, otherTestData);
		assertEquals("Equal function data instances don't have equal hash code.",
				testData.hashCode(), otherTestData.hashCode());
	}
}

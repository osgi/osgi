/*
 * Copyright (c) OSGi Alliance (2011). All Rights Reserved.
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

package org.osgi.test.cases.util.tr069;

import java.io.UnsupportedEncodingException;

import info.dmtree.DmtData;
import info.dmtree.MetaNode;

import org.osgi.test.cases.util.tr069.helper.TestMetaNode;
import org.osgi.test.cases.util.tr069.helper.TR069UriTestCaseConstants;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.util.tr069.TR069MappingException;
import org.osgi.util.tr069.TR069ParameterValue;

/**
 * Checks the transformation between DMT data and TR-069 value for correctness.
 * 
 * @author Evgeni Grigorov, e.grigorov@prosyst.com
 */
public final class TR069ParameterValueTestCase extends OSGiTestCase {

	/** Tests {@link TR069ParameterValue} structure with different values. */
	public void testTR069ParameterValueStructure() {
		checkTR069ParameterValueStructure(
				TR069UriTestCaseConstants.TR069_VALUE_TRUE,
				TR069ParameterValue.TR069_TYPE_BOOLEAN);
		checkTR069ParameterValueStructure(
				TR069UriTestCaseConstants.TR069_INSTANCE_NUMBER_STRING,
				TR069ParameterValue.TR069_TYPE_INT);
		// TODO: uncomment after the fix of: 1999
		// checkTR069ParameterValueStructure(
		// TR069UriTestCaseConstants.TR069_VALUE_BASE64_STRING,
		// TR069ParameterValue.TR069_TYPE_BASE64);
		checkTR069ParameterValueStructure(
				TR069UriTestCaseConstants.TR069_VALUE_DATE_TIME,
				TR069ParameterValue.TR069_TYPE_DATETIME);
		checkTR069ParameterValueStructure(
				TR069UriTestCaseConstants.TR069_VALUE_HEX_STRING,
				TR069ParameterValue.TR069_TYPE_HEXBINARY);
		checkTR069ParameterValueStructure(
				TR069UriTestCaseConstants.TR069_INSTANCE_NUMBER_STRING,
				TR069ParameterValue.TR069_TYPE_LONG);
		checkTR069ParameterValueStructure("anyString",
				TR069ParameterValue.TR069_TYPE_STRING);
		// TODO: uncomment after the fix of: 1998
		// checkTR069ParameterValueStructure("",
		// TR069ParameterValue.TR069_TYPE_STRING);
		checkTR069ParameterValueStructure(
				TR069UriTestCaseConstants.TR069_INSTANCE_NUMBER_STRING,
				TR069ParameterValue.TR069_TYPE_UNSIGNED_INT);
		checkTR069ParameterValueStructure(
				TR069UriTestCaseConstants.TR069_INSTANCE_NUMBER_STRING,
				TR069ParameterValue.TR069_TYPE_UNSIGNED_LONG);
	}

	/**
	 * Tests the transformation of
	 * {@link TR069ParameterValue.TR069_TYPE_BOOLEAN}.
	 */
	public void testTR069BooleanType() throws IllegalArgumentException,
			UnsupportedEncodingException, TR069MappingException {
		MetaNode booleanMetaNode = new TestMetaNode(DmtData.FORMAT_BOOLEAN);

		// FIXME: Set null as node uri.
		// https://www.osgi.org/members/bugzilla/show_bug.cgi?id=1937
		// [PA Utility Classes] TR-069 boolean value issues
		// checkSimpleDmtData(DmtData.FALSE_VALUE,
		// TR069ParameterValue.TR069_TYPE_BOOLEAN,
		// TR069UriTestCaseConstants.TR069_VALUE_0,
		// TR069UriTestCaseConstants.DMT_URI_FULL, booleanMetaNode);
		// checkSimpleDmtData(DmtData.FALSE_VALUE,
		// TR069ParameterValue.TR069_TYPE_BOOLEAN,
		// TR069UriTestCaseConstants.TR069_VALUE_FALSE,
		// TR069UriTestCaseConstants.DMT_URI_FULL, booleanMetaNode);
		checkSimpleDmtData(DmtData.TRUE_VALUE,
				TR069ParameterValue.TR069_TYPE_BOOLEAN,
				TR069UriTestCaseConstants.TR069_VALUE_1,
				TR069UriTestCaseConstants.DMT_URI_FULL, booleanMetaNode);
		// checkSimpleDmtData(DmtData.TRUE_VALUE,
		// TR069ParameterValue.TR069_TYPE_BOOLEAN,
		// TR069UriTestCaseConstants.TR069_VALUE_TRUE,
		// TR069UriTestCaseConstants.DMT_URI_FULL, booleanMetaNode);
	}

	/**
	 * Tests the transformation of {@link TR069ParameterValue.TR069_TYPE_INT}.
	 */
	public void testTR069IntType() throws IllegalArgumentException,
			UnsupportedEncodingException, TR069MappingException {
		DmtData expectedDmtData = new DmtData(
				TR069UriTestCaseConstants.TR069_INSTANCE_NUMBER);

		MetaNode intMetaNode = new TestMetaNode(DmtData.FORMAT_INTEGER);

		checkSimpleDmtData(expectedDmtData, TR069ParameterValue.TR069_TYPE_INT,
				TR069UriTestCaseConstants.TR069_INSTANCE_NUMBER_STRING,
				TR069UriTestCaseConstants.DMT_URI_FULL, intMetaNode);
	}

	/**
	 * Tests the transformation of
	 * {@link TR069ParameterValue.TR069_TYPE_UNSIGNED_INT}.
	 */
	public void testTR069UnsignedIntType() throws IllegalArgumentException,
			UnsupportedEncodingException, TR069MappingException {
		DmtData expectedDmtData = new DmtData(
				TR069UriTestCaseConstants.TR069_INSTANCE_NUMBER_STRING,
				DmtData.FORMAT_UNSIGNED_INTEGER);

		MetaNode unsignedIntMetaNode = new TestMetaNode(
				DmtData.FORMAT_UNSIGNED_INTEGER);

		checkSimpleDmtData(expectedDmtData,
				TR069ParameterValue.TR069_TYPE_UNSIGNED_INT,
				TR069UriTestCaseConstants.TR069_INSTANCE_NUMBER_STRING,
				TR069UriTestCaseConstants.DMT_URI_FULL, unsignedIntMetaNode);
	}

	/**
	 * Tests the transformation of {@link TR069ParameterValue.TR069_TYPE_LONG}.
	 */
	public void testTR069LongType() throws IllegalArgumentException,
			UnsupportedEncodingException, TR069MappingException {
		DmtData expectedDmtData = new DmtData(
				(long) TR069UriTestCaseConstants.TR069_INSTANCE_NUMBER);

		MetaNode longMetaNode = new TestMetaNode(DmtData.FORMAT_LONG);

		checkSimpleDmtData(expectedDmtData,
				TR069ParameterValue.TR069_TYPE_LONG,
				TR069UriTestCaseConstants.TR069_INSTANCE_NUMBER_STRING,
				TR069UriTestCaseConstants.DMT_URI_FULL, longMetaNode);
	}

	/**
	 * Tests the transformation of
	 * {@link TR069ParameterValue.TR069_TYPE_UNSIGNED_LONG}.
	 */
	public void testTR069UnsignedLongType() throws IllegalArgumentException,
			UnsupportedEncodingException, TR069MappingException {
		DmtData expectedDmtData = new DmtData(
				TR069UriTestCaseConstants.TR069_INSTANCE_NUMBER_STRING,
				DmtData.FORMAT_UNSIGNED_LONG);

		MetaNode unsignedLongMetaNode = new TestMetaNode(
				DmtData.FORMAT_UNSIGNED_LONG);

		checkSimpleDmtData(expectedDmtData,
				TR069ParameterValue.TR069_TYPE_UNSIGNED_LONG,
				TR069UriTestCaseConstants.TR069_INSTANCE_NUMBER_STRING,
				TR069UriTestCaseConstants.DMT_URI_FULL, unsignedLongMetaNode);
	}

	/**
	 * Tests the transformation of
	 * {@link TR069ParameterValue.TR069_TYPE_DATETIME}.
	 */
	public void testTR069DateTimeType() throws IllegalArgumentException,
			UnsupportedEncodingException, TR069MappingException {
		DmtData expectedDmtData = new DmtData(
				TR069UriTestCaseConstants.TR069_VALUE_DATE_TIME,
				DmtData.FORMAT_DATE_TIME);

		MetaNode dateTimeMetaNode = new TestMetaNode(DmtData.FORMAT_DATE_TIME);

		checkSimpleDmtData(expectedDmtData,
				TR069ParameterValue.TR069_TYPE_DATETIME,
				TR069UriTestCaseConstants.TR069_VALUE_DATE_TIME,
				TR069UriTestCaseConstants.DMT_URI_FULL, dateTimeMetaNode);
	}

	/**
	 * Tests the transformation of {@link TR069ParameterValue.TR069_TYPE_BASE64}
	 * .
	 */
	public void testTR069Base64Type() throws IllegalArgumentException,
			UnsupportedEncodingException, TR069MappingException {
		// Meta node is base 64
		MetaNode testMetaNode = new TestMetaNode(DmtData.FORMAT_BASE64);
		DmtData expectedDmtData = new DmtData(
				TR069UriTestCaseConstants.TR069_VALUE_BIN_STRING.getBytes(),
				true);
		checkSimpleDmtData(expectedDmtData,
				TR069ParameterValue.TR069_TYPE_BASE64,
				TR069UriTestCaseConstants.TR069_VALUE_BASE64_STRING,
				TR069UriTestCaseConstants.DMT_URI_FULL, testMetaNode);

		// Meta node is raw binary
		expectedDmtData = new DmtData(
				TR069UriTestCaseConstants.FORMAT_OSGi_BIN,
				TR069UriTestCaseConstants.TR069_VALUE_BASE64_STRING.getBytes());
		testMetaNode = new TestMetaNode(DmtData.FORMAT_RAW_BINARY,
				new String[] {expectedDmtData.getFormatName()});
		checkSimpleDmtData(expectedDmtData,
				TR069ParameterValue.TR069_TYPE_BASE64,
				TR069UriTestCaseConstants.TR069_VALUE_BASE64_STRING,
				TR069UriTestCaseConstants.DMT_URI_FULL, testMetaNode);
	}

	/**
	 * Tests the transformation of
	 * {@link TR069ParameterValue.TR069_TYPE_HEXBINARY}.
	 */
	public void testTR069HexType() throws IllegalArgumentException,
			UnsupportedEncodingException, TR069MappingException {
		// Meta node is hex binary
		MetaNode testMetaNode = new TestMetaNode(DmtData.FORMAT_HEX_BINARY);
		DmtData expectedDmtData = new DmtData(
				TR069UriTestCaseConstants.TR069_VALUE_BIN_STRING.getBytes(),
				DmtData.FORMAT_HEX_BINARY);
		checkSimpleDmtData(expectedDmtData,
				TR069ParameterValue.TR069_TYPE_HEXBINARY,
				TR069UriTestCaseConstants.TR069_VALUE_HEX_STRING,
				TR069UriTestCaseConstants.DMT_URI_FULL, testMetaNode);

		// Meta node is binary
		testMetaNode = new TestMetaNode(DmtData.FORMAT_BINARY);
		expectedDmtData = new DmtData(
				TR069UriTestCaseConstants.TR069_VALUE_HEX_STRING.getBytes(),
				DmtData.FORMAT_BINARY);
		checkSimpleDmtData(expectedDmtData,
				TR069ParameterValue.TR069_TYPE_HEXBINARY,
				TR069UriTestCaseConstants.TR069_VALUE_HEX_STRING,
				TR069UriTestCaseConstants.DMT_URI_FULL, testMetaNode);
	}

	/**
	 * Tests the transformation of {@link TR069ParameterValue.TR069_TYPE_STRING}
	 * .
	 */
	public void testTR069StringType() throws IllegalArgumentException,
			UnsupportedEncodingException, TR069MappingException {
		// Meta node points to null format
		MetaNode testMetaNode = new TestMetaNode(DmtData.FORMAT_NULL);
		checkSimpleDmtData(DmtData.NULL_VALUE,
				TR069ParameterValue.TR069_TYPE_STRING,
				TR069UriTestCaseConstants.EMPTY_STRING,
				TR069UriTestCaseConstants.DMT_URI_FULL, testMetaNode);

		// Meta node points to float format
		testMetaNode = new TestMetaNode(DmtData.FORMAT_FLOAT);
		DmtData expectedDmtData = new DmtData(
				(float) TR069UriTestCaseConstants.TR069_INSTANCE_NUMBER);
		checkSimpleDmtData(expectedDmtData,
				TR069ParameterValue.TR069_TYPE_STRING,
				TR069UriTestCaseConstants.TR069_INSTANCE_NUMBER_STRING,
				TR069UriTestCaseConstants.DMT_URI_FULL, testMetaNode);

		// Meta node points to date format
		testMetaNode = new TestMetaNode(DmtData.FORMAT_DATE);
		expectedDmtData = new DmtData(
				TR069UriTestCaseConstants.TR069_VALUE_DATE, DmtData.FORMAT_DATE);
		checkSimpleDmtData(expectedDmtData,
				TR069ParameterValue.TR069_TYPE_STRING,
				TR069UriTestCaseConstants.TR069_VALUE_DATE,
				TR069UriTestCaseConstants.DMT_URI_FULL, testMetaNode);

		// Meta node points to time format
		testMetaNode = new TestMetaNode(DmtData.FORMAT_TIME);
		expectedDmtData = new DmtData(
				TR069UriTestCaseConstants.TR069_VALUE_TIME, DmtData.FORMAT_TIME);
		checkSimpleDmtData(expectedDmtData,
				TR069ParameterValue.TR069_TYPE_STRING,
				TR069UriTestCaseConstants.TR069_VALUE_TIME,
				TR069UriTestCaseConstants.DMT_URI_FULL, testMetaNode);

		// Meta node points to node URI
		testMetaNode = new TestMetaNode(DmtData.FORMAT_NODE_URI);
		expectedDmtData = new DmtData(TR069UriTestCaseConstants.DMT_URI_FULL,
				DmtData.FORMAT_NODE_URI);
		checkSimpleDmtData(expectedDmtData,
				TR069ParameterValue.TR069_TYPE_STRING,
				TR069UriTestCaseConstants.TR069_PATH_FULL,
				TR069UriTestCaseConstants.DMT_URI_FULL, testMetaNode);

		// Meta node points to raw string
		expectedDmtData = new DmtData(
				TR069UriTestCaseConstants.FORMAT_OSGi_BIN,
				TR069UriTestCaseConstants.TR069_VALUE_HEX_STRING);
		testMetaNode = new TestMetaNode(DmtData.FORMAT_RAW_STRING,
				new String[] {expectedDmtData.getFormatName()});
		checkSimpleDmtData(expectedDmtData,
				TR069ParameterValue.TR069_TYPE_STRING,
				TR069UriTestCaseConstants.TR069_VALUE_HEX_STRING,
				TR069UriTestCaseConstants.DMT_URI_FULL, testMetaNode);

		// Meta node points to string
		testMetaNode = new TestMetaNode(DmtData.FORMAT_STRING);
		expectedDmtData = new DmtData(
				TR069UriTestCaseConstants.TR069_VALUE_HEX_STRING);
		checkSimpleDmtData(expectedDmtData,
				TR069ParameterValue.TR069_TYPE_STRING,
				TR069UriTestCaseConstants.TR069_VALUE_HEX_STRING,
				TR069UriTestCaseConstants.DMT_URI_FULL, testMetaNode);

		// Meta node points to XML
		testMetaNode = new TestMetaNode(DmtData.FORMAT_XML);
		expectedDmtData = new DmtData(
				TR069UriTestCaseConstants.TR069_VALUE_HEX_STRING,
				DmtData.FORMAT_XML);
		checkSimpleDmtData(expectedDmtData,
				TR069ParameterValue.TR069_TYPE_STRING,
				TR069UriTestCaseConstants.TR069_VALUE_HEX_STRING,
				TR069UriTestCaseConstants.DMT_URI_FULL, testMetaNode);
	}

	/** Tests the transformation of the comma separated list value. */
	public void testTR069List() throws IllegalArgumentException,
			UnsupportedEncodingException, TR069MappingException {
		// check integer list
		MetaNode testMetaNode = new TestMetaNode(DmtData.FORMAT_INTEGER);
		checkDmtDataForListValue(TR069UriTestCaseConstants.DMT_DATA_ARRAY_INT,
				TR069ParameterValue.TR069_TYPE_INT, testMetaNode);

		// check string list
		testMetaNode = new TestMetaNode(DmtData.FORMAT_STRING);
		checkDmtDataForListValue(
				TR069UriTestCaseConstants.DMT_DATA_ARRAY_STRING,
				TR069ParameterValue.TR069_TYPE_STRING, testMetaNode);

		// check node URI list
		testMetaNode = new TestMetaNode(DmtData.FORMAT_NODE_URI);
		checkDmtDataForListValue(
				TR069UriTestCaseConstants.DMT_DATA_ARRAY_NODE_URI,
				TR069ParameterValue.TR069_TYPE_STRING, testMetaNode);
	}

	/** Tests the transformation of {@link DmtData#FORMAT_BASE64}. */
	public void testDmtBase64Format() {
		// TODO: uncomment after the fix of: 1999
		// DmtData dmtData = new DmtData(
		// TR069UriTestCaseConstants.TR069_VALUE_BIN_STRING.getBytes(),
		// true);
		// TR069ParameterValue expectedValue = new TR069ParameterValue(
		// TR069UriTestCaseConstants.TR069_VALUE_BASE64_STRING,
		// TR069ParameterValue.TR069_TYPE_BASE64);
		// checkTR069Value(expectedValue,
		// TR069ParameterValue.getTR069ParameterValue(dmtData));
	}

	/** Tests the transformation of {@link DmtData#FORMAT_BINARY}. */
	public void testDmtBinaryFormat() {
		DmtData dmtData = new DmtData(
				TR069UriTestCaseConstants.TR069_VALUE_BIN_STRING.getBytes(),
				false);
		TR069ParameterValue expectedValue = new TR069ParameterValue(
				TR069UriTestCaseConstants.TR069_VALUE_HEX_STRING,
				TR069ParameterValue.TR069_TYPE_HEXBINARY);
		checkTR069Value(expectedValue,
				TR069ParameterValue.getTR069ParameterValue(dmtData));
	}

	/** Tests the transformation of {@link DmtData#FORMAT_BOOLEAN}. */
	public void testDmtBooleanFormat() {
		TR069ParameterValue expectedValue = new TR069ParameterValue(
				TR069UriTestCaseConstants.TR069_VALUE_0,
				TR069ParameterValue.TR069_TYPE_BOOLEAN);
		checkTR069Value(expectedValue,
				TR069ParameterValue.getTR069ParameterValue(DmtData.FALSE_VALUE));
	}

	/** Tests the transformation of {@link DmtData#FORMAT_DATE}. */
	public void testDmtDateFormat() {
		DmtData dmtData = new DmtData(
				TR069UriTestCaseConstants.TR069_VALUE_DATE, DmtData.FORMAT_DATE);
		TR069ParameterValue expectedValue = new TR069ParameterValue(
				TR069UriTestCaseConstants.TR069_VALUE_DATE,
				TR069ParameterValue.TR069_TYPE_STRING);
		checkTR069Value(expectedValue,
				TR069ParameterValue.getTR069ParameterValue(dmtData));
	}

	/** Tests the transformation of {@link DmtData#FORMAT_FLOAT}. */
	public void testDmtFloatFormat() {
		DmtData dmtData = new DmtData(
				(float) TR069UriTestCaseConstants.TR069_INSTANCE_NUMBER);
		TR069ParameterValue expectedValue = new TR069ParameterValue(
				dmtData.toString(), TR069ParameterValue.TR069_TYPE_STRING);
		checkTR069Value(expectedValue,
				TR069ParameterValue.getTR069ParameterValue(dmtData));
	}

	/** Tests the transformation of {@link DmtData#FORMAT_INTEGER}. */
	public void testDmtIntegerFormat() {
		DmtData dmtData = new DmtData(
				TR069UriTestCaseConstants.TR069_INSTANCE_NUMBER);
		TR069ParameterValue expectedValue = new TR069ParameterValue(
				dmtData.toString(), TR069ParameterValue.TR069_TYPE_INT);
		checkTR069Value(expectedValue,
				TR069ParameterValue.getTR069ParameterValue(dmtData));
	}

	/** Tests the transformation of {@link DmtData#FORMAT_NULL}. */
	public void testDmtNULLFormat() {
		TR069ParameterValue expectedValue = new TR069ParameterValue(
				DmtData.NULL_VALUE.toString(),
				TR069ParameterValue.TR069_TYPE_STRING);
		checkTR069Value(expectedValue,
				TR069ParameterValue.getTR069ParameterValue(DmtData.NULL_VALUE));
	}

	/** Tests the transformation of {@link DmtData#FORMAT_RAW_BINARY}. */
	public void testDmtRawBinaryFormat() {
		// TODO: uncomment after the fix of: 1999
		// DmtData dmtData = new DmtData(
		// TR069UriTestCaseConstants.FORMAT_OSGi_BIN,
		// TR069UriTestCaseConstants.TR069_VALUE_BIN_STRING.getBytes());
		// TR069ParameterValue expectedValue = new TR069ParameterValue(
		// TR069UriTestCaseConstants.TR069_VALUE_BASE64_STRING,
		// TR069ParameterValue.TR069_TYPE_BASE64);
		// checkTR069Value(expectedValue,
		// TR069ParameterValue.getTR069ParameterValue(dmtData));
	}

	/** Tests the transformation of {@link DmtData#FORMAT_RAW_STRING}. */
	public void testDmtRawStringFormat() {
		DmtData dmtData = new DmtData(
				TR069UriTestCaseConstants.FORMAT_OSGi_BIN,
				TR069UriTestCaseConstants.TR069_VALUE_BIN_STRING);
		TR069ParameterValue expectedValue = new TR069ParameterValue(
				TR069UriTestCaseConstants.TR069_VALUE_BIN_STRING,
				TR069ParameterValue.TR069_TYPE_STRING);
		checkTR069Value(expectedValue,
				TR069ParameterValue.getTR069ParameterValue(dmtData));
	}

	/** Tests the transformation of {@link DmtData#FORMAT_STRING}. */
	public void testDmtStringFormat() {
		DmtData dmtData = new DmtData(
				TR069UriTestCaseConstants.TR069_VALUE_BIN_STRING);
		TR069ParameterValue expectedValue = new TR069ParameterValue(
				TR069UriTestCaseConstants.TR069_VALUE_BIN_STRING,
				TR069ParameterValue.TR069_TYPE_STRING);
		checkTR069Value(expectedValue,
				TR069ParameterValue.getTR069ParameterValue(dmtData));
	}

	/** Tests the transformation of {@link DmtData#FORMAT_TIME}. */
	public void testDmtTimeFormat() {
		DmtData dmtData = new DmtData(
				TR069UriTestCaseConstants.TR069_VALUE_TIME, DmtData.FORMAT_TIME);
		TR069ParameterValue expectedValue = new TR069ParameterValue(
				TR069UriTestCaseConstants.TR069_VALUE_TIME,
				TR069ParameterValue.TR069_TYPE_STRING);
		checkTR069Value(expectedValue,
				TR069ParameterValue.getTR069ParameterValue(dmtData));
	}

	/** Tests the transformation of {@link DmtData#FORMAT_XML}. */
	public void testDmtXMLFormat() {
		DmtData dmtData = new DmtData(
				TR069UriTestCaseConstants.TR069_PATH_FULL, DmtData.FORMAT_XML);
		TR069ParameterValue expectedValue = new TR069ParameterValue(
				TR069UriTestCaseConstants.TR069_PATH_FULL,
				TR069ParameterValue.TR069_TYPE_STRING);
		checkTR069Value(expectedValue,
				TR069ParameterValue.getTR069ParameterValue(dmtData));
	}

	/** Tests the transformation of {@link DmtData#FORMAT_UNSIGNED_INTEGER}. */
	public void testDmtUnsignedIntFormat() {
		DmtData dmtData = new DmtData(TR069UriTestCaseConstants.TR069_VALUE_1,
				DmtData.FORMAT_UNSIGNED_INTEGER);
		TR069ParameterValue expectedValue = new TR069ParameterValue(
				TR069UriTestCaseConstants.TR069_VALUE_1,
				TR069ParameterValue.TR069_TYPE_UNSIGNED_INT);
		checkTR069Value(expectedValue,
				TR069ParameterValue.getTR069ParameterValue(dmtData));
	}

	/** Tests the transformation of {@link DmtData#FORMAT_UNSIGNED_LONG}. */
	public void testDmtUnsignedLongFormat() {
		DmtData dmtData = new DmtData(TR069UriTestCaseConstants.TR069_VALUE_1,
				DmtData.FORMAT_UNSIGNED_LONG);
		TR069ParameterValue expectedValue = new TR069ParameterValue(
				TR069UriTestCaseConstants.TR069_VALUE_1,
				TR069ParameterValue.TR069_TYPE_UNSIGNED_LONG);
		checkTR069Value(expectedValue,
				TR069ParameterValue.getTR069ParameterValue(dmtData));
	}

	/** Tests the transformation of {@link DmtData#FORMAT_LONG}. */
	public void testDmtLongFormat() {
		DmtData dmtData = new DmtData(
				(long) TR069UriTestCaseConstants.TR069_INSTANCE_NUMBER);
		TR069ParameterValue expectedValue = new TR069ParameterValue(
				TR069UriTestCaseConstants.TR069_INSTANCE_NUMBER_STRING,
				TR069ParameterValue.TR069_TYPE_LONG);
		checkTR069Value(expectedValue,
				TR069ParameterValue.getTR069ParameterValue(dmtData));
	}

	// TODO: uncomment after the fix of: 2005
	// /** Tests the transformation of {@link DmtData#FORMAT_DATETIME}. */
	// public void testDmtDateTimeFormat() {
	// DmtData dmtData = new DmtData(
	// TR069UriTestCaseConstants.TR069_VALUE_DATE_TIME,
	// DmtData.FORMAT_DATETIME);
	// TR069ParameterValue expectedValue = new TR069ParameterValue(
	// TR069UriTestCaseConstants.TR069_VALUE_DATE_TIME,
	// TR069ParameterValue.TR069_TYPE_DATETIME);
	// checkTR069Value(expectedValue,
	// TR069ParameterValue.getTR069ParameterValue(dmtData));
	// }

	/** Tests the transformation of {@link DmtData#FORMAT_HEXBINARY}. */
	public void testDmtHexFormat() {
		DmtData dmtData = new DmtData(
				TR069UriTestCaseConstants.TR069_VALUE_BIN_STRING.getBytes(),
				DmtData.FORMAT_HEX_BINARY);
		TR069ParameterValue expectedValue = new TR069ParameterValue(
				TR069UriTestCaseConstants.TR069_VALUE_HEX_STRING,
				TR069ParameterValue.TR069_TYPE_HEXBINARY);
		checkTR069Value(expectedValue,
				TR069ParameterValue.getTR069ParameterValue(dmtData));
	}

	/** Tests the transformation of {@link DmtData#FORMAT_NODE_URI}. */
	public void testDmtNodeUriFormat() {
		// TODO: uncomment after the fix of: 1997
		// DmtData dmtData = new DmtData(TR069UriTestCaseConstants.DMT_URI_FULL,
		// DmtData.FORMAT_NODE_URI);
		// TR069ParameterValue expectedValue = new TR069ParameterValue(
		// TR069UriTestCaseConstants.TR069_PATH_FULL,
		// TR069ParameterValue.TR069_TYPE_STRING);
		// checkTR069Value(expectedValue,
		// TR069ParameterValue.getTR069ParameterValue(dmtData));
	}

	/** Tests the transformation of a list of DMT data instances. */
	public void testDmtListSubtreeFormat() {
		TR069ParameterValue expectedValue = new TR069ParameterValue(
				generateListValue(TR069UriTestCaseConstants.DMT_DATA_ARRAY_STRING),
				TR069ParameterValue.TR069_TYPE_STRING);
		checkTR069Value(
				expectedValue,
				TR069ParameterValue
						.getTR069ParameterValueForList(TR069UriTestCaseConstants.DMT_DATA_ARRAY_STRING));

		expectedValue = new TR069ParameterValue(
				generateListValue(TR069UriTestCaseConstants.DMT_DATA_ARRAY_INT),
				TR069ParameterValue.TR069_TYPE_STRING);
		checkTR069Value(
				expectedValue,
				TR069ParameterValue
						.getTR069ParameterValueForList(TR069UriTestCaseConstants.DMT_DATA_ARRAY_INT));

		// TODO: uncomment after the fix of: 2000
		// expectedValue = new TR069ParameterValue(
		// generateListValue(TR069UriTestCaseConstants.DMT_DATA_ARRAY_NODE_URI),
		// TR069ParameterValue.TR069_TYPE_STRING);
		// checkTR069Value(
		// expectedValue,
		// TR069ParameterValue
		// .getTR069ParameterValueForList(TR069UriTestCaseConstants.DMT_DATA_ARRAY_NODE_URI));
	}

	private void checkTR069Value(TR069ParameterValue expectedValue,
			TR069ParameterValue actualValue) {
		assertEquals("The TR-069 parameter value is not correct!",
				expectedValue.getValue(), actualValue.getValue());
		assertEquals("The TR-069 parameter type is not correct!",
				expectedValue.getType(), actualValue.getType());
	}

	private void checkDmtDataForListValue(DmtData[] values, String tr069Type,
			MetaNode metaNode) throws IllegalArgumentException,
			UnsupportedEncodingException, TR069MappingException {
		String listValue = generateListValue(values);
		DmtData[] actualValues = TR069ParameterValue.getDmtDataForList(
				listValue, tr069Type, TR069UriTestCaseConstants.CHARSET_UTF8,
				TR069UriTestCaseConstants.DMT_URI_FULL, metaNode);
		assertEquals("The number of DmtData instances is not correct!",
				values.length, actualValues.length);
		for (int i = 0; i < values.length; i++) {
			assertEquals("The DmtData is not correct for the list!", values[i],
					actualValues[i]);
		}
	}

	private void checkSimpleDmtData(DmtData expectedDmtData, String tr069Type,
			String tr069Value, String nodeUri, MetaNode metaNode)
			throws IllegalArgumentException, UnsupportedEncodingException,
			TR069MappingException {
		DmtData actualDmtData = TR069ParameterValue.getDmtData(tr069Value,
				tr069Type, TR069UriTestCaseConstants.CHARSET_UTF8, nodeUri,
				metaNode);
		assertEquals("The DMT data is not correct!", expectedDmtData,
				actualDmtData);
		assertEquals("The DMT data format name is not correct!",
				expectedDmtData.getFormatName(), actualDmtData.getFormatName());
	}

	private void checkTR069ParameterValueStructure(String value, String type) {
		TR069ParameterValue TR069Param = new TR069ParameterValue(value, type);
		assertEquals("The parameter value is not expected one!", value,
				TR069Param.getValue());
		assertEquals("The parameter type is not expected one!", type,
				TR069Param.getType());
	}

	private static String generateListValue(DmtData[] values) {
		StringBuffer listValue = new StringBuffer();
		for (int i = 0, lastIndex = values.length - 1; i <= lastIndex; i++) {
			listValue.append(TR069ParameterValue.getTR069ParameterValue(
					values[i]).getValue());
			if (i != lastIndex) {
				listValue.append(',');
			}
		}
		return listValue.toString();
	}
}

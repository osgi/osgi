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
 * Checks the behavior of the transformation between DMT data and TR-069 value
 * for invalid parameters.
 * 
 * @author Evgeni Grigorov, e.grigorov@prosyst.com
 */
public final class TR069ParameterValueFailTestCase extends OSGiTestCase {

	/**
	 * Tests {@link TR069ParameterValue} structure with different invalid
	 * values.
	 */
	public void testTR069ParameterValueStructureFail() {
		checkTR069ParameterValueStructureFail(null, null);
		checkTR069ParameterValueStructureFail("anyValue", null);
		checkTR069ParameterValueStructureFail(null, "anyType");
		checkTR069ParameterValueStructureFail("anyValue", "anyType");
		checkTR069ParameterValueStructureFail(null,
				TR069ParameterValue.TR069_TYPE_STRING);
	}

	/**
	 * Tests the transformation of invalid Dmt data instances like
	 * <code>null</code>, complex object etc.
	 */
	public void testSimpleDmtDataFail() {
		// check null value
		try {
			TR069ParameterValue.getTR069ParameterValue(null);
			fail("The TR-069 parameter value must trow NullPointerException for null argument!");
		}
		catch (NullPointerException npe) {
			// go ahead, it's ok
		}

		// check complex object
		try {
			TR069ParameterValue
					.getTR069ParameterValue(new DmtData(new Object()));
			fail("The TR-069 parameter value must trow IllegalArgumentException for complex object DmtData!");
		}
		catch (IllegalArgumentException iae) {
			// go ahead, it's ok
		}
	}

	/** Tests the transformation of invalid arrays of Dmt data. */
	public void testListDmtDataFail() {
		// check null value
		try {
			TR069ParameterValue.getTR069ParameterValueForList(null);
			fail("The TR-069 parameter value for list must trow NullPointerException for null argument!");
		}
		catch (NullPointerException npe) {
			// go ahead, it's ok
		}

		// check null element
		try {
			TR069ParameterValue
					.getTR069ParameterValueForList(new DmtData[] {null});
			fail("The TR-069 parameter value for list must trow NullPointerException for null argument!");
		}
		catch (NullPointerException npe) {
			// go ahead, it's ok
		}
	}

	/** Test the transformation of invalid TR-069 parameter values. */
	public void testSimpleTR069ValueFail() throws UnsupportedEncodingException,
			TR069MappingException {
		checkTR069ValueFail(false);
	}

	/** Test the transformation of invalid TR-069 parameter list values. */
	public void testListTR069ValueFail() throws UnsupportedEncodingException,
			TR069MappingException {
		checkTR069ValueFail(true);
	}

	private void checkTR069ValueFail(boolean isList)
			throws UnsupportedEncodingException, TR069MappingException {
		MetaNode metaNode = new TestMetaNode(DmtData.FORMAT_STRING);

		// check null value
		try {
			if (isList) {
				TR069ParameterValue.getDmtDataForList(null,
						TR069ParameterValue.TR069_TYPE_STRING,
						TR069UriTestCaseConstants.CHARSET_UTF8,
						TR069UriTestCaseConstants.DMT_URI_FULL, metaNode);
			}
			else {
				TR069ParameterValue.getDmtData(null,
						TR069ParameterValue.TR069_TYPE_STRING,
						TR069UriTestCaseConstants.CHARSET_UTF8,
						TR069UriTestCaseConstants.DMT_URI_FULL, metaNode);
			}
			fail("DMT data cannot be constructed with null value!");
		}
		catch (IllegalArgumentException iae) {
			// go ahead, it's ok
		}

		// check null type
		try {
			if (isList) {
				TR069ParameterValue.getDmtDataForList(
						TR069UriTestCaseConstants.TR069_VALUE_BIN_STRING, null,
						TR069UriTestCaseConstants.CHARSET_UTF8,
						TR069UriTestCaseConstants.DMT_URI_FULL, metaNode);
			}
			else {
				TR069ParameterValue.getDmtData(
						TR069UriTestCaseConstants.TR069_VALUE_BIN_STRING, null,
						TR069UriTestCaseConstants.CHARSET_UTF8,
						TR069UriTestCaseConstants.DMT_URI_FULL, metaNode);
			}
			fail("DMT data cannot be constructed with null type!");
		}
		catch (IllegalArgumentException iae) {
			// go ahead, it's ok
		}

		// check null URI
		try {
			if (isList) {
				TR069ParameterValue.getDmtDataForList(
						TR069UriTestCaseConstants.TR069_VALUE_BIN_STRING,
						TR069ParameterValue.TR069_TYPE_STRING,
						TR069UriTestCaseConstants.CHARSET_UTF8, null, metaNode);
			}
			else {
				TR069ParameterValue.getDmtData(
						TR069UriTestCaseConstants.TR069_VALUE_BIN_STRING,
						TR069ParameterValue.TR069_TYPE_STRING,
						TR069UriTestCaseConstants.CHARSET_UTF8, null, metaNode);
			}
			fail("DMT data cannot be constructed with null URI!");
		}
		catch (IllegalArgumentException iae) {
			// go ahead, it's ok
		}

		// check null meta data
		try {
			if (isList) {
				TR069ParameterValue.getDmtDataForList(
						TR069UriTestCaseConstants.TR069_VALUE_BIN_STRING,
						TR069ParameterValue.TR069_TYPE_STRING,
						TR069UriTestCaseConstants.CHARSET_UTF8,
						TR069UriTestCaseConstants.DMT_URI_FULL, null);
			}
			else {
				TR069ParameterValue.getDmtData(
						TR069UriTestCaseConstants.TR069_VALUE_BIN_STRING,
						TR069ParameterValue.TR069_TYPE_STRING,
						TR069UriTestCaseConstants.CHARSET_UTF8,
						TR069UriTestCaseConstants.DMT_URI_FULL, null);
			}
			fail("DMT data cannot be constructed with null URI!");
		}
		catch (IllegalArgumentException iae) {
			// go ahead, it's ok
		}

		// check null raw format names
		try {
			MetaNode rawStringMetaNode = new TestMetaNode(
					DmtData.FORMAT_RAW_STRING);
			if (isList) {
				TR069ParameterValue.getDmtDataForList(
						TR069UriTestCaseConstants.TR069_VALUE_BIN_STRING,
						TR069ParameterValue.TR069_TYPE_STRING,
						TR069UriTestCaseConstants.CHARSET_UTF8,
						TR069UriTestCaseConstants.DMT_URI_FULL,
						rawStringMetaNode);
			}
			else {
				TR069ParameterValue.getDmtData(
						TR069UriTestCaseConstants.TR069_VALUE_BIN_STRING,
						TR069ParameterValue.TR069_TYPE_STRING,
						TR069UriTestCaseConstants.CHARSET_UTF8,
						TR069UriTestCaseConstants.DMT_URI_FULL,
						rawStringMetaNode);
			}
			fail("DMT raw string data cannot be constructed with null raw format names!");
		}
		catch (TR069MappingException me) {
			// go ahead, it's ok
		}

		// check invalid integer string
		try {
			metaNode = new TestMetaNode(DmtData.FORMAT_INTEGER);
			String value = "it's not an integer";
			if (isList) {
				TR069ParameterValue.getDmtDataForList(value,
						TR069ParameterValue.TR069_TYPE_INT,
						TR069UriTestCaseConstants.CHARSET_UTF8,
						TR069UriTestCaseConstants.DMT_URI_FULL, metaNode);
			}
			else {
				TR069ParameterValue.getDmtData(value,
						TR069ParameterValue.TR069_TYPE_INT,
						TR069UriTestCaseConstants.CHARSET_UTF8,
						TR069UriTestCaseConstants.DMT_URI_FULL, metaNode);
			}
			fail("DMT data cannot be constructed with an invalid integer string!");
		}
		catch (TR069MappingException me) {
			// go ahead, it's ok
		}

		// check invalid boolean
		try {
			metaNode = new TestMetaNode(DmtData.FORMAT_BOOLEAN);
			String value = "it's not a boolean";
			if (isList) {
				TR069ParameterValue.getDmtDataForList(value,
						TR069ParameterValue.TR069_TYPE_BOOLEAN,
						TR069UriTestCaseConstants.CHARSET_UTF8,
						TR069UriTestCaseConstants.DMT_URI_FULL, metaNode);
			}
			else {
				TR069ParameterValue.getDmtData(value,
						TR069ParameterValue.TR069_TYPE_BOOLEAN,
						TR069UriTestCaseConstants.CHARSET_UTF8,
						TR069UriTestCaseConstants.DMT_URI_FULL, metaNode);
			}
			fail("DMT data cannot be constructed with an invalid boolean string!");
		}
		catch (TR069MappingException me) {
			// go ahead, it's ok
		}

		// check invalid hex
		try {
			metaNode = new TestMetaNode(DmtData.FORMAT_HEXBINARY);
			String value = "-1-1-1";
			if (isList) {
				TR069ParameterValue.getDmtDataForList(value,
						TR069ParameterValue.TR069_TYPE_HEXBINARY,
						TR069UriTestCaseConstants.CHARSET_UTF8,
						TR069UriTestCaseConstants.DMT_URI_FULL, metaNode);
			}
			else {
				TR069ParameterValue.getDmtData(value,
						TR069ParameterValue.TR069_TYPE_HEXBINARY,
						TR069UriTestCaseConstants.CHARSET_UTF8,
						TR069UriTestCaseConstants.DMT_URI_FULL, metaNode);
			}
			fail("DMT data cannot be constructed with an invalid boolean string!");
		}
		catch (TR069MappingException me) {
			// go ahead, it's ok
		}
	}

	private static void checkTR069ParameterValueStructureFail(String value,
			String type) {
		try {
			new TR069ParameterValue(value, type);
			fail("IllegalArgumentException is expected for value: " + value
					+ "; type: " + type);
		}
		catch (IllegalArgumentException ise) {
			// nothing to do, it's expected
		}
	}

}

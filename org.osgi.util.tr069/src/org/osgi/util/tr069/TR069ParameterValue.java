/*
 * Copyright (c) OSGi Alliance (2007, 2011). All Rights Reserved.
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

package org.osgi.util.tr069;

import info.dmtree.DmtData;
import info.dmtree.MetaNode;
import info.dmtree.Uri;

import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

/******************************/
/**
 * Class which contains value and data type for TR-069, and static methods of
 * utilities.
 * 
 * @author Ikuo YAMASAKI, NTT Corporation
 * 
 */
public class TR069ParameterValue {
	/**
	 * Constant representing the TR-069 integer type.
	 * 
	 * The value of TR069_TYPE_INT is "int".
	 */
	public final static String	TR069_TYPE_INT				= "int";

	/**
	 * Constant representing the TR-069 unsinged integer type.
	 * 
	 * The value of TR069_TYPE_UNSIGNED_INT is "unsignedInt".
	 */
	public final static String	TR069_TYPE_UNSIGNED_INT		= "unsignedInt";

	/**
	 * Constant representing the TR-069 long type.
	 * 
	 * The value of TR069_TYPE_LONG is "long".
	 */
	public final static String	TR069_TYPE_LONG				= "long";

	/**
	 * Constant representing the TR-069 unsigned long type.
	 * 
	 * The value of TR069_TYPE_UNSIGNED_LONG is "unsignedLong".
	 */
	public final static String	TR069_TYPE_UNSIGNED_LONG	= "unsignedLong";

	/**
	 * Constant representing the TR-069 string type.
	 * 
	 * The value of TR069_TYPE_STRING is "string".
	 */
	public final static String	TR069_TYPE_STRING			= "string";

	/**
	 * Constant representing the TR-069 boolean type.
	 * 
	 * The value of TR069_TYPE_BOOLEAN is "boolean".
	 */
	public final static String	TR069_TYPE_BOOLEAN			= "boolean";

	/**
	 * Constant representing the TR-069 base64 type.
	 * 
	 * The value of TR069_TYPE_BASE64 is "base64".
	 */
	public final static String	TR069_TYPE_BASE64			= "base64";

	/**
	 * Constant representing the TR-069 hex binary type.
	 * 
	 * The value of TR069_TYPE_HEXBINARY is "hexBinary".
	 */
	public final static String	TR069_TYPE_HEXBINARY		= "hexBinary";

	/**
	 * Constant representing the TR-069 date time type.
	 * 
	 * The value of TR069_TYPE_DATETIME is "dateTime".
	 */
	public final static String	TR069_TYPE_DATETIME			= "dateTime";

	private static boolean		debug						= true;
	private final String		value;
	private final String		type;

	/**
	 * Constructor of TR-069 Parameter Value.
	 * 
	 * @param value value to be used. It can be either lexical or canonical
	 *        representation as defined by XML Schema.
	 * @param type data type defined in TR-069.
	 * @throws IllegalArgumentException if value is {@code null} or empty, if
	 *         type is {@code null}, if type is not any of defined types.
	 */
	public TR069ParameterValue(String value, String type)
			throws IllegalArgumentException {
		if (value == null)
			throw new IllegalArgumentException("value must not be null");
		if (value.length() == 0)
			throw new IllegalArgumentException("value must not be empty");
		if (type == null)
			throw new IllegalArgumentException("type must not be null");
		if ((!type.equals(TR069_TYPE_INT))
				&& (!type.equals(TR069_TYPE_UNSIGNED_INT))
				&& (!type.equals(TR069_TYPE_LONG))
				&& (!type.equals(TR069_TYPE_UNSIGNED_LONG))
				&& (!type.equals(TR069_TYPE_STRING))
				&& (!type.equals(TR069_TYPE_BOOLEAN))
				&& (!type.equals(TR069_TYPE_BASE64))
				&& (!type.equals(TR069_TYPE_HEXBINARY))
				&& (!type.equals(TR069_TYPE_DATETIME)))
			throw new IllegalArgumentException(
					"type must be one of the defined ones. type=" + type);
		// TODO lexical/representative presentation.
		if (type.equals(TR069_TYPE_BASE64))
			if ((!value.equals("1")) && (!value.equals("0"))) {
				throw new IllegalArgumentException(
						"Although type is TR069_TYPE_BASE64, invalid value="
								+ value);
			}

		this.value = value;
		this.type = type;
	}

	/**
	 * Get the value this TR-069 Parameter value contains.
	 * 
	 * The value must be canonical representation as defined by XML Schema.,
	 * e.g. upper-case Hex letters in hexBinary, and no leading zeroes in
	 * numeric values.
	 * 
	 * @return value
	 */
	public String getValue() {
		// TODO canonical representation.
		return this.value;
	}

	/**
	 * Get the type this TR-069 Parameter value contains.
	 * 
	 * It must not be null and must be one of constants defined in
	 * TR069ParameterValue.
	 * 
	 * @return type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Convert the TR-069 value to a DmtData.
	 * <p>
	 * Case A: the tr069Type equals TR069ParameterValue.TR069_TYPE_BOOLEAN.
	 * <ol type="1">
	 * <li>If the metaNode supports FORMAT _BOOLEAN,
	 * <ol type="a">
	 * <li>if the value equals Åg0Åh or ÅgfalseÅh, return DmtData.FALSE_VALUE.</li>
	 * <li>if the value equals Åg1Åh or ÅgtrueÅh, return DmtData.TRUE_VALUE.</li>
	 * <li>Otherwise, IllegalArgumentException will be thrown.</li>
	 * </ol>
	 * </li>
	 * <li>Otherwise, ITR069MappingException will be thrown.</li>
	 * </ol>
	 * <p>
	 * Case B: the tr069Type equals TR069ParameterValue.TR069_TYPE_INT.
	 * <ol type="1">
	 * <li>If the metaNode supports FORMAT_INTEGER,
	 * <ol type="a">
	 * <li>if the value can be interpreted as an integer, create a new DmtData
	 * by DmtData(int) with the interpreted integer and return it.</li>
	 * <li>Otherwise, IllegalArgumentException will be thrown.</li>
	 * </ol>
	 * </li>
	 * <li>
	 * Otherwise, ITR069MappingException will be thrown.</li>
	 * </ol>
	 * <p>
	 * Case C: the tr069Type equals TR069ParameterValue.TR069_TYPE_UNSIGNED_INT.
	 * <ol type="1">
	 * <li>If the metaNode supports FORMAT_UNSIGNED_INTEGER,
	 * <ol type="a">
	 * <li>Create a new DmtData by DmtData(String, int) with the of
	 * FORMAT_UNSIGNED_INTEGER If succeeded, the DmtData will be returned.</li>
	 * <li>Otherwise, IllegalArgumentException will be thrown.</li>
	 * </ol>
	 * </li>
	 * <li>Otherwise, TR069MappingException will be thrown.</li>
	 * </ol>
	 * <p>
	 * Case D: the tr069Type equals
	 * TR069ParameterValue.TR069_TYPE_UNSIGNED_LONG.
	 * <ol type="1">
	 * <li>If the metaNode supports FORMAT_UNSIGNED_LONG,
	 * <ol type="a">
	 * <li>Create a new DmtData by DmtData(String, int) with the of
	 * FORMAT_UNSIGNED_LONG. If succeeded, the DmtData will be returned.</li>
	 * <li>Otherwise, IllegalArgumentException will be thrown.</li>
	 * </ol>
	 * </li>
	 * <li>Otherwise, TR069MappingException will be thrown.</li>
	 * </ol>
	 * <p>
	 * Case E: the tr069Type equals TR069ParameterValue.TR069_TYPE_LONG.
	 * <ol type="1">
	 * <li>If the metaNode supports FORMAT_LONG,
	 * <ol type="a">
	 * <li>Create a new DmtData by DmtData(String, int) with the of FORMAT_LONG.
	 * If succeeded, the DmtData will be returned.</li>
	 * <li>Otherwise, IllegalArgumentException will be thrown.</li>
	 * </ol>
	 * <li>Otherwise, TR069MappingException will be thrown.</li>
	 * </ol>
	 * <p>
	 * Case F: the tr069Type equals TR069ParameterValue.TR069_TYPE_DATETIME.
	 * <ol type="1">
	 * <li>If the metaNode supports FORMAT_DATETIME,
	 * <ol type="a">
	 * <li>Create a new DmtData by DmtData(String, int) with the of
	 * FORMAT_DATETIME. If succeeded, the DmtData will be returned.</li>
	 * <li>Otherwise, IllegalArgumentException will be thrown.</li>
	 * </ol>
	 * </li>
	 * <li>Otherwise, TR069MappingException will be thrown.</li>
	 * </ol>
	 * <p>
	 * Case G: the tr069Type equals TR069ParameterValue.TR069_TYPE_BASE64.
	 * <ol type="1">
	 * <li>If the metaNode supports FORMAT_BASE64, go to Step 1.a., otherwise,
	 * go to Step 2.
	 * <ol type="a">
	 * <li>The specified value will be converted into base64 byte array and
	 * create a new DmtData by DmtData(byte[], boolean) with ÅgtrueÅh as the
	 * second argument.</li>
	 * </ol>
	 * </li>
	 * <li>If the metaNode supports FORMAT_RAW_BINARY, go to Step 2.a.,
	 * otherwise, go to Step 3.
	 * <ol type="a">
	 * <li>The array of raw format names is retrieved by
	 * MetaNode.getRawFormatNames().</li>
	 * <li>If the returned array has size zero, or the first element of it is
	 * null or empty String, IllegalArgumentException will be thrown. Otherwise,
	 * go to Step 2.c</li>
	 * <li>The value will be encoded into a sequence of bytes using the
	 * character set specified. If the encoding fails, go to Step3. Otherwise,
	 * then a new DmtData will be created by DmtData(String, byte[]) with the
	 * first elements in the array of raw format names as the first argument.</li>
	 * </ol>
	 * </li>
	 * <li>TR069MappingException will be thrown.</li>
	 * </ol>
	 * <p>
	 * Case H: the tr069Type equals TR069ParameterValue.TR069_TYPE_HEXBINARY.
	 * <ol type="1">
	 * <li>If the metaNode supports FORMAT_HEX_BINARYT, go to Step 1.a.,
	 * otherwise, go to Step 2.
	 * <ol type="a">
	 * <li>The specified value will be converted into hexBinary byte array.</li>
	 * <li>A new DmtData will be created by DmtData(byte[], int) with
	 * FORMAT_HEX_BINARY as the second argument and be returned.</li>
	 * </ol>
	 * </li>
	 * <li>If the metaNode supports FORMAT_BINARY, go to Step 2.a., otherwise,
	 * IllegalArgumentException will be thrown.The value will be encoded into a
	 * sequence of bytes using the character set specified. If the encoding
	 * fails, go to Step3. Otherwise, then a new DmtData will be created by
	 * DmtData(byte[], int) with FORMAT_BINARY as the second argument and be
	 * returned.</li>
	 * <li>TR069MappingException will be thrown.</li>
	 * </ol>
	 * <p>
	 * Case I: the tr069Type equals TR069ParameterValue.TR069_TYPE_STRING.
	 * <ol type="1">
	 * <li>If the node supports FORMAT_NULL, go to Step 1.a., otherwise, go to
	 * Step 2.
	 * <ol type="a">
	 * <li>If the value is empty String, then return DmtData.NULL_VALUE.
	 * Otherwise go to Step 2.</li>
	 * </ol>
	 * </li>
	 * <li>If the node supports FORMAT_FLOAT, go to Step 2.a., otherwise, go to
	 * Step 3.
	 * <ol type="a">
	 * <li>If the parsing the value as float succeeds, then FORMAT_FLOAT is
	 * chosen. Otherwise go to Step 3.</li>
	 * </ol>
	 * </li>
	 * <li>If the node supports FORMAT_DATE, go to Step 3.a., otherwise, go to
	 * Step 4.
	 * <ol type="a">
	 * <li>If the constructing DmtData(String, int) with FORMAT_DATE as the
	 * first argument succeeds, then the DmtData will be returned. Otherwise go
	 * to Step 4.</li>
	 * </ol>
	 * </li>
	 * <li>If the node supports FORMAT_TIME, go to Step 4.a., otherwise, go to
	 * Step 5.
	 * <ol type="a">
	 * <li>If the constructing DmtData(String, int) with FORMAT_TIME as the
	 * first argument succeeds, then the DmtData will be returned. Otherwise go
	 * to Step 5.</li>
	 * </ol>
	 * </li>
	 * <li>If the node supports FORMAT_NODE_URI, go to Step 5.a., otherwise, go
	 * to Step 6.
	 * <ol type="a">
	 * <li>Translate the value to absolute URI, if the value is relative path
	 * reference, which starts with "./". The specified nodeUri is used for the
	 * translation.</li>
	 * <li>If the constructing DmtData(String, FORMAT_NODE_URI) succeeds, then
	 * the DmtData will be returned. Otherwise go to Step 6.</li>
	 * </ol>
	 * </li>
	 * <li>If the node supports FORMAT_RAW_STRING, go to Step 6.a., otherwise,
	 * go to Step 7.
	 * 
	 * <ol type="a">
	 * <li>The TR-069 Protocol Adapter gets array of raw format names by
	 * MetaNode.getRawFormatNames().</li>
	 * <li>If the returned array has size zero or the first element of it is
	 * null or empty String, go to Step 7. Otherwise,go to Step 6.c. c. The
	 * DmtData is created by DmtData(String, String) with the first element in
	 * the returned array as the first argument. Then the DmtData will be
	 * returned.</li>
	 * </ol>
	 * </li>
	 * <li>If the node supports FORMAT_STRING, go to Step 7.a., otherwise, go to
	 * Step 8. a. The DmtData is created by DmtData(String). Then the DmtData
	 * will be returned.</li>
	 * <li>If the node supports FORMAT_XML, go to Step 8.a., otherwise, go to
	 * Step 9.
	 * <ol type="a">
	 * <li>The DmtData is constructed by DmtData(String, int) with FORMAT_XML as
	 * the first argument. Then the DmtData will be returned.</li>
	 * </ol>
	 * </li>
	 * <li>IllegalArgumentException will be thrown.</li>
	 * </ol>
	 * 
	 * @param value value to be set to the specified node uri. It must not be
	 *        null and can be lexical representation as defined by XML Schema.
	 * @param tr069Type TR069 data type. It must not be null or empty.
	 * @param valueCharsetName character set to be used for the value. If null,
	 *        a platform default character set will be used. The character set
	 *        is used for getting byte array from the specified value.
	 * @param nodeUri URI of the leaf node in DMT. It must be valid absolute DMT
	 *        URI.
	 * @param metaNode Meta node of the specified node uri. It must not be null.
	 * 
	 * @return Converted DmtData. It must not be null.
	 * @throws TR069MappingException if converting DmtData is failed for some
	 *         reasons.
	 * @throws java.io.UnsupportedEncodingException if the specified character
	 *         set name is not supported.
	 * @throws java.lang.IllegalArgumetException if value is null, if tr069Type
	 *         is either null or empty, if nodeUri is invalid absolute DMT URI,
	 *         or if metaNode is null.
	 */
	public static DmtData getDmtData(final String value,
			final String tr069Type, final String valueCharsetName,
			final String nodeUri, final MetaNode metaNode)
			throws TR069MappingException, IllegalArgumentException,
			UnsupportedEncodingException {

		checkArguments(value, tr069Type, nodeUri, metaNode);

		// TODO update the code according to the javadoc.
		final int format = metaNode.getFormat();
		DmtData data = null;
		if (tr069Type.equals(TR069_TYPE_BASE64)) {
			if ((format & DmtData.FORMAT_BASE64) != 0) {
				byte[] bytes = Base64.base64ToByteArray(value);
				data = new DmtData(bytes, true);
			}
			else {
				if ((format & DmtData.FORMAT_RAW_BINARY) != 0) {
					String[] formatNames = metaNode.getRawFormatNames();
					if (formatNames != null && formatNames.length > 0) {
						byte[] bytes = value.getBytes(valueCharsetName);
						data = new DmtData(formatNames[0], bytes);
					}
					else {
						errorInSetParameterValues("Despite of TR069 dataType="
								+ tr069Type
								+ " and FORMAT_RAW_BINARY, no RawFormatNames.");
					}
				}
				else
					errorInSetParameterValues("Despite of TR069 dataType="
							+ tr069Type
							+ ", FORMAT is neither FORMAT_BASE64 nor FORMAT_RAW_BINARY.");
			}
			return data;
		}
		if (tr069Type.equals(TR069_TYPE_BOOLEAN)) {
			if ((format & DmtData.FORMAT_BOOLEAN) != 0) {
				if ("0".equals(value))
					data = DmtData.FALSE_VALUE;
				else
					if ("1".equals(value))
						data = DmtData.TRUE_VALUE;
					else
						errorInSetParameterValues("Despite of TR069 dataType="
								+ tr069Type + "and FORMAT_BOOLEAN, value(="
								+ value + ") is neither \"0\" nor \"1\".");
			}
			else
				errorInSetParameterValues("Despite of TR069 dataType="
						+ tr069Type + ", FORMAT is not FORMAT_BOOLEAN.");
			return data;
		}
		if (tr069Type.equals(TR069_TYPE_INT)) {
			if ((format & DmtData.FORMAT_INTEGER) != 0) {
				try {
					int valueInt = Integer.parseInt(value);
					data = new DmtData(valueInt);
				}
				catch (Exception e) {
					errorInSetParameterValues("Despite of TR069 dataType="
							+ tr069Type + "and FORMAT_INTEGER, value(=" + value
							+ ") cannot be parsed. " + e);
				}
			}
			else
				errorInSetParameterValues("Despite of TR069 dataType="
						+ tr069Type + ", FORMAT is not FORMAT_INTEGER.");
			return data;
		}
		if (tr069Type.equals(TR069_TYPE_STRING)) {
			StringBuffer sb = new StringBuffer();
			if ((value == null || value.length() == 0)
					&& (format & DmtData.FORMAT_NULL) != 0) {
				data = DmtData.NULL_VALUE;
			}
			if (data == null && (format & DmtData.FORMAT_FLOAT) != 0) {
				try {
					float valueFloat = Float.parseFloat(value);
					data = new DmtData(valueFloat);
				}
				catch (Exception e) {
					sb.append("\n");
					sb.append("Although the format of the node supports FORMAT_FLOAT, Fail to construct DmtData. "
							+ e.toString());
				}
			}
			if (data == null && (format & DmtData.FORMAT_DATE) != 0) {
				try {
					data = new DmtData(value, DmtData.FORMAT_DATE);
				}
				catch (Exception e) {
					sb.append("\n");
					sb.append("Although the format of the node supports FORMAT_DATE, Fail to construct DmtData. "
							+ e.toString());
				}
			}
			if (data == null && (format & DmtData.FORMAT_TIME) != 0) {
				try {
					data = new DmtData(value, DmtData.FORMAT_TIME);
				}
				catch (Exception e) {
					sb.append("\n");
					sb.append("Although the format of the node supports FORMAT_TIME, Fail to construct DmtData. "
							+ e.toString());
				}
			}
			if (data == null && (format & DmtData.FORMAT_NODE_URI) != 0) {
				if (TR069URI.isValidTR069Path(value)) {
					String valueUri = TR069URI.getDmtUri(TR069URI
							.getTR069AbsolutePath(
									TR069URI.getTR069Path(nodeUri), value));

					try {
						data = new DmtData(valueUri, DmtData.FORMAT_NODE_URI);
					}
					catch (Exception e) {
						sb.append("\n");
						sb.append("Although the format of the node supports FORMAT_NODE_URI, Fail to construct DmtData. "
								+ e.toString());
					}
				}
			}
			if (data == null && (format & DmtData.FORMAT_RAW_STRING) != 0) {
				try {
					String[] names = metaNode.getRawFormatNames();
					if (names != null && names.length > 0 && names[0] != null
							&& (names[0].length() != 0)) {
						data = new DmtData(names[0], value);
					}
				}
				catch (Exception e) {
					sb.append("\n");
					sb.append("Although the format of the node supports FORMAT_RAW_STRING, Fail to construct DmtData. "
							+ e.toString());
				}
			}
			if (data == null && (format & DmtData.FORMAT_STRING) != 0) {
				data = new DmtData(value);
			}
			if (data == null && (format & DmtData.FORMAT_XML) != 0) {
				data = new DmtData(value, DmtData.FORMAT_XML);
			}
			if (data == null)
				errorInSetParameterValues("Despite of TR069 dataType="
						+ tr069Type
						+ ", proper FORMAT is not supported by the node. "
						+ sb.toString());
			return data;
		}

		if (tr069Type.equals(TR069_TYPE_UNSIGNED_INT)) {
			if ((format & DmtData.FORMAT_UNSIGNED_INTEGER) != 0) {
				try {
					data = new DmtData(value, DmtData.FORMAT_UNSIGNED_INTEGER);
				}
				catch (Exception e) {
					errorInSetParameterValues("Despite of TR069 dataType="
							+ tr069Type
							+ "and FORMAT_UNSIGNED_INTEGER, value(=" + value
							+ ") is not in appropriate format." + e);
				}
			}
			else
				errorInSetParameterValues("Despite of TR069 dataType="
						+ tr069Type
						+ ", FORMAT is not FORMAT_UNSIGNED_INTEGER.");
			return data;
		}
		if (tr069Type.equals(TR069_TYPE_LONG)) {
			if ((format & DmtData.FORMAT_LONG) != 0) {
				try {
					long valueLong = Long.parseLong(value);
					data = new DmtData(valueLong);
				}
				catch (Exception e) {
					errorInSetParameterValues("Despite of TR069 dataType="
							+ tr069Type + "and FORMAT_LONG, value(=" + value
							+ ") cannot be parsed. " + e);
				}
			}
			else
				errorInSetParameterValues("Despite of TR069 dataType="
						+ tr069Type + ", FORMAT is not FORMAT_LONG.");
			return data;
		}
		if (tr069Type.equals(TR069_TYPE_UNSIGNED_LONG)) {
			if ((format & DmtData.FORMAT_UNSIGNED_LONG) != 0) {
				try {
					data = new DmtData(value, DmtData.FORMAT_UNSIGNED_LONG);
				}
				catch (Exception e) {
					errorInSetParameterValues("Despite of TR069 dataType="
							+ tr069Type + "and FORMAT_UNSIGNED_LONG, value(="
							+ value + ") is not in appropriate format." + e);
				}
			}
			else
				errorInSetParameterValues("Despite of TR069 dataType="
						+ tr069Type
						+ ", FORMAT is not FORMAT_UNSIGNED_INTEGER.");
			return data;
		}
		if (tr069Type.equals(TR069_TYPE_DATETIME)) {
			if ((format & DmtData.FORMAT_DATETIME) != 0) {
				String target = getDmtDataCompatibleValue(value);
				// https://www.osgi.org/members/bugzilla/show_bug.cgi?id=1938#c8
				try {
					data = new DmtData(target, DmtData.FORMAT_DATETIME);
				}
				catch (Exception e) {
					errorInSetParameterValues("Despite of TR069 dataType="
							+ tr069Type + "and FORMAT_DATETIME, value(="
							+ value + ") is not in appropriate format." + e);
				}
			}
			else
				errorInSetParameterValues("Despite of TR069 dataType="
						+ tr069Type + ", FORMAT is not FORMAT_DATETIME.");
			return data;
		}
		if (tr069Type.equals(TR069_TYPE_HEXBINARY)) {
			StringBuffer sb = new StringBuffer();
			if (data == null && (format & DmtData.FORMAT_HEXBINARY) != 0) {
				try {
					byte[] bytes = HexBinary.hexBinaryToByteArray(value);
					data = new DmtData(bytes, DmtData.FORMAT_HEXBINARY);
				}
				catch (Exception e) {
					sb.append("\n");
					sb.append("Although the format of the node supports FORMAT_HEXBINARY, Fail to construct DmtData. "
							+ e.toString());
				}
			}
			if (data == null && (format & DmtData.FORMAT_BINARY) != 0) {
				try {
					byte[] bytes = value.getBytes(valueCharsetName);
					data = new DmtData(bytes, DmtData.FORMAT_BINARY);
				}
				catch (Exception e) {
					sb.append("\n");
					sb.append("Although the format of the node supports FORMAT_HEXBINARY, Fail to construct DmtData. "
							+ e.toString());
				}
			}
			if (data == null)
				errorInSetParameterValues("Despite of TR069 dataType="
						+ tr069Type
						+ ", proper FORMAT is not supported by the node." + sb);
		}

		return data;
	}

	// public static void main(String[] args) {
	//
	// String target = "-Device-Services-Hoge-a";
	// System.out.println("target=[" + target + "]");
	// String ret = getDmtDataCompatibleValue(target);
	// System.out.println("ret=[" + ret + "]");
	// }

	/**
	 * Convert the specified value to the String which is compatible to
	 * DmtData.FORMAT_DATE.
	 * 
	 * Just remove hyphens '-'.
	 * 
	 * @param value String to be converted
	 * @return converted String
	 */
	private static String getDmtDataCompatibleValue(String value) {
		StringBuffer sb = new StringBuffer(value.length());
		int position = -1;
		int beginIndex = 0;
		while (true) {
			position = value.indexOf('-');
			if (position == -1) {
				sb.append(value);
				break;
			}
			if (position != 0)
				sb.append(value.substring(beginIndex, position));
			value = value.substring(position + 1);
		}
		return sb.toString();
	}

	/**
	 * Private method for checking arguments.
	 * 
	 * @param value
	 * @param tr069Type
	 * @param nodeUri
	 * @param metaNode
	 * @throws IllegalArgumentException if value is null, if tr069Type is either
	 *         null or empty, if nodeUri is invalid absolute DMT URI, or if
	 *         metaNode is null.
	 */
	private static void checkArguments(String value, String tr069Type,
			String nodeUri, MetaNode metaNode) throws IllegalArgumentException {
		String errmsg = null;
		if (value == null)
			errmsg = "value must not be null:" + value;
		else
			if (tr069Type == null)
				errmsg = "tr069Type must not be null:" + tr069Type;
			else
				if (tr069Type.length() == 0)
					errmsg = "tr069Type must not be empty:" + tr069Type;
				else
					if (!Uri.isValidUri(nodeUri))
						errmsg = "nodeUri must be valid URI:" + nodeUri;
					else
						if (!Uri.isAbsoluteUri(nodeUri))
							errmsg = "nodeUri must be absolute URI:" + nodeUri;
						else
							if (metaNode == null)
								errmsg = "metaNode must not be null.";
		if (errmsg != null)
			throw new IllegalArgumentException(errmsg);
	}

	private static void errorInSetParameterValues(String string)
			throws TR069MappingException {
		throw new TR069MappingException(string);

	}

	/**
	 * Convert the TR-069 value to a DmtData array for a list subtree.
	 * <p>
	 * The node specified by the node uri must be interior node which has the
	 * node type of {@value info.dmtree.spi.DMTConstants#DDF_LIST_SUBTREE}.
	 * <p>
	 * The value must be parsed by using a separator of comma. The each item
	 * parsed must be unescaped as specified in TR-106 and unescaped item will
	 * be converted into a DmtData. For the conversion,
	 * {@code TR069ParameterValue.getDmtData()} can be used. The returned array
	 * will have the converted DmtData for each item.
	 * 
	 * 
	 * @param value value to be set to the specified interior node. It must not
	 *        be null and can be lexical representation as defined by XML
	 *        Schema.
	 * @param tr069Type TR069 data type. It must not be null or empty.
	 * @param valueCharsetName character set to be used for the value. If null,
	 *        a platform default character set will be used. The character set
	 *        is used for getting byte array from the specified value.
	 * @param nodeUri URI of the interior node in DMT. It must be valid absolute
	 *        DMT URI. *
	 * @param metaNode Meta node of the child leaf node of the specified node
	 *        uri. It must not be null. Returns: array of DmtData to be used for
	 *        DmtSession#setNodeValue() against the child nodes of the specified
	 *        node uri. It can be empty array but cannot be null.
	 * @return Converted array of DmtData. It can be empty array but must not be
	 *         null.
	 * @throws TR069MappingException if convertionng DmtData is failed for some
	 *         reasons.
	 * @throws java.io.UnsupportedEncodingException if the specified character
	 *         set name is not supported.
	 * @throws java.lang.IllegalArgumetException if value is {@code null}, if
	 *         value contains empty element in the comma-separated list, if
	 *         tr069Type is either {@code null} or empty, if nodeUri is invalid
	 *         absolute DMT URI, or if metaNode is {@code null}.
	 * 
	 */
	public static DmtData[] getDmtDataForList(String value, String tr069Type,
			String valueCharsetName, String nodeUri, MetaNode metaNode)
			throws TR069MappingException, IllegalArgumentException,
			UnsupportedEncodingException {

		checkArguments(value, tr069Type, nodeUri, metaNode);
		if (value.indexOf(",,") != -1)
			throw new IllegalArgumentException("value must not contain \",,\":"
					+ value);

		StringTokenizer tokenizer = new StringTokenizer(value, ",", false);
		int count = tokenizer.countTokens();
		DmtData[] datas = new DmtData[count];
		for (int i = 0; i < count; i++) {
			String token = tokenizer.nextToken().trim();
			String uri = nodeUri + Uri.PATH_SEPARATOR + (i + 1);
			String valueString = escapeDecode(token);
			datas[i] = TR069ParameterValue.getDmtData(valueString, tr069Type,
					valueCharsetName, uri, metaNode);
		}
		return datas;
	}

	/**
	 * Convert the array of DmtData to a TR-069 value for a list subtree.
	 * <p>
	 * 
	 * <ol type="1">
	 * <li>For each item of the specified array, the following will be done:
	 * <ol type="a">
	 * <li>Convert the DmtData object to a TR069ParameterValue according to the
	 * rules in . gtTR069ParameterValue(DmtData).</li>
	 * 
	 * <li>Retrieve the value as String from the converted TR069ParameterValue.</li>
	 * <li>Escaping must be done as specified by TR-106.</li>
	 * </ol>
	 * </li>
	 * <li>Creating the comma-separated list by concatenating the retrieved
	 * values in the order of the appearance of the array.</li>
	 * <li>Construct new TR069ParameterValue with ÅgstringÅh and the created
	 * comma-separated list, and return it.</li>
	 * </ol>
	 * <p>
	 * This method does not check whether the all items of the specified array
	 * have the same format.
	 * 
	 * @param data array of DmtData retrieved from DMT.
	 * @return Converted TR-069 value for list subtree.
	 */
	public static TR069ParameterValue getTR069ParameterValueForList(
			DmtData[] data) {
		StringBuffer sb = null;
		for (int i = 0; i < data.length; i++) {
			if (sb == null)
				sb = new StringBuffer();

			if (i != 0)
				sb.append(",");
			sb.append(escapeEncode(data[i].toString()));
		}

		return new TR069ParameterValue((sb == null) ? "" : sb.toString(),
				TR069_TYPE_STRING);
	}

	/**
	 * Convert the DmtData to a TR069 value.
	 * 
	 * <ol type="1">
	 * <li>In case that the format of the data is FORMAT_BASE64,
	 * <ol type="a">
	 * <li>The byte array returned by DmtData.getBase64() will be converted into
	 * base64. The result of the conversion will be used as the value.</li>
	 * <li>Construct new TR069ParameterValue with
	 * TR069ParameterValue.TR069_TYPE_BASE64 and the value, and return it.</li>
	 * </ol>
	 * </li>
	 * <li>In case that the format of the data is FORMAT_BINARY,
	 * <ol type="a">
	 * <li>The byte array returned by DmtData.getBinary() will be converted into
	 * hexBinary in canonical representation as defined in XML Schema. The
	 * result of the conversion will be used as the value.</li>
	 * <li>Construct new TR069ParameterValue with
	 * TR069ParameterValue.TR069_TYPE_HEXBINARY and the value, and return it.</li>
	 * </ol>
	 * </li>
	 * <li>In case that the format of the data is FORMAT_HEXBINARY,
	 * <ol type="a">
	 * <li>The byte array returned by DmtData.getHexBinary() will be converted
	 * into hexBinary in canonical representation as defined in XML Schema. The
	 * result of the conversion will be used as the value.</li>
	 * <li>Construct new TR069ParameterValue with
	 * TR069ParameterValue.TR069_TYPE_HEXBINARY and the value, and return it.</li>
	 * </ol>
	 * </li>
	 * <li>In case that the format of the data is FORMAT_BOOLEAN,
	 * <ol type="a">
	 * <li>If DmtData.getBoolean() returns Boolean.TRUE or the DmtData equals
	 * DmtData.TRUE_VALUE, the value will be Åg1Åh. Otherwise the value will be
	 * Åg0Åh.</li>
	 * <li>Construct new TR069ParameterValue with
	 * TR069ParameterValue.TR069_TYPE_BOOLEAN and the value, and return it.</li>
	 * </ol>
	 * </li>
	 * <li>In case that the format of the data is FORMAT_DATE,
	 * <ol type="a">
	 * <li>DmtData.toString() or DmtData.getDate() will be used as the value.</li>
	 * <li>Construct new TR069ParameterValue with
	 * TR069ParameterValue.TR069_TYPE_STRING and the value, and return it.</li>
	 * </ol>
	 * </li>
	 * <li>In case that the format of the data is any of FORMAT_FLOAT,
	 * FORMAT_INTEGER, FORMAT_LONG, and FORMAT_NULL,
	 * <ol type="a">
	 * <li>DmtData.toString() will be used as the value.</li>
	 * <li>Construct new TR069ParameterValue with
	 * TR069ParameterValue.TR069_TYPE_STRING and the value, and return it.</li>
	 * </ol>
	 * </li>
	 * <li>In case that the format of the data is FORMAT_STRING.
	 * <ol type="a">
	 * <li>DmtData.toString() or DmtData.getString() will be used as the value.</li>
	 * <li>Construct new TR069ParameterValue with
	 * TR069ParameterValue.TR069_TYPE_STRING and the value, and return it.</li>
	 * </ol>
	 * </li>
	 * <li>In case that the format of the data is FORMAT_RAW_STRING,
	 * <ol type="a">
	 * <li>DmtData.toString() or DmtData.getRawString() will be used as the
	 * value.</li>
	 * <li>Construct new TR069ParameterValue with
	 * TR069ParameterValue.TR069_TYPE_STRING and the value, and return it.</li>
	 * </ol>
	 * </li>
	 * <li>In case that the format of the data is FORMAT_INTEGER,
	 * <ol type="a">
	 * <li>DmtData.toString() will be used as the value.</li>
	 * <li>Construct new TR069ParameterValue with
	 * TR069ParameterValue.TR069_TYPE_INT and the value, and return it.</li>
	 * </ol>
	 * </li>
	 * <li>In case that the format of the data is FORMAT_LONG,
	 * <ol type="a">
	 * <li>DmtData.toString() will be used as the value.</li>
	 * <li>Construct new TR069ParameterValue with
	 * TR069ParameterValue.TR069_TYPE_LONG and the value and return it.</li>
	 * </ol>
	 * </li>
	 * <li>In case that the format of the data is FORMAT_TIME,
	 * <ol type="a">
	 * <li>DmtData.toString() or DmtData.geetTime() will be used as the value.</li>
	 * <li>Construct new TR069ParameterValue with
	 * TR069ParameterValue.TR069_TYPE_STRING and the value and return it.</li>
	 * </ol>
	 * </li>
	 * <li>In case that the format of the data is FORMAT_XML,
	 * <ol type="a">
	 * <li>DmtData.toString() or DmtData.getXml() will be used as the value.</li>
	 * <li>Construct new TR069ParameterValue with
	 * TR069ParameterValue.TR069_TYPE_STRING and the value and return it.</li>
	 * </ol>
	 * </li>
	 * <li>In case that the format of the data is FORMAT_UNSIGNED_INTEGER,
	 * <ol type="a">
	 * <li>DmtData.toString() or DmtData.getUnsignedInteger() will be used as
	 * the value.</li>
	 * <li>Construct new TR069ParameterValue with
	 * TR069ParameterValue.TR069_TYPE_UNSIGNED_INT and the value, and return it.
	 * </li>
	 * </ol>
	 * </li>
	 * <li>In case that the format of the data is FORMAT_UNSIGNED_LONG,
	 * <ol type="a">
	 * <li>DmtData.toString() or DmtData.getUnsignedLong() will be used as the
	 * value.</li>
	 * <li>Construct new TR069ParameterValue with
	 * TR069ParameterValue.TR069_TYPE_UNSIGNED_LONG and the value and return it.
	 * </li>
	 * </ol>
	 * </li>
	 * <li>In case that the format of the data is FORMAT_DATETIME,
	 * <ol type="a">
	 * <li>DmtData.toString() or DmtData.getDateTime() will be used as the
	 * value.</li>
	 * <li>Construct new TR069ParameterValue with
	 * TR069ParameterValue.TR069_TYPE_DATETIME and the value, and return it.</li>
	 * </ol>
	 * </li>
	 * <li>In case that the format of the data is FORMAT_NODE_URI,
	 * <ol type="a">
	 * <li>The returned String by DmtData.toString() or DmtData.getNodeUri()
	 * will be translated into TR-069Path. The result of the translation will be
	 * used as value.</li>
	 * <li>Construct new TR069ParameterValue with
	 * TR069ParameterValue.TR069_TYPE_STRING and the value, and return it.</li>
	 * </ol>
	 * 
	 * @param data DmtData retrieved from DMT.
	 * @return Converted TR069 value.
	 */
	public static TR069ParameterValue getTR069ParameterValue(DmtData data) {
		int format = data.getFormat();
		String value = null;
		switch (format) {
			case DmtData.FORMAT_BASE64 :
				value = Base64.byteArrayToBase64(data.getBase64());
				return new TR069ParameterValue(value, TR069_TYPE_BASE64);
			case DmtData.FORMAT_BINARY :
				value = getHexDumpWithoutSpace(data.getBinary());
				return new TR069ParameterValue(value, TR069_TYPE_HEXBINARY);
			case DmtData.FORMAT_BOOLEAN :
				value = (data.getBoolean() ? "1" : "0");
				return new TR069ParameterValue(value, TR069_TYPE_BOOLEAN);
			case DmtData.FORMAT_DATE :
			case DmtData.FORMAT_FLOAT :
			case DmtData.FORMAT_NULL :
			case DmtData.FORMAT_RAW_STRING :
			case DmtData.FORMAT_STRING :
			case DmtData.FORMAT_TIME :
			case DmtData.FORMAT_XML :
				return new TR069ParameterValue(data.toString(),
						TR069_TYPE_STRING);
			case DmtData.FORMAT_INTEGER :
				return new TR069ParameterValue(data.toString(), TR069_TYPE_INT);
			case DmtData.FORMAT_NODE :
				throw new IllegalArgumentException("The format is FORMAT_NODE.");
			case DmtData.FORMAT_RAW_BINARY :
				value = Base64.byteArrayToBase64(data.getRawBinary());
				return new TR069ParameterValue(value, TR069_TYPE_BASE64);
			case DmtData.FORMAT_UNSIGNED_INTEGER :
				return new TR069ParameterValue(data.toString(),
						TR069_TYPE_UNSIGNED_INT);
			case DmtData.FORMAT_LONG :
				return new TR069ParameterValue(data.toString(), TR069_TYPE_LONG);
			case DmtData.FORMAT_UNSIGNED_LONG :
				return new TR069ParameterValue(data.toString(),
						TR069_TYPE_UNSIGNED_LONG);
			case DmtData.FORMAT_HEXBINARY :
				value = getHexDumpWithoutSpace(data.getHexBinary());
				return new TR069ParameterValue(value, TR069_TYPE_HEXBINARY);
			case DmtData.FORMAT_NODE_URI :
				value = TR069URI.getTR069Path(data.toString());
				return new TR069ParameterValue(value, TR069_TYPE_STRING);
			default :
				break;
		}

		return null;
	}

	private static String escapeEncode(String target) {
		String encoded1 = replaceString(target, '%', "%25");
		// System.out.println("encoded1\t:" + encoded1);
		String encoded2 = replaceString(encoded1, ',', "%2C");
		return encoded2;
	}

	/**
	 * Replace all specified char into the specified String in the specified
	 * target.
	 * 
	 * @param msg original message string
	 * @param ch character, which is replaced by the specified replacement.
	 * @param replacement String, which will replace the specified target
	 *        character.
	 * @return resulting String
	 */
	private static String replaceString(String message, char ch,
			String replacement) {
		if (null == message) {
			return null;
		}
		char[] messageChars = message.toCharArray();
		StringBuffer result = new StringBuffer();
		int nextToInsert = 0;
		for (int i = 0; i < messageChars.length; i++) {
			if (ch == messageChars[i]) {
				result.append(messageChars, nextToInsert, i - nextToInsert);
				nextToInsert = i + 1;
				result.append(replacement);
			}
		}
		if (nextToInsert < messageChars.length) {
			result.append(messageChars, nextToInsert, messageChars.length
					- nextToInsert);
		}
		return result.toString();
	}

	private static String escapeDecode(String target) {
		String encoded1 = replaceString(target, "%2C", ',');
		String encoded2 = replaceString(encoded1, "%25", '%');
		return encoded2;
	}

	/**
	 * Replace all specified String into the specified char in the specified
	 * target.
	 * 
	 * @param msg original message string
	 * @param target string, which is replaced by the specified replacement.
	 * @param replacement character, which will replace the specified target
	 *        string.
	 * @return resulting String
	 */
	private static String replaceString(String message, String target,
			char replacement) {
		if (debug)
			System.out.println("message:" + message + "\t,target:" + target
					+ "\t,replacement=" + replacement);
		if (null == message) {
			return null;
		}
		char[] messageChars = message.toCharArray();
		char[] targetChars = target.toCharArray();
		StringBuffer result = new StringBuffer();
		int nextToInsert = 0;
		for (int i = 0; i < messageChars.length; i++) {
			boolean match = true;
			for (int j = 0; j < targetChars.length; j++) {
				if (messageChars[i + j] != targetChars[j]) {
					match = false;
					break;
				}
			}
			if (match) {
				result.append(messageChars, nextToInsert, i - nextToInsert);
				nextToInsert = i + targetChars.length;
				result.append(replacement);
			}
		}
		if (nextToInsert < messageChars.length) {
			result.append(messageChars, nextToInsert, messageChars.length
					- nextToInsert);
		}
		if (debug)
			System.out.println("result\t:" + result.toString());
		return result.toString();
	}

	// character array of hexadecimal digits, used for printing binary data
	private static char[]	hex	= "0123456789ABCDEF".toCharArray();

	// generates a hexadecimal dump of the given binary data
	private static String getHexDumpWithoutSpace(byte[] bytes) {
		if (bytes.length == 0)
			return "";

		StringBuffer buf = new StringBuffer();
		appendHexByte(buf, bytes[0]);
		for (int i = 1; i < bytes.length; i++)
			appendHexByte(buf, bytes[i]);

		return buf.toString();
	}

	private static void appendHexByte(StringBuffer buf, byte b) {
		buf.append(hex[(b & 0xF0) >> 4]).append(hex[b & 0x0F]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		if (!(object instanceof TR069ParameterValue))
			return false;
		TR069ParameterValue target = (TR069ParameterValue) object;
		if (!target.type.equals(type))
			return false;

		// TODO regarding value, might need to consider canonical and
		// representational
		if (!target.value.equals(value))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return type.hashCode() + value.hashCode();
	}

}

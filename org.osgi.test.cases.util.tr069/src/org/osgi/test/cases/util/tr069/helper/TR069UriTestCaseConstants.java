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

package org.osgi.test.cases.util.tr069.helper;

import info.dmtree.DmtData;
import info.dmtree.Uri;

/**
 * A container class for the test constants.
 * 
 * @author Evgeni Grigorov, e.grigorov@prosyst.com
 */
public abstract class TR069UriTestCaseConstants {

	private TR069UriTestCaseConstants() {
		/* prevent the inheritance */
	}

	// TODO: remove after:
	// https://www.osgi.org/members/bugzilla/show_bug.cgi?id=1866#c4
	// The node uri path separator and tree root can be defined as constants
	private static final String		DMT_PATH_SEPARATOR				= "/";

	private static final String		TR069_PATH_OSGI_EXT				= "X_OSGI-ORG_OSGiSpecificName";

	private static final String		DEVICE							= "Device";
	private static final String		SERVICES						= "Services";

	/** Test instance number integer constant. The constant value is {@value}. */
	public static final int			TR069_INSTANCE_NUMBER			= 101;
	/** The string representation of {@link #TR069_INSTANCE_NUMBER} */
	public static final String		TR069_INSTANCE_NUMBER_STRING	= String.valueOf(TR069_INSTANCE_NUMBER);

	/** Test string constant with value {@value}. */
	public static final String		TR069_VALUE_1					= "1";
	/** Test string constant with value {@value}. */
	public static final String		TR069_VALUE_0					= "0";
	/** Test string constant with value {@value}. */
	public static final String		TR069_VALUE_TRUE				= "true";
	/** Test string constant with value {@value}. */
	public static final String		TR069_VALUE_FALSE				= "false";

	/** Test string constant with value {@value}. */
	public static final String		TR069_VALUE_BIN_STRING			= "you";

	/**
	 * Test string constant with value {@value}. It contains the base64 encoded
	 * {@link #TR069_VALUE_BIN_STRING}
	 */
	public static final String		TR069_VALUE_BASE64_STRING		= "eW91";

	// TODO: remove spaces after the fix of
	// https://www.osgi.org/members/bugzilla/show_bug.cgi?id=1946
	// [PA Utility Classes] getTR069ParameterValue uses hex dump instead of hex
	// sequence
	/**
	 * Test string constant with value {@value}. It represents
	 * {@link #TR069_VALUE_BIN_STRING} in hex format.
	 */
	public static final String		TR069_VALUE_HEX_STRING			= "796F75";
	public static final String		TR069_VALUE_HEX_DUMP_STRING		= "79 6F 75";

	/** Test string constant with value {@value}. */
	public static final String		TR069_VALUE_DATE				= "20110115";
	/** Test string constant with value {@value}. */
	public static final String		TR069_VALUE_TIME				= "000000";
	/** Test string constant with value {@value}. */
	public static final String		TR069_VALUE_DATE_TIME			= TR069_VALUE_DATE
																			+ 'T'
																			+ TR069_VALUE_TIME;

	/** Test string constant with value {@value}. */
	public static final String		CHARSET_UTF8					= "UTF-8";

	/** Test string constant with value {@value}. */
	public static final String		FORMAT_OSGi_BIN					= "osgiBin";

	/** An empty string constant. */
	public static final String		EMPTY_STRING					= "";
	/** A string contains the point character. */
	public static final String		POINT_STRING					= ".";

	/** Test string constant with value {@value}. */
	public static final String		TR069_PATH_MIDDLE_POINTS		= "Device..Services";

	/** Test string constant with value {@value}. */
	public static final String		TR069_PATH_FULL					= DEVICE
																			+ POINT_STRING
																			+ SERVICES;
	/** Test string constant with value {@value}. */
	public static final String		TR069_PATH_PARTIAL				= TR069_PATH_FULL
																			+ POINT_STRING;
	/** Test string constant with value {@value}. */
	public static final String		TR069_PATH_EXT					= TR069_PATH_PARTIAL
																			+ TR069_PATH_OSGI_EXT;
	/** Test string constant with value {@value}. */
	public static final String		TR069_PATH_INSTANCE				= TR069_PATH_PARTIAL
																			+ TR069_INSTANCE_NUMBER;
	/** Test string constant with value {@value}. */
	public static final String		TR069_PATH_STARTING_UNDERSCORE	= '_' + TR069_PATH_FULL;

	/** Test string constant with value {@value}. */
	public static final String		TR069_PATH_STARTING_DIGIT		= TR069_INSTANCE_NUMBER
																			+ TR069_PATH_FULL;
	/** Test string constant with value {@value}. */
	public static final String		TR069_PATH_STARTING_HYPHEN		= '-' + TR069_PATH_FULL;

	/** Test string constant with value {@value}. */
	public static final String		TR069_PATH_STARTING_POINT		= POINT_STRING
																			+ TR069_PATH_FULL;

	/** Test string constant with value {@value}. */
	public static final String		DMT_URI_FULL					= Uri.toUri(new String[] {
			POINT_STRING, DEVICE, SERVICES							});
	/** Test string constant with value {@value}. */
	public static final String		DMT_URI_INSTANCE				= DMT_URI_FULL
																			+ DMT_PATH_SEPARATOR
																			+ TR069_INSTANCE_NUMBER;
	/** Test string constant with value {@value}. */
	public static final String		DMT_URI_EXT						= DMT_URI_FULL
																			+ DMT_PATH_SEPARATOR
																			+ TR069_PATH_OSGI_EXT;
	/** An array of integer DmtData instances. */
	public static final DmtData[]	DMT_DATA_ARRAY_INT				= new DmtData[] {
			new DmtData(1), new DmtData(2), new DmtData(3)			};
	/** An array of string DmtData instances. */
	public static final DmtData[]	DMT_DATA_ARRAY_STRING			= new DmtData[] {
			new DmtData("1"), new DmtData("2"), new DmtData("3")	};
	/** An array of node Uri DmtData instances. */
	public static final DmtData[]	DMT_DATA_ARRAY_NODE_URI			= new DmtData[] {
			new DmtData("./1", DmtData.FORMAT_NODE_URI),
			new DmtData("./2", DmtData.FORMAT_NODE_URI),
			new DmtData("./3", DmtData.FORMAT_NODE_URI)				};

	/**
	 * The base char ranges are a part of valid TR-069 path name letters. They
	 * are collected from Appendix B of the XML specification according
	 * definition in TR-106. The number of elements is even, where
	 * <code>i</code>-th element is the starting element of the range. The
	 * <code>(i+1)</code>-th element is the last element in the range. All
	 * elements in the range are between those two.
	 */
	public static final char[]		TR069_PATH_BASE_CHAR_RANGES		= new char[] {
			0x0041, 0x005A, 0x0061, 0x007A, 0x00C0, 0x00D6, 0x00D8, 0x00F6,
			0x00F8, 0x00FF, 0x0100, 0x0131, 0x0134, 0x013E, 0x0141, 0x0148,
			0x014A, 0x017E, 0x0180, 0x01C3, 0x01CD, 0x01F0, 0x01F4, 0x01F5,
			0x01FA, 0x0217, 0x0250, 0x02A8, 0x02BB, 0x02C1, 0x0386, 0x0386,
			0x0388, 0x038A, 0x038C, 0x038C, 0x038E, 0x03A1, 0x03A3, 0x03CE,
			0x03D0, 0x03D6, 0x03DA, 0x03DA, 0x03DC, 0x03DC, 0x03DE, 0x03DE,
			0x03E0, 0x03E0, 0x03E2, 0x03F3, 0x0401, 0x040C, 0x040E, 0x044F,
			0x0451, 0x045C, 0x045E, 0x0481, 0x0490, 0x04C4, 0x04C7, 0x04C8,
			0x04CB, 0x04CC, 0x04D0, 0x04EB, 0x04EE, 0x04F5, 0x04F8, 0x04F9,
			0x0531, 0x0556, 0x0559, 0x0559, 0x0561, 0x0586, 0x05D0, 0x05EA,
			0x05F0, 0x05F2, 0x0621, 0x063A, 0x0641, 0x064A, 0x0671, 0x06B7,
			0x06BA, 0x06BE, 0x06C0, 0x06CE, 0x06D0, 0x06D3, 0x06D5, 0x06D5,
			0x06E5, 0x06E6, 0x0905, 0x0939, 0x093D, 0x093D, 0x0958, 0x0961,
			0x0985, 0x098C, 0x098F, 0x0990, 0x0993, 0x09A8, 0x09AA, 0x09B0,
			0x09B2, 0x09B2, 0x09B6, 0x09B9, 0x09DC, 0x09DD, 0x09DF, 0x09E1,
			0x09F0, 0x09F1, 0x0A05, 0x0A0A, 0x0A0F, 0x0A10, 0x0A13, 0x0A28,
			0x0A2A, 0x0A30, 0x0A32, 0x0A33, 0x0A35, 0x0A36, 0x0A38, 0x0A39,
			0x0A59, 0x0A5C, 0x0A5E, 0x0A5E, 0x0A72, 0x0A74, 0x0A85, 0x0A8B,
			0x0A8D, 0x0A8D, 0x0A8F, 0x0A91, 0x0A93, 0x0AA8, 0x0AAA, 0x0AB0,
			0x0AB2, 0x0AB3, 0x0AB5, 0x0AB9, 0x0ABD, 0x0ABD, 0x0AE0, 0x0AE0,
			0x0B05, 0x0B0C, 0x0B0F, 0x0B10, 0x0B13, 0x0B28, 0x0B2A, 0x0B30,
			0x0B32, 0x0B33, 0x0B36, 0x0B39, 0x0B3D, 0x0B3D, 0x0B5C, 0x0B5D,
			0x0B5F, 0x0B61, 0x0B85, 0x0B8A, 0x0B8E, 0x0B90, 0x0B92, 0x0B95,
			0x0B99, 0x0B9A, 0x0B9C, 0x0B9C, 0x0B9E, 0x0B9F, 0x0BA3, 0x0BA4,
			0x0BA8, 0x0BAA, 0x0BAE, 0x0BB5, 0x0BB7, 0x0BB9, 0x0C05, 0x0C0C,
			0x0C0E, 0x0C10, 0x0C12, 0x0C28, 0x0C2A, 0x0C33, 0x0C35, 0x0C39,
			0x0C60, 0x0C61, 0x0C85, 0x0C8C, 0x0C8E, 0x0C90, 0x0C92, 0x0CA8,
			0x0CAA, 0x0CB3, 0x0CB5, 0x0CB9, 0x0CDE, 0x0CDE, 0x0CE0, 0x0CE1,
			0x0D05, 0x0D0C, 0x0D0E, 0x0D10, 0x0D12, 0x0D28, 0x0D2A, 0x0D39,
			0x0D60, 0x0D61, 0x0E01, 0x0E2E, 0x0E30, 0x0E30, 0x0E32, 0x0E33,
			0x0E40, 0x0E45, 0x0E81, 0x0E82, 0x0E84, 0x0E84, 0x0E87, 0x0E88,
			0x0E8A, 0x0E8A, 0x0E8D, 0x0E8D, 0x0E94, 0x0E97, 0x0E99, 0x0E9F,
			0x0EA1, 0x0EA3, 0x0EA5, 0x0EA5, 0x0EA7, 0x0EA7, 0x0EAA, 0x0EAB,
			0x0EAD, 0x0EAE, 0x0EB0, 0x0EB0, 0x0EB2, 0x0EB3, 0x0EBD, 0x0EBD,
			0x0EC0, 0x0EC4, 0x0F40, 0x0F47, 0x0F49, 0x0F69, 0x10A0, 0x10C5,
			0x10D0, 0x10F6, 0x1100, 0x1100, 0x1102, 0x1103, 0x1105, 0x1107,
			0x1109, 0x1109, 0x110B, 0x110C, 0x110E, 0x1112, 0x113C, 0x113C,
			0x113E, 0x113E, 0x1140, 0x1140, 0x114C, 0x114C, 0x114E, 0x114E,
			0x1150, 0x1150, 0x1154, 0x1155, 0x1159, 0x1159, 0x115F, 0x1161,
			0x1163, 0x1163, 0x1165, 0x1165, 0x1167, 0x1167, 0x1169, 0x1169,
			0x116D, 0x116E, 0x1172, 0x1173, 0x1175, 0x1175, 0x119E, 0x119E,
			0x11A8, 0x11A8, 0x11AB, 0x11AB, 0x11AE, 0x11AF, 0x11B7, 0x11B8,
			0x11BA, 0x11BA, 0x11BC, 0x11C2, 0x11EB, 0x11EB, 0x11F0, 0x11F0,
			0x11F9, 0x11F9, 0x1E00, 0x1E9B, 0x1EA0, 0x1EF9, 0x1F00, 0x1F15,
			0x1F18, 0x1F1D, 0x1F20, 0x1F45, 0x1F48, 0x1F4D, 0x1F50, 0x1F57,
			0x1F59, 0x1F59, 0x1F5B, 0x1F5B, 0x1F5D, 0x1F5D, 0x1F5F, 0x1F7D,
			0x1F80, 0x1FB4, 0x1FB6, 0x1FBC, 0x1FBE, 0x1FBE, 0x1FC2, 0x1FC4,
			0x1FC6, 0x1FCC, 0x1FD0, 0x1FD3, 0x1FD6, 0x1FDB, 0x1FE0, 0x1FEC,
			0x1FF2, 0x1FF4, 0x1FF6, 0x1FFC, 0x2126, 0x2126, 0x212A, 0x212B,
			0x212E, 0x212E, 0x2180, 0x2182, 0x3041, 0x3094, 0x30A1, 0x30FA,
			0x3105, 0x312C, 0xAC00, 0xD7A3							};

	/**
	 * The ideographic char ranges are a part of valid TR-069 path name letters.
	 * They are collected from Appendix B of the XML specification according
	 * definition in TR-106. The number of elements is even, where
	 * <code>i</code>-th element is the starting element of the range. The
	 * <code>(i+1)</code>-th element is the last element in the range. All
	 * elements in the range are between those two.
	 */
	public static final char[]		TR069_PATH_IDEOGRAPHIC_RANGES	= new char[] {
			0x4E00, 0x9FA5, 0x3007, 0x3007, 0x3021, 0x3029			};

	/**
	 * The digit char ranges are a part of valid TR-069 path name letters. They
	 * are collected from Appendix B of the XML specification according
	 * definition in TR-106. The number of elements is even, where
	 * <code>i</code>-th element is the starting element of the range. The
	 * <code>(i+1)</code>-th element is the last element in the range. All
	 * elements in the range are between those two.
	 */
	public static final char[]		TR069_PATH_DIGIT_RANGES			= new char[] {
			0x0030, 0x0039, 0x0660, 0x0669, 0x06F0, 0x06F9, 0x0966, 0x096F,
			0x09E6, 0x09EF, 0x0A66, 0x0A6F, 0x0AE6, 0x0AEF, 0x0B66, 0x0B6F,
			0x0BE7, 0x0BEF, 0x0C66, 0x0C6F, 0x0CE6, 0x0CEF, 0x0D66, 0x0D6F,
			0x0E50, 0x0E59, 0x0ED0, 0x0ED9, 0x0F20, 0x0F29			};
}

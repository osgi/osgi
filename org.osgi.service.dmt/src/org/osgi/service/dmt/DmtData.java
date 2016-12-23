/*
 * Copyright (c) OSGi Alliance (2004, 2016). All Rights Reserved.

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

package org.osgi.service.dmt;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * An immutable data structure representing the contents of a leaf or interior
 * node. This structure represents only the value and the format property of the
 * node, all other properties (like MIME type) can be set and read using the
 * {@code DmtSession} interface.
 * <p>
 * Different constructors are available to create nodes with different formats.
 * Nodes of {@code null} format can be created using the static
 * {@link #NULL_VALUE} constant instance of this class.
 * <p>
 * {@link #FORMAT_RAW_BINARY} and {@link #FORMAT_RAW_STRING} enable the support
 * of future data formats. When using these formats, the actual format name is
 * specified as a {@code String}. The application is responsible for the proper
 * encoding of the data according to the specified format.
 * 
 * @Immutable
 * @author $Id$
 */
public final class DmtData {

	/**
	 * The node holds an OMA DM {@code int} value.
	 */
	public static final int		FORMAT_INTEGER		= 0x0001;

	/**
	 * The node holds an OMA DM {@code float} value.
	 */
	public static final int		FORMAT_FLOAT		= 0x0002;

	/**
	 * The node holds an OMA DM {@code chr} value.
	 */
	public static final int		FORMAT_STRING		= 0x0004;

	/**
	 * The node holds an OMA DM {@code bool} value.
	 */
	public static final int		FORMAT_BOOLEAN		= 0x0008;

	/**
	 * The node holds an OMA DM {@code date} value.
	 */
	public static final int		FORMAT_DATE			= 0x0010;

	/**
	 * The node holds an OMA DM {@code time} value.
	 */
	public static final int		FORMAT_TIME			= 0x0020;

	/**
	 * The node holds an OMA DM {@code bin} value. The value of the node
	 * corresponds to the Java {@code byte[]} type.
	 */
	public static final int		FORMAT_BINARY		= 0x0040;

	/**
	 * The node holds an OMA DM {@code b64} value. Like {@link #FORMAT_BINARY},
	 * this format is also represented by the Java {@code byte[]} type, the
	 * difference is only in the corresponding OMA DM format.
	 * 
	 * This format does not affect the internal storage format of the data as
	 * {@code byte[]}. It is intended as a hint for the external representation
	 * of this data. Protocol Adapters can use this hint for their further
	 * processing.
	 */
	public static final int		FORMAT_BASE64		= 0x0080;

	/**
	 * The node holds an OMA DM {@code xml} value.
	 */
	public static final int		FORMAT_XML			= 0x0100;

	/**
	 * The node holds an OMA DM {@code null} value. This corresponds to the Java
	 * {@code null} type.
	 */
	public static final int		FORMAT_NULL			= 0x0200;

	/**
	 * Format specifier of an internal node. An interior node can hold a Java
	 * object as value (see {@link DmtData#DmtData(Object)} and
	 * {@link DmtData#getNode()}). This value can be used by Java programs that
	 * know a specific URI understands the associated Java type. This type is
	 * further used as a return value of the {@link MetaNode#getFormat()} method
	 * for interior nodes.
	 */
	public static final int		FORMAT_NODE			= 0x0400;

	/**
	 * The node holds raw protocol data encoded as {@code String}. The
	 * {@link #getFormatName()} method can be used to get the actual format
	 * name.
	 */
	public static final int		FORMAT_RAW_STRING	= 0x0800;

	/**
	 * The node holds raw protocol data encoded in binary format. The
	 * {@link #getFormatName()} method can be used to get the actual format
	 * name.
	 */
	public static final int		FORMAT_RAW_BINARY	= 0x1000;

	/**
	 * The node holds a long value. The {@link #getFormatName()} method can be
	 * used to get the actual format name.
	 * 
	 * @since 2.0
	 */
	public static final int		FORMAT_LONG			= 0x2000;

	/**
	 * The node holds a Date object. If the getTime() equals zero then the date
	 * time is not known. If the getTime() is negative it must be interpreted as
	 * a relative number of milliseconds.
	 * 
	 * @since 2.0
	 */
	public static final int		FORMAT_DATE_TIME	= 0x4000;

	// FORMAT_NAMES must be initialized before any constructor is called.
	private static final Map<Integer, String>	FORMAT_NAMES		= new HashMap<>();
	static {
		FORMAT_NAMES.put(Integer.valueOf(FORMAT_BASE64), "base64");
		FORMAT_NAMES.put(Integer.valueOf(FORMAT_BINARY), "binary");
		FORMAT_NAMES.put(Integer.valueOf(FORMAT_BOOLEAN), "boolean");
		FORMAT_NAMES.put(Integer.valueOf(FORMAT_DATE), "date");
		FORMAT_NAMES.put(Integer.valueOf(FORMAT_FLOAT), "float");
		FORMAT_NAMES.put(Integer.valueOf(FORMAT_INTEGER), "integer");
		FORMAT_NAMES.put(Integer.valueOf(FORMAT_NODE), "NODE");
		FORMAT_NAMES.put(Integer.valueOf(FORMAT_NULL), "null");
		FORMAT_NAMES.put(Integer.valueOf(FORMAT_STRING), "string");
		FORMAT_NAMES.put(Integer.valueOf(FORMAT_TIME), "time");
		FORMAT_NAMES.put(Integer.valueOf(FORMAT_XML), "xml");
		FORMAT_NAMES.put(Integer.valueOf(FORMAT_LONG), "long");
		FORMAT_NAMES.put(Integer.valueOf(FORMAT_DATE_TIME), "dateTime");
	}

	/**
	 * Constant instance representing a leaf node of {@code null} format.
	 */
	public static final DmtData	NULL_VALUE			= new DmtData();

	/**
	 * Constant instance representing a boolean {@code true} value.
	 * 
	 * @since 2.0
	 */
	public static final DmtData	TRUE_VALUE			= new DmtData(true);

	/**
	 * Constant instance representing a boolean {@code false} value.
	 * 
	 * @since 2.0
	 */
	public static final DmtData	FALSE_VALUE			= new DmtData(false);

	private final int			format;
	private final Object		value;

	/*
	 * For format names, can be overridden by raw formats
	 */
	private String				formatName;

	/**
	 * Create a {@code DmtData} instance of {@code null} format. This
	 * constructor is private and used only to create the public
	 * {@link #NULL_VALUE} constant.
	 */
	private DmtData() {
		this(null, FORMAT_NULL, null);
	}

	/**
	 * Create a {@code DmtData} instance of {@code chr} format with the given
	 * string value. The {@code null} string argument is valid.
	 * 
	 * @param string the string value to set
	 */
	public DmtData(String string) {
		this(string, FORMAT_STRING, null);
	}

	/**
	 * Create a {@code DmtData} instance of {@code dateTime} format with the
	 * given Date value. The given Date value must be a non-null {@code Date}
	 * object.
	 * 
	 * @param date the Date object to set
	 */
	public DmtData(Date date) {
		if (date == null)
			throw new NullPointerException("The date argument is null.");
		this.format = FORMAT_DATE_TIME;
		this.value = date;
	}

	/**
	 * Create a {@code DmtData} instance of {@code node} format with the given
	 * object value. The value represents complex data associated with an
	 * interior node.
	 * <p>
	 * Certain interior nodes can support access to their subtrees through such
	 * complex values, making it simpler to retrieve or update all leaf nodes in
	 * a subtree.
	 * <p>
	 * The given value must be a non-{@code null} immutable object.
	 * 
	 * @param complex the complex data object to set
	 */
	public DmtData(Object complex) {
		if (complex == null)
			throw new NullPointerException("Complex data argument is null.");
		format = FORMAT_NODE;
		value = complex;
	}

	/**
	 * Create a {@code DmtData} instance of the specified format and set its
	 * value based on the given string. Only the following string-based formats
	 * can be created using this constructor:
	 * <ul>
	 * <li>{@link #FORMAT_STRING} - value can be any string</li>
	 * <li>{@link #FORMAT_XML} - value must contain an XML fragment (the
	 * validity is not checked by this constructor)</li>
	 * <li>{@link #FORMAT_DATE} - value must be parsable to an ISO 8601 calendar
	 * date in complete representation, basic format (pattern {@code CCYYMMDD})</li>
	 * <li>{@link #FORMAT_TIME} - value must be parsable to an ISO 8601 time of
	 * day in either local time, complete representation, basic format (pattern
	 * {@code hhmmss}) or Coordinated Universal Time, basic format (pattern
	 * {@code hhmmssZ})</li>
	 * </ul>
	 * * The {@code null} string argument is only valid if the format is string
	 * or XML.
	 * 
	 * @param value the string, XML, date, or time value to set
	 * @param format the format of the {@code DmtData} instance to be created,
	 *        must be one of the formats specified above
	 * @throws IllegalArgumentException if {@code format} is not one of the
	 *         allowed formats, or {@code value} is not a valid string for the
	 *         given format
	 * @throws NullPointerException if a string, XML, date, or time is
	 *         constructed and {@code value} is {@code null}
	 */
	public DmtData(String value, int format) {
		this(value, format, null);
		if (format == FORMAT_STRING || format == FORMAT_XML || format == FORMAT_DATE || format == FORMAT_TIME)
			return;

		throw new IllegalArgumentException("Wrong format for DmtData(String,int), format must be one of FORMAT_STRING, FORMAT_XML, FORMAT_DATE, or FORMAT_TIME");
	}

	/**
	 * Create a {@code DmtData} instance of {@code int} format and set its
	 * value.
	 * 
	 * @param integer the integer value to set
	 */
	public DmtData(int integer) {
		this(Integer.valueOf(integer), FORMAT_INTEGER, null);
	}

	/**
	 * Create a {@code DmtData} instance of {@code float} format and set its
	 * value.
	 * 
	 * @param flt the float value to set
	 */
	public DmtData(float flt) {
		this(Float.valueOf(flt), FORMAT_FLOAT, null);
	}

	/**
	 * Create a {@code DmtData} instance of {@code long} format and set its
	 * value.
	 * 
	 * @param lng the long value to set
	 * @since 2.0
	 */
	public DmtData(long lng) {
		this(Long.valueOf(lng), FORMAT_LONG, null);
	}

	/**
	 * Create a {@code DmtData} instance of {@code bool} format and set its
	 * value.
	 * 
	 * @param bool the boolean value to set
	 */
	public DmtData(boolean bool) {
		this(Boolean.valueOf(bool), FORMAT_BOOLEAN, null);
	}

	/**
	 * Create a {@code DmtData} instance of {@code bin} format and set its
	 * value.
	 * 
	 * @param bytes the byte array to set, must not be {@code null}
	 * @throws NullPointerException if {@code bytes} is {@code null}
	 */
	public DmtData(byte[] bytes) {
		this(bytes, FORMAT_BINARY, null);
	}

	/**
	 * Create a {@code DmtData} instance of {@code bin} or {@code b64} format
	 * and set its value. The chosen format is specified by the {@code base64}
	 * parameter.
	 * 
	 * @param bytes the byte array to set, must not be {@code null}
	 * @param base64 if {@code true}, the new instance will have {@code b64}
	 *        format, if {@code false}, it will have {@code bin} format
	 * @throws NullPointerException if {@code bytes} is {@code null}
	 */
	public DmtData(byte[] bytes, boolean base64) {
		this(bytes, base64 ? FORMAT_BASE64 : FORMAT_BINARY, null);
	}

	/**
	 * Create a {@code DmtData} instance of the specified format and set its
	 * value based on the given {@code byte[]}. Only the following
	 * {@code byte[]} based formats can be created using this constructor:
	 * 
	 * <ul>
	 * <li>{@link #FORMAT_BINARY}</li>
	 * <li>{@link #FORMAT_BASE64}</li>
	 * </ul>
	 * 
	 * 
	 * @param bytes the byte array to set, must not be {@code null}
	 * @param format the format of the DmtData instance to be created, must be
	 *        one of the formats specified above
	 * @throws IllegalArgumentException if format is not one of the allowed
	 *         formats
	 * @throws NullPointerException if {@code bytes} is {@code null}
	 */
	public DmtData(byte[] bytes, int format) {
		this(bytes, format, null);
		if (format == FORMAT_BINARY || format == FORMAT_BASE64)
			return;
		throw new IllegalArgumentException("Invalid format for DmtDate(byte[],format), only FORMAT_BINARY and FORMAT_BASE64 are allowed");
	}

	/**
	 * Create a {@code DmtData} instance in {@link #FORMAT_RAW_STRING} format.
	 * The data is provided encoded as a {@code String}. The actual data format
	 * is specified in {@code formatName}. The encoding used in {@code data}
	 * must conform to this format.
	 * 
	 * @param formatName the name of the format, must not be {@code null}
	 * @param data the data encoded according to the specified format, must not
	 *        be {@code null}
	 * @throws NullPointerException if {@code formatName} or {@code data} is
	 *         {@code null}
	 */
	public DmtData(String formatName, String data) {
		this(data.toString(), FORMAT_RAW_STRING, formatName.toString());
	}

	/*
	 * Constructor used by all others.
	 * 
	 * @param value The value
	 * 
	 * @param format The format
	 * 
	 * @param formatName The name of the format, can be null will then be set to
	 * the format name
	 */

	private DmtData(Object value, int format, String formatName) {
		this.value = value;
		this.format = format;
		this.formatName = formatName;
		validate();
	}

	/**
	 * Create a {@code DmtData} instance in {@link #FORMAT_RAW_BINARY} format.
	 * The data is provided encoded as binary. The actual data format is
	 * specified in {@code formatName}. The encoding used in {@code data} must
	 * conform to this format.
	 * 
	 * @param formatName the name of the format, must not be {@code null}
	 * @param data the data encoded according to the specified format, must not
	 *        be {@code null}
	 * @throws NullPointerException if {@code formatName} or {@code data} is
	 *         {@code null}
	 */
	public DmtData(String formatName, byte[] data) {
		this(data, FORMAT_RAW_BINARY, formatName);
		if (formatName == null)
			throw new NullPointerException("Format name argument is null.");
	}

	/*
	 * Validate the setup
	 */
	private void validate() {
		Class<?> c;
		switch (format) {
			case FORMAT_INTEGER :
				c = Integer.class;
				break;

			case FORMAT_FLOAT :
				c = Float.class;
				break;

			case FORMAT_TIME :
				checkTimeFormat((String) value);
				c = String.class;
				break;

			case FORMAT_DATE :
				checkDateFormat((String) value);
				c = String.class;
				break;

			case FORMAT_STRING :
			case FORMAT_XML :
				c = String.class;
				if (value != null) {
					break;
				} else {
					return;
				}

			case FORMAT_RAW_STRING :
				c = String.class;
				break;

			case FORMAT_BOOLEAN :
				c = Boolean.class;
				break;

			case FORMAT_BINARY :
			case FORMAT_RAW_BINARY :
			case FORMAT_BASE64 :
				if (value != null) {
					c = byte[].class;
					break;
				}
				throw new NullPointerException("The bytes argument is null.");

			case FORMAT_NULL :
				if (value != null)
					throw new IllegalArgumentException("Format is null but value is not ");
				else
					return;

			case FORMAT_NODE :
				c = Object.class;
				break;

			case FORMAT_LONG :
				c = Long.class;
				break;

			default :
				throw new IllegalArgumentException("Invalid format number for DmtData " + format);

		}
		if (!c.isInstance(value))
			throw new IllegalArgumentException("Invalid type type for DmtData, expected " + c.getClass() + " but have " + value.getClass());

	}

	/**
	 * Gets the value of a node with string ({@code chr}) format.
	 * 
	 * @return the string value
	 * @throws DmtIllegalStateException if the format of the node is not string
	 */
	public String getString() {
		if (format == FORMAT_STRING)
			return (String) value;

		throw new DmtIllegalStateException("DmtData value is not string.");
	}

	/**
	 * Gets the value of a node with date format. The returned date string is
	 * formatted according to the ISO 8601 definition of a calendar date in
	 * complete representation, basic format (pattern {@code CCYYMMDD}).
	 * 
	 * @return the date value
	 * @throws DmtIllegalStateException if the format of the node is not date
	 */
	public String getDate() {
		if (format == FORMAT_DATE)
			return (String) value;

		throw new DmtIllegalStateException("DmtData value is not date.");
	}

	/**
	 * Gets the value of a node with time format. The returned time string is
	 * formatted according to the ISO 8601 definition of the time of day. The
	 * exact format depends on the value the object was initialized with: either
	 * local time, complete representation, basic format (pattern {@code hhmmss}
	 * ) or Coordinated Universal Time, basic format (pattern {@code hhmmssZ}).
	 * 
	 * @return the time value
	 * @throws DmtIllegalStateException if the format of the node is not time
	 */
	public String getTime() {
		if (format == FORMAT_TIME)
			return (String) value;

		throw new DmtIllegalStateException("DmtData value is not time.");
	}

	/**
	 * Gets the value of a node with {@code dateTime} format.
	 * 
	 * @return the Date value
	 * @throws DmtIllegalStateException if the format of the node is not time
	 * @since 2.0
	 */
	public Date getDateTime() {
		if (format == FORMAT_DATE_TIME)
			return (Date) value;

		throw new DmtIllegalStateException("DmtData value is not dateTime.");
	}

	/**
	 * Gets the value of a node with {@code xml} format.
	 * 
	 * @return the XML value
	 * @throws DmtIllegalStateException if the format of the node is not
	 *         {@code xml}
	 */
	public String getXml() {
		if (format == FORMAT_XML)
			return (String) value;

		throw new DmtIllegalStateException("DmtData value is not XML.");
	}

	/**
	 * Gets the value of a node with integer ({@code int}) format.
	 * 
	 * @return the integer value
	 * @throws DmtIllegalStateException if the format of the node is not integer
	 */
	public int getInt() {
		if (format == FORMAT_INTEGER)
			return ((Integer) value).intValue();

		throw new DmtIllegalStateException("DmtData value is not integer.");
	}

	/**
	 * Gets the value of a node with long format.
	 * 
	 * @return the long value
	 * @throws DmtIllegalStateException if the format of the node is not long
	 * @since 2.0
	 */
	public long getLong() {
		if (format == FORMAT_LONG)
			return ((Long) value).longValue();

		throw new DmtIllegalStateException("DmtData value is not long.");
	}

	/**
	 * Gets the value of a node with {@code float} format.
	 * 
	 * @return the float value
	 * @throws DmtIllegalStateException if the format of the node is not
	 *         {@code float}
	 */
	public float getFloat() {
		if (format == FORMAT_FLOAT)
			return ((Float) value).floatValue();

		throw new DmtIllegalStateException("DmtData value is not float.");
	}

	/**
	 * Gets the value of a node with boolean ({@code bool}) format.
	 * 
	 * @return the boolean value
	 * @throws DmtIllegalStateException if the format of the node is not boolean
	 */
	public boolean getBoolean() {
		if (format == FORMAT_BOOLEAN)
			return ((Boolean) value).booleanValue();

		throw new DmtIllegalStateException("DmtData value is not boolean.");
	}

	/**
	 * Gets the value of a node with binary ({@code bin}) format.
	 * 
	 * @return the binary value
	 * @throws DmtIllegalStateException if the format of the node is not binary
	 */
	public byte[] getBinary() {
		if (format == FORMAT_BINARY) {
			return copyBytes();
		}

		throw new DmtIllegalStateException("DmtData value is not a byte array.");
	}

	/*
	 * Copy bytes to a new buffer
	 * 
	 * @return the copied bytes
	 */
	private byte[] copyBytes() {
		byte[] bytes = (byte[]) value;
		byte[] bytesCopy = new byte[bytes.length];
		System.arraycopy(bytes, 0, bytesCopy, 0, bytes.length);
		return bytesCopy;
	}

	/**
	 * Gets the value of a node in raw binary ({@link #FORMAT_RAW_BINARY})
	 * format.
	 * 
	 * @return the data value in raw binary format
	 * @throws DmtIllegalStateException if the format of the node is not raw
	 *         binary
	 */
	public byte[] getRawBinary() {
		if (format == FORMAT_RAW_BINARY)
			return copyBytes();

		throw new DmtIllegalStateException("DmtData value is not in raw binary format.");
	}

	/**
	 * Gets the value of a node in raw {@code String} (
	 * {@link #FORMAT_RAW_STRING}) format.
	 * 
	 * @return the data value in raw {@code String} format
	 * @throws DmtIllegalStateException if the format of the node is not raw
	 *         {@code String}
	 */
	public String getRawString() {
		if (format == FORMAT_RAW_STRING)
			return (String) value;

		throw new DmtIllegalStateException("DmtData value is not in raw string format.");
	}

	/**
	 * Gets the value of a node with base 64 ({@code b64}) format.
	 * 
	 * @return the binary value
	 * @throws DmtIllegalStateException if the format of the node is not base
	 *         64.
	 */
	public byte[] getBase64() {
		if (format == FORMAT_BASE64) {
			return copyBytes();
		}

		throw new DmtIllegalStateException("DmtData value is not in base 64 format.");
	}

	/**
	 * Gets the complex data associated with an interior node ({@code node}
	 * format).
	 * <p>
	 * Certain interior nodes can support access to their subtrees through
	 * complex values, making it simpler to retrieve or update all leaf nodes in
	 * the subtree.
	 * 
	 * @return the data object associated with an interior node
	 * @throws DmtIllegalStateException if the format of the data is not
	 *         {@code node}
	 */
	public Object getNode() {
		if (format == FORMAT_NODE)
			return value;

		throw new DmtIllegalStateException("DmtData does not contain interior node data.");
	}

	/**
	 * Get the node's format, expressed in terms of type constants defined in
	 * this class. Note that the 'format' term is a legacy from OMA DM, it is
	 * more customary to think of this as 'type'.
	 * 
	 * @return the format of the node
	 */
	public int getFormat() {
		return format;
	}

	/**
	 * Returns the format of this {@code DmtData} as {@code String}. For the
	 * predefined data formats this is the OMA DM defined name of the format.
	 * For {@link #FORMAT_RAW_STRING} and {@link #FORMAT_RAW_BINARY} this is the
	 * format specified when the object was created.
	 * 
	 * @return the format name as {@code String}
	 */
	public String getFormatName() {
		if (formatName == null)
			return getFormatName(format);
		return formatName;
	}

	/**
	 * Get the size of the data. The returned value depends on the format of
	 * data in the node:
	 * <ul>
	 * <li>{@link #FORMAT_STRING}, {@link #FORMAT_XML}, {@link #FORMAT_BINARY},
	 * {@link #FORMAT_BASE64}, {@link #FORMAT_RAW_STRING}, and
	 * {@link #FORMAT_RAW_BINARY}: the length of the stored data, or 0 if the
	 * data is {@code null}</li>
	 * <li>{@link #FORMAT_INTEGER} and {@link #FORMAT_FLOAT}: 4</li>
	 * <li>{@link #FORMAT_LONG} and {@link #FORMAT_DATE_TIME}: 8</li>
	 * <li>{@link #FORMAT_DATE} and {@link #FORMAT_TIME}: the length of the date
	 * or time in its string representation</li>
	 * <li>{@link #FORMAT_BOOLEAN}: 1</li>
	 * <li>{@link #FORMAT_NODE}: -1 (unknown)</li>
	 * <li>{@link #FORMAT_NULL}: 0</li>
	 * </ul>
	 * 
	 * @return the size of the data stored by this object
	 */
	public int getSize() {
		switch (format) {
			case FORMAT_STRING :
			case FORMAT_XML :
			case FORMAT_DATE :
			case FORMAT_TIME :
			case FORMAT_RAW_STRING :
				if (value != null) {
					return ((String) value).length();
				} else {
					return 0;
				}
			case FORMAT_BINARY :
			case FORMAT_BASE64 :
			case FORMAT_RAW_BINARY :
				return ((byte[]) value).length;
			case FORMAT_INTEGER :
			case FORMAT_FLOAT :
				return 4;
			case FORMAT_BOOLEAN :
				return 1;
			case FORMAT_NODE :
				return -1;
			case FORMAT_DATE_TIME :
			case FORMAT_LONG :
				return 8;
			case FORMAT_NULL :
				return 0;
		}

		return 0; // never reached
	}

	/**
	 * Gets the string representation of the {@code DmtData}. This method works
	 * for all formats.
	 * <p>
	 * For string format data - including {@link #FORMAT_RAW_STRING} - the
	 * string value itself is returned, while for XML, date, time, integer,
	 * float, boolean, long and node formats the string form of the value is
	 * returned. Binary - including {@link #FORMAT_RAW_BINARY} - base64 data is
	 * represented by two-digit hexadecimal numbers for each byte separated by
	 * spaces. The {@link #NULL_VALUE} data has the string form of "
	 * {@code null}". Data of string or XML format containing the Java
	 * {@code null} value is represented by an empty string. DateTime data is
	 * formatted as {@code yyyy-MM-dd'T'HH:mm:SS'Z'}).
	 * 
	 * @return the string representation of this {@code DmtData} instance
	 */
	@Override
	public String toString() {
		switch (format) {
			case FORMAT_STRING :
			case FORMAT_XML :
			case FORMAT_DATE :
			case FORMAT_TIME :
			case FORMAT_RAW_STRING :
				if (value != null) {
					return (String) value;
				} else {
					return "";
				}

			case FORMAT_INTEGER :
			case FORMAT_LONG :
			case FORMAT_FLOAT :
			case FORMAT_BOOLEAN :
			case FORMAT_NODE :
				return value.toString();

			case FORMAT_BINARY :
			case FORMAT_BASE64 :
			case FORMAT_RAW_BINARY :
				return getHexDump((byte[]) value);

			case FORMAT_NULL :
				return "null";

			case FORMAT_DATE_TIME :
				// unfortunately SimpleDateFormat is not thread safe :-(
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SS'Z'");
				sf.setTimeZone(TimeZone.getTimeZone("UTC"));
				return sf.format(value);

			default :
				throw new IllegalStateException("Invalid format " + format);
		}
	}

	/**
	 * Compares the specified object with this {@code DmtData} instance. Two
	 * {@code DmtData} objects are considered equal if their format is the same,
	 * and their data (selected by the format) is equal.
	 * <p>
	 * In case of {@link #FORMAT_RAW_BINARY} and {@link #FORMAT_RAW_STRING} the
	 * textual name of the data format - as returned by {@link #getFormatName()}
	 * - must be equal as well.
	 * 
	 * @param obj the object to compare with this {@code DmtData}
	 * @return true if the argument represents the same {@code DmtData} as this
	 *         object
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DmtData))
			return false;

		DmtData other = (DmtData) obj;

		if (format != other.format)
			return false;

		if (formatName != null) {
			if (!formatName.equals(other.formatName)) {
				return false;
			}
		}

		if (value == null)
			if (other.value == null)
				return true;
			else
				return false;

		if (value.equals(other.value)) {
			return true;
		}

		switch (format) {
			case FORMAT_BINARY :
			case FORMAT_BASE64 :
			case FORMAT_RAW_BINARY :
				return Arrays.equals((byte[]) value, (byte[]) other.value);
		}

		return false;
	}

	/**
	 * Returns the hash code value for this {@code DmtData} instance. The hash
	 * code is calculated based on the data (selected by the format) of this
	 * object.
	 * 
	 * @return the hash code value for this object
	 */
	@Override
	public int hashCode() {
		if (value == null)
			return 42;
		else
			return value.hashCode();
	}

	private static void checkDateFormat(String value) {
		if (value.length() != 8)
			throw new IllegalArgumentException("Date string '" + value + "' does not follow the format 'CCYYMMDD'.");

		int year = checkNumber(value, "Date", 0, 4, 0, 9999);
		int month = checkNumber(value, "Date", 4, 2, 1, 12);
		int day = checkNumber(value, "Date", 6, 2, 1, 31);

		// Date checking is not prepared for all special rules (for example
		// historical leap years), production code could contain a full check.

		// Day 31 is invalid for April, June, September and November
		if ((month == 4 || month == 6 || month == 9 || month == 11) && day == 31)
			throw new IllegalArgumentException("Date string '" + value + "' contains an invalid date.");

		// February 29 is invalid except for leap years, Feb. 30-31 are invalid
		if (month == 2 && day > 28 && !(day == 29 && year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)))
			throw new IllegalArgumentException("Date string '" + value + "' contains an invalid date.");
	}

	private static void checkTimeFormat(String value) {
		if (value.length() > 0 && value.charAt(value.length() - 1) == 'Z')
			value = value.substring(0, value.length() - 1);

		if (value.length() != 6)
			throw new IllegalArgumentException("Time string '" + value + "' does not follow the format 'hhmmss' or 'hhmmssZ'.");

		// Time checking is not prepared for all special rules (for example
		// leap seconds), production code could contain a full check.

		// if hour is 24, only 240000 should be allowed
		checkNumber(value, "Time", 0, 2, 0, 24);
		checkNumber(value, "Time", 2, 2, 0, 59);
		checkNumber(value, "Time", 4, 2, 0, 59);

		if (value.startsWith("24") && !value.startsWith("240000"))
			throw new IllegalArgumentException("Time string is out of range.");
	}

	private static int checkNumber(String value, String name, int from, int length, int min, int max) {
		String part = value.substring(from, from + length);
		int number;
		try {
			number = Integer.parseInt(part);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(name + " string '" + value + "' contains a non-numeric part.");
		}
		if (number < min || number > max)
			throw new IllegalArgumentException("A segment of the " + name + " string '" + value + "' is out of range.");

		return number;
	}

	// character array of hexadecimal digits, used for printing binary data
	private static char[]	hex	= "0123456789ABCDEF".toCharArray();

	// generates a hexadecimal dump of the given binary data
	private static String getHexDump(byte[] bytes) {
		if (bytes.length == 0)
			return "";

		StringBuilder buf = new StringBuilder();
		String del = "";
		for (int i = 0; i < bytes.length; i++) {
			appendHexByte(buf.append(del), bytes[i]);
			del = " ";
		}

		return buf.toString();
	}

	private static void appendHexByte(StringBuilder buf, byte b) {
		buf.append(hex[(b & 0xF0) >> 4]).append(hex[b & 0x0F]);
	}

	private static String getFormatName(int format) {
		return FORMAT_NAMES.get(Integer.valueOf(format));
	}
}

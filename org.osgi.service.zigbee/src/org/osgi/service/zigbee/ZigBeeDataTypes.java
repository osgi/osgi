/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
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

package org.osgi.service.zigbee;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * This interface represents all ZigBee data types, and contains the common
 * serialize/deserialize methods for the org.osgi.service.zigbee.types.*
 * 
 * This constants are not the same provided by the ZigBee specification, and
 * follows the rules below:
 * 
 * <p>
 * <em>bit 0-3</em>: if bit 6 is one, these bits represents the size of the data
 * type in bytes.<br>
 * <em>bit 6</em>: if set to 1 bits 0-3 represents the size of the data type in
 * bytes. <br>
 * <em>bit 7</em>: if one the data type represents a unsigned value, otherwise
 * it is signed.
 * 
 * 
 * <p>
 * <dl>
 * Related documentation:
 * <dd>[1] ZigBee Cluster Library specification, Document 075123r04ZB, May 29,
 * 2012.
 * </dl>
 * 
 * @author $Id$
 */
public class ZigBeeDataTypes {

	/**
	 * 2.5.2.1 No Data Type
	 * 
	 * The no data type is a special type to represent an attribute with no
	 * associated data.
	 */
	public static final short	NO_DATA					= 0x00;

	/**
	 * 2.5.2.2 General Data (8, 16, 24, 32, 40, 48, 56 and 64-bit)
	 * 
	 * This type has no rules about its use, and may be used when a data element
	 * is needed but its use does not conform to any of the standard types.
	 */
	public static final short	GENERAL_DATA_8			= 0x50;
	/** */
	public static final short	GENERAL_DATA_16			= 0x51;
	/** */
	public static final short	GENERAL_DATA_24			= 0x52;
	/** */
	public static final short	GENERAL_DATA_32			= 0x53;
	/** */
	public static final short	GENERAL_DATA_40			= 0x54;
	/** */
	public static final short	GENERAL_DATA_48			= 0x55;
	/** */
	public static final short	GENERAL_DATA_56			= 0x56;
	/** */
	public static final short	GENERAL_DATA_64			= 0x57;

	/**
	 * 2.5.2.3 Boolean
	 * 
	 * The Boolean type represents a logical value, either FALSE (0x00) or TRUE
	 * (0x01). The value 0xff represents an invalid value of this type. All
	 * other values of this type are forbidden.
	 */
	public static final short	BOOLEAN					= 0x01;

	/**
	 * 2.5.2.4 Bitmap (8, 16, 24, 32, 40, 48, 56 and 64-bit)
	 * 
	 * The Bitmap type holds 8, 16, 24, 32, 40, 48, 56 or 64 logical values, one
	 * per bit, depending on its length. There is no value that represents an
	 * invalid value of this type.
	 */
	public static final short	BITMAP_8				= 0x58;
	/** */
	public static final short	BITMAP_16				= 0x59;
	/** */
	public static final short	BITMAP_24				= 0x5a;
	/** */
	public static final short	BITMAP_32				= 0x5b;
	/** */
	public static final short	BITMAP_40				= 0x5c;
	/** */
	public static final short	BITMAP_48				= 0x5d;
	/** */
	public static final short	BITMAP_56				= 0x5e;
	/** */
	public static final short	BITMAP_64				= 0x5f;

	/**
	 * 2.5.2.5 Unsigned Integer (8, 16, 24, 32, 40, 48, 56 and 64-bit)
	 * 
	 * This type represents an unsigned integer with a decimal range of 0 to
	 * 2^8-1, 0 to 2^16-1, 0 to 2^24-1, 0 to 2^32-1, 0 to 2^40-1, 0 to 2^48-1, 0
	 * to 2^56-1, or 0 to 2^64-1, depending on its length. The values that
	 * represents an invalid value of this type are 0xff, 0xffff, 0xffffff,
	 * 0xffffffff, 0xffffffffff, 0xffffffffffff, 0xffffffffffffff and
	 * 0xffffffffffffffff respectively.
	 */
	public static final short	UNSIGNED_INTEGER_8		= 0x60;
	/** */
	public static final short	UNSIGNED_INTEGER_16		= 0x61;
	/** */
	public static final short	UNSIGNED_INTEGER_24		= 0x62;
	/** */
	public static final short	UNSIGNED_INTEGER_32		= 0x63;
	/** */
	public static final short	UNSIGNED_INTEGER_40		= 0x64;
	/** */
	public static final short	UNSIGNED_INTEGER_48		= 0x65;
	/** */
	public static final short	UNSIGNED_INTEGER_56		= 0x66;
	/** */
	public static final short	UNSIGNED_INTEGER_64		= 0x67;

	/**
	 * 2.5.2.6 Signed Integer (8, 16, 24, 32, 40, 48, 56 and 64-bit)
	 * 
	 * This type represents a signed integer with a decimal range of -(2^7-1) to
	 * 2^7-1, - (2^15-1) to 2^15-1, -(2^23-1) to 2^23-1, -(2^31-1) to 2^31-1,
	 * -(2^39-1) to 2^39-1, -(2^47-1) to 2^47-1, -(2^55-1) to 2^55-1, or
	 * -(2^63-1) to 2^63-1, depending on its length. The values that represents
	 * an invalid value of this type are 0x80, 0x8000, 0x800000, 0x80000000,
	 * 0x8000000000, 0x800000000000, 0x80000000000000 and 0x8000000000000000
	 * respectively.
	 */
	public static final short	SIGNED_INTEGER_8		= 0xe0;
	/** */
	public static final short	SIGNED_INTEGER_16		= 0xe1;
	/** */
	public static final short	SIGNED_INTEGER_24		= 0xe2;
	/** */
	public static final short	SIGNED_INTEGER_32		= 0xe3;
	/** */
	public static final short	SIGNED_INTEGER_40		= 0xe4;
	/** */
	public static final short	SIGNED_INTEGER_48		= 0xe5;
	/** */
	public static final short	SIGNED_INTEGER_56		= 0xe6;
	/** */
	public static final short	SIGNED_INTEGER_64		= 0xe7;

	/**
	 * 2.5.2.7 Enumeration (8-bit, 16-bit)
	 * 
	 * The Enumeration type represents an index into a lookup table to determine
	 * the final value. The values 0xff and 0xffff represent invalid values of
	 * the 8-bit and 16- bit types respectively.
	 */
	public static final short	ENUMERATION_8			= 0x70;
	/** */
	public static final short	ENUMERATION_16			= 0x71;

	/**
	 * 2.5.2.8 Semi-precision
	 * 
	 * The ZigBee semi-precision number format is based on the IEEE 754 standard
	 * for binary floating-point arithmetic. This number format should be used
	 * very sparingly, when absolutely necessary, keeping in mind the code and
	 * processing required supporting it.
	 * 
	 * See reference on top of this class.
	 */
	public static final short	FLOATING_SEMI			= 0xf8;

	/**
	 * 2.5.2.9 Single Precision
	 * 
	 * The format of the single precision data type is based on the IEEE 754
	 * standard for binary floating-point arithmetic. This number format should
	 * be used very sparingly, when absolutely necessary, keeping in mind the
	 * code and processing required supporting it.
	 * 
	 * See reference on top of this class.
	 */
	public static final short	FLOATING_SINGLE			= 0xf9;

	/**
	 * 2.5.2.10 Double Precision
	 * 
	 * The format of the double precision data type is based on the IEEE 754
	 * standard for binary floating-point arithmetic. This number format should
	 * be used very sparingly, when absolutely necessary, keeping in mind the
	 * code and processing required supporting it.
	 * 
	 * See reference on top of this class.
	 */
	public static final short	FLOATING_DOUBLE			= 0xfa;

	/**
	 * 2.5.2.11 Octet String
	 * 
	 * The octet string data type contains data in an application-defined
	 * format, not defined in this specification.
	 * 
	 * See reference on top of this class.
	 */
	public static final short	OCTET_STRING			= 0x78;

	/**
	 * 2.5.2.12 Character String
	 * 
	 * The character string data type contains data octets encoding characters
	 * according to the language and character set field of the complex
	 * descriptor.
	 * 
	 * See reference on top of this class.
	 */
	public static final short	CHARACTER_STRING		= 0x79;

	/**
	 * 2.5.2.13 Long Octet String
	 * 
	 * The long octet string data type contains data in an application-defined
	 * format, not defined in this specification.
	 * 
	 * See reference on top of this class.
	 */
	public static final short	LONG_OCTET_STRING		= 0x7a;

	/**
	 * 2.5.2.14 Long Character String
	 * 
	 * The long character string data type contains data octets encoding
	 * characters according to the language and character set field of the
	 * complex descriptor.
	 * 
	 * See reference on top of this class.
	 */
	public static final short	LONG_CHARACTER_STRING	= 0x7b;

	/**
	 * 2.5.2.15 Array
	 * 
	 * An array is an ordered sequence of zero or more elements, all of the same
	 * data type. This data type may be any ZCL defined data type, including
	 * array, structure, bag or set. The total nesting depth is limited to 15,
	 * and may be further limited by any relevant profile or application.
	 * 
	 * See reference on top of this class.
	 */
	public static final short	ARRAY					= 0x10;

	/**
	 * 2.5.2.16 Structure
	 * 
	 * A structure is an ordered sequence of elements, which may be of different
	 * data types. Each data type may be any ZCL defined data type, including
	 * array, structure, bag or set. The total nesting depth is limited to 15,
	 * and may be further limited by any relevant profile or application.
	 * 
	 * See reference on top of this class.
	 */
	public static final short	STRUCTURE				= 0x11;

	/**
	 * 2.5.2.17 Set
	 * 
	 * A set is a collection of elements with no associated order. Each element
	 * has the same data type, which may be any ZCL defined data type, including
	 * array, structure, bag or set. The nesting depth is limited to 15, and may
	 * be further limited by any relevant profile or application.
	 * 
	 * See reference on top of this class.
	 */
	public static final short	SET						= 0x12;

	/**
	 * 2.5.2.18 Bag
	 * 
	 * A bag behaves exactly the same as a set, except that the restriction that
	 * no two elements may have the same value is removed.
	 */
	public static final short	BAG						= 0x13;

	/**
	 * 2.5.2.19 Time of Day
	 * 
	 * The Time of Day data type shall be formatted as illustrated in spec.
	 * 
	 * See reference on top of this class.
	 */
	public static final short	TIME_OF_DAY				= 0x02;

	/**
	 * 2.5.2.20 Date
	 * 
	 * The Time of day data type shall be formatted as illustrated in spec.
	 * 
	 * See reference on top of this class.
	 */
	public static final short	DATE					= 0x03;

	/**
	 * 2.5.2.21 UTCTime
	 * 
	 * UTCTime is an unsigned 32-bit value representing the number of seconds
	 * since 0 hours, 0 minutes, 0 seconds, on the 1st of January, 2000 UTC
	 * (Universal Coordinated Time). The value that represents an invalid value
	 * of this type is 0xffffffffff.
	 * 
	 * Note that UTCTime does not hold a standard textual representation of
	 * Universal Coordinated Time (UTC). However, UTC (to a precision of one
	 * second) may be derived from it.
	 */
	public static final short	UTC_TIME				= 0x04;

	/**
	 * 2.5.2.22 Cluster ID
	 * 
	 * This type represents a cluster identifier as defined in spec.
	 * 
	 * See reference on top of this class.
	 */
	public static final short	CLUSTER_ID				= 0x05;

	/**
	 * 2.5.2.23 Attribute ID
	 * 
	 * This type represents an attribute identifier as defined in spec.
	 * 
	 * See reference on top of this class.
	 */
	public static final short	ATTRIBUTE_ID			= 0x06;

	/**
	 * 2.5.2.24 BACnet OID (Object Identifier)
	 * 
	 * The BACnet OID data type is included to allow interworking with BACnet.
	 * The format is described in the referenced standard.
	 * 
	 * See reference on top of this class.
	 */
	public static final short	BACNET_OID				= 0x07;

	/**
	 * 2.5.2.25 IEEE Address
	 * 
	 * The IEEE Address data type is a 64-bit IEEE address that is unique to
	 * every ZigBee device. A value of 0xffffffffffffffff indicates that the
	 * address is unknown.
	 */
	public static final short	IEEE_ADDRESS			= 0x08;

	/**
	 * 2.5.2.26 128-bit Security Key
	 * 
	 * The 128-bit Security Key data type is for use in ZigBee security, and may
	 * take any 128-bit value.
	 */
	public static final short	SECURITY_KEY_128		= 0x09;

	/** UNKNOWN = 0xff */
	public static final short	UNKNOWN					= 0xff;

	static final long			zigBeeTimeZero			= 946684800000L;	// 1/1/2000

	/**
	 * Marshal the passed value into a {@code ZigBeeDataOutput} stream,
	 * according the {@code dataType} argument. An
	 * {@code IllegalArgumentException} is thrown when the the passed
	 * {@code value} does not belong to the class allowed for the
	 * {@code dataType}.
	 * 
	 * <p>
	 * If the data type allows that, a null value is marshaled into ZCL Invalid
	 * Value for that data type.
	 *
	 * @param os a {@link ZigBeeDataOutput} stream where to stream the value.
	 *        This parameter cannot be null. If it is nul a
	 *        {@link NullPointerException} is thrown.
	 * 
	 * @param dataType The data type that have to be marshaled on the output
	 *        stream.
	 * 
	 * @param value The value that have to be serialized on the output stream.
	 *        If null is passed this method outputs on the stream the ZigBee
	 *        invalid value related to the specified data type. If the data type
	 *        do not allow any invalid value and the passed value is null an
	 *        {@link IllegalArgumentException} is thrown.
	 * 
	 * @throws IllegalArgumentException Thrown when the the passed {@code value}
	 *         does not belong to the allowed class for the {@code dataType} as
	 *         described in the specification or when the value exceed the range
	 *         allowed by that type (i.e. length for Octet String data types).
	 */
	public static void serializeDataType(ZigBeeDataOutput os, short dataType, Object value) {

		if (os == null) {
			throw new NullPointerException();
		}

		if (value == null) {
			/*
			 * the passed value must be marshalled as ZCL Data Type Invalid
			 * Number
			 */

			if (dataType >= ZigBeeDataTypes.UNSIGNED_INTEGER_8 && dataType <= ZigBeeDataTypes.UNSIGNED_INTEGER_64) {
				// This is an Unsigned Integer Data Type

				if (value == null) {
					// serialize an Invalid Value for Unsigned Integers
					switch (dataType) {
						case ZigBeeDataTypes.UNSIGNED_INTEGER_8 :
						case ZigBeeDataTypes.UNSIGNED_INTEGER_16 :
						case ZigBeeDataTypes.UNSIGNED_INTEGER_24 :
						case ZigBeeDataTypes.UNSIGNED_INTEGER_32 :
							os.writeInt(0xffffffff, (dataType & 0x07) + 1);
							return;

						case ZigBeeDataTypes.UNSIGNED_INTEGER_40 :
						case ZigBeeDataTypes.UNSIGNED_INTEGER_48 :
						case ZigBeeDataTypes.UNSIGNED_INTEGER_56 :
						case ZigBeeDataTypes.UNSIGNED_INTEGER_64 :
							os.writeLong(0xffffffffffffffffL, (dataType & 0x07) + 1);
							return;

						default :
							break;

					}
				}
			} else {
				switch (dataType) {
					case ZigBeeDataTypes.BOOLEAN :
					case ZigBeeDataTypes.ENUMERATION_8 :
					case ZigBeeDataTypes.CHARACTER_STRING :
					case ZigBeeDataTypes.OCTET_STRING :
						os.writeByte((byte) 0xff);
						return;

					case ZigBeeDataTypes.ENUMERATION_16 :
					case ZigBeeDataTypes.LONG_CHARACTER_STRING :
					case ZigBeeDataTypes.LONG_OCTET_STRING :
					case ZigBeeDataTypes.CLUSTER_ID :
					case ZigBeeDataTypes.ATTRIBUTE_ID :
						os.writeInt(0xffff, 2);
						return;

					case ZigBeeDataTypes.FLOATING_SEMI :
						os.writeFloat(Float.NaN, 2);
						return;

					case ZigBeeDataTypes.FLOATING_SINGLE :
						os.writeFloat(Float.NaN, 4);
						return;

					case ZigBeeDataTypes.FLOATING_DOUBLE :
						os.writeDouble(Double.NaN);
						return;

					case ZigBeeDataTypes.ARRAY :
					case ZigBeeDataTypes.BAG :
					case ZigBeeDataTypes.SET :
					case ZigBeeDataTypes.STRUCTURE :
						// TODO
						break;

					case ZigBeeDataTypes.TIME_OF_DAY :
					case ZigBeeDataTypes.DATE :
					case ZigBeeDataTypes.UTC_TIME :
					case ZigBeeDataTypes.BACNET_OID :
						os.writeInt(0xffffffff, 4);
						return;

					case ZigBeeDataTypes.IEEE_ADDRESS :
						os.writeLong(0xffffffffffffffffL, 8);
						return;

					case ZigBeeDataTypes.NO_DATA :
						// do nothing!!!
						return;

					default :
						break;
				}
			}
		} else {
			int size = (dataType & 0x07) + 1;
			// serialize the actual value
			switch (dataType) {
				case ZigBeeDataTypes.UNSIGNED_INTEGER_8 :
				case ZigBeeDataTypes.GENERAL_DATA_8 :
				case ZigBeeDataTypes.BITMAP_8 :
				case ZigBeeDataTypes.ENUMERATION_8 :
					if (value instanceof Short) {
						os.writeInt((((Number) value).shortValue()), size);
					} else {
						throw new IllegalArgumentException();
					}
					return;

				case ZigBeeDataTypes.UNSIGNED_INTEGER_16 :
				case ZigBeeDataTypes.UNSIGNED_INTEGER_24 :
				case ZigBeeDataTypes.GENERAL_DATA_16 :
				case ZigBeeDataTypes.GENERAL_DATA_24 :
				case ZigBeeDataTypes.BITMAP_16 :
				case ZigBeeDataTypes.BITMAP_24 :

				case ZigBeeDataTypes.ENUMERATION_16 :
					if (value instanceof Integer) {
						os.writeInt((((Number) value).intValue()), size);
					} else {
						throw new IllegalArgumentException();
					}
					return;

				case ZigBeeDataTypes.UNSIGNED_INTEGER_32 :
				case ZigBeeDataTypes.UNSIGNED_INTEGER_40 :
				case ZigBeeDataTypes.UNSIGNED_INTEGER_48 :
				case ZigBeeDataTypes.UNSIGNED_INTEGER_56 :
				case ZigBeeDataTypes.GENERAL_DATA_32 :
				case ZigBeeDataTypes.GENERAL_DATA_40 :
				case ZigBeeDataTypes.GENERAL_DATA_48 :
				case ZigBeeDataTypes.GENERAL_DATA_56 :
				case ZigBeeDataTypes.BITMAP_32 :
				case ZigBeeDataTypes.BITMAP_40 :
				case ZigBeeDataTypes.BITMAP_48 :
				case ZigBeeDataTypes.BITMAP_56 :
					if (value instanceof Long) {
						os.writeLong((((Number) value).longValue()), size);
					} else {
						throw new IllegalArgumentException();
					}
					return;

				case ZigBeeDataTypes.UNSIGNED_INTEGER_64 :
				case ZigBeeDataTypes.GENERAL_DATA_64 :
				case ZigBeeDataTypes.BITMAP_64 :
					if (value instanceof Long) {
						os.writeLong((((Number) value).longValue()), size);
					} else {
						throw new IllegalArgumentException();
					}
					return;

				case ZigBeeDataTypes.SIGNED_INTEGER_8 :
				case ZigBeeDataTypes.SIGNED_INTEGER_16 :
					if (value instanceof Short) {
						os.writeInt((((Short) value).shortValue()), size);
					} else {
						throw new IllegalArgumentException();
					}
					return;

				case ZigBeeDataTypes.SIGNED_INTEGER_24 :
				case ZigBeeDataTypes.SIGNED_INTEGER_32 :
					if (value instanceof Integer) {
						os.writeInt((((Integer) value).intValue()), size);
					} else {
						throw new IllegalArgumentException();
					}
					return;

				case ZigBeeDataTypes.SIGNED_INTEGER_40 :
				case ZigBeeDataTypes.SIGNED_INTEGER_48 :
				case ZigBeeDataTypes.SIGNED_INTEGER_56 :
					if (value instanceof Number) {
						os.writeLong((((Long) value).longValue()), size);
					} else {
						throw new IllegalArgumentException();
					}
					return;

				case ZigBeeDataTypes.SIGNED_INTEGER_64 :
					if (value instanceof Long) {
						os.writeLong((((Long) value).longValue()), size);
					} else {
						throw new IllegalArgumentException();
					}
					return;

				case ZigBeeDataTypes.BOOLEAN :
					boolean b = ((Boolean) value).booleanValue();
					os.writeByte((byte) (b ? 1 : 0));
					return;

				case ZigBeeDataTypes.CHARACTER_STRING : {
					String s = (String) value;
					if (s.length() > 0xfe) {
						throw new IllegalArgumentException();
					}
					os.writeByte((byte) s.length());
					os.writeBytes(s.getBytes(), s.length());
					return;
				}

				case ZigBeeDataTypes.OCTET_STRING : {
					byte[] array = (byte[]) value;
					if (array.length > 0xfe) {
						throw new IllegalArgumentException();
					}
					os.writeByte((byte) array.length);
					os.writeBytes(array, array.length);
					return;
				}

				case ZigBeeDataTypes.LONG_CHARACTER_STRING : {
					String s = (String) value;
					if (s.length() > 0xfffe) {
						throw new IllegalArgumentException();
					}
					os.writeByte((byte) s.length());
					os.writeBytes(s.getBytes(), s.length());
					return;
				}

				case ZigBeeDataTypes.LONG_OCTET_STRING : {
					byte[] array = (byte[]) value;
					if (array.length > 0xfffe) {
						throw new IllegalArgumentException();
					}
					os.writeByte((byte) array.length);
					os.writeBytes(array, array.length);
					return;
				}

				case ZigBeeDataTypes.CLUSTER_ID :
				case ZigBeeDataTypes.ATTRIBUTE_ID : {
					if (value instanceof Integer) {
						int i = ((Integer) value).intValue();
						os.writeInt(i, 2);
					} else {
						throw new IllegalArgumentException();
					}
					return;
				}

				case ZigBeeDataTypes.FLOATING_SEMI : {
					float f = ((Float) value).floatValue();
					os.writeFloat(f, 2);
					return;
				}

				case ZigBeeDataTypes.FLOATING_SINGLE : {
					float f = ((Float) value).floatValue();
					os.writeFloat(f, 4);
					return;
				}

				case ZigBeeDataTypes.FLOATING_DOUBLE : {
					double d = ((Double) value).doubleValue();
					os.writeDouble(d);
					return;
				}

				case ZigBeeDataTypes.ARRAY :
				case ZigBeeDataTypes.BAG :
				case ZigBeeDataTypes.SET :
				case ZigBeeDataTypes.STRUCTURE :
					// TODO
					break;

				case ZigBeeDataTypes.TIME_OF_DAY : {
					Date d = (Date) value;
					Calendar c = GregorianCalendar.getInstance();
					c.setTime(d);
					os.writeByte((byte) c.get(Calendar.HOUR_OF_DAY));
					os.writeByte((byte) c.get(Calendar.MINUTE));
					os.writeByte((byte) c.get(Calendar.SECOND));
					os.writeByte((byte) c.get(Calendar.MILLISECOND / 10));
					return;
				}
				case ZigBeeDataTypes.DATE : {
					Date d = (Date) value;
					Calendar c = GregorianCalendar.getInstance();
					c.setTime(d);
					os.writeByte((byte) c.get(Calendar.YEAR));
					os.writeByte((byte) c.get(Calendar.MONTH));
					os.writeByte((byte) c.get(Calendar.DAY_OF_MONTH));
					os.writeByte((byte) c.get(Calendar.DAY_OF_WEEK));
					return;
				}

				case ZigBeeDataTypes.UTC_TIME : {
					Date d = (Date) value;
					// TODO: the following value is a constant!

					long utc = d.getTime() - zigBeeTimeZero;
					os.writeInt((int) utc, 4);
					return;
				}

				case ZigBeeDataTypes.IEEE_ADDRESS :
					if (value instanceof Number) {
						os.writeLong((((Number) value).longValue()), 8);
					} else {
						throw new IllegalArgumentException();
					}
					return;

				case ZigBeeDataTypes.SECURITY_KEY_128 : {
					byte[] array = (byte[]) value;
					if (array.length != 8) {
						throw new IllegalArgumentException();
					}
					os.writeBytes(array, array.length);
					return;
				}

				default :
					break;
			}
		}

		throw new IllegalArgumentException();
	}

	/**
	 * @param is A valid {@link ZigBeeDataInput} stream instance. This parameter
	 *        cannot be null.
	 * 
	 * @param dataType The data type that have to be deserialized. This value
	 *        must be one of the valid data types constants defined at the
	 *        beginning of this interface, otherwise an IllegalArgumentException
	 *        exception is thrown.
	 * 
	 * @return The deserialized object. The returned value is null if the
	 *         deserialized value is equal to the <em>Invalid Value</em> for the
	 *         specified dataType.
	 * 
	 * @throws IOException in case of problems while deserializing the
	 *         {@code ZigBeeDataInput}. For instance this could happen if in the
	 *         stream the remaining bytes are not enough to be able to unmarshal
	 *         the requested data type.
	 * 
	 * @throws IllegalArgumentException if the passed {@code dataType} is not
	 *         supported.
	 * 
	 * @throws NullPointerException if the passed {@code ZigBeeDataInput} is
	 *         null
	 */
	public static Object deserializeDataType(ZigBeeDataInput is, short dataType) throws IOException {
		if (is == null) {
			throw new NullPointerException();
		}
		switch (dataType) {
			case ZigBeeDataTypes.GENERAL_DATA_8 :
			case ZigBeeDataTypes.BITMAP_8 :
			case ZigBeeDataTypes.ENUMERATION_8 : {
				short s = (short) (is.readInt(1) & 0xff);
				return new Short(s);
			}

			case ZigBeeDataTypes.GENERAL_DATA_16 :
			case ZigBeeDataTypes.BITMAP_16 :
			case ZigBeeDataTypes.ENUMERATION_16 : {
				int i = is.readInt(2) & 0xffff;
				return new Integer(i);
			}

			case ZigBeeDataTypes.GENERAL_DATA_24 :
			case ZigBeeDataTypes.BITMAP_24 : {
				int i = is.readInt(3) & 0xffffff;
				return new Integer(i);
			}

			case ZigBeeDataTypes.GENERAL_DATA_32 :
			case ZigBeeDataTypes.BITMAP_32 : {
				long l = is.readLong(4) & 0xffffffff;
				return new Long(l);
			}

			case ZigBeeDataTypes.GENERAL_DATA_40 :
			case ZigBeeDataTypes.BITMAP_40 : {
				long l = is.readLong(5) & 0xffffffffffL;
				if (l == 0xffffffff) {
					return null;
				}
				return new Long(l);
			}

			case ZigBeeDataTypes.GENERAL_DATA_48 :
			case ZigBeeDataTypes.BITMAP_48 : {
				long l = is.readLong(6) & 0xffffffffffffL;
				return new Long(l);
			}

			case ZigBeeDataTypes.GENERAL_DATA_56 :
			case ZigBeeDataTypes.BITMAP_56 : {
				long l = is.readLong(7) & 0xffffffffffffffL;
				return new Long(l);
			}

			case ZigBeeDataTypes.GENERAL_DATA_64 :
			case ZigBeeDataTypes.BITMAP_64 : {
				long l = is.readLong(8) & 0xffffffffffffffffL;
				return BigInteger.valueOf(l);
			}

			case ZigBeeDataTypes.UNSIGNED_INTEGER_8 : {
				short s = (short) (is.readInt(1) & 0xff);
				if (s == 0xff) {
					return null;
				}
				return new Short(s);
			}

			case ZigBeeDataTypes.UNSIGNED_INTEGER_16 : {
				int i = is.readInt(2) & 0xffff;
				if (i == 0xffff) {
					return null;
				}
				return new Integer(i);
			}

			case ZigBeeDataTypes.UNSIGNED_INTEGER_24 : {
				int i = is.readInt(3) & 0xffffff;
				if (i == 0xffffff) {
					return null;
				}
				return new Integer(i);
			}

			case ZigBeeDataTypes.UNSIGNED_INTEGER_32 : {
				long l = is.readLong(4) & 0xffffffff;
				if (l == 0xffffffffL) {
					return null;
				}
				return new Long(l);
			}

			case ZigBeeDataTypes.UNSIGNED_INTEGER_40 : {
				long l = is.readLong(5) & 0xffffffffffL;
				if (l == 0xffffffffffL) {
					return null;
				}
				return new Long(l);
			}

			case ZigBeeDataTypes.UNSIGNED_INTEGER_48 : {
				long l = is.readLong(6) & 0xffffffffffffL;
				if (l == 0xffffffffffffL) {
					return null;
				}
				return new Long(l);
			}

			case ZigBeeDataTypes.UNSIGNED_INTEGER_56 : {
				long l = is.readLong(7) & 0xffffffffffffffL;
				if (l == 0xffffffffffffffL) {
					return null;
				}
				return new Long(l);
			}

			case ZigBeeDataTypes.UNSIGNED_INTEGER_64 : {
				long l = is.readLong(8) & 0xffffffffffffffffL;
				// BigInteger a = BigInteger.valueOf(l);
				// a.and(BigInteger.valueOf(0xffffffffffffffffL));
				if (l == 0xffffffffffffffffL) {
					return null;
				}
				// FIXME find a better way for doing this!!!
				BigInteger bl = BigInteger.valueOf(l & 0xffffffffL);
				BigInteger bh = BigInteger.valueOf(l >>> 32).shiftLeft(32);

				BigInteger bi = bh.or(bl);

				return bi;
			}

			case ZigBeeDataTypes.SIGNED_INTEGER_8 : {
				short s = is.readByte();
				if (s == Short.MIN_VALUE) {
					return null;
				}
				return new Short(s);
			}

			case ZigBeeDataTypes.SIGNED_INTEGER_16 : {
				short s = (short) is.readInt(2);
				if (s == Short.MIN_VALUE) {
					return null;
				}
				return new Short(s);
			}

			case ZigBeeDataTypes.SIGNED_INTEGER_24 : {
				int i = is.readInt(3);
				if (i == Integer.MIN_VALUE) {
					return null;
				}
				return new Integer(i);
			}

			case ZigBeeDataTypes.SIGNED_INTEGER_32 : {
				long l = is.readLong(4);
				if (l == Long.MIN_VALUE) {
					return null;
				}
				return new Long(l);
			}

			case ZigBeeDataTypes.SIGNED_INTEGER_40 : {
				long l = is.readLong(5);
				if (l == Long.MIN_VALUE) {
					return null;
				}
				return new Long(l);
			}

			case ZigBeeDataTypes.SIGNED_INTEGER_48 : {
				long l = is.readLong(6);
				if (l == Long.MIN_VALUE) {
					return null;
				}
				return new Long(l);
			}

			case ZigBeeDataTypes.SIGNED_INTEGER_56 : {
				long l = is.readLong(7);
				if (l == Long.MIN_VALUE) {
					return null;
				}
				return new Long(l);
			}

			case ZigBeeDataTypes.SIGNED_INTEGER_64 : {
				long l = is.readLong(8);
				if (l == Long.MIN_VALUE) {
					return null;
				}
				return new Long(l);
			}

			case ZigBeeDataTypes.BOOLEAN : {
				byte b = is.readByte();
				if (b == -1) {
					return null;
				} else {
					return (b > 0) ? Boolean.TRUE : Boolean.FALSE;
				}
			}

			case ZigBeeDataTypes.CHARACTER_STRING : {
				byte len = is.readByte();
				if (len == -1) {
					return null;
				}
				byte[] array = is.readBytes(len & 0xff);
				return new String(array);
			}

			case ZigBeeDataTypes.OCTET_STRING : {
				byte len = is.readByte();
				if (len == -1) {
					return null;
				}
				byte[] array = is.readBytes(len & 0xff);
				return new String(array);
			}

			case ZigBeeDataTypes.LONG_CHARACTER_STRING : {
				short len = (short) is.readInt(2);
				if (len == -1) {
					return null;
				}
				byte[] array = is.readBytes(len & 0xffff);
				return new String(array);
			}

			case ZigBeeDataTypes.LONG_OCTET_STRING : {
				short len = (short) is.readInt(2);
				if (len == -1) {
					return null;
				}
				byte[] array = is.readBytes(len & 0xffff);
				return new String(array);
			}

			case ZigBeeDataTypes.CLUSTER_ID :
			case ZigBeeDataTypes.ATTRIBUTE_ID : {
				int value = is.readInt(2);
				if (value == 0xffff) {
					return null;
				}
				return new Integer(value);
			}

			case ZigBeeDataTypes.FLOATING_SEMI : {
				float f = is.readFloat(2);
				return new Float(f);
			}

			case ZigBeeDataTypes.FLOATING_SINGLE : {
				float f = is.readFloat(4);
				// this is the right way to compare d with NaN. The == operator
				// doesn't seem to work!
				if (Float.compare(f, Float.NaN) == 0) {
					return null;
				}
				return new Float(f);
			}

			case ZigBeeDataTypes.FLOATING_DOUBLE : {
				double d = is.readDouble();
				// this is the right way to compare d with NaN. The == operator
				// doesn't seem to work!
				if (Double.compare(d, Double.NaN) == 0) {
					return null;
				}
				return new Double(d);
			}

			case ZigBeeDataTypes.ARRAY :
			case ZigBeeDataTypes.BAG :
			case ZigBeeDataTypes.SET :
			case ZigBeeDataTypes.STRUCTURE :
				// TODO
				break;

			case ZigBeeDataTypes.TIME_OF_DAY : {
				int value = is.readInt(4);
				if (value == -1) {
					return null;
				}

				// FIXSPEC: it is not clear whether or not we have to
				// map to Date. On marshalling is OK but when we unmarshall
				// we need to convert the content in an actual date
				// this is not the meaning of this datatype.
				return new Date();
			}

			case ZigBeeDataTypes.DATE : {
				int value = is.readInt(4);
				if (value == -1) {
					return null;
				}
				Calendar c = GregorianCalendar.getInstance();

				// NOTE: the Year byte contains years since 1900!
				c.set(Calendar.YEAR, (is.readByte() & 0xff) + 1900);
				c.set(Calendar.MONTH, is.readByte() & 0xff);
				c.set(Calendar.DAY_OF_MONTH, is.readByte() & 0xff);
				c.set(Calendar.DAY_OF_WEEK, is.readByte() & 0xff);

				return c.getTime();
			}

			case ZigBeeDataTypes.UTC_TIME : {
				int value = is.readInt(4);
				if (value == -1) {
					return null;
				}
				return new Date((value & 0xffffffff) + zigBeeTimeZero);
			}

			case ZigBeeDataTypes.IEEE_ADDRESS : {
				// FIXME: attention who is in charge of swapping bytes? Now we
				// are doing that here or we can add a readBytesSwapped() method
				// in IS.

				byte[] array = is.readBytes(8);
				swap(array);
				BigInteger invalidValue = new BigInteger("-1");
				BigInteger value = new BigInteger(array);
				if (value.equals(invalidValue)) {
					return null;
				}
				return value;
			}

			case ZigBeeDataTypes.SECURITY_KEY_128 : {
				byte[] array = is.readBytes(8);
				swap(array);
				return array;
			}

			default :
				break;
		}

		throw new IllegalArgumentException();
	}

	private static byte[] swap(byte[] array) {
		int j = array.length - 1;
		for (int i = 0; i < array.length / 2; i++) {
			byte tmp = array[i];
			array[i] = array[j];
			array[j] = tmp;
			j--;
		}
		return array;
	}
}

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

/**
 * This interface contains the constants that are used internally by these API
 * to represent the ZCL data types.
 * 
 * <p>
 * This constants do not match the values provided by the ZigBee specification,
 * and follows the rules below:
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
 * Related documentation:
 * [1] ZigBee Cluster Library specification, Document 075123r04ZB, May 29,
 * 2012.
 * 
 * 
 * @author $Id$
 */
public interface ZigBeeDataTypes {

	/**
	 * 2.5.2.1 No Data Type
	 * 
	 * <p>
	 * The no data type is a special type to represent an attribute with no
	 * associated data.
	 */
	public static final short	NO_DATA					= 0x00;

	/**
	 * 2.5.2.2 General Data (8, 16, 24, 32, 40, 48, 56 and 64-bit)
	 * 
	 * <p>
	 * This type has no rules about its use, and may be used when a data element
	 * is needed but its use does not conform to any of the standard types.
	 */
	public static final short	GENERAL_DATA_8			= 0x50;
	/** General Data 16-bit*/
	public static final short	GENERAL_DATA_16			= 0x51;
	/** General Data 24-bit*/
	public static final short	GENERAL_DATA_24			= 0x52;
	/** General Data 32-bit*/
	public static final short	GENERAL_DATA_32			= 0x53;
	/** General Data 40-bit*/
	public static final short	GENERAL_DATA_40			= 0x54;
	/** General Data 48-bit*/
	public static final short	GENERAL_DATA_48			= 0x55;
	/** General Data 56-bit*/
	public static final short	GENERAL_DATA_56			= 0x56;
	/** General Data 64-bit*/
	public static final short	GENERAL_DATA_64			= 0x57;

	/**
	 * 2.5.2.3 Boolean
	 * 
	 * <p>
	 * The Boolean type represents a logical value, either FALSE (0x00) or TRUE
	 * (0x01). The value 0xff represents an invalid value of this type. All
	 * other values of this type are forbidden.
	 */
	public static final short	BOOLEAN					= 0x01;

	/**
	 * 2.5.2.4 Bitmap (8, 16, 24, 32, 40, 48, 56 and 64-bit)
	 * 
	 * <p>
	 * The Bitmap type holds 8, 16, 24, 32, 40, 48, 56 or 64 logical values, one
	 * per bit, depending on its length. There is no value that represents an
	 * invalid value of this type.
	 */
	public static final short	BITMAP_8				= 0x58;
	/** Bitmap16-bit*/
	public static final short	BITMAP_16				= 0x59;
	/** Bitmap 24-bit*/
	public static final short	BITMAP_24				= 0x5a;
	/** Bitmap 32-bit*/
	public static final short	BITMAP_32				= 0x5b;
	/** Bitmap 40-bit*/
	public static final short	BITMAP_40				= 0x5c;
	/** Bitmap 48-bit*/
	public static final short	BITMAP_48				= 0x5d;
	/** Bitmap 56-bit*/
	public static final short	BITMAP_56				= 0x5e;
	/** Bitmap 64-bit*/
	public static final short	BITMAP_64				= 0x5f;

	/**
	 * 2.5.2.5 Unsigned Integer (8, 16, 24, 32, 40, 48, 56 and 64-bit)
	 * 
	 * <p>
	 * This type represents an unsigned integer with a decimal range of 0 to
	 * 2^8-1, 0 to 2^16-1, 0 to 2^24-1, 0 to 2^32-1, 0 to 2^40-1, 0 to 2^48-1, 0
	 * to 2^56-1, or 0 to 2^64-1, depending on its length. The values that
	 * represents an invalid value of this type are 0xff, 0xffff, 0xffffff,
	 * 0xffffffff, 0xffffffffff, 0xffffffffffff, 0xffffffffffffff and
	 * 0xffffffffffffffff respectively.
	 */
	public static final short	UNSIGNED_INTEGER_8		= 0x60;
	/** Unsigned Integer 16-bit*/
	public static final short	UNSIGNED_INTEGER_16		= 0x61;
	/** Unsigned Integer 24-bit*/
	public static final short	UNSIGNED_INTEGER_24		= 0x62;
	/** Unsigned Integer 32-bit*/
	public static final short	UNSIGNED_INTEGER_32		= 0x63;
	/** Unsigned Integer 40-bit*/
	public static final short	UNSIGNED_INTEGER_40		= 0x64;
	/** Unsigned Integer 48-bit*/
	public static final short	UNSIGNED_INTEGER_48		= 0x65;
	/** Unsigned Integer 56-bit*/
	public static final short	UNSIGNED_INTEGER_56		= 0x66;
	/** Unsigned Integer 64-bit*/
	public static final short	UNSIGNED_INTEGER_64		= 0x67;

	/**
	 * 2.5.2.6 Signed Integer (8, 16, 24, 32, 40, 48, 56 and 64-bit)
	 * 
	 * <p>
	 * This type represents a signed integer with a decimal range of -(2^7-1) to
	 * 2^7-1, - (2^15-1) to 2^15-1, -(2^23-1) to 2^23-1, -(2^31-1) to 2^31-1,
	 * -(2^39-1) to 2^39-1, -(2^47-1) to 2^47-1, -(2^55-1) to 2^55-1, or
	 * -(2^63-1) to 2^63-1, depending on its length. The values that represents
	 * an invalid value of this type are 0x80, 0x8000, 0x800000, 0x80000000,
	 * 0x8000000000, 0x800000000000, 0x80000000000000 and 0x8000000000000000
	 * respectively.
	 */
	public static final short	SIGNED_INTEGER_8		= 0xe0;
	/** Signed Integer 16-bit*/
	public static final short	SIGNED_INTEGER_16		= 0xe1;
	/** Signed Integer 24-bit*/
	public static final short	SIGNED_INTEGER_24		= 0xe2;
	/** Signed Integer 32-bit*/
	public static final short	SIGNED_INTEGER_32		= 0xe3;
	/** Signed Integer 40-bit*/
	public static final short	SIGNED_INTEGER_40		= 0xe4;
	/** Signed Integer 48-bit*/
	public static final short	SIGNED_INTEGER_48		= 0xe5;
	/** Signed Integer 56-bit*/
	public static final short	SIGNED_INTEGER_56		= 0xe6;
	/** Signed Integer 64-bit*/
	public static final short	SIGNED_INTEGER_64		= 0xe7;

	/**
	 * 2.5.2.7 Enumeration (8-bit, 16-bit)
	 * 
	 * The Enumeration type represents an index into a lookup table to determine
	 * the final value. The values 0xff and 0xffff represent invalid values of
	 * the 8-bit and 16- bit types respectively.
	 */
	public static final short	ENUMERATION_8			= 0x70;
	/** Enumeration 16-bit*/
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
	 * <p>
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
	 * <p>
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
	 * <p>
	 * See reference on top of this class.
	 */
	public static final short	CHARACTER_STRING		= 0x79;

	/**
	 * 2.5.2.13 Long Octet String
	 * 
	 * <p>
	 * The long octet string data type contains data in an application-defined
	 * format, not defined in this specification.
	 * 
	 * <p>
	 * See reference on top of this class.
	 */
	public static final short	LONG_OCTET_STRING		= 0x7a;

	/**
	 * 2.5.2.14 Long Character String
	 * 
	 * <p>
	 * The long character string data type contains data octets encoding
	 * characters according to the language and character set field of the
	 * complex descriptor.
	 * 
	 * <p>
	 * See reference on top of this class.
	 */
	public static final short	LONG_CHARACTER_STRING	= 0x7b;

	/**
	 *
	 * 2.5.2.15 Array
	 *
	 * <p>
	 * An array is an ordered sequence of zero or more elements, all of the same
	 * data type. This data type may be any ZCL defined data type, including
	 * array, structure, bag or set. The total nesting depth is limited to 15,
	 * and may be further limited by any relevant profile or application.
	 * 
	 * <p>
	 * See reference on top of this class.
	 */
	public static final short	ARRAY					= 0x10;

	/**
	 * 2.5.2.16 Structure
	 * 
	 * <p>
	 * A structure is an ordered sequence of elements, which may be of different
	 * data types. Each data type may be any ZCL defined data type, including
	 * array, structure, bag or set. The total nesting depth is limited to 15,
	 * and may be further limited by any relevant profile or application.
	 * 
	 * <p>
	 * See reference on top of this class.
	 */
	public static final short	STRUCTURE				= 0x11;

	/**
	 * 2.5.2.17 Set
	 * 
	 * <p>
	 * A set is a collection of elements with no associated order. Each element
	 * has the same data type, which may be any ZCL defined data type, including
	 * array, structure, bag or set. The nesting depth is limited to 15, and may
	 * be further limited by any relevant profile or application.
	 * 
	 * <p>
	 * See reference on top of this class.
	 */
	public static final short	SET						= 0x12;

	/**
	 * 2.5.2.18 Bag
	 * 
	 * <p>
	 * A bag behaves exactly the same as a set, except that the restriction that
	 * no two elements may have the same value is removed.
	 */
	public static final short	BAG						= 0x13;

	/**
	 * 2.5.2.19 Time of Day
	 * 
	 * The Time of Day data type shall be formatted as illustrated in spec.
	 * 
	 * <p>
	 * See reference on top of this class.
	 */
	public static final short	TIME_OF_DAY				= 0x02;

	/**
	 * 2.5.2.20 Date
	 * 
	 * <p>
	 * The Time of day data type shall be formatted as illustrated in spec.
	 * 
	 * <p>
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
	 * <p>
	 * This type represents a cluster identifier as defined in spec.
	 * 
	 * <p>
	 * See reference on top of this class.
	 */
	public static final short	CLUSTER_ID				= 0x05;

	/**
	 * 2.5.2.23 Attribute ID
	 * 
	 * <p>
	 * This type represents an attribute identifier as defined in spec.
	 * 
	 * <p>
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
	 * <p>
	 * The IEEE Address data type is a 64-bit IEEE address that is unique to
	 * every ZigBee device. A value of 0xffffffffffffffff indicates that the
	 * address is unknown.
	 */
	public static final short	IEEE_ADDRESS			= 0x08;

	/**
	 * 2.5.2.26 128-bit Security Key
	 * 
	 * <p>
	 * The 128-bit Security Key data type is for use in ZigBee security, and may
	 * take any 128-bit value.
	 */
	public static final short	SECURITY_KEY_128		= 0x09;

	/** UNKNOWN = 0xff */
	public static final short	UNKNOWN					= 0xff;
}

/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.service.zigbee;

/**
 * This class contains the constants that are used internally by these API to
 * represent the ZCL data types.
 * 
 * <p>
 * These constants do not match the values used in the ZigBee specification, but
 * follow the rules below:
 * <p>
 * <ul>
 * <li><em>bit 0-3</em>: if bit 6 is one, these bits represents the size of the
 * data type in bytes.</li>
 * <li><em>bit 6</em>: if set to 1 bits 0-3 represents the size of the data type
 * in bytes.</li>
 * </ul>
 * 
 * <p>
 * Related documentation: [1] ZigBee Cluster Library specification, Document
 * 075123r04ZB, May 29, 2012.
 * 
 * @author $Id$
 */
public class ZigBeeDataTypes {
	private ZigBeeDataTypes() {
	}

	/**
	 * According to ZigBee Cluster Library [1], the no data type represents an
	 * attribute with no associated data.
	 */
	public static final short	NO_DATA					= 0x00;

	/**
	 * According to ZigBee Cluster Library [1], the General Data type may be
	 * used when a data element is needed but its use does not conform to any of
	 * other types. The General Data type is defined with several sizes: 8, 16,
	 * 24, 32, 40, 48, 56 and 64 bits.
	 */
	public static final short	GENERAL_DATA_8			= 0x50;
	/** General Data 16-bit */
	public static final short	GENERAL_DATA_16			= 0x51;
	/** General Data 24-bit */
	public static final short	GENERAL_DATA_24			= 0x52;
	/** General Data 32-bit */
	public static final short	GENERAL_DATA_32			= 0x53;
	/** General Data 40-bit */
	public static final short	GENERAL_DATA_40			= 0x54;
	/** General Data 48-bit */
	public static final short	GENERAL_DATA_48			= 0x55;
	/** General Data 56-bit */
	public static final short	GENERAL_DATA_56			= 0x56;
	/** General Data 64-bit */
	public static final short	GENERAL_DATA_64			= 0x57;

	/**
	 * According to ZigBee Cluster Library [1], the Boolean type represents a
	 * logical value, either FALSE (0x00) or TRUE (0x01). The value 0xff
	 * represents an invalid value of this type. All other values of this type
	 * are forbidden.
	 */
	public static final short	BOOLEAN					= 0x01;

	/**
	 * According to ZigBee Cluster Library [1], the Bitmap type holds logical
	 * values, one per bit, depending on its length. There is no value that
	 * represents an invalid value of this type. The Bitmap type is defined with
	 * several sizes: 8, 16, 24, 32, 40, 48, 56 and 64 bits.
	 */
	public static final short	BITMAP_8				= 0x58;
	/** Bitmap16-bit */
	public static final short	BITMAP_16				= 0x59;
	/** Bitmap 24-bit */
	public static final short	BITMAP_24				= 0x5a;
	/** Bitmap 32-bit */
	public static final short	BITMAP_32				= 0x5b;
	/** Bitmap 40-bit */
	public static final short	BITMAP_40				= 0x5c;
	/** Bitmap 48-bit */
	public static final short	BITMAP_48				= 0x5d;
	/** Bitmap 56-bit */
	public static final short	BITMAP_56				= 0x5e;
	/** Bitmap 64-bit */
	public static final short	BITMAP_64				= 0x5f;

	/**
	 * According to ZigBee Cluster Library [1], the Unsigned Integer type
	 * represents an unsigned integer with a decimal range of 0 to 2^8-1, 0 to
	 * 2^16-1, 0 to 2^24-1, 0 to 2^32-1, 0 to 2^40-1, 0 to 2^48-1, 0 to 2^56-1,
	 * or 0 to 2^64-1, depending on its length. The values that represents an
	 * invalid value of this type are 0xff, 0xffff, 0xffffff, 0xffffffff,
	 * 0xffffffffff, 0xffffffffffff, 0xffffffffffffff and 0xffffffffffffffff
	 * respectively. This type is defined with several sizes: 8, 16, 24, 32, 40,
	 * 48, 56 and 64 bits.
	 */
	public static final short	UNSIGNED_INTEGER_8		= 0x60;
	/** Unsigned Integer 16-bit */
	public static final short	UNSIGNED_INTEGER_16		= 0x61;
	/** Unsigned Integer 24-bit */
	public static final short	UNSIGNED_INTEGER_24		= 0x62;
	/** Unsigned Integer 32-bit */
	public static final short	UNSIGNED_INTEGER_32		= 0x63;
	/** Unsigned Integer 40-bit */
	public static final short	UNSIGNED_INTEGER_40		= 0x64;
	/** Unsigned Integer 48-bit */
	public static final short	UNSIGNED_INTEGER_48		= 0x65;
	/** Unsigned Integer 56-bit */
	public static final short	UNSIGNED_INTEGER_56		= 0x66;
	/** Unsigned Integer 64-bit */
	public static final short	UNSIGNED_INTEGER_64		= 0x67;

	/**
	 * According to ZigBee Cluster Library [1], the Signed Integer type
	 * represents a signed integer with a decimal range of -(2^7-1) to 2^7-1, -
	 * (2^15-1) to 2^15-1, -(2^23-1) to 2^23-1, -(2^31-1) to 2^31-1, -(2^39-1)
	 * to 2^39-1, -(2^47-1) to 2^47-1, -(2^55-1) to 2^55-1, or -(2^63-1) to
	 * 2^63-1, depending on its length. The values that represents an invalid
	 * value of this type are 0x80, 0x8000, 0x800000, 0x80000000, 0x8000000000,
	 * 0x800000000000, 0x80000000000000 and 0x8000000000000000 respectively.
	 * This type is defined with several sizes: 8, 16, 24, 32, 40, 48, 56 and 64
	 * bits.
	 */
	public static final short	SIGNED_INTEGER_8		= 0xe0;
	/** Signed Integer 16-bit */
	public static final short	SIGNED_INTEGER_16		= 0xe1;
	/** Signed Integer 24-bit */
	public static final short	SIGNED_INTEGER_24		= 0xe2;
	/** Signed Integer 32-bit */
	public static final short	SIGNED_INTEGER_32		= 0xe3;
	/** Signed Integer 40-bit */
	public static final short	SIGNED_INTEGER_40		= 0xe4;
	/** Signed Integer 48-bit */
	public static final short	SIGNED_INTEGER_48		= 0xe5;
	/** Signed Integer 56-bit */
	public static final short	SIGNED_INTEGER_56		= 0xe6;
	/** Signed Integer 64-bit */
	public static final short	SIGNED_INTEGER_64		= 0xe7;

	/**
	 * According to ZigBee Cluster Library [1], the Enumeration type represents
	 * an index into a lookup table to determine the final value. The values
	 * 0xff and 0xffff represent invalid values of the 8-bit and 16-bit types
	 * respectively.
	 */
	public static final short	ENUMERATION_8			= 0x70;
	/** Enumeration 16-bit */
	public static final short	ENUMERATION_16			= 0x71;

	/**
	 * According to ZigBee Cluster Library [1], the ZigBee semi-precision number
	 * format is based on the IEEE 754 standard for binary floating-point
	 * arithmetic.
	 */
	public static final short	FLOATING_SEMI			= 0xf8;

	/**
	 * According to ZigBee Cluster Library [1], the format of the single
	 * precision data type is based on the IEEE 754 standard for binary
	 * floating-point arithmetic.
	 */
	public static final short	FLOATING_SINGLE			= 0xf9;

	/**
	 * According to ZigBee Cluster Library [1], the format of the double
	 * precision data type is based on the IEEE 754 standard for binary
	 * floating-point arithmetic.
	 */
	public static final short	FLOATING_DOUBLE			= 0xfa;

	/**
	 * According to ZigBee Cluster Library [1], the Octet String data type
	 * contains data in application-defined formats.
	 */
	public static final short	OCTET_STRING			= 0x78;

	/**
	 * According to ZigBee Cluster Library [1], the Character String data type
	 * contains data octets encoding characters according to the language and
	 * character set field of the complex descriptor.
	 */
	public static final short	CHARACTER_STRING		= 0x79;

	/**
	 * According to ZigBee Cluster Library [1], the Long Octet String data type
	 * contains data in application-defined formats.
	 */
	public static final short	LONG_OCTET_STRING		= 0x7a;

	/**
	 * According to ZigBee Cluster Library [1], the Long Character String data
	 * type contains data octets encoding characters according to the language
	 * and character set field of the complex descriptor.
	 */
	public static final short	LONG_CHARACTER_STRING	= 0x7b;

	/**
	 * According to ZigBee Cluster Library [1], an Array is an ordered sequence
	 * of zero or more elements, all of the same data type. This data type may
	 * be any ZCL defined data type, including Array, Structure, Bag or Set. The
	 * total nesting depth is limited to 15.
	 */
	public static final short	ARRAY					= 0x10;

	/**
	 * According to ZigBee Cluster Library [1], a Structure is an ordered
	 * sequence of elements, which may be of different data types. Each data
	 * type may be any ZCL defined data type, including Array, Structure, Bag or
	 * Set. The total nesting depth is limited to 15.
	 */
	public static final short	STRUCTURE				= 0x11;

	/**
	 * According to ZigBee Cluster Library [1], a Set is a collection of
	 * elements with no associated order. Each element has the same data type,
	 * which may be any ZCL defined data type, including Array, Structure, Bag
	 * or Set. The nesting depth is limited to 15.
	 */
	public static final short	SET						= 0x12;

	/**
	 * According to ZigBee Cluster Library [1], a Bag behaves exactly the same
	 * as a Set, except that two elements may have the same value.
	 */
	public static final short	BAG						= 0x13;

	/**
	 * The Time of Day data type format is specified in section 2.5.2.19 of ZCL
	 * specification [1].
	 */
	public static final short	TIME_OF_DAY				= 0x02;

	/**
	 * The Date data type format is specified in section 2.5.2.20 of ZigBee
	 * Cluster Specification [1].
	 */
	public static final short	DATE					= 0x03;

	/**
	 * According to ZigBee Cluster Library [1], UTCTime is an unsigned 32-bit
	 * value representing the number of seconds since 0 hours, 0 minutes, 0
	 * seconds, on the 1st of January, 2000 UTC (Universal Coordinated Time).
	 * The value that represents an invalid value of this type is 0xffffffffff.
	 */
	public static final short	UTC_TIME				= 0x04;

	/**
	 * The type of a cluster identifier.
	 */
	public static final short	CLUSTER_ID				= 0x05;

	/**
	 * The type of an attribute identifier.
	 */
	public static final short	ATTRIBUTE_ID			= 0x06;

	/**
	 * According to ZigBee Cluster Library [1], the BACnet OID data type is
	 * included to allow interworking with BACnet. The format is described in
	 * the referenced standard.
	 */
	public static final short	BACNET_OID				= 0x07;

	/**
	 * According to ZigBee Cluster Library [1], the IEEE Address data type is a
	 * 64-bit IEEE address that is unique to every ZigBee device. A value of
	 * 0xffffffffffffffff indicates that the address is unknown.
	 */
	public static final short	IEEE_ADDRESS			= 0x08;

	/**
	 * According to ZigBee Cluster Library [1], the 128-bit Security Key data
	 * type is for use in ZigBee security, and may take any 128-bit value.
	 */
	public static final short	SECURITY_KEY_128		= 0x09;

	/**
	 * The UNKNOWN type is used when the data type is unknown.
	 */
	public static final short	UNKNOWN					= 0xff;
}

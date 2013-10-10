/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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
 * This interface represents all ZigBee data types, and contains the common
 * serialize/deserialize methods for the org.osgi.service.zigbee.types.*
 * 
 * @author Fabrice Blache
 * 
 * @version 1.0
 */
public class ZigBeeDataTypes {

	/**
	 * No data type The no data type is a special type to represent an attribute
	 * with no associated data.
	 */
	public static final short		NO_DATA					= 0x00;

	/**
	 * Boolean The Boolean type represents a logical value, either TRUE (0x00)
	 * or FALSE (0x01). The value 0xff represents an invalid value of this type.
	 * All other values of this type are forbidden
	 */
	public static final short		GENERAL_DATA_8			= 0x08;
	/** */
	public static final short		GENERAL_DATA_16			= 0x09;
	/** */
	public static final short		GENERAL_DATA_24			= 0x0a;
	/** */
	public static final short		GENERAL_DATA_32			= 0x0b;
	/** */
	public static final short		GENERAL_DATA_40			= 0x0c;
	/** */
	public static final short		GENERAL_DATA_48			= 0x0d;
	/** */
	public static final short		GENERAL_DATA_56			= 0x0e;
	/** */
	public static final short		GENERAL_DATA_64			= 0x0f;

	/**
	 * Boolean The Boolean type represents a logical value, either TRUE (0x00)
	 * or FALSE (0x01). The value 0xff represents an invalid value of this type.
	 * All other values of this type are forbidden.
	 */
	public static final short		BOOLEAN					= 0x10;

	/**
	 * Bitmap (8-bit, 16-bit, 24-bit and 32-bit) The Bitmap type holds 8, 16, 24
	 * or 32 logical values, one per bit, depending on its length. There is no
	 * value to represent an invalid value of this type.
	 */
	public static final short		BITMAP_8				= 0x18;
	/** */
	public static final short		BITMAP_16				= 0x19;
	/** */
	public static final short		BITMAP_24				= 0x1a;
	/** */
	public static final short		BITMAP_32				= 0x1b;
	/** */
	public static final short		BITMAP_40				= 0x1c;
	/** */
	public static final short		BITMAP_48				= 0x1d;
	/** */
	public static final short		BITMAP_56				= 0x1e;
	/** */
	public static final short		BITMAP_64				= 0x1f;

	/**
	 * Unsigned integer (8-bit, 16-bit, 24-bit and 32-bit) This type represents
	 * an unsigned integer with a decimal range of 0 to 28-1, 0 to 216-1, 0 to
	 * 224-1, or 0 to 232-1, depending on its length. The values that represents
	 * an invalid value of this type are 0xff, 0xffff, 0xffffff and 0xffffffff
	 * respectively.
	 */
	public static final short		UNSIGNED_INTEGER_8		= 0x20;
	/** */
	public static final short		UNSIGNED_INTEGER_16		= 0x21;
	/** */
	public static final short		UNSIGNED_INTEGER_24		= 0x22;
	/** */
	public static final short		UNSIGNED_INTEGER_32		= 0x23;
	/** */
	public static final short		UNSIGNED_INTEGER_40		= 0x24;
	/** */
	public static final short		UNSIGNED_INTEGER_48		= 0x25;
	/** */
	public static final short		UNSIGNED_INTEGER_56		= 0x26;
	/** */
	public static final short		UNSIGNED_INTEGER_64		= 0x27;

	/**
	 * Signed integer (8-bit, 16-bit, 24-bit and 32-bit) This type represents a
	 * signed integer with a decimal range of -(27-1) to 27-1, -(215-1) to
	 * 215-1, -(223-1) to 223-1, or -(231-1) to 231-1, depending on its length.
	 * The values that represents an invalid value of this type are 0x80,
	 * 0x8000, 0x800000 and 0x80000000 respectively.
	 */
	public static final short		SIGNED_INTEGER_8		= 0x28;
	/** */
	public static final short		SIGNED_INTEGER_16		= 0x29;
	/** */
	public static final short		SIGNED_INTEGER_24		= 0x2a;
	/** */
	public static final short		SIGNED_INTEGER_32		= 0x2b;
	/** */
	public static final short		SIGNED_INTEGER_40		= 0x2c;
	/** */
	public static final short		SIGNED_INTEGER_48		= 0x2d;
	/** */
	public static final short		SIGNED_INTEGER_56		= 0x2e;
	/** */
	public static final short		SIGNED_INTEGER_64		= 0x2f;

	/**
	 * Enumeration (8-bit) The Enumeration type represents an index into a
	 * lookup table to determine the final value. The value 0xff represents an
	 * invalid value of this type
	 */
	public static final short		ENUMERATION_8			= 0x30;
	/** */
	public static final short		ENUMERATION_16			= 0x31;

	/**
	 * Semi-precision The ZigBee semi-precision number format is based on the
	 * IEEE 754 standard for binary floating-point arithmetic [R11]. This number
	 * format should be used very sparingly, when absolutely necessary, keeping
	 * in mind the code and processing required supporting it. The value is
	 * calculated as: Value = -1Sign * (Hidden + Mantissa/1024) * 2
	 * (Exponent-15)
	 */
	public static final short		FLOATING_SEMI			= 0x38;

	/**
	 * Single precision The format of the single precision data type is based on
	 * the IEEE 754 standard for binary floating-point arithmetic [R11]. This
	 * number format should be used very sparingly, when absolutely necessary,
	 * keeping in mind the code and processing required supporting it. The
	 * format and interpretation of values of this data type follow the same
	 * rules as given for the semi-precision data type, but with shorter
	 * sub-fields, as follows. Length of mantissa = 23 bits, length of exponent
	 * = 8 bits For further details, see [R11]. = 0x43;
	 */
	public static final short		FLOATING_SINGLE			= 0x39;

	/**
	 * Double precision The format of the double precision data type is based on
	 * the IEEE 754 standard for binary floating-point arithmetic [R11]. This
	 * number format should be used very sparingly, when absolutely necessary,
	 * keeping in mind the code and processing required supporting it. The
	 * format and interpretation of values of this data type follow the same
	 * rules as given for the semi-precision data type, but with shorter
	 * sub-fields, as follows. Length of mantissa = 52 bits, length of exponent
	 * = 11 bits For further details, see [R11].
	 */
	public static final short		FLOATING_DOUBLE			= 0x3a;

	/**
	 * The octet string data type contains data in an application-defined
	 * format, not defined in this specification. The octet string data type
	 * shall be formatted as illustrated in Error! Reference source not found..
	 * 
	 * Octets: 1 Variable Octet count Octet data Figure 27 - Format of the octet
	 * string type The octet count sub-field is one octet in length and
	 * specifies the number of octets contained in the octet data sub-field.
	 * Setting this sub-field to 0x00 represents an octet string with no octet
	 * data (an "empty string"). Setting this sub-field to 0xff represents an
	 * invalid octet string value. In both cases the octet data sub-field has
	 * zero length. The octet data sub-field is n octets in length, where n is
	 * the value of the octet count sub-field. This sub-field contains the
	 * application-defined data.
	 */
	public static final short		OCTET_STRING			= 0x41;

	/**
	 * Character string The character string data type contains data octets
	 * encoding characters according to the language and character set field of
	 * the complex descriptor. The character string data type shall be formatted
	 * as illustrated in Figure 28.
	 * 
	 * Octets: 1 Variable Character count Character data
	 * 
	 * The character count sub-field is one octet in length and specifies the
	 * number of characters, encoded according to the language and character set
	 * field of the complex descriptor (see [R2]), contained in the character
	 * data sub-field. Setting this sub-field to 0x00 represents a character
	 * string with no character data (an "empty string"). Setting this sub-field
	 * to 0xff represents an invalid character string value. In both cases the
	 * character data sub-field has zero length. The character data sub-field is
	 * e*n octets in length, where e is the size of the character, as specified
	 * by the language and character set field of the complex descriptor, and n
	 * is the value of the character count sub-field. This sub-field contains
	 * the encoded characters that comprise the desired character string. A
	 * character string with no contents, i.e. with the character count
	 * sub-field equal to 0x00 and a zero length character data sub-field, shall
	 * be referred to as an 'empty string'.
	 * 
	 */
	public static final short		CHARACTER_STRING		= 0x42;
	/** */
	public static final short		LONG_OCTET_STRING		= 0x43;
	/** */
	public static final short		LONG_CHARACTER_STRING	= 0x44;
	/** */
	public static final short		ARRAY					= 0x48;
	/** */
	public static final short		STRUCTURE				= 0x4c;
	/** */
	public static final short		SET						= 0x50;
	/** */
	public static final short		BAG						= 0x51;

	/**
	 * Time of day The Time of day data type shall be formatted as illustrated
	 * in Figure 29.
	 * 
	 * Octets: 1 1 1 1 Hours Minutes Seconds Hundredths
	 * 
	 * The hours subfield represents hours according to a 24 hour clock. The
	 * range is from 0 to 23. The minutes subfield represents minutes of the
	 * current hour. The range is from 0 to 59. The seconds subfield represents
	 * seconds of the current minute. The range is from 0 to 59. The hundredths
	 * subfield represents 100ths of the current second. The range is from 0 to
	 * 99. A value of 0xff in any subfield indicates an unused subfield. If all
	 * subfields have the value 0xff, this indicates an invalid or 'don't care'
	 * value of the data type.
	 */
	public static final short		TIME_OF_DAY				= 0xe0;

	/**
	 * Date The Time of day data type shall be formatted as illustrated in
	 * Figure 30. Octets: 1 1 1 1 Year - 1900 Month Day of month Day of week
	 * 
	 * The year - 1900 subfield has a range of 0 to 255, representing years from
	 * 1900 to 2155. The month subfield has a range of 1 to 12, representing
	 * January to December. The day of month subfield has a range of 1 to 31.
	 * Note that values in the range 29 to 31 may be invalid, depending on the
	 * month and year. The day of week subfield has a range of 1 to 7,
	 * representing Monday to Sunday. A value of 0xff in any subfield indicates
	 * an unused subfield. If all subfields have the value 0xff, this indicates
	 * an invalid or 'don't care' value of the data type.
	 */
	public static final short		DATE					= 0xe1;
	/** */
	public static final short		UTC_TIME				= 0xe2;
	/** */
	public static final short		CLUSTER_ID				= 0xe8;
	/** */
	public static final short		ATTRIBUTE_ID			= 0xe9;

	/**
	 * BACnet OID (Object Identifier) The BACnet OID data type is included to
	 * allow interworking with BACnet (see [R12]). The format is described in
	 * the referenced standard.
	 */
	public static final short		BACNET_OID				= 0xea;
	/** */
	public static final short		IEEE_ADDRESS			= 0xf0;

	/** Pour inverser les octets */
	protected static final boolean	RETOURNER				= true;

	/**
	 * @param type the value's type
	 * @param value the Java value
	 * @return the given value encoded as a byte[]
	 */
	public static byte[] encode(short type, Object value) {
		// TODO
		return null;
	}

	/**
	 * @param type the value's type
	 * @param value the byte[] value
	 * @return the given value decoded as a Java Object
	 */
	public static Object decode(short type, byte[] value) {
		// TODO
		return null;
	}

}

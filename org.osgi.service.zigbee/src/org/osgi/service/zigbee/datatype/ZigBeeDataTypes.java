/*
 * Copyright (c) OSGi Alliance (${year}). All Rights Reserved.
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

package org.osgi.service.zigbee.datatype;

import org.osgi.service.zigbee.datatype.types.ZigBeeArray;
import org.osgi.service.zigbee.datatype.types.ZigBeeAttributeID;
import org.osgi.service.zigbee.datatype.types.ZigBeeBacnetOID;
import org.osgi.service.zigbee.datatype.types.ZigBeeBag;
import org.osgi.service.zigbee.datatype.types.ZigBeeBitmap16;
import org.osgi.service.zigbee.datatype.types.ZigBeeBitmap24;
import org.osgi.service.zigbee.datatype.types.ZigBeeBitmap32;
import org.osgi.service.zigbee.datatype.types.ZigBeeBitmap40;
import org.osgi.service.zigbee.datatype.types.ZigBeeBitmap48;
import org.osgi.service.zigbee.datatype.types.ZigBeeBitmap56;
import org.osgi.service.zigbee.datatype.types.ZigBeeBitmap64;
import org.osgi.service.zigbee.datatype.types.ZigBeeBitmap8;
import org.osgi.service.zigbee.datatype.types.ZigBeeBooleanType;
import org.osgi.service.zigbee.datatype.types.ZigBeeCharacterString;
import org.osgi.service.zigbee.datatype.types.ZigBeeClusterID;
import org.osgi.service.zigbee.datatype.types.ZigBeeDate;
import org.osgi.service.zigbee.datatype.types.ZigBeeEnumeration16;
import org.osgi.service.zigbee.datatype.types.ZigBeeEnumeration8;
import org.osgi.service.zigbee.datatype.types.ZigBeeFloatingDouble;
import org.osgi.service.zigbee.datatype.types.ZigBeeFloatingSemi;
import org.osgi.service.zigbee.datatype.types.ZigBeeFloatingSingle;
import org.osgi.service.zigbee.datatype.types.ZigBeeGeneralData16;
import org.osgi.service.zigbee.datatype.types.ZigBeeGeneralData24;
import org.osgi.service.zigbee.datatype.types.ZigBeeGeneralData32;
import org.osgi.service.zigbee.datatype.types.ZigBeeGeneralData40;
import org.osgi.service.zigbee.datatype.types.ZigBeeGeneralData48;
import org.osgi.service.zigbee.datatype.types.ZigBeeGeneralData56;
import org.osgi.service.zigbee.datatype.types.ZigBeeGeneralData64;
import org.osgi.service.zigbee.datatype.types.ZigBeeGeneralData8;
import org.osgi.service.zigbee.datatype.types.ZigBeeIEEEADDRESS;
import org.osgi.service.zigbee.datatype.types.ZigBeeLongCharacterString;
import org.osgi.service.zigbee.datatype.types.ZigBeeLongOctetString;
import org.osgi.service.zigbee.datatype.types.ZigBeeOctetString;
import org.osgi.service.zigbee.datatype.types.ZigBeeSet;
import org.osgi.service.zigbee.datatype.types.ZigBeeSignedInteger16;
import org.osgi.service.zigbee.datatype.types.ZigBeeSignedInteger24;
import org.osgi.service.zigbee.datatype.types.ZigBeeSignedInteger32;
import org.osgi.service.zigbee.datatype.types.ZigBeeSignedInteger40;
import org.osgi.service.zigbee.datatype.types.ZigBeeSignedInteger48;
import org.osgi.service.zigbee.datatype.types.ZigBeeSignedInteger56;
import org.osgi.service.zigbee.datatype.types.ZigBeeSignedInteger64;
import org.osgi.service.zigbee.datatype.types.ZigBeeSignedInteger8;
import org.osgi.service.zigbee.datatype.types.ZigBeeStructure;
import org.osgi.service.zigbee.datatype.types.ZigBeeTimeOfDay;
import org.osgi.service.zigbee.datatype.types.ZigBeeUTCTime;
import org.osgi.service.zigbee.datatype.types.ZigBeeUnsignedInteger16;
import org.osgi.service.zigbee.datatype.types.ZigBeeUnsignedInteger24;
import org.osgi.service.zigbee.datatype.types.ZigBeeUnsignedInteger32;
import org.osgi.service.zigbee.datatype.types.ZigBeeUnsignedInteger40;
import org.osgi.service.zigbee.datatype.types.ZigBeeUnsignedInteger48;
import org.osgi.service.zigbee.datatype.types.ZigBeeUnsignedInteger56;
import org.osgi.service.zigbee.datatype.types.ZigBeeUnsignedInteger64;
import org.osgi.service.zigbee.datatype.types.ZigBeeUnsignedInteger8;

/**
 * This interface represents all ZigBee data types
 * 
 * @version 1.0
 */
public class ZigBeeDataTypes {

	/**
	 * Boolean The Boolean type represents a logical value, either TRUE (0x00)
	 * or FALSE (0x01). The value 0xff represents an invalid value of this type.
	 * All other values of this type are forbidden
	 */
	public static final ZigBeeDataTypeDescription	GENERAL_DATA_8			= new ZigBeeGeneralData8();
	public static final ZigBeeDataTypeDescription	GENERAL_DATA_16			= new ZigBeeGeneralData16();
	public static final ZigBeeDataTypeDescription	GENERAL_DATA_24			= new ZigBeeGeneralData24();
	public static final ZigBeeDataTypeDescription	GENERAL_DATA_32			= new ZigBeeGeneralData32();
	public static final ZigBeeDataTypeDescription	GENERAL_DATA_40			= new ZigBeeGeneralData40();
	public static final ZigBeeDataTypeDescription	GENERAL_DATA_48			= new ZigBeeGeneralData48();
	public static final ZigBeeDataTypeDescription	GENERAL_DATA_56			= new ZigBeeGeneralData56();
	public static final ZigBeeDataTypeDescription	GENERAL_DATA_64			= new ZigBeeGeneralData64();

	/**
	 * Boolean The Boolean type represents a logical value, either TRUE (0x00)
	 * or FALSE (0x01). The value 0xff represents an invalid value of this type.
	 * All other values of this type are forbidden.
	 */
	public static final ZigBeeDataTypeDescription	BOOLEAN					= new ZigBeeBooleanType();

	/**
	 * Bitmap (8-bit, 16-bit, 24-bit and 32-bit) The Bitmap type holds 8, 16, 24
	 * or 32 logical values, one per bit, depending on its length. There is no
	 * value to represent an invalid value of this type.
	 */
	public static final ZigBeeDataTypeDescription	BITMAP_8				= new ZigBeeBitmap8();
	public static final ZigBeeDataTypeDescription	BITMAP_16				= new ZigBeeBitmap16();
	public static final ZigBeeDataTypeDescription	BITMAP_24				= new ZigBeeBitmap24();
	public static final ZigBeeDataTypeDescription	BITMAP_32				= new ZigBeeBitmap32();
	public static final ZigBeeDataTypeDescription	BITMAP_40				= new ZigBeeBitmap40();
	public static final ZigBeeDataTypeDescription	BITMAP_48				= new ZigBeeBitmap48();
	public static final ZigBeeDataTypeDescription	BITMAP_56				= new ZigBeeBitmap56();
	public static final ZigBeeDataTypeDescription	BITMAP_64				= new ZigBeeBitmap64();

	/**
	 * Unsigned shorteger (8-bit, 16-bit, 24-bit and 32-bit) This type
	 * represents an unsigned shorteger with a decimal range of 0 to 28-1, 0 to
	 * 216-1, 0 to 224-1, or 0 to 232-1, depending on its length. The values
	 * that represents an invalid value of this type are 0xff, 0xffff, 0xffffff
	 * and 0xffffffff respectively.
	 */
	public static final ZigBeeDataTypeDescription	UNSIGNED_INTEGER_8		= new ZigBeeUnsignedInteger8();
	public static final ZigBeeDataTypeDescription	UNSIGNED_INTEGER_16		= new ZigBeeUnsignedInteger16();
	public static final ZigBeeDataTypeDescription	UNSIGNED_INTEGER_24		= new ZigBeeUnsignedInteger24();
	public static final ZigBeeDataTypeDescription	UNSIGNED_INTEGER_32		= new ZigBeeUnsignedInteger32();
	public static final ZigBeeDataTypeDescription	UNSIGNED_INTEGER_40		= new ZigBeeUnsignedInteger40();
	public static final ZigBeeDataTypeDescription	UNSIGNED_INTEGER_48		= new ZigBeeUnsignedInteger48();
	public static final ZigBeeDataTypeDescription	UNSIGNED_INTEGER_56		= new ZigBeeUnsignedInteger56();
	public static final ZigBeeDataTypeDescription	UNSIGNED_INTEGER_64		= new ZigBeeUnsignedInteger64();

	/**
	 * Signed shorteger (8-bit, 16-bit, 24-bit and 32-bit). This type represents
	 * a signed shorteger with a decimal range of -(27-1) to 27-1, -(215-1) to
	 * 215-1, -(223-1) to 223-1, or -(231-1) to 231-1, depending on its length.
	 * The values that represents an invalid value of this type are 0x80,
	 * 0x8000, 0x800000 and 0x80000000 respectively.
	 */
	public static final ZigBeeDataTypeDescription	SIGNED_INTEGER_8		= new ZigBeeSignedInteger8();
	public static final ZigBeeDataTypeDescription	SIGNED_INTEGER_16		= new ZigBeeSignedInteger16();
	public static final ZigBeeDataTypeDescription	SIGNED_INTEGER_24		= new ZigBeeSignedInteger24();
	public static final ZigBeeDataTypeDescription	SIGNED_INTEGER_32		= new ZigBeeSignedInteger32();
	public static final ZigBeeDataTypeDescription	SIGNED_INTEGER_40		= new ZigBeeSignedInteger40();
	public static final ZigBeeDataTypeDescription	SIGNED_INTEGER_48		= new ZigBeeSignedInteger48();
	public static final ZigBeeDataTypeDescription	SIGNED_INTEGER_56		= new ZigBeeSignedInteger56();
	public static final ZigBeeDataTypeDescription	SIGNED_INTEGER_64		= new ZigBeeSignedInteger64();

	/**
	 * Enumeration (8-bit) The Enumeration type represents an index shorto a
	 * lookup table to determine the final value. The value 0xff represents an
	 * invalid value of this type
	 */
	public static final ZigBeeDataTypeDescription	ENUMERATION_8			= new ZigBeeEnumeration8();
	public static final ZigBeeDataTypeDescription	ENUMERATION_16			= new ZigBeeEnumeration16();

	/**
	 * Semi-precision The ZigBee semi-precision number format is based on the
	 * IEEE 754 standard for binary floating-poshort arithmetic [R11]. This
	 * number format should be used very sparingly, when absolutely necessary,
	 * keeping in mind the code and processing required supporting it. The value
	 * is calculated as: Value = -1Sign * (Hidden + Mantissa/1024) * 2
	 * (Exponent-15)
	 */
	public static final ZigBeeDataTypeDescription	FLOATING_SEMI			= new ZigBeeFloatingSemi();

	/**
	 * Single precision The format of the single precision data type is based on
	 * the IEEE 754 standard for binary floating-poshort arithmetic [R11]. This
	 * number format should be used very sparingly, when absolutely necessary,
	 * keeping in mind the code and processing required supporting it. The
	 * format and shorterpretation of values of this data type follow the same
	 * rules as given for the semi-precision data type, but with shorter
	 * sub-fields, as follows. Length of mantissa = 23 bits, length of exponent
	 * = 8 bits For further details, see [R11]. = 0x43;
	 */
	public static final ZigBeeDataTypeDescription	FLOATING_SINGLE			= new ZigBeeFloatingSingle();

	/**
	 * Double precision The format of the double precision data type is based on
	 * the IEEE 754 standard for binary floating-poshort arithmetic [R11]. This
	 * number format should be used very sparingly, when absolutely necessary,
	 * keeping in mind the code and processing required supporting it. The
	 * format and shorterpretation of values of this data type follow the same
	 * rules as given for the semi-precision data type, but with shorter
	 * sub-fields, as follows. Length of mantissa = 52 bits, length of exponent
	 * = 11 bits For further details, see [R11].
	 */
	public static final ZigBeeDataTypeDescription	FLOATING_DOUBLE			= new ZigBeeFloatingDouble();

	/**
	 * The octet string data type contains data in an application-defined
	 * format, not defined in this specification. The octet string data type
	 * shall be formatted as illustrated in Error! Reference source not found..
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
	public static final ZigBeeDataTypeDescription	OCTET_STRING			= new ZigBeeOctetString();

	/**
	 * Character string The character string data type contains data octets
	 * encoding characters according to the language and character set field of
	 * the complex descriptor. The character string data type shall be formatted
	 * as illustrated in Figure 28.
	 * 
	 * *Octets: 1 Variable Character count Character data The character count
	 * sub-field is one octet in length and specifies the number of characters,
	 * encoded according to the language and character set field of the complex
	 * descriptor (see [R2]), contained in the character data sub-field. Setting
	 * this sub-field to 0x00 represents a character string with no character
	 * data (an "empty string"). Setting this sub-field to 0xff represents an
	 * invalid character string value. In both cases the character data
	 * sub-field has zero length. The character data sub-field en octets in
	 * length, where e is the size of the character, as specified by the
	 * language and character set field of the complex descriptor, and n is the
	 * value of the character count sub-field. This sub-field contains the
	 * encoded characters that comprise the desired character string. A
	 * character string with no contents, i.e. with the character count
	 * sub-field equal to 0x00 and a zero length character data sub-field, shall
	 * be referred to as an 'empty string'.
	 */
	public static final ZigBeeDataTypeDescription	CHARACTER_STRING		= new ZigBeeCharacterString();
	public static final ZigBeeDataTypeDescription	LONG_OCTET_STRING		= new ZigBeeLongOctetString();
	public static final ZigBeeDataTypeDescription	LONG_CHARACTER_STRING	= new ZigBeeLongCharacterString();
	public static final ZigBeeDataTypeDescription	ARRAY					= new ZigBeeArray();
	public static final ZigBeeDataTypeDescription	STRUCTURE				= new ZigBeeStructure();
	public static final ZigBeeDataTypeDescription	SET						= new ZigBeeSet();
	public static final ZigBeeDataTypeDescription	BAG						= new ZigBeeBag();

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
	public static final ZigBeeDataTypeDescription	TIME_OF_DAY				= new ZigBeeTimeOfDay();

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
	public static final ZigBeeDataTypeDescription	DATE					= new ZigBeeDate();
	public static final ZigBeeDataTypeDescription	UTC_TIME				= new ZigBeeUTCTime();
	public static final ZigBeeDataTypeDescription	CLUSTER_ID				= new ZigBeeClusterID();
	public static final ZigBeeDataTypeDescription	ATTRIBUTE_ID			= new ZigBeeAttributeID();

	/**
	 * BACnet OID (Object Identifier) The BACnet OID data type is included to
	 * allow shorterworking with BACnet (see [R12]). The format is described in
	 * the referenced standard.
	 */
	public static final ZigBeeDataTypeDescription	BACNET_OID				= new ZigBeeBacnetOID();
	public static final ZigBeeDataTypeDescription	IEEE_ADDRESS			= new ZigBeeIEEEADDRESS();
}

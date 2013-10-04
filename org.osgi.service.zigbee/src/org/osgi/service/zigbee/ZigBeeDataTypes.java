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

import org.osgi.service.zigbee.descriptions.ZigBeeDataTypeDescription;
import org.osgi.service.zigbee.types.ZigBeeArray;
import org.osgi.service.zigbee.types.ZigBeeAttributeID;
import org.osgi.service.zigbee.types.ZigBeeBacnetOID;
import org.osgi.service.zigbee.types.ZigBeeBag;
import org.osgi.service.zigbee.types.ZigBeeBitmap16;
import org.osgi.service.zigbee.types.ZigBeeBitmap24;
import org.osgi.service.zigbee.types.ZigBeeBitmap32;
import org.osgi.service.zigbee.types.ZigBeeBitmap40;
import org.osgi.service.zigbee.types.ZigBeeBitmap48;
import org.osgi.service.zigbee.types.ZigBeeBitmap56;
import org.osgi.service.zigbee.types.ZigBeeBitmap64;
import org.osgi.service.zigbee.types.ZigBeeBitmap8;
import org.osgi.service.zigbee.types.ZigBeeBoolean;
import org.osgi.service.zigbee.types.ZigBeeCharacterString;
import org.osgi.service.zigbee.types.ZigBeeClusterID;
import org.osgi.service.zigbee.types.ZigBeeDate;
import org.osgi.service.zigbee.types.ZigBeeEnumeration16;
import org.osgi.service.zigbee.types.ZigBeeEnumeration8;
import org.osgi.service.zigbee.types.ZigBeeFloatingDouble;
import org.osgi.service.zigbee.types.ZigBeeFloatingSemi;
import org.osgi.service.zigbee.types.ZigBeeFloatingSingle;
import org.osgi.service.zigbee.types.ZigBeeGeneralData16;
import org.osgi.service.zigbee.types.ZigBeeGeneralData24;
import org.osgi.service.zigbee.types.ZigBeeGeneralData32;
import org.osgi.service.zigbee.types.ZigBeeGeneralData40;
import org.osgi.service.zigbee.types.ZigBeeGeneralData48;
import org.osgi.service.zigbee.types.ZigBeeGeneralData56;
import org.osgi.service.zigbee.types.ZigBeeGeneralData64;
import org.osgi.service.zigbee.types.ZigBeeGeneralData8;
import org.osgi.service.zigbee.types.ZigBeeIEEEADDRESS;
import org.osgi.service.zigbee.types.ZigBeeLongCharacterString;
import org.osgi.service.zigbee.types.ZigBeeLongOctetString;
import org.osgi.service.zigbee.types.ZigBeeOctetString;
import org.osgi.service.zigbee.types.ZigBeeSet;
import org.osgi.service.zigbee.types.ZigBeeSignedInteger16;
import org.osgi.service.zigbee.types.ZigBeeSignedInteger24;
import org.osgi.service.zigbee.types.ZigBeeSignedInteger32;
import org.osgi.service.zigbee.types.ZigBeeSignedInteger40;
import org.osgi.service.zigbee.types.ZigBeeSignedInteger48;
import org.osgi.service.zigbee.types.ZigBeeSignedInteger56;
import org.osgi.service.zigbee.types.ZigBeeSignedInteger64;
import org.osgi.service.zigbee.types.ZigBeeSignedInteger8;
import org.osgi.service.zigbee.types.ZigBeeStructure;
import org.osgi.service.zigbee.types.ZigBeeTimeOfDay;
import org.osgi.service.zigbee.types.ZigBeeUTCTime;
import org.osgi.service.zigbee.types.ZigBeeUnsignedInteger16;
import org.osgi.service.zigbee.types.ZigBeeUnsignedInteger24;
import org.osgi.service.zigbee.types.ZigBeeUnsignedInteger32;
import org.osgi.service.zigbee.types.ZigBeeUnsignedInteger40;
import org.osgi.service.zigbee.types.ZigBeeUnsignedInteger48;
import org.osgi.service.zigbee.types.ZigBeeUnsignedInteger56;
import org.osgi.service.zigbee.types.ZigBeeUnsignedInteger64;
import org.osgi.service.zigbee.types.ZigBeeUnsignedInteger8;

/**
 * This interface represents all ZigBee data types, and contains the common
 * serialize/deserialize methods for the org.osgi.service.zigbee.types.*
 * 
 * @version 1.0
 */
public class ZigBeeDataTypes {

	/**
	 * Boolean The Boolean type represents a logical value, either TRUE (0x00)
	 * or FALSE (0x01). The value 0xff represents an invalid value of this type.
	 * All other values of this type are forbidden
	 */
	public static final ZigBeeDataTypeDescription	GENERAL_DATA_8			= ZigBeeGeneralData8.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	GENERAL_DATA_16			= ZigBeeGeneralData16.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	GENERAL_DATA_24			= ZigBeeGeneralData24.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	GENERAL_DATA_32			= ZigBeeGeneralData32.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	GENERAL_DATA_40			= ZigBeeGeneralData40.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	GENERAL_DATA_48			= ZigBeeGeneralData48.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	GENERAL_DATA_56			= ZigBeeGeneralData56.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	GENERAL_DATA_64			= ZigBeeGeneralData64.getInstance();

	/**
	 * Boolean The Boolean type represents a logical value, either TRUE (0x00)
	 * or FALSE (0x01). The value 0xff represents an invalid value of this type.
	 * All other values of this type are forbidden.
	 */
	public static final ZigBeeDataTypeDescription	BOOLEAN					= ZigBeeBoolean.getInstance();

	/**
	 * Bitmap (8-bit, 16-bit, 24-bit and 32-bit) The Bitmap type holds 8, 16, 24
	 * or 32 logical values, one per bit, depending on its length. There is no
	 * value to represent an invalid value of this type.
	 */
	public static final ZigBeeDataTypeDescription	BITMAP_8				= ZigBeeBitmap8.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	BITMAP_16				= ZigBeeBitmap16.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	BITMAP_24				= ZigBeeBitmap24.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	BITMAP_32				= ZigBeeBitmap32.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	BITMAP_40				= ZigBeeBitmap40.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	BITMAP_48				= ZigBeeBitmap48.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	BITMAP_56				= ZigBeeBitmap56.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	BITMAP_64				= ZigBeeBitmap64.getInstance();

	/**
	 * Unsigned shorteger (8-bit, 16-bit, 24-bit and 32-bit) This type
	 * represents an unsigned shorteger with a decimal range of 0 to 28-1, 0 to
	 * 216-1, 0 to 224-1, or 0 to 232-1, depending on its length. The values
	 * that represents an invalid value of this type are 0xff, 0xffff, 0xffffff
	 * and 0xffffffff respectively.
	 */
	public static final ZigBeeDataTypeDescription	UNSIGNED_INTEGER_8		= ZigBeeUnsignedInteger8.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	UNSIGNED_INTEGER_16		= ZigBeeUnsignedInteger16.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	UNSIGNED_INTEGER_24		= ZigBeeUnsignedInteger24.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	UNSIGNED_INTEGER_32		= ZigBeeUnsignedInteger32.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	UNSIGNED_INTEGER_40		= ZigBeeUnsignedInteger40.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	UNSIGNED_INTEGER_48		= ZigBeeUnsignedInteger48.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	UNSIGNED_INTEGER_56		= ZigBeeUnsignedInteger56.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	UNSIGNED_INTEGER_64		= ZigBeeUnsignedInteger64.getInstance();

	/**
	 * Signed shorteger (8-bit, 16-bit, 24-bit and 32-bit). This type represents
	 * a signed shorteger with a decimal range of -(27-1) to 27-1, -(215-1) to
	 * 215-1, -(223-1) to 223-1, or -(231-1) to 231-1, depending on its length.
	 * The values that represents an invalid value of this type are 0x80,
	 * 0x8000, 0x800000 and 0x80000000 respectively.
	 */
	public static final ZigBeeDataTypeDescription	SIGNED_INTEGER_8		= ZigBeeSignedInteger8.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	SIGNED_INTEGER_16		= ZigBeeSignedInteger16.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	SIGNED_INTEGER_24		= ZigBeeSignedInteger24.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	SIGNED_INTEGER_32		= ZigBeeSignedInteger32.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	SIGNED_INTEGER_40		= ZigBeeSignedInteger40.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	SIGNED_INTEGER_48		= ZigBeeSignedInteger48.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	SIGNED_INTEGER_56		= ZigBeeSignedInteger56.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	SIGNED_INTEGER_64		= ZigBeeSignedInteger64.getInstance();

	/**
	 * Enumeration (8-bit) The Enumeration type represents an index shorto a
	 * lookup table to determine the final value. The value 0xff represents an
	 * invalid value of this type
	 */
	public static final ZigBeeDataTypeDescription	ENUMERATION_8			= ZigBeeEnumeration8.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	ENUMERATION_16			= ZigBeeEnumeration16.getInstance();

	/**
	 * Semi-precision The ZigBee semi-precision number format is based on the
	 * IEEE 754 standard for binary floating-poshort arithmetic [R11]. This
	 * number format should be used very sparingly, when absolutely necessary,
	 * keeping in mind the code and processing required supporting it. The value
	 * is calculated as: Value = -1Sign * (Hidden + Mantissa/1024) * 2
	 * (Exponent-15)
	 */
	public static final ZigBeeDataTypeDescription	FLOATING_SEMI			= ZigBeeFloatingSemi.getInstance();

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
	public static final ZigBeeDataTypeDescription	FLOATING_SINGLE			= ZigBeeFloatingSingle.getInstance();

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
	public static final ZigBeeDataTypeDescription	FLOATING_DOUBLE			= ZigBeeFloatingDouble.getInstance();

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
	public static final ZigBeeDataTypeDescription	OCTET_STRING			= ZigBeeOctetString.getInstance();

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
	public static final ZigBeeDataTypeDescription	CHARACTER_STRING		= ZigBeeCharacterString.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	LONG_OCTET_STRING		= ZigBeeLongOctetString.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	LONG_CHARACTER_STRING	= ZigBeeLongCharacterString.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	ARRAY					= ZigBeeArray.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	STRUCTURE				= ZigBeeStructure.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	SET						= ZigBeeSet.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	BAG						= ZigBeeBag.getInstance();

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
	public static final ZigBeeDataTypeDescription	TIME_OF_DAY				= ZigBeeTimeOfDay.getInstance();

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
	public static final ZigBeeDataTypeDescription	DATE					= ZigBeeDate.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	UTC_TIME				= ZigBeeUTCTime.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	CLUSTER_ID				= ZigBeeClusterID.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	ATTRIBUTE_ID			= ZigBeeAttributeID.getInstance();

	/**
	 * BACnet OID (Object Identifier) The BACnet OID data type is included to
	 * allow shorterworking with BACnet (see [R12]). The format is described in
	 * the referenced standard.
	 */
	public static final ZigBeeDataTypeDescription	BACNET_OID				= ZigBeeBacnetOID.getInstance();
	/** see above */
	public static final ZigBeeDataTypeDescription	IEEE_ADDRESS			= ZigBeeIEEEADDRESS.getInstance();
}

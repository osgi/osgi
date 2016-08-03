
package org.osgi.test.cases.zigbee;

import java.io.IOException;
import java.math.BigInteger;
import org.osgi.service.zigbee.ZigBeeDataInput;
import org.osgi.service.zigbee.ZigBeeDataOutput;
import org.osgi.service.zigbee.ZigBeeDataTypes;
import org.osgi.service.zigbee.descriptions.ZCLSimpleTypeDescription;
import org.osgi.service.zigbee.types.ZigBeeAttributeID;
import org.osgi.service.zigbee.types.ZigBeeBACnet;
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
import org.osgi.service.zigbee.types.ZigBeeIEEE_ADDRESS;
import org.osgi.service.zigbee.types.ZigBeeLongCharacterString;
import org.osgi.service.zigbee.types.ZigBeeLongOctetString;
import org.osgi.service.zigbee.types.ZigBeeOctetString;
import org.osgi.service.zigbee.types.ZigBeeSecurityKey128;
import org.osgi.service.zigbee.types.ZigBeeSignedInteger16;
import org.osgi.service.zigbee.types.ZigBeeSignedInteger24;
import org.osgi.service.zigbee.types.ZigBeeSignedInteger32;
import org.osgi.service.zigbee.types.ZigBeeSignedInteger40;
import org.osgi.service.zigbee.types.ZigBeeSignedInteger48;
import org.osgi.service.zigbee.types.ZigBeeSignedInteger56;
import org.osgi.service.zigbee.types.ZigBeeSignedInteger64;
import org.osgi.service.zigbee.types.ZigBeeSignedInteger8;
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
import org.osgi.test.cases.zigbee.mock.ZigBeeSerializer;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * Partial test of the ZCL data types classes
 * 
 * @author $Id$
 *
 */
public class ZCLDataTypesTestCase extends DefaultTestBundleControl {

	byte[]		nullDate			= new byte[] {0x00, 0x00, 0x00, 0x00};
	Object[]	dataTypesZeroValue	= new Object[] {new Byte((byte) 0), new Short((short) 0), new Integer(0), new Long(0), BigInteger.valueOf(0), new String(), nullDate};

	public void testGeneralDataTypes() {

		// true if it is analog
		boolean A;

		// true if allows invalid values
		boolean I;

		// true if it is unsigned
		boolean U;

		I = false;
		A = false;
		U = false;

		this.basicDataTypeChecks(ZigBeeGeneralData8.getInstance(), ZigBeeDataTypes.GENERAL_DATA_8, 1, Byte.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeGeneralData16.getInstance(), ZigBeeDataTypes.GENERAL_DATA_16, 2, Short.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeGeneralData24.getInstance(), ZigBeeDataTypes.GENERAL_DATA_24, 3, Integer.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeGeneralData32.getInstance(), ZigBeeDataTypes.GENERAL_DATA_32, 4, Integer.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeGeneralData40.getInstance(), ZigBeeDataTypes.GENERAL_DATA_40, 5, Long.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeGeneralData48.getInstance(), ZigBeeDataTypes.GENERAL_DATA_48, 6, Long.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeGeneralData56.getInstance(), ZigBeeDataTypes.GENERAL_DATA_56, 7, Long.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeGeneralData64.getInstance(), ZigBeeDataTypes.GENERAL_DATA_64, 8, Long.class, U, I, A);

	}

	public void testBitmapDataTypes() {

		// true if it is analog
		boolean A;

		// true if allows invalid values
		boolean I;

		// true if it is unsigned
		boolean U;

		A = false;
		I = false;
		U = true;

		this.basicDataTypeChecks(ZigBeeBitmap8.getInstance(), ZigBeeDataTypes.BITMAP_8, 1, Byte.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeBitmap16.getInstance(), ZigBeeDataTypes.BITMAP_16, 2, Short.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeBitmap24.getInstance(), ZigBeeDataTypes.BITMAP_24, 3, Integer.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeBitmap32.getInstance(), ZigBeeDataTypes.BITMAP_32, 4, Integer.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeBitmap40.getInstance(), ZigBeeDataTypes.BITMAP_40, 5, Long.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeBitmap48.getInstance(), ZigBeeDataTypes.BITMAP_48, 6, Long.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeBitmap56.getInstance(), ZigBeeDataTypes.BITMAP_56, 7, Long.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeBitmap64.getInstance(), ZigBeeDataTypes.BITMAP_64, 8, Long.class, U, I, A);
	}

	public void testUnsignedIntegerDataTypes() {

		// true if it is analog
		boolean A;

		// true if allows invalid values
		boolean I;

		// true if it is unsigned
		boolean U;

		A = true;
		I = true;
		U = true;

		this.basicDataTypeChecks(ZigBeeUnsignedInteger8.getInstance(), ZigBeeDataTypes.UNSIGNED_INTEGER_8, 1, Short.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeUnsignedInteger16.getInstance(), ZigBeeDataTypes.UNSIGNED_INTEGER_16, 2, Integer.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeUnsignedInteger24.getInstance(), ZigBeeDataTypes.UNSIGNED_INTEGER_24, 3, Integer.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeUnsignedInteger32.getInstance(), ZigBeeDataTypes.UNSIGNED_INTEGER_32, 4, Long.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeUnsignedInteger40.getInstance(), ZigBeeDataTypes.UNSIGNED_INTEGER_40, 5, Long.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeUnsignedInteger48.getInstance(), ZigBeeDataTypes.UNSIGNED_INTEGER_48, 6, Long.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeUnsignedInteger56.getInstance(), ZigBeeDataTypes.UNSIGNED_INTEGER_56, 7, Long.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeUnsignedInteger64.getInstance(), ZigBeeDataTypes.UNSIGNED_INTEGER_64, 8, BigInteger.class, U, I, A);

	}

	public void testSignedIntegerDataTypes() {

		// true if it is analog
		boolean A;

		// true if allows invalid values
		boolean I;

		// true if it is unsigned
		boolean U;

		A = true;
		I = true;
		U = false;

		this.basicDataTypeChecks(ZigBeeSignedInteger8.getInstance(), ZigBeeDataTypes.SIGNED_INTEGER_8, 1, Byte.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeSignedInteger16.getInstance(), ZigBeeDataTypes.SIGNED_INTEGER_16, 2, Short.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeSignedInteger24.getInstance(), ZigBeeDataTypes.SIGNED_INTEGER_24, 3, Integer.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeSignedInteger32.getInstance(), ZigBeeDataTypes.SIGNED_INTEGER_32, 4, Integer.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeSignedInteger40.getInstance(), ZigBeeDataTypes.SIGNED_INTEGER_40, 5, Long.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeSignedInteger48.getInstance(), ZigBeeDataTypes.SIGNED_INTEGER_48, 6, Long.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeSignedInteger56.getInstance(), ZigBeeDataTypes.SIGNED_INTEGER_56, 7, Long.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeSignedInteger64.getInstance(), ZigBeeDataTypes.SIGNED_INTEGER_64, 8, Long.class, U, I, A);
	}

	public void testEnumDataTypes() {

		// true if it is analog
		boolean A;

		// true if allows invalid values
		boolean I;

		// true if it is unsigned
		boolean U;

		A = false;
		I = true;
		U = true;

		this.basicDataTypeChecks(ZigBeeEnumeration8.getInstance(), ZigBeeDataTypes.ENUMERATION_8, 1, Short.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeEnumeration16.getInstance(), ZigBeeDataTypes.ENUMERATION_16, 2, Integer.class, U, I, A);
	}

	public void testTimeDataTypes() {

		// true if it is analog
		boolean A;

		// true if allows invalid values
		boolean I;

		// true if it is unsigned
		boolean U;

		A = true;
		I = true;
		U = true; // don't care for Time of Day and Date

		this.basicDataTypeChecks(ZigBeeTimeOfDay.getInstance(), ZigBeeDataTypes.TIME_OF_DAY, 4, byte[].class, U, I, A);
		this.basicDataTypeChecks(ZigBeeDate.getInstance(), ZigBeeDataTypes.DATE, 4, byte[].class, U, I, A);
		this.basicDataTypeChecks(ZigBeeUTCTime.getInstance(), ZigBeeDataTypes.UTC_TIME, 4, Long.class, U, I, A);
	}

	public void testIdentifierDataTypes() {

		// true if it is analog
		boolean A;

		// true if allows invalid values
		boolean I;

		// true if it is unsigned
		boolean U;

		A = false;
		I = true;
		U = true;

		this.basicDataTypeChecks(ZigBeeAttributeID.getInstance(), ZigBeeDataTypes.ATTRIBUTE_ID, 2, Integer.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeClusterID.getInstance(), ZigBeeDataTypes.CLUSTER_ID, 2, Integer.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeBACnet.getInstance(), ZigBeeDataTypes.BACNET_OID, 4, Long.class, U, I, A);
	}

	public void testBooleanDataType() {

		// true if it is analog
		boolean A;

		// true if allows invalid values
		boolean I;

		// true if it is unsigned
		boolean U;

		A = false;
		I = true;
		U = true; // we don't care

		this.basicDataTypeChecks(ZigBeeBoolean.getInstance(), ZigBeeDataTypes.BOOLEAN, 1, Boolean.class, U, I, A);
	}

	public void testFloatDataTypes() {

		// true if it is analog
		boolean A;

		// true if allows invalid values
		boolean I;

		// true if it is unsigned
		boolean U;

		A = true;
		I = true;
		U = false;

		this.basicDataTypeChecks(ZigBeeFloatingSemi.getInstance(), ZigBeeDataTypes.FLOATING_SEMI, 2, Float.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeFloatingSingle.getInstance(), ZigBeeDataTypes.FLOATING_SINGLE, 4, Float.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeFloatingDouble.getInstance(), ZigBeeDataTypes.FLOATING_DOUBLE, 8, Double.class, U, I, A);
	}

	public void testStringDataTypes() {

		// true if it is analog
		boolean A;

		// true if allows invalid values
		boolean I;

		// true if it is unsigned (only for numbers)
		boolean U;

		A = false;
		I = true;
		U = false; // don't care for Strings

		this.basicDataTypeChecks(ZigBeeCharacterString.getInstance(), ZigBeeDataTypes.CHARACTER_STRING, 1, String.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeOctetString.getInstance(), ZigBeeDataTypes.OCTET_STRING, 1, byte[].class, U, I, A);
		this.basicDataTypeChecks(ZigBeeLongCharacterString.getInstance(), ZigBeeDataTypes.LONG_CHARACTER_STRING, 2, String.class, U, I, A);
		this.basicDataTypeChecks(ZigBeeLongOctetString.getInstance(), ZigBeeDataTypes.LONG_OCTET_STRING, 2, byte[].class, U, I, A);
	}

	public void testMiscellaneousDataTypes() {

		// true if it is analog
		boolean A;

		// true if allows invalid values
		boolean I;

		// true if it is unsigned (only for numbers)
		boolean U;

		A = false;
		I = true;
		U = false;

		this.basicDataTypeChecks(ZigBeeIEEE_ADDRESS.getInstance(), ZigBeeDataTypes.IEEE_ADDRESS, 8, BigInteger.class, U, I, A);

		I = false;
		this.basicDataTypeChecks(ZigBeeSecurityKey128.getInstance(), ZigBeeDataTypes.SECURITY_KEY_128, 8, byte[].class, U, I, A);
	}

	protected void basicDataTypeChecks(ZCLSimpleTypeDescription dataType, short dataTypeId, int size, Class clazz, boolean U, boolean I, boolean A) {

		/*
		 * Checks if the name assigned to the data type is the expected one.
		 */

		/*
		 * Create a buffer of big enough to store the data marshaled or
		 * un-marshaled in the following part of this method.
		 */
		byte[] data = new byte[100];

		GenericFrame payload = new GenericFrame(data);

		ZigBeeDataInput dataInput = payload.getDataInput();
		ZigBeeDataOutput dataOutput = payload.getDataOutput();

		String packageName = dataType.getClass().getPackage().getName();
		String expectedName = dataType.getClass().getName().substring(packageName.length() + ".ZigBee".length());

		assertEquals("data type name is not correct", expectedName, dataType.getName());

		/*
		 * Checks if the id assigned to the data type is the expected one
		 */
		assertEquals("data type ID is not correct", dataTypeId, dataType.getId());

		/*
		 * Checks if the java type used to map the data type is correct.
		 */
		assertEquals("java class is not correct ", clazz.getName(), dataType.getJavaDataType().getName());

		/*
		 * Checks the correctness of the analog/digital information
		 */
		assertEquals("isAnalog() method returned the wrong value for data type " + dataType.getClass().getName(), A, dataType.isAnalog());

		if (!I) {
			try {
				dataType.serialize(dataOutput, null);
				fail("expected IllegalArgumentException exception since the data type " + dataType.getName() + " do not support invalid values");
			} catch (IllegalArgumentException e) {
				// we expect it!
			} catch (Throwable e) {
				fail("unexpected exception: " + e.getMessage());
			}

			int outputIndex = payload.getCurrentOutputIndex();
			if (outputIndex != 0) {
				fail("even if we didn't serialize nothing, the cursor inside the ZigBeeData buffer has moved.");
			}
		} else {
			/*
			 * The data type supports invalid values. Check it!
			 */
			try {
				dataType.serialize(dataOutput, null);
			} catch (Throwable e) {
				fail("unexpected exception: " + e.getMessage() + " while serializing an invalid value");
			}

			try {
				Object value = dataType.deserialize(dataInput);
				assertNull("deserialize() must return 'null' as indication of invalid value in data type: " + dataType.getName(), value);
			} catch (IOException e) {
				fail("unexpected exception: " + e.getMessage());
			}

			int outputIndex = payload.getCurrentOutputIndex();
			if (outputIndex != size) {
				fail("even if we didn't serialize nothing, the cursor inside the ZigBeeData buffer has moved.");
			}
		}

		/*
		 * According to the data type java class mapping we perform different
		 * checks.
		 */

		payload = new GenericFrame(data);

		dataInput = payload.getDataInput();
		dataOutput = payload.getDataOutput();

		if (clazz.equals(Byte.class)) {
			if (size != 1) {
				fail("internal TC errror: ZigBeeDataTypes that maps to a Short must have a size of 1");
			}

			checkAgainstSerializeWrongJavaType(dataType, dataOutput);
			checkValue(dataType, payload, new Byte((byte) 0), size, new byte[] {0x00});
			checkValue(dataType, payload, new Byte(Byte.MAX_VALUE), size, new byte[] {Byte.MAX_VALUE});
			/*
			 * since the data type may allow Invalid number values, we do not
			 * have to use it, otherwise the deserialize method would return
			 * null instead of the number we serialized causing this test to
			 * fail.
			 */
			checkValue(dataType, payload, new Byte((byte) 0x81), size, new byte[] {(byte) 0x81});

		} else if (clazz.equals(Short.class)) {
			if ((size < 1) || (size > 2)) {
				fail("internal TC errror: ZigBeeDataTypes that maps to a Short must have a size in the range [1,2]");
			}

			checkAgainstSerializeWrongJavaType(dataType, dataOutput);

			if (size == 1) {
				/*
				 * we have to do with unsigned data types. The size is 1, but we
				 * need a bigger java wrapper to allow unsigned numbers.
				 */
				checkValue(dataType, payload, new Short((byte) 0), size, new byte[] {0x00, 0x00});
				/*
				 * since the data type may allow Invalid number values, we do
				 * not have to use it, otherwise the deserialize method would
				 * return null instead of the number we serialized causing this
				 * test to fail.
				 */
				checkValue(dataType, payload, new Short((short) 0xfe), size, new byte[] {(byte) 0xfe});
			} else if (size == 2) {
				/*
				 * This is a signed data type or an unsigned data type that we
				 * would like to manage like signed (i.e. general data type or
				 * bitmap)
				 */
				checkValue(dataType, payload, new Short((short) 0), size, new byte[] {0x00, 0x00});
				checkValue(dataType, payload, new Short(Short.MAX_VALUE), size, new byte[] {(byte) 0xff, 0x7f});
				/*
				 * since the data type may allow Invalid number values, we do
				 * not have to use it, otherwise the deserialize method would
				 * return null instead of the number we serialized causing this
				 * test to fail.
				 */
				checkValue(dataType, payload, new Short((short) 0x8001), size, new byte[] {0x01, (byte) 0x80});
			}
		} else if (clazz.equals(Integer.class)) {
			if ((size < 2) || (size > 4)) {
				fail("internal TC errror: ZigBeeDataTypes that maps to a Integer must have a size in the range [2, 4]");
			}

			checkAgainstSerializeWrongJavaType(dataType, dataOutput);

			if (size == 2) {
				/*
				 * Here we could have only unsigned data types like attributeID
				 * that do not fit into a Short.
				 */

				checkValue(dataType, payload, new Integer(0), size, new byte[] {0x00, 0x00});

				/*
				 * the maximum unsigned value that fits into 2 bytes and that it
				 * cannot confused with an Invalid number
				 */
				checkValue(dataType, payload, new Integer(0xfffe), size, new byte[] {(byte) 0xfe, (byte) 0xff});

				// another number
				checkValue(dataType, payload, new Integer(0xfafb), size, new byte[] {(byte) 0xfb, (byte) 0xfa});
			} else if (size == 3) {
				/*
				 * we could have to do here with both unsigned and signed data
				 * types.
				 */

				checkValue(dataType, payload, new Integer(0), size, new byte[] {0x00, 0x00, 0x00, 0x00});
				/*
				 * since the data type may allow Invalid number values, we do
				 * not have to use it, otherwise the deserialize method would
				 * return null instead of the number we serialized causing the
				 * following check to fail.
				 */
				if (U) {
					/*
					 * the maximum unsigned value that fits into 3 bytes and
					 * that it cannot confused with an Invalid number
					 */
					checkValue(dataType, payload, new Integer(0xfffffe), size, new byte[] {(byte) 0xfe, (byte) 0xff, (byte) 0xff});

					// another number
					checkValue(dataType, payload, new Integer(0xfafbfc), size, new byte[] {(byte) 0xfc, (byte) 0xfb, (byte) 0xfa});
				} else {
					// the maximum signed value that fits into 3 bytes.
					checkValue(dataType, payload, new Integer(0x7f0000), size, new byte[] {0x00, 0x00, 0x7f});

					// the minimum signed value that fits into 3 bytes
					checkValue(dataType, payload, new Integer(0x800000 + 1), size, new byte[] {(byte) 0x01, 0x00, (byte) 0x80});
				}
			} else if (size == 4) {
				/*
				 * This is a signed data type or an unsigned data type that is
				 * handled like signed (i.e general data and bitmap).
				 */

				checkValue(dataType, payload, new Integer(0), size, new byte[] {0x00, 0x00, 0x00, 0x00});
				checkValue(dataType, payload, new Integer(Integer.MAX_VALUE), size, new byte[] {(byte) 0xff, (byte) 0xff, (byte) 0xff, 0x7f});
				/*
				 * since the data type may allow Invalid number values, we do
				 * not have to use it, otherwise the deserialize method would
				 * return null instead of the number we serialized causing this
				 * test to fail.
				 */
				checkValue(dataType, payload, new Integer(0x80123456), size, new byte[] {0x56, 0x34, 0x12, (byte) 0x80});
			}
		} else if (clazz.equals(Long.class)) {
			if ((size < 4) || (size > 8)) {
				fail("internal TC errror: ZigBeeDataTypes that maps to a Long must have a size in the range [5, 8]");
			}

			checkAgainstSerializeWrongJavaType(dataType, dataOutput);

			if (size == 4) {
				/*
				 * This is an unsigned type. For instance an unsigned int 32.
				 */
				checkValue(dataType, payload, new Long(0), size, new byte[] {0x00, 0x00, 0x00, 0x00});

				/*
				 * the maximum unsigned value that fits into 4 bytes and that it
				 * cannot confused with an Invalid number
				 */
				checkValue(dataType, payload, new Long(0xfffffffeL), size, new byte[] {(byte) 0xfe, (byte) 0xff, (byte) 0xff, (byte) 0xff});

				// another number
				checkValue(dataType, payload, new Long(0xf8f9fafbL), size, new byte[] {(byte) 0xfb, (byte) 0xfa, (byte) 0xf9, (byte) 0xf8});

			} else if (size == 5) {
				/*
				 * we could have to do here with both unsigned and signed data
				 * types (i.e. unsigned integer 32 or signed integer 40
				 */

				checkValue(dataType, payload, new Long(0), size, new byte[] {0x00, 0x00, 0x00, 0x00, 0x00});
				/*
				 * since the data type may allow Invalid number values, we do
				 * not have to use it, otherwise the deserialize method would
				 * return null instead of the number we serialized causing the
				 * following check to fail.
				 */
				if (U) {
					/*
					 * the maximum unsigned value that fits into 4 bytes and
					 * that it cannot confused with an Invalid number
					 */
					checkValue(dataType, payload, new Long(0xfffffffffeL), size, new byte[] {(byte) 0xfe, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff});

					// another number
					checkValue(dataType, payload, new Long(0xf8f9fafbfcL), size, new byte[] {(byte) 0xfc, (byte) 0xfb, (byte) 0xfa, (byte) 0xf9, (byte) 0xf8});
				} else {
					// the maximum signed value that fits into 3 bytes.
					checkValue(dataType, payload, new Long(0x7f00000000L), size, new byte[] {0x00, 0x00, 0x00, 0x00, 0x7f});

					// the minimum signed value that fits into 3 bytes
					checkValue(dataType, payload, new Long(0x8000000000L + 1), size, new byte[] {0x01, 0x00, 0x00, 0x00, (byte) 0x80});
				}
			} else if (size == 6) {
				/*
				 * we could have to do here with both unsigned and signed data
				 * types (i.e. unsigned integer 40 or signed integer 48
				 */
				checkValue(dataType, payload, new Long(0), size, new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
				/*
				 * since the data type may allow Invalid number values, we do
				 * not have to use it, otherwise the deserialize method would
				 * return null instead of the number we serialized causing the
				 * following check to fail.
				 */
				if (U) {
					/*
					 * the maximum unsigned value that fits into 4 bytes and
					 * that it cannot confused with an Invalid number
					 */
					checkValue(dataType, payload, new Long(0xfffffffffffeL), size, new byte[] {(byte) 0xfe, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff});

					// another number
					checkValue(dataType, payload, new Long(0xf8f9fafbfcfdL), size, new byte[] {(byte) 0xfd, (byte) 0xfc, (byte) 0xfb, (byte) 0xfa, (byte) 0xf9, (byte) 0xf8});
				} else {
					// the maximum signed value that fits into 3 bytes.
					checkValue(dataType, payload, new Long(0x7f0000000000L), size, new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x7f});

					// the minimum signed value that fits into 3 bytes
					checkValue(dataType, payload, new Long(0x800000000000L + 1), size, new byte[] {0x01, 0x00, 0x00, 0x00, 0x00, (byte) 0x80});
				}
			} else if (size == 7) {
				/*
				 * we could have to do here with both unsigned and signed data
				 * types (i.e. unsigned integer 40 or signed integer 48
				 */
				checkValue(dataType, payload, new Long(0), size, new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
				/*
				 * since the data type may allow Invalid number values, we do
				 * not have to use it, otherwise the deserialize method would
				 * return null instead of the number we serialized causing the
				 * following check to fail.
				 */
				if (U) {
					/*
					 * the maximum unsigned value that fits into 4 bytes and
					 * that it cannot confused with an Invalid number
					 */
					checkValue(dataType, payload, new Long(0xfffffffffffffeL), size, new byte[] {(byte) 0xfe, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff});

					// another number
					checkValue(dataType, payload, new Long(0xf8f9fafbfcfdfeL), size, new byte[] {(byte) 0xfe, (byte) 0xfd, (byte) 0xfc, (byte) 0xfb, (byte) 0xfa, (byte) 0xf9, (byte) 0xf8});
				} else {
					// the maximum signed value that fits into 3 bytes.
					checkValue(dataType, payload, new Long(0x7f000000000000L), size, new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x7f});

					// the minimum signed value that fits into 3 bytes
					checkValue(dataType, payload, new Long(0x80000000000000L + 1), size, new byte[] {0x01, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0x80});
				}
			} else if (size == 8) {
				/*
				 * we could have to do here with both unsigned and signed data
				 * types (i.e. unsigned integer 56 or signed integer 64 or
				 * bitmap 64
				 */

				checkValue(dataType, payload, new Long(0), size, new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
				/*
				 * since the data type may allow Invalid number values, we do
				 * not have to use it, otherwise the deserialize method would
				 * return null instead of the number we serialized causing the
				 * following check to fail.
				 */
				if (U) {
					/*
					 * the maximum unsigned value that fits into 7 bytes and
					 * that it cannot confused with an Invalid number
					 */
					checkValue(dataType,
							payload,
							new Long(0xfffffffffffffffeL),
							size,
							new byte[] {(byte) 0xfe, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff});

					// another number
					checkValue(dataType,
							payload,
							new Long(0xf8f9fafbfcfdfeffL),
							size,
							new byte[] {(byte) 0xff, (byte) 0xfe, (byte) 0xfd, (byte) 0xfc, (byte) 0xfb, (byte) 0xfa, (byte) 0xf9, (byte) 0xf8});
				} else {
					// the maximum signed value that fits into 3 bytes.
					checkValue(dataType, payload, new Long(0x7f00000000000000L), size, new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x7f});

					// the minimum signed value that fits into 3 bytes
					checkValue(dataType, payload, new Long(0x8000000000000000L + 1), size, new byte[] {0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0x80});
				}
			}
		} else if (clazz.equals(BigInteger.class)) {
			if (size != 8) {
				fail("internal TC errror: ZigBeeDataTypes that maps to a BigInteger must have a size in the range [8, 8]");
			}

			checkAgainstSerializeWrongJavaType(dataType, dataOutput);

			/*
			 * the maximum unsigned value that fits into 8 bytes and that it
			 * cannot confused with an Invalid number
			 */

			byte[] big1 = new byte[] {0x00, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xfe};
			byte[] big2 = new byte[] {0x00, (byte) 0xf8, (byte) 0xf9, (byte) 0xfa, (byte) 0xfb, (byte) 0xfc, (byte) 0xfd, (byte) 0xfe, (byte) 0xff};

			checkValue(dataType,
					payload,
					new BigInteger(big1),
					size,
					new byte[] {(byte) 0xfe, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff});

			// another number
			checkValue(dataType,
					payload,
					new BigInteger(big2),
					size,
					new byte[] {(byte) 0xff, (byte) 0xfe, (byte) 0xfd, (byte) 0xfc, (byte) 0xfb, (byte) 0xfa, (byte) 0xf9, (byte) 0xf8});

		} else if (clazz.equals(String.class)) {
			if ((size != 1) && (size != 2)) {
				fail("internal TC errror: ZigBeeDataTypes that maps to a String must have a size of 1 or 2");
			}
			checkAgainstSerializeWrongJavaType(dataType, dataOutput);
			if (size == 1) {
				// character string
				checkArrayValues(dataType, payload, new String("Test"), 5, new byte[] {0x04, 0x54, 0x65, 0x73, 0x74});
			} else if (size == 2) {
				// long character string
				checkArrayValues(dataType, payload, new String("Test"), 6, new byte[] {0x04, 0x00, 0x54, 0x65, 0x73, 0x74});
			}
		} else if (clazz.equals(byte[].class)) {
			checkAgainstSerializeWrongJavaType(dataType, dataOutput);

			byte[] octetString = new byte[] {0x01, 0x02, 0x03};
			if (size == 1) {
				/*
				 * Octet String
				 */

				checkArrayValues(dataType, payload, octetString, 4, new byte[] {0x03, 0x01, 0x02, 0x03});
			} else if (size == 2) {
				/*
				 * Long octet string
				 */

				checkArrayValues(dataType, payload, octetString, 5, new byte[] {0x03, 0x00, 0x01, 0x02, 0x03});
			} else if (size == 4) {
				/*
				 * Time and Date related types
				 */
				byte[] time = new byte[] {0x01, 0x02, (byte) 0xff, 0x04};
				byte[] timeWrong = new byte[] {0x01, 0x02, (byte) 0xff, 0x04, 0x05};

				// FIXME: which is the right order of the bytes of a date???
				checkArrayValues(dataType, payload, time, size, new byte[] {0x01, 0x02, (byte) 0xff, 0x04});

				try {
					checkArrayValues(dataType, payload, timeWrong, timeWrong.length, new byte[] {0x01, 0x02, (byte) 0xff, 0x04});
					fail("expecting IllegalArgumentException but got nothing");
				} catch (IllegalArgumentException e) {
					// we expect this
				}

			} else if (size == 8) {
				/*
				 * Security Key 128 data type
				 */

				byte[] key1 = new byte[] {(byte) 0xf1, (byte) 0xf2, (byte) 0xf3, (byte) 0x00, (byte) 0x50, (byte) 0x02, (byte) 0x04, (byte) 0xff};
				checkArrayValues(dataType, payload, key1, size, new byte[] {(byte) 0xff, 0x04, 0x02, 0x50, 0x00, (byte) 0xf3, (byte) 0xf2, (byte) 0xf1});
			}

		} else if (clazz.equals(Float.class)) {
			if ((size != 2) && (size != 4)) {
				fail("internal TC errror: ZigBeeDataTypes that maps to a Float must have a size of 2 or 4");
			}

			checkAgainstSerializeWrongJavaType(dataType, dataOutput);

			if (size == 2) {
				checkValue(dataType, payload, new Float(-252.5), size, new byte[] {(byte) 0xe4, (byte) 0xdb});
			} else if (size == 4) {
				checkValue(dataType, payload, new Float(-252.5), size, new byte[] {0x00, (byte) 0x80, 0x7c, (byte) 0xc3});
			}

		} else if (clazz.equals(Double.class)) {
			if (size != 8) {
				fail("internal TC errror: ZigBeeDataTypes that maps to a Double must have a size of 8");
			}
			checkAgainstSerializeWrongJavaType(dataType, dataOutput);

			checkValue(dataType, payload, new Double(-218.5), size, new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0x50, 0x6b, (byte) 0xc0});

		} else if (clazz.equals(Boolean.class)) {
			checkAgainstSerializeWrongJavaType(dataType, dataOutput);

			checkArrayValues(dataType, payload, Boolean.TRUE, size, new byte[] {0x01});
			checkArrayValues(dataType, payload, Boolean.FALSE, size, new byte[] {0x00});
		} else {
			fail("class " + clazz.getName() + " not checked!");
		}
	}

	private void checkValue(ZCLSimpleTypeDescription dataType, GenericFrame payload, Number value, int size, byte[] expectedOutput) {

		payload.reset();

		ZigBeeDataInput dataInput = payload.getDataInput();
		ZigBeeDataOutput dataOutput = payload.getDataOutput();

		int offset = payload.getCurrentOutputIndex();

		Class clazz = dataType.getJavaDataType();
		// try to serialize a zero value
		try {
			dataType.serialize(dataOutput, value);
		} catch (Throwable e) {
			fail("unexpected exception while serializing a " + clazz.getName() + " with value '" + value + "'");
		}

		int newOffset = payload.getCurrentOutputIndex();
		assertEquals("wrong number of byte where actually serialized on the ZigBeeOutputStream", size, newOffset - offset);

		// checks if the actual data corresponds to the expected one.

		if (!compare(payload.getBytes(), expectedOutput, offset, size)) {
			fail("wrong value of the serialized data: expected " + hexDump(expectedOutput) + ", got " + hexDump(payload.getBytes()));
		}

		Object d = null;
		try {
			d = dataType.deserialize(dataInput);
		} catch (Throwable e) {
			fail("unexpected exception while serializing a " + clazz.getName() + " with value '" + value + "'");
		}

		if (d == null) {
			fail("unexpected null value returned deserializing data type " + dataType.getName());
		}
		assertEquals("wrong class returned by the deserialize() method", clazz.getName(), d.getClass().getName());

		Number n = (Number) d;

		assertEquals("wrong value got deserializing type " + dataType.getName(), value, n);

	}

	private void checkArrayValues(ZCLSimpleTypeDescription dataType, GenericFrame payload, Object value, int size, byte[] expectedOutput) {
		payload.reset();

		ZigBeeDataInput dataInput = payload.getDataInput();
		ZigBeeDataOutput dataOutput = payload.getDataOutput();

		int offset = payload.getCurrentOutputIndex();

		Class clazz = dataType.getJavaDataType();
		try {
			dataType.serialize(dataOutput, value);
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (Throwable e) {
			fail("unexpected exception while serializing a " + clazz.getName() + " with value '" + value + "'");
		}

		int newOffset = payload.getCurrentOutputIndex();
		assertEquals("wrong number of byte where actually serialized on the ZigBeeOutputStream", size, newOffset - offset);

		// checks if the actual data corresponds to the expected one.

		if (!compare(payload.getBytes(), expectedOutput, offset, size)) {
			fail("wrong value of the serialized data: expected " + hexDump(expectedOutput) + ", got " + hexDump(payload.getBytes()));
		}

		Object d = null;
		try {
			d = dataType.deserialize(dataInput);
		} catch (Throwable e) {
			fail("unexpected exception while serializing a " + clazz.getName() + " with value '" + value + "'");
		}

		if (d == null) {
			fail("unexpected null value returned deserializing data type " + dataType.getName());
		}

		assertEquals("wrong class returned by the deserialize() method", clazz.getName(), d.getClass().getName());

		if (d.getClass().equals(String.class)) {
			assertEquals("wrong value got deserializing type " + dataType.getName(), value, d);
		} else if (d.getClass().equals(byte[].class)) {
			/*
			 * we use the value length here because for octet strings the size
			 * is length + 1 or + 2
			 */
			boolean equals = this.compare((byte[]) value, (byte[]) d, 0, ((byte[]) value).length);
			if (!equals) {
				fail("wrong value got deserializing type " + dataType.getName());
			}
		} else if (d.getClass().equals(Boolean.class)) {
			assertEquals("wrong value got deserializing type " + dataType.getName(), value, d);
		}

	}

	/**
	 * Hex dump a byte array
	 * 
	 * @param bytes The byte array
	 * @return The hex dump.
	 */

	public String hexDump(byte[] array) {
		final char[] hexDigits = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
		String out = "";
		for (int i = 0; i < array.length; i++) {
			int d = array[i];
			out += "0x" + hexDigits[((d >> 4) & 0x0f)] + hexDigits[(d & 0x0f)] + ", ";
		}
		return out;
	}

	/**
	 * Check if a specific range of index of passed byte array are zero.
	 * 
	 * @param array The array
	 * @param start The index to start to check for zero.
	 * @param size The number of bytes
	 * @return {@code true} if the bytes in the range [start, start + size) are
	 *         all zero.
	 */
	private boolean isEmpty(byte[] array, int start, int size) {
		for (int i = start; i < (start + size); i++) {
			if (array[i] != 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check if {@code array2} match a subset of array1 and returns {@code true}
	 * if they contain the same data.
	 * 
	 * @param array1 The first array
	 * @param array2 The second array
	 * @param start The offset index where to start to compare the two arrays
	 * @param size The number of bytes to compare.
	 * @return {@code true} if the two arrays are equals in the range of index
	 *         [start, start + size)
	 */

	private boolean compare(byte[] array1, byte[] array2, int start, int size) {
		if (array1 == null && array2 == null) {
			return true;
		}
		if ((array1 == null) || (array2 == null)) {
			return false;
		}

		int j = 0;
		for (int i = start; i < (start + size); i++) {
			if (array1[i] != array2[j++]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Try to serialize a wrong java type value. We should get an
	 * {@link IllegalArgumentException} exception!
	 */
	private void checkAgainstSerializeWrongJavaType(ZCLSimpleTypeDescription dataType, ZigBeeDataOutput dataOutput) {
		Class clazz = dataType.getJavaDataType();

		for (int i = 0; i < dataTypesZeroValue.length; i++) {
			Object zeroValue = this.dataTypesZeroValue[i];
			if (zeroValue.getClass().equals(clazz)) {
				// skip the actual class
				continue;
			}

			try {
				dataType.serialize(dataOutput, this.dataTypesZeroValue[i]);
			} catch (IllegalArgumentException e) {
				// we have to get this exception!
				continue;
			} catch (Throwable e) {
				// any other exception is not expected.
				fail("unexpected exception while serializing a " + clazz.getName() + " with value: '" + zeroValue + "'");
			}
			fail("expected an exception because passing a " + zeroValue.getClass().getName() + " to a dataType that is expecting a " + clazz.getClass().getName());
		}
	}

	private Object createUnsignedNumber(int size) {
		byte b = (byte) 0xBF;

		long l = 0;

		for (int i = 0; i < size; i++) {
			l = l | b & 0xff;
			b >>= 1;
		}

		return null;
	}

	private Object createObject(Class clazz, long unsignedValue) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Implementation of the a generic frame used for testing the ZigBee data
	 * types.
	 */
	class GenericFrame extends ZigBeeSerializer {

		public int getCurrentOutputIndex() {
			return getIndex();
		}

		public void reset() {
			this.resetIndex();
		}

		public GenericFrame(byte[] data) {
			this.data = data;
			this.reset();
		}
	}
}

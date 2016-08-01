
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

	byte[] payloadTestBasic = new byte[] {
			/* readByte() */ (byte) 0xf1,
			/* readInt(1) */ (byte) 0xfa,
			/* readInt(2) */ (byte) 0xf1, (byte) 0xf2,
			/* readInt(3) */ (byte) 0xf1, (byte) 0xf2, (byte) 0xf3,
			/* readInt(4) */ (byte) 0xf1, (byte) 0xf2, (byte) 0xf3, (byte) 0xf4,
			/* readLong(1) */ (byte) 0xfa,
			/* readLong(2) */ (byte) 0xf1, (byte) 0xf2,
			/* readLong(3) */ (byte) 0xf1, (byte) 0xf2, (byte) 0xf3,
			/* readLong(4) */ (byte) 0xf1, (byte) 0xf2, (byte) 0xf3, (byte) 0xf4,
			/* readLong(5) */ (byte) 0xf1, (byte) 0xf2, (byte) 0xf3, (byte) 0xf4, (byte) 0xf5,
			/* readLong(6) */ (byte) 0xf1, (byte) 0xf2, (byte) 0xf3, (byte) 0xf4, (byte) 0xf5, (byte) 0xf6,
			/* readLong(7) */ (byte) 0xf1, (byte) 0xf2, (byte) 0xf3, (byte) 0xf4, (byte) 0xf5, (byte) 0xf6, (byte) 0xf7,
			/* readLong(8) */ (byte) 0xf1, (byte) 0xf2, (byte) 0xf3, (byte) 0xf4, (byte) 0xf5, (byte) 0xf6, (byte) 0xf7, (byte) 0xf8,
			/* readBytes(4) */ 0x05, 0x06, 0x07, 0x08
	};

	public void testGeneralDataTypes() {

		// true if it is analog
		boolean A;

		// true if allows invalid values
		boolean I;

		// true if it is unsigned
		boolean U;

		I = false;
		A = false;
		U = true;

		this.checkUnsignedDataType(ZigBeeGeneralData8.getInstance(), ZigBeeDataTypes.GENERAL_DATA_8, 1, Byte.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeGeneralData16.getInstance(), ZigBeeDataTypes.GENERAL_DATA_16, 2, Short.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeGeneralData24.getInstance(), ZigBeeDataTypes.GENERAL_DATA_24, 3, Integer.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeGeneralData32.getInstance(), ZigBeeDataTypes.GENERAL_DATA_32, 4, Integer.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeGeneralData40.getInstance(), ZigBeeDataTypes.GENERAL_DATA_40, 5, Long.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeGeneralData48.getInstance(), ZigBeeDataTypes.GENERAL_DATA_48, 6, Long.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeGeneralData56.getInstance(), ZigBeeDataTypes.GENERAL_DATA_56, 7, Long.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeGeneralData64.getInstance(), ZigBeeDataTypes.GENERAL_DATA_64, 8, Long.class, U, I, A);
	}

	public void testBitmapDataTypes() {

		// true if it is analog
		boolean A;

		// true if allows invalid values
		boolean I;

		// true if it is unsigned
		boolean U;

		I = false;
		A = false;
		U = true;

		this.checkUnsignedDataType(ZigBeeBitmap8.getInstance(), ZigBeeDataTypes.BITMAP_8, 1, Byte.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeBitmap16.getInstance(), ZigBeeDataTypes.BITMAP_16, 2, Short.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeBitmap24.getInstance(), ZigBeeDataTypes.BITMAP_24, 3, Integer.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeBitmap32.getInstance(), ZigBeeDataTypes.BITMAP_32, 4, Integer.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeBitmap40.getInstance(), ZigBeeDataTypes.BITMAP_40, 5, Long.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeBitmap48.getInstance(), ZigBeeDataTypes.BITMAP_48, 6, Long.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeBitmap56.getInstance(), ZigBeeDataTypes.BITMAP_56, 7, Long.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeBitmap64.getInstance(), ZigBeeDataTypes.BITMAP_64, 8, Long.class, U, I, A);
	}

	public void testUnsignedIntegerDataTypes() {

		// true if it is analog
		boolean A;

		// true if allows invalid values
		boolean I;

		// true if it is unsigned
		boolean U;

		I = true;
		A = true;
		U = true;

		this.checkUnsignedDataType(ZigBeeUnsignedInteger8.getInstance(), ZigBeeDataTypes.UNSIGNED_INTEGER_8, 1, Short.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeUnsignedInteger16.getInstance(), ZigBeeDataTypes.UNSIGNED_INTEGER_16, 2, Integer.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeUnsignedInteger24.getInstance(), ZigBeeDataTypes.UNSIGNED_INTEGER_24, 3, Integer.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeUnsignedInteger32.getInstance(), ZigBeeDataTypes.UNSIGNED_INTEGER_32, 4, Long.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeUnsignedInteger40.getInstance(), ZigBeeDataTypes.UNSIGNED_INTEGER_40, 5, Long.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeUnsignedInteger48.getInstance(), ZigBeeDataTypes.UNSIGNED_INTEGER_48, 6, Long.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeUnsignedInteger56.getInstance(), ZigBeeDataTypes.UNSIGNED_INTEGER_56, 7, Long.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeUnsignedInteger64.getInstance(), ZigBeeDataTypes.UNSIGNED_INTEGER_64, 8, BigInteger.class, U, I, A);
	}

	public void testSignedIntegerDataTypes() {

		// true if it is analog
		boolean A;

		// true if allows invalid values
		boolean I;

		// true if it is unsigned
		boolean U;

		I = true;
		A = true;
		U = false;

		this.checkUnsignedDataType(ZigBeeSignedInteger8.getInstance(), ZigBeeDataTypes.SIGNED_INTEGER_8, 1, Byte.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeSignedInteger16.getInstance(), ZigBeeDataTypes.SIGNED_INTEGER_16, 2, Short.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeSignedInteger24.getInstance(), ZigBeeDataTypes.SIGNED_INTEGER_24, 3, Integer.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeSignedInteger32.getInstance(), ZigBeeDataTypes.SIGNED_INTEGER_32, 4, Integer.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeSignedInteger40.getInstance(), ZigBeeDataTypes.SIGNED_INTEGER_40, 5, Long.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeSignedInteger48.getInstance(), ZigBeeDataTypes.SIGNED_INTEGER_48, 6, Long.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeSignedInteger56.getInstance(), ZigBeeDataTypes.SIGNED_INTEGER_56, 7, Long.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeSignedInteger64.getInstance(), ZigBeeDataTypes.SIGNED_INTEGER_64, 8, Long.class, U, I, A);
	}

	public void testEnumDataTypes() {

		// true if allows invalid values
		boolean I;

		// true if it is analog
		boolean A;

		// true if it is unsigned
		boolean U;

		I = true;
		A = false;
		U = true;

		this.checkUnsignedDataType(ZigBeeEnumeration8.getInstance(), ZigBeeDataTypes.ENUMERATION_8, 1, Short.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeEnumeration16.getInstance(), ZigBeeDataTypes.ENUMERATION_16, 2, Integer.class, U, I, A);
	}

	public void testTimeDataTypes() {

		// true if allows invalid values
		boolean I;

		// true if it is analog
		boolean A;

		// true if it is unsigned
		boolean U;

		I = true;
		A = true;
		U = true; // don't care for Time of Day and Date

		this.checkUnsignedDataType(ZigBeeTimeOfDay.getInstance(), ZigBeeDataTypes.TIME_OF_DAY, 4, byte[].class, U, I, A);
		this.checkUnsignedDataType(ZigBeeDate.getInstance(), ZigBeeDataTypes.DATE, 4, byte[].class, U, I, A);
		this.checkUnsignedDataType(ZigBeeUTCTime.getInstance(), ZigBeeDataTypes.UTC_TIME, 4, Long.class, U, I, A);
	}

	public void testIdentifierDataTypes() {

		// true if allows invalid values
		boolean I;

		// true if it is analog
		boolean A;

		// true if it is unsigned
		boolean U;

		I = true;
		A = false;
		U = true;

		this.checkUnsignedDataType(ZigBeeAttributeID.getInstance(), ZigBeeDataTypes.ATTRIBUTE_ID, 2, Integer.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeClusterID.getInstance(), ZigBeeDataTypes.CLUSTER_ID, 2, Integer.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeBACnet.getInstance(), ZigBeeDataTypes.BACNET_OID, 4, Long.class, U, I, A);
	}

	public void testBooleanDataType() {

		// true if allows invalid values
		boolean I;

		// true if it is analog
		boolean A;

		// true if it is unsigned
		boolean U;

		I = true;
		A = false;
		U = true; // we don't care

		this.checkUnsignedDataType(ZigBeeBoolean.getInstance(), ZigBeeDataTypes.BOOLEAN, 1, Boolean.class, U, I, A);
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

		this.checkUnsignedDataType(ZigBeeFloatingSemi.getInstance(), ZigBeeDataTypes.FLOATING_SEMI, 2, Float.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeFloatingSingle.getInstance(), ZigBeeDataTypes.FLOATING_SINGLE, 4, Float.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeFloatingDouble.getInstance(), ZigBeeDataTypes.FLOATING_DOUBLE, 8, Double.class, U, I, A);
	}

	public void testStringDataTypes() {

		// true if allows invalid values
		boolean I;

		// true if it is analog
		boolean A;

		// true if it is unsigned (only for numbers)
		boolean U;

		I = true;
		A = false;
		U = false; // don't care for Strings

		this.checkUnsignedDataType(ZigBeeCharacterString.getInstance(), ZigBeeDataTypes.CHARACTER_STRING, 1, String.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeOctetString.getInstance(), ZigBeeDataTypes.OCTET_STRING, 1, byte[].class, U, I, A);
		this.checkUnsignedDataType(ZigBeeLongCharacterString.getInstance(), ZigBeeDataTypes.LONG_CHARACTER_STRING, 2, String.class, U, I, A);
		this.checkUnsignedDataType(ZigBeeLongOctetString.getInstance(), ZigBeeDataTypes.LONG_OCTET_STRING, 2, byte[].class, U, I, A);
	}

	public void testMiscellaneousDataTypes() {

		// true if allows invalid values
		boolean I;

		// true if it is analog
		boolean A;

		// true if it is unsigned (only for numbers)
		boolean U;

		I = true;
		A = false;
		U = false;

		this.checkUnsignedDataType(ZigBeeIEEE_ADDRESS.getInstance(), ZigBeeDataTypes.IEEE_ADDRESS, 8, BigInteger.class, U, I, A);

		I = false;
		this.checkUnsignedDataType(ZigBeeSecurityKey128.getInstance(), ZigBeeDataTypes.SECURITY_KEY_128, 1, byte[].class, U, I, A);
	}

	protected void checkUnsignedDataType(ZCLSimpleTypeDescription dataType, short dataTypeId, int size, Class clazz, boolean U, boolean I, boolean A) {

		/*
		 * Checks if the name assigned to the data type is the expected one.
		 */

		/*
		 * Create a buffer of big enough!
		 */
		byte[] data = new byte[100];

		ZigBeeDataImpl payload = new ZigBeeDataImpl(data);

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
		 * Checks the correctnes of the analog/digital information
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
	}

	/**
	 * Implementation of the ZigBeeDataInput for testing the ZigBee data types
	 */
	class ZigBeeDataImpl extends ZigBeeSerializer {

		private int	outputIndex	= 0;
		private int	inputIndex	= 0;

		public int getCurrentInputIndex() {
			return inputIndex;
		}

		public int getCurrentOutputIndex() {
			return getIndex();
		}

		public void resetIndexes() {
			outputIndex = 0;
			inputIndex = 0;
		}

		public ZigBeeDataImpl(byte[] data) {
			this.data = data;
			this.resetIndexes();
		}
	}
}

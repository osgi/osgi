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

package org.osgi.test.cases.zigbee;

import java.io.EOFException;
import org.osgi.service.zigbee.ZCLFrame;
import org.osgi.service.zigbee.ZCLHeader;
import org.osgi.service.zigbee.ZigBeeDataInput;
import org.osgi.service.zigbee.ZigBeeDataOutput;
import org.osgi.test.cases.zigbee.config.file.ConfigurationFileReader;
import org.osgi.test.cases.zigbee.mock.ZCLFrameImpl;
import org.osgi.test.cases.zigbee.mock.ZCLHeaderImpl;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * Test Cases to test the internal ZCLFrame and ZCLHeader interfaces
 * implementations
 * 
 * @author $Id$
 */
public class ZCLFrameImplTestCase extends DefaultTestBundleControl {

	private static int		minHeaderSize;
	private static int		maxHeaderSize;

	byte[]					payloadTest1	= new byte[] {(byte) 0xf1, (byte) 0xfa, (byte) 0xf1, (byte) 0xf2, (byte) 0xf1, (byte) 0xf2,
			(byte) 0xf3, (byte) 0xf1, (byte) 0xf2, (byte) 0xf3, (byte) 0xf4, (byte) 0xfa, (byte) 0xf1, (byte) 0xf2,
			(byte) 0xf1, (byte) 0xf2, (byte) 0xf3, (byte) 0xf1, (byte) 0xf2, (byte) 0xf3, (byte) 0xf4, (byte) 0xf1,
			(byte) 0xf2, (byte) 0xf3, (byte) 0xf4, (byte) 0xf5, (byte) 0xf1, (byte) 0xf2, (byte) 0xf3, (byte) 0xf4,
			(byte) 0xf5, (byte) 0xf6, (byte) 0xf1, (byte) 0xf2, (byte) 0xf3, (byte) 0xf4, (byte) 0xf5, (byte) 0xf6,
			(byte) 0xf7, (byte) 0xf1, (byte) 0xf2, (byte) 0xf3, (byte) 0xf4, (byte) 0xf5, (byte) 0xf6, (byte) 0xf7,
			(byte) 0xf8, 0x05, 0x06, 0x07, 0x08, 0x00, (byte) 0x00, (byte) 0x00, 0x00, (byte) 0x01, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

	byte[]					bytes1			= new byte[] {0x05, 0x06, 0x07, 0x08};

	private boolean			skipNaN			= true;

	/**
	 * Tests the internal implementation of the ZCLFrame interface (not
	 * manufacturer specific case)
	 */

	ConfigurationFileReader	conf;

	private String			confFilePath	= "zigbee-template.xml";

	protected void setUp() throws Exception {
		log("Prepare for ZigBee Test Case");

		prepareTestStart();
		log("Prepared for ZigBee Test Case");
	}

	protected void tearDown() throws Exception {
		log("Tear down ZigBee Test Case");
	}

	private void prepareTestStart() throws Exception {
		TestStepLauncher launcher = TestStepLauncher.launch(confFilePath, getContext());
		conf = launcher.getConfReader();
		minHeaderSize = conf.getHeaderMinSize();
		maxHeaderSize = conf.getHeaderMaxSize();

		System.out.println("minHeaderSize = " + minHeaderSize);
		System.out.println("maxHeaderSize = " + maxHeaderSize);
	}

	public void testInternalZCLFrameImpl1() {

		int commandId = 0x10;
		boolean isClusterSpecificCommand = true;
		boolean isClientServerDirection = false;
		boolean disableDefaultResponse = true;
		byte sequenceNumber = 46;

		ZCLHeader headerInit = new ZCLHeaderImpl(commandId,
				isClusterSpecificCommand,
				isClientServerDirection,
				disableDefaultResponse,
				sequenceNumber);

		/*
		 * Test if the newly created frame returns the set values accordingly
		 */

		ZCLFrameImpl frame = new ZCLFrameImpl(headerInit);

		ZCLHeader header = frame.getHeader();

		assertNotNull("the getHeader() method must return a not null ZCLHeader instance", header);
		assertEquals("ZCLHeader.isClusterSpecificCommand()",
				isClusterSpecificCommand,
				header.isClusterSpecificCommand());
		assertEquals("ZCLHeader.isClientServerDirection()", isClientServerDirection, header.isClientServerDirection());
		assertEquals("ZCLHeader.isDefaultResponseDisabled()",
				disableDefaultResponse,
				header.isDefaultResponseDisabled());
		assertEquals("ZCLHeader.isManufacturerSpecific()", false, header.isManufacturerSpecific());
		assertEquals("ZCLHeader.getManufacturerCode()", -1, header.getManufacturerCode());
		assertEquals("ZCLHeader.getSequenceNumber()", sequenceNumber, header.getSequenceNumber());

		commonChecks(frame);
	}

	/**
	 * Tests the internal implementation of the ZCLFrame interface (manufacturer
	 * specific case)
	 */

	public void testInternalZCLFrameImplManufacturerSpecific() {

		int commandId = 0x10;
		boolean isClusterSpecificCommand = false;
		boolean isClientServerDirection = true;
		boolean disableDefaultResponse = false;
		int manufacturerCode = 0x1010;
		byte sequenceNumber = 44;

		ZCLHeader headerInit = new ZCLHeaderImpl(commandId,
				isClusterSpecificCommand,
				isClientServerDirection,
				disableDefaultResponse,
				sequenceNumber,
				manufacturerCode);

		/*
		 * Test if the newly created frame returns the set values accordingly
		 */

		ZCLFrameImpl frame = new ZCLFrameImpl(headerInit);

		ZCLHeader header = frame.getHeader();

		assertNotNull("the getHeader() method must return a not null ZCLHeader instance", header);
		assertEquals("ZCLHeader isClusterSpecificCommand()",
				isClusterSpecificCommand,
				header.isClusterSpecificCommand());
		assertEquals("ZCLHeader disableDefaultResponse()", isClientServerDirection, header.isClientServerDirection());
		assertEquals("ZCLHeader isDefaultResponseEnabled()",
				disableDefaultResponse,
				header.isDefaultResponseDisabled());
		assertEquals("ZCLHeader isManufacturerSpecific()", true, header.isManufacturerSpecific());
		assertEquals("ZCLHeader.getManufacturerCode()", manufacturerCode, header.getManufacturerCode());
		assertEquals("ZCLHeader.getSequenceNumber()", sequenceNumber, header.getSequenceNumber());

		commonChecks(frame);
	}

	public void commonChecks(ZCLFrameImpl frame) {

		ZCLHeader header = frame.getHeader();

		byte[] rawFrame = frame.getBytes();

		assertNotNull("the getBytes() must return a valid byte array", rawFrame);

		int minFrameSize = getMinFrameSize(frame.getHeader());
		if (rawFrame.length < minFrameSize) {
			fail("a the ZCL raw frame is too short");
		}

		byte[] otherRawFrame = frame.getBytes();

		assertNotNull("the getHeader() method must return a not null ZCLHeader instance", header);

		if (otherRawFrame == rawFrame) {
			fail("the getBytes() must return a different array each time it is called");
		}

		ZigBeeDataInput is = frame.getDataInput();

		assertNotNull("getInputStream() must not return null", is);

		ZigBeeDataInput isOther = frame.getDataInput();

		assertNotNull("calling ZCLFrame.getDataInput() twice must not return null", isOther);

		if (is == isOther) {
			fail("calling ZCLFrame.getDataInput() twice must not return differenct ZigBeeDataInput instances");
		}
	}

	public void testDataInput() {

		int commandId = 0x10;
		boolean isClusterSpecificCommand = true;
		boolean isClientServerDirection = true;
		boolean disableDefaultResponse = false;
		int manufacturerCode = 0x1010;
		byte sequenceNumber = 45;

		/*
		 * Creates a ZCLHeader
		 */
		ZCLHeader headerInit = new ZCLHeaderImpl(commandId,
				isClusterSpecificCommand,
				isClientServerDirection,
				disableDefaultResponse,
				sequenceNumber,
				manufacturerCode);

		/*
		 * Creates the frame with initial payload, paylaodTest1
		 */
		ZCLFrame frame = new ZCLFrameImpl(headerInit, payloadTest1);

		ZigBeeDataInput dataInput = frame.getDataInput();

		/**
		 * Test ZigBeeDataInput.readByte()
		 */

		try {
			assertEquals(payloadTest1[0], dataInput.readByte());
		} catch (Exception e) {
			fail("got an unexpected exception while reading from data input");
		}

		/**
		 * ########## Test ZigBeeDataInput.readInt()
		 */

		/**
		 * Checks on IllegalArgumentException on wrong size parameter
		 */

		try {
			int v = dataInput.readInt(0);
			fail("we expected an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// We expect this exception
		} catch (Exception e) {
			fail("got an unexpected exception while reading int, we expected an IllegalArgumentException");
		}

		try {
			int v = dataInput.readInt(5);
			fail("we expected an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// We expect this exception
		} catch (Exception e) {
			fail("got an unexpected exception while reading int, we expected an IllegalArgumentException");
		}

		try {
			assertEquals(0xfa, dataInput.readInt(1));
			assertEquals(0xf2f1, dataInput.readInt(2));
			assertEquals(0xf3f2f1, dataInput.readInt(3));
			assertEquals(0xf4f3f2f1, dataInput.readInt(4));
		} catch (Exception e) {
			fail("got an unexpected exception while reading from data input, ");
		}

		/**
		 * Test ZigBeeDataInput.readLong()
		 */

		/**
		 * Checks on IllegalArgumentException on wrong size parameter
		 */

		try {
			long v = dataInput.readLong(0);
			fail("we expected an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// We expect this exception
		} catch (Exception e) {
			fail("got an unexpected exception while reading long, we expected an IllegalArgumentException");
		}

		try {
			long v = dataInput.readLong(9);
			fail("we expected an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// We expect this exception
		} catch (Exception e) {
			fail("got an unexpected exception while reading long, we expected an IllegalArgumentException");
		}

		try {
			assertEquals(0xfa, dataInput.readLong(1));
			assertEquals(0xf2f1, dataInput.readLong(2));
			assertEquals(0xf3f2f1, dataInput.readLong(3));
			assertEquals(0xf4f3f2f1L, dataInput.readLong(4));
			assertEquals(0xf5f4f3f2f1L, dataInput.readLong(5));
			assertEquals(0xf6f5f4f3f2f1L, dataInput.readLong(6));
			assertEquals(0xf7f6f5f4f3f2f1L, dataInput.readLong(7));
			assertEquals(0xf8f7f6f5f4f3f2f1L, dataInput.readLong(8));
		} catch (Exception e) {
			fail("got an unexpected exception while reading from data input");
		}

		/**
		 * Test ########## ZigBeeDataInput.readBytes()
		 */

		try {
			byte[] b = dataInput.readBytes(bytes1.length);
			assertNotNull("ZigbeeDataInput.getBytes() returned a null array", b);
			assertEquals("readBytes returned a wrong number of bytes", bytes1.length, b.length);

			boolean differs = false;
			for (int i = 0; i < b.length; i++) {
				if (b[i] != bytes1[i]) {
					differs = true;
				}
			}

			if (differs) {
				fail("ZigbeeDataInput.getBytes() returned a wrong byte array");
			}

		} catch (Exception e) {
			fail("got an unexpected exception while reading from data input");
		}

		/**
		 * Test ########## ZigBeeDataInput.readFloat()
		 */

		/**
		 * Checks on IllegalArgumentException on wrong size parameter
		 */

		try {
			float v = dataInput.readFloat(1);
			fail("we expected an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// We expect this exception
		} catch (Exception e) {
			fail("got an unexpected exception while reading float, we expected an IllegalArgumentException");
		}

		try {
			float v = dataInput.readFloat(3);
			fail("we expected an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// We expect this exception
		} catch (Exception e) {
			fail("got an unexpected exception while reading float, we expected an IllegalArgumentException");
		}

		try {
			float v = dataInput.readFloat(5);
			fail("we expected an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// We expect this exception
		} catch (Exception e) {
			fail("got an unexpected exception while reading float, we expected an IllegalArgumentException");
		}

		try {
			// FIXME: not supported, yet, semi precision
			// assertEquals(4, dataInput.readFloat(2));

			if (!skipNaN) {
				float f = dataInput.readFloat(4);
			}
			assertEquals(0.0, dataInput.readFloat(4), 0.00001);
			assertEquals(Float.MIN_VALUE, dataInput.readFloat(4), 0.00001);
		} catch (Exception e) {
			fail("got an unexpected exception while reading from data input");
		}

		try {
			if (!skipNaN) {
				double d = dataInput.readDouble();
			}
			assertEquals(0.0, dataInput.readDouble(), 0.00001);
			assertEquals(Double.MIN_VALUE, dataInput.readDouble(), 0.00001);
		} catch (Exception e) {
			fail("got an unexpected exception while reading from data input");
		}

		try {
			dataInput.readByte();
			fail("we expected EOFException");
		} catch (EOFException e) {
			// We expect the end of the buffer
		} catch (Exception e) {
			fail("got unexpected exception, instead of EOFException: " + e.getMessage());
		}
	}

	public void testDataOutput() {

		int commandId = 0x10;
		boolean isClusterSpecificCommand = true;
		boolean isClientServerDirection = true;
		boolean disableDefaultResponse = false;
		int manufacturerCode = 0x1010;
		byte sequenceNumber = 45;

		/*
		 * Creates a ZCLHeader
		 */
		ZCLHeader headerInit = new ZCLHeaderImpl(commandId,
				isClusterSpecificCommand,
				isClientServerDirection,
				disableDefaultResponse,
				sequenceNumber,
				manufacturerCode);

		/*
		 * Creates the frame with no initial payload
		 */
		ZCLFrameImpl frame = new ZCLFrameImpl(headerInit, payloadTest1.length + 20);

		/*
		 * The getDataOutput() method is not present in the ZCLFrame interface.
		 */
		ZigBeeDataOutput dataOutput = frame.getDataOutput();

		/**
		 * Checks on IllegalArgumentException on wrong size parameter
		 */

		try {
			dataOutput.writeInt(0, 0);
			fail("we expected an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// We expect this exception
		} catch (Exception e) {
			fail("got an unexpected exception while reading float, we expected an IllegalArgumentException");
		}

		try {
			dataOutput.writeInt(0, 5);
			fail("we expected an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// We expect this exception
		} catch (Exception e) {
			fail("got an unexpected exception in the writeInt(), we expected an IllegalArgumentException");
		}

		try {
			dataOutput.writeLong(0, 0);
			fail("we expected an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// We expect this exception
		} catch (Exception e) {
			fail("got an unexpected exception in the writeLong(), we expected an IllegalArgumentException");
		}

		try {
			dataOutput.writeLong(0, 9);
			fail("we expected an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// We expect this exception
		} catch (Exception e) {
			fail("got an unexpected exception in the writeLong(), we expected an IllegalArgumentException");
		}

		/**
		 * Test writeByte() methods of ZigBeeDataOutput interface
		 */

		try {
			dataOutput.writeByte(payloadTest1[0]);
		} catch (Exception e) {
			fail("got an unexpected exception while reading from data input: " + e.getMessage());
		}

		/**
		 * Test writeInt() method of ZigBeeDataOutput interface
		 */

		try {
			dataOutput.writeInt(0xfa, 1);
			dataOutput.writeInt(0xf2f1, 2);
			dataOutput.writeInt(0xf3f2f1, 3);
			dataOutput.writeInt(0xf4f3f2f1, 4);
		} catch (Exception e) {
			fail("got an unexpected exception while reading from data input: " + e.getMessage());
		}

		/**
		 * Test writeLong() method of ZigBeeDataOutput interface
		 */

		try {
			dataOutput.writeLong(0xfa, 1);
			dataOutput.writeLong(0xf2f1, 2);
			dataOutput.writeLong(0xf3f2f1, 3);
			dataOutput.writeLong(0xf4f3f2f1, 4);
			dataOutput.writeLong(0xf5f4f3f2f1L, 5);
			dataOutput.writeLong(0xf6f5f4f3f2f1L, 6);
			dataOutput.writeLong(0xf7f6f5f4f3f2f1L, 7);
			dataOutput.writeLong(0xf8f7f6f5f4f3f2f1L, 8);
		} catch (Exception e) {
			fail("got an unexpected exception while reading from data input: " + e.getMessage());
		}

		/**
		 * Test writeBytes() method of ZigBeeDataOutput interface
		 */

		try {
			dataOutput.writeBytes(bytes1, bytes1.length);
		} catch (Exception e) {
			fail("got an unexpected exception while reading from data input: " + e.getMessage());
		}

		/**
		 * Test writeFloat() method of ZigBeeDataOutput interface
		 */

		try {
			if (!skipNaN) {
				dataOutput.writeFloat(Float.NaN, 4);
			}
			dataOutput.writeFloat(0, 4);
			dataOutput.writeFloat(Float.MIN_VALUE, 4);
		} catch (Exception e) {
			fail("got an unexpected exception while reading from data input: " + e.getMessage());
		}

		/**
		 * Test writeDouble() method of ZigBeeDataOutput interface
		 */

		try {
			if (!skipNaN) {
				dataOutput.writeDouble(Double.NaN);
			}
			dataOutput.writeDouble(0);
			dataOutput.writeDouble(Double.MIN_VALUE);
		} catch (Exception e) {
			fail("got an unexpected exception while reading from data input: " + e.getMessage());
		}

		/*
		 * Checks if the data has been marshalled correctly
		 */

		byte[] rawFrame = frame.getBytes();

		boolean equals = true;

		int offs = getMinFrameSize(frame.getHeader());

		int i;
		for (i = 0; i < rawFrame.length - offs; i++) {
			if (rawFrame[i + offs] != payloadTest1[i]) {
				equals = false;
				break;
			}
		}

		if (!equals) {
			fail("marshalling failed at index " + i);
		} else {
			assertEquals("ZCLFrame.getBytes() size", payloadTest1.length + offs, rawFrame.length);
		}
	}

	protected int getMinFrameSize(ZCLHeader header) {
		return header.isManufacturerSpecific() ? (maxHeaderSize) : minHeaderSize;
	}
}

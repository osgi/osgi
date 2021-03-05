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

package org.osgi.test.cases.zigbee;

import java.io.EOFException;

import org.osgi.service.zigbee.ZCLFrame;
import org.osgi.service.zigbee.ZCLHeader;
import org.osgi.service.zigbee.ZigBeeDataInput;
import org.osgi.service.zigbee.ZigBeeDataOutput;
import org.osgi.test.cases.zigbee.config.file.ConfigurationFileReader;
import org.osgi.test.cases.zigbee.mock.ZCLFrameImpl;
import org.osgi.test.cases.zigbee.mock.ZCLHeaderImpl;

/**
 * Test Cases to test the internal ZCLFrame and ZCLHeader interfaces
 * implementations
 * 
 * @author $Id$
 */
public class ZCLFrameImplTestCase extends ZigBeeTestCases {

	private static final String	TAG					= ZCLFrameImplTestCase.class.getName();

	private static int			minHeaderSize;
	private static int			maxHeaderSize;

	byte[]						payloadTestBasic	= new byte[] {
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

	/**
	 * This is a payload used for testing marshaling and unmarshaling of float
	 * and double types.
	 */
	byte[]						payloadTestFloat	= new byte[] {
			/* readFloat(2), NaN */ 0x01, 0x7C,
			/* readFloat(2), 0.0 */ 0x00, 0x00,
			/* readFloat(2), + INFIN */ 0x00, (byte) 0x7C,
			/* readFloat(2), - INFIN */ 0x00, (byte) 0xFC,
			/* readFloat(2), denormalized */ 0x68, 0x03,
			/* readFloat(4), normalized (-252.5) */ (byte) 0xe4, (byte) 0xdb,
			/* readFloat(4), normalized (+118.5) */ 0x68, 0x57,

			/* readFloat(4), NaN */ 0x01, 0x00, (byte) 0x80, (byte) 0x7f,
			/* readFloat(4), 0.0 */ 0x00, 0x00, 0x00, 0x00,
			/* readFloat(4), + INFIN */ 0x00, 0x00, (byte) 0x80, (byte) 0x7f,
			/* readFloat(4), - INFIN */ 0x00, 0x00, (byte) 0x80, (byte) 0xff,
			/* readFloat(4), denormalized */ 0x00, 0x00, (byte) 0x6d, (byte) 0x00,
			/* readFloat(4), normalized (-252.5) */ 0x00, (byte) 0x80, 0x7c, (byte) 0xc3,
			/* readFloat(4), normalized (+118.5) */ 0x00, 0x00, (byte) 0xed, (byte) 0x42,

			/* readDouble(), NaN */ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xf8, 0x7f,
			/* readDouble(), 0.0 */ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			/* readDouble(), + INFIN */ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xf0, 0x7f,
			/* readDouble(), - INFIN */ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xf0, (byte) 0xff,
			/* readDouble(), denormalized */ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0x08, 0x00,
			/* readDouble(), normalized (-252.5) */ 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0x90, 0x6f, (byte) 0xc0,
			/* readDouble(), normalized (+118.5) */ 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xa0, 0x5d, 0x40
	};

	byte[]						bytes1				= new byte[] {0x05, 0x06, 0x07, 0x08};

	/**
	 * Tests the internal implementation of the ZCLFrame interface (not
	 * manufacturer specific case)
	 */

	ConfigurationFileReader		conf;

	protected void setUp() throws Exception {
		log(TAG, "Prepare for ZigBee Test Case");

		prepareTestStart();
		log(TAG, "Prepared for ZigBee Test Case");
	}

	protected void tearDown() throws Exception {
		log(TAG, "Tear down ZigBee Test Case");
	}

	private void prepareTestStart() throws Exception {
		TestStepLauncher launcher = TestStepLauncher.getInstance(getContext());

		conf = launcher.getConfiguration();
		minHeaderSize = conf.getHeaderMinSize();
		maxHeaderSize = conf.getHeaderMaxSize();

		log(TAG, "minHeaderSize = " + minHeaderSize);
		log(TAG, "maxHeaderSize = " + maxHeaderSize);
	}

	public void testZCLHeader() {

		short commandId = 0x10;
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

		log(TAG, "testInternalZCLFrameImpl");

		short commandId = 0x10;
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
		assertNotNull("ZCLHeader.getBytes(): must return a valid byte array", rawFrame);

		int minFrameSize = getMinFrameSize(frame.getHeader());
		if (rawFrame.length < minFrameSize) {
			fail("ZCLHeader.getBytes(): the returned raw frame is too short.");
		}

		/*
		 * Check getSize()
		 */
		int size = frame.getSize();
		assertEquals("ZCLFrame.getSize(): must be equal to the buffer length returned by ZCLFrame.getBytes()", rawFrame.length, size);

		byte[] otherRawFrame = frame.getBytes();

		assertNotNull("ZCLHeader.getHeader(): the must return a not null ZCLHeader instance", header);
		assertNotSame("ZCLFrame.getBytes(): must return a different array instance each time it is called", otherRawFrame, rawFrame);

		ZigBeeDataInput is = frame.getDataInput();
		assertNotNull("ZCLFrame.getInputStream() must not return null", is);

		ZigBeeDataInput isOther = frame.getDataInput();
		assertNotNull("ZCLFrame.getDataInput(): calling this method twice must not return null", isOther);
		assertNotSame("ZCLFrame.getDataInput(): must return a different ZigBeeDataInput instance at each call.", is, isOther);

		/*
		 * Check the other flavor of getBytes()
		 */

		byte buffer[] = new byte[size + 20];

		int len = frame.getBytes(buffer);
		assertEquals("ZCLFrame.getBuffer(byte[] buffer): the returned length must be identical to ZCLFrame.getSize()", size, len);

		/*
		 * compares only the payload of the buffer with the payload of the raw
		 * frame
		 */
		for (int i = minFrameSize; i < rawFrame.length; i++) {
			assertEquals("ZCLFrame: the payload section of the raw frame filled by ZCLFrame.getBytes(byte[] buffer) do not match with the ZCLFrame.getBytes() at index " + i, rawFrame[i], buffer[i]);
		}
	}

	public void testDataInput() {

		log(TAG, "testDataInput()");

		short commandId = 0x10;
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
		 * Creates the frame with initial payload, payloadTestBasic and we
		 * compare what it is read with what it is expected.
		 */
		ZCLFrame frame = new ZCLFrameImpl(headerInit, payloadTestBasic);

		ZigBeeDataInput dataInput = frame.getDataInput();

		/*
		 * Test ZigBeeDataInput.readByte()
		 */

		try {
			assertEquals("ZigBeeDataInput.readByte(): wrong value retrieved.", payloadTestBasic[0], dataInput.readByte());
		} catch (Exception e) {
			fail("got an unexpected exception while reading from data input");
		}

		/*
		 * ########## Test ZigBeeDataInput.readInt()
		 */

		/*
		 * Checks on IllegalArgumentException on wrong size parameter
		 */

		log(TAG, "test ZigBeeDataInput.readInt()");

		try {
			dataInput.readInt(0);
			fail("we expected an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// We expect this exception
		} catch (Exception e) {
			fail("ZigBeeDataInput.readInt(): got an unexpected exception while reading int, we expected an IllegalArgumentException");
		}

		try {
			dataInput.readInt(5);
			fail("we expected an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// We expect this exception
		} catch (Exception e) {
			fail("ZigBeeDataInput.readInt(): got an unexpected exception while reading int, we expected an IllegalArgumentException");
		}

		try {
			assertEquals("ZigBeeDataInput.readInt(): wrong value retrieved.", 0xfffffffa, dataInput.readInt(1));
			assertEquals("ZigBeeDataInput.readInt(): wrong value retrieved.", 0xfffff2f1, dataInput.readInt(2));
			assertEquals("ZigBeeDataInput.readInt(): wrong value retrieved.", 0xfff3f2f1, dataInput.readInt(3));
			assertEquals("ZigBeeDataInput.readInt(): wrong value retrieved.", 0xf4f3f2f1, dataInput.readInt(4));
		} catch (Exception e) {
			fail("ZigBeeDataInput.readInt(): got an unexpected exception while reading from data input, ");
		}

		/*
		 * Test ZigBeeDataInput.readLong()
		 */

		/*
		 * Checks on IllegalArgumentException on wrong size parameter
		 */

		log(TAG, "test ZigBeeDataInput.readLong()");

		try {
			dataInput.readLong(0);
			fail("we expected an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// We expect this exception
		} catch (Exception e) {
			fail("got an unexpected exception while reading long, we expected an IllegalArgumentException");
		}

		try {
			dataInput.readLong(9);
			fail("we expected an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// We expect this exception
		} catch (Exception e) {
			fail("got an unexpected exception while reading long, we expected an IllegalArgumentException");
		}

		try {
			assertEquals(0xfffffffffffffffaL, dataInput.readLong(1));
			assertEquals(0xfffffffffffff2f1L, dataInput.readLong(2));
			assertEquals(0xfffffffffff3f2f1L, dataInput.readLong(3));
			assertEquals(0xfffffffff4f3f2f1L, dataInput.readLong(4));
			assertEquals(0xfffffff5f4f3f2f1L, dataInput.readLong(5));
			assertEquals(0xfffff6f5f4f3f2f1L, dataInput.readLong(6));
			assertEquals(0xfff7f6f5f4f3f2f1L, dataInput.readLong(7));
			assertEquals(0xf8f7f6f5f4f3f2f1L, dataInput.readLong(8));
		} catch (Exception e) {
			fail("got an unexpected exception while reading from data input");
		}

		/*
		 * Test ########## ZigBeeDataInput.readBytes()
		 */

		log(TAG, "test ZigBeeDataInput.readBytes()");

		try {
			byte[] b = dataInput.readBytes(bytes1.length);
			assertNotNull("ZigbeeDataInput.readBytes() returned a null array", b);
			assertEquals("ZigbeeDataInput.readBytes(): readBytes() returned a wrong number of bytes", bytes1.length, b.length);

			boolean differs = false;
			for (int i = 0; i < b.length; i++) {
				if (b[i] != bytes1[i]) {
					differs = true;
				}
			}

			if (differs) {
				fail("ZigbeeDataInput.readBytes() returned a wrong byte array");
			}

		} catch (Exception e) {
			fail("got an unexpected exception while reading from data input");
		}

		/*
		 * Issue reads using a frame pre-initialized with some already known
		 * values
		 */
		frame = new ZCLFrameImpl(headerInit, payloadTestFloat);

		dataInput = frame.getDataInput();

		/*
		 * Test ########## ZigBeeDataInput.readFloat()
		 */

		log(TAG, "test ZigBeeDataInput.readFloat()");

		/*
		 * Checks on IllegalArgumentException on wrong value of the size
		 * parameter
		 */

		try {
			dataInput.readFloat(1);
			fail("we expected an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// We expect this exception
		} catch (Exception e) {
			fail("got an unexpected exception while reading float, we expected an IllegalArgumentException");
		}

		try {
			dataInput.readFloat(3);
			fail("we expected an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// We expect this exception
		} catch (Exception e) {
			fail("got an unexpected exception while reading float, we expected an IllegalArgumentException");
		}

		try {
			dataInput.readFloat(5);
			fail("we expected an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// We expect this exception
		} catch (Exception e) {
			fail("got an unexpected exception while reading float, we expected an IllegalArgumentException");
		}

		/*
		 * Test ########## semi-precision readFloat()
		 */

		try {
			// NaN
			float f = dataInput.readFloat(2);
			assertTrue("the semi-precision float read must be NaN", Float.isNaN(f));

			// Zero
			assertEquals(0.0, dataInput.readFloat(2), 0f);

			// Positive Infinitive
			assertEquals(Float.POSITIVE_INFINITY, dataInput.readFloat(2), 0f);

			// Negative Infinitive
			assertEquals(Float.NEGATIVE_INFINITY, dataInput.readFloat(2), 0f);

			// denormalized value
			assertEquals(1.0010069E-38f, dataInput.readFloat(2), 0f);

			// normalized value 1
			assertEquals(-252.5f, dataInput.readFloat(2), 0f);

			// normalized value 2
			assertEquals(118.5f, dataInput.readFloat(2), 0f);

		} catch (Exception e) {
			fail("got an unexpected exception while reading from data input");
		}

		/*
		 * Test ########## standard precision readFloat()
		 */

		try {

			// NaN
			float f = dataInput.readFloat(4);
			assertTrue("the semi-precision float read must be NaN", Float.isNaN(f));

			// Zero
			assertEquals(0.0, dataInput.readFloat(4), 0.00001);

			// Positive Infinitive
			assertEquals(Float.POSITIVE_INFINITY, dataInput.readFloat(4), 0f);

			// Negative Infinitive
			assertEquals(Float.NEGATIVE_INFINITY, dataInput.readFloat(4), 0f);

			// denormalized value
			assertEquals(1.0010069E-38f, dataInput.readFloat(4), 0f);

			// normalized value 1
			assertEquals(-252.5f, dataInput.readFloat(4), 0f);

			// normalized value 2
			assertEquals(118.5f, dataInput.readFloat(4), 0f);
		} catch (Exception e) {
			fail("got an unexpected exception while reading from data input");
		}

		/*
		 * Test ########## readDouble()
		 */

		log(TAG, "test ZigBeeDataInput.readDouble()");

		try {
			// NaN
			double d = dataInput.readDouble();
			assertTrue("the semi-precision float read must be NaN", Double.isNaN(d));

			// Zero
			assertEquals(0.0, dataInput.readDouble(), 0d);

			// Positive Infinitive
			assertEquals(Double.POSITIVE_INFINITY, dataInput.readDouble(), 0d);

			// Negative Infinitive
			assertEquals(Double.NEGATIVE_INFINITY, dataInput.readDouble(), 0d);

			// denormalized value
			assertEquals(1.1125369292536007E-308, dataInput.readDouble(), 0d);

			// normalized value 1
			assertEquals(-252.5d, dataInput.readDouble(), 0d);

			// normalized value 2
			assertEquals(118.5d, dataInput.readDouble(), 0d);

		} catch (Exception e) {
			fail("got an unexpected exception while reading from data input");
		}

		log(TAG, "test ZigBeeDataInput.readByte()");

		try {
			dataInput.readByte();
			fail("we expected EOFException");
		} catch (EOFException e) {
			// We expect the end of the buffer
		} catch (Exception e) {
			fail("got unexpected exception, instead of EOFException: " + e.getMessage());
		}
	}

	/**
	 * Tests marshaling on on the ZCLHeader implementation
	 */

	public void testDataOutput() {

		short commandId = 0x10;
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
		 * Creates an empty frame with a specific maximum payload size.
		 */
		ZCLFrameImpl frame = new ZCLFrameImpl(headerInit, payloadTestBasic.length + 20);

		/*
		 * The getDataOutput() method is not present in the ZCLFrame interface,
		 * but only in the implementation class
		 */
		ZigBeeDataOutput dataOutput = frame.getDataOutput();

		/*
		 * Checks on IllegalArgumentException on wrong size parameter
		 */

		log(TAG, "test ZigBeeDataInput.writeInt()");

		try {
			dataOutput.writeInt(0, 0);
			fail("we expected an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// We expect this exception
		} catch (Throwable e) {
			fail("got an unexpected exception while reading float, we expected an IllegalArgumentException");
		}

		try {
			dataOutput.writeInt(0, 5);
			fail("we expected an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// We expect this exception
		} catch (Throwable e) {
			fail("got an unexpected exception in the writeInt(), we expected an IllegalArgumentException");
		}

		log(TAG, "test ZigBeeDataInput.writeLong()");

		try {
			dataOutput.writeLong(0, 0);
			fail("we expected an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// We expect this exception
		} catch (Throwable e) {
			fail("got an unexpected exception in the writeLong(), we expected an IllegalArgumentException");
		}

		try {
			dataOutput.writeLong(0, 9);
			fail("we expected an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// We expect this exception
		} catch (Throwable e) {
			fail("got an unexpected exception in the writeLong(), we expected an IllegalArgumentException");
		}

		/*
		 * Test writeByte() methods of ZigBeeDataOutput interface
		 */

		log(TAG, "test ZigBeeDataInput.writeByte()");

		try {
			dataOutput.writeByte(payloadTestBasic[0]);
		} catch (Throwable e) {
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

		/*
		 * Test writeBytes() method of ZigBeeDataOutput interface
		 */

		log(TAG, "test ZigBeeDataInput.writeBytes()");

		try {
			dataOutput.writeBytes(bytes1, bytes1.length);
		} catch (Throwable e) {
			fail("got an unexpected exception while reading from data input: " + e.getMessage());
		}

		/*
		 * Checks if the data has been marshaled correctly
		 */

		byte[] rawFrame = frame.getBytes();

		boolean equals = true;

		int offs = getMinFrameSize(frame.getHeader());

		int i;
		for (i = 0; i < rawFrame.length - offs; i++) {
			if (rawFrame[i + offs] != payloadTestBasic[i]) {
				equals = false;
				break;
			}
		}

		if (!equals) {
			fail("ZCLFrame.getBytes(): marshaled data is different from what it is expected, at index " + i);
		} else {
			assertEquals("ZCLFrame.getBytes() size", payloadTestBasic.length + offs, rawFrame.length);
		}

		frame = new ZCLFrameImpl(headerInit, payloadTestFloat.length + 20);

		dataOutput = frame.getDataOutput();

		/*
		 * Checks on IllegalArgumentException on wrong value of the size
		 * parameter for writeFloat() method of the ZigBeeDataOutput interface.
		 */

		try {
			dataOutput.writeFloat(0f, 1);
			fail("we expected an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// We expect this exception
		} catch (Throwable e) {
			fail("got an unexpected exception while writing a float of invalid size, we expected an IllegalArgumentException");
		}

		try {
			dataOutput.writeFloat(0f, 3);
			fail("we expected an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// We expect this exception
		} catch (Throwable e) {
			fail("got an unexpected exception while writing a float of invalid size, we expected an IllegalArgumentException");
		}

		try {
			dataOutput.writeFloat(0f, 5);
			fail("we expected an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// We expect this exception
		} catch (Throwable e) {
			fail("got an unexpected exception while writing a float of invalid size, we expected an IllegalArgumentException");
		}

		/*
		 * Test writeFloat() method of ZigBeeDataOutput interface for size 2
		 * (semi-float precision)
		 */

		try {
			dataOutput.writeFloat(Float.NaN, 2);
			dataOutput.writeFloat(0f, 2);
			dataOutput.writeFloat(Float.POSITIVE_INFINITY, 2);
			dataOutput.writeFloat(Float.NEGATIVE_INFINITY, 2);

			/*
			 * too big to fit in a semi-precision float. The actual value that
			 * must be written is {@link Float.POSITIVE_INFINITY}
			 */
			dataOutput.writeFloat(Float.MAX_VALUE, 2);
			dataOutput.writeFloat(-252.5f, 2);
			dataOutput.writeFloat(+118.5f, 2);
		} catch (Throwable e) {
			fail("got an unexpected exception while reading from data input: " + e.getMessage());
		}

		/*
		 * Test writeFloat() method of ZigBeeDataOutput interface for size 4
		 * (i.e. stardard precision float)
		 */

		try {
			dataOutput.writeFloat(Float.NaN, 4);
			dataOutput.writeFloat(0f, 4);
			dataOutput.writeFloat(Float.POSITIVE_INFINITY, 4);
			dataOutput.writeFloat(Float.NEGATIVE_INFINITY, 4);
			dataOutput.writeFloat(-252.5f, 4);
			dataOutput.writeFloat(+118.5f, 4);
		} catch (Throwable e) {
			fail("got an unexpected exception while reading from data input: " + e.getMessage());
		}

		/*
		 * Test writeDouble() method of ZigBeeDataOutput interface
		 */

		try {
			dataOutput.writeDouble(Double.NaN);
			dataOutput.writeDouble(0d);
			dataOutput.writeDouble(Double.POSITIVE_INFINITY);
			dataOutput.writeDouble(Double.NEGATIVE_INFINITY);
			dataOutput.writeDouble(-252.5d);
			dataOutput.writeDouble(+118.5d);
		} catch (Throwable e) {
			fail("got an unexpected exception while reading from data input: " + e.getMessage());
		}

		/*
		 * opens a data input stream that should point to the first byte of the
		 * frame paylaod. Here what it is tested is the correctness of the
		 * writeFloat() and writeDouble() methods, because the readFloat() and
		 * readDouble() methods are tested in the testDataInput() testcase.
		 */
		ZigBeeDataInput dataInput = frame.getDataInput();

		/*
		 * Try to read what has been written in the DataOutput stream in the
		 * lines above.
		 */

		try {

			/*
			 * read the values that we have written above, and we check that the
			 * actual returned value is the expected one
			 */

			/*
			 * Read back the semi-float numbers and check if are those that are
			 * expected to be.
			 */

			assertTrue("expected NaN", Float.isNaN(dataInput.readFloat(2)));
			assertEquals(0f, dataInput.readFloat(2), 0f);
			assertEquals("expected positive infinity", Float.POSITIVE_INFINITY, dataInput.readFloat(2), 0f);
			assertEquals("expected negative infinity", Float.NEGATIVE_INFINITY, dataInput.readFloat(2), 0f);
			assertEquals("expected positive infinity", Float.POSITIVE_INFINITY, dataInput.readFloat(2), 0f);
			assertEquals("wrong value read", -252.5f, dataInput.readFloat(2), 0f);
			assertEquals("wrong value read", +118.5f, dataInput.readFloat(2), 0f);

			/*
			 * Read back the float numbers and check if are those that are
			 * expected to be.
			 */
			assertTrue("expected NaN", Float.isNaN(dataInput.readFloat(4)));
			assertEquals(0f, dataInput.readFloat(4), 0f);
			assertEquals("expected positive infinity", Float.POSITIVE_INFINITY, dataInput.readFloat(4), 0f);
			assertEquals("expected negative infinity", Float.NEGATIVE_INFINITY, dataInput.readFloat(4), 0f);
			assertEquals("wrong value read", -252.5f, dataInput.readFloat(4), 0f);
			assertEquals("wrong value read", +118.5f, dataInput.readFloat(4), 0f);

			/*
			 * Read back the double numbers and check if are those that are
			 * expected to be.
			 */
			assertTrue("expected NaN", Double.isNaN(dataInput.readDouble()));
			assertEquals(0d, dataInput.readDouble(), 0d);
			assertEquals("expected positive infinity", Double.POSITIVE_INFINITY, dataInput.readDouble(), 0d);
			assertEquals("expected negative infinity", Double.NEGATIVE_INFINITY, dataInput.readDouble(), 0d);
			assertEquals("wrong value read", -252.5d, dataInput.readDouble(), 0d);
			assertEquals("wrong value read", +118.5d, dataInput.readDouble(), 0d);

		} catch (Throwable e) {
			fail("got an unexpected exception while reading from data input: " + e.getMessage());
		}

		/*
		 * If we read any additional byte we should get an EOF exception.
		 */

		try {
			dataInput.readByte();
			fail("expected an EOFException but no exception was thrown");
		} catch (EOFException e) {
			// we expect this exception!
		} catch (Throwable e) {
			fail("expected an EOFException, got: " + e.getMessage());
		}
	}

	protected int getMinFrameSize(ZCLHeader header) {
		return header.isManufacturerSpecific() ? (maxHeaderSize) : minHeaderSize;
	}
}

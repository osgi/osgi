/*
 * Copyright (c) OSGi Alliance (2013, 2020). All Rights Reserved.
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

package org.osgi.impl.service.enocean.basedriver.impl;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.enocean.basedriver.esp.EspPacket;
import org.osgi.impl.service.enocean.utils.Logger;
import org.osgi.impl.service.enocean.utils.Utils;
import org.osgi.impl.service.enocean.utils.teststep.TestStepForEnOceanImpl;
import org.osgi.service.enocean.EnOceanChannel;
import org.osgi.service.enocean.descriptions.EnOceanChannelDescription;
import org.osgi.service.enocean.descriptions.EnOceanChannelDescriptionSet;
import org.osgi.service.enocean.descriptions.EnOceanDataChannelDescription;
import org.osgi.service.enocean.descriptions.EnOceanMessageDescription;
import org.osgi.service.enocean.descriptions.EnOceanMessageDescriptionSet;
import org.osgi.test.support.step.TestStep;

/**
 * EnOcean host implementation fir test purpose.
 */
public class EnOceanHostTestImpl extends EnOceanHostImpl {

    /**
     * EnOcean base driver impl's tag/prefix for logger.
     */
	@SuppressWarnings("hiding")
	private static final String				TAG						= "EnOceanHostTestImpl";

    private TestStepForEnOceanImpl testStepForEnOceanImpl = new TestStepForEnOceanImpl();
	private ServiceRegistration<TestStep>	testStepSR;

    /**
     * @param path
     * @param bc
     */
    public EnOceanHostTestImpl(String path, BundleContext bc) {
	super(path, bc);
	Logger.d(TAG, "Create, and register EnOcean's Test Step OSGi service.");
	this.testStepSR = bc.registerService(TestStep.class,
		testStepForEnOceanImpl, null);
	Logger.d(TAG,
		"EnOcean's Test Step OSGi service has been created, and registered.");
    }

	@Override
	public void startup() {
	this.isRunning = true;
	this.start();
    }

    @Override
	public void run() {
	while (this.isRunning) {
	    try {
		Thread.sleep(400);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	    try {
		byte[] command = testStepForEnOceanImpl.getCurrentCommandAndReplaceItByNull();
		if (command == null) {
		    // Logger.d(TAG, "null command");
		    continue;
		}

		if (command[0] == EspPacket.SYNC_BYTE) {
		    Logger.d(TAG, "data[0] == EspPacket.SYNC_BYTE");
		    Logger.d(TAG, "read bytes: " + Utils.bytesToHexString(command));
		    EspPacket packet = readPacket(command);
		    if (packet.getPacketType() == EspPacket.TYPE_RADIO) {
			Logger.d(TAG, "packet.getPacketType() == EspPacket.TYPE_RADIO");
			processReceivedMessage(packet.getFullData());
		    }
		} else {
		    String commandStr = new String(command);
		    Logger.d(TAG, "command: " + commandStr);
		    if (commandStr.equals(TestStepForEnOceanImpl.MDSET_WITH_MD)) {
			EnOceanMessageDescriptionSet mdSet = createEnOceanMessageDescriptionSet();
			bc.registerService(EnOceanMessageDescriptionSet.class.getName(),
				mdSet, null);

		    } else if (commandStr.equals(TestStepForEnOceanImpl.CDSET_WITH_CD)) {
			EnOceanChannelDescriptionSet cdSet = createEnOceanChannelDescriptionSet();
			bc.registerService(EnOceanChannelDescriptionSet.class.getName(),
				cdSet, null);

		    } else {
			Logger.d(TAG, "The given command: " + commandStr + " is UNKNOWN.");
		    }
		}
	    } catch (IOException e) {
		Logger.e(TAG, "Error while reading input packet: " + e.getMessage());
		e.printStackTrace();
	    } catch (Exception e) {
		Logger.e(TAG, "Error while processing command: " + e.getMessage());
		e.printStackTrace();
	    }
	}
	Logger.d(TAG, "Unregister EnOcean's Test Step OSGi service.");
	this.testStepSR.unregister();
    }

    /**
     * 
     */
    public void close() {
	this.isRunning = false;
    }

    @Override
	public void send(byte[] data) {
	testStepForEnOceanImpl.pushDataInTestStep(data);
    }

    /**
     * Low-level ESP3 reader implementation. Reads the header, deducts the
     * paylsoad size, checks for errors, and sends back the read packet to the
     * caller.
     * 
     * @return the complete byte[] ESP packet
     * @throws IOException
     */
    private EspPacket readPacket(byte[] data) throws IOException {
	Logger.d(TAG, "data: " + data);
	Logger.d(TAG, "data.length: " + data.length);
	// I don't understand why, but the first byte must be ignored... So
	// int j = 1; instead of int j = 0;
	int j = 1;
	byte[] header = new byte[4];
	for (int i = 0; i < 4; i++) {
	    header[i] = data[j];
	    j = j + 1;
	}
	Logger.d(TAG, "read header: " + Utils.bytesToHexString(header));
	// Check the CRC
	int headerCrc = data[j];
	j = j + 1;
	if (headerCrc == -1) {
	    throw new IOException("could not read entire packet");
	}
	if ((byte) headerCrc != Utils.crc8(header)) {
	    Logger.d(TAG, "header_crc = 0x" + Utils.bytesToHexString(new byte[] { (byte) headerCrc }));
	    Logger.d(TAG, "h_comp_crc = 0x"
		    + Utils.bytesToHexString(new byte[] { Utils.crc8(header) }));
	    throw new IOException("header was malformed or corrupt");
	}

	// Read the payload using header info
	int payloadLength = ((header[0] << 8) | header[1]) + header[2];
	byte[] payload = new byte[payloadLength];
	for (int i = 0; i < payloadLength; i++) {
	    payload[i] = data[j];
	    j = j + 1;
	}
	Logger.d(TAG, "read payload: " + Utils.bytesToHexString(payload));
	// Check payload CRC
	int payloadCrc = data[j];
	if (payloadCrc == -1) {
	    throw new IOException("could not read entire packet");
	}
	if ((byte) payloadCrc != Utils.crc8(payload)) {
	    Logger.d(TAG, "orig_crc     = 0x"
		    + Utils.bytesToHexString(new byte[] { (byte) payloadCrc }));
	    Logger.d(TAG, "computed_crc = 0x"
		    + Utils.bytesToHexString(new byte[] { Utils.crc8(payload) }));
	    throw new IOException("payload was malformed or corrupt");
	}
	payload = Utils.byteConcat(payload, (byte) payloadCrc);
	// Add the sync byte to the header
	header = Utils.byteConcat(EspPacket.SYNC_BYTE, header);
	header = Utils.byteConcat(header, (byte) headerCrc);
	Logger.d(TAG, "Received EnOcean packet. Frame data: "
		+ Utils.bytesToHexString(Utils.byteConcat(header, payload)));
	return new EspPacket(header, payload);
    }

    private EnOceanMessageDescriptionSet createEnOceanMessageDescriptionSet() {
	return new EnOceanMessageDescriptionSet() {
	    @Override
		public EnOceanMessageDescription getMessageDescription(int rorg,
		    int func, int type, int extra) throws IllegalArgumentException {
		return new EnOceanMessageDescription() {
		    EnOceanChannel floatValue = new TemperatureChannel_00();
		    EnOceanChannel learn = new LearnChannel_4BS();

		    @Override
			public EnOceanChannel[] deserialize(byte[] data) throws IllegalArgumentException {

			/*
			 * Every message description should ensure this
			 */
			if (data == null) {
			    throw new IllegalArgumentException("Input data was NULL");
			}
			if (data.length != 4) {
			    throw new IllegalArgumentException("Input data size was wrong");
			}
			byte lrnByte = (byte) ((data[3] >> 3) & 0x01);
			floatValue.setRawValue(Utils.byteToBytes(data[2]));
			learn.setRawValue(new byte[] { lrnByte });

			return new EnOceanChannel[] { floatValue, learn };
		    }

		    @Override
			public byte[] serialize(EnOceanChannel[] channels)
			    throws IllegalArgumentException {
			return null;
		    }

		    class TemperatureChannel_00 implements EnOceanChannel {

			private byte b0;

			@Override
			public String getChannelId() {
			    return "CID";
			}

			@Override
			public void setRawValue(byte[] rawValue) {
			    b0 = rawValue[0];
			}

			@Override
			public int getSize() {
			    return 8;
			}

			@Override
			public byte[] getRawValue() {
			    return Utils.byteToBytes(b0);
			}

			@Override
			public int getOffset() {
			    return 16;
			}

		    }

		    class LearnChannel_4BS implements EnOceanChannel {

			private boolean isLearn;

			@Override
			public String getChannelId() {
			    return "LRN_4BS";
			}

			@Override
			public void setRawValue(byte[] rawValue) {
			    isLearn = rawValue[0] == 0;
			}

			@Override
			public int getSize() {
			    return 1;
			}

			@Override
			public byte[] getRawValue() {
			    return isLearn ? new byte[] { 0x0 }
			    : new byte[] { 0x1 };
			}

			@Override
			public int getOffset() {
			    return 28;
			}

		    }

		    @Override
			public String getMessageDescription() {
			return "A description";
		    }

		};
	    }
	};
    }

    private EnOceanChannelDescriptionSet createEnOceanChannelDescriptionSet() {
	return new EnOceanChannelDescriptionSet() {
		private Map<String,EnOceanDataChannelDescription> channelTable = null;

	    @Override
		public EnOceanChannelDescription getChannelDescription(String channelId) 
		    throws IllegalArgumentException {
		if (channelTable == null) {
				channelTable = new Hashtable<>();
		    channelTable.put("CID",
			    new EnOceanDataChannelDescription() {

			@Override
			public String getType() {
			    return EnOceanChannelDescription.TYPE_DATA;
			}

			private float scale(int x) {
			    // y = a*x + b
			    // where...
			    float denominator = (getDomainStop() - getDomainStart());
			    float numerator_a = (float) (getRangeStop() - getRangeStart());
			    float numerator_b = (float) (getRangeStart() * getDomainStop() 
				    - getRangeStop() * getDomainStart());
			    float a = numerator_a / denominator;
			    float b = numerator_b / denominator;
			    return a * x + b;
			}

			private int unscale(float y) {
			    // x = A*y + B where A = 1/a and B = -b/a,
			    // so...
			    float denominator = (float) (getRangeStop() - getRangeStart());
			    float numerator_A = getDomainStop() - getDomainStart();
			    float numerator_B = (float) (getRangeStop() * getDomainStart() 
				    - getRangeStart() * getDomainStop());
			    float A = numerator_A / denominator;
			    float B = numerator_B / denominator;
			    return Math.round(A * y + B);
			}

			@Override
			public byte[] serialize(Object obj) throws IllegalArgumentException {
			    float value;
			    if (obj == null) {
				throw new IllegalArgumentException("Supplied object was NULL");
			    }
			    try {
				Float valueObj = (Float) obj;
				value = valueObj.floatValue();
			    } catch (ClassCastException e) {
				throw new IllegalArgumentException("Invalid input in channel description");
			    }
			    if (value < getRangeStart() || value > getRangeStop()) {
				throw new IllegalArgumentException("Supplied value out of range");
			    }
			    int input = unscale(value);
			    return new byte[] { (byte) input };
			}

			@Override
			public Object deserialize(byte[] bytes) throws IllegalArgumentException {
			    if (bytes == null) {
				throw new IllegalArgumentException("Supplied array was NULL");
			    }
			    if (bytes.length != 1)
				throw new IllegalArgumentException("Input was invalid, too many bytes");
			    byte b = bytes[0];
			    int input = b;
			    if (input < getDomainStart() && input > getDomainStop()) {
				throw new IllegalArgumentException("Supplied value out of input domain");
			    }
			    Float output = Float.valueOf(scale(input));
			    return output;
			}

			@Override
			public int getDomainStart() {
			    return 0;
			}

			@Override
			public int getDomainStop() {
			    return 255;
			}

			@Override
			public double getRangeStart() {
			    return -40.0f;
			}

			@Override
			public double getRangeStop() {
			    return 0.0f;
			}

			@Override
			public String getUnit() {
			    return "°C";
			}
		    });
		}
		if (channelId == null) {
		    throw new IllegalArgumentException("Input ID was NULL");
		}
		try {
		    return channelTable.get(channelId);
		} catch (Exception e) {
		    throw new IllegalArgumentException("There was an error reading the messageSet : "
			    + e.getMessage());
		}
	    }
	};
    }

}

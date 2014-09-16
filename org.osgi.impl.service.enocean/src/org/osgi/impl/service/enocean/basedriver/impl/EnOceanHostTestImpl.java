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

package org.osgi.impl.service.enocean.basedriver.impl;

import java.io.IOException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.enocean.basedriver.esp.EspPacket;
import org.osgi.impl.service.enocean.utils.EnOceanHostImplException;
import org.osgi.impl.service.enocean.utils.Logger;
import org.osgi.impl.service.enocean.utils.Utils;
import org.osgi.impl.service.enocean.utils.teststep.TestStepForEnOceanImpl;
import org.osgi.service.enocean.EnOceanChannel;
import org.osgi.service.enocean.EnOceanException;
import org.osgi.service.enocean.descriptions.EnOceanMessageDescription;
import org.osgi.service.enocean.descriptions.EnOceanMessageDescriptionSet;
import org.osgi.test.cases.enoceansimulation.teststep.TestStep;

/**
 *
 */
public class EnOceanHostTestImpl extends EnOceanHostImpl {

	/**
	 * EnOcean base driver impl's tag/prefix for logger.
	 */
	protected static final String	TAG						= "EnOceanHostTestImpl";

	private TestStepForEnOceanImpl	testStepForEnOceanImpl	= new TestStepForEnOceanImpl();
	private ServiceRegistration		testStepSR;

	/**
	 * @param path
	 * @param bc
	 */
	public EnOceanHostTestImpl(String path, BundleContext bc) {
		super(path, bc);

		Logger.d(this.getClass().getName(), "Create, and register EnOcean's Test Step OSGi service.");
		this.testStepSR = bc.registerService(
				TestStep.class.getName(), testStepForEnOceanImpl, null);
		Logger.d(this.getClass().getName(), "EnOcean's Test Step OSGi service has been created, and registered.");
	}

	public void startup() throws EnOceanHostImplException {
		this.isRunning = true;
		this.start();
	}

	public void run() {
		while (this.isRunning) {
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Logger.d(TAG,
					"EnOceanHostTestImpl.run() - periodically check testStepService.getCurrentCommandAndReplaceItByNull()");
			try {
				byte[] command =
						testStepForEnOceanImpl.getCurrentCommandAndReplaceItByNull();
				Logger.d(TAG, "command: " + command);
				if (command == null) {
					Logger.d(TAG, "command == null");
				} else {
					byte[] data = command;
					Logger.d(TAG, "data: "+data);
					if (data[0] == ENOCEAN_ESP_FRAME_START) {
						Logger.d(TAG, "data[0] == ENOCEAN_ESP_FRAME_START");
						Logger.d(TAG, "read bytes: " + Utils.bytesToHexString(data));
						if (data[0] == ENOCEAN_ESP_FRAME_START) {
							Logger.d(TAG, "data[0] == ENOCEAN_ESP_FRAME_START");
							EspPacket packet = readPacket(data);
							if (packet.getPacketType() == EspPacket.TYPE_RADIO) {
								Logger.d(TAG,
										"packet.getPacketType() == EspPacket.TYPE_RADIO");
								dispatchToListeners(packet.getFullData());
							}
						} else {
							Logger.d(TAG, "The given data: " + data + " is UNKNOWN.");
						}
					} else {
						if ("EnOceanMessageDescriptionSet_with_an_EnOceanMessageDescription_A5_02_01".equals(new String(data))) {
							EnOceanMessageDescriptionSet enOceanMessageDescriptionSet = new EnOceanMessageDescriptionSet() {
								public EnOceanMessageDescription getMessageDescription(int rorg, int func, int type, int extra) throws IllegalArgumentException {
									return new EnOceanMessageDescription() {

										/**
										 * @return hardcoded 0xA5.
										 */
										public int getRorg() {
											return 0xA5;
										}

										/**
										 * @return hardcoded 0x02.
										 */
										public int getFunc() {
											return 0x02;
										}

										/**
										 * @return hardcoded 0x01.
										 */
										public int getType() {
											return 0x01;
										}

										EnOceanChannel	temperature	= new TemperatureChannel_00();
										EnOceanChannel	learn		= new LearnChannel_4BS();

										public EnOceanChannel[] deserialize(byte[] data) throws EnOceanException {

											/*
											 * Every message description should
											 * ensure this
											 */
											if (data == null) {
												throw new EnOceanException("Input data was NULL");
											}
											if (data.length != 4) {
												throw new EnOceanException("Input data size was wrong");
											}
											byte lrnByte = (byte) ((data[3] >> 3) & 0x01);
											temperature.setRawValue(Utils.byteToBytes(data[2]));
											learn.setRawValue(new byte[] {lrnByte});

											return new EnOceanChannel[] {temperature, learn};
										}

										public byte[] serialize(EnOceanChannel[] channels) throws EnOceanException {
											// TODO Auto-generated method stub
											return null;
										}

										class TemperatureChannel_00 implements EnOceanChannel {

											private byte	b0;

											public String getChannelId() {
												return "TMP_00";
											}

											public void setRawValue(byte[] rawValue) {
												b0 = rawValue[0];
											}

											public int getSize() {
												return 8;
											}

											public byte[] getRawValue() {
												return Utils.byteToBytes(b0);
											}

											public int getOffset() {
												return 16;
											}

										}

										class LearnChannel_4BS implements EnOceanChannel {

											private boolean	isLearn;

											public String getChannelId() {
												return "LRN_4BS";
											}

											public void setRawValue(byte[] rawValue) {
												isLearn = rawValue[0] == 0;
											}

											public int getSize() {
												return 1;
											}

											public byte[] getRawValue() {
												if (isLearn) {
													return new byte[] {0x0};
												} else {
													return new byte[] {0x1};
												}
											}

											public int getOffset() {
												return 28;
											}

										}

									};
								}
							};
							bc.registerService(EnOceanMessageDescriptionSet.class.getName(), enOceanMessageDescriptionSet, null);
						} else {
							Logger.d(TAG, "The given data: " + data + " is UNKNOWN.");
						}
					}
				}
			} catch (IOException ioexception) {
				Logger.e(TAG, "Error while reading input packet: " + ioexception.getMessage());
			}
		}
		Logger.d(this.getClass().getName(), "Unregister EnOcean's Test Step OSGi service.");
		this.testStepSR.unregister();
		Logger.d(this.getClass().getName(), "EnOcean's Test Step OSGi service has been unregistered.");
	}

	/**
	 * 
	 */
	public void close() {
		this.isRunning = false;
	}

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
		Logger.d(TAG, "header_crc = 0x" + Utils.bytesToHexString(new byte[] {(byte) headerCrc}));
		Logger.d(TAG, "h_comp_crc = 0x" + Utils.bytesToHexString(new byte[] {Utils.crc8(header)}));
		if ((byte) headerCrc != Utils.crc8(header)) {
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
		Logger.d(TAG, "orig_crc     = 0x" + Utils.bytesToHexString(new byte[] {(byte) payloadCrc}));
		Logger.d(TAG, "computed_crc = 0x" + Utils.bytesToHexString(new byte[] {Utils.crc8(payload)}));
		if ((byte) payloadCrc != Utils.crc8(payload)) {
			throw new IOException("payload was malformed or corrupt");
		}
		payload = Utils.byteConcat(payload, (byte) payloadCrc);
		// Add the sync byte to the header
		header = Utils.byteConcat(EspPacket.SYNC_BYTE, header);
		header = Utils.byteConcat(header, (byte) headerCrc);
		Logger.d(TAG, "Received EnOcean packet. Frame data: " + Utils.bytesToHexString(Utils.byteConcat(header, payload)));
		return new EspPacket(header, payload);
	}

}

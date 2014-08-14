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
import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.enocean.basedriver.esp.EspPacket;
import org.osgi.impl.service.enocean.utils.EnOceanHostImplException;
import org.osgi.impl.service.enocean.utils.Logger;
import org.osgi.impl.service.enocean.utils.Utils;
import org.osgi.test.cases.enoceansimulation.EnOceanInOut;
import org.osgi.test.cases.enoceansimulation.teststep.TestStep;
import org.osgi.test.cases.enoceansimulation.teststep.impl.TestStepForEnOceanImpl;

/**
 *
 */
public class EnOceanHostTestImpl extends EnOceanHostImpl {

	/**
	 * EnOcean base driver impl's tag/prefix for logger.
	 */
	protected static final String	TAG	= "EnOceanHostTestImpl";

	private EnOceanInOut			enOceanInOut;
	// private CustomInputStream duplicatedInputStream;
	private TestStepForEnOceanImpl				testStepService;

	/**
	 * @param path
	 * @param bc
	 */
	public EnOceanHostTestImpl(String path, BundleContext bc) {
		super(path, bc);
		// Create, and register the EnOceanInOut service.
		try {
			enOceanInOut = new EnOceanInOutImpl();
			bc.registerService(EnOceanInOut.class.getName(), enOceanInOut, null);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.e(TAG, "exception when registering enOceanInOut. e.getMessage(): " + e.getMessage());
		}

		// Get TestStepService service.
		ServiceReference testStepServiceRef = bc.getServiceReference(TestStep.class.getName());
		if (testStepServiceRef == null) {
			String errorMessage = "EnOceanHostTestImpl can NOT get at least one ServiceReference object for a service that implements and was registered under the " + TestStep.class.getName()
					+ " class.";
			Logger.d(this.getClass().getName(), errorMessage);
			throw new IllegalStateException(errorMessage);
		} else {
			testStepService = (TestStepForEnOceanImpl) bc.getService(testStepServiceRef);
		}

	}

	public void startup() throws EnOceanHostImplException {
		this.isRunning = true;
		// this.duplicatedInputStream = (CustomInputStream)
		// enOceanInOut.getInputStream();
		this.start();
	}

	public void run() {
		while (this.isRunning) {
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// Logger.d(TAG,
			// "EnOceanHostTestImpl.run() - periodically read in enOceanInOut.getOutputStream()");
			try {

				TestStepForEnOceanImpl testStepForEnOceanImpl =
						testStepService;
				byte[] command =
						testStepForEnOceanImpl.getCurrentCommandAndReplaceItByNull();
				// Logger.d(TAG, "command: " + command);
				if (command == null) {
					// Logger.d(TAG, "command == null");
				} else {
					byte[] data = command;
					if (data[0] != ENOCEAN_ESP_FRAME_START) {
						Logger.d(TAG, "data[0] != ENOCEAN_ESP_FRAME_START");
					} else {
						// duplicatedInputStream.replace(data);
						// duplicatedInputStream.read();
						// byteOutputStream.reset();
						Logger.d(TAG, "read bytes: " + Utils.bytesToHexString(data));
						if (data[0] == ENOCEAN_ESP_FRAME_START) {
							Logger.d(TAG, "data[0] == ENOCEAN_ESP_FRAME_START");
							// InputStream is =
							// this.enOceanInOut.getInputStream()
							EspPacket packet = readPacket(data);
							if (packet.getPacketType() == EspPacket.TYPE_RADIO) {
								Logger.d(TAG,
										"packet.getPacketType() == EspPacket.TYPE_RADIO");
								dispatchToListeners(packet.getFullData());
							}
						}
					}
				}

				// // Move the following from the use of EnOceanInOut to
				// TestStep.
				// ByteArrayOutputStream byteOutputStream =
				// (ByteArrayOutputStream) enOceanInOut.getOutputStream();
				// if (byteOutputStream.size() == 0) {
				// continue;
				// }
				// byte[] data = byteOutputStream.toByteArray();
				// if (data[0] != ENOCEAN_ESP_FRAME_START) {
				// continue;
				// }
				// duplicatedInputStream.replace(data);
				// duplicatedInputStream.read();
				// byteOutputStream.reset();
				// Logger.d(TAG, "read bytes: " + Utils.bytesToHexString(data));
				// if (data[0] == ENOCEAN_ESP_FRAME_START) {
				// EspPacket packet = readPacket();
				// if (packet.getPacketType() == EspPacket.TYPE_RADIO) {
				// dispatchToListeners(packet.getFullData());
				// }
				// }
			} catch (IOException ioexception) {
				Logger.e(TAG, "Error while reading input packet: " + ioexception.getMessage());
			}
		}
	}

	/**
	 * 
	 */
	public void close() {
		this.isRunning = false;
		// if (this.enOceanInOut.getOutputStream() != null) {
		// try {
		// this.enOceanInOut.getOutputStream().close();
		// } catch (IOException ioexception) {
		// Logger.w(TAG, "Error while closing output stream.");
		// }
		// }
		// if (this.enOceanInOut.getInputStream() != null) {
		// try {
		// this.enOceanInOut.getInputStream().close();
		// } catch (IOException ioexception1) {
		// Logger.w(TAG, "Error while closing input stream.");
		// }
		// }
	}

	public void send(byte[] data) {
		// duplicatedInputStream.replace(data);
		testStepService.pushDataInTestStep(data);
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
		// I don't understand why, but the first byte must be ignored... So int
		// j = 1; instead of int j = 0;
		int j = 1;
		byte[] header = new byte[4];
		for (int i = 0; i < 4; i++) {
			// header[i] = (byte) this.enOceanInOut.getInputStream().read();
			header[i] = data[j];
			j = j + 1;
		}
		Logger.d(TAG, "read header: " + Utils.bytesToHexString(header));
		// Check the CRC
		// int headerCrc = this.enOceanInOut.getInputStream().read();
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
			// payload[i] = (byte) this.enOceanInOut.getInputStream().read();
			// Logger.d(TAG, "i: " + i + ", j: " + j);
			// Logger.d(TAG, "data[j]: " + data[j]);
			payload[i] = data[j];
			j = j + 1;
		}
		Logger.d(TAG, "read payload: " + Utils.bytesToHexString(payload));
		// Check payload CRC
		// int payloadCrc = this.enOceanInOut.getInputStream().read();
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

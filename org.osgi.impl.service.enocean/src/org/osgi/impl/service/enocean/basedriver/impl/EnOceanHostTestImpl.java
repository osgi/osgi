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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.osgi.framework.BundleContext;
import org.osgi.impl.service.enocean.basedriver.esp.EspPacket;
import org.osgi.impl.service.enocean.utils.EnOceanHostImplException;
import org.osgi.impl.service.enocean.utils.Logger;
import org.osgi.impl.service.enocean.utils.Utils;
import org.osgi.service.enocean.EnOceanSerialInOut;

public class EnOceanHostTestImpl extends EnOceanHostImpl implements EnOceanSerialInOut {

	private ByteArrayOutputStream	byteStream;
	private CustomInputStream		duplicatedStream;

	public EnOceanHostTestImpl(String path, BundleContext bc) {
		super(path, bc);
	}

	public void startup() throws EnOceanHostImplException {
		this.isRunning = true;
		this.inputStream = new CustomInputStream(new byte[] {});
		this.outputStream = new ByteArrayOutputStream();
		this.duplicatedStream = (CustomInputStream) inputStream;
		this.byteStream = (ByteArrayOutputStream) outputStream;
		this.start();
	}

	public void resetBuffers() {
		this.duplicatedStream.reset();
		this.byteStream.reset();
	}

	public void run() {
		while (this.isRunning) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				if (byteStream.size() == 0) {
					continue;
				}
				byte[] data = byteStream.toByteArray();
				if (data[0] != ENOCEAN_ESP_FRAME_START)
					continue;
				duplicatedStream.replace(data);
				duplicatedStream.read();
				byteStream.reset();
				Logger.d(TAG, "read bytes: " + Utils.bytesToHexString(data));
				if (data[0] == ENOCEAN_ESP_FRAME_START) {
					EspPacket packet = readPacket();
					if (packet.getPacketType() == EspPacket.TYPE_RADIO) {
						dispatchToListeners(packet.getFullData());
					}
				}
			} catch (IOException ioexception) {
				Logger.e(TAG, "Error while reading input packet: " + ioexception.getMessage());
			}
		}
	}

	public void close() {
		this.isRunning = false;
		if (this.outputStream != null)
			try {
				this.outputStream.close();
			} catch (IOException ioexception) {
				Logger.w(TAG, "Error while closing output stream.");
			}
		if (this.inputStream != null) {
			try {
				this.inputStream.close();
			} catch (IOException ioexception1) {
				Logger.w(TAG, "Error while closing input stream.");
			}
		}
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void send(byte[] data) {
		duplicatedStream.replace(data);
	}

	class CustomInputStream extends ByteArrayInputStream {

		public CustomInputStream(byte[] var0) {
			super(var0);
		}

		public void replace(byte[] data) {
			this.buf = (byte[]) data.clone();
			this.mark = 0;
			this.pos = 0;
			this.count = data.length;
		}

	}
}

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


package org.osgi.impl.service.enocean.basedriver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import org.osgi.impl.service.enocean.utils.Logger;
import org.osgi.service.enocean.EnOceanException;
import org.osgi.service.enocean.EnOceanHost;

public class EnOceanFileHost extends Thread implements EnOceanHost {

	private static final String	TAG						= "EnOceanFileHost";

	private static final byte	ENOCEAN_ESP_FRAME_START	= 0x55;
	private Object				synchronizer;
	private ArrayList			listeners;
	private InputStream			inputStream;
	private OutputStream		outputStream;
	private File				file;
	private boolean				isRunning;

	private String				streamPath;

	public EnOceanFileHost(String path) throws FileNotFoundException {
		this.streamPath = path;
		listeners = new ArrayList();
		isRunning = false;
		synchronizer = new Object();

		file = new File(path);
		inputStream = new FileInputStream(file);
		outputStream = new FileOutputStream(file);
	}

	public void reset() throws EnOceanException {
		// TODO Auto-generated method stub

	}

	public String appVersion() throws EnOceanException {
		// TODO Auto-generated method stub
		return null;
	}

	public String apiVersion() throws EnOceanException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getBaseID() throws EnOceanException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setBaseID(int baseID) throws EnOceanException {
		// TODO Auto-generated method stub

	}

	public void setRepeaterLevel(int level) throws EnOceanException {
		// TODO Auto-generated method stub

	}

	public int getRepeaterLevel() throws EnOceanException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getSenderId(String servicePID) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Implementation-specific method to add a remote packet listener.
	 * 
	 * @param packetListener an object implementing the
	 *        {@link EnOceanPacketListener} interface.
	 */
	public void addPacketListener(EnOceanPacketListener packetListener) {
		if (!listeners.contains(packetListener)) {
			listeners.add(packetListener);
		}
	}

	public void run() {
		isRunning = true;
		while (isRunning) {
			try {
				/*
				 * synchronized (this.synchronizer) { if
				 * (inputStream.available() == 0) { synchronizer.wait(); } }
				 */
				int _byte = inputStream.read();
				if (_byte == -1) {
					throw new IOException("end of stream reached ?!");
				}
				byte c = (byte) _byte;
				if (c == ENOCEAN_ESP_FRAME_START) {
					byte[] packet = readPacket();
					dispatchToListeners(packet);
				}
			} catch (IOException e) {
				Logger.e(TAG, "an exception occured while reading stream '" + streamPath + "' : " + e.getMessage());
			}
		}

	}

	private void dispatchToListeners(byte[] data) {
		for (int i = 0; i < listeners.size(); i++) {
			EnOceanPacketListener listener = (EnOceanPacketListener) listeners.get(i);
			listener.packetReceived(data);
		}
	}

	private byte[] readPacket() throws IOException {
		byte[] header = new byte[4];
		inputStream.read(header);
		return header;
	}
}

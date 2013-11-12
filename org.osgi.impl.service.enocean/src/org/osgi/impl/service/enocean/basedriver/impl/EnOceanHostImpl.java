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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownServiceException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Properties;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.enocean.basedriver.EnOceanBaseDriver;
import org.osgi.impl.service.enocean.basedriver.EnOceanPacketListener;
import org.osgi.impl.service.enocean.basedriver.esp.EspPacket;
import org.osgi.impl.service.enocean.utils.Logger;
import org.osgi.impl.service.enocean.utils.Utils;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.enocean.EnOceanException;
import org.osgi.service.enocean.EnOceanHost;
import org.osgi.service.event.EventAdmin;

public class EnOceanHostImpl extends Thread implements EnOceanHost {

	private static final String	TAG						= "EnOceanHostImpl";

	private static final byte	ENOCEAN_ESP_FRAME_START	= 0x55;

	private static final int	MAX_ALLOCATED_CHIP_ID	= 127;
	private Object				synchronizer;
	private ArrayList			listeners;
	private InputStream			inputStream;
	private OutputStream		outputStream;
	private File				file;
	private boolean				isRunning;
	private EventAdmin			eventAdmin;
	private int					chipId;
	private int					baseId;
	private Configuration		chipIdConfig;
	private Dictionary			allocatedChipIds;

	private String				streamPath;

	private BundleContext		bc;

	public EnOceanHostImpl(int chipId, int baseId, String path, BundleContext bc) throws IOException {
		this.bc = bc;
		this.streamPath = path;
		listeners = new ArrayList();
		isRunning = false;
		synchronizer = new Object();

		ServiceReference configRef = bc.getServiceReference(ConfigurationAdmin.class.getName());
		if (configRef != null) {
			ConfigurationAdmin confAdmin = (ConfigurationAdmin) bc.getService(configRef);
			chipIdConfig = confAdmin.getConfiguration(EnOceanBaseDriver.CONFIG_EXPORTED_PID_TABLE);
			allocatedChipIds = chipIdConfig.getProperties();
			if (allocatedChipIds == null) {
				allocatedChipIds = new Properties();
				chipIdConfig.update(allocatedChipIds);
			}
		} else {
			throw new UnknownServiceException("ConfigAdmin service was not found !");
		}

		/* Init the allocated CHIP ID */
		this.chipId = chipId;
		this.baseId = baseId;

		/* Get a global eventAdmin handle */
		ServiceReference ref = bc.getServiceReference(EventAdmin.class.getName());
		eventAdmin = (EventAdmin) bc.getService(ref);

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

	public int getChipId(String servicePID) {
		String str = (String) allocatedChipIds.get(servicePID);
		if (str == null) {
			return -1;
		}
		return Integer.parseInt(str);
	}

	public void generateChipID(String servicePID) throws ArrayIndexOutOfBoundsException, IOException {
		int chipId = getChipId(servicePID);
		if (chipId == -1) {
			// Allocate one
			if (allocatedChipIds.size() < MAX_ALLOCATED_CHIP_ID) {
				// FIXME this is quite basic and should be improved
				chipId = baseId + allocatedChipIds.size();
				allocatedChipIds.put(servicePID, String.valueOf(chipId));
				chipIdConfig.update(allocatedChipIds);
			} else {
				throw new ArrayIndexOutOfBoundsException("No more CHIP_ID can be allocated.");
			}
		}
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
					EspPacket packet = readPacket();
					if (packet.getPacketType() == EspPacket.TYPE_RADIO) {
						dispatchToListeners(packet.getFullData());
					}
				}
			} catch (IOException e) {
				Logger.e(TAG, "an exception occured while reading stream '" + streamPath + "' : " + e.getMessage());
			}
		}

	}

	private void dispatchToListeners(byte[] data) {
		for (int i = 0; i < listeners.size(); i++) {
			EnOceanPacketListener listener = (EnOceanPacketListener) listeners.get(i);
			listener.radioPacketReceived(data);
		}
	}

	/**
	 * Low-level ESP3 reader implementation. Reads the header, deducts the
	 * payload size, checks for errors, and sends back the read packet to the
	 * caller.
	 * 
	 * @return the complete byte[] ESP packet
	 * @throws IOException
	 */
	private EspPacket readPacket() throws IOException {
		byte[] header = new byte[4];
		inputStream.read(header);
		// Check the CRC
		int headerCrc = inputStream.read();
		if (headerCrc == -1) {
			throw new IOException("could not read entire packet");
		}
		if ((byte) headerCrc != Utils.crc8(header)) {
			throw new IOException("packet was malformed or corrupt");
		}
		// Read the payload using header info
		int payloadLength = ((header[0] << 8) | header[1]) + header[2];
		byte[] payload = new byte[payloadLength];
		inputStream.read(payload);
		// Check payload CRC
		int payloadCrc = inputStream.read();
		if (payloadCrc == -1) {
			throw new IOException("could not read entire packet");
		}
		if ((byte) payloadCrc != Utils.crc8(payload)) {
			throw new IOException("packet was malformed or corrupt");
		}
		payload = Utils.byteConcat(payload, (byte) payloadCrc);
		// Add the sync byte to the header
		header = Utils.byteConcat(EspPacket.SYNC_BYTE, header);
		header = Utils.byteConcat(header, (byte) headerCrc);
		return new EspPacket(header, payload);
	}

	public void send(byte[] data) {
		try {
			outputStream.write(data);
			outputStream.flush();
		} catch (IOException e) {
			Logger.e(TAG, "an exception occured while writing to stream '" + streamPath + "' : " + e.getMessage());
		}

	}
}

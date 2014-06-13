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
import org.osgi.impl.service.enocean.utils.EnOceanHostImplException;
import org.osgi.impl.service.enocean.utils.Logger;
import org.osgi.impl.service.enocean.utils.Utils;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.enocean.EnOceanException;
import org.osgi.service.enocean.EnOceanHost;

public class EnOceanHostImpl extends Thread implements EnOceanHost {

	protected static final String	TAG						= "EnOceanHostImpl";
	protected static final byte		ENOCEAN_ESP_FRAME_START	= 0x55;
	protected String				donglePath;
	protected InputStream			inputStream;
	protected OutputStream			outputStream;

	private static final int		MAX_ALLOCATED_CHIP_ID	= 127;
	private ArrayList				listeners;
	private int						baseId;

	protected boolean				isRunning;
	private ChipPIDMapping			chipIdPidMap;

	public EnOceanHostImpl(String path, BundleContext bc) {
		this.isRunning = false;
		this.listeners = new ArrayList();
		this.donglePath = path;
		try {
			this.chipIdPidMap = new ChipPIDMapping(bc);
		} catch (Exception e) {
			Logger.w(TAG, "could not allocate Config Admin chipId/servicePID mapping : " + e.getMessage());
		}
	}

	public void startup() throws EnOceanHostImplException {
	}

	public void allocChipID(String servicePID) throws ArrayIndexOutOfBoundsException, IOException {
		int chipId = getChipId(servicePID);
		if (chipId == -1) {
			int size = chipIdPidMap.size();
			if (size < MAX_ALLOCATED_CHIP_ID) {
				int newChipId = baseId + size; // FIXME: VERY basic method
				chipIdPidMap.put(servicePID, String.valueOf(newChipId));
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
		String chipId = chipIdPidMap.get(servicePID);
		if (chipId == null) {
			return -1;
		} else {
			return Integer.parseInt(chipIdPidMap.get(servicePID));
		}
	}

	protected void dispatchToListeners(byte[] data) {
		for (int i = 0; i < listeners.size(); i++) {
			EnOceanPacketListener listener = (EnOceanPacketListener) listeners.get(i);
			listener.radioPacketReceived(data);
		}
	}

	/**
	 * Low-level ESP3 reader implementation. Reads the header, deducts the
	 * paylsoad size, checks for errors, and sends back the read packet to the
	 * caller.
	 * 
	 * @return the complete byte[] ESP packet
	 * @throws IOException
	 */
	protected EspPacket readPacket() throws IOException {
		byte[] header = new byte[4];
		for (int i = 0; i < 4; i++) {
			header[i] = (byte) inputStream.read();
		}
		Logger.d(TAG, "read header: " + Utils.bytesToHexString(header));
		// Check the CRC
		int headerCrc = inputStream.read();
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
			payload[i] = (byte) inputStream.read();
		}
		Logger.d(TAG, "read payload: " + Utils.bytesToHexString(payload));
		// Check payload CRC
		int payloadCrc = inputStream.read();
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

	public void send(byte[] data) {
	}

	class ChipPIDMapping {

		private Configuration	config;
		private Dictionary		mappings;

		public ChipPIDMapping(BundleContext bc) throws UnknownServiceException, IOException {
			ServiceReference ref = bc.getServiceReference(ConfigurationAdmin.class.getName());
			if (ref != null) {
				ConfigurationAdmin configAdmin = (ConfigurationAdmin) bc.getService(ref);
				config = configAdmin.getConfiguration(EnOceanBaseDriver.CONFIG_EXPORTED_PID_TABLE);
				mappings = config.getProperties();
				if (mappings == null) {
					mappings = new Properties();
					config.update(mappings);
				}
			} else {
				throw new UnknownServiceException("ConfigAdmin service was not found !");
			}

		}

		public void put(String servicePID, String chipId) {
			mappings.put(servicePID, chipId);
			try {
				config.update(mappings);
			} catch (IOException e) {
				Logger.w(TAG, "chip ID '" + chipId + "' could not be saved in ConfigAdmin with service PID '" + servicePID + "'");
			}
		}

		public int size() {
			return mappings.size();
		}

		public String get(String servicePID) {
			return (String) mappings.get(servicePID);
		}
	}
}

/*
 * Copyright (c) OSGi Alliance (2013, 2015). All Rights Reserved.
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
import java.net.UnknownServiceException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.enocean.basedriver.EnOceanBaseDriver;
import org.osgi.impl.service.enocean.basedriver.EnOceanPacketListener;
import org.osgi.impl.service.enocean.utils.EnOceanHostImplException;
import org.osgi.impl.service.enocean.utils.Logger;
import org.osgi.impl.service.enocean.utils.Utils;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.enocean.EnOceanException;
import org.osgi.service.enocean.EnOceanHost;

/**
 * EnOcean host implementation.
 */
public class EnOceanHostImpl extends Thread implements EnOceanHost {

    /**
     * EnOcean base driver impl's tag/prefix for logger.
     */
    protected static final String TAG = "EnOceanHostImpl";
    private static final int MAX_ALLOCATED_CHIP_ID = 127;

    /**
     * EnOceanHostImpl's dongle path.
     */
    protected String donglePath;

    private ArrayList listeners;
    private int baseId;
    private int repeaterLevel;

    /** Is EnOceanHostImpl running? */
    protected boolean isRunning;
    private ChipPIDMapping chipIdPidMap;

    /**
     * BundleContext
     */
    protected BundleContext bc;

    /**
     * @param path
     * @param bc
     */
    public EnOceanHostImpl(String path, BundleContext bc) {
	this.bc = bc;
	this.isRunning = false;
	this.listeners = new ArrayList();
	this.donglePath = path;
	try {
	    this.chipIdPidMap = new ChipPIDMapping(this.bc);
	} catch (Exception e) {
	    Logger.w(TAG, "could not allocate Config Admin chipId/servicePID mapping : " + e.getMessage());
	}
    }

    /**
     * @throws EnOceanHostImplException
     */
    public void startup() throws EnOceanHostImplException {
	// TODO Auto-generated method stub
    }

    /**
     * @param servicePID
     * @throws ArrayIndexOutOfBoundsException
     * @throws IOException
     */
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
	return baseId;
    }

    public void setBaseID(int baseID) throws EnOceanException {
	this.baseId = baseID;
    }

    public void setRepeaterLevel(int level) throws EnOceanException {
	this.repeaterLevel = level;
    }

    public int getRepeaterLevel() throws EnOceanException {
	return repeaterLevel;
    }

    public int getChipId(String servicePID) {
	String chipId = chipIdPidMap.get(servicePID);
	return (chipId == null) ? -1 : Integer.parseInt(chipIdPidMap.get(servicePID));
    }

    /**
     * @param data
     */
    protected void processReceivedMessage(byte[] data) {
	Logger.d(TAG, "processMessage(" + Utils.bytesToHexString(data) + ")");
	for (int i = 0; i < listeners.size(); i++) {
	    EnOceanPacketListener listener = (EnOceanPacketListener) listeners.get(i);
	    listener.radioPacketReceived(data);
	}
    }

    /**
     * @param data
     */
    public void send(byte[] data) {
	// TODO Auto-generated method stub
    }


    class ChipPIDMapping {

	private Configuration config;
	private Dictionary mappings;

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
		Logger.w(TAG, "chip ID '" + chipId 
			+ "' could not be saved in ConfigAdmin with service PID '" + servicePID + "'");
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

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
package org.osgi.impl.service.upnp.cd.ssdp;

import java.util.Hashtable;

// This class contains upnp device info for sending NOTIFY ans M-SEARCH responses. 
public class DeviceDetails {
	private String		uuid;
	private String		devType;
	private String		location;
	private String		server;
	private long		sentPacketsTime;
	private Hashtable<String,String>	services	= new Hashtable<>(5);
	private boolean		root		= false;

	// This methods sets root flag as true
	void setRoot() {
		root = true;
	}

	// This methods returns true if the device is root device
	boolean isRoot() {
		if (root) {
			return true;
		}
		return false;
	}

	// This methods returns true if the requested device type with this device
	// type.
	boolean isDevAvailable(String type) {
		if (devType.equals(type)) {
			return true;
		}
		return false;
	}

	// This methods returns true if the requested service type matches with any
	// this device services
	boolean isServiceAvailable(String type) {
		if (services.get(type) != null) {
			return true;
		}
		return false;
	}

	// This methods sets the device UUID.
	void setUUID(String uuid) {
		this.uuid = uuid;
	}

	// This methods sets the device type.
	void setDevice(String type) {
		devType = type;
	}

	// This methods sets the service type.
	void setServices(String type) {
		services.put(type, type);
	}

	// This methods sets the location for device description.
	void setLocation(String location) {
		this.location = location;
	}

	// This methods sets the server for device.
	void setServer(String server) {
		this.server = server;
	}

	// This methods sets the crated time or sent NOTIFY packets for device.
	void setTime(long time1) {
		sentPacketsTime = time1;
	}

	// This methods returns the device UUID.
	String getUUID() {
		return uuid;
	}

	// This methods returns the device type.
	String getDevType() {
		return devType;
	}

	// This methods returns the all services.
	Hashtable<String,String> getServices() {
		return services;
	}

	// This methods returns the location of device description.
	String getLocation() {
		return location;
	}

	// This methods returns the server of device.
	String getServer() {
		return server;
	}

	// This methods returns the crated time or sent NOTIFY packets of device.
	long getTime() {
		return sentPacketsTime;
	}
}

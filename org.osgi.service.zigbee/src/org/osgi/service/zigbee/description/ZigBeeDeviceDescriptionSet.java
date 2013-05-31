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


package org.osgi.service.zigbee.description;

/**
 * This interface represents a ZigBee Device description Set
 * ZigBeeDeviceDescriptionSet is registered as an OSGi Service Provides method
 * to retrieve Endpoints descriptions
 * 
 * @version 1.0
 */
public interface ZigBeeDeviceDescriptionSet {

	public final static String	VERSION				= "zigbee.profile.version";

	public final static String	PROFILE_ID			= "zigbee.profile.id";

	public final static String	PROFILE_NAME		= "zigbee.profile.name";

	public final static String	MANUFACTURER_CODE	= "zigbee.profile.manufacturer.code";

	public final static String	MANUFACTURER_NAME	= "zigbee.profile.manufacturer.name";

	public final static String	DEVICES				= "zigbee.profile.devices";

	/**
	 * @param id deviceId Identifier of the device.
	 * @param version The version of the application profile.
	 * @return The associated device description.
	 */
	public ZigBeeDeviceDescription getDeviceSpecification(int deviceId, short version);
}

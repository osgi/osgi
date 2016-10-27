/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
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

package org.osgi.service.zigbee.descriptions;

import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeNode;

/**
 * This interface represents a ZigBee Device description Set. A Set is
 * registered as an OSGi Service that provides method to retrieve endpoint
 * descriptions.
 * 
 * In addition to the ZigBeeDeviceDescriptionSet's (OSGi service) properties;
 * ZigBeeDeviceDescriptionSet is also expected to be registered as an OSGi
 * service with the following {@link ZigBeeEndpoint#PROFILE_ID}, and
 * {@link ZigBeeNode#MANUFACTURER_CODE} properties.
 * 
 * @author $Id$
 */
public interface ZigBeeDeviceDescriptionSet {

	/**
	 * Property key for a version of the application profile. The format is
	 * 'major.minor' with major and minor being integers.
	 * 
	 * This property is <b>mandatory</b>.
	 */
	public final static String	VERSION			= "zigbee.profile.version";

	/**
	 * Property key for a profile name.
	 * 
	 * This property is <b>mandatory</b>.
	 */
	public final static String	PROFILE_NAME	= "zigbee.profile.name";

	/**
	 * Property key for a comma separated list of devices identifiers supported
	 * by the set.
	 * 
	 * This property is <b>mandatory</b>.
	 */
	public final static String	DEVICES			= "zigbee.profile.devices";

	/**
	 * Returns the description of a device identified by its identifier and its
	 * version.
	 * 
	 * @param deviceId Identifier of the device.
	 * @param version The version of the application profile.
	 * @return The associated device description.
	 */
	public ZigBeeDeviceDescription getDeviceSpecification(int deviceId, short version);

}

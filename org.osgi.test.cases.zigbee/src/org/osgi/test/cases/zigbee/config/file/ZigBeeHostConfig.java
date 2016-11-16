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

package org.osgi.test.cases.zigbee.config.file;

import java.math.BigInteger;
import org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeePowerDescriptor;

/**
 * This class represent the expected host configuration.
 * 
 * @author portinaro
 *
 */
public class ZigBeeHostConfig extends ZigBeeNodeConfig {

	public ZigBeeHostConfig(int panId, int channel, int securityLevel, BigInteger IEEEAddress, ZigBeeNodeDescriptor nodeDesc, ZigBeePowerDescriptor powerDesc,
			String userdescription) {
		super(IEEEAddress, null, nodeDesc, powerDesc, userdescription);
	}
}

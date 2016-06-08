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

package org.osgi.test.cases.zigbee.mock;

import org.osgi.service.zigbee.ZCLCluster;
import org.osgi.service.zigbee.descriptors.ZigBeeSimpleDescriptor;
import org.osgi.test.cases.zigbee.config.file.ConfigurationFileReader;

/**
 * 
 *
 * Class used by the configuration file reader. see
 * {@link ConfigurationFileReader}
 * 
 * @author $Id: 7dfcfe97916e5b41964da4bdde5c2141bcc044e4 $
 */
public class ZigBeeEndpointConf extends ZigBeeEndpointImpl {

	private ZigBeeSimpleDescriptor desc;

	public ZigBeeEndpointConf(short id, ZCLCluster[] inputs, ZCLCluster[] ouputs, ZigBeeSimpleDescriptor desc) {
		super(id, inputs, ouputs, desc);
		this.desc = desc;

	}

	public ZigBeeSimpleDescriptor getSimpleDescriptor() {

		return desc;
	}

}

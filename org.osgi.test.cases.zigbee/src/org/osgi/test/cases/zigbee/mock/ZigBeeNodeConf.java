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

import java.math.BigInteger;
import org.osgi.framework.BundleContext;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeePowerDescriptor;
import org.osgi.test.cases.zigbee.config.file.ConfigurationFileReader;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

/**
 * 
 * 
 * Class used by the configuration file reader. see
 * {@link ConfigurationFileReader}
 * 
 * @author $Id$
 */
public class ZigBeeNodeConf extends ZigBeeNodeImpl {

	private String			userDesc;
	private String			endpointNb;
	private BundleContext	bc;

	public ZigBeeNodeConf(BigInteger IEEEAddress, String hostPId, ZigBeeEndpoint[] endpoints,
			ZigBeeNodeDescriptor nodeDesc, ZigBeePowerDescriptor powerDesc, String userDescription, String endpointNb,
			BundleContext bc) {

		super(IEEEAddress, hostPId, endpoints, nodeDesc, powerDesc, userDescription);
		userDesc = userDescription;
		this.endpointNb = endpointNb;
		this.bc = bc;
	}

	public ZigBeeNodeConf(BigInteger IEEEAddress, String hostPId, ZigBeeEndpoint[] endpoints,
			ZigBeeNodeDescriptor nodeDesc, ZigBeePowerDescriptor powerDesc, String userDescription, String endpointNb) {

		super(IEEEAddress, hostPId, endpoints, nodeDesc, powerDesc, userDescription);
		userDesc = userDescription;
		this.endpointNb = endpointNb;
	}

	public ZigBeeNodeConf(BigInteger IEEEAddress, String hostPId, ZigBeeEndpoint[] endpoints,
			ZigBeeNodeDescriptor nodeDesc, ZigBeePowerDescriptor powerDesc, String userDescription) {
		super(IEEEAddress, hostPId, endpoints, nodeDesc, powerDesc, userDescription);
		userDesc = userDescription;

	}

	public Promise getNodeDescriptor() {

		return Promises.resolved(nodeDescriptor);
	}

	public Promise getPowerDescriptor() {

		return Promises.resolved(powerDescriptor);
	}

	public Promise getComplexDescriptor() {

		return Promises.resolved(complexDescriptor);
	}

	public String getUserDesc() {
		return userDesc;
	}

	public int getEndpointNb() {
		return Integer.parseInt(endpointNb);
	}

	public ZigBeeEndpoint[] getEndpoints() {

		return endpoints;
	}
}

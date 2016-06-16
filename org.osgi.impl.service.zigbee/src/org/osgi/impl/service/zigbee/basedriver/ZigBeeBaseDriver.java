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

package org.osgi.impl.service.zigbee.basedriver;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Properties;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.zigbee.basedriver.configuration.ConfigurationFileReader;
import org.osgi.impl.service.zigbee.util.teststep.EndpointServicesListener;
import org.osgi.impl.service.zigbee.util.teststep.TestStepForZigBeeImpl;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeEvent;
import org.osgi.service.zigbee.ZigBeeHost;
import org.osgi.service.zigbee.ZigBeeNode;
import org.osgi.test.support.step.TestStep;

/**
 * Mocked impl of ZigBeeDeviceNodeListener.
 * 
 * @author $Id$
 */
public class ZigBeeBaseDriver {

	private BundleContext		bc;
	private List				Endpoints;
	EndpointServicesListener	listener;

	/**
	 * This constructor creates the ZigBeeBaseDriver object based on the
	 * controller and the BundleContext object.
	 * 
	 * @param bc
	 */
	public ZigBeeBaseDriver(BundleContext bc) {
		this.bc = bc;
		Endpoints = new ArrayList();
	}

	/**
	 * This method starts the base driver. And registers with controller for
	 * getting notifications.
	 */
	public void start() {
		System.out.println(this.getClass().getName() + " - Start the base driver.");

		TestStepForZigBeeImpl testStep = new TestStepForZigBeeImpl(this);
		ServiceRegistration testStepSR = bc.registerService(TestStep.class.getName(), testStep, null);
		try {
			listener = new EndpointServicesListener(bc);
			listener.open();
		} catch (InvalidSyntaxException e1) {
			e1.printStackTrace();
		}
		try {
			// this.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method stops the base driver. And unregisters with controller for
	 * getting notifications.
	 */
	public void stop() {
		listener.close();
		System.out.println(this.getClass().getName() + " - Stop the base driver.");
	}

	public void notifyEvent(ZigBeeEvent event) {

	}

	public void loadConfigurationFIle(String filePAth) {
		ConfigurationFileReader conf = ConfigurationFileReader.getInstance(filePAth, bc);

		// set the min header size
		ZCLFrameImpl.minHeaderSize = conf.getHeaderMinSize();

		// register host
		ZigBeeHost host = conf.getZigBeeHost();
		Dictionary hostProperties = new Properties();
		hostProperties.put(ZigBeeNode.IEEE_ADDRESS, host.getIEEEAddress());
		bc.registerService(ZigBeeHost.class.getName(), host, hostProperties);

		// register endpoints
		for (int i = 0; i < conf.nodes.length; i++) {

			ZigBeeNodeImpl node = conf.nodes[i];
			ZigBeeEndpoint[] endpoints = conf.getEnpoints(node);
			for (int j = 0; j < endpoints.length; j++) {
				ZigBeeEndpoint ep = endpoints[j];
				Dictionary endpointProperties = new Properties();
				endpointProperties.put(ZigBeeNode.IEEE_ADDRESS, node.getIEEEAddress());
				endpointProperties.put(ZigBeeEndpoint.ENDPOINT_ID, String.valueOf(ep.getId()));
				bc.registerService(ZigBeeEndpoint.class.getName(), ep, endpointProperties);
				Endpoints.add(ep);

			}
			Dictionary nodeProperties = new Properties();
			nodeProperties.put(ZigBeeNode.IEEE_ADDRESS, node.getIEEEAddress());

			// register node
			bc.registerService(ZigBeeNode.class.getName(), node, nodeProperties);

		}

	}

}

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

package org.osgi.test.cases.zigbee;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.service.zigbee.ZCLCluster;
import org.osgi.service.zigbee.ZDPException;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeException;
import org.osgi.service.zigbee.ZigBeeHost;
import org.osgi.service.zigbee.ZigBeeNode;
import org.osgi.service.zigbee.descriptions.ZCLClusterDescription;
import org.osgi.test.cases.zigbee.config.file.ZigBeeEndpointConfig;
import org.osgi.test.cases.zigbee.config.file.ZigBeeNodeConfig;
import org.osgi.test.cases.zigbee.mock.TestNotExportedZigBeeEndpoint;
import org.osgi.test.cases.zigbee.mock.ZCLClusterImpl;

public class ZigBeeExportTestCase extends ZigBeeTestCases {
	private static final String TAG = ZigBeeExportTestCase.class.getName();

	/**
	 * This test verifies if the driver implementation is correctly calling the
	 * {@code ZigBeeEndpoint.notExported()} method. The exported ZigBeeEndpoint
	 * cannot rely on a call to this method from the ZigBee implementation to
	 * state that it has been actually exported or not, but we expect it to be
	 * called within a short time (in the CT the amount in milliseconds is the
	 * discovery timeout configuration property), if all the following
	 * conditions are met:
	 * <ul>
	 * <li>The ZigBeeHost service is registered.
	 * <li>The exported ZigBeeEndpoint is not suitable to be exported.
	 * </ul>
	 * The latter condition may occur if:
	 * <ul>
	 * <li>The ZigBeeEndpoint does not have the mandatory service properties
	 * set.
	 * <li>The new ZigBeeEndpoint has the same
	 * {@link ZigBeeEndpoint#ENDPOINT_ID} and the same
	 * {@link ZigBeeNode#IEEE_ADDRESS} service properties values of another
	 * ZigBeeEndpoint.
	 * <li>The new ZigBeeEndpoint do not have the
	 * {@link ZigBeeEndpoint#ENDPOINT_ID} and {@link ZigBeeNode#IEEE_ADDRESS}
	 * </ul>
	 * 
	 * The above checks may be issued by the base driver because it have a
	 * complete overview of the services that it has registered and, for
	 * instance, it can figure out if a {@code ZigBeeEndpoint} is a valid one or
	 * one that has been registered by an application with the purpose to be
	 * exported, but that do not have the {@link ZigBeeEndpoint#ZIGBEE_EXPORT}
	 * property set.
	 * 
	 * @throws Exception In case of failure.
	 */

	public void testExport() throws Exception {
		log(TAG, "---- testExport");

		ZigBeeHost host = getZigBeeHost(conf.getDiscoveryTimeout());

		if (!host.isStarted()) {
			host.start();
		}

		ZigBeeNodeConfig nodeConfig = conf.getFirstNode();

		/*
		 * The exported endpoint identifier cannot be a broadcast endpoint.
		 */
		short broadcastEndpointId = 0xff;

		this.registerInvalidEndpointId(getContext(), host, nodeConfig, broadcastEndpointId);

		/*
		 * The exported endpoint identifier must be in the range [0, 0xff)
		 */
		this.registerInvalidEndpointId(getContext(), host, nodeConfig, (short) 300);

		this.registerInvalidEndpointId(getContext(), host, nodeConfig, (short) -10);
	}

	/**
	 * Used to verify if the RI is calling the appropriate notExported()
	 * callback when an exported ZigBeeEndpoint with wrong service properties is
	 * registered.
	 * 
	 * @param bc The BundleContext
	 * @param host The ZigBeeHost where to export the ZigBeeEndpoint.
	 * @param invalidEndpointId An invalid value of the endpoint identifier.
	 */

	private void registerInvalidEndpointId(BundleContext bc, ZigBeeHost host, ZigBeeNodeConfig nodeConfig, short invalidEndpointId) {

		/**
		 * Try to export an invalid ZigBeeEnpoint, by setting invalid values of
		 * the service properties. The invalid endpoint is configured with some
		 * ZCLClusters that are the exact couterpart of the first node found in
		 * the xml file provided by the user.
		 */

		ZigBeeEndpoint[] hostEndpoints = host.getEndpoints();

		assertNotNull("ZigBeeHost.getEndpoints() cannot return null", hostEndpoints);

		int initialHostEndpointsNumber = host.getEndpoints().length;

		ZigBeeEndpointConfig ep = nodeConfig.getEndpoints()[0];

		ZCLClusterDescription[] serverClustersConfig = ep.getServerClusters();
		ZCLClusterDescription[] clientClustersConfig = ep.getServerClusters();

		ZCLCluster[] serverClusters = new ZCLCluster[serverClustersConfig.length];
		ZCLCluster[] clientClusters = new ZCLCluster[clientClustersConfig.length];

		for (int i = 0; i < serverClusters.length; i++) {
			ZCLCluster cluster = new ZCLClusterImpl(serverClustersConfig[i]);
			serverClusters[i] = cluster;
		}

		for (int i = 0; i < clientClusters.length; i++) {
			ZCLCluster cluster = new ZCLClusterImpl(clientClustersConfig[i]);
			clientClusters[i] = cluster;
		}

		/**
		 * Creates a ZigBeeEndpoint swapping the client and server clusters and
		 * by using a wrong endpoint identifier (endpoint broadcast)
		 */

		ZigBeeEndpoint badEnpoint = new TestNotExportedZigBeeEndpoint(invalidEndpointId, clientClusters, serverClusters, ep.getSimpleDescriptor());

		Dictionary<String,Object> endpointProperties = new Hashtable<>();
		endpointProperties.put(ZigBeeNode.IEEE_ADDRESS, host.getIEEEAddress());
		endpointProperties.put(ZigBeeEndpoint.ENDPOINT_ID, String.valueOf(badEnpoint.getId()));
		endpointProperties.put(ZigBeeEndpoint.ZIGBEE_EXPORT, "exported");

		/*
		 * Register this ZigBeeEndpoint
		 */

		log("Register  endpoint: " + badEnpoint + " in the OSGi services registry");

		bc.registerService(ZigBeeEndpoint.class.getName(), badEnpoint, endpointProperties);

		try {
			/*
			 * It might takes time to register the service test framework.
			 */
			log("Waiting wait for a discovery timeout value of milliseconds = " + DISCOVERY_TIMEOUT);
			Thread.sleep(DISCOVERY_TIMEOUT);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("No exception is expected during sleep!");
		}

		TestNotExportedZigBeeEndpoint t = (TestNotExportedZigBeeEndpoint) badEnpoint;

		ZigBeeException notExportedException = t.getNotExportedException();

		if (notExportedException == null) {
			fail("method ZigBeeEndpoint.notExported() should have been called within the discovery timeout, as expected.");
		} else if (notExportedException instanceof ZDPException) {
			assertEquals("method ZigBeeEndpoint.notExported() was called with the wrong exception error code",
					ZDPException.INVALID_EP,
					notExportedException.getErrorCode());
		} else {
			fail("the ZigBeeEndpoint.notExported() method has been called with the wrong exception (it should be a ZDPException.INVALID_EP.");
		}

		int newNb = host.getEndpoints().length;
		assertEquals("The number of enpoints returned by the ZigBeeHost.getEnpoints() is changed desipite the ZigBee endpoint is wrong.", newNb, initialHostEndpointsNumber);

	}

}

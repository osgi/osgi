/*
 * Copyright (c) OSGi Alliance (2016, 2020). All Rights Reserved.
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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.zigbee.event.EndResponse;
import org.osgi.impl.service.zigbee.event.ZCLCommandResponseImpl;
import org.osgi.impl.service.zigbee.event.ZCLCommandResponseStreamImpl;
import org.osgi.service.zigbee.ZCLCommandResponseStream;
import org.osgi.service.zigbee.ZCLFrame;
import org.osgi.service.zigbee.ZDPException;
import org.osgi.service.zigbee.ZDPFrame;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeHost;
import org.osgi.service.zigbee.ZigBeeLinkQuality;
import org.osgi.service.zigbee.ZigBeeNode;
import org.osgi.service.zigbee.ZigBeeRoute;
import org.osgi.service.zigbee.descriptors.ZigBeeComplexDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeePowerDescriptor;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

/**
 * Mocked impl.
 * 
 * @author $Id$
 */
public class ZigBeeNodeImpl implements ZigBeeNode {

	private ZigBeeHost					host;
	private BigInteger					IEEEAddress;
	private int							nwkAddress;

	private ZigBeeEndpointImpl[]		endpoints				= new ZigBeeEndpointImpl[0];

	protected ZigBeeNodeDescriptor		nodeDescriptor;
	protected ZigBeePowerDescriptor		powerDescriptor;
	protected ZigBeeComplexDescriptor	complexDescriptor;
	protected String					userDescription;

	private String						nodePid;

	private List<ServiceRegistration<ZigBeeEndpoint>>	endpointsServiceRegs	= new ArrayList<>();

	/**
	 * If true enables the ZigBeeNode to fail for those methods that could file
	 * because the feature is not available.
	 */
	private boolean						forceFailure			= true;
	private ServiceRegistration<ZigBeeNode>				nodeServiceReg;

	public ZigBeeNodeImpl(BigInteger IEEEAddress, int nwkAddress, ZigBeeEndpointImpl[] endpoints,
			ZigBeeNodeDescriptor nodeDescriptor, ZigBeePowerDescriptor powerDescriptor, String userdescription) {
		this.IEEEAddress = IEEEAddress;
		this.endpoints = endpoints;
		this.powerDescriptor = powerDescriptor;
		this.nodeDescriptor = nodeDescriptor;
		this.userDescription = userdescription;
		this.nwkAddress = nwkAddress;

		for (int i = 0; i < endpoints.length; i++) {
			endpoints[i].setZigBeeNode(this);
		}

	}

	public ZigBeeNodeImpl(ZigBeeHost host, BigInteger IEEEAddress, int nwkAddress, ZigBeeEndpointImpl[] endpoints,
			ZigBeeNodeDescriptor nodeDesc, ZigBeePowerDescriptor powerDesc, String userdescription) {
		this(IEEEAddress, nwkAddress, endpoints, nodeDesc, powerDesc, userdescription);

		this.host = host;

		nodePid = IEEEAddress.toString() + ".node.pid";
	}

	@Override
	public BigInteger getIEEEAddress() {
		return this.IEEEAddress;
	}

	@Override
	public int getNetworkAddress() {
		return this.nwkAddress;
	}

	@Override
	public String getHostPid() {
		return host.getHostPid();
	}

	@Override
	public int getPanId() {
		return host.getPanId();
	}

	@Override
	public BigInteger getExtendedPanId() {
		return host.getExtendedPanId();
	}

	@Override
	public Promise<ZigBeeNodeDescriptor> getNodeDescriptor() {
		return Promises.resolved(nodeDescriptor);
	}

	@Override
	public Promise<ZigBeePowerDescriptor> getPowerDescriptor() {
		return Promises.resolved(powerDescriptor);
	}

	@Override
	public Promise<ZigBeeComplexDescriptor> getComplexDescriptor() {
		if (nodeDescriptor.isComplexDescriptorAvailable()) {
			return Promises.resolved(complexDescriptor);
		} else {
			return Promises.failed(new ZDPException(ZDPException.NO_DESCRIPTOR, " descriptor is not available"));
		}
	}

	@Override
	public Promise<Map<String,ZigBeeLinkQuality>> getLinksQuality()
			throws ZDPException {

		if (forceFailure) {
			return Promises.failed(new ZDPException(ZDPException.NOT_SUPPORTED, ""));
		}

		/*
		 * Return a fake content.
		 */

		ZigBeeLinkQuality linkQuality = new ZigBeeLinkQuality() {

			@Override
			public int getRelationship() {
				return 0;
			}

			@Override
			public String getNeighbor() {
				return "neighbor";
			}

			@Override
			public int getLQI() {
				return 3;
			}

			@Override
			public int getDepth() {
				return 1;
			}
		};

		Map<String,ZigBeeLinkQuality> linksQualityMap = new HashMap<>();
		linksQualityMap.put(nodePid, linkQuality);

		return Promises.resolved(linksQualityMap);
	}

	@Override
	public Promise<Map<String,ZigBeeRoute>> getRoutingTable() {
		if (forceFailure) {
			return Promises.failed(new ZDPException(ZDPException.NOT_SUPPORTED, ""));
		}

		Map<String,ZigBeeRoute> routingTableMap = new HashMap<>();

		/*
		 * Return a fake content.
		 */

		ZigBeeRoute routeEntry = new ZigBeeRoute() {
			@Override
			public int getStatus() {
				return 0;
			}

			@Override
			public String getNextHop() {
				return "";
			}

			@Override
			public String getDestination() {
				return "";
			}
		};

		routingTableMap.put(this.nodePid, routeEntry);

		return Promises.resolved(routingTableMap);
	}

	@Override
	public Promise<Void> leave() {
		if (forceFailure) {
			return Promises.failed(new ZDPException(ZDPException.NOT_SUPPORTED, ""));
		}
		return Promises.resolved(null);
	}

	@Override
	public Promise<Void> leave(boolean rejoin, boolean removeChildren) {
		if (forceFailure) {
			return Promises.failed(new ZDPException(ZDPException.NOT_SUPPORTED, ""));
		}

		return Promises.failed(new UnsupportedOperationException("Not implemented"));
	}

	@Override
	public Promise<ZDPFrame> invoke(int clusterIdReq, int expectedClusterIdRsp,
			ZDPFrame message) {
		return Promises.failed(new UnsupportedOperationException("Not implemented"));
	}

	@Override
	public Promise<ZDPFrame> invoke(int clusterIdReq, ZDPFrame message) {
		return Promises.failed(new UnsupportedOperationException("Not implemented"));
	}

	@Override
	public Promise<String> getUserDescription() {
		if (nodeDescriptor.isUserDescriptorAvailable()) {
			return Promises.resolved(userDescription);
		} else {
			return Promises.failed(new ZDPException(ZDPException.NO_DESCRIPTOR, ""));
		}
	}

	@Override
	public Promise<Void> setUserDescription(String userDescriptor) {
		if (nodeDescriptor.isUserDescriptorAvailable()) {
			this.userDescription = userDescriptor;
			return Promises.resolved(null);
		} else {
			return Promises.failed(new ZDPException(ZDPException.NO_DESCRIPTOR, ""));
		}
	}

	@Override
	public ZCLCommandResponseStream broadcast(int clusterID, ZCLFrame frame) {
		ZCLCommandResponseStreamImpl impl = new ZCLCommandResponseStreamImpl();

		// Stub out the response by immediately filling it with an Unsupported
		// Operation Exception and ending it
		impl.handleResponse(new ZCLCommandResponseImpl(Promises.failed(
				new UnsupportedOperationException("Not yet implemented"))));
		impl.handleResponse(new EndResponse());

		return impl;
	}

	@Override
	public ZCLCommandResponseStream broadcast(int clusterID, ZCLFrame frame, String exportedServicePID) {
		ZCLCommandResponseStreamImpl impl = new ZCLCommandResponseStreamImpl();

		// Stub out the response by immediately filling it with an Unsupported
		// Operation Exception and ending it
		impl.handleResponse(new ZCLCommandResponseImpl(Promises.failed(
				new UnsupportedOperationException("Not yet implemented"))));
		impl.handleResponse(new EndResponse());

		return impl;
	}

	@Override
	public ZigBeeEndpoint[] getEndpoints() {
		if (endpoints == null) {
			return new ZigBeeEndpoint[0];
		}
		return endpoints;
	}

	@Override
	public String toString() {
		return "" + this.getClass().getName() + "[IEEEAddress: " + IEEEAddress + ", nwkAddress: " + nwkAddress
				+ ", hostPId: " + host.getHostPid() + "]";
	}

	/**
	 * Activates the node.
	 * 
	 * @param bc The BundleContext.
	 */
	protected void activate(BundleContext bc) {

		endpointsServiceRegs.clear();

		for (int j = 0; j < endpoints.length; j++) {
			ZigBeeEndpointImpl endpoint = endpoints[j];
			Dictionary<String,Object> props = new Hashtable<>();

			props.put(ZigBeeNode.IEEE_ADDRESS, this.getIEEEAddress());
			props.put(ZigBeeEndpoint.ENDPOINT_ID,
					Short.valueOf(endpoint.getId()));
			props.put(ZigBeeEndpoint.PROFILE_ID,
					Integer.valueOf(endpoint.getSimpleDescriptorInternal()
							.getApplicationProfileId()));
			props.put(ZigBeeEndpoint.DEVICE_ID,
					Integer.valueOf(endpoint.getSimpleDescriptorInternal()
							.getApplicationDeviceId()));
			props.put(ZigBeeEndpoint.DEVICE_VERSION,
					Byte.valueOf(endpoint.getSimpleDescriptorInternal()
							.getApplicationDeviceVersion()));
			props.put(ZigBeeEndpoint.HOST_PID, this.getHostPid());
			props.put(ZigBeeEndpoint.OUTPUT_CLUSTERS, endpoint.getSimpleDescriptorInternal().getOutputClusters());
			props.put(ZigBeeEndpoint.INPUT_CLUSTERS, endpoint.getSimpleDescriptorInternal().getInputClusters());
			props.put(org.osgi.service.device.Constants.DEVICE_CATEGORY, ZigBeeEndpoint.DEVICE_CATEGORY);
			props.put("service.pid", getEndpointServicePid(endpoint));

			ServiceRegistration<ZigBeeEndpoint> endpointServiceReg = bc
					.registerService(ZigBeeEndpoint.class, endpoint, props);
			endpointsServiceRegs.add(endpointServiceReg);
		}

		/*
		 * The ZigBeeNode must be registered only after its ZigBeeEndpoints have
		 * been registered.
		 */
		Dictionary<String,Object> nodeProperties = new Hashtable<>();

		nodeProperties.put(ZigBeeNode.PAN_ID, Integer.valueOf(this.getPanId()));
		nodeProperties.put(ZigBeeNode.EXTENDED_PAN_ID, this.getExtendedPanId());
		nodeProperties.put(ZigBeeNode.IEEE_ADDRESS, this.getIEEEAddress());
		nodeProperties.put(ZigBeeNode.LOGICAL_TYPE,
				Short.valueOf(nodeDescriptor.getLogicalType()));
		nodeProperties.put(ZigBeeNode.MANUFACTURER_CODE,
				Integer.valueOf(nodeDescriptor.getManufacturerCode()));
		nodeProperties.put(ZigBeeNode.RECEIVER_ON_WHEN_IDLE, Boolean.valueOf(
				nodeDescriptor.getMacCapabilityFlags().isReceiverOnWhenIdle()));
		nodeProperties.put(ZigBeeNode.POWER_SOURCE, Boolean.valueOf(
				nodeDescriptor.getMacCapabilityFlags().isMainsPower()));
		nodeProperties.put("service.pid", nodePid);

		nodeProperties.put(org.osgi.service.device.Constants.DEVICE_CATEGORY, ZigBeeEndpoint.DEVICE_CATEGORY);
		if (nodeDescriptor.isComplexDescriptorAvailable() && complexDescriptor != null) {
			nodeProperties.put(org.osgi.service.device.Constants.DEVICE_DESCRIPTION, complexDescriptor.getModelName());
			nodeProperties.put(org.osgi.service.device.Constants.DEVICE_SERIAL, complexDescriptor.getSerialNumber());
		}

		nodeServiceReg = bc.registerService(ZigBeeNode.class, this,
				nodeProperties);
	}

	/**
	 * Deactivate the node, unregistering itself and then its ZigBeeEndpoints.
	 * 
	 * @param bc The BundleContext
	 */

	protected void deactivate(BundleContext bc) {
		/*
		 * The ZigBeeNode must be unregistered before its ZigBeeEndpoints being
		 * unregistered.
		 */

		nodeServiceReg.unregister();

		for (Iterator<ServiceRegistration<ZigBeeEndpoint>> iterator = endpointsServiceRegs
				.iterator(); iterator.hasNext();) {
			ServiceRegistration<ZigBeeEndpoint> sReg = iterator.next();
			sReg.unregister();
		}

		endpointsServiceRegs.clear();
	}

	public String getEndpointServicePid(ZigBeeEndpointImpl endpoint) {
		return this.getIEEEAddress() + "." + endpoint.getId() + ".endpoint.pid";
	}
}


package org.osgi.test.cases.zigbee.config.file;

import java.math.BigInteger;
import org.osgi.service.zigbee.descriptors.ZigBeeComplexDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeePowerDescriptor;

public class ZigBeeNodeConfig {

	private BigInteger				IEEEAddress;
	private int						nwkAddress;
	private ZigBeeEndpointConfig[]	endpoints;
	private ZigBeeNodeDescriptor	nodeDescriptor;
	private ZigBeePowerDescriptor	powerDescriptor;
	private ZigBeeComplexDescriptor	complexDescriptor;
	private String					userDescription;
	private int						actualEndpointsNumber;

	private ZigBeeHostConfig		host;

	protected ZigBeeNodeConfig(BigInteger IEEEAddress, ZigBeeEndpointConfig[] endpoints,
			ZigBeeNodeDescriptor nodeDescriptor, ZigBeePowerDescriptor poswerDescriptor, String userDescription) {
		this.IEEEAddress = IEEEAddress;
		this.endpoints = endpoints;
		this.powerDescriptor = poswerDescriptor;
		this.nodeDescriptor = nodeDescriptor;
		this.userDescription = userDescription;
	}

	public ZigBeeNodeConfig(ZigBeeHostConfig host, BigInteger IEEEAddress, ZigBeeEndpointConfig[] endpoints,
			ZigBeeNodeDescriptor nodeDescriptor, ZigBeePowerDescriptor powerDescriptor, String userDescription) {
		this(IEEEAddress, endpoints, nodeDescriptor, powerDescriptor, userDescription);

		this.host = host;
	}

	public BigInteger getIEEEAddress() {
		return this.IEEEAddress;
	}

	public int getNetworkAddress() {
		return this.nwkAddress;
	}

	public String getHostPid() {
		return host.getHostPid();
	}

	public ZigBeeEndpointConfig[] getEnpoints() {
		return endpoints;
	}

	public int getPanId() {
		return host.getPanId();
	}

	public BigInteger getExtendedPanId() {
		return null;
	}

	public ZigBeeNodeDescriptor getNodeDescriptor() {
		return nodeDescriptor;
	}

	public String getUserDescription() {
		return userDescription;
	}

	public ZigBeePowerDescriptor getPowerDescriptor() {
		return powerDescriptor;
	}

	public ZigBeeComplexDescriptor getComplexDescriptor() {
		return complexDescriptor;
	}

	public void setActualEndpointsNumber(int actualEndpointsNumber) {
		this.actualEndpointsNumber = actualEndpointsNumber;
	}

	public int getActualEndpointsNumber() {
		return actualEndpointsNumber;
	}
}

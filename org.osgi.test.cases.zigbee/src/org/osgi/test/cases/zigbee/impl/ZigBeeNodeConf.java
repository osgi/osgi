package org.osgi.test.cases.zigbee.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.zigbee.ZigBeeEndpoint;
import org.osgi.service.zigbee.ZigBeeNode;
import org.osgi.service.zigbee.descriptors.ZigBeeComplexDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor;
import org.osgi.service.zigbee.descriptors.ZigBeePowerDescriptor;

/**
 * 
 * 
 * TODO Add Javadoc comment for this type.
 * 
 * @author $Id$
 */
public class ZigBeeNodeConf extends ZigBeeNodeImpl {

	private String userDesc;
	private String endpointNb;
	private BundleContext bc;

	public ZigBeeNodeConf(BigInteger IEEEAddress, String hostPId,
			ZigBeeEndpoint[] endpoints, ZigBeeNodeDescriptor nodeDesc,
			ZigBeePowerDescriptor powerDesc, String userDescription,
			String endpointNb, BundleContext bc) {

		super(IEEEAddress, hostPId, endpoints, nodeDesc, powerDesc,
				userDescription);
		userDesc = userDescription;
		this.endpointNb = endpointNb;
		this.bc = bc;
	}

	public ZigBeeNodeConf(BigInteger IEEEAddress, String hostPId,
			ZigBeeEndpoint[] endpoints, ZigBeeNodeDescriptor nodeDesc,
			ZigBeePowerDescriptor powerDesc, String userDescription,
			String endpointNb) {

		super(IEEEAddress, hostPId, endpoints, nodeDesc, powerDesc,
				userDescription);
		userDesc = userDescription;
		this.endpointNb = endpointNb;
	}

	public ZigBeeNodeConf(BigInteger IEEEAddress, String hostPId,
			ZigBeeEndpoint[] endpoints, ZigBeeNodeDescriptor nodeDesc,
			ZigBeePowerDescriptor powerDesc, String userDescription) {
		super(IEEEAddress, hostPId, endpoints, nodeDesc, powerDesc,
				userDescription);
		userDesc = userDescription;

	}

	public ZigBeeNodeDescriptor getNodeDescriptor() {
		// TODO Auto-generated method stub
		return nodeDescriptor;
	}

	public ZigBeePowerDescriptor getPowerDescriptor() {
		// TODO Auto-generated method stub
		return powerDescriptor;
	}

	public ZigBeeComplexDescriptor getComplexDescriptor() {
		// TODO Auto-generated method stub
		return complexDescriptor;
	}

	public String getUserDesc() {
		return userDesc;
	}

	public int getEndpointNb() {
		return Integer.parseInt(endpointNb);
	}

	public ZigBeeEndpoint[] getEndpoints() {
		BigInteger endpointIeeeAddress = this.getIEEEAddress();
		ZigBeeEndpoint[] result = null;
		List zEndpoints = new ArrayList();
		try {
			ServiceReference[] srs = bc.getAllServiceReferences(
					ZigBeeEndpoint.class.getName(), null);
			int srsIndex = 0;
			while (srsIndex < srs.length) {
				ServiceReference sr = srs[srsIndex];
				if (endpointIeeeAddress.equals(sr
						.getProperty(ZigBeeNode.IEEE_ADDRESS))) {
					zEndpoints.add(bc.getService(srs[srsIndex]));
				}
				srsIndex = srsIndex + 1;

			}
			int length = zEndpoints.size();
			result = new ZigBeeEndpoint[length];
			for (int i = 0; i < length; i++) {

				result[i] = (ZigBeeEndpoint) zEndpoints.get(i);
			}
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}

		return result;
	}
}


package org.osgi.impl.service.zigbee.basedriver.configuration;

import java.math.BigInteger;

/**
 * This class is used to identify an attribute Id in the network
 * 
 * @author $Id$
 */
public class NetworkAttributeIds {

	private BigInteger	ieeeAddresss;
	private short		endpointId;
	private int			clusterId;
	private int			attributeId;

	public NetworkAttributeIds(BigInteger ieeeAddresss, short endpointId, int clusterId, int attributeId) {
		this.ieeeAddresss = ieeeAddresss;
		this.endpointId = endpointId;
		this.clusterId = clusterId;
		this.attributeId = attributeId;
	}

	public BigInteger getIeeeAddresss() {
		return ieeeAddresss;
	}

	public short getEndpointId() {
		return endpointId;
	}

	public int getClusterId() {
		return clusterId;
	}

	public int getAttributeId() {
		return attributeId;
	}
}

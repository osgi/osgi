
package org.osgi.test.cases.zigbee.config.file;

import org.osgi.service.zigbee.ZCLAttribute;
import org.osgi.service.zigbee.descriptions.ZCLClusterDescription;

public class ZCLClusterConfig {

	private ZCLAttribute[]			attributes;
	private int[]					commandIds;
	private ZCLClusterDescription	clusterDescription;
	private int						anUnsupportedAttributeId;

	public ZCLClusterConfig(int[] commandIds, ZCLAttribute[] attributes, ZCLClusterDescription clusterDescription, int anUnsupportedAttributeId) {
		this.commandIds = commandIds;
		this.attributes = attributes;
		this.clusterDescription = clusterDescription;
		this.anUnsupportedAttributeId = anUnsupportedAttributeId;
	}

	public int getId() {
		return clusterDescription.getId();
	}

	public ZCLAttribute[] getAttributes() {
		return attributes;
	}

	public int getAnUnsupportedAttributeId() {
		return anUnsupportedAttributeId;
	}

	public int[] getCommandIds() {
		return commandIds;
	}

	public ZCLClusterDescription getClusterDescription() {
		return clusterDescription;
	}
}

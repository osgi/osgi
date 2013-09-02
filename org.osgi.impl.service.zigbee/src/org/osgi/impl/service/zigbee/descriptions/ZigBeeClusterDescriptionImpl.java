
package org.osgi.impl.service.zigbee.descriptions;

import org.osgi.service.zigbee.descriptions.ZigBeeAttributeDescription;
import org.osgi.service.zigbee.descriptions.ZigBeeClusterDescription;
import org.osgi.service.zigbee.descriptions.ZigBeeCommandDescription;
import org.osgi.service.zigbee.descriptions.ZigBeeGlobalClusterDescription;

public class ZigBeeClusterDescriptionImpl implements ZigBeeClusterDescription {

	private int								id;
	private ZigBeeGlobalClusterDescription	global;

	public ZigBeeClusterDescriptionImpl(int id, ZigBeeGlobalClusterDescription global) {
		this.id = id;
		this.global = global;
	}

	public int getId() {
		return id;
	}

	public ZigBeeGlobalClusterDescription getGlobalClusterDescription() {
		return global;
	}

	public ZigBeeCommandDescription[] getReceivedCommandDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}

	public ZigBeeCommandDescription[] getGeneratedCommandDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}

	public ZigBeeAttributeDescription[] getAttributeDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}
}

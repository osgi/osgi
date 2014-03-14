
package org.osgi.impl.service.zigbee.descriptions;

import org.osgi.service.zigbee.descriptions.ZCLClusterDescription;
import org.osgi.service.zigbee.descriptions.ZCLGlobalClusterDescription;
import org.osgi.service.zigbee.descriptions.ZigBeeAttributeDescription;
import org.osgi.service.zigbee.descriptions.ZigBeeCommandDescription;

/**
 * Mocked impl.
 */
public class ZCLClusterDescriptionImpl implements ZCLClusterDescription {

	private int							id;
	private ZCLGlobalClusterDescription	global;

	/**
	 * @param id
	 * @param global
	 */
	public ZCLClusterDescriptionImpl(int id, ZCLGlobalClusterDescription global) {
		this.id = id;
		this.global = global;
	}

	public int getId() {
		return id;
	}

	public ZCLGlobalClusterDescription getGlobalClusterDescription() {
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

	public String toString() {
		return "" + this.getClass().getName() + "[id: " + id + ", global: " + global + "]";
	}

}

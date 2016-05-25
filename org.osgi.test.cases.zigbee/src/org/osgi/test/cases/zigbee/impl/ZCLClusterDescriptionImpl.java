
package org.osgi.test.cases.zigbee.impl;

import org.osgi.service.zigbee.descriptions.ZCLAttributeDescription;
import org.osgi.service.zigbee.descriptions.ZCLClusterDescription;
import org.osgi.service.zigbee.descriptions.ZCLCommandDescription;
import org.osgi.service.zigbee.descriptions.ZCLGlobalClusterDescription;

/**
 * Mocked impl.
 * 
 * @author $Id$
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

	public ZCLCommandDescription[] getReceivedCommandDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}

	public ZCLCommandDescription[] getGeneratedCommandDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}

	public ZCLAttributeDescription[] getAttributeDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}

	public String toString() {
		return "" + this.getClass().getName() + "[id: " + id + ", global: " + global + "]";
	}

}

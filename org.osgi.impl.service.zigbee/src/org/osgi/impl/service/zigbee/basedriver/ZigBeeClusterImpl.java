
package org.osgi.impl.service.zigbee.basedriver;

import java.util.HashMap;
import java.util.Map;
import org.osgi.service.zigbee.ZigBeeAttribute;
import org.osgi.service.zigbee.ZigBeeAttributeRecord;
import org.osgi.service.zigbee.ZigBeeCluster;
import org.osgi.service.zigbee.ZigBeeCommand;
import org.osgi.service.zigbee.ZigBeeHandler;
import org.osgi.service.zigbee.descriptions.ZigBeeClusterDescription;

/**
 * Mocked impl of ZigBeeDeviceNodeListener.
 */
public class ZigBeeClusterImpl implements ZigBeeCluster {

	private int							id;
	private ZigBeeAttributeImpl[]		attributes;
	private ZigBeeCommand[]				commands;
	private ZigBeeClusterDescription	description;

	/**
	 * @param id
	 */
	public ZigBeeClusterImpl(int id) {
		this.id = id;
	}

	/**
	 * @param commands
	 * @param attributes
	 * @param desc
	 */
	public ZigBeeClusterImpl(ZigBeeCommand[] commands, ZigBeeAttributeImpl[] attributes, ZigBeeClusterDescription desc) {
		id = desc.getId();
		this.commands = commands;
		this.attributes = attributes;
		this.description = desc;
	}

	public int getId() {
		return id;
	}

	public ZigBeeAttribute getAttribute(int attributeId) {
		return attributes[attributeId];
	}

	public ZigBeeAttribute[] getAttributes() {
		return attributes;
	}

	public ZigBeeCommand getCommand(int commandId) {
		return commands[commandId];
	}

	public ZigBeeCommand[] getCommands() {
		return commands;
	}

	public String toString() {
		return description.getGlobalClusterDescription().getClusterName();
	}

	public void readAttributes(int[] attributesIds, ZigBeeHandler handler) {
		Map response = new HashMap();
		for (int i : attributesIds) {
			ZigBeeAttributeImpl attribute = attributes[i];
			response.put(attribute.getId(), attribute.getValue());
		}
		handler.onSuccess(response);
	}

	public void writeAttributes(boolean undivided, ZigBeeAttributeRecord[] attributesRecords, ZigBeeHandler handler) {
		// TODO Auto-generated method stub
	}

}

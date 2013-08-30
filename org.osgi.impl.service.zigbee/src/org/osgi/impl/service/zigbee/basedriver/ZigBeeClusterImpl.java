
package org.osgi.impl.service.zigbee.basedriver;

import org.osgi.service.zigbee.ZigBeeAttribute;
import org.osgi.service.zigbee.ZigBeeAttributeRecord;
import org.osgi.service.zigbee.ZigBeeCluster;
import org.osgi.service.zigbee.ZigBeeCommand;
import org.osgi.service.zigbee.ZigBeeHandler;
import org.osgi.service.zigbee.ZigBeeNoDescriptionAvailableException;
import org.osgi.service.zigbee.descriptions.ZigBeeClusterDescription;

public class ZigBeeClusterImpl implements ZigBeeCluster {

	private int							id;
	private ZigBeeAttribute[]			attributes;
	private ZigBeeCommand[]				commands;
	private ZigBeeClusterDescription	description;

	public ZigBeeClusterImpl(int id) {
		this.id = id;
	}

	public ZigBeeClusterImpl(ZigBeeCommand[] commands, ZigBeeAttribute[] attributes, ZigBeeClusterDescription desc) {
		id = desc.getId();
		this.commands = commands;
		this.attributes = attributes;
		this.description = desc;
	}

	public int getId() {
		return id;
	}

	public ZigBeeClusterDescription getDescription() {
		return description;
	}

	public ZigBeeAttribute getAttribute(int id) {
		return attributes[id];
	}

	public ZigBeeAttribute[] getAttributes() {
		return attributes;
	}

	public ZigBeeCommand getCommand(int id) {
		return commands[id];
	}

	public ZigBeeCommand[] getCommands() {
		return commands;
	}

	public ZigBeeClusterDescription getClusterDescription() {
		return description;
	}

	public String toString() {
		// TODO Auto-generated method stub
		return description.getGlobalClusterDescription().getClusterName();
	}

	public void readAttributes(int[] attributesIds, ZigBeeHandler handler) {
		// TODO Auto-generated method stub
	}

	public void readAttributesAsBytes(int[] attibutesIds, ZigBeeHandler handler) {
		// TODO Auto-generated method stub
	}

	public void writeAttributes(boolean undivided, int[] attributesIds, byte[] values, ZigBeeHandler handler) throws ZigBeeNoDescriptionAvailableException {
		// TODO Auto-generated method stub
	}

	public void writeAttributes(boolean undivided, ZigBeeAttributeRecord[] attributes, ZigBeeHandler handler) {
		// TODO Auto-generated method stub

	}
}

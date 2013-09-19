package org.osgi.impl.service.zigbee.basedriver;

import org.osgi.service.zigbee.ZigBeeAttribute;
import org.osgi.service.zigbee.ZigBeeAttributeRecord;
import org.osgi.service.zigbee.ZigBeeCluster;
import org.osgi.service.zigbee.ZigBeeCommand;
import org.osgi.service.zigbee.ZigBeeException;
import org.osgi.service.zigbee.ZigBeeHandler;
import org.osgi.service.zigbee.ZigBeeNoDescriptionAvailableException;
import org.osgi.service.zigbee.descriptions.ZigBeeClusterDescription;

/**
 * Mocked impl of ZigBeeDeviceNodeListener.
 */
public class ZigBeeClusterImpl implements ZigBeeCluster {

	private int							id;
	private ZigBeeAttribute[]			attributes;
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
		ZigBeeAttribute attribute = attributes[attributesIds[0]];
		try {
			attribute.getValue(handler);
		} catch (ZigBeeException e) {
			e.printStackTrace();
		}
	}

	public void readAttributesAsBytes(int[] attibutesIds, ZigBeeHandler handler) {
		// TODO: AAA: Auto-generated method stub
	}

	public void writeAttributes(boolean undivided, ZigBeeAttributeRecord[] attributesRecords, ZigBeeHandler handler) {
		// TODO Auto-generated method stub

	}

	public void writeAttributes(boolean undivided, int[] attributesIds, byte[] values, ZigBeeHandler handler) throws ZigBeeNoDescriptionAvailableException {
		// TODO Auto-generated method stub

	}


}

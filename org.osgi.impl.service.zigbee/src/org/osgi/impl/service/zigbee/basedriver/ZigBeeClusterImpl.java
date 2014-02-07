
package org.osgi.impl.service.zigbee.basedriver;

import java.util.HashMap;
import java.util.Map;
import org.osgi.service.zigbee.ZCLFrame;
import org.osgi.service.zigbee.ZigBeeAttribute;
import org.osgi.service.zigbee.ZigBeeAttributeRecord;
import org.osgi.service.zigbee.ZigBeeAttributesHandler;
import org.osgi.service.zigbee.ZigBeeCluster;
import org.osgi.service.zigbee.ZigBeeCommandHandler;
import org.osgi.service.zigbee.ZigBeeException;
import org.osgi.service.zigbee.descriptions.ZigBeeClusterDescription;

/**
 * Mocked impl.
 */
public class ZigBeeClusterImpl implements ZigBeeCluster {

	private Integer						id;
	private ZigBeeAttributeImpl[]		attributes;
	private int[]						commandIds;
	private ZigBeeClusterDescription	description;

	/**
	 * @param id
	 */
	public ZigBeeClusterImpl(Integer id) {
		this.id = id;
	}

	/**
	 * @param commandIds
	 * @param attributes
	 * @param desc
	 */
	public ZigBeeClusterImpl(int[] commandIds, ZigBeeAttributeImpl[] attributes, ZigBeeClusterDescription desc) {
		id = desc.getId();
		this.commandIds = commandIds;
		this.attributes = attributes;
		this.description = desc;
	}

	public Integer getId() {
		return id;
	}

	public ZigBeeAttribute getAttribute(int attributeId) {
		return attributes[attributeId];
	}

	public ZigBeeAttribute[] getAttributes() {
		return attributes;
	}

	public String toString() {
		return description.getGlobalClusterDescription().getClusterName();
	}

	public void readAttributes(int[] attributesIds, ZigBeeAttributesHandler handler) {
		// TODO Auto-generated method stub
		Map<Integer, byte[]> response = new HashMap<Integer, byte[]>();
		int i = 0;
		// for (int i : attributesIds) {
		ZigBeeAttributeImpl attribute = attributes[i];
		byte[] attributeValue = {0};
		response.put(attribute.getId(), attributeValue);
		// }
		handler.onSuccess(response);
	}

	public void writeAttributes(boolean undivided, ZigBeeAttributeRecord[] attributesRecords, ZigBeeAttributesHandler handler) {
		// TODO Auto-generated method stub
	}

	public int[] getCommandIds() {
		return this.commandIds;
	}

	public void invoke(ZCLFrame frame, ZigBeeCommandHandler handler) throws ZigBeeException {
		// mocked invocation.
		handler.notifyResponse(frame);
	}

	public void invoke(ZCLFrame frame, ZigBeeCommandHandler handler, String exportedServicePID) throws ZigBeeException {
		// mocked invocation.
		handler.notifyResponse(frame);
	}

}

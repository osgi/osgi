
package org.osgi.impl.service.zigbee.basedriver;

import java.util.HashMap;
import java.util.Map;
import org.osgi.service.zigbee.ZCLCluster;
import org.osgi.service.zigbee.ZCLFrame;
import org.osgi.service.zigbee.ZigBeeAttribute;
import org.osgi.service.zigbee.ZigBeeAttributeRecord;
import org.osgi.service.zigbee.ZigBeeAttributesHandler;
import org.osgi.service.zigbee.ZigBeeCommandHandler;
import org.osgi.service.zigbee.ZigBeeException;
import org.osgi.service.zigbee.descriptions.ZCLClusterDescription;

/**
 * Mocked impl.
 */
public class ZCLClusterImpl implements ZCLCluster {

	private Integer					id;
	private ZigBeeAttributeImpl[]	attributes;
	private int[]					commandIds;
	private ZCLClusterDescription	description;

	/**
	 * @param id
	 */
	public ZCLClusterImpl(Integer id) {
		this.id = id;
	}

	/**
	 * @param commandIds
	 * @param attributes
	 * @param desc
	 */
	public ZCLClusterImpl(int[] commandIds, ZigBeeAttributeImpl[] attributes, ZCLClusterDescription desc) {
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

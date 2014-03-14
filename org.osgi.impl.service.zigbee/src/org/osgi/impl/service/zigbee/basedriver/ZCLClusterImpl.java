
package org.osgi.impl.service.zigbee.basedriver;

import java.util.HashMap;
import java.util.Map;
import org.osgi.service.zigbee.ZCLCluster;
import org.osgi.service.zigbee.ZCLFrame;
import org.osgi.service.zigbee.ZigBeeAttribute;
import org.osgi.service.zigbee.ZigBeeAttributeRecord;
import org.osgi.service.zigbee.ZigBeeCommandHandler;
import org.osgi.service.zigbee.ZigBeeException;
import org.osgi.service.zigbee.ZigBeeMapHandler;
import org.osgi.service.zigbee.descriptions.ZCLClusterDescription;

/**
 * Mocked impl.
 */
public class ZCLClusterImpl implements ZCLCluster {

	private int						id;
	private ZigBeeAttribute[]		attributes;
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
	public ZCLClusterImpl(int[] commandIds, ZigBeeAttribute[] attributes, ZCLClusterDescription desc) {
		this.id = desc.getId();
		this.commandIds = commandIds;
		this.attributes = attributes;
		this.description = desc;
	}

	public int getId() {
		return id;
	}

	public void getAttribute(int attributeId, ZigBeeMapHandler handler) {
		Map response = new HashMap<String, Object>();
		response.put("Attributes", attributes[attributeId]);
		handler.onSuccess(response);
	}

	public void getAttributes(ZigBeeMapHandler handler) {
		Map response = new HashMap<String, Object>();
		response.put("Attributes", this.attributes);
		handler.onSuccess(response);
	}

	public void getCommandIds(ZigBeeMapHandler handler) {
		Map response = new HashMap<String, Object>();
		response.put("CommandIds", this.commandIds);
		handler.onSuccess(response);
	}

	public void readAttributes(int[] attributesIds, ZigBeeMapHandler handler) {
		// TODO Auto-generated method stub
		Map<Integer, byte[]> response = new HashMap<Integer, byte[]>();
		int i = 0;
		// for (int i : attributesIds) {
		ZigBeeAttribute attribute = attributes[i];
		byte[] attributeValue = {0};
		response.put(attribute.getId(), attributeValue);
		// }
		handler.onSuccess(response);
	}

	public void writeAttributes(boolean undivided, ZigBeeAttributeRecord[] attributesRecords, ZigBeeMapHandler handler) {
		// TODO Auto-generated method stub
	}

	public void invoke(ZCLFrame frame, ZigBeeCommandHandler handler) throws ZigBeeException {
		// mocked invocation.
		handler.notifyResponse(frame);
	}

	public void invoke(ZCLFrame frame, ZigBeeCommandHandler handler, String exportedServicePID) throws ZigBeeException {
		// mocked invocation.
		handler.notifyResponse(frame);
	}

	// public String toString() {
	// return description.getGlobalClusterDescription().getClusterName();
	// }

	public String toString() {
		String attributesAsAString = null;
		if (attributes != null) {
			attributesAsAString = "[";
			int i = 0;
			while (i < attributes.length) {
				attributesAsAString = attributesAsAString + attributes[i].toString();
				i = i + 1;
			}
			attributesAsAString = attributesAsAString + "]";
		}

		String commandIdsAsAString = null;
		if (commandIds != null) {
			commandIdsAsAString = "[";
			int i = 0;
			while (i < commandIds.length) {
				commandIdsAsAString = commandIdsAsAString + commandIds[i];
				i = i + 1;
			}
			commandIdsAsAString = commandIdsAsAString + "]";
		}

		return "" + this.getClass().getName() + "[id: " + id + ", attributes: " + attributesAsAString + ", commandIds: " + commandIdsAsAString + ", description: " + description + "]";
	}

}

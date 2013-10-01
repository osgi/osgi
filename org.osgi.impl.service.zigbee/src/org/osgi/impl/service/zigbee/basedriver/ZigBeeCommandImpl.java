
package org.osgi.impl.service.zigbee.basedriver;

import org.osgi.service.zigbee.ZigBeeCommand;
import org.osgi.service.zigbee.ZigBeeCommandHandler;
import org.osgi.service.zigbee.ZigBeeException;
import org.osgi.service.zigbee.descriptions.ZigBeeCommandDescription;

/**
 * Mocked impl of ZigBeeCommand.
 */
public class ZigBeeCommandImpl implements ZigBeeCommand {

	private int							id;
	private ZigBeeCommandDescription	description;

	/**
	 * @param description
	 */
	public ZigBeeCommandImpl(ZigBeeCommandDescription description) {
		id = description.getId();
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public void invoke(byte[] bytes, ZigBeeCommandHandler handler)
			throws ZigBeeException {
		byte[] response = {};
		handler.onSuccess(response);
	}

	public String toString() {
		return description.getName();
	}

}

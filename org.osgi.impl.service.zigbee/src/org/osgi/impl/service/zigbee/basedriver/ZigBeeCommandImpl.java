
package org.osgi.impl.service.zigbee.basedriver;

import java.util.HashMap;
import java.util.Map;
import org.osgi.service.zigbee.ZigBeeCommand;
import org.osgi.service.zigbee.ZigBeeException;
import org.osgi.service.zigbee.ZigBeeHandler;
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

	public void invoke(byte[] bytes, ZigBeeHandler handler)
			throws ZigBeeException {
		Map response = null;
		response = new HashMap();
		String mockedValue = ("mockedValue");
		response.put(id, mockedValue);
		handler.onSuccess(response);
	}

	// public void invoke(Object[] values, ZigBeeDataTypeDescription[]
	// inputTypes,
	// ZigBeeDataTypeDescription[] outputTypes, ZigBeeHandler handler)
	// throws ZigBeeException {
	// // log.info("command " +name+" invoked...");
	// for (int i = 0; i < values.length; i++) {
	// // log.info(values[i]+" : "+inputTypes[i]);
	// }
	// }

	public String toString() {
		return description.getName();
	}
}

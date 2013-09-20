
package org.osgi.impl.service.zigbee.basedriver;

import java.util.HashMap;
import java.util.Map;
import org.osgi.service.zigbee.ZigBeeCommand;
import org.osgi.service.zigbee.ZigBeeException;
import org.osgi.service.zigbee.ZigBeeHandler;
import org.osgi.service.zigbee.descriptions.ZigBeeCommandDescription;
import org.osgi.service.zigbee.descriptions.ZigBeeDataTypeDescription;

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

	public ZigBeeCommandDescription getDescription() {
		return description;
	}

	public ZigBeeDataTypeDescription[] getInputParametersTypes() {
		ZigBeeDataTypeDescription[] params = null;
		if (description != null) {
			if (description.getParameterDescriptions() != null && description.getParameterDescriptions().length > 0) {
				params = new ZigBeeDataTypeDescription[description.getParameterDescriptions().length];
				for (int i = 0; i < description.getParameterDescriptions().length; i++) {
					params[i] = description.getParameterDescriptions()[i].getDataTypeDescription();
				}
			}
		}
		return params;
	}

	public ZigBeeDataTypeDescription[] getOutputParametersTypes() {
		// TODO Auto-generated method stub
		return null;
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

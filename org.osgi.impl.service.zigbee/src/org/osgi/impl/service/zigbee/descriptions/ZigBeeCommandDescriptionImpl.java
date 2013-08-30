
package org.osgi.impl.service.zigbee.descriptions;

import org.osgi.service.zigbee.descriptions.ZigBeeCommandDescription;
import org.osgi.service.zigbee.descriptions.ZigBeeParameterDescription;

public class ZigBeeCommandDescriptionImpl implements ZigBeeCommandDescription {

	private int								id;
	private String							name;
	private boolean							isMandatory;
	private ZigBeeParameterDescription[]	parametersDesc;

	public ZigBeeCommandDescriptionImpl(int id, String name, boolean mandatory) {
		this.id = id;
		this.name = name;
		this.isMandatory = mandatory;
		parametersDesc = null;
	}

	public ZigBeeCommandDescriptionImpl(int id, String name, boolean mandatory, ZigBeeParameterDescription[] parametersDesc) {
		this.id = id;
		this.name = name;
		this.isMandatory = mandatory;
		this.parametersDesc = parametersDesc;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getShortDescription() {
		return "command description";
	}

	public boolean isMandatory() {
		return isMandatory;
	}

	public ZigBeeParameterDescription[] getParameterDescriptions() {
		return parametersDesc;
	}
}


package org.osgi.test.cases.zigbee.impl;

import org.osgi.service.zigbee.ZCLFrame;
import org.osgi.service.zigbee.ZCLHeader;
import org.osgi.service.zigbee.descriptions.ZCLCommandDescription;
import org.osgi.service.zigbee.descriptions.ZCLParameterDescription;

/**
 * Mocked impl.
 * 
 * @author $Id$
 * 
 */
public class ZCLCommandDescriptionImpl implements ZCLCommandDescription {

	private int							id;
	private String						name;
	private boolean						isMandatory;
	private ZCLParameterDescription[]	parametersDesc;

	/**
	 * @param id
	 * @param name
	 * @param mandatory
	 */
	public ZCLCommandDescriptionImpl(int id, String name, boolean mandatory) {
		this.id = id;
		this.name = name;
		this.isMandatory = mandatory;
		parametersDesc = null;
	}

	/**
	 * @param id
	 * @param name
	 * @param mandatory
	 * @param parametersDesc
	 */
	public ZCLCommandDescriptionImpl(int id, String name, boolean mandatory, ZCLParameterDescription[] parametersDesc) {
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

	public ZCLParameterDescription[] getParameterDescriptions() {
		return parametersDesc;
	}

	public ZCLFrame serialize(ZCLHeader header, Object[] javaValues) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object[] deserialize(ZCLFrame frame) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isClusterSpecificCommand() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getManufacturerCode() {
		// TODO Auto-generated method stub
		return -1;
	}

	public boolean isClientServerDirection() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isManufacturerSpecific() {
		// TODO Auto-generated method stub
		return false;
	}

}

package org.osgi.service.zigbee.description;

/**
 * This interface represents a ZigBee Cluster description
 * 
 * @version 1.0
 */
public interface ZigBeeCommandDescription {
	/**
	 * @return the command identifier
	 */
	public int getId();
	
	/**
	 * @return the command name
	 */
	public String getName();
	
	/**
	 * @return the command functional description 
	 */
	public String getShortDescription();
	
	/**
	 * @return true, if and only if the command is mandatory
	 */
	public boolean isMandatory();
	
	/**
	 * @return an array of command's parameters description
	 */
	public ZigBeeParameterDescription[] getParameterDescriptions();
}

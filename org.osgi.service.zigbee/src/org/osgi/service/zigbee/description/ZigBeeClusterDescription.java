package org.osgi.service.zigbee.description;

/**
 * This interface represents a ZigBee Cluster description
 * 
 * @version 1.0
 */
public interface ZigBeeClusterDescription {
	/**
	 * @return the cluster identifier
	 */
	public int getId();
	
	/**
	 * @return an array of cluster's generated command description
	 */
	public ZigBeeCommandDescription[] getGeneratedCommandDescriptions();
	
	/**
	 * @return an array of cluster's received command description
	 */
	public ZigBeeCommandDescription[] getReceivedCommandDescriptions();
	
	/**
	 * @return an array of cluster's Attributes description
	 */
	public ZigBeeAttributeDescription[] getAttributeDescriptions();
	
	/**
	 * @return an array of cluster's Commands description
	 */
	public ZigBeeGlobalClusterDescription getGlobalClusterDescription();
}

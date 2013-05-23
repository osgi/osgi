package org.osgi.service.zigbee.description;

/**
 * This interface represents Cluster global description  
 * 
 * @version 1.0
 */
public interface ZigBeeGlobalClusterDescription {
	/**
	 * 
	 * @return the cluster identifier
	 */
	public int getClusterId();
	
	/**
	 * 
	 * @return the cluster name
	 */
	public String getClusterName();
	
	/**
	 * 
	 * @return the cluster functional description
	 */
	public String getClusterDescription();
	
	/**
	 * 
	 * @return a {@link ZigBeeClusterDescription} representing the client cluster description
	 */
	public ZigBeeClusterDescription getClientClusterDescription();
	
	/**
	 * 
	 * @return a {@link ZigBeeClusterDescription} representing the server cluster description
	 */
	public ZigBeeClusterDescription getServerClusterDescription();

}

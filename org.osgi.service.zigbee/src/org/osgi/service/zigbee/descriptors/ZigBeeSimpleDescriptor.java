package org.osgi.service.zigbee.descriptors;

/**
 * This interface represents a simple descriptor as described in the ZigBee Specification 
 * The Simple Descriptor contains information specific to each endpoint present in the node.
 * 
 * @version 1.0
 */

public interface ZigBeeSimpleDescriptor {
	/**
	 * @return the application profile id.
	 */
	public int getApplicationProfileId();
	
	/**
	 * @return device id as defined per profile.
	 */
	public int getApplicationDeviceId();
	
	/**
	 * @return the endpoint for which this descriptor is defined.
	 */
	public short getEndpoint();
	
	/**
	 * @return the version of the application.
	 */
	public short getApplicationDeviceVersion();
	
	/**
	 * @return the count of input clusters.
	 */
	public short getApplicationInputClusterCount();
	
	/**
	 * @return the count of output clusters.
	 */
	public short getApplicationOutputClusterCount();
	
	/**
	 * @return list of input cluster identifiers.
	 */
	public int[] getInputClusterList();
	
	/**
	 * @return list of output cluster identifiers.
	 */
	public int[] getOutputClusterList();
	
	/**
	 * @param id
	 * @return true if and only if the endPoint implements the given cluster id as input cluster
	 */
	public boolean providesInputCluster(int id);
	
	/**
	 * @param id the Cluster identifier
	 * @return true if and only if the endPoint implements the given cluster id as output cluster
	 */
	public boolean providesOutputCluster(int id);
}

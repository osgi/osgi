package org.osgi.service.zigbee;

/**
 * This type represents a successful ZDP invocation.
 * 
 * Note that the underlying call may not have succeeded, The ZDPFrame frame must
 * be introspected to identify the response from the {@link ZigBeeNode}.
 *
 */
public interface ZDPResponse {


	/**
	 * Returns the clusterId this response refers to.
	 * 
	 * @return the clusterId this response refers to.
	 */
	int getClusterId();

	/**
	 * Returns the {@link ZDPFrame} containing the response.
	 * 
	 * @return the {@link ZDPFrame} containing the response.
	 */
	ZDPFrame getFrame();

}

package org.osgi.service.zigbee.handler;

import java.util.Map;

/**
 * ZigBeeHandler manages response of a request to the Base Driver
 * 
 * @version 1.0
 */

public interface ZigBeeHandler {
	/**
	 * Notifies the request response
	 * @param status request response status : SUCCESS or FAILURE
	 * @param response type is Map<int, Object>.
	 */
	public void notifyResponse(short status, Map response);
}

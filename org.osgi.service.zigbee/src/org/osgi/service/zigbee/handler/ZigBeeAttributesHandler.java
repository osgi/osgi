package org.osgi.service.zigbee.handler;

/**
 * ZigBeeAttributesHandler manages response of a read/write operation of 
 * of attributes groups
 * 
 * @version 1.0
 */

import java.util.Map;

public interface ZigBeeAttributesHandler {
	/**
	 * Notifies the request response
	 * @param status request response status : SUCCESS or FAILURE
	 * @param response A Map<int, Object>,  <i>int</i> represents the attribute identifier and <i>Object</i> represents the attribute value.
	 */
	void notifyResponse(short status, Map response);
}

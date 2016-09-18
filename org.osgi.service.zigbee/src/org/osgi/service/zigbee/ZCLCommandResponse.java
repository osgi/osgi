package org.osgi.service.zigbee;

import org.osgi.util.promise.Promise;

/**
 * A response event for a {@link ZCLCommandResponseStream}.
 */
public interface ZCLCommandResponse {

	/**
	 * Returns a promise holding the response.
	 * 
	 * @return A Promise holding the {@link ZCLFrame} response, or a failure
	 *         exception if this is not a success response.
	 */
	Promise /* <ZCLFrame> */ getResponse();
	
	/**
	 * Checks if this is a terminal close event.
	 * 
	 * @return <code>true</code> if this is a terminal close event.
	 */
	boolean isEnd();
}

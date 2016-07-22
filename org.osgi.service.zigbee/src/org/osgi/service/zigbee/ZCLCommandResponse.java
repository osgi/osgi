package org.osgi.service.zigbee;

/**
 * A response event for a {@link ZCLCommandMultiResponse}
 */
public interface ZCLCommandResponse {

	/**
	 * An event representing a failure will have an exception included.
	 * 
	 * @return A failure, or <code>null</code> if there was no failure
	 */
	Exception getFailure();
	
	/**
	 * An event representing a successful response from a ZigBee commmand
	 * 
	 * @return A {@link ZCLFrame} response, or <code>null</code> if this is not
	 *         a success response
	 */
	ZCLFrame getResponse();
	
	/**
	 * @return <code>true</code> if this is a terminal close event
	 */
	boolean isEnd();
}

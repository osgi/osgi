package org.osgi.service.enocean;

/**
 * The interface used to get callback answers from a RPC or a Message.
 * 
 * @author $Id$
 */
public interface EnOceanResponseHandler {
	
	/**
	 * Notifies of the answer to a Message.
	 * 
	 * @param original the original {@link EnOceanMessage} that originated this answer.
	 * @param payload the payload of the response; may be deserialiazed to an {@link EnOceanMessage} object.
	 */
	public void notifyResponse(EnOceanMessage original, byte[] payload);
	
	/**
	 * Notifies of the answer to a RPC.
	 * 
	 * @param original the original {@link EnOceanRPC} that originated this answer.
	 * @param payload the payload of the response; may be deserialiazed to an {@link EnOceanRPC} object.
	 */
	public void notifyResponse(EnOceanRPC original, byte[] payload);

}

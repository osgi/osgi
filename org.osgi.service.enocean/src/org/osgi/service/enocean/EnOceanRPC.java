package org.osgi.service.enocean;

/**
 * An interface to call RPCs on remote devices.
 * 
 * @author $Id$
 */
public interface EnOceanRPC {
	
	/**
	 * Property name for the manufacturer code of the RPC, mandatory
	 */
	public final static String MANUFACTURER_CODE = "enocean.device.rpc.manufacturer_code";
	
	/**
	 * Property name for the function code of the RPC, mandatory
	 */
	public final static String FUNCTION_CODE = "enocean.device.rpc.function_code";
	
	/**
	 * Get the sender ID of this RPC.
	 * 
	 * @return the sender ID of the RPC
	 */
	public int getSenderId();
	
	/**
	 * Sets the sender ID of this RPC.
	 * 
	 * @param senderId the sender ID to be set
	 */
	public void setSenderId(int senderId);
	
	/**
	 * Gets the current destination for this RPC.
	 * 
	 * @return the destination ID of the RPC
	 */
	public int getDestinationId();
	
	/**
	 * Gets the current payload of the RPC.
	 * 
	 * @return the payload, in bytes, of this RPC.
	 */
	public byte[] getPayload();
	
	/**
	 * Invoke the RPC with the specified argument payload.
	 * 
	 * @param destinationId the CHIP_ID of the destination device, of 0xFFFFFFFF (broadcast)
	 * @param payload the byte[] payload for this message;
	 * @param an optional {@link EnOceanResponseHandler} object.
	 */
	public void invoke(int destinationId, byte[] payload, EnOceanResponseHandler handler) throws EnOceanException;
	
}

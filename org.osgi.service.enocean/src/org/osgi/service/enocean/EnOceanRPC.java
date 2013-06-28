package org.osgi.service.enocean;

/**
 * 
 *
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
	 * @return the sender ID of the RPC
	 */
	public int getSenderId();
	
	/**
	 * @param senderId the sender ID to be set
	 */
	public void setSenderId(int senderId);
	
	/**
	 * @return the destination ID of the RPC
	 */
	public int getDestinationId();
	
	/**
	 * @param senderId the destination ID for this RPC.
	 */
	public void setDestinationId(int destinationId);
	
	/**
	 * @return the payload, in bytes, of this RPC.
	 */
	public byte[] getPayload();

	/**
	 * @param payload the byte payload for this RPC.
	 */
	public void setPayload(byte[] payload);
	
	/**
	 * Invoke the RPC with the specified argument payload.
	 */
	public void invoke(EnOceanRPCHandler handler);
	
}

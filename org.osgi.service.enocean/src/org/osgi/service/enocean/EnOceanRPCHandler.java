package org.osgi.service.enocean;

/**
 * 
 *
 * The interface used to get callback answers from a RPC, if any.
 * 
 * @author $Id$
 */
public interface EnOceanRPCHandler {
	
	/**
	 * Notifies the user of any answer that may be fired by the RPC (lightweight version)
	 * @param chipId
	 * @param manufId
	 * @param functionId
	 * @param payload
	 */
	public void notifyAnswer(int chipId, int manufId, int functionId, byte[] payload);
	
	/**
	 * Notifies the user of any answer that may be fired by the RPC ({@link EnOceanRPC} version)
	 * @param answer The answer under an EnOceanRPC object form.
	 */
	public void notifyAnswer(EnOceanRPC answer);

}

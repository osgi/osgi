package org.osgi.service.enocean;

public interface EnOceanRPCHandler {
	
	/**
	 * 
	 * @param chipId
	 * @param manufId
	 * @param functionId
	 * @param payload
	 */
	public void notifyAnswer(int chipId, int manufId, int functionId, byte[] payload);
	
	/**
	 * @param answer The answer under an EnOceanRPC object form.
	 */
	public void notifyAnswer(EnOceanRPC answer);

}

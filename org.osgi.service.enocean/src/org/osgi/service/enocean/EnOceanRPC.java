/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.osgi.service.enocean;

/**
 * A very basic interface for RPCs. 
 * 
 * @version 1.0
 * @author Victor Perron <victor.perron@orange.fr>
 */
public interface EnOceanRPC {
	
	/**
	 * The Manufacturer ID property string, used in EventAdmin RPC broadcasting.
	 */
	public static final String MANUFACTURER_ID = "enocean.rpc.manufacturer_id";

	/**
	 * The Function ID property string, used in EventAdmin RPC broadcasting.
	 */
	public static final String FUNCTION_ID = "enocean.rpc.function_id";
			
	/**
	 * Gets the manufacturerID for this RPC.
	 * 
	 * @return
	 */
	public int getManufacturerId();
	
	/**
	 * Gets the functionID for this RPC. 
	 * 
	 * @return
	 */
	public int getFunctionId();
	
	/**
	 * Gets the current payload of the RPC.
	 * 
	 * @return the payload, in bytes, of this RPC.
	 */
	public byte[] getPayload();
	
	/**
	 * Sets the current payload of the RPC.
	 * 
	 * @param the payload, in bytes, of this RPC.
	 */
	public void setPayload(byte[] data);

	/**
	 * Sets the RPC's senderID.
	 * 
	 * This member has to belong to {@link EnOceanRPC} interface, for the object may be sent as a standalone using EventAdmin for instance.
	 * @return
	 */
	public int getSenderId();
	
	/**
	 * Sets the RPC's senderID.
	 * @return
	 */
	public void setSenderId(int chipId);
	
}

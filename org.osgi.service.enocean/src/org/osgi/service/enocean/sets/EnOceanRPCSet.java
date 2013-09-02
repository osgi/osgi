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


package org.osgi.service.enocean.sets;

import org.osgi.service.enocean.EnOceanRPC;



/**
 * This interface represents an EnOcean RPC Set.
 * {@link EnOceanRPCSet} is registered as an OSGi Service.
 * Provides a method to retrieve the {@link EnOceanRPC} objects
 * it documents.
 * 
 * @version 1.0
 * @author Victor Perron <victor.perron@orange.fr>
 */
public interface EnOceanRPCSet {
	
	public final static String	VERSION				= "rpc.set.version";

	public final static String	PROVIDER_ID			= "rpc.set.provider_id";

	/**
	 * Retrieves a {@link EnOceanRPC} object according to its identifier.
	 * 
	 * @param id the unique string identifier of the description object.
	 * @return The corresponding {@link EnOceanRPC} object, or null.
	 */
	public EnOceanRPC getRPC(short manufacturerId, short commandID);

}
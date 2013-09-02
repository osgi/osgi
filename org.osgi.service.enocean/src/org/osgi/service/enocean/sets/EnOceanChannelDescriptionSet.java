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

import org.osgi.service.enocean.channels.EnOceanChannelDescription;


/**
 * This interface represents an EnOcean Channel Description Set.
 * {@link EnOceanChannelDescriptionSet} is registered as an OSGi Service.
 * Provides a method to retrieve the {@link EnOceanChannelDescription} objects
 * it documents.
 * 
 * @version 1.0
 * @author Victor Perron <victor.perron@orange.fr>
 */
public interface EnOceanChannelDescriptionSet {
	
	public final static String	VERSION				= "channel.description.set.version";

	public final static String	PROVIDER_ID			= "channel.description.set.provider_id";

	/**
	 * Retrieves a {@link EnOceanChannelDescription} object according to its identifier.
	 * 
	 * @param id the unique string identifier of the description object.
	 * @return The corresponding {@link EnOceanChannelDescription} object, or null.
	 */
	public EnOceanChannelDescription getChannelDescription(short rorg, short func, short type, short extra);

}
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


package org.osgi.service.enocean.channels;


/**
 * Subinterface of {@link EnOceanChannelDescription} that describes physical measuring channels.
 * 
 * @version 1.0
 * @author Victor Perron <victor.perron@orange.fr>
 */
public interface EnOceanDataChannelDescription extends EnOceanChannelDescription {
	
	/**
	 * The start of the raw input range for this channel.
	 * @return
	 */
	public int getDomainStart();
	
	/**
	 * The end of the raw input range for this channel.
	 * @return
	 */
	public int getDomainStop();
	
	/**
	 * The scale start at which this channel will be mapped to (-20,0°C for instance) 
	 * @return
	 */
	public double getRangeStart();
	
	/**
	 * The scale stop at which this channel will be mapped to (+30,0°C for instance) 
	 * @return
	 */
	public double getRangeStop();	
	
	/**
	 * The non-mandatory physical unit description of this channel.
	 * @return
	 */
	public String getUnit();
	
}

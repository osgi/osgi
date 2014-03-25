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

package org.osgi.service.zigbee;

/**
 * This interface represents a ZCL Header.
 * 
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano "Kismet" Lenzi</a>
 * @author <a href="mailto:francesco.furfari@isti.cnr.it">Francesco Furfari</a>
 */
public interface ZCLHeader {

	/**
	 * Get this ZCLHeader's command id
	 * 
	 * @return the commandId
	 */
	int getCommandId();

	/**
	 * Get manufacturerCode
	 * 
	 * Default value is: -1 (no code)
	 * 
	 * @return the manufacturerCode
	 */
	int getManufacturerCode();

	/**
	 * @return the isClusterSpecificCommand value
	 */
	boolean isClusterSpecificCommand();

	/**
	 * @return the isManufacturerSpecific value
	 */
	boolean isManufacturerSpecific();

	/**
	 * @return the isClientServerDirection value
	 */
	boolean isClientServerDirection();

	/**
	 * @return the isDefaultResponseEnabled value
	 */
	boolean isDefaultResponseEnabled();

}

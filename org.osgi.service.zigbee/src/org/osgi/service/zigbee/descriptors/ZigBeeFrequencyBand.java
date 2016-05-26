/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
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

package org.osgi.service.zigbee.descriptors;

/**
 * 
 *
 * TODO Add Javadoc comment for this type.
 * 
 * @author $Id$
 */
public interface ZigBeeFrequencyBand {

	/**
	 * @return {@code true} if and only if the radio is operating in the
	 *         frequency band 868 to 868.6 MHz
	 */
	public boolean is868();

	/**
	 * @return {@code true} if and only if the radio is operating in the
	 *         frequency band 908Mhz to 928Mhz
	 */
	public boolean is915();

	/**
	 * @return {@code true} if and only if the radio is operating in the
	 *         frequency band 2400Mhz to 2483Mhz
	 */
	public boolean is2400();
}

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
 * This interface represents a the frequency band field.
 * 
 * @noimplement
 * 
 * @author $Id$
 */
public interface ZigBeeFrequencyBand {

	/**
	 * Checks if the radio band is 868MHz.
	 * 
	 * @return {@code true} if and only if the radio is operating in the
	 *         frequency band 868 to 868.6 MHz.
	 */
	public boolean is868();

	/**
	 * Checks if the radio band is 900MHz.
	 * 
	 * @return {@code true} if and only if the radio is operating in the
	 *         frequency band 908MHz to 928MHz.
	 */
	public boolean is915();

	/**
	 * Checks if the radio band is 2.4GHz.
	 * 
	 * @return {@code true} if and only if the radio is operating in the
	 *         frequency band 2400MHz to 2483MHz.
	 */
	public boolean is2400();
}

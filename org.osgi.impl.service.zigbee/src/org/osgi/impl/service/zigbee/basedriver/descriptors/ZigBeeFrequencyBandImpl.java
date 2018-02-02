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

package org.osgi.impl.service.zigbee.basedriver.descriptors;

import org.osgi.service.zigbee.descriptors.ZigBeeFrequencyBand;

/**
 * Implementation of the FrequencyBand field.
 * 
 * @author $Id$
 */
public class ZigBeeFrequencyBandImpl implements ZigBeeFrequencyBand {

	private int value;

	public ZigBeeFrequencyBandImpl(short band) {
		this.value = band;
	}

	public boolean is868() {
		return value == 868;
	}

	public boolean is915() {
		return value == 915;
	}

	public boolean is2400() {
		return value == 2400;
	}
}

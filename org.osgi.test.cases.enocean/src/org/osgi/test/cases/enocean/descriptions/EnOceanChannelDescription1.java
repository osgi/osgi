/*
 * Copyright (c) OSGi Alliance (2014, 2017). All Rights Reserved.
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

package org.osgi.test.cases.enocean.descriptions;

import org.osgi.service.enocean.descriptions.EnOceanChannelDescription;
import org.osgi.service.enocean.descriptions.EnOceanChannelEnumValue;
import org.osgi.service.enocean.descriptions.EnOceanEnumChannelDescription;
import org.osgi.test.cases.enocean.descriptions.enumvalues.EnOceanChannelEnumValue0;
import org.osgi.test.cases.enocean.descriptions.enumvalues.EnOceanChannelEnumValue1;
import org.osgi.test.cases.enocean.descriptions.enumvalues.EnOceanChannelEnumValue2;
import org.osgi.test.cases.enocean.descriptions.enumvalues.EnOceanChannelEnumValue3;

/**
 * @author $Id$
 */
public class EnOceanChannelDescription1 implements EnOceanEnumChannelDescription {

	EnOceanChannelEnumValue[]	possibleValues	= new EnOceanChannelEnumValue[] {
												new EnOceanChannelEnumValue0(),
												new EnOceanChannelEnumValue1(),
												new EnOceanChannelEnumValue2(),
												new EnOceanChannelEnumValue3()
												};

	public String getType() {
		return EnOceanChannelDescription.TYPE_ENUM;
	}

	public byte[] serialize(Object obj) throws IllegalArgumentException {
		int state = ((Integer) obj).intValue();
		return new byte[] {(byte) (state & 0x7)};
	}

	public Object deserialize(byte[] bytes) throws IllegalArgumentException {
		if ((bytes == null) || bytes.length != 1)
			throw new IllegalArgumentException();
		return Integer.valueOf((bytes[0] & 0x07));
	}

	public EnOceanChannelEnumValue[] getPossibleValues() {
		return possibleValues;
	}

}

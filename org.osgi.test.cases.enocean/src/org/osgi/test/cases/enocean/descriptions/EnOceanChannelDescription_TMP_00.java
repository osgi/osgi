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

package org.osgi.test.cases.enocean.descriptions;

import org.osgi.service.enocean.descriptions.EnOceanChannelDescription;
import org.osgi.service.enocean.descriptions.EnOceanDataChannelDescription;

/**
 *
 */
public class EnOceanChannelDescription_TMP_00 implements EnOceanDataChannelDescription {

	public String getType() {
		return EnOceanChannelDescription.TYPE_DATA;
	}

	private float scale(int x) {
		// y = a*x + b where...
		float denominator = (getDomainStop() - getDomainStart());
		float numerator_a = (float) (getRangeStop() - getRangeStart());
		float numerator_b = (float) (getRangeStart() * getDomainStop() - getRangeStop() * getDomainStart());
		float a = numerator_a / denominator;
		float b = numerator_b / denominator;
		return a * x + b;
	}

	private int unscale(float y) {
		// x = A*y + B where A = 1/a and B = -b/a, so...
		float denominator = (float) (getRangeStop() - getRangeStart());
		float numerator_A = getDomainStop() - getDomainStart();
		float numerator_B = (float) (getRangeStop() * getDomainStart() - getRangeStart() * getDomainStop());
		float A = numerator_A / denominator;
		float B = numerator_B / denominator;
		return Math.round(A * y + B);
	}

	public byte[] serialize(Object obj) throws IllegalArgumentException {
		float value;
		if (obj == null) {
			throw new IllegalArgumentException("Supplied object was NULL");
		}
		try {
			Float valueObj = (Float) obj;
			value = valueObj.floatValue();
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("Invalid input in channel description");
		}
		if (value < getRangeStart() || value > getRangeStop()) {
			throw new IllegalArgumentException("Supplied value out of range");
		}
		int input = unscale(value);
		return new byte[] {(byte) input};
	}

	public Object deserialize(byte[] bytes) throws IllegalArgumentException {
		if (bytes == null) {
			throw new IllegalArgumentException("Supplied array was NULL");
		}
		if (bytes.length != 1)
			throw new IllegalArgumentException("Input was invalid, too many bytes");
		byte b = bytes[0];
		int input = b;
		if (input < getDomainStart() && input > getDomainStop()) {
			throw new IllegalArgumentException("Supplied value out of input domain");
		}
		Float output = new Float(scale(input));
		return output;
	}

	public int getDomainStart() {
		return 0;
	}

	public int getDomainStop() {
		return 255;
	}

	public double getRangeStart() {
		return -40.0f;
	}

	public double getRangeStop() {
		return 0.0f;
	}

	public String getUnit() {
		return "°C";
	}

}

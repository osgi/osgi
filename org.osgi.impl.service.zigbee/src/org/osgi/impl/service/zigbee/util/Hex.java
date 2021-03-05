/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.impl.service.zigbee.util;

import java.math.BigInteger;

/**
 * Utility for printing and parsing hex numbers.
 * 
 * @author $Id$
 */
public class Hex {
	public static String toHexString(BigInteger number, int hexDigits) {
		String s = number.toString(16);
		if (s.length() < hexDigits) {
			int length = s.length();
			for (int i = 0; i < (hexDigits - length); i++) {
				s = "0" + s;
			}
		}
		return s;
	}

	public static String toHexString(int number, int hexDigits) {
		String s = Integer.toHexString(number);
		if (s.length() < hexDigits) {
			int length = s.length();
			for (int i = 0; i < (hexDigits - length); i++) {
				s = "0" + s;
			}
		}
		return s;
	}
}

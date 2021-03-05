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

package org.osgi.test.cases.enocean.channels;

import org.osgi.service.enocean.EnOceanChannel;

/**
 * @author $Id$
 */
public class LChannel implements EnOceanChannel {

	private boolean	isLearn;

	public String getChannelId() {
		return "LRN_4BS";
	}

	public void setRawValue(byte[] rawValue) {
		isLearn = rawValue[0] == 0;
	}

	public int getSize() {
		return 1;
	}

	public byte[] getRawValue() {
		if (isLearn) {
			return new byte[] {0x0};
		} else {
			return new byte[] {0x1};
		}
	}

	public int getOffset() {
		return 28;
	}

}

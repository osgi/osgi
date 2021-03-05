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

/* REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 26/01/2005   Andre Assad
 * 1         	Implement TCK
 * ===========  ==============================================================
*/

package org.osgi.test.cases.dmt.tc1.tbc.DmtData;

import org.osgi.service.dmt.DmtData;

import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;

/**
 * This Test Case Validates the values of all constants> of DmtData, according to MEG specification
 */
public class DmtDataConstants extends DmtTestControl {
	/**
	 * This method asserts the constants values
	 * 
	 * @spec 117.12.5
	 */
	public void testConstants001() {
		log("#testConstants001");
		assertEquals("Asserting FORMAT_BASE64 value", 128,
				DmtData.FORMAT_BASE64);
		assertEquals("Asserting FORMAT_BINARY value", 64, DmtData.FORMAT_BINARY);
		assertEquals("Asserting FORMAT_BOOLEAN value", 8,
				DmtData.FORMAT_BOOLEAN);
		assertEquals("Asserting FORMAT_DATE value", 16, DmtData.FORMAT_DATE);
		assertEquals("Asserting FORMAT_FLOAT value", 2, DmtData.FORMAT_FLOAT);
		assertEquals("Asserting FORMAT_INTEGER value", 1,
				DmtData.FORMAT_INTEGER);
		assertEquals("Asserting FORMAT_NODE value", 1024, DmtData.FORMAT_NODE);
		assertEquals("Asserting FORMAT_NULL value", 512, DmtData.FORMAT_NULL);
		assertEquals("Asserting FORMAT_STRING value", 4, DmtData.FORMAT_STRING);
		assertEquals("Asserting FORMAT_XML value", 256, DmtData.FORMAT_XML);
		assertEquals("Asserting FORMAT_RAW_BINARY value", 4096,
				DmtData.FORMAT_RAW_BINARY);
		assertEquals("Asserting FORMAT_RAW_STRING value", 2048,
				DmtData.FORMAT_RAW_STRING);

	}
}

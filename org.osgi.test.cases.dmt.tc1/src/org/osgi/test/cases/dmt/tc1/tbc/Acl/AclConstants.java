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

/* 
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Jan 31, 2005  Andre Assad
 * 1             Implement MEG TCK
 * ============  ==============================================================
 * Feb 11, 2005  Andre Assad
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc1.tbc.Acl;

import org.osgi.service.dmt.Acl;

import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;

/**
 * @author Andre Assad
 *
 * This test case validates all <code>constants</code> of Acl, according to MEG specification
 * 
 */
public class AclConstants extends DmtTestControl {

	/**
	 * Tests all constants values according to the specified values
	 */
	public void testConstants001() {
		log("#testConstants001");
		assertEquals("Asserting GET value", 1, Acl.GET);
		assertEquals("Asserting ADD value", 2, Acl.ADD);
		assertEquals("Asserting REPLACE value", 4, Acl.REPLACE);
		assertEquals("Asserting DELETE value", 8, Acl.DELETE);
		assertEquals("Asserting EXEC value", 16, Acl.EXEC);
		assertEquals("Asserting ALL_PERMISSION 	 value", 31, Acl.ALL_PERMISSION);
	}
}

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
 * May 25, 2005  Luiz Felipe Guimaraes
 * 1             Implement MEG TCK
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc1.tbc.Acl;

import org.osgi.service.dmt.Acl;

import org.osgi.test.cases.dmt.tc1.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;
/**
 * @author Luiz Felipe Guimaraes
 * 
 * This test case validates the implementation of <code>hashcode<code> method of Acl, 
 * according to MEG specification
 */
public class Hashcode extends DmtTestControl {
	/**
	 * Asserts that two Acl have the same hashcode when they are equals
	 * 
	 * @spec Acl.hashcode()
	 */
	public void testHashcode001() {
		try {
			log("#testHashcode001");
			String[] principals = { DmtConstants.PRINCIPAL, DmtConstants.PRINCIPAL_2 } ;
			int[] permissions = { Acl.ADD, Acl.GET };
			Acl Acl = new Acl(principals,permissions);
			Acl Acl2 = new Acl(principals,permissions);
			assertTrue(
					"Asserts that two Acl have the same hashcode when they are equals",
					Acl.hashCode() == Acl2.hashCode());
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

}

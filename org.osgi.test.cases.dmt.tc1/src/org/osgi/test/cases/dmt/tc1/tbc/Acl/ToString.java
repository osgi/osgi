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
 * Feb 14, 2005  Andre Assad
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc1.tbc.Acl;

import org.osgi.service.dmt.Acl;

import org.osgi.test.cases.dmt.tc1.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * This test case validates the implementation of <code>toString<code> method of Acl, 
 * according to MEG reference specification.
 */
public class ToString extends DmtTestControl {

	private Acl Acl;


	/**
	 * This method asserts that the operations are in the following order: 
	 * (Add, Delete, Exec, Get, Replace), that principal names are sorted 
	 * alphabetically and that all of the principal are returned
	 * 
	 * @spec Acl.toString()
	 */
	public void testToString001() {
		try {

			log("#testToString001");

			String canonic = "Exec=" + DmtConstants.PRINCIPAL + "&Add="
					+ DmtConstants.PRINCIPAL_2 + "&Delete="
					+ DmtConstants.PRINCIPAL + "&Replace="
					+ DmtConstants.PRINCIPAL + "&Get="
					+ DmtConstants.PRINCIPAL + "&Add="
					+ DmtConstants.PRINCIPAL;
			Acl = new org.osgi.service.dmt.Acl(canonic);

			String result = Acl.toString();

			assertEquals("Asserts toString returned value", "Add="
					+ DmtConstants.PRINCIPAL + "+"+ DmtConstants.PRINCIPAL_2 +"&Delete="
					+ DmtConstants.PRINCIPAL + "&Exec="
					+ DmtConstants.PRINCIPAL + "&Get="
					+ DmtConstants.PRINCIPAL + "&Replace="
					+ DmtConstants.PRINCIPAL, result);

		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * This method asserts that the operations are in the following order: (Add, Get), that principal 
	 * names are sorted alphabetically and that all of the principal are returned
	 * 
	 * @spec Acl.toString()
	 */
	public void testToString002() {
		try {
			log("#testToString002");

			String canonic = "Get=" + DmtConstants.PRINCIPAL + "&Add=*&Get="
					+ DmtConstants.PRINCIPAL_2;
			Acl = new org.osgi.service.dmt.Acl(canonic);

			String result = Acl.toString();

			assertEquals("Asserts toString returned value", "Add=*"
					+ "&Get=" + DmtConstants.PRINCIPAL + "+"
					+ DmtConstants.PRINCIPAL_2, result);

		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}
}

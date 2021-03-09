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
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * Jun 27, 2005 Leonardo Barros
 * 34           Implement MEGTCK for the application RFC 
 * ===========  ==============================================================
 */
package org.osgi.test.cases.dmt.tc1.tbc.DmtPrincipalPermission;

import org.osgi.service.dmt.security.DmtPrincipalPermission;

import org.osgi.test.cases.dmt.tc1.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;

/**
 * This test ccase validates the implementation of <code>hashCode</code> method of DmtPrincipalPermission, 
 * according to MEG specification
 */
public class HashCode extends DmtTestControl {
	/**
	 * Assert if hashCodes are the same for two DmtPrincipalPermission instances that are equal
	 * 
	 * @spec DmtPrincipalPermission.hashCode()
	 */
	public void testHashCode001() {
		try {
			log("#testHashCode001");

			DmtPrincipalPermission d1 = new DmtPrincipalPermission("*");
			DmtPrincipalPermission d2 = new DmtPrincipalPermission("*");

			assertTrue(
					"Assert if hashCodes are the same for two DmtPrincipalPermission instances that are equal",
					d1.hashCode()==d2.hashCode());

		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}
	
	/**
	 * Assert if hashCodes are the same for two DmtPrincipalPermission instances that are equal
     * using the constructor with two parameters
	 * 
	 * @spec DmtPrincipalPermission.hashCode()
	 */
	public void testHashCode002() {
		try {
			log("#testHashCode002");

			DmtPrincipalPermission d1 = new DmtPrincipalPermission(DmtConstants.PRINCIPAL,"*");
			DmtPrincipalPermission d2 = new DmtPrincipalPermission(DmtConstants.PRINCIPAL,"*");

			assertTrue(
					"Assert if hashCodes are the same for two DmtPrincipalPermission instances that are equal",
					d1.hashCode()==d2.hashCode());

		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

}

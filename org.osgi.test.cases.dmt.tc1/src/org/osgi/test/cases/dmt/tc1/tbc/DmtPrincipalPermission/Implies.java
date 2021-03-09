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
 * This test case validates the implementation of <code>implies</code> method of DmtPrincipalPermission, 
 * according to MEG specification
 */
public class Implies extends DmtTestControl {
	/**
	 * This method asserts that an object implies other object using the same target
	 * 
	 * @spec DmtPrincipalPermission.implies(Permission)
	 */
	public void testImplies001() {
		try {
			log("#testImplies001");
			DmtPrincipalPermission d1 = new DmtPrincipalPermission("*");
			DmtPrincipalPermission d2 = new DmtPrincipalPermission("*");

			assertTrue(
							"Asserts that an object implies other object using the same target",
							d1.implies(d2));

		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * This method asserts that an object implies other object using the same target and actions
	 * 
	 * @spec DmtPrincipalPermission.implies(Permission)
	 */
	public void testImplies002() {
		try {
			log("#testImplies002");

			DmtPrincipalPermission d1 = new DmtPrincipalPermission(
					DmtConstants.PRINCIPAL, "*");
			DmtPrincipalPermission d2 = new DmtPrincipalPermission(
					DmtConstants.PRINCIPAL, "*");

			assertTrue(
							"Asserts that an object implies other object using the same target and actions",
							d1.implies(d2));

		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * This method asserts that an object does not imply other object using the different target and actions
	 * 
	 * @spec DmtPrincipalPermission.implies(Permission)
	 */
	public void testImplies003() {
		try {
			log("#testImplies003");

			DmtPrincipalPermission d1 = new DmtPrincipalPermission(
					DmtConstants.PRINCIPAL, "*");
			DmtPrincipalPermission d2 = new DmtPrincipalPermission("*", "*");

			assertTrue(
							"Asserts that an object does not imply other object using the different target and actions",
							!d1.implies(d2));

		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

    /**
     * Asserts that an object implies other object using a wildcard
     * 
     * @spec DmtPrincipalPermission.implies(Permission)
     */
    public void testImplies004() {
        try {
            log("#testImplies004");


            DmtPrincipalPermission d1 = new DmtPrincipalPermission(DmtConstants.PRINCIPAL + "*", "*");
            DmtPrincipalPermission d2 = new DmtPrincipalPermission(
                DmtConstants.PRINCIPAL +"a", "*");

            assertTrue(
                            "Asserts that an object implies other object using a wildcard",
                            d1.implies(d2));

        } catch (Exception e) {
            failUnexpectedException(e);
        }
    }
}

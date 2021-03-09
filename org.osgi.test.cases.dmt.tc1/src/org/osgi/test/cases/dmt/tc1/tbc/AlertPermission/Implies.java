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
 * Jul 12, 2005 Luiz Felipe Guimaraes
 * 1            Implement MEGTCK  
 * ===========  ==============================================================
 */
package org.osgi.test.cases.dmt.tc1.tbc.AlertPermission;

import org.osgi.service.dmt.security.AlertPermission;

import org.osgi.test.cases.dmt.tc1.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;

/**
 * This test case validates the implementation of <code>implies<code> method of AlertPermission, 
 * according to MEG specification
 */
public class Implies extends DmtTestControl {

	/**
	 * This method asserts that an object implies other object using the same target
	 * 
	 * @spec AlertPermission.implies(Permission)
	 */
	public void testImplies001() {
		try {
			log("#testImplies001");

			AlertPermission d1 = new AlertPermission(DmtConstants.REMOTE_SERVER);
			AlertPermission d2 = new AlertPermission(DmtConstants.REMOTE_SERVER);

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
	 * @spec AlertPermission.implies(Permission)
	 */
	public void testImplies002() {
		try {
			log("#testImplies002");

			AlertPermission d1 = new AlertPermission(
					DmtConstants.REMOTE_SERVER, "*");
			AlertPermission d2 = new AlertPermission(
					DmtConstants.REMOTE_SERVER, "*");

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
	 * @spec AlertPermission.implies(Permission)
	 */
	public void testImplies003() {
		try {
			log("#testImplies003");

			AlertPermission d1 = new AlertPermission(
					DmtConstants.REMOTE_SERVER, "*");
			AlertPermission d2 = new AlertPermission(DmtConstants.REMOTE_SERVER + "a", "*");

			assertTrue(
							"Asserts that an object does not imply other object using the different target and actions",
							!d1.implies(d2));

		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}
    
    /**
     * Asserts that an object implies other object using the '*' wildcard
     * 
     * @spec AlertPermission.implies(Permission)
     */
    public void testImplies004() {
        try {
            log("#testImplies004");

            AlertPermission d1 = new AlertPermission(
                    DmtConstants.REMOTE_SERVER+"*", "*");
            AlertPermission d2 = new AlertPermission(DmtConstants.REMOTE_SERVER + "a", "*");

            assertTrue(
                            "Asserts that an object implies other object using the '*' wildcard",
                            d1.implies(d2));

        } catch (Exception e) {
            failUnexpectedException(e);
        }
    }

}

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
 * Abr 04, 2005  Luiz Felipe Guimaraes
 * 13            [MEGTCK][DMT] CVS update 
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc1.tbc.DmtPermission;

import org.osgi.service.dmt.security.AlertPermission;
import org.osgi.service.dmt.security.DmtPermission;

import org.osgi.test.cases.dmt.tc1.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;

/**
 * This test case validates the implementation of <code>implies</code> method of DmtPermission, 
 * according to MEG specification
 */
public class Implies extends DmtTestControl {
	/**
	 * This method asserts that an object implies other object using the same uri actions
	 * 
	 * @spec DmtPermission.implies(Permission)
	 */
	public void testImplies001() {
		try {
			log("#testImplies001");
			DmtPermission permission = new DmtPermission(
					DmtConstants.OSGi_LOG, DmtConstants.ACTIONS);
			DmtPermission permission2 = new DmtPermission(
					DmtConstants.OSGi_LOG, DmtConstants.ACTIONS);
			assertTrue(
							"Asserts that an object implies other object using the same uri and actions",
							permission.implies(permission2));
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * Asserts that an object implies other object using the same uri and the actions it contains
	 * 
	 * @spec DmtPermission.implies(Permission)
	 */
	public void testImplies002() {
		try {
			log("#testImplies002");
			DmtPermission permission = new DmtPermission(
					DmtConstants.OSGi_LOG, DmtPermission.ADD + ","
							+ DmtPermission.GET);
			DmtPermission permission2 = new DmtPermission(
					DmtConstants.OSGi_LOG, DmtPermission.ADD);
			assertTrue(
							"Asserts that an object implies other object using the same uri and the actions it contains",
							permission.implies(permission2));
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * Asserts that an object doesn't imply other object using 
	 * the same uri and the actions it doesn't contain
	 * 
	 * @spec DmtPermission.implies(Permission)
	 */
	public void testImplies003() {
		try {
			log("#testImplies003");
			DmtPermission permission = new DmtPermission(
					DmtConstants.OSGi_LOG, DmtPermission.ADD);
			DmtPermission permission2 = new DmtPermission(
					DmtConstants.OSGi_LOG, DmtPermission.ADD + ","
							+ DmtPermission.GET);
			assertTrue(
							"Asserts that an object doesn't imply other object using the same uri and the actions it doesn't contain",
							!permission.implies(permission2));
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * Asserts that an object doesn't imply other object using different uris and the same actions
	 * 
	 * @spec DmtPermission.implies(Permission)
	 */
	public void testImplies004() {
		try {
			log("#testImplies004");
			DmtPermission permission = new DmtPermission(
					DmtConstants.OSGi_LOG, DmtPermission.ADD);
			DmtPermission permission2 = new DmtPermission(
					DmtConstants.OSGi_CONFIGURATION, DmtPermission.ADD);
			assertTrue(
							"Asserts that an object implies other object using the same uri and the same actions",
							!permission.implies(permission2));
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * Asserts that an object imply other object using the same uri and actions but in a different order
	 * 
	 * @spec DmtPermission.implies(Permission)
	 */
	public void testImplies005() {
		try {
			log("#testImplies005");
			DmtPermission permission = new DmtPermission(
					DmtConstants.OSGi_LOG, DmtPermission.ADD + ","
							+ DmtPermission.REPLACE);
			DmtPermission permission2 = new DmtPermission(
					DmtConstants.OSGi_LOG, DmtPermission.REPLACE + ","
							+ DmtPermission.ADD);
			assertTrue(
							"Asserts that an object imply other object using the same uri and actions but in a different order",
							permission.implies(permission2));
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}
    
    /**
     * Asserts that an object implies other object when using wildcard to match the uri
     * 
     * @spec DmtPermission.implies(Permission)
     */
    public void testImplies006() {
        try {
            log("#testImplies006");
            DmtPermission permission = new DmtPermission(
            		DmtConstants.OSGi_CONFIGURATION + "/a*", DmtPermission.ADD);
            DmtPermission permission2 = new DmtPermission(
            		DmtConstants.OSGi_CONFIGURATION + "/abc", DmtPermission.ADD);
            assertTrue(
                            "Asserts that an object implies other object when using wildcard to match the uri",
                            permission.implies(permission2));
        } catch (Exception e) {
            failUnexpectedException(e);
        }
    }
	/**
	 * Asserts that an object doesn't imply other object if they are from different types
	 * 
	 * @spec DmtPermission.implies(Permission)
	 */
	public void testImplies007() {
		try {
			log("#testImplies007");
			DmtPermission permission = new DmtPermission("./*","*");
			AlertPermission permission2 = new AlertPermission("./*","*");
			assertTrue(
						"Asserts that an object doesn't imply other object if they are from different types",
						!permission.implies(permission2));
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}
    
    /**
     * Asserts that a permission with "*" uri implies a permission with "." uri
     * 
     * @spec DmtPermission.implies(Permission)
     */
    public void testImplies008() {
        try {
            log("#testImplies008");
            DmtPermission permission = new DmtPermission("*",DmtConstants.ALL_ACTIONS);
            DmtPermission permission2 = new DmtPermission(".", "Get");
            assertTrue(
                        "Asserts that the \"*\" implies \".\"",
                        permission.implies(permission2));
        } catch (Exception e) {
            failUnexpectedException(e);
        }
    }
}

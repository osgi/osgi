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
 * This test class validates the implementation of <code>equals<code> method of DmtPrincipalPermission, 
 * according to MEG specification
 */
public class Equals extends DmtTestControl {
	/**
	 * Assert if two DmtPrincipalPermission instances are equal
	 * 
	 * DmtPrincipalPermission.equals(Object)
	 */
	public void testEquals001() {
		try {
			log("#testEquals001");

			DmtPrincipalPermission d1 = new DmtPrincipalPermission("*");
			DmtPrincipalPermission d2 = new DmtPrincipalPermission("*");

			assertTrue(
					"Assert if two DmtPrincipalPermission instances are equal",
					d1.equals(d2));

		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}
	
	/**
	 * Assert if two DmtPrincipalPermission instances are equal
	 * using the constructor with 2 parameters
	 * 
	 * DmtPrincipalPermission.equals(Object)
	 */
	public void testEquals002() {
		try {
			log("#testEquals002");

			DmtPrincipalPermission d1 = new DmtPrincipalPermission("*","*");
			DmtPrincipalPermission d2 = new DmtPrincipalPermission("*","*");

			assertTrue(
					"Assert if two DmtPrincipalPermission instances are equal",
					d1.equals(d2));

		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * Assert if two DmtPrincipalPermission instances are different
	 * 
	 * DmtPrincipalPermission.equals(Object)
	 */
	public void testEquals003() {
		try {
			log("#testEquals003");

			DmtPrincipalPermission d1 = new DmtPrincipalPermission(DmtConstants.PRINCIPAL);
			DmtPrincipalPermission d2 = new DmtPrincipalPermission("*");

			assertTrue(
					"Assert if two DmtPrincipalPermission instances are not equal",
					!d1.equals(d2));

		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * Assert if two DmtPrincipalPermission instances are different
	 * using the constructor with 2 parameters
	 * 
	 * DmtPrincipalPermission.equals(Object)
	 */
	public void testEquals004() {
		try {
			log("#testEquals004");

			DmtPrincipalPermission d1 = new DmtPrincipalPermission(DmtConstants.PRINCIPAL,"*");
			DmtPrincipalPermission d2 = new DmtPrincipalPermission("*","*");

			assertTrue(
					"Assert if two DmtPrincipalPermission instances are not equal",
					!d1.equals(d2));

		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}
    
    /**
     * Assert if two DmtPrincipalPermission instances are equal using different constructors
     * 
     * DmtPrincipalPermission.equals(Object)
     */
    public void testEquals005() {
        try {
            log("#testEquals005");

            DmtPrincipalPermission d1 = new DmtPrincipalPermission(DmtConstants.PRINCIPAL,"*");
            DmtPrincipalPermission d2 = new DmtPrincipalPermission(DmtConstants.PRINCIPAL);

            assertTrue(
					"Assert if two DmtPrincipalPermission instances are equal using different constructors",
					d1.equals(d2));

        } catch (Exception e) {
            failUnexpectedException(e);
        }
    }
}

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
 * Jan 26, 2005  Leonardo Barros
 * 1             Implement TCK
 * ============  ==============================================================
 * Feb 14, 2005  Luiz Felipe Guimaraes
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-002)
 * ============  ==============================================================
 * Feb 14, 2005  Luiz Felipe Guimaraes
 * 34            [MEGTCK][DMT] CVS update 
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc1.tbc.DmtPrincipalPermission;

import org.osgi.test.cases.dmt.tc1.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;

/**
 * This class tests DmtPrincipalPermission constructors according with MEG specification
 * 
 */

public class DmtPrincipalPermission extends DmtTestControl {

	/**
	 * It asserts if the principal passed in the constructor is
	 * equals to DmtPrincipalPermission.getName() method.
	 * 
	 * @spec DmtPrincipalPermission.DmtPrincipalPermission(String)
	 */
	public void testDmtPrincipalPermission001() {
		try {
			log("#testDmtPrincipalPermission001");
			org.osgi.service.dmt.security.DmtPrincipalPermission dmtPrincipalPermission = new org.osgi.service.dmt.security.DmtPrincipalPermission(
					DmtConstants.PRINCIPAL);
			assertEquals(
							"Asserts that the principal passed as parameter is equal to the returned value",
							DmtConstants.PRINCIPAL, dmtPrincipalPermission
									.getName());
			assertEquals("Asserts that the actions returned is '*'", "*",
					dmtPrincipalPermission.getActions());
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * Asserts that '*' is returned when it is passed as principal.
	 * 
	 * @spec DmtPrincipalPermission.DmtPrincipalPermission(String)
	 */
	public void testDmtPrincipalPermission002() {
		try {
			log("#testDmtPrincipalPermission002");
			org.osgi.service.dmt.security.DmtPrincipalPermission dmtPrincipalPermission = new org.osgi.service.dmt.security.DmtPrincipalPermission(
					"*");
			assertEquals(
							"Asserts that the principal passed as parameter is equal to the returned value.",
							"*", dmtPrincipalPermission.getName());
			assertEquals("Asserts that the actions returned is '*'.", "*",
					dmtPrincipalPermission.getActions());
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * It asserts if the principal passed in the constructor with 2 arguments is
	 * equals to DmtPrincipalPermission.getName() method.
	 * 
	 * @spec DmtPrincipalPermission.DmtPrincipalPermission(String,String)
	 */
	public void testDmtPrincipalPermission003() {
		try {
			log("#testDmtPrincipalPermission003");
			org.osgi.service.dmt.security.DmtPrincipalPermission dmtPrincipalPermission = new org.osgi.service.dmt.security.DmtPrincipalPermission(
					DmtConstants.PRINCIPAL, "*");
			assertEquals(
							"Asserts that the principal passed as parameter is equal to the returned value",
							DmtConstants.PRINCIPAL, dmtPrincipalPermission
									.getName());
			assertEquals(
							"Asserts that the actions passed as parameter is equal to the returned value",
							"*", dmtPrincipalPermission.getActions());
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * Asserts that NullPointerException is thrown if name is null constructor with 1 parameter
	 * 
	 * @spec DmtPrincipalPermission.DmtPrincipalPermission(String)
	 */
	public void testDmtPrincipalPermission004() {
		try {
			log("#testDmtPrincipalPermission004");
			new org.osgi.service.dmt.security.DmtPrincipalPermission(null);
			failException("#", NullPointerException.class);
		} catch (NullPointerException e) {
			pass("NullPointerException correctly thrown");
		} catch (Exception e) {
			failExpectedOtherException(NullPointerException.class, e);
		}
	}

	/**
	 * Asserts that IllegalArgumentException is thrown if name is empty constructor with 1 parameter
	 * 
	 * @spec DmtPrincipalPermission.DmtPrincipalPermission(String)
	 */
	public void testDmtPrincipalPermission005() {
		try {
			log("#testDmtPrincipalPermission005");
			new org.osgi.service.dmt.security.DmtPrincipalPermission("");
			failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			pass("IllegalArgumentException correctly thrown");
		} catch (Exception e) {
			failExpectedOtherException(IllegalArgumentException.class, e);
		}
	}

	/**
	 * Asserts that NullPointerException is thrown if name is null constructor with 2 parameter
	 * 
	 * @spec DmtPrincipalPermission.DmtPrincipalPermission(String,String)
	 */
	public void testDmtPrincipalPermission006() {
		try {
			log("#testDmtPrincipalPermission006");
			new org.osgi.service.dmt.security.DmtPrincipalPermission(null, "*");
			failException("#", NullPointerException.class);
		} catch (NullPointerException e) {
			pass("NullPointerException correctly thrown");
		} catch (Exception e) {
			failExpectedOtherException(NullPointerException.class, e);
		}
	}

	/**
	 * Asserts that IllegalArgumentException is thrown if name is empty constructor with 2 parameter
	 * 
	 * @spec DmtPrincipalPermission.DmtPrincipalPermission(String,String)
	 */
	public void testDmtPrincipalPermission007() {
		try {
			log("#testDmtPrincipalPermission007");
			new org.osgi.service.dmt.security.DmtPrincipalPermission("", "*");
			failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			pass("IllegalArgumentException correctly thrown");
		} catch (Exception e) {
			failExpectedOtherException(IllegalArgumentException.class, e);
		}
	}

	/**
	 * Asserts that NullPointerException is thrown if actions is null
	 * 
	 * @spec DmtPrincipalPermission.DmtPrincipalPermission(String,String)
	 */
	public void testDmtPrincipalPermission008() {
		try {
			log("#testDmtPrincipalPermission008");
			new org.osgi.service.dmt.security.DmtPrincipalPermission(
					DmtConstants.PRINCIPAL, null);
			failException("#", NullPointerException.class);
		} catch (NullPointerException e) {
			pass("NullPointerException correctly thrown");
		} catch (Exception e) {
			failExpectedOtherException(NullPointerException.class, e);
		}
	}

	/**
	 * Asserts that IllegalArgumentException is thrown if actions is not "*"
	 * 
	 * @spec DmtPrincipalPermission.DmtPrincipalPermission(String,String)
	 */
	public void testDmtPrincipalPermission009() {
		try {
			log("#testDmtPrincipalPermission009");
			new org.osgi.service.dmt.security.DmtPrincipalPermission(
					DmtConstants.PRINCIPAL, "");
			failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			pass("IllegalArgumentException correctly thrown");
		} catch (Exception e) {
			failExpectedOtherException(IllegalArgumentException.class, e);
		}
	}

	/**
	 * Asserts that the name of the principal can end with "*" to match any principal
	 * using the constructor with 1 parameter
	 * 
	 * @spec DmtPrincipalPermission.DmtPrincipalPermission(String)
	 */
	public void testDmtPrincipalPermission010() {
		try {
			log("#testDmtPrincipalPermission010");
			String principal = "www.cesar*";
			org.osgi.service.dmt.security.DmtPrincipalPermission dmtPrincipalPermission = new org.osgi.service.dmt.security.DmtPrincipalPermission(
					principal);
			assertEquals(
							"Asserts that the principal passed as parameter is equal to the returned value",
							principal, dmtPrincipalPermission.getName());
			assertEquals("Asserts that the actions returned is '*'", "*",
					dmtPrincipalPermission.getActions());
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * Asserts that the name of the principal can end with "*" to match any principal
	 * using the constructor with 2 parameters
	 * 
	 * @spec DmtPrincipalPermission.DmtPrincipalPermission(String,String)
	 */
	public void testDmtPrincipalPermission011() {
		try {
			log("#testDmtPrincipalPermission011");
			String principal = "www.cesar*";
			org.osgi.service.dmt.security.DmtPrincipalPermission dmtPrincipalPermission = new org.osgi.service.dmt.security.DmtPrincipalPermission(
					principal, "*");
			assertEquals(
							"Asserts that the principal passed as parameter is equal to the returned value",
							principal, dmtPrincipalPermission.getName());
			assertEquals("Asserts that the actions returned is '*'", "*",
					dmtPrincipalPermission.getActions());
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}
}

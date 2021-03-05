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
 * Abr 01, 2005  Luiz Felipe Guimaraes
 * 34            [MEGTCK][DMT] CVS update 
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc1.tbc.DmtPermission;

import java.util.StringTokenizer;

import org.osgi.test.cases.dmt.tc1.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;

/**
 * This class tests DmtPermission constructors according to MEG specification 
 */

public class DmtPermission extends DmtTestControl {
	/**
	 * It asserts that the actions passed in the constructor is equals to 
	 * DmtPermission.getActions() method (listed in this order: 
	 * Add, Get, Replace.).
	 * 
	 * @spec DmtPermission.DmtPermission(String,String)
	 */
	public void testDmtPermission001() {
		try {
			log("#testDmtPermission001");
			StringTokenizer stringToken = new StringTokenizer(
					new org.osgi.service.dmt.security.DmtPermission(
							DmtConstants.OSGi_LOG, DmtConstants.ACTIONS)
							.getActions(), ",");

			boolean ordered = true;
			boolean hasADD = false;
			boolean hasGET = false;
			boolean hasREPLACE = false;
			boolean errorFound = false;
			while (stringToken.hasMoreTokens()) {
				String currentToken = stringToken.nextToken().trim();
				if (currentToken.equals(org.osgi.service.dmt.security.DmtPermission.ADD)) {
					hasADD = true;
				} else if (currentToken
						.equals(org.osgi.service.dmt.security.DmtPermission.GET)) {
					hasGET = true;
					if (!hasADD) {
						ordered = false;
					}
				} else if (currentToken
						.equals(org.osgi.service.dmt.security.DmtPermission.REPLACE)) {
					hasREPLACE = true;
					if (!hasGET) {
						ordered = false;
					}
				} else {
					errorFound = true;
				}
			}

			assertTrue(
					"Asserts that no unexpected permissions were returned.",
					!errorFound);
			assertTrue("Asserts that all of the actions were returned.",
					hasADD && hasGET && hasREPLACE);
			assertTrue(
					"Asserts that the order returned was the specified.",
					ordered);
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * Asserts that the list of actions is really all of the actions 
	 * when using a wildcard "*" and that it is listed in this order: 
	 * Add, Delete, Exec, Get, Replace.
	 * 
	 * @spec DmtPermission.DmtPermission(String,String)
	 */
	public void testDmtPermission002() {
		try {
			log("#testDmtPermission002");
			StringTokenizer stringToken = new StringTokenizer(
					new org.osgi.service.dmt.security.DmtPermission(
							DmtConstants.OSGi_LOG, "*").getActions(), ",");

			boolean ordered = true;
			boolean hasADD = false;
			boolean hasDELETE = false;
			boolean hasEXEC = false;
			boolean hasGET = false;
			boolean hasREPLACE = false;
			boolean errorFound = false;

			while (stringToken.hasMoreTokens()) {
				String currentToken = stringToken.nextToken().trim();
				if (currentToken.equals(org.osgi.service.dmt.security.DmtPermission.ADD)) {
					hasADD = true;

				} else if (currentToken
						.equals(org.osgi.service.dmt.security.DmtPermission.DELETE)) {
					hasDELETE = true;
					if (!hasADD) {
						ordered = false;
					}

				} else if (currentToken
						.equals(org.osgi.service.dmt.security.DmtPermission.EXEC)) {
					hasEXEC = true;
					if (!hasDELETE) {
						ordered = false;
					}
				} else if (currentToken
						.equals(org.osgi.service.dmt.security.DmtPermission.GET)) {
					hasGET = true;
					if (!hasEXEC) {
						ordered = false;
					}
				} else if (currentToken
						.equals(org.osgi.service.dmt.security.DmtPermission.REPLACE)) {
					hasREPLACE = true;
					if (!hasGET) {
						ordered = false;
					}
				} else {
					errorFound = true;
				}

			}

			assertTrue(
					"Asserts that no unexpected permissions were returned.",
					!errorFound);
			assertTrue("Asserts that all of the actions were returned.",
					hasADD && hasDELETE && hasEXEC && hasGET && hasREPLACE);
			assertTrue(
					"Asserts that the order returned was the specified.",
					ordered);
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * Asserts that NullPointerException is thrown if actions parameter is null
	 * 
	 * @spec DmtPermission.DmtPermission(String,String)
	 */
	public void testDmtPermission003() {
		log("#testDmtPermission003");
		try {
			new org.osgi.service.dmt.security.DmtPermission(DmtConstants.OSGi_LOG,
					null);
			failException("#", NullPointerException.class);
		} catch (NullPointerException e) {
			pass("NullPointerException correctly thrown");
		} catch (Exception e) {
			failExpectedOtherException(NullPointerException.class, e);
		}

	}

	/**
	 * Asserts that NullPointerException is thrown if dmtUri is null
	 * 
	 * @spec DmtPermission.DmtPermission(String,String)
	 */
	public void testDmtPermission004() {
		log("#testDmtPermission004");
		try {
			new org.osgi.service.dmt.security.DmtPermission(null, DmtConstants.ACTIONS);
			failException("#", NullPointerException.class);
		} catch (NullPointerException e) {
			pass("NullPointerException correctly thrown");
		} catch (Exception e) {
			failExpectedOtherException(NullPointerException.class, e);
		}

	}

	/**
	 * Asserts that IllegalArgumentException is thrown if actions are invalid
	 * 
	 * @spec DmtPermission.DmtPermission(String,String)
	 */
	public void testDmtPermission005() {
		log("#testDmtPermission005");
		try {
			new org.osgi.service.dmt.security.DmtPermission(DmtConstants.OSGi_LOG,
					DmtConstants.TITLE);
			failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			pass("IllegalArgumentException correctly thrown");
		} catch (Exception e) {
			failExpectedOtherException(IllegalArgumentException.class, e);
		}

	}

	/**
	 * Asserts that IllegalArgumentException is thrown if dmtUri is invalid
	 * 
	 * @spec DmtPermission.DmtPermission(String,String)
	 */
	public void testDmtPermission006() {
		log("#testDmtPermission006");
		try {
			new org.osgi.service.dmt.security.DmtPermission(DmtConstants.INVALID,
					DmtConstants.ACTIONS);
			failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			pass("IllegalArgumentException correctly thrown");
		} catch (Exception e) {
			failExpectedOtherException(IllegalArgumentException.class, e);
		}

	}

	/**
	 * Asserts that a wildcard is permitted on dmtUri
	 * 
	 * @spec DmtPermission.DmtPermission(String,String)
	 */
	public void testDmtPermission007() {
		log("#testDmtPermission007");
		try {
			String actions = new org.osgi.service.dmt.security.DmtPermission(
					DmtConstants.OSGi_ROOT + "/l*",
					org.osgi.service.dmt.security.DmtPermission.GET).getActions();

			assertEquals("Asserts that a wildcard is permitted on dmtUri",
					org.osgi.service.dmt.security.DmtPermission.GET, actions.trim());
		} catch (Exception e) {
			failUnexpectedException(e);
		}

	}

	/**
	 * Asserts that a wildcard is permitted on dmtUri after '/' character
	 * 
	 * @spec DmtPermission.DmtPermission(String,String)
	 */
	public void testDmtPermission008() {
		log("#testDmtPermission008");
		try {
			String actions = new org.osgi.service.dmt.security.DmtPermission(
					DmtConstants.OSGi_ROOT + "/*",
					org.osgi.service.dmt.security.DmtPermission.GET).getActions();

			assertEquals(
							"Asserts that a wildcard is permitted on dmtUri after '/' character",
							org.osgi.service.dmt.security.DmtPermission.GET, actions
									.trim());
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}
	
	/**
	 * Asserts that action names are interpreted case-insensitively, 
	 * and that the canonical action string returned by getActions() 
	 * uses the forms defined by the action constants. 
	 * 
	 * @spec DmtPermission.DmtPermission(String,String)
	 */
	public void testDmtPermission009() {
		log("#testDmtPermission009");
		try {
			String expectedActions = org.osgi.service.dmt.security.DmtPermission.ADD + "," + 
			org.osgi.service.dmt.security.DmtPermission.DELETE + "," +
			org.osgi.service.dmt.security.DmtPermission.EXEC + "," + 
			org.osgi.service.dmt.security.DmtPermission.GET + "," + 
			org.osgi.service.dmt.security.DmtPermission.REPLACE;
			
			
			String actions = new org.osgi.service.dmt.security.DmtPermission(
					DmtConstants.OSGi_ROOT,expectedActions.toUpperCase()).getActions();

			assertEquals(
					"Asserts that action names are interpreted case-insensitively, "
							+
					"and that the canonical action string returned by getActions() uses the " +
					"forms defined by the action constants.",expectedActions,actions);

		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}
}

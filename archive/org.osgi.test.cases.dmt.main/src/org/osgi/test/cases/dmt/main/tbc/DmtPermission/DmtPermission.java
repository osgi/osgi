/*
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

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

package org.osgi.test.cases.dmt.main.tbc.DmtPermission;

import java.util.StringTokenizer;

import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;

/**
 * @methodUnderTest org.osgi.service.dmt.DmtPermission#DmtPermission
 * @generalDescription This class tests DmtPermission constructors according
 *                     with MEG specification (rfc0085)
 */

public class DmtPermission {
	private DmtTestControl tbc;

	public DmtPermission(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testDmtPermission001();
		testDmtPermission002();
		testDmtPermission003();
		testDmtPermission004();
		testDmtPermission005();
		testDmtPermission006();
		testDmtPermission007();
		testDmtPermission008();
	}

	/**
	 * @testID testDmtPermission001
	 * @testDescription It asserts that the actions passed in the constructor is
	 *                  equals to DmtPermission.getActions() method (listed in
	 *                  this order: Add, Delete, Exec, Get, Replace.).
	 */
	private void testDmtPermission001() {
		try {
			tbc.log("#testDmtPermission001");
			StringTokenizer stringToken = new StringTokenizer(
					new org.osgi.service.dmt.DmtPermission(
							DmtTestControl.OSGi_LOG, DmtTestControl.ACTIONS)
							.getActions(), ",");

			boolean ordered = true;
			boolean hasADD = false;
			boolean hasGET = false;
			boolean hasREPLACE = false;
			boolean errorFound = false;
			while (stringToken.hasMoreTokens()) {
				String currentToken = stringToken.nextToken().trim();
				if (currentToken.equals(org.osgi.service.dmt.DmtPermission.ADD)) {
					hasADD = true;
				} else if (currentToken
						.equals(org.osgi.service.dmt.DmtPermission.GET)) {
					hasGET = true;
					if (!hasADD) {
						ordered = false;
					}
				} else if (currentToken
						.equals(org.osgi.service.dmt.DmtPermission.REPLACE)) {
					hasREPLACE = true;
					if (!hasGET) {
						ordered = false;
					}
				} else {
					errorFound = true;
				}
			}

			tbc.assertTrue(
					"Asserts that no unexpected permissions were returned.",
					!errorFound);
			tbc.assertTrue("Asserts that all of the actions were returned.",
					hasADD && hasGET && hasREPLACE);
			tbc.assertTrue(
					"Asserts that the order returned was the specified.",
					ordered);
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testDmtPermission002
	 * @testDescription Asserts that the list of actions is really all of the
	 *                  actions and that it is listed in this order: Add,
	 *                  Delete, Exec, Get, Replace.
	 */
	private void testDmtPermission002() {
		try {
			tbc.log("#testDmtPermission002");
			StringTokenizer stringToken = new StringTokenizer(
					new org.osgi.service.dmt.DmtPermission(
							DmtTestControl.OSGi_LOG, "*").getActions(), ",");

			boolean ordered = true;
			boolean hasADD = false;
			boolean hasDELETE = false;
			boolean hasEXEC = false;
			boolean hasGET = false;
			boolean hasREPLACE = false;
			boolean errorFound = false;

			while (stringToken.hasMoreTokens()) {
				String currentToken = stringToken.nextToken().trim();
				if (currentToken.equals(org.osgi.service.dmt.DmtPermission.ADD)) {
					hasADD = true;

				} else if (currentToken
						.equals(org.osgi.service.dmt.DmtPermission.DELETE)) {
					hasDELETE = true;
					if (!hasADD) {
						ordered = false;
					}

				} else if (currentToken
						.equals(org.osgi.service.dmt.DmtPermission.EXEC)) {
					hasEXEC = true;
					if (!hasDELETE) {
						ordered = false;
					}
				} else if (currentToken
						.equals(org.osgi.service.dmt.DmtPermission.GET)) {
					hasGET = true;
					if (!hasEXEC) {
						ordered = false;
					}
				} else if (currentToken
						.equals(org.osgi.service.dmt.DmtPermission.REPLACE)) {
					hasREPLACE = true;
					if (!hasGET) {
						ordered = false;
					}
				} else {
					errorFound = true;
				}

			}

			tbc.assertTrue(
					"Asserts that no unexpected permissions were returned.",
					!errorFound);
			tbc.assertTrue("Asserts that all of the actions were returned.",
					hasADD && hasDELETE && hasEXEC && hasGET && hasREPLACE);
			tbc.assertTrue(
					"Asserts that the order returned was the specified.",
					ordered);
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testDmtPermission003
	 * @testDescription Asserts that NullPointerException is thrown if actions
	 *                  are null
	 */
	private void testDmtPermission003() {
		tbc.log("#testDmtPermission003");
		try {
			new org.osgi.service.dmt.DmtPermission(DmtTestControl.OSGi_LOG,
					null);
			tbc.failException("#", NullPointerException.class);
		} catch (NullPointerException e) {
			tbc.pass("NullPointerException correctly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + NullPointerException.class.getName()
					+ " but was " + e.getClass().getName());
		}

	}

	/**
	 * @testID testDmtPermission004
	 * @testDescription Asserts that NullPointerException is thrown if dmtUri is
	 *                  null
	 */
	private void testDmtPermission004() {
		tbc.log("#testDmtPermission004");
		try {
			new org.osgi.service.dmt.DmtPermission(null, DmtTestControl.ACTIONS);
			tbc.failException("#", NullPointerException.class);
		} catch (NullPointerException e) {
			tbc.pass("NullPointerException correctly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + NullPointerException.class.getName()
					+ " but was " + e.getClass().getName());
		}

	}

	/**
	 * @testID testDmtPermission005
	 * @testDescription Asserts that IllegalArgumentException is thrown if
	 *                  actions are invalid
	 */
	private void testDmtPermission005() {
		tbc.log("#testDmtPermission005");
		try {
			new org.osgi.service.dmt.DmtPermission(DmtTestControl.OSGi_LOG,
					DmtTestControl.TITLE);
			tbc.failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass("IllegalArgumentException correctly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalArgumentException.class.getName()
					+ " but was " + e.getClass().getName());
		}

	}

	/**
	 * @testID testDmtPermission006
	 * @testDescription Asserts that IllegalArgumentException is thrown if
	 *                  dmtUri is invalid
	 */
	private void testDmtPermission006() {
		tbc.log("#testDmtPermission006");
		try {
			new org.osgi.service.dmt.DmtPermission(DmtTestControl.INVALID,
					DmtTestControl.ACTIONS);
			tbc.failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass("IllegalArgumentException correctly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalArgumentException.class.getName()
					+ " but was " + e.getClass().getName());
		}

	}

	/**
	 * @testID testDmtPermission007
	 * @testDescription Asserts that a wildcard is permitted on dmtUri
	 */
	private void testDmtPermission007() {
		tbc.log("#testDmtPermission007");
		try {
			String actions = new org.osgi.service.dmt.DmtPermission(
					DmtTestControl.OSGi_ROOT + "/l*",
					org.osgi.service.dmt.DmtPermission.GET).getActions();

			tbc.assertEquals("Asserts that a wildcard is permitted on dmtUri",
					org.osgi.service.dmt.DmtPermission.GET, actions.trim());
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}

	}

	/**
	 * @testID testDmtPermission008
	 * @testDescription Asserts that a wildcard is permitted on dmtUri after '/'
	 *                  character
	 */
	private void testDmtPermission008() {
		tbc.log("#testDmtPermission008");
		try {
			String actions = new org.osgi.service.dmt.DmtPermission(
					DmtTestControl.OSGi_ROOT + "/*",
					org.osgi.service.dmt.DmtPermission.GET).getActions();

			tbc
					.assertEquals(
							"Asserts that a wildcard is permitted on dmtUri after '/' character",
							org.osgi.service.dmt.DmtPermission.GET, actions
									.trim());
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}

}

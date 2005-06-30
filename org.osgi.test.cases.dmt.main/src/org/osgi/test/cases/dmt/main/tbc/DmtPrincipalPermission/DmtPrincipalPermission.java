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

package org.osgi.test.cases.dmt.main.tbc.DmtPrincipalPermission;

import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;

/**
 * @methodUnderTest org.osgi.service.dmt.DmtPrincipalPermission#DmtPrincipalPermission
 * @generalDescription This class tests DmtPrincipalPermission constructors
 *                     according with MEG specification (rfc0085)
 */

public class DmtPrincipalPermission {

	private DmtTestControl tbc;

	public DmtPrincipalPermission(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testDmtPrincipalPermission001();
		testDmtPrincipalPermission002();
		testDmtPrincipalPermission003();
		testDmtPrincipalPermission004();
		testDmtPrincipalPermission005();
		testDmtPrincipalPermission006();
		testDmtPrincipalPermission007();
		testDmtPrincipalPermission008();
		testDmtPrincipalPermission009();
		testDmtPrincipalPermission010();
		testDmtPrincipalPermission011();
	}

	/**
	 * @testID testDmtPrincipalPermission001
	 * @testDescription It asserts if the principal passed in the constructor is
	 *                  equals to DmtPrincipalPermission.getName() method.
	 */
	private void testDmtPrincipalPermission001() {
		try {
			tbc.log("#testDmtPrincipalPermission001");
			org.osgi.service.dmt.DmtPrincipalPermission dmtPrincipalPermission = new org.osgi.service.dmt.DmtPrincipalPermission(
					DmtTestControl.PRINCIPAL);
			tbc
					.assertEquals(
							"Asserts that the principal passed as parameter is equal to the returned value",
							DmtTestControl.PRINCIPAL, dmtPrincipalPermission
									.getName());
			tbc.assertEquals("Asserts that the actions returned is '*'", "*",
					dmtPrincipalPermission.getActions());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		}
	}

	/**
	 * @testID testDmtPrincipalPermission002
	 * @testDescription Asserts that '*' is returned.
	 */
	private void testDmtPrincipalPermission002() {
		try {
			tbc.log("#testDmtPrincipalPermission002");
			org.osgi.service.dmt.DmtPrincipalPermission dmtPrincipalPermission = new org.osgi.service.dmt.DmtPrincipalPermission(
					"*");
			tbc
					.assertEquals(
							"Asserts that the principal passed as parameter is equal to the returned value.",
							"*", dmtPrincipalPermission.getName());
			tbc.assertEquals("Asserts that the actions returned is '*'.", "*",
					dmtPrincipalPermission.getActions());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		}
	}

	/**
	 * @testID testDmtPrincipalPermission003
	 * @testDescription Tests the constructor which has two string parameters.
	 *                  The second parameter is ignored and the first one sets
	 *                  the principal
	 */
	private void testDmtPrincipalPermission003() {
		try {
			tbc.log("#testDmtPrincipalPermission003");
			org.osgi.service.dmt.DmtPrincipalPermission dmtPrincipalPermission = new org.osgi.service.dmt.DmtPrincipalPermission(
					DmtTestControl.PRINCIPAL, "*");
			tbc
					.assertEquals(
							"Asserts that the principal passed as parameter is equal to the returned value",
							DmtTestControl.PRINCIPAL, dmtPrincipalPermission
									.getName());
			tbc
					.assertEquals(
							"Asserts that the actions passed as parameter is equal to the returned value",
							"*", dmtPrincipalPermission.getActions());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		}
	}

	/**
	 * @testID testDmtPrincipalPermission004
	 * @testDescription Tests if NullPointerException is thrown if name is null
	 *                  constructor with 1 parameter
	 */
	private void testDmtPrincipalPermission004() {
		try {
			tbc.log("#testDmtPrincipalPermission004");
			new org.osgi.service.dmt.DmtPrincipalPermission(null);
			tbc.failException("#", NullPointerException.class);
		} catch (NullPointerException e) {
			tbc.pass("NullPointerException correctly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + NullPointerException.class.getName()
					+ " but was " + e.getClass().getName());
		}
	}

	/**
	 * @testID testDmtPrincipalPermission005
	 * @testDescription Tests if IllegalArgumentException is thrown if name is
	 *                  empty constructor with 1 parameter
	 */
	private void testDmtPrincipalPermission005() {
		try {
			tbc.log("#testDmtPrincipalPermission005");
			new org.osgi.service.dmt.DmtPrincipalPermission("");
			tbc.failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass("IllegalArgumentException correctly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + NullPointerException.class.getName()
					+ " but was " + e.getClass().getName());
		}
	}

	/**
	 * @testID testDmtPrincipalPermission006
	 * @testDescription Tests if NullPointerException is thrown if name is null
	 *                  constructor with 2 parameter
	 */
	private void testDmtPrincipalPermission006() {
		try {
			tbc.log("#testDmtPrincipalPermission006");
			new org.osgi.service.dmt.DmtPrincipalPermission(null, "*");
			tbc.failException("#", NullPointerException.class);
		} catch (NullPointerException e) {
			tbc.pass("NullPointerException correctly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + NullPointerException.class.getName()
					+ " but was " + e.getClass().getName());
		}
	}

	/**
	 * @testID testDmtPrincipalPermission007
	 * @testDescription Tests if IllegalArgumentException is thrown if name is
	 *                  empty constructor with 2 parameter
	 */
	private void testDmtPrincipalPermission007() {
		try {
			tbc.log("#testDmtPrincipalPermission007");
			new org.osgi.service.dmt.DmtPrincipalPermission("", "*");
			tbc.failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass("IllegalArgumentException correctly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + NullPointerException.class.getName()
					+ " but was " + e.getClass().getName());
		}
	}

	/**
	 * @testID testDmtPrincipalPermission008
	 * @testDescription Tests if NullPointerException is thrown if actions is
	 *                  null
	 */
	private void testDmtPrincipalPermission008() {
		try {
			tbc.log("#testDmtPrincipalPermission008");
			new org.osgi.service.dmt.DmtPrincipalPermission(
					DmtTestControl.PRINCIPAL, null);
			tbc.failException("#", NullPointerException.class);
		} catch (NullPointerException e) {
			tbc.pass("NullPointerException correctly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + NullPointerException.class.getName()
					+ " but was " + e.getClass().getName());
		}
	}

	/**
	 * @testID testDmtPrincipalPermission009
	 * @testDescription Tests if IllegalArgumentException is thrown if actions
	 *                  is not "*"
	 */
	private void testDmtPrincipalPermission009() {
		try {
			tbc.log("#testDmtPrincipalPermission009");
			new org.osgi.service.dmt.DmtPrincipalPermission(
					DmtTestControl.PRINCIPAL, "");
			tbc.failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass("IllegalArgumentException correctly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + NullPointerException.class.getName()
					+ " but was " + e.getClass().getName());
		}
	}

	/**
	 * @testID testDmtPrincipalPermission010
	 * @testDescription It asserts if the name of the principal can end with "*"
	 *                  to match any principal
	 */
	private void testDmtPrincipalPermission010() {
		try {
			tbc.log("#testDmtPrincipalPermission010");
			String principal = "www.cesar*";
			org.osgi.service.dmt.DmtPrincipalPermission dmtPrincipalPermission = new org.osgi.service.dmt.DmtPrincipalPermission(
					principal);
			tbc
					.assertEquals(
							"Asserts that the principal passed as parameter is equal to the returned value",
							principal, dmtPrincipalPermission.getName());
			tbc.assertEquals("Asserts that the actions returned is '*'", "*",
					dmtPrincipalPermission.getActions());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		}
	}

	/**
	 * @testID testDmtPrincipalPermission011
	 * @testDescription It asserts if the name of the principal can end with "*"
	 *                  to match any principal
	 */
	private void testDmtPrincipalPermission011() {
		try {
			tbc.log("#testDmtPrincipalPermission011");
			String principal = "www.cesar*";
			org.osgi.service.dmt.DmtPrincipalPermission dmtPrincipalPermission = new org.osgi.service.dmt.DmtPrincipalPermission(
					principal, "*");
			tbc
					.assertEquals(
							"Asserts that the principal passed as parameter is equal to the returned value",
							principal, dmtPrincipalPermission.getName());
			tbc.assertEquals("Asserts that the actions returned is '*'", "*",
					dmtPrincipalPermission.getActions());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		}
	}
}

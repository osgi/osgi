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
			info.dmtree.security.DmtPrincipalPermission dmtPrincipalPermission = new info.dmtree.security.DmtPrincipalPermission(
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
			info.dmtree.security.DmtPrincipalPermission dmtPrincipalPermission = new info.dmtree.security.DmtPrincipalPermission(
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
			info.dmtree.security.DmtPrincipalPermission dmtPrincipalPermission = new info.dmtree.security.DmtPrincipalPermission(
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
			new info.dmtree.security.DmtPrincipalPermission(null);
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
			new info.dmtree.security.DmtPrincipalPermission("");
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
			new info.dmtree.security.DmtPrincipalPermission(null, "*");
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
			new info.dmtree.security.DmtPrincipalPermission("", "*");
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
			new info.dmtree.security.DmtPrincipalPermission(
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
			new info.dmtree.security.DmtPrincipalPermission(
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
			info.dmtree.security.DmtPrincipalPermission dmtPrincipalPermission = new info.dmtree.security.DmtPrincipalPermission(
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
			info.dmtree.security.DmtPrincipalPermission dmtPrincipalPermission = new info.dmtree.security.DmtPrincipalPermission(
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

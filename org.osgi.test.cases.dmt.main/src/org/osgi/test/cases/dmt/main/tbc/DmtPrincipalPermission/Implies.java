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
 * 
 */

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
package org.osgi.test.cases.dmt.main.tbc.DmtPrincipalPermission;

import org.osgi.service.dmt.DmtPrincipalPermission;
import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;

/**
 * @methodUnderTest org.osgi.service.dmt.DmtPrincipalPermission#implies
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>implies<code> method, according to MEG reference
 *                     documentation.
 */
public class Implies {
	private DmtTestControl tbc;

	public Implies(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testImplies001();
		testImplies002();
		testImplies003();
	}

	/**
	 * @testID testImplies001
	 * @testDescription This method asserts that an object implies other object
	 *                  using the same target
	 */
	private void testImplies001() {
		try {
			tbc.log("#testImplies001");

			DmtPrincipalPermission d1 = new DmtPrincipalPermission("*");
			DmtPrincipalPermission d2 = new DmtPrincipalPermission("*");

			tbc
					.assertTrue(
							"Asserts that an object implies other object using the same target",
							d1.implies(d2));

		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testImplies002
	 * @testDescription This method asserts that an object implies other object
	 *                  using the same target and actions
	 */
	private void testImplies002() {
		try {
			tbc.log("#testImplies002");

			DmtPrincipalPermission d1 = new DmtPrincipalPermission(
					DmtTestControl.PRINCIPAL, "*");
			DmtPrincipalPermission d2 = new DmtPrincipalPermission(
					DmtTestControl.PRINCIPAL, "*");

			tbc
					.assertTrue(
							"Asserts that an object implies other object using the same target and actions",
							d1.implies(d2));

		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testImplies003
	 * @testDescription This method asserts that an object does not imply other
	 *                  object using the different target and actions
	 */
	private void testImplies003() {
		try {
			tbc.log("#testImplies003");

			DmtPrincipalPermission d1 = new DmtPrincipalPermission(
					DmtTestControl.PRINCIPAL, "*");
			DmtPrincipalPermission d2 = new DmtPrincipalPermission("*", "*");

			tbc
					.assertTrue(
							"Asserts that an object does not imply other object using the different target and actions",
							!d1.implies(d2));

		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}

}

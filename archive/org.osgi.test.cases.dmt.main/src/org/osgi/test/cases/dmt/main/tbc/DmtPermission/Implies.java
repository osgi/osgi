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
 * Abr 04, 2005  Luiz Felipe Guimaraes
 * 13            [MEGTCK][DMT] CVS update 
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.main.tbc.DmtPermission;

import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;
import org.osgi.service.dmt.DmtPermission;

/**
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtPermission#implies
 * @generalDescription This Test Case Validates the implementation of
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
		testImplies004();
		testImplies005();
	}

	/**
	 * @testID testImplies001
	 * @testDescription This method asserts that an object implies other object
	 *                  using the same uri actions
	 */
	private void testImplies001() {
		try {
			tbc.log("#testImplies001");
			DmtPermission permission = new DmtPermission(
					DmtTestControl.OSGi_LOG, DmtTestControl.ACTIONS);
			DmtPermission permission2 = new DmtPermission(
					DmtTestControl.OSGi_LOG, DmtTestControl.ACTIONS);
			tbc
					.assertTrue(
							"Asserts that an object implies other object using the same uri and actions",
							permission.implies(permission2));
		} catch (Exception e) {
			tbc.fail("Unexpected exception:" + e.getClass().getName());
		}
	}

	/**
	 * @testID testImplies002
	 * @testDescription Asserts that an object implies other object using the
	 *                  same uri and the actions it contains
	 */
	private void testImplies002() {
		try {
			tbc.log("#testImplies002");
			DmtPermission permission = new DmtPermission(
					DmtTestControl.OSGi_LOG, DmtPermission.ADD + ","
							+ DmtPermission.GET);
			DmtPermission permission2 = new DmtPermission(
					DmtTestControl.OSGi_LOG, DmtPermission.ADD);
			tbc
					.assertTrue(
							"Asserts that an object implies other object using the same uri and the actions it contains",
							permission.implies(permission2));
		} catch (Exception e) {
			tbc.fail("Unexpected exception:" + e.getClass().getName());
		}
	}

	/**
	 * @testID testImplies003
	 * @testDescription Asserts that an object doesn't imply other object using
	 *                  the same uri and the actions it doesn't contain
	 */
	private void testImplies003() {
		try {
			tbc.log("#testImplies003");
			DmtPermission permission = new DmtPermission(
					DmtTestControl.OSGi_LOG, DmtPermission.ADD);
			DmtPermission permission2 = new DmtPermission(
					DmtTestControl.OSGi_LOG, DmtPermission.ADD + ","
							+ DmtPermission.GET);
			tbc
					.assertTrue(
							"Asserts that an object doesn't imply other object using the same uri and the actions it doesn't contain",
							!permission.implies(permission2));
		} catch (Exception e) {
			tbc.fail("Unexpected exception:" + e.getClass().getName());
		}
	}

	/**
	 * @testID testImplies004
	 * @testDescription Asserts that an object doesn't imply other object using
	 *                  different uris and the same actions
	 */
	private void testImplies004() {
		try {
			tbc.log("#testImplies004");
			DmtPermission permission = new DmtPermission(
					DmtTestControl.OSGi_LOG, DmtPermission.ADD);
			DmtPermission permission2 = new DmtPermission(
					DmtTestControl.OSGi_CFG, DmtPermission.ADD);
			tbc
					.assertTrue(
							"Asserts that an object implies other object using the same uri and the same actions",
							!permission.implies(permission2));
		} catch (Exception e) {
			tbc.fail("Unexpected exception:" + e.getClass().getName());
		}
	}

	/**
	 * @testID testImplies005
	 * @testDescription Asserts that an object imply other object using the same
	 *                  uri and actions but in a different order
	 */
	private void testImplies005() {
		try {
			tbc.log("#testImplies005");
			DmtPermission permission = new DmtPermission(
					DmtTestControl.OSGi_LOG, DmtPermission.ADD + ","
							+ DmtPermission.REPLACE);
			DmtPermission permission2 = new DmtPermission(
					DmtTestControl.OSGi_LOG, DmtPermission.REPLACE + ","
							+ DmtPermission.ADD);
			tbc
					.assertTrue(
							"Asserts that an object imply other object using the same uri and actions but in a different order",
							permission.implies(permission2));
		} catch (Exception e) {
			tbc.fail("Unexpected exception:" + e.getClass().getName());
		}
	}

}

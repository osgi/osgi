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

import org.osgi.service.dmt.security.AlertPermission;
import org.osgi.service.dmt.security.DmtPermission;
import org.osgi.test.cases.dmt.main.tbc.DmtConstants;
import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.main.tbc.Plugin.ExecPlugin.TestExecPluginActivator;

/**
 * This test case validates the implementation of <code>implies</code> method of DmtPermission, 
 * according to MEG specification
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
        testImplies006();
        testImplies007();
	}

	/**
	 * This method asserts that an object implies other object using the same uri actions
	 * 
	 * @spec DmtPermission.implies(Permission)
	 */
	private void testImplies001() {
		try {
			tbc.log("#testImplies001");
			DmtPermission permission = new DmtPermission(
					DmtConstants.OSGi_LOG, DmtConstants.ACTIONS);
			DmtPermission permission2 = new DmtPermission(
					DmtConstants.OSGi_LOG, DmtConstants.ACTIONS);
			tbc
					.assertTrue(
							"Asserts that an object implies other object using the same uri and actions",
							permission.implies(permission2));
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}

	/**
	 * Asserts that an object implies other object using the same uri and the actions it contains
	 * 
	 * @spec DmtPermission.implies(Permission)
	 */
	private void testImplies002() {
		try {
			tbc.log("#testImplies002");
			DmtPermission permission = new DmtPermission(
					DmtConstants.OSGi_LOG, DmtPermission.ADD + ","
							+ DmtPermission.GET);
			DmtPermission permission2 = new DmtPermission(
					DmtConstants.OSGi_LOG, DmtPermission.ADD);
			tbc
					.assertTrue(
							"Asserts that an object implies other object using the same uri and the actions it contains",
							permission.implies(permission2));
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}

	/**
	 * Asserts that an object doesn't imply other object using 
	 * the same uri and the actions it doesn't contain
	 * 
	 * @spec DmtPermission.implies(Permission)
	 */
	private void testImplies003() {
		try {
			tbc.log("#testImplies003");
			DmtPermission permission = new DmtPermission(
					DmtConstants.OSGi_LOG, DmtPermission.ADD);
			DmtPermission permission2 = new DmtPermission(
					DmtConstants.OSGi_LOG, DmtPermission.ADD + ","
							+ DmtPermission.GET);
			tbc
					.assertTrue(
							"Asserts that an object doesn't imply other object using the same uri and the actions it doesn't contain",
							!permission.implies(permission2));
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}

	/**
	 * Asserts that an object doesn't imply other object using different uris and the same actions
	 * 
	 * @spec DmtPermission.implies(Permission)
	 */
	private void testImplies004() {
		try {
			tbc.log("#testImplies004");
			DmtPermission permission = new DmtPermission(
					DmtConstants.OSGi_LOG, DmtPermission.ADD);
			DmtPermission permission2 = new DmtPermission(
					TestExecPluginActivator.ROOT, DmtPermission.ADD);
			tbc
					.assertTrue(
							"Asserts that an object implies other object using the same uri and the same actions",
							!permission.implies(permission2));
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}

	/**
	 * Asserts that an object imply other object using the same uri and actions but in a different order
	 * 
	 * @spec DmtPermission.implies(Permission)
	 */
	private void testImplies005() {
		try {
			tbc.log("#testImplies005");
			DmtPermission permission = new DmtPermission(
					DmtConstants.OSGi_LOG, DmtPermission.ADD + ","
							+ DmtPermission.REPLACE);
			DmtPermission permission2 = new DmtPermission(
					DmtConstants.OSGi_LOG, DmtPermission.REPLACE + ","
							+ DmtPermission.ADD);
			tbc
					.assertTrue(
							"Asserts that an object imply other object using the same uri and actions but in a different order",
							permission.implies(permission2));
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}
    
    /**
     * Asserts that an object implies other object when using wildcard to match the uri
     * 
     * @spec DmtPermission.implies(Permission)
     */
    private void testImplies006() {
        try {
            tbc.log("#testImplies006");
            DmtPermission permission = new DmtPermission(
                    TestExecPluginActivator.ROOT + "/a*", DmtPermission.ADD);
            DmtPermission permission2 = new DmtPermission(
                TestExecPluginActivator.ROOT + "/abc", DmtPermission.ADD);
            tbc
                    .assertTrue(
                            "Asserts that an object implies other object when using wildcard to match the uri",
                            permission.implies(permission2));
        } catch (Exception e) {
            tbc.fail("Unexpected exception: " + e.getClass().getName());
        }
    }
	/**
	 * Asserts that an object doesn't imply other object if they are from different types
	 * 
	 * @spec DmtPermission.implies(Permission)
	 */
	private void testImplies007() {
		try {
			tbc.log("#testImplies007");
			DmtPermission permission = new DmtPermission("*","*");
			AlertPermission permission2 = new AlertPermission("*","*");
			tbc.assertTrue(
						"Asserts that an object doesn't imply other object if they are from different types",
						!permission.implies(permission2));
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}
}

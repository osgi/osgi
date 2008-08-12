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
 * Jan 31, 2005  André Assad
 * 1             Implement MEG TCK
 * ============  ==============================================================
 * Feb 11, 2005  André Assad
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ============  ==============================================================
 * Mar 04, 2005  André Assad
 * 23            Updates after changes in the DmtAcl API
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tbc.DmtAcl;

import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * org.osgi.service.dmt.DmtAcl#setPermission This Test Class Validates the
 * implementation of
 * <code>setPermission<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class SetPermission {
	private DmtTestControl	tbc;

	/**
	 * @param tbc
	 */
	public SetPermission(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testSetPermission001();
		testSetPermission002();
		testSetPermission003();
		testSetPermission004();
		testSetPermission005();
	}

	/**
	 * This method asserts that a principal gets all the permissions previously
	 * set.
	 * 
	 * @spec DmtAcl.setPermission(String,int)
	 */
	public void testSetPermission001() {
		org.osgi.service.dmt.DmtAcl dmtAcl = new org.osgi.service.dmt.DmtAcl();

		dmtAcl = dmtAcl.setPermission(DmtTestControl.PRINCIPAL,
				org.osgi.service.dmt.DmtAcl.ADD
						| org.osgi.service.dmt.DmtAcl.EXEC);

		tbc.assertEquals("Asserting Get Permission",
				org.osgi.service.dmt.DmtAcl.ADD
						| org.osgi.service.dmt.DmtAcl.EXEC, dmtAcl
						.getPermissions(DmtTestControl.PRINCIPAL));
	}

	/**
	 * 
	 * This method asserts that a set of permissions the principal had is
	 * overriden by new ones.
	 * 
	 * @spec DmtAcl.setPermission(String,int)
	 */
	public void testSetPermission002() {
		org.osgi.service.dmt.DmtAcl dmtAcl = new org.osgi.service.dmt.DmtAcl(
				"Add=" + DmtTestControl.PRINCIPAL + "&Delete="
						+ DmtTestControl.PRINCIPAL);

		dmtAcl = dmtAcl.setPermission(DmtTestControl.PRINCIPAL,
				org.osgi.service.dmt.DmtAcl.ADD
						| org.osgi.service.dmt.DmtAcl.EXEC);

		tbc.assertEquals("Asserting Get Permission",
				org.osgi.service.dmt.DmtAcl.ADD
						| org.osgi.service.dmt.DmtAcl.EXEC, dmtAcl
						.getPermissions(DmtTestControl.PRINCIPAL));
	}

	/**
	 * 
	 * Asserts that an IllegalArgumentException is thrown whenever an invalid
	 * permission is set to a principal.
	 * 
	 * @spec DmtAcl.setPermission(String,int)
	 */
	public void testSetPermission003() {
		org.osgi.service.dmt.DmtAcl dmtAcl = new org.osgi.service.dmt.DmtAcl(
				"Add=" + DmtTestControl.PRINCIPAL + "&Delete="
						+ DmtTestControl.PRINCIPAL);

		try {
			dmtAcl = dmtAcl.setPermission(DmtTestControl.PRINCIPAL,
					org.osgi.service.dmt.DmtAcl.ADD | 2005);
			// should not reach this point
			tbc.fail("No Exception thrown");
		}
		catch (IllegalArgumentException e) {
			tbc.pass("IllegalArgumentException correctly thrown");
		}
	}

	/**
	 * 
	 * Asserts that an IllegalArgumentException is thrown whenever a valid
	 * permission is set to an invalid principal.
	 * 
	 * @spec DmtAcl.setPermission(String,int)
	 */
	public void testSetPermission004() {
		String principal = "www.cesar.org.br";
		org.osgi.service.dmt.DmtAcl dmtAcl = new org.osgi.service.dmt.DmtAcl(
				"Add=" + DmtTestControl.PRINCIPAL + "&Delete="
						+ DmtTestControl.PRINCIPAL);

		try {
			dmtAcl = dmtAcl.setPermission("INVALID_PRINCIPAL",
					org.osgi.service.dmt.DmtAcl.ADD
							| org.osgi.service.dmt.DmtAcl.REPLACE);
			// should not reach this point
			tbc.fail("No Exception thrown");
		}
		catch (IllegalArgumentException e) {
			tbc.pass("IllegalArgumentException correctly thrown");
		}
	}

	/**
	 * 
	 * Asserts that an IllegalArgumentException is thrown whenever a valid
	 * permission is set to a principal which was not created on the
	 * constructor.
	 * 
	 * @spec DmtAcl.setPermission(String,int)
	 */
	public void testSetPermission005() {
		org.osgi.service.dmt.DmtAcl dmtAcl = new org.osgi.service.dmt.DmtAcl(
				"Add=" + DmtTestControl.PRINCIPAL + "&Delete="
						+ DmtTestControl.PRINCIPAL + "&Exec="
						+ DmtTestControl.PRINCIPAL + "&Get=*");
		try {
			dmtAcl = dmtAcl.setPermission(DmtTestControl.PRINCIPAL_2,
					org.osgi.service.dmt.DmtAcl.ADD
							| org.osgi.service.dmt.DmtAcl.GET
							| org.osgi.service.dmt.DmtAcl.EXEC);

			tbc.assertEquals("Assert " + DmtTestControl.PRINCIPAL_2
					+ " permission.", org.osgi.service.dmt.DmtAcl.ADD
					| org.osgi.service.dmt.DmtAcl.GET
					| org.osgi.service.dmt.DmtAcl.EXEC, dmtAcl
					.getPermissions(DmtTestControl.PRINCIPAL_2));
		}
		catch (IllegalArgumentException e) {
			tbc.fail("Unexpected IllegalArgumentException");
		}
	}

	/**
	 * 
	 * Passes all permission and check if the permission set is the same as
	 * ALL_PERMISSION.
	 * 
	 * @spec DmtAcl.setPermission(String,int)
	 */
	public void testSetPermission006() {
		String principal = "www.cesar.org.br";
		org.osgi.service.dmt.DmtAcl dmtAcl = new org.osgi.service.dmt.DmtAcl(
				"Add=" + principal + "&Delete=" + principal + "&Exec="
						+ principal);
		try {
			dmtAcl.setPermission(principal, org.osgi.service.dmt.DmtAcl.ADD
					| org.osgi.service.dmt.DmtAcl.GET
					| org.osgi.service.dmt.DmtAcl.EXEC
					| org.osgi.service.dmt.DmtAcl.DELETE
					| org.osgi.service.dmt.DmtAcl.REPLACE);

			tbc.assertEquals("Assert " + principal + " all permissions",
					org.osgi.service.dmt.DmtAcl.ALL_PERMISSION, dmtAcl
							.getPermissions(principal));
		}
		catch (IllegalArgumentException e) {
			tbc.fail("Unexpected IllegalArgumentException");
		}
	}
}

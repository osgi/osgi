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
 */

package org.osgi.test.cases.dmt.tbc.DmtAcl;

import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * @author Andre Assad
 *
 * This Test Class Validates the implementation of
 *                     <code>deletePermission<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class DeletePermission {
	private DmtTestControl tbc;

	/**
	 * @param tbc
	 */
	public DeletePermission(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testDeletePermission001();
		testDeletePermission002();
		testDeletePermission003();
		testDeletePermission004();
		testDeletePermission005();
		testDeletePermission006();
	}

	/**
	 * This method asserts that a principal permission is
	 *                  correctly deleted from its Acl.
	 * @spec DmtAcl.deletePermission(String,int)
	 */
	public void testDeletePermission001() {
		String principal = "www.cesar.org.br";
		org.osgi.service.dmt.DmtAcl dmtAcl = new org.osgi.service.dmt.DmtAcl(
				"Add=" + principal + "&Delete=" + principal + "&Get=*");

		dmtAcl.deletePermission(principal, org.osgi.service.dmt.DmtAcl.DELETE);

		tbc.assertEquals("Asserting deleted permission",
				org.osgi.service.dmt.DmtAcl.ADD
						| org.osgi.service.dmt.DmtAcl.GET, dmtAcl
						.getPermissions(principal));
	}

	/**
	 * Test that more than one permission can be deleted in the
	 *                  same time from a principal.
	 * @spec DmtAcl.deletePermission(String,int)
	 */
	public void testDeletePermission002() {
		String principal = "www.cesar.org.br";
		org.osgi.service.dmt.DmtAcl dmtAcl = new org.osgi.service.dmt.DmtAcl(
				"Add=" + principal + "&Delete=" + principal + "&Exec="
						+ principal + "&Get=*");

		dmtAcl.deletePermission(principal, org.osgi.service.dmt.DmtAcl.DELETE
				| org.osgi.service.dmt.DmtAcl.EXEC);

		tbc.assertEquals("Asserting deleted permission",
				org.osgi.service.dmt.DmtAcl.ADD
						| org.osgi.service.dmt.DmtAcl.GET, dmtAcl
						.getPermissions(principal));
	}

	/**
	 * Asserts that an IllegalArgumentException is thrown
	 *                  whenever an invalid permission is deleted from a
	 *                  principal.
	 * @spec DmtAcl.deletePermission(String,int)
	 */
	public void testDeletePermission003() {
		String principal = "www.cesar.org.br";
		org.osgi.service.dmt.DmtAcl dmtAcl = new org.osgi.service.dmt.DmtAcl(
				"Add=" + principal + "&Delete=" + principal + "&Exec="
						+ principal + "&Get=*");
		try {
			dmtAcl.deletePermission(principal, 2005);
			// should not reach
			tbc.fail("No Exception thrown");
		} catch (IllegalArgumentException e) {
			tbc.pass("IllegalArgumentException correctly thrown");
		}
	}

	/**
	 * Asserts that an IllegalArgumentException is thrown
	 *                  whenever a permission which does not belong to a
	 *                  principal is deleted.
	 * @spec DmtAcl.deletePermission(String,int)
	 */
	public void testDeletePermission004() {
		String principal = "www.cesar.org.br";
		org.osgi.service.dmt.DmtAcl dmtAcl = new org.osgi.service.dmt.DmtAcl(
				"Add=" + principal + "&Delete=" + principal + "&Exec="
						+ principal + "&Get=*");
		try {
			dmtAcl.deletePermission(principal,
					org.osgi.service.dmt.DmtAcl.REPLACE);
			// should not reach
			tbc.fail("No Exception thrown");
		} catch (IllegalArgumentException e) {
			tbc.pass("IllegalArgumentException correctly thrown");
		}
	}

	/**
	 * Asserts that an IllegalArgumentException is thrown
	 *                  whenever a valid permission is deleted from an invalid
	 *                  principal.
	 * @spec DmtAcl.deletePermission(String,int)
	 */
	public void testDeletePermission005() {
		String principal = "www.cesar.org.br";
		org.osgi.service.dmt.DmtAcl dmtAcl = new org.osgi.service.dmt.DmtAcl(
				"Add=" + principal + "&Delete=" + principal + "&Exec="
						+ principal + "&Get=*");
		try {
			dmtAcl.deletePermission("INVALID_PRINCIPAL",
					org.osgi.service.dmt.DmtAcl.EXEC);
			// should not reach
			tbc.fail("No Exception thrown");
		} catch (IllegalArgumentException e) {
			tbc.pass("IllegalArgumentException correctly thrown");
		}
	}

	/**
	 * Asserts that an IllegalArgumentException is thrown
	 *                  whenever a valid permission is deleted from a principal
	 *                  which was not created on the constructor.
	 * @spec DmtAcl.deletePermission(String,int)
	 */
	public void testDeletePermission006() {
		String principal_1 = "www.cesar.org.br";
		String principal_2 = "www.cin.ufpe.br";
		org.osgi.service.dmt.DmtAcl dmtAcl = new org.osgi.service.dmt.DmtAcl(
				"Add=" + principal_1 + "&Delete=" + principal_1 + "&Exec="
						+ principal_1 + "&Get=*");
		org.osgi.service.dmt.DmtAcl dmtAcl2 = new org.osgi.service.dmt.DmtAcl(
				"Add=" + principal_2 + "&Delete=" + principal_2 + "&Exec="
						+ principal_2 + "&Get=*");
		try {
			dmtAcl.deletePermission(principal_2,
					org.osgi.service.dmt.DmtAcl.EXEC);
			// should not reach
			tbc.fail("No Exception thrown");
		} catch (IllegalArgumentException e) {
			tbc.pass("IllegalArgumentException correctly thrown");
		}
	}
}

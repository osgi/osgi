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
 * Jan 31, 2005  Andre Assad
 * 1             Implement MEG TCK
 * ============  ==============================================================
 * Feb 11, 2005  Andre Assad
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ============  ==============================================================
 * Mar 01, 2005  Luiz Felipe Guimaraes
 * 1             Updates 
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc1.tbc.Acl;

import info.dmtree.Acl;

import org.osgi.test.cases.dmt.tc1.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * This test case validates the implementation of <code>deletePermission<code> method of Acl, 
 * according to MEG specification
 */
public class DeletePermission extends DmtTestControl {

	private static final String ACL_DEFAULT = "Add=" + DmtConstants.PRINCIPAL
			+ "&Delete=" + DmtConstants.PRINCIPAL + "&Exec="
			+ DmtConstants.PRINCIPAL + "&Get=*";


	/**
	 * This method asserts that a principal permission is correctly deleted from its Acl.
	 * 
	 * @spec Acl.deletePermission(String,int)
	 */
	public void testDeletePermission001() {
		try {
			log("#testDeletePermission001");
			Acl acl = new Acl(ACL_DEFAULT);

			acl = acl.deletePermission(DmtConstants.PRINCIPAL,
					Acl.DELETE);

			assertEquals("Asserting deleted permission", Acl.ADD
					| Acl.EXEC | Acl.GET, acl
					.getPermissions(DmtConstants.PRINCIPAL));
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * Test that more than one permission can be deleted in the same time from a principal.
	 * 
	 * @spec Acl.deletePermission(String,int)
	 */
	public void testDeletePermission002() {
		try {
			log("#testDeletePermission002");
			Acl acl = new Acl(ACL_DEFAULT);

			acl = acl.deletePermission(DmtConstants.PRINCIPAL,
					Acl.DELETE | Acl.EXEC);

			assertEquals("Asserting deleted permission", Acl.ADD
					| Acl.GET, acl
					.getPermissions(DmtConstants.PRINCIPAL));
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * Asserts that an IllegalArgumentException is thrown whenever an invalid permission is 
	 * deleted from a principal.
	 * 
	 * @spec Acl.deletePermission(String,int)
	 */
	public void testDeletePermission003() {
		try {
			log("#testDeletePermission003");
			Acl acl = new Acl(ACL_DEFAULT);
			acl = acl.deletePermission(DmtConstants.PRINCIPAL, 2005);
			failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			pass("IllegalArgumentException correctly thrown");
		} catch (Exception e) {
			failExpectedOtherException(IllegalArgumentException.class, e);
		}
	}

	/**
	 * Asserts that an IllegalArgumentException is thrown whenever a valid permission is 
	 * deleted from an invalid principal.
	 * 
	 * @spec Acl.deletePermission(String,int)
	 */
	public void testDeletePermission004() {
		try {
			log("#testDeletePermission004");
			Acl acl = new Acl(ACL_DEFAULT);
			acl = acl.deletePermission(DmtConstants.INVALID,
					Acl.EXEC);
			failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			pass("IllegalArgumentException correctly thrown");
		} catch (Exception e) {
			failExpectedOtherException(IllegalArgumentException.class, e);
		}
	}

	/**
	 * Tests if this method creates a new instance of Acl without removing 
	 * any permissions from the existing Acl
	 * 
	 * @spec Acl.deletePermission(String,int)
	 */
	public void testDeletePermission005() {
		try {
			log("#testDeletePermission005");
			Acl acl = new Acl("Add=" + DmtConstants.PRINCIPAL
					+ "&Delete=" + DmtConstants.PRINCIPAL + "&Exec="
					+ DmtConstants.PRINCIPAL + "&Get=*");

			Acl acl2 = acl.deletePermission(DmtConstants.PRINCIPAL,
					Acl.EXEC);

			assertEquals("Assert " + DmtConstants.PRINCIPAL
					+ " permission", Acl.GET | Acl.ADD | Acl.EXEC
					| Acl.DELETE, acl
					.getPermissions(DmtConstants.PRINCIPAL));

			assertEquals(
					"Assert that '*' grants permission to all principals",
					Acl.GET, acl2
							.getPermissions(DmtConstants.PRINCIPAL_2));

			assertEquals("Assert " + DmtConstants.PRINCIPAL
					+ " permission", Acl.GET | Acl.ADD | Acl.DELETE,
					acl2.getPermissions(DmtConstants.PRINCIPAL));

			assertEquals(
					"Assert that '*' grants permission to all principals",
					Acl.GET, acl
							.getPermissions(DmtConstants.PRINCIPAL_2));
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * Asserts that an IllegalArgumentException is thrown when a globally granted 
	 * permission is revoked from a specific principal
	 * 
	 * @spec Acl.deletePermission(String,int)
	 */
	public void testDeletePermission006() {
		try {
			log("#testDeletePermission006");
			Acl acl = new Acl(ACL_DEFAULT);

			acl = acl.deletePermission(DmtConstants.PRINCIPAL,
					Acl.GET);
			failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			pass("IllegalArgumentException correctly thrown");
		} catch (Exception e) {
			failExpectedOtherException(IllegalArgumentException.class, e);
		}
	}

	/**
	 * Asserts if "*" revokes permissions from all principals.
	 * 
	 * @spec Acl.deletePermission(String,int)
	 */
	public void testDeletePermission007() {
		try {
			log("#testDeletePermission007");
			String[] principal = { DmtConstants.PRINCIPAL,
					DmtConstants.PRINCIPAL_2, "*" };
			int[] perm = { Acl.GET, Acl.EXEC, Acl.ADD };

			Acl acl = new Acl(principal, perm);
			acl = acl.deletePermission("*", Acl.ADD);
			boolean passed = false;

			if (acl.getPermissions(principal[0]) == (Acl.GET)) {
				if (acl.getPermissions(principal[1]) == (Acl.EXEC)) {
					passed = true;
				}
			}
			assertTrue(
					"Asserts if '*' revokes permissions from all principals.",
					passed);

		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

}

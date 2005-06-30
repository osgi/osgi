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
 * Mar 04, 2005  Andre Assad
 * 23            Update test cases according to changes in the DmtAcl API
 * ============  ==============================================================
 * Mar 24, 2005  Luiz Felipe Guimaraes
 * 23            Update test cases according to changes in the DmtAcl API
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.main.tbc.DmtAcl;

import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtAcl#DmtAcl, getPrincipals,
 *                  getPermissions
 * @generalDescription This Class Validates the implementation of
 *                     <code>DmtAcl<code> costructor, according to MEG reference
 *                     documentation (rfc0085).
 */
public class DmtAcl {
	private DmtTestControl tbc;

	public DmtAcl(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testDmtAcl001();
		testDmtAcl002();
		testDmtAcl003();
		testDmtAcl004();
		testDmtAcl005();
		testDmtAcl006();
		testDmtAcl007();
		testDmtAcl008();
		testDmtAcl009();
		testDmtAcl010();
		testDmtAcl011();
		testDmtAcl012();
		testDmtAcl013();
		testDmtAcl014();
		testDmtAcl015();
	}

	/**
	 * @testID testDmtAcl001
	 * @testDescription This method asserts that an instance of the ACL is
	 *                  created from its canonic string representation. It also
	 *                  tests getPermissions, and getPrincipals.
	 */
	private void testDmtAcl001() {

		try {
			tbc.log("#testDmtAcl001");
			org.osgi.service.dmt.DmtAcl dmtAcl = null;
			dmtAcl = new org.osgi.service.dmt.DmtAcl("Add="
					+ DmtTestControl.PRINCIPAL + "&Delete="
					+ DmtTestControl.PRINCIPAL + "&Get=*");
			tbc.assertEquals(
					"Asserting that all of the permissions were found",
					org.osgi.service.dmt.DmtAcl.ADD
							| org.osgi.service.dmt.DmtAcl.DELETE
							| org.osgi.service.dmt.DmtAcl.GET, dmtAcl
							.getPermissions(DmtTestControl.PRINCIPAL));

			boolean found = false;
			for (int i = 0; i < dmtAcl.getPrincipals().length && !found; i++) {
				String _principal = (String) dmtAcl.getPrincipals()[i];
				found = (_principal != null && _principal
						.equals(DmtTestControl.PRINCIPAL)) ? true : false;
			}
			tbc.assertTrue("Asserting that the principal was found", found);
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testDmtAcl002
	 * @testDescription This method asserts that an instance of the ACL is
	 *                  created from its canonic string representation. It also
	 *                  tests getPermissions, and getPrincipals.
	 */
	private void testDmtAcl002() {
		try {
			tbc.log("#testDmtAcl002");
			String[] principals = { DmtTestControl.PRINCIPAL,
					DmtTestControl.PRINCIPAL_2 };
			int[] perm = {
					org.osgi.service.dmt.DmtAcl.GET
							| org.osgi.service.dmt.DmtAcl.EXEC,
					org.osgi.service.dmt.DmtAcl.ADD
							| org.osgi.service.dmt.DmtAcl.REPLACE };
			org.osgi.service.dmt.DmtAcl dmtAcl = new org.osgi.service.dmt.DmtAcl(
					principals, perm);
			boolean passed = false;
			if (dmtAcl.getPermissions(principals[0]) == (org.osgi.service.dmt.DmtAcl.GET | org.osgi.service.dmt.DmtAcl.EXEC)) {
				if (dmtAcl.getPermissions(principals[1]) == (org.osgi.service.dmt.DmtAcl.ADD | org.osgi.service.dmt.DmtAcl.REPLACE)) {
					passed = true;
				}
			}

			int totalExpected = principals.length;
			int totalFound = 0;
			for (int i = 0; i < dmtAcl.getPrincipals().length; i++) {
				String principal = (String) dmtAcl.getPrincipals()[i];
				for (int j = 0; j < principals.length; j++) {
					if (principal.equals(principals[j])) {
						totalFound++;
					}
				}

			}
			tbc.assertEquals("Asserting that all of the principals were found",
					totalExpected, totalFound);
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testDmtAcl003
	 * @testDescription This method asserts that an ACL instance that represents
	 *                  an empty list of principals with no permissions is
	 *                  created.
	 */
	private void testDmtAcl003() {
		try {
			tbc.log("#testDmtAcl003");
			org.osgi.service.dmt.DmtAcl dmtAcl = new org.osgi.service.dmt.DmtAcl();
			String[] principals = dmtAcl.getPrincipals();
			int permissions = dmtAcl.getPermissions("*");

			tbc.assertNotNull("Principals are not null", principals);
			tbc.assertTrue("Asserting empty principals",
					(principals.length == 0));
			tbc.assertTrue("Asserting that no global permissions were granted",
					permissions == 0);
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testDmtAcl004
	 * @testDescription Checks if IllegalArgumentException is thrown on DmtAcl
	 *                  constructors.
	 */
	private void testDmtAcl004() {
		try {
			tbc.log("#testDmtAcl004");
			org.osgi.service.dmt.DmtAcl dmtAcl = new org.osgi.service.dmt.DmtAcl(
					DmtTestControl.INVALID);
			tbc.failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass("Exception correctly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName()
					+ " but was " + e.getClass().getName());
		}
	}

	/**
	 * @testID testDmtAcl005
	 * @testDescription Passes NULL as argument in the constructor and asserts
	 *                  that an empty list of principals with no permissions is
	 *                  created.
	 */

	private void testDmtAcl005() {
		try {
			tbc.log("#testDmtAcl005");
			org.osgi.service.dmt.DmtAcl dmtAcl = new org.osgi.service.dmt.DmtAcl(
					null);
			String[] principals = dmtAcl.getPrincipals();
			int permissions = dmtAcl.getPermissions("*");

			tbc.assertNotNull("Principals are not null", principals);
			tbc.assertTrue("Asserting empty principals",
					(principals.length == 0));
			tbc.assertTrue("Asserting that no global permissions were granted",
					permissions == 0);
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}

	}

	/**
	 * @testID testDmtAcl006
	 * @testDescription Validates a constructor of an DmtAcl object with more
	 *                  than one principal.
	 */
	private void testDmtAcl006() {
		try {
			tbc.log("#testDmtAcl006");
			String[] principal = { DmtTestControl.PRINCIPAL,
					DmtTestControl.PRINCIPAL_2 };
			org.osgi.service.dmt.DmtAcl dmtAcl = null;
			dmtAcl = new org.osgi.service.dmt.DmtAcl("Add=" + principal[0]
					+ "&Delete=" + principal[1] + "&Get=*");
			tbc.assertEquals("Asserting permissions of " + principal[0],
					org.osgi.service.dmt.DmtAcl.ADD
							| org.osgi.service.dmt.DmtAcl.GET, dmtAcl
							.getPermissions(principal[0]));
			tbc.assertEquals("Asserting permissions of " + principal[1],
					org.osgi.service.dmt.DmtAcl.DELETE
							| org.osgi.service.dmt.DmtAcl.GET, dmtAcl
							.getPermissions(principal[1]));

			int found = 0;
			for (int i = 0; i < dmtAcl.getPrincipals().length
					&& found < principal.length; i++) {
				String pr = (String) dmtAcl.getPrincipals()[i];
				for (int j = 0; j < principal.length; j++) {
					if (pr.equals(principal[j])) {
						found++;
					}
				}
			}
			tbc.assertEquals("All principals were found", principal.length,
					found);
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testDmtAcl007
	 * @testDescription Asserts that an IllegalArgumentException is thrown
	 *                  whenever an ACL is created from an acl String with an
	 *                  invalid permission code.
	 */
	private void testDmtAcl007() {
		try {
			tbc.log("#testDmtAcl007");
			org.osgi.service.dmt.DmtAcl dmtAcl = null;
			dmtAcl = new org.osgi.service.dmt.DmtAcl("Invalid="
					+ DmtTestControl.PRINCIPAL + "&Install="
					+ DmtTestControl.PRINCIPAL + "&Remove=*");
			tbc.failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass("Correctly caught IllegalArgumentException");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName()
					+ " but was " + e.getClass().getName());
		}
	}

	/**
	 * @testID testDmtAcl008
	 * @testDescription Asserts that an IllegalArgumentException is thrown
	 *                  whenever an ACL is created from an acl String with an
	 *                  invalid principal.
	 */
	private void testDmtAcl008() {
		try {
			tbc.log("#testDmtAcl008");
			org.osgi.service.dmt.DmtAcl dmtAcl = null;
			dmtAcl = new org.osgi.service.dmt.DmtAcl("Add="
					+ DmtTestControl.INVALID + "&Delete="
					+ DmtTestControl.INVALID + "&Get=*");
			tbc.failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass("Correctly caught IllegalArgumentException");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName()
					+ " but was " + e.getClass().getName());
		}
	}

	/**
	 * @testID testDmtAcl009
	 * @testDescription Test if an IllegalArgumentException is thrown when the
	 *                  DmtAcl constructors receives two arrays with different
	 *                  lenghts.
	 */
	private void testDmtAcl009() {
		try {
			tbc.log("#testDmtAcl009");
			String[] principal = { DmtTestControl.PRINCIPAL,
					DmtTestControl.PRINCIPAL_2 };
			int[] perm = { org.osgi.service.dmt.DmtAcl.GET
					| org.osgi.service.dmt.DmtAcl.DELETE
					| org.osgi.service.dmt.DmtAcl.ADD };

			org.osgi.service.dmt.DmtAcl dmtAcl = null;
			dmtAcl = new org.osgi.service.dmt.DmtAcl(principal, perm);
			tbc.failException("#", IllegalArgumentException.class);

		} catch (IllegalArgumentException e) {
			tbc.pass("Correctly caught IllegalArgumentException.");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName()
					+ " but was " + e.getClass().getName());
		}
	}

	/**
	 * @testID testDmtAcl010
	 * @testDescription Test if an IllegalArgumentException is thrown when the
	 *                  DmtAcl constructors receives an invalid permission array
	 *                  element.
	 */
	private void testDmtAcl010() {
		try {
			tbc.log("#testDmtAcl010");
			String[] principal = { DmtTestControl.PRINCIPAL,
					DmtTestControl.PRINCIPAL_2 };
			int[] perm = { org.osgi.service.dmt.DmtAcl.GET | 99 };
			org.osgi.service.dmt.DmtAcl dmtAcl = null;

			dmtAcl = new org.osgi.service.dmt.DmtAcl(principal, perm);
			tbc.failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass("Correctly caught IllegalArgumentException.");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName()
					+ " but was " + e.getClass().getName());
		}
	}

	/**
	 * @testID testDmtAcl011
	 * @testDescription Test if an IllegalArgumentException is thrown when the
	 *                  DmtAcl constructors receives an invalid principal array
	 *                  element.
	 */
	private void testDmtAcl011() {
		try {
			tbc.log("#testDmtAcl011");
			String[] principal = { DmtTestControl.PRINCIPAL,
					DmtTestControl.INVALID };
			int[] perm = { org.osgi.service.dmt.DmtAcl.GET,
					org.osgi.service.dmt.DmtAcl.EXEC };
			org.osgi.service.dmt.DmtAcl dmtAcl = null;
			dmtAcl = new org.osgi.service.dmt.DmtAcl(principal, perm);
			tbc.failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass("Correctly caught IllegalArgumentException.");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName()
					+ " but was " + e.getClass().getName());
		}
	}

	/**
	 * @testID testDmtAcl012
	 * @testDescription Test if "*" grants permissions to all principals
	 */
	private void testDmtAcl012() {
		try {
			tbc.log("#testDmtAcl012");
			String[] principal = { DmtTestControl.PRINCIPAL,
					DmtTestControl.PRINCIPAL_2, "*" };
			int[] perm = { org.osgi.service.dmt.DmtAcl.GET,
					org.osgi.service.dmt.DmtAcl.EXEC,
					org.osgi.service.dmt.DmtAcl.ADD };

			org.osgi.service.dmt.DmtAcl dmtAcl = new org.osgi.service.dmt.DmtAcl(
					principal, perm);

			boolean passed = false;

			if (dmtAcl.getPermissions(principal[0]) == (org.osgi.service.dmt.DmtAcl.GET | org.osgi.service.dmt.DmtAcl.ADD)) {
				if (dmtAcl.getPermissions(principal[1]) == (org.osgi.service.dmt.DmtAcl.EXEC | org.osgi.service.dmt.DmtAcl.ADD)) {
					passed = true;
				}
			}
			tbc.assertTrue(
					"Asserts if '*' grants permissions to all principals.",
					passed);

		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testDmtAcl013
	 * @testDescription Test if "*" grants permissions to all principals, even
	 *                  if it is not passed on the constructor
	 */
	private void testDmtAcl013() {
		try {
			tbc.log("#testDmtAcl013");
			String[] principal = { DmtTestControl.PRINCIPAL, "*" };
			int[] perm = { org.osgi.service.dmt.DmtAcl.GET,
					org.osgi.service.dmt.DmtAcl.ADD };

			org.osgi.service.dmt.DmtAcl dmtAcl = new org.osgi.service.dmt.DmtAcl(
					principal, perm);

			boolean passed = false;
			if (dmtAcl.getPermissions(principal[0]) == (org.osgi.service.dmt.DmtAcl.GET | org.osgi.service.dmt.DmtAcl.ADD)) {
				if (dmtAcl.getPermissions(DmtTestControl.PRINCIPAL_2) == org.osgi.service.dmt.DmtAcl.ADD) {
					passed = true;
				}
			}
			tbc.assertTrue(
					"Asserts if '*' grants permissions to all principals, "
							+ "even if it is not passed on the constructor.",
					passed);

		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testDmtAcl014
	 * @testDescription Test if an IllegalArgumentException is thrown if a
	 *                  principal appears multiple times in the principals array
	 */
	private void testDmtAcl014() {
		try {
			tbc.log("#testDmtAcl011");
			String[] principal = { DmtTestControl.PRINCIPAL,
					DmtTestControl.PRINCIPAL_2, DmtTestControl.PRINCIPAL };
			int[] perm = { org.osgi.service.dmt.DmtAcl.GET,
					org.osgi.service.dmt.DmtAcl.EXEC,
					org.osgi.service.dmt.DmtAcl.ADD };
			org.osgi.service.dmt.DmtAcl dmtAcl = new org.osgi.service.dmt.DmtAcl(
					principal, perm);
			tbc.failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc
					.pass("IllegalArgumentException was thrown when a principal appeared multiple times in the principals array");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName()
					+ " but was " + e.getClass().getName());
		}
	}

	/**
	 * @testID testDmtAcl015
	 * @testDescription Test if an IllegalArgumentException is thrown when the
	 *                  DmtAcl constructors receives an array of permissions
	 *                  with an invalid value.
	 */
	private void testDmtAcl015() {
		try {
			tbc.log("#testDmtAcl015");
			String[] principal = { DmtTestControl.PRINCIPAL,
					DmtTestControl.PRINCIPAL_2 };
			int[] perm = { org.osgi.service.dmt.DmtAcl.GET, 99};

			org.osgi.service.dmt.DmtAcl dmtAcl = null;
			dmtAcl = new org.osgi.service.dmt.DmtAcl(principal, perm);
			tbc.failException("#", IllegalArgumentException.class);

		} catch (IllegalArgumentException e) {
			tbc.pass("Correctly caught IllegalArgumentException.");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName()
					+ " but was " + e.getClass().getName());
		}
	}

}

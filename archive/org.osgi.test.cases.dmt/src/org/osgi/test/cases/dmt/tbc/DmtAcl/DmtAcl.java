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
 * 23            Update test cases according to changes in the DmtAcl API
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tbc.DmtAcl;

import org.osgi.test.cases.dmt.tbc.DmtTestControl;



/**
 * @author Andre Assad
 * 
 * This Class Validates the implementation of
 *                     <code>DmtAcl<code> costructor, according to MEG reference
 *                     documentation (rfc0085).
 * 
 */
public class DmtAcl {
	private DmtTestControl tbc;

	/**
	 * @param tbc
	 */
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
	}

	/**
	 * This method asserts that an instance of the ACL is
	 *                  created from its canonic string representation. It also
	 *                  tests getPermissions, and getPrincipals.
	 *                  
	 *  @spec DmtAcl.DmtAcl(String)
	 */
	public void testDmtAcl001() {
		String principal = "www.cesar.org.br";
		org.osgi.service.dmt.DmtAcl dmtAcl = null;
		try {
			dmtAcl = new org.osgi.service.dmt.DmtAcl("Add=" + principal
					+ "&Delete=" + principal + "&Get=*");
			tbc.assertEquals("Asserting Permissions",
					org.osgi.service.dmt.DmtAcl.ADD
							| org.osgi.service.dmt.DmtAcl.DELETE
							| org.osgi.service.dmt.DmtAcl.GET, dmtAcl
							.getPermissions(principal));

			boolean found = false;
			tbc.log("#Asserting principal value");
			for (int i = 0; i < dmtAcl.getPrincipals().length && !found; i++) {
				String _principal = (String) dmtAcl.getPrincipals()[i];
				found = (_principal != null && _principal.equals(principal)) ? true
						: false;
			}
			tbc.assertTrue("Asserting ACL principal found", found);
		} catch (IllegalArgumentException e) {
			tbc.fail("Exception should not have been thrown.");
		}
	}

	/**
	 * This method creates a new ACL from another predefined
	 *                  ACL, and asserts that it have got all the later ACL
	 *                  properties.
	 *  @spec DmtAcl.DmtAcl(String,int)
	 */
	public void testDmtAcl002() {
		String[] principal = { "www.cesar.org.br" };
		int[] perm = { org.osgi.service.dmt.DmtAcl.GET };
		org.osgi.service.dmt.DmtAcl newDmtAcl = null;
		try {
			newDmtAcl = new org.osgi.service.dmt.DmtAcl(principal, perm);
			tbc.assertEquals("Asserting Permissions",
					org.osgi.service.dmt.DmtAcl.GET, newDmtAcl
							.getPermissions(principal[0]));

			boolean found = false;
			tbc.log("#Asserting principal value");
			for (int i = 0; i < newDmtAcl.getPrincipals().length && !found; i++) {
				String _principal = (String) newDmtAcl.getPrincipals()[i];
				found = (_principal != null && _principal.equals(principal)) ? true
						: false;
			}
			tbc.assertTrue("Asserting ACL principal found", found);
		} catch (IllegalArgumentException e) {
			tbc.fail("Exception should not have been thrown.");
		}
	}

	/**
	 * This method asserts that an ACL instance that represents
	 *                  an empty list of principals with no permissions is
	 *                  created.
	 *  @spec DmtAcl.getPrincipals()
	 */
	public void testDmtAcl003() {
		org.osgi.service.dmt.DmtAcl dmtAcl = new org.osgi.service.dmt.DmtAcl();
		String[] principals = dmtAcl.getPrincipals();

		tbc.assertNotNull("Principals are not null", principals);
		tbc.assertTrue("Asserting empty principals", (principals.length <= 0));
	}

	/**
	 * Checks if IllegalArgumentException is thrown on
	 *                  DmtAcl(java.lang.String acl) constructors.
	 *  @spec DmtAcl.DmtAcl(String)
	 */
	public void testDmtAcl004() {
		String aclStr = "INVALID_ACL_STRING";
		try {
			tbc
					.log("#Checking DmtAcl(java.lang.String acl) IllegalArgumentException");
			org.osgi.service.dmt.DmtAcl dmtAcl = new org.osgi.service.dmt.DmtAcl(
					aclStr);
			tbc.failException("No Exception thrown",
					IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass("Exception correctly thrown");
		}
	}

	/**
	 * Passes NULL as argument in the constructor and asserts
	 *                  that an empty list of principals with no permissions is
	 *                  created.
	 * @spec DmtAcl.getPrincipals()
	 */
	/*
	 * Test is failing because DmtAcl(null) constructor is ambiguous. rfc0085
	 * says it should be allowed for DmtAcl(java.lang.String acl) constructor.
	 */
	public void testDmtAcl005() {
		  org.osgi.service.dmt.DmtAcl dmtAcl = null;
		try { 
			dmtAcl = new org.osgi.service.dmt.DmtAcl(null);
		} catch (IllegalArgumentException e) {
			tbc.fail("Exception should not have beenthrown.");
		}
		String[] principals = dmtAcl.getPrincipals();

		tbc.assertNotNull("Principals are not null", principals);
		tbc.assertTrue("Asserting empty principals", (principals.length<=0));
		 
	}

	/**
	 * 
	 * Validates a constructor of an DmtAcl object with more
	 *                  than one principal.
	 * @spec DmtAcl.DmtAcl(String)
	 */
	public void testDmtAcl006() {
		String[] principal = { "www.cesar.org.br", "www.cin.ufpe.br" };
		org.osgi.service.dmt.DmtAcl dmtAcl = null;
		try {
			dmtAcl = new org.osgi.service.dmt.DmtAcl("Add=" + principal[0]
					+ "&Delete=" + principal[1] + "&Get=*");
			tbc.assertEquals("Asserting Permissions of " + principal[0],
					org.osgi.service.dmt.DmtAcl.ADD
							| org.osgi.service.dmt.DmtAcl.GET, dmtAcl
							.getPermissions(principal[0]));
			tbc.assertEquals("Asserting Permissions of " + principal[1],
					org.osgi.service.dmt.DmtAcl.DELETE
							| org.osgi.service.dmt.DmtAcl.GET, dmtAcl
							.getPermissions(principal[1]));

			int found = 0;
			for (int i = 0; i < dmtAcl.getPrincipals().length && found < principal.length; i++) {
				String pr = (String) dmtAcl.getPrincipals()[i];
				for (int j = 0; j < principal.length; j++) {
					if (pr.equals(principal[j])) {
						found++;
					}
				}
			}
			tbc.assertEquals("All principals were found", principal.length,
					found);
		} catch (IllegalArgumentException e) {
			tbc.fail("Exception should not have been thrown.");
		}
	}

	/**
	 * 
	 * Asserts that an IllegalArgumentException is thrown
	 *                  whenever an ACL is created from an acl String with an
	 *                  invalid permission code.
	 *                  
	 * ### Why does the constructor not throw an exception?
	 * 
	 * @spec DmtAcl.getPermissions(String)
	 */
	public void testDmtAcl007() {
		String principal = "www.cesar.org.br";
		org.osgi.service.dmt.DmtAcl dmtAcl = null;
		dmtAcl = new org.osgi.service.dmt.DmtAcl("Invalid=" + principal
				+ "&Install=" + principal + "&Remove=*");
		try {
			int perm = dmtAcl.getPermissions(principal);
			// should not reach
			tbc
					.failException(
							"No Exception thrown when an invalid permission code is passed on the ACL string",
							IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass("Correctly caught IllegalArgumentException");
		}
	}

	/**
	 * 
	 * Asserts that an IllegalArgumentException is thrown
	 *                  whenever an ACL is created from an acl String with a
	 *                  null principal.
	 *                  
	 * @spec DmtAcl.DmtAcl(String)
	 */
	public void testDmtAcl008() {
		String principal = null;
		org.osgi.service.dmt.DmtAcl dmtAcl = null;
		dmtAcl = new org.osgi.service.dmt.DmtAcl("Add=" + principal
				+ "&Delete=" + principal + "&Get=*");
		try {
			int perm = dmtAcl.getPermissions(principal);
			// should not reach
			tbc
					.failException(
							"No Exception thrown when a null principal is passed on the ACL string",
							IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.fail("Correctly caught IllegalArgumentException");
		}
	}

	/**
	 * 
	 * Asserts that an IllegalArgumentException is thrown
	 *                  whenever an ACL is created from an acl String with an
	 *                  invalid principal.
	 *                  
	 * @spec DmtAcl.DmtAcl(String)
	 */
	public void testDmtAcl009() {
		String principal = "%INVALID*_PRINCIPAL_NÂME";
		org.osgi.service.dmt.DmtAcl dmtAcl = null;
		dmtAcl = new org.osgi.service.dmt.DmtAcl("Add=" + principal
				+ "&Delete=" + principal + "&Get=*");
		try {
			int perm = dmtAcl.getPermissions(principal);
			// should not reach
			tbc
					.failException(
							"No Exception thrown when an invalid principal name is passed on the ACL string",
							IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.fail("Correctly caught IllegalArgumentException");
		}
	}

	/**
	 * 
	 * Test if an IllegalArgumentException is thrown when the
	 *                  DmtAcl constructors receives two arrays with different
	 *                  lenghts.
	 *                  
	 * @spec DmtAcl.DmtAcl(String[],int)
	 */
	public void testDmtAcl010() {
		String[] principal = { "www.cesar.org.br", "www.cin.ufpe.br" };
		int[] perm = { org.osgi.service.dmt.DmtAcl.GET
				| org.osgi.service.dmt.DmtAcl.DELETE };
		org.osgi.service.dmt.DmtAcl newDmtAcl = null;
		try {
			newDmtAcl = new org.osgi.service.dmt.DmtAcl(principal, perm);
			tbc.failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass("Correctly caught IllegalArgumentException.");
		}
	}

	/**
	 * 
	 * Test if an IllegalArgumentException is thrown when the
	 *                  DmtAcl constructors receives an invalid permission array
	 *                  element.
	 * @spec DmtAcl.DmtAcl(String[],int)
	 */
	public void testDmtAcl011() {
		String[] principal = { "www.cesar.org.br", "www.cin.ufpe.br" };
		int[] perm = { org.osgi.service.dmt.DmtAcl.GET | 99 };
		org.osgi.service.dmt.DmtAcl newDmtAcl = null;
		try {
			newDmtAcl = new org.osgi.service.dmt.DmtAcl(principal, perm);
			tbc.failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass("Correctly caught IllegalArgumentException.");
		}
	}
	
	/**
	 * 
	 * Test if an IllegalArgumentException is thrown when the
	 *                  DmtAcl constructors receives an invalid principal array
	 *                  element.
	 * @spec DmtAcl.DmtAcl(String[],int)
	 */
	public void testDmtAcl012() {
		String[] principal = { "www.cesar.org.br", "%INVALID*_PRINCIPAL_NÂME" };
		int[] perm = { org.osgi.service.dmt.DmtAcl.GET, org.osgi.service.dmt.DmtAcl.EXEC};
		org.osgi.service.dmt.DmtAcl newDmtAcl = null;
		try {
			newDmtAcl = new org.osgi.service.dmt.DmtAcl(principal, perm);
			tbc.failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass("Correctly caught IllegalArgumentException.");
		}
	}
}

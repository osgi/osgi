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
 * 23            Update test cases according to changes in the Acl API
 * ============  ==============================================================
 * Mar 24, 2005  Luiz Felipe Guimaraes
 * 23            Update test cases according to changes in the Acl API
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc1.tbc.Acl;

import org.osgi.test.cases.dmt.tc1.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;


/**
 * @author Andre Assad
 * 
 * This Class Validates the implementation of <code>Acl<code> costructor, 
 * according to MEG specification
 */
public class Acl extends DmtTestControl {

	/**
	 * This method asserts that an instance of the ACL is created from its canonic string representation. 
	 * It also tests getPermissions, and getPrincipals.
	 * 
	 * @spec Acl.Acl(String)
	 */
	public void testAcl001() {

		try {
			log("#testAcl001");
			info.dmtree.Acl Acl = null;
			Acl = new info.dmtree.Acl("Add="
					+ DmtConstants.PRINCIPAL + "&Delete="
					+ DmtConstants.PRINCIPAL + "&Get=*");
			assertEquals(
					"Asserting that all of the permissions were found",
					info.dmtree.Acl.ADD
							| info.dmtree.Acl.DELETE
							| info.dmtree.Acl.GET, Acl
							.getPermissions(DmtConstants.PRINCIPAL));

			boolean found = false;
			for (int i = 0; i < Acl.getPrincipals().length && !found; i++) {
				String principal = Acl.getPrincipals()[i];
				found = (principal != null && principal
						.equals(DmtConstants.PRINCIPAL)) ? true : false;
			}
			assertTrue("Asserting that the principal was found", found);
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * This method asserts that an instance of the ACL is created from its canonic string representation. 
	 * It also tests getPermissions, and getPrincipals.
	 * 
	 * @spec Acl.Acl(String[],int[])
	 */
	public void testAcl002() {
		try {
			log("#testAcl002");
			String[] principals = { DmtConstants.PRINCIPAL,
					DmtConstants.PRINCIPAL_2 };
			int[] perm = {
					info.dmtree.Acl.GET
							| info.dmtree.Acl.EXEC,
					info.dmtree.Acl.ADD
							| info.dmtree.Acl.REPLACE };
			info.dmtree.Acl acl = new info.dmtree.Acl(
					principals, perm);
			boolean passed = false;
			if (acl.getPermissions(principals[0]) == (info.dmtree.Acl.GET | info.dmtree.Acl.EXEC)) {
				if (acl.getPermissions(principals[1]) == (info.dmtree.Acl.ADD | info.dmtree.Acl.REPLACE)) {
					passed = true;
				}
			}
            assertTrue(
					"All permissions granted to different principals were found",
					passed);

			int totalExpected = principals.length;
			int totalFound = 0;
			for (int i = 0; i < acl.getPrincipals().length; i++) {
				String principal = acl.getPrincipals()[i];
				for (int j = 0; j < principals.length; j++) {
					if (principal.equals(principals[j])) {
						totalFound++;
					}
				}

			}
			assertEquals("Asserting that all of the principals were found",
					totalExpected, totalFound);
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}



	/**
	 * Checks if IllegalArgumentException is thrown if acl is not a valid OMA DM ACL string
	 * 
	 * @spec Acl.Acl(String)
	 */
	public void testAcl003() {
		try {
			log("#testAcl003");
			new info.dmtree.Acl(DmtConstants.INVALID);
			failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			pass("Exception correctly thrown");
		} catch (Exception e) {
			failExpectedOtherException(
					IllegalArgumentException.class, e);
		}
	}

	/**
	 * Passes NULL as argument in the constructor and asserts that an empty list of 
	 * principals with no permissions is created.
	 * 
	 * @spec Acl.Acl(String)
	 */

	public void testAcl004() {
		try {
			log("#testAcl004");
			info.dmtree.Acl Acl = new info.dmtree.Acl(
					null);
			String[] principals = Acl.getPrincipals();
			int permissions = Acl.getPermissions("*");

			assertNotNull("Principals are not null", principals);
			assertTrue("Asserting empty principals",
					(principals.length == 0));
			assertTrue("Asserting that no global permissions were granted",
					permissions == 0);
		} catch (Exception e) {
			failUnexpectedException(e);
		}

	}

	/**
	 * Validates a constructor of an Acl object with more than one principal.
	 * 
	 * @spec Acl.Acl(String)
	 */
	public void testAcl005() {
		try {
			log("#testAcl005");
			String[] principal = { DmtConstants.PRINCIPAL,
					DmtConstants.PRINCIPAL_2 };
			info.dmtree.Acl Acl = null;
			Acl = new info.dmtree.Acl("Add=" + principal[0]
					+ "&Delete=" + principal[1] + "&Get=*");
			assertEquals("Asserting permissions of " + principal[0],
					info.dmtree.Acl.ADD
							| info.dmtree.Acl.GET, Acl
							.getPermissions(principal[0]));
			assertEquals("Asserting permissions of " + principal[1],
					info.dmtree.Acl.DELETE
							| info.dmtree.Acl.GET, Acl
							.getPermissions(principal[1]));

			int found = 0;
			for (int i = 0; i < Acl.getPrincipals().length
					&& found < principal.length; i++) {
				String pr = Acl.getPrincipals()[i];
				for (int j = 0; j < principal.length; j++) {
					if (pr.equals(principal[j])) {
						found++;
					}
				}
			}
			assertEquals("All principals were found", principal.length,
					found);
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * Asserts that an IllegalArgumentException is thrown whenever an ACL is 
	 * created from an acl String with an invalid permission code.
	 * 
	 * @spec Acl.Acl(String)
	 */
	public void testAcl006() {
		try {
			log("#testAcl006");
			
            new info.dmtree.Acl("Invalid="
					+ DmtConstants.PRINCIPAL + "&Install="
					+ DmtConstants.PRINCIPAL + "&Remove=*");
            
			failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			pass("Correctly caught IllegalArgumentException");
		} catch (Exception e) {
			failExpectedOtherException(IllegalArgumentException.class, e);
		}
	}

	/**
	 * Asserts that an IllegalArgumentException is thrown whenever an ACL is 
	 * created from an acl String with an invalid principal.
	 * 
	 * @spec Acl.Acl(String)
	 */
	public void testAcl007() {
		try {
			log("#testAcl007");
            
			new info.dmtree.Acl("Add="+ DmtConstants.INVALID + "&Delete="
					+ DmtConstants.INVALID + "&Get=*");
			failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			pass("Correctly caught IllegalArgumentException");
		} catch (Exception e) {
			failExpectedOtherException(IllegalArgumentException.class, e);
		}
	}

	/**
	 * Test if an IllegalArgumentException is thrown when the Acl constructors receives 
	 * two arrays with different lenghts.
	 * 
	 * @spec Acl.Acl(String[],int[])
	 */
	public void testAcl008() {
		try {
			log("#testAcl008");
			String[] principal = { DmtConstants.PRINCIPAL,
					DmtConstants.PRINCIPAL_2 };
			int[] perm = { info.dmtree.Acl.GET
					| info.dmtree.Acl.DELETE
					| info.dmtree.Acl.ADD };

			new info.dmtree.Acl(principal, perm);
            
			failException("#", IllegalArgumentException.class);

		} catch (IllegalArgumentException e) {
			pass("Correctly caught IllegalArgumentException.");
		} catch (Exception e) {
			failExpectedOtherException(IllegalArgumentException.class, e);
		}
	}

	/**
	 * Test if an IllegalArgumentException is thrown when the Acl constructors 
	 * receives an invalid permission array element.
	 * 
	 * @spec Acl.Acl(String[],int[])
	 */
	public void testAcl009() {
		try {
			log("#testAcl009");
			String[] principal = { DmtConstants.PRINCIPAL,
					DmtConstants.PRINCIPAL_2 };
			int[] perm = { info.dmtree.Acl.GET | 99 };
			
            new info.dmtree.Acl(principal, perm);
			
            failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			pass("Correctly caught IllegalArgumentException.");
		} catch (Exception e) {
			failExpectedOtherException(IllegalArgumentException.class, e);
		}
	}

	/**
	 * Test if an IllegalArgumentException is thrown when the Acl constructors receives 
	 * an invalid principal array element.
	 * 
	 * @spec Acl.Acl(String[],int[])
	 */
	public void testAcl010() {
		try {
			log("#testAcl010");
			String[] principal = { DmtConstants.PRINCIPAL,
					DmtConstants.INVALID };
			int[] perm = { info.dmtree.Acl.GET,
					info.dmtree.Acl.EXEC };
			
            new info.dmtree.Acl(principal, perm);
			
            failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			pass("Correctly caught IllegalArgumentException.");
		} catch (Exception e) {
			failExpectedOtherException(IllegalArgumentException.class, e);
		}
	}

	/**
	 * Test if "*" grants permissions to all principals
	 * 
	 * @spec Acl.Acl(String[],int[])
	 */
	public void testAcl011() {
		try {
			log("#testAcl011");
			String[] principal = { DmtConstants.PRINCIPAL,
					DmtConstants.PRINCIPAL_2, "*" };
			int[] perm = { info.dmtree.Acl.GET,
					info.dmtree.Acl.EXEC,
					info.dmtree.Acl.ADD };

			info.dmtree.Acl Acl = new info.dmtree.Acl(
					principal, perm);

			boolean passed = false;

			if (Acl.getPermissions(principal[0]) == (info.dmtree.Acl.GET | info.dmtree.Acl.ADD)) {
				if (Acl.getPermissions(principal[1]) == (info.dmtree.Acl.EXEC | info.dmtree.Acl.ADD)) {
					passed = true;
				}
			}
			assertTrue(
					"Asserts if '*' grants permissions to all principals.",
					passed);

		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * Test if "*" grants permissions to all principals, even if it is not passed on the constructor
	 * 
	 * @spec Acl.Acl(String[],int[])
	 */
	public void testAcl012() {
		try {
			log("#testAcl012");
			String[] principal = { DmtConstants.PRINCIPAL, "*" };
			int[] perm = { info.dmtree.Acl.GET,
					info.dmtree.Acl.ADD };

			info.dmtree.Acl Acl = new info.dmtree.Acl(
					principal, perm);

			boolean passed = false;
			if (Acl.getPermissions(principal[0]) == (info.dmtree.Acl.GET | info.dmtree.Acl.ADD)) {
				if (Acl.getPermissions(DmtConstants.PRINCIPAL_2) == info.dmtree.Acl.ADD) {
					passed = true;
				}
			}
			assertTrue(
					"Asserts if '*' grants permissions to all principals, "
							+ "even if it is not passed on the constructor.",
					passed);

		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * Test if an IllegalArgumentException is thrown if a principal appears multiple times in the principals array
	 * 
	 * @spec Acl.Acl(String[],int[])
	 */
	public void testAcl013() {
		try {
			log("#testAcl013");
			String[] principal = { DmtConstants.PRINCIPAL,
					DmtConstants.PRINCIPAL_2, DmtConstants.PRINCIPAL };
			int[] perm = { info.dmtree.Acl.GET,
					info.dmtree.Acl.EXEC,
					info.dmtree.Acl.ADD };
			
            new info.dmtree.Acl(principal, perm);
            
			failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			pass("IllegalArgumentException was thrown when a principal appeared multiple times in the principals array");
		} catch (Exception e) {
			failExpectedOtherException(IllegalArgumentException.class, e);
		}
	}

	
	/**
	 * Asserts if the constructor ignores principals with empty permissions.
	 * 
	 * @spec Acl.Acl(String[],int[])
	 */
	public void testAcl014() {
		try {
			log("#testAcl014");
			String[] principal = { DmtConstants.PRINCIPAL,
					DmtConstants.PRINCIPAL_2 };
			int[] perm = {
					info.dmtree.Acl.GET
							| info.dmtree.Acl.EXEC , };
			
			 info.dmtree.Acl acl = new info.dmtree.Acl(principal, perm);
			 
			 acl.getPermissions(principal[1]);
				failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			pass("Correctly caught IllegalArgumentException. The constructor is ignoring principals with empty permissions.");
		} catch (Exception e) {
			failExpectedOtherException(IllegalArgumentException.class, e);
		}
	}
    
    
    /**
     * Test if getPermissions '*' gets the permissions that are granted globally, to all principals 
     * 
     * @spec Acl.getPermissions(String)
     */
    public void testAcl015() {
        try {
            log("#testAcl015");
            info.dmtree.Acl acl = new info.dmtree.Acl("Add=*&Get=" + DmtConstants.PRINCIPAL +"&Replace="+DmtConstants.PRINCIPAL_2);
            
            int perm = acl.getPermissions("*");
            assertTrue(
					"Asserts that getPermissions '*' gets the permissions that are granted globally, to all principals", 
                perm==info.dmtree.Acl.ADD);

        } catch (Exception e) {
        	failUnexpectedException(e);
        }
    }
    
	/**
	 * Passes an empty string as argument in the constructor and asserts that an empty list of 
	 * principals with no permissions is created.
	 * 
	 * @spec Acl.Acl(String)
	 */

	public void testAcl016() {
		try {
			log("#testAcl016");
			info.dmtree.Acl Acl = new info.dmtree.Acl("");
			String[] principals = Acl.getPrincipals();
			int permissions = Acl.getPermissions("*");

			assertNotNull("Principals are not null", principals);
			
			assertTrue("Asserting empty principals", (principals.length == 0));
			
			assertTrue("Asserting that no global permissions were granted",
					permissions == 0);
		} catch (Exception e) {
			failUnexpectedException(e);
		}

	}
	
	/**
	 * Checks if IllegalArgumentException is thrown if the remote server id is not a valid OMA DM 
	 * ACL string (contains '=', '&', '*' or white-space characters). '+' is not in the list because
	 * it is interpreted as a different principal
	 * 
	 * @spec Acl.Acl(String)
	 */
	public void testAcl017() {
		char[] invalidAclChar = new char[] { '=','&','*',' ' };
		
		try {
			log("#testAcl017");
			for (int i = 0; i < invalidAclChar.length; i++) {
				try {
					new info.dmtree.Acl("Add=prin" + invalidAclChar[i] + "cipal");
					failException("", IllegalArgumentException.class);
				} catch (IllegalArgumentException e) {
					pass("IllegalArgumentException correctly thrown when creating an Acl "
							+
							"using the invalid character \"" + invalidAclChar[i] + "\" in the remote server id");
				}
			}
		} catch (Exception e) {
			failExpectedOtherException(IllegalArgumentException.class, e);
		}
	}
}


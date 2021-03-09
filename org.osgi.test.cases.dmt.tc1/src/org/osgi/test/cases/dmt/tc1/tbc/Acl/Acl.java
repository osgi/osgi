/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

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
			org.osgi.service.dmt.Acl Acl = null;
			Acl = new org.osgi.service.dmt.Acl("Add="
					+ DmtConstants.PRINCIPAL + "&Delete="
					+ DmtConstants.PRINCIPAL + "&Get=*");
			assertEquals(
					"Asserting that all of the permissions were found",
					org.osgi.service.dmt.Acl.ADD
							| org.osgi.service.dmt.Acl.DELETE
							| org.osgi.service.dmt.Acl.GET, Acl
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
					org.osgi.service.dmt.Acl.GET
							| org.osgi.service.dmt.Acl.EXEC,
					org.osgi.service.dmt.Acl.ADD
							| org.osgi.service.dmt.Acl.REPLACE };
			org.osgi.service.dmt.Acl acl = new org.osgi.service.dmt.Acl(
					principals, perm);
			boolean passed = false;
			if (acl.getPermissions(principals[0]) == (org.osgi.service.dmt.Acl.GET | org.osgi.service.dmt.Acl.EXEC)) {
				if (acl.getPermissions(principals[1]) == (org.osgi.service.dmt.Acl.ADD | org.osgi.service.dmt.Acl.REPLACE)) {
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
			new org.osgi.service.dmt.Acl(DmtConstants.INVALID);
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
			org.osgi.service.dmt.Acl Acl = new org.osgi.service.dmt.Acl(
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
			org.osgi.service.dmt.Acl Acl = null;
			Acl = new org.osgi.service.dmt.Acl("Add=" + principal[0]
					+ "&Delete=" + principal[1] + "&Get=*");
			assertEquals("Asserting permissions of " + principal[0],
					org.osgi.service.dmt.Acl.ADD
							| org.osgi.service.dmt.Acl.GET, Acl
							.getPermissions(principal[0]));
			assertEquals("Asserting permissions of " + principal[1],
					org.osgi.service.dmt.Acl.DELETE
							| org.osgi.service.dmt.Acl.GET, Acl
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
			
            new org.osgi.service.dmt.Acl("Invalid="
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
            
			new org.osgi.service.dmt.Acl("Add="+ DmtConstants.INVALID + "&Delete="
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
			int[] perm = { org.osgi.service.dmt.Acl.GET
					| org.osgi.service.dmt.Acl.DELETE
					| org.osgi.service.dmt.Acl.ADD };

			new org.osgi.service.dmt.Acl(principal, perm);
            
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
			int[] perm = { org.osgi.service.dmt.Acl.GET | 99 };
			
            new org.osgi.service.dmt.Acl(principal, perm);
			
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
			int[] perm = { org.osgi.service.dmt.Acl.GET,
					org.osgi.service.dmt.Acl.EXEC };
			
            new org.osgi.service.dmt.Acl(principal, perm);
			
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
			int[] perm = { org.osgi.service.dmt.Acl.GET,
					org.osgi.service.dmt.Acl.EXEC,
					org.osgi.service.dmt.Acl.ADD };

			org.osgi.service.dmt.Acl Acl = new org.osgi.service.dmt.Acl(
					principal, perm);

			boolean passed = false;

			if (Acl.getPermissions(principal[0]) == (org.osgi.service.dmt.Acl.GET | org.osgi.service.dmt.Acl.ADD)) {
				if (Acl.getPermissions(principal[1]) == (org.osgi.service.dmt.Acl.EXEC | org.osgi.service.dmt.Acl.ADD)) {
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
			int[] perm = { org.osgi.service.dmt.Acl.GET,
					org.osgi.service.dmt.Acl.ADD };

			org.osgi.service.dmt.Acl Acl = new org.osgi.service.dmt.Acl(
					principal, perm);

			boolean passed = false;
			if (Acl.getPermissions(principal[0]) == (org.osgi.service.dmt.Acl.GET | org.osgi.service.dmt.Acl.ADD)) {
				if (Acl.getPermissions(DmtConstants.PRINCIPAL_2) == org.osgi.service.dmt.Acl.ADD) {
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
			int[] perm = { org.osgi.service.dmt.Acl.GET,
					org.osgi.service.dmt.Acl.EXEC,
					org.osgi.service.dmt.Acl.ADD };
			
            new org.osgi.service.dmt.Acl(principal, perm);
            
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
					org.osgi.service.dmt.Acl.GET
							| org.osgi.service.dmt.Acl.EXEC , };
			
			 org.osgi.service.dmt.Acl acl = new org.osgi.service.dmt.Acl(principal, perm);
			 
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
            org.osgi.service.dmt.Acl acl = new org.osgi.service.dmt.Acl("Add=*&Get=" + DmtConstants.PRINCIPAL +"&Replace="+DmtConstants.PRINCIPAL_2);
            
            int perm = acl.getPermissions("*");
            assertTrue(
					"Asserts that getPermissions '*' gets the permissions that are granted globally, to all principals", 
                perm==org.osgi.service.dmt.Acl.ADD);

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
			org.osgi.service.dmt.Acl Acl = new org.osgi.service.dmt.Acl("");
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
					new org.osgi.service.dmt.Acl("Add=prin" + invalidAclChar[i] + "cipal");
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


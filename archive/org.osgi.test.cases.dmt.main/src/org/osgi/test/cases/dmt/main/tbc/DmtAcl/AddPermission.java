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

package org.osgi.test.cases.dmt.main.tbc.DmtAcl;

import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;
import org.osgi.service.dmt.DmtAcl;
/**
 * @author Andre Assad
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtAcl#addPermission
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>addPermission<code> method, according to MEG reference
 *                     documentation.
 */
public class AddPermission {
	private DmtTestControl tbc;
	
	private static final String ACL_DEFAULT = "Add=" + DmtTestControl.PRINCIPAL + "&Get=*";

	
	public AddPermission(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testAddPermission001();
		testAddPermission002();
		testAddPermission003();
		testAddPermission004();
		testAddPermission005();
		testAddPermission006();
	}

	/**
	 * @testID testAddPermission001
	 * @testDescription This method creates a new Acl and asserts that a new
	 *                  permission is correctly added to the Acl principal and
	 * 					that "*" grants permissions to all principals.
	 */
	private void testAddPermission001() {
		try {
			tbc.log("#testAddPermission001");
			DmtAcl dmtAcl = new DmtAcl(
					ACL_DEFAULT);
	
			dmtAcl =  dmtAcl.addPermission(DmtTestControl.PRINCIPAL, DmtAcl.DELETE);
	
			tbc.assertEquals("Asserting added permission",
					DmtAcl.ADD | DmtAcl.GET | DmtAcl.DELETE, dmtAcl
							.getPermissions(DmtTestControl.PRINCIPAL));
			tbc.assertEquals("Asserting that '*' grants permissions to all principals",
					DmtAcl.GET, dmtAcl.getPermissions(DmtTestControl.PRINCIPAL_2));			
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testAddPermission002
	 * @testDescription Test that more than one permission is added at the same
	 *                  time to a principal.
	 */
	private void testAddPermission002() {
		try {
			tbc.log("#testAddPermission002");
			DmtAcl dmtAcl = new DmtAcl(
					ACL_DEFAULT);
	
			dmtAcl = dmtAcl.addPermission(DmtTestControl.PRINCIPAL, DmtAcl.DELETE | DmtAcl.EXEC | DmtAcl.REPLACE);
	
			tbc.assertEquals("Asserting principal permissions",
					DmtAcl.ALL_PERMISSION, dmtAcl
							.getPermissions(DmtTestControl.PRINCIPAL));
			tbc.assertEquals("Asserting that '*' grants permissions to all principals",
					DmtAcl.GET, dmtAcl.getPermissions(DmtTestControl.PRINCIPAL_2));			
			
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testAddPermission003
	 * @testDescription Asserts that an IllegalArgumentException is thrown
	 *                  whenever an invalid permission is added to a principal.
	 */
	private void testAddPermission003() {
		try {
			tbc.log("#testAddPermission003");
			DmtAcl dmtAcl = new DmtAcl(ACL_DEFAULT);
			dmtAcl = dmtAcl.addPermission(DmtTestControl.PRINCIPAL, 2005);
			tbc.failException("#",IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass("IllegalArgumentException correctly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalArgumentException.class.getName()
					+ " but was " + e.getClass().getName());
		}		
	}

	/**
	 * @testID testAddPermission004
	 * @testDescription Asserts that an IllegalArgumentException is thrown
	 *                  whenever a valid permission is added to an invalid
	 *                  principal.
	 */
	private void testAddPermission004() {
		tbc.log("#testAddPermission004");
		DmtAcl dmtAcl = new DmtAcl(ACL_DEFAULT);
		try {
			dmtAcl = dmtAcl.addPermission(DmtTestControl.INVALID, DmtAcl.DELETE);
			tbc.failException("#",IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass("IllegalArgumentException correctly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalArgumentException.class.getName()
					+ " but was " + e.getClass().getName());
		}
	}
	/**
	 * @testID testAddPermission005
	 * @testDescription Test if two different principals has different permissions 
	 * 					using the same DmtAcl
	 */
	private void testAddPermission005() {
		try {
			tbc.log("#testAddPermission005");
			DmtAcl dmtAcl = new DmtAcl(
					"Add=" + DmtTestControl.PRINCIPAL + "&Delete=" + DmtTestControl.PRINCIPAL + "&Exec="
					+ DmtTestControl.PRINCIPAL + "&Replace=" + DmtTestControl.PRINCIPAL + "&Get=*");
			
			dmtAcl = dmtAcl.addPermission(DmtTestControl.PRINCIPAL_2, DmtAcl.GET
					| DmtAcl.EXEC);
			tbc.assertEquals("Assert " + DmtTestControl.PRINCIPAL_2 + " permission",
					DmtAcl.GET
							| DmtAcl.EXEC, dmtAcl.getPermissions(DmtTestControl.PRINCIPAL_2));
			tbc.assertEquals("Assert " + DmtTestControl.PRINCIPAL + " permission",
					DmtAcl.ALL_PERMISSION, dmtAcl.getPermissions(DmtTestControl.PRINCIPAL));
			
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}
	
	/**
	 * @testID testAddPermission006
	 * @testDescription Asserts if "*" grants permissions to all principals.
	 */
	private void testAddPermission006() {
		try {
			tbc.log("#testAddPermission006");
			String[] principal = { DmtTestControl.PRINCIPAL ,DmtTestControl.PRINCIPAL_2 };
			int[] perm = { DmtAcl.GET, DmtAcl.EXEC };
			
			DmtAcl dmtAcl = new DmtAcl(principal,perm);
			dmtAcl = dmtAcl.addPermission("*",DmtAcl.ADD);
			boolean passed = false;
			
			if (dmtAcl.getPermissions(principal[0]) == (DmtAcl.GET | DmtAcl.ADD) ) {
				if (dmtAcl.getPermissions(principal[1]) == (DmtAcl.EXEC | DmtAcl.ADD) ) {
					passed = true;
				}
			}
			tbc.assertTrue("Asserts if '*' grants permissions to all principals.",passed);
			
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}	

}

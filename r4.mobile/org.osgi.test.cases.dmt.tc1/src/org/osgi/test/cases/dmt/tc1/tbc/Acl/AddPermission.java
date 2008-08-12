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
 * This test case validates the implementation of <code>addPermission<code> method of Acl, 
 * according to MEG specification
 */
public class AddPermission {
	private DmtTestControl tbc;
	
	private static final String ACL_DEFAULT = "Add=" + DmtConstants.PRINCIPAL + "&Get=*";

	
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
	 * This method creates a new Acl and asserts that a new permission is correctly added to 
	 * the Acl principal and that "*" grants permissions to all principals.
	 * 
	 * @spec Acl.addPermission(String,int)
	 */
	private void testAddPermission001() {
		try {
			tbc.log("#testAddPermission001");
			Acl acl = new Acl(
					ACL_DEFAULT);
	
			acl =  acl.addPermission(DmtConstants.PRINCIPAL, Acl.DELETE);
	
			tbc.assertEquals("Asserting added permission",
					Acl.ADD | Acl.GET | Acl.DELETE, acl
							.getPermissions(DmtConstants.PRINCIPAL));
			tbc.assertEquals("Asserting that '*' grants permissions to all principals",
					Acl.GET, acl.getPermissions(DmtConstants.PRINCIPAL_2));			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		}
	}

	/**
	 * Test that more than one permission is added at the same time to a principal.
	 * 
	 * @spec Acl.addPermission(String,int)
	 */
	private void testAddPermission002() {
		try {
			tbc.log("#testAddPermission002");
			Acl acl = new Acl(
					ACL_DEFAULT);
	
			acl = acl.addPermission(DmtConstants.PRINCIPAL, Acl.DELETE | Acl.EXEC | Acl.REPLACE);
	
			tbc.assertEquals("Asserting principal permissions",
					Acl.ALL_PERMISSION, acl
							.getPermissions(DmtConstants.PRINCIPAL));
			tbc.assertEquals("Asserting that '*' grants permissions to all principals",
					Acl.GET, acl.getPermissions(DmtConstants.PRINCIPAL_2));			
			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		}
	}

	/**
	 * Asserts that an IllegalArgumentException is thrown whenever an invalid permission is added 
	 * to a principal.
	 * 
	 * @spec Acl.addPermission(String,int)
	 */
	private void testAddPermission003() {
		try {
			tbc.log("#testAddPermission003");
			Acl acl = new Acl(ACL_DEFAULT);
			acl = acl.addPermission(DmtConstants.PRINCIPAL, 2005);
			tbc.failException("#",IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass("IllegalArgumentException correctly thrown");
		} catch (Exception e) {
			tbc.failExpectedOtherException(IllegalArgumentException.class,e);
		}		
	}

	/**
	 * Asserts that an IllegalArgumentException is thrown whenever a valid permission 
	 * is added to an invalid principal.
	 * 
	 * @spec Acl.addPermission(String,int)
	 */
	private void testAddPermission004() {
		tbc.log("#testAddPermission004");
		Acl acl = new Acl(ACL_DEFAULT);
		try {
			acl = acl.addPermission(DmtConstants.INVALID, Acl.DELETE);
			tbc.failException("#",IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass("IllegalArgumentException correctly thrown");
		} catch (Exception e) {
			tbc.failExpectedOtherException(IllegalArgumentException.class,e);
		}
	}
	/**
	 * Test if two different principals has different permissions using the same Acl
	 * 
	 * @spec Acl.addPermission(String,int)
	 */
	private void testAddPermission005() {
		try {
			tbc.log("#testAddPermission005");
			Acl acl = new Acl(
					"Add=" + DmtConstants.PRINCIPAL + "&Delete=" + DmtConstants.PRINCIPAL + "&Exec="
					+ DmtConstants.PRINCIPAL + "&Replace=" + DmtConstants.PRINCIPAL + "&Get=*");
			
			acl = acl.addPermission(DmtConstants.PRINCIPAL_2, Acl.GET
					| Acl.EXEC);
			tbc.assertEquals("Assert " + DmtConstants.PRINCIPAL_2 + " permission",
					Acl.GET
							| Acl.EXEC, acl.getPermissions(DmtConstants.PRINCIPAL_2));
			tbc.assertEquals("Assert " + DmtConstants.PRINCIPAL + " permission",
					Acl.ALL_PERMISSION, acl.getPermissions(DmtConstants.PRINCIPAL));
			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		}
	}
	
	/**
	 * Asserts if "*" grants permissions to all principals.
	 * 
	 * @spec Acl.addPermission(String,int)
	 */
	private void testAddPermission006() {
		try {
			tbc.log("#testAddPermission006");
			String[] principal = { DmtConstants.PRINCIPAL ,DmtConstants.PRINCIPAL_2 };
			int[] perm = { Acl.GET, Acl.EXEC };
			
			Acl acl = new Acl(principal,perm);
			acl = acl.addPermission("*",Acl.ADD);
			boolean passed = false;
			
			if (acl.getPermissions(principal[0]) == (Acl.GET | Acl.ADD) ) {
				if (acl.getPermissions(principal[1]) == (Acl.EXEC | Acl.ADD) ) {
					passed = true;
				}
			}
			tbc.assertTrue("Asserts if '*' grants permissions to all principals.",passed);
			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		}
	}	

}

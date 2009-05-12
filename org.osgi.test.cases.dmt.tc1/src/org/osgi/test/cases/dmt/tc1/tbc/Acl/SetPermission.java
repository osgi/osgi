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
 * 23            Updates after changes in the Acl API
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc1.tbc.Acl;

import info.dmtree.Acl;

import org.osgi.test.cases.dmt.tc1.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;
/**
 * @author Andre Assad
 * 
 * This test case validates the implementation of <code>setPermission</code> method of Acl, 
 * according to MEG specification.
 */
public class SetPermission extends DmtTestControl {
	
	private static final String ACL_DEFAULT = "Add=" + DmtConstants.PRINCIPAL + "&Delete=" + DmtConstants.PRINCIPAL;


	/**
	 * This method asserts that a set of permissions the principal had is overriden by new ones.
	 * 
	 * @spec Acl.setPermission(String,int)
	 */
	public void testSetPermission001() {
		try {		
			log("#testSetPermission001");
			Acl acl = new Acl(ACL_DEFAULT);
	
			acl = acl.setPermission(DmtConstants.PRINCIPAL, Acl.ADD
					| Acl.EXEC);
	
			assertEquals("Asserting Get Permission", Acl.ADD | Acl.EXEC,
					acl.getPermissions(DmtConstants.PRINCIPAL));
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * Asserts that an IllegalArgumentException is thrown whenever an invalid 
	 * permission is set to a principal.
	 * 
	 * @spec Acl.setPermission(String,int)
	 */
	public void testSetPermission002() {
		try {
			log("#testSetPermission002");
			Acl acl = new Acl(ACL_DEFAULT);
			
			acl = acl.setPermission(DmtConstants.PRINCIPAL,Acl.ADD | 2005);

			failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			pass("IllegalArgumentException correctly thrown");
		} catch (Exception e) {
			failExpectedOtherException(IllegalArgumentException.class, e);
		}
	}

	/**
	 * Asserts that an IllegalArgumentException is thrown whenever a valid permission 
	 * is set to an invalid principal.
	 * 
	 * @spec Acl.setPermission(String,int)
	 */
	public void testSetPermission003() {
		try {
			log("#testSetPermission003");
			Acl acl = new Acl(ACL_DEFAULT);
			acl = acl.setPermission(DmtConstants.INVALID, Acl.ADD | Acl.REPLACE);

			failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			pass("IllegalArgumentException correctly thrown");
		} catch (Exception e) {
			failExpectedOtherException(IllegalArgumentException.class, e);
		}
	}

	/**
	 * Asserts that all permissions the principal had will be overwritten
	 * 
	 * @spec Acl.setPermission(String,int)
	 */
	public void testSetPermission004() {
		try {
			log("#testSetPermission004");
			Acl acl = new Acl(
					"Add=" + DmtConstants.PRINCIPAL + "&Delete=" + DmtConstants.PRINCIPAL + "&Get=*");
			
			acl = acl.setPermission(DmtConstants.PRINCIPAL, 
					Acl.EXEC | Acl.REPLACE | Acl.GET);
			
			assertEquals("Assert " + DmtConstants.PRINCIPAL + " permission.",
					Acl.REPLACE | Acl.GET | Acl.EXEC, 
					acl.getPermissions(DmtConstants.PRINCIPAL));
			
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * Asserts that IllegalArgumentException is thrown if a globally granted 
	 * permission is revoked from the principal
	 * 
	 * @spec Acl.setPermission(String,int)
	 */
	public void testSetPermission005() {
		try {
			log("#testSetPermission005");
			Acl acl = new Acl(
					"Add=" + DmtConstants.PRINCIPAL + "&Delete=" + DmtConstants.PRINCIPAL + "&Get=*");
			
			acl = acl.setPermission(DmtConstants.PRINCIPAL, Acl.EXEC
					| Acl.REPLACE);
			failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			pass("IllegalArgumentException correctly thrown");
		} catch (Exception e) {
			failExpectedOtherException(IllegalArgumentException.class, e);
		}
	}	
	/**
	 * Passes all permission and check if the permission set is the same as ALL_PERMISSION.
	 * 
	 * @spec Acl.setPermission(String,int) 
	 */
	public void testSetPermission006() {
		try {
			log("#testSetPermission006");
			Acl acl = new Acl(
					"Add=" + DmtConstants.PRINCIPAL + "&Delete=" + DmtConstants.PRINCIPAL + "&Exec=" + DmtConstants.PRINCIPAL );			
			acl = acl.setPermission(DmtConstants.PRINCIPAL, 
					Acl.ADD | Acl.GET | Acl.EXEC | Acl.DELETE | Acl.REPLACE);
			
			assertEquals("Assert " + DmtConstants.PRINCIPAL
					+ " all permissions",
					Acl.ALL_PERMISSION, acl.getPermissions(DmtConstants.PRINCIPAL));

		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}
	/**
	 * Asserts that '*' grants permissions to all principals
	 * 
	 * @spec Acl.setPermission(String,int)
	 */
	public void testSetPermission007() {
		try {
			log("#testSetPermission007");
			Acl acl = new Acl(
					"Add=" + DmtConstants.PRINCIPAL);			
			acl = acl.setPermission("*", Acl.ADD | Acl.GET | Acl.EXEC | 
					Acl.DELETE | Acl.REPLACE);
			
			assertEquals(
					"Assert that '*' grants permissions to all principals ["
							+ DmtConstants.PRINCIPAL + "]",
					Acl.ALL_PERMISSION, acl.getPermissions(DmtConstants.PRINCIPAL));
			assertEquals(
					"Assert that '*' grants permissions to all principals ["
							+ DmtConstants.PRINCIPAL_2 + "]",
					Acl.ALL_PERMISSION, acl.getPermissions(DmtConstants.PRINCIPAL_2));

		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}	

	
}

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

package org.osgi.test.cases.dmt.main.tbc.Acl;

import org.osgi.service.dmt.Acl;
import org.osgi.test.cases.dmt.main.tbc.DmtConstants;
import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;
/**
 * @author Andre Assad
 * 
 * This test case validates the implementation of <code>setPermission</code> method of Acl, 
 * according to MEG specification.
 */
public class SetPermission {
	private DmtTestControl tbc;
	
	private static final String ACL_DEFAULT = "Add=" + DmtConstants.PRINCIPAL + "&Delete=" + DmtConstants.PRINCIPAL;

	public SetPermission(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testSetPermission001();
		testSetPermission002();
		testSetPermission003();
		testSetPermission004();
		testSetPermission005();
		testSetPermission006();
		testSetPermission007();
		testSetPermission008();
	}

	/**
	 * This method asserts that a principal gets all the permissions previously set.
	 * 
	 * @spec Acl.setPermission(String,int)
	 */
	private void testSetPermission001() {
		try {
			tbc.log("#testSetPermission001");
			Acl acl = new Acl();
	
			acl = acl.setPermission(DmtConstants.PRINCIPAL, Acl.ADD | Acl.EXEC);
	
			tbc.assertEquals("Asserting Get Permission",Acl.ADD | Acl.EXEC, 
					acl.getPermissions(DmtConstants.PRINCIPAL));
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}

	/**
	 * This method asserts that a set of permissions the principal had is overriden by new ones.
	 * 
	 * @spec Acl.setPermission(String,int)
	 */
	private void testSetPermission002() {
		try {		
			tbc.log("#testSetPermission002");
			Acl acl = new Acl(ACL_DEFAULT);
	
			acl = acl.setPermission(DmtConstants.PRINCIPAL, Acl.ADD
					| Acl.EXEC);
	
			tbc.assertEquals("Asserting Get Permission",Acl.ADD | Acl.EXEC,
					acl.getPermissions(DmtConstants.PRINCIPAL));
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}

	/**
	 * Asserts that an IllegalArgumentException is thrown whenever an invalid 
	 * permission is set to a principal.
	 * 
	 * @spec Acl.setPermission(String,int)
	 */
	private void testSetPermission003() {
		try {
			tbc.log("#testSetPermission003");
			Acl acl = new Acl(ACL_DEFAULT);
			
			acl = acl.setPermission(DmtConstants.PRINCIPAL,Acl.ADD | 2005);

			tbc.failException("#",IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass("IllegalArgumentException correctly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalArgumentException.class.getName()
					+ " but was " + e.getClass().getName());
		}
	}

	/**
	 * Asserts that an IllegalArgumentException is thrown whenever a valid permission 
	 * is set to an invalid principal.
	 * 
	 * @spec Acl.setPermission(String,int)
	 */
	private void testSetPermission004() {
		try {
			tbc.log("#testSetPermission004");
			Acl acl = new Acl(ACL_DEFAULT);
			acl = acl.setPermission(DmtConstants.INVALID, Acl.ADD | Acl.REPLACE);

			tbc.failException("#",IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass("IllegalArgumentException correctly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalArgumentException.class.getName()
					+ " but was " + e.getClass().getName());
		}
	}

	/**
	 * Asserts that all permissions the principal had will be overwritten
	 * 
	 * @spec Acl.setPermission(String,int)
	 */
	private void testSetPermission005() {
		try {
			tbc.log("#testSetPermission005");
			Acl acl = new Acl(
					"Add=" + DmtConstants.PRINCIPAL + "&Delete=" + DmtConstants.PRINCIPAL + "&Get=*");
			
			acl = acl.setPermission(DmtConstants.PRINCIPAL, 
					Acl.EXEC | Acl.REPLACE | Acl.GET);
			
			tbc.assertEquals("Assert " + DmtConstants.PRINCIPAL + " permission.",
					Acl.REPLACE | Acl.GET | Acl.EXEC, 
					acl.getPermissions(DmtConstants.PRINCIPAL));
			
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}

	/**
	 * Asserts that IllegalArgumentException is thrown if a globally granted 
	 * permission is revoked from the principal
	 * 
	 * @spec Acl.setPermission(String,int)
	 */
	private void testSetPermission006() {
		try {
			tbc.log("#testSetPermission006");
			Acl acl = new Acl(
					"Add=" + DmtConstants.PRINCIPAL + "&Delete=" + DmtConstants.PRINCIPAL + "&Get=*");
			
			acl = acl.setPermission(DmtConstants.PRINCIPAL, Acl.EXEC
					| Acl.REPLACE);
			tbc.failException("#",IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass("IllegalArgumentException correctly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalArgumentException.class.getName()
					+ " but was " + e.getClass().getName());
		}
	}	
	/**
	 * Passes all permission and check if the permission set is the same as ALL_PERMISSION.
	 * 
	 * @spec Acl.setPermission(String,int) 
	 */
	private void testSetPermission007() {
		try {
			tbc.log("#testSetPermission007");
			Acl acl = new Acl(
					"Add=" + DmtConstants.PRINCIPAL + "&Delete=" + DmtConstants.PRINCIPAL + "&Exec=" + DmtConstants.PRINCIPAL );			
			acl = acl.setPermission(DmtConstants.PRINCIPAL, 
					Acl.ADD | Acl.GET | Acl.EXEC | Acl.DELETE | Acl.REPLACE);
			
			tbc.assertEquals("Assert " + DmtConstants.PRINCIPAL + " all permissions",
					Acl.ALL_PERMISSION, acl.getPermissions(DmtConstants.PRINCIPAL));

		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}
	/**
	 * Asserts that '*' grants permissions to all principals
	 * 
	 * @spec Acl.setPermission(String,int)
	 */
	private void testSetPermission008() {
		try {
			tbc.log("#testSetPermission008");
			Acl acl = new Acl(
					"Add=" + DmtConstants.PRINCIPAL);			
			acl = acl.setPermission("*", Acl.ADD | Acl.GET | Acl.EXEC | 
					Acl.DELETE | Acl.REPLACE);
			
			tbc.assertEquals("Assert that '*' grants permissions to all principals [" + DmtConstants.PRINCIPAL + "]",
					Acl.ALL_PERMISSION, acl.getPermissions(DmtConstants.PRINCIPAL));
			tbc.assertEquals("Assert that '*' grants permissions to all principals [" + DmtConstants.PRINCIPAL_2 + "]",
					Acl.ALL_PERMISSION, acl.getPermissions(DmtConstants.PRINCIPAL_2));

		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}	

	
}

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
 * 23            Updates after changes in the DmtAcl API
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.main.tbc.DmtAcl;

import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;
import org.osgi.service.dmt.DmtAcl;
/**
 * @author Andre Assad
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtAcl#setPermission
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>setPermission<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class SetPermission {
	private DmtTestControl tbc;
	
	private static final String ACL_DEFAULT = "Add=" + DmtTestControl.PRINCIPAL + "&Delete=" + DmtTestControl.PRINCIPAL;

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
	 * @testID testSetPermission001
	 * @testDescription This method asserts that a principal gets all the
	 *                  permissions previously set.
	 */
	private void testSetPermission001() {
		try {
			tbc.log("#testSetPermission001");
			DmtAcl dmtAcl = new DmtAcl();
	
			dmtAcl = dmtAcl.setPermission(DmtTestControl.PRINCIPAL, DmtAcl.ADD | DmtAcl.EXEC);
	
			tbc.assertEquals("Asserting Get Permission",DmtAcl.ADD | DmtAcl.EXEC, 
					dmtAcl.getPermissions(DmtTestControl.PRINCIPAL));
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testSetPermission002
	 * @testDescription This method asserts that a set of permissions the
	 *                  principal had is overriden by new ones.
	 */
	private void testSetPermission002() {
		try {		
			tbc.log("#testSetPermission002");
			DmtAcl dmtAcl = new DmtAcl(ACL_DEFAULT);
	
			dmtAcl = dmtAcl.setPermission(DmtTestControl.PRINCIPAL, DmtAcl.ADD
					| DmtAcl.EXEC);
	
			tbc.assertEquals("Asserting Get Permission",DmtAcl.ADD | DmtAcl.EXEC,
					dmtAcl.getPermissions(DmtTestControl.PRINCIPAL));
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testSetPermission003
	 * @testDescription Asserts that an IllegalArgumentException is thrown
	 *                  whenever an invalid permission is set to a principal.
	 */
	private void testSetPermission003() {
		try {
			tbc.log("#testSetPermission003");
			DmtAcl dmtAcl = new DmtAcl(ACL_DEFAULT);
			
			dmtAcl = dmtAcl.setPermission(DmtTestControl.PRINCIPAL,DmtAcl.ADD | 2005);

			tbc.failException("#",IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass("IllegalArgumentException correctly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalArgumentException.class.getName()
					+ " but was " + e.getClass().getName());
		}
	}

	/**
	 * @testID testSetPermission004
	 * @testDescription Asserts that an IllegalArgumentException is thrown
	 *                  whenever a valid permission is set to an invalid
	 *                  principal.
	 */
	private void testSetPermission004() {
		try {
			tbc.log("#testSetPermission004");
			DmtAcl dmtAcl = new DmtAcl(ACL_DEFAULT);
			dmtAcl = dmtAcl.setPermission(DmtTestControl.INVALID, DmtAcl.ADD | DmtAcl.REPLACE);

			tbc.failException("#",IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass("IllegalArgumentException correctly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalArgumentException.class.getName()
					+ " but was " + e.getClass().getName());
		}
	}

	/**
	 * @testID testSetPermission005
	 * @testDescription Asserts that all permissions the principal had will be overwritten
	 */
	private void testSetPermission005() {
		try {
			tbc.log("#testSetPermission005");
			DmtAcl dmtAcl = new DmtAcl(
					"Add=" + DmtTestControl.PRINCIPAL + "&Delete=" + DmtTestControl.PRINCIPAL + "&Get=*");
			
			dmtAcl = dmtAcl.setPermission(DmtTestControl.PRINCIPAL, 
					DmtAcl.EXEC | DmtAcl.REPLACE | DmtAcl.GET);
			
			tbc.assertEquals("Assert " + DmtTestControl.PRINCIPAL + " permission.",
					DmtAcl.REPLACE | DmtAcl.GET | DmtAcl.EXEC, 
					dmtAcl.getPermissions(DmtTestControl.PRINCIPAL));
			
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testSetPermission006
	 * @testDescription Asserts that IllegalArgumentException is thrown if
	 * 					a globally granted permission is revoked from the principal

	 */
	private void testSetPermission006() {
		try {
			tbc.log("#testSetPermission006");
			DmtAcl dmtAcl = new DmtAcl(
					"Add=" + DmtTestControl.PRINCIPAL + "&Delete=" + DmtTestControl.PRINCIPAL + "&Get=*");
			
			dmtAcl = dmtAcl.setPermission(DmtTestControl.PRINCIPAL, DmtAcl.EXEC
					| DmtAcl.REPLACE);
			tbc.failException("#",IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass("IllegalArgumentException correctly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalArgumentException.class.getName()
					+ " but was " + e.getClass().getName());
		}
	}	
	/**
	 * @testID testSetPermission007
	 * @testDescription Passes all permission and check if the permission set is the same as ALL_PERMISSION. 
	 */
	private void testSetPermission007() {
		try {
			tbc.log("#testSetPermission007");
			DmtAcl dmtAcl = new DmtAcl(
					"Add=" + DmtTestControl.PRINCIPAL + "&Delete=" + DmtTestControl.PRINCIPAL + "&Exec=" + DmtTestControl.PRINCIPAL );			
			dmtAcl = dmtAcl.setPermission(DmtTestControl.PRINCIPAL, 
					DmtAcl.ADD | DmtAcl.GET | DmtAcl.EXEC | DmtAcl.DELETE | DmtAcl.REPLACE);
			
			tbc.assertEquals("Assert " + DmtTestControl.PRINCIPAL + " all permissions",
					DmtAcl.ALL_PERMISSION, dmtAcl.getPermissions(DmtTestControl.PRINCIPAL));

		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}
	/**
	 * @testID testSetPermission008
	 * @testDescription Asserts that '*' grants permissions to all principals
	 */
	private void testSetPermission008() {
		try {
			tbc.log("#testSetPermission008");
			DmtAcl dmtAcl = new DmtAcl(
					"Add=" + DmtTestControl.PRINCIPAL);			
			dmtAcl = dmtAcl.setPermission("*", DmtAcl.ADD | DmtAcl.GET | DmtAcl.EXEC | 
					DmtAcl.DELETE | DmtAcl.REPLACE);
			
			tbc.assertEquals("Assert that '*' grants permissions to all principals [" + DmtTestControl.PRINCIPAL + "]",
					DmtAcl.ALL_PERMISSION, dmtAcl.getPermissions(DmtTestControl.PRINCIPAL));
			tbc.assertEquals("Assert that '*' grants permissions to all principals [" + DmtTestControl.PRINCIPAL_2 + "]",
					DmtAcl.ALL_PERMISSION, dmtAcl.getPermissions(DmtTestControl.PRINCIPAL_2));			

		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}	

	
}

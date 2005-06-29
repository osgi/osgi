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
 */

package org.osgi.test.cases.dmt.tbc.DmtAcl;

import org.osgi.service.dmt.DmtAcl;
import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * This Test Class Validates the implementation of
 *                     <code>isPermitted<code> method, according to MEG reference
 *                     documentation.
 */
public class IsPermitted {
	private DmtTestControl tbc;

	private DmtAcl dmtAcl;

	/**
	 * @param arg0
	 */
	public IsPermitted(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testIsPermitted001();
		testIsPermitted002();
		testIsPermitted003();
		testIsPermitted004();
		testIsPermitted005();
		testIsPermitted006();
	}

	/**
	 * This method tests if a given permission is granted to a
	 *                  principal.
	 *                  
	 * @spec DmtAcl.isPermitted(String,int)
	 */
	public void testIsPermitted001() {
		String principal = "www.cesar.org.br";
		dmtAcl = new org.osgi.service.dmt.DmtAcl("Add=" + principal
				+ "&Delete=" + principal + "&Get=*");
		tbc.assertTrue("Asserting Get Permission", dmtAcl.isPermitted(
				principal, org.osgi.service.dmt.DmtAcl.GET));
	}

	/**
	 * This method asserts that a given permission is not
	 *                  granted to a principal.
	 *                  
	 * @spec DmtAcl.isPermitted(String,int)
	 */
	public void testIsPermitted002() {
		String principal = "www.cesar.org.br";
		dmtAcl = new org.osgi.service.dmt.DmtAcl("Add=" + principal
				+ "&Delete=" + principal + "&Get=*");
		tbc.assertTrue("Asserting Get Permission", !dmtAcl.isPermitted(
				principal, org.osgi.service.dmt.DmtAcl.EXEC));
	}

	/**
	 * Asserts that an IllegalArgumentException is thrown
	 *                  whenever an invalid permission code is passed to
	 *                  isPermitted(java.lang.String principal, int
	 *                  permissions).
	 *                  
	 * @spec DmtAcl.isPermitted(String,int)
	 */
	public void testIsPermitted003() {
		String principal = "www.cesar.org.br";
		dmtAcl = new org.osgi.service.dmt.DmtAcl("Add=" + principal
				+ "&Delete=" + principal + "&Get=*");
		try {
			dmtAcl.isPermitted(principal, 2005);
			// should not reach
			tbc
					.fail("No Exception thrown on isPermitted when an invalid permission code is passed");
		} catch (IllegalArgumentException e) {
			tbc
					.pass("IllegalArgumentException correctly thrown on isPermitted method");
		}
	}

	/**
	 * 
	 *  Asserts that an IllegalArgumentException is thrown
	 *                  whenever a valid permission code and an invalid
	 *                  principal is passed isPermitted(java.lang.String
	 *                  principal, int permissions).
	 *                  
	 * @spec DmtAcl.isPermitted(String,int)
	 */
	public void testIsPermitted004() {
		String principal = "www.cesar.org.br";
		dmtAcl = new org.osgi.service.dmt.DmtAcl("Add=" + principal
				+ "&Delete=" + principal + "&Get=*");
		try {
			dmtAcl.isPermitted("*INVALID_&PRINCIPÁL", DmtAcl.DELETE | DmtAcl.GET);
			// should not reach
			tbc
					.fail("No Exception thrown on isPermitted when an invalid principal is passed");
		} catch (IllegalArgumentException e) {
			tbc
					.pass("IllegalArgumentException correctly thrown on isPermitted method");
		}
	}

	/**
	 * 
	 *  Asserts that an IllegalArgumentException is thrown
	 *                  whenever a valid permission code and null principal is
	 *                  passed isPermitted(java.lang.String principal, int
	 *                  permissions).
	 */
	public void testIsPermitted005() {
		String principal = "www.cesar.org.br";
		dmtAcl = new org.osgi.service.dmt.DmtAcl("Add=" + principal
				+ "&Delete=" + principal + "&Get=*");
		try {
			dmtAcl.isPermitted(null, DmtAcl.DELETE | DmtAcl.GET);
			// should not reach
			tbc
					.fail("No Exception thrown on isPermitted when a null principal code is passed");
		} catch (IllegalArgumentException e) {
			tbc
					.pass("IllegalArgumentException correctly thrown on isPermitted method");
		}
	}
	
	/**
	 * 
	 *  Asserts that an IllegalArgumentException is thrown
	 *                  whenever a valid permission is deleted from a principal
	 *                  which was not created on the constructor.
	 * @spec DmtAcl.isPermitted(String,int)
	 */
	public void testIsPermitted006() {
		String principal_1 = "www.cesar.org.br";
		String principal_2 = "www.cin.ufpe.br";
		org.osgi.service.dmt.DmtAcl dmtAcl = new org.osgi.service.dmt.DmtAcl(
				"Add=" + principal_1 + "&Delete=" + principal_1 + "&Exec="
						+ principal_1 + "&Get=*");
		org.osgi.service.dmt.DmtAcl dmtAcl2 = new org.osgi.service.dmt.DmtAcl(
				"Add=" + principal_2 + "&Delete=" + principal_2 + "&Exec="
						+ principal_2 + "&Get=*");
		try {
			dmtAcl.isPermitted(principal_2,
					org.osgi.service.dmt.DmtAcl.EXEC);
			// should not reach
			tbc.fail("No Exception thrown on isPermitted when it access a permission from a principal which was not created on the constructor");
		} catch (IllegalArgumentException e) {
			tbc.pass("IllegalArgumentException correctly thrown");
		}
	}
}

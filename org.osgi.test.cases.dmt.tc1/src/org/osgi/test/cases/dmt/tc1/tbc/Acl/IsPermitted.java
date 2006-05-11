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
 */

package org.osgi.test.cases.dmt.tc1.tbc.Acl;

import info.dmtree.Acl;
import org.osgi.test.cases.dmt.tc1.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * This test case validates the implementation of <code>isPermitted<code> method of Acl, 
 * according to MEG specification
 */
public class IsPermitted {
	private DmtTestControl tbc;
	private static final String ACL_DEFAULT = "Add=" + DmtConstants.PRINCIPAL
			+ "&Delete=" + DmtConstants.PRINCIPAL + "&Get=*";

	public IsPermitted(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testIsPermitted001();
		testIsPermitted002();
		testIsPermitted003();
		testIsPermitted004();
	}

	/**
	 * This method tests if a given permission is granted to a principal.
	 * 
	 * @spec Acl.isPermitted(String,int)
	 */
	private void testIsPermitted001() {
		try {
			tbc.log("#testIsPermitted001");
			Acl acl = new Acl(ACL_DEFAULT);
			tbc.assertTrue("Asserting that isPermitted returns true only for permissions granted", acl.isPermitted(
					DmtConstants.PRINCIPAL, Acl.GET | Acl.ADD | Acl.DELETE));
			tbc.assertTrue("Asserting that isPermitted returns false for permissions not granted", !acl.isPermitted(
					DmtConstants.PRINCIPAL, Acl.EXEC | Acl.REPLACE));			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		}
	}

	/**
	 * Asserts that an IllegalArgumentException is thrown whenever an invalid 
	 * permission code is passed to isPermitted(String principal, int permissions).
	 * 
	 * @spec Acl.isPermitted(String,int)
	 */
	private void testIsPermitted002() {
		try {
			tbc.log("#testIsPermitted002");
			Acl acl = new Acl(ACL_DEFAULT);
			acl.isPermitted(DmtConstants.PRINCIPAL, 2005);

			tbc.failException("#",IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass("IllegalArgumentException correctly thrown on isPermitted method");
		} catch (Exception e) {
			tbc.failExpectedOtherException(IllegalArgumentException.class,e);
		}

	}

	/**
	 * Asserts that an IllegalArgumentException is thrown whenever a valid permission code 
	 * and an invalid principal is passed isPermitted(String principal, int permissions).
	 * 
	 * @spec Acl.isPermitted(String,int)
	 */
	private void testIsPermitted003() {
		try {
			tbc.log("#testIsPermitted003");
			Acl acl = new Acl(ACL_DEFAULT);
			
			acl.isPermitted(DmtConstants.INVALID, Acl.DELETE | Acl.GET);
			tbc.failException("#",IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass("IllegalArgumentException correctly thrown on isPermitted method");
		} catch (Exception e) {
			tbc.failExpectedOtherException(IllegalArgumentException.class,e);
		}
	}

	/**
	 * Asserts global permissions
	 * 
	 * @spec Acl.isPermitted(String,int)
	 */
	private void testIsPermitted004() {
		try {
			tbc.log("#testIsPermitted004");
			Acl acl = new Acl(ACL_DEFAULT);
			
			tbc.assertTrue("Asserting that '*' returns true if the permission is global", acl.isPermitted("*", Acl.GET));
			tbc.assertTrue("Asserting that '*' returns false if the permission is not global", !acl.isPermitted("*", Acl.ADD | Acl.DELETE | Acl.EXEC | Acl.REPLACE));
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		}
	}
}

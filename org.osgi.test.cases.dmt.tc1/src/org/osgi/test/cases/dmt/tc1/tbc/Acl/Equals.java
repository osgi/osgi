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
 * May 25, 2005  Luiz Felipe Guimaraes
 * 1             Implement MEG TCK
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc1.tbc.Acl;

import info.dmtree.Acl;

import org.osgi.test.cases.dmt.tc1.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;
/**
 * @author Luiz Felipe Guimaraes
 * 
 * This test case validates the implementation of <code>equals</code> method of Acl, 
 * according to MEG specification
 */
public class Equals extends DmtTestControl {
	
	/**
	 * Asserts that two Acl are equal when they have the same set of principals and permissions
	 * 
	 * @spec Acl.equals(Object)
	 */
	public void testEquals001() {
		try {
			log("#testEquals001");
			String[] principals = { DmtConstants.PRINCIPAL, DmtConstants.PRINCIPAL_2 } ;
			int[] permissions = { Acl.ADD, Acl.GET };
			Acl Acl = new Acl(principals,permissions);
			Acl Acl2 = new Acl(principals,permissions);

			assertTrue(
					"Asserts that two Acl are equal when they have the same set of principals and permissions",
					Acl.equals(Acl2));
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * Asserts that two Acl are different when they have the same set of permissions but different principals
	 * 
	 * @spec Acl.equals(Object)
	 */
	public void testEquals002() {
		try {
			log("#testEquals002");
			String[] principals = { DmtConstants.PRINCIPAL, DmtConstants.PRINCIPAL_2 } ;
			String[] principals2 = { DmtConstants.PRINCIPAL, DmtConstants.PRINCIPAL_3 } ;
			int[] permissionsBoth = { Acl.ADD, Acl.GET };
			Acl Acl = new Acl(principals,permissionsBoth);
			Acl Acl2 = new Acl(principals2,permissionsBoth);
			assertTrue(
					"Asserts that two Acl are different when they have the same set of permissions but different principals",
					!Acl.equals(Acl2));
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}	
	/**
	 * Asserts that two Acl are different when they have the same set of principals but different permissions
	 * 
	 * @spec Acl.equals(Object)
	 */
	public void testEquals003() {
		try {
			log("#testEquals003");
			String[] principalsBoth = { DmtConstants.PRINCIPAL, DmtConstants.PRINCIPAL_2 } ;
			int[] permissions = { Acl.ADD, Acl.GET };
			int[] permissions2 = { Acl.GET, Acl.ADD };
			Acl Acl = new Acl(principalsBoth,permissions);
			Acl Acl2 = new Acl(principalsBoth,permissions2);
			assertTrue(
					"Asserts that two Acl are different when they have the same set of principals but different permissions",
					!Acl.equals(Acl2));
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

}

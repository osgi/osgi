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

package org.osgi.test.cases.dmt.main.tbc.DmtAcl;

import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;
import org.osgi.service.dmt.DmtAcl;
/**
 * @author Luiz Felipe Guimaraes
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtAcl#equals
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>equals<code> method, according to MEG reference
 *                     documentation.
 */
public class Equals {
	private DmtTestControl tbc;
	
	public Equals(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testEquals001();
		testEquals002();
		testEquals003();
	}

	/**
	 * @testID testEquals001
	 * @testDescription Asserts that two DmtAcl are equal when they have the same set of principals and permissions
	 */
	private void testEquals001() {
		try {
			tbc.log("#testEquals001");
			String[] principals = { DmtTestControl.PRINCIPAL, DmtTestControl.PRINCIPAL_2 } ;
			int[] permissions = { DmtAcl.ADD, DmtAcl.GET };
			DmtAcl dmtAcl = new DmtAcl(principals,permissions);
			DmtAcl dmtAcl2 = new DmtAcl(principals,permissions);

			tbc.assertTrue("Asserts that two DmtAcl are equal when they have the same set of principals and permissions",dmtAcl.equals(dmtAcl2));
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testEquals002
	 * @testDescription Asserts that two DmtAcl are different when they have the same set of permissions but different principals
	 */
	private void testEquals002() {
		try {
			tbc.log("#testEquals002");
			String[] principals = { DmtTestControl.PRINCIPAL, DmtTestControl.PRINCIPAL_2 } ;
			String[] principals2 = { DmtTestControl.PRINCIPAL, DmtTestControl.PRINCIPAL_3 } ;
			int[] permissionsBoth = { DmtAcl.ADD, DmtAcl.GET };
			DmtAcl dmtAcl = new DmtAcl(principals,permissionsBoth);
			DmtAcl dmtAcl2 = new DmtAcl(principals2,permissionsBoth);
			tbc.assertTrue("Asserts that two DmtAcl are different when they have the same set of permissions but different principals",!dmtAcl.equals(dmtAcl2));
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}	
	/**
	 * @testID testEquals003
	 * @testDescription Asserts that two DmtAcl are different when they have the same set of principals but different permissions
	 */
	private void testEquals003() {
		try {
			tbc.log("#testEquals003");
			String[] principalsBoth = { DmtTestControl.PRINCIPAL, DmtTestControl.PRINCIPAL_2 } ;
			int[] permissions = { DmtAcl.ADD, DmtAcl.GET };
			int[] permissions2 = { DmtAcl.GET, DmtAcl.ADD };
			DmtAcl dmtAcl = new DmtAcl(principalsBoth,permissions);
			DmtAcl dmtAcl2 = new DmtAcl(principalsBoth,permissions2);
			tbc.assertTrue("Asserts that two DmtAcl are different when they have the same set of principals but different permissions",!dmtAcl.equals(dmtAcl2));
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}

}

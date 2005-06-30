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
 * @methodUnderTest org.osgi.service.dmt.DmtAcl#clone
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>clone<code> method, according to MEG reference
 *                     documentation.
 */
public class Clone {
	private DmtTestControl tbc;
	
	public Clone(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testClone001();
	}

	/**
	 * @testID testClone001
	 * @testDescription Asserts that clone creates a copy of the object
	 */
	private void testClone001() {
		try {
			tbc.log("#testClone001");
			String[] principals = { DmtTestControl.PRINCIPAL, DmtTestControl.PRINCIPAL_2 } ;
			int[] permissions = { DmtAcl.ADD, DmtAcl.GET };
			DmtAcl dmtAcl = new DmtAcl(principals,permissions);
			
			tbc.assertTrue("Asserts that clone creates a copy of the object",dmtAcl.equals(dmtAcl.clone()));
		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}
}

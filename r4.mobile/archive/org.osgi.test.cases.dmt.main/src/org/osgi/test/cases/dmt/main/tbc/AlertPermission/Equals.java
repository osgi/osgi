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

/* REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 05/07/2005   Luiz Felipe Guimaraes
 * 1            Implement TCK
 * ===========  ==============================================================
 */

package org.osgi.test.cases.dmt.main.tbc.AlertPermission;

import org.osgi.test.cases.dmt.main.tbc.DmtConstants;
import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;

/**
 * 
 * This test case validates the implementation of <code>equals<code> method of AlerPermission, 
 * according to MEG specification
 */
public class Equals {
	private DmtTestControl tbc;

	public Equals(DmtTestControl tbc) {
		this.tbc = tbc;

	}

	public void run() {
		testEquals001();
		testEquals002();
		
	}

	/**
	 * Asserts that two AlertPermission instances are equal if they have the same target string
	 * 
	 * @spec AlertPermission.equals(Object)
	 */
	private void testEquals001() {
		try {		
			tbc.log("#testEquals001");
			org.osgi.service.dmt.security.AlertPermission permission = new org.osgi.service.dmt.security.AlertPermission(DmtConstants.REMOTE_SERVER);
			org.osgi.service.dmt.security.AlertPermission permission2 = new org.osgi.service.dmt.security.AlertPermission(DmtConstants.REMOTE_SERVER);
			tbc.assertTrue("Asserts that two AlertPermission instances are equal if they have the same target string",permission.equals(permission2));
		} catch(Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		}
			
	}
	
	/**
	 * Asserts that two AlertPermission instances are different if they have different target strings
	 * 
	 * @spec AlertPermission.equals(Object)
	 */
	private void testEquals002() {
		try {		
			tbc.log("#testEquals002");
			org.osgi.service.dmt.security.AlertPermission permission = new org.osgi.service.dmt.security.AlertPermission(DmtConstants.REMOTE_SERVER);
			org.osgi.service.dmt.security.AlertPermission permission2 = new org.osgi.service.dmt.security.AlertPermission(DmtConstants.REMOTE_SERVER + "a");
			tbc.assertTrue("Asserts that two AlertPermission instances are different if they have different target strings",!permission.equals(permission2));
		} catch(Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		}
			
	}
	
	
}

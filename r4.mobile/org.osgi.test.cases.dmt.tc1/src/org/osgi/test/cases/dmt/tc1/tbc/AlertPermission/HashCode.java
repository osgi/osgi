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

package org.osgi.test.cases.dmt.tc1.tbc.AlertPermission;

import org.osgi.test.cases.dmt.tc1.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;

/**
 * This class validates the implementation of <code>hashCode<code> method, 
 * according to MEG specification.
 */
public class HashCode {
	private DmtTestControl tbc;

	public HashCode(DmtTestControl tbc) {
		this.tbc = tbc;

	}

	public void run() {
		testHashCode001();
		
	}

	/**
	 * Asserts that two AlertPermission instances produces the same hashCode if they have the same target string
     * 
     * @spec AlertPermission.hashCode()
	 */
	private void testHashCode001() {
		try {		
			tbc.log("#testHashCode001");
			info.dmtree.security.AlertPermission permission = new info.dmtree.security.AlertPermission(DmtConstants.REMOTE_SERVER);
			info.dmtree.security.AlertPermission permission2 = new info.dmtree.security.AlertPermission(DmtConstants.REMOTE_SERVER);
			tbc.assertTrue("Asserts that two AlertPermission instances produces the same hashCode if they have the same target string",permission.hashCode() == permission2.hashCode());
		} catch(Exception e) {
			tbc.failUnexpectedException(e);
		}
			
	}
}

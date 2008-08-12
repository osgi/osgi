/*
 *  Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
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
 * 
 */

/*
 * REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 13/10/2005   Alexandre Santos
 * 153          Implement OAT test cases  
 * ===========  ==============================================================
 */

package org.osgi.test.cases.application.tbc.ApplicationAdminPermission;

import org.osgi.test.cases.application.tbc.ApplicationConstants;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;

/**
 * @author Alexandre Santos
 * 
 * This test class validates the implementation of
 * <code>hashCode</code> method, according to MEG reference
 * documentation.
 */
public class HashCode {
	private ApplicationTestControl tbc;

	/**
	 * @param tbc
	 */
	public HashCode(ApplicationTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testHashCode001();
	}

    /**
     * This method asserts if two equals permissions have the same
     * hashCode number.
     * 
     * @spec ApplicationAdminPermission.hashCode()
     */         
	private void testHashCode001() {
		try {
			tbc.log("#testHashCode001");
			org.osgi.service.application.ApplicationAdminPermission app = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationConstants.ACTIONS);
			org.osgi.service.application.ApplicationAdminPermission app2 = new org.osgi.service.application.ApplicationAdminPermission(
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationConstants.ACTIONS_DIFFERENT_ORDER);
			tbc.assertTrue("Asserting if two equals permissions have the same hashCode number.", app.hashCode()==app2.hashCode());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}
}

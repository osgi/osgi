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
 * 
 */

/*
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Apr 12, 2005  Alexandre Alves
 * 26            Implement MEG TCK
 * ============  ==============================================================
 */
package org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentAdminPermission;

import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentTestControl;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;

/**
 * This Test Class Validates the constants according to MEG specification.
 */
public class DeploymentAdminPermissionConstants {
	private DeploymentTestControl tbc;

	public DeploymentAdminPermissionConstants(DeploymentTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testConstants001();
	}

	/**
	 * Tests all constants values according to constants fields values.
	 * 
	 * @spec 114.14.3 
	 */
	public void testConstants001() {
		tbc.log("#testConstants001");
		tbc.assertEquals("Asserting ACTION_INSTALL value", "install", DeploymentAdminPermission.INSTALL);
		tbc.assertEquals("Asserting ACTION_CANCEL value", "cancel", DeploymentAdminPermission.CANCEL);
		tbc.assertEquals("Asserting ACTION_LIST value", "list", DeploymentAdminPermission.LIST);
		tbc.assertEquals("Asserting ACTION_UNINSTALL value", "uninstall", DeploymentAdminPermission.UNINSTALL);
		tbc.assertEquals("Asserting ACTION_UNINSTALL_FORCEFUL value", "uninstall_forced", DeploymentAdminPermission.UNINSTALL_FORCED);
	}

}

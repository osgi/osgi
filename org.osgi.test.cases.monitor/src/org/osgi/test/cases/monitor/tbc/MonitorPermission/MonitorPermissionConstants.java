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
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * Feb 21, 2005 Alexandre Santos
 * 14           Implement MEG TCK
 * ===========  ==============================================================
 */
package org.osgi.test.cases.monitor.tbc.MonitorPermission;

import org.osgi.test.cases.monitor.tbc.MonitorTestControl;

/**
 * @author Leonardo Barros
 * 
 * This test class validates MonitorPermission constants according to MEG
 * reference documentation.
 */
public class MonitorPermissionConstants {
	private MonitorTestControl tbc;

	/**
	 * @param tbc
	 */
	public MonitorPermissionConstants(MonitorTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testConstants001();
	}

	/**
	 * This method asserts MonitorPermission constant fields.
	 * 
	 * @spec MonitorPermission constant fields
	 */
	private void testConstants001() {
		tbc.log("#testConstants001");
		tbc.assertEquals("Asserting PUBLISH value", "publish",
				org.osgi.service.monitor.MonitorPermission.PUBLISH);
		tbc.assertEquals("Asserting READ value", "read",
				org.osgi.service.monitor.MonitorPermission.READ);
		tbc.assertEquals("Asserting RESET value", "reset",
				org.osgi.service.monitor.MonitorPermission.RESET);
		tbc.assertEquals("Asserting STARTJOB value", "startjob",
				org.osgi.service.monitor.MonitorPermission.STARTJOB);
		tbc.assertEquals("Asserting SWITCHEVENTS value", "switchevents",
				org.osgi.service.monitor.MonitorPermission.SWITCHEVENTS);
	}
}

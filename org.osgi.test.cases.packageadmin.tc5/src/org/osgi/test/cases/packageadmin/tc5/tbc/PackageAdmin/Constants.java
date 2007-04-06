/*
 * $Id$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
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
package org.osgi.test.cases.packageadmin.tc5.tbc.PackageAdmin;

import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.test.cases.packageadmin.tc5.tbc.TestControl;

/**
 * Test the constants of org.osgi.service.packageadmin.PackageAdmin class.
 * 
 * @author left
 * @version $Revision$
 */
public class Constants {

	private TestControl	control;

	/**
	 * Creates a new Constants
	 * 
	 * @param _control the bundle control for this test
	 */
	public Constants(TestControl _control) {
		control = _control;
	}

	/**
	 * Run the tests
	 */
	public void run() throws Exception {
		testConstantValues0001();
	}

	/**
	 * Test the constant values
	 */
	public void testConstantValues0001() throws Exception {
		control.assertEquals(
				"Testing the constant value PackageAdmin.BUNDLE_TYPE_FRAGMENT",
				0x00000001, PackageAdmin.BUNDLE_TYPE_FRAGMENT);
	}

}
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
 * 26/01/2005   André Assad
 * 1         	Implement TCK
 * ===========  ==============================================================
*/

package org.osgi.test.cases.dmt.tbc.DmtData;

import org.osgi.test.cases.util.DefaultTestBundleControl;

/**
 *
 * @generalDescription This Test Case Validates the values of all
 *                     <code>constants<code> of DmtData, according to MEG reference
 *                     documentation.
 */
public class DmtDataConstants {
	private DefaultTestBundleControl tbc;

	/**
	 * @param arg0
	 */
	public DmtDataConstants(DefaultTestBundleControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testDmtDataConstants();
	}

	/**
	 * @testID testDmtDataConstants
	 * @testDescription This method asserts the constants values
	 */
	public void testDmtDataConstants() {

		tbc.assertEquals("Asserting FORMAT_BINARY value", 8,
				org.osgi.service.dmt.DmtData.FORMAT_BINARY);
        tbc.assertEquals("Asserting FORMAT_BOOLEAN value", 4,
                org.osgi.service.dmt.DmtData.FORMAT_BOOLEAN);
        tbc.assertEquals("Asserting FORMAT_INTEGER value", 1,
                org.osgi.service.dmt.DmtData.FORMAT_INTEGER);
        tbc.assertEquals("Asserting FORMAT_NODE value", 64,
                org.osgi.service.dmt.DmtData.FORMAT_NODE);
        tbc.assertEquals("Asserting NULL_FORMAT value", 32,
                org.osgi.service.dmt.DmtData.FORMAT_NULL);
        tbc.assertEquals("Asserting FORMAT_STRING value", 2,
                org.osgi.service.dmt.DmtData.FORMAT_STRING);
        tbc.assertEquals("Asserting FORMAT_XML value", 16,
                org.osgi.service.dmt.DmtData.FORMAT_XML);

	}
}

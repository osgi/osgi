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
 * 26/01/2005   Andre Assad
 * 1         	Implement TCK
 * ===========  ==============================================================
*/

package org.osgi.test.cases.dmt.tc1.tbc.DmtData;

import info.dmtree.DmtData;
import org.osgi.test.cases.util.DefaultTestBundleControl;

/**
 * This Test Case Validates the values of all constants> of DmtData, according to MEG specification
 */
public class DmtDataConstants {
	private DefaultTestBundleControl tbc;

	public DmtDataConstants(DefaultTestBundleControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testConstants001();
	}

	/**
	 * This method asserts the constants values
	 * 
	 * @spec 117.12.5
	 */
	private void testConstants001() {
		tbc.log("#testConstants001");
		tbc.assertEquals("Asserting FORMAT_BASE64 value", 128,DmtData.FORMAT_BASE64);
		tbc.assertEquals("Asserting FORMAT_BINARY value", 64,DmtData.FORMAT_BINARY);
        tbc.assertEquals("Asserting FORMAT_BOOLEAN value", 8,DmtData.FORMAT_BOOLEAN);
        tbc.assertEquals("Asserting FORMAT_DATE value", 16,DmtData.FORMAT_DATE);
        tbc.assertEquals("Asserting FORMAT_FLOAT value", 2,DmtData.FORMAT_FLOAT);
        tbc.assertEquals("Asserting FORMAT_INTEGER value", 1,DmtData.FORMAT_INTEGER);
        tbc.assertEquals("Asserting FORMAT_NODE value", 1024,DmtData.FORMAT_NODE);
        tbc.assertEquals("Asserting FORMAT_NULL value", 512,DmtData.FORMAT_NULL);
        tbc.assertEquals("Asserting FORMAT_STRING value", 4,DmtData.FORMAT_STRING);
        tbc.assertEquals("Asserting FORMAT_XML value", 256,DmtData.FORMAT_XML);
        tbc.assertEquals("Asserting FORMAT_RAW_BINARY value", 4096,DmtData.FORMAT_RAW_BINARY);
        tbc.assertEquals("Asserting FORMAT_RAW_STRING value", 2048,DmtData.FORMAT_RAW_STRING);

	}
}

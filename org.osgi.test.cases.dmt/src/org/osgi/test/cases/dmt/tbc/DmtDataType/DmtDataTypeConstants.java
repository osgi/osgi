
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
 * Jan 26, 2005  Andre Assad
 * 1             Implement MEG TCK
 * ============  ==============================================================
 * Feb 11, 2005  Luiz Felipe Guimarães
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-002)
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tbc.DmtDataType;

import org.osgi.service.dmt.DmtDataType;
import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * @author André Assad
 * 
 * @classUnderTest org.osgi.service.DmtDataType
 * @generalDescription This class contains all the tests for DmtDataType
 *                     Constants. In other words it asserts the all the
 *                     constants values specified on the reference documentation
 *                     has been inplemented correctly.
 */
public class DmtDataTypeConstants {
	private DmtTestControl tbc;

	/**
	 * @param tbc
	 * 
	 * The default constructor that passes over the DefaultMEGTestBundleControl
	 */

	public DmtDataTypeConstants(DmtTestControl tbc) {
		this.tbc = tbc;

	}

	public void run() {
		testDmtDataTypeConstants001();

	}

	/**
	 * @testID testDmtDataTypeConstants001
	 * @testDescription This method asserts the value of the constants
	 */
	public void testDmtDataTypeConstants001() {
		tbc.assertEquals("Assertion of the INTEGER constant", 1,
				DmtDataType.INTEGER);
		tbc.assertEquals("Assertion of the STRING constant", 2,
				DmtDataType.STRING);
		tbc.assertEquals("Assertion of the BOOLEAN constant", 3,
				DmtDataType.BOOLEAN);
		tbc.assertEquals("Assertion of the BINARY constant", 4,
				DmtDataType.BINARY);
		tbc.assertEquals("Assertion of the XML constant", 5, DmtDataType.XML);
		tbc.assertEquals("Assertion of the NULL constant", 6, DmtDataType.NULL);
		tbc.assertEquals("Assertion of the NODE constant", 7, DmtDataType.NODE);
		tbc.assertEquals("Assertion of the UNSPECIFIED constant", 8, 
				DmtDataType.UNSPECIFIED);
	}


}

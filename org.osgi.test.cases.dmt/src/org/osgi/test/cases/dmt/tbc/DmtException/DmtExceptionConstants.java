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
 * Jan 26, 2005  Leonardo Barros
 * 1             Implement TCK
 * ============  ==============================================================
 * Feb 14, 2005  Luiz Felipe Guimarães
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-002)
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tbc.DmtException;

import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * @constantsUnderTest org.osgi.service.dmt.DmtException#*
 * @generalDescription This class tests DmtExpcetion constants according with
 *                     MEG specification (rfc0085)
 */

public class DmtExceptionConstants {
	private DmtTestControl	tbc;

	public DmtExceptionConstants(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testDmtExceptionConstants001();
	}

	/**
	 * 
	 * Tests if constants have a correct value
	 * 
	 * @spec DmtException.ALERT_NOT_ROUTED
	 * @spec DmtException.COMMAND_FAILED
	 * @spec DmtException.COMMAND_NOT_ALLOWED
	 * @spec DmtException.CONCURRENT_ACCESS
	 * @spec DmtException.DATA_STORE_FAILURE
	 * @spec DmtException.FEATURE_NOT_SUPPORTED
	 * @spec DmtException.FORMAT_NOT_SUPPORTED
	 * @spec DmtException.INVALID_URI
	 * @spec DmtException.METADATA_MISMATCH
	 * @spec DmtException.NODE_ALREADY_EXISTS
	 * @spec DmtException.NODE_NOT_FOUND
	 * @spec DmtException.OTHER_ERROR
	 * @spec DmtException.PERMISSION_DENIED
	 * @spec DmtException.REMOTE_ERROR
	 * @spec DmtException.ROLLBACK_FAILED
	 * @spec DmtException.URI_TOO_LONG
	 */
	public void testDmtExceptionConstants001() {
		tbc.assertEquals("Asserts DmtException.ALERT_NOT_ROUTED", 5,
				org.osgi.service.dmt.DmtException.ALERT_NOT_ROUTED);
		tbc.assertEquals("Asserts DmtException.COMMAND_FAILED", 500,
				org.osgi.service.dmt.DmtException.COMMAND_FAILED);
		tbc.assertEquals("Asserts DmtException.COMMAND_NOT_ALLOWED", 405,
				org.osgi.service.dmt.DmtException.COMMAND_NOT_ALLOWED);
		tbc.assertEquals("Asserts DmtException.CONCURRENT_ACCESS", 4,
				org.osgi.service.dmt.DmtException.CONCURRENT_ACCESS);
		tbc.assertEquals("Asserts DmtException.DATA_STORE_FAILURE", 510,
				org.osgi.service.dmt.DmtException.DATA_STORE_FAILURE);
		tbc.assertEquals("Asserts DmtException.FEATURE_NOT_SUPPORTED", 406,
				org.osgi.service.dmt.DmtException.FEATURE_NOT_SUPPORTED);
		tbc.assertEquals("Asserts DmtException.FORMAT_NOT_SUPPORTED", 415,
				org.osgi.service.dmt.DmtException.FORMAT_NOT_SUPPORTED);
		tbc.assertEquals("Asserts DmtException.INVALID_URI", 3,
				org.osgi.service.dmt.DmtException.INVALID_URI);
		tbc.assertEquals("Asserts DmtException.METADATA_MISMATCH", 2,
				org.osgi.service.dmt.DmtException.METADATA_MISMATCH);
		tbc.assertEquals("Asserts DmtException.NODE_ALREADY_EXISTS", 418,
				org.osgi.service.dmt.DmtException.NODE_ALREADY_EXISTS);
		tbc.assertEquals("Asserts DmtException.NODE_NOT_FOUND", 404,
				org.osgi.service.dmt.DmtException.NODE_NOT_FOUND);
		tbc.assertEquals("Asserts DmtException.OTHER_ERROR", 0,
				org.osgi.service.dmt.DmtException.OTHER_ERROR);
		tbc.assertEquals("Asserts DmtException.PERMISSION_DENIED", 425,
				org.osgi.service.dmt.DmtException.PERMISSION_DENIED);
		tbc.assertEquals("Asserts DmtException.REMOTE_ERROR", 1,
				org.osgi.service.dmt.DmtException.REMOTE_ERROR);
		tbc.assertEquals("Asserts DmtException.ROLLBACK_FAILED", 516,
				org.osgi.service.dmt.DmtException.ROLLBACK_FAILED);
		tbc.assertEquals("Asserts DmtException.URI_TOO_LONG", 414,
				org.osgi.service.dmt.DmtException.URI_TOO_LONG);
	}

}

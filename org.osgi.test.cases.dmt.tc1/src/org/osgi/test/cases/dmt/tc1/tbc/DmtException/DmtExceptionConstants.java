/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

/* 
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Jan 26, 2005  Leonardo Barros
 * 1             Implement TCK
 * ============  ==============================================================
 * Feb 14, 2005  Luiz Felipe Guimaraes
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-002)
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc1.tbc.DmtException;

import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;

/**
 * This class tests DmtExpcetion constants according to MEG specification
 */

public class DmtExceptionConstants extends DmtTestControl {
	/**
	 * Tests if constants contains the specified value
	 * 
	 * @spec 117.12.6 
	 */
	public void testConstants001(){
		log("#testConstants001");
		assertEquals("Asserts DmtException.ALERT_NOT_ROUTED", 5,
				org.osgi.service.dmt.DmtException.ALERT_NOT_ROUTED);
		assertEquals("Asserts DmtException.COMMAND_FAILED", 500,
				org.osgi.service.dmt.DmtException.COMMAND_FAILED);
		assertEquals("Asserts DmtException.COMMAND_NOT_ALLOWED", 405,
				org.osgi.service.dmt.DmtException.COMMAND_NOT_ALLOWED);
		assertEquals("Asserts DmtException.CONCURRENT_ACCESS", 4,
				org.osgi.service.dmt.DmtException.CONCURRENT_ACCESS);
		assertEquals("Asserts DmtException.DATA_STORE_FAILURE", 510,
				org.osgi.service.dmt.DmtException.DATA_STORE_FAILURE);
		assertEquals("Asserts DmtException.FEATURE_NOT_SUPPORTED", 406,
				org.osgi.service.dmt.DmtException.FEATURE_NOT_SUPPORTED);
		assertEquals("Asserts DmtException.INVALID_URI", 3,
				org.osgi.service.dmt.DmtException.INVALID_URI);
		assertEquals("Asserts DmtException.METADATA_MISMATCH", 2,
				org.osgi.service.dmt.DmtException.METADATA_MISMATCH);
		assertEquals("Asserts DmtException.NODE_ALREADY_EXISTS", 418,
				org.osgi.service.dmt.DmtException.NODE_ALREADY_EXISTS);
		assertEquals("Asserts DmtException.NODE_NOT_FOUND", 404,
				org.osgi.service.dmt.DmtException.NODE_NOT_FOUND);
		assertEquals("Asserts DmtException.PERMISSION_DENIED", 425,
				org.osgi.service.dmt.DmtException.PERMISSION_DENIED);
		assertEquals("Asserts DmtException.REMOTE_ERROR", 1,
				org.osgi.service.dmt.DmtException.REMOTE_ERROR);
		assertEquals("Asserts DmtException.ROLLBACK_FAILED", 516,
				org.osgi.service.dmt.DmtException.ROLLBACK_FAILED);
		assertEquals("Asserts DmtException.SESSION_CREATION_TIMEOUT", 7,
				org.osgi.service.dmt.DmtException.SESSION_CREATION_TIMEOUT);
		assertEquals("Asserts DmtException.TRANSACTION_ERROR", 6,
				org.osgi.service.dmt.DmtException.TRANSACTION_ERROR);
		assertEquals("Asserts DmtException.URI_TOO_LONG", 414,
				org.osgi.service.dmt.DmtException.URI_TOO_LONG);
		assertEquals("Asserts DmtException.UNAUTHORIZED", 401,
				org.osgi.service.dmt.DmtException.UNAUTHORIZED);
	}

}

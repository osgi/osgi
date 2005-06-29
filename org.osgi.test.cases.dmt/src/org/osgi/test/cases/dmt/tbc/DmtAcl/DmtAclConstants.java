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
 * Jan 31, 2005  André Assad
 * 1             Implement MEG TCK
 * ============  ==============================================================
 * Feb 11, 2005  André Assad
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tbc.DmtAcl;

import org.osgi.service.dmt.DmtAcl;
import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * This Test Class Validates the Constants Fields Values, according to MEG
 * reference documentation (rfc0085).
 */
public class DmtAclConstants {
	private DmtTestControl	tbc;

	/**
	 * @param arg0
	 */
	public DmtAclConstants(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testDmtAclConstants001();
	}

	/**
	 * Tests all constants values according to Constants fields values JavaDoc.
	 * 
	 * @spec DmtAcl.GET
	 * @spec DmtAcl.ADD
	 * @spec DmtAcl.REPLACE
	 * @spec DmtAcl.DELETE
	 * @spec DmtAcl.EXEC
	 * 
	 * ### btw, this tests the compiler, not the system under test because the
	 * constants are final so they are compiled inline ... You need to use
	 * reflection.
	 */
	public void testDmtAclConstants001() {
		tbc.assertEquals("Asserting GET value", 1, DmtAcl.GET);
		tbc.assertEquals("Asserting ADD value", 2, DmtAcl.ADD);
		tbc.assertEquals("Asserting REPLACE value", 4, DmtAcl.REPLACE);
		tbc.assertEquals("Asserting DELETE value", 8, DmtAcl.DELETE);
		tbc.assertEquals("Asserting EXEC value", 16, DmtAcl.EXEC);
	}
}

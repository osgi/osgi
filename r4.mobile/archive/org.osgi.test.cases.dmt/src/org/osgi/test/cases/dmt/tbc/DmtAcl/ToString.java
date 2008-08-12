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
 * Feb 14, 2005  André Assad
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tbc.DmtAcl;

import org.osgi.service.dmt.DmtAcl;
import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * This Test Class Validates the implementation of
 * <code>toString<code> method, according to MEG reference
 *                     documentation.
 */
public class ToString {
	private DmtTestControl	tbc;

	private DmtAcl			dmtAcl;

	/**
	 * @param tbc
	 */
	public ToString(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testToString001();
	}

	/**
	 * 
	 * This method asserts that the string representing this ACL is the same as
	 * the canonic string as specified in OMA DM.
	 * 
	 * @spec DmtAcl.toString()
	 */
	public void testToString001() {
		String principal = "www.cesar.org.br";
		String canonic = "Add=" + principal + "&Delete=" + principal + "&Get=*";
		dmtAcl = new org.osgi.service.dmt.DmtAcl(canonic);

		tbc.assertEquals("Asserting toString", canonic, dmtAcl.toString());
	}
}

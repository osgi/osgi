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
 * Jan 31, 2005  Andre Assad
 * 1             Implement MEG TCK
 * ============  ==============================================================
 * Feb 14, 2005  Andre Assad
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.main.tbc.DmtAcl;

import org.osgi.service.dmt.DmtAcl;
import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtAcl#toString
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>toString<code> method, according to MEG reference
 *                     documentation.
 */
public class ToString {
	private DmtTestControl tbc;

	private DmtAcl dmtAcl;

	/**
	 * @param tbc
	 */
	public ToString(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testToString001();
		testToString002();
	}

	/**
	 * @testID testToString001
	 * @testDescription This method asserts that the operations are in the
	 *                  following order: (Add, Delete, Exec, Get, Replace), that
	 *                  principal names are sorted alphabetically and that all
	 *                  of the principal are returned
	 */
	private void testToString001() {
		try {

			tbc.log("#testToString001");

			String canonic = "Exec=" + DmtTestControl.PRINCIPAL + "&Add="
					+ DmtTestControl.PRINCIPAL_2 + "&Delete="
					+ DmtTestControl.PRINCIPAL + "&Replace="
					+ DmtTestControl.PRINCIPAL + "&Get="
					+ DmtTestControl.PRINCIPAL + "&Add="
					+ DmtTestControl.PRINCIPAL;
			dmtAcl = new org.osgi.service.dmt.DmtAcl(canonic);

			String result = dmtAcl.toString();

			tbc.assertEquals("Asserts toString returned value", "Add="
					+ DmtTestControl.PRINCIPAL + "&Delete="
					+ DmtTestControl.PRINCIPAL + "&Exec="
					+ DmtTestControl.PRINCIPAL + "&Get="
					+ DmtTestControl.PRINCIPAL + "&Replace="
					+ DmtTestControl.PRINCIPAL, result);

		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testToString002
	 * @testDescription This method asserts that the operations are in the
	 *                  following order: (Add, Get), that principal names are
	 *                  sorted alphabetically and that all of the principal are
	 *                  returned
	 */
	private void testToString002() {
		try {
			tbc.log("#testToString002");

			String canonic = "Get=" + DmtTestControl.PRINCIPAL + "&Add=*&Get="
					+ DmtTestControl.PRINCIPAL_2;
			dmtAcl = new org.osgi.service.dmt.DmtAcl(canonic);

			String result = dmtAcl.toString();

			tbc.assertEquals("Asserts toString returned value", "Add=*"
					+ "&Get=" + DmtTestControl.PRINCIPAL + "&Get="
					+ DmtTestControl.PRINCIPAL_2, result);

		} catch (Exception e) {
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}
}

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

package org.osgi.test.cases.dmt.tc1.tbc.Acl;

import info.dmtree.Acl;
import org.osgi.test.cases.dmt.tc1.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * This test case validates the implementation of <code>toString<code> method of Acl, 
 * according to MEG reference specification.
 */
public class ToString {
	private DmtTestControl tbc;

	private Acl Acl;

	public ToString(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testToString001();
		testToString002();
	}

	/**
	 * This method asserts that the operations are in the following order: 
	 * (Add, Delete, Exec, Get, Replace), that principal names are sorted 
	 * alphabetically and that all of the principal are returned
	 * 
	 * @spec Acl.toString()
	 */
	private void testToString001() {
		try {

			tbc.log("#testToString001");

			String canonic = "Exec=" + DmtConstants.PRINCIPAL + "&Add="
					+ DmtConstants.PRINCIPAL_2 + "&Delete="
					+ DmtConstants.PRINCIPAL + "&Replace="
					+ DmtConstants.PRINCIPAL + "&Get="
					+ DmtConstants.PRINCIPAL + "&Add="
					+ DmtConstants.PRINCIPAL;
			Acl = new info.dmtree.Acl(canonic);

			String result = Acl.toString();

			tbc.assertEquals("Asserts toString returned value", "Add="
					+ DmtConstants.PRINCIPAL + "+"+ DmtConstants.PRINCIPAL_2 +"&Delete="
					+ DmtConstants.PRINCIPAL + "&Exec="
					+ DmtConstants.PRINCIPAL + "&Get="
					+ DmtConstants.PRINCIPAL + "&Replace="
					+ DmtConstants.PRINCIPAL, result);

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		}
	}

	/**
	 * This method asserts that the operations are in the following order: (Add, Get), that principal 
	 * names are sorted alphabetically and that all of the principal are returned
	 * 
	 * @spec Acl.toString()
	 */
	private void testToString002() {
		try {
			tbc.log("#testToString002");

			String canonic = "Get=" + DmtConstants.PRINCIPAL + "&Add=*&Get="
					+ DmtConstants.PRINCIPAL_2;
			Acl = new info.dmtree.Acl(canonic);

			String result = Acl.toString();

			tbc.assertEquals("Asserts toString returned value", "Add=*"
					+ "&Get=" + DmtConstants.PRINCIPAL + "+"
					+ DmtConstants.PRINCIPAL_2, result);

		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		}
	}
}

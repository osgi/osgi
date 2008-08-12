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

import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtData#hashCode
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>hashCode<code> method, according to MEG reference
 *                     documentation.
 */
public class HashCode {
	private DmtTestControl tbc;

	private org.osgi.service.dmt.DmtData dd;

	/**
	 * @param arg0
	 */
	public HashCode(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testHashCode001();
		testHashCode002();
		testHashCode003();
		testHashCode004();
	}

	/**
	 * @testID testDmtData001
	 * @testDescription This test method asserts that a String hash code is set
	 *                  for a String DmtData.
	 */
	private void testHashCode001() {
		String string = "string";
		dd = new org.osgi.service.dmt.DmtData(string);

		tbc.assertEquals("Asserting hash code", new String(string).hashCode(),
				dd.hashCode());
	}

	/**
	 * @testID testDmtData002
	 * @testDescription This test method asserts that an Integer hash code is
	 *                  set for an Integer DmtData.
	 */
	private void testHashCode002() {
		int i = 2;
		dd = new org.osgi.service.dmt.DmtData(i);

		tbc.assertEquals("Asserting hash code", new Integer(i).hashCode(), dd
				.hashCode());
	}

	/**
	 * @testID testDmtData003
	 * @testDescription This test method asserts that a Boolean hash code is set
	 *                  for a Boolean DmtData.
	 */
	private void testHashCode003() {
		boolean b = true;
		dd = new org.osgi.service.dmt.DmtData(b);

		tbc.assertEquals("Asserting hash code", new Boolean(b).hashCode(), dd
				.hashCode());
	}

	/**
	 * @testID testDmtData004
	 * @testDescription This test method asserts that a Byte hash code is set
	 *                  for a Byte DmtData.
	 */
	private void testHashCode004() {
		byte b = 127;
		dd = new org.osgi.service.dmt.DmtData(b);

		tbc.assertEquals("Asserting hash code", new Byte(b).hashCode(), dd
				.hashCode());
	}

}

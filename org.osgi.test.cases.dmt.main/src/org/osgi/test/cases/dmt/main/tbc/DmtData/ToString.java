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

package org.osgi.test.cases.dmt.main.tbc.DmtData;

import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;

/**
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtData#toString
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>toString<code> method, according to MEG reference
 *                     documentation.
 */
public class ToString {
	private DmtTestControl tbc;

	public ToString(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testToString001();
		testToString002();
		testToString003();
		testToString004();
		testToString005();
		testToString006();
		testToString007();

	}

	/**
	 * @testID testToString001
	 * @testDescription Asserts that the string value itself is returned in case
	 *                  of format string
	 */
	private void testToString001() {
		try {
			tbc.log("#testToString001");
			String string = "test";
			org.osgi.service.dmt.DmtData data = new org.osgi.service.dmt.DmtData(
					string);
			tbc
					.assertEquals(
							"Asserts that the string value itself is returned in case of format string",
							string, data.toString());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testToString002
	 * @testDescription Asserts that the string value itself is returned in case
	 *                  of format string
	 */
	private void testToString002() {
		try {
			tbc.log("#testToString002");
			String string = "test";
			org.osgi.service.dmt.DmtData data = new org.osgi.service.dmt.DmtData(
					string, false);
			tbc
					.assertEquals(
							"Asserts that the string value itself is returned in case of format string",
							string, data.toString());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testToString003
	 * @testDescription Asserts that the string form of the value is returned in
	 *                  case of format xml
	 */
	private void testToString003() {
		try {
			tbc.log("#testToString003");
			String string = "<test></test>";
			org.osgi.service.dmt.DmtData data = new org.osgi.service.dmt.DmtData(
					string, true);
			tbc
					.assertEquals(
							"Asserts that the string form of the value is returned in case of format xml",
							string, data.toString());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testToString004
	 * @testDescription Asserts that the string form of the value is returned in
	 *                  case of format boolean
	 */
	private void testToString004() {
		try {
			tbc.log("#testToString004");
			boolean value = true;
			org.osgi.service.dmt.DmtData data = new org.osgi.service.dmt.DmtData(
					value);
			tbc
					.assertEquals(
							"Asserts that the string form of the value is returned in case of format boolean",
							String.valueOf(value), data.toString());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testToString005
	 * @testDescription Asserts that the string form of the value is returned in
	 *                  case of format int
	 */
	private void testToString005() {
		try {
			tbc.log("#testToString005");
			int value = 0;
			org.osgi.service.dmt.DmtData data = new org.osgi.service.dmt.DmtData(
					value);
			tbc
					.assertEquals(
							"Asserts that the string form of the value is returned in case of format int",
							String.valueOf(value), data.toString());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testToString006
	 * @testDescription Asserts that the string form of the value is returned in
	 *                  case of format null
	 */
	private void testToString006() {
		try {
			tbc.log("#testToString006");
			org.osgi.service.dmt.DmtData data = org.osgi.service.dmt.DmtData.NULL_VALUE;
			tbc
					.assertEquals(
							"Asserts that the string form of the value is returned in case of format null",
							"null", data.toString());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testToString007
	 * @testDescription Asserts that the Btwo-digit hexadecimal numbers for each
	 *                  byte separated by spaces is returned in case of format
	 *                  binary
	 */
	private void testToString007() {
		try {
			tbc.log("#testToString007");
			byte[] value = new byte[] { 1, 10, 127 };
			String expectedReturn = "01 0A 7F";
			org.osgi.service.dmt.DmtData data = new org.osgi.service.dmt.DmtData(
					value);
			tbc
					.assertEquals(
							"Asserts that two-digit hexadecimal numbers for each byte separated by spaces is returned in case of format binary",
							expectedReturn, data.toString().trim());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName());
		}
	}
}

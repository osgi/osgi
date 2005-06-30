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
 * 1            Implement TCK
 * ===========  ==============================================================
 * 14/02/2005   Leonardo Barros
 * 1            Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-002)
 */

package org.osgi.test.cases.dmt.main.tbc.DmtData;

import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;
import org.osgi.service.dmt.DmtData;

/**
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtData#equals
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>equals<code> method, according to MEG reference
 *                     documentation.
 */
public class Equals {
	private DmtTestControl tbc;

	private org.osgi.service.dmt.DmtData dd;

	public Equals(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testEquals001();
		testEquals002();
		testEquals003();
		testEquals004();
		testEquals005();
		testEquals006();
		testEquals007();
		testEquals008();
		testEquals009();
		testEquals010();
		testEquals011();
		testEquals012();
		testEquals013();
		testEquals014();
		testEquals015();
		testEquals016();
		testEquals017();
		testEquals018();
		testEquals019();
		testEquals020();
	}

	/**
	 * @testID testEquals001
	 * @testDescription Asserts if two DmtData objects initialized with the same
	 *                  string are equal
	 */
	private void testEquals001() {
		try {
			tbc.log("#testEquals001");
			String str = "String";
			dd = new DmtData(str);
			DmtData dd2 = new DmtData(str);
			tbc
					.assertTrue(
							"Asserts if two DmtData objects initialized with the same string are equal",
							dd.equals(dd2));
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testEquals002
	 * @testDescription This method asserts that two instances of DmtData
	 *                  created with the same integer are equal.
	 */
	private void testEquals002() {
		try {
			tbc.log("#testEquals002");
			int i = 10;
			dd = new DmtData(i);
			DmtData dd2 = new DmtData(i);
			tbc
					.assertTrue(
							"Asserts if two DmtData objects initialized with the same int are equal",
							dd.equals(dd2));
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testEquals003
	 * @testDescription This method asserts that two instances of DmtData
	 *                  created with the same boolean are equal.
	 */
	private void testEquals003() {
		try {
			tbc.log("#testEquals003");
			boolean b = false;
			dd = new DmtData(b);
			DmtData dd2 = new DmtData(b);
			tbc
					.assertTrue(
							"Asserts if two DmtData objects initialized with the same boolean are equal",
							dd.equals(dd2));
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testEquals004
	 * @testDescription This method asserts that two instances of DmtData
	 *                  created with the same byte array are equal.
	 */
	private void testEquals004() {
		try {
			tbc.log("#testEquals004");
			byte[] b = new byte[1024];
			dd = new DmtData(b);
			DmtData dd2 = new DmtData(b);
			tbc
					.assertTrue(
							"Asserts if two DmtData objects initialized with the same byte array are equal",
							dd.equals(dd2));
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testEquals005
	 * @testDescription This method asserts that two instances of DmtData
	 *                  created with the same xml are equal.
	 */
	private void testEquals005() {
		try {
			tbc.log("#testEquals005");
			String xml = "<test1></test1>";
			dd = new DmtData(xml, true);

			DmtData dd2 = new DmtData(xml, true);
			tbc
					.assertTrue(
							"Asserts if two DmtData objects initialized with the same xml are equal",
							dd.equals(dd2));
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testEquals006
	 * @testDescription Asserts if two DmtData objects initialized with
	 *                  different strings are different
	 */
	private void testEquals006() {
		try {
			tbc.log("#testEquals006");

			dd = new DmtData("a");
			DmtData dd2 = new DmtData("b");
			tbc
					.assertTrue(
							"Asserts if two DmtData objects initialized with different strings are different",
							!dd.equals(dd2));
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testEquals007
	 * @testDescription Asserts if two DmtData objects initialized with
	 *                  different ints are different
	 */
	private void testEquals007() {
		try {
			tbc.log("#testEquals007");
			dd = new DmtData(1);
			DmtData dd2 = new DmtData(2);
			tbc
					.assertTrue(
							"Asserts if two DmtData objects initialized with different ints are different",
							!dd.equals(dd2));
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testEquals008
	 * @testDescription This method asserts that two instances of DmtData
	 *                  created with different booleans are different.
	 */
	private void testEquals008() {
		try {
			tbc.log("#testEquals008");
			dd = new DmtData(true);
			DmtData dd2 = new DmtData(false);
			tbc
					.assertTrue(
							"Asserts if two DmtData objects initialized with different booleans are different",
							!dd.equals(dd2));
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testEquals009
	 * @testDescription This method asserts that two instances of DmtData
	 *                  created with different byte arrays are different.
	 */
	private void testEquals009() {
		try {
			tbc.log("#testEquals009");
			dd = new DmtData(new byte[1]);
			DmtData dd2 = new DmtData(new byte[2]);
			tbc
					.assertTrue(
							"Asserts if two DmtData objects initialized with the different byte arrays are different",
							!dd.equals(dd2));
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testEquals010
	 * @testDescription This method asserts that two instances of DmtData
	 *                  created with different xml are different.
	 */
	private void testEquals010() {
		try {
			tbc.log("#testEquals010");
			dd = new DmtData("<test1></test1>", true);
			DmtData dd2 = new DmtData("<test2></test2>", true);
			tbc
					.assertTrue(
							"Asserts if two DmtData objects initialized with different xmls are different",
							!dd.equals(dd2));
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testEquals011
	 * @testDescription Asserts if two DmtData objects initialized with
	 *                  different formats are different (string / byte)
	 */
	private void testEquals011() {
		try {
			tbc.log("#testEquals011");
			dd = new DmtData("a");
			DmtData dd2 = new DmtData(new byte[1]);
			tbc
					.assertTrue(
							"Asserts if two DmtData objects initialized with different formats are different (string / byte)",
							!dd.equals(dd2));
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testEquals012
	 * @testDescription Asserts if two DmtData objects initialized with
	 *                  different formats are different (string / boolean)
	 */
	private void testEquals012() {
		try {
			tbc.log("#testEquals012");
			dd = new DmtData("a");
			DmtData dd2 = new DmtData(false);

			tbc
					.assertTrue(
							"Asserts if two DmtData objects initialized with different formats are different (string / boolean)",
							!dd.equals(dd2));
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testEquals013
	 * @testDescription Asserts if two DmtData objects initialized with
	 *                  different formats are different (string / int)
	 */
	private void testEquals013() {
		try {
			tbc.log("#testEquals013");
			dd = new DmtData("a");
			DmtData dd2 = new DmtData(1);
			tbc
					.assertTrue(
							"Asserts if two DmtData objects initialized with different formats are different (string / int)",
							!dd.equals(dd2));
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testEquals014
	 * @testDescription Asserts if two DmtData objects initialized with
	 *                  different formats are different (string / xml)
	 */
	private void testEquals014() {
		try {
			tbc.log("#testEquals014");
			dd = new DmtData("a");

			DmtData dd2 = new DmtData("<test1></test1>", true);

			tbc
					.assertTrue(
							"Asserts if two DmtData objects initialized with different formats are different (string / xml)",
							!dd.equals(dd2));
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testEquals015
	 * @testDescription Asserts if two DmtData objects initialized with
	 *                  different formats are different (byte / boolean)
	 */
	private void testEquals015() {
		try {
			tbc.log("#testEquals015");
			dd = new DmtData(new byte[1]);
			DmtData dd2 = new DmtData(true);
			tbc
					.assertTrue(
							"Asserts if two DmtData objects initialized with different formats are different (byte / boolean)",
							!dd.equals(dd2));
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testEquals016
	 * @testDescription Asserts if two DmtData objects initialized with
	 *                  different formats are different (byte / int)
	 */
	private void testEquals016() {
		try {
			tbc.log("#testEquals016");
			dd = new DmtData(new byte[1]);
			DmtData dd2 = new DmtData(1);
			tbc
					.assertTrue(
							"Asserts if two DmtData objects initialized with different formats are different (byte / int)",
							!dd.equals(dd2));
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testEquals017
	 * @testDescription Asserts if two DmtData objects initialized with
	 *                  different formats are different (byte / xml)
	 */
	private void testEquals017() {
		try {
			tbc.log("#testEquals017");
			dd = new DmtData(new byte[1]);
			DmtData dd2 = new DmtData("<test1></test1>", true);
			tbc
					.assertTrue(
							"Asserts if two DmtData objects initialized with different formats are different (byte / xml)",
							!dd.equals(dd2));
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testEquals018
	 * @testDescription Asserts if two DmtData objects initialized with
	 *                  different formats are different (boolean / int)
	 */
	private void testEquals018() {
		try {
			tbc.log("#testEquals018");
			dd = new DmtData(true);
			DmtData dd2 = new DmtData(1);
			tbc
					.assertTrue(
							"Asserts if two DmtData objects initialized with different formats are different (boolean / int)",
							!dd.equals(dd2));
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testEquals019
	 * @testDescription Asserts if two DmtData objects initialized with
	 *                  different formats are different (boolean / xml)
	 */
	private void testEquals019() {
		try {
			tbc.log("#testEquals019");
			dd = new DmtData(false);
			DmtData dd2 = new DmtData("<test1></test1>", true);
			tbc
					.assertTrue(
							"Asserts if two DmtData objects initialized with different formats are different (boolean / xml)",
							!dd.equals(dd2));
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName());
		}
	}

	/**
	 * @testID testEquals020
	 * @testDescription Asserts if two DmtData objects initialized with
	 *                  different formats are different (int / xml)
	 */
	private void testEquals020() {
		try {
			tbc.log("#testEquals020");
			dd = new DmtData(1);
			DmtData dd2 = new DmtData("<test1></test1>", true);
			tbc
					.assertTrue(
							"Asserts if two DmtData objects initialized with different formats are different (int / xml)",
							!dd.equals(dd2));
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName());
		}
	}

}

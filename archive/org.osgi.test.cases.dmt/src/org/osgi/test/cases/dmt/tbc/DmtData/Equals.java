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
 * 1            Implement TCK
 * ===========  ==============================================================
 * 14/02/2005   Leonardo Barros
 * 1            Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-002)
 */

package org.osgi.test.cases.dmt.tbc.DmtData;

import org.osgi.test.cases.dmt.tbc.DmtTestControl;

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

	/**
	 * @param tbc
	 */
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
	}

	/**
	 * @testID testEquals001
	 * @testDescription Asserts if two DmtData objects initialized with the same
	 *                  string are equal
	 */
	public void testEquals001() {
		String str = "String";
		dd = new org.osgi.service.dmt.DmtData(str);

		org.osgi.service.dmt.DmtData dd2 = new org.osgi.service.dmt.DmtData(str);

		tbc.assertTrue("Asserting equals", dd.equals(dd2));
	}

	/**
	 * @testID testEquals002
	 * @testDescription This method asserts that two instances of DmtData
	 *                  created with the same integer are equal.
	 */
	public void testEquals002() {
		int i = 10;
		dd = new org.osgi.service.dmt.DmtData(i);

		org.osgi.service.dmt.DmtData dd2 = new org.osgi.service.dmt.DmtData(i);

		tbc.assertTrue("Asserting equals", dd.equals(dd2));
	}

	/**
	 * @testID testEquals003
	 * @testDescription This method asserts that two instances of DmtData
	 *                  created with the same boolean are equal.
	 */
	public void testEquals003() {
		boolean b = false;
		dd = new org.osgi.service.dmt.DmtData(b);

		org.osgi.service.dmt.DmtData dd2 = new org.osgi.service.dmt.DmtData(b);

		tbc.assertTrue("Asserting equals", dd.equals(dd2));
	}

	/**
	 * @testID testEquals004
	 * @testDescription This method asserts that two instances of DmtData
	 *                  created with the same byte array are equal.
	 */
	public void testEquals004() {
		byte[] b = new byte[1024];
		dd = new org.osgi.service.dmt.DmtData(b);

		org.osgi.service.dmt.DmtData dd2 = new org.osgi.service.dmt.DmtData(b);

		tbc.assertTrue("Asserting equals", dd.equals(dd2));
	}

	/**
	 * @testID testEquals005
	 * @testDescription Asserts if two DmtData objects created with different
	 *                  values are not equal
	 */
	public void testEquals005() {
		dd = new org.osgi.service.dmt.DmtData(0);

		org.osgi.service.dmt.DmtData dd2 = new org.osgi.service.dmt.DmtData(1);

		tbc.assertTrue("Asserting equals", !dd.equals(dd2));
	}
	
	/**
	 * @testID testEquals006
	 * @testDescription Asserts if two DmtData objects created with different
	 *                  types are not equal
	 */
	public void testEquals006() {
		dd = new org.osgi.service.dmt.DmtData(true);

		org.osgi.service.dmt.DmtData dd2 = new org.osgi.service.dmt.DmtData(1);

		tbc.assertTrue("Asserting equals", !dd.equals(dd2));
	}

	/**
	 * @testID testEquals007
	 * @testDescription Asserts if two DmtData objects created with different
	 *                  values are not equal
	 */
	public void testEquals007() {
		dd = new org.osgi.service.dmt.DmtData(true);

		org.osgi.service.dmt.DmtData dd2 = new org.osgi.service.dmt.DmtData(false);

		tbc.assertTrue("Asserting equals", !dd.equals(dd2));
	}

	/**
	 * @testID testEquals008
	 * @testDescription Asserts if two DmtData objects created with different
	 *                  types are not equal
	 */
	public void testEquals008() {
		dd = new org.osgi.service.dmt.DmtData(true);

		org.osgi.service.dmt.DmtData dd2 = new org.osgi.service.dmt.DmtData(new byte[1]);

		tbc.assertTrue("Asserting equals", !dd.equals(dd2));
	}

	/**
	 * @testID testEquals009
	 * @testDescription Asserts if two DmtData objects created with different
	 *                  values are not equal
	 */
	public void testEquals009() {
		byte b1[] = new byte[]{0,1};
		byte b2[] = new byte[]{0,2};
		
		dd = new org.osgi.service.dmt.DmtData(b1);

		org.osgi.service.dmt.DmtData dd2 = new org.osgi.service.dmt.DmtData(b2);

		tbc.assertTrue("Asserting equals", !dd.equals(dd2));
	}
	
	/**
	 * @testID testEquals010
	 * @testDescription Asserts if two DmtData objects created with different
	 *                  values are not equal
	 */
	public void testEquals010() {
		
		dd = new org.osgi.service.dmt.DmtData("<test1></test1>",true);

		org.osgi.service.dmt.DmtData dd2 = new org.osgi.service.dmt.DmtData("<test2></test2>",true);

		tbc.assertTrue("Asserting equals", !dd.equals(dd2));
	}

	/**
	 * @testID testEquals011
	 * @testDescription Asserts if two DmtData objects created with different
	 *                  types are not equal
	 */
	public void testEquals011() {
		dd = new org.osgi.service.dmt.DmtData("<test1></test1>",true);

		org.osgi.service.dmt.DmtData dd2 = new org.osgi.service.dmt.DmtData(new byte[1]);

		tbc.assertTrue("Asserting equals", !dd.equals(dd2));
	}
	
}

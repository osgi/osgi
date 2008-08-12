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

import java.util.Vector;

import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * This class tests DmtException constructors according with
 *                     MEG specification (rfc0085)
 */

public class DmtException {
	private DmtTestControl tbc;

	public DmtException(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testDmtException001();
		testDmtException002();
		testDmtException003();
	}

	/**
	 * 
	 *  Tests if the values that are passed as parameters for
	 *                  the constructor are equal to the values returned through
	 *                  its corresponding get methods
	 * 
	 * @spec DmtException.DmtException(String,int,String)
	 */
	public void testDmtException001() {

		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException(
				"par1", org.osgi.service.dmt.DmtException.INVALID_URI, "par2");

		tbc.assertEquals("Asserts getURI() method","par1",de.getURI());
		tbc.assertTrue("Asserts getMessage() method", de.getMessage().indexOf(
				"par2") > -1);
		tbc.assertEquals("Asserts getCode() method",org.osgi.service.dmt.DmtException.INVALID_URI,de.getCode());

		de = new org.osgi.service.dmt.DmtException(null, -1, null);
		tbc.assertNull("Asserts getURI() method", de.getURI());
		tbc.assertNull("Asserts getMessage() method", de.getMessage());
		tbc.assertEquals("Asserts getCode() method",-1,de.getCode());
	}

	/**
	 * 
	 *  Tests if the values that are passed as parameters for
	 *                  the constructor are equal to the values returned through
	 *                  its corresponding get methods
	 * @spec DmtException.DmtException(String,int,String,Throwable)
	 */
	public void testDmtException002() {

		// TODO test passing an integer number that is one of the possible
		// values for constants
		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException(
				"par1", org.osgi.service.dmt.DmtException.FORMAT_NOT_SUPPORTED, "par2", new Exception());

		tbc.assertEquals("Asserts getURI() method","par1",de.getURI());
		tbc.assertTrue("Asserts getMessage() method", de.getMessage().indexOf(
				"par2") > -1);
		tbc.assertEquals("Asserts getCode() method",org.osgi.service.dmt.DmtException.FORMAT_NOT_SUPPORTED,de.getCode());
		tbc.assertException("Asserts getCause() method", Exception.class, de
				.getCause());

		de = new org.osgi.service.dmt.DmtException(null, -1, null,
				new Exception());
		tbc.assertNull("Asserts getURI() method", de.getURI());
		tbc.assertNull("Asserts getMessage() method", de.getMessage());
		tbc.assertEquals("Asserts getCode() method",-1,de.getCode());

	}

	/**
	 * 
	 *  Tests if the values that are passed as parameters for
	 *                  the constructor are equal to the values returned through
	 *                  its corresponding get methods
	 * @spec DmtException.DmtException(String,int,String,throwable)
	 */
	public void testDmtException003() {
		Vector causes = new Vector();
		causes.add(0, new Exception("teste"));

		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException(
				null, -1, null, causes);
		tbc.assertNull("Asserts getURI() method", de.getURI());
		tbc.assertNull("Asserts getMessage() method", de.getMessage());
		tbc.assertEquals("Asserts getCode() method",-1,de.getCode());
		tbc.assertEquals("Asserts the size of getCauses() method",1, de.getCauses()
						.size());
        tbc.assertTrue("Asserts the name of getCauses() method", (((Exception) de.getCauses().elementAt(0)).toString().indexOf("teste") > -1));
	}

}

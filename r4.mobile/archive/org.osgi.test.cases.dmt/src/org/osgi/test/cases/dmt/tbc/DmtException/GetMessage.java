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
 * org.osgi.service.dmt.DmtException#getMessage
 * This class tests DmtException.getMessage method according
 *                     with MEG specification (rfc0085)
 */
public class GetMessage {
	private DmtTestControl tbc;

	public GetMessage(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetMessage001();
		testGetMessage002();
		testGetMessage003();
		testGetMessage004();
	}

	/**
	 * 
	 *  Tests if getMessage returns a value that is equals to
	 *                  the value passed as a parameter for the constructor
	 *  @spec DmtException.getMessage()
	 *  @spec DmtException.DmtException(String,int,String)

	 */
	public void testGetMessage001() {
		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException(
				null, 0, null);
		tbc.assertNull("Asserts getMessage method", de.getMessage());

		de = new org.osgi.service.dmt.DmtException(null, 0, "par1");
		tbc.assertEquals("Asserts getMessage method","par1",de.getMessage());

		de = new org.osgi.service.dmt.DmtException(null, 0, null,
				new Exception());
		tbc.assertNull("Asserts getMessage method", de.getMessage());

		de = new org.osgi.service.dmt.DmtException(null, 0, "par1",
				new Exception());
		tbc.assertEquals("Asserts getMessage method","par1",de.getMessage());

		de = new org.osgi.service.dmt.DmtException(null, 0, null, new Vector());
		tbc.assertNull("Asserts getMessage method", de.getMessage());

		de = new org.osgi.service.dmt.DmtException(null, 0, "par1",
				new Vector());
		tbc.assertEquals("Asserts getMessage method","par1",de.getMessage());
	}

	/**
	 * 
	 *  Tests if getMessage returns a string that contains the
	 *                  message and code values passed as parameters for the
	 *                  constructor
	 *  @spec DmtException.getMessage()
	 *  @spec DmtException.DmtException(String,int,String)
	 */
	public void testGetMessage002() {
		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException(
				null, org.osgi.service.dmt.DmtException.ALERT_NOT_ROUTED,
				"par1");

		tbc.assertTrue("Asserts getMessage method",
						de.getMessage().indexOf("par1") > -1
								&& de.getMessage().indexOf(String.valueOf(org.osgi.service.dmt.DmtException.ALERT_NOT_ROUTED)) > -1);
	}

	/**
	 * 
	 *  Tests if getMessage returns a string that contains the
	 *                  message and URI values passed as parameters for the
	 *                  constructor
	 *  @spec DmtException.getMessage()
	 *  @spec DmtException.DmtException(String,int,String)
	 */
	public void testGetMessage003() {
		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException(
				"par0", 0, "par1");

		tbc.assertTrue("Asserts getMessage method", de.getMessage().indexOf(
				"par1") > -1
				&& de.getMessage().indexOf("par0") > -1);

	}

	/**
	 * 
	 *  Tests if getMessage returns a string that contains the
	 *                  message, code and URI values passed as parameters for
	 *                  the constructor
	 *  @spec DmtException.getMessage()
	 *  @spec DmtException.DmtException(String,int,String)
	 */
	public void testGetMessage004() {
		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException(
				"par0", org.osgi.service.dmt.DmtException.ALERT_NOT_ROUTED,
				"par1");

		tbc.assertTrue("Asserts getMessage method",
						de.getMessage().indexOf("par1") > -1
								&& de.getMessage().indexOf("par0") > -1
								&& de.getMessage().indexOf(String.valueOf(org.osgi.service.dmt.DmtException.ALERT_NOT_ROUTED)) > -1);

	}

}

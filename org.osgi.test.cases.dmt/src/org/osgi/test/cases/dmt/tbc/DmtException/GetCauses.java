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
 *
 * REVISION HISTORY:
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
 * This class tests DmtException.getCauses method according
 *                     with MEG specification (rfc0085)
 */

public class GetCauses {
	private DmtTestControl tbc;

	public GetCauses(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetCauses001();
		testGetCauses002();
	}

	/**
	 * 
	 *  Tests if the exception returned in the getCauses method
	 *                  is from the same type of the exception passed as a
	 *                  parameter for the constructor
	 *                  
	 *  @spec DmtException.getCauses()
	 *  @spec DmtException.DmtException(String,int,String,Vector)
	 */
	public void testGetCauses001() {
		Vector causes = new Vector();
		causes.add(0, new Exception());

		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException(
				null, org.osgi.service.dmt.DmtException.OTHER_ERROR, null, causes);

		tbc.assertEquals("Asserts getCauses method",1,de.getCauses().size());
		tbc.assertException("Asserts getCauses method", Exception.class, (Exception) de
				.getCauses().get(0));
	}

	/**
	 * 
	 *  Tests If an empty vector is returned no cause was
	 *                  specified in the constructor
	 *  @spec DmtException.getCauses()
	 *  @spec DmtException.DmtException(String,int,String,Vector)
	 */
	public void testGetCauses002() {
		Vector causes = new Vector();

		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException(
				null, org.osgi.service.dmt.DmtException.OTHER_ERROR, null, causes);

		tbc.assertEquals("Asserts getCauses method",0,de.getCauses().size());
	}
	/**
	 * 
	 *  Tests if an empty vector is returned if no cause is
	 *                  specified in the constructor
	 *  @spec DmtException.getCauses()
	 *  @spec DmtException.DmtException(String,int,String,Vector)
	 */
	public void testGetCauses003() {

		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException(null, org.osgi.service.dmt.DmtException.OTHER_ERROR, null);

		tbc.assertEquals("Asserts getCauses method",0,de.getCauses().size());
	}

}

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
 * This class tests DmtException.getURI method according
 *                     with MEG specification (rfc0085)
 */
public class GetURI {
	private DmtTestControl tbc;

	public GetURI(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetURI001();
	}

	/**
	 * 
	 *  Tests if getURI returns a value that is equals to the
	 *                  value passed as a parameter for the constructor
	 *  @spec DmtException.getURI()
	 *  @spec DmtException.DmtException(String,int,String)
	 */
	public void testGetURI001() {
		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException(
				null, 2, null);
		tbc.assertNull("Asserts getURI method", de.getURI());

		de = new org.osgi.service.dmt.DmtException("par1", 2, null);
		tbc.assertEquals("Asserts getURI method","par1",de.getURI());
		
		de = new org.osgi.service.dmt.DmtException(null, 2, null, new Exception());
		tbc.assertNull("Asserts getURI method", de.getURI());

		de = new org.osgi.service.dmt.DmtException("par1", 2, null, new Exception());
		tbc.assertEquals("Asserts getURI method","par1",de.getURI());

		de = new org.osgi.service.dmt.DmtException(null, 2, null, new Vector());
		tbc.assertNull("Asserts getURI method", de.getURI());

		de = new org.osgi.service.dmt.DmtException("par1", 2, null, new Vector());
		tbc.assertEquals("Asserts getURI method","par1",de.getURI());
	}
	
}

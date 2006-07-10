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
 * Apr 17, 2006  Luiz Felipe Guimaraes
 * 283           [MEGTCK][DMT] DMT API changes
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tb1.DmtEvent;

import info.dmtree.DmtEvent;

import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.TestInterface;



/**
 * @author Luiz Felipe Guimaraes
 * 
 * This Class Validates the implementation of <code>DmtEvent<code> constants, 
 * according to MEG specification
 */
public class DmtEventConstants implements TestInterface {
	private DmtTestControl tbc;
	
	public DmtEventConstants(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testDmtEventConstants001();
	}

	/**
	 * Asserts the DmtEvent constants
	 * 
	 * @spec 117.13.5
	 */
	private void testDmtEventConstants001() {

		try {
			tbc.log("#testDmtEventConstants001");
			tbc.assertEquals("Asserting DmtEvent.ADDED constant value", 1, DmtEvent.ADDED);
			tbc.assertEquals("Asserting DmtEvent.COPIED constant value", 2, DmtEvent.COPIED);
			tbc.assertEquals("Asserting DmtEvent.DELETED constant value", 4, DmtEvent.DELETED);
			tbc.assertEquals("Asserting DmtEvent.RENAMED constant value", 8, DmtEvent.RENAMED);
			tbc.assertEquals("Asserting DmtEvent.REPLACED constant value", 16, DmtEvent.REPLACED);
			tbc.assertEquals("Asserting DmtEvent.SESSION_OPENED constant value", 32, DmtEvent.SESSION_OPENED);
			tbc.assertEquals("Asserting DmtEvent.SESSION_CLOSED constant value", 64, DmtEvent.SESSION_CLOSED);
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		}
	
	}
	
		
}


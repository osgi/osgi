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
 * Jan 31, 2005  André Assad
 * 1             Implement MEG TCK
 * ============  ==============================================================
 * Feb 16, 2005  André Assad
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tbc.DmtSession;

import org.osgi.service.dmt.DmtException;
import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * @methodUnderTest org.osgi.service.dmt.DmtSession#isNodeUri
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>isNodeUri<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class IsNodeUri {
	private DmtTestControl tbc;

	public IsNodeUri(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testIsNodeUri001();
		testIsNodeUri002();
		testIsNodeUri003();
		testIsNodeUri004();
	}

	/**
	 * @testID testIsNodeUri001
	 * @testDescription Tests if the method returns true for a valid URI of the
	 *                  DMT.
	 */
	private void testIsNodeUri001() {
		String internal = DmtTestControl.OSGi_LOG + "internal";
		try {
			tbc.getSession().createInteriorNode(internal);
			tbc.assertTrue("Assert isNodeUri", tbc.getSession().isNodeUri(
					internal));
		} catch (DmtException e) {
			tbc.fail("Unexpected DmtException");
			e.printStackTrace();
		} finally {
			tbc.cleanUp(new String[] { internal });
		}
	}

	/**
	 * @testID testIsNodeUri002
	 * @testDescription Tests if the method returns false for an invalid URI of
	 *                  the DMT.
	 */
	private void testIsNodeUri002() {
		tbc.assertTrue("Assert isNodeUri", !tbc.getSession().isNodeUri(
				DmtTestControl.INVALID_NODE));
	}

	/**
	 * @testID testIsNodeUri003
	 * @testDescription Simulates a IllegalStateException for close() session
	 *                  case.
	 */
	private void testIsNodeUri003() {
		String internal = DmtTestControl.OSGi_LOG + "internal";
		try {
			tbc.getSession().createInteriorNode(internal);
			tbc.getSession().close();
		} catch (DmtException e) {
			tbc.fail("Unexpected DmtException");
			e.printStackTrace();
		}
		try {
			tbc.getSession().isNodeUri(internal);
		} catch (IllegalStateException e) {
			tbc.pass("IllegalStateException properly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName()
					+ " but was " + e.getClass().getName());
		} finally {
			tbc.cleanUp(new String[] { internal });
		}
	}

	/**
	 * @testID testIsNodeUri004
	 * @testDescription Simulates a IllegalStateException for rollback() session
	 *                  case.
	 */
	private void testIsNodeUri004() {
		String internal = DmtTestControl.OSGi_LOG + "internal";
		try {
			tbc.getSession().createInteriorNode(internal);
			tbc.getSession().rollback();
		} catch (DmtException e) {
			tbc.fail("Unexpected DmtException");
			e.printStackTrace();
		}
		try {
			tbc.getSession().isNodeUri(internal);
		} catch (IllegalStateException e) {
			tbc.pass("IllegalStateException properly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName()
					+ " but was " + e.getClass().getName());
		} finally {
			tbc.cleanUp(new String[] { internal });
		}
	}
}

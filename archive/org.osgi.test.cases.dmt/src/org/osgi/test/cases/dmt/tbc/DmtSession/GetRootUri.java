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
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 21/JAN/2005  Andre Assad
 * CR 1         Implement MEG TCK
 * ===========  ==============================================================
 * Feb 11, 2005  Alexandre Santos
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ===========  ==============================================================
 * Feb 14, 2005  Leonardo Barros
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ===========  ==============================================================
 */

package org.osgi.test.cases.dmt.tbc.DmtSession;

import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * @methodUnderTest org.osgi.service.dmt.DmtSession#getRootUri
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>getRootUri<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class GetRootUri {
	
	// TODO tests if IllegalStateException is thrown when session is invalidated
	// because of timeout
	
	private DmtTestControl tbc;

	public GetRootUri(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetRootUri001();
		testGetRootUri002();
		testGetRootUri003();
		testGetRootUri004();
	}

	/**
	 * @testID testGetRootUri001
	 * @testDescription Test if the comple URI of the root node is returned.
	 */
	private void testGetRootUri001() {
		tbc.getSession(DmtTestControl.OSGi_ROOT,
				DmtSession.LOCK_TYPE_ATOMIC);
		try {
			tbc.assertEquals("Asserting root uri", DmtTestControl.OSGi_ROOT,
					tbc.getSession().getRootUri());
		}catch(Exception e) {
			tbc.fail("Exception incorrectly thrown");
		}
		tbc.fail("no tbc.getSession() created");
	}

	/**
	 * @testID testGetRootUri002
	 * @testDescription tests that the value "." is returned whenever a null
	 *                  root node is passed as a parameter.
	 */
	private void testGetRootUri002() {
		tbc.getSession(null, DmtSession.LOCK_TYPE_ATOMIC);
		try {
			tbc.assertEquals("Asserting root uri", ".", tbc.getSession()
					.getRootUri());
		}catch(Exception e){
			tbc.fail("Exception incorrectly thrown");
		}
	}
	
	/**
	 * @testID testGetRootUri003
	 * @testDescription This method asserts if IllegalStateException is thrown
	 *                  when must be.
	 */
	private void testGetRootUri003() {
		try {
			DmtSession session = tbc.getSession(DmtTestControl.OSGi_LOG);
			session.close();
			session.getRootUri();
			tbc.fail("getRootUri did not throw an exception");
		} catch (IllegalStateException e) {
			tbc.pass("IllegalStateException correctly thrown");
		} catch (Exception e) {
			tbc.fail("Incorrect exception thrown");
		}
	}

	/**
	 * @testID testGetRootUri004
	 * @testDescription This method asserts if IllegalStateException is
	 *                  correctly thrown
	 */
	private void testGetRootUri004() {
		try {
			DmtSession session = tbc.getSession(DmtTestControl.OSGi_LOG,
					DmtSession.LOCK_TYPE_ATOMIC);
			session.rollback();
			session.getRootUri();
			tbc.fail("No DmtException thrown.");
		} catch (IllegalStateException e) {
			tbc.pass("IllegalStateException correctly thrown");
		} catch (Exception e) {
			tbc.fail("Exception incorrectly thrown");
		}
	}
	
}

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
 * Jan 31, 2005  Andre Assad
 * 1             Implement MEG TCK
 * ============  ==============================================================
 * Feb 16, 2005  Andre Assad
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.main.tb1.DmtSession;

import org.osgi.service.dmt.DmtPermission;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.main.tbc.TestInterface;
import org.osgi.test.cases.dmt.main.tbc.Plugin.TestExecPluginActivator;

/**
 * @methodUnderTest org.osgi.service.dmt.DmtSession#isNodeUri
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>isNodeUri<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class IsNodeUri implements TestInterface {
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
		DmtSession session = null;
		try {
			tbc.log("#testIsNodeUri001");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			tbc.assertTrue("Assert isNodeUri", session
					.isNodeUri(DmtTestControl.OSGi_LOG));
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testIsNodeUri002
	 * @testDescription Tests if the method returns false for an invalid URI of
	 *                  the DMT.
	 */
	private void testIsNodeUri002() {
		DmtSession session = null;
		try {
			tbc.log("#testIsNodeUri002");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);
			tbc.assertTrue("Assert isNodeUri", !session
					.isNodeUri(DmtTestControl.INVALID_NODE));
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testIsNodeUri003
	 * @testDescription Simulates a IllegalStateException for close() session
	 *                  case.
	 */
	private void testIsNodeUri003() {
		DmtSession session = null;
		try {
			tbc.log("#testIsNodeUri003");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.close();

			session.isNodeUri(DmtTestControl.OSGi_LOG);
			tbc.failException("", IllegalStateException.class);
		} catch (IllegalStateException e) {
			tbc.pass("IllegalStateException properly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName()
					+ " but was " + e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}
	
	/**
	 * @testID testIsNodeUri004
	 * @testDescription This method asserts that relative URI works as described.
	 * 
	 */
	private void testIsNodeUri004() {
		DmtSession session = null;
		try {
			tbc.log("#testIsNodeUri004");
			
			tbc.setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtTestControl.ALL_NODES,DmtTestControl.ALL_ACTIONS));
			
			session = tbc.getDmtAdmin().getSession(
					TestExecPluginActivator.ROOT, DmtSession.LOCK_TYPE_ATOMIC);

			session.isNodeUri(TestExecPluginActivator.INTERIOR_VALUE);

			tbc.pass("A relative URI can be used with isNodeUri.");
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
		} finally {
			tbc.closeSession(session);
		}
	}   	
}

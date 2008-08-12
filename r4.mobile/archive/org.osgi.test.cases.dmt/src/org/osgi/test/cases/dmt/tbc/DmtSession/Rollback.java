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
 * ===========   ==============================================================
 * Jan 21, 2005  Andre Assad
 * 1             Implement MEG TCK
 * ===========   ==============================================================
 * Feb 11, 2005  Luiz Felipe Guimarães
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-002)
 * ===========   ==============================================================
 */

package org.osgi.test.cases.dmt.tbc.DmtSession;

import org.osgi.service.dmt.DmtAcl;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtSession#rollback
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>rollback<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class Rollback {
	//TODO TCs for ROLLBACK_FAILED and IllegalStateException when a session is invalidated because of timeout
	private DmtTestControl tbc;

	public Rollback(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testRollback001();
		testRollback002();
		testRollback003();
		testRollback004();
		testRollback005();
	}

	/**
	 * @testID testRollback001
	 * @testDescription This method asserts that a series of DMT operations are
	 *                  rolled back since the tbc.getSession() was created.
	 */
	private void testRollback001()  {
		try {
			DmtAcl acl = new DmtAcl(DmtTestControl.ACL_CESAR);
			tbc.getSession().setNodeTitle(DmtTestControl.OSGi_ROOT,
					DmtTestControl.TITLE);
			tbc.getSession().setNodeAcl(DmtTestControl.OSGi_ROOT, acl);
			// rolls back
			tbc.getSession().rollback();
			// asserts that operations were rolled back
			tbc.assertTrue("Asserting set node title roll back", !tbc
					.getSession().getNodeTitle(DmtTestControl.OSGi_ROOT)
					.equals(DmtTestControl.TITLE));
			tbc.assertTrue("Asserting set node acl roll back", !tbc
					.getSession().getNodeAcl(DmtTestControl.OSGi_ROOT)
					.equals(acl));
		} catch (Exception e) {
			tbc.fail("Throws an unexpected DmtException.");

		}
	}

	/**
	 * @testID testRollback002
	 * @testDescription This method asserts that whenever a DmtSession with
	 *                  without LOCK_TYPE_ATOMIC lock is created, the roll back
	 *                  operation is not supported.
	 */
	private void testRollback002() {
		try {
			tbc.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
			DmtAcl acl = new DmtAcl(DmtTestControl.ACL_CESAR);
			tbc.getSession().setNodeTitle(DmtTestControl.OSGi_ROOT,
					DmtTestControl.TITLE);
			tbc.getSession().setNodeAcl(DmtTestControl.OSGi_ROOT, acl);

			tbc.getSession().rollback();
		} catch (DmtException e) {
			tbc.assertEquals("Asserting DmtException type",
					DmtException.FEATURE_NOT_SUPPORTED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Unexpected exception error.");
		}
	}
	
	/**
	 * @testID testRollback003
	 * @testDescription This method asserts that whenever a DmtSession with
	 *                  without LOCK_TYPE_ATOMIC lock is created, the roll back
	 *                  operation is not supported (second case).
	 */
	private void testRollback003() {
		try {
			tbc.getSession(".", DmtSession.LOCK_TYPE_SHARED);
			DmtAcl acl = new DmtAcl(DmtTestControl.ACL_CESAR);
			tbc.getSession().setNodeTitle(DmtTestControl.OSGi_ROOT,
					DmtTestControl.TITLE);
			tbc.getSession().setNodeAcl(DmtTestControl.OSGi_ROOT, acl);

			// rolls back
			tbc.getSession().rollback();
		} catch (DmtException e) {
			tbc.assertEquals("Asserting DmtException type",
					DmtException.FEATURE_NOT_SUPPORTED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Unexpected exception error.");
		}
	}
	/**
	 * @testID testRollback004
	 * @testDescription This method asserts that an IllegalStateException 
	 *                  is thrown when a session is already rolled back
	 */
	private void testRollback004() {
		try {

            DmtSession session = tbc.getSession(".", DmtSession.LOCK_TYPE_ATOMIC);
            session.rollback();
			session.rollback();
			tbc.failException("#",IllegalStateException.class);
		} catch (IllegalStateException e) {
			tbc.pass("The Exception was IllegalStateException");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName() + " but was " + e.getClass().getName());
		}
	}
	/**
	 * @testID testRollback005
	 * @testDescription This method asserts that an IllegalStateException 
	 *                  is thrown when a session is already rolled back
	 */
	private void testRollback005() {
		try {

            DmtSession session = tbc.getSession(".", DmtSession.LOCK_TYPE_ATOMIC);
            session.close();
			session.rollback();
			tbc.failException("#",IllegalStateException.class);
		} catch (IllegalStateException e) {
			tbc.pass("The Exception was IllegalStateException");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName() + " but was " + e.getClass().getName());
		}
	}
}

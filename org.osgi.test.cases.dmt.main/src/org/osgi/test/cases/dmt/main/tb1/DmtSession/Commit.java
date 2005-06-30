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
 * Mar 15, 2005  Luiz Felipe Guimaraes
 * 1             Implement MEG TCK
 * ===========   ==============================================================
 */

package org.osgi.test.cases.dmt.main.tb1.DmtSession;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.main.tbc.TestInterface;
/**
 * @author Luiz Felipe Guimaraes
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtSession#commit
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>commit<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class Commit implements TestInterface {
	private DmtTestControl tbc;

	public Commit(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testCommit001();
		testCommit002();
		testCommit003();
        testCommit004();
	}

	/**
	 * @testID testCommit001
	 * @testDescription This method asserts that whenever a DmtSession with
	 *                  LOCK_TYPE_EXCLUSIVE lock is created, the commit
	 *                  operation is not supported.
	 */
	private void testCommit001() {
		DmtSession session = null;
		try {
			tbc.log("#testCommit001");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);		
			session.commit();
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserting DmtException type",
					DmtException.FEATURE_NOT_SUPPORTED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testCommit002
	 * @testDescription This method asserts that whenever a DmtSession with
	 *                  LOCK_TYPE_SHARED lock is created, the commit operation
	 *                  is not supported (second case).
	 */
	private void testCommit002() {
		DmtSession session = null;
		try {
			tbc.log("#testCommit002");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_SHARED);
			session.commit();
			tbc.failException("#", DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserting DmtException type",
					DmtException.FEATURE_NOT_SUPPORTED, e.getCode());
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName() + " [Message: " + e.getMessage() +"]");
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * @testID testCommit003
	 * @testDescription This method asserts that an IllegalStateException is
	 *                  thrown when a session is already closed
	 */
	private void testCommit003() {
		DmtSession session = null;
		try {
			tbc.log("#testCommit003");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_ATOMIC);
			session.close();
			session.commit();
			tbc.failException("#", IllegalStateException.class);
		} catch (IllegalStateException e) {
			tbc.pass("The exception was IllegalStateException");
		} catch (Exception e) {
			tbc.fail("Expected " + IllegalStateException.class.getName()
					+ " but was " + e.getClass().getName());
		} finally {
			tbc.closeSession(session);
		}
	}
    
    /**
     * @testID testCommit004
     * @testDescription This method asserts that commit() will pass correctly.
     */
    private void testCommit004() {
        DmtSession session = null;
        try {
            tbc.log("#testCommit004");
            session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_ATOMIC);
            session.commit();
            session.close();
            tbc.pass("Asserting that session were commited correctly");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalStateException.class.getName()
                    + " but was " + e.getClass().getName());
        } finally {
            tbc.closeSession(session);
        }
    }
    
}

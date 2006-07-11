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

package org.osgi.test.cases.dmt.tc2.tb1.DmtSession;

import info.dmtree.DmtSession;
import info.dmtree.DmtIllegalStateException;
import info.dmtree.security.DmtPermission;

import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.TestInterface;
/**
 * @author Luiz Felipe Guimaraes
 * 
 * This test case validates the implementation of <code>commit</code> method of DmtSession, 
 * according to MEG specification.
 * 
 */
public class Commit implements TestInterface {
	private DmtTestControl tbc;

	public Commit(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
        prepare();
		testCommit001();
		testCommit002();
		testCommit003();
        testCommit004();
	}
    private void prepare() {
        tbc.setPermissions(new PermissionInfo[] {
                new PermissionInfo(DmtPermission.class.getName(), ".", DmtPermission.GET)
        });
    }
	/**
	 * This method asserts that whenever a DmtSession with LOCK_TYPE_EXCLUSIVE 
	 * lock is created, the commit operation is not supported.
	 * 
	 * @spec DmtSession.commit()
	 */
	private void testCommit001() {
		DmtSession session = null;
		try {
			tbc.log("#testCommit001");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.commit();
			tbc.failException("#", DmtIllegalStateException.class);
		} catch (DmtIllegalStateException e) {
			tbc.pass("DmtIllegalStateException is thrown if the session is tried to commit a non-atomic session");
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtIllegalStateException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts that whenever a DmtSession with LOCK_TYPE_SHARED lock is created, 
	 * the commit operation is not supported (second case).
	 * 
	 * @spec DmtSession.commit()
	 */
	private void testCommit002() {
		DmtSession session = null;
		try {
			tbc.log("#testCommit002");
			session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_SHARED);
			session.commit();
			tbc.failException("#", DmtIllegalStateException.class);
		} catch (DmtIllegalStateException e) {
			tbc.pass("DmtIllegalStateException is thrown if the session is tried to commit a non-atomic session");
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtIllegalStateException.class, e);
		} finally {
			tbc.closeSession(session);
		}
	}

    
    /**
     * This method asserts that commit() passes correctly.
     * 
     * @spec DmtSession.commit()
     */
    private void testCommit003() {
        DmtSession session = null;
        try {
            tbc.log("#testCommit003");
            session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_ATOMIC);
            session.commit();
            session.close();
            tbc.pass("Asserting that session was committed correctly");
        } catch (Exception e) {
            tbc.failUnexpectedException(e);
        } finally {
            tbc.closeSession(session);
        }
    }
    
    /**
     * This method asserts that after a commit() the session is not closed.
     * 
     * @spec DmtSession.commit()
     */
    private void testCommit004() {
        DmtSession session = null;
        try {
            tbc.log("#testCommit004");
            session = tbc.getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_ATOMIC);
            session.commit();
            tbc.assertEquals("Asserting that after a commit(), the session is not closed.", session.getState(), DmtSession.STATE_OPEN);
        } catch (Exception e) {
        	tbc.failUnexpectedException(e);
        } finally {
            tbc.closeSession(session);
        }
    }
    
}

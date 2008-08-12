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
 */

package org.osgi.test.cases.dmt.main.tb1.DmtSession;

import org.osgi.service.dmt.DmtSession;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.main.tbc.TestInterface;

/**
 * @author Andre Assad
 * 
 * This test case validates the implementation of <code>getLockType<code> method of DmtSession, 
 * according to MEG specification
 */
public class GetLockType implements TestInterface {
	private DmtTestControl tbc;

	public GetLockType(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
        prepare();
		testGetLockType001();
		testGetLockType002();
		testGetLockType003();
	}

    private void prepare() {
        //This method do not throw any exceptions, so, if it is checking for DmtPermission an exception is
        //incorrectly thrown.
        tbc.setPermissions(new PermissionInfo[0]);
    }
	/**
	 * This method asserts that DmtSession.LOCK_TYPE_ATOMIC is returned when
	 * getLockType is called in an atomic session 
	 * 
	 * @spec DmtSession.getLockType()
	 */
	private void testGetLockType001() {
		DmtSession session = null;
	    try {
			tbc.log("#testGetLockType001");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_ATOMIC);
	        tbc.assertEquals("Asserting lock type", DmtSession.LOCK_TYPE_ATOMIC,
	                session.getLockType());
	    } catch (Exception e) {
	    	tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
	    }  finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts that DmtSession.LOCK_TYPE_EXCLUSIVE is returned when
	 * getLockType is called in an exclusive session 
	 * 
	 * @spec DmtSession.getLockType()
	 */
	private void testGetLockType002() {
		DmtSession session = null;
	    try {
			tbc.log("#testGetLockType002");
			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_EXCLUSIVE);
	        tbc.assertEquals("Asserting lock type", DmtSession.LOCK_TYPE_EXCLUSIVE,
	                session.getLockType());
	    } catch (Exception e) {
	    	tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
	    } finally {
			tbc.closeSession(session);
		}
	}

	/**
	 * This method asserts that DmtSession.LOCK_TYPE_SHARED is returned when
	 * getLockType is called in a read only session 
	 * 
	 * @spec DmtSession.getLockType()
	 */
	private void testGetLockType003() {
		DmtSession session = null;
	    try {
			tbc.log("#testGetLockType003");

			session = tbc.getDmtAdmin().getSession(".",
					DmtSession.LOCK_TYPE_SHARED);

	        tbc.assertEquals("Asserting lock type", DmtSession.LOCK_TYPE_SHARED,
	                session.getLockType());
	    } catch (Exception e) {
	    	tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");
	    } finally {
			tbc.closeSession(session);
		}       
	}
    
}

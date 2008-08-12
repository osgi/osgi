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

package org.osgi.test.cases.dmt.tbc.DmtSession;

import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtSession#getLockType
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>getLockType<code> method, according to MEG reference
 *                     documentation.
 */
public class GetLockType {
	private DmtTestControl tbc;

	public GetLockType(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetLockType001();
		testGetLockType002();
		testGetLockType003();
        testGetLockType004();
        testGetLockType005();
	}

	/**
	 * @testID testGetLockType001
	 * @testDescription This method asserts the current type of the session's
	 *                  lock.
	 */
	private void testGetLockType001() {
	    try {
	        tbc.getSession(".", DmtSession.LOCK_TYPE_ATOMIC);
	        tbc.assertEquals("Asserting lock type", DmtSession.LOCK_TYPE_ATOMIC,
	                tbc.getSession().getLockType());
	    } catch (Exception e) {
	        tbc.fail("Unexpected exception.");
	    }
	}

	/**
	 * @testID testGetLockType002
	 * @testDescription This method asserts the current type of the session's
	 *                  lock.
	 */
	private void testGetLockType002() {
	    try {
	        tbc.getSession(".", DmtSession.LOCK_TYPE_EXCLUSIVE);
	        tbc.assertEquals("Asserting lock type", DmtSession.LOCK_TYPE_EXCLUSIVE,
	                tbc.getSession().getLockType());
	    }catch (Exception e) {
	        tbc.fail("Unexpected exception.");
	    }
	}

	/**
	 * @testID testGetLockType003
	 * @testDescription This method asserts the current type of the session's
	 *                  lock.
	 */
	private void testGetLockType003() {
	    try {
	        tbc.getSession(".", DmtSession.LOCK_TYPE_SHARED);
	        tbc.assertEquals("Asserting lock type", DmtSession.LOCK_TYPE_SHARED,
	                tbc.getSession().getLockType());
	    }catch (Exception e) {
	        tbc.fail("Unexpected exception.");
	    }        
	}
    
    /**
     * @testID testGetLockType004
     * @testDescription This method asserts that an IllegalStateException is thrown when attemps to get a lock type of a closed session.
     */
    private void testGetLockType004() {
        
        try {
            tbc.getSession().close();
            tbc.getSession().getLockType();
            tbc.failException("", IllegalStateException.class);
        } catch (IllegalStateException e) {
            tbc.pass("The Exception was IllegalStateException");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalStateException.class.getName() + " but was "+e.getClass().getName());
        }
        
    }    
    
    /**
     * @testID testGetLockType005
     * @testDescription This method asserts that an IllegalStateException is thrown when attemps to get a lock type after a rollback.
     */
    private void testGetLockType005() {
        
        try {
            tbc.getSession().rollback();
            tbc.getSession().getLockType();
            tbc.failException("", IllegalStateException.class);
        } catch (IllegalStateException e) {
            tbc.pass("The Exception was IllegalStateException");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalStateException.class.getName() + " but was "+e.getClass().getName());
        }
        
    }        

}

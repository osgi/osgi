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
 * @methodUnderTest org.osgi.service.dmt.DmtSession#getPrincipal
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>getPrincipal<code> method, according to MEG reference
 *                     documentation.
 */
public class GetPrincipal {
	private DmtTestControl tbc;

	public GetPrincipal(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetPrincipal001();
		testGetPrincipal002();
        testGetPrincipal003();
        testGetPrincipal004();
	}

	/**
	 * @testID testGetPrincipal001
	 * @testDescription This method asserts that the same principal passed on
	 *                  the tbc.getSession() creation, really belongs to the
	 *                  tbc.getSession() object created.
	 */
	private void testGetPrincipal001() {
	    try {        
	        tbc.getSession(DmtTestControl.PRINCIPAL, ".", DmtSession.LOCK_TYPE_ATOMIC);
	        
	        tbc.assertEquals("Asserting tbc.getSession() principal", DmtTestControl.PRINCIPAL, tbc
	                .getSession().getPrincipal());
	    } catch (Exception e) {
	        tbc.fail("Unexpected exception " + e.getClass());
	    }
	}

	/**
	 * @testID testGetPrincipal002
	 * @testDescription This method asserts that for a local tbc.getSession() a
	 *                  null principal is created.
	 */
	private void testGetPrincipal002() {
	    try {
	        tbc.getSession(".", DmtSession.LOCK_TYPE_ATOMIC);
	        tbc.assertNull("Asserting tbc.getSession() principal", tbc
	                .getSession().getPrincipal());
	    } catch (Exception e) {
	        tbc.fail("Unexpected exception " + e.getClass());
	    }        
	}
    
    /**
     * @testID testGetSessionId003
     * @testDescription This method asserts that an IllegalStateException is thrown when attemps to get a Principal of a closed session.
     */
    private void testGetPrincipal003() {
        
        try {
            tbc.getSession().close();
            tbc.getSession().getPrincipal();
            tbc.fail("It gets a Principal of a closed session.");
        } catch (IllegalArgumentException e) {
            tbc.pass("The Exception was IllegalStateException");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalArgumentException.class.getName() + " but was "+e.getClass().getName());
        }
        
    }    
    
    /**
     * @testID testGetSessionId004
     * @testDescription This method asserts that an IllegalStateException is thrown when attemps to get a Principal after a rollback.
     */
    private void testGetPrincipal004() {
        
        try {
            tbc.getSession().rollback();
            tbc.getSession().getPrincipal();
            tbc.failException("", IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            tbc.pass("The Exception was IllegalStateException");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalArgumentException.class.getName() + " but was "+e.getClass().getName());
        }
        
    }      
}

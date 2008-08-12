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
 * Feb 11, 2005 Alexandre Santos
 * 1            Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ===========  ==============================================================
 */

package org.osgi.test.cases.dmt.tbc.DmtSession;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtSession#getSessionId
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>getSessionId<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class GetSessionId {
	private DmtTestControl tbc;

	public GetSessionId(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetSessionId001();
        testGetSessionId002();
        testGetSessionId003();
	}

	/**
	 * @testID testGetSessionId001
     * @testDescription This method asserts that two different sessions has differents ids
	 */
	private void testGetSessionId001() {
		DmtSession session1, session2 = null;
        
        try {
            session1 = tbc.getSession(".");
            session2 = tbc.getFactory().getSession(".");
            tbc.assertNotSame("Asserting that session1 and session2 has differents ids.", new Integer(session1.getSessionId()), new Integer(session2.getSessionId()));
        } catch (DmtException e) {
            tbc.fail("Fatal error. It doesn't have to enter here. ");
        } finally {
        	try {
				session2.close();
			} catch (DmtException e1) {
				tbc.fail("Unexpected DmtException");
			}
        }
        
	}
    
    /**
     * @testID testGetSessionId002
     * @testDescription This method asserts that an IllegalStateException is thrown when attemps to get a sessionId of a closed session.
     */
    private void testGetSessionId002() {
        
        try {
            tbc.getSession().close();
            tbc.getSession().getSessionId();
            tbc.failException("", IllegalStateException.class);
        } catch (IllegalStateException e) {
            tbc.pass("The Exception was IllegalStateException");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalStateException.class.getName() + " but was "+e.getClass().getName());
        }
        
    }
    
    /**
     * @testID testGetSessionId003
     * @testDescription This method asserts that an IllegalStateException is thrown when attemps to get a sessionId after a rollback.
     */
    private void testGetSessionId003() {
        
        try {
            tbc.getSession().rollback();
            tbc.getSession().getSessionId();
            tbc.failException("", IllegalStateException.class);
        } catch (IllegalStateException e) {
            tbc.pass("The Exception was IllegalStateException");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalStateException.class.getName() + " but was "+e.getClass().getName());
        }
        
    }    
    
}

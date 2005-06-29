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
 * 
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
 * Feb 11, 2005  Alexandre Santos
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-002)
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tbc.DmtAdmin;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * This Test Class Validates the implementation of
 *                     <code>getSession<code> constructor, according to MEG reference
 *                     documentation (rfc0085).
 */
public class GetSession {
    //TODO TCs for TIMEOUT in all getSessions
	private DmtTestControl tbc;


	public GetSession(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetSession001();
		testGetSession002();
		testGetSession003();
        testGetSession004();
        testGetSession005();
        testGetSession006();
        testGetSession007();
        testGetSession008();
        testGetSession009();
        testGetSession010();
        testGetSession011();
	}

	/**
	 * 
	 *  This method asserts that a session is opened with the
     *                  specified subtree, lock type and principal. This constructor use by default LOCK_TYPE_EXCLUSIVE
	 * @spec DmtAdmin.getSession(String)
	 */
	public void testGetSession001() {
		try {
			DmtSession session = tbc.getFactory().getSession(DmtTestControl.OSGi_LOG);
			tbc.assertEquals("Asserting subtree", DmtTestControl.OSGi_LOG, session.getRootUri());
            tbc.assertEquals("Asserting lock type", DmtSession.LOCK_TYPE_EXCLUSIVE, session.getLockType());
            tbc.assertNull("Asserting principal", session.getPrincipal());
		} catch (DmtException e) {
            tbc.fail("Fatal error. It doesn't have to enter here. ");
        } catch (Exception e) {
            tbc.fail("The exception wasn't DmtException.");
        }
	}
    
    /**
     * 
     *  This method asserts if an invalid node causes 
     *                  an exception with NODE_NOT_FOUND code
	 * @spec DmtAdmin.getSession(String)
     */
    public void testGetSession002() {
        try {
            DmtSession session = tbc.getFactory().getSession(DmtTestControl.INVALID_NODE);
            tbc.fail("The session was open and it didn't thrown an exception.");
        } catch (DmtException e) {
            tbc.assertEquals("Asserting if the exception's code is NODE_NOT_FOUND.", DmtException.NODE_NOT_FOUND, e.getCode());
        } catch (Exception e) {
            tbc.fail("The exception wasn't DmtException.");
        }
    }
    
    /**
     * 
     *  This method asserts if a too long uri causes 
     *                  an exception with URI_TOO_LONG code
	 * @spec DmtAdmin.getSession(String)
     */
    public void testGetSession003() {
        try {
            DmtSession session = tbc.getFactory().getSession(DmtTestControl.URI_LONG);
            tbc.fail("The session was open and it didn't thrown an exception.");
        } catch (DmtException e) {
            tbc.assertEquals("Asserting if the exception's code is URI_TOO_LONG.", DmtException.URI_TOO_LONG, e.getCode());
        } catch (Exception e) {
            tbc.fail("The exception wasn't DmtException.");
        }
    }    
    
    /**
     * 
     *  This method asserts if a node using invalid characters causes 
     *                  an exception with INVALID_URI code
	 * @spec DmtAdmin.getSession(String)
     */
    public void testGetSession004() {
        try {
            DmtSession session = tbc.getFactory().getSession(DmtTestControl.INVALID_URI);
            tbc.fail("The session was open and it didn't thrown an exception.");
        } catch (DmtException e) {
            tbc.assertEquals("Asserting if the exception's code is INVALID_URI.", DmtException.INVALID_URI, e.getCode());
        } catch (Exception e) {
            tbc.fail("The exception wasn't DmtException.");
        }
    }        
    

	/**
     * 
     *  This method asserts that a session is opened with the
     *                  specified subtree, lock type and principal. This constructor use by default LOCK_TYPE_SHARED
	 * @spec DmtAdmin.getSession(String)
	 */
	public void testGetSession005() {
        try {
            DmtSession session = tbc.getFactory().getSession(DmtTestControl.OSGi_LOG, DmtSession.LOCK_TYPE_SHARED);
            tbc.assertEquals("Asserting subtree", DmtTestControl.OSGi_LOG, session.getRootUri());
            tbc.assertEquals("Asserting lock type", DmtSession.LOCK_TYPE_SHARED, session.getLockType());
            tbc.assertNull("Asserting principal", session.getPrincipal());
        } catch (DmtException e) {
            tbc.fail("Fatal error. It doesn't have to enter here. ");
        } catch (Exception e) {
            tbc.fail("The exception wasn't DmtException.");
        }
	}
    
    /**
     * 
     *  This method asserts if an invalid node causes 
     *                  an exception with NODE_NOT_FOUND code
	 * @spec DmtAdmin.getSession(String,int)
	 * @spec DmtSession.LOCK_TYPE_SHARED
     */
    public void testGetSession006() {
        try {
            DmtSession session = tbc.getFactory().getSession(DmtTestControl.INVALID_NODE, DmtSession.LOCK_TYPE_SHARED);
            tbc.fail("The session was open and it didn't thrown an exception.");
        } catch (DmtException e) {
            tbc.assertEquals("Asserting if the exception's code is NODE_NOT_FOUND.", DmtException.NODE_NOT_FOUND, e.getCode());
        } catch (Exception e) {
            tbc.fail("The exception wasn't DmtException.");
        }
    }
    
    /**
     * 
     *  This method asserts if a too long uri causes 
     *                  an exception with URI_TOO_LONG code
	 * @spec DmtAdmin.getSession(String,int)
	 * @spec DmtSession.LOCK_TYPE_SHARED
     */
    public void testGetSession007() {
        try {
            DmtSession session = tbc.getFactory().getSession(DmtTestControl.URI_LONG, DmtSession.LOCK_TYPE_SHARED);
            tbc.fail("The session was open and it didn't thrown an exception.");
        } catch (DmtException e) {
            tbc.assertEquals("Asserting if the exception's code is URI_TOO_LONG.", DmtException.URI_TOO_LONG, e.getCode());
        } catch (Exception e) {
            tbc.fail("The exception wasn't DmtException.");
        }
    }        
    
    /**
     * 
     *  This method asserts if a invalid lock mode causes 
     *                  an exception with OTHER_ERROR code
	 * @spec DmtAdmin.getSession(String,int)
     */
    public void testGetSession008() {
        try {
            DmtSession session = tbc.getFactory().getSession(DmtTestControl.OSGi_LOG, DmtTestControl.INVALID_LOCKMODE);
            tbc.fail("The session was open and it didn't thrown an exception.");
        } catch (DmtException e) {
            tbc.assertEquals("Asserting if the exception's code is OTHER_ERROR.", DmtException.OTHER_ERROR, e.getCode());
        } catch (Exception e) {
            tbc.fail("The exception wasn't DmtException.");
        }
    }       
    
    /**
     * 
     *  This method asserts if a node using invalid characters causes 
     *                  an exception with INVALID_URI code
	 * @spec DmtAdmin.getSession(String,int)
	 * @spec DmtSession.LOCK_TYPE_SHARED
     */
    public void testGetSession009() {
        try {
            DmtSession session = tbc.getFactory().getSession(DmtTestControl.INVALID_URI, DmtSession.LOCK_TYPE_SHARED);
            tbc.fail("The session was open and it didn't thrown an exception.");
        } catch (DmtException e) {
            tbc.assertEquals("Asserting if the exception's code is INVALID_URI.", DmtException.INVALID_URI, e.getCode());
        } catch (Exception e) {
            tbc.fail("The exception wasn't DmtException.");
        }
    }          

	/**
	 * 
     *  This method asserts that a session is opened with the
     *                  specified subtree, lock type and principal. This constructor use by default LOCK_TYPE_ATOMIC
     *                  
     * TODO TCs for java.lang.SecurityException using this getSessions
     * TODO remove String principal ?!?! (String principal) but I need it to test the getSession with 3 arguments.
	 * @spec DmtAdmin.getSession(String,String,int)
	 * @spec DmtSession.LOCK_TYPE_ATOMIC
	 */
	public void testGetSession010() {
        try {
            DmtSession session = tbc.getFactory().getSession(DmtTestControl.PRINCIPAL, DmtTestControl.OSGi_LOG, DmtSession.LOCK_TYPE_ATOMIC);
            tbc.assertEquals("Asserting subtree", DmtTestControl.OSGi_LOG, session.getRootUri());
            tbc.assertEquals("Asserting lock type", DmtSession.LOCK_TYPE_ATOMIC, session.getLockType());
            tbc.assertEquals("Asserting principal", DmtTestControl.PRINCIPAL, session.getPrincipal());
        } catch (DmtException e) {
            tbc.fail("Fatal error. It doesn't have to enter here. ");
        } catch (Exception e) {
            tbc.fail("The exception wasn't DmtException.");
        }
	}
    
    /**
     * 
     *  This method asserts if an invalid node causes 
     *                  an exception with NODE_NOT_FOUND code
	 * @spec DmtAdmin.getSession(String,String,int)
	 * @spec DmtSession.LOCK_TYPE_SHARED
     */
    public void testGetSession011() {
        try {
            DmtSession session = tbc.getFactory().getSession(DmtTestControl.PRINCIPAL, DmtTestControl.INVALID_NODE, DmtSession.LOCK_TYPE_SHARED);
            tbc.fail("The session was open and it didn't thrown an exception.");
        } catch (DmtException e) {
            tbc.assertEquals("Asserting if the exception's code is NODE_NOT_FOUND.", DmtException.NODE_NOT_FOUND, e.getCode());
        } catch (Exception e) {
            tbc.fail("The exception wasn't DmtException.");
        }
    }
    
    /**
     * 
     *  This method asserts if a too long uri causes 
     *                  an exception with URI_TOO_LONG code
	 * @spec DmtAdmin.getSession(String,String,int)
	 * @spec DmtSession.LOCK_TYPE_SHARED
     */
    public void testGetSession012() {
        try {
            DmtSession session = tbc.getFactory().getSession(DmtTestControl.PRINCIPAL, DmtTestControl.URI_LONG, DmtSession.LOCK_TYPE_SHARED);
            tbc.fail("The session was open and it didn't thrown an exception.");
        } catch (DmtException e) {
            tbc.assertEquals("Asserting if the exception's code is URI_TOO_LONG.", DmtException.URI_TOO_LONG, e.getCode());
        } catch (Exception e) {
            tbc.fail("The exception wasn't DmtException.");
        }
    }        
    
    /**
     * 
     *  This method asserts if a invalid lock mode causes 
     *                  an exception with OTHER_ERROR code
	 * @spec DmtAdmin.getSession(String,String,int)
     */
    public void testGetSession013() {
        try {
            DmtSession session = tbc.getFactory().getSession(DmtTestControl.PRINCIPAL, DmtTestControl.OSGi_LOG, DmtTestControl.INVALID_LOCKMODE);
            tbc.fail("The session was open and it didn't thrown an exception.");
        } catch (DmtException e) {
            tbc.assertEquals("Asserting if the exception's code is OTHER_ERROR.", DmtException.OTHER_ERROR, e.getCode());
        } catch (Exception e) {
            tbc.fail("The exception wasn't DmtException.");
        }
    }
    
    /**
     * 
     *  This method asserts if a node using invalid characters causes 
     *                  an exception with INVALID_URI code
	 * @spec DmtAdmin.getSession(String,String,int)
	 * @spec DmtSession.LOCK_TYPE_SHARED
     */
    public void testGetSession014() {
        try {
            DmtSession session = tbc.getFactory().getSession(DmtTestControl.PRINCIPAL, DmtTestControl.INVALID_URI, DmtSession.LOCK_TYPE_SHARED);
            tbc.fail("The session was open and it didn't thrown an exception.");
        } catch (DmtException e) {
            tbc.assertEquals("Asserting if the exception's code is INVALID_URI.", DmtException.INVALID_URI, e.getCode());
        } catch (Exception e) {
            tbc.fail("The exception wasn't DmtException.");
        }
    }       

}

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
 * Jan 26, 2005  Andre Assad
 * CR 1          Implement MEG TCK
 * ============  ==============================================================
 * Feb 15, 2005  Alexandre Santos
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ===========  ===============================================================
 */

package org.osgi.test.cases.dmt.tbc.DmtSession;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtMetaNode;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtSession#getMetaData
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>getMetaData<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class GetMetaNode {
	// TODO TCs for PERMISSION_DENIED, COMMAND_FAILED, COMMAND_NOT_ALLOWED, FEATURE_NOT_SUPPORTED, DATA_STORE_FAILURE
	private DmtTestControl tbc;

	public GetMetaNode(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
        testGetMetaNode001();
        testGetMetaNode002();
        testGetMetaNode003();
        testGetMetaNode004();
        testGetMetaNode005();
        testGetMetaNode006();
	}

	/**
	 * @testID testGetMetaNode001
	 * @testDescription Assert if a DmtException is thrown when a node not found is
	 *                  passed.
	 */
	private void testGetMetaNode001() {
	    try {
	        DmtMetaNode metaNode = tbc.getSession().getMetaNode(DmtTestControl.INVALID_NODE);
            tbc.failException("", DmtException.class);
	    } catch (DmtException e) {
	        tbc.assertEquals("Asserting that DmtException code is NODE_NOT_FOUND", DmtException.NODE_NOT_FOUND, e.getCode());
	    } catch (Exception e) {
	        tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
	    } 
	}
    
    /**
     * @testID testGetMetaNode002
     * @testDescription Assert if a DmtException is thrown when an uri too long is
     *                  passed.
     */
    private void testGetMetaNode002() {
        try {
            DmtMetaNode metaNode = tbc.getSession().getMetaNode(DmtTestControl.URI_LONG);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is URI_TOO_LONG", DmtException.URI_TOO_LONG, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        } 
    }
    
    /**
     * @testID testGetMetaNode003
     * @testDescription Assert if a DmtException is thrown when an invalid uri is
     *                  passed.
     */
    private void testGetMetaNode003() {
        try {
            DmtMetaNode metaNode = tbc.getSession().getMetaNode(DmtTestControl.INVALID_URI);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is INVALID_URI", DmtException.INVALID_URI, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        } 
    }        
    
    /**
     * @testID testGetMetaNode004
     * @testDescription Assert if a DmtException is thrown when a node that can't
     *                  be acessed is passed.
     */
    private void testGetMetaNode004() {
        
        try {
            DmtSession session = tbc.getSession(DmtTestControl.OSGi_LOG);
            DmtMetaNode metaNode = session.getMetaNode(DmtTestControl.OSGi_ROOT);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is OTHER_ERROR", DmtException.OTHER_ERROR, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }     
    }
    
    /**
     * @testID testGetMetaNode005
     * @testDescription This method asserts that an IllegalStateException is thrown when attemps to get a meta node of a closed session
     */
    private void testGetMetaNode005() {
        
        try {
            tbc.getSession().close();
            DmtMetaNode metaNode = tbc.getSession().getMetaNode(DmtTestControl.OSGi_ROOT);
            tbc.failException("", IllegalStateException.class);
        } catch (IllegalStateException e) {
            tbc.pass("The Exception was IllegalStateException when use a closed session");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalStateException.class.getName() + " but was "+e.getClass().getName());
        }
        
    }  
    
    /**
     * @testID testGetMetaNode006
     * @testDescription This method asserts that an IllegalStateException is thrown when attemps to get a meta node after a rollback.
     */
    private void testGetMetaNode006() {
        
        try {
            tbc.getSession().rollback();
            DmtMetaNode metaNode = tbc.getSession().getMetaNode(DmtTestControl.OSGi_ROOT);
            tbc.failException("", IllegalStateException.class);
        } catch (IllegalStateException e) {
            tbc.pass("The Exception was IllegalStateException when use it after a rollback.");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalStateException.class.getName() + " but was "+e.getClass().getName());
        }
        
    }      
    
}

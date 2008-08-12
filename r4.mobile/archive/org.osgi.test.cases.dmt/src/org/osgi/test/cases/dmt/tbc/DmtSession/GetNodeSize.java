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
 * ===========  ============================================================== 
 */

package org.osgi.test.cases.dmt.tbc.DmtSession;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtSession#getNodeSize
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>getNodeSize<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class GetNodeSize {
    //TODO TCs for PERMISSION_DENIED, COMMAND_FAILED, COMMAND_NOT_ALLOWED, FEATURE_NOT_SUPPORTED, DATA_STORE_FAILURE
	private DmtTestControl tbc;

	public GetNodeSize(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetNodeSize001();
		testGetNodeSize002();
		testGetNodeSize003();
        testGetNodeSize004();
        testGetNodeSize005();
        testGetNodeSize006();
        testGetNodeSize007();
	}

	/**
	 * @testID testGetNodeSize001
	 * @testDescription This method asserts the size of the DmtData of a String
	 *                  leaf node types.
	 */
	private void testGetNodeSize001() {
	    String value = "value";
	    try {
	        tbc.getSession().createInteriorNode(DmtTestControl.SOURCE);
	        tbc.getSession().createLeafNode(DmtTestControl.SOURCE_LEAF, new DmtData(value));
	        tbc.assertEquals("Asserting string node data size in bytes", value
	                .getBytes().length, tbc.getSession().getNodeSize(DmtTestControl.SOURCE_LEAF));
	    } catch (DmtException e) {
	        tbc.fail("Unexpected Exception");            
	    } finally {
	        tbc.cleanUp(new String[] { DmtTestControl.SOURCE_LEAF, DmtTestControl.SOURCE });
	    }
	}

    /**
     * @testID testGetNodeSize002
     * @testDescription This method asserts that getNodeSize correctly throws
     *                  DmtException when a unexistent node is used.
     */
    private void testGetNodeSize002() {
        try {
            tbc.getSession().getNodeSize(DmtTestControl.INVALID_NODE);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException's code is NODE_NOT_FOUND", DmtException.NODE_NOT_FOUND, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }  
    }
    
    /**
     * @testID testGetNodeSize003
     * @testDescription This method asserts that a DmtException with error code equals to 
     *                  URI_TOO_LONG is thrown.
     */
    private void testGetNodeSize003() {
        
        try {
            tbc.getSession().getNodeSize(DmtTestControl.URI_LONG);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is URI_TOO_LONG", DmtException.URI_TOO_LONG, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }        
    }        
    
    /**
     * @testID testGetNodeSize004
     * @testDescription This method asserts that a DmtException with error code equals to 
     *                  INVALID_URI is thrown.
     */
    private void testGetNodeSize004() {
        
        try {
            tbc.getSession().getNodeSize(DmtTestControl.INVALID_URI);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is INVALID_URI", DmtException.INVALID_URI, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }        
    }           

    /**
     * @testID testGetNodeSize005
     * @testDescription This method asserts that a DmtException with error code equals to 
     *                  OTHER_ERROR is thrown.
     */
    private void testGetNodeSize005() {
        
        try {
            tbc.getSession().createInteriorNode(DmtTestControl.SOURCE);
            tbc.getSession().createInteriorNode(DmtTestControl.DESTINY);
            DmtSession session = tbc.getSession(DmtTestControl.SOURCE);
            session.getNodeSize(DmtTestControl.DESTINY);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is OTHER_ERROR", DmtException.OTHER_ERROR, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        } finally { 
            tbc.cleanUp(new String[] { DmtTestControl.SOURCE, DmtTestControl.DESTINY });
        }
    }   
    
    /**
     * @testID testGetNodeSize006
     * @testDescription This method asserts that an IllegalStateException is thrown when attemps to execute getNodeSize using a closed session.
     */
    private void testGetNodeSize006() {
        
        try {
            tbc.getSession().close();
            tbc.getSession().getNodeSize(DmtTestControl.SOURCE);
            tbc.failException("", IllegalStateException.class);
        } catch (IllegalStateException e) {
            tbc.pass("The Exception was IllegalStateException");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalStateException.class.getName() + " but was "+e.getClass().getName());
        }
        
    }       
    
    /**
     * @testID testGetNodeSize007
     * @testDescription This method asserts that an IllegalStateException is thrown when attemps to execute the getNodeSize after a rollback.
     */
    private void testGetNodeSize007() {
        
        try {
            tbc.getSession().rollback();
            tbc.getSession().getNodeSize(DmtTestControl.SOURCE);
            tbc.failException("", IllegalStateException.class);
        } catch (IllegalStateException e) {
            tbc.pass("The Exception was IllegalStateException");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalStateException.class.getName() + " but was "+e.getClass().getName());
        }
        
    }       
    
}

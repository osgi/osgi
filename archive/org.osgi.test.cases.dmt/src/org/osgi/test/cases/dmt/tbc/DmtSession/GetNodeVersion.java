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
 * @methodUnderTest org.osgi.service.dmt.DmtReadOnly#getNodeVersion
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>getNodeVersion<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class GetNodeVersion {
	private DmtTestControl tbc;

	/**
	 * Constructor
	 * 
	 * @param tbc
	 */
	public GetNodeVersion(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetNodeVersion001();
		testGetNodeVersion002();
        testGetNodeVersion003();
        testGetNodeVersion004();
        testGetNodeVersion005();
        testGetNodeVersion006();
        testGetNodeVersion007();
	}

	/**
	 * @testID testGetNodeVersion001
	 * @testDescription This test method asserts that when a data value of a
	 *                  node changes its version changes as well. <br>
	 *                  Assumption: when a node is created its version is set to
	 *                  1.
	 */
	private void testGetNodeVersion001() {
		try {
			tbc.getSession().createInteriorNode(DmtTestControl.SOURCE);
			tbc.getSession().createLeafNode(DmtTestControl.SOURCE_LEAF, new DmtData(10));

			tbc.assertEquals("Asserting internal node version", 1, tbc
					.getSession().getNodeVersion(DmtTestControl.SOURCE));
			tbc.assertEquals("Asserting node first version", 1, tbc
					.getSession().getNodeVersion(DmtTestControl.SOURCE_LEAF));
			tbc.getSession().setNodeValue(DmtTestControl.SOURCE_LEAF, new DmtData(11));
			tbc.assertEquals("Asserting second version", 2, tbc.getSession()
					.getNodeVersion(DmtTestControl.SOURCE_LEAF));
        } catch (DmtException e) {
            tbc.fail("Unexpected Exception");
		} catch (Exception e) {
			tbc.fail("Unexpected exception: "+e.getClass().getName());            
		} finally {
			tbc.cleanUp(new String[] { DmtTestControl.SOURCE_LEAF, DmtTestControl.SOURCE});
		}
	}

    /**
     * @testID testGetNodeVersion002
     * @testDescription This method asserts that getNodeVersion correctly throws
     *                  DmtException when a unexistent node is used.
     */
    private void testGetNodeVersion002() {
        try {
            tbc.getSession().getNodeVersion(DmtTestControl.INVALID_NODE);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException's code is NODE_NOT_FOUND", DmtException.NODE_NOT_FOUND, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }  
    }
    
    /**
     * @testID testGetNodeVersion003
     * @testDescription This method asserts that a DmtException with error code equals to 
     *                  URI_TOO_LONG is thrown.
     */
    private void testGetNodeVersion003() {
        
        try {
            tbc.getSession().getNodeVersion(DmtTestControl.URI_LONG);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is URI_TOO_LONG", DmtException.URI_TOO_LONG, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }        
    }  
    
    /**
     * @testID testGetNodeVersion004
     * @testDescription This method asserts that a DmtException with error code equals to 
     *                  INVALID_URI is thrown.
     */
    private void testGetNodeVersion004() {
        
        try {
            tbc.getSession().getNodeVersion(DmtTestControl.INVALID_URI);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is INVALID_URI", DmtException.INVALID_URI, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }        
    }      
    
    /**
     * @testID testGetNodeVersion005
     * @testDescription This method asserts that a DmtException with error code equals to 
     *                  OTHER_ERROR is thrown.
     */
    private void testGetNodeVersion005() {
        
        try {
            tbc.getSession().createInteriorNode(DmtTestControl.SOURCE);
            tbc.getSession().createInteriorNode(DmtTestControl.DESTINY);
            DmtSession session = tbc.getSession(DmtTestControl.SOURCE);
            session.getNodeVersion(DmtTestControl.DESTINY);
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
     * @testID testGetNodeVersion006
     * @testDescription This method asserts that an IllegalStateException is thrown when attemps to execute getNodeVersion using a closed session.
     */
    private void testGetNodeVersion006() {
        
        try {
            tbc.getSession().close();
            tbc.getSession().getNodeVersion(DmtTestControl.SOURCE);
            tbc.failException("", IllegalStateException.class);
        } catch (IllegalStateException e) {
            tbc.pass("The Exception was IllegalStateException");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalStateException.class.getName() + " but was "+e.getClass().getName());
        }
        
    }       
    
    /**
     * @testID testGetNodeVersion007
     * @testDescription This method asserts that an IllegalStateException is thrown when attemps to execute the getNodeVersion after a rollback.
     */
    private void testGetNodeVersion007() {
        
        try {
            tbc.getSession().rollback();
            tbc.getSession().getNodeVersion(DmtTestControl.SOURCE);
            tbc.failException("", IllegalStateException.class);
        } catch (IllegalStateException e) {
            tbc.pass("The Exception was IllegalStateException");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalStateException.class.getName() + " but was "+e.getClass().getName());
        }
        
    }       
    
}

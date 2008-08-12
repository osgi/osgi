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

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtSession#isLeafNode
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>isLeafNode<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class IsLeafNode {
	private DmtTestControl tbc;

	public IsLeafNode(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testIsLeafNode001();
		testIsLeafNode002();
        testIsLeafNode003();
        testIsLeafNode004();
	}

	/**
	 * @testID testIsLeafNode001
	 * @testDescription This method creates a new leaf node and test wheather
	 *                  the node is a leaf or an interior node.
	 */

	private void testIsLeafNode001() {
	    try {
	        tbc.getSession().createInteriorNode(DmtTestControl.SOURCE);
	        tbc.getSession().createLeafNode(DmtTestControl.SOURCE_LEAF, new DmtData("string"));
	        
	        tbc.assertTrue("Asserting true for leaf node", tbc.getSession()
	                .isLeafNode(DmtTestControl.SOURCE_LEAF));
	    } catch (DmtException e) {
	        tbc.fail("Unexpected Exception.");            
	    } finally {
	        tbc.cleanUp(new String[] { DmtTestControl.SOURCE_LEAF, DmtTestControl.SOURCE });	        
	    }
	}

	/**
	 * @testID testIsLeafNode002
	 * @testDescription This method tests wheather the OSGi_ROOT node is a leaf
	 *                  or an interior node.
	 */
	private void testIsLeafNode002() {
        try {
		tbc.assertTrue("Asserting true for leaf node", !tbc
				.getSession().isLeafNode(DmtTestControl.OSGi_ROOT));
        } catch (DmtException e) {
            tbc.fail("Unexpected Exception.");
        }
	}
    
    /**
     * @testID testIsLeafNode003
     * @testDescription This method asserts that an IllegalStateException is thrown when attemps to get a sessionId of a closed session.
     */
    private void testIsLeafNode003() {
        
        try {
            tbc.getSession().close();
            tbc.getSession().isLeafNode(DmtTestControl.OSGi_ROOT);
            tbc.failException("", IllegalStateException.class);
        } catch (IllegalStateException e) {
            tbc.pass("The Exception was IllegalStateException");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalStateException.class.getName() + " but was "+e.getClass().getName());
        }
        
    }
    
    /**
     * @testID testIsLeafNode004
     * @testDescription This method asserts that an IllegalStateException is thrown when attemps to get a sessionId after a rollback.
     */
    private void testIsLeafNode004() {
        
        try {
            tbc.getSession().rollback();
            tbc.getSession().isLeafNode(DmtTestControl.OSGi_ROOT);
            tbc.failException("", IllegalStateException.class);
        } catch (IllegalStateException e) {
            tbc.pass("The Exception was IllegalStateException");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalStateException.class.getName() + " but was "+e.getClass().getName());
        }
        
    }        
}

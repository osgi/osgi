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
 * Jan 25, 2005  Andre Assad
 * CR 1          Implement MEG TCK
 * ============  ==============================================================
 * Feb 14, 2005  Alexandre Santos
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
 * @methodUnderTest org.osgi.service.dmt.DmtSession#deleteNode
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>deleteNode<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class DeleteNode {
    //TODO TCs for PERMISSION_DENIED, METADATA_MISMATCH, DATA_STORE_FAILURE, TRANSACTION_ERROR 
    //TODO confirm TCs URI_TOO_LONG already implemented.
	private DmtTestControl tbc;

	public DeleteNode(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testDeleteNode001();
		testDeleteNode002();
		testDeleteNode003();
        testDeleteNode004();
        testDeleteNode005();
        testDeleteNode006();
        testDeleteNode007();
        testDeleteNode008();
        testDeleteNode009();
	}

	/**
	 * @testID testDeleteNode001
	 * @testDescription This method tests that a leaf node is correctly deleted
	 *                  from the subtree.
	 */
	private void testDeleteNode001() {
	    try {
	        // creating a valid interior level for creating a leaf node
	        tbc.getSession().createInteriorNode(DmtTestControl.DESTINY);
	        tbc.getSession().createLeafNode(DmtTestControl.DESTINY_LEAF, new DmtData(10));
	        tbc.getSession().deleteNode(DmtTestControl.DESTINY_LEAF);
	        boolean found = false;
	        String[] childs = tbc.getSession().getChildNodeNames(DmtTestControl.DESTINY);
	        for (int i = 0; i < childs.length && !found; i++) {
	            if (childs[i].equals("maxsize")) {
	                found = true;
	                tbc.fail("Leaf node was not deleted");
	            }
	        }
	        if (!found) {
	            tbc.pass("Leaf node correctly deleted");
	        }
	    } catch (DmtException e) {
	        tbc.fail("Unexpected exception");
	    } catch (Exception e) {
	    	tbc.fail("Unexpected exception: "+e.getClass().getName());
	    } finally {
	        tbc.cleanUp(new String[] { DmtTestControl.DESTINY } );
	    }
	}

	/**
	 * @testID testDeleteNode002
	 * @testDescription This method tests that an interior node is recursively
	 *                  deleted from the subtree.
	 */
	private void testDeleteNode002() {
		try {
			tbc.getSession().createInteriorNode(DmtTestControl.DESTINY);
			tbc.getSession().createLeafNode(DmtTestControl.DESTINY_LEAF, new DmtData(10));
			tbc.getSession().deleteNode(DmtTestControl.DESTINY);
			boolean found = false;
			String[] childs = tbc.getSession().getChildNodeNames(DmtTestControl.OSGi_LOG);
			for (int i = 0; i < childs.length && !found; i++) {
				if (childs[i].equals("interior")) {
					found = true;
					tbc.fail("Leaf node was not deleted");
				}
			}
			if (!found) {
				tbc.pass("Leaf node correctly deleted");
			}
		} catch (DmtException e) {
			tbc.fail("Unexpected exception");
		} catch (Exception e) {
			tbc.fail("Unexpected exception: "+e.getClass().getName());
		}
	}

	/**
	 * @testID testDeleteNode003
	 * @testDescription This method tests that a DmtException is thrown using the NODE_NOT_FOUND code whenever
	 *                  an invalid node is deleted.
	 */
	private void testDeleteNode003() {
		try {
			tbc.getSession().deleteNode(DmtTestControl.INVALID_NODE);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException's code is NODE_NOT_FOUND", DmtException.NODE_NOT_FOUND, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }  
	}
       
   
    /**
     * @testID testDeleteNode004
     * @testDescription This method asserts that an IllegalStateException is thrown when attemps to delete a node after a rollback.
     */
    private void testDeleteNode004() {
        
        try {
            tbc.getSession().rollback();
            tbc.getSession().deleteNode(DmtTestControl.OSGi_LOG);
            tbc.failException("", IllegalStateException.class);
        } catch (IllegalStateException e) {
            tbc.pass("The Exception was IllegalStateException");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalStateException.class.getName() + " but was "+e.getClass().getName());
        }
        
    }         
    
    /**
     * @testID testDeleteNode005
     * @testDescription This method asserts that a DmtException with error code equals to 
     *                  OTHER_ERROR is thrown.
     */
    private void testDeleteNode005() {
        
        try {
            tbc.getSession().createInteriorNode(DmtTestControl.SOURCE);
            tbc.getSession().createInteriorNode(DmtTestControl.DESTINY);
            DmtSession session = tbc.getSession(DmtTestControl.SOURCE);
            session.deleteNode(DmtTestControl.DESTINY);
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
     * @testID testDeleteNode006
     * @testDescription This method asserts that a DmtException with error code equals to 
     *                  INVALID_URI is thrown.
     */
    private void testDeleteNode006() {
        
        try {
            tbc.getSession().deleteNode(DmtTestControl.INVALID_URI);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is INVALID_URI", DmtException.INVALID_URI, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }        
    }     
    
    /**
     * @testID testDeleteNode007
     * @testDescription This method asserts that a DmtException with error code equals to 
     *                  URI_TOO_LONG is thrown.
     */
    private void testDeleteNode007() {
        
        try {
            tbc.getSession().deleteNode(DmtTestControl.URI_LONG);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is URI_TOO_LONG", DmtException.URI_TOO_LONG, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }        
    }
    
    /**
     * @testID testDeleteNode008
     * @testDescription This method asserts that an IllegalStateException is thrown when attemps to delete a node using a closed session.
     */
    private void testDeleteNode008() {
        
        try {
            tbc.getSession().close();
            tbc.getSession().deleteNode(DmtTestControl.SOURCE);
            tbc.failException("", IllegalStateException.class);
        } catch (IllegalStateException e) {
            tbc.pass("The Exception was IllegalStateException");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalStateException.class.getName() + " but was "+e.getClass().getName());
        }
        
    }        
    
    /**
     * @testID testDeleteNode009
     * @testDescription This method asserts that a DmtException with error code equals to 
     *                  COMMAND_NOT_ALLOWED is thrown.
     */
    private void testDeleteNode009() {
        
        try {
            tbc.getSession().deleteNode(DmtTestControl.OSGi_ROOT);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is COMMAND_NOT_ALLOWED", DmtException.COMMAND_NOT_ALLOWED, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }        
    }       
    
}

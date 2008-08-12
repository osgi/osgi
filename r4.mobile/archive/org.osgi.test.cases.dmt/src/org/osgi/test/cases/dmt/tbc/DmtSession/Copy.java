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
 * Jan 24, 2005  Andre Assad
 * CR 1          Implement MEG TCK
 * ============  ==============================================================
 * Feb 14, 2005  Alexandre Santos
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ===========  ===============================================================
 * Feb 17, 2005  Leonardo Barros
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ===========  ===============================================================
 */

package org.osgi.test.cases.dmt.tbc.DmtSession;

import org.osgi.service.dmt.DmtAcl;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtSession#copy
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>copy<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class Copy {
    //TODO TCs for PERMISSION_DENIED, METADATA_MISMATCH, DATA_STORE_FAILURE, FORMAT_NOT_SUPPORTED, TRANSACTION_ERROR
    
	private DmtTestControl tbc;

	public Copy(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testCopy001();
		testCopy002();
        testCopy003();
        testCopy004();
        testCopy005();
        testCopy006();
        testCopy007();
        testCopy008();
        testCopy009();
        testCopy010();
	}

	/**
	 * @testID testCopy001
	 * @testDescription This method test the copy execution on a recursive
	 *                  subtree copy.
	 */
	private void testCopy001() {
	    
	    try {
	        DmtAcl acl = new DmtAcl(DmtTestControl.ACLSTR);
	        DmtData data = new DmtData(10);
	        tbc.getSession().createInteriorNode(DmtTestControl.SOURCE);
            tbc.getSession().createLeafNode(DmtTestControl.SOURCE_LEAF, data);
	        tbc.getSession().setNodeAcl(DmtTestControl.SOURCE, acl);
	        // copying node
	        tbc.getSession().copy(DmtTestControl.SOURCE, DmtTestControl.DESTINY, true);
	        // asserting changes
	        tbc.assertEquals("Asserting acl String", acl.toString(), tbc.getSession()
	                .getNodeAcl(DmtTestControl.DESTINY).toString());
	        // asserting leaf node names on recursively subtree	        
	        boolean found = false;
	        String[] childs = tbc.getSession().getChildNodeNames(DmtTestControl.DESTINY);
	        tbc.assertTrue("Asserts children node",!(childs.length != 1 || !childs[0].equals("maxsize")));
	        
	    } catch (DmtException e) {
	        tbc.fail("Throws an unexpected DmtException.");
	        
	    } finally {
	        tbc.cleanUp(new String[] { DmtTestControl.SOURCE_LEAF, DmtTestControl.DESTINY_LEAF, DmtTestControl.SOURCE, DmtTestControl.DESTINY } );
	    }
	    
	}

	/**
	 * @testID testCopy002
	 * @testDescription This method test the copy execution on a non-recursive
	 *                  subtree copy.
	 */
	private void testCopy002() {
	    try {
	        DmtAcl acl = new DmtAcl(DmtTestControl.ACLSTR);
	        DmtData data = new DmtData(10);
	        
	        // creating source node
	        tbc.getSession().createInteriorNode(DmtTestControl.SOURCE);
	        // performing changes to current node tbc.getSession()
	        tbc.getSession().setNodeAcl(DmtTestControl.SOURCE, acl);
	        tbc.getSession().createLeafNode(DmtTestControl.SOURCE_LEAF, data);

            // copying node
	        tbc.getSession().copy(DmtTestControl.SOURCE, DmtTestControl.DESTINY, false);
	        // asserting changes
            tbc.assertEquals("Asserting acl String", acl.toString(), tbc.getSession()
                    .getNodeAcl(DmtTestControl.DESTINY).toString());
	        // asserting leaf node names on non-recursive subtree copy
	        boolean found = false;
	        for (int i = 0; i < tbc.getSession().getChildNodeNames(DmtTestControl.DESTINY).length && !found; i++) {
	            if (tbc.getSession().getChildNodeNames(DmtTestControl.DESTINY)[i].equals("maxsize")) {
	                found = true;
	                tbc.fail("Leaf child node found");
	            }
	        }
	        tbc.assertTrue("Leaf child node not found",!found);
        } catch (DmtException ex) {
            tbc.fail("Throws an unexpected DmtException.");           
            
        } finally {
            tbc.cleanUp(new String[] { DmtTestControl.SOURCE_LEAF, DmtTestControl.DESTINY_LEAF, DmtTestControl.SOURCE, DmtTestControl.DESTINY } );
        }
        
	}
    
    /**
     * @testID testCopy003
     * @testDescription This method test if the code in dmtexception is 
     *                  NODE_ALREADY_EXISTS.
     */
    private void testCopy003() {
       
        try {
            tbc.getSession().createInteriorNode(DmtTestControl.SOURCE);
            tbc.getSession().createInteriorNode(DmtTestControl.DESTINY);
            tbc.getSession().copy(DmtTestControl.SOURCE, DmtTestControl.DESTINY, true);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is NODE_ALREADY_EXISTS", DmtException.NODE_ALREADY_EXISTS, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        } finally {
            tbc.cleanUp(new String[] { DmtTestControl.SOURCE, DmtTestControl.DESTINY });
        }
        
    }
    
    /**
     * @testID testCopy004
     * @testDescription This method test if the code in dmtexception is 
     *                  NODE_NOT_FOUND.
     */
    private void testCopy004() {
       
        try {
            tbc.getSession().copy(DmtTestControl.INVALID_NODE, DmtTestControl.DESTINY, true);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is NODE_NOT_FOUND", DmtException.NODE_NOT_FOUND, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }        
    }
    
    /**
     * @testID testCopy005
     * @testDescription This method test if the code in dmtexception is 
     *                  OTHER_ERROR.
     */
    private void testCopy005() {
       
        try {
            DmtSession session = tbc.getSession(DmtTestControl.SOURCE);
            session.copy(DmtTestControl.DESTINY, DmtTestControl.SOURCE, true);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is OTHER_ERROR", DmtException.OTHER_ERROR, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }        
    }    
    
    /**
     * @testID testCopy006
     * @testDescription This method test if the code in dmtexception is 
     *                  COMMAND_NOT_ALLOWED.
     */
    private void testCopy006() {
       
        try {
            tbc.getSession().createInteriorNode(DmtTestControl.SOURCE);
            tbc.getSession().copy(DmtTestControl.SOURCE, DmtTestControl.SUBTREE, true);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is COMMAND_NOT_ALLOWED", DmtException.COMMAND_NOT_ALLOWED, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }        
    }        
    
    /**
     * @testID testCopy007
     * @testDescription This method test if the code in dmtexception is 
     *                  INVALID_URI.
     */
    private void testCopy007() {
       
        try {
            tbc.getSession().copy(DmtTestControl.INVALID_URI, DmtTestControl.DESTINY, true);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is INVALID_URI", DmtException.INVALID_URI, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }        
    }  
    
    /**
     * @testID testCopy008
     * @testDescription This method test if the code in dmtexception is 
     *                  URI_TOO_LONG.
     */
    private void testCopy008() {
       
        try {
            tbc.getSession().copy(DmtTestControl.URI_LONG, DmtTestControl.DESTINY, true);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is URI_TOO_LONG", DmtException.URI_TOO_LONG, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }        
    }
    
    /**
     * @testID testCopy009
     * @testDescription This method asserts that an IllegalStateException is thrown when attemps to copy an uri using a closed session.
     */
    private void testCopy009() {
        
        try {
            tbc.getSession().close();
            tbc.getSession().copy(DmtTestControl.SOURCE, DmtTestControl.DESTINY, false);
            tbc.failException("", IllegalStateException.class);
        } catch (IllegalStateException e) {
            tbc.pass("The Exception was IllegalStateException");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalStateException.class.getName() + " but was "+e.getClass().getName());
        }
        
    }    
    
    /**
     * @testID testCopy010
     * @testDescription This method asserts that an IllegalStateException is thrown when attemps to copy an uri after a rollback.
     */
    private void testCopy010() {
        
        try {
            tbc.getSession().rollback();
            tbc.getSession().copy(DmtTestControl.SOURCE, DmtTestControl.DESTINY, false);
            tbc.failException("", IllegalStateException.class);
        } catch (IllegalStateException e) {
            tbc.pass("The Exception was IllegalStateException");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalStateException.class.getName() + " but was "+e.getClass().getName());
        }
        
    }        
    
    
}

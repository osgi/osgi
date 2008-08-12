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
 * Jan 1, 2005   Andre Assad
 * CR 1          Implement MEG TCK
 * ============  ==============================================================
 * Feb 15, 2005  Alexandre Santos
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ===========  ===============================================================
 */
package org.osgi.test.cases.dmt.tbc.DmtSession;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtSession#createLeafNode
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>createLeafNode<code> method, according to MEG reference
 *                     documentation (rfc0085).
 */
public class CreateLeafNode {
    //TODO TCs for COMMAND_NOT_ALLOWED, PERMISSION_DENIED, DATA_STORE_FAILURE, TRANSACTION_ERROR for all CreateLeafNode
    //TODO TCs for METADATA_MISMATCH for CreateLeafNode with 2 and 3 arguments
    //TODO URI_TOO_LONG need to be updated.
    //TODO how to test the constructor with only one argument in success case.
    //TODO TCs for constructor with 3 parameters.
	private DmtTestControl tbc;

	public CreateLeafNode(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testCreateLeafNode001();
		testCreateLeafNode002();
		//testCreateLeafNode003();
        testCreateLeafNode004();
        testCreateLeafNode005();
        testCreateLeafNode006();
        testCreateLeafNode007();
        testCreateLeafNode008();
        testCreateLeafNode009();
        testCreateLeafNode010();
        testCreateLeafNode011();
        testCreateLeafNode012();
        testCreateLeafNode013();
        testCreateLeafNode014();
        testCreateLeafNode015();
        testCreateLeafNode016();
	}

	/**
	 * @testID testCreateLeafNode001
	 * @testDescription This method asserts that a new leaf node is created and
	 *                  its DmtData is correctly set using the constructor with two parameters.
	 */
	private void testCreateLeafNode001() {
	    try {
	        DmtData data = new DmtData(10);
	        // setting a valid level for creating a leaf node
	        tbc.getSession().createInteriorNode(DmtTestControl.DESTINY);
	        tbc.getSession().createLeafNode(DmtTestControl.DESTINY_LEAF, data);
	        tbc.assertEquals("Asserting node value", data, tbc.getSession().getNodeValue(DmtTestControl.DESTINY_LEAF));
	    } catch (DmtException e) {
	        tbc.fail("Unexpected DmtException.");
	    } catch (Exception e) {
	    	tbc.fail("Unexpected exception: "+e.getClass().getName());
	    } finally {
	        tbc.cleanUp(new String[] { DmtTestControl.DESTINY_LEAF, DmtTestControl.DESTINY });
	    }   
	    
	}

	/**
	 * @testID testCreateLeafNode002
	 * @testDescription This method asserts that a new leaf node is created with
	 *                  a DmtData null.
	 */
	private void testCreateLeafNode002() {
	    try {
	        // setting a valid level for creating a leaf node
	        tbc.getSession().createInteriorNode(DmtTestControl.DESTINY);
	        tbc.getSession().createLeafNode(DmtTestControl.DESTINY_LEAF, DmtData.NULL_VALUE);
	        tbc.assertEquals("Asserting null node value", DmtData.NULL_VALUE, tbc
	                .getSession().getNodeValue(DmtTestControl.DESTINY_LEAF));
	    } catch (DmtException e) {
	        tbc.fail("#Exception should not happen");
	    } catch (Exception e) {
	    	tbc.fail("Unexpected exception: "+e.getClass().getName());
	    } finally {
	        tbc.cleanUp(new String[] { DmtTestControl.DESTINY_LEAF, DmtTestControl.DESTINY });
	    }
	}

	/**
	 * @testID testCreateInteriorNode003
	 * @testDescription This method asserts that a DmtException with error code
	 *                  equals to METADATA_MISMATCH, is thrown whenever a new
	 *                  leaf node without a default DmtData value is created 
     *                  using the constructor with one argument.
	 */
	private void testCreateLeafNode003() {
	    try {
	        // setting a valid level for creating a leaf node
	        tbc.getSession().createInteriorNode(DmtTestControl.DESTINY);
	        tbc.getSession().createLeafNode(DmtTestControl.DESTINY_LEAF);
            tbc.failException("", DmtException.class);
	    } catch (DmtException e) {
	        tbc.assertEquals("Asserting DmtException code",
	                DmtException.METADATA_MISMATCH, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());           
	    } finally {
	        tbc.cleanUp(new String[] { DmtTestControl.DESTINY_LEAF, DmtTestControl.DESTINY });
	    }
	}
    
    /**
     * @testID testCreateLeafNode004
     * @testDescription This method asserts that a DmtException with error code equals to 
     *                  NODE_ALREADY_EXISTS is thrown using the constructor with two parameters.
     */
    private void testCreateLeafNode004() {
       
        try {
            tbc.getSession().createInteriorNode(DmtTestControl.SOURCE);
            tbc.getSession().createLeafNode(DmtTestControl.SOURCE_LEAF, new DmtData(10));
            tbc.getSession().createLeafNode(DmtTestControl.SOURCE_LEAF, new DmtData(10));
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is NODE_ALREADY_EXISTS", DmtException.NODE_ALREADY_EXISTS, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        } finally {
            tbc.cleanUp(new String[] { DmtTestControl.SOURCE_LEAF, DmtTestControl.SOURCE });
        }
        
    }
    
    /**
     * @testID testCreateLeafNode005
     * @testDescription This method asserts that a DmtException with error code equals to 
     *                  OTHER_ERROR is thrown using the constructor with two parameters.
     */
    private void testCreateLeafNode005() {
        
        try {
            tbc.getSession().createInteriorNode(DmtTestControl.SOURCE);
            tbc.getSession().createInteriorNode(DmtTestControl.DESTINY);
            DmtSession session = tbc.getSession(DmtTestControl.SOURCE);
            session.createLeafNode(DmtTestControl.DESTINY, new DmtData(10));
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
     * @testID testCreateLeafNode006
     * @testDescription This method asserts that a DmtException with error code equals to 
     *                  INVALID_URI is thrown using the constructor with two parameters.
     */
    private void testCreateLeafNode006() {
        
        try {
            tbc.getSession().createLeafNode(DmtTestControl.INVALID_URI, new DmtData(10));
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is INVALID_URI", DmtException.INVALID_URI, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }        
    }     
    
    /**
     * @testID testCreateLeafNode007
     * @testDescription This method asserts that a DmtException with error code equals to 
     *                  URI_TOO_LONG is thrown using the constructor with two parameters.
     */
    private void testCreateLeafNode007() {
        
        try {
            tbc.getSession().createLeafNode(DmtTestControl.URI_LONG, new DmtData(10));
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is URI_TOO_LONG", DmtException.URI_TOO_LONG, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }        
    }
    
    /**
     * @testID testCreateLeafNode008
     * @testDescription This method asserts that an IllegalStateException is thrown when attemps to create a leaf node using a closed session
     *  using the constructor with two parameters.
     */
    private void testCreateLeafNode008() {
        
        try {
            tbc.getSession().close();
            tbc.getSession().createLeafNode(DmtTestControl.SOURCE, new DmtData(10));
            tbc.failException("", IllegalStateException.class);
        } catch (IllegalStateException e) {
            tbc.pass("The Exception was IllegalStateException");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalStateException.class.getName() + " but was "+e.getClass().getName());
        }
        
    }  
    
    /**
     * @testID testCreateLeafNode009
     * @testDescription This method asserts that an IllegalStateException is thrown when attemps to create a leaf node after a rollback
     *  using the constructor with two parameters.
     */
    private void testCreateLeafNode009() {
        
        try {
            tbc.getSession().rollback();
            tbc.getSession().createLeafNode(DmtTestControl.SOURCE, new DmtData(10));
            tbc.failException("", IllegalStateException.class);
        } catch (IllegalStateException e) {
            tbc.pass("The Exception was IllegalStateException");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalStateException.class.getName() + " but was "+e.getClass().getName());
        }
        
    }    
    
    /**
     * @testID testCreateLeafNode010
     * @testDescription This method asserts that a DmtException with error code equals to 
     *                  NODE_ALREADY_EXISTS is thrown using the constructor with only one argument.
     */
    private void testCreateLeafNode010() {
       
        try {
            tbc.getSession().createInteriorNode(DmtTestControl.SOURCE);
            tbc.getSession().createLeafNode(DmtTestControl.SOURCE_LEAF, new DmtData(10));
            tbc.getSession().createLeafNode(DmtTestControl.SOURCE_LEAF);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is NODE_ALREADY_EXISTS", DmtException.NODE_ALREADY_EXISTS, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        } finally {
            tbc.cleanUp(new String[] { DmtTestControl.SOURCE_LEAF, DmtTestControl.SOURCE });
        }
        
    }  
    
    /**
     * @testID testCreateLeafNode0011
     * @testDescription This method asserts that a DmtException with error code equals to 
     *                  OTHER_ERROR is thrown using the constructor with only one argument.
     */
    private void testCreateLeafNode011() {
        
        try {
            DmtSession session = tbc.getSession(DmtTestControl.SOURCE);
            session.createLeafNode(DmtTestControl.DESTINY_LEAF, new DmtData(10));
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is OTHER_ERROR", DmtException.OTHER_ERROR, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }        
    }    
    
    /**
     * @testID testCreateLeafNode012
     * @testDescription This method asserts that a DmtException with error code equals to 
     *                  INVALID_URI is thrown using the constructor with only one argument.
     */
    private void testCreateLeafNode012() {
        
        try {
            tbc.getSession().createLeafNode(DmtTestControl.INVALID_URI);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is INVALID_URI", DmtException.INVALID_URI, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }        
    }     
    
    /**
     * @testID testCreateLeafNode013
     * @testDescription This method asserts that a DmtException with error code equals to 
     *                  URI_TOO_LONG is thrown using the constructor with only one argument.
     */
    private void testCreateLeafNode013() {
        
        try {
            tbc.getSession().createLeafNode(DmtTestControl.URI_LONG);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is URI_TOO_LONG", DmtException.URI_TOO_LONG, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }        
    }
    
    /**
     * @testID testCreateLeafNode014
     * @testDescription This method asserts that an IllegalStateException is thrown when attemps to create a leaf node using a closed session
     *  using the constructor with only one argument.
     */
    private void testCreateLeafNode014() {
        
        try {
            tbc.getSession().close();
            tbc.getSession().createLeafNode(DmtTestControl.SOURCE);
            tbc.failException("", IllegalStateException.class);
        } catch (IllegalStateException e) {
            tbc.pass("The Exception was IllegalStateException");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalStateException.class.getName() + " but was "+e.getClass().getName());
        }
        
    }      
    
    /**
     * @testID testCreateLeafNode015
     * @testDescription This method asserts that a DmtException with error code equals to 
     *                  COMMAND_FAILED is thrown using the constructor with two parameters.
     */
    private void testCreateLeafNode015() {
        
        try {
            tbc.getSession().createInteriorNode(DmtTestControl.SOURCE);
            tbc.getSession().createLeafNode(DmtTestControl.SOURCE_LEAF, null);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is COMMAND_FAILED", DmtException.COMMAND_FAILED, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        } finally {
            tbc.cleanUp(new String[] { DmtTestControl.SOURCE });
        }
    }
    
    /**
     * @testID testCreateLeafNode016
     * @testDescription This method asserts that an IllegalStateException is thrown when attemps to create a leaf node after a rollback
     *  using the constructor with only one argument.
     */
    private void testCreateLeafNode016() {
        
        try {
            tbc.getSession().rollback();
            tbc.getSession().createLeafNode(DmtTestControl.SOURCE);
            tbc.failException("", IllegalStateException.class);
        } catch (IllegalStateException e) {
            tbc.pass("The Exception was IllegalStateException");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalStateException.class.getName() + " but was "+e.getClass().getName());
        }
        
    }         
    
    
}

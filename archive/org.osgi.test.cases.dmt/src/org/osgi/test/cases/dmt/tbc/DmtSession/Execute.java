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
 * Jan 31, 2005  Andre Assad
 * CR 1          Implement MEG TCK
 * ============  ==============================================================
 * Feb 14, 2005  Alexandre Santos
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ===========  ==============================================================
 */

package org.osgi.test.cases.dmt.tbc.DmtSession;

import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtSession#execute
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>execute<code> method, according to MEG reference
 *                     documentation.
 */
public class Execute {
    // Actually it has only tcs for exception types.
	// TODO what result is expected on the execute method (testExecute001).
    // TODO TCs for COMMAND_FAILED, DATA_STORE_FAILURE
	private DmtTestControl tbc;

	/**
	 * @param tbc
	 */
	public Execute(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		//testExecute001();
		testExecute002();
        testExecute003();
        testExecute004();
        testExecute005();
        testExecute006();
        testExecute007();
        testExecute008();
	}

	/**
	 * @testID testExecute001
	 * @testDescription This method asserts the execution of the execute()
	 *                  method of DmtSession.
	 */
	private void testExecute001()  {
		String nodeURI = "./OSGi";
		
		String data = "<Exec>\n<CmdID>3</CmdID>\n<Item>\n<Target>\n<LocURI>./bin/shutdown</LocURI>\n</Target>\n<Data></Data>\n</Item>\n</Exec>";

	}

	/**
	 * @testID testExecute002
	 * @testDescription This method asserts that execute correctly throws
	 *                  DmtException when a unexistent node is executed
	 */
	private void testExecute002() {
	    try {
	        tbc.getSession().execute(DmtTestControl.INVALID_NODE, null);
	        tbc.failException("", DmtException.class);
	    } catch (DmtException e) {
	        tbc.assertEquals("Asserting that DmtException's code is NODE_NOT_FOUND", DmtException.NODE_NOT_FOUND, e.getCode());
	    } catch (Exception e) {
	        tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
	    }
	}
    
    /**
     * @testID testExecute003
     * @testDescription This method asserts that a DmtException with error code equals to 
     *                  URI_TOO_LONG is thrown.
     */
    private void testExecute003() {
        try {
            tbc.getSession().execute(DmtTestControl.URI_LONG, null);
        	tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is URI_TOO_LONG", DmtException.URI_TOO_LONG, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }
    }    
    
    /**
     * @testID testExecute004
     * @testDescription This method asserts that a DmtException with error code equals to 
     *                  INVALID_URI is thrown.
     */
    private void testExecute004() {
        
        try {
            tbc.getSession().execute(DmtTestControl.INVALID_URI, null);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is INVALID_URI", DmtException.INVALID_URI, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }        
    }         
    
    /**
     * @testID testExecute005
     * @testDescription This method asserts that a DmtException with error code equals to 
     *                  OTHER_ERROR is thrown.
     */
    private void testExecute005() {
        
        try {
            tbc.getSession().createInteriorNode(DmtTestControl.SOURCE);
            tbc.getSession().createInteriorNode(DmtTestControl.DESTINY);
            DmtSession session = tbc.getSession(DmtTestControl.SOURCE);
            session.execute(DmtTestControl.DESTINY, null);
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
     * @testID testExecute006
     * @testDescription This method asserts that a DmtException with error code equals to 
     *                  COMMAND_NOT_ALLOWED is thrown.
     */
    private void testExecute006() {
        
        try {
            tbc.getSession().execute(DmtTestControl.OSGi_ROOT, null);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code is COMMAND_NOT_ALLOWED", DmtException.COMMAND_NOT_ALLOWED, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }        
    }      
    
    /**
     * @testID testExecute007
     * @testDescription This method asserts that an IllegalStateException is thrown when attemps to run the execute using a closed session.
     */
    private void testExecute007() {
        
        try {
            tbc.getSession().close();
            tbc.getSession().execute(DmtTestControl.SOURCE, null);
            tbc.failException("", IllegalStateException.class);
        } catch (IllegalStateException e) {
            tbc.pass("The Exception was IllegalStateException");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalStateException.class.getName() + " but was "+e.getClass().getName());
        }
        
    }       
    
    /**
     * @testID testExecute008
     * @testDescription This method asserts that an IllegalStateException is thrown when attemps to run the execute after a rollback.
     */
    private void testExecute008() {
        
        try {
            tbc.getSession().rollback();
            tbc.getSession().execute(DmtTestControl.SOURCE, null);
            tbc.failException("", IllegalStateException.class);
        } catch (IllegalStateException e) {
            tbc.pass("The Exception was IllegalStateException");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalStateException.class.getName() + " but was "+e.getClass().getName());
        }
        
    }           
    
}

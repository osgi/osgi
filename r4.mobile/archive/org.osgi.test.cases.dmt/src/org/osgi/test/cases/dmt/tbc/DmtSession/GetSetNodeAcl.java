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
 * Feb 14, 2005  Alexandre Santos
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ===========  ==============================================================
 */

package org.osgi.test.cases.dmt.tbc.DmtSession;

import org.osgi.service.dmt.DmtAcl;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.test.cases.dmt.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * @methodUnderTest org.osgi.service.dmt.DmtSession#getNodeAcl,setNodeAcl
 * @generalDescription This Test Case Validates the implementation of
 *                     <code>getNodeAcl, and setNodeAcl<code> methods, according to MEG reference
 *                     documentation.
 */
public class GetSetNodeAcl {
    //TODO TCs for PERMISSION_DENIED, COMMAND_NOT_ALLOWED, DATA_STORE_FAILURE 
	private DmtTestControl tbc;

	public GetSetNodeAcl(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetSetNodeAcl001();
		testGetSetNodeAcl002();
		testGetSetNodeAcl003();
        testGetSetNodeAcl004();
        testGetSetNodeAcl005();
        testGetSetNodeAcl006();
        testGetSetNodeAcl007();
        testGetSetNodeAcl008();
        testGetSetNodeAcl009();
        testGetSetNodeAcl010();
        testGetSetNodeAcl011();
        testGetSetNodeAcl012();
        testGetSetNodeAcl013();
        testGetSetNodeAcl014();
	}

	/**
	 * @testID testGetSetNodeAcl001
	 * @testDescription This method asserts that the DmtAcl is correctly set for
	 *                  a given session node on the tree.
	 */
	private void testGetSetNodeAcl001() {
	    try {
	        
	        DmtAcl acl = new DmtAcl(DmtTestControl.ACLSTR);
	        
	        tbc.getSession().setNodeAcl(DmtTestControl.OSGi_ROOT, acl);
	        
	        tbc.assertEquals("Asserting node Acl", acl.toString(), tbc.getSession()
	                .getNodeAcl(DmtTestControl.OSGi_ROOT).toString());
	        
	    } catch (DmtException e) {
	        tbc.assertException("Not expected.", DmtException.class, e);
	    } catch (Exception e) {
	    	tbc.fail("Unexpected exception: "+e.getClass().getName());
	    }
	    
	}

	/**
	 * @testID testGetSetNodeAcl002
	 * @testDescription This method asserts that a DmtException is thrown for an
	 *                  invalid node of the tree.
	 */
	private void testGetSetNodeAcl002()  {

		DmtAcl acl = new DmtAcl(DmtTestControl.ACLSTR);
		try {
			tbc.getSession().setNodeAcl(DmtTestControl.INVALID_NODE, acl);
			tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code was NODE_NOT_FOUND.", DmtException.NODE_NOT_FOUND, e.getCode());
		} catch (Exception e) {
            tbc.assertException("Not expected.", DmtException.class, e);
		}
	}
    
    /**
     * @testID testGetSetNodeAcl003
     * @testDescription This method asserts that a DmtException is thrown for an
     *                  uri too long of the tree.
     */
    private void testGetSetNodeAcl003()  {

        DmtAcl acl = new DmtAcl(DmtTestControl.ACLSTR);
        try {
            tbc.getSession().setNodeAcl(DmtTestControl.URI_LONG, acl);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code was URI_TOO_LONG.", DmtException.URI_TOO_LONG, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }
    }    
    
    /**
     * @testID testGetSetNodeAcl004
     * @testDescription This method asserts that a DmtException is thrown for an
     *                  invalid uri of the tree.
     */
    private void testGetSetNodeAcl004()  {

        DmtAcl acl = new DmtAcl(DmtTestControl.ACLSTR);
        try {
            tbc.getSession().setNodeAcl(DmtTestControl.INVALID_URI, acl);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code was INVALID_URI.", DmtException.INVALID_URI, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }
    }        
    
    /**
     * @testID testGetSetNodeAcl005
     * @testDescription This method asserts that a DmtException is thrown for an
     *                  other error of the tree.
     */
    private void testGetSetNodeAcl005()  {

        DmtAcl acl = new DmtAcl(DmtTestControl.ACLSTR);
        try {
            DmtSession session = tbc.getSession(DmtTestControl.OSGi_LOG);
            session.setNodeAcl(DmtTestControl.OSGi_ROOT, acl);           
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code was OTHER_ERROR.", DmtException.OTHER_ERROR, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }
    }     
    
    /**
     * @testID testGetSetNodeAcl006
     * @testDescription This method asserts that java.lang.IllegalStateException is thrown when
     *                  it closes the session and then try to set an acl 
     */
    private void testGetSetNodeAcl006()  {

        DmtAcl acl = new DmtAcl(DmtTestControl.ACLSTR);
        try {
            tbc.getSession().close();
            tbc.getSession().setNodeAcl(DmtTestControl.OSGi_LOG, acl);
            tbc.failException("", IllegalStateException.class);
        } catch (java.lang.IllegalStateException e) {
            tbc.pass("The Exception was IllegalStateException");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalStateException.class.getName() + " but was "+e.getClass().getName());
        }
    }
    
    /**
     * @testID testGetSetNodeAcl007
     * @testDescription This method asserts that java.lang.IllegalStateException is thrown when
     *                  it try to set an acl after a rollback. 
     */
    private void testGetSetNodeAcl007()  {

        DmtAcl acl = new DmtAcl(DmtTestControl.ACLSTR);
        try {
            tbc.getSession().rollback();
            tbc.getSession().setNodeAcl(DmtTestControl.OSGi_LOG, acl);
            tbc.failException("", IllegalStateException.class);
        } catch (java.lang.IllegalStateException e) {
            tbc.pass("The Exception was IllegalStateException");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalStateException.class.getName() + " but was "+e.getClass().getName());
        }
    }    

	/**
	 * @testID testGetSetNodeAcl008
	 * @testDescription This method asserts that a DmtException is thrown when
	 *                  the session attempts to get an ACL of an invalid node
	 *                  for an invalid node of the tree.
	 */
	private void testGetSetNodeAcl008() {

        try {
			tbc.getSession().getNodeAcl(DmtTestControl.INVALID_NODE);
			tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code was NODE_NOT_FOUND.", DmtException.NODE_NOT_FOUND, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }
	}
    
    /**
     * @testID testGetSetNodeAcl009
     * @testDescription This method asserts that a DmtException is thrown for an
     *                  invalid node of the tree.
     */
    private void testGetSetNodeAcl009()  {

        DmtAcl acl = new DmtAcl(DmtTestControl.ACLSTR);
        try {
            tbc.getSession().getNodeAcl(DmtTestControl.INVALID_NODE);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code was NODE_NOT_FOUND.", DmtException.NODE_NOT_FOUND, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }
    }
    
    /**
     * @testID testGetSetNodeAcl010
     * @testDescription This method asserts that a DmtException is thrown for an
     *                  uri too long of the tree.
     */
    private void testGetSetNodeAcl010()  {

        DmtAcl acl = new DmtAcl(DmtTestControl.ACLSTR);
        try {
            tbc.getSession().getNodeAcl(DmtTestControl.URI_LONG);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code was URI_TOO_LONG.", DmtException.URI_TOO_LONG, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }
    }    
    
    /**
     * @testID testGetSetNodeAcl011
     * @testDescription This method asserts that a DmtException is thrown for an
     *                  invalid uri of the tree.
     */
    private void testGetSetNodeAcl011()  {

        DmtAcl acl = new DmtAcl(DmtTestControl.ACLSTR);
        try {
            tbc.getSession().getNodeAcl(DmtTestControl.INVALID_URI);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code was INVALID_URI.", DmtException.INVALID_URI, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }
    }        
    
    /**
     * @testID testGetSetNodeAcl012
     * @testDescription This method asserts that a DmtException is thrown for an
     *                  other error of the tree.
     */
    private void testGetSetNodeAcl012()  {

        DmtAcl acl = new DmtAcl(DmtTestControl.ACLSTR);
        try {            
            DmtSession session = tbc.getSession(DmtTestControl.OSGi_LOG);
            session.getNodeAcl(DmtTestControl.OSGi_ROOT);
            tbc.failException("", DmtException.class);
        } catch (DmtException e) {
            tbc.assertEquals("Asserting that DmtException code was OTHER_ERROR.", DmtException.OTHER_ERROR, e.getCode());
        } catch (Exception e) {
            tbc.fail("Expected " + DmtException.class.getName() + " but was "+e.getClass().getName());
        }
    }     
    
    /**
     * @testID testGetSetNodeAcl013
     * @testDescription This method asserts that java.lang.IllegalStateException is thrown when
     *                  it closes the session and then try to get an acl 
     */
    private void testGetSetNodeAcl013()  {

        DmtAcl acl = new DmtAcl(DmtTestControl.ACLSTR);
        try {     
            tbc.getSession().close();
            tbc.getSession().getNodeAcl(DmtTestControl.OSGi_LOG);
            tbc.failException("", IllegalStateException.class);
        } catch (java.lang.IllegalStateException e) {
            tbc.pass("The Exception was IllegalStateException");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalStateException.class.getName() + " but was "+e.getClass().getName());
        } 
    }
    
    /**
     * @testID testGetSetNodeAcl014
     * @testDescription This method asserts that java.lang.IllegalStateException is thrown when
     *                  it try to set an acl after a rollback. 
     */
    private void testGetSetNodeAcl014()  {

        DmtAcl acl = new DmtAcl(DmtTestControl.ACLSTR);
        try {
            tbc.getSession().rollback();
            tbc.getSession().getNodeAcl(DmtTestControl.OSGi_LOG);
            tbc.failException("", IllegalStateException.class);
        } catch (java.lang.IllegalStateException e) {
            tbc.pass("The Exception was IllegalStateException");
        } catch (Exception e) {
            tbc.fail("Expected " + IllegalStateException.class.getName() + " but was "+e.getClass().getName());
        }
    }        
}

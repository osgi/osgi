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
 * Jan 26, 2005  Leonardo Barros
 * 1             Implement TCK
 * ============  ==============================================================
 * Feb 14, 2005  Luiz Felipe Guimaraes
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.main.tbc.DmtException;

import java.util.Iterator;
import java.util.Vector;
import org.osgi.test.cases.dmt.main.tbc.DmtConstants;
import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.main.tbc.Plugin.ExecPlugin.TestExecPluginActivator;

/**
 * This class tests DmtException constructors according to MEG specification 
 */

public class DmtException {
	private DmtTestControl tbc;
	private static final String EXCEPTION_MSG = "exception_msg";
	public DmtException(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testDmtException001();
		testDmtException002();
		testDmtException003();
		testDmtException004();
		testDmtException005();
		testDmtException006();
		testDmtException007();
		testDmtException008();
		testDmtException009();
		testDmtException010();
        testDmtException011();
        testDmtException012();
	}

	/**
	 * Tests if the values that are passed as parameters for the constructor are equal 
	 * to the values returned through its corresponding get methods using the constructor
     * DmtException(String,int,String)
	 * 
	 * @spec DmtException.DmtException(String,int,String)
	 * 
	 */
	private void testDmtException001() {
		tbc.log("#testDmtException001");
		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException(
				DmtConstants.OSGi_LOG,
				org.osgi.service.dmt.DmtException.INVALID_URI,
				DmtConstants.MESSAGE);

		tbc.assertEquals("Asserts getURI() method", DmtConstants.OSGi_LOG, de.getURI());
		tbc.assertEquals("Asserts getMessage() method","INVALID_URI: '"+DmtConstants.OSGi_LOG+"': "+DmtConstants.MESSAGE,de.getMessage());
		
		tbc.assertEquals("Asserts getCode() method",org.osgi.service.dmt.DmtException.INVALID_URI, de.getCode());
		
		tbc.assertEquals("Asserts getCauses() method", 0, de.getCauses().size());
		tbc.assertNull("Asserts getCause() method", de.getCause());
		tbc.assertTrue("Asserts isFatal() method", !de.isFatal());
	}
	
	
	/**
	 * Tests if the values that are passed as parameters for the constructor are equal 
	 * to the values returned through its corresponding get methods using the constructor
     * DmtException(String,int,String) and 'null' values whenever it is possible
	 * 
	 * @spec DmtException.DmtException(String,int,String)
	 * 
	 */
	private void testDmtException002() {
		tbc.log("#testDmtException002");
		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException((String)null, org.osgi.service.dmt.DmtException.CONCURRENT_ACCESS, null);
		tbc.assertNull("Asserts getURI() method", de.getURI());
        
		tbc.assertEquals("Asserts getCode() method", org.osgi.service.dmt.DmtException.CONCURRENT_ACCESS, de.getCode());
        tbc.assertEquals("Asserts getMessage() method","CONCURRENT_ACCESS",de.getMessage());
		tbc.assertNull("Asserts getCause() method", de.getCause());		
		tbc.assertTrue("Asserts that getCauses() returns an empty Vector if there is no cause", 
				de.getCauses().size()==0);
		tbc.assertTrue("Asserts isFatal() method", !de.isFatal());
		
	}
	
	/**
	 * Tests if the values that are passed as parameters for the constructor are equal 
	 * to the values returned through its corresponding get methods using the constructor
     * DmtException(String[],int,String)
	 * 
	 * @spec DmtException.DmtException(String[],int,String)
	 * 
	 */
	private void testDmtException003() {
		tbc.log("#testDmtException003");
		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException(
				new String[] {".","a","b","c" },
				org.osgi.service.dmt.DmtException.COMMAND_NOT_ALLOWED,
				DmtConstants.MESSAGE);

		String uri = "./a/b/c";

		tbc.assertEquals("Asserts getURI() method", uri, de.getURI());

        tbc.assertEquals("Asserts getMessage() method","COMMAND_NOT_ALLOWED: '"+uri+"': "+DmtConstants.MESSAGE,de.getMessage());

		tbc.assertEquals("Asserts getCode() method",
				org.osgi.service.dmt.DmtException.COMMAND_NOT_ALLOWED, de.getCode());
		
		tbc.assertEquals("Asserts getCauses() method", 0, de.getCauses().size());
		tbc.assertNull("Asserts getCause() method", de.getCause());
		tbc.assertTrue("Asserts isFatal() method", !de.isFatal());

	}
	
	
	/**
	 * Tests if the values that are passed as parameters for the constructor are equal 
	 * to the values returned through its corresponding get methods using the constructor
     * DmtException(String[],int,String) and 'null' values whenever it is possible
	 * 
	 * @spec DmtException.DmtException(String[],int,String)
	 * 
	 */
	private void testDmtException004() {
		tbc.log("#testDmtException004");
		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException((String[])null, org.osgi.service.dmt.DmtException.ALERT_NOT_ROUTED, null);
		
		tbc.assertNull("Asserts getURI() method", de.getURI());
		
		tbc.assertEquals("Asserts getCode() method", org.osgi.service.dmt.DmtException.ALERT_NOT_ROUTED, de.getCode());
        tbc.assertEquals("Asserts getMessage() method","ALERT_NOT_ROUTED",de.getMessage());
		tbc.assertNull("Asserts getCause() method", de.getCause());		
		tbc.assertTrue("Asserts that getCauses() returns an empty Vector if there is no cause", 
				de.getCauses().size()==0);
		tbc.assertTrue("Asserts isFatal() method", !de.isFatal());
		
	}

	/**
	 * Tests if the values that are passed as parameters for the constructor are equal 
	 * to the values returned through its corresponding get methods using the constructor
	 * DmtException(String,int,String,Throwable)
     * 
	 * @spec DmtException.DmtException(String,int,String,Throwable)
	 * 
	 */
	private void testDmtException005() {
		tbc.log("#testDmtException005");
		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException(
				DmtConstants.OSGi_LOG,
				org.osgi.service.dmt.DmtException.CONCURRENT_ACCESS,
				DmtConstants.MESSAGE, new Exception());

		tbc.assertEquals("Asserts getURI() method", DmtConstants.OSGi_LOG, de
				.getURI());
		

        tbc.assertEquals("Asserts getMessage() method","CONCURRENT_ACCESS: '"+DmtConstants.OSGi_LOG+"': "+DmtConstants.MESSAGE,de.getMessage());
		tbc.assertEquals("Asserts getCode() method",
				org.osgi.service.dmt.DmtException.CONCURRENT_ACCESS, de.getCode());
		
		tbc.assertException("Asserts getCause() method", Exception.class, de.getCause());
		
		tbc.assertEquals("Asserts getCauses() method", 1, de.getCauses().size());
		
		tbc.assertException("Asserts getCauses() method", Exception.class,
				(Exception) de.getCauses().get(0));
		tbc.assertTrue("Asserts isFatal() method", !de.isFatal());
	}
	/**
	 * Tests if the values that are passed as parameters for the constructor are equal 
	 * to the values returned through its corresponding get methods using the constructor
     * DmtException(String,int,String,Throwable)  and 'null' values whenever it is possible
	 * 
	 * @spec DmtException.DmtException(String,int,String,Throwable)
	 * 
	 */
	private void testDmtException006() {
		tbc.log("#testDmtException006");
		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException((String)null, org.osgi.service.dmt.DmtException.COMMAND_FAILED, null,
				(Throwable)null);
		tbc.assertNull("Asserts getURI() method", de.getURI());
        tbc.assertEquals("Asserts getMessage() method","COMMAND_FAILED",de.getMessage());
		
		tbc.assertEquals("Asserts getCode() method",  org.osgi.service.dmt.DmtException.COMMAND_FAILED, de.getCode());
		
		tbc.assertNull("Asserts getCause() method",de.getCause());
		tbc.assertTrue("Asserts that getCauses() returns an empty Vector if there is no cause", 
				de.getCauses().size()==0);
		tbc.assertTrue("Asserts isFatal() method", !de.isFatal());

	}	
	/**
	 * Tests if the values that are passed as parameters for the constructor are equal 
	 * to the values returned through its corresponding get methods using the constructor
     * DmtException(String[],int,String,Throwable)
	 * 
	 * @spec DmtException.DmtException(String[],int,String,Throwable)
	 * 
	 */
	private void testDmtException007() {
		tbc.log("#testDmtException007");
		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException(
				new String[] {".","d", "e", "f"},
				org.osgi.service.dmt.DmtException.REMOTE_ERROR,
				DmtConstants.MESSAGE, new IllegalArgumentException());
		String uri = "./d/e/f";

		tbc.assertEquals("Asserts getURI() method", uri, de.getURI());

        tbc.assertEquals("Asserts getMessage() method","REMOTE_ERROR: '"+uri+"': "+DmtConstants.MESSAGE,de.getMessage());
        
		tbc.assertEquals("Asserts getCode() method",
				org.osgi.service.dmt.DmtException.REMOTE_ERROR, de.getCode());
		
		tbc.assertException("Asserts getCause() method", IllegalArgumentException.class, de.getCause());
		
		tbc.assertEquals("Asserts getCauses() method", 1, de.getCauses().size());
		
		tbc.assertException("Asserts getCauses() method", IllegalArgumentException.class,
				(Exception) de.getCauses().get(0));
		tbc.assertTrue("Asserts isFatal() method", !de.isFatal());
	}
	/**
	 * Tests if the values that are passed as parameters for the constructor are equal 
	 * to the values returned through its corresponding get methods using the constructor
	 * DmtException(String[],int,String,Throwable) and 'null' values whenever it is possible
     * 
	 * @spec DmtException.DmtException(String[],int,String,Throwable)
	 * 
	 */
	private void testDmtException008() {
		tbc.log("#testDmtException008");
		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException((String[])null, org.osgi.service.dmt.DmtException.URI_TOO_LONG, null,
				(Throwable)null);
		tbc.assertNull("Asserts getURI() method", de.getURI());
        tbc.assertEquals("Asserts getMessage() method","URI_TOO_LONG",de.getMessage());
		tbc.assertEquals("Asserts getCode() method", org.osgi.service.dmt.DmtException.URI_TOO_LONG, de.getCode());
		tbc.assertNull("Asserts getCause() method",de.getCause());
		tbc.assertTrue("Asserts that getCauses() returns an empty Vector if there is no cause", 
				de.getCauses().size()==0);
		tbc.assertTrue("Asserts isFatal() method", !de.isFatal());

	}
	/**
	 * Tests if the values that are passed as parameters for the constructor are equal 
	 * to the values returned through its corresponding get methods using the constructor
     * DmtException(String,int,String,Vector,boolean)
	 * 
	 * @spec DmtException.DmtException(String,int,String,Vector,boolean)
	 * 
	 */
	private void testDmtException009() {
		tbc.log("#testDmtException009");
		Vector causes = new Vector();
		causes.add(0, new IllegalStateException(EXCEPTION_MSG));
		causes.add(1, new Exception(EXCEPTION_MSG));
		causes.add(2, new IllegalArgumentException(EXCEPTION_MSG));
		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException(
				TestExecPluginActivator.ROOT,
				org.osgi.service.dmt.DmtException.COMMAND_NOT_ALLOWED,
				DmtConstants.MESSAGE, causes,true);

		tbc.assertEquals("Asserts getURI() method", TestExecPluginActivator.ROOT, de.getURI());
		
        tbc.assertEquals("Asserts getMessage() method","COMMAND_NOT_ALLOWED: '"+TestExecPluginActivator.ROOT+"': "+DmtConstants.MESSAGE,de.getMessage());
        
		tbc.assertEquals("Asserts getCode() method",org.osgi.service.dmt.DmtException.COMMAND_NOT_ALLOWED, de.getCode());
		
		tbc.assertException("Asserts that getCause returns the first exception in case of more than one exception", 
				IllegalStateException.class, de.getCause());
		
		Vector causesReturned = de.getCauses();
		tbc.assertEquals("Asserts the size of getCauses() method", causes.size(), causesReturned.size());
		Iterator iter = causesReturned.iterator();
		int found=0;
		int expected = causes.size();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (obj instanceof IllegalStateException) {
                if (((IllegalStateException)obj).toString().indexOf(EXCEPTION_MSG)>-1) {
                    found++;
                }
            } else if (obj instanceof Exception) {
                if (((Exception)obj).toString().indexOf(EXCEPTION_MSG)>-1) {
                    found++;
                }
            } else if (obj instanceof IllegalArgumentException) {
                if (((IllegalArgumentException)obj).toString().indexOf(EXCEPTION_MSG)>-1) {
                    found++;
                }
            }           
            
        }
		tbc.assertEquals("Asserts the all of the exceptions were returned correctly",expected,found);
		tbc.assertTrue("Asserts isFatal() method", de.isFatal());
	}

	/**
	 * Tests if the values that are passed as parameters for the constructor are equal 
	 * to the values returned through its corresponding get methods using the constructor
	 * DmtException(String,int,String,Vector,boolean) and 'null' values whenever it is possible
     * 
	 * @spec DmtException.DmtException(String,int,String,Vector,boolean)
	 * 
	 */
	private void testDmtException010() {
		tbc.log("#testDmtException010");
		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException((String)null, org.osgi.service.dmt.DmtException.TRANSACTION_ERROR, null, (Vector)null, true);
		tbc.assertNull("Asserts getURI() method", de.getURI());
        tbc.assertEquals("Asserts getMessage() method","TRANSACTION_ERROR",de.getMessage());
		tbc.assertEquals("Asserts getCode() method",org.osgi.service.dmt.DmtException.TRANSACTION_ERROR, de.getCode());
		tbc.assertNull("Asserts getCause() method",de.getCause());
		tbc.assertTrue("Asserts that getCauses() returns an empty Vector if there is no cause", de.getCauses().size()==0);
		tbc.assertTrue("Asserts isFatal() method", de.isFatal());
	}
	
	/**
	 * Tests if the values that are passed as parameters for the constructor are equal 
	 * to the values returned through its corresponding get methods using the constructor
	 * DmtException(String[],int,String,Vector,boolean)
     * 
	 * @spec DmtException.DmtException(String[],int,String,Vector,boolean)
	 * 
	 */
	private void testDmtException011() {
		tbc.log("#testDmtException011");
		Vector causes = new Vector();
		causes.add(0, new IllegalArgumentException(EXCEPTION_MSG));
		causes.add(1, new Exception(EXCEPTION_MSG));
		causes.add(2, new IllegalStateException(EXCEPTION_MSG));
		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException(
				new String [] {".","g", "h", "i"},
				org.osgi.service.dmt.DmtException.FEATURE_NOT_SUPPORTED,
				DmtConstants.MESSAGE, causes, true);

		String uri = "./g/h/i";

		tbc.assertEquals("Asserts getURI() method", uri, de.getURI());

        tbc.assertEquals("Asserts getMessage() method","FEATURE_NOT_SUPPORTED: '"+uri+"': "+DmtConstants.MESSAGE,de.getMessage());
		tbc.assertEquals("Asserts getCode() method",
				org.osgi.service.dmt.DmtException.FEATURE_NOT_SUPPORTED, de.getCode());

		tbc.assertException("Asserts that getCause returns the first exception in case of more than one exception", 
				IllegalArgumentException.class, de.getCause());
		
		Vector causesReturned = de.getCauses();
		tbc.assertEquals("Asserts the size of getCauses() method", causes.size(), causesReturned.size());
        Iterator iter = causesReturned.iterator();
        int found=0;
        int expected = causes.size();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (obj instanceof IllegalStateException) {
                if (((IllegalStateException)obj).toString().indexOf(EXCEPTION_MSG)>-1) {
                    found++;
                }
            } else if (obj instanceof Exception) {
                if (((Exception)obj).toString().indexOf(EXCEPTION_MSG)>-1) {
                    found++;
                }
            } else if (obj instanceof IllegalArgumentException) {
                if (((IllegalArgumentException)obj).toString().indexOf(EXCEPTION_MSG)>-1) {
                    found++;
                }
            }           
            
        }
		tbc.assertEquals("Asserts the all of the exceptions were returned correctly",expected,found);
		tbc.assertTrue("Asserts isFatal() method", de.isFatal());
	}

	/**
	 * Tests if the values that are passed as parameters for the constructor are equal 
	 * to the values returned through its corresponding get methods using the constructor
	 * DmtException(String[],int,String,Vector,boolean) and 'null' values whenever it is possible
     * 
	 * @spec DmtException.DmtException(String[],int,String,Vector,boolean)
	 * 
	 */
	private void testDmtException012() {
		tbc.log("#testDmtException012");
		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException((String[])null, org.osgi.service.dmt.DmtException.NODE_ALREADY_EXISTS, null, (Vector)null, true);
		
		tbc.assertNull("Asserts getURI() method", de.getURI());
        tbc.assertEquals("Asserts getMessage() method","NODE_ALREADY_EXISTS",de.getMessage());
		tbc.assertEquals("Asserts getCode() method", org.osgi.service.dmt.DmtException.NODE_ALREADY_EXISTS, de.getCode());
		tbc.assertNull("Asserts getCause() method",de.getCause());
		tbc.assertTrue("Asserts that getCauses() returns an empty Vector if there is no cause", 
				de.getCauses().size()==0);
		
		tbc.assertTrue("Asserts isFatal() method", de.isFatal());
	}
	

}

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

package org.osgi.test.cases.dmt.tc1.tbc.DmtException;

import info.dmtree.DmtIllegalStateException;

import java.util.Vector;

import org.osgi.test.cases.dmt.tc1.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;

/**
 * This class tests DmtException constructors according to MEG specification 
 */

public class DmtException extends DmtTestControl {
	private static final String EXCEPTION_MSG = "exception_msg";

	/**
	 * Tests if the values that are passed as parameters for the constructor are equal 
	 * to the values returned through its corresponding get methods using the constructor
     * DmtException(String,int,String)
	 * 
	 * @spec DmtException.DmtException(String,int,String)
	 * 
	 */
	public void testDmtException001() {
		log("#testDmtException001");
		info.dmtree.DmtException de = new info.dmtree.DmtException(
				DmtConstants.OSGi_LOG,
				info.dmtree.DmtException.INVALID_URI,
				DmtConstants.MESSAGE);

		assertEquals("Asserts getURI() method", DmtConstants.OSGi_LOG, de
				.getURI());
		assertEquals("Asserts getMessage() method", "INVALID_URI: '"
				+ DmtConstants.OSGi_LOG + "': " + DmtConstants.MESSAGE, de
				.getMessage());
		
		assertEquals("Asserts getCode() method",
				info.dmtree.DmtException.INVALID_URI, de.getCode());
		
		assertEquals("Asserts getCauses() method", 0, de.getCauses().length);
		assertNull("Asserts getCause() method", de.getCause());
		assertTrue("Asserts isFatal() method", !de.isFatal());
	}
	
	
	/**
	 * Tests if the values that are passed as parameters for the constructor are equal 
	 * to the values returned through its corresponding get methods using the constructor
     * DmtException(String,int,String) and 'null' values whenever it is possible
	 * 
	 * @spec DmtException.DmtException(String,int,String)
	 * 
	 */
	public void testDmtException002() {
		log("#testDmtException002");
		info.dmtree.DmtException de = new info.dmtree.DmtException((String)null, info.dmtree.DmtException.CONCURRENT_ACCESS, null);
		assertNull("Asserts getURI() method", de.getURI());
        
		assertEquals("Asserts getCode() method",
				info.dmtree.DmtException.CONCURRENT_ACCESS, de.getCode());
		assertEquals("Asserts getMessage() method", "CONCURRENT_ACCESS", de
				.getMessage());
		assertNull("Asserts getCause() method", de.getCause());
		assertTrue(
				"Asserts that getCauses() returns an empty array if there is no cause", 
				de.getCauses().length==0);
		assertTrue("Asserts isFatal() method", !de.isFatal());
		
	}
	
	/**
	 * Tests if the values that are passed as parameters for the constructor are equal 
	 * to the values returned through its corresponding get methods using the constructor
     * DmtException(String[],int,String)
	 * 
	 * @spec DmtException.DmtException(String[],int,String)
	 * 
	 */
	public void testDmtException003() {
		log("#testDmtException003");
		info.dmtree.DmtException de = new info.dmtree.DmtException(
				new String[] {".","a","b","c" },
				info.dmtree.DmtException.COMMAND_NOT_ALLOWED,
				DmtConstants.MESSAGE);

		String uri = "./a/b/c";

		assertEquals("Asserts getURI() method", uri, de.getURI());

        assertEquals("Asserts getMessage() method", "COMMAND_NOT_ALLOWED: '"
				+ uri + "': " + DmtConstants.MESSAGE, de.getMessage());

		assertEquals("Asserts getCode() method",
				info.dmtree.DmtException.COMMAND_NOT_ALLOWED, de.getCode());
		
		assertEquals("Asserts getCauses() method", 0, de.getCauses().length);
		assertNull("Asserts getCause() method", de.getCause());
		assertTrue("Asserts isFatal() method", !de.isFatal());

	}
	
	
	/**
	 * Tests if the values that are passed as parameters for the constructor are equal 
	 * to the values returned through its corresponding get methods using the constructor
     * DmtException(String[],int,String) and 'null' values whenever it is possible
	 * 
	 * @spec DmtException.DmtException(String[],int,String)
	 * 
	 */
	public void testDmtException004() {
		log("#testDmtException004");
		info.dmtree.DmtException de = new info.dmtree.DmtException((String[])null, info.dmtree.DmtException.ALERT_NOT_ROUTED, null);
		
		assertNull("Asserts getURI() method", de.getURI());
		
		assertEquals("Asserts getCode() method",
				info.dmtree.DmtException.ALERT_NOT_ROUTED, de.getCode());
		assertEquals("Asserts getMessage() method", "ALERT_NOT_ROUTED", de
				.getMessage());
		assertNull("Asserts getCause() method", de.getCause());
		assertTrue(
				"Asserts that getCauses() returns an empty array if there is no cause", 
				de.getCauses().length==0);
		assertTrue("Asserts isFatal() method", !de.isFatal());
		
	}

	/**
	 * Tests if the values that are passed as parameters for the constructor are equal 
	 * to the values returned through its corresponding get methods using the constructor
	 * DmtException(String,int,String,Throwable)
     * 
	 * @spec DmtException.DmtException(String,int,String,Throwable)
	 * 
	 */
	public void testDmtException005() {
		log("#testDmtException005");
		info.dmtree.DmtException de = new info.dmtree.DmtException(
				DmtConstants.OSGi_LOG,
				info.dmtree.DmtException.CONCURRENT_ACCESS,
				DmtConstants.MESSAGE, new Exception());

		assertEquals("Asserts getURI() method", DmtConstants.OSGi_LOG, de
				.getURI());
		

        assertEquals("Asserts getMessage() method", "CONCURRENT_ACCESS: '"
				+ DmtConstants.OSGi_LOG + "': " + DmtConstants.MESSAGE, de
				.getMessage());
		assertEquals("Asserts getCode() method",
				info.dmtree.DmtException.CONCURRENT_ACCESS, de.getCode());
		
		assertException("Asserts getCause() method", Exception.class, de
				.getCause());
		
		assertEquals("Asserts getCauses() method", 1, de.getCauses().length);
		
		assertException("Asserts getCauses() method", Exception.class,
				de
				.getCauses()[0]);
		assertTrue("Asserts isFatal() method", !de.isFatal());
	}
	/**
	 * Tests if the values that are passed as parameters for the constructor are equal 
	 * to the values returned through its corresponding get methods using the constructor
     * DmtException(String,int,String,Throwable)  and 'null' values whenever it is possible
	 * 
	 * @spec DmtException.DmtException(String,int,String,Throwable)
	 * 
	 */
	public void testDmtException006() {
		log("#testDmtException006");
		info.dmtree.DmtException de = new info.dmtree.DmtException((String)null, info.dmtree.DmtException.COMMAND_FAILED, null,
				(Throwable)null);
		assertNull("Asserts getURI() method", de.getURI());
		assertEquals("Asserts getMessage() method", "COMMAND_FAILED", de
				.getMessage());
		
		assertEquals("Asserts getCode() method",
				info.dmtree.DmtException.COMMAND_FAILED, de.getCode());
		
		assertNull("Asserts getCause() method", de.getCause());
		assertTrue(
				"Asserts that getCauses() returns an empty array if there is no cause", 
				de.getCauses().length==0);
		assertTrue("Asserts isFatal() method", !de.isFatal());

	}	
	/**
	 * Tests if the values that are passed as parameters for the constructor are equal 
	 * to the values returned through its corresponding get methods using the constructor
     * DmtException(String[],int,String,Throwable)
	 * 
	 * @spec DmtException.DmtException(String[],int,String,Throwable)
	 * 
	 */
	public void testDmtException007() {
		log("#testDmtException007");
		info.dmtree.DmtException de = new info.dmtree.DmtException(
				new String[] {".","d", "e", "f"},
				info.dmtree.DmtException.REMOTE_ERROR,
				DmtConstants.MESSAGE, new IllegalArgumentException());
		String uri = "./d/e/f";

		assertEquals("Asserts getURI() method", uri, de.getURI());

        assertEquals("Asserts getMessage() method", "REMOTE_ERROR: '" + uri
				+ "': " + DmtConstants.MESSAGE, de.getMessage());
        
		assertEquals("Asserts getCode() method",
				info.dmtree.DmtException.REMOTE_ERROR, de.getCode());
		
		assertException("Asserts getCause() method",
				IllegalArgumentException.class, de.getCause());
		
		assertEquals("Asserts getCauses() method", 1, de.getCauses().length);
		
		assertException("Asserts getCauses() method",
				IllegalArgumentException.class,
				de.getCauses()[0]);
		assertTrue("Asserts isFatal() method", !de.isFatal());
	}
	/**
	 * Tests if the values that are passed as parameters for the constructor are equal 
	 * to the values returned through its corresponding get methods using the constructor
	 * DmtException(String[],int,String,Throwable) and 'null' values whenever it is possible
     * 
	 * @spec DmtException.DmtException(String[],int,String,Throwable)
	 * 
	 */
	public void testDmtException008() {
		log("#testDmtException008");
		info.dmtree.DmtException de = new info.dmtree.DmtException((String[])null, info.dmtree.DmtException.URI_TOO_LONG, null,
				(Throwable)null);
		assertNull("Asserts getURI() method", de.getURI());
		assertEquals("Asserts getMessage() method", "URI_TOO_LONG", de
				.getMessage());
		assertEquals("Asserts getCode() method",
				info.dmtree.DmtException.URI_TOO_LONG, de.getCode());
		assertNull("Asserts getCause() method", de.getCause());
		assertTrue(
				"Asserts that getCauses() returns an empty array if there is no cause", 
				de.getCauses().length==0);
		assertTrue("Asserts isFatal() method", !de.isFatal());

	}
	/**
	 * Tests if the values that are passed as parameters for the constructor are equal 
	 * to the values returned through its corresponding get methods using the constructor
     * DmtException(String,int,String,Vector,boolean)
	 * 
	 * @spec DmtException.DmtException(String,int,String,Vector,boolean)
	 * 
	 */
	public void testDmtException009() {
		log("#testDmtException009");
		Vector causes = new Vector();
		causes.add(0, new DmtIllegalStateException(EXCEPTION_MSG));
		causes.add(1, new Exception(EXCEPTION_MSG));
		causes.add(2, new IllegalArgumentException(EXCEPTION_MSG));
		info.dmtree.DmtException de = new info.dmtree.DmtException(
				DmtConstants.OSGi_CONFIGURATION,
				info.dmtree.DmtException.COMMAND_NOT_ALLOWED,
				DmtConstants.MESSAGE, causes,true);

		assertEquals("Asserts getURI() method",
				DmtConstants.OSGi_CONFIGURATION, de.getURI());
		
        assertEquals("Asserts getMessage() method", "COMMAND_NOT_ALLOWED: '"
				+ DmtConstants.OSGi_CONFIGURATION + "': "
				+ DmtConstants.MESSAGE, de.getMessage());
        
		assertEquals("Asserts getCode() method",
				info.dmtree.DmtException.COMMAND_NOT_ALLOWED, de.getCode());
		
		assertException(
				"Asserts that getCause returns the first exception in case of more than one exception", 
				DmtIllegalStateException.class, de.getCause());
		
		Throwable[] causesReturned = de.getCauses();
		assertEquals("Asserts the size of getCauses() method", causes.size(),
				causesReturned.length);
		
		int found=0;
		int expected = causes.size();
        for (int i = 0; i < causesReturned.length; i++) {
            Object obj = causesReturned[i];
            if (obj instanceof DmtIllegalStateException) {
                if (((DmtIllegalStateException)obj).toString().indexOf(EXCEPTION_MSG)>-1) {
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
		assertEquals(
				"Asserts the all of the exceptions were returned correctly",
				expected, found);
		assertTrue("Asserts isFatal() method", de.isFatal());
	}

	/**
	 * Tests if the values that are passed as parameters for the constructor are equal 
	 * to the values returned through its corresponding get methods using the constructor
	 * DmtException(String,int,String,Vector,boolean) and 'null' values whenever it is possible
     * 
	 * @spec DmtException.DmtException(String,int,String,Vector,boolean)
	 * 
	 */
	public void testDmtException010() {
		log("#testDmtException010");
		info.dmtree.DmtException de = new info.dmtree.DmtException((String)null, info.dmtree.DmtException.TRANSACTION_ERROR, null, (Vector)null, true);
		assertNull("Asserts getURI() method", de.getURI());
		assertEquals("Asserts getMessage() method", "TRANSACTION_ERROR", de
				.getMessage());
		assertEquals("Asserts getCode() method",
				info.dmtree.DmtException.TRANSACTION_ERROR, de.getCode());
		assertNull("Asserts getCause() method", de.getCause());
		assertTrue(
				"Asserts that getCauses() returns an empty array if there is no cause",
				de.getCauses().length == 0);
		assertTrue("Asserts isFatal() method", de.isFatal());
	}
	
	/**
	 * Tests if the values that are passed as parameters for the constructor are equal 
	 * to the values returned through its corresponding get methods using the constructor
	 * DmtException(String[],int,String,Vector,boolean)
     * 
	 * @spec DmtException.DmtException(String[],int,String,Vector,boolean)
	 * 
	 */
	public void testDmtException011() {
		log("#testDmtException011");
		Vector causes = new Vector();
		causes.add(0, new IllegalArgumentException(EXCEPTION_MSG));
		causes.add(1, new Exception(EXCEPTION_MSG));
		causes.add(2, new DmtIllegalStateException(EXCEPTION_MSG));
		info.dmtree.DmtException de = new info.dmtree.DmtException(
				new String [] {".","g", "h", "i"},
				info.dmtree.DmtException.FEATURE_NOT_SUPPORTED,
				DmtConstants.MESSAGE, causes, true);

		String uri = "./g/h/i";

		assertEquals("Asserts getURI() method", uri, de.getURI());

        assertEquals("Asserts getMessage() method", "FEATURE_NOT_SUPPORTED: '"
				+ uri + "': " + DmtConstants.MESSAGE, de.getMessage());
		assertEquals("Asserts getCode() method",
				info.dmtree.DmtException.FEATURE_NOT_SUPPORTED, de.getCode());

		assertException(
				"Asserts that getCause returns the first exception in case of more than one exception", 
				IllegalArgumentException.class, de.getCause());
		
		Throwable[] causesReturned = de.getCauses();
		assertEquals("Asserts the size of getCauses() method", causes.size(),
				causesReturned.length);
        
        int found=0;
        int expected = causes.size();
        for (int i = 0; i < causesReturned.length; i++) {
            Object obj = causesReturned[i];
            if (obj instanceof DmtIllegalStateException) {
                if (((DmtIllegalStateException)obj).toString().indexOf(EXCEPTION_MSG)>-1) {
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

		assertEquals(
				"Asserts the all of the exceptions were returned correctly",
				expected, found);
		assertTrue("Asserts isFatal() method", de.isFatal());
	}

	/**
	 * Tests if the values that are passed as parameters for the constructor are equal 
	 * to the values returned through its corresponding get methods using the constructor
	 * DmtException(String[],int,String,Vector,boolean) and 'null' values whenever it is possible
     * 
	 * @spec DmtException.DmtException(String[],int,String,Vector,boolean)
	 * 
	 */
	public void testDmtException012() {
		log("#testDmtException012");
		info.dmtree.DmtException de = new info.dmtree.DmtException((String[])null, info.dmtree.DmtException.NODE_ALREADY_EXISTS, null, (Vector)null, true);
		
		assertNull("Asserts getURI() method", de.getURI());
		assertEquals("Asserts getMessage() method", "NODE_ALREADY_EXISTS", de
				.getMessage());
		assertEquals("Asserts getCode() method",
				info.dmtree.DmtException.NODE_ALREADY_EXISTS, de.getCode());
		assertNull("Asserts getCause() method", de.getCause());
		assertTrue(
				"Asserts that getCauses() returns an empty Vector if there is no cause", 
				de.getCauses().length==0);
		
		assertTrue("Asserts isFatal() method", de.isFatal());
	}
	

}

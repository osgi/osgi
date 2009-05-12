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
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-002)
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc1.tbc.DmtException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Vector;

import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;

/**
 * This test case validates the implementation of <code>printStackTrace</code> method of DmtException, 
 * according to MEG specification
 */
public class PrintStackTrace extends DmtTestControl {
	/**
	 *  Asserts that any causes that were specified for this exception are printed
	 * 
	 * @spec DmtException.printStackTrace(java.io.PrintStream)
	 */
	public void testPrintStackTrace001() {
		log("#testPrintStackTrace001");
        try {
    		info.dmtree.DmtException de = new info.dmtree.DmtException(
    				(String)null, 0, null, new Exception());
    
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		PrintStream ps = new PrintStream(baos);
    		de.printStackTrace(ps);
    
    		assertTrue("Asserts printStackTrace method",
    						baos.toString().indexOf("java.lang.Exception") > -1);
    		
    		try {
    			ps.close();
    			baos.close();
    		} catch (IOException e) {
    			// ignore
    		}
        } catch (Exception e) {
        	failUnexpectedException(e);
        }
	}
	
	/**
	 * Asserts that any causes that were specified for this exception are printed when
     * there is more than one cause
	 * 
	 * @spec DmtException.printStackTrace(java.io.PrintStream)
	 */
	public void testPrintStackTrace002() {
		log("#testPrintStackTrace002");
        try {
    		Vector causes = new Vector();
    		causes.add(0,new Exception());
    		causes.add(1,new NullPointerException());
    		
    		info.dmtree.DmtException de = new info.dmtree.DmtException(
    				(String)null, 0, null,causes,false);
    
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		PrintStream ps = new PrintStream(baos);
    		de.printStackTrace(ps);
    
    		assertTrue("Asserts printStackTrace method",
    						baos.toString().indexOf("java.lang.Exception") > -1);
    
    		assertTrue("Asserts printStackTrace method",
    				baos.toString().indexOf("java.lang.NullPointerException") > -1);
    
    		try {
    			ps.close();
    			baos.close();
    		} catch (IOException e) {
    			// ignore
    		}
        } catch (Exception e) {
        	failUnexpectedException(e);
        }
	}

}

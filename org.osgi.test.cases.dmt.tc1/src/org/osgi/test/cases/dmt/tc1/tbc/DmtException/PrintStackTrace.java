/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
 
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
    		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException(
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
			Vector<Exception> causes = new Vector<>();
    		causes.add(0,new Exception());
    		causes.add(1,new NullPointerException());
    		
    		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException(
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

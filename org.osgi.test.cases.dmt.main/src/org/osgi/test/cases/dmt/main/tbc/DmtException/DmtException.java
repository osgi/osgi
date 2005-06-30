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

import java.util.Vector;

import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;

/**
 * @methodUnderTest org.osgi.service.dmt.DmtException#DmtException
 * @generalDescription This class tests DmtException constructors according with
 *                     MEG specification (rfc0085)
 */

public class DmtException {
	private DmtTestControl tbc;
	private static final String EXCEPTION_MSG = "test";
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
	}

	/**
	 * @testID testDmtException001
	 * @testDescription Tests if the values that are passed as parameters for
	 *                  the constructor are equal to the values returned through
	 *                  its corresponding get methods 
	 * 
	 */
	private void testDmtException001() {
		tbc.log("#testDmtException001");
		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException(
				DmtTestControl.OSGi_LOG,
				org.osgi.service.dmt.DmtException.INVALID_URI,
				DmtTestControl.MESSAGE);

		tbc.assertEquals("Asserts getURI() method", DmtTestControl.OSGi_LOG, de
				.getURI());
		tbc.assertTrue("Asserts getMessage() method", de.getMessage().indexOf(
				DmtTestControl.MESSAGE) > -1);
		tbc.assertEquals("Asserts getCode() method",
				org.osgi.service.dmt.DmtException.INVALID_URI, de.getCode());
		tbc.assertEquals("Asserts getCauses() method", 0, de.getCauses().size());
		tbc.assertNull("Asserts getCause() method", de.getCause());
		tbc.assertTrue("Asserts isFatal() method", !de.isFatal());

	}
	
	
	/**
	 * @testID testDmtException002
	 * @testDescription Tests if the values that are passed as parameters for
	 *                  the constructor are equal to the values returned through
	 *                  its corresponding get methods
	 * 
	 */
	private void testDmtException002() {
		tbc.log("#testDmtException002");
		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException(null, -1, null);
		tbc.assertNull("Asserts getURI() method", de.getURI());
		tbc.assertNull("Asserts getMessage() method", de.getMessage());
		tbc.assertEquals("Asserts getCode() method", -1, de.getCode());
		
		tbc.assertNull("Asserts getCause() method", de.getCause());		
		tbc.assertTrue("Asserts that getCauses() returns an empty Vector if there is no cause", de.getCauses().size()==0);
		tbc.assertTrue("Asserts isFatal() method", !de.isFatal());
		
	}
	

	/**
	 * @testID testDmtException003
	 * @testDescription Tests if the values that are passed as parameters for
	 *                  the constructor are equal to the values returned through
	 *                  its corresponding get methods
	 */
	private void testDmtException003() {
		tbc.log("#testDmtException003");
		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException(
				DmtTestControl.OSGi_LOG,
				org.osgi.service.dmt.DmtException.FORMAT_NOT_SUPPORTED,
				DmtTestControl.MESSAGE, new Exception());

		tbc.assertEquals("Asserts getURI() method", DmtTestControl.OSGi_LOG, de
				.getURI());
		tbc.assertTrue("Asserts getMessage() method", de.getMessage().indexOf(
				DmtTestControl.MESSAGE) > -1);
		tbc.assertEquals("Asserts getCode() method",
				org.osgi.service.dmt.DmtException.FORMAT_NOT_SUPPORTED, de.getCode());
		
		tbc.assertException("Asserts getCause() method", Exception.class, de.getCause());
		
		tbc.assertEquals("Asserts getCauses() method", 1, de.getCauses().size());
		
		tbc.assertException("Asserts getCauses() method", Exception.class,
				(Exception) de.getCauses().get(0));
		tbc.assertTrue("Asserts isFatal() method", !de.isFatal());
	}
	/**
	 * @testID testDmtException004
	 * @testDescription Tests if the values that are passed as parameters for
	 *                  the constructor are equal to the values returned through
	 *                  its corresponding get methods
	 */
	private void testDmtException004() {
		tbc.log("#testDmtException004");
		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException(null, -1, null,
				(Throwable)null);
		tbc.assertNull("Asserts getURI() method", de.getURI());
		tbc.assertNull("Asserts getMessage() method", de.getMessage());
		tbc.assertEquals("Asserts getCode() method", -1, de.getCode());
		tbc.assertNull("Asserts getCause() method",de.getCause());
		tbc.assertTrue("Asserts that getCauses() returns an empty Vector if there is no cause", de.getCauses().size()==0);
		tbc.assertTrue("Asserts isFatal() method", !de.isFatal());

	}	

	/**
	 * @testID testDmtException005
	 * @testDescription Tests if the values that are passed as parameters for
	 *                  the constructor are equal to the values returned through
	 *                  its corresponding get methods
	 */
	private void testDmtException005() {
		tbc.log("#testDmtException005");
		Vector causes = new Vector();
		causes.add(0, new Exception(EXCEPTION_MSG));

		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException(
				DmtTestControl.OSGi_LOG,
				org.osgi.service.dmt.DmtException.METADATA_MISMATCH,
				DmtTestControl.MESSAGE, causes);
		
		tbc.assertEquals("Asserts getURI() method", DmtTestControl.OSGi_LOG,de.getURI());
		tbc.assertTrue("Asserts getMessage() method", de.getMessage().indexOf(
				DmtTestControl.MESSAGE) > -1);
		tbc.assertEquals("Asserts getCode() method",
				org.osgi.service.dmt.DmtException.METADATA_MISMATCH, de.getCode());
		
		tbc.assertException("Asserts getCause() method", Exception.class, de.getCause());
		tbc.assertTrue("Asserts getCause() method", de.getCause().toString().indexOf(EXCEPTION_MSG)>-1);
		tbc.assertEquals("Asserts the size of getCauses() method", causes.size(), de.getCauses().size());
		tbc.assertTrue("Asserts the name of getCauses() method",
				(((Exception) de.getCauses().elementAt(0)).toString().indexOf(
						EXCEPTION_MSG) > -1));
		tbc.assertTrue("Asserts isFatal() method", !de.isFatal());

	}
	/**
	 * @testID testDmtException006
	 * @testDescription Tests if the values that are passed as parameters for
	 *                  the constructor are equal to the values returned through
	 *                  its corresponding get methods
	 */
	private void testDmtException006() {
		tbc.log("#testDmtException006");
		Vector causes = new Vector();
		causes.add(0, new IllegalArgumentException(EXCEPTION_MSG));

		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException(null, -1, null, (Vector)null);
		tbc.assertNull("Asserts getURI() method", de.getURI());
		tbc.assertNull("Asserts getMessage() method", de.getMessage());
		tbc.assertEquals("Asserts getCode() method", -1, de.getCode());
		tbc.assertNull("Asserts getCause() method",de.getCause());
		tbc.assertTrue("Asserts that getCauses() returns an empty Vector if there is no cause", de.getCauses().size()==0);
		tbc.assertTrue("Asserts isFatal() method", !de.isFatal());

	}
	/**
	 * @testID testDmtException007
	 * @testDescription Tests if the values the method isFatal() returns true
	 *                  correctly
	 */
	private void testDmtException007() {
		tbc.log("#testDmtException007");
		Vector causes = new Vector();
		causes.add(0, new Exception(EXCEPTION_MSG));
		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException(
				DmtTestControl.OSGi_LOG,
				org.osgi.service.dmt.DmtException.INVALID_URI,
				DmtTestControl.MESSAGE, causes, true);

		tbc.assertEquals("Asserts getURI() method", DmtTestControl.OSGi_LOG, de
				.getURI());
		tbc.assertTrue("Asserts getMessage() method", de.getMessage().indexOf(
				DmtTestControl.MESSAGE) > -1);
		tbc.assertEquals("Asserts getCode() method",
				org.osgi.service.dmt.DmtException.INVALID_URI, de.getCode());
		tbc.assertEquals("Asserts the size of getCauses() method", 1, de
				.getCauses().size());
		tbc.assertTrue("Asserts the name of getCauses() method",
				(((Exception) de.getCauses().elementAt(0)).toString().indexOf(
						EXCEPTION_MSG) > -1));
		tbc.assertTrue("Asserts isFatal() method", de.isFatal());
	}

	/**
	 * @testID testDmtException008
	 * @testDescription Tests if the values the method isFatal() returns true
	 *                  correctly, that getCause returns null if there is no cause
	 * 					and that getCauses returns an empty vector
	 */
	private void testDmtException008() {
		tbc.log("#testDmtException008");
		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException(null, -1, null, (Vector)null, true);
		tbc.assertNull("Asserts getURI() method", de.getURI());
		tbc.assertNull("Asserts getMessage() method", de.getMessage());
		tbc.assertEquals("Asserts getCode() method", -1, de.getCode());
		tbc.assertNull("Asserts getCause() method",de.getCause());
		tbc.assertTrue("Asserts that getCauses() returns an empty Vector if there is no cause", de.getCauses().size()==0);
		tbc.assertTrue("Asserts isFatal() method", de.isFatal());
	}
	
	/**
	 * @testID testDmtException009
	 * @testDescription Tests if the values the method isFatal() returns false
	 *                  correctly. It also tests if GetMessage returns 
	 *                  the associated URI and the exception code
	 */
	private void testDmtException009() {
		tbc.log("#testDmtException009");
		Vector causes = new Vector();
		causes.add(0, new Exception(EXCEPTION_MSG));
		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException(
				DmtTestControl.OSGi_LOG,
				org.osgi.service.dmt.DmtException.INVALID_URI,
				DmtTestControl.MESSAGE, causes, false);

		tbc.assertEquals("Asserts getURI() method", DmtTestControl.OSGi_LOG, de
				.getURI());
		String message = de.getMessage();
		
		tbc.assertTrue("Asserts getMessage() method",
					message.indexOf(DmtTestControl.MESSAGE) > -1
					&& message.indexOf(DmtTestControl.OSGi_LOG) > -1
					&& message.indexOf(String.valueOf(org.osgi.service.dmt.DmtException.INVALID_URI)) > -1);

		tbc.assertEquals("Asserts getCode() method",
				org.osgi.service.dmt.DmtException.INVALID_URI, de.getCode());
		tbc.assertEquals("Asserts the size of getCauses() method", causes.size(), de
				.getCauses().size());
		tbc.assertTrue("Asserts the name of getCauses() method",
				(((Exception) de.getCauses().elementAt(0)).toString().indexOf(
						EXCEPTION_MSG) > -1));
		tbc.assertTrue("Asserts isFatal() method", !de.isFatal());
	}
	
	/**
	 * @testID testDmtException010
	 * @testDescription Tests if, when there are more than one exception, getCause returns the first one
	 * 					and getCauses returns all of them 
	 */
	private void testDmtException010() {
		tbc.log("#testDmtException010");
		Vector causes = new Vector();
		causes.add(0, new IllegalStateException(EXCEPTION_MSG));
		causes.add(1, new Exception(EXCEPTION_MSG));
		causes.add(2, new IllegalArgumentException(EXCEPTION_MSG));
		org.osgi.service.dmt.DmtException de = new org.osgi.service.dmt.DmtException(
				DmtTestControl.OSGi_CFG,
				org.osgi.service.dmt.DmtException.OTHER_ERROR,
				DmtTestControl.MESSAGE, causes);

		tbc.assertEquals("Asserts getURI() method", DmtTestControl.OSGi_CFG, de.getURI());
		
		String message = de.getMessage();
		tbc.assertTrue("Asserts getMessage() method",
						message.indexOf(DmtTestControl.MESSAGE) > -1
								&& message.indexOf(DmtTestControl.OSGi_CFG) > -1
								&& message.indexOf(String.valueOf(org.osgi.service.dmt.DmtException.OTHER_ERROR)) > -1);
								
		tbc.assertEquals("Asserts getCode() method",org.osgi.service.dmt.DmtException.OTHER_ERROR, de.getCode());
		
		tbc.assertException("Asserts that getCause returns the first exception in case of more than one exception", 
				IllegalStateException.class, de.getCause());
		
		Vector causesReturned = de.getCauses();
		tbc.assertEquals("Asserts the size of getCauses() method", causes.size(), causesReturned.size());
		
		int found=0;
		int expected = causes.size();
		for (int i=0;i<causesReturned.size();i++) {
			if (causesReturned.elementAt(i) instanceof IllegalStateException) {
				if (((IllegalStateException)causesReturned.elementAt(i)).toString().indexOf(EXCEPTION_MSG)>-1) {
					found++;
					continue;
				}
			}
			if (causesReturned.elementAt(i) instanceof Exception) {
				if (((Exception)causesReturned.elementAt(i)).toString().indexOf(EXCEPTION_MSG)>-1) {
					found++;
					continue;
				}
			}
			if (causesReturned.elementAt(i) instanceof IllegalArgumentException) {
				if (((IllegalArgumentException)causesReturned.elementAt(i)).toString().indexOf(EXCEPTION_MSG)>-1) {
					found++;
					continue;
				}
			}			
				
		}
		tbc.assertEquals("Asserts the all of the exceptions were returned correctly",expected,found);
		tbc.assertTrue("Asserts isFatal() method", !de.isFatal());
	}	

}

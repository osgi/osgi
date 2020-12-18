/*
 * Copyright (c) OSGi Alliance (2004, 2020). All Rights Reserved.
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
 * Apr 17, 2006  Luiz Felipe Guimaraes
 * 283           [MEGTCK][DMT] DMT API changes
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tbc.Uri;

import org.osgi.service.dmt.Uri;

import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;


/**
 * @author Luiz Felipe Guimaraes
 * 
 * This Class Validates the implementation of <code>Uri.toUri(String[])<code> method, 
 * according to MEG specification
 */
public class ToUri {
	private DmtTestControl tbc;
	
	public ToUri(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testToUri001();
		testToUri002();
		testToUri003();
		testToUri004();
	}

	/**
	 * Asserts that NullPointerException is thrown if the specified URI is null 
	 * 
	 * @spec Uri.toUri(String[])
	 */
	private void testToUri001() {

		try {
			DefaultTestBundleControl.log("#testToUri001");
			Uri.toUri(null);
			DefaultTestBundleControl.failException("", NullPointerException.class);
		} catch (NullPointerException e) {
			DefaultTestBundleControl.pass("NullPointerException correctly thrown");
		} catch (Exception e) {
			tbc.failExpectedOtherException(NullPointerException.class,e);
		}
	
	}
	
	/**
	 * Asserts that NullPointerException is thrown if any segment of the URI is null 
	 * 
	 * @spec Uri.toUri(String[])
	 */
	private void testToUri002() {

		try {
			DefaultTestBundleControl.log("#testToUri002");
			Uri.toUri(new String[] { ".", "Test",null, "Dmt" });
			DefaultTestBundleControl.failException("", NullPointerException.class);
		} catch (NullPointerException e) {
			DefaultTestBundleControl.pass("NullPointerException correctly thrown");
		} catch (Exception e) {
			tbc.failExpectedOtherException(NullPointerException.class,e);
		}
	
	}
	/**
	 * Asserts that Uri.toUri(String[]) returns an empty URI ("") 
	 * if the specified path is an empty array
	 *   
	 * @spec Uri.touri(String[])
	 */
	private void testToUri003() {

		try {
			DefaultTestBundleControl.log("#testToUri003");
			TestCase.assertTrue("Asserts that Uri.toUri(String[]) returns an empty URI (\"\") if " +
					"the specified path is an empty array",Uri.toUri(new String[0]).equals(""));
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		}
	}
	
	/**
	 * Asserts that IllegalArgumentException is thrown if the specified URI is malformed 
	 * 
	 * @spec Uri.toUri(String[])
	 */

	private void testToUri004() {
		try {
			DefaultTestBundleControl.log("#testToUri004");
			for (int i = 0; i < DmtTestControl.URIS_TOO_LONG.length; i++) {
				try {
					Uri.toUri(DmtTestControl.toPath(DmtTestControl.URIS_TOO_LONG[i]));
					DefaultTestBundleControl.failException("", IllegalArgumentException.class);
				} catch (IllegalArgumentException e) {
					DefaultTestBundleControl.pass("IllegalArgumentException correctly thrown when calling Uri.toUri(" + DmtTestControl.URIS_TOO_LONG[i] + ")");
				}
			}
			
		} catch (Exception e) {
			tbc.failExpectedOtherException(IllegalArgumentException.class,e);
		}
	}
	
}


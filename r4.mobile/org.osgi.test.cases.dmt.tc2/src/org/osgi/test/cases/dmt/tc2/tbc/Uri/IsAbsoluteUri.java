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
 * Apr 17, 2006  Luiz Felipe Guimaraes
 * 283           [MEGTCK][DMT] DMT API changes
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tbc.Uri;

import info.dmtree.Uri;

import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;


/**
 * @author Luiz Felipe Guimaraes
 * 
 * This Class Validates the implementation of <code>Uri.isAbsoluteUri(String)<code> method, 
 * according to MEG specification
 */
public class IsAbsoluteUri {
	private DmtTestControl tbc;
	public IsAbsoluteUri(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testIsAbsoluteUri001();
		testIsAbsoluteUri002();
		testIsAbsoluteUri003();
		testIsAbsoluteUri004();
	}

	/**
	 * Asserts that NullPointerException is thrown if the specified URI is null 
	 * 
	 * @spec Uri.isAbsoluteUri(String)
	 */
	private void testIsAbsoluteUri001() {

		try {
			tbc.log("#testIsAbsoluteUri001");
			Uri.isAbsoluteUri(null);
			tbc.failException("", NullPointerException.class);
		} catch (NullPointerException e) {
			tbc.pass("NullPointerException correctly thrown");
		} catch (Exception e) {
			tbc.failExpectedOtherException(NullPointerException.class,e);
		}
	}
	
	/**
	 * Asserts that the specified URI is an absolute URI 
	 * 
	 * @spec Uri.isAbsoluteUri(String)
	 */
	private void testIsAbsoluteUri002() {

		try {
			tbc.log("#testIsAbsoluteUri002");
			tbc.assertTrue("Asserts that the specified URI is an absolute URI", Uri.isAbsoluteUri("./Test"));
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		}
	}
	
	/**
	 * Asserts that the specified URI is not an absolute URI 
	 * 
	 * @spec Uri.isAbsoluteUri(String)
	 */
	private void testIsAbsoluteUri003() {

		try {
			tbc.log("#testIsAbsoluteUri003");
			tbc.assertTrue("Asserts that the specified URI is not an absolute URI", !Uri.isAbsoluteUri("Test"));
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		}
	}
	
	/**
	 * Asserts that IllegalArgumentException is thrown if the specified URI is malformed 
	 * 
	 * @spec Uri.isAbsoluteUri(String)
	 */
	private void testIsAbsoluteUri004() {

		try {
			tbc.log("#testIsAbsoluteUri004");
            //It is from 1 because 0 is 'null' and null throws NullPointerException in this method
			for (int i = 1; i < DmtTestControl.INVALID_URIS.length; i++) {
				String uri = null;
				try {
					uri = DmtTestControl.INVALID_URIS[i].toString();
					Uri.isAbsoluteUri(uri);
					tbc.failException("", IllegalArgumentException.class);
				} catch (IllegalArgumentException e) {
					tbc.pass("IllegalArgumentException correctly thrown when calling Uri.isAbsoluteUri(" + uri + ")");
				}
			}
		} catch (Exception e) {
			tbc.failExpectedOtherException(IllegalArgumentException.class,e);
		}
	}
	
	
}


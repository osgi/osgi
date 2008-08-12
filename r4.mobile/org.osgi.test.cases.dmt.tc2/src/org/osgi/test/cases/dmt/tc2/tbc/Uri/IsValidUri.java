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
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;


/**
 * @author Luiz Felipe Guimaraes
 * 
 * This Class Validates the implementation of <code>Uri.isValidUri(String)<code> method, 
 * according to MEG specification
 */
public class IsValidUri {
	private DmtTestControl tbc;
	public IsValidUri(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testIsValidUri001();
		testIsValidUri002();
		testIsValidUri003();
		
	}

	/**
	 * Asserts that Uri.isValidUri(String) returns true if Uri parameter contains a valid uri
	 * 
	 * @spec Uri.isValidUri(String)
	 */
	private void testIsValidUri001() {

		try {
			tbc.log("#testIsValidUri001");
			
			tbc.assertTrue("Asserts that Uri.isValidUri(String) returns true if Uri parameter contains a valid uri",
					Uri.isValidUri(TestExecPluginActivator.INTERIOR_NODE));
			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		}
	}
	
	/**
	 * Asserts that Uri.isValidUri(String) returns false if Uri parameter contains an invalid uri
	 * 
	 * @spec Uri.isValidUri(String)
	 */
	private void testIsValidUri002() {

		try {
			tbc.log("#testIsValidUri002");
			
			for (int i = 0; i < DmtTestControl.INVALID_URIS.length; i++) {
				String uri = null;
				//It covers two cases: if the URI is not null and if the URI follows the syntax defined for valid DMT URIs 
				if (DmtTestControl.INVALID_URIS[i]!=null) {
					uri = DmtTestControl.INVALID_URIS[i].toString();
				}
				tbc.assertTrue("Asserts that "+ uri +" is not a valid URI", !Uri.isValidUri(uri));
			}
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		}
	}
	
	/**
	 * Asserts that Uri.isValidUri(String) returns false if Uri parameter contains an uri that exceeds
	 * the maximum allowed 
	 * 
	 * @spec Uri.isValidUri(String)
	 */
	private void testIsValidUri003() {

		try {
			tbc.log("#testIsValidUri003");
			//It covers three cases: if the length of the URI is not more than getMaxUriLength(), 
			//if the URI doesn't contain more than getMaxUriSegments() segments and if the 
			//length of each segment of the URI is less than or equal to getMaxSegmentNameLength(). 
			for (int i = 0; i < DmtTestControl.URIS_TOO_LONG.length; i++) {
				tbc.assertTrue("Asserts that "+ DmtTestControl.URIS_TOO_LONG[i] +" is not a valid URI", 
						!Uri.isValidUri(DmtTestControl.URIS_TOO_LONG[i]));
			}
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		}
	}
	
}


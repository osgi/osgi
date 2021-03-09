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
 * Apr 17, 2006  Luiz Felipe Guimaraes
 * 283           [MEGTCK][DMT] DMT API changes
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tbc.Uri;

import org.osgi.service.dmt.Uri;

import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;


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
			DefaultTestBundleControl.log("#testIsValidUri001");
			
			TestCase.assertTrue("Asserts that Uri.isValidUri(String) returns true if Uri parameter contains a valid uri",
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
			DefaultTestBundleControl.log("#testIsValidUri002");
			
			for (int i = 0; i < DmtTestControl.INVALID_URIS.length; i++) {
				String uri = null;
				//It covers two cases: if the URI is not null and if the URI follows the syntax defined for valid DMT URIs 
				if (DmtTestControl.INVALID_URIS[i]!=null) {
					uri = DmtTestControl.INVALID_URIS[i].toString();
				}
				TestCase.assertTrue("Asserts that "+ uri +" is not a valid URI", !Uri.isValidUri(uri));
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
			DefaultTestBundleControl.log("#testIsValidUri003");
			//It covers three cases: if the length of the URI is not more than getMaxUriLength(), 
			//if the URI doesn't contain more than getMaxUriSegments() segments and if the 
			//length of each segment of the URI is less than or equal to getMaxSegmentNameLength(). 
			for (int i = 0; i < DmtTestControl.URIS_TOO_LONG.length; i++) {
				TestCase.assertTrue("Asserts that "+ DmtTestControl.URIS_TOO_LONG[i] +" is not a valid URI", 
						!Uri.isValidUri(DmtTestControl.URIS_TOO_LONG[i]));
			}
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		}
	}
	
}


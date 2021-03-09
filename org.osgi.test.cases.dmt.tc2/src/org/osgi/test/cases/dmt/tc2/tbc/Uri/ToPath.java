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
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;


/**
 * @author Luiz Felipe Guimaraes
 * 
 * This Class Validates the implementation of <code>Uri.toPath(String)<code> method, 
 * according to MEG specification
 */
public class ToPath {
	private DmtTestControl tbc;
	public ToPath(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testToPath001();
		testToPath002();
		testToPath003();
	}

	/**
	 * Asserts that NullPointerException is thrown if the specified URI is null 
	 * 
	 * @spec Uri.toPath(String)
	 */
	private void testToPath001() {

		try {
			DefaultTestBundleControl.log("#testToPath001");
			Uri.toPath(null);
			DefaultTestBundleControl.failException("", NullPointerException.class);
		} catch (NullPointerException e) {
			DefaultTestBundleControl.pass("NullPointerException correctly thrown");
		} catch (Exception e) {
			tbc.failExpectedOtherException(NullPointerException.class,e);
		}
	}
		
	/**
	 * Asserts that Uri.toPath(String) splits the specified URI along the path separator 
	 * '/' charaters and return an array of URI segments 
	 * 
	 * @spec Uri.toPath(String)
	 */
	private void testToPath002() {

		try {
			DefaultTestBundleControl.log("#testToPath002");
			String[] array = Uri.toPath("./Test/Dmt");
			TestCase.assertTrue("Asserts that Uri.toPath(String) splits the specified URI " +
					"along the path separator '/' charaters and return an array of URI segments.",array.length==3 
					&& array[0].equals(".")&& array[1].equals("Test")&& array[2].equals("Dmt"));
			
		} catch (Exception e) {
			tbc.failUnexpectedException(e);
		}
	}
	
	/**
	 * Asserts that IllegalArgumentException is thrown if the specified URI is malformed 
	 * 
	 * @spec Uri.toPath(String)
	 */

	private void testToPath003() {

		try {
			DefaultTestBundleControl.log("#testToPath003");
            //It is from 1 because 0 is 'null' and null throws NullPointerException in this method
			for (int i = 1; i < DmtTestControl.INVALID_URIS.length; i++) {
				String uri = null;
				try {
					uri = DmtTestControl.INVALID_URIS[i].toString();
					Uri.toPath(uri);
					DefaultTestBundleControl.failException("", IllegalArgumentException.class);
				} catch (IllegalArgumentException e) {
					DefaultTestBundleControl.pass("IllegalArgumentException correctly thrown when calling Uri.toPath(" + uri + ")");
				}
			}
		} catch (Exception e) {
			tbc.failExpectedOtherException(IllegalArgumentException.class,e);
		}
	}
	
}


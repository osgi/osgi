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
 * Jan 31, 2005  Andre Assad
 * 1             Implement MEG TCK
 * ============  ==============================================================
 * Feb 11, 2005  Andre Assad
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-001)
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc1.tbc.Acl;

import org.osgi.service.dmt.Acl;

import org.osgi.test.cases.dmt.tc1.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;

/**
 * @author Andre Assad
 * 
 * This test case validates the implementation of <code>isPermitted<code> method of Acl, 
 * according to MEG specification
 */
public class IsPermitted extends DmtTestControl {
	private static final String ACL_DEFAULT = "Add=" + DmtConstants.PRINCIPAL
			+ "&Delete=" + DmtConstants.PRINCIPAL + "&Get=*";

	/**
	 * This method tests if a given permission is granted to a principal.
	 * 
	 * @spec Acl.isPermitted(String,int)
	 */
	public void testIsPermitted001() {
		try {
			log("#testIsPermitted001");
			Acl acl = new Acl(ACL_DEFAULT);
			assertTrue(
					"Asserting that isPermitted returns true only for permissions granted",
					acl.isPermitted(
					DmtConstants.PRINCIPAL, Acl.GET | Acl.ADD | Acl.DELETE));
			assertTrue(
					"Asserting that isPermitted returns false for permissions not granted",
					!acl.isPermitted(
					DmtConstants.PRINCIPAL, Acl.EXEC | Acl.REPLACE));			
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * Asserts that an IllegalArgumentException is thrown whenever an invalid 
	 * permission code is passed to isPermitted(String principal, int permissions).
	 * 
	 * @spec Acl.isPermitted(String,int)
	 */
	public void testIsPermitted002() {
		try {
			log("#testIsPermitted002");
			Acl acl = new Acl(ACL_DEFAULT);
			acl.isPermitted(DmtConstants.PRINCIPAL, 2005);

			failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			pass("IllegalArgumentException correctly thrown on isPermitted method");
		} catch (Exception e) {
			failExpectedOtherException(IllegalArgumentException.class, e);
		}

	}

	/**
	 * Asserts that an IllegalArgumentException is thrown whenever a valid permission code 
	 * and an invalid principal is passed isPermitted(String principal, int permissions).
	 * 
	 * @spec Acl.isPermitted(String,int)
	 */
	public void testIsPermitted003() {
		try {
			log("#testIsPermitted003");
			Acl acl = new Acl(ACL_DEFAULT);
			
			acl.isPermitted(DmtConstants.INVALID, Acl.DELETE | Acl.GET);
			failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			pass("IllegalArgumentException correctly thrown on isPermitted method");
		} catch (Exception e) {
			failExpectedOtherException(IllegalArgumentException.class, e);
		}
	}

	/**
	 * Asserts global permissions
	 * 
	 * @spec Acl.isPermitted(String,int)
	 */
	public void testIsPermitted004() {
		try {
			log("#testIsPermitted004");
			Acl acl = new Acl(ACL_DEFAULT);
			
			assertTrue(
					"Asserting that '*' returns true if the permission is global",
					acl.isPermitted("*", Acl.GET));
			assertTrue(
					"Asserting that '*' returns false if the permission is not global",
					!acl.isPermitted("*", Acl.ADD | Acl.DELETE | Acl.EXEC
							| Acl.REPLACE));
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}
}

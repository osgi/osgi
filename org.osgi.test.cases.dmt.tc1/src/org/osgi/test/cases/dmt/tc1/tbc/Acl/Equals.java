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
 * May 25, 2005  Luiz Felipe Guimaraes
 * 1             Implement MEG TCK
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc1.tbc.Acl;

import org.osgi.service.dmt.Acl;

import org.osgi.test.cases.dmt.tc1.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;
/**
 * @author Luiz Felipe Guimaraes
 * 
 * This test case validates the implementation of <code>equals</code> method of Acl, 
 * according to MEG specification
 */
public class Equals extends DmtTestControl {
	
	/**
	 * Asserts that two Acl are equal when they have the same set of principals and permissions
	 * 
	 * @spec Acl.equals(Object)
	 */
	public void testEquals001() {
		try {
			log("#testEquals001");
			String[] principals = { DmtConstants.PRINCIPAL, DmtConstants.PRINCIPAL_2 } ;
			int[] permissions = { Acl.ADD, Acl.GET };
			Acl Acl = new Acl(principals,permissions);
			Acl Acl2 = new Acl(principals,permissions);

			assertTrue(
					"Asserts that two Acl are equal when they have the same set of principals and permissions",
					Acl.equals(Acl2));
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * Asserts that two Acl are different when they have the same set of permissions but different principals
	 * 
	 * @spec Acl.equals(Object)
	 */
	public void testEquals002() {
		try {
			log("#testEquals002");
			String[] principals = { DmtConstants.PRINCIPAL, DmtConstants.PRINCIPAL_2 } ;
			String[] principals2 = { DmtConstants.PRINCIPAL, DmtConstants.PRINCIPAL_3 } ;
			int[] permissionsBoth = { Acl.ADD, Acl.GET };
			Acl Acl = new Acl(principals,permissionsBoth);
			Acl Acl2 = new Acl(principals2,permissionsBoth);
			assertTrue(
					"Asserts that two Acl are different when they have the same set of permissions but different principals",
					!Acl.equals(Acl2));
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}	
	/**
	 * Asserts that two Acl are different when they have the same set of principals but different permissions
	 * 
	 * @spec Acl.equals(Object)
	 */
	public void testEquals003() {
		try {
			log("#testEquals003");
			String[] principalsBoth = { DmtConstants.PRINCIPAL, DmtConstants.PRINCIPAL_2 } ;
			int[] permissions = { Acl.ADD, Acl.GET };
			int[] permissions2 = { Acl.GET, Acl.ADD };
			Acl Acl = new Acl(principalsBoth,permissions);
			Acl Acl2 = new Acl(principalsBoth,permissions2);
			assertTrue(
					"Asserts that two Acl are different when they have the same set of principals but different permissions",
					!Acl.equals(Acl2));
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

}

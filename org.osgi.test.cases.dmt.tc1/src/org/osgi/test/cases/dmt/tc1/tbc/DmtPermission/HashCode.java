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
 * Abr 04, 2005  Luiz Felipe Guimaraes
 * 34            [MEGTCK][DMT] CVS update 
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc1.tbc.DmtPermission;

import org.osgi.service.dmt.security.DmtPermission;

import org.osgi.test.cases.dmt.tc1.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc1.tbc.DmtTestControl;

/**
 * This test case validates the implementation of <code>hashCode<code> method of DmtPermission, 
 * according to MEG specification
 */
public class HashCode extends DmtTestControl {
	/**
	 * Asserts that two objects initialized with the same dmtUri and actions have the same hashcode
	 * 
	 * @spec DmtPermission.hashCode()
	 */
	public void testHashCode001() {
		try {
			log("#testHashCode001");
			org.osgi.service.dmt.security.DmtPermission permission = new org.osgi.service.dmt.security.DmtPermission(
					DmtConstants.OSGi_LOG, DmtConstants.ACTIONS);
			assertEquals(
							"Asserting that two objects initialized with the same dmtUri and actions have the same hashcode",
							new org.osgi.service.dmt.security.DmtPermission(
									DmtConstants.OSGi_LOG,
									DmtConstants.ACTIONS).hashCode(),
							permission.hashCode());
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

	/**
	 * Asserts that two objects initialized with the same dmtUri and the same set of actions (in a different
	 * order) have the same hashcode using the constructor with two parameters
	 * 
	 * @spec DmtPermission.hashCode()
	 */
	public void testHashCode002() {
		try {
			log("#testHashCode002");
			String actions = DmtPermission.ADD + "," + DmtPermission.DELETE
					+ "," + DmtPermission.EXEC;
			String actionsDifferentOrder = DmtPermission.DELETE + ","
					+ DmtPermission.EXEC + "," + DmtPermission.ADD;
			org.osgi.service.dmt.security.DmtPermission permission = new org.osgi.service.dmt.security.DmtPermission(
					DmtConstants.OSGi_LOG, actions);
			assertEquals(
							"Asserting that two objects initialized with the same dmtUri and actions are equal",
							permission.hashCode(),
							(new org.osgi.service.dmt.security.DmtPermission(
									DmtConstants.OSGi_LOG,
									actionsDifferentOrder)).hashCode());
		} catch (Exception e) {
			failUnexpectedException(e);
		}
	}

}

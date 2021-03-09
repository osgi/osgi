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
 *
 * This test case validates the implementation of <code>equals</code> method of DmtPermission, 
 * according to MEG specification
 */
public class Equals extends DmtTestControl {
	/**
	 * Asserts that two objects initialized with the same dmtUri and actions are equal
	 * 
	 * @spec DmtPermission.equals(Object)
	 */
	public void testEquals001() {
		try {
			log("#testEquals001");
			assertTrue(
					"Asserting that two objects initialized with the same dmtUri and actions are equal", 
					new org.osgi.service.dmt.security.DmtPermission(DmtConstants.OSGi_LOG,DmtConstants.ACTIONS)
					.equals(new org.osgi.service.dmt.security.DmtPermission(DmtConstants.OSGi_LOG,DmtConstants.ACTIONS)));
		} catch (Exception e) { 
			failUnexpectedException(e);
		}
	}
	
	/**
	 * Asserts that two objects initialized with the same dmtUri but different actions are different
	 * 
	 * @spec DmtPermission.equals(Object)
	 */
	public void testEquals002() {
		try {
			log("#testEquals002");
			assertTrue(
					"Asserting that two objects initialized with the same dmtUri but different actions are different", 
					!new org.osgi.service.dmt.security.DmtPermission(DmtConstants.OSGi_LOG,DmtConstants.ACTIONS)
					.equals(new org.osgi.service.dmt.security.DmtPermission(DmtConstants.OSGi_LOG,DmtConstants.DIFFERENT_ACTIONS)));
		} catch (Exception e) { 
			failUnexpectedException(e);
		}
	}
	
	/**
	 * Asserts that two objects initialized with the same actions but different dmtUri are different
	 * 
	 * @spec DmtPermission.equals(Object)
	 */
	public void testEquals003() {
		try {
			log("#testEquals003");
			assertTrue(
					"Asserting that two objects initialized with the same actions but different dmtUri are different", 
					!new org.osgi.service.dmt.security.DmtPermission(DmtConstants.OSGi_LOG,DmtConstants.ACTIONS)
					.equals(new org.osgi.service.dmt.security.DmtPermission(DmtConstants.OSGi_CONFIGURATION,DmtConstants.ACTIONS)));
		} catch (Exception e) { 
			failUnexpectedException(e);
		}
	}
	/**
	 * Asserts that two objects initialized with the same dmtUri and the same set of actions 
	 * (in a different order) are equal
	 * 
	 * @spec DmtPermission.equals(Object)
	 */
	public void testEquals004() {
		try {
			log("#testEquals004");
			String actions = DmtPermission.ADD + "," + DmtPermission.DELETE + "," +DmtPermission.EXEC;
			String actionsDifferentOrder = DmtPermission.DELETE + "," +DmtPermission.EXEC+"," +DmtPermission.ADD;
			assertTrue(
					"Asserting that two objects initialized with the same dmtUri and actions are equal", 
					new org.osgi.service.dmt.security.DmtPermission(DmtConstants.OSGi_LOG,actions)
					.equals(new org.osgi.service.dmt.security.DmtPermission(DmtConstants.OSGi_LOG,actionsDifferentOrder)));
		} catch (Exception e) { 
			failUnexpectedException(e);
		}
	}	
	
	/**
	 * Asserts that the "*" action mask is considered equal to a mask containing all actions.
	 * 
	 * @spec DmtPermission.equals(Object)
	 */
	public void testEquals005() {
		try {
			log("#testEquals005");
			assertTrue(
					"Asserts that the \"*\" action mask is considered equal to a mask containing all actions.", 
					new org.osgi.service.dmt.security.DmtPermission(DmtConstants.OSGi_LOG,"*")
					.equals(new org.osgi.service.dmt.security.DmtPermission(DmtConstants.OSGi_LOG,DmtConstants.ALL_ACTIONS)));
		} catch (Exception e) { 
			failUnexpectedException(e);
		}
	}
}

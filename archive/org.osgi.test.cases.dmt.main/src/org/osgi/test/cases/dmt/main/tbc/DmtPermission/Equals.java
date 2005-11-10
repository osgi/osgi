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
 * Abr 04, 2005  Luiz Felipe Guimaraes
 * 34            [MEGTCK][DMT] CVS update 
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.main.tbc.DmtPermission;

import org.osgi.service.dmt.security.DmtPermission;
import org.osgi.test.cases.dmt.main.tbc.DmtConstants;
import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.main.tbc.Plugin.ExecPlugin.TestExecPluginActivator;

/**
 *
 * This test case validates the implementation of <code>equals</code> method of DmtPermission, 
 * according to MEG specification
 */
public class Equals {
	private DmtTestControl tbc;

	public Equals(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testEquals001();
		testEquals002();
		testEquals003();
		testEquals004();
		testEquals005();
	}

	/**
	 * Asserts that two objects initialized with the same dmtUri and actions are equal
	 * 
	 * @spec DmtPermission.equals(Object)
	 */
	private void testEquals001() {
		try {
			tbc.log("#testEquals001");
			tbc.assertTrue("Asserting that two objects initialized with the same dmtUri and actions are equal", 
					new org.osgi.service.dmt.security.DmtPermission(DmtConstants.OSGi_LOG,DmtConstants.ACTIONS)
					.equals(new org.osgi.service.dmt.security.DmtPermission(DmtConstants.OSGi_LOG,DmtConstants.ACTIONS)));
		} catch (Exception e) { 
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}
	
	/**
	 * Asserts that two objects initialized with the same dmtUri but different actions are different
	 * 
	 * @spec DmtPermission.equals(Object)
	 */
	private void testEquals002() {
		try {
			tbc.log("#testEquals002");
			tbc.assertTrue("Asserting that two objects initialized with the same dmtUri but different actions are different", 
					!new org.osgi.service.dmt.security.DmtPermission(DmtConstants.OSGi_LOG,DmtConstants.ACTIONS)
					.equals(new org.osgi.service.dmt.security.DmtPermission(DmtConstants.OSGi_LOG,DmtConstants.DIFFERENT_ACTIONS)));
		} catch (Exception e) { 
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}
	
	/**
	 * Asserts that two objects initialized with the same actions but different dmtUri are different
	 * 
	 * @spec DmtPermission.equals(Object)
	 */
	private void testEquals003() {
		try {
			tbc.log("#testEquals003");
			tbc.assertTrue("Asserting that two objects initialized with the same actions but different dmtUri are different", 
					!new org.osgi.service.dmt.security.DmtPermission(DmtConstants.OSGi_LOG,DmtConstants.ACTIONS)
					.equals(new org.osgi.service.dmt.security.DmtPermission(TestExecPluginActivator.ROOT,DmtConstants.ACTIONS)));
		} catch (Exception e) { 
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}
	/**
	 * Asserts that two objects initialized with the same dmtUri and the same set of actions 
	 * (in a different order) are equal
	 * 
	 * @spec DmtPermission.equals(Object)
	 */
	private void testEquals004() {
		try {
			tbc.log("#testEquals004");
			String actions = DmtPermission.ADD + "," + DmtPermission.DELETE + "," +DmtPermission.EXEC;
			String actionsDifferentOrder = DmtPermission.DELETE + "," +DmtPermission.EXEC+"," +DmtPermission.ADD;
			tbc.assertTrue("Asserting that two objects initialized with the same dmtUri and actions are equal", 
					new org.osgi.service.dmt.security.DmtPermission(DmtConstants.OSGi_LOG,actions)
					.equals(new org.osgi.service.dmt.security.DmtPermission(DmtConstants.OSGi_LOG,actionsDifferentOrder)));
		} catch (Exception e) { 
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}	
	
	/**
	 * Asserts that the "*" action mask is considered equal to a mask containing all actions.
	 * 
	 * @spec DmtPermission.equals(Object)
	 */
	private void testEquals005() {
		try {
			tbc.log("#testEquals005");
			tbc.assertTrue("Asserts that the \"*\" action mask is considered equal to a mask containing all actions.", 
					new org.osgi.service.dmt.security.DmtPermission(DmtConstants.OSGi_LOG,"*")
					.equals(new org.osgi.service.dmt.security.DmtPermission(DmtConstants.OSGi_LOG,DmtConstants.ALL_ACTIONS)));
		} catch (Exception e) { 
			tbc.fail("Unexpected exception: " + e.getClass().getName());
		}
	}
}

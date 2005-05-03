/*
 * Copyright (c) The Open Services Gateway Initiative (2004).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

/*
 * REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * Mar 9, 2005  Alexandre Santos
 * 14           Implement MEG TCK
 * ===========  ==============================================================
 */
package org.osgi.test.cases.monitor.tbc.MonitorPermission;

import java.security.AllPermission;

import org.osgi.service.monitor.MonitorPermission;
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @methodUnderTest org.osgi.test.cases.monitor.tbc.MonitorPermission#implies
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>Implies<code> method, according to MEG reference
 *                     documentation.
 */
public class Implies {

	private MonitorTestControl tbc;

	/**
	 * @param tbc
	 */
	public Implies(MonitorTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testImplies001();
		testImplies002();
		testImplies003();
		testImplies004();
		testImplies005();
	}

	/**
	 * @testID testImplies001
	 * @testDescription Asserts if a MonitorPermission doesn't implies a
	 *                  AllPermission
	 */
	public void testImplies001() {
		try {
			MonitorPermission mp1 = new MonitorPermission("*/*",
					org.osgi.service.monitor.MonitorPermission.READ);
			AllPermission all = new AllPermission();

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "a MonitorPermission does not implies a AllPermission" }),
							!mp1.implies(all));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testImplies002
	 * @testDescription Asserts if a MonitorPermission with less actions then it
	 *                  is implied by it.
	 */
	public void testImplies002() {
		try {
			MonitorPermission mp1 = new MonitorPermission(
					"*/*",
					org.osgi.service.monitor.MonitorPermission.READ
							+ ","
							+ org.osgi.service.monitor.MonitorPermission.RESET
							+ ","
							+ org.osgi.service.monitor.MonitorPermission.DISCOVER);
			MonitorPermission mp2 = new MonitorPermission("*/*",
					org.osgi.service.monitor.MonitorPermission.READ + ","
							+ org.osgi.service.monitor.MonitorPermission.RESET);
			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "a MonitorPermission with less actions then it is implied by it" }),
							mp1.implies(mp2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testImplies003
	 * @testDescription Asserts if a MonitorPermission implies another
	 *                  MonitorPermission with same target and actions.
	 */
	public void testImplies003() {
		try {
			MonitorPermission mp1 = new MonitorPermission(
					"*/*",
					org.osgi.service.monitor.MonitorPermission.READ
							+ ","
							+ org.osgi.service.monitor.MonitorPermission.RESET
							+ ","
							+ org.osgi.service.monitor.MonitorPermission.DISCOVER);
			MonitorPermission mp2 = new MonitorPermission(
					"*/*",
					org.osgi.service.monitor.MonitorPermission.READ
							+ ","
							+ org.osgi.service.monitor.MonitorPermission.RESET
							+ ","
							+ org.osgi.service.monitor.MonitorPermission.DISCOVER);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "a MonitorPermission implies another MonitorPermission with same target and actions" }),
							mp1.implies(mp2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testImplies004
	 * @testDescription Asserts if a MonitorPermission doesn't implies another
	 *                  MonitorPermission with more actions then it.
	 */
	public void testImplies004() {
		try {
			MonitorPermission mp1 = new MonitorPermission("*/*",
					org.osgi.service.monitor.MonitorPermission.STARTJOB + ","
							+ org.osgi.service.monitor.MonitorPermission.RESET);
			MonitorPermission mp2 = new MonitorPermission(
					"*/*",
					org.osgi.service.monitor.MonitorPermission.STARTJOB
							+ ","
							+ org.osgi.service.monitor.MonitorPermission.RESET
							+ ","
							+ org.osgi.service.monitor.MonitorPermission.SWITCHEVENTS);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "a MonitorPermission implies another MonitorPermission with more actions then it" }),
							!mp1.implies(mp2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testImplies005
	 * @testDescription Asserts if a MonitorPermission implies another
	 *                  MonitorPermission with different targets.
	 */
	public void testImplies005() {
		try {
			MonitorPermission mp1 = new MonitorPermission(
					"com.mycomp.myapp/*",
					org.osgi.service.monitor.MonitorPermission.READ
							+ ","
							+ org.osgi.service.monitor.MonitorPermission.RESET
							+ ","
							+ org.osgi.service.monitor.MonitorPermission.DISCOVER);
			MonitorPermission mp2 = new MonitorPermission(
					"co.myco.myap/*",
					org.osgi.service.monitor.MonitorPermission.READ
							+ ","
							+ org.osgi.service.monitor.MonitorPermission.RESET
							+ ","
							+ org.osgi.service.monitor.MonitorPermission.DISCOVER);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "a MonitorPermission implies another MonitorPermission with different targets" }),
							!mp1.implies(mp2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}
}

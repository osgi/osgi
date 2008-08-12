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
 * @author Leonardo Barros
 * 
 * This test class validates the implementation of
 * <code>implies</code> method, according to MEG reference
 * documentation.
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
		testImplies006();
		testImplies007();
	}

	/**
	 * This method asserts if a MonitorPermission does not implies an
	 * AllPermission
	 * 
	 * @spec MonitorPermission.implies(Object)
	 */
	private void testImplies001() {
		try {
			tbc.log("#testImplies001");
			MonitorPermission mp1 = new MonitorPermission("*/*",
					org.osgi.service.monitor.MonitorPermission.READ);
			AllPermission all = new AllPermission();

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "a MonitorPermission does not implies an AllPermission" }),
							!mp1.implies(all));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if implies method returns true when a
	 * MonitorPermission has a lower set of actions allowed than this one
	 * 
	 * @spec MonitorPermission.implies(Object)
	 */
	private void testImplies002() {
		try {
			tbc.log("#testImplies002");
			MonitorPermission mp1 = new MonitorPermission(
					"*/*",
					org.osgi.service.monitor.MonitorPermission.READ
							+ ","
							+ org.osgi.service.monitor.MonitorPermission.RESET
							+ ","
							+ org.osgi.service.monitor.MonitorPermission.STARTJOB);
			MonitorPermission mp2 = new MonitorPermission("*/*",
					org.osgi.service.monitor.MonitorPermission.READ + ","
							+ org.osgi.service.monitor.MonitorPermission.RESET);
			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "implies method returns true when a MonitorPermission has a lower set of actions allowed than this one" }),
							mp1.implies(mp2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if a MonitorPermission implies another
	 * MonitorPermission with same target and actions
	 * 
	 * @spec MonitorPermission.implies(Object)
	 */
	private void testImplies003() {
		try {
			tbc.log("#testImplies003");
			MonitorPermission mp1 = new MonitorPermission("*/*",
					org.osgi.service.monitor.MonitorPermission.READ + ","
							+ org.osgi.service.monitor.MonitorPermission.RESET);
			MonitorPermission mp2 = new MonitorPermission("*/*",
					org.osgi.service.monitor.MonitorPermission.READ + ","
							+ org.osgi.service.monitor.MonitorPermission.RESET);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "a MonitorPermission implies another	MonitorPermission with same target and actions" }),
							mp1.implies(mp2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if implies method returns false when a
	 * MonitorPermission has a broader set of actions allowed than this one
	 * 
	 * @spec MonitorPermission.implies(Object)
	 */
	private void testImplies004() {
		try {
			tbc.log("#testImplies004");
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
											new String[] { "implies method returns false when a MonitorPermission has a broader set of actions allowed than this one" }),
							!mp1.implies(mp2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if a MonitorPermission does not imply another
	 * MonitorPermission with different targets
	 * 
	 * @spec MonitorPermission.implies(Object)
	 */
	private void testImplies005() {
		try {
			tbc.log("#testImplies005");
			MonitorPermission mp1 = new MonitorPermission("com.mycomp.myapp/*",
					org.osgi.service.monitor.MonitorPermission.READ + ","
							+ org.osgi.service.monitor.MonitorPermission.RESET);
			MonitorPermission mp2 = new MonitorPermission("co.myco.myap/*",
					org.osgi.service.monitor.MonitorPermission.READ + ","
							+ org.osgi.service.monitor.MonitorPermission.RESET);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "a MonitorPermission does not imply another MonitorPermission with different targets" }),
							!mp1.implies(mp2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if a MonitorPermission implies another
	 * MonitorPermission with a broader minimal sampling interval
	 * 
	 * @spec MonitorPermission.implies(Object)
	 */
	private void testImplies006() {
		try {
			tbc.log("#testImplies006");
			MonitorPermission mp1 = new MonitorPermission("*/*",
					org.osgi.service.monitor.MonitorPermission.STARTJOB + ":2");
			MonitorPermission mp2 = new MonitorPermission("*/*",
					org.osgi.service.monitor.MonitorPermission.STARTJOB + ":4");

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "a MonitorPermission implies another MonitorPermission with a broader minimal sampling interval" }),
							mp1.implies(mp2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if a MonitorPermission does not imply another
	 * MonitorPermission with a lower minimal sampling interval
	 * 
	 * @spec MonitorPermission.implies(Object)
	 */
	private void testImplies007() {
		try {
			tbc.log("#testImplies007");
			MonitorPermission mp1 = new MonitorPermission("*/*",
					org.osgi.service.monitor.MonitorPermission.STARTJOB + ":4");
			MonitorPermission mp2 = new MonitorPermission("*/*",
					org.osgi.service.monitor.MonitorPermission.STARTJOB + ":2");

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "a MonitorPermission does not imply another MonitorPermission with a lower minimal sampling interval" }),
							!mp1.implies(mp2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}
}

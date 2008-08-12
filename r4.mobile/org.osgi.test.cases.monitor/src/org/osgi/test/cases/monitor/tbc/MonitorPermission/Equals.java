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
 * 
 */

/*
 * REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 21/02/2005   Alexandre Alves
 * 14           Implement MEG TCK
 * ===========  ==============================================================
 */
package org.osgi.test.cases.monitor.tbc.MonitorPermission;

import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;
import org.osgi.service.monitor.MonitorPermission;

/**
 * @author Leonardo Barros
 * 
 * This test class validates the implementation of
 * <code>equals</code> method, according to MEG reference
 * documentation.
 */

public class Equals {
	private MonitorTestControl tbc;

	public Equals(MonitorTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testEquals001();
		testEquals002();
		testEquals003();
		testEquals004();
		testEquals005();
		testEquals006();
	}

	/**
	 * This method asserts if equals method returns true when comparing two
	 * MonitorPermission objects which are initialized with same target and
	 * action strings
	 * 
	 * @spec MonitorPermission.equals(Object)
	 */
	private void testEquals001() {
		try {
			tbc.log("#testEquals001");
			MonitorPermission mp1 = new MonitorPermission("*/*",
					org.osgi.service.monitor.MonitorPermission.READ);
			MonitorPermission mp2 = new MonitorPermission("*/*",
					org.osgi.service.monitor.MonitorPermission.READ);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "equals method returns true when comparing two MonitorPermission objects which are initialized with same target and action strings" }),
							mp1.equals(mp2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if equals method returns true when comparing two
	 * MonitorPermission objects which are initialized with same target and
	 * action strings
	 * 
	 * @spec MonitorPermission.equals(Object)
	 */
	private void testEquals002() {
		try {
			tbc.log("#testEquals002");
			MonitorPermission mp1 = new MonitorPermission(
					"com.mycomp.myapp/queue_length",
					org.osgi.service.monitor.MonitorPermission.READ + ","
							+ org.osgi.service.monitor.MonitorPermission.RESET);
			MonitorPermission mp2 = new MonitorPermission(
					"com.mycomp.myapp/queue_length",
					org.osgi.service.monitor.MonitorPermission.READ + ","
							+ org.osgi.service.monitor.MonitorPermission.RESET);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "equals method returns true when comparing two MonitorPermission objects which are initialized with same target and action strings" }),
							mp1.equals(mp2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if equals method returns false when comparing two
	 * MonitorPermission objects which are initialized with different target
	 * string
	 * 
	 * @spec MonitorPermission.equals(Object)
	 */
	private void testEquals003() {
		try {
			tbc.log("#testEquals003");
			MonitorPermission mp1 = new MonitorPermission("*/*",
					org.osgi.service.monitor.MonitorPermission.READ);
			MonitorPermission mp2 = new MonitorPermission(
					"com.mycomp.myapp/queue_length",
					org.osgi.service.monitor.MonitorPermission.READ);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "equals method returns false when comparing two MonitorPermission objects which are initialized with different target string" }),
							!mp1.equals(mp2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if equals method returns false when comparing two
	 * MonitorPermission objects which are initialized with different action
	 * string
	 * 
	 * @spec MonitorPermission.equals(Object)
	 */
	private void testEquals004() {
		try {
			tbc.log("#testEquals004");
			MonitorPermission mp1 = new MonitorPermission("*/*",
					org.osgi.service.monitor.MonitorPermission.READ);
			MonitorPermission mp2 = new MonitorPermission("*/*",
					org.osgi.service.monitor.MonitorPermission.RESET);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "equals method returns false when comparing two MonitorPermission objects which are initialized with different action string" }),
							!mp1.equals(mp2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if equals method returns false when comparing two
	 * MonitorPermission objects which are initialized with different target and
	 * action strings
	 * 
	 * @spec MonitorPermission.equals(Object)
	 */
	private void testEquals005() {
		try {
			tbc.log("#testEquals005");
			MonitorPermission mp1 = new MonitorPermission(
					"*/*",
					org.osgi.service.monitor.MonitorPermission.READ
							+ ","
							+ org.osgi.service.monitor.MonitorPermission.STARTJOB);
			MonitorPermission mp2 = new MonitorPermission(
					"com.mycomp.myapp/queue_length",
					org.osgi.service.monitor.MonitorPermission.READ
							+ ","
							+ org.osgi.service.monitor.MonitorPermission.RESET
							+ ","
							+ org.osgi.service.monitor.MonitorPermission.STARTJOB);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "equals method returns false when comparing two MonitorPermission objects which are initialized with different target and action strings" }),
							!mp1.equals(mp2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if equals method returns true when comparing two
	 * MonitorPermission objects which are initialized with same actions but in
	 * different order
	 * 
	 * @spec MonitorPermission.equals(Object)
	 */
	private void testEquals006() {
		try {
			tbc.log("#testEquals006");
			MonitorPermission mp1 = new MonitorPermission(
					"com.mycomp.myapp/queue_length",
					org.osgi.service.monitor.MonitorPermission.READ + ","
							+ org.osgi.service.monitor.MonitorPermission.RESET);
			MonitorPermission mp2 = new MonitorPermission(
					"com.mycomp.myapp/queue_length",
					org.osgi.service.monitor.MonitorPermission.RESET + ","
							+ org.osgi.service.monitor.MonitorPermission.READ);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "equals method returns true when comparing two MonitorPermission objects which are initialized with same actions but in different order" }),
							mp1.equals(mp2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

}

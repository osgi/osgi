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

import org.osgi.service.monitor.MonitorPermission;
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @methodUnderTest org.osgi.service.monitor.MonitorPermission#hashCode
 * @generalDescription This class tests equals method according with MEG
 *                     specification (rfc0084)
 */

public class HashCode {

	private MonitorTestControl tbc;

	public HashCode(MonitorTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testHashCode001();
		testHashCode002();
		testHashCode003();
		testHashCode004();
		testHashCode005();
		testHashCode006();
	}

	/**
	 * @testID testHashCode001
	 * @testDescription Tests if the hashCode of two MonitorPermission objects
	 *                  are equal when equals method return true
	 */
	public void testHashCode001() {
		try {
			MonitorPermission mp1 = new MonitorPermission("*/*",
					org.osgi.service.monitor.MonitorPermission.READ);
			MonitorPermission mp2 = new MonitorPermission("*/*",
					org.osgi.service.monitor.MonitorPermission.READ);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "the hashCode of two MonitorPermission objects are equal when equals method returns true" }),
							mp1.hashCode() == mp2.hashCode());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testHashCode002
	 * @testDescription Tests if the hashCode of two MonitorPermission objects
	 *                  are equal when equals method return true
	 */
	public void testHashCode002() {
		try {
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
											new String[] { "the hashCode of two MonitorPermission objects are equal when equals method returns true" }),
							mp1.hashCode() == mp2.hashCode());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testHashCode003
	 * @testDescription Tests if the hashCode of two MonitorPermission objects
	 *                  are different when equals method return false
	 */
	public void testHashCode003() {
		try {
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
											new String[] { "the hashCode of two MonitorPermission objects are different when equals method returns false" }),
							!(mp1.hashCode() == mp2.hashCode()));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testHashCode004
	 * @testDescription Tests if the hashCode of two MonitorPermission objects
	 *                  are different when equals method return false
	 */
	public void testHashCode004() {
		try {
			MonitorPermission mp1 = new MonitorPermission("*/*",
					org.osgi.service.monitor.MonitorPermission.READ);
			MonitorPermission mp2 = new MonitorPermission("*/*",
					org.osgi.service.monitor.MonitorPermission.RESET);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "the hashCode of two MonitorPermission objects are different when equals method returns false" }),
							!(mp1.hashCode() == mp2.hashCode()));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testHashCode005
	 * @testDescription Tests if the hashCode of two MonitorPermission objects
	 *                  are different when equals method return false
	 */
	public void testHashCode005() {
		try {
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
											new String[] { "the hashCode of two MonitorPermission objects are different when equals method returns false" }),
							!(mp1.hashCode() == mp2.hashCode()));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testHashCode006
	 * @testDescription Tests if the hashCode of two MonitorPermission objects
	 *                  are equal when equals method return true
	 */
	public void testHashCode006() {
		try {
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
											new String[] { "the hashCode of two MonitorPermission objects are equal when equals method return true" }),
							mp1.hashCode() == mp2.hashCode());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

}

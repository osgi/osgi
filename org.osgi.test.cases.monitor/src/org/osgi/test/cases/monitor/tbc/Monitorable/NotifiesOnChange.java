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
 * Feb 24, 2005	Alexandre Santos
 * 14           Implement MEG TCK
 * ===========  ==============================================================
 */
package org.osgi.test.cases.monitor.tbc.Monitorable;

import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @methodUnderTest org.osgi.service.monitor#NotifiesOnChange
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>NotifiesOnChange<code> method, according to MEG reference
 *                     documentation.
 */
public class NotifiesOnChange {
	private MonitorTestControl tbc;

	/**
	 * @param tbc
	 */
	public NotifiesOnChange(MonitorTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testNotifiesOnChange001();
		testNotifiesOnChange002();
	}

	/**
	 * @testID testNotifiesOnChange001
	 * @testDescription This method asserts if ours monitorable throws throws a
	 *                  IllegalArgumentException when use a invalid monitorable.
	 */
	public void testNotifiesOnChange001() {
		try {
			tbc.log("#testNotifiesOnChange001");
			tbc.getMonitorable().notifiesOnChange(
					MonitorTestControl.INVALID_MONITORABLE_SV);
			tbc.failException("", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		}
	}


	/**
	 * @testID testNotifiesOnChange002
	 * @testDescription This method asserts if ours monitorable returns true
	 * 					when use a valid id.
	 */
	public void testNotifiesOnChange002() {
		try {
			tbc.log("#testNotifiesOnChange002");
			tbc
					.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE, new String[] { "ours monitorable returns true when use a valid id." }),
							tbc.getMonitorable().notifiesOnChange(
									MonitorTestControl.SV_NAME1));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}
}

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
 * Mar 10, 2005 Alexandre Santos
 * 14           Implement MEG TCK
 * ===========  ==============================================================
 */
package org.osgi.test.cases.monitor.tbc.Monitorable;

import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @methodUnderTest org.osgi.test.cases.monitor.tbc.Monitorable#getDescription
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>getDescription<code> method, according to MEG reference
 *                     documentation.
 */
public class GetDescription {
	private MonitorTestControl tbc;

	/**
	 * @param tbc
	 */
	public GetDescription(MonitorTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetDescription001();
		testGetDescription002();
	}

	/**
	 * @testID testGetDescription001
	 * @testDescription This method asserts that an invalid statusvariable
	 *                  throws a IllegalArgumentException
	 */
	public void testGetDescription001() {
		try {
			tbc.log("#testGetDescription001");
			tbc.getMonitorable().getDescription(
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
	 * @testID testGetDescription002
	 * @testDescription This method asserts that the correct implementation of
	 *                  Monitorable is called.
	 */
	public void testGetDescription002() {
		try {
			tbc.log("#testGetDescription002");
			tbc.assertNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NULL, new String[] { "getDescription returned value" }), tbc
					.getMonitorable().getDescription(
							MonitorTestControl.SV_NAME1));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}

}

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
 * 05/05/2005   Leonardo Barros
 * 38           Implement MEGTCK for the application RFC 
 * ===========  ==============================================================
 */
package org.osgi.test.cases.application.tbc.SingletonException;

import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;

/**
 * @methodUnderTest org.osgi.service.application.SingletonException#SingletonException
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>SingletonException<code> method, according to MEG reference
 *                     documentation.
 */
public class SingletonException {
	private ApplicationTestControl tbc;

	public SingletonException(ApplicationTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testSingletonException001();
		testSingletonException002();
	}

	/**
	 * @testID testSingletonException001
	 * @testDescription Asserts if a null message is returned when
	 *                  SingletonException is created with default constructor
	 */
	public void testSingletonException001() {
		try {
			tbc.log("#testSingletonException001");
			org.osgi.service.application.SingletonException se = new org.osgi.service.application.SingletonException();

			tbc
					.assertNull(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_NULL,
											new String[] { "the message returned when SingletonException is created with default constructor" }),
							se.getMessage());

		} catch (Exception e) {
			tbc.fail(e.getClass().getName()+ " incorrectly thrown.");
		}
	}

	/**
	 * @testID testSingletonException002
	 * @testDescription Asserts if the message returned is equals to the message
	 *                  passed as parameter to SingletonException constructor
	 */
	public void testSingletonException002() {
		try {
			tbc.log("#testSingletonException002");
			org.osgi.service.application.SingletonException se = new org.osgi.service.application.SingletonException(
					"test");

			tbc
					.assertEquals(
							"Asserts if the message returned is equals to the message passed as parameter to SingletonException constructor",
							"test", se.getMessage());

		} catch (Exception e) {
			tbc.fail(e.getClass().getName()+ " incorrectly thrown.");
		}
	}

}

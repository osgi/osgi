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
 * 09/03/05 	Leonardo Barros
 * 14           Implement MEG TCK
 * ===========  ==============================================================
 * 24/03/2005   Alexandre Alves
 * 14           Updates after formal inspection (JSTD-MEGTCK-CODE-INSP011)
 * ===========  ==============================================================
 */
package org.osgi.test.cases.monitor.tbc.StatusVariable;

import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @methodUnderTest org.osgi.service.monitor.StatusVariable#equals
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>equals<code> method, according to MEG reference
 *                     documentation.
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
		testEquals007();
		testEquals008();
		testEquals009();
		testEquals010();
		testEquals011();
		testEquals012();
		testEquals013();
		testEquals014();
		testEquals015();
		testEquals016();
		testEquals017();
		testEquals018();
		testEquals019();
		testEquals020();
		testEquals021();
	}

	/**
	 * @testID testEquals001
	 * @testDescription This method asserts if two objects created with same
	 *                  parameters are equal.
	 */
	public void testEquals001() {
		try {
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_DOUBLE_VALUE);
			org.osgi.service.monitor.StatusVariable sv2 = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_DOUBLE_VALUE);
			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "two objects created with same parameters are equal" }),
							sv.equals(sv2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testEquals002
	 * @testDescription This method asserts if two objects created with
	 *                  different data are not equal.
	 */
	public void testEquals002() {
		try {
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_DOUBLE_VALUE);

			org.osgi.service.monitor.StatusVariable sv2 = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_DOUBLE_VALUE + 10);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "two objects created with different data are not equal" }),
							!sv.equals(sv2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testEquals003
	 * @testDescription This method asserts if two objects created with
	 *                  different collection methods are not equal.
	 */
	public void testEquals003() {
		try {
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_DOUBLE_VALUE);

			org.osgi.service.monitor.StatusVariable sv2 = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_DER,
					MonitorTestControl.SV_DOUBLE_VALUE);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "two objects created with different collection methods are not equal" }),
							!sv.equals(sv2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testEquals004
	 * @testDescription This method asserts if two objects created with
	 *                  different path are not equal.
	 */
	public void testEquals004() {
		try {
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_DOUBLE_VALUE);

			org.osgi.service.monitor.StatusVariable sv2 = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME2,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_DOUBLE_VALUE);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "two objects created with different path are not equal" }),
							!sv.equals(sv2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testEquals005
	 * @testDescription This method asserts if two objects with
	 *                  different path are not equal.
	 */
	public void testEquals005() {
		try {
			org.osgi.service.monitor.StatusVariable sv = tbc.getMonitorAdmin().getStatusVariable(MonitorTestControl.SVS[0]);

			org.osgi.service.monitor.StatusVariable sv2 = tbc.getMonitorAdmin().getStatusVariable(MonitorTestControl.SVS_NOT_SUPPORT_NOTIFICATION[0]);
			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "two objects with different path are not equal" }),
							!sv.equals(sv2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testEquals006
	 * @testDescription This method asserts if two objects created with
	 *                  different types are not equal.
	 */
	public void testEquals006() {
		try {
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_DOUBLE_VALUE);

			org.osgi.service.monitor.StatusVariable sv2 = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_STRING_VALUE);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "two objects created with different types are not equal" }),
							!sv.equals(sv2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testEquals007
	 * @testDescription This method asserts if two objects created with same
	 *                  parameters are equal.
	 */
	public void testEquals007() {
		try {
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_LONG_VALUE);
			org.osgi.service.monitor.StatusVariable sv2 = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_LONG_VALUE);
			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "two objects created with same parameters are equal" }),
							sv.equals(sv2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testEquals008
	 * @testDescription This method asserts if two objects created with
	 *                  different data are not equal.
	 */
	public void testEquals008() {
		try {
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_LONG_VALUE);

			org.osgi.service.monitor.StatusVariable sv2 = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_LONG_VALUE + 10);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "two objects created with different data are not equal" }),
							!sv.equals(sv2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testEquals009
	 * @testDescription This method asserts if two objects created with
	 *                  different collection methods are not equal.
	 */
	public void testEquals009() {
		try {
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_LONG_VALUE);

			org.osgi.service.monitor.StatusVariable sv2 = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_DER,
					MonitorTestControl.SV_LONG_VALUE);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "two objects created with different collection methods are not equal" }),
							!sv.equals(sv2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testEquals010
	 * @testDescription This method asserts if two objects created with
	 *                  different path are not equal.
	 */
	public void testEquals010() {
		try {
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_LONG_VALUE);

			org.osgi.service.monitor.StatusVariable sv2 = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME2,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_LONG_VALUE);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "two objects created with different path are not equal" }),
							!sv.equals(sv2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testEquals011
	 * @testDescription This method asserts if two objects created with
	 *                  different type are not equal.
	 */
	public void testEquals011() {
		try {
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_LONG_VALUE);

			org.osgi.service.monitor.StatusVariable sv2 = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_BOOLEAN_VALUE);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "two objects created with different type are not equal" }),
							!sv.equals(sv2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testEquals012
	 * @testDescription This method asserts if two objects created with same
	 *                  parameters are equal.
	 */
	public void testEquals012() {
		try {
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_MONITORABLEID1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_STRING_VALUE);
			org.osgi.service.monitor.StatusVariable sv2 = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_STRING_VALUE);
			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "two objects created with same parameters are equal" }),
							sv.equals(sv2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testEquals013
	 * @testDescription This method asserts if two objects created with
	 *                  different data are not equal.
	 */
	public void testEquals013() {
		try {
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_STRING_VALUE);

			org.osgi.service.monitor.StatusVariable sv2 = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_STRING_VALUE + "1");

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "two objects created with different data are not equal" }),
							!sv.equals(sv2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testEquals014
	 * @testDescription This method asserts if two objects created with
	 *                  different collection methods are not equal.
	 */
	public void testEquals014() {
		try {
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_STRING_VALUE);

			org.osgi.service.monitor.StatusVariable sv2 = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_DER,
					MonitorTestControl.SV_STRING_VALUE);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "two objects created with different collection methods are not equal" }),
							!sv.equals(sv2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testEquals015
	 * @testDescription This method asserts if two objects created with
	 *                  different path are not equal.
	 */
	public void testEquals015() {
		try {
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_STRING_VALUE);

			org.osgi.service.monitor.StatusVariable sv2 = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME2,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_STRING_VALUE);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "two objects created with different path are not equal" }),
							!sv.equals(sv2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testEquals016
	 * @testDescription This method asserts if two objects created with
	 *                  different type are not equal.
	 */
	public void testEquals016() {
		try {
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_STRING_VALUE);

			org.osgi.service.monitor.StatusVariable sv2 = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_LONG_VALUE);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "two objects created with different type are not equal" }),
							!sv.equals(sv2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testEquals017
	 * @testDescription This method asserts if two objects created with same
	 *                  parameters are equal.
	 */
	public void testEquals017() {
		try {
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_BOOLEAN_VALUE);
			org.osgi.service.monitor.StatusVariable sv2 = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_BOOLEAN_VALUE);
			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "two objects created with same parameters are equal" }),
							sv.equals(sv2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testEquals018
	 * @testDescription This method asserts if two objects created with
	 *                  different data are not equal.
	 */
	public void testEquals018() {
		try {
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_BOOLEAN_VALUE);

			org.osgi.service.monitor.StatusVariable sv2 = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_BOOLEAN_VALUE + "1");

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "two objects created with different data are not equal" }),
							!sv.equals(sv2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testEquals019
	 * @testDescription This method asserts if two objects created with
	 *                  different collection methods are not equal.
	 */
	public void testEquals019() {
		try {
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_BOOLEAN_VALUE);

			org.osgi.service.monitor.StatusVariable sv2 = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_DER,
					MonitorTestControl.SV_BOOLEAN_VALUE);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "two objects created with different collection methods are not equal" }),
							!sv.equals(sv2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testEquals020
	 * @testDescription This method asserts if two objects created with
	 *                  different path are not equal.
	 */
	public void testEquals020() {
		try {
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_BOOLEAN_VALUE);

			org.osgi.service.monitor.StatusVariable sv2 = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME2,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_BOOLEAN_VALUE);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "two objects created with different path are not equal" }),
							!sv.equals(sv2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testEquals021
	 * @testDescription This method asserts if two objects created with
	 *                  different type are not equal.
	 */
	public void testEquals021() {
		try {
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_BOOLEAN_VALUE);

			org.osgi.service.monitor.StatusVariable sv2 = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_LONG_VALUE);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "two objects created with different type are not equal" }),
							!sv.equals(sv2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

}

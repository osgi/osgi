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

import org.osgi.service.monitor.StatusVariable;
import org.osgi.test.cases.monitor.tbc.MonitorConstants;
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

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
		testEquals007();
	}

	/**
	 * This method asserts if equals method returns true when two equal
	 * StatusVariable are compared, otherwise it must returns false.
	 * StatusVariables are created ranging all possible values for second
	 * parameter and with an integer passed as third parameter.
	 * 
	 * @spec StatusVariable.equals(Object)
	 */
	private void testEquals001() {
		try {
			tbc.log("#testEquals001");
			StatusVariable sv = null;
			StatusVariable sv2 = null;

			for (int i = 0; i < 4; i++) {
				sv = new StatusVariable(MonitorConstants.SV_NAME1, i,
						MonitorConstants.SV_INT_VALUE);
				for (int j = 0; j < 4; j++) {
					sv2 = new StatusVariable(MonitorConstants.SV_NAME1, j,
							MonitorConstants.SV_INT_VALUE);
					if (i == j) {
						tbc
								.assertTrue(
										"Asserts if equals method returns true when comparing two equal StatusVariable.",
										sv.equals(sv2));
					} else {
						tbc
								.assertTrue(
										"Asserts if equals method returns false when comparing two different StatusVariable.",
										!sv.equals(sv2));
					}
				}
			}
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if equals method returns true when two equal
	 * StatusVariable are compared, otherwise it must returns false.
	 * StatusVariables are created ranging all possible values for second
	 * parameter and with a boolean passed as third parameter.
	 * 
	 * @spec StatusVariable.equals(Object)
	 */
	private void testEquals002() {
		try {
			tbc.log("#testEquals002");
			StatusVariable sv = null;
			StatusVariable sv2 = null;

			for (int i = 0; i < 4; i++) {
				sv = new StatusVariable(MonitorConstants.SV_NAME1, i,
						MonitorConstants.SV_BOOLEAN_VALUE);
				for (int j = 0; j < 4; j++) {
					sv2 = new StatusVariable(MonitorConstants.SV_NAME1, j,
							MonitorConstants.SV_BOOLEAN_VALUE);
					if (i == j) {
						tbc
								.assertTrue(
										"Asserts if equals method returns true when comparing two equal StatusVariable.",
										sv.equals(sv2));
					} else {
						tbc
								.assertTrue(
										"Asserts if equals method returns false when comparing two different StatusVariable.",
										!sv.equals(sv2));
					}
				}
			}
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if equals method returns true when two equal
	 * StatusVariable are compared, otherwise it must returns false.
	 * StatusVariables are created ranging all possible values for second
	 * parameter and with a String passed as third parameter.
	 * 
	 * @spec StatusVariable.equals(Object)
	 */
	private void testEquals003() {
		try {
			tbc.log("#testEquals003");
			StatusVariable sv = null;
			StatusVariable sv2 = null;

			for (int i = 0; i < 4; i++) {
				sv = new StatusVariable(MonitorConstants.SV_NAME1, i,
						MonitorConstants.SV_STRING_VALUE);
				for (int j = 0; j < 4; j++) {
					sv2 = new StatusVariable(MonitorConstants.SV_NAME1, j,
							MonitorConstants.SV_STRING_VALUE);
					if (i == j) {
						tbc
								.assertTrue(
										"Asserts if equals method returns true when comparing two equal StatusVariable.",
										sv.equals(sv2));
					} else {
						tbc
								.assertTrue(
										"Asserts if equals method returns false when comparing two different StatusVariable.",
										!sv.equals(sv2));
					}
				}
			}
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if equals method returns true when two equal
	 * StatusVariable are compared, otherwise it must returns false.
	 * StatusVariables are created ranging all possible values for second
	 * parameter and with a float passed as third parameter.
	 * 
	 * @spec StatusVariable.equals(Object)
	 */
	private void testEquals004() {
		try {
			tbc.log("#testEquals004");
			StatusVariable sv = null;
			StatusVariable sv2 = null;

			for (int i = 0; i < 4; i++) {
				sv = new StatusVariable(MonitorConstants.SV_NAME1, i,
						MonitorConstants.SV_FLOAT_VALUE);
				for (int j = 0; j < 4; j++) {
					sv2 = new StatusVariable(MonitorConstants.SV_NAME1, j,
							MonitorConstants.SV_FLOAT_VALUE);
					if (i == j) {
						tbc
								.assertTrue(
										"Asserts if equals method returns true when comparing two equal StatusVariable.",
										sv.equals(sv2));
					} else {
						tbc
								.assertTrue(
										"Asserts if equals method returns false when comparing two different StatusVariable.",
										!sv.equals(sv2));
					}
				}
			}
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if equals method returns true when two equal
	 * StatusVariable are compared. StatusVariables are created ranging all
	 * possible types for the third parameter of the constructor.
	 * 
	 * @spec StatusVariable.equals(Object)
	 */
	private void testEquals005() {
		try {
			tbc.log("#testEquals005");
			StatusVariable sv = null;
			StatusVariable sv2 = null;

			for (int i = 0; i < 4; i++) {
				if (i == 0) {
					sv = new StatusVariable(MonitorConstants.SV_NAME1,
							StatusVariable.CM_CC,
							MonitorConstants.SV_INT_VALUE);
					sv2 = new StatusVariable(MonitorConstants.SV_NAME1,
							StatusVariable.CM_CC,
							MonitorConstants.SV_INT_VALUE);
				} else if (i == 1) {
					sv = new StatusVariable(MonitorConstants.SV_NAME1,
							StatusVariable.CM_CC,
							MonitorConstants.SV_BOOLEAN_VALUE);
					sv2 = new StatusVariable(MonitorConstants.SV_NAME1,
							StatusVariable.CM_CC,
							MonitorConstants.SV_BOOLEAN_VALUE);
				} else if (i == 2) {
					sv = new StatusVariable(MonitorConstants.SV_NAME1,
							StatusVariable.CM_CC,
							MonitorConstants.SV_FLOAT_VALUE);
					sv2 = new StatusVariable(MonitorConstants.SV_NAME1,
							StatusVariable.CM_CC,
							MonitorConstants.SV_FLOAT_VALUE);
				} else if (i == 3) {
					sv = new StatusVariable(MonitorConstants.SV_NAME1,
							StatusVariable.CM_CC,
							MonitorConstants.SV_STRING_VALUE);
					sv2 = new StatusVariable(MonitorConstants.SV_NAME1,
							StatusVariable.CM_CC,
							MonitorConstants.SV_STRING_VALUE);
				}

				tbc
						.assertTrue(
								"Asserts if equals method returns true when comparing two equal StatusVariable.",
								sv.equals(sv2));
			}
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if equals method returns false when two different
	 * StatusVariable are compared. StatusVariables are created ranging all
	 * possible types for the third parameter with different values.
	 * 
	 * @spec StatusVariable.equals(Object)
	 */
	private void testEquals006() {
		try {
			tbc.log("#testEquals006");
			StatusVariable sv = null;
			StatusVariable sv2 = null;

			for (int i = 0; i < 4; i++) {
				if (i == 0) {
					sv = new StatusVariable(MonitorConstants.SV_NAME1,
							StatusVariable.CM_CC,
							MonitorConstants.SV_INT_VALUE);
				} else if (i == 1) {
					sv = new StatusVariable(MonitorConstants.SV_NAME1,
							StatusVariable.CM_CC,
							MonitorConstants.SV_BOOLEAN_VALUE);
				} else if (i == 2) {
					sv = new StatusVariable(MonitorConstants.SV_NAME1,
							StatusVariable.CM_CC,
							MonitorConstants.SV_FLOAT_VALUE);
				} else if (i == 3) {
					sv = new StatusVariable(MonitorConstants.SV_NAME1,
							StatusVariable.CM_CC,
							MonitorConstants.SV_STRING_VALUE);
				}
				for (int j = 0; j < 4; j++) {

					if (j == 0) {
						sv2 = new StatusVariable(MonitorConstants.SV_NAME1,
								StatusVariable.CM_CC,
								MonitorConstants.SV_INT_VALUE + 1);
					} else if (j == 1) {
						sv2 = new StatusVariable(MonitorConstants.SV_NAME1,
								StatusVariable.CM_CC,
								!MonitorConstants.SV_BOOLEAN_VALUE);
					} else if (j == 2) {
						sv2 = new StatusVariable(MonitorConstants.SV_NAME1,
								StatusVariable.CM_CC,
								MonitorConstants.SV_FLOAT_VALUE + 1.0f);
					} else if (j == 3) {
						sv2 = new StatusVariable(MonitorConstants.SV_NAME1,
								StatusVariable.CM_CC,
								MonitorConstants.SV_STRING_VALUE + "a");
					}
					tbc
							.assertTrue(
									"Asserting equals method returns false when we pass two different statusvariable.",
									!sv.equals(sv2));
				}
			}
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if equals method returns false when comparing two
	 * StatusVariable, created with different values for the first parameter.
	 * 
	 * @spec StatusVariable.equals(Object)
	 */
	private void testEquals007() {
		try {
			tbc.log("#testEquals007");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_INT_VALUE);

			org.osgi.service.monitor.StatusVariable sv2 = new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.SV_NAME2,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_INT_VALUE);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "two StatusVariable, created with different paths, are different" }),
							!sv.equals(sv2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

}

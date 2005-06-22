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
 * 14/06/2005   Alexandre Alves
 * 14           Updates after revision of spec.
 * ===========  ==============================================================
 */
package org.osgi.test.cases.monitor.tbc.StatusVariable;

import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @methodUnderTest org.osgi.service.monitor.StatusVariable#StatusVariable,
 *                  getCollectionMethod, getBoolean, getDouble, getLong,
 *                  getString getTimeStamp, getType, getID
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>StatusVariable,
 *                  getCollectionMethod, getBoolean, getDouble, getLong, getString
 *                  getTimeStamp, getType, getID<code> method, according to MEG reference
 *                     documentation.
 */
public class StatusVariable {
	private MonitorTestControl tbc;

	public StatusVariable(MonitorTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testStatusVariable001();
		testStatusVariable002();
		testStatusVariable003();
		testStatusVariable004();
		testStatusVariable005();
		testStatusVariable006();
		testStatusVariable007();
		testStatusVariable008();
		testStatusVariable009();
		testStatusVariable010();
		testStatusVariable011();
		testStatusVariable012();
		testStatusVariable013();
		testStatusVariable014();
		testStatusVariable015();
		testStatusVariable016();
		testStatusVariable017();
		testStatusVariable018();
		testStatusVariable019();
		testStatusVariable020();
		testStatusVariable021();
		testStatusVariable022();
		testStatusVariable023();
		testStatusVariable024();
		testStatusVariable025();
		testStatusVariable026();
		testStatusVariable027();
		testStatusVariable028();
		testStatusVariable029();
		testStatusVariable030();
		testStatusVariable031();
		testStatusVariable032();
		testStatusVariable033();
		testStatusVariable034();
		testStatusVariable035();
		testStatusVariable036();
		testStatusVariable037();
		testStatusVariable038();
		testStatusVariable039();
		testStatusVariable040();
	}

	/**
	 * @testID testStatusVariable001
	 * @testDescription This method asserts that a
	 *                  java.lang.IllegalArgumentException is thrown when use
	 *                  invalid characters for StatusVariable name on the
	 *                  constructor.
	 */
	public void testStatusVariable001() {
		try {
			tbc.log("#testStatusVariable001");
			new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.INVALID_ID,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_LONG_VALUE);
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
	 * @testID testStatusVariable002
	 * @testDescription This method asserts that a
	 *                  java.lang.IllegalArgumentException is thrown when use
	 *                  invalid characters for StatusVariable name on the
	 *                  constructor.
	 */
	public void testStatusVariable002() {
		try {
			tbc.log("#testStatusVariable002");
			new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.INVALID_ID,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_STRING_VALUE);
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
	 * @testID testStatusVariable003
	 * @testDescription This method asserts that a
	 *                  java.lang.IllegalArgumentException is thrown when use
	 *                  invalid characters for StatusVariable name on the
	 *                  constructor.
	 */
	public void testStatusVariable003() {
		try {
			tbc.log("#testStatusVariable003");
			new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.INVALID_ID,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_DOUBLE_VALUE);
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
	 * @testID testStatusVariable004
	 * @testDescription This method asserts that a
	 *                  java.lang.IllegalArgumentException is thrown when use
	 *                  invalid characters for StatusVariable name on the
	 *                  constructor.
	 */
	public void testStatusVariable004() {
		try {
			tbc.log("#testStatusVariable004");
			new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.INVALID_ID,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_BOOLEAN_VALUE);
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
	 * @testID testStatusVariable005
	 * @testDescription This method asserts that a
	 *                  java.lang.NullPointerException is thrown when use null
	 *                  for Id on the constructor.
	 */
	public void testStatusVariable005() {
		try {
			tbc.log("#testStatusVariable005");
			new org.osgi.service.monitor.StatusVariable(
					null,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_LONG_VALUE);
			tbc.failException("", java.lang.NullPointerException.class);
		} catch (java.lang.NullPointerException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { NullPointerException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							NullPointerException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testStatusVariable006
	 * @testDescription This method asserts that a
	 *                  java.lang.NullPointerException is thrown when use null
	 *                  for Id on the constructor.
	 */
	public void testStatusVariable006() {
		try {
			tbc.log("#testStatusVariable006");
			new org.osgi.service.monitor.StatusVariable(
					null,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_DOUBLE_VALUE);
			tbc.failException("", java.lang.NullPointerException.class);
		} catch (java.lang.NullPointerException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { NullPointerException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							NullPointerException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testStatusVariable007
	 * @testDescription This method asserts that a
	 *                  java.lang.NullPointerException is thrown when use null
	 *                  for Id on the constructor.
	 */
	public void testStatusVariable007() {
		try {
			tbc.log("#testStatusVariable007");
			new org.osgi.service.monitor.StatusVariable(
					null,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_STRING_VALUE);
			tbc.failException("", java.lang.NullPointerException.class);
		} catch (java.lang.NullPointerException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { NullPointerException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							NullPointerException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testStatusVariable008
	 * @testDescription This method asserts that a
	 *                  java.lang.NullPointerException is thrown when use null
	 *                  for Id on the constructor.
	 */
	public void testStatusVariable008() {
		try {
			tbc.log("#testStatusVariable008");
			new org.osgi.service.monitor.StatusVariable(
					null,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_BOOLEAN_VALUE);
			tbc.failException("", java.lang.NullPointerException.class);
		} catch (java.lang.NullPointerException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { NullPointerException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							NullPointerException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testStatusVariable009
	 * @testDescription This method asserts that a
	 *                  java.lang.IllegalArgumentException is thrown when cm is
	 *                  not one of the collection method constants.
	 */
	public void testStatusVariable009() {
		try {
			tbc.log("#testStatusVariable009");
			new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1, MonitorTestControl.INVALID_CM,
					MonitorTestControl.SV_LONG_VALUE);
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
	 * @testID testStatusVariable010
	 * @testDescription This method asserts that a
	 *                  java.lang.IllegalArgumentException is thrown when cm is
	 *                  not one of the collection method constants.
	 */
	public void testStatusVariable010() {
		try {
			tbc.log("#testStatusVariable010");
			new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1, MonitorTestControl.INVALID_CM,
					MonitorTestControl.SV_DOUBLE_VALUE);
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
	 * @testID testStatusVariable011
	 * @testDescription This method asserts that a
	 *                  java.lang.IllegalArgumentException is thrown when cm is
	 *                  not one of the collection method constants.
	 */
	public void testStatusVariable011() {
		try {
			tbc.log("#testStatusVariable011");
			new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1, MonitorTestControl.INVALID_CM,
					MonitorTestControl.SV_STRING_VALUE);
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
	 * @testID testStatusVariable012
	 * @testDescription This method asserts that a
	 *                  java.lang.IllegalArgumentException is thrown when cm is
	 *                  not one of the collection method constants.
	 */
	public void testStatusVariable012() {
		try {
			tbc.log("#testStatusVariable012");
			new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1, MonitorTestControl.INVALID_CM,
					MonitorTestControl.SV_BOOLEAN_VALUE);
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
	 * @testID testStatusVariable013
	 * @testDescription This method asserts if the value passed in constructor
	 *                  is the same returned.
	 */
	public void testStatusVariable013() {
		try {
			tbc.log("#testStatusVariable013");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_DOUBLE_VALUE);

			double val = sv.getDouble();
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"getDouble() result", MonitorTestControl.SV_DOUBLE_VALUE+"" }),							
					MonitorTestControl.SV_DOUBLE_VALUE, val, 0);

			String id = sv.getID();
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"getID() result", MonitorTestControl.SV_NAME1 }),
					MonitorTestControl.SV_NAME1, id);

			int type = sv.getType();
			tbc
					.assertEquals(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_EQUALS,
											new String[] {
													"getType results", (org.osgi.service.monitor.StatusVariable.TYPE_DOUBLE+"") }),
							org.osgi.service.monitor.StatusVariable.TYPE_DOUBLE,
							type);

			int method = sv.getCollectionMethod();
			tbc
					.assertEquals(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_EQUALS,
											new String[] {
													"GetCollectionMethod() results",
															org.osgi.service.monitor.StatusVariable.CM_CC+"",
													}),
							org.osgi.service.monitor.StatusVariable.CM_CC,
							method);
			
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NULL, new String[] { "getTimeStamp()" }), sv.getTimeStamp());
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}

	/**
	 * @testID testStatusVariable014
	 * @testDescription This method asserts if the value passed in constructor
	 *                  is the same returned.
	 */
	public void testStatusVariable014() {
		try {
			tbc.log("#testStatusVariable014");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_LONG_VALUE);

			long val = sv.getLong();
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"getLong() results", MonitorTestControl.SV_LONG_VALUE+"",
							}),
					MonitorTestControl.SV_LONG_VALUE, val);

			String id = sv.getID();
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"getID() results", MonitorTestControl.SV_NAME1 }),
					MonitorTestControl.SV_NAME1, id);
			
			int type = sv.getType();
			tbc
					.assertEquals(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_EQUALS,
											new String[] {
													"getType() results", 
															org.osgi.service.monitor.StatusVariable.TYPE_LONG+"",
													}),
							org.osgi.service.monitor.StatusVariable.TYPE_LONG,
							type);
			
			int method = sv.getCollectionMethod();
			tbc
					.assertEquals(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_EQUALS,
											new String[] {
													"getCollectionMethod() results",
															org.osgi.service.monitor.StatusVariable.CM_CC+""
													 }),
							org.osgi.service.monitor.StatusVariable.CM_CC,
							method);
			
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NULL, new String[] { "getTimeStamp()" }), sv.getTimeStamp());
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}

	/**
	 * @testID testStatusVariable015
	 * @testDescription This method asserts if the value passed in constructor
	 *                  is the same returned.
	 */
	public void testStatusVariable015() {
		try {
			tbc.log("#testStatusVariable015");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_STRING_VALUE);

			String val = sv.getString();
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"getString() results", MonitorTestControl.SV_STRING_VALUE }),
					MonitorTestControl.SV_STRING_VALUE, val);

			String id = sv.getID();
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"getID() results", MonitorTestControl.SV_NAME1 }),
					MonitorTestControl.SV_NAME1, id);

			
			int type = sv.getType();
			tbc
					.assertEquals(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_EQUALS,
											new String[] {
													"getType() results", 
															org.osgi.service.monitor.StatusVariable.TYPE_STRING+"" }),
							org.osgi.service.monitor.StatusVariable.TYPE_STRING,
							type);
			
			int method = sv.getCollectionMethod();
			tbc
					.assertEquals(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_EQUALS,
											new String[] {
													"getCollectionMethod() results",
															org.osgi.service.monitor.StatusVariable.CM_CC+"" }),
							org.osgi.service.monitor.StatusVariable.CM_CC,
							method);
			
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NULL, new String[] { "getTimeStamp()" }), sv.getTimeStamp());
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}

	/**
	 * @testID testStatusVariable016
	 * @testDescription This method asserts if the value passed in constructor
	 *                  is the same returned.
	 */
	public void testStatusVariable016() {
		try {
			tbc.log("#testStatusVariable016");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_BOOLEAN_VALUE);

			boolean val = sv.getBoolean();
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"getBoolean() results",
							MonitorTestControl.SV_BOOLEAN_VALUE+"" }),
					MonitorTestControl.SV_BOOLEAN_VALUE, val);
			
			String id = sv.getID();
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"getID() results", MonitorTestControl.SV_NAME1}),
					MonitorTestControl.SV_NAME1, id);

			int type = sv.getType();
			tbc
					.assertEquals(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_EQUALS,
											new String[] {
													"getType() results", org.osgi.service.monitor.StatusVariable.TYPE_BOOLEAN+"",
													}),
							org.osgi.service.monitor.StatusVariable.TYPE_BOOLEAN,
							type);
			
			int method = sv.getCollectionMethod();
			tbc
					.assertEquals(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_EQUALS,
											new String[] {
													"getCollectionMethod() results", 
															org.osgi.service.monitor.StatusVariable.CM_CC+"",
													}),
							org.osgi.service.monitor.StatusVariable.CM_CC,
							method);
			
			tbc.assertNotNull(MessagesConstants.getMessage(MessagesConstants.ASSERT_NULL, new String[] { "getTimeStamp()" }), sv.getTimeStamp());
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}

	/**
	 * @testID testStatusVariable017
	 * @testDescription This method asserts if an IllegalStateException is
	 *                  thrown when use getLong instead of getDouble
	 */
	public void testStatusVariable017() {
		try {
			tbc.log("#testStatusVariable017");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_DOUBLE_VALUE);
			sv.getLong();
			tbc.failException("", java.lang.IllegalStateException.class);
		} catch (java.lang.IllegalStateException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalStateException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalStateException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testStatusVariable018
	 * @testDescription This method asserts if an IllegalStateException is
	 *                  thrown when use getDouble instead of getLong
	 */
	public void testStatusVariable018() {
		try {
			tbc.log("#testStatusVariable018");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_LONG_VALUE);
			sv.getDouble();
			tbc.failException("", java.lang.IllegalStateException.class);
		} catch (java.lang.IllegalStateException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalStateException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalStateException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testStatusVariable019
	 * @testDescription This method asserts if an IllegalStateException is
	 *                  thrown when use getString instead of getLong
	 */
	public void testStatusVariable019() {
		try {
			tbc.log("#testStatusVariable019");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_LONG_VALUE);
			sv.getString();
			tbc.failException("", java.lang.IllegalStateException.class);
		} catch (java.lang.IllegalStateException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalStateException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalStateException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testStatusVariable020
	 * @testDescription This method asserts if an IllegalStateException is
	 *                  thrown when use getBoolean instead of getLong
	 */
	public void testStatusVariable020() {
		try {
			tbc.log("#testStatusVariable020");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_LONG_VALUE);
			sv.getBoolean();
			tbc.failException("", java.lang.IllegalStateException.class);
		} catch (java.lang.IllegalStateException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalStateException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalStateException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testStatusVariable021
	 * @testDescription This method asserts if an IllegalStateException is
	 *                  thrown when use getString instead of getDouble
	 */
	public void testStatusVariable021() {
		try {
			tbc.log("#testStatusVariable021");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_DOUBLE_VALUE);
			sv.getString();
			tbc.failException("", java.lang.IllegalStateException.class);
		} catch (java.lang.IllegalStateException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalStateException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalStateException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testStatusVariable022
	 * @testDescription This method asserts if an IllegalStateException is
	 *                  thrown when use getBoolean instead of getDouble
	 */
	public void testStatusVariable022() {
		try {
			tbc.log("#testStatusVariable022");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_DOUBLE_VALUE);
			sv.getBoolean();
			tbc.failException("", java.lang.IllegalStateException.class);
		} catch (java.lang.IllegalStateException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalStateException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalStateException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testStatusVariable023
	 * @testDescription This method asserts if an IllegalStateException is
	 *                  thrown when use getDouble instead of getString
	 */
	public void testStatusVariable023() {
		try {
			tbc.log("#testStatusVariable023");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_STRING_VALUE);
			sv.getDouble();
			tbc.failException("", java.lang.IllegalStateException.class);
		} catch (java.lang.IllegalStateException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalStateException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalStateException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testStatusVariable024
	 * @testDescription This method asserts if an IllegalStateException is
	 *                  thrown when use getLong instead of getString
	 */
	public void testStatusVariable024() {
		try {
			tbc.log("#testStatusVariable024");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_STRING_VALUE);
			sv.getLong();
			tbc.failException("", java.lang.IllegalStateException.class);
		} catch (java.lang.IllegalStateException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalStateException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalStateException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testStatusVariable025
	 * @testDescription This method asserts if an IllegalStateException is
	 *                  thrown when use getBoolean instead of getString
	 */
	public void testStatusVariable025() {
		try {
			tbc.log("#testStatusVariable025");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_STRING_VALUE);
			sv.getBoolean();
			tbc.failException("", java.lang.IllegalStateException.class);
		} catch (java.lang.IllegalStateException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalStateException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalStateException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testStatusVariable026
	 * @testDescription This method asserts if an IllegalStateException is
	 *                  thrown when use getLong instead of getBoolean
	 */
	public void testStatusVariable026() {
		try {
			tbc.log("#testStatusVariable026");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_BOOLEAN_VALUE);
			sv.getLong();
			tbc.failException("", java.lang.IllegalStateException.class);
		} catch (java.lang.IllegalStateException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalStateException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalStateException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testStatusVariable027
	 * @testDescription This method asserts if an IllegalStateException is
	 *                  thrown when use getDouble instead of getBoolean
	 */
	public void testStatusVariable027() {
		try {
			tbc.log("#testStatusVariable027");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_BOOLEAN_VALUE);
			sv.getDouble();
			tbc.failException("", java.lang.IllegalStateException.class);
		} catch (java.lang.IllegalStateException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalStateException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalStateException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testStatusVariable028
	 * @testDescription This method asserts if an IllegalStateException is
	 *                  thrown when use getString instead of getBoolean
	 */
	public void testStatusVariable028() {
		try {
			tbc.log("#testStatusVariable028");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_BOOLEAN_VALUE);
			sv.getString();
			tbc.failException("", java.lang.IllegalStateException.class);
		} catch (java.lang.IllegalStateException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalStateException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalStateException.class.getName(),
							e.getClass().getName() }));
		}
	}
	
	/**
	 * @testID testStatusVariable029
	 * @testDescription This method asserts if an IllegalArgumentException is
	 *                  thrown when use empty string for id
	 */
	public void testStatusVariable029() {
		try {
			tbc.log("#testStatusVariable029");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					"",
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_BOOLEAN_VALUE);
			sv.getString();
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
	 * @testID testStatusVariable030
	 * @testDescription This method asserts if an IllegalArgumentException is
	 *                  thrown when use empty string for id
	 */
	public void testStatusVariable030() {
		try {
			tbc.log("#testStatusVariable030");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					"",
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_DOUBLE_VALUE);
			sv.getString();
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
	 * @testID testStatusVariable031
	 * @testDescription This method asserts if an IllegalArgumentException is
	 *                  thrown when use empty string for id
	 */
	public void testStatusVariable031() {
		try {
			tbc.log("#testStatusVariable031");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					"",
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_LONG_VALUE);
			sv.getString();
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
	 * @testID testStatusVariable032
	 * @testDescription This method asserts if an IllegalArgumentException is
	 *                  thrown when use empty string for id
	 */
	public void testStatusVariable032() {
		try {
			tbc.log("#testStatusVariable032");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					"",
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_STRING_VALUE);
			sv.getString();
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
	 * @testID testStatusVariable033
	 * @testDescription This method asserts if an IllegalArgumentException is
	 *                  thrown when use a statusvariable name with more than 20 characters.
	 */
	public void testStatusVariable033() {
		try {
			tbc.log("#testStatusVariable033");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.LONGID,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_DOUBLE_VALUE);
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
	 * @testID testStatusVariable034
	 * @testDescription This method asserts if an IllegalArgumentException is
	 *                  thrown when use a statusvariable name with more than 20 characters.
	 */
	public void testStatusVariable034() {
		try {
			tbc.log("#testStatusVariable034");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.LONGID,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_BOOLEAN_VALUE);
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
	 * @testID testStatusVariable035
	 * @testDescription This method asserts if an IllegalArgumentException is
	 *                  thrown when use a statusvariable name with more than 20 characters.
	 */
	public void testStatusVariable035() {
		try {
			tbc.log("#testStatusVariable035");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.LONGID,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_LONG_VALUE);
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
	 * @testID testStatusVariable036
	 * @testDescription This method asserts if an IllegalArgumentException is
	 *                  thrown when use a statusvariable name with more than 20 characters.
	 */
	public void testStatusVariable036() {
		try {
			tbc.log("#testStatusVariable036");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.LONGID,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_STRING_VALUE);
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
	 * @testID testStatusVariable037
	 * @testDescription This method asserts no exception was thrown when
	 *                  we pass a valid id as name.
	 */
	public void testStatusVariable037() {
		try {
			tbc.log("#testStatusVariable037");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.VALID_ID,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_STRING_VALUE);
			tbc.pass("Passed. No exception was thrown.");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}
	
	/**
	 * @testID testStatusVariable038
	 * @testDescription This method asserts no exception was thrown when
	 *                  we pass a valid id as name.
	 */
	public void testStatusVariable038() {
		try {
			tbc.log("#testStatusVariable038");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.VALID_ID,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_BOOLEAN_VALUE);
			tbc.pass("Passed. No exception was thrown.");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}	
	
	/**
	 * @testID testStatusVariable039
	 * @testDescription This method asserts no exception was thrown when
	 *                  we pass a valid id as name.
	 */
	public void testStatusVariable039() {
		try {
			tbc.log("#testStatusVariable039");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.VALID_ID,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_DOUBLE_VALUE);
			tbc.pass("Passed. No exception was thrown.");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}	
	
	/**
	 * @testID testStatusVariable040
	 * @testDescription This method asserts no exception was thrown when
	 *                  we pass a valid id as name.
	 */
	public void testStatusVariable040() {
		try {
			tbc.log("#testStatusVariable040");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.VALID_ID,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorTestControl.SV_LONG_VALUE);
			tbc.pass("Passed. No exception was thrown.");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}		

}

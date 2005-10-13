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

import org.osgi.test.cases.monitor.tbc.MonitorConstants;
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @author Leonardo Barros
 * 
 * This test class validates the implementation of
 * <code>StatusVariable</code> method, according to MEG reference documentation.
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
	 * This method asserts if IllegalArgumentException is thrown if the given id
	 * is not a valid StatusVariable name
	 * 
	 * @spec StatusVariable.StatusVariable(String, int, int)
	 */
	private void testStatusVariable001() {
		try {
			tbc.log("#testStatusVariable001");
			new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.INVALID_ID,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_INT_VALUE);
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
	 * This method asserts if IllegalArgumentException is thrown if the given id
	 * is not a valid StatusVariable name
	 * 
	 * @spec StatusVariable.StatusVariable(String, int, String)
	 */
	private void testStatusVariable002() {
		try {
			tbc.log("#testStatusVariable002");
			new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.INVALID_ID,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_STRING_VALUE);
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
	 * This method asserts if IllegalArgumentException is thrown if the given id
	 * is not a valid StatusVariable name
	 * 
	 * @spec StatusVariable.StatusVariable(String, int, float)
	 */
	private void testStatusVariable003() {
		try {
			tbc.log("#testStatusVariable003");
			new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.INVALID_ID,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_FLOAT_VALUE);
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
	 * This method asserts if IllegalArgumentException is thrown if the given id
	 * is not a valid StatusVariable name
	 * 
	 * @spec StatusVariable.StatusVariable(String, int, boolean)
	 */
	private void testStatusVariable004() {
		try {
			tbc.log("#testStatusVariable004");
			new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.INVALID_ID,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_BOOLEAN_VALUE);
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
	 * This method asserts if NullPointerException is thrown if the id parameter
	 * is null
	 * 
	 * @spec StatusVariable.StatusVariable(String, int, int)
	 */
	private void testStatusVariable005() {
		try {
			tbc.log("#testStatusVariable005");
			new org.osgi.service.monitor.StatusVariable(null,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_INT_VALUE);
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
	 * This method asserts if NullPointerException is thrown if the id parameter
	 * is null
	 * 
	 * @spec StatusVariable.StatusVariable(String, int, float)
	 */
	private void testStatusVariable006() {
		try {
			tbc.log("#testStatusVariable006");
			new org.osgi.service.monitor.StatusVariable(null,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_FLOAT_VALUE);
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
	 * This method asserts if NullPointerException is thrown if the id parameter
	 * is null
	 * 
	 * @spec StatusVariable.StatusVariable(String, int, String)
	 */
	private void testStatusVariable007() {
		try {
			tbc.log("#testStatusVariable007");
			new org.osgi.service.monitor.StatusVariable(null,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_STRING_VALUE);
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
	 * This method asserts if NullPointerException is thrown if the id parameter
	 * is null
	 * 
	 * @spec StatusVariable.StatusVariable(String, int, boolean)
	 */
	private void testStatusVariable008() {
		try {
			tbc.log("#testStatusVariable008");
			new org.osgi.service.monitor.StatusVariable(null,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_BOOLEAN_VALUE);
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
	 * This method asserts if IllegalArgumentException is thrown if cm is not
	 * one of the collection method constants
	 * 
	 * @spec StatusVariable.StatusVariable(String, int, int)
	 */
	private void testStatusVariable009() {
		try {
			tbc.log("#testStatusVariable009");
			new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.SV_NAME1, MonitorConstants.INVALID_CM,
					MonitorConstants.SV_INT_VALUE);
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
	 * This method asserts if IllegalArgumentException is thrown if cm is not
	 * one of the collection method constants
	 * 
	 * @spec StatusVariable.StatusVariable(String, int, float)
	 */
	private void testStatusVariable010() {
		try {
			tbc.log("#testStatusVariable010");
			new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.SV_NAME1, MonitorConstants.INVALID_CM,
					MonitorConstants.SV_FLOAT_VALUE);
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
	 * This method asserts if IllegalArgumentException is thrown if cm is not
	 * one of the collection method constants
	 * 
	 * @spec StatusVariable.StatusVariable(String, int, String)
	 */
	private void testStatusVariable011() {
		try {
			tbc.log("#testStatusVariable011");
			new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.SV_NAME1, MonitorConstants.INVALID_CM,
					MonitorConstants.SV_STRING_VALUE);
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
	 * This method asserts if IllegalArgumentException is thrown if cm is not
	 * one of the collection method constants
	 * 
	 * @spec StatusVariable.StatusVariable(String, int, boolean)
	 */
	private void testStatusVariable012() {
		try {
			tbc.log("#testStatusVariable012");
			new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.SV_NAME1, MonitorConstants.INVALID_CM,
					MonitorConstants.SV_BOOLEAN_VALUE);
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
	 * This method asserts if the values passed as parameter are equal to the
	 * corresponding values returned from get methods
	 * 
	 * @spec StatusVariable.StatusVariable(String, int, float)
	 */
	private void testStatusVariable013() {
		try {
			tbc.log("#testStatusVariable013");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_FLOAT_VALUE);

			float val = sv.getFloat();
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"getDouble() result",
							MonitorConstants.SV_FLOAT_VALUE + "" }),
					MonitorConstants.SV_FLOAT_VALUE, val, 0);

			String id = sv.getID();
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"getID() result", MonitorConstants.SV_NAME1 }),
					MonitorConstants.SV_NAME1, id);

			int type = sv.getType();
			tbc
					.assertEquals(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_EQUALS,
											new String[] {
													"getType results",
													(org.osgi.service.monitor.StatusVariable.TYPE_FLOAT + "") }),
							org.osgi.service.monitor.StatusVariable.TYPE_FLOAT,
							type);

			int method = sv.getCollectionMethod();
			tbc
					.assertEquals(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_EQUALS,
											new String[] {
													"GetCollectionMethod() results",
													org.osgi.service.monitor.StatusVariable.CM_CC
															+ "", }),
							org.osgi.service.monitor.StatusVariable.CM_CC,
							method);

			tbc.assertNotNull(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_NULL,
					new String[] { "getTimeStamp()" }), sv.getTimeStamp());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if the values passed as parameter are equal to the
	 * corresponding values returned from get methods
	 * 
	 * @spec StatusVariable.StatusVariable(String, int, int)
	 */
	private void testStatusVariable014() {
		try {
			tbc.log("#testStatusVariable014");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_INT_VALUE);

			int val = sv.getInteger();
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"getLong() results",
							MonitorConstants.SV_INT_VALUE + "", }),
					MonitorConstants.SV_INT_VALUE, val);

			String id = sv.getID();
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"getID() results", MonitorConstants.SV_NAME1 }),
					MonitorConstants.SV_NAME1, id);

			int type = sv.getType();
			tbc
					.assertEquals(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_EQUALS,
											new String[] {
													"getType() results",
													org.osgi.service.monitor.StatusVariable.TYPE_INTEGER
															+ "", }),
							org.osgi.service.monitor.StatusVariable.TYPE_INTEGER,
							type);

			int method = sv.getCollectionMethod();
			tbc
					.assertEquals(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_EQUALS,
											new String[] {
													"getCollectionMethod() results",
													org.osgi.service.monitor.StatusVariable.CM_CC
															+ "" }),
							org.osgi.service.monitor.StatusVariable.CM_CC,
							method);

			tbc.assertNotNull(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_NULL,
					new String[] { "getTimeStamp()" }), sv.getTimeStamp());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if the values passed as parameter are equal to the
	 * corresponding values returned from get methods
	 * 
	 * @spec StatusVariable.StatusVariable(String, int, String)
	 */
	private void testStatusVariable015() {
		try {
			tbc.log("#testStatusVariable015");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_STRING_VALUE);

			String val = sv.getString();
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"getString() results",
							MonitorConstants.SV_STRING_VALUE }),
					MonitorConstants.SV_STRING_VALUE, val);

			String id = sv.getID();
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"getID() results", MonitorConstants.SV_NAME1 }),
					MonitorConstants.SV_NAME1, id);

			int type = sv.getType();
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"getType() results",
							org.osgi.service.monitor.StatusVariable.TYPE_STRING
									+ "" }),
					org.osgi.service.monitor.StatusVariable.TYPE_STRING, type);

			int method = sv.getCollectionMethod();
			tbc
					.assertEquals(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_EQUALS,
											new String[] {
													"getCollectionMethod() results",
													org.osgi.service.monitor.StatusVariable.CM_CC
															+ "" }),
							org.osgi.service.monitor.StatusVariable.CM_CC,
							method);

			tbc.assertNotNull(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_NULL,
					new String[] { "getTimeStamp()" }), sv.getTimeStamp());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if the values passed as parameter are equal to the
	 * corresponding values returned from get methods
	 * 
	 * @spec StatusVariable.StatusVariable(String, int, boolean)
	 */
	private void testStatusVariable016() {
		try {
			tbc.log("#testStatusVariable016");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_BOOLEAN_VALUE);

			boolean val = sv.getBoolean();
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"getBoolean() results",
							MonitorConstants.SV_BOOLEAN_VALUE + "" }),
					MonitorConstants.SV_BOOLEAN_VALUE, val);

			String id = sv.getID();
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] {
							"getID() results", MonitorConstants.SV_NAME1 }),
					MonitorConstants.SV_NAME1, id);

			int type = sv.getType();
			tbc
					.assertEquals(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_EQUALS,
											new String[] {
													"getType() results",
													org.osgi.service.monitor.StatusVariable.TYPE_BOOLEAN
															+ "", }),
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
													org.osgi.service.monitor.StatusVariable.CM_CC
															+ "", }),
							org.osgi.service.monitor.StatusVariable.CM_CC,
							method);

			tbc.assertNotNull(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_NULL,
					new String[] { "getTimeStamp()" }), sv.getTimeStamp());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if an IllegalStateException is thrown when call
	 * getInteger instead of getFloat
	 * 
	 * @spec StatusVariable.getInteger()
	 */
	private void testStatusVariable017() {
		try {
			tbc.log("#testStatusVariable017");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_FLOAT_VALUE);
			sv.getInteger();
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
	 * This method asserts if an IllegalStateException is thrown when call
	 * getFloat instead of getInteger
	 * 
	 * @spec StatusVariable.getFloat()
	 */
	private void testStatusVariable018() {
		try {
			tbc.log("#testStatusVariable018");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_INT_VALUE);
			sv.getFloat();
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
	 * This method asserts if an IllegalStateException is thrown when call
	 * getString instead of getInteger
	 * 
	 * @spec StatusVariable.getString()
	 */
	private void testStatusVariable019() {
		try {
			tbc.log("#testStatusVariable019");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_INT_VALUE);
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
	 * This method asserts if an IllegalStateException is thrown when call
	 * getBoolean instead of getInteger
	 * 
	 * @spec StatusVariable.getBoolean()
	 */
	private void testStatusVariable020() {
		try {
			tbc.log("#testStatusVariable020");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_INT_VALUE);
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
	 * This method asserts if an IllegalStateException is thrown when call
	 * getString instead of getFloat
	 * 
	 * @spec StatusVariable.getString()
	 */
	private void testStatusVariable021() {
		try {
			tbc.log("#testStatusVariable021");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_FLOAT_VALUE);
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
	 * This method asserts if an IllegalStateException is thrown when call
	 * getBoolean instead of getFloat
	 * 
	 * @spec StatusVariable.getBoolean()
	 */
	private void testStatusVariable022() {
		try {
			tbc.log("#testStatusVariable022");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_FLOAT_VALUE);
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
	 * This method asserts if an IllegalStateException is thrown when call
	 * getFloat instead of getString
	 * 
	 * @spec StatusVariable.getFloat()
	 */
	private void testStatusVariable023() {
		try {
			tbc.log("#testStatusVariable023");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_STRING_VALUE);
			sv.getFloat();
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
	 * This method asserts if an IllegalStateException is thrown when call
	 * getInteger instead of getString
	 * 
	 * @spec StatusVariable.getInteger()
	 */
	private void testStatusVariable024() {
		try {
			tbc.log("#testStatusVariable024");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_STRING_VALUE);
			sv.getInteger();
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
	 * This method asserts if an IllegalStateException is thrown when call
	 * getBoolean instead of getString
	 * 
	 * @spec StatusVariable.getBoolean()
	 */
	private void testStatusVariable025() {
		try {
			tbc.log("#testStatusVariable025");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_STRING_VALUE);
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
	 * This method asserts if an IllegalStateException is thrown when call
	 * getInteger instead of getBoolean
	 * 
	 * @spec StatusVariable.getInteger()
	 */
	private void testStatusVariable026() {
		try {
			tbc.log("#testStatusVariable026");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_BOOLEAN_VALUE);
			sv.getInteger();
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
	 * This method asserts if an IllegalStateException is thrown when call
	 * getFloat instead of getBoolean
	 * 
	 * @spec StatusVariable.getFloat()
	 */
	private void testStatusVariable027() {
		try {
			tbc.log("#testStatusVariable027");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_BOOLEAN_VALUE);
			sv.getFloat();
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
	 * This method asserts if an IllegalStateException is thrown when call
	 * getString instead of getBoolean
	 * 
	 * @spec StatusVariable.getString()
	 */
	private void testStatusVariable028() {
		try {
			tbc.log("#testStatusVariable028");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_BOOLEAN_VALUE);
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
	 * This method asserts if an IllegalArgumentException is thrown when passes
	 * an empty string for id parameter
	 * 
	 * @spec StatusVariable.StatusVariable(String, int, boolean)
	 */
	private void testStatusVariable029() {
		try {
			tbc.log("#testStatusVariable029");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					"", org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_BOOLEAN_VALUE);
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
	 * This method asserts if an IllegalArgumentException is thrown when passes
	 * an empty string for id parameter
	 * 
	 * @spec StatusVariable.StatusVariable(String, int, float)
	 */
	private void testStatusVariable030() {
		try {
			tbc.log("#testStatusVariable030");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					"", org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_FLOAT_VALUE);
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
	 * This method asserts if an IllegalArgumentException is thrown when passes
	 * an empty string for id parameter
	 * 
	 * @spec StatusVariable.StatusVariable(String, int, int)
	 */
	private void testStatusVariable031() {
		try {
			tbc.log("#testStatusVariable031");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					"", org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_INT_VALUE);
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
	 * This method asserts if an IllegalArgumentException is thrown when passes
	 * an empty string for id parameter
	 * 
	 * @spec StatusVariable.StatusVariable(String, int, String)
	 */
	private void testStatusVariable032() {
		try {
			tbc.log("#testStatusVariable032");
			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					"", org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_STRING_VALUE);
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
	 * This method asserts if an IllegalArgumentException is thrown if id
	 * parameter exceeds 20 characters.
	 * 
	 * @spec StatusVariable.StatusVariable(String, int, float)
	 */
	private void testStatusVariable033() {
		try {
			tbc.log("#testStatusVariable033");
			new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.LONGID,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_FLOAT_VALUE);
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
	 * This method asserts if an IllegalArgumentException is thrown if id
	 * parameter exceeds 20 characters.
	 * 
	 * @spec StatusVariable.StatusVariable(String, int, boolean)
	 */
	private void testStatusVariable034() {
		try {
			tbc.log("#testStatusVariable034");
			new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.LONGID,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_BOOLEAN_VALUE);
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
	 * This method asserts if an IllegalArgumentException is thrown if id
	 * parameter exceeds 20 characters.
	 * 
	 * @spec StatusVariable.StatusVariable(String, int, int)
	 */
	private void testStatusVariable035() {
		try {
			tbc.log("#testStatusVariable035");
			new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.LONGID,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_INT_VALUE);
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
	 * This method asserts if an IllegalArgumentException is thrown if id
	 * parameter exceeds 20 characters.
	 * 
	 * @spec StatusVariable.StatusVariable(String, int, String)
	 */
	private void testStatusVariable036() {
		try {
			tbc.log("#testStatusVariable036");
			new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.LONGID,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_STRING_VALUE);
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
	 * This method asserts if no exception is thrown when passing valid
	 * parameters.
	 * 
	 * @spec StatusVariable.StatusVariable(String, int, String)
	 */
	private void testStatusVariable037() {
		try {
			tbc.log("#testStatusVariable037");
			new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.INEXISTENT_MONITORABLE,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_STRING_VALUE);
			tbc.pass("Passed. No exception was thrown.");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if no exception is thrown when passing valid
	 * parameters.
	 * 
	 * @spec StatusVariable.StatusVariable(String, int, boolean)
	 */
	private void testStatusVariable038() {
		try {
			tbc.log("#testStatusVariable038");
			new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.INEXISTENT_MONITORABLE,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_BOOLEAN_VALUE);
			tbc.pass("Passed. No exception was thrown.");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if no exception is thrown when passing valid
	 * parameters.
	 * 
	 * @spec StatusVariable.StatusVariable(String, int, float)
	 */
	private void testStatusVariable039() {
		try {
			tbc.log("#testStatusVariable039");
			new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.INEXISTENT_MONITORABLE,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_FLOAT_VALUE);
			tbc.pass("Passed. No exception was thrown.");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if no exception is thrown when passing valid
	 * parameters.
	 * 
	 * @spec StatusVariable.StatusVariable(String, int, int)
	 */
	private void testStatusVariable040() {
		try {
			tbc.log("#testStatusVariable040");
			new org.osgi.service.monitor.StatusVariable(
					MonitorConstants.INEXISTENT_MONITORABLE,
					org.osgi.service.monitor.StatusVariable.CM_CC,
					MonitorConstants.SV_INT_VALUE);
			tbc.pass("Passed. No exception was thrown.");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

}

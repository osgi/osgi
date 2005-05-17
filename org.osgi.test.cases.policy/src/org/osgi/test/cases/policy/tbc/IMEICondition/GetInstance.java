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
 * 05/04/2005   Leonardo Barros
 * 33           Implement MEG TCK
 * ===========  ==============================================================
 * 12/05/2005   Eduardo Oliveira
 * 33           Updates after inspection JSTD-MEGTCK-CODE-INSP014 
 * ===========  ==============================================================
 */

package org.osgi.test.cases.policy.tbc.IMEICondition;

import org.osgi.service.condpermadmin.Condition;
import org.osgi.test.cases.policy.tbc.PolicyTestControl;
import org.osgi.test.cases.policy.tbc.util.MessagesConstants;
import org.osgi.util.gsm.IMEICondition;

/**
 * @methodUnderTest org.osgi.util.gsm.IMEICondition#getInstance
 * @generalDescription This class tests getInstance method according with MEG
 *                     specification (rfc0092)
 */
public class GetInstance {
	private PolicyTestControl tbc;

	public GetInstance(PolicyTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetInstance001();
		testGetInstance002();
		testGetInstance003();
		testGetInstance004();
		testGetInstance005();
		testGetInstance006();
		testGetInstance007();
		testGetInstance008();
	}



	/**
	 * @testID testGetInstance001
	 * @testDescription Asserts if a condition is sucessfully created when imei
	 *                  parameter is a valid code
	 */
	public void testGetInstance001() {
		tbc.log("#testGetInstance001");
        try {
			Condition cond = IMEICondition.getInstance(
					PolicyTestControl.TEST_BUNDLE,
					PolicyTestControl.IMEI_VALID_CODE);

			tbc.assertNotNull(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_NOT_NULL,
					new String[] { "created condition" }), cond);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		}
	}

	/**
	 * @testID testGetInstance002
	 * @testDescription Asserts if NullPointerException is thrown when null is
	 *                  passed for the first parameter
	 */
	public void testGetInstance002() {
        tbc.log("#testGetInstance002");
        try {
			Condition cond = IMEICondition.getInstance(null,
					PolicyTestControl.IMEI_VALID_CODE);

			tbc.failException("", NullPointerException.class);
		} catch (NullPointerException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { e.getClass().getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							NullPointerException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testGetInstance003
	 * @testDescription Asserts if NullPointerException is thrown when null is
	 *                  passed for the second parameter
	 */
	public void testGetInstance003() {
        tbc.log("#testGetInstance003");
        try {
			Condition cond = IMEICondition.getInstance(
					PolicyTestControl.TEST_BUNDLE, null);

			tbc.failException("", NullPointerException.class);
		} catch (NullPointerException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { e.getClass().getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							NullPointerException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testGetInstance004
	 * @testDescription Asserts if IllegalArgumentException is thrown if the
	 *                  imei is not a string of 15 digits
	 */
	public void testGetInstance004() {
        tbc.log("#testGetInstance004");
        try {
			Condition cond = IMEICondition.getInstance(
					PolicyTestControl.TEST_BUNDLE,
					PolicyTestControl.INVALID_CODE);

			tbc.failException("", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { e.getClass().getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testGetInstance005
	 * @testDescription Asserts if IllegalArgumentException is thrown if the
	 *                  imei is not a string of 15 digits
	 */
	public void testGetInstance005() {
        tbc.log("#testGetInstance005");
        try {
			Condition cond = IMEICondition.getInstance(
					PolicyTestControl.TEST_BUNDLE,
					PolicyTestControl.IMEI_LESS_DIGIT_CODE);

			tbc.failException("", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { e.getClass().getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		}
	}
	
	/**
	 * @testID testGetInstance006
	 * @testDescription Asserts if IllegalArgumentException is thrown if the
	 *                  imei is not a string of 15 digits
	 */
	public void testGetInstance006() {
        tbc.log("#testGetInstance006");
        try {
			Condition cond = IMEICondition.getInstance(
					PolicyTestControl.TEST_BUNDLE,
					PolicyTestControl.IMEI_MORE_DIGIT_CODE);

			tbc.failException("", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { e.getClass().getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testGetInstance007
	 * @testDescription Asserts if a condition is sucessfully created when imei
	 *                  parameter is a char code
	 */
	public void testGetInstance007() {
        tbc.log("#testGetInstance007");
        try {
			Condition cond = IMEICondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					PolicyTestControl.IMEI_CHAR_CODE);
	
            tbc.failException("", IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            tbc.pass(MessagesConstants.getMessage(
                    MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
                    new String[] { e.getClass().getName() }));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.EXCEPTION_THROWN, new String[] {
                            IllegalArgumentException.class.getName(),
                            e.getClass().getName() }));
        }
	}

	/**
	 * @testID testGetInstance008
	 * @testDescription Asserts if a condition is sucessfully created when imei
	 *                  parameter is equals to IMEI phone code
	 */
	public void testGetInstance008() {
        tbc.log("#testGetInstance008");
        try {
			Condition cond = IMEICondition.getInstance(
					PolicyTestControl.TEST_BUNDLE,
					PolicyTestControl.IMEI_PHONE_CODE);

            tbc.failException("", IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            tbc.pass(MessagesConstants.getMessage(
                    MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
                    new String[] { e.getClass().getName() }));
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.EXCEPTION_THROWN, new String[] {
                            IllegalArgumentException.class.getName(),
                            e.getClass().getName() }));
        }
	}
	
}

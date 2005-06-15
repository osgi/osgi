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
 * 31/05/2005   Eduardo Oliveira
 * 101          Implement MEG TCK 
 * ===========  ==============================================================
 */

package org.osgi.test.cases.policy.tbc.IMEICondition;

import org.osgi.service.condpermadmin.Condition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.test.cases.policy.tbc.PolicyTestControl;
import org.osgi.test.cases.policy.tbc.util.MessagesConstants;
import org.osgi.util.gsm.IMEICondition;

/**
 * @methodUnderTest org.osgi.util.gsm.IMEICondition#getCondition
 * @generalDescription This class tests getCondition method according with MEG
 *                     specification (rfc0092)
 */
public class GetCondition {
    private PolicyTestControl tbc;

    public GetCondition(PolicyTestControl tbc) {
        this.tbc = tbc;
    }

    public void run() {
        testGetCondition001();
        testGetCondition002();
        testGetCondition003();
        testGetCondition004();
        testGetCondition005();
        testGetCondition006();
        testGetCondition007();
        testGetCondition008();
    }



    /**
     * @testID testGetCondition001
     * @testDescription Asserts if a condition is sucessfully created when imei
     *                  parameter is a valid code
     */
    public void testGetCondition001() {
        tbc.log("#testGetCondition001");
        try {
            Condition cond = IMEICondition.getCondition(
                    PolicyTestControl.TEST_BUNDLE,
                    new ConditionInfo("org.osgi.util.gsm.IMEICondition",new String[]{PolicyTestControl.IMEI_VALID_CODE}));

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
     * @testID testGetCondition002
     * @testDescription Asserts if NullPointerException is thrown when null is
     *                  passed for the first parameter
     */
    public void testGetCondition002() {
        tbc.log("#testGetCondition002");
        try {
            Condition cond = IMEICondition.getCondition(null,
                    new ConditionInfo("org.osgi.util.gsm.IMEICondition",new String[]{PolicyTestControl.IMEI_VALID_CODE}));

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
     * @testID testGetCondition003
     * @testDescription Asserts if NullPointerException is thrown when null is
     *                  passed for the second parameter
     */
    public void testGetCondition003() {
        tbc.log("#testGetCondition003");
        try {
            Condition cond = IMEICondition.getCondition(
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
     * @testID testGetCondition004
     * @testDescription Asserts if IllegalArgumentException is thrown if the
     *                  imei code is not a valid string
     */
    public void testGetCondition004() {
        tbc.log("#testGetCondition004");
        try {
            Condition cond = IMEICondition.getCondition(
                    PolicyTestControl.TEST_BUNDLE,
                    new ConditionInfo("org.osgi.util.gsm.IMEICondition",new String[]{PolicyTestControl.INVALID_CODE}));

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
     * @testID testGetCondition005
     * @testDescription Asserts if IllegalArgumentException is thrown if the
     *                  imei code is a string with less than 15 digits
     */
    public void testGetCondition005() {
        tbc.log("#testGetCondition005");
        try {
            Condition cond = IMEICondition.getCondition(
                    PolicyTestControl.TEST_BUNDLE,
                    new ConditionInfo("org.osgi.util.gsm.IMEICondition",new String[]{PolicyTestControl.IMEI_LESS_DIGIT_CODE}));

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
     * @testID testGetCondition006
     * @testDescription Asserts if IllegalArgumentException is thrown if the
     *                  imei code is a string with more than 15 digits
     */
    public void testGetCondition006() {
        tbc.log("#testGetCondition006");
        try {
            Condition cond = IMEICondition.getCondition(
                    PolicyTestControl.TEST_BUNDLE,
                    new ConditionInfo("org.osgi.util.gsm.IMEICondition",new String[]{PolicyTestControl.IMEI_MORE_DIGIT_CODE}));

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
     * @testID testGetCondition007
     * @testDescription Asserts if a condition is not created when imei
     *                  parameter is a char code
     */
    public void testGetCondition007() {
        tbc.log("#testGetCondition007");
        try {
            Condition cond = IMEICondition.getCondition(PolicyTestControl.TEST_BUNDLE,
                    new ConditionInfo("org.osgi.util.gsm.IMEICondition",new String[]{PolicyTestControl.IMEI_CHAR_CODE}));
    
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
     * @testID testGetCondition008
     * @testDescription Asserts if IllegalArgumentException is thrown if the
     *                  imei code is a string with hyphens
     */
    public void testGetCondition008() {
        tbc.log("#testGetCondition008");
        try {
            Condition cond = IMEICondition.getCondition(
                    PolicyTestControl.TEST_BUNDLE,
                    new ConditionInfo("org.osgi.util.gsm.IMEICondition",new String[]{PolicyTestControl.IMEI_INVALID_CODE}));

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

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
 * 02/05/2005   Eduardo Oliveira
 * 101          Implement MEG TCK
 * ===========  ==============================================================
 */
package org.osgi.test.cases.policy.tbc.TransferCostCondition;

import org.osgi.service.condpermadmin.Condition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.test.cases.policy.tbc.PolicyTestControl;
import org.osgi.test.cases.policy.tbc.util.MessagesConstants;
import org.osgi.util.mobile.TransferCostCondition;

/**
 * This Test Class Validates the implementation of
 * <code>isPostponed<code> method, according to MEG
 * reference documentation.
 */
public class IsPostponed {

    private PolicyTestControl tbc;

    public IsPostponed(PolicyTestControl tbc) {
        this.tbc = tbc;
    }

    public void run() {
        testIsPostponed001();
        testIsPostponed002();
        testIsPostponed003();

    }


    /**
     * This test asserts if condition evaluation is postponed with HIGH parameter, 
     * because isSatisfied() cannot give answer instantly.
     *
     * @spec TransferCostCondition.isPostponed()
     */

    public void testIsPostponed001() {
        tbc.log("#testIsMutable001");
        try {
            Condition cond = TransferCostCondition.getCondition(PolicyTestControl.TEST_BUNDLE,
                    new ConditionInfo("",new String[]{TransferCostCondition.HIGH}));

            tbc.assertTrue("Asserting if isEvaluated() returns answer instantly",!cond.isPostponed());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
                            .getClass().getName() }));
        }
    }


    /**
     * This test asserts if condition evaluation is postponed with MEDIUM parameter,
     * because isSatisfied() cannot give answer instantly.
     *
     * @spec TransferCostCondition.isPostponed()
     */

    public void testIsPostponed002() {
        tbc.log("#testIsMutable002");
        try {
            Condition cond = TransferCostCondition.getCondition(PolicyTestControl.TEST_BUNDLE,
                    new ConditionInfo("",new String[]{TransferCostCondition.MEDIUM}));

            tbc.assertTrue("Asserting if isEvaluated() returns answer instantly",!cond.isPostponed());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
                            .getClass().getName() }));
        }
    }
    
           
    /**
     * This test asserts if condition evaluation is postponed with LOW parameter, 
     * because isSatisfied() cannot give answer instantly.
     *
     * @spec TransferCostCondition.isPostponed()
     */

    public void testIsPostponed003() {
        tbc.log("#testIsMutable003");
        try {
            Condition cond = TransferCostCondition.getCondition(PolicyTestControl.TEST_BUNDLE,
                    new ConditionInfo("",new String[]{TransferCostCondition.LOW}));

            tbc.assertTrue("Asserting if isEvaluated() returns answer instantly",!cond.isPostponed());
        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
                            .getClass().getName() }));
        }
    }
    
}

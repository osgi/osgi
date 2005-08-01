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
 * 06/04/2005   Leonardo Barros
 * 33           Implement MEG TCK
 * ===========  ==============================================================
 * 13/04/2005   Eduardo Oliveira
 * 33           Implement MEG TCK
 * ===========  ==============================================================
 * 31/05/2005   Eduardo Oliveira
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
 * <code>setTransferCost<code> method, according to MEG
 * reference documentation.
 */
public class SetTransferCost {

	private PolicyTestControl tbc;

	public SetTransferCost(PolicyTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testSetTransferCost001();
		testSetTransferCost002();
		testSetTransferCost003();
		testSetTransferCost004();
		testSetTransferCost005();
		testSetTransferCost006();
		testSetTransferCost007();
		testSetTransferCost008();
		testSetTransferCost009();
		testSetTransferCost010();
	}


    /**
     * This test asserts if isSatisfied method returns true if
     * setTransferCost method is called with a value equals to
     * the current condition value
     *
     * @spec TransferCostCondition.setTransferCost(String)
     */

	public void testSetTransferCost001() {
        tbc.log("#testSetTransferCost001");
		try {
			Condition cond = TransferCostCondition.getCondition(PolicyTestControl.TEST_BUNDLE,
                    new ConditionInfo("",new String[] {TransferCostCondition.LOW}));

			TransferCostCondition.setTransferCost(TransferCostCondition.LOW);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "isSatisfied method returns true after setTransferCost method is called with a value equals to the current condition value" }),
							cond.isSatisfied());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}


    /**
     * This test asserts if isSatisfied method returns true if
     * setTransferCost method is called with a value equals to
     * the current condition value
     *
     * @spec TransferCostCondition.setTransferCost(String)
     */

	public void testSetTransferCost002() {
        tbc.log("#testSetTransferCost002");
		try {
			Condition cond = TransferCostCondition.getCondition(PolicyTestControl.TEST_BUNDLE,
                    new ConditionInfo("",new String[] {TransferCostCondition.MEDIUM}));

			TransferCostCondition.setTransferCost(TransferCostCondition.MEDIUM);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "isSatisfied method returns true after setTransferCost method is called with a value equals to the current condition value" }),
							cond.isSatisfied());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}


    /**
     * This test asserts if isSatisfied method returns true if
     * setTransferCost method is called with a value equals to
     * the current condition value
     *
     * @spec TransferCostCondition.setTransferCost(String)
     */

	public void testSetTransferCost003() {
        tbc.log("#testSetTransferCost003");
		try {
			Condition cond = TransferCostCondition.getCondition(PolicyTestControl.TEST_BUNDLE,
                    new ConditionInfo("",new String[] {TransferCostCondition.HIGH}));

			TransferCostCondition.setTransferCost(TransferCostCondition.HIGH);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "isSatisfied method returns true after setTransferCost method is called with a value equals to the current condition value" }),
							cond.isSatisfied());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}


    /**
     * This test asserts if isSatisfied method returns true if
     * setTransferCost method is called with a value higher to
     * the current condition value
     *
     * @spec TransferCostCondition.setTransferCost(String)
     */

	public void testSetTransferCost004() {
        tbc.log("#testSetTransferCost004");
		try {
			Condition cond = TransferCostCondition.getCondition(PolicyTestControl.TEST_BUNDLE,
                    new ConditionInfo("",new String[] {TransferCostCondition.MEDIUM}));

			TransferCostCondition.setTransferCost(TransferCostCondition.LOW);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "isSatisfied method returns true if setTransferCost method is called with a value higher to the current condition value" }),
							cond.isSatisfied());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}


    /**
     * This test asserts if isSatisfied method returns true if
     * setTransferCost method is called with a value higher to
     * the current condition value
     *
     * @spec TransferCostCondition.setTransferCost(String)
     */

	public void testSetTransferCost005() {
        tbc.log("#testSetTransferCost005");
		try {
			Condition cond = TransferCostCondition.getCondition(PolicyTestControl.TEST_BUNDLE,
                    new ConditionInfo("",new String[] {TransferCostCondition.HIGH}));

			TransferCostCondition.setTransferCost(TransferCostCondition.LOW);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "isSatisfied method returns true if setTransferCost method is called with a value higher to the current condition value" }),
							cond.isSatisfied());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}


    /**
     * This test asserts if isSatisfied method returns true if
     * setTransferCost method is called with a value higher to
     * the current condition value
     *
     * @spec TransferCostCondition.setTransferCost(String)
     */

	public void testSetTransferCost006() {
        tbc.log("#testSetTransferCost006");
		try {
			Condition cond = TransferCostCondition.getCondition(PolicyTestControl.TEST_BUNDLE,
                    new ConditionInfo("",new String[] {TransferCostCondition.HIGH}));

			TransferCostCondition.setTransferCost(TransferCostCondition.MEDIUM);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "isSatisfied method returns true if setTransferCost method is called with a value higher to the current condition value" }),
							cond.isSatisfied());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}


    /**
     * This test asserts if isSatisfied method returns false if
     * setTransferCost method is called with a value lower to
     * the current condition value
     *
     * @spec TransferCostCondition.setTransferCost(String)
     */

	public void testSetTransferCost007() {
        tbc.log("#testSetTransferCost007");
		try {
			Condition cond = TransferCostCondition.getCondition(PolicyTestControl.TEST_BUNDLE,
                    new ConditionInfo("",new String[] {TransferCostCondition.LOW}));

			TransferCostCondition.setTransferCost(TransferCostCondition.MEDIUM);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "isSatisfied method returns false after setTransferCost method is called with a value lower to the current condition value" }),
							!cond.isSatisfied());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}


    /**
     * This test asserts if isSatisfied method returns false if
     * setTransferCost method is called with a value lower to
     * the current condition value
     *
     * @spec TransferCostCondition.setTransferCost(String)
     */

	public void testSetTransferCost008() {
        tbc.log("#testSetTransferCost008");
        try {
			Condition cond = TransferCostCondition.getCondition(PolicyTestControl.TEST_BUNDLE,
                    new ConditionInfo("",new String[] {TransferCostCondition.LOW}));

			TransferCostCondition.setTransferCost(TransferCostCondition.HIGH);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "isSatisfied method returns false after setTransferCost method is called with a value lower to the current condition value" }),
							!cond.isSatisfied());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}


    /**
     * This test asserts if isSatisfied method returns false if
     * setTransferCost method is called with a value lower to
     * the current condition value
     *
     * @spec TransferCostCondition.setTransferCost(String)
     */

	public void testSetTransferCost009() {
        tbc.log("#testSetTransferCost009");
		try {
			Condition cond = TransferCostCondition.getCondition(PolicyTestControl.TEST_BUNDLE,
                    new ConditionInfo("",new String[] {TransferCostCondition.MEDIUM}));

			TransferCostCondition.setTransferCost(TransferCostCondition.HIGH);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "isSatisfied method returns false after setTransferCost method is called with a value lower to the current condition value" }),
							!cond.isSatisfied());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}

    /**
     * This test asserts if an IllegalArgumentException is throw
     * if the cost parameter is not from the possible values.
     *
     * @spec TransferCostCondition.setTransferCost(String)
     */

	public void testSetTransferCost010() {
        tbc.log("#testSetTransferCost010");
		try {
			Condition cond = TransferCostCondition.getCondition(PolicyTestControl.TEST_BUNDLE,
					new ConditionInfo("",new String[] {TransferCostCondition.HIGH}));

			TransferCostCondition.setTransferCost(PolicyTestControl.INVALID_COST_LIMIT);

			tbc.failException("#", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { e.getClass().getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}
	
	
}

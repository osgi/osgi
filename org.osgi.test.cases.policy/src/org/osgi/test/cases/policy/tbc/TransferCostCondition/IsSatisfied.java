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
 * 13/05/2005   Eduardo Oliveira
 * 33           Updates after inspection JSTD-MEGTCK-CODE-INSP014 
 * ===========  ==============================================================
 */
package org.osgi.test.cases.policy.tbc.TransferCostCondition;

import org.osgi.service.condpermadmin.Condition;
import org.osgi.test.cases.policy.tbc.PolicyTestControl;
import org.osgi.test.cases.policy.tbc.util.MessagesConstants;
import org.osgi.util.mobile.TransferCostCondition;

/**
 * @methodUnderTest org.osgi.util.mobile.TransferCostCondition#IsSatisfied
 * @generalDescription This class tests isEvaluated method according with MEG
 *                     specification (rfc0092)
 */
public class IsSatisfied {
	private PolicyTestControl tbc;

	public IsSatisfied(PolicyTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testIsSatisfied001();
		testIsSatisfied002();
		testIsSatisfied003();
		testIsSatisfied004();
		testIsSatisfied005();
		testIsSatisfied006();
		testIsSatisfied007();
		testIsSatisfied008();
		testIsSatisfied009();
		testIsSatisfied010();
		testIsSatisfied011();
		testIsSatisfied012();
		testIsSatisfied013();
		testIsSatisfied014();
		testIsSatisfied015();
		testIsSatisfied016();
		testIsSatisfied017();
		testIsSatisfied018();
        testIsSatisfied019();
        testIsSatisfied020();
        testIsSatisfied021();
        testIsSatisfied022();
        testIsSatisfied023();
        testIsSatisfied024();
        testIsSatisfied025();
        testIsSatisfied026();
        testIsSatisfied027();
	}

/**
	 * @testID testIsSatisfied001
	 * @testDescription Asserts if isSatisfied method returns true when all
	 *                  transfer costs passed as parameter are equal to the
	 *                  thread cost value
	 * 
	 */
	public void testIsSatisfied001() {
        tbc.log("#testIsSatisfied001");
        try {
			TransferCostCondition.setTransferCost(TransferCostCondition.HIGH);

			Condition cond1 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.HIGH);

			Condition cond2 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.HIGH);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "Asserts if all transfer costs are equal to local thread cost value" }),
							cond1.isSatisfied(new Condition[] { cond1, cond2 },
									null));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}

	/**
	 * @testID testIsSatisfied002
	 * @testDescription Asserts if isSatisfied method returns true when all
	 *                  transfer costs passed as parameter are equal to the
	 *                  thread cost value
	 * 
	 */
	public void testIsSatisfied002() {
        tbc.log("#testIsSatisfied002");
		try {
			TransferCostCondition.setTransferCost(TransferCostCondition.MEDIUM);

			Condition cond1 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.MEDIUM);

			Condition cond2 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.MEDIUM);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "Asserts if all transfer costs are equal to local thread cost value" }),
							cond1.isSatisfied(new Condition[] { cond1, cond2 },
									null));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}

	/**
	 * @testID testIsSatisfied003
	 * @testDescription Asserts if isSatisfied method returns true when all
	 *                  transfer costs passed as parameter are equal to the
	 *                  thread cost value
	 * 
	 */
	public void testIsSatisfied003() {
        tbc.log("#testIsSatisfied003");
		try {
			TransferCostCondition.setTransferCost(TransferCostCondition.LOW);

			Condition cond1 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.LOW);

			Condition cond2 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.LOW);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "Asserts if all transfer costs are equal to local thread cost value" }),
							cond1.isSatisfied(new Condition[] { cond1, cond2 },
									null));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}

	/**
	 * @testID testIsSatisfied004
	 * @testDescription Asserts if isSatisfied method returns true when all
	 *                  transfer costs passed as parameter are higher than the
	 *                  thread cost value
	 * 
	 */
	public void testIsSatisfied004() {
        tbc.log("#testIsSatisfied004");
        try {
			TransferCostCondition.setTransferCost(TransferCostCondition.LOW);

			Condition cond1 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.MEDIUM);

			Condition cond2 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.MEDIUM);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "Asserts if isSatisfied method returns true when all transfer costs passed as parameter are higher than the thread cost value" }),
							cond1.isSatisfied(new Condition[] { cond1, cond2 },
									null));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}

	/**
	 * @testID testIsSatisfied005
	 * @testDescription Asserts if isSatisfied method returns true when all
	 *                  transfer costs passed as parameter are higher than the
	 *                  thread cost value
	 * 
	 */
	public void testIsSatisfied005() {
        tbc.log("#testIsSatisfied005");
		try {
			TransferCostCondition.setTransferCost(TransferCostCondition.MEDIUM);

			Condition cond1 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.HIGH);

			Condition cond2 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.HIGH);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "Asserts if isSatisfied method returns true when all transfer costs passed as parameter are higher than the thread cost value" }),
							cond1.isSatisfied(new Condition[] { cond1, cond2 },
									null));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}

	/**
	 * @testID testIsSatisfied006
	 * @testDescription Asserts if isSatisfied method returns true when all
	 *                  transfer costs passed as parameter are higher than the
	 *                  thread cost value
	 * 
	 */
	public void testIsSatisfied006() {
        tbc.log("#testIsSatisfied006");
		try {
			TransferCostCondition.setTransferCost(TransferCostCondition.LOW);

			Condition cond1 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.HIGH);

			Condition cond2 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.HIGH);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "Asserts if isSatisfied method returns true when all transfer costs passed as parameter are higher than the thread cost value" }),
							cond1.isSatisfied(new Condition[] { cond1, cond2 },
									null));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}

	/**
	 * @testID testIsSatisfied007
	 * @testDescription Asserts if isSatisfied method returns true when the
	 *                  transfer costs passed as parameter are higher than or
	 *                  equal the thread cost value
	 * 
	 */
	public void testIsSatisfied007() {
        tbc.log("#testIsSatisfied007");
		try {
			TransferCostCondition.setTransferCost(TransferCostCondition.LOW);

			Condition cond1 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.LOW);

			Condition cond2 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.MEDIUM);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "Asserts if isSatisfied method returns true when all transfer costs passed as parameter are higher than or equal the thread cost value" }),
							cond1.isSatisfied(new Condition[] { cond1, cond2 },
									null));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}

	/**
	 * @testID testIsSatisfied008
	 * @testDescription Asserts if isSatisfied method returns true when the
	 *                  transfer costs passed as parameter are higher than or
	 *                  equal the thread cost value
	 * 
	 */
	public void testIsSatisfied008() {
        tbc.log("#testIsSatisfied008");
		try {
			TransferCostCondition.setTransferCost(TransferCostCondition.MEDIUM);

			Condition cond1 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.HIGH);

			Condition cond2 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.MEDIUM);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "Asserts if isSatisfied method returns true when the transfer costs passed as parameter are higher than or equal the thread cost value" }),
							cond1.isSatisfied(new Condition[] { cond1, cond2 },
									null));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}

	/**
	 * @testID testIsSatisfied009
	 * @testDescription Asserts if isSatisfied method returns false when all
	 *                  transfer costs passed as parameter are lower than the
	 *                  thread cost value
	 * 
	 */
	public void testIsSatisfied009() {
        tbc.log("#testIsSatisfied009");
		try {
			TransferCostCondition.setTransferCost(TransferCostCondition.MEDIUM);

			Condition cond1 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.LOW);

			Condition cond2 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.LOW);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "Asserts if isSatisfied method returns false when all transfer costs passed as parameter are lower than the thread cost value" }),
							!cond1.isSatisfied(
									new Condition[] { cond1, cond2 }, null));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}

	/**
	 * @testID testIsSatisfied010
	 * @testDescription Asserts if isSatisfied method returns false when all
	 *                  transfer costs passed as parameter are lower than the
	 *                  thread cost value
	 * 
	 */
	public void testIsSatisfied010() {
        tbc.log("#testIsSatisfied010");
		try {
			TransferCostCondition.setTransferCost(TransferCostCondition.HIGH);

			Condition cond1 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.LOW);

			Condition cond2 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.LOW);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "Asserts if isSatisfied method returns false when all transfer costs passed as parameter are lower than the thread cost value" }),
							!cond1.isSatisfied(
									new Condition[] { cond1, cond2 }, null));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}

	/**
	 * @testID testIsSatisfied011
	 * @testDescription Asserts if isSatisfied method returns false when all
	 *                  transfer costs passed as parameter are lower than the
	 *                  thread cost value
	 * 
	 */
	public void testIsSatisfied011() {
        tbc.log("#testIsSatisfied011");
		try {
			TransferCostCondition.setTransferCost(TransferCostCondition.HIGH);

			Condition cond1 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.MEDIUM);

			Condition cond2 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.MEDIUM);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "Asserts if isSatisfied method returns false when all transfer costs passed as parameter are lower than the thread cost value" }),
							!cond1.isSatisfied(
									new Condition[] { cond1, cond2 }, null));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}
	
	/**
	 * @testID testIsSatisfied012
	 * @testDescription Asserts if isSatisfied method returns false when one of the
	 *                  transfer costs passed as parameter is lower than the
	 *                  thread cost value
	 * 
	 */
	public void testIsSatisfied012() {
        tbc.log("#testIsSatisfied012");
		try {
			TransferCostCondition.setTransferCost(TransferCostCondition.HIGH);

			Condition cond1 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.MEDIUM);

			Condition cond2 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.HIGH);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "Asserts if isSatisfied method returns false when one of the transfer costs passed as parameter is lower than the thread cost value" }),
							!cond1.isSatisfied(
									new Condition[] { cond1, cond2 }, null));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}
	
	/**
	 * @testID testIsSatisfied013
	 * @testDescription Asserts if isSatisfied method returns false when one of the
	 *                  transfer costs passed as parameter is lower than the
	 *                  thread cost value
	 * 
	 */
	public void testIsSatisfied013() {
        tbc.log("#testIsSatisfied013");
		try {
			TransferCostCondition.setTransferCost(TransferCostCondition.MEDIUM);

			Condition cond1 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.MEDIUM);

			Condition cond2 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.LOW);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "Asserts if isSatisfied method returns false when one of the transfer costs passed as parameter is lower than the thread cost value" }),
							!cond1.isSatisfied(
									new Condition[] { cond1, cond2 }, null));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}
	

	/**
	 * @testID testIsSatisfied014
	 * @testDescription Asserts if isSatisfied method returns false when one of the
	 *                  transfer costs passed as parameter is lower than the
	 *                  thread cost value
	 * 
	 */
	public void testIsSatisfied014() {
        tbc.log("#testIsSatisfied014");
		try {
			TransferCostCondition.setTransferCost(TransferCostCondition.MEDIUM);

			Condition cond1 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.HIGH);

			Condition cond2 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.LOW);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "Asserts if isSatisfied method returns false when one of the transfer costs passed as parameter is lower than the thread cost value" }),
							!cond1.isSatisfied(
									new Condition[] { cond1, cond2 }, null));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}

	/**
	 * @testID testIsSatisfied015
	 * @testDescription Asserts if isSatisfied method returns true when the
	 *                  transfer costs passed as parameter are higher than or
	 *                  equal the thread cost value
	 * 
	 */
	public void testIsSatisfied015() {
        tbc.log("#testIsSatisfied015");
		try {
			TransferCostCondition.setTransferCost(TransferCostCondition.LOW);

			Condition cond1 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.LOW);

			Condition cond2 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.HIGH);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "Asserts if isSatisfied method returns true when all transfer costs passed as parameter are higher than or equal the thread cost value" }),
							cond1.isSatisfied(new Condition[] { cond1, cond2 },
									null));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}
	
	/**
	 * @testID testIsSatisfied016
	 * @testDescription Asserts if isSatisfied method returns false when the
	 *                  transfer costs passed as parameter are lower than 
	 *                  the thread cost value
	 * 
	 */
	public void testIsSatisfied016() {
        tbc.log("#testIsSatisfied016");
		try {
			TransferCostCondition.setTransferCost(TransferCostCondition.HIGH);

			Condition cond1 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.LOW);

			Condition cond2 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.MEDIUM);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "Asserts if isSatisfied method returns false when all transfer costs passed as parameter are lower than the thread cost value" }),
							!cond1.isSatisfied(new Condition[] { cond1, cond2 },
									null));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}
	
	/**
	 * @testID testIsSatisfied017
	 * @testDescription Asserts if isSatisfied method returns false when the
	 *                  transfer costs passed as parameter are lower than 
	 *                  the thread cost value
	 * 
	 */
	public void testIsSatisfied017() {
        tbc.log("#testIsSatisfied017");
		try {
			TransferCostCondition.setTransferCost(TransferCostCondition.HIGH);

			Condition cond1 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.LOW);

			Condition cond2 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.HIGH);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "Asserts if isSatisfied method returns false when all transfer costs passed as parameter are lower than the thread cost value" }),
							!cond1.isSatisfied(new Condition[] { cond1, cond2 },
									null));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}
	
	/**
	 * @testID testIsSatisfied018
	 * @testDescription Asserts if isSatisfied method returns true when the
	 *                  transfer costs passed as parameter are higher than or
	 *                  equal the thread cost value
	 * 
	 */
	public void testIsSatisfied018() {
        tbc.log("#testIsSatisfied018");
		try {
			TransferCostCondition.setTransferCost(TransferCostCondition.LOW);

			Condition cond1 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.MEDIUM);

			Condition cond2 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.HIGH);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "Asserts if isSatisfied method returns true when all transfer costs passed as parameter are higher than or equal the thread cost value" }),
							cond1.isSatisfied(new Condition[] { cond1, cond2 },
									null));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}
	
	/**
	 * @testID testIsSatisfied019
	 * @testDescription Asserts if isSatisfied method returns true when the
	 *                  transfer costs passed as parameter is higher or equal than the
	 *                  thread cost value
	 * 
	 */
	public void testIsSatisfied019() {
        tbc.log("#testIsSatisfied019");
		try {
			TransferCostCondition.setTransferCost(TransferCostCondition.LOW);

			Condition cond1 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.MEDIUM);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "Asserts if isSatisfied method returns true when the transfer costs passed as parameter is higher or equal than the thread cost value" }),
							cond1.isSatisfied());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}

	/**
	 * @testID testIsSatisfied020
	 * @testDescription Asserts if isSatisfied method returns true when the
	 *                  transfer costs passed as parameter is higher or equal than the
	 *                  thread cost value
	 * 
	 */
	public void testIsSatisfied020() {
        tbc.log("#testIsSatisfied020");
		try {
			TransferCostCondition.setTransferCost(TransferCostCondition.LOW);

			Condition cond1 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.HIGH);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "Asserts if isSatisfied method returns true when the transfer costs passed as parameter is higher or equal than the thread cost value" }),
							cond1.isSatisfied());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}
	
	/**
	 * @testID testIsSatisfied021
	 * @testDescription Asserts if isSatisfied method returns true when the
	 *                  transfer costs passed as parameter is higher or equal than the
	 *                  thread cost value
	 * 
	 */
	public void testIsSatisfied021() {
        tbc.log("#testIsSatisfied021");
		try {
			TransferCostCondition.setTransferCost(TransferCostCondition.MEDIUM);

			Condition cond1 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.HIGH);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "Asserts if isSatisfied method returns true when the transfer costs passed as parameter is higher or equal than the thread cost value" }),
							cond1.isSatisfied());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}
	
	/**
	 * @testID testIsSatisfied022
	 * @testDescription Asserts if isSatisfied method returns false when the
	 *                  transfer costs passed as parameter is lower or equal than the
	 *                  thread cost value
	 * 
	 */
	public void testIsSatisfied022() {
        tbc.log("#testIsSatisfied022");
		try {
			TransferCostCondition.setTransferCost(TransferCostCondition.HIGH);

			Condition cond1 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.MEDIUM);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "Asserts if isSatisfied method returns false when the transfer costs passed as parameter is lower than the thread cost value" }),
							!cond1.isSatisfied());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}
	
	/**
	 * @testID testIsSatisfied023
	 * @testDescription Asserts if isSatisfied method returns false when the
	 *                  transfer costs passed as parameter is lower or equal than the
	 *                  thread cost value
	 * 
	 */
	public void testIsSatisfied023() {
        tbc.log("#testIsSatisfied023");
		try {
			TransferCostCondition.setTransferCost(TransferCostCondition.HIGH);

			Condition cond1 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
					TransferCostCondition.LOW);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "Asserts if isSatisfied method returns false when the transfer costs passed as parameter is lower than the thread cost value" }),
							!cond1.isSatisfied());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}
	
	/**
	 * @testID testIsSatisfied024
	 * @testDescription Asserts if isSatisfied method returns false when the
	 *                  transfer costs passed as parameter is lower or equal than the
	 *                  thread cost value
	 * 
	 */
	public void testIsSatisfied024() {
        tbc.log("#testIsSatisfied024");
		try {
			TransferCostCondition.setTransferCost(TransferCostCondition.MEDIUM);

			Condition cond1 = TransferCostCondition.getInstance(
					PolicyTestControl.TEST_BUNDLE, TransferCostCondition.LOW);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "Asserts if isSatisfied method returns false when the transfer costs passed as parameter is lower than the thread cost value" }),
							!cond1.isSatisfied());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		} finally {
			TransferCostCondition.resetTransferCost();
		}
	}
    
    /**
     * @testID testIsSatisfied025
     * @testDescription Asserts if isSatisfied method returns true when the
     *                  transfer costs passed as parameter is higher or equal than the
     *                  thread cost value
     * 
     */
    public void testIsSatisfied025() {
        tbc.log("#testIsSatisfied025");
        try {
            TransferCostCondition.setTransferCost(TransferCostCondition.LOW);

            Condition cond1 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
                    TransferCostCondition.LOW);

            tbc
                    .assertTrue(
                            MessagesConstants
                                    .getMessage(
                                            MessagesConstants.ASSERT_TRUE,
                                            new String[] { "Asserts if isSatisfied method returns true when the transfer costs passed as parameter is higher or equal than the thread cost value" }),
                            cond1.isSatisfied());

        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
                            .getClass().getName() }));
        } finally {
            TransferCostCondition.resetTransferCost();
        }
    }
    /**
     * @testID testIsSatisfied026
     * @testDescription Asserts if isSatisfied method returns true when the
     *                  transfer costs passed as parameter is higher or equal than the
     *                  thread cost value
     * 
     */
    public void testIsSatisfied026() {
        tbc.log("#testIsSatisfied026");
        try {
            TransferCostCondition.setTransferCost(TransferCostCondition.MEDIUM);

            Condition cond1 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
                    TransferCostCondition.MEDIUM);

            tbc
                    .assertTrue(
                            MessagesConstants
                                    .getMessage(
                                            MessagesConstants.ASSERT_TRUE,
                                            new String[] { "Asserts if isSatisfied method returns true when the transfer costs passed as parameter is higher or equal than the thread cost value" }),
                            cond1.isSatisfied());

        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
                            .getClass().getName() }));
        } finally {
            TransferCostCondition.resetTransferCost();
        }
    }
    
    /**
     * @testID testIsSatisfied027
     * @testDescription Asserts if isSatisfied method returns true when the
     *                  transfer costs passed as parameter is higher or equal than the
     *                  thread cost value
     * 
     */
    public void testIsSatisfied027() {
        tbc.log("#testIsSatisfied027");
        try {
            TransferCostCondition.setTransferCost(TransferCostCondition.HIGH);

            Condition cond1 = TransferCostCondition.getInstance(PolicyTestControl.TEST_BUNDLE,
                    TransferCostCondition.HIGH);

            tbc
                    .assertTrue(
                            MessagesConstants
                                    .getMessage(
                                            MessagesConstants.ASSERT_TRUE,
                                            new String[] { "Asserts if isSatisfied method returns true when the transfer costs passed as parameter is higher or equal than the thread cost value" }),
                            cond1.isSatisfied());

        } catch (Exception e) {
            tbc.fail(MessagesConstants.getMessage(
                    MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
                            .getClass().getName() }));
        } finally {
            TransferCostCondition.resetTransferCost();
        }
    }
}

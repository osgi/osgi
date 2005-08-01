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
 * <code>getCondition<code> method, according to MEG
 * reference documentation.
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
	}

    /**
     * This test asserts if a condition is sucessfully created when "LOW"
     * is passed as costLimit parameter
     *
     * @spec TransferCostCondition.getCondition(Bundle,ConditionInfo)
     */

	public void testGetCondition001() {
		tbc.log("#testGetCondition001");
		try {
			Condition cond = TransferCostCondition.getCondition(
					PolicyTestControl.TEST_BUNDLE, new ConditionInfo("",
							new String[] { TransferCostCondition.LOW }));

			tbc.assertNotNull(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_NOT_NULL,
					new String[] { "created condition" }), cond);

			tbc
					.assertTrue(
							"The returned condition is an instance of TransferCostCondition",
							(cond instanceof TransferCostCondition));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		}
	}


    /**
     * This test asserts if a condition is sucessfully created when
     * "MEDIUM" is passed as costLimit parameter
     *
     * @spec TransferCostCondition.getCondition(Bundle,ConditionInfo)
     */

	public void testGetCondition002() {
		tbc.log("#testGetCondition002");
		try {
			Condition cond = TransferCostCondition.getCondition(
					PolicyTestControl.TEST_BUNDLE, new ConditionInfo("",
							new String[] { TransferCostCondition.MEDIUM }));

			tbc.assertNotNull(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_NOT_NULL,
					new String[] { "created condition" }), cond);

			tbc
					.assertTrue(
							"The returned condition is an instance of TransferCostCondition",
							(cond instanceof TransferCostCondition));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		}
	}


    /**
     * This test asserts if a condition is sucessfully created when
     * "HIGH" is passed as costLimit parameter
     *
     * @spec TransferCostCondition.getCondition(Bundle,ConditionInfo)
     */

	public void testGetCondition003() {
		tbc.log("#testGetCondition003");
		try {
			Condition cond = TransferCostCondition.getCondition(
					PolicyTestControl.TEST_BUNDLE, new ConditionInfo("",
							new String[] { TransferCostCondition.HIGH }));

			tbc.assertNotNull(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_NOT_NULL,
					new String[] { "created condition" }), cond);

			tbc
					.assertTrue(
							"The returned condition is an instance of TransferCostCondition",
							(cond instanceof TransferCostCondition));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		}
	}


    /**
     * This test asserts if an IllegalArgumentException is thrown if the
     * costLimit parameter is not from the possible values
     *
     * @spec TransferCostCondition.getCondition(Bundle,ConditionInfo)
     */

	public void testGetCondition004() {
		tbc.log("#testGetCondition004");
		try {
			Condition cond = TransferCostCondition
					.getCondition(
							PolicyTestControl.TEST_BUNDLE,
							new ConditionInfo(
									"",
									new String[] { PolicyTestControl.INVALID_COST_LIMIT }));

			tbc.failException("#", IllegalArgumentException.class);
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
     * This test asserts if a NullPointerException is thrown if one of
     * the parameters is null
     *
     * @spec TransferCostCondition.getCondition(Bundle,ConditionInfo)
     */

	public void testGetCondition005() {
		tbc.log("#testGetCondition005");
		try {
			Condition cond = TransferCostCondition
					.getCondition(
							null,
							new ConditionInfo(
									"",
									new String[] { PolicyTestControl.INVALID_COST_LIMIT }));

			tbc.failException("#", NullPointerException.class);
		} catch (NullPointerException e) {
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
     * This test asserts if a NullPointerException is thrown if one of
     * the parameters is null
     *
     * @spec TransferCostCondition.getCondition(Bundle,ConditionInfo)
     */

	public void testGetCondition006() {
		tbc.log("#testGetCondition006");
		try {
			Condition cond = TransferCostCondition.getCondition(
					PolicyTestControl.TEST_BUNDLE, null);

			tbc.failException("#", NullPointerException.class);
		} catch (NullPointerException e) {
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

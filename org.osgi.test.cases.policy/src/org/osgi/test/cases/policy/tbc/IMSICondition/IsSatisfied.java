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
 * 10/05/2005   Eduardo Oliveira
 * 33           Implement MEG TCK
 * ===========  ==============================================================
 */
package org.osgi.test.cases.policy.tbc.IMSICondition;

import org.osgi.service.condpermadmin.Condition;
import org.osgi.test.cases.policy.tbc.PolicyTestControl;
import org.osgi.test.cases.policy.tbc.util.MessagesConstants;
import org.osgi.util.gsm.IMSICondition;

/**
 * @methodUnderTest org.osgi.util.gsm.IMSICondition#isSatisfied
 * @generalDescription This class tests isSatisfied method according with MEG
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
	}


	/**
	 * @testID testIsSatisfied001
	 * @testDescription Asserts IsSatisfied method returns false when a
	 *                  condition is created with a IMSI that is different from
	 *                  phone's IMSI
	 */
	public void testIsSatisfied001() {
        tbc.log("#testIsSatisfied001");
		try {
			Condition cond = IMSICondition.getInstance(
					PolicyTestControl.TEST_BUNDLE,
					PolicyTestControl.IMSI_VALID_CODE);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "IsSatisfied method returns false when a condition is created with a IMSI that is different from phone's IMSI" }),
							!cond.isSatisfied());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		}
	}

	/**
	 * @testID testIsSatisfied002
	 * @testDescription Asserts IsSatisfied method calls NullPointerException if
	 * 					condition is null
	 * 
	 */
	public void testIsSatisfied002() {
        tbc.log("#testIsSatisfied002");
		try {
			Condition cond1 = IMSICondition.getInstance(
					PolicyTestControl.TEST_BUNDLE,
					PolicyTestControl.IMSI_VALID_CODE);			
		
			cond1.isSatisfied(null,null);
			tbc.failException("#",NullPointerException.class);
		} catch (NullPointerException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] { e
							.getClass().getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		}
	}
	
	/**
	 * @testID testIsSatisfied003
	 * @testDescription Asserts IsSatisfied method returns true when conditions
	 *                  with same IMSI code are matched
	 */
	public void testIsSatisfied003() {
        tbc.log("#testIsSatisfied003");
		try {
			Condition cond1 = IMSICondition.getInstance(
					PolicyTestControl.TEST_BUNDLE,
					PolicyTestControl.IMSI_PHONE_CODE);

			Condition cond2 = IMSICondition.getInstance(
					PolicyTestControl.TEST_BUNDLE,
					PolicyTestControl.IMSI_PHONE_CODE);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "IsSatisfied method returns true when a condition is created with a IMSI that is equals to phone's IMSI and all condition match the device" }),
							cond2.isSatisfied(new Condition[] { cond1 }, null));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		}
	}

	/**
	 * @testID testIsSatisfied004
	 * @testDescription Asserts IsSatisfied method returns false when conditions
	 *                  with different IMSI code are matched
	 * 
	 */
	public void testIsSatisfied004() {
        tbc.log("#testIsSatisfied004");
		try {
			Condition cond1 = IMSICondition.getInstance(
					PolicyTestControl.TEST_BUNDLE,
					PolicyTestControl.IMSI_VALID_CODE);

			Condition cond2 = IMSICondition.getInstance(
					PolicyTestControl.TEST_BUNDLE,
					PolicyTestControl.IMSI_PHONE_CODE);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "IsSatisfied method returns false when conditions with different IMSI code are not matched" }),
							!cond2.isSatisfied(new Condition[] { cond1 }, null));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		}
	}


	
	/**
	 * @testID testIsSatisfied005
	 * @testDescription Asserts IsSatisfied method returns true when a condition
	 *                  is created with a IMSI that is equals to phone's IMSI
	 */
	public void testIsSatisfied005() {
        tbc.log("#testIsSatisfied005");
		try {
			Condition cond = IMSICondition.getInstance(
					PolicyTestControl.TEST_BUNDLE,
					PolicyTestControl.IMSI_PHONE_CODE);

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "IsSatisfied method returns true when a condition is created with a IMSI equals to phone's IMSI" }),
							cond.isSatisfied());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		}
	}
	
	/**
	 * @testID testIsSatisfied006
	 * @testDescription Asserts IsSatisfied method returns false when more than one condition
	 *                  is passed as parameter
	 */
	public void testIsSatisfied006() {
        tbc.log("#testIsSatisfied006");
		try {
			Condition cond1 = IMSICondition.getInstance(
					PolicyTestControl.TEST_BUNDLE,
					PolicyTestControl.IMSI_PHONE_CODE);

			Condition cond2 = IMSICondition.getInstance(
					PolicyTestControl.TEST_BUNDLE,
					PolicyTestControl.IMSI_PHONE_CODE);
			
			Condition cond3 = IMSICondition.getInstance(
					PolicyTestControl.TEST_BUNDLE,
					PolicyTestControl.IMSI_CHAR_CODE);
			
			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "IsSatisfied method returns false when more than one condition is passed as parameter" }),
							cond2.isSatisfied(new Condition[] { cond1,cond3 }, null));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] { e
							.getClass().getName() }));
		}
	}
	

}

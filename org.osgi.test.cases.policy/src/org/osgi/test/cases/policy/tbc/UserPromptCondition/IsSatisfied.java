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
 * 20/10/2005   Alexandre Alves
 * 33           Implement MEG TCK
 * ===========  ==============================================================
 */
package org.osgi.test.cases.policy.tbc.UserPromptCondition;

import java.util.Hashtable;

import org.osgi.service.condpermadmin.Condition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.test.cases.policy.tbc.PolicyConstants;
import org.osgi.test.cases.policy.tbc.PolicyTestControl;
import org.osgi.test.cases.policy.tbc.util.MessagesConstants;
import org.osgi.util.mobile.UserPromptCondition;

/**
 * @author Alexandre Alves
 * 
 * This Test Class Validates the implementation of
 * <code>isSatisfied</code> method, according to MEG reference
 * documentation.
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
    }
    
    /**
     * This method asserts that if the user rejects the question,
     * false is returned by isSatisfied using LEVEL_BLANKET.
     * 
     * @spec UserPromptCondition.isSatisfied()
     */
    private void testIsSatisfied001() {
        tbc.log("#testIsSatisfied001");
        try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.CATALOG_NAME, "1- You must REJECT this question to make this test pass with success. " +
									"[PS: If this is the second time that you receive this question there is an error in the implementation because the same question must remember your previous answer so, you have to do the opposite that it says, you have to ACCEPT this question.]" })
							);
			tbc.log("#If the user does not reject this question, this test will fail.");
			tbc.assertTrue("Asserting that false is returned by the selection of the user.", !condition.isSatisfied());
			
			condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.CATALOG_NAME, "1- You must REJECT this question to make this test pass with success. " +
									"[PS: If this is the second time that you receive this question there is an error in the implementation because the same question must remember your previous answer so, you have to do the opposite that it says, you have to ACCEPT this question.]" })
							);
			tbc.log("#This is the second time of this question and the system must remember your previous answer.");
			tbc.assertTrue("Asserting if the system remember your previous answer.", !condition.isSatisfied());			
        } catch (Exception e) {
            tbc.fail("Unexpected exception was thrown + : " 
                    + e.getClass().getName());
        }
    }
    
    /**
     * This method asserts that if the user accepts the question,
     * true is returned by isSatisfied using LEVEL_BLANKET.
     * 
     * @spec UserPromptCondition.isSatisfied()
     */
    private void testIsSatisfied002() {
        tbc.log("#testIsSatisfied002");
        try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.CATALOG_NAME, "2- You must ACCEPT this question to make this test pass with success." +
							"[PS: If this is the second time that you receive this question there is an error in the implementation because the same question must remember your previous answer so, you have to do the opposite that it says, you have to REJECT this question.]" })
							);
			tbc.log("#If the user does not accept this question, this test will fail.");
			tbc.assertTrue("Asserting that true is returned by the selection of the user.", condition.isSatisfied());
			
			condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.CATALOG_NAME, "2- You must ACCEPT this question to make this test pass with success." +
							"[PS: If this is the second time that you receive this question there is an error in the implementation because the same question must remember your previous answer so, you have to do the opposite that it says, you have to REJECT this question.]" })
							);
			tbc.log("#This is the second time of this question and the system must remember your previous answer.");
			tbc.assertTrue("Asserting if the system remember your previous answer.", condition.isSatisfied());			
        } catch (Exception e) {
            tbc.fail("Unexpected exception was thrown + : " 
                    + e.getClass().getName());
        }
    }    
    
    /**
     * This method asserts that if the user accepts the question,
     * true is returned by isSatisfied using LEVEL_ONESHOT.
     * 
     * @spec UserPromptCondition.isSatisfied()
     */
    private void testIsSatisfied003() {    	
    	tbc.log("#testIsSatisfied003");
        try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.CATALOG_NAME, "3- You must ACCEPT this question to make this test pass with success." +
					"[PS: If this is the second time that you receive this question the implementation is ok in this case (ONESHOT) you have to REJECT this question to validate the second question.]" })
							);
				tbc.log("#If the user does not accept this question, this test will fail.");
				tbc.assertTrue("Asserting that true is returned by the selection of the user.", condition.isSatisfied());

				condition = UserPromptCondition.getCondition(tbc.getBundle(), 
						new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
								PolicyConstants.LEVEL_ONESHOT,
								PolicyConstants.LEVEL_ONESHOT,
								PolicyConstants.CATALOG_NAME, "3- You must ACCEPT this question to make this test pass with success." +
						"[PS: If this is the second time that you receive this question the implementation is ok in this case (ONESHOT) you have to REJECT this question to validate the second question.]" })
								);
								
				tbc.log("#This is the second time that you receive this question, you have to reject this question.");
				tbc.assertTrue("Asserting that false is returned by the selection of the user.", !condition.isSatisfied());			
        } catch (Exception e) {
            tbc.fail("Unexpected exception was thrown + : " 
                    + e.getClass().getName());
        }
    } 
       
    /**
     * This method asserts that if the user rejects the question,
     * false is returned by isSatisfied using LEVEL_SESSION.
     * 
     * @spec UserPromptCondition.isSatisfied()
     */
    private void testIsSatisfied004() {
         tbc.log("#testIsSatisfied004");
        try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.CATALOG_NAME, "4- You must REJECT this question to make this test pass with success." +
					"[PS: If this is the second time that you receive this question there is an error in the implementation because the same question must remember your previous answer so, you have to do the opposite that it says, you have to ACCEPT this question.]" })
							);
			tbc.log("#If the user does not reject this question, this test will fail.");
			tbc.assertTrue("Asserting that false is returned by the selection of the user.", !condition.isSatisfied());
			
			condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.CATALOG_NAME, "4- You must REJECT this question to make this test pass with success." +
					"[PS: If this is the second time that you receive this question there is an error in the implementation because the same question must remember your previous answer so, you have to do the opposite that it says, you have to ACCEPT this question.]" })
							);	
			tbc.log("#This is the second time of this question and the system must remember your previous answer.");
			tbc.assertTrue("Asserting if the system remember your previous answer.", !condition.isSatisfied());				
        } catch (Exception e) {
            tbc.fail("Unexpected exception was thrown + : " 
                    + e.getClass().getName());
        }
    }    
    
    /**
     * This method asserts that if the user accepts the question,
     * true is returned by isSatisfied using LEVEL_SESSION.
     * 
     * @spec UserPromptCondition.isSatisfied()
     */
    private void testIsSatisfied005() {
         tbc.log("#testIsSatisfied005");
        try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.CATALOG_NAME, "5- You must ACCEPT this question to make this test pass with success." +
					"[PS: If this is the second time that you receive this question there is an error in the implementation because the same question must remember your previous answer so, you have to do the opposite that it says, you have to REJECT this question.]" })
							);
			tbc.log("#If the user does not accept this question, this test will fail.");
			tbc.assertTrue("Asserting that true is returned by the selection of the user.", condition.isSatisfied());
			
			condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.CATALOG_NAME, "5- You must ACCEPT this question to make this test pass with success." +
					"[PS: If this is the second time that you receive this question there is an error in the implementation because the same question must remember your previous answer so, you have to do the opposite that it says, you have to REJECT this question.]" })
							);
			tbc.log("#This is the second time of this question and the system must remember your previous answer.");
			tbc.assertTrue("Asserting if the system remember your previous answer.", condition.isSatisfied());		
        } catch (Exception e) {
            tbc.fail("Unexpected exception was thrown + : " 
                    + e.getClass().getName());
        }
    }
    
    /**
     * This method asserts that if the user choose never as answer
     * to the question, false is returned by isSatisfied using LEVEL_ONESHOT.
     * 
     * @spec UserPromptCondition.isSatisfied()
     */
    private void testIsSatisfied006() {
        tbc.log("#testIsSatisfied006");
        try {
 			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
 					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
 							PolicyConstants.LEVEL_ONESHOT,
 							PolicyConstants.LEVEL_ONESHOT,
 							PolicyConstants.CATALOG_NAME, "6- You must choose NEVER to this question to make this test pass with success. " +
 									"[PS: If this is the second time that you receive this question there is an error in the implementation and you have to choose yes as answer because when the user choose NEVER as answer, the RI never ask the user again.]" })
 							);
 			tbc.log("#If the user does choose NEVER to this question, this test will fail.");
 			tbc.assertTrue("Asserting that false is returned by the selection of the user.", !condition.isSatisfied());
 			
 			condition = UserPromptCondition.getCondition(tbc.getBundle(), 
 					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
 							PolicyConstants.LEVEL_ONESHOT,
 							PolicyConstants.LEVEL_ONESHOT,
 							PolicyConstants.CATALOG_NAME, "6- You must choose NEVER to this question to make this test pass with success. " +
 									"[PS: If this is the second time that you receive this question there is an error in the implementation and you have to choose yes as answer because when the user choose NEVER as answer, the RI never ask the user again.]" })
 							);
 			tbc.log("#This is the second time of this question and the system must remember your previous answer.");
 			tbc.assertTrue("Asserting if the system remember your previous answer.", !condition.isSatisfied());			
        } catch (Exception e) {
            tbc.fail("Unexpected exception was thrown + : " 
                    + e.getClass().getName());
        }
    }   
    
    /**
     * This method asserts that if the user choose never as answer
     * to the question, false is returned by isSatisfied using LEVEL_SESSION.
     * 
     * @spec UserPromptCondition.isSatisfied()
     */
    private void testIsSatisfied007() {
        tbc.log("#testIsSatisfied007");
        try {
 			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
 					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
 							PolicyConstants.LEVEL_SESSION,
 							PolicyConstants.LEVEL_SESSION,
 							PolicyConstants.CATALOG_NAME, "7- You must choose NEVER to this question to make this test pass with success. " +
 									"[PS: If this is the second time that you receive this question there is an error in the implementation and you have to choose yes as answer because when the user choose NEVER as answer, the RI never ask the user again until the system is restarted.]" })
 							);
 			tbc.log("#If the user does choose NEVER to this question, this test will fail.");
 			tbc.assertTrue("Asserting that false is returned by the selection of the user.", !condition.isSatisfied());
 			
 			condition = UserPromptCondition.getCondition(tbc.getBundle(), 
 					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
 							PolicyConstants.LEVEL_SESSION,
 							PolicyConstants.LEVEL_SESSION,
 							PolicyConstants.CATALOG_NAME, "7- You must choose NEVER to this question to make this test pass with success. " +
 									"[PS: If this is the second time that you receive this question there is an error in the implementation and you have to choose yes as answer because when the user choose NEVER as answer, the RI never ask the user again until the system is restarted.]" })
 							);
 			tbc.log("#This is the second time of this question and the system must remember your previous answer.");
 			tbc.assertTrue("Asserting if the system remember your previous answer.", !condition.isSatisfied());			
        } catch (Exception e) {
            tbc.fail("Unexpected exception was thrown + : " 
                    + e.getClass().getName());
        }
    }       
      
   /** 
    * This method asserts if the user receive the message
    * "8-Did you see this question?" and the user
    * must answer yes.
    * 
    * @spec UserPromptCondition.isSatisfied()
    */
   private void testIsSatisfied008() {
        tbc.log("#testIsSatisfied008");
       try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.ALL_LEVELS,
							"",
							PolicyConstants.CATALOG_NAME, "%test" })
							);
			tbc.log("#If the user see the the message \"8-Did you see this question\" he must accept this question. Otherwise, he must reject.");
			tbc.assertTrue("Asserting if the user see the the message \"8-Did you see this question\".", condition.isSatisfied());
       } catch (Exception e) {
           tbc.fail("Unexpected exception was thrown + : " 
                   + e.getClass().getName());
       }
   }
   
   /**
    * This method asserts that if the user receive three questions
    * in the same time and isSatisfied can be called two times and
    * it remember the previous answer. And if the user reject all the
    * three answers, false is returned by isSatisfied.
    * 
    * @spec UserPromptCondition.isSatisfied(Condition[],Dictionary)
    */
   private void testIsSatisfied009() {
        tbc.log("#testIsSatisfied009");
       try {
			Condition condition1 = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.CATALOG_NAME, "9- You must REJECT this question to make this test pass with success. " +
									"[PS: If this is the second time that you receive this question there is an error in the implementation because the same question must remember your previous answer so, you have to do the opposite that it says, you have to ACCEPT this question.]" })
							);
			
			Condition condition2 = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.CATALOG_NAME, "10- You must REJECT this question to make this test pass with success." +
					"[PS: If this is the second time that you receive this question there is an error in the implementation because the same question must remember your previous answer so, you have to do the opposite that it says, you have to ACCEPT this question.]" })
							);
			
			Condition condition3 = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.CATALOG_NAME, "11- You must REJECT this question to make this test pass with success." +
					"[PS: If this is the second time that you receive this question there is an error in the implementation because the same question must remember your previous answer even if the level is ONESHOT so, you have to do the opposite that it says, you have to ACCEPT this question.]" }));
						
			Condition[] conditions = { condition1, condition2, condition3 };					
					
			Hashtable hash = new Hashtable();
				
			tbc.log("#If the user does not reject all the answers, this test will fail.");
			
			tbc.assertTrue("Asserting that isSatisfied returns false.", !condition3.isSatisfied(conditions, hash));	
			tbc.log("#This is the second time for this question, if it appears you must accept to make this test fail.");
			tbc.assertTrue("Asserting that isSatisfied returns false again.", !condition3.isSatisfied(conditions, hash));
       } catch (Exception e) {
           tbc.fail("Unexpected exception was thrown + : " 
                   + e.getClass().getName());
       }
   }   
   
   /**
    * This method asserts that if the user receive three questions
    * in the same time and isSatisfied can be called two times and
    * it does not remember the previous answer because null was
    * passed as context. And when the user accepts the three questions,
    * true must be returned.

    * 
    * @spec UserPromptCondition.isSatisfied(Condition[],Dictionary)
    */
   private void testIsSatisfied010() {
        tbc.log("#testIsSatisfied010");
       try {
			Condition condition1 = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.CATALOG_NAME, "12- You must ACCEPT this question to make this test pass with success." +
					"[PS: If this is the second time that you receive this question the implementation is ok and you have to REJECT this question.]" }));
    	   
    	   
			Condition condition2 = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.CATALOG_NAME, "13- You must ACCEPT this question to make this test pass with success. " +
					"[PS: If this is the second time that you receive this question the implementation is ok and you have to REJECT this question.]" })
							);
			
			Condition condition3 = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.CATALOG_NAME, "14- You must ACCEPT this question to make this test pass with success." +
					"[PS: If this is the second time that you receive this question the implementation is ok and you have to REJECT this question.]" })
							);
			
						
			Condition[] conditions = { condition1, condition2, condition3 };				
								
			tbc.log("#If the user does not accept all the questions, this test will fail.");
			
			tbc.assertTrue("Asserting that isSatisfied returns true.", condition3.isSatisfied(conditions, null));
			tbc.log("#The second question must appear because null was passed as context. Otherwise the test will fail.");
			tbc.assertTrue("Asserting that isSatisfied returns false.", !condition3.isSatisfied(conditions, null));
       } catch (Exception e) {
           tbc.fail("Unexpected exception was thrown + : " 
               + e.getClass().getName());
       }
   }    
   
   /**
    * This method asserts that NullPointerException is thrown
    * when we pass null for the first parameter.
    * 
    * @spec UserPromptCondition.isSatisfied(Condition[],Dictionary)
    */
   private void testIsSatisfied011() {
        tbc.log("#testIsSatisfied011");
       try {
			Condition condition1 = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.CATALOG_NAME, "15- You must ACCEPT this question to make this test pass with success." }));
		
			Hashtable hash = new Hashtable();
			
			condition1.isSatisfied(null, hash);
			tbc.failException("#",NullPointerException.class);
		} catch (NullPointerException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] {
							e.getClass().getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							NullPointerException.class.getName(), e.getClass().getName() }));
		}
		
	}
   
   /**
    * This method asserts that NullPointerException is thrown
    * when null is passed as element inside the Condition array.
    * 
    * @spec UserPromptCondition.isSatisfied(Condition[],Dictionary)
    */
   private void testIsSatisfied012() {
        tbc.log("#testIsSatisfied012");
       try {
			Condition condition1 = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.CATALOG_NAME, "16- You must ACCEPT this question to make this test pass with success." +
					"[PS: If this is the second time that you receive this question there is an error in the implementation because the same question must remember your previous answer even if the level is ONESHOT so, you have to do the opposite that it says, you have to REJECT this question.]" }));
    	   
    	   
			Condition condition2 = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.CATALOG_NAME, "17- You must ACCEPT this question to make this test pass with success. " +
									"[PS: If this is the second time that you receive this question there is an error in the implementation because the same question must remember your previous answer so, you have to do the opposite that it says, you have to REJECT this question.]" }));
						
			Condition[] conditions = { condition1, condition2, null };	
			condition1.isSatisfied(conditions, null);
			tbc.failException("#",NullPointerException.class);
		} catch (NullPointerException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] {
							e.getClass().getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							NullPointerException.class.getName(), e.getClass().getName() }));
		}
		
	}
   
   /**
    * This method asserts that if the user choose never as answer
    * to the question, false is returned by isSatisfied using LEVEL_BLANKET.
    * 
    * @spec UserPromptCondition.isSatisfied()
    */
   private void testIsSatisfied013() {
       tbc.log("#testIsSatisfied013");
       try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.CATALOG_NAME, "18- You must choose NEVER to this question to make this test pass with success. " +
									"[PS: If this is the second time that you receive this question there is an error in the implementation and you have to choose yes as answer because when the user choose NEVER as answer, the RI never ask the user again even if the system is restarted.]" })
							);
			tbc.log("#If the user does choose NEVER to this question, this test will fail.");
			tbc.assertTrue("Asserting that false is returned by the selection of the user.", !condition.isSatisfied());
			
			condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.CATALOG_NAME, "18- You must choose NEVER to this question to make this test pass with success. " +
									"[PS: If this is the second time that you receive this question there is an error in the implementation and you have to choose yes as answer because when the user choose NEVER as answer, the RI never ask the user again even if the system is restarted.]" })
							);
			tbc.log("#This is the second time of this question and the system must remember your previous answer.");
			tbc.assertTrue("Asserting if the system remember your previous answer.", !condition.isSatisfied());			
       } catch (Exception e) {
           tbc.fail("Unexpected exception was thrown + : " 
                   + e.getClass().getName());
       }
   }    
   
   /**
    * This method asserts that if the user choose always as answer
    * to the question, true is returned by isSatisfied using LEVEL_BLANKET.
    * 
    * @spec UserPromptCondition.isSatisfied()
    */
   private void testIsSatisfied014() {
       tbc.log("#testIsSatisfied014");
       try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.CATALOG_NAME, "19- You must choose ALWAYS to this question to make this test pass with success. " +
									"[PS: If this is the second time that you receive this question there is an error in the implementation and you have to choose never as answer because when the user choose ALWAYS as answer, the RI never ask the user again even if the system is restarted.]" })
							);
			tbc.log("#If the user does choose ALWAYS to this question, this test will fail.");
			tbc.assertTrue("Asserting that true is returned by the selection of the user.", condition.isSatisfied());
			
			condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.CATALOG_NAME, "19- You must choose ALWAYS to this question to make this test pass with success. " +
									"[PS: If this is the second time that you receive this question there is an error in the implementation and you have to choose never as answer because when the user choose ALWAYS as answer, the RI never ask the user again even if the system is restarted.]" })
							);
			tbc.log("#This is the second time of this question and the system must remember your previous answer.");
			tbc.assertTrue("Asserting if the system remember your previous answer.", condition.isSatisfied());			
       } catch (Exception e) {
           tbc.fail("Unexpected exception was thrown + : " 
                   + e.getClass().getName());
       }
   }   
   
   /**
    * This method asserts that if the user choose never
    * as answer, the question will not be prompted anymore.
    * 
    * @spec UserPromptCondition.isSatisfied()
    */
   private void testIsSatisfied015() {    	
   	tbc.log("#testIsSatisfied015");
       try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.CATALOG_NAME, "20- You must choose NEVER to this question to make this test pass with success." +
					"[PS: If this is the second time that you receive this question there is an error in implementation and you have to ACCEPT this question to make the test fail because when you choose NEVER as answer, the RI must never ask the user again and always return false.]" })
							);
				tbc.log("#If the user does not reject this question, this test will fail.");
				tbc.assertTrue("Asserting that false is returned by the selection of the user.", !condition.isSatisfied());

				condition = UserPromptCondition.getCondition(tbc.getBundle(), 
						new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
								PolicyConstants.LEVEL_ONESHOT,
								PolicyConstants.LEVEL_ONESHOT,
								PolicyConstants.CATALOG_NAME, "20- You must choose NEVER to this question to make this test pass with success." +
						"[PS: If this is the second time that you receive this question there is an error in implementation and you have to ACCEPT this question to make the test fail because when you choose NEVER as answer, the RI must never ask the user again and always return false.]" })
								);
								
				tbc.log("#This is the second time that you receive this question, you have to accept this question.");
				tbc.assertTrue("Asserting that false is returned by the selection of the user.", !condition.isSatisfied());			
       } catch (Exception e) {
           tbc.fail("Unexpected exception was thrown + : " 
                   + e.getClass().getName());
       }
   }
   
   /**
    * This method asserts that if the user rejects the question,
    * false is returned by isSatisfied using LEVEL_SESSION.
    * 
    * @spec UserPromptCondition.isSatisfied()
    */
   private void testIsSatisfied016() {
        tbc.log("#testIsSatisfied016");
       try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.CATALOG_NAME, "21- You must choose NEVER to this question to make this test pass with success." +
					"[PS: If this is the second time that you receive this question there is an error in implementation and you have to ACCEPT this question to make the test fail because when you choose NEVER as answer, the RI must never ask the user again and always return false until the system is restarted.]" })
							);
			tbc.log("#If the user does not reject this question, this test will fail.");
			tbc.assertTrue("Asserting that false is returned by the selection of the user.", !condition.isSatisfied());
			
			condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.CATALOG_NAME, "21- You must choose NEVER to this question to make this test pass with success." +
					"[PS: If this is the second time that you receive this question there is an error in implementation and you have to ACCEPT this question to make the test fail because when you choose NEVER as answer, the RI must never ask the user again and always return false until the system is restarted.]" })
							);
			tbc.log("#This is the second time that you receive this question, you have to accept this question.");
			tbc.assertTrue("Asserting if the system remember your previous answer.", !condition.isSatisfied());				
       } catch (Exception e) {
           tbc.fail("Unexpected exception was thrown + : " 
                   + e.getClass().getName());
       }
   }
   
   /**
    * This method asserts that if isSatisfied is called passing a dictionary,
    * the oneshot questions will be prompted only one time.

    * 
    * @spec UserPromptCondition.isSatisfied(Condition[],Dictionary)
    */
   private void testIsSatisfied017() {
        tbc.log("#testIsSatisfied017");
       try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.CATALOG_NAME, "22- Choose yes as answer. PS: If this is the second time that you receive this question, there is an error in the implementation and you must reject this test choosing no as answer." }));
								
			Hashtable hash = new Hashtable();
			
			Condition[] conditions = { condition };
			
			tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied(conditions, hash));
			
			tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied(conditions, hash));
			
       } catch (Exception e) {
           tbc.fail("Unexpected exception was thrown + : " 
                   + e.getClass().getName());
       }
   }      
 
}

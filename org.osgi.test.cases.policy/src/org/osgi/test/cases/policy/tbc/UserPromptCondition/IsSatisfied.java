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
  private int propertiesIndex;
  private int answerIndex;

  public IsSatisfied(PolicyTestControl tbc) {
    this.tbc = tbc;
    ConditionConstants.CONDITIONS_LEVEL  = ConditionConstants.IS_CONDITIONS_LEVELS;
    ConditionConstants.CONDITIONS_DEFAULT = ConditionConstants.IS_CONDITIONS_DEFAULT;
    ConditionConstants.CONDITIONS_CATALOG = ConditionConstants.IS_CONDITIONS_CATALOG;
    ConditionConstants.CONDITIONS_MESSAGE = ConditionConstants.IS_CONDITIONS_MESSAGE;
    ConditionConstants.CONDITIONS_ANSWERS = ConditionConstants.IS_ANSWERS;
    propertiesIndex = 0;
    answerIndex = 0;
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
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.log("#If the user does not reject this question, this test will fail.");
      tbc.assertTrue("Asserting that false is returned by the selection of the user.", !condition.isSatisfied());
      propertiesIndex++;
      answerIndex++;
      condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
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
      propertiesIndex++;
      answerIndex++;          
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.log("#If the user does not accept this question, this test will fail.");
      tbc.assertTrue("Asserting that true is returned by the selection of the user.", condition.isSatisfied());
      propertiesIndex++;
      answerIndex++;			
      condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
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
      propertiesIndex++;
      answerIndex++;          
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.log("#If the user does not accept this question, this test will fail.");
      tbc.assertTrue("Asserting that true is returned by the selection of the user.", condition.isSatisfied());

      propertiesIndex++;
      answerIndex++;          
      condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;  								
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
      propertiesIndex++;
      answerIndex++;    
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.log("#If the user does not reject this question, this test will fail.");
      tbc.assertTrue("Asserting that false is returned by the selection of the user.", !condition.isSatisfied());

      propertiesIndex++;
      answerIndex++;    
      condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
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
      propertiesIndex++;
      answerIndex++;    
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.log("#If the user does not accept this question, this test will fail.");
      tbc.assertTrue("Asserting that true is returned by the selection of the user.", condition.isSatisfied());

      propertiesIndex++;
      answerIndex++;    
      condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
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
      propertiesIndex++;
      answerIndex++;
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.log("#If the user does choose NEVER to this question, this test will fail.");
      tbc.assertTrue("Asserting that false is returned by the selection of the user.", !condition.isSatisfied());

      propertiesIndex++;
      answerIndex++;
      condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
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
      propertiesIndex++;
      answerIndex++;          
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.log("#If the user does choose NEVER to this question, this test will fail.");
      tbc.assertTrue("Asserting that false is returned by the selection of the user.", !condition.isSatisfied());

      propertiesIndex++;
      answerIndex++;
      condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
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
      propertiesIndex++;
      answerIndex++;
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
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
      propertiesIndex++;
      answerIndex++;
      ConditionConstants.CURRENT_ANSWER_INDEX = -1;
      ConditionConstants.CURRENT_ANSWERS_INDICES = new int[3];
      Condition condition1 = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      propertiesIndex++;
      ConditionConstants.CURRENT_ANSWERS_INDICES[0] = answerIndex++;
      Condition condition2 = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      propertiesIndex++;
      ConditionConstants.CURRENT_ANSWERS_INDICES[1] = answerIndex++;
      Condition condition3 = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      //increase answers the next usage is in this method
      ConditionConstants.CURRENT_ANSWERS_INDICES[2] = answerIndex++;
      Condition[] conditions = { condition1, condition2, condition3 };					

      Hashtable hash = new Hashtable();

      tbc.log("#If the user does not reject all the answers, this test will fail.");
      tbc.assertTrue("Asserting that isSatisfied returns false.", !condition3.isSatisfied(conditions, hash));	
      tbc.log("#This is the second time for this question, if it appears you must accept to make this test fail.");
      
      ConditionConstants.CURRENT_ANSWERS_INDICES = new int[3];//use new array so to notify whoever use the constants for new answers
      ConditionConstants.CURRENT_ANSWERS_INDICES[0] = answerIndex++;
      ConditionConstants.CURRENT_ANSWERS_INDICES[1] = answerIndex++;
      ConditionConstants.CURRENT_ANSWERS_INDICES[2] = answerIndex;
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
      propertiesIndex++;
      answerIndex++;
      ConditionConstants.CURRENT_ANSWER_INDEX = -1;
      ConditionConstants.CURRENT_ANSWERS_INDICES = new int[2];            
      ConditionConstants.CURRENT_ANSWERS_INDICES[0] = answerIndex++;
      Condition condition2 = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      propertiesIndex++;
      ConditionConstants.CURRENT_ANSWERS_INDICES[1] = answerIndex;
      Condition condition3 = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      Condition[] conditions = { condition2, condition3 };				

      tbc.log("#If the user does not accept all the questions, this test will fail.");

      tbc.assertTrue("Asserting that isSatisfied returns true.", condition3.isSatisfied(conditions, null));
      tbc.log("#The second question must not appear because SESSION and BLANKET levels were used. Otherwise the test will fail.");
      
      ConditionConstants.CURRENT_ANSWERS_INDICES = null;//there should be only one answer in next evaluation
      ConditionConstants.CURRENT_ANSWER_INDEX = ++answerIndex;
      tbc.assertTrue("Asserting that isSatisfied returns true.", condition3.isSatisfied(conditions, null));
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
      propertiesIndex++;
      answerIndex++;
      Condition condition1 = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));

      Hashtable hash = new Hashtable();
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
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
      propertiesIndex++;
      answerIndex++;
      ConditionConstants.CURRENT_ANSWER_INDEX = -1;
      ConditionConstants.CURRENT_ANSWERS_INDICES = new int[2];            
      Condition condition1 = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));

      propertiesIndex++;
      ConditionConstants.CURRENT_ANSWERS_INDICES[0] = answerIndex++;
      Condition condition2 = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      ConditionConstants.CURRENT_ANSWERS_INDICES[1] = answerIndex;
      
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
      propertiesIndex++;
      answerIndex++;      
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.log("#If the user does choose NEVER to this question, this test will fail.");
      tbc.assertTrue("Asserting that false is returned by the selection of the user.", !condition.isSatisfied());
      
      propertiesIndex++;
      answerIndex++;
      condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
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
      propertiesIndex++;
      answerIndex++;      
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.log("#If the user does choose ALWAYS to this question, this test will fail.");
      tbc.assertTrue("Asserting that true is returned by the selection of the user.", condition.isSatisfied());

      propertiesIndex++;
      answerIndex++;      
      condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
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
      propertiesIndex++;
      answerIndex++;      
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.log("#If the user does not reject this question, this test will fail.");
      tbc.assertTrue("Asserting that false is returned by the selection of the user.", !condition.isSatisfied());
      
      propertiesIndex++;
      answerIndex++;      
      condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
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
      propertiesIndex++;
      answerIndex++;      
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.log("#If the user does not reject this question, this test will fail.");
      tbc.assertTrue("Asserting that false is returned by the selection of the user.", !condition.isSatisfied());
      
      propertiesIndex++;
      answerIndex++;      
      condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
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
      propertiesIndex++;
      answerIndex++;      
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));

      Hashtable hash = new Hashtable();

      Condition[] conditions = { condition };
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied(conditions, hash));
      ConditionConstants.CURRENT_ANSWER_INDEX = ++answerIndex;
      tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied(conditions, hash));

    } catch (Exception e) {
      tbc.fail("Unexpected exception was thrown + : " 
          + e.getClass().getName());
    }
  }  
  
  /**
   * This method asserts that if the user receives one question
   * using null as context, each oneshot question will be prompted.
   * 
   * @spec UserPromptCondition.isSatisfied(Condition[],Dictionary)
   */
  private void testIsSatisfied018() {
    tbc.log("#testIsSatisfied018");
    try {
      propertiesIndex++;
      answerIndex++;
      ConditionConstants.CURRENT_ANSWERS_INDICES = null;//there should be only one answer in next evaluation
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex++;
       
      Condition condition1 = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IS_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IS_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      
      Condition[] conditions = { condition1 };				

      tbc.log("#If the user does not accept the questions, this test will fail.");

      tbc.assertTrue("Asserting that isSatisfied returns true.", condition1.isSatisfied(conditions, null));
      tbc.log("#The second question must appear because null was passed as context. Otherwise the test will fail.");
      
      ConditionConstants.CURRENT_ANSWERS_INDICES = null;//there should be only one answer in next evaluation
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting that isSatisfied returns false.", !condition1.isSatisfied(conditions, null));
    } catch (Exception e) {
      tbc.fail("Unexpected exception was thrown + : " 
          + e.getClass().getName());
    }
  }      

}

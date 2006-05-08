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
import org.osgi.util.mobile.UserPromptCondition;

/**
 * @author Alexandre Alves
 * 
 * This Test Class Validates the implementation of
 * <code>isPostponed</code> method, according to MEG reference
 * documentation.
 */
public class IsPostponed {
  private PolicyTestControl tbc;
  private int propertiesIndex;
  private int answerIndex;

  public IsPostponed(PolicyTestControl tbc) {
    this.tbc = tbc;
    ConditionConstants.CONDITIONS_LEVEL  = ConditionConstants.IP_CONDITIONS_LEVELS;
    ConditionConstants.CONDITIONS_DEFAULT = ConditionConstants.IP_CONDITIONS_DEFAULT;
    ConditionConstants.CONDITIONS_CATALOG = ConditionConstants.IP_CONDITIONS_CATALOG;
    ConditionConstants.CONDITIONS_MESSAGE = ConditionConstants.IP_CONDITIONS_MESSAGE;
    ConditionConstants.CONDITIONS_ANSWERS = ConditionConstants.IP_ANSWERS;
    propertiesIndex = 0;
    answerIndex = 0;                
  }

  public void run() {   	
    testIsPostponed001();
    testIsPostponed002();
    testIsPostponed003();
    testIsPostponed004();
    testIsPostponed005();
    testIsPostponed006();
    testIsPostponed007();
    testIsPostponed008();
    testIsPostponed009();
    testIsPostponed010();
    testIsPostponed011();
    testIsPostponed012();
    testIsPostponed013();
    testIsPostponed014();
    testIsPostponed015();
  }

  /**
   * This method asserts that when an user interaction is needed
   * true is returned and after an isSatisified call, the interaction
   * is not needed anymore and isPostponed must return false using BLANKET
   * as level and the isSatisfied() method.
   * 
   * @spec UserPromptCondition.isPostponed()
   */
  private void testIsPostponed001() {
    tbc.log("#testIsPostponed001");
    try {
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IP_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_MESSAGE[propertiesIndex]
          }));

      tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied());
      tbc.assertTrue("Asserting that false is returned when an user interaction is not needed.", !condition.isPostponed());
      
      propertiesIndex++;
      answerIndex++;          
      condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IP_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_MESSAGE[propertiesIndex]
          }));

      tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if false is returned.", !condition.isSatisfied());
      tbc.assertTrue("Asserting that false is returned when an user interaction is not needed.", !condition.isPostponed());			

    } catch (Exception e) {
      tbc.fail("Unexpected exception was thrown + : " 
          + e.getClass().getName());
    }
  }

  /**
   * This method asserts that when an user interaction is needed
   * true is returned and after an isSatisified call, the interaction
   * is not needed anymore and isPostponed must return false using SESSION
   * as level and the isSatisfied() method.
   * 
   * @spec UserPromptCondition.isPostponed()
   */
  private void testIsPostponed002() {
    tbc.log("#testIsPostponed02");
    try {
      propertiesIndex++;
      answerIndex++;          
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IP_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_MESSAGE[propertiesIndex]
          }));

      tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied());
      tbc.assertTrue("Asserting that false is returned when an user interaction is not needed.", !condition.isPostponed());

      propertiesIndex++;
      answerIndex++;          
      condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IP_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_MESSAGE[propertiesIndex]
          }));

      tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if false is returned.", !condition.isSatisfied());
      tbc.assertTrue("Asserting that false is returned when an user interaction is not needed.", !condition.isPostponed());			

    } catch (Exception e) {
      tbc.fail("Unexpected exception was thrown + : " 
          + e.getClass().getName());
    }
  }  

  /**
   * This method asserts that when an user interaction is needed
   * true is returned and after an isSatisified call, the interaction
   * still need an user interaction using ONESHOT as level and the isSatisfied() method.
   * 
   * @spec UserPromptCondition.isPostponed()
   */
  private void testIsPostponed003() {
    tbc.log("#testIsPostponed03");
    try {
      propertiesIndex++;
      answerIndex++;          
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IP_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_MESSAGE[propertiesIndex]
          }));

      tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied());
      tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());

      propertiesIndex++;
      answerIndex++;          
      condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IP_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_MESSAGE[propertiesIndex]
          }));

      tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if false is returned.", !condition.isSatisfied());
      tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());			

    } catch (Exception e) {
      tbc.fail("Unexpected exception was thrown + : " 
          + e.getClass().getName());
    }
  }     

  /**
   * This method asserts that when an user interaction is needed
   * true is returned and after an isSatisified call, if never was choose
   * the interaction is stiil needed.
   * 
   * @spec UserPromptCondition.isPostponed()
   */
  private void testIsPostponed004() {
    tbc.log("#testIsPostponed04");
    try {
      propertiesIndex++;
      answerIndex++;          
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IP_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if false is returned.", !condition.isSatisfied());
      tbc.assertTrue("Asserting that false is returned after the user choose NEVER as answer.", !condition.isPostponed());			
    } catch (Exception e) {
      tbc.fail("Unexpected exception was thrown + : " 
          + e.getClass().getName());
    }
  }     

  /**
   * This method asserts that when an user interaction is needed
   * true is returned and after an isSatisified call, the interaction
   * is not needed anymore using BLANKET
   * as level and the isSatisfied(Condition[],Dictionary) method passing
   * null as Dictionary.
   * 
   * 
   * @spec UserPromptCondition.isPostponed()
   */
  private void testIsPostponed005() {
    tbc.log("#testIsPostponed005");
    propertiesIndex++;
    answerIndex++;          
    try {
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IP_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_MESSAGE[propertiesIndex]
          }));

      tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());

      Condition[] conditions = { condition };
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied(conditions, null));

      tbc.assertTrue("Asserting that false is returned when an user interaction is not needed.", !condition.isPostponed());

      propertiesIndex++;
      answerIndex++;          
      condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IP_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_MESSAGE[propertiesIndex]
          }));

      tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());

      conditions = new Condition[] { condition };
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if false is returned.", !condition.isSatisfied(conditions, null));

      tbc.assertTrue("Asserting that false is returned when an user interaction is not needed.", !condition.isPostponed());					
    } catch (Exception e) {
      tbc.fail("Unexpected exception was thrown + : " 
          + e.getClass().getName());
    }
  }

  /**
   * This method asserts that when an user interaction is needed
   * true is returned and after an isSatisified call, the interaction
   * is not needed anymore even USING SESSION
   * as level and the isSatisfied(Condition[],Dictionary) method passing
   * null as Dictionary.
   * 
   * @spec UserPromptCondition.isPostponed()
   */
  private void testIsPostponed006() {
    tbc.log("#testIsPostponed006");
    try {
      propertiesIndex++;
      answerIndex++;          
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IP_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_MESSAGE[propertiesIndex]
          }));

      tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());

      Condition[] conditions = { condition };
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied(conditions, null));

      tbc.assertTrue("Asserting that false is returned when an user interaction is not needed.", !condition.isPostponed());

      propertiesIndex++;
      answerIndex++;          
      condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IP_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_MESSAGE[propertiesIndex]
          }));

      tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());

      conditions = new Condition[] { condition };
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if false is returned.", !condition.isSatisfied(conditions, null));

      tbc.assertTrue("Asserting that false is returned when an user interaction is not needed.", !condition.isPostponed());			

    } catch (Exception e) {
      tbc.fail("Unexpected exception was thrown + : " 
          + e.getClass().getName());
    }
  }  

  /**
   * This method asserts that when an user interaction is needed
   * true is returned and after an isSatisified call, the interaction
   * still need an user interaction using ONESHOT
   * as level and the isSatisfied(Condition[],Dictionary) method passing
   * null as Dictionary.
   * 
   * @spec UserPromptCondition.isPostponed()
   */
  private void testIsPostponed007() {
    tbc.log("#testIsPostponed007");
    try {
      propertiesIndex++;
      answerIndex++;          
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IP_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_MESSAGE[propertiesIndex]
          }));

      tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());

      Condition[] conditions = { condition };
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied(conditions, null));

      tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());	

      propertiesIndex++;
      answerIndex++;          
      condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IP_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_MESSAGE[propertiesIndex]
          }));

      tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());

      conditions = new Condition[] { condition };
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if false is returned.", !condition.isSatisfied(conditions, null));

      tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());					
    } catch (Exception e) {
      tbc.fail("Unexpected exception was thrown + : " 
          + e.getClass().getName());
    }
  }     

  /**
   * This method asserts that when an user interaction is needed
   * true is returned and after an isSatisified call, the interaction
   * is still needed when the user has chose NEVER as answer even USING SESSION
   * as level and the isSatisfied(Condition[],Dictionary) method passing
   * null as Dictionary.
   * 
   * @spec UserPromptCondition.isPostponed()
   */
  private void testIsPostponed008() {
    tbc.log("#testIsPostponed008");
    try {
      propertiesIndex++;
      answerIndex++;          
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IP_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_MESSAGE[propertiesIndex]
          }));

      Condition[] conditions = { condition };
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if false is returned.", !condition.isSatisfied(conditions, null));

      tbc.assertTrue("Asserting that false is returned when an user interaction is not needed.", !condition.isPostponed());			
    } catch (Exception e) {
      tbc.fail("Unexpected exception was thrown + : " 
          + e.getClass().getName());
    }
  }    

  /**
   * This method asserts that when an user interaction is needed
   * true is returned and after an isSatisified call, the interaction
   * is not needed anymore and isPostponed must return false using BLANKET
   * as level and the isSatisfied(Condition[],Dictionary) method passing
   * a hashtable as Dictionary.
   * 
   * @spec UserPromptCondition.isPostponed()
   */
  private void testIsPostponed009() {
    tbc.log("#testIsPostponed009");
    try {
      propertiesIndex++;
      answerIndex++;          
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IP_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_MESSAGE[propertiesIndex]
          }));

      tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());

      Hashtable hash = new Hashtable();

      Condition[] conditions = { condition };
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied(conditions, hash));

      tbc.assertTrue("Asserting that false is returned when an user interaction is not needed.", !condition.isPostponed());

      propertiesIndex++;
      answerIndex++;          
      condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IP_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_MESSAGE[propertiesIndex]
          }));

      tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());

      conditions = new Condition[]{ condition };
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if false is returned.", !condition.isSatisfied(conditions, hash));

      tbc.assertTrue("Asserting that false is returned when an user interaction is not needed.", !condition.isPostponed());			
    } catch (Exception e) {
      tbc.fail("Unexpected exception was thrown + : " 
          + e.getClass().getName());
    }
  }

  /**
   * This method asserts that when an user interaction is needed
   * true is returned and after an isSatisified call, the interaction
   * is not needed anymore and isPostponed must return false using SESSION
   * as level and the isSatisfied(Condition[],Dictionary) method passing
   * a hashtable as Dictionary.
   * 
   * @spec UserPromptCondition.isPostponed()
   */
  private void testIsPostponed010() {
    tbc.log("#testIsPostponed010");
    try {
      propertiesIndex++;
      answerIndex++;          
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IP_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_MESSAGE[propertiesIndex]
          }));

      tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());

      Hashtable hash = new Hashtable();

      Condition[] conditions = { condition };
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied(conditions, hash));

      tbc.assertTrue("Asserting that false is returned when an user interaction is not needed.", !condition.isPostponed());

      propertiesIndex++;
      answerIndex++;          
      condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IP_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_MESSAGE[propertiesIndex]
          }));

      tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());

      conditions = new Condition[] { condition };
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if false is returned.", !condition.isSatisfied(conditions, hash));

      tbc.assertTrue("Asserting that false is returned when an user interaction is not needed.", !condition.isPostponed());			
    } catch (Exception e) {
      tbc.fail("Unexpected exception was thrown + : " 
          + e.getClass().getName());
    }
  }  

  /**
   * This method asserts that when an user interaction is needed
   * true is returned and after an isSatisified call, the isPostponed
   * still returns true using LEVEL_ONESHOT and
   * isSatisfied(Condition[],Dictionary) method passing
   * a hashtable as Dictionary.
   * 
   * @spec UserPromptCondition.isPostponed()
   */
  private void testIsPostponed011() {
    tbc.log("#testIsPostponed011");
    try {
      propertiesIndex++;
      answerIndex++;          
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IP_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_MESSAGE[propertiesIndex]
          }));

      tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());

      Hashtable hash = new Hashtable();

      Condition[] conditions = { condition };
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied(conditions, hash));

      tbc.assertTrue("Asserting that true is returned when an user interaction is not needed.", condition.isPostponed());

      propertiesIndex++;
      answerIndex++;          
      condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IP_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_MESSAGE[propertiesIndex]
          }));

      tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());

      conditions = new Condition[] { condition };
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if false is returned.", !condition.isSatisfied(conditions, hash));

      tbc.assertTrue("Asserting that true is returned when an user interaction is not needed.", condition.isPostponed());			
    } catch (Exception e) {
      tbc.fail("Unexpected exception was thrown + : " 
          + e.getClass().getName());
    }
  }     

  /**
   * This method asserts that when an user interaction is needed
   * true is returned and after an isSatisified call, the interaction
   * is still needed if the user choose NEVER as answer. Therefore, 
   * isPostponed must return true using SESSION
   * as level and the isSatisfied(Condition[],Dictionary) method passing
   * a hashtable as Dictionary.
   * 
   * @spec UserPromptCondition.isPostponed()
   */
  private void testIsPostponed012() {
    tbc.log("#testIsPostponed012");
    try {
      propertiesIndex++;
      answerIndex++;          
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IP_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_MESSAGE[propertiesIndex]
          }));

      Hashtable hash = new Hashtable();

      Condition[] conditions = { condition };
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if false is returned.", !condition.isSatisfied(conditions, hash));

      tbc.assertTrue("Asserting that false is returned when an user interaction is not needed.", !condition.isPostponed());

    } catch (Exception e) {
      tbc.fail("Unexpected exception was thrown + : " 
          + e.getClass().getName());
    }
  }

  /**
   * This method asserts that when an user interaction is needed
   * true is returned and after an isSatisified call, if never was choose
   * the interaction is stiil needed.
   * 
   * @spec UserPromptCondition.isPostponed()
   */
  private void testIsPostponed013() {
    tbc.log("#testIsPostponed013");
    try {
      propertiesIndex++;
      answerIndex++;          
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IP_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if false is returned.", !condition.isSatisfied());
      tbc.assertTrue("Asserting that false is returned after the user choose NEVER as answer.", !condition.isPostponed());			
    } catch (Exception e) {
      tbc.fail("Unexpected exception was thrown + : " 
          + e.getClass().getName());
    }
  }    

  /**
   * This method asserts that when an user interaction is needed
   * true is returned and after an isSatisified call, the interaction
   * is still needed when the user has chose NEVER as answer even USING ONESHOT
   * as level and the isSatisfied(Condition[],Dictionary) method passing
   * null as Dictionary.
   * 
   * @spec UserPromptCondition.isPostponed()
   */
  private void testIsPostponed014() {
    tbc.log("#testIsPostponed014");
    try {
      propertiesIndex++;
      answerIndex++;          
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IP_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_MESSAGE[propertiesIndex]
          }));

      Condition[] conditions = { condition };
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if false is returned.", !condition.isSatisfied(conditions, null));

      tbc.assertTrue("Asserting that false is returned when an user interaction is not needed.", !condition.isPostponed());			
    } catch (Exception e) {
      tbc.fail("Unexpected exception was thrown + : " 
          + e.getClass().getName());
    }
  }

  /**
   * This method asserts that when an user interaction is needed
   * true is returned and after an isSatisified call, the interaction
   * is still needed if the user choose NEVER as answer. Therefore, 
   * isPostponed must return true using ONESHOT
   * as level and the isSatisfied(Condition[],Dictionary) method passing
   * a hashtable as Dictionary.
   * 
   * @spec UserPromptCondition.isPostponed()
   */
  private void testIsPostponed015() {
    tbc.log("#testIsPostponed015");
    try {
      propertiesIndex++;
      answerIndex++;          
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IP_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IP_CONDITIONS_MESSAGE[propertiesIndex]
          }));

      Hashtable hash = new Hashtable();

      Condition[] conditions = { condition };
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if false is returned.", !condition.isSatisfied(conditions, hash));

      tbc.assertTrue("Asserting that false is returned when an user interaction is not needed.", !condition.isPostponed());

    } catch (Exception e) {
      tbc.fail("Unexpected exception was thrown + : " 
          + e.getClass().getName());
    }
  }

}

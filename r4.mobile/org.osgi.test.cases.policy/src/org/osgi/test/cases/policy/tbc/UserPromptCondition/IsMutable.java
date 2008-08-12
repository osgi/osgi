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
 * <code>isMutable</code> method, according to MEG reference
 * documentation.
 */
public class IsMutable {
  private PolicyTestControl tbc;
  private int propertiesIndex;
  private int answerIndex;

  public IsMutable(PolicyTestControl tbc) {
    this.tbc = tbc;
    ConditionConstants.CONDITIONS_LEVEL  = ConditionConstants.IM_CONDITIONS_LEVELS;
    ConditionConstants.CONDITIONS_DEFAULT = ConditionConstants.IM_CONDITIONS_DEFAULT;
    ConditionConstants.CONDITIONS_CATALOG = ConditionConstants.IM_CONDITIONS_CATALOG;
    ConditionConstants.CONDITIONS_MESSAGE = ConditionConstants.IM_CONDITIONS_MESSAGE;
    ConditionConstants.CONDITIONS_ANSWERS = ConditionConstants.IM_ANSWERS;
    propertiesIndex = 0;
    answerIndex = 0;        
  }

  public void run() {   	
    testIsMutable001();
    testIsMutable002();
    testIsMutable003();
    testIsMutable004();
    testIsMutable005();
  }

  /**
   * This method asserts that if no call to isSatisfied
   * was made, true is returned using isSatisfied().
   * 
   * @spec UserPromptCondition.isMutable()
   */
  private void testIsMutable001() {
    tbc.log("#testIsMutable001");
    try {
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IM_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IM_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IM_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IM_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;//although there should be no user interaction in this case
      tbc.assertTrue("Asserting if true is returned when no call to isSatisfied was made.", condition.isMutable());
    } catch (Exception e) {
      tbc.fail("Unexpected exception was thrown + : " 
          + e.getClass().getName());
    }
  }

  /**
   * This method asserts that when a call to isSatisfied
   * was made using ONESHOT as level, true is returned using isSatisfied().
   * 
   * @spec UserPromptCondition.isMutable()
   */
  private void testIsMutable002() {
    tbc.log("#testIsMutable002");
    try {
      propertiesIndex++;
      answerIndex++;          
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IM_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IM_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IM_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IM_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied());
      tbc.assertTrue("Asserting if true is returned when a call to isSatisfied was made using ONESHOT as level.", condition.isMutable());
      
      propertiesIndex++;
      answerIndex++;          
      condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IM_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IM_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IM_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IM_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if false is returned.", !condition.isSatisfied());
      tbc.assertTrue("Asserting if true is returned when a call to isSatisfied was made using ONESHOT as level.", condition.isMutable());			
    } catch (Exception e) {
      tbc.fail("Unexpected exception was thrown + : " 
          + e.getClass().getName());
    }
  } 

  /**
   * This method asserts that when a call to isSatisfied
   * was made using ONESHOT as level, true is returned
   * using isSatisfied(Condition[],Dictionary) passing null
     as context.
   * 
   * @spec UserPromptCondition.isMutable()
   */
  private void testIsMutable003() {
    tbc.log("#testIsMutable003");
    try {
      propertiesIndex++;
      answerIndex++;          
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IM_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IM_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IM_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IM_CONDITIONS_MESSAGE[propertiesIndex]
          }));

      Condition[] conditions = { condition };
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied(conditions, null));
      tbc.assertTrue("Asserting if true is returned when a call to isSatisfied was made using ONESHOT as level.", condition.isMutable());

      propertiesIndex++;
      answerIndex++;          
      condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IM_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IM_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IM_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IM_CONDITIONS_MESSAGE[propertiesIndex]
          }));

      conditions = new Condition[] { condition };
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if false is returned.", !condition.isSatisfied(conditions, null));

      tbc.assertTrue("Asserting if true is returned when a call to isSatisfied was made using ONESHOT as level.", condition.isMutable());			
    } catch (Exception e) {
      tbc.fail("Unexpected exception was thrown + : " 
          + e.getClass().getName());
    }
  } 

  /**
   * This method asserts that when a call to isSatisfied
   * was made using ONESHOT as level, true is returned
   * using isSatisfied(Condition[],Dictionary) passing
   * a hashtable as context.
   * 
   * @spec UserPromptCondition.isMutable()
   */
  private void testIsMutable004() {
    tbc.log("#testIsMutable004");
    try {
      propertiesIndex++;
      answerIndex++;          
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IM_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IM_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IM_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IM_CONDITIONS_MESSAGE[propertiesIndex]
          }));

      Condition[] conditions = { condition };
      Hashtable hash = new Hashtable();
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied(conditions, hash));
      tbc.assertTrue("Asserting if true is returned when a call to isSatisfied was made using ONESHOT as level.", condition.isMutable());

      propertiesIndex++;
      answerIndex++;          
      condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IM_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IM_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IM_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IM_CONDITIONS_MESSAGE[propertiesIndex]
          }));

      conditions = new Condition[] { condition };
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if false is returned.", !condition.isSatisfied(conditions, hash));

      tbc.assertTrue("Asserting if true is returned when a call to isSatisfied was made using ONESHOT as level.", condition.isMutable());			
    } catch (Exception e) {
      tbc.fail("Unexpected exception was thrown + : " 
          + e.getClass().getName());
    }
  } 

  /**
   * This method asserts that when a call to isSatisfied
   * was made using ONESHOT as level and the user chose never
   * as answer, isMutable returns false.
   * 
   * @spec UserPromptCondition.isMutable()
   */
  private void testIsMutable005() {
    tbc.log("#testIsMutable005");
    try {
      propertiesIndex++;
      answerIndex++;          
      Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
          new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
            ConditionConstants.IM_CONDITIONS_LEVELS[propertiesIndex],
            ConditionConstants.IM_CONDITIONS_DEFAULT[propertiesIndex],
            ConditionConstants.IM_CONDITIONS_CATALOG[propertiesIndex],
            ConditionConstants.IM_CONDITIONS_MESSAGE[propertiesIndex]
          }));
      ConditionConstants.CURRENT_ANSWER_INDEX = answerIndex;
      tbc.assertTrue("Asserting if false is returned.", !condition.isSatisfied());
      tbc.assertTrue("Asserting if false is returned when a call to isSatisfied was made using ONESHOT as level and the user chose NEVER as answer.", !condition.isMutable());

    } catch (Exception e) {
      tbc.fail("Unexpected exception was thrown + : " 
          + e.getClass().getName());
    }
  }      

}

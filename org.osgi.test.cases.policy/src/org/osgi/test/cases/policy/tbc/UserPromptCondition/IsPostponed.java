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

    public IsPostponed(PolicyTestControl tbc) {
        this.tbc = tbc;
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
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.CATALOG_NAME, "1: Choose always as answer. Otherwise this test method will fail." }));
						
			tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());
			tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied());
			tbc.assertTrue("Asserting that false is returned when an user interaction is not needed.", !condition.isPostponed());
			
			condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.CATALOG_NAME, "1: Choose never as answer. Otherwise this test method will fail." }));
						
			tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());
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
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.CATALOG_NAME, "2: Choose a positive answer. Otherwise this test method will fail." }));
						
			tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());
			tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied());
			tbc.assertTrue("Asserting that false is returned when an user interaction is not needed.", !condition.isPostponed());
			
			condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.CATALOG_NAME, "2: Choose a negative answer. Otherwise this test method will fail." }));
						
			tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());
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
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.CATALOG_NAME, "3: Choose yes as answer. Otherwise this test method will fail." }));
						
			tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());
			tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied());
			tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());
			
			condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.CATALOG_NAME, "3: Choose no as answer. Otherwise this test method will fail." }));
						
			tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());
			tbc.assertTrue("Asserting if false is returned.", !condition.isSatisfied());
			tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());			
			
        } catch (Exception e) {
            tbc.fail("Unexpected exception was thrown + : " 
                    + e.getClass().getName());
        }
    }     
          
    /**
     * This method asserts that when an user interaction is needed
     * true is returned and after an isSatisified call, the interaction
     * still need an user interaction using ALL levels and the isSatisfied() method.
     * 
     * @spec UserPromptCondition.isPostponed()
     */
    private void testIsPostponed004() {
         tbc.log("#testIsPostponed04");
        try {
        	//TODO waiting for nokia feedback
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.ALL_LEVELS,
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.CATALOG_NAME, "4: Choose whatever you want." }));
						
			tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());
			condition.isSatisfied();
			tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());			
        } catch (Exception e) {
            tbc.fail("Unexpected exception was thrown + : " 
                    + e.getClass().getName());
        }
    }    
    
    /**
     * This method asserts that when an user interaction is needed
     * true is returned and after an isSatisified call, the interaction
     * still need an user interaction using BLANKET
     * as level and the isSatisfied(Condition[],Dictionary) method passing
     * null as Dictionary.
     * 
     * 
     * @spec UserPromptCondition.isPostponed()
     */
    private void testIsPostponed005() {
         tbc.log("#testIsPostponed005");
        try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.CATALOG_NAME, "5: Choose always as answer. Otherwise this test method will fail." }));
						
			tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());
			
			Condition[] conditions = { condition };
			
			tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied(conditions, null));
			
			tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());
			
			condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.CATALOG_NAME, "5: Choose never as answer. Otherwise this test method will fail." }));
						
			tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());
			
			conditions = new Condition[] { condition };
			
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
     * still need an user interaction using SESSION
     * as level and the isSatisfied(Condition[],Dictionary) method passing
     * null as Dictionary.
     * 
     * @spec UserPromptCondition.isPostponed()
     */
    private void testIsPostponed006() {
         tbc.log("#testIsPostponed006");
        try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.CATALOG_NAME, "6: Choose a positive answer. Otherwise this test method will fail." }));
						
			tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());
			
			Condition[] conditions = { condition };
			
			tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied(conditions, null));
			
			tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());
			
			condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.CATALOG_NAME, "6: Choose a negative answer. Otherwise this test method will fail." }));
						
			tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());
			
			conditions = new Condition[] { condition };
			
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
     * still need an user interaction using ONESHOT
     * as level and the isSatisfied(Condition[],Dictionary) method passing
     * null as Dictionary.
     * 
     * @spec UserPromptCondition.isPostponed()
     */
    private void testIsPostponed007() {
         tbc.log("#testIsPostponed007");
        try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.CATALOG_NAME, "7: Choose yes as answer. Otherwise this test method will fail." }));
						
			tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());
			
			Condition[] conditions = { condition };
			
			tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied(conditions, null));
			
			tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());	
			
			condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.CATALOG_NAME, "7: Choose no as answer. Otherwise this test method will fail." }));
						
			tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());
			
			conditions = new Condition[] { condition };
			
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
     * still need an user interaction using ALL levels and the 
     * isSatisfied(Condition[],Dictionary) method passing
     * null as Dictionary.
     * 
     * @spec UserPromptCondition.isPostponed()
     */
    private void testIsPostponed008() {
         tbc.log("#testIsPostponed008");
        try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.ALL_LEVELS,
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.CATALOG_NAME, "8: Choose whatever you want." }));
						
			tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());
			
			Condition[] conditions = { condition };
			
			condition.isSatisfied(conditions, null);
			
			tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());			
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
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.CATALOG_NAME, "9: Choose always as answer. Otherwise this test method will fail." }));
						
			tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());
			
			Hashtable hash = new Hashtable();
			
			Condition[] conditions = { condition };
			
			tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied(conditions, hash));
			
			tbc.assertTrue("Asserting that false is returned when an user interaction is not needed.", !condition.isPostponed());
			
			condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.CATALOG_NAME, "9: Choose never as answer. Otherwise this test method will fail." }));
						
			tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());
					
			conditions = new Condition[]{ condition };
			
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
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.CATALOG_NAME, "10: Choose a positive answer. Otherwise this test method will fail." }));
						
			tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());
			
			Hashtable hash = new Hashtable();
			
			Condition[] conditions = { condition };
			
			tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied(conditions, hash));
			
			tbc.assertTrue("Asserting that false is returned when an user interaction is not needed.", !condition.isPostponed());
			
			condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.CATALOG_NAME, "10: Choose a negative answer. Otherwise this test method will fail." }));
						
			tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());
			
			conditions = new Condition[] { condition };
			
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
     * still need an user interaction using ONESHOT as level and the
     * isSatisfied(Condition[],Dictionary) method passing
     * a hashtable as Dictionary.
     * 
     * @spec UserPromptCondition.isPostponed()
     */
    private void testIsPostponed011() {
         tbc.log("#testIsPostponed011");
        try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.CATALOG_NAME, "11: Choose yes as answer. Otherwise this test method will fail." }));
						
			tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());
			
			Hashtable hash = new Hashtable();
			
			Condition[] conditions = { condition };
			
			tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied(conditions, hash));
			
			tbc.assertTrue("Asserting that false is returned when an user interaction is not needed.", !condition.isPostponed());
			
			condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.CATALOG_NAME, "11: Choose no as answer. Otherwise this test method will fail." }));
						
			tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());
					
			conditions = new Condition[] { condition };
			
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
     * still need an user interaction using ALL levels and the 
     * isSatisfied(Condition[],Dictionary) method passing
     * a hashtable as Dictionary.
     * 
     * @spec UserPromptCondition.isPostponed()
     */
    private void testIsPostponed012() {
         tbc.log("#testIsPostponed012");
        try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.ALL_LEVELS,
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.CATALOG_NAME, "12: Choose whatever you want." }));
						
			tbc.assertTrue("Asserting that true is returned when an user interaction is needed.", condition.isPostponed());
			
			Hashtable hash = new Hashtable();
			
			Condition[] conditions = { condition };
			
			condition.isSatisfied(conditions, hash);
			
			tbc.assertTrue("Asserting that false is returned when an user interaction is not needed.", !condition.isPostponed());			
        } catch (Exception e) {
            tbc.fail("Unexpected exception was thrown + : " 
                    + e.getClass().getName());
        }
    }        
    
}

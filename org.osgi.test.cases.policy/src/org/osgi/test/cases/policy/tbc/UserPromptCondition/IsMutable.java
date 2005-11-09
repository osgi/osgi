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

    public IsMutable(PolicyTestControl tbc) {
        this.tbc = tbc;
    }

    public void run() {   	
        testIsMutable001();
        testIsMutable002();
        testIsMutable003();
        testIsMutable004();
        testIsMutable005();
        testIsMutable006();
        testIsMutable007();
        testIsMutable008();
        testIsMutable009();
        testIsMutable010();
        testIsMutable011();
        testIsMutable012();
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
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.CATALOG_NAME, "1-Choose whatever you want." }));
			tbc.assertTrue("Asserting if true is returned when no call to isSatisfied was made.", condition.isMutable());
        } catch (Exception e) {
            tbc.fail("Unexpected exception was thrown + : " 
                    + e.getClass().getName());
        }
    }
    
    /**
     * This method asserts that when a call to isSatisfied
     * was made using BLANKET as level, false is returned using isSatisfied().
     * 
     * @spec UserPromptCondition.isMutable()
     */
    private void testIsMutable002() {
         tbc.log("#testIsMutable002");
        try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.CATALOG_NAME, "2-Choose always as answer. Otherwise this test method will fail." }));
			tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied());
			tbc.assertTrue("Asserting if false is returned when a call to isSatisfied was made.", !condition.isMutable());
			
			condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.CATALOG_NAME, "2-Choose never as answer. Otherwise this test method will fail." }));
			tbc.assertTrue("Asserting if false is returned.", !condition.isSatisfied());
			tbc.assertTrue("Asserting if false is returned when a call to isSatisfied was made.", !condition.isMutable());			
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
    private void testIsMutable003() {
         tbc.log("#testIsMutable003");
        try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.CATALOG_NAME, "3-Choose yes as answer. Otherwise this test method will fail." }));
			tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied());
			tbc.assertTrue("Asserting if true is returned when a call to isSatisfied was made using ONESHOT as level.", condition.isMutable());
			
			condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.CATALOG_NAME, "3-Choose no as answer. Otherwise this test method will fail." }));
			tbc.assertTrue("Asserting if false is returned.", !condition.isSatisfied());
			tbc.assertTrue("Asserting if true is returned when a call to isSatisfied was made using ONESHOT as level.", condition.isMutable());			
        } catch (Exception e) {
            tbc.fail("Unexpected exception was thrown + : " 
                    + e.getClass().getName());
        }
    } 
    
    /**
     * This method asserts that when a call to isSatisfied
     * was made using SESSION as level, true is returned using isSatisfied().
     * 
     * @spec UserPromptCondition.isMutable()
     */
    private void testIsMutable004() {
         tbc.log("#testIsMutable004");
        try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.CATALOG_NAME, "4-Choose a posistive answer. Otherwise this test method will fail." }));
			tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied());
			tbc.assertTrue("Asserting if true is returned when a call to isSatisfied was made using SESSION as level.", condition.isMutable());
			
			condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.CATALOG_NAME, "4-Choose a negative answer. Otherwise this test method will fail." }));
			tbc.assertTrue("Asserting if false is returned.", !condition.isSatisfied());
			tbc.assertTrue("Asserting if true is returned when a call to isSatisfied was made using SESSION as level.", condition.isMutable());			
        } catch (Exception e) {
            tbc.fail("Unexpected exception was thrown + : " 
                    + e.getClass().getName());
        }
    } 
    
    /**
     * This method asserts that when a call to isSatisfied
     * was made using BLANKET and SESSION as level, true is returned using isSatisfied().
     * 
     * @spec UserPromptCondition.isMutable()
     */
    private void testIsMutable005() {
         tbc.log("#testIsMutable005");
        try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_SESSION+","+PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.CATALOG_NAME, "5-Choose a positive answer. Otherwise this test method will fail." }));
			tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied());
			tbc.assertTrue("Asserting if true is returned when a call to isSatisfied was made using BLANKET and SESSION as level.", condition.isMutable());
			
			condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_SESSION+","+PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.CATALOG_NAME, "5-Choose a negative answer. Otherwise this test method will fail." }));
			tbc.assertTrue("Asserting if false is returned.", !condition.isSatisfied());
			tbc.assertTrue("Asserting if true is returned when a call to isSatisfied was made using BLANKET and SESSION as level.", condition.isMutable());			
        } catch (Exception e) {
            tbc.fail("Unexpected exception was thrown + : " 
                    + e.getClass().getName());
        }
    }    
   
    /**
     * This method asserts that when a call to isSatisfied
     * was made using BLANKET as level, true is returned using
     * isSatisfied(Condition[],Dictionary) because null was passed
     * as context.
     * 
     * @spec UserPromptCondition.isMutable()
     */
    private void testIsMutable006() {
         tbc.log("#testIsMutable006");
        try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.CATALOG_NAME, "6-Choose always as answer. Otherwise this test method will fail." }));

			Condition[] conditions = { condition };
			
			tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied(conditions, null));
			tbc.assertTrue("Asserting if true is returned when a call to isSatisfied was made.", condition.isMutable());
			
			condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.CATALOG_NAME, "6-Choose never as answer. Otherwise this test method will fail." }));

			conditions = new Condition[] { condition };
			
			tbc.assertTrue("Asserting if false is returned.", !condition.isSatisfied(conditions, null));
			tbc.assertTrue("Asserting if true is returned when a call to isSatisfied was made.", condition.isMutable());			
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
    private void testIsMutable007() {
         tbc.log("#testIsMutable007");
        try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.CATALOG_NAME, "7-Choose yes as answer. Otherwise this test method will fail." }));

			Condition[] conditions = { condition };
			tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied(conditions, null));
			
			tbc.assertTrue("Asserting if true is returned when a call to isSatisfied was made using ONESHOT as level.", condition.isMutable());
			
			condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.CATALOG_NAME, "7-Choose no as answer. Otherwise this test method will fail." }));

			conditions = new Condition[] { condition };
			tbc.assertTrue("Asserting if false is returned.", !condition.isSatisfied(conditions, null));
			
			tbc.assertTrue("Asserting if true is returned when a call to isSatisfied was made using ONESHOT as level.", condition.isMutable());			
        } catch (Exception e) {
            tbc.fail("Unexpected exception was thrown + : " 
                    + e.getClass().getName());
        }
    } 
    
    /**
     * This method asserts that when a call to isSatisfied
     * was made using SESSION as level, true is returned
     * using isSatisfied(Condition[],Dictionary) passing null
     * as context.
     * 
     * @spec UserPromptCondition.isMutable()
     */
    private void testIsMutable008() {
         tbc.log("#testIsMutable008");
        try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.CATALOG_NAME, "8-Choose a positive answer. Otherwise this test method will fail." }));
			
			Condition[] conditions = { condition };
			tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied(conditions, null));
			
			tbc.assertTrue("Asserting if true is returned when a call to isSatisfied was made using SESSION as level.", condition.isMutable());
			
			condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.CATALOG_NAME, "8-Choose a negative answer. Otherwise this test method will fail." }));
			
			conditions = new Condition[] { condition };
			tbc.assertTrue("Asserting if false is returned.", !condition.isSatisfied(conditions, null));
			
			tbc.assertTrue("Asserting if true is returned when a call to isSatisfied was made using SESSION as level.", condition.isMutable());			
        } catch (Exception e) {
            tbc.fail("Unexpected exception was thrown + : " 
                    + e.getClass().getName());
        }
    } 
    
    /**
     * This method asserts that when a call to isSatisfied
     * was made using BLANKET and SESSION as level, true is returned
     * using isSatisfied(Condition[],Dictionary) passing null as context.
     * 
     * @spec UserPromptCondition.isMutable()
     */
    private void testIsMutable009() {
         tbc.log("#testIsMutable009");
        try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_SESSION+","+PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.CATALOG_NAME, "9-Choose whatever you want." })
							);
			
			Condition[] conditions = { condition };
			condition.isSatisfied(conditions, null);
			
			tbc.assertTrue("Asserting if true is returned when a call to isSatisfied was made using BLANKET and SESSION as level.", condition.isMutable());
        } catch (Exception e) {
            tbc.fail("Unexpected exception was thrown + : " 
                    + e.getClass().getName());
        }
    }
    
    /**
     * This method asserts that when a call to isSatisfied
     * was made using BLANKET as level, false is returned using
     * isSatisfied(Condition[],Dictionary) because a hashtable
     * was passed as context.
     * 
     * @spec UserPromptCondition.isMutable()
     */
    private void testIsMutable010() {
         tbc.log("#testIsMutable010");
        try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.CATALOG_NAME, "10-Choose always as answer. Otherwise this test method will fail." }));

			Condition[] conditions = { condition };
			Hashtable hash = new Hashtable();
			
			
			tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied(conditions, hash));
			tbc.assertTrue("Asserting if false is returned when a call to isSatisfied was made.", !condition.isMutable());
			
			condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.CATALOG_NAME, "10-Choose never as answer. Otherwise this test method will fail." }));

			conditions = new Condition[] { condition };
			
			tbc.assertTrue("Asserting if false is returned.", !condition.isSatisfied(conditions, hash));
			tbc.assertTrue("Asserting if false is returned when a call to isSatisfied was made.", !condition.isMutable());			
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
    private void testIsMutable011() {
         tbc.log("#testIsMutable011");
        try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.CATALOG_NAME, "11-Choose yes as answer. Otherwise this test method will fail." }));

			Condition[] conditions = { condition };
			Hashtable hash = new Hashtable();
			tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied(conditions, hash));
			
			tbc.assertTrue("Asserting if true is returned when a call to isSatisfied was made using ONESHOT as level.", condition.isMutable());
			
			condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.CATALOG_NAME, "11-Choose no as answer. Otherwise this test method will fail." }));

			conditions = new Condition[] { condition };
			tbc.assertTrue("Asserting if false is returned.", !condition.isSatisfied(conditions, hash));
			
			tbc.assertTrue("Asserting if true is returned when a call to isSatisfied was made using ONESHOT as level.", condition.isMutable());			
        } catch (Exception e) {
            tbc.fail("Unexpected exception was thrown + : " 
                    + e.getClass().getName());
        }
    } 
    
    /**
     * This method asserts that when a call to isSatisfied
     * was made using SESSION as level, true is returned
     * using isSatisfied(Condition[],Dictionary) passing a
     * hashtable as context.
     * 
     * @spec UserPromptCondition.isMutable()
     */
    private void testIsMutable012() {
         tbc.log("#testIsMutable012");
        try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.CATALOG_NAME, "12-Choose a positive answer. Otherwise this test method will fail." }));
			
			Condition[] conditions = { condition };
			Hashtable hash = new Hashtable();
			tbc.assertTrue("Asserting if true is returned.", condition.isSatisfied(conditions, hash));
			
			tbc.assertTrue("Asserting if true is returned when a call to isSatisfied was made using SESSION as level.", condition.isMutable());
			
			condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo(UserPromptCondition.class.getName(), new String[] {
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.CATALOG_NAME, "12-Choose a negative answer. Otherwise this test method will fail." }));
			
			conditions = new Condition[] { condition };
			tbc.assertTrue("Asserting if false is returned.", !condition.isSatisfied(conditions, hash));
			
			tbc.assertTrue("Asserting if true is returned when a call to isSatisfied was made using SESSION as level.", condition.isMutable());			
        } catch (Exception e) {
            tbc.fail("Unexpected exception was thrown + : " 
                    + e.getClass().getName());
        }
    }    
    
}

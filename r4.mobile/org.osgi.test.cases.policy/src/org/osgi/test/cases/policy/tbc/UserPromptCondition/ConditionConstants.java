package org.osgi.test.cases.policy.tbc.UserPromptCondition;

import org.osgi.test.cases.policy.tbc.PolicyConstants;

public class ConditionConstants {
  //Answers
  /**
   * Holds the constat which indicates oneshot yes answer
   */
  public static final int ONESHOT_YES    = 0;
  /**
   * Holds the constat which indicates oneshot no answer
   */
  public static final int ONESHOT_NO     = 1;
  /**
   * Holds the constat which indicates oneshot never answer
   */
  public static final int ONESHOT_NEVER  = 2;
  /**
   * Holds the constat which indicates session yes answer
   */
  public static final int SESSION_YES    = 3;
  /**
   * Holds the constat which indicates session no answer
   */
  public static final int SESSION_NO     = 4;
  /**
   * Holds the constat which indicates session never answer
   */
  public static final int SESSION_NEVER  = 5;
  /**
   * Holds the constat which indicates blanket always answer
   */
  public static final int BLANKET_ALWAYS = 6;
  /**
   * Holds the constat which indicates blanket never answer
   */
  public static final int BLANKET_NEVER  = 7;

  /**
   * Holds the array with condition levels which currantly are used to create conditions with 
   */
  public static String[] CONDITIONS_LEVEL = null;
  /**
   * Holds the array with default conditions which currantly are used to create conditions with 
   */
  public static String[] CONDITIONS_DEFAULT = null;
  /**
   * Holds the array with condition catalog which currantly are used to create conditions with 
   */
  public static String[] CONDITIONS_CATALOG = null;
  /**
   * Holds the array with condition messages which currantly are used to create conditions with 
   */
  public static String[] CONDITIONS_MESSAGE = null;
  /**
   * Holds the array with correct answers for currently tested conditions 
   */
  public static int[] CONDITIONS_ANSWERS = null;
  
  /**
   * Holds the index of the answer of currently tested condition. This index points to element in 
   * CONDITIONS_ANSWERS array
   */
  public static int CURRENT_ANSWER_INDEX = -1;
  /**
   * Holds the idices of correct answers for currently tested conditions. This array is used when
   * several conditions are evaluated at the same time. it should be used when CURRENT_ANSWER_INDEX 
   * is equal to -1 
   */
  public static int[] CURRENT_ANSWERS_INDICES = null;
  
  
  //IsSatisfied
  /**
   * Holds the condition's level properties used to create test condition in IsSatisfied test class.
   */
  public static final String[] IS_CONDITIONS_LEVELS = new String[] {
    //testIsSatisfied001
    PolicyConstants.LEVEL_BLANKET,
    PolicyConstants.LEVEL_BLANKET,
    //testIsSatisfied002
    PolicyConstants.LEVEL_BLANKET,
    PolicyConstants.LEVEL_BLANKET,
    //testIsSatisfied003
    PolicyConstants.LEVEL_ONESHOT,
    PolicyConstants.LEVEL_ONESHOT,
    //testIsSatisfied004
    PolicyConstants.LEVEL_SESSION,
    PolicyConstants.LEVEL_SESSION,
    //testIsSatisfied005
    PolicyConstants.LEVEL_SESSION,
    PolicyConstants.LEVEL_SESSION,
    //testIsSatisfied006
    PolicyConstants.LEVEL_ONESHOT,
    PolicyConstants.LEVEL_ONESHOT,
    //testIsSatisfied007
    PolicyConstants.LEVEL_SESSION,
    PolicyConstants.LEVEL_SESSION,
    //testIsSatisfied008
    PolicyConstants.ALL_LEVELS,
    //testIsSatisfied009
    PolicyConstants.LEVEL_BLANKET,
    PolicyConstants.LEVEL_SESSION,
    PolicyConstants.LEVEL_ONESHOT,
    //testIsSatisfied010
    PolicyConstants.LEVEL_BLANKET,
    PolicyConstants.LEVEL_SESSION,
    //testIsSatisfied011
    PolicyConstants.LEVEL_ONESHOT,
    //testIsSatisfied012
    PolicyConstants.LEVEL_ONESHOT,
    PolicyConstants.LEVEL_BLANKET,
    //testIsSatisfied013
    PolicyConstants.LEVEL_BLANKET,
    PolicyConstants.LEVEL_BLANKET,
    //testIsSatisfied014
    PolicyConstants.LEVEL_BLANKET,
    PolicyConstants.LEVEL_BLANKET,
    //testIsSatisfied015
    PolicyConstants.LEVEL_ONESHOT,
    PolicyConstants.LEVEL_ONESHOT,
    //testIsSatisfied016
    PolicyConstants.LEVEL_SESSION,
    PolicyConstants.LEVEL_SESSION,
    //testIsSatisfied017
    PolicyConstants.LEVEL_ONESHOT,
    //testIsSatisfied018
    PolicyConstants.LEVEL_ONESHOT
  };

  //IsSatisfied
  /**
   * Holds the condition's default properties used to create test condition in IsSatisfied test class.
   */
  public static final String[] IS_CONDITIONS_DEFAULT = new String[] {
    //testIsSatisfied001
    PolicyConstants.LEVEL_BLANKET,
    PolicyConstants.LEVEL_BLANKET,
    //testIsSatisfied002
    PolicyConstants.LEVEL_BLANKET,
    PolicyConstants.LEVEL_BLANKET,
    //testIsSatisfied003
    PolicyConstants.LEVEL_ONESHOT,
    PolicyConstants.LEVEL_ONESHOT,
    //testIsSatisfied004
    PolicyConstants.LEVEL_SESSION,
    PolicyConstants.LEVEL_SESSION,
    //testIsSatisfied005
    PolicyConstants.LEVEL_SESSION,
    PolicyConstants.LEVEL_SESSION,
    //testIsSatisfied006
    PolicyConstants.LEVEL_ONESHOT,
    PolicyConstants.LEVEL_ONESHOT,
    //testIsSatisfied007
    PolicyConstants.LEVEL_SESSION,
    PolicyConstants.LEVEL_SESSION,
    //testIsSatisfied008
    "",
    //testIsSatisfied009
    PolicyConstants.LEVEL_BLANKET,
    PolicyConstants.LEVEL_SESSION,
    PolicyConstants.LEVEL_ONESHOT,
    //testIsSatisfied010
    PolicyConstants.LEVEL_BLANKET,
    PolicyConstants.LEVEL_SESSION,
    //testIsSatisfied011
    PolicyConstants.LEVEL_ONESHOT,
    //testIsSatisfied012
    PolicyConstants.LEVEL_ONESHOT,
    PolicyConstants.LEVEL_BLANKET,
    //testIsSatisfied013
    PolicyConstants.LEVEL_BLANKET,
    PolicyConstants.LEVEL_BLANKET,
    //testIsSatisfied014
    PolicyConstants.LEVEL_BLANKET,
    PolicyConstants.LEVEL_BLANKET,
    //testIsSatisfied015
    PolicyConstants.LEVEL_ONESHOT,
    PolicyConstants.LEVEL_ONESHOT,
    //testIsSatisfied016
    PolicyConstants.LEVEL_SESSION,
    PolicyConstants.LEVEL_SESSION,
    //testIsSatisfied017    
    PolicyConstants.LEVEL_ONESHOT,
    //testIsSatisfied018
    PolicyConstants.LEVEL_ONESHOT
  };
  
  //IsSatisfied
  /**
   * Holds the condition's catalog properties used to create test condition in IsSatisfied test class.
   */
  public static final String[] IS_CONDITIONS_CATALOG = new String[] {
    //testIsSatisfied001
    PolicyConstants.CATALOG_NAME,
    PolicyConstants.CATALOG_NAME,
    //testIsSatisfied002
    PolicyConstants.CATALOG_NAME,
    PolicyConstants.CATALOG_NAME,
    //testIsSatisfied003
    PolicyConstants.CATALOG_NAME,
    PolicyConstants.CATALOG_NAME,
    //testIsSatisfied004
    PolicyConstants.CATALOG_NAME,
    PolicyConstants.CATALOG_NAME,
    //testIsSatisfied005
    PolicyConstants.CATALOG_NAME,
    PolicyConstants.CATALOG_NAME,
    //testIsSatisfied006
    PolicyConstants.CATALOG_NAME,
    PolicyConstants.CATALOG_NAME,
    //testIsSatisfied007
    PolicyConstants.CATALOG_NAME,
    PolicyConstants.CATALOG_NAME,
    //testIsSatisfied008
    PolicyConstants.CATALOG_NAME,
    //testIsSatisfied009
    PolicyConstants.CATALOG_NAME,
    PolicyConstants.CATALOG_NAME,
    PolicyConstants.CATALOG_NAME,
    //testIsSatisfied010
    PolicyConstants.CATALOG_NAME,
    PolicyConstants.CATALOG_NAME,
    //testIsSatisfied011
    PolicyConstants.CATALOG_NAME,
    //testIsSatisfied012
    PolicyConstants.CATALOG_NAME,
    PolicyConstants.CATALOG_NAME,
    //testIsSatisfied013
    PolicyConstants.CATALOG_NAME,
    PolicyConstants.CATALOG_NAME,
    //testIsSatisfied014
    PolicyConstants.CATALOG_NAME,
    PolicyConstants.CATALOG_NAME,
    //testIsSatisfied015
    PolicyConstants.CATALOG_NAME,
    PolicyConstants.CATALOG_NAME,
    //testIsSatisfied016
    PolicyConstants.CATALOG_NAME,
    PolicyConstants.CATALOG_NAME,
    //testIsSatisfied017    
    PolicyConstants.CATALOG_NAME,
    //testIsSatisfied018
    PolicyConstants.CATALOG_NAME
  };  

  //IsSatisfied
  /**
   * Holds the condition's messages used to create test condition in IsSatisfied test class.
   */
  public static final String[] IS_CONDITIONS_MESSAGE = new String[] {
    //testIsSatisfied001
    "1- You must REJECT this question to make this test pass with success. " +
    "[PS: If this is the second time that you receive this question there is an error in the implementation because the same question must remember your previous answer so, you have to do the opposite that it says, you have to ACCEPT this question.]",
    "1- You must REJECT this question to make this test pass with success. " +
    "[PS: If this is the second time that you receive this question there is an error in the implementation because the same question must remember your previous answer so, you have to do the opposite that it says, you have to ACCEPT this question.]",    
    //testIsSatisfied002
    "2- You must ACCEPT this question to make this test pass with success." +
    "[PS: If this is the second time that you receive this question there is an error in the implementation because the same question must remember your previous answer so, you have to do the opposite that it says, you have to REJECT this question.]",
    "2- You must ACCEPT this question to make this test pass with success." +
    "[PS: If this is the second time that you receive this question there is an error in the implementation because the same question must remember your previous answer so, you have to do the opposite that it says, you have to REJECT this question.]",    
    //testIsSatisfied003
    "3- You must ACCEPT this question to make this test pass with success." +
    "[PS: If this is the second time that you receive this question the implementation is ok in this case (ONESHOT) you have to REJECT this question to validate the second question.]",
    "3- You must ACCEPT this question to make this test pass with success." +
    "[PS: If this is the second time that you receive this question the implementation is ok in this case (ONESHOT) you have to REJECT this question to validate the second question.]",    
    //testIsSatisfied004
    "4- You must REJECT this question to make this test pass with success." +
    "[PS: If this is the second time that you receive this question there is an error in the implementation because the same question must remember your previous answer so, you have to do the opposite that it says, you have to ACCEPT this question.]",
    "4- You must REJECT this question to make this test pass with success." +
    "[PS: If this is the second time that you receive this question there is an error in the implementation because the same question must remember your previous answer so, you have to do the opposite that it says, you have to ACCEPT this question.]",    
    //testIsSatisfied005
    "5- You must ACCEPT this question to make this test pass with success." +
    "[PS: If this is the second time that you receive this question there is an error in the implementation because the same question must remember your previous answer so, you have to do the opposite that it says, you have to REJECT this question.]",
    "5- You must ACCEPT this question to make this test pass with success." +
    "[PS: If this is the second time that you receive this question there is an error in the implementation because the same question must remember your previous answer so, you have to do the opposite that it says, you have to REJECT this question.]",    
    //testIsSatisfied006
    "6- You must choose NEVER to this question to make this test pass with success. " +
      "[PS: If this is the second time that you receive this question there is an error in the implementation and you have to choose yes as answer because when the user choose NEVER as answer, the RI never ask the user again.]",
    "6- You must choose NEVER to this question to make this test pass with success. " +
      "[PS: If this is the second time that you receive this question there is an error in the implementation and you have to choose yes as answer because when the user choose NEVER as answer, the RI never ask the user again.]",    
    //testIsSatisfied007
    "7- You must choose NEVER to this question to make this test pass with success. " +
      "[PS: If this is the second time that you receive this question there is an error in the implementation and you have to choose yes as answer because when the user choose NEVER as answer, the RI never ask the user again until the system is restarted.]",
      "7- You must choose NEVER to this question to make this test pass with success. " +
        "[PS: If this is the second time that you receive this question there is an error in the implementation and you have to choose yes as answer because when the user choose NEVER as answer, the RI never ask the user again until the system is restarted.]",
    //testIsSatisfied008      
    "%test",
    //testIsSatisfied009
    "9- You must REJECT this question to make this test pass with success. " +
    "[PS: If this is the second time that you receive this question there is an error in the implementation because the same question must remember your previous answer so, you have to do the opposite that it says, you have to ACCEPT this question.]",
    "10- You must REJECT this question to make this test pass with success." +
    "[PS: If this is the second time that you receive this question there is an error in the implementation because the same question must remember your previous answer so, you have to do the opposite that it says, you have to ACCEPT this question.]",
    "11- You must REJECT this question to make this test pass with success." +
    "[PS: If this is the second time that you receive this question there is an error in the implementation because the same question must remember your previous answer even if the level is ONESHOT so, you have to do the opposite that it says, you have to ACCEPT this question.]",
    //testIsSatisfied010
    "12- You must ACCEPT this question to make this test pass with success. " +
    "[PS: If this is the second time that you receive this question there is an error in the implementation because the same question must remeber your previous answer so, you have to do the opposite that it says, you have to REJECT this question.]",
    "13- You must ACCEPT this question to make this test pass with success." +
    "[PS: If this is the second time that you receive this question there is an error in the implementation because the same question must remeber your previous answer so, you have to do the opposite that it says, you have to REJECT this question.]",
    //testIsSatisfied011
    "14- You must ACCEPT this question to make this test pass with success.",
    //testIsSatisfied012
    "15- You must ACCEPT this question to make this test pass with success." +
    "[PS: If this is the second time that you receive this question there is an error in the implementation because the same question must remember your previous answer even if the level is ONESHOT so, you have to do the opposite that it says, you have to REJECT this question.]",
    "16- You must ACCEPT this question to make this test pass with success. " +
    "[PS: If this is the second time that you receive this question there is an error in the implementation because the same question must remember your previous answer so, you have to do the opposite that it says, you have to REJECT this question.]",
    //testIsSatisfied013
    "17- You must choose NEVER to this question to make this test pass with success. " +
    "[PS: If this is the second time that you receive this question there is an error in the implementation and you have to choose yes as answer because when the user choose NEVER as answer, the RI never ask the user again even if the system is restarted.]",
    "17- You must choose NEVER to this question to make this test pass with success. " +
    "[PS: If this is the second time that you receive this question there is an error in the implementation and you have to choose yes as answer because when the user choose NEVER as answer, the RI never ask the user again even if the system is restarted.]",
    //testIsSatisfied014
    "18- You must choose ALWAYS to this question to make this test pass with success. " +
    "[PS: If this is the second time that you receive this question there is an error in the implementation and you have to choose never as answer because when the user choose ALWAYS as answer, the RI never ask the user again even if the system is restarted.]",
    "18- You must choose ALWAYS to this question to make this test pass with success. " +
    "[PS: If this is the second time that you receive this question there is an error in the implementation and you have to choose never as answer because when the user choose ALWAYS as answer, the RI never ask the user again even if the system is restarted.]",
    //testIsSatisfied015
    "19- You must choose NEVER to this question to make this test pass with success." +
    "[PS: If this is the second time that you receive this question there is an error in implementation and you have to ACCEPT this question to make the test fail because when you choose NEVER as answer, the RI must never ask the user again and always return false.]",
    "19- You must choose NEVER to this question to make this test pass with success." +
    "[PS: If this is the second time that you receive this question there is an error in implementation and you have to ACCEPT this question to make the test fail because when you choose NEVER as answer, the RI must never ask the user again and always return false.]",    
    //testIsSatisfied016
    "20- You must choose NEVER to this question to make this test pass with success." +
    "[PS: If this is the second time that you receive this question there is an error in implementation and you have to ACCEPT this question to make the test fail because when you choose NEVER as answer, the RI must never ask the user again and always return false until the system is restarted.]",
    "20- You must choose NEVER to this question to make this test pass with success." +
    "[PS: If this is the second time that you receive this question there is an error in implementation and you have to ACCEPT this question to make the test fail because when you choose NEVER as answer, the RI must never ask the user again and always return false until the system is restarted.]",    
    //testIsSatisfied017
    "21- Choose yes as answer. PS: If this is the second time that you receive this question, there is an error in the implementation and you must reject this test choosing no as answer.",
    //testIsSatisfied018
    "22- You must ACCEPT this question to make this test pass with success." +
    "[PS: If this is the second time that you receive this question the implementation is ok and you have to REJECT this question.]"
  };
  
  //IsSatisfied
  /**
   * Holds the condition's correct answers used to automate testing for IsSatisfied test class.
   */
  public static final int IS_ANSWERS[] = new int[] {
    //testIsSatisfied001
    BLANKET_NEVER,
    BLANKET_ALWAYS,
    //testIsSatisfied002
    BLANKET_ALWAYS,
    BLANKET_NEVER,
    //testIsSatisfied003
    ONESHOT_YES,
    ONESHOT_NO,
    //testIsSatisfied004
    SESSION_NO,
    SESSION_YES,
    //testIsSatisfied005
    SESSION_YES,
    SESSION_NO,
    //testIsSatisfied006
    ONESHOT_NEVER,
    ONESHOT_YES,
    //testIsSatisfied007
    SESSION_NEVER,
    SESSION_YES,
    //testIsSatisfied008
    ONESHOT_YES,//caution with this: this checks loading of messages from catalog and the user accept if only the messages is seen 
    //testIsSatisfied009
    BLANKET_NEVER,
    SESSION_NEVER,
    ONESHOT_NO,
    //answers for second isSatisfy call if the implementation is ok they should be never used
    BLANKET_ALWAYS,
    SESSION_YES,
    ONESHOT_YES,
    //testIsSatisfied010
    BLANKET_ALWAYS,
    SESSION_YES,
    //the second two must not show another message dialogs they are already evaluetated in previous isSatisfied call
    //testIsSatisfied011
    ONESHOT_YES,//this one should never be used because if the implementation is correct an earlier exception should be thrown
    //testIsSatisfied012
    //these two also should be never used because if the implementation is correct an earlier exception should be thrown
    ONESHOT_YES,
    BLANKET_ALWAYS,
    //testIsSatisfied013
    BLANKET_NEVER,
    BLANKET_ALWAYS,
    //testIsSatisfied014
    BLANKET_ALWAYS,
    BLANKET_NEVER,
    //testIsSatisfied015
    ONESHOT_NEVER,
    ONESHOT_YES,
    //testIsSatisfied016
    SESSION_NEVER,
    SESSION_YES,
    //testIsSatisfied017
    ONESHOT_YES,
    ONESHOT_NO,
    //testIsSatisfied018
    ONESHOT_YES,
    ONESHOT_NO
  };
  
  //IsMutable
  /**
   * Holds the condition's level properties used to create test condition in IsMutable test class.
   */  
  public static final String[] IM_CONDITIONS_LEVELS = new String[] {
    //testIsMutable001
    PolicyConstants.LEVEL_BLANKET,
    //testIsMutable002
    PolicyConstants.LEVEL_ONESHOT,
    PolicyConstants.LEVEL_ONESHOT,
    //testIsMutable003
    PolicyConstants.LEVEL_ONESHOT,
    PolicyConstants.LEVEL_ONESHOT,
    //testIsMutable004
    PolicyConstants.LEVEL_ONESHOT,
    PolicyConstants.LEVEL_ONESHOT,
    //testIsMutable005
    PolicyConstants.LEVEL_ONESHOT
  };

  //IsMutable
  /**
   * Holds the condition's default properties used to create test condition in IsMutable test class.
   */  
  public static final String[] IM_CONDITIONS_DEFAULT = new String[] {
    //testIsMutable001
    PolicyConstants.LEVEL_BLANKET,
    //testIsMutable002
    PolicyConstants.LEVEL_ONESHOT,
    PolicyConstants.LEVEL_ONESHOT,
    //testIsMutable003
    PolicyConstants.LEVEL_ONESHOT,
    PolicyConstants.LEVEL_ONESHOT,
    //testIsMutable004
    PolicyConstants.LEVEL_ONESHOT,
    PolicyConstants.LEVEL_ONESHOT,
    //testIsMutable005
    PolicyConstants.LEVEL_ONESHOT
  };
  
  //IsMutable
  /**
   * Holds the condition's catalog properties used to create test condition in IsMutable test class.
   */  
  public static final String[] IM_CONDITIONS_CATALOG = new String[] {
    //testIsMutable001
    PolicyConstants.CATALOG_NAME,
    //testIsMutable002
    PolicyConstants.CATALOG_NAME,
    PolicyConstants.CATALOG_NAME,
    //testIsMutable003
    PolicyConstants.CATALOG_NAME,
    PolicyConstants.CATALOG_NAME,
    //testIsMutable004
    PolicyConstants.CATALOG_NAME,
    PolicyConstants.CATALOG_NAME,
    //testIsMutable005
    PolicyConstants.CATALOG_NAME,
  };  

  //IsMutable
  /**
   * Holds the condition's messages used to create test condition in IsMutable test class.
   */  
  public static final String[] IM_CONDITIONS_MESSAGE = new String[] {
    //testIsMutable001
    "1-Choose whatever you want.",
    //testIsMutable002
    "2-Choose yes as answer. Otherwise this test method will fail.",
    "2-Choose no as answer. Otherwise this test method will fail.",
    //testIsMutable003
    "3-Choose yes as answer. Otherwise this test method will fail.",
    "3-Choose no as answer. Otherwise this test method will fail.",
    //testIsMutable004
    "4-Choose yes as answer. Otherwise this test method will fail.",
    "4-Choose no as answer. Otherwise this test method will fail.",
    //testIsMutable005
    "13-Choose NEVER as answer. Otherwise this test method will fail."
  };
  
  //IsMutable
  /**
   * Holds the condition's correct answers used to automate testing for IsMutable test class.
   */  
  public static final int IM_ANSWERS[] = new int[] {
    //testIsMutable001
    BLANKET_ALWAYS,
    //testIsMutable002
    ONESHOT_YES,
    ONESHOT_NO,
    //testIsMutable003
    ONESHOT_YES,
    ONESHOT_NO,
    //testIsMutable004
    ONESHOT_YES,
    ONESHOT_NO,    
    //testIsMutable005
    ONESHOT_NEVER
  };
  
  //IsPosteponed
  /**
   * Holds the condition's level properties used to create test condition in IsPosteponed test class.
   */  
  public static final String[] IP_CONDITIONS_LEVELS = new String[] {
    //testIsPostponed001
    PolicyConstants.LEVEL_BLANKET,
    PolicyConstants.LEVEL_BLANKET,
    //testIsPostponed002
    PolicyConstants.LEVEL_SESSION,
    PolicyConstants.LEVEL_SESSION,
    //testIsPostponed003
    PolicyConstants.LEVEL_ONESHOT,
    PolicyConstants.LEVEL_ONESHOT,
    //testIsPostponed004
    PolicyConstants.LEVEL_SESSION,
    //testIsPostponed005
    PolicyConstants.LEVEL_BLANKET,
    PolicyConstants.LEVEL_BLANKET,
    //testIsPostponed006
    PolicyConstants.LEVEL_SESSION,
    PolicyConstants.LEVEL_SESSION,
    //testIsPostponed007
    PolicyConstants.LEVEL_ONESHOT,
    PolicyConstants.LEVEL_ONESHOT,
    //testIsPostponed008
    PolicyConstants.LEVEL_SESSION,
    //testIsPostponed009
    PolicyConstants.LEVEL_BLANKET,
    PolicyConstants.LEVEL_BLANKET,
    //testIsPostponed010
    PolicyConstants.LEVEL_SESSION,
    PolicyConstants.LEVEL_SESSION,
    //testIsPostponed011
    PolicyConstants.LEVEL_ONESHOT,
    PolicyConstants.LEVEL_ONESHOT,
    //testIsPostponed012
    PolicyConstants.LEVEL_SESSION,
    //testIsPostponed013
    PolicyConstants.LEVEL_ONESHOT,
    //testIsPostponed014
    PolicyConstants.LEVEL_ONESHOT,
    //testIsPostponed015    
    PolicyConstants.LEVEL_ONESHOT
  };

  //IsPosteponed
  /**
   * Holds the condition's default properties used to create test condition in IsPosteponed test class.
   */    
  public static final String[] IP_CONDITIONS_DEFAULT = new String[] {
    //testIsPostponed001
    PolicyConstants.LEVEL_BLANKET,
    PolicyConstants.LEVEL_BLANKET,
    //testIsPostponed002
    PolicyConstants.LEVEL_SESSION,
    PolicyConstants.LEVEL_SESSION,
    //testIsPostponed003
    PolicyConstants.LEVEL_ONESHOT,
    PolicyConstants.LEVEL_ONESHOT,
    //testIsPostponed004
    PolicyConstants.LEVEL_SESSION,
    //testIsPostponed005
    PolicyConstants.LEVEL_BLANKET,
    PolicyConstants.LEVEL_BLANKET,
    //testIsPostponed006
    PolicyConstants.LEVEL_SESSION,
    PolicyConstants.LEVEL_SESSION,
    //testIsPostponed007
    PolicyConstants.LEVEL_ONESHOT,
    PolicyConstants.LEVEL_ONESHOT,
    //testIsPostponed008
    PolicyConstants.LEVEL_SESSION,
    //testIsPostponed009
    PolicyConstants.LEVEL_BLANKET,
    PolicyConstants.LEVEL_BLANKET,
    //testIsPostponed010
    PolicyConstants.LEVEL_SESSION,
    PolicyConstants.LEVEL_SESSION,
    //testIsPostponed011
    PolicyConstants.LEVEL_ONESHOT,
    PolicyConstants.LEVEL_ONESHOT,
    //testIsPostponed012
    PolicyConstants.LEVEL_SESSION,
    //testIsPostponed013
    PolicyConstants.LEVEL_ONESHOT,
    //testIsPostponed014
    PolicyConstants.LEVEL_ONESHOT,
    //testIsPostponed015    
    PolicyConstants.LEVEL_ONESHOT
  };
  
  //IsPosteponed
  /**
   * Holds the condition's catalog properties used to create test condition in IsPosteponed test class.
   */  
  public static final String[] IP_CONDITIONS_CATALOG = new String[] {
    //testIsPostponed001
    PolicyConstants.CATALOG_NAME,
    PolicyConstants.CATALOG_NAME,
    //testIsPostponed002
    PolicyConstants.CATALOG_NAME,
    PolicyConstants.CATALOG_NAME,
    //testIsPostponed003
    PolicyConstants.CATALOG_NAME,
    PolicyConstants.CATALOG_NAME,
    //testIsPostponed004
    PolicyConstants.CATALOG_NAME,
    //testIsPostponed005
    PolicyConstants.CATALOG_NAME,
    PolicyConstants.CATALOG_NAME,
    //testIsPostponed006
    PolicyConstants.CATALOG_NAME,
    PolicyConstants.CATALOG_NAME,
    //testIsPostponed007
    PolicyConstants.CATALOG_NAME,
    PolicyConstants.CATALOG_NAME,
    //testIsPostponed008
    PolicyConstants.CATALOG_NAME,
    //testIsPostponed009
    PolicyConstants.CATALOG_NAME,
    PolicyConstants.CATALOG_NAME,
    //testIsPostponed010
    PolicyConstants.CATALOG_NAME,
    PolicyConstants.CATALOG_NAME,
    //testIsPostponed011
    PolicyConstants.CATALOG_NAME,
    PolicyConstants.CATALOG_NAME,
    //testIsPostponed012
    PolicyConstants.CATALOG_NAME,
    //testIsPostponed013
    PolicyConstants.CATALOG_NAME,
    //testIsPostponed014
    PolicyConstants.CATALOG_NAME,
    //testIsPostponed015    
    PolicyConstants.CATALOG_NAME
  };  

  //IsPosteponed
  /**
   * Holds the condition's messages used to create test condition in IsPosteponed test class.
   */  
  public static final String[] IP_CONDITIONS_MESSAGE = new String[] {
    //testIsPostponed001
    "1: Choose always as answer. Otherwise this test method will fail.",
    "1: Choose never as answer. Otherwise this test method will fail.",
    //testIsPostponed002
    "2: Choose a positive answer. Otherwise this test method will fail.",
    "2: Choose a negative answer. Otherwise this test method will fail.",
    //testIsPostponed003
    "3: Choose yes as answer. Otherwise this test method will fail.",
    "3: Choose no as answer. Otherwise this test method will fail.",
    //testIsPostponed004
    "4: Choose NEVER as answer. Otherwise this test method will fail.",
    //testIsPostponed005
    "5: Choose always as answer. Otherwise this test method will fail.",
    "5: Choose never as answer. Otherwise this test method will fail.",
    //testIsPostponed006
    "6: Choose a positive answer. Otherwise this test method will fail.",
    "6: Choose a negative answer. Otherwise this test method will fail.",
    //testIsPostponed007
    "7: Choose yes as answer. Otherwise this test method will fail.",
    "7: Choose no as answer. Otherwise this test method will fail.",
    //testIsPostponed008
    "8: Choose NEVER as answer. Otherwise this test method will fail.",
    //testIsPostponed009
    "9: Choose always as answer. Otherwise this test method will fail.",
    "9: Choose never as answer. Otherwise this test method will fail.",
    //testIsPostponed010
    "10: Choose a positive answer. Otherwise this test method will fail.",
    "10: Choose a negative answer. Otherwise this test method will fail.",
    //testIsPostponed011
    "11: Choose yes as answer. Otherwise this test method will fail.",
    "11: Choose no as answer. Otherwise this test method will fail.",
    //testIsPostponed012
    "12: Choose NEVER as answer. Otherwise this test method will fail.",
    //testIsPostponed013
    "13: Choose NEVER as answer. Otherwise this test method will fail.",
    //testIsPostponed014
    "14: Choose NEVER as answer. Otherwise this test method will fail.",
    //testIsPostponed015    
    "15: Choose NEVER as answer. Otherwise this test method will fail."
  };
  
  //IsPosteponed
  /**
   * Holds the condition's correct answers used to automate testing for IsPosteponed test class.
   */  
  public static final int IP_ANSWERS[] = new int[] {
    //testIsPostponed001
    BLANKET_ALWAYS,
    BLANKET_NEVER,
    //testIsPostponed002
    SESSION_YES,
    SESSION_NO,
    //testIsPostponed003
    ONESHOT_YES,
    ONESHOT_NO,
    //testIsPostponed004
    SESSION_NEVER,
    //testIsPostponed005
    BLANKET_ALWAYS,
    BLANKET_NEVER,
    //testIsPostponed006
    SESSION_YES,
    SESSION_NO,
    //testIsPostponed007
    ONESHOT_YES,
    ONESHOT_NO,
    //testIsPostponed008
    SESSION_NEVER,
    //testIsPostponed009
    BLANKET_ALWAYS,
    BLANKET_NEVER,
    //testIsPostponed010
    SESSION_YES,
    SESSION_NO,
    //testIsPostponed011
    ONESHOT_YES,
    ONESHOT_NO,
    //testIsPostponed012
    SESSION_NEVER,
    //testIsPostponed013
    ONESHOT_NEVER,
    //testIsPostponed014
    ONESHOT_NEVER,
    //testIsPostponed015
    ONESHOT_NEVER
  };
}

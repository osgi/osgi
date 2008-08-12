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
 * 16/08/2005   Luiz Felipe Guimaraes
 * 33           Implement MEG TCK
 * ===========  ==============================================================
 */
package org.osgi.test.cases.policy.tbc.UserPromptCondition;

import org.osgi.service.condpermadmin.Condition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.test.cases.policy.tbc.PolicyConstants;
import org.osgi.test.cases.policy.tbc.PolicyTestControl;
import org.osgi.test.cases.policy.tbc.util.MessagesConstants;
import org.osgi.util.mobile.UserPromptCondition;

/**
 * This class tests <code>getCondition</code> method according with MEG specification (rfc0092)
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
		testGetCondition007();
		testGetCondition008();
		testGetCondition009();
		testGetCondition010();
		testGetCondition011();
		testGetCondition012();
		testGetCondition013();
		testGetCondition014();
		testGetCondition015();
		testGetCondition016();
		testGetCondition017();
		testGetCondition018();
		testGetCondition019();
		testGetCondition020();
		testGetCondition021();
		testGetCondition022();
		testGetCondition023();
	}

	/**
	 * Tests if UserPrompt object is created with oneshot permission.
	 * 
	 * @spec UserPromptCondition.getCondition(Bundle,ConditionInfo)
	 */
	private void testGetCondition001() {
		tbc.log("#testGetCondition001");
		
		try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo("", new String[] {
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.CATALOG_NAME, "Test for question" })
							);
			tbc.assertNotNull(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_NOT_NULL,
					new String[] { "Created UserPrompt" }), condition);

			// ### The following test is not valid. The condition does not
			// have to be an instance of the UserPromptCondition
			//tbc.assertTrue("Asserts that the returned Condition is an instanceof UserPromptCondition.",(condition instanceof UserPromptCondition));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] {
							e.getClass().getName() }));
		}
		
	}

	/**
	 * Tests if UserPrompt object is created with SESSION permission
	 * 
	 * @spec UserPromptCondition.getCondition(Bundle,ConditionInfo)
	 */
	private void testGetCondition002() {
        tbc.log("#testGetCondition002");
		try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo("", new String[] {
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.CATALOG_NAME, "" })
							);
			
			tbc.assertNotNull(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_NOT_NULL,
					new String[] { "Created UserPrompt" }), condition);
			
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] {
							e.getClass().getName() }));
		}
		
	}
	
	/**
	 * Tests if UserPrompt object is created with BLANKET permission
	 * 
	 * @spec UserPromptCondition.getCondition(Bundle,ConditionInfo)
	 */
	private void testGetCondition003() {
        tbc.log("#testGetCondition003");
		try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo("", new String[] {
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.CATALOG_NAME, "" })
							);
			
			tbc.assertNotNull(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_NOT_NULL,
					new String[] { "Created UserPrompt" }), condition);
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] {
							e.getClass().getName() }));
		}
		
	}

	/**
	 * Tests if UserPrompt object is created with ONESHOT and SESSION permission
	 * 
	 * @spec UserPromptCondition.getCondition(Bundle,ConditionInfo)
	 */
	private void testGetCondition004() {
        tbc.log("#testGetCondition004");
		try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo("", new String[] {
							PolicyConstants.LEVEL_ONESHOT+","+PolicyConstants.LEVEL_SESSION,
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.CATALOG_NAME, "" })
							);
			
			tbc.assertNotNull(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_NOT_NULL,
					new String[] { "Created UserPrompt" }), condition);
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] {
							e.getClass().getName() }));
		}
		
	}
	
	/**
	 * Tests if UserPrompt object is created with ONESHOT and BLANKET permission
	 * 
	 * @spec UserPromptCondition.getCondition(Bundle,ConditionInfo)
	 */
	private void testGetCondition005() {
        tbc.log("#testGetCondition005");
		try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo("", new String[] {
							PolicyConstants.LEVEL_ONESHOT+","+PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.CATALOG_NAME, "" })
							);
			
			tbc.assertNotNull(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_NOT_NULL,
					new String[] { "Created UserPrompt" }), condition);
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] {
							e.getClass().getName() }));
		}
		
	}

	/**
	 * Tests if UserPrompt object is created with BLANKET and SESSION permission
	 * 
	 * @spec UserPromptCondition.getCondition(Bundle,ConditionInfo)
	 */
	private void testGetCondition006() {
        tbc.log("#testGetCondition006");
		try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo("", new String[] {
							PolicyConstants.LEVEL_BLANKET+","+PolicyConstants.LEVEL_SESSION,
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.CATALOG_NAME, "" })
							);
			
			tbc.assertNotNull(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_NOT_NULL,
					new String[] { "Created UserPrompt" }), condition);
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] {
							e.getClass().getName() }));
		}
		
	}
	
	/**
	 * Tests if UserPrompt object is created with ONESHOT, SESSION and BLANKET permission
	 * 
	 * @spec UserPromptCondition.getCondition(Bundle,ConditionInfo)
	 */
	private void testGetCondition007() {
        tbc.log("#testGetCondition007");
		try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(),
				new ConditionInfo("", new String[] {
						PolicyConstants.ALL_LEVELS,
						PolicyConstants.LEVEL_SESSION,
						PolicyConstants.CATALOG_NAME, "" })
						);
					
			tbc.assertNotNull(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_NOT_NULL,
					new String[] { "Created UserPrompt" }), condition);
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] {
							e.getClass().getName() }));
		}
		
	}
	


	/**
	 * Tests if NullPointerException is called if conditionInfo parameter is null.
	 * 
	 * @spec UserPromptCondition.getCondition(Bundle,ConditionInfo)
 	 */
	private void testGetCondition008() {
        tbc.log("#testGetCondition008");
		try {
			UserPromptCondition.getCondition(tbc.getBundle(),null);
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
     * Tests if NullPointerException is called if bundle parameter is null.
     * 
     * @spec UserPromptCondition.getCondition(Bundle,ConditionInfo)
     */
	private void testGetCondition009() {
        tbc.log("#testGetCondition009");
		try {
			UserPromptCondition.getCondition(null,
					new ConditionInfo("", new String[] {
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.CATALOG_NAME, "" })
							);
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
     * Tests if IllegalArgumentException is thrown if the ConditionInfo is constructed with an array of
     * String with more than 4 Strings.
     * 
     * @spec UserPromptCondition.getCondition(Bundle,ConditionInfo)
     */
	private void testGetCondition010() {
        tbc.log("#testGetCondition010");
		try {
			UserPromptCondition.getCondition(tbc.getBundle(),
					new ConditionInfo("", new String[] {
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.INVALID_CODE,							
							PolicyConstants.CATALOG_NAME, "","" })
							);			
			tbc.failException("#",IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] {
							e.getClass().getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(), e.getClass().getName() }));
		}
		
	}
	
	/**
     * Tests if IllegalArgumentException is thrown if the ConditionInfo is constructed with an array of
     * String with less than 4 Strings.
     * 
     * @spec UserPromptCondition.getCondition(Bundle,ConditionInfo)
     */
	private void testGetCondition011() {
        tbc.log("#testGetCondition011");
		try {
			UserPromptCondition.getCondition(tbc.getBundle(),
					new ConditionInfo("", new String[] {
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.INVALID_CODE,							
							PolicyConstants.CATALOG_NAME })
							);			
			tbc.failException("#",IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] {
							e.getClass().getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(), e.getClass().getName() }));
		}
		
	}
	
	/**
     * Tests if IllegalArgumentException is thrown when an invalid level
     * is passed as parameter.
     * 
     * @spec UserPromptCondition.getCondition(Bundle,ConditionInfo)
     */
	private void testGetCondition012() {
        tbc.log("#testGetCondition012");
		try {
			UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo("", new String[] {
							PolicyConstants.TEST_NODE,
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.CATALOG_NAME, "" })
							);		
			tbc.failException("#",IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] {
							e.getClass().getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(), e.getClass().getName() }));
		}
		
	}	
	
	/**
     * Tests if IllegalArgumentException is thrown when an invalid level
     * is passed as default permission level.
     * 
     * @spec UserPromptCondition.getCondition(Bundle,ConditionInfo)
     */
	private void testGetCondition013() {
        tbc.log("#testGetCondition013");
		try {
			UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo("", new String[] {
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.TEST_NODE,
							PolicyConstants.CATALOG_NAME, "" })
							);		
			tbc.failException("#",IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] {
							e.getClass().getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(), e.getClass().getName() }));
		}
		
	}	
	
	/**
	 * Tests if UserPrompt object is created when we pass
	 * an empty string as default permission level.
	 * 
	 * @spec UserPromptCondition.getCondition(Bundle,ConditionInfo)
	 */
	private void testGetCondition014() {
		tbc.log("#testGetCondition014");
		
		try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo("", new String[] {
							PolicyConstants.LEVEL_ONESHOT,
							"",
							PolicyConstants.CATALOG_NAME, "" })
							);
			tbc.assertNotNull(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_NOT_NULL,
					new String[] { "Created UserPrompt" }), condition);

			//### See 9.7.1, a Condition object must implement the
			// Condition interface, it does not have to be an instance of
			// the given implementation class.
			//tbc.assertTrue("Asserts that the returned Condition is an instanceof UserPromptCondition.",(condition instanceof UserPromptCondition));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] {
							e.getClass().getName() }));
		}
		
	}
	
	/**
     * Tests if IllegalArgumentException is thrown when
     * two defaults levels are passed in ConditionInfo constructor.
     * 
     * @spec UserPromptCondition.getCondition(Bundle,ConditionInfo)
     */
	private void testGetCondition015() {
        tbc.log("#testGetCondition015");
		try {
			UserPromptCondition.getCondition(tbc.getBundle(),
					new ConditionInfo("", new String[] {
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.LEVEL_SESSION+","+PolicyConstants.LEVEL_ONESHOT,							
							PolicyConstants.CATALOG_NAME })
							);			
			tbc.failException("#",IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] {
							e.getClass().getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(), e.getClass().getName() }));
		}
		
	}	

	/**
     * Tests if IllegalArgumentException is thrown when
     * an empty string is passed as level and default levels parameter.
     * 
     * @spec UserPromptCondition.getCondition(Bundle,ConditionInfo)
     */
	private void testGetCondition016() {
        tbc.log("#testGetCondition016");
		try {
			UserPromptCondition.getCondition(tbc.getBundle(),
					new ConditionInfo("", new String[] {
							"",
							"",							
							PolicyConstants.CATALOG_NAME })
							);			
			tbc.failException("#",IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] {
							e.getClass().getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(), e.getClass().getName() }));
		}
		
	}	
	
	/**
     * Tests if IllegalArgumentException is thrown when
     * only the default level is passed, an empty string is passed as level.
     * 
     * @spec UserPromptCondition.getCondition(Bundle,ConditionInfo)
     */
	private void testGetCondition017() {
        tbc.log("#testGetCondition017");
		try {
			UserPromptCondition.getCondition(tbc.getBundle(),
					new ConditionInfo("", new String[] {
							"",
							PolicyConstants.LEVEL_SESSION,							
							PolicyConstants.CATALOG_NAME })
							);			
			tbc.failException("#",IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] {
							e.getClass().getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(), e.getClass().getName() }));
		}
		
	}
	
	/**
	 * Tests if the permission level parameter is case-insensitive. Passing
	 * the LEVEL_ONESHOT as parameter.
	 *  
	 * @spec UserPromptCondition.getCondition(Bundle,ConditionInfo)
	 */
	private void testGetCondition018() {
		tbc.log("#testGetCondition018");
		
		try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo("", new String[] {
							PolicyConstants.LEVEL_ONESHOT.toLowerCase(),
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.CATALOG_NAME, "Test for permission level parameter case-isensitive" })
							);
			tbc.assertNotNull(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_NOT_NULL,
					new String[] { "Created UserPrompt" }), condition);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] {
							e.getClass().getName() }));
		}
		
	}
	
	/**
	 * Tests if the permission level parameter is case-insensitive. Passing
	 * the LEVEL_BLANKET as parameter.
	 *  
	 * @spec UserPromptCondition.getCondition(Bundle,ConditionInfo)
	 */
	private void testGetCondition019() {
		tbc.log("#testGetCondition019");
		
		try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo("", new String[] {
							PolicyConstants.LEVEL_BLANKET.toLowerCase(),
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.CATALOG_NAME, "Test for permission level parameter case-isensitive" })
							);
			tbc.assertNotNull(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_NOT_NULL,
					new String[] { "Created UserPrompt" }), condition);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] {
							e.getClass().getName() }));
		}
		
	}	
	
	/**
	 * Tests if the permission level parameter is case-insensitive. Passing
	 * the LEVEL_SESSION as parameter.
	 *  
	 * @spec UserPromptCondition.getCondition(Bundle,ConditionInfo)
	 */
	private void testGetCondition020() {
		tbc.log("#testGetCondition020");
		
		try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo("", new String[] {
							PolicyConstants.LEVEL_SESSION.toLowerCase(),
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.CATALOG_NAME, "Test for permission level parameter case-isensitive" })
							);
			tbc.assertNotNull(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_NOT_NULL,
					new String[] { "Created UserPrompt" }), condition);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] {
							e.getClass().getName() }));
		}
		
	}
	
	/**
	 * Tests if the default permission level parameter is case-insensitive. Passing
	 * the LEVEL_ONESHOT as parameter.
	 *  
	 * @spec UserPromptCondition.getCondition(Bundle,ConditionInfo)
	 */
	private void testGetCondition021() {
		tbc.log("#testGetCondition021");
		
		try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo("", new String[] {
							PolicyConstants.LEVEL_ONESHOT,
							PolicyConstants.LEVEL_ONESHOT.toLowerCase(),
							PolicyConstants.CATALOG_NAME, "Test for permission level parameter case-isensitive" })
							);
			tbc.assertNotNull(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_NOT_NULL,
					new String[] { "Created UserPrompt" }), condition);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] {
							e.getClass().getName() }));
		}
		
	}
	
	/**
	 * Tests if the default permission level parameter is case-insensitive. Passing
	 * the LEVEL_BLANKET as parameter.
	 *  
	 * @spec UserPromptCondition.getCondition(Bundle,ConditionInfo)
	 */
	private void testGetCondition022() {
		tbc.log("#testGetCondition022");
		
		try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo("", new String[] {
							PolicyConstants.LEVEL_BLANKET,
							PolicyConstants.LEVEL_BLANKET.toLowerCase(),
							PolicyConstants.CATALOG_NAME, "Test for permission level parameter case-isensitive" })
							);
			tbc.assertNotNull(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_NOT_NULL,
					new String[] { "Created UserPrompt" }), condition);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] {
							e.getClass().getName() }));
		}
		
	}	
	
	/**
	 * Tests if the default permission level parameter is case-insensitive. Passing
	 * the LEVEL_SESSION as parameter.
	 *  
	 * @spec UserPromptCondition.getCondition(Bundle,ConditionInfo)
	 */
	private void testGetCondition023() {
		tbc.log("#testGetCondition023");
		
		try {
			Condition condition = UserPromptCondition.getCondition(tbc.getBundle(), 
					new ConditionInfo("", new String[] {
							PolicyConstants.LEVEL_SESSION,
							PolicyConstants.LEVEL_SESSION.toLowerCase(),
							PolicyConstants.CATALOG_NAME, "Test for permission level parameter case-isensitive" })
							);
			tbc.assertNotNull(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_NOT_NULL,
					new String[] { "Created UserPrompt" }), condition);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.UNEXPECTED_EXCEPTION, new String[] {
							e.getClass().getName() }));
		}
		
	}	
	
	
}
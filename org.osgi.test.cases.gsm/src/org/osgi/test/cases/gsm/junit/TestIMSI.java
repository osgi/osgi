/*
 * ============================================================================
 * (c) Copyright 2005 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */

package org.osgi.test.cases.gsm.junit;

import junit.framework.TestCase;

import org.osgi.framework.Bundle;
import org.osgi.service.condpermadmin.Condition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.test.support.MockFactory;
import org.osgi.util.gsm.IMSICondition;

public class TestIMSI extends TestCase {
	private Bundle				bundle		= (Bundle) MockFactory.newMock(
													Bundle.class, null);
	public static final String	SYSTEM_IMSI	= "012345678901234";
	public static final String	OTHER_IMSI	= "123456789012345";
	static {
		System.getProperties().put("org.osgi.util.gsm.imsi", SYSTEM_IMSI);
	}
	
	public void testBasic() throws Exception {
		Condition imsi = IMSICondition.getCondition(bundle, new ConditionInfo(
				"", new String[] {SYSTEM_IMSI}));
		assertFalse(imsi.isPostponed());
		assertTrue(imsi.isSatisfied());
		
		imsi = IMSICondition.getCondition(bundle, new ConditionInfo("",
				new String[] {OTHER_IMSI}));
		assertFalse(imsi.isPostponed());
		assertFalse(imsi.isSatisfied());
	}
	
	public void testIMSIValidator() throws Exception {
		try {
			IMSICondition.getCondition(bundle,new ConditionInfo("",new String[]{""}));
			fail();
		}
		catch (IllegalArgumentException e) {/* expected */
		}
		try {
			IMSICondition.getCondition(bundle,new ConditionInfo("",new String[]{"12345678901234"}));
			fail();
		}
		catch (IllegalArgumentException e) {/* expected */
		}
		try {
			IMSICondition.getCondition(bundle,new ConditionInfo("",new String[]{"1234567890123456"}));
			fail();
		}
		catch (IllegalArgumentException e) {/* expected */
		}
		try {
			IMSICondition.getCondition(bundle,new ConditionInfo("",new String[]{"12345678901234a"}));
			fail();
		}
		catch (IllegalArgumentException e) {/* expected */
		}
		IMSICondition.getCondition(bundle,new ConditionInfo("",new String[]{"12345678901234*"}));
		try {
			IMSICondition.getCondition(bundle,new ConditionInfo("",new String[]{"123456789012345*"}));
			fail();
		}
		catch (IllegalArgumentException e) {/* expected */
		}
	}

	public void testWildcards() throws Exception {
		Condition IMSI = IMSICondition.getCondition(bundle, new ConditionInfo(
				"", new String[] {SYSTEM_IMSI.substring(0, 5) + "*"}));
		assertTrue(IMSI.isSatisfied());	

		IMSI = IMSICondition.getCondition(bundle, new ConditionInfo("",
				new String[] {"777*"}));
		assertFalse(IMSI.isSatisfied());
	}

  /**
   * This test asserts if a condition is sucessfully created when imsi
   * parameter is a valid code
   *
   * @spec IMSICondition.getCondition(Bundle,ConditionInfo)
   */

  public void testGetCondition001() {
          Condition cond = IMSICondition.getCondition(
              bundle,
                  new ConditionInfo("org.osgi.util.gsm.IMSICondition",new String[]{PolicyConstants.IMSI_VALID_CODE}));

          assertNotNull("created condition", cond);
  }


  /**
   * testGetCondition002 has been removed as it is no valid anymore... used to be:
   * This test asserts if NullPointerException is thrown when null is
   * passed for the first parameter
   */


  /**
   * This test asserts if NullPointerException is thrown when null is
   * passed for the second parameter
   *
   * @spec IMSICondition.getCondition(Bundle,ConditionInfo)
   */

  public void testGetCondition003() {
      try {
          IMSICondition.getCondition(
              bundle, null);

          failException("", NullPointerException.class);
      } catch (NullPointerException e) {
        // expected
      }
  }


  /**
   * This test asserts if IllegalArgumentException is thrown if the
   * imsi is not a valid string
   *
   * @spec IMSICondition.getCondition(Bundle,ConditionInfo)
   */

  public void testGetCondition004() {
      try {
          IMSICondition.getCondition(
              bundle,
                  new ConditionInfo("org.osgi.util.gsm.IMSICondition",new String[]{PolicyConstants.INVALID_CODE}));

          failException("", IllegalArgumentException.class);
      } catch (IllegalArgumentException e) {
        // expected
      }
  }


  /**
   * This test asserts if IllegalArgumentException is thrown if the
   * imsi code is with more than 15 digits
   *
   * @spec IMSICondition.getCondition(Bundle,ConditionInfo)
   */

  public void testGetCondition005() {
      try {
          IMSICondition.getCondition(
              bundle,
                  new ConditionInfo("org.osgi.util.gsm.IMSICondition",new String[]{PolicyConstants.IMSI_MORE_DIGIT_CODE}));

          failException("", IllegalArgumentException.class);
      } catch (IllegalArgumentException e) {
        // expected
      }
  }


  /**
   * This test asserts if a condition is not created when imsi
   * parameter is an invalid char code
   *
   * @spec IMSICondition.getCondition(Bundle,ConditionInfo)
   */

  public void testGetCondition006() {
      try {
          IMSICondition.getCondition(bundle,
                  new ConditionInfo("org.osgi.util.gsm.IMSICondition",new String[]{PolicyConstants.IMSI_CHAR_CODE}));

          failException("", IllegalArgumentException.class);
      } catch (IllegalArgumentException e) {
        // expected
      }
  }


  /**
   * This test asserts if IllegalArgumentException is thrown if the
   * imsi code is with less than 15 digits
   *
   * @spec IMSICondition.getCondition(Bundle,ConditionInfo)
   */

  public void testGetCondition007() {
      try {
          IMSICondition.getCondition(
              bundle,
                  new ConditionInfo("org.osgi.util.gsm.IMSICondition",new String[]{PolicyConstants.IMSI_LESS_DIGIT_CODE}));

             failException("", IllegalArgumentException.class);
      } catch (IllegalArgumentException e) {
        // expected
      }
  }


  /**
   * This test asserts if IllegalArgumentException is thrown if the
   * imsi code is a string with hyphens
   *
   * @spec IMSICondition.getCondition(Bundle,ConditionInfo)
   */

  public void testGetCondition008() {
      try {
          IMSICondition.getCondition(
              bundle,
                  new ConditionInfo("org.osgi.util.gsm.IMSICondition",new String[]{PolicyConstants.IMSI_INVALID_CODE}));

          failException("", IllegalArgumentException.class);
      } catch (IllegalArgumentException e) {
        // expected
      }
  }

  /**
   * This test asserts if a condition is sucessfully created when the imei
   * parameter ends with a wildcard
   *
   * @spec IMSICondition.getCondition(Bundle,ConditionInfo)
   */

  public void testGetCondition009() {
        Condition cond = IMSICondition.getCondition(
            bundle,
                  new ConditionInfo("org.osgi.util.gsm.IMSICondition",new String[]{PolicyConstants.IMSI_VALID_CODE_WILDCARD}));

          assertNotNull("created condition", cond);
  }


  /**
   * This test asserts if a condition is sucessfully created when the imei
   * parameter has only one element, a wildcard.
   *
   * @spec IMSICondition.getCondition(Bundle,ConditionInfo)
   */

  public void testGetCondition010() {
        Condition cond = IMSICondition.getCondition(
            bundle,
                  new ConditionInfo("org.osgi.util.gsm.IMSICondition",new String[]{"*"}));

        assertNotNull("created condition", cond);
  }

  public static void failException(String message,
      Class expectedExceptionClass) {
    fail(message + " expected:[" + expectedExceptionClass.getName()
        + "] and got nothing");
  }  
}

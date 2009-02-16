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

package org.osgi.test.cases.gsm;

import junit.framework.TestCase;

import org.osgi.framework.Bundle;
import org.osgi.service.condpermadmin.Condition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.test.support.MockFactory;
import org.osgi.util.gsm.IMEICondition;

public class TestIMEI extends TestCase {
	private Bundle				bundle		= (Bundle) MockFactory.newMock(
													Bundle.class, null);
	public static final String	SYSTEM_IMEI	= "012345678901234";
	public static final String	OTHER_IMEI	= "123456789012345";
	static {
		System.getProperties().put("org.osgi.util.gsm.imei", SYSTEM_IMEI);
	}
	
	public void testBasic() throws Exception {
		Condition imei = IMEICondition.getCondition(bundle, new ConditionInfo(
				"", new String[] {SYSTEM_IMEI}));
		assertFalse(imei.isPostponed());
		assertTrue(imei.isSatisfied());
		
		imei = IMEICondition.getCondition(bundle, new ConditionInfo("",
				new String[] {OTHER_IMEI}));
		assertFalse(imei.isPostponed());
		assertFalse(imei.isSatisfied());
	}
	
	public void testIMEIValidator() throws Exception {
		try {
			IMEICondition.getCondition(bundle,new ConditionInfo("",new String[]{""}));
			fail();
		}
		catch (IllegalArgumentException e) {/* expected */
		}
		try {
			IMEICondition.getCondition(bundle,new ConditionInfo("",new String[]{"12345678901234"}));
			fail();
		}
		catch (IllegalArgumentException e) {/* expected */
		}
		try {
			IMEICondition.getCondition(bundle,new ConditionInfo("",new String[]{"1234567890123456"}));
			fail();
		}
		catch (IllegalArgumentException e) {/* expected */
		}
		try {
			IMEICondition.getCondition(bundle,new ConditionInfo("",new String[]{"12345678901234a"}));
			fail();
		}
		catch (IllegalArgumentException e) {/* expected */
		}

		IMEICondition.getCondition(bundle,new ConditionInfo("",new String[]{"01234567891234*"}));

		try {
			IMEICondition.getCondition(bundle,new ConditionInfo("",new String[]{"012345678901234*"}));
			fail();
		}
		catch (IllegalArgumentException e) {/* expected */
		}
	}
	
	public void testWildcards() throws Exception {
		Condition imei = IMEICondition.getCondition(bundle, new ConditionInfo(
				"", new String[] {SYSTEM_IMEI.substring(0, 5) + "*"}));
		assertTrue(imei.isSatisfied());	

		imei = IMEICondition.getCondition(bundle, new ConditionInfo("",
				new String[] {"777*"}));
		assertFalse(imei.isSatisfied());
	}
}

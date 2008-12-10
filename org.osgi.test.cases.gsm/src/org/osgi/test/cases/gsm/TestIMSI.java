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
import org.osgi.framework.BundleContext;
import org.osgi.service.condpermadmin.Condition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.util.gsm.IMSICondition;

public class TestIMSI extends TestCase {
	private BundleContext	context;
	private Bundle			bundle;

	public void setBundleContext(BundleContext context) {
		this.context = context;
		bundle = this.context.getBundle();
	}
	
	public static final String SYSTEM_IMSI = System.getProperty("org.osgi.util.gsm.imsi");
	public static final String OTHER_IMSI = "123456789012345";
	
	public void testBasic() throws Exception {
		Condition imsi = (Condition) IMSICondition.getCondition(bundle,new ConditionInfo("",new String[]{SYSTEM_IMSI}));
		assertFalse(imsi.isPostponed());
		assertTrue(imsi.isSatisfied());
		
		imsi = (Condition) IMSICondition.getCondition(bundle,new ConditionInfo("",new String[]{OTHER_IMSI}));
		assertFalse(imsi.isPostponed());
		assertFalse(imsi.isSatisfied());
	}
	
	public void testIMSIValidator() throws Exception {
		try {
			IMSICondition.getCondition(bundle,new ConditionInfo("",new String[]{""}));
			fail();
		} catch (IllegalArgumentException e) {}
		try {
			IMSICondition.getCondition(bundle,new ConditionInfo("",new String[]{"12345678901234"}));
			fail();
		} catch (IllegalArgumentException e) {}
		try {
			IMSICondition.getCondition(bundle,new ConditionInfo("",new String[]{"1234567890123456"}));
			fail();
		} catch (IllegalArgumentException e) {}
		try {
			IMSICondition.getCondition(bundle,new ConditionInfo("",new String[]{"12345678901234a"}));
			fail();
		} catch (IllegalArgumentException e) {}
		IMSICondition.getCondition(bundle,new ConditionInfo("",new String[]{"12345678901234*"}));
		try {
			IMSICondition.getCondition(bundle,new ConditionInfo("",new String[]{"123456789012345*"}));
			fail();
		} catch (IllegalArgumentException e) {}
	}

	public void testWildcards() throws Exception {
		Condition IMSI = (Condition) IMSICondition.getCondition(bundle,new ConditionInfo("",new String[]{SYSTEM_IMSI.substring(0,5)+"*"}));
		assertTrue(IMSI.isSatisfied());	

		IMSI = (Condition) IMSICondition.getCondition(bundle,new ConditionInfo("",new String[]{"777*"}));
		assertFalse(IMSI.isSatisfied());
	}
}

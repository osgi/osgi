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

package org.osgi.test.cases.cdma;

import junit.framework.TestCase;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.condpermadmin.Condition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.util.cdma.MEIDCondition;

public class TestMEID extends TestCase {
	private BundleContext	context;
	private Bundle			bundle;

	public void setBundleContext(BundleContext context) {
		this.context = context;
		bundle = this.context.getBundle();
	}

	public static final String SYSTEM_MEID = System.getProperty("org.osgi.util.cdma.meid");
	public static final String OTHER_MEID = "CAFEBABE42CAFE";
	public void testBasic() throws Exception {
		Condition esn = (Condition) MEIDCondition.getCondition(bundle,new ConditionInfo("",new String[]{SYSTEM_MEID}));
		assertFalse(esn.isPostponed());
		assertTrue(esn.isSatisfied());
		
		esn = (Condition) MEIDCondition.getCondition(bundle,new ConditionInfo("",new String[]{OTHER_MEID}));
		assertFalse(esn.isPostponed());
		assertFalse(esn.isSatisfied());
	}
	
	public void testMEIDValidator() throws Exception {
		try {
			MEIDCondition.getCondition(bundle,new ConditionInfo("",new String[]{""}));
			fail();
		} catch (IllegalArgumentException e) {}
		try {
			MEIDCondition.getCondition(bundle,new ConditionInfo("",new String[]{"1234567890123"}));
			fail();
		} catch (IllegalArgumentException e) {}
		try {
			MEIDCondition.getCondition(bundle,new ConditionInfo("",new String[]{"123456789012345"}));
			fail();
		} catch (IllegalArgumentException e) {}
		try {
			MEIDCondition.getCondition(bundle,new ConditionInfo("",new String[]{"1234567890ghij"}));
			fail();
		} catch (IllegalArgumentException e) {}

		MEIDCondition.getCondition(bundle,new ConditionInfo("",new String[]{"1234567891*"}));

		try {
			MEIDCondition.getCondition(bundle,new ConditionInfo("",new String[]{"12345678901234*"}));
			fail();
		} catch (IllegalArgumentException e) {}
	}
	
	public void testWildcards() throws Exception {
		Condition esn = (Condition) MEIDCondition.getCondition(bundle,new ConditionInfo("",new String[]{SYSTEM_MEID.substring(0,5)+"*"}));
		assertTrue(esn.isSatisfied());	

		esn = (Condition) MEIDCondition.getCondition(bundle,new ConditionInfo("",new String[]{"777*"}));
		assertFalse(esn.isSatisfied());
		
	}
}

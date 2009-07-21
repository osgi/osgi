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

package org.osgi.test.cases.cdma.junit;

import junit.framework.TestCase;

import org.osgi.framework.Bundle;
import org.osgi.service.condpermadmin.Condition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.test.support.MockFactory;
import org.osgi.util.cdma.ESNCondition;

public class TestESN extends TestCase {
	private Bundle				bundle		= (Bundle) MockFactory.newMock(
													Bundle.class, null);
	private static final String	SYSTEM_ESN	= "89abcdef";
	private static final String	OTHER_ESN	= "CAFEBABE";
	static {
		System.getProperties().put("org.osgi.util.cdma.esn", SYSTEM_ESN);
	}

	public void testBasic() throws Exception {
		Condition esn = ESNCondition.getCondition(bundle, new ConditionInfo("",
				new String[] {SYSTEM_ESN}));
		assertFalse(esn.isPostponed());
		assertTrue(esn.isSatisfied());

		esn = ESNCondition.getCondition(bundle, new ConditionInfo("",
				new String[] {OTHER_ESN}));
		assertFalse(esn.isPostponed());
		assertFalse(esn.isSatisfied());
	}

	public void testESNValidator() throws Exception {
		try {
			ESNCondition.getCondition(bundle, new ConditionInfo("",
					new String[] {""}));
			fail();
		}
		catch (IllegalArgumentException e) {/* expected */
		}
		try {
			ESNCondition.getCondition(bundle, new ConditionInfo("",
					new String[] {"1234567"}));
			fail();
		}
		catch (IllegalArgumentException e) {/* expected */
		}
		try {
			ESNCondition.getCondition(bundle, new ConditionInfo("",
					new String[] {"123456789"}));
			fail();
		}
		catch (IllegalArgumentException e) {/* expected */
		}
		try {
			ESNCondition.getCondition(bundle, new ConditionInfo("",
					new String[] {"1234567g"}));
			fail();
		}
		catch (IllegalArgumentException e) {/* expected */
		}

		ESNCondition.getCondition(bundle, new ConditionInfo("",
				new String[] {"123456*"}));

		try {
			ESNCondition.getCondition(bundle, new ConditionInfo("",
					new String[] {"12345678*"}));
			fail();
		}
		catch (IllegalArgumentException e) {/* expected */
		}
	}

	public void testWildcards() throws Exception {
		Condition esn = ESNCondition.getCondition(bundle, new ConditionInfo("",
				new String[] {SYSTEM_ESN.substring(0, 5) + "*"}));
		assertTrue(esn.isSatisfied());

		esn = ESNCondition.getCondition(bundle, new ConditionInfo("",
				new String[] {"777*"}));
		assertFalse(esn.isSatisfied());
	}
}

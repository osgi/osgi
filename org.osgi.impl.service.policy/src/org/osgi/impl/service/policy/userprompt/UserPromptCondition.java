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

package org.osgi.impl.service.policy.userprompt;

import java.util.Dictionary;
import java.util.HashMap;
import org.osgi.framework.Bundle;
import org.osgi.service.condpermadmin.Condition;

/**
 *
 * Basic console userprompt implementation.
 * TODO: this is currently just a stub!
 * 
 * @version $Revision$
 */
public class UserPromptCondition extends
		org.osgi.util.mobile.UserPromptCondition {
	
	final Bundle bundle;
	final String catalogName;
	final String message;
	
	/**
	 * Stores UserPrompts with key as created by generateUniqueID().
	 */
	HashMap userPrompts = new HashMap();
	
	/**
	 * Create an identifier to use as storage key for a userprompt condition
	 * @param bundle
	 * @param catalogName
	 * @param message
	 * @return
	 */
	static String generateUniqueID(Bundle bundle,String catalogName,String message) {
		return ""+bundle.getBundleId()+"|"+catalogName+"|"+message;
	}

	
	
	public boolean isEvaluated() {
		return true;
	}
	public boolean isMutable() {
		return false;
	}
	public boolean isSatisfied() {
		//System.out.println("isSatisfied called");
		return true;
	}
	public boolean isSatisfied(Condition[] conds, Dictionary context) {
		return false;
	}

	protected UserPromptCondition(Bundle bundle, String levels, String defaultLevel, String catalogName, String message) {
		this.bundle = bundle;
		this.catalogName = catalogName;
		this.message = message;
	}

	
	public static class Factory implements org.osgi.util.mobile.UserPromptCondition.UserPromptConditionFactory {
		public org.osgi.util.mobile.UserPromptCondition getInstance(Bundle bundle, String levels, String defaultLevel, String catalogName, String message) {
			return new UserPromptCondition(bundle,levels,defaultLevel,catalogName,message);
		}
	}
}

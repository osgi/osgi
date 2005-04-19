/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
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
 */
package org.osgi.util.mobile;

import java.util.Dictionary;
import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.service.condpermadmin.Condition;

/**
 * Class representing a user prompt condition. Instances of this class hold two
 * values: a prompt string that is to be displayed to the user and the
 * permission level string according to MIDP2.0 (oneshot, session, blanket).
 *  
 */
public class UserPromptCondition implements Condition {
	protected static interface UserPromptConditionFactory {
		public UserPromptCondition getInstance(
				Bundle bundle,
				String levels,
				String defaultLevel, 
				String catalogName, 
				String message);
	}

	private static UserPromptConditionFactory factory = null;
	protected static void setFactory(UserPromptConditionFactory factory) {
		SecurityManager sm = System.getSecurityManager();
		if (sm!=null) {
			sm.checkPermission(new AdminPermission("*","*"));
		}
		UserPromptCondition.factory = factory;
	}
	
	private final Bundle bundle;
	private final String levels;
	private final String defaultLevel;
	private final String catalogName;
	private final String message;
	private UserPromptCondition realUserPromptCondition;

	/**
	 * Creates an UserPrompt object with the given prompt string and permission
	 * level. The user should be given choice as to what level of permission is
	 * given. Thus, the lifetime of the permission is controlled by the user.
	 *
	 * @param bundle the bundle to ask about
	 * @param levels the possible permission levels. This is a comma-separated list that can contain
	 * 		following strings: ONESHOT SESSION BLANKET. The order is not important.
	 * @param defaultLevel the default permission level, one chosen from the levels parameter. I
	 * 		f it is an empty string, then there is no default.
	 * @param catalogName the message catalog base name
	 * @param message textual description of the condition, to be displayed to the user. If
	 * 		it starts with a '%' sign, then the message is looked up from the catalog specified
	 * 		by the catalogName parameter. The key is the rest of the string after the '%' sign.
	 */
	public static Condition getInstance(Bundle bundle,
					String levels,
					String defaultLevel, 
					String catalogName, 
					String message) 
	{
		if (factory==null) {
			// the bundle implementing the UserPromptCondition has not started yet.
			// Do wrapping magick.
			return new UserPromptCondition(false,bundle,levels,defaultLevel,catalogName,message);
		} else {
			// there is already a factory, no need to do any wrapping magic
			return factory.getInstance(bundle,levels,defaultLevel,catalogName,message);
		}
	}

	/**
	 * Instances of the UserPromptCondition are simply store the construction parameters
	 * until a "real" UserPromptCondition is registered in setFactory(). At that point, it
	 * will delegate all calls there.
	 * @param unused this parameter is here so that ConditionalPermissionAdmin would not
	 * 		use this as the constructor instead of the getInstance
	 * @param bundle
	 * @param levels
	 * @param defaultLevel
	 * @param catalogName
	 * @param message
	 */
	private UserPromptCondition(boolean unused,Bundle bundle,String levels,String defaultLevel,String catalogName,String message) {
		this.bundle=bundle;
		this.levels=levels;
		this.defaultLevel=defaultLevel;
		this.catalogName=catalogName;
		this.message=message;
	}
	
	protected UserPromptCondition() {
		bundle=null;
		levels=null;
		defaultLevel=null;
		catalogName=null;
		message=null;
	}

	/**
	 * Check if a factory is registered, and if yes, create userprompt to delegate calls to.
	 */
	private void lookForImplementation() {
		if ((realUserPromptCondition==null)&&(factory!=null)) {
			realUserPromptCondition = factory.getInstance(bundle,levels,defaultLevel,catalogName,message);
		}
	}
	
	/**
	 * Checks whether the condition is evaluated. Depending on the permission
	 * level, the function returns the following values.
	 * <ul>
	 * <li>ONESHOT - isSatisfied always returns false. The condition
	 * reevaluated each time it is checked.
	 * <li>SESSION - isSatisfied returns false until isEvaluated() returns true
	 * (the user accepts the prompt for this execution of the bundle). Then
	 * isSatisfied() returns true until the bundle is stopped.
	 * <li>BLANKET - isSatisfied returns false until isEvaluated() returns true
	 * (the user accepts the prompt for the lifetime of the bundle). Then
	 * isSatisfied() returns true until the bundle is uninstalled.
	 * </ul>
	 * 
	 * @return true if the condition is satisfied.
	 */
	public boolean isEvaluated() {
		lookForImplementation();
		if (realUserPromptCondition!=null) {
			return realUserPromptCondition.isEvaluated();
		} else {
			return false;
		}
	}

	/**
	 * Checks whether the condition may change in the future.
	 * 
	 * @return
	 * <ul>
	 * <li>false, if it is already evaluated and is a BLANKET condition
	 * <li>true otherwise
	 * </ul>
	 */
	public boolean isMutable() {
		lookForImplementation();
		if (realUserPromptCondition!=null) {
			return realUserPromptCondition.isMutable();
		} else {
			// since we don't know what the actual status is, we cannot say
			// "the condition cannot change anymore"
			return true;
		}
	}

	/**
	 * Checks whether the condition is satisfied. Displays the prompt string to
	 * the user and returns true if the user presses "accept". Depending on the
	 * amount of levels the condition is assigned to, the prompt may have
	 * multiple "accept" buttons and one of them can be selected by default (see
	 * deflevel parameter at UserPrompt constructor). It must be always possible
	 * to deny the permission (e.g. by a separate "deny" button).
	 * 
	 * @return true if the user accepts the prompt (or accepts any prompt in
	 *         case there are multiple permission levels).
	 */
	public boolean isSatisfied() {
		lookForImplementation();
		if (realUserPromptCondition!=null) {
			return realUserPromptCondition.isSatisfied();
		} else {
			// paranoid security option
			return false;
		}
	}

	/**
	 * Checks an array of UserPrompt conditions
	 * 
	 * @param conds the array containing the UserPrompt conditions to evaluate
	 * @param context storage area for one-shot evaluation
	 * @return true, if all conditions are satisfied
	 */
	public boolean isSatisfied(Condition[] conds, Dictionary context) {
		lookForImplementation();
		if (realUserPromptCondition!=null) {
			return realUserPromptCondition.isSatisfied(conds,context);
		} else {
			// paranoid security option
			return false;
		}
	}
}

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
	static {
		String factoryName = System.getProperty("org.osgi.util.mobile.userpromptcondition.factory");
		if (factoryName!=null) {
			try {
				factory = (UserPromptConditionFactory) Class.forName(factoryName).newInstance();
			}
			catch (IllegalAccessException e) {
			}
			catch (InstantiationException e) {
			}
			catch (ClassNotFoundException e) {
			}
		}
	}

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
		if (factory==null) return null;
		return factory.getInstance(bundle,levels,defaultLevel,catalogName,message);
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
		return false;
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
		return false;
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
		return false;
	}

	/**
	 * Checks an array of UserPrompt conditions
	 * 
	 * @param conds the array containing the UserPrompt conditions to evaluate
	 * @param context storage area for one-shot evaluation
	 * @return true, if all conditions are satisfied
	 */
	public boolean isSatisfied(Condition[] conds, Dictionary context) {
		for(int i=0;i<conds.length;i++) {
			if (!conds[i].isSatisfied()) return false;
		}
		return true;
	}
}

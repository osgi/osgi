/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
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
	protected static UserPromptCondition unWrap(UserPromptCondition c) {
		if (c.realUserPromptCondition==null) return c;
		return c.realUserPromptCondition;
	}

	/**
	 * Creates an UserPrompt object with the given prompt string and permission
	 * level. The user should be given choice as to what level of permission is
	 * given. Thus, the lifetime of the permission is controlled by the user.
	 *
	 * @param bundle the bundle to ask about.
	 * @param levels the possible permission levels. This is a comma-separated list that can contain
	 * 		following strings: ONESHOT SESSION BLANKET. The order is not important.
	 * @param defaultLevel the default permission level, one chosen from the levels parameter. If
	 * 		it is an empty string, then there is no default.
	 * @param catalogName the message catalog base name. It will be loaded by a {@link java.util.ResourceBundle},
	 * 		or equivalent
	 * 		from an exporting OSGi Bundle. Thus, if the catalogName is "com.provider.messages.userprompt",
	 * 		then there should be an OSGi Bundle exporting the "com.provider.messages" package, and inside
	 * 		it files like "userprompt_en_US.properties".
	 * @param message textual description of the condition, to be displayed to the user. If
	 * 		it starts with a '%' sign, then the message is looked up from the catalog specified
	 * 		by the catalogName parameter. The key is the rest of the string after the '%' sign.
	 * @throws IllegalArgumentException if the parameters are malformed.
	 * @throws NullPointerException if one of the parameters is <code>null</code>.
	 */
	public static Condition getInstance(Bundle bundle,
					String levels,
					String defaultLevel, 
					String catalogName, 
					String message) 
	{
		if (bundle==null) throw new NullPointerException("bundle");
		if (levels==null) throw new NullPointerException("levels");
		if (defaultLevel==null) throw new NullPointerException("defaultLevel");
		if (catalogName==null) throw new NullPointerException("catalogName");
		if (message==null) throw new NullPointerException("message");
		
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
	 * Checks if the {@link #isSatisfied()} method can return a result without actually
	 * prompting the user. This depends on the possible permission levels given in 
	 * {@link UserPromptCondition#getInstance(Bundle, String, String, String, String)}. 
	 * <ul>
	 * <li>ONESHOT - isEvaluated always returns false. The user is prompted for question every time.
	 * <li>SESSION - isEvaluated returns false until the user decides either yes or no for the current session.
	 * <li>BLANKET - isEvaluated returns false until the user decides either always or never.
	 * 			After that, it is always true.
	 * </ul>
	 * 
	 * @return True, if no prompt is needed.
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
	 * Checks whether the condition may change during the lifetime of the UserPromptCondition object.
	 * 
	 * @return
	 * <ul>
	 * <li>false, if it is a BLANKET condition, and the user already answered to the question.
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
	 * Displays the prompt string to
	 * the user and returns true if the user accepts. Depending on the
	 * amount of levels the condition is assigned to, the prompt may have
	 * multiple accept buttons and one of them can be selected by default (see
	 * deflevel parameter at UserPrompt constructor). It must be always possible
	 * to deny the permission (e.g. by a separate "deny" button). In case of BLANKET
	 * and SESSION levels, it is possible that the user has already answered the question,
	 * in this case there will be no prompting, but immediate return with the previous answer.
	 * 
	 * @return True if the user accepts the prompt (or accepts any prompt in
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
	 * Checks an array of UserPrompt conditions.
	 * 
	 * @param conds The array containing the UserPrompt conditions to evaluate.
	 * @param context Storage area for evaluation. The {@link org.osgi.service.condpermadmin.ConditionalPermissionAdmin}
	 * 		may evaluate a condition several times for one permission check, so this context
	 * 		will be used to store results of ONESHOT questions. This way asking the same question
	 * 		twice in a row can be avoided. If context is null, temporary results will not be stored.
	 * @return True, if all conditions are satisfied.
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

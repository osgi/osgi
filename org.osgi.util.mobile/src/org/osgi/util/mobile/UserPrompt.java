/*
 * Copyright (c) The Open Services Gateway Initiative (2004).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.util.mobile;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import org.osgi.framework.Bundle;
import org.osgi.service.condpermadmin.Condition;

/**
 * Class representing a user prompt condition. Instances of this class hold two
 * values: a prompt string that is to be displayed to the user and the
 * permission level string according to MIDP2.0 (oneshot, session, blanket).
 *  
 */
public class UserPrompt implements Condition {
	private long				bundleId;
	private Long				bundleIdObj;
	private String				level;
	private String				prompt;
	private static HashMap		results	= new HashMap();
	public static final String	ONESHOT	= "oneshot";
	public static final String	BLANKET	= "blanket";
	private static Object		promptHandler;
	private static Method		doPromptMethod;

	/**
	 * Creates an UserPrompt object with the given prompt string and permission
	 * level. There is one interaction mode with this permission and that is the
	 * default as well.
	 * 
	 * @param bundle the bundle to ask about
	 * @param prompt The message to display to the user.
	 * @param level The permission level. This must be ONESHOT, SESSION or
	 *        BLANKET.
	 */
	public UserPrompt(Bundle bundle, String prompt, String level) {
		this.bundleId = bundle.getBundleId();
		this.bundleIdObj = new Long(bundleId);
		this.prompt = prompt;
		if (level.equals(ONESHOT)) {
			this.level = ONESHOT;
		}
		else
			if (level.equals(BLANKET)) {
				this.level = BLANKET;
			}
			else
				throw new IllegalArgumentException("unknown level: " + level);
	}

	/**
	 * Creates an UserPrompt object with the given prompt string and permission
	 * levels. The user should be given choice as to what level of permission is
	 * given. Thus, the lifetime of the permission is controlled by the user.
	 * 
	 * @param bundle the bundle to ask about
	 * @param prompt The message to display to the user.
	 * @param deflevel The default level. If it is "X" then there's no default
	 *        level, if it is "0" then level1 is the default level.
	 * @param level1 1st permission level. This must be ONESHOT, SESSION,
	 *        BLANKET or X if this field does not convey a permission level.
	 * @param level2 2nd permission level. This must be ONESHOT, SESSION,
	 *        BLANKET or X if this field does not convey a permission level.
	 * @param level3 3rd permission level. This must be ONESHOT, SESSION,
	 *        BLANKET or X if this field does not convey a permission level.
	 */
	public UserPrompt(Bundle bundle, String prompt, String deflevel,
			String level1, String level2, String level3) {
		throw new IllegalArgumentException("not implemented yet");
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
		if (level == ONESHOT) {
			return false;
		}
		else {
			Boolean result = (Boolean) results.get(bundleIdObj);
			return result != null;
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
		if (level == ONESHOT)
			throw new IllegalStateException();
		Boolean result = (Boolean) results.get(bundleIdObj);
		return result.booleanValue();
	}

	/**
	 * Checks an array of UserPrompt conditions
	 * 
	 * @param conds the array containing the UserPrompt conditions to evaluate
	 * @param context storage area for one-shot evaluation
	 * @return true, if all conditions are satisfied
	 */
	public boolean isSatisfied(Condition[] conds, Dictionary context) {
		ArrayList toQuestion = getOpenQuestions(conds, context);
		for (Iterator i = toQuestion.iterator(); i.hasNext();) {
			UserPrompt cond = (UserPrompt) i.next();
			boolean result = cond.doPrompt();
			cond.storeResult(result, context);
			if (result == false)
				return false;
		}
		return true;
	}

	// -----------------------------------
	// prototype hacks start here
	//----------------------------------
	static ArrayList getOpenQuestions(Condition[] conds, Dictionary oneShotData) {
		ArrayList toQuestion = new ArrayList();
		for (int i = 0; i < conds.length; i++) {
			UserPrompt cond = (UserPrompt) conds[i];
			if (cond.needsToPrompt(oneShotData))
				toQuestion.add(cond);
		}
		return toQuestion;
	}

	boolean needsToPrompt(Dictionary oneShotData) {
		Boolean result;
		if (level == BLANKET) {
			result = (Boolean) results.get(bundleIdObj);
		}
		else {
			result = (Boolean) oneShotData.get(bundleIdObj);
		}
		return result == null;
	}

	public static void clearDatabase() {
		results = new HashMap();
	}

	public static void setPromptHandler(Object handler) {
		promptHandler = handler;
		try {
			doPromptMethod = promptHandler.getClass().getDeclaredMethod(
					"doPrompt", new Class[] {String.class});
			doPromptMethod.setAccessible(true);
		}
		catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean doPrompt() {
		try {
			Boolean result = (Boolean) doPromptMethod.invoke(promptHandler,
					new Object[] {prompt});
			return result.booleanValue();
		}
		catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new RuntimeException();
	}

	private void storeResult(boolean result, Dictionary oneShotData) {
		if (level == BLANKET) {
			//results.put(bundleIdObj,Boolean.valueOf(result));
			results.put(bundleIdObj, new Boolean(result));
		}
		else {
			//oneShotData.put(bundleIdObj,Boolean.valueOf(result));
			oneShotData.put(bundleIdObj, new Boolean(result));
		}
	}
}

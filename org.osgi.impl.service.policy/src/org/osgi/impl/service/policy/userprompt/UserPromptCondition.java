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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
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
	
	public static String ONESHOT_STRING = "ONESHOT";
	public static String SESSION_STRING = "SESSION";
	public static String BLANKET_STRING = "BLANKET";
	public static String NEVER_STRING = "NEVER";
	
	/**
	 * All userprompt conditions are stored here, so that there are no duplicates.
	 * The keys are strings as given by generateUniqueID, the values are the conditions themselves.
	 */
	static HashMap conditions;
	
	final Bundle bundle;
	final String catalogName;
	final String message;
	final String defaultLevel;
	final String levels;
	final boolean oneshotAllowed;
	final boolean sessionAllowed;
	final boolean blanketAllowed;
	
	/**
	 * What is the choice the user made last time. Possible values "SESSION", "BLANKET", "NEVER",
	 * or null, meaning there has been no choice yet. "ONESHOT" is not included, since even
	 * if the user said yes to it, it has no consequence on subsequent prompts.
	 */
	String status = null;
	
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
		return status!=null;
	}
	
	public boolean isMutable() {
		return status!=null;
		// Our implementation doesn't have a separate management interface for setting permissions,
		// so once the user chose for example "blanket", it will always stay that way.
	}

	private String getLocalizedMessage() {
		String localizedMessage;
		if (message.startsWith("%")) {
			String messageID = message.substring(1);
			ResourceBundle resourceBundle = ResourceBundle.getBundle(catalogName);
			localizedMessage = resourceBundle.getString(messageID);
			if (localizedMessage==null) { // fall back
				localizedMessage = messageID;
			}
		} else {
			localizedMessage = message;
		}
		return localizedMessage;
		
	}

	private List getPossibleAnswers() {
		ArrayList al = new ArrayList(5);
		if (blanketAllowed) {
			al.add("always");
		}
		if (sessionAllowed) {
			al.add("session");
		}
		if (oneshotAllowed) {
			al.add("yes");
			al.add("no");
		}
		al.add("never");
		return al;
	}
	
	public boolean isSatisfied() {
		if (status!=null) {
			if (status.equals(BLANKET_STRING)||status.equals(SESSION_STRING)) return true;
			if (status.equals(NEVER_STRING)) return false;
		}

		System.out.println("User Question:");
		System.out.println(getLocalizedMessage());
		List possibleAnswers = getPossibleAnswers();
		System.out.println(possibleAnswers.toString());
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String answer = null;
		while (answer==null) {
			try {
				answer = reader.readLine();
			}
			catch (IOException e) {
				// TODO do log and whatever
				e.printStackTrace();
				return false;
			}
			if (possibleAnswers.contains(answer)) break;
			answer = null;
		}
		
		// so we have an answer
		if (answer.equals("always")) {
			status = BLANKET_STRING;
			return true;
		}
		if (answer.equals("session")) {
			status = SESSION_STRING;
			return true;
		}
		if (answer.equals("never")) {
			status = NEVER_STRING;
			return false;
		}
		throw new IllegalStateException("todo");
	}
	
	public boolean isSatisfied(Condition[] conds, Dictionary context) {
		System.out.println("multiple isSatisfied called!");
		return false;
	}

	protected UserPromptCondition(Bundle bundle, String levels, String defaultLevel, String catalogName, String message) {
		this.bundle = bundle;
		this.catalogName = catalogName;
		this.message = message;
		this.defaultLevel = defaultLevel;
		this.levels = levels;
		
		if (levels.equals("")) {
			throw new IllegalArgumentException("Userpromt levels is empty string");
		}
		boolean oneshot = false;
		boolean session = false;
		boolean blanket = false;
		String[] alevels = Splitter.split(levels,',',-1);
		for(int i=0;i<alevels.length;i++) {
			if (alevels[i].equals(ONESHOT_STRING)) { oneshot = true; }
			else if (alevels[i].equals(SESSION_STRING)) { session = true; }
			else if (alevels[i].equals(BLANKET_STRING)) { blanket = true; }
			else throw new IllegalArgumentException("Unknown userprompt level: "+alevels[i]);
		}
		oneshotAllowed = oneshot;
		sessionAllowed = session;
		blanketAllowed = blanket;
				
		if (!defaultLevel.equals("") &&
			!defaultLevel.equals(ONESHOT_STRING)&&
			!defaultLevel.equals(SESSION_STRING)&&
			!defaultLevel.equals(BLANKET_STRING)) 
		{
			throw new IllegalArgumentException("Unknown default userprompt level: "+defaultLevel);
		}
		
		if ((!oneshotAllowed && defaultLevel.equals(ONESHOT_STRING)) ||
			(!sessionAllowed && defaultLevel.equals(SESSION_STRING)) ||
			(!blanketAllowed && defaultLevel.equals(BLANKET_STRING))) 
		{
			throw new IllegalArgumentException("Default userprompt level is not among possible levels: "+defaultLevel);
		}
	}

	public static class Factory implements org.osgi.util.mobile.UserPromptCondition.UserPromptConditionFactory {
		public org.osgi.util.mobile.UserPromptCondition getInstance(Bundle bundle, String levels, String defaultLevel, String catalogName, String message) {
			String id = generateUniqueID(bundle,catalogName,message);
			UserPromptCondition condition = (UserPromptCondition) conditions.get(id);
			if (condition==null) {
				condition = new UserPromptCondition(bundle,levels,defaultLevel,catalogName,message);
				conditions.put(id,condition);
			}
			return condition;
		}
	}

	public static void loadFromStorage() {
		conditions = new HashMap();
	}
	
	public static void saveToStorage() {
		// TODO
	}
	
	public static void deregisterMySelf() {
		org.osgi.util.mobile.UserPromptCondition.setFactory(null);
	}

	public static void registerMySelf() {
		org.osgi.util.mobile.UserPromptCondition.setFactory(new Factory());
	}
}

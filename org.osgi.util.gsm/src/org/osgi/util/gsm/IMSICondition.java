/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package org.osgi.util.gsm;

import java.util.Dictionary;
import org.osgi.framework.Bundle;
import org.osgi.service.condpermadmin.Condition;
import org.osgi.service.condpermadmin.ConditionInfo;

/**
 * Class representing an IMSI condition. Instances of this class contain a
 * string value that is matched against the IMSI of the subscriber.
 */
public class IMSICondition implements Condition {
	// this reference implementation only supports one IMSI, that cannot change
	protected static final String imsi = System.getProperty("org.osgi.util.gsm.imsi");

	private IMSICondition() {
		// this class is never instantiated
		throw new IllegalArgumentException();
	}

	/**
	 * Creates an IMSI condition object.
	 * 
	 * @param bundle ignored, as the IMSI number is the same for all bundles.
	 * @param conditionInfo contains the IMSI value of the subscriber identity. Its
	 * 		{@link ConditionInfo#getArgs()} method should return a String array with one String,
	 * 		the IMSI value. The IMSI must be 15 digits, no hypens.
	 * @throws NullPointerException if one of the parameters is null.
	 * @throws IllegalArgumentException if the imsi is not a string of 15 digits.
	 */
	public static Condition getInstance(Bundle bundle, ConditionInfo conditionInfo) {
		if (bundle==null) throw new NullPointerException("bundle");
		if (conditionInfo==null) throw new NullPointerException("conditionInfo");
		String imsi = conditionInfo.getArgs()[0];
		if (imsi==null) throw new NullPointerException("imsi");
		if (imsi.length()!=15) throw new IllegalArgumentException("not a valid imei: "+imsi);
		for(int i=0;i<imsi.length();i++) {
			int c = imsi.charAt(i);
			if (c<'0'||c>'9') throw new IllegalArgumentException("not a valid imei: "+imsi);
		}
		return (imsi.equals(IMSICondition.imsi))?TRUE:FALSE;
	}

	/**
	 * Checks whether the condition is true. 
	 * 
	 * @return True if the IMSI value in this condition is the same as the IMSI of subscriber.
	 */
	public boolean isSatisfied() {
		// this class is never instantiated
		throw new IllegalStateException();
	}

	/**
	 * Checks whether condition evaluation is postponed, because {@link #isSatisfied()} cannot give answer instantly.
	 * 
	 * @return True if the {@link #isSatisfied()} method cannot give results instantly.
	 */
	public boolean isPostponed() {
		// this class is never instantiated
		throw new IllegalStateException();
	}

	/**
	 * Checks whether the condition can change. On most systems the IMSI cannot change, since the
	 * device need to be powered down to change the SIM card.
	 * 
	 * @return True, if the IMSI can change (mobile device supports live SIM card swaps, etc.)
	 */
	public boolean isMutable() {
		// this class is never instantiated
		throw new IllegalStateException();
	}

	/**
	 * Checks whether an array of IMSI conditions match.
	 * 
	 * @param conds an array, containing only IMSI conditions.
	 * @param context ignored.
	 * @return True, if they all match.
	 * @throws NullPointerException if conds is <code>null</code>.
	 */
	public boolean isSatisfied(Condition[] conds, Dictionary context) {
		// this class is never instantiated
		throw new IllegalStateException();
	}
}
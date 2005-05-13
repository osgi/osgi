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

/**
 * Class representing an IMSI condition. Instances of this class contain a
 * string value that is matched against the IMSI of the subscriber.
 */
public class IMSICondition implements Condition {
	// this reference implementation only supports one IMSI, that cannot change
	protected static final String imsi = System.getProperty("org.osgi.util.gsm.imsi");

	private static final IMSICondition trueCondition = new IMSICondition(true);
	private static final IMSICondition falseCondition = new IMSICondition(false);
	private final boolean satisfied;

	private IMSICondition(boolean satisfied) { this.satisfied = satisfied; }

	/**
	 * Creates an IMSI condition object.
	 * 
	 * @param bundle ignored, as the IMSI number is the same for all bundles.
	 * @param imsi the IMSI value of the subscriber identity.
	 * @throws NullPointerException if one of the parameters is null.
	 * @throws IllegalArgumentException if the imsi is not a string of 15 digits.
	 */
	public static Condition getInstance(Bundle bundle, String imsi) {
		if (bundle==null) throw new NullPointerException("bundle");
		if (imsi==null) throw new NullPointerException("imsi");
		if (imsi.length()!=15) throw new IllegalArgumentException("not a valid imei: "+imsi);
		for(int i=0;i<imsi.length();i++) {
			int c = imsi.charAt(i);
			if (c<'0'||c>'9') throw new IllegalArgumentException("not a valid imei: "+imsi);
		}
		return (imsi.equals(IMSICondition.imsi))?trueCondition:falseCondition;
	}

	/**
	 * Checks whether the condition is true. 
	 * 
	 * @return True if the IMSI value in this condition is the same as the IMSI of subscriber.
	 */
	public boolean isSatisfied() {
		return satisfied;
	}

	/**
	 * Checks whether the condition is evaluated.
	 * 
	 * @return True if the {@link #isSatisfied()} method can give results instantly.
	 */
	public boolean isEvaluated() {
		// this reference implementation only supports one IMSI, that cannot change
		return true;
	}

	/**
	 * Checks whether the condition can change. On most systems the IMSI cannot change, since the
	 * device need to be powered down to change the SIM card.
	 * 
	 * @return True, if the IMSI can change (mobile device supports live SIM card swaps, etc.)
	 */
	public boolean isMutable() {
		// this reference implementation only supports one IMSI, that cannot change
		return false;
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
		// we don't use context
		for(int i=0;i<conds.length;i++) {
			if (!conds[i].isSatisfied()) return false;
		}
		return true;
	}
}
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
 * Class representing an IMEI condition. Instances of this class contain a
 * string value that is matched against the IMEI of the device.
 */
public class IMEICondition implements Condition {
	protected static final String imei = System.getProperty("org.osgi.util.gsm.imei");
	private static final IMEICondition trueCondition = new IMEICondition(true);
	private static final IMEICondition falseCondition = new IMEICondition(false);

	private final boolean satisfied;
	
	// the default constructor is public
	protected IMEICondition(boolean match) { satisfied = match; }

	/**
	 * Creates an IMEICondition object.
	 * 
	 * @param bundle ignored, as the IMEI number is the property of the mobile device,
	 * 					and thus the same for all bundles.
	 * @param imei the IMEI value to match the device's IMEI against. Must be 15 digits, no hypens.
	 * @return An IMEICondition object, that can tell whether its IMEI number matches that of the device.
	 * @throws NullPointerException if one of the parameters is <code>null</code>.
	 * @throws IllegalArgumentException if the IMEI is not a string of 15 digits.
	 */
	public static Condition getInstance(Bundle bundle, String imei) {
		if (bundle==null) throw new NullPointerException("bundle");
		if (imei==null) throw new NullPointerException("imei");
		if (imei.length()!=15) throw new IllegalArgumentException("not a valid imei: "+imei);
		for(int i=0;i<imei.length();i++) {
			int c = imei.charAt(i);
			if (c<'0'||c>'9') throw new IllegalArgumentException("not a valid imei: "+imei);
		}
		return imei.equals(IMEICondition.imei)?trueCondition:falseCondition;
	}

	/**
	 * Checks whether IMEI value in the condition and of the device match.
	 * 
	 * @return True if the IMEI value in this condition is the same as the IMEI of the device.
	 */
	public boolean isSatisfied() {
		return satisfied;
	}

	/**
	 * Checks whether the condition is evaluated.
	 * 
	 * @return True if the {@link #isSatisfied()} method can give results instantly.
	 */
	public boolean isPostponed() {
		return false;
	}

	/**
	 * Checks whether the condition can change. As an IMEI number is factory-set in the
	 * device, this condition is not mutable.
	 * 
	 * @return Always false.
	 */
	public boolean isMutable() {
		return false;
	}

	/**
	 * Checks an array of IMEI conditions if they all match this device.
	 * 
	 * @param conds an array, containing only IMEIConditions.
	 * @param context ignored.
	 * @return True if all conditions match.
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

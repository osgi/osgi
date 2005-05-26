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
 * Class representing an IMEI condition. Instances of this class contain a
 * string value that is matched against the IMEI of the device.
 */
public class IMEICondition implements Condition {
	protected static final String imei = System.getProperty("org.osgi.util.gsm.imei");
	
	private IMEICondition() {
		// this class is never instantiated
		throw new IllegalArgumentException();
	}

	/**
	 * Creates an IMEICondition object.
	 * 
	 * @param bundle ignored, as the IMEI number is the property of the mobile device,
	 * 					and thus the same for all bundles.
	 * @param conditionInfo contains the IMEI value to match the device's IMEI against. Its
	 * 		{@link ConditionInfo#getArgs()} method should return a String array with one value, the
	 * 		IMEI string. The IMEI  must be 15 digits, no hypens.
	 * @return An IMEICondition object, that can tell whether its IMEI number matches that of the device.
	 * @throws NullPointerException if one of the parameters is <code>null</code>.
	 * @throws IllegalArgumentException if the IMEI is not a string of 15 digits.
	 */
	public static Condition getInstance(Bundle bundle, ConditionInfo conditionInfo) {
		if (bundle==null) throw new NullPointerException("bundle");
		String imei = conditionInfo.getArgs()[0];
		if (imei==null) throw new NullPointerException("imei");
		if (imei.length()!=15) throw new IllegalArgumentException("not a valid imei: "+imei);
		for(int i=0;i<imei.length();i++) {
			int c = imei.charAt(i);
			if (c<'0'||c>'9') throw new IllegalArgumentException("not a valid imei: "+imei);
		}
		return imei.equals(IMEICondition.imei)?TRUE:FALSE;
	}

	/**
	 * Checks whether IMEI value in the condition and of the device match.
	 * 
	 * @return True if the IMEI value in this condition is the same as the IMEI of the device.
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
	 * Checks whether the condition can change. As an IMEI number is factory-set in the
	 * device, this condition is not mutable.
	 * 
	 * @return Always false.
	 */
	public boolean isMutable() {
		// this class is never instantiated
		throw new IllegalStateException();
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
		// this class is never instantiated
		throw new IllegalStateException();
	}
}

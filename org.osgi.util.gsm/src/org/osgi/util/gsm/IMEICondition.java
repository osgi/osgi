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

import java.security.*;

import org.osgi.framework.Bundle;
import org.osgi.service.condpermadmin.*;

/**
 * Class representing an IMEI condition. Instances of this class contain a
 * string value that is matched against the IMEI of the device.
 */
public class IMEICondition {
	private static final String ORG_OSGI_UTIL_GSM_IMEI = "org.osgi.util.gsm.imei";
	private static String imei ;
		
	static {
		AccessController.doPrivileged(
				new PrivilegedAction() {
					public Object run() {
					imei = System.getProperty(ORG_OSGI_UTIL_GSM_IMEI);
					return null;
					}
				}
				);
	}
	
	private IMEICondition() {
	}

	/**
	 * Creates an IMEICondition object.
	 * 
	 * @param bundle ignored, as the IMEI number is the property of the mobile device,
	 * 					and thus the same for all bundles.
	 * @param conditionInfo contains the IMEI value to match the device's IMEI against. Its
	 * 		{@link ConditionInfo#getArgs()} method should return a String array with one value, the
	 * 		IMEI string. The IMEI is 15 digits without hypens. Limited pattern matching is allowed,
	 * 		then the string is 0 to 14 digits, followed by an asterisk(<code>*</code>).
	 * @return An IMEICondition object, that can tell whether its IMEI number matches that of the device.
	 * 			If the number contains an asterisk(<code>*</code>), then the beginning
	 * 			of the imei is compared to the pattern.
	 * @throws NullPointerException if one of the parameters is <code>null</code>.
	 * @throws IllegalArgumentException if the IMEI is not a string of 15 digits, or 
	 * 		0 to 14 digits with an <code>*</code> at the end.
	 */
	public static Condition getCondition(Bundle bundle, ConditionInfo conditionInfo) {
		if (bundle==null) throw new NullPointerException("bundle");
		String imei = conditionInfo.getArgs()[0];
		if (imei.length()>15) throw new IllegalArgumentException("imei too long: "+imei);
		if (imei.endsWith("*")) {
			imei = imei.substring(0,imei.length()-1);
		} else {
			if (imei.length()!=15) throw new IllegalArgumentException("not a valid imei: "+imei);
		}
		for(int i=0;i<imei.length();i++) {
			int c = imei.charAt(i);
			if (c<'0'||c>'9') throw new IllegalArgumentException("not a valid imei: "+imei);
		}
		if (IMEICondition.imei==null) {
			System.err.println("The OSGI Reference Implementation of org.osgi.util.gsm.IMEICondition ");
			System.err.println("needs the system property "+ORG_OSGI_UTIL_GSM_IMEI+" set.");
			return Condition.FALSE;
		}
		return IMEICondition.imei.startsWith(imei)?Condition.TRUE:Condition.FALSE;
	}
}

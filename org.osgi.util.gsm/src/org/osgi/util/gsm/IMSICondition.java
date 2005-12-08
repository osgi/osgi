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
 * Class representing an IMSI condition. Instances of this class contain a
 * string value that is matched against the IMSI of the subscriber.
 */
public class IMSICondition {
	private static final String ORG_OSGI_UTIL_GSM_IMSI = "org.osgi.util.gsm.imsi";
	private static String imsi;
	
	static {
		AccessController.doPrivileged(
				new PrivilegedAction() {
					public Object run() {
					imsi = System.getProperty(ORG_OSGI_UTIL_GSM_IMSI);
					return null;
					}
				}
				);
	}

	private IMSICondition() {}

	/**
	 * Creates an IMSI condition object.
	 * 
	 * @param bundle ignored, as the IMSI number is the same for all bundles.
	 * @param conditionInfo contains the IMSI value to match the device's IMSI against. Its
	 * 		{@link ConditionInfo#getArgs()} method should return a String array with one value, the
	 * 		IMSI string. The IMSI is 15 digits without hypens. Limited pattern matching is allowed,
	 * 		then the string is 0 to 14 digits, followed by an asterisk(<code>*</code>).
	 * @return An IMSICondition object, that can tell whether its IMSI number matches that of the device.
	 * 			If the number contains an asterisk(<code>*</code>), then the beginning
	 * 			of the IMSI is compared to the pattern.
	 * @throws NullPointerException if one of the parameters is <code>null</code>.
	 * @throws IllegalArgumentException if the IMSI is not a string of 15 digits, or 
	 * 		0 to 14 digits with an <code>*</code> at the end.
	 */
	public static Condition getCondition(Bundle bundle, ConditionInfo conditionInfo) {
		if (bundle==null) throw new NullPointerException("bundle");
		if (conditionInfo==null) throw new NullPointerException("conditionInfo");
		String imsi = conditionInfo.getArgs()[0];
		if (imsi.length()>15) throw new IllegalArgumentException("imsi too long: "+imsi);
		if (imsi.endsWith("*")) {
			imsi = imsi.substring(0,imsi.length()-1);
		} else {
			if (imsi.length()!=15) throw new IllegalArgumentException("not a valid imei: "+imsi);
		}
		for(int i=0;i<imsi.length();i++) {
			int c = imsi.charAt(i);
			if (c<'0'||c>'9') throw new IllegalArgumentException("not a valid imei: "+imsi);
		}
		if (IMSICondition.imsi==null) {
			System.err.println("The OSGI Reference Implementation of org.osgi.util.gsm.IMSICondition ");
			System.err.println("needs the system property "+ORG_OSGI_UTIL_GSM_IMSI+" set.");
			return Condition.FALSE;
		}
		return (IMSICondition.imsi.startsWith(imsi))?Condition.TRUE:Condition.FALSE;
	}
}
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
	private static String imsi;
	
	static {
		AccessController.doPrivileged(
				new PrivilegedAction() {
					public Object run() {
					imsi = System.getProperty("org.osgi.util.gsm.imsi");
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
	 * @param conditionInfo contains the IMSI value of the subscriber identity. Its
	 * 		{@link ConditionInfo#getArgs()} method should return a String array with one String,
	 * 		the IMSI value. The IMSI must be 15 digits, no hypens.
	 * @throws NullPointerException if one of the parameters is null.
	 * @throws IllegalArgumentException if the imsi is not a string of 15 digits.
	 */
	public static Condition getCondition(Bundle bundle, ConditionInfo conditionInfo) {
		if (bundle==null) throw new NullPointerException("bundle");
		if (conditionInfo==null) throw new NullPointerException("conditionInfo");
		String imsi = conditionInfo.getArgs()[0];
		if (imsi==null) throw new NullPointerException("imsi");
		if (imsi.length()!=15) throw new IllegalArgumentException("not a valid imei: "+imsi);
		for(int i=0;i<imsi.length();i++) {
			int c = imsi.charAt(i);
			if (c<'0'||c>'9') throw new IllegalArgumentException("not a valid imei: "+imsi);
		}
		return (imsi.equals(IMSICondition.imsi))?Condition.TRUE:Condition.FALSE;
	}
}
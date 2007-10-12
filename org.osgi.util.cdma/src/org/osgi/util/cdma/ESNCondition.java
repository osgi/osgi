/*
 * Copyright (c) OSGi Alliance (2004, 2006). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.util.cdma;

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.osgi.framework.Bundle;
import org.osgi.service.condpermadmin.Condition;
import org.osgi.service.condpermadmin.ConditionInfo;

/**
 * Class representing an ESN condition. Instances of this class contain a
 * string value that is matched against the ESN of the device.
 */
public class ESNCondition {
	private static final String ORG_OSGI_UTIL_CDMA_ESN = "org.osgi.util.cdma.esn";
	private static final String esn ;
		
	static {
		esn = (String)
		AccessController.doPrivileged(
				new PrivilegedAction() {
					public Object run() {
					String esn = System.getProperty(ORG_OSGI_UTIL_CDMA_ESN);
					return (null==esn)?esn:esn.toUpperCase();
					}
				}
				);
	}
	
	private ESNCondition() {
	}
	
	private static final String HEXDIGIT = "0123456789ABCDEF";

	/**
	 * Creates an ESNCondition object.
	 * 
	 * @param bundle ignored, as the ESN number is the property of the mobile device,
	 * 					and thus the same for all bundles.
	 * @param conditionInfo contains the ESN value to match the device's ESN against. Its
	 * 		{@link ConditionInfo#getArgs()} method should return a String array with one value, the
	 * 		ESN string. The ESN is 8 hexadecimal digits (32 bits) without hypens. Limited pattern matching is allowed,
	 * 		then the string is 0 to 7 digits, followed by an asterisk(<code>*</code>).
	 * @return An ESNCondition object, that can tell whether its ESN number matches that of the device.
	 * 			If the number contains an asterisk(<code>*</code>), then the beginning
	 * 			of the ESN is compared to the pattern.
	 * @throws NullPointerException if one of the parameters is <code>null</code>.
	 * @throws IllegalArgumentException if the ESN is not a string of 8 hexadecimal digits, or 
	 * 		0 to 7 hexadecimal digits with an <code>*</code> at the end.
	 */
	public static Condition getCondition(Bundle bundle, ConditionInfo conditionInfo) {
		if (bundle==null) throw new NullPointerException("bundle");
		String esn = conditionInfo.getArgs()[0].toUpperCase();
		if (esn.length()>8) throw new IllegalArgumentException("ESN too long: "+esn);
		if (esn.endsWith("*")) {
			esn = esn.substring(0,esn.length()-1);
		} else {
			if (esn.length()<8) throw new IllegalArgumentException("not a valid ESN: "+esn);
		}
		for(int i=0;i<esn.length();i++) {
			int c = esn.charAt(i);
			if (HEXDIGIT.indexOf(c)<0) throw new IllegalArgumentException("not a valid ESN: "+esn);
		}
		if (ESNCondition.esn==null) {
			System.err.println("The OSGi Reference Implementation of org.osgi.util.cdma.ESNCondition ");
			System.err.println("needs the system property "+ORG_OSGI_UTIL_CDMA_ESN+" set.");
			return Condition.FALSE;
		}
		return ESNCondition.esn.startsWith(esn)?Condition.TRUE:Condition.FALSE;
	}
}

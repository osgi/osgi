/*
 * Copyright (c) OSGi Alliance (2004, 2008). All Rights Reserved.
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
 * Class representing an MEID condition. Instances of this class contain a
 * string value that is matched against the MEID of the device.
 * 
 * @version $Revision$
 */
public class MEIDCondition {
	private static final String ORG_OSGI_UTIL_CDMA_MEID = "org.osgi.util.cdma.meid";
	private static final String meid ;
		
	static {
		meid = (String)
		AccessController.doPrivileged(
				new PrivilegedAction() {
					public Object run() {
					String meid = System.getProperty(ORG_OSGI_UTIL_CDMA_MEID);
					return (null==meid)?meid:meid.toUpperCase();
					}
				}
				);
	}
	
	private MEIDCondition() {
	}
	
	private static final String HEXDIGIT = "0123456789ABCDEF";

	/**
	 * Creates a MEIDCondition object.
	 * 
	 * @param bundle ignored, as the MEID number is the property of the mobile device,
	 * 					and thus the same for all bundles.
	 * @param conditionInfo contains the MEID value to match the device's MEID against. Its
	 * 		{@link ConditionInfo#getArgs()} method should return a String array with one value, the
	 * 		MEID string. The MEID is 14 hexadecimal digits (56 bits) without hypens. Limited pattern 
	 *      matching is allowed, then the string is 0 to 13 digits, followed by an asterisk(<code>*</code>).
	 * @return A MEIDCondition object, that can tell whether its MEID number matches that of the device.
	 * 			If the number contains an asterisk(<code>*</code>), then the beginning
	 * 			of the MEID is compared to the pattern.
	 * @throws NullPointerException if one of the parameters is <code>null</code>.
	 * @throws IllegalArgumentException if the MEID is not a string of 14 hexadecimal digits, or 
	 * 		0 to 13 hexadecimal digits with an <code>*</code> at the end.
	 */
	public static Condition getCondition(Bundle bundle, ConditionInfo conditionInfo) {
		if (bundle==null) throw new NullPointerException("bundle");
		String meid = conditionInfo.getArgs()[0].toUpperCase();
		if (meid.length()>14) throw new IllegalArgumentException("MEID too long: "+meid);
		if (meid.endsWith("*")) {
			meid = meid.substring(0,meid.length()-1);
		} else {
			if (meid.length()<14) throw new IllegalArgumentException("not a valid MEID: "+meid);
		}
		for(int i=0;i<meid.length();i++) {
			int c = meid.charAt(i);
			if (HEXDIGIT.indexOf(c)<0) throw new IllegalArgumentException("not a valid MEID: "+meid);
		}
		if (MEIDCondition.meid==null) {
			System.err.println("The OSGi Reference Implementation of org.osgi.util.cdma.MEIDCondition ");
			System.err.println("needs the system property "+ORG_OSGI_UTIL_CDMA_MEID+" set.");
			return Condition.FALSE;
		}
		return MEIDCondition.meid.startsWith(meid)?Condition.TRUE:Condition.FALSE;
	}
}

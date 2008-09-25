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
package org.osgi.util.gsm;

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.osgi.framework.Bundle;
import org.osgi.service.condpermadmin.Condition;
import org.osgi.service.condpermadmin.ConditionInfo;

/**
 * Class representing an IMEI condition. Instances of this class contain a
 * string value that is matched against the IMEI of the device.
 * 
 * @ThreadSafe
 * @version $Revision$
 */
public class IMEICondition {
	private static final String ORG_OSGI_UTIL_GSM_IMEI = "org.osgi.util.gsm.imei";
	private static final String	imei;
		
	static {
		imei = (String)
		AccessController.doPrivileged(
				new PrivilegedAction() {
					public Object run() {
					return System.getProperty(ORG_OSGI_UTIL_GSM_IMEI);
					}
				}
				);
	}
	
	private IMEICondition() {
	}

	/**
	 * Creates an IMEI condition object.
	 * 
	 * @param bundle This parameter is ignored, as the IMEI number is a property
	 *        of the mobile device and thus is the same for all bundles.
	 * @param conditionInfo Contains the IMEI value against which to match the
	 *        device's IMEI. Its {@link ConditionInfo#getArgs()} method should
	 *        return a String array with one value: the IMEI string. The IMEI is
	 *        15 digits without hyphens. Limited pattern matching is allowed:
	 *        the string is 0 to 14 digits, followed by an asterisk (
	 *        <code>*</code>).
	 * @return A Condition object that indicates whether the specified IMEI
	 *         number matches that of the device. If the number contains an
	 *         asterisk ( <code>*</code>), then the beginning of the IMEI is
	 *         compared to the pattern.
	 * @throws IllegalArgumentException If the IMEI is not a string of 15
	 *         digits, or 0 to 14 digits with an <code>*</code> at the end.
	 */
	public static Condition getCondition(Bundle bundle, ConditionInfo conditionInfo) {
		String imei = conditionInfo.getArgs()[0];
		if (imei.length()>15) throw new IllegalArgumentException("imei too long: "+imei);
		if (imei.endsWith("*")) {
			imei = imei.substring(0,imei.length()-1);
		} else {
			if (imei.length() != 15) {
				throw new IllegalArgumentException("not a valid imei: " + imei);
			}
		}
		for (int i = 0; i < imei.length(); i++) {
			int c = imei.charAt(i);
			if (c < '0' || c > '9') {
				throw new IllegalArgumentException("not a valid imei: " + imei);
			}
		}
		if (IMEICondition.imei==null) {
			System.err.println("The OSGi Reference Implementation of org.osgi.util.gsm.IMEICondition ");
			System.err.println("needs the system property "+ORG_OSGI_UTIL_GSM_IMEI+" set.");
			return Condition.FALSE;
		}
		return IMEICondition.imei.startsWith(imei) ? Condition.TRUE
				: Condition.FALSE;
	}
}

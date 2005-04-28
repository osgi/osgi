/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
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
	public boolean isEvaluated() {
		return true;
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

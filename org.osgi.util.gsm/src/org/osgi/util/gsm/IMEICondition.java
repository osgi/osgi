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
	 * Creates an IMEI object.
	 * 
	 * @param bundle ignored
	 * @param imei The IMEI value of the device.
	 */
	public static Condition getInstance(Bundle bundle, String imei) {
		return imei.equals(IMEICondition.imei)?trueCondition:falseCondition;
	}

	/**
	 * Checks whether the condition is satisfied. The IMEI of the object
	 * instance is compared against the IMEI of the device.
	 * 
	 * @return true if the IMEI value matches.
	 */
	public boolean isSatisfied() {
		return satisfied;
	}

	/**
	 * Checks whether the condition is evaluated.
	 * 
	 * @return if the condition status can be evaluated instantly
	 */
	public boolean isEvaluated() {
		return true;
	}

	/**
	 * check whether the condition can change.
	 * 
	 * @return always false
	 */
	public boolean isMutable() {
		return false;
	}

	/**
	 * Checks for an array of IMEI conditions if they all match this device
	 * 
	 * @param conds an array, containing only IMEI conditions
	 * @param context ignored
	 * @return true if all conditions match
	 */
	public boolean isSatisfied(Condition[] conds, Dictionary context) {
		// we don't use context
		for(int i=0;i<conds.length;i++) {
			if (!conds[i].isSatisfied()) return false;
		}
		return true;
	}
}
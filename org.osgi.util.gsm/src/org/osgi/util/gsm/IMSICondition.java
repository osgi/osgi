/*
 * Copyright (c) The Open Services Gateway Initiative (2004).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.util.gsm;

import java.util.Dictionary;
import org.osgi.framework.Bundle;
import org.osgi.service.condpermadmin.Condition;

/**
 * Class representing an IMSI condition. Instances of this class contain a
 * string value that is matched against the IMSI of the subscriber.
 */
public class IMSICondition implements Condition {
	protected static final String imsi = System.getProperty("org.osgi.util.gsm.imsi");

	private static final IMSICondition trueCondition = new IMSICondition();
	private static final IMSICondition falseCondition = new IMSICondition();

	private IMSICondition() {}

	/**
	 * Creates an IMSI condition object
	 * 
	 * @param bundle ignored
	 * @param imsi The IMSI value of the subscriber.
	 */
	public static Condition getInstance(Bundle bundle, String imsi) {
		return (imsi.equals(IMSICondition.imsi))?trueCondition:falseCondition;
	}

	/**
	 * Checks whether the condition is true. The IMSI of the object instance is
	 * compared against the IMSI of the device's user.
	 * 
	 * @return true if the IMSI value match.
	 */
	public boolean isSatisfied() {
		return (this==trueCondition);
	}

	/**
	 * Checks whether the condition is evaluated
	 * 
	 * @return always true
	 */
	public boolean isEvaluated() {
		return true;
	}

	/**
	 * Checks whether the condition can change
	 * 
	 * @return always false
	 */
	public boolean isMutable() {
		return false;
	}

	/**
	 * Checks whether an array of IMSI conditions match
	 * 
	 * @param conds an array, containing only IMSI conditions
	 * @param context ignored
	 * @return true, if they all match
	 */
	public boolean isSatisfied(Condition[] conds, Dictionary context) {
		// we don't use context
		for(int i=0;i<conds.length;i++) {
			if (!conds[i].isSatisfied()) return false;
		}
		return true;
	}
}
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
package org.osgi.util.mobile;

import java.util.Dictionary;
import org.osgi.framework.Bundle;
import org.osgi.service.condpermadmin.Condition;

/**
 * Class representing abstract transfer cost values. Implementation assigns
 * concrete bearers to abstract transfer costs. <br>
 * There is a static setTransferCost() method that sets the current transfer
 * cost limit in a thread local variable. The TransferCost condition will check
 * this, and will fail if the cost is greater. The setTransferCost() and
 * resetTransferCost() methods behave stack-like, putting and removing costs
 * from a stack. The limit is always the value on the top. This way nested
 * permission checks can be implemented. It is recommended that
 * resetTransferCost() is always put in a finally block, to ensure proper
 * stacking behavior.
 */
public class TransferCostCondition implements Condition {
	private static ThreadLocal	context	= new ThreadLocal();

	/**
	 * Creates a TransferCost object. This constructor is intended to be called
	 * when Permission Admin initializes the object.
	 * 
	 * @param bundle ignored
	 * @param cost The abstract limit cost. Possible values are "LOW","MEDIUM"
	 *        and "HIGH".
	 */
	public TransferCostCondition(Bundle bundle, String cost) {
	}

	/**
	 * Sets a thread-local transfer cost limit. All isSatisfied() method calls
	 * in this thread will check for this limit. The caller MUST call
	 * resetTransferCost(), after the permission checks are done. If this
	 * function is not called, the default behavior is 'no limit'.
	 * 
	 * @param cost the upper limit of transfer cost
	 */
	public static void setTransferCost(int cost) {
	}

	/**
	 * Resets the transfer cost to the previous value.
	 */
	public static void resetTransferCost() {
	}

	/**
	 * Checks whether the condition is satisfied. The limit cost represented by
	 * this object instance is compared against the cost in the thread context.
	 * 
	 * @return true if the cost in this condition is greater or equals the cost
	 *         limit set in thread context
	 */
	public boolean isSatisfied() {
		return true;
	}

	/**
	 * Checks whether the condition is evaluated.
	 * 
	 * @return always false
	 */
	public boolean isEvaluated() {
		return false;
	}

	/**
	 * check whether the condition can change.
	 * 
	 * @return always true
	 */
	public boolean isMutable() {
		return true;
	}

	/**
	 * Checks for an array of TransferCost conditions if they all match
	 * 
	 * @param conds an array, containing only TransferCost conditions
	 * @param context ignored
	 * @return true if all transfer costs are below limit
	 */
	public boolean isSatisfied(Condition[] conds, Dictionary context) {
		return false;
	}
}
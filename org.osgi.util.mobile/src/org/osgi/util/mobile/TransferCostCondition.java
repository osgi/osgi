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
package org.osgi.util.mobile;

import java.util.Dictionary;
import org.osgi.framework.Bundle;
import org.osgi.service.condpermadmin.Condition;

/**
 * Class representing abstract transfer cost values. Implementation assigns
 * concrete bearers to abstract transfer costs. <br>
 * There is a static setTransferCost() method that sets the current transfer
 * cost in a thread local variable. The TransferCost condition will check
 * this, and will fail if the cost is greater. It is recommended that
 * resetTransferCost() is always put in a finally block, to ensure that if
 * something fails, the cost value is removed.
 */
public class TransferCostCondition implements Condition {
	private static ThreadLocal	context	= new ThreadLocal();

	public static final String LOW = "LOW";
	public static final String MEDIUM = "MEDIUM";
	public static final String HIGH = "HIGH";

	private static final TransferCostCondition lowCostLimit = new TransferCostCondition();
	private static final TransferCostCondition mediumCostLimit = new TransferCostCondition();
	private static final TransferCostCondition highCostLimit = new TransferCostCondition();

	// default constructor is public, and we don't want that
	private TransferCostCondition() {};

	/**
	 * Creates a TransferCostCondition object. This constructor is intended to be called
	 * when Permission Admin initializes the object.
	 * 
	 * @param bundle ignored
	 * @param costLimit The abstract limit cost. Possible values are "LOW","MEDIUM"
	 *        and "HIGH". 
	 */
	public static Condition getInstance(Bundle bundle, String costLimit) {
		if (costLimit.equals("LOW")) return lowCostLimit;
		if (costLimit.equals("MEDIUM")) return mediumCostLimit;
		if (costLimit.equals("HIGH")) return highCostLimit;
		throw new IllegalArgumentException("unknown costLimit: "+costLimit);
	}

	/**
	 * Sets a thread-local transfer cost. All isSatisfied() method calls
	 * in this thread will check for this cost, and only those permissions will be
	 * activated, that have a cost limit higher than this. The caller MUST call
	 * resetTransferCost(), after the permission checks are done. If this
	 * function is not called, the default behavior is 'no checks permformed, all tests
	 * succeed'.
	 * 
	 * @param cost the cost of the current transaction. Only those conditions will evaluate to true,
	 * 			that are equal or higher than this.
	 */
	public static void setTransferCost(String cost) {
		if (cost==null) { context.set(null); return; }
		if (LOW.equals(cost)) { context.set(LOW); return; }
		if (MEDIUM.equals(cost)) { context.set(MEDIUM); return; }
		if (HIGH.equals(cost)) { context.set(HIGH); return; }

		throw new IllegalArgumentException("unknown cost: "+cost);
	}

	/**
	 * Resets the transfer cost. After this, the transfer cost checks always succeed.
	 */
	public static void resetTransferCost() {
		context.set(null);
	}

	/**
	 * Gets the current transfer cost.
	 * @return the transfer cost value, or null
	 */
	protected static String getTransferCost() {
		return (String) context.get();
	}

	/**
	 * Checks whether the condition is satisfied. The limit cost represented by
	 * this object instance is compared against the cost in the thread context.
	 * 
	 * @return true if the cost in this condition is greater or equals the cost
	 *         limit set in thread context
	 */
	public boolean isSatisfied() {
		String cost = getTransferCost();
		if (cost==null) return true;
		if (this==highCostLimit) return true;
		if (this==lowCostLimit) {
			return cost==LOW;
		}
		if (this==mediumCostLimit) {
			return cost==LOW || cost==MEDIUM;
		}
		throw new IllegalStateException("unknown enum for cost created: "+cost);
	}

	/**
	 * Checks whether the condition is evaluated.
	 * 
	 * @return true if the condition status can be evaluated instantly
	 */
	public boolean isEvaluated() {
		return true;
	}

	/**
	 * check whether the condition can change.
	 * 
	 * @return true if the condition can change
	 */
	public boolean isMutable() {
		if (this==highCostLimit) return false;
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
		// we don't use context
		for(int i=0;i<conds.length;i++) {
			if (!conds[i].isSatisfied()) return false;
		}
		return true;
	}
}
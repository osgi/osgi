/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package org.osgi.util.mobile;

import java.util.Dictionary;
import org.osgi.framework.Bundle;
import org.osgi.service.condpermadmin.Condition;

/**
 * Class representing abstract transfer cost values. Implementation assigns
 * concrete bearers to abstract transfer costs. <br>
 * There is a static {@link #setTransferCost(String)} method that sets the current transfer
 * cost in a thread local variable. The TransferCost condition will check
 * this, and will fail if the cost is greater. It is recommended to
 * always put {@link #resetTransferCost()} in a finally block, to ensure that if
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
	 * when {@link org.osgi.service.condpermadmin.ConditionalPermissionAdmin Conditional Permission Admin} initializes the object.
	 * 
	 * @param bundle ignored.
	 * @param costLimit the abstract limit cost. Possible values are <code>"LOW"</code>,<code>"MEDIUM"</code>
	 *        and <code>"HIGH"</code>.
	 * @return A TransferCostCondition object with the specified costLimit.
	 * @throws IllegalArgumentException if the costLimit parameter is not from the possible values.
	 * @throws NullPointerException if one of the parameters is <code>null</code>.
	 */
	public static Condition getInstance(Bundle bundle, String costLimit) {
		if (costLimit==null) throw new NullPointerException("costLimit");
		if (bundle==null) throw new NullPointerException("bundle");
		if (costLimit.equals("LOW")) return lowCostLimit;
		if (costLimit.equals("MEDIUM")) return mediumCostLimit;
		if (costLimit.equals("HIGH")) return highCostLimit;
		throw new IllegalArgumentException("unknown costLimit: "+costLimit);
	}

	/**
	 * Sets a thread-local transfer cost. All {@link #isSatisfied()} method calls
	 * in this thread will check for this cost, and only those permissions will be
	 * activated, that have a cost limit higher than the actual cost. The caller MUST call
	 * {@link #resetTransferCost()}, after the permission checks are done. If this
	 * function is not called, the default behavior is 'no checks permformed, isSatisfied method
	 * always returns true'.
	 * 
	 * @param cost the cost of the current transaction. Only those conditions will evaluate to true,
	 * 			that are equal or higher than this.
	 * @throws IllegalArgumentException if the cost parameter is not from the possible values.
	 */
	public static void setTransferCost(String cost) {
		if (cost==null) { context.set(null); return; }
		if (LOW.equals(cost)) { context.set(LOW); return; }
		if (MEDIUM.equals(cost)) { context.set(MEDIUM); return; }
		if (HIGH.equals(cost)) { context.set(HIGH); return; }

		throw new IllegalArgumentException("unknown cost: "+cost);
	}

	/**
	 * Resets the transfer cost. After this, the {@link #isSatisfied()} calls in the current thread return true,
	 * regardless of what the cost limit for the given condition is.
	 */
	public static void resetTransferCost() {
		context.set(null);
	}

	/**
	 * Gets the current transfer cost.
	 * @return The transfer cost value, or <code>null</code> if there is none set (like, after calling {@link #resetTransferCost()}).
	 */
	protected static String getTransferCost() {
		return (String) context.get();
	}

	/**
	 * Checks whether the current transfer cost is smaller than the cost limit set.
	 * 
	 * @return True if the cost in this condition is greater or equals the cost
	 *         limit set in {@link TransferCostCondition#setTransferCost(String)}.
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
		// extreme paranoia code, this should never happen
		throw new IllegalStateException("unknown enum for cost created: "+cost);
	}

	/**
	 * Checks whether the condition is evaluated, so {@link #isSatisfied()} can give answer instantly.
	 * 
	 * @return Always true.
	 */
	public boolean isPostponed() {
		return false;
	}

	/**
	 * Checks whether the condition can change.
	 * 
	 * @return False, if the costLimit is HIGH, true if the costLimit is MEDIUM or LOW. 
	 */
	public boolean isMutable() {
		if (this==highCostLimit) return false;
		return true;
	}

	/**
	 * Checks for an array of TransferCost conditions if they are all satisfied.
	 * 
	 * @param conds an array, containing only TransferCost conditions.
	 * @param context ignored.
	 * @return True, if all TransferCost conditions have a cost limit that are greater or equal than
	 * 	the current transfer cost.
	 */
	public boolean isSatisfied(Condition[] conds, Dictionary context) {
		// we don't use context
		for(int i=0;i<conds.length;i++) {
			if (!conds[i].isSatisfied()) return false;
		}
		return true;
	}
}

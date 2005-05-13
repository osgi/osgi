/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.service.condpermadmin;

import java.util.Dictionary;

/**
 * This interface is used to implement Conditions that are bound to Permissions
 * using ConditionalPermissionCollection. The Permissions of the
 * ConditionalPermissionCollection can only be used if the associated Condition
 * is satisfied.
 */
public interface Condition {
	/**
	 * This method returns true if the Condition has already been evaluated, and
	 * its satisfiability can be determined from its internal state. In other
	 * words, isSatisfied() will return very quickly since no external sources,
	 * such as users, need to be consulted.
	 */
	boolean isEvaluated();

	/**
	 * This method returns true if the Condition is satisfied.
	 */
	boolean isSatisfied();

	/**
	 * This method returns true if the satisfiability may change.
	 */
	boolean isMutable();

	/**
	 * This method returns true if the set of Conditions are satisfied. Although
	 * this method is not static, it should be implemented as if it were static.
	 * All of the passed Conditions will have the same type and will correspond
	 * to the class type of the object on which this method is invoked.
	 *
	 * @param conds the array of Conditions that must be satisfied
	 * @param context a Dictionary object that implementors can use to track 
	 * state. If this method is invoked multiple times in the same permission 
	 * evaluation, the same Dictionary will be passed multiple times. The
	 * SecurityManager treats this Dictionary as an opaque object simply
	 * creates an empty dictionary and passes it to subsequent invocations
	 * if multiple invocatios are needed.
	 * @return true if all the Conditions are satisfied.
	 */
	boolean isSatisfied(Condition conds[], Dictionary context);
}

/*
 * $Header$
 *
 * Copyright (c) The Open Services Gateway Initiative (2001, 2002).
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

package org.osgi.service.condpermadmin;

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
	 */
	boolean isSatisfied(Condition conds[]);
}

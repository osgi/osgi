/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
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
package org.osgi.service.condpermadmin;

import java.util.Dictionary;
import java.util.Hashtable;
import org.eclipse.osgi.framework.internal.core.AbstractBundle;
import org.eclipse.osgi.framework.internal.core.FilterImpl;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.osgi.framework.Bundle;
import org.osgi.framework.InvalidSyntaxException;

/**
 * This Condition evalutes the symbolic name and version of a bundle.
 * There are two constructors. One takes a BundleSymbolicName that may have
 *  `*' to do wildcarding as defined in * LDAP queries ?[5]. The second constructor
 * adds is the version range as defined in section 5.4 of RFC 79. This 
 * condition is immutable since the bundle symbolic name cannot change after
 * the bundle has been installed.
 * 
 * @version $Revision$
 */
public class BundleSymbolicNameCondition implements Condition {
	private boolean	satisfied;

	/**
	 * Checks the symbolic name of the given bundle against the given expression.
	 * @param bundle the bundle to be checked.
	 * @param nameExp the expression to check against the bundle symbolic name. The
	 * expression is defined in ITEF RFC 2253. Any special characters must be escaped
	 * as specified in the RFC.
	 * @throws InvalidSyntaxException
	 */
	public BundleSymbolicNameCondition(Bundle bundle, String nameExp) throws InvalidSyntaxException {
		FilterImpl filter = new FilterImpl("(name="+nameExp+")");
		Hashtable dict = new Hashtable(1);
		dict.put("name", bundle.getSymbolicName());
		satisfied = filter.match(dict);
	}

	/**
	 * Checks the symbolic name and the version of the given bundle against the
	 * given expressions.
	 * @param bundle the bundle to be checked.
	 * @param nameExp the expression to check against the bundle symbolic name. The
	 * expression is defined in ITEF RFC 2253. Any special characters must be escaped
	 * as specified in the RFC.
	 * @param versionExp specifies the version range of the bundle that will satisfy
	 * this condition.
	 * @throws InvalidSyntaxException
	 */
	public BundleSymbolicNameCondition(Bundle bundle, String nameExp,
			String versionExp) throws InvalidSyntaxException {
		this(bundle, nameExp);
		if (satisfied) {
			AbstractBundle realBundle = (AbstractBundle) bundle;
			VersionRange range = new VersionRange(versionExp);
			satisfied = range.isIncluded(realBundle.getVersion());
		}
	}

	/**
	 * This always returns true since this is an immutable condition.
	 * 
	 * @return always true.
	 * @see org.osgi.service.condpermadmin.Condition#isEvaluated()
	 */
	public boolean isEvaluated() {
		return true;
	}

	/**
	 * Returns true if the Bundle that was used to construct this conditions
	 * matches the SymbolicName description.
	 * 
	 * @return true if the Bundle that was used to construct this conditions
	 *         matches the SymbolicName description.
	 * @see org.osgi.service.condpermadmin.Condition#isSatisfied()
	 */
	public boolean isSatisfied() {
		return satisfied;
	}

	/**
	 * This method always returns false since this is an immutable condition.
	 * 
	 * @return always false.
	 * @see org.osgi.service.condpermadmin.Condition#isMutable()
	 */
	public boolean isMutable() {
		return false;
	}

	/**
	 * This method really shouldn't be called since this is an immutable
	 * condition. It simply calls isSatisifed() on each member of
	 * <code>conds</code> and returns true if they all return true.
	 * 
	 * @param conds the conditions to be evaluated.
	 * @param context not used.
	 * @return
	 * @see org.osgi.service.condpermadmin.Condition#isSatisfied(org.osgi.service.condpermadmin.Condition[],
	 *      java.util.Dictionary)
	 */
	public boolean isSatisfied(Condition[] conds, Dictionary context) {
		for (int i = 0; i < conds.length; i++) {
			if (!conds[i].isSatisfied()) {
				return false;
			}
		}
		return true;
	}
}

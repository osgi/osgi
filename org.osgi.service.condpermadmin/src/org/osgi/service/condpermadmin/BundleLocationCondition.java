/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package org.osgi.service.condpermadmin;

import java.io.FilePermission;
import java.util.Dictionary;
import org.osgi.framework.Bundle;
import org.osgi.service.condpermadmin.Condition;

/**
 * 
 * Checks to see if a Bundle matches the given location pattern. Pattern matching
 * is done using FilePermission style patterns.
 * 
 * @version $Revision$
 */
public class BundleLocationCondition implements Condition {
	boolean	satisfied;

	/**
	 * Constructs a condition that tries to match the passed Bundle's location
	 * to the location pattern.
	 * 
	 * @param bundle the Bundle being evaluated.
	 * @param location the location specification to match the Bundle
	 *        location to. Matching is done according to the patterns documented
	 *        in FilePermission.
	 */
	public BundleLocationCondition(Bundle bundle, String location) {
		FilePermission locationPat = new FilePermission(location, "read");
		FilePermission sourcePat = new FilePermission(bundle.getLocation()
				.toString(), "read");
		satisfied = locationPat.implies(sourcePat);
	}

	/**
	 * This method is always true since this is an immutable Condition.
	 * 
	 * @return always true.
	 * @see org.osgi.service.condpermadmin.Condition#isPostponed()
	 */
	public boolean isPostponed() {
		return false;
	}

	/**
	 * This method returns true if the location of the bundle matches the the
	 * location pattern that was used to construct this Condition. The matching
	 * is done according to the matching scheme in FilePermission.
	 * 
	 * @return true if the location of the bundle matches the location pattern
	 *         of this condition.
	 * @see org.osgi.service.condpermadmin.Condition#isSatisfied()
	 * @see java.io.FilePermission
	 */
	public boolean isSatisfied() {
		return satisfied;
	}

	/**
	 * This Condition never changes, so this method always returns false.
	 * 
	 * @return always returns false.
	 * @see org.osgi.service.condpermadmin.Condition#isMutable()
	 */
	public boolean isMutable() {
		return false;
	}

	/**
	 * This method should never get called since this is an immutable Condition. As implemented it simply loops through all the Conditions calling isSatisfied()
	 * @param conds the conditions to check for satisfiability.
	 * @param context not used.
	 * @return true if all of the conditions are satisfied.
	 * @see org.osgi.service.condpermadmin.Condition#isSatisfied(org.osgi.service.condpermadmin.Condition[],Dictionary)
	 */
	public boolean isSatisfied(Condition[] conds, Dictionary context) {
		for(int i = 0; i < conds.length; i++) {
			if (!conds[i].isSatisfied()) return false;
		}
		return true;
	}
}

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

import java.io.FilePermission;
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
	 * @see org.osgi.service.condpermadmin.Condition#isEvaluated()
	 */
	public boolean isEvaluated() {
		return true;
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
	 * @return true if all of the conditions are satisfied.
	 * @see org.osgi.service.condpermadmin.Condition#isSatisfied(org.osgi.service.condpermadmin.Condition[])
	 */
	public boolean isSatisfied(Condition[] conds) {
		for(int i = 0; i < conds.length; i++) {
			if (!conds[i].isSatisfied()) return false;
		}
		return true;
	}
}

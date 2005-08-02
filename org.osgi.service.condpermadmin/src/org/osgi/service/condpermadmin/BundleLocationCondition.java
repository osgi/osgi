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

import java.io.File;
import java.io.FilePermission;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;

import org.osgi.framework.*;

/**
 * 
 * Checks to see if a Bundle matches the given location pattern. Pattern
 * matching is done according to the filter string matching rules.
 * 
 * @version $Revision$
 */
public class BundleLocationCondition {
	private static final String	CONDITION_TYPE	= "org.osgi.service.condpermadmin.BundleLocationCondition";

	/**
	 * Constructs a condition that tries to match the passed Bundle's location
	 * to the location pattern.
	 * 
	 * @param bundle the Bundle being evaluated.
	 * @param info the ConditionInfo to construct the condition for. The args of
	 *        the ConditionInfo specify the location to match the Bundle
	 *        location to. Matching is done according to the filter string 
	 *        matching rules.
	 */
	static public Condition getCondition(final Bundle bundle, ConditionInfo info) {
		if (!CONDITION_TYPE.equals(info.getType()))
			throw new IllegalArgumentException(
					"ConditionInfo must be of type \"" + CONDITION_TYPE + "\"");
		String[] args = info.getArgs();
		if (args.length != 1)
			throw new IllegalArgumentException("Illegal number of args: "
					+ args.length);
		String bundleLocation = (String) AccessController.doPrivileged(new PrivilegedAction() {
					public Object run() {
						return bundle.getLocation();
					}
				});
		Filter filter = null;
		try {
			filter = FrameworkUtil.createFilter("(location=" + args[0] + ")");
		}
		catch (InvalidSyntaxException e) {
			// this should never happen, but just incase
			throw new RuntimeException("Invalid filter: " + e.getFilter());
		}
		Hashtable matchProps = new Hashtable(2);
		matchProps.put("location", bundleLocation);
		return filter.match(matchProps) ? Condition.TRUE : Condition.FALSE;
	}

	private BundleLocationCondition() {
		// private constructor to prevent objects of this type
	}
}

/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * Copyright (c) 2005 IBM Corporation and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.framework;

import org.eclipse.osgi.framework.internal.core.FilterImpl;

/**
 * Framework Utility class.
 * 
 * <p>
 * This class contains utility methods which access Framework functions that may
 * be useful to bundles.
 * 
 * @version $Revision$
 * @since 1.3
 */
public class FrameworkUtil {

	/**
	 * FrameworkUtil objects may not be constructed. 
	 */
	private FrameworkUtil() {}
	
	/**
	 * Creates a <code>Filter</code> object. This <code>Filter</code> object
	 * may be used to match a <code>ServiceReference</code> object or a
	 * <code>Dictionary</code> object.
	 * 
	 * <p>
	 * If the filter cannot be parsed, an {@link InvalidSyntaxException} will be
	 * thrown with a human readable message where the filter became unparsable.
	 * 
	 * @param filter The filter string.
	 * @return A <code>Filter</code> object encapsulating the filter string.
	 * @throws InvalidSyntaxException If <code>filter</code> contains an
	 *         invalid filter string that cannot be parsed.
	 * @throws NullPointerException If <code>filter</code> is null.
	 * 
	 * @see Filter
	 */
	public static Filter createFilter(String filter)
			throws InvalidSyntaxException {
		return new FilterImpl(filter);
	}
}

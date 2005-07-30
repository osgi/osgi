/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2000, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.framework;

/**
 * Supports a configuration object.
 * 
 * <p>
 * <code>Configurable</code> is an interface that should be used by a bundle
 * developer in support of a configurable service. Bundles that need to
 * configure a service may test to determine if the service object is an
 * <code>instanceof Configurable</code>.
 * 
 * @version $Revision$
 * @deprecated Since 1.2. Please use Configuration Admin service.
 */
public interface Configurable {
	/**
	 * Returns this service's configuration object.
	 * 
	 * <p>
	 * Services implementing <code>Configurable</code> should take care when
	 * returning a service configuration object since this object is probably
	 * sensitive.
	 * <p>
	 * If the Java Runtime Environment supports permissions, it is recommended
	 * that the caller is checked for some appropriate permission before
	 * returning the configuration object.
	 * 
	 * @return The configuration object for this service.
	 * @throws java.lang.SecurityException If the caller does not have an
	 *         appropriate permission and the Java Runtime Environment supports
	 *         permissions.
	 * @deprecated Since 1.2. Please use Configuration Admin service.
	 */
	public Object getConfigurationObject();
}

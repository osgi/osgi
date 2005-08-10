/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2001, 2005). All Rights Reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package org.osgi.service.device;

import org.osgi.framework.ServiceReference;

/**
 * Instances of <code>Match</code> are used in the {@link DriverSelector#select}
 * method to identify Driver services matching a Device service.
 * 
 * @version $Revision$
 * @since 1.1
 * @see DriverSelector
 */
public interface Match {
	/**
	 * Return the reference to a Driver service.
	 * 
	 * @return <code>ServiceReference</code> object to a Driver service.
	 */
	public ServiceReference getDriver();

	/**
	 * Return the match value of this object.
	 * 
	 * @return the match value returned by this Driver service.
	 */
	public int getMatchValue();
}

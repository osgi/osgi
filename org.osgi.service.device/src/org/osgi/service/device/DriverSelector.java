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
 * When the device manager detects a new Device service, it calls all registered
 * Driver services to determine if anyone matches the Device service. If at
 * least one Driver service matches, the device manager must choose one. If
 * there is a Driver Selector service registered with the Framework, the device
 * manager will ask it to make the selection. If there is no Driver Selector
 * service, or if it returns an invalid result, or throws an <code>Exception</code>,
 * the device manager uses the default selection strategy.
 * 
 * @version $Revision$
 * @since 1.1
 */
public interface DriverSelector {
	/**
	 * Return value from <code>DriverSelector.select</code>, if no Driver service
	 * should be attached to the Device service. The value is -1.
	 */
	public static final int	SELECT_NONE	= -1;

	/**
	 * Select one of the matching Driver services. The device manager calls this
	 * method if there is at least one driver bidding for a device. Only Driver
	 * services that have responded with nonzero (not {@link Device#MATCH_NONE})
	 * <code></code> match values will be included in the list.
	 * 
	 * @param reference the <code>ServiceReference</code> object of the Device
	 *        service.
	 * @param matches the array of all non-zero matches.
	 * @return index into the array of <code>Match</code> objects, or
	 *         <code>SELECT_NONE</code> if no Driver service should be attached
	 */
	public int select(ServiceReference reference, Match[] matches);
}

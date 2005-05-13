/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2000, 2005). All Rights Reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package org.osgi.service.device;

/**
 * <p>
 * Interface for identifying device services.
 * 
 * <p>
 * A service must implement this interface or use the
 * {@link Constants#DEVICE_CATEGORY}registration property to indicate that it
 * is a device. Any services implementing this interface or registered with the
 * <code>DEVICE_CATEGORY</code> property will be discovered by the device manager.
 * 
 * <p>
 * Device services implementing this interface give the device manager the
 * opportunity to indicate to the device that no drivers were found that could
 * (further) refine it. In this case, the device manager calls the
 * {@link #noDriverFound}method on the <code>Device</code> object.
 * 
 * <p>
 * Specialized device implementations will extend this interface by adding
 * methods appropriate to their device category to it.
 * 
 * @version $Revision$
 * @see Driver
 */
public abstract interface Device {
	/**
	 * Return value from {@link Driver#match}indicating that the driver cannot
	 * refine the device presented to it by the device manager.
	 * 
	 * The value is zero.
	 */
	public static final int	MATCH_NONE	= 0;

	/**
	 * Indicates to this <code>Device</code> object that the device manager has
	 * failed to attach any drivers to it.
	 * 
	 * <p>
	 * If this <code>Device</code> object can be configured differently, the
	 * driver that registered this <code>Device</code> object may unregister it
	 * and register a different Device service instead.
	 */
	public abstract void noDriverFound();
}

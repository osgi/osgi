/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2000, 2002).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.service.device;

/**
 * <p>Interface for identifying device services.
 *
 * <p>A service must implement this interface or use the
 * {@link Constants#DEVICE_CATEGORY} registration property
 * to indicate that it is a device. Any services
 * implementing this interface or registered with the <tt>DEVICE_CATEGORY</tt>
 * property will be discovered by the device manager.
 *
 * <p>Device services implementing this interface give the device manager
 * the opportunity to indicate to the device that no drivers were found
 * that could (further) refine it.
 * In this case, the device manager calls the {@link #noDriverFound} method on
 * the <tt>Device</tt> object.
 *
 * <p>Specialized device implementations will extend this interface by adding
 * methods appropriate to their device category to it.
 *
 * @version $Revision$
 * @see Driver
 */

public abstract interface Device
{
    /**
     * Return value from {@link Driver#match} indicating that the
     * driver cannot refine the device presented to it by the device manager.
	 *
     * The value is zero.
     */
    public static final int MATCH_NONE = 0;

    /**
     * Indicates to this <tt>Device</tt> object that the device manager has failed to attach
     * any drivers to it.
     *
     * <p> If this <tt>Device</tt> object can be configured differently, the driver
     * that registered this <tt>Device</tt> object may unregister it and register a different
     * Device service instead.
     */
    public abstract void noDriverFound();
}

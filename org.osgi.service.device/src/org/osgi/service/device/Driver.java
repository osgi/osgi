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

import org.osgi.framework.ServiceReference;

/**
 * A <code>Driver</code> service object must be registered by each Driver bundle
 * wishing to attach to Device services provided by other drivers. For each
 * newly discovered {@link Device}object, the device manager enters a bidding
 * phase. The <code>Driver</code> object whose {@link #match}method bids the
 * highest for a particular <code>Device</code> object will be instructed by the
 * device manager to attach to the <code>Device</code> object.
 * 
 * @version $Revision$
 * @see Device
 * @see DriverLocator
 */
public abstract interface Driver {
	/**
	 * Checks whether this Driver service can be attached to the Device service.
	 * 
	 * The Device service is represented by the given {@link ServiceReference}
	 * and returns a value indicating how well this driver can support the given
	 * Device service, or {@link Device#MATCH_NONE}if it cannot support the
	 * given Device service at all.
	 * 
	 * <p>
	 * The return value must be one of the possible match values defined in the
	 * device category definition for the given Device service, or
	 * <code>Device.MATCH_NONE</code> if the category of the Device service is not
	 * recognized.
	 * 
	 * <p>
	 * In order to make its decision, this Driver service may examine the
	 * properties associated with the given Device service, or may get the
	 * referenced service object (representing the actual physical device) to
	 * talk to it, as long as it ungets the service and returns the physical
	 * device to a normal state before this method returns.
	 * 
	 * <p>
	 * A Driver service must always return the same match code whenever it is
	 * presented with the same Device service.
	 * 
	 * <p>
	 * The match function is called by the device manager during the matching
	 * process.
	 * 
	 * @param reference the <code>ServiceReference</code> object of the device to
	 *        match
	 * 
	 * @return value indicating how well this driver can support the given
	 *         Device service, or <code>Device.MATCH_NONE</code> if it cannot
	 *         support the Device service at all
	 * 
	 * @exception java.lang.Exception if this Driver service cannot examine the
	 *            Device service
	 */
	public abstract int match(ServiceReference reference) throws Exception;

	/**
	 * Attaches this Driver service to the Device service represented by the
	 * given <code>ServiceReference</code> object.
	 * 
	 * <p>
	 * A return value of <code>null</code> indicates that this Driver service has
	 * successfully attached to the given Device service. If this Driver service
	 * is unable to attach to the given Device service, but knows of a more
	 * suitable Driver service, it must return the <code>DRIVER_ID</code> of that
	 * Driver service. This allows for the implementation of referring drivers
	 * whose only purpose is to refer to other drivers capable of handling a
	 * given Device service.
	 * 
	 * <p>
	 * After having attached to the Device service, this driver may register the
	 * underlying device as a new service exposing driver-specific
	 * functionality.
	 * 
	 * <p>
	 * This method is called by the device manager.
	 * 
	 * @param reference the <code>ServiceReference</code> object of the device to
	 *        attach to
	 * 
	 * @return <code>null</code> if this Driver service has successfully attached
	 *         to the given Device service, or the <code>DRIVER_ID</code> of a
	 *         more suitable driver
	 * 
	 * @exception java.lang.Exception if the driver cannot attach to the given
	 *            device and does not know of a more suitable driver
	 */
	public abstract String attach(ServiceReference reference) throws Exception;
}

/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2001, 2002).
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
 * When the device manager detects a new Device service, it calls all registered
 * Driver services to determine if anyone matches the Device service. If at least one
 * Driver service matches, the device manager must choose one. If there is a
 * Driver Selector service registered with the Framework, the
 * device manager will ask it to make the selection. If there is no
 * Driver Selector service, or if it returns an invalid result, or
 * throws an <tt>Exception</tt>, the device manager uses the default selection
 * strategy.
 *
 * @version $Revision$
 * @since 1.1
 */

public abstract interface DriverSelector
{
    /**
     * Return value from <tt>DriverSelector.select</tt>, if no Driver service
     * should be attached to the Device service. The value is -1.
     */
    public static final int SELECT_NONE = -1;

    /**
     * Select one of the matching Driver services. The device manager calls this
     * method if there is at least one driver bidding for a device. Only
     * Driver services that have responded with nonzero (not
     * {@link Device#MATCH_NONE}) <tt></tt>match values will be included in the
     * list.
     *
     * @param reference the <tt>ServiceReference</tt> object of the Device service.
     * @param matches the array of all non-zero matches.
     * @return index into the array of <tt>Match</tt> objects, or
     *   <tt>SELECT_NONE</tt> if no Driver service should be attached
     */
    public abstract int select(ServiceReference reference,
                  Match[] matches);
}

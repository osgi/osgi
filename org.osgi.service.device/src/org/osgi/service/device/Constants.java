/*
 * $Header$
 *
 * Copyright (c) The OSGi Alliance (2000, 2002).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
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
 * This interface defines standard names for property keys associated
 * with {@link Device} and {@link Driver} services.
 *
 * <p> The values associated with these keys are of type
 * <tt>java.lang.String</tt>, unless otherwise stated.
 *
 * @version $Revision$
 * @since 1.1
 * @see Device
 * @see Driver
 */

public interface Constants
{
    /**
     * Property (named &quot;DRIVER_ID&quot;) identifying a driver.
     *
     * <p>A <tt>DRIVER_ID</tt> should start with the reversed domain name of
     * the company that implemented the driver (e.g., <tt>com.acme</tt>), and
     * must meet the following requirements:
     *
     * <ul>
     * <li>It must be independent of the location from where it is obtained.
     * <li>It must be independent of the {@link DriverLocator} service
     * that downloaded it.
     * <li>It must be unique.
     * <li>It must be different for different revisions of the same driver.
     * </ul>
     *
     * <p> This property is mandatory, i.e., every <tt>Driver</tt> service
     * must be registered with it.
     */
    public static final String DRIVER_ID = "DRIVER_ID";

    /**
     * Property (named &quot;DEVICE_CATEGORY&quot;) containing a human readable
     * description of the device categories implemented by a device. This
     * property is of type <tt>String[]</tt>
     *
     * <p>Services registered with this property will be treated as devices
     * and discovered by the device manager
     */
    public static final String DEVICE_CATEGORY = "DEVICE_CATEGORY";

    /**
     * Property (named &quot;DEVICE_SERIAL&quot;) specifying
     * a device's serial number.
     */
    public static final String DEVICE_SERIAL = "DEVICE_SERIAL";

    /**
     * Property (named &quot;DEVICE_DESCRIPTION&quot;) containing a human
     * readable string describing the actual hardware device.
     */
    public static final String DEVICE_DESCRIPTION = "DEVICE_DESCRIPTION";
}

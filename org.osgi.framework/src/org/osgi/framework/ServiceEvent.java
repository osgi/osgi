/*
 * $Header$
 *
 * Copyright (c) The OSGi Alliance (2000-2001).
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

package org.osgi.framework;

import java.util.EventObject;

/**
 * A service lifecycle change event.
 * <p><tt>ServiceEvent</tt> objects are delivered to a <tt>ServiceListener</tt> objects when a change
 * occurs in this service's lifecycle. A type code is used to identify the event type for
 * future extendability.
 *
 * <p>The OSGi Alliance reserves the right to extend the set of types.
 *
 * @version $Revision$
 * @see ServiceListener
 */

public class ServiceEvent extends EventObject
{
    /**
     * Reference to the service that had a change occur in its lifecycle.
     */
    private transient ServiceReference reference;

    /**
     * Type of service lifecycle change.
     */
    private transient int type;

    /**
     * This service has been registered.
     * <p>This event is synchronously delivered <strong>after</strong> the service has been registered with the
     * Framework.
     *
     * <p>The value of <tt>REGISTERED</tt> is 0x00000001.
     *
     * @see BundleContext#registerService
     */
    public final static int REGISTERED = 0x00000001;

    /**
     * The properties of a registered service have been modified.
     * <p>This event is synchronously delivered <strong>after</strong> the service properties
     * have been modified.
     *
     * <p>The value of <tt>MODIFIED</tt> is 0x00000002.
     *
     * @see ServiceRegistration#setProperties
     */
    public final static int MODIFIED = 0x00000002;

    /**
     * This service is in the process of being unregistered.
     * <p>This event is synchronously delivered <strong>before</strong> the service has completed
     * unregistering.
     *
     * <p>If a bundle is using a service that is <tt>UNREGISTERING</tt>, the bundle should release
     * its use of the service when it receives this event. If the bundle does not release its use of
     * the service when it receives this event, the Framework will automatically release the bundle's
     * use of the service while completing the service unregistration operation.
     *
     * <p>The value of UNREGISTERING is 0x00000004.
     *
     * @see ServiceRegistration#unregister
     * @see BundleContext#ungetService
     */
    public final static int UNREGISTERING = 0x00000004;


    /**
     * Creates a new service event object.
     *
     * @param type The event type.
     * @param reference A <tt>ServiceReference</tt> object to the service that had a lifecycle change.
     */
    public ServiceEvent(int type, ServiceReference reference)
    {
        super(reference);
        this.reference = reference;
        this.type = type;
    }

    /**
     * Returns a reference to the service that had a change occur in its lifecycle.
     * <p>This reference is the source of the event.
     *
     * @return Reference to the service that had a lifecycle change.
     */
    public ServiceReference getServiceReference()
    {
        return(reference);
    }

    /**
     * Returns the type of event.
     * The event type values are:
     * <ul>
     * <li>{@link #REGISTERED}
     * <li>{@link #MODIFIED}
     * <li>{@link #UNREGISTERING}
     * </ul>
     * @return Type of service lifecycle change.
     */

    public int getType()
    {
        return(type);
    }
}



/*
 * $Header$
 *
 * Copyright (c) The OSGi Alliance (2001).
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

package org.osgi.service.useradmin;

import org.osgi.framework.ServiceReference;

/**
 * <tt>Role</tt> change event.
 * <p><tt>UserAdminEvent</tt> objects are delivered asynchronously to any
 * <tt>UserAdminListener</tt> objects when a change occurs in any of the <tt>Role</tt> objects managed
 * by a User Admin service.
 *
 * <p>A type code is used to identify the event. The following event types are
 * defined: {@link #ROLE_CREATED}type, {@link #ROLE_CHANGED}type, and
 * {@link #ROLE_REMOVED}type. Additional event types may be defined in the future.
 *
 * @see UserAdmin
 * @see UserAdminListener
 *
 * @version $Revision$
 */

public class UserAdminEvent {

    private ServiceReference ref;
    private int type;
    private Role role;

    /**
     * A <tt>Role</tt> object has been created.
     *
     * <p>The value of <tt>ROLE_CREATED</tt> is 0x00000001.
     */
    public static final int ROLE_CREATED = 0x00000001;

    /**
     * A <tt>Role</tt> object has been modified.
     *
     * <p>The value of <tt>ROLE_CHANGED</tt> is 0x00000002.
     */
    public static final int ROLE_CHANGED = 0x00000002;

    /**
     * A <tt>Role</tt> object has been removed.
     *
     * <p>The value of <tt>ROLE_REMOVED</tt> is 0x00000004.
     */
    public static final int ROLE_REMOVED = 0x00000004;

    /**
     * Constructs a <tt>UserAdminEvent</tt> object from the given <tt>ServiceReference</tt> object, event type,
     * and <tt>Role</tt> object.
     *
     * @param ref The <tt>ServiceReference</tt> object of the User Admin service that generated this
     * event.
     * @param type The event type.
     * @param role The <tt>Role</tt> object on which this event occurred.
     */
    public UserAdminEvent(ServiceReference ref, int type, Role role) {
    this.ref = ref;
    this.type = type;
    this.role = role;
    }

    /**
     * Gets the <tt>ServiceReference</tt> object of the User Admin service that generated this event.
     *
     * @return The User Admin service's <tt>ServiceReference</tt> object.
     */
    public ServiceReference getServiceReference() { return ref; }

    /**
     * Returns the type of this event.
     *
     * <p>The type values are {@link #ROLE_CREATED}type, {@link #ROLE_CHANGED}type,
     * and {@link #ROLE_REMOVED}type.
     *
     * @return The event type.
     */
    public int getType() { return type; }

    /**
     * Gets the <tt>Role</tt> object this event was generated for.
     *
     * @return The <tt>Role</tt> object this event was generated for.
     */
    public Role getRole() { return role; }
}

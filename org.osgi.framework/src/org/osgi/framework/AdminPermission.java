/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2000, 2004).
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

package org.osgi.framework;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.security.Permission;
import java.security.BasicPermission;
import java.security.PermissionCollection;

/**
 * Indicates the caller's authority to perform lifecycle operations on
 * or to get sensitive information about a bundle.
 * <p><tt>AdminPermission</tt> has no actions or target.
 * <p>The <tt>hashCode()</tt> method of <tt>AdminPermission</tt> is
 * inherited from <tt>java.security.BasicPermission</tt>. The hash code it
 * returns is the hash code of the name "AdminPermission", which is
 * always the same for all instances of <tt>AdminPermission</tt>. <p>
 *
 * @version $Revision$
 */

public final class AdminPermission extends BasicPermission
{
    static final long serialVersionUID = 207051004521261705L;
    /**
     * Creates a new <tt>AdminPermission</tt> object with its name set to
     * "AdminPermission".
     */
    public AdminPermission()
    {
        super("AdminPermission");
    }

    /**
     * Creates a new <tt>AdminPermission</tt> object for use by the <code>Policy</code>
     * object to instantiate new <tt>Permission</tt> objects.
     *
     * @param name Ignored; always set to "AdminPermission".
     * @param actions Ignored.
     */
    public AdminPermission(String name, String actions)
    {
        this();
    }

    /**
     * Determines if the specified permission is implied by this object.
     * <p>This method returns <tt>true</tt> if the specified
     * permission is an instance of <tt>AdminPermission</tt>.
     *
     * @param p The permission to interrogate.
     *
     * @return <tt>true</tt> if the permission is an
     * instance of this class; <tt>false</tt> otherwise.
     */
    public boolean implies(Permission p)
    {
        return(p instanceof AdminPermission);
    }

    /**
     * Determines the equality of two <tt>AdminPermission</tt> objects. <p>Two <tt>AdminPermission</tt>
     * objects are always equal.
     *
     * @param obj The object being compared for equality with this object.
     * @return <tt>true</tt> if <tt>obj</tt> is an <tt>AdminPermission</tt>; <tt>false</tt> otherwise.
     */
    public boolean equals(Object obj)
    {
        return(obj instanceof AdminPermission);
    }

    /**
     * Returns a new <tt>PermissionCollection</tt> object suitable for storing
     * <tt>AdminPermission</tt>s.
     * <p>
     *
     * @return A new <tt>PermissionCollection</tt> object.
     */

    public PermissionCollection newPermissionCollection()
    {
        return(new AdminPermissionCollection());
    }
}

/**
 * Stores a collection of <tt>AdminPermission</tt>s.
 *
 */
final class AdminPermissionCollection extends PermissionCollection
{
    static final long serialVersionUID = 3226083842806530691L;
    /**
     * True if collection is non-empty.
     *
     * @serial
     */
    private boolean hasElement;

    /**
     * Creates an empty <tt>AdminPermission</tt> object.
     *
     */
    public AdminPermissionCollection()
    {
        hasElement = false;
    }

    /**
     * Adds the specified permission to the <tt>AdminPermissionCollection</tt>.
     * The key for the hash is the interface name of the service.
     *
     * @param permission The <tt>Permission</tt> object to add.
     *
     * @exception IllegalArgumentException If the permission is not an
     * <tt>AdminPermission</tt>.
     *
     * @exception SecurityException If this AdminPermissionCollection
     * object has been marked read-only.
     */

    public void add(Permission permission)
    {
        if (!(permission instanceof AdminPermission))
        {
            throw new IllegalArgumentException("invalid permission: "+
                                               permission);
        }

        if (isReadOnly())
            throw new SecurityException("attempt to add a Permission to a " +
                    "readonly PermissionCollection");

        hasElement = true;
    }

    /**
     * Determines if the specified set of permissions implies the
     * permissions expressed in the parameter <tt>permission</tt>.
     *
     * @param p The Permission object to compare.
     *
     * @return true if permission is a proper subset of a permission in
     * the set; false otherwise.
     */

    public boolean implies(Permission p)
    {
        return(hasElement && (p instanceof AdminPermission));
    }

    /**
     * Returns an enumeration of an <tt>AdminPermission</tt> object.
     *
     * @return Enumeration of an <tt>AdminPermission</tt> object.
     */

    public Enumeration elements()
    {
        return (new Enumeration()
               {
                   private boolean more = hasElement;

                   public boolean hasMoreElements()
                   {
                       return(more);
                   }

                   public Object nextElement()
                   {
                       if (more)
                       {
                           more = false;

                           return (new AdminPermission());
                       }
                       else
                       {
                           throw new NoSuchElementException();
                       }
                   }
               });
    }
}



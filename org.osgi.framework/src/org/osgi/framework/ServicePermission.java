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

package org.osgi.framework;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Enumeration;
import java.security.Permission;
import java.security.BasicPermission;
import java.security.PermissionCollection;

/**
 * Indicates a bundle's authority to register or get a service.
 * <ul>
 * <li>The <tt>ServicePermission.REGISTER</tt> action allows a bundle
 * to register a service on the specified names.
 * <li>The <tt>ServicePermission.GET</tt> action allows a bundle to detect a service and
 * get it.
 * </ul>
 * Permission to get a service is required in order to detect events regarding the service.
 * Untrusted bundles should not be able to detect the presence of certain services unless they have
 * the appropriate <tt>ServicePermission</tt> to get the specific service.
 *
 * @version $Revision$
 */

final public class ServicePermission extends BasicPermission
{

    /**
     * The action string <tt>get</tt> (Value is "get").
     */
    public final static String GET  = "get";
    /**
     * The action string <tt>register</tt> (Value is "register").
     */
    public final static String REGISTER = "register";

    private final static int ACTION_GET             = 0x00000001;
    private final static int ACTION_REGISTER        = 0x00000002;
    private final static int ACTION_ALL             = ACTION_GET | ACTION_REGISTER;
    private final static int ACTION_NONE            = 0;
    private final static int ACTION_ERROR           = 0x80000000;

    /**
     * The actions mask.
     */
    private transient int action_mask = ACTION_NONE;

    /**
     * The actions in canonical form.
     *
     * @serial
     */
    private String actions = null;

    /**
     * Create a new ServicePermission.
     *
     * <p>The name of the service is specified as a fully qualified class name.
     * <pre>
     * ClassName ::= &lt;class name&gt; | &lt;class name ending in ".*"&gt;
     * </pre>
     * Examples:
     * <pre>
     *    org.osgi.service.http.HttpService
     *    org.osgi.service.http.*
     *    org.osgi.service.snmp.*
     * </pre>
     *
     * <p>There are two possible actions: <tt>get</tt> and <tt>register</tt>.
     * The <tt>get</tt> permission allows the owner of this permission
     * to obtain a service with this name. The <tt>register</tt> permission
     * allows the bundle to register a service under that name.
     *
     * @param name class name
     * @param actions <tt>get</tt>, <tt>register</tt> (canonical order)
     */

    public ServicePermission(String name, String actions)
    {
        this(name, getMask(actions));
    }

    /**
     * Package private constructor used by ServicePermissionCollection.
     *
     * @param name class name
     * @param action mask
     */
    ServicePermission(String name, int mask)
    {
        super(name);

        init(mask);
    }

    /**
     * Called by constructors and when deserialized.
     *
     * @param action mask
     */
    private void init(int mask)
    {
        if ((mask == ACTION_NONE) ||
            ((mask & ACTION_ALL) != mask))
        {
            throw new IllegalArgumentException("invalid action string");
        }

        action_mask = mask;
    }

    /**
     * Parse action string into action mask.
     *
     * @param actions Action string.
     * @return action mask.
     */
    private static int getMask(String actions)
    {
    boolean seencomma = false;

    int mask = ACTION_NONE;

    if (actions == null) {
        return mask;
    }

    char[] a = actions.toCharArray();

    int i = a.length - 1;
    if (i < 0)
        return mask;

    while (i != -1) {
        char c;

        // skip whitespace
        while ((i!=-1) && ((c = a[i]) == ' ' ||
                  c == '\r' ||
                  c == '\n' ||
                  c == '\f' ||
                  c == '\t'))
        i--;

        // check for the known strings
        int matchlen;

        if (i >= 2 && (a[i-2] == 'g' || a[i-2] == 'G') &&
              (a[i-1] == 'e' || a[i-1] == 'E') &&
              (a[i] == 't' || a[i] == 'T'))
        {
        matchlen = 3;
        mask |= ACTION_GET;

        } else if (i >= 7 && (a[i-7] == 'r' || a[i-7] == 'R') &&
                       (a[i-6] == 'e' || a[i-6] == 'E') &&
                       (a[i-5] == 'g' || a[i-5] == 'G') &&
                 (a[i-4] == 'i' || a[i-4] == 'I') &&
                 (a[i-3] == 's' || a[i-3] == 'S') &&
                 (a[i-2] == 't' || a[i-2] == 'T') &&
                 (a[i-1] == 'e' || a[i-1] == 'E') &&
                 (a[i] == 'r' || a[i] == 'R'))
        {
        matchlen = 8;
        mask |= ACTION_REGISTER;

        } else {
        // parse error
        throw new IllegalArgumentException(
            "invalid permission: " + actions);
        }

        // make sure we didn't just match the tail of a word
        // like "ackbarfregister".  Also, skip to the comma.
        seencomma = false;
        while (i >= matchlen && !seencomma) {
        switch(a[i-matchlen]) {
        case ',':
            seencomma = true;
            /*FALLTHROUGH*/
        case ' ': case '\r': case '\n':
        case '\f': case '\t':
            break;
        default:
            throw new IllegalArgumentException("invalid permission: " +
                             actions);
        }
        i--;
        }

        // point i at the location of the comma minus one (or -1).
        i -= matchlen;
    }

    if (seencomma) {
        throw new IllegalArgumentException("invalid permission: " +
                        actions);
    }

    return mask;
    }

    /**
     * Determines if a <tt>ServicePermission</tt> object "implies" the
     * specified permission.
     *
     * @param p The target permission to check.
     * @return <tt>true</tt> if the specified permission is implied by
     * this object; <tt>false</tt> otherwise.
     */

    public boolean implies(Permission p)
    {
        if (p instanceof ServicePermission)
        {
            ServicePermission target = (ServicePermission) p;

            return(((action_mask & target.action_mask)==target.action_mask) &&
           super.implies(p));
        }

        return(false);
    }

    /**
     * Returns the canonical string representation of the actions.
     * Always returns present actions in the following order:
     * <tt>get</tt>, <tt>register</tt>.
     * @return The canonical string representation of the actions.
     */
    public String getActions()
    {
        if (actions == null)
        {
            StringBuffer sb = new StringBuffer();
            boolean comma = false;

            if ((action_mask & ACTION_GET) == ACTION_GET)
            {
                sb.append(GET);
                comma = true;
            }

            if ((action_mask & ACTION_REGISTER) == ACTION_REGISTER)
            {
                if (comma) sb.append(',');
                sb.append(REGISTER);
            }

            actions = sb.toString();
        }

        return(actions);
    }

    /**
     * Returns a new <tt>PermissionCollection</tt> object for storing
     * <tt>ServicePermission<tt> objects.
     *
     * @return A new <tt>PermissionCollection</tt> object suitable for storing
     * <tt>ServicePermission</tt> objects.
     */
    public PermissionCollection newPermissionCollection()
    {
        return (new ServicePermissionCollection());
    }

    /**
     * Determines the equalty of two ServicePermission objects.
     *
     * Checks that specified object has the same class name
     * and action as this <tt>ServicePermission</tt>.
     *
     * @param obj The object to test for equality.
     * @return true if obj is a <tt>ServicePermission</tt>, and has the
     * same class name and actions as this <tt>ServicePermission</tt> object; <tt>false</tt> otherwise.
     */
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return(true);
        }

        if (!(obj instanceof ServicePermission))
        {
            return(false);
        }

        ServicePermission p = (ServicePermission) obj;

        return((action_mask == p.action_mask) &&
        getName().equals(p.getName()));
    }

    /**
     * Returns the hash code value for this object.
     *
     * @return Hash code value for this object.
     */

    public int hashCode()
    {
        return(getName().hashCode() ^ getActions().hashCode());
    }

    /**
     * Returns the current action mask.
     * Used by the ServicePermissionCollection object.
     *
     * @return The actions mask.
     */
    int getMask()
    {
        return (action_mask);
    }

    /**
     * WriteObject is called to save the state of the ServicePermission
     * to a stream. The actions are serialized, and the superclass
     * takes care of the name.
     */

    private synchronized void writeObject(java.io.ObjectOutputStream s)
        throws IOException
    {
        // Write out the actions. The superclass takes care of the name
        // call getActions to make sure actions field is initialized
        if (actions == null)
            getActions();
        s.defaultWriteObject();
    }

    /**
     * readObject is called to restore the state of the ServicePermission from
     * a stream.
     */
    private synchronized void readObject(java.io.ObjectInputStream s)
        throws IOException, ClassNotFoundException
    {
        // Read in the action, then initialize the rest
        s.defaultReadObject();
        init(getMask(actions));
    }
}

/**
 * Stores a set of ServicePermission permissions.
 *
 * @see java.security.Permission
 * @see java.security.Permissions
 * @see java.security.PermissionCollection
 */

final class ServicePermissionCollection extends PermissionCollection
{

    /**
     * Table of permissions.
     *
     * @serial
     */
    private Hashtable permissions;

    /**
     * Boolean saying if "*" is in the collection.
     *
     * @serial
     */
    private boolean all_allowed;

    /**
     * Creates an empty ServicePermissions object.
     *
     */

    public ServicePermissionCollection()
    {
        permissions = new Hashtable();
        all_allowed = false;
    }

    /**
     * Adds a permission to the <tt>ServicePermission</tt> objects using the key for the hash as
     * the name.
     *
     * @param permission The Permission object to add.
     *
     * @exception IllegalArgumentException If the permission is not a ServicePermission object.
     *
     * @exception SecurityException If this <tt>ServicePermissionCollection</tt> object has been marked read-only.
     */

    public void add(Permission permission)
    {
        if (! (permission instanceof ServicePermission))
            throw new IllegalArgumentException("invalid permission: "+
                                               permission);
        if (isReadOnly())
            throw new SecurityException("attempt to add a Permission to a " +
                    "readonly PermissionCollection");

        ServicePermission sp = (ServicePermission) permission;
        String name = sp.getName();

        ServicePermission existing =
        (ServicePermission) permissions.get(name);

        if (existing != null)
        {
            int oldMask = existing.getMask();
            int newMask = sp.getMask();
            if (oldMask != newMask)
            {
                permissions.put(name,
                new ServicePermission(name,
                            oldMask | newMask));
            }
        }
        else
        {
            permissions.put(name, permission);
        }

        if (!all_allowed)
        {
            if (name.equals("*"))
                all_allowed = true;
        }
    }

    /**
     * Determines if a set of permissions implies the permissions
     * expressed in <tt>permission</tt>.
     *
     * @param p The Permission object to compare.
     *
     * @return <tt>true</tt> if <tt>permission</tt> is a proper subset of a permission in
     * the set; <tt>false</tt> otherwise.
     */

    public boolean implies(Permission permission)
    {
        if (!(permission instanceof ServicePermission))
            return (false);

        ServicePermission sp = (ServicePermission) permission;
        ServicePermission x;

        int desired = sp.getMask();
        int effective = 0;

        // short circuit if the "*" Permission was added
        if (all_allowed)
        {
            x = (ServicePermission) permissions.get("*");
            if (x != null)
            {
                effective |= x.getMask();
                if ((effective & desired) == desired)
                    return (true);
            }
        }

        // strategy:
        // Check for full match first. Then work our way up the
        // name looking for matches on a.b.*

        String name = sp.getName();

        x = (ServicePermission) permissions.get(name);

        if (x != null)
        {
            // we have a direct hit!
            effective |= x.getMask();
            if ((effective & desired) == desired)
                return (true);
        }

        // work our way up the tree...
        int last, offset;

        offset = name.length()-1;

        while ((last = name.lastIndexOf(".", offset)) != -1)
        {

            name = name.substring(0, last+1) + "*";
            x = (ServicePermission) permissions.get(name);

            if (x != null)
            {
                effective |= x.getMask();
                if ((effective & desired) == desired)
                    return (true);
            }
            offset = last -1;
        }

        // we don't have to check for "*" as it was already checked
        // at the top (all_allowed), so we just return false
        return (false);
    }

    /**
     * Returns an enumeration of all the <tt>ServicePermission</tt> objects in the
     * container.
     *
     * @return Enumeration of all the ServicePermission objects.
     */

    public Enumeration elements()
    {
        return (permissions.elements());
    }
}



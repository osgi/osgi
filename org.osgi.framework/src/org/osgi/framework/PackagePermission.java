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
 * A bundle's authority to import or export a package.
 *
 * <p>A package is a dot-separated string that defines a fully qualified Java
 * package.
 * <p>For example:
 * <pre>
 * <tt>org.osgi.service.http</tt>
 * </pre>
 * <p><tt>PackagePermission</tt> has two actions: <tt>EXPORT</tt> and <tt>IMPORT</tt>.
 * The <tt>EXPORT</tt> action implies the <tt>IMPORT</tt> action.
 *
 * @version $Revision$
 */

public final class PackagePermission extends BasicPermission
{

    /**
     * The action string <tt>export</tt>.
     */
    public final static String EXPORT = "export";

    /**
     * The action string <tt>import</tt>.
     */
    public final static String IMPORT = "import";

    private final static int ACTION_EXPORT      = 0x00000001;
    private final static int ACTION_IMPORT      = 0x00000002;
    private final static int ACTION_ALL         = ACTION_EXPORT | ACTION_IMPORT;
    private final static int ACTION_NONE        = 0;
    private final static int ACTION_ERROR       = 0x80000000;

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
     * Defines the authority to import and/or export a package within the OSGi environment.
     * <p>The name is specified as a normal Java package name: a dot-separated string. Wildcards may be used.
     * For example:
     * <pre>
     * org.osgi.service.http
     * javax.servlet.*
     * *
     * </pre>
     * <p>Package Permissions are granted over all possible versions of a package.
     *
     * A bundle that needs to export a package must have the appropriate <tt>PackagePermission</tt>
     * for that package; similarly, a bundle that needs to import a package must have the appropriate
     * <tt>PackagePermssion</tt> for that package.
     * <p>Permission is granted for both classes and resources.
     * @param name Package name.
     * @param actions <tt>EXPORT</tt>, <tt>IMPORT</tt> (canonical order).
     */

    public PackagePermission(String name, String actions)
    {
        this(name, getMask(actions));
    }

    /**
     * Package private constructor used by PackagePermissionCollection.
     *
     * @param name class name
     * @param action mask
     */
    PackagePermission(String name, int mask)
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

        if (actions == null)
        {
            return (mask);
        }

        char[] a = actions.toCharArray();

        int i = a.length - 1;
        if (i < 0)
            return (mask);

        while (i != -1)
        {
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

            if (i >= 5 && (a[i-5] == 'i' || a[i-5] == 'I') &&
                (a[i-4] == 'm' || a[i-4] == 'M') &&
                (a[i-3] == 'p' || a[i-3] == 'P') &&
                (a[i-2] == 'o' || a[i-2] == 'O') &&
                (a[i-1] == 'r' || a[i-1] == 'R') &&
                (a[i] == 't' || a[i] == 'T'))
            {
                matchlen = 6;
                mask |= ACTION_IMPORT;

            }
            else if (i >= 5 && (a[i-5] == 'e' || a[i-5] == 'E') &&
                     (a[i-4] == 'x' || a[i-4] == 'X') &&
                     (a[i-3] == 'p' || a[i-3] == 'P') &&
                     (a[i-2] == 'o' || a[i-2] == 'O') &&
                     (a[i-1] == 'r' || a[i-1] == 'R') &&
                     (a[i] == 't' || a[i] == 'T'))
            {
                matchlen = 6;
                mask |= ACTION_EXPORT | ACTION_IMPORT;

            }
            else
            {
                // parse error
                throw new IllegalArgumentException("invalid permission: " +
                                                   actions);
            }

            // make sure we didn't just match the tail of a word
            // like "ackbarfimport".  Also, skip to the comma.
            seencomma = false;
            while (i >= matchlen && !seencomma)
            {
                switch (a[i-matchlen])
                {
                    case ',':
                        seencomma = true;
                        /*FALLTHROUGH*/
                    case ' ':
                    case '\r':
                    case '\n':
                    case '\f':
                    case '\t':
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

        if (seencomma)
        {
            throw new IllegalArgumentException("invalid permission: " +
                                               actions);
        }

        return (mask);
    }

    /**
     * Determines if the specified permission is implied by this object.
     *
     * <p>This method checks that the package name of the target is implied by the package name
     * of this object. The list of <tt>PackagePermission</tt> actions must either match or allow
     * for the list of the target object to imply the target <tt>PackagePermission</tt> action.
     * <p>The permission to export a package implies the permission to import the named package.
     * <pre>
     * x.y.*,"export" -> x.y.z,"export" is true
     * *,"import" -> x.y, "import"      is true
     * *,"export" -> x.y, "import"      is true
     * x.y,"export" -> x.y.z, "export"  is false
     * </pre>
     *
     * @param p The target permission to interrogate.
     * @return <tt>true</tt> if the specified <tt>PackagePermission</tt> action is implied by
     * this object; <tt>false</tt> otherwise.
     */

    public boolean implies(Permission p)
    {
        if (p instanceof PackagePermission)
        {
            PackagePermission target = (PackagePermission) p;

            return(((action_mask & target.action_mask)==target.action_mask) &&
                   super.implies(p));
        }

        return(false);
    }

    /**
     * Returns the canonical string representation of the <tt>PackagePermission</tt> actions.
     *
     * <p>Always returns present <tt>PackagePermission</tt> actions in the following order:
     * <tt>EXPORT</tt>, <tt>IMPORT</tt>.
     * @return Canonical string representation of the <tt>PackagePermission</tt> actions.
     */

    public String getActions()
    {
        if (actions == null)
        {
            StringBuffer sb = new StringBuffer();
            boolean comma = false;

            if ((action_mask & ACTION_EXPORT) == ACTION_EXPORT)
            {
                sb.append(EXPORT);
                comma = true;
            }

            if ((action_mask & ACTION_IMPORT) == ACTION_IMPORT)
            {
                if (comma) sb.append(',');
                sb.append(IMPORT);
            }

            actions = sb.toString();
        }

        return(actions);
    }

    /**
     * Returns a new <tt>PermissionCollection</tt> object suitable for storing
     * <tt>PackagePermission</tt> objects.
     *
     * @return A new <tt>PermissionCollection</tt> object.
     */
    public PermissionCollection newPermissionCollection()
    {
        return(new PackagePermissionCollection());
    }

    /**
     * Determines the equality of two <tt>PackagePermission</tt> objects.
     *
     * This method checks that specified package has the same package name
     * and <tt>PackagePermission</tt> actions as this <tt>PackagePermission</tt> object.
     *
     * @param obj The object to test for equality with this <tt>PackagePermission</tt> object.
     * @return <tt>true</tt> if <tt>obj</tt> is a <tt>PackagePermission</tt>, and has the
     * same package name and actions as this <tt>PackagePermission</tt> object; <tt>false</tt> otherwise.
     */
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return(true);
        }

        if (!(obj instanceof PackagePermission))
        {
            return(false);
        }

        PackagePermission p = (PackagePermission) obj;

        return((action_mask == p.action_mask) &&
               getName().equals(p.getName()));
    }

    /**
     * Returns the hash code value for this object.
     *
     * @return A hash code value for this object.
     */

    public int hashCode()
    {
        return(getName().hashCode() ^ getActions().hashCode());
    }

    /**
     * Returns the current action mask.
     * <p>Used by the PackagePermissionCollection class.
     *
     * @return Current action mask.
     */
    int getMask()
    {
        return(action_mask);
    }

    /**
     * WriteObject is called to save the state of the <tt>ServicePermission</tt>
     * object to a stream. The actions are serialized, and the superclass
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
 * Stores a set of <tt>PackagePermission</tt> permissions.
 *
 * @see java.security.Permission
 * @see java.security.Permissions
 * @see java.security.PermissionCollection
 */

final class PackagePermissionCollection extends PermissionCollection
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
     * Create an empty PackagePermissions object.
     *
     */

    public PackagePermissionCollection()
    {
        permissions = new Hashtable();
        all_allowed = false;
    }

    /**
     * Adds a permission to the <tt>PackagePermission</tt> objects. The key for the hash is
     * the name.
     *
     * @param permission The <tt>PackagePermission</tt> object to add.
     *
     * @exception IllegalArgumentException If the permission is not a
     * <tt>PackagePermission</tt> instance.
     *
     * @exception SecurityException If this <tt>PackagePermissionCollection</tt>
     * object has been marked read-only.
     */

    public void add(Permission permission)
    {
        if (! (permission instanceof PackagePermission))
            throw new IllegalArgumentException("invalid permission: "+
                                               permission);
        if (isReadOnly())
            throw new SecurityException("attempt to add a Permission to a " +
                                        "readonly PermissionCollection");

        PackagePermission pp = (PackagePermission) permission;
        String name = pp.getName();

        PackagePermission existing =
        (PackagePermission) permissions.get(name);

        if (existing != null)
        {
            int oldMask = existing.getMask();
            int newMask = pp.getMask();
            if (oldMask != newMask)
            {
                permissions.put(name,
                                new PackagePermission(name,
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
     * Determines if the specified permissions implies the permissions
     * expressed in <tt>permission</tt>.
     *
     * @param p The Permission object to compare with this <tt>PackagePermission</tt> object.
     *
     * @return <tt>true</tt> if <tt>permission</tt> is a proper subset of a permission in
     * the set; <tt>false</tt> otherwise.
     */

    public boolean implies(Permission permission)
    {
        if (!(permission instanceof PackagePermission))
            return(false);

        PackagePermission pp = (PackagePermission) permission;
        PackagePermission x;

        int desired = pp.getMask();
        int effective = 0;

        // short circuit if the "*" Permission was added
        if (all_allowed)
        {
            x = (PackagePermission) permissions.get("*");
            if (x != null)
            {
                effective |= x.getMask();
                if ((effective & desired) == desired)
                    return(true);
            }
        }

        // strategy:
        // Check for full match first. Then work our way up the
        // name looking for matches on a.b.*

        String name = pp.getName();

        x = (PackagePermission) permissions.get(name);

        if (x != null)
        {
            // we have a direct hit!
            effective |= x.getMask();
            if ((effective & desired) == desired)
                return(true);
        }

        // work our way up the tree...
        int last, offset;

        offset = name.length()-1;

        while ((last = name.lastIndexOf(".", offset)) != -1)
        {

            name = name.substring(0, last+1) + "*";
            x = (PackagePermission) permissions.get(name);

            if (x != null)
            {
                effective |= x.getMask();
                if ((effective & desired) == desired)
                    return(true);
            }
            offset = last -1;
        }

        // we don't have to check for "*" as it was already checked
        // at the top (all_allowed), so we just return false
        return(false);
    }

    /**
     * Returns an enumeration of all <tt>PackagePermission</tt> objects in the
     * container.
     *
     * @return Enumeration of all <tt>PackagePermission</tt> objects.
     */

    public Enumeration elements()
    {
        return(permissions.elements());
    }
}





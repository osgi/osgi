/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2002).
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

package org.osgi.service.wireadmin;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Enumeration;
import java.security.Permission;
import java.security.BasicPermission;
import java.security.PermissionCollection;

/**
 * Permission for the scope of a <tt>Wire</tt> object. When a <tt>Envelope</tt> object is used for
 * communication with the <tt>poll</tt> or <tt>update</tt> method, and the
 * scope is set, then the <tt>Wire</tt> object must
 * verify that the Consumer service has <tt>WirePermission[name,CONSUME]</tt> and the
 * Producer service has <tt>WirePermission[name,PRODUCE]</tt> for all names in the scope.
 * <p>
 * The names are compared with the normal rules for permission names. This means
 * that they may end with a "*" to indicate wildcards. E.g. Door.* indicates all scope
 * names starting with the string "Door". The last period is required due to the
 * implementations of the <tt>BasicPermission</tt> class.
 *
 * @version $Revision$
 */
final public class WirePermission extends BasicPermission
{

    /**
     * The action string for the <tt>PRODUCE</tt> action: value is "produce".
     */
    public static final String PRODUCE  = "produce";

    /**
     * The action string for the <tt>CONSUME</tt> action: value is "consume".
     */
    public static final String CONSUME  = "consume";


    private final static int ACTION_PRODUCE         = 0x00000001;
    private final static int ACTION_CONSUME         = 0x00000002;
    private final static int ACTION_ALL             = ACTION_PRODUCE | ACTION_CONSUME;
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
     * Create a new WirePermission with the given name (may be wildcard) and actions.
     */
    public WirePermission( String name, String actions)
    {
        this(name, getMask(actions));
    }

    /**
     * Package private constructor used by WirePermissionCollection.
     *
     * @param name class name
     * @param action mask
     */
    WirePermission(String name, int mask)
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
            return mask;
        }

        char[] a = actions.toCharArray();

        int i = a.length - 1;
        if (i < 0)
            return mask;

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

            if (i >= 6 && (a[i-6] == 'p' || a[i-6] == 'P') &&
                (a[i-5] == 'r' || a[i-5] == 'R') &&
                (a[i-4] == 'o' || a[i-4] == 'O') &&
                (a[i-3] == 'd' || a[i-3] == 'D') &&
                (a[i-2] == 'u' || a[i-2] == 'U') &&
                (a[i-1] == 'c' || a[i-1] == 'C') &&
                (a[i]   == 'e' || a[i]   == 'E'))
            {
                matchlen = 7;
                mask |= ACTION_PRODUCE;
            }
            else
                if (i >= 6 && (a[i-6] == 'c' || a[i-6] == 'C') &&
                    (a[i-5] == 'o' || a[i-5] == 'O') &&
                    (a[i-4] == 'n' || a[i-4] == 'N') &&
                    (a[i-3] == 's' || a[i-3] == 'S') &&
                    (a[i-2] == 'u' || a[i-2] == 'U') &&
                    (a[i-1] == 'm' || a[i-1] == 'M') &&
                    (a[i]   == 'e' || a[i]   == 'E'))
            {
                matchlen = 7;
                mask |= ACTION_CONSUME;
            }
            else
            {
                // parse error
                throw new IllegalArgumentException(
                                                  "invalid permission: " + actions);
            }

            // make sure we didn't just match the tail of a word
            // like "ackbarfregister".  Also, skip to the comma.
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

        return mask;
    }

    /**
     * Checks if this <tt>WirePermission</tt> object <tt>implies</tt> the
     * specified permission.
     * <P>
     * More specifically, this method returns <tt>true</tt> if:<p>
     * <ul>
     * <li> <i>p</i> is an instanceof the <tt>WirePermission</tt> class,
     * <li> <i>p</i>'s actions are a proper subset of this
     * object's actions, and
     * <li> <i>p</i>'s name is implied by this object's name. For example,
     * <tt>java.*</tt> implies <tt>java.home</tt>.
     * </ul>
     * @param p The permission to check against.
     *
     * @return <tt>true</tt> if the specified permission is implied by this
     * object; <tt>false</tt> otherwise.
     */
    public boolean implies(Permission p)
    {
        if (p instanceof WirePermission)
        {
            WirePermission target = (WirePermission) p;
            return ((action_mask & target.action_mask) == target.action_mask) && super.implies(p);
        }

        return false;
    }

    /**
     * Returns the canonical string representation of the actions.
     * Always returns present actions in the following order:
     * <tt>produce</tt>, <tt>consume</tt>.
     * @return The canonical string representation of the actions.
     */
    public String getActions()
    {
        if (actions == null)
        {
            StringBuffer sb = new StringBuffer();
            boolean comma = false;

            if ((action_mask & ACTION_PRODUCE) == ACTION_PRODUCE)
            {
                sb.append(PRODUCE);
                comma = true;
            }

            if ((action_mask & ACTION_CONSUME) == ACTION_CONSUME)
            {
                if (comma) sb.append(',');
                sb.append(CONSUME);
            }

            actions = sb.toString();
        }

        return actions;
    }

    /**
     * Returns a new <tt>PermissionCollection</tt> object for storing
     * <tt>WirePermission</tt> objects.
     *
     * @return A new <tt>PermissionCollection</tt> object suitable for storing
     * <tt>WirePermission</tt> objects.
     */
    public PermissionCollection newPermissionCollection()
    {
        return new WirePermissionCollection();
    }

    /**
     * Determines the equalty of two <tt>WirePermission</tt> objects.
     *
     * Checks that specified object has the same name
     * and actions as this <tt>WirePermission</tt> object.
     *
     * @param obj The object to test for equality.
     * @return true if <tt>obj</tt> is a <tt>WirePermission</tt>, and has the
     * same name and actions as this <tt>WirePermission</tt> object; <tt>false</tt> otherwise.
     */
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (!(obj instanceof WirePermission))
        {
            return false;
        }

        WirePermission p = (WirePermission) obj;

        return (action_mask == p.action_mask) && getName().equals(p.getName());
    }

    /**
     * Returns the hash code value for this object.
     *
     * @return Hash code value for this object.
     */

    public int hashCode()
    {
        return getName().hashCode() ^ getActions().hashCode();
    }

    /**
     * Returns the current action mask.
     * Used by the WirePermissionCollection object.
     *
     * @return The actions mask.
     */
    int getMask()
    {
        return action_mask;
    }

    /**
     * Returns a string describing this <tt>WirePermission</tt>.
     * The convention is to specify the class name, the permission name, and
     * the actions in the following format:
     * '(org.osgi.service.wireadmin.WirePermission &quot;name&quot; &quot;actions&quot;)'.
     *
     * @return information about this <tt>Permission</tt> object.
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append('(');
        sb.append(getClass().getName());
        sb.append(" \"");
        sb.append(getName());
        sb.append("\" \"");
        sb.append(getActions());
        sb.append("\")");

        return sb.toString();
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
 * A <tt>WirePermissionCollection</tt> stores a set of <tt>WirePermission</tt>
 * permissions.
 */
final class WirePermissionCollection extends PermissionCollection
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
     * Creates an empty WirePermissionCollection object.
     *
     */

    public WirePermissionCollection()
    {
        permissions = new Hashtable();
        all_allowed = false;
    }

    /**
     * Adds a permission to this PermissionCollection.
     *
     * @param permission The Permission object to add.
     *
     * @exception IllegalArgumentException If the permission is not a WirePermission object.
     *
     * @exception SecurityException If this PermissionCollection has been marked read-only.
     */

    public void add(Permission permission)
    {
        if (! (permission instanceof WirePermission))
            throw new IllegalArgumentException("invalid permission: "+
                                               permission);
        if (isReadOnly())
            throw new SecurityException("attempt to add a Permission to a " +
                    "readonly PermissionCollection");

        WirePermission p = (WirePermission) permission;
        String name = p.getName();

        WirePermission existing =
        (WirePermission) permissions.get(name);

        if (existing != null)
        {
            int oldMask = existing.getMask();
            int newMask = p.getMask();
            if (oldMask != newMask)
            {
                permissions.put(name,
                new WirePermission(name,
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
        if (!(permission instanceof WirePermission))
            return false;

        WirePermission p = (WirePermission) permission;
        WirePermission x;

        int desired = p.getMask();
        int effective = 0;

        // short circuit if the "*" Permission was added
        if (all_allowed)
        {
            x = (WirePermission) permissions.get("*");
            if (x != null)
            {
                effective |= x.getMask();
                if ((effective & desired) == desired)
                    return true;
            }
        }

        // strategy:
        // Check for full match first. Then work our way up the
        // name looking for matches on a.b.*

        String name = p.getName();

        x = (WirePermission) permissions.get(name);

        if (x != null)
        {
            // we have a direct hit!
            effective |= x.getMask();
            if ((effective & desired) == desired)
                return true;
        }

        // work our way up the tree...
        int last, offset;

        offset = name.length()-1;

        while ((last = name.lastIndexOf(".", offset)) != -1)
        {

            name = name.substring(0, last+1) + "*";
            x = (WirePermission) permissions.get(name);

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
        return false;
    }

    /**
     * Returns an enumeration of all the Permission objects in the
     * container.
     *
     * @return Enumeration of all the Permission objects.
     */

    public Enumeration elements()
    {
        return permissions.elements();
    }
}


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

package org.osgi.service.useradmin;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Enumeration;
import java.security.Permission;
import java.security.BasicPermission;
import java.security.PermissionCollection;

/**
 * Permission to configure and access the {@link Role} objects managed by a
 * User Admin service.
 *
 * <p>This class represents access to the <tt>Role</tt> objects managed by a User Admin service and their
 * properties and credentials (in the case of {@link User} objects).
 * <p>
 * The permission name is the name (or name prefix) of a property or
 * credential.
 * The naming convention follows the hierarchical property naming convention.
 * Also, an asterisk may appear at the end of the name, following a
 * &quot;.&quot;, or by itself, to signify a wildcard match. For example:
 * &quot;org.osgi.security.protocol.*&quot; or &quot;*&quot; is valid, but
 * &quot;*protocol&quot; or &quot;a*b&quot; are not valid.
 *
 * <p>The <tt>UserAdminPermission</tt> with the reserved name &quot;admin&quot;
 * represents the permission required for creating and removing <tt>Role</tt> objects in
 * the User Admin service, as well as adding and removing members in a <tt>Group</tt> object. This
 * <tt>UserAdminPermission</tt> does not have any actions associated with it.
 *
 * <p>The actions to be granted are passed to the constructor in a string
 * containing a list of one or more comma-separated keywords.
 * The possible keywords are: <tt>changeProperty</tt>,
 * <tt>changeCredential</tt>, and <tt>getCredential</tt>.
 * Their meaning is defined as follows:
 * <pre>
 * action
 * changeProperty    Permission to change (i.e., add and remove)
 *                   Role object properties whose names start with
 *                   the name argument specified in the constructor.
 * changeCredential  Permission to change (i.e., add and remove)
 *                   User object credentials whose names start
 *                   with the name argument specified in the constructor.
 * getCredential     Permission to retrieve and check for the
 *                   existence of User object credentials whose names
 *                   start with the name argument specified in the
 *                   constructor.
 * </pre>
 * The action string is converted to lowercase before processing.
 *
 * <p>Following is a PermissionInfo style policy entry which grants a user
 * administration bundle a number of <tt>UserAdminPermission</tt> object:
 *
 * <pre>
 * (org.osgi.service.useradmin.UserAdminPermission "admin")
 * (org.osgi.service.useradmin.UserAdminPermission "com.foo.*" "changeProperty,getCredential,changeCredential")
 * (org.osgi.service.useradmin.UserAdminPermission "user.*", "changeProperty,changeCredential")
 * </pre>
 *
 * The first permission statement grants the bundle the permission to
 * perform any User Admin service operations of type "admin", that is, create and
 * remove roles and configure <tt>Group</tt> objects.
 *
 * <p>The second permission statement grants the bundle the permission to
 * change any properties as well as get and change any credentials
 * whose names start with <tt>com.foo.</tt>.
 *
 * <p>The third permission statement grants the bundle the permission to
 * change any properties and credentials whose names start with
 * <tt>user.</tt>. This means that the bundle is allowed to change,
 * but not retrieve any credentials with the given prefix.
 *
 * <p>The following policy entry empowers the Http Service bundle to perform user
 * authentication:
 * <pre>
 * grant codeBase "${jars}http.jar" {
 *   permission org.osgi.service.useradmin.UserAdminPermission
 *     "user.password", "getCredential";
 * };
 * </pre>
 * <p>The permission statement grants the Http Service bundle the permission to validate
 * any password credentials (for authentication purposes), but the bundle
 * is not allowed to change any properties or credentials.
 *
 * @version $Revision$
 */

public final class UserAdminPermission extends BasicPermission
{
    /**
     * The permission name &quot;admin&quot;.
     */
    public static final String ADMIN = "admin";

    /**
     * The action string &quot;changeProperty&quot;.
     */
    public static final String CHANGE_PROPERTY = "changeProperty";
    private static final int ACTION_CHANGE_PROPERTY = 0x1;

    /**
     * The action string &quot;changeCredential&quot;.
     */
    public static final String CHANGE_CREDENTIAL = "changeCredential";
    private static final int ACTION_CHANGE_CREDENTIAL = 0x2;

    /**
     * The action string &quot;getCredential&quot;.
     */
    public static final String GET_CREDENTIAL = "getCredential";
    private static final int ACTION_GET_CREDENTIAL = 0x4;

    /**
     * All actions
     */
    private static final int ACTION_ALL =
    ACTION_CHANGE_PROPERTY | ACTION_CHANGE_CREDENTIAL | ACTION_GET_CREDENTIAL;

    /**
     * No actions.
     */
    static final int ACTION_NONE = 0x0;

    /**
     * The actions in canonical form.
     *
     * @serial
     */
    private String actions = null;

    /**
     * The actions mask.
     */
    private transient int action_mask = ACTION_NONE;

    /* Description of this <tt>UserAdminPermission</tt> (returned by <tt>toString</tt>) */
    private transient String description;

    /**
     * Creates a new <tt>UserAdminPermission</tt> with the specified name and actions.
     * <tt>name</tt> is either the reserved string &quot;admin&quot; or the
     * name of a credential or property,
     * and <tt>actions</tt> contains a comma-separated list of the
     * actions granted on the specified name.
     * Valid actions are <tt>changeProperty</tt>,
     * <tt>changeCredential</tt>, and getCredential.
     *
     * @param name the name of this <tt>UserAdminPermission</tt>
     * @param actions the action string.
     *
     * @throws IllegalArgumentException If <tt>name</tt> equals
     * &quot;admin&quot; and <tt>actions</tt> are specified.
     */
    public UserAdminPermission(String name, String actions)
    {
        this(name, getMask(actions));
    }

    /**
     * Package private constructor used by <tt>UserAdminPermissionCollection</tt>.
     *
     * @param name class name
     * @param action mask
     */
    UserAdminPermission(String name, int mask)
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
        if (getName().equals(ADMIN))
        {
            if (mask != ACTION_NONE)
            {
                throw new IllegalArgumentException("Actions specified for " +
                                                   "no-action " +
                                                   "UserAdminPermission");
            }
        }
        else
        {
            if ((mask == ACTION_NONE) || ((mask & ACTION_ALL) != mask))
            {
                throw new IllegalArgumentException("Invalid action string");
            }
        }
        action_mask = mask;
    }

    /**
     * Parses the action string into the action mask.
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

            if (i >= 12 && match_get(a, i-10) && match_credential(a, i))
            {
                matchlen = 13;
                mask |= ACTION_GET_CREDENTIAL;

            }
            else if (i >= 13 && match_change(a, i-8) && match_property(a, i))
            {
                matchlen = 14;
                mask |= ACTION_CHANGE_PROPERTY;

            }
            else if (i >= 15 && match_change(a, i-10) && match_credential(a, i))
            {
                matchlen = 16;
                mask |= ACTION_CHANGE_CREDENTIAL;

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

    private static boolean match_change(char[] a, int i)
    {
        return((a[i-5] == 'c' || a[i-5] == 'C') &&
               (a[i-4] == 'h' || a[i-4] == 'H') &&
               (a[i-3] == 'a' || a[i-3] == 'A') &&
               (a[i-2] == 'n' || a[i-2] == 'N') &&
               (a[i-1] == 'g' || a[i-1] == 'G') &&
               (a[i-0] == 'e' || a[i-0] == 'E'));
    }

    private static boolean match_get(char[] a, int i)
    {
        return((a[i-2] == 'g' || a[i-2] == 'G') &&
               (a[i-1] == 'e' || a[i-1] == 'E') &&
               (a[i-0] == 't' || a[i-0] == 'T'));
    }

    private static boolean match_property(char[] a, int i)
    {
        return((a[i-7] == 'p' || a[i-7] == 'P') &&
               (a[i-6] == 'r' || a[i-6] == 'R') &&
               (a[i-5] == 'o' || a[i-5] == 'O') &&
               (a[i-4] == 'p' || a[i-4] == 'P') &&
               (a[i-3] == 'e' || a[i-3] == 'E') &&
               (a[i-2] == 'r' || a[i-2] == 'R') &&
               (a[i-1] == 't' || a[i-1] == 'T') &&
               (a[i-0] == 'y' || a[i-0] == 'Y'));
    }

    private static boolean match_credential(char[] a, int i)
    {
        return((a[i-9] == 'c' || a[i-9] == 'C') &&
               (a[i-8] == 'r' || a[i-8] == 'R') &&
               (a[i-7] == 'e' || a[i-7] == 'E') &&
               (a[i-6] == 'd' || a[i-6] == 'D') &&
               (a[i-5] == 'e' || a[i-5] == 'E') &&
               (a[i-4] == 'n' || a[i-4] == 'N') &&
               (a[i-3] == 't' || a[i-3] == 'T') &&
               (a[i-2] == 'i' || a[i-2] == 'I') &&
               (a[i-1] == 'a' || a[i-1] == 'A') &&
               (a[i-0] == 'l' || a[i-0] == 'L'));
    }

    /**
     * Checks if this <tt>UserAdminPermission</tt> object &quot;implies&quot; the
     * specified permission.
     * <P>
     * More specifically, this method returns <tt>true</tt> if:<p>
     * <ul>
     * <li> <i>p</i> is an instanceof <tt>UserAdminPermission</tt>,
     * <li> <i>p</i>'s actions are a proper subset of this
     * object's actions, and
     * <li> <i>p</i>'s name is implied by this object's name. For example,
     * &quot;java.*&quot; implies &quot;java.home&quot;.
     * </ul>
     * @param p the permission to check against.
     *
     * @return <tt>true</tt> if the specified permission is implied by this
     * object; <tt>false</tt> otherwise.
     */
    public boolean implies(Permission p)
    {
        if (p instanceof UserAdminPermission)
        {
            UserAdminPermission target = (UserAdminPermission) p;
            return(// Check that the we have the requested action
           ((target.action_mask & action_mask) == target.action_mask)&&
           // If the target action mask is ACTION_NONE, it must be an
           // admin permission, and then we must be that too
           (target.action_mask != ACTION_NONE ||
            action_mask == ACTION_NONE) &&
           // Check that name name matches
                   super.implies(p));
        }
        else
        {
            return (false);
        }
    }

    /**
     * Returns the canonical string representation of the actions, separated
     * by comma.
     *
     * @return the canonical string representation of the actions.
     */
    public String getActions()
    {
        if (actions == null)
        {
            StringBuffer sb = new StringBuffer();
            boolean comma = false;

            if ((action_mask & ACTION_CHANGE_CREDENTIAL) == ACTION_CHANGE_CREDENTIAL)
            {
                sb.append(CHANGE_CREDENTIAL);
                comma = true;
            }

            if ((action_mask & ACTION_CHANGE_PROPERTY) == ACTION_CHANGE_PROPERTY)
            {
                if (comma) sb.append(',');
                sb.append(CHANGE_PROPERTY);
                comma = true;
            }

            if ((action_mask & ACTION_GET_CREDENTIAL) == ACTION_GET_CREDENTIAL)
            {
                if (comma) sb.append(',');
                sb.append(GET_CREDENTIAL);
            }

            actions = sb.toString();
        }

        return (actions);
    }

    /**
     * Returns a new <tt>PermissionCollection</tt> object for storing
     * <tt>UserAdminPermission</tt> objects.
     *
     * @return a new <tt>PermissionCollection</tt> object suitable for storing
     * <tt>UserAdminPermission</tt> objects.
     */
    public PermissionCollection newPermissionCollection()
    {
        return(new UserAdminPermissionCollection());
    }

    /**
     * Checks two <tt>UserAdminPermission</tt> objects for equality.
     * Checks that <tt>obj</tt> is a <tt>UserAdminPermission</tt>, and has the same
     * name and actions as this object.
     *
     * @param obj the object to be compared for equality with this object.
     *
     * @return <tt>true</tt> if <tt>obj</tt> is a <tt>UserAdminPermission</tt> object,
     * and has the same name and actions as this <tt>UserAdminPermission</tt> object.
     */
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return (true);
        }
        if (obj instanceof UserAdminPermission)
        {
            UserAdminPermission uap = (UserAdminPermission)obj;
            return((action_mask == uap.action_mask)
                   && getName().equals(uap.getName()));
        }
        else
        {
            return (false);
        }
    }

    /**
     * Returns the hash code of this <tt>UserAdminPermission</tt> object.
     */
    public int hashCode()
    {
        return (getName().hashCode() ^ getActions().hashCode());
    }


    /**
     * Returns the current action mask.
     * Used by the <tt>UserAdminPermissionCollection</tt> class.
     *
     * @return the actions mask.
     */
    int getMask()
    {
        return (action_mask);
    }


    /**
     * writeObject is called to save the state of this object
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
    /*
     * Restores this object from a stream (i.e., deserializes it).
     */
    private synchronized void readObject(java.io.ObjectInputStream ois)
    throws IOException, ClassNotFoundException
    {
        ois.defaultReadObject();
        init(getMask(actions));
    }

    /**
     * Returns a string describing this <tt>UserAdminPermission</tt> object.
     * This string must be in <tt>PermissionInfo</tt> encoded format.
     *
     * @return The <tt>PermissionInfo</tt> encoded string for this <tt>UserAdminPermission</tt> object.
     * @see org.osgi.service.permissionadmin.PermissionInfo#getEncoded
     */
    public String toString()
    {
        if (description == null)
        {
            StringBuffer sb = new StringBuffer();

            sb.append('(');
            sb.append(getClass().getName());
            sb.append(" \"");
            sb.append(getName());
            String actions = getActions();
            if (actions.length() > 0)
            {
                sb.append("\" \"");
                sb.append(actions);
            }
            sb.append("\")");

            description = sb.toString();
        }

        return (description);
    }

}

/**
 * A <tt>UserAdminPermissionCollection</tt> stores a set of <tt>UserAdminPermission</tt>
 * permissions.
 */
final class UserAdminPermissionCollection extends PermissionCollection
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
     * Creates an empty <tt>UserAdminPermissionCollection</tt> object.
     */
    public UserAdminPermissionCollection()
    {
        permissions = new Hashtable();
        all_allowed = false;
    }

    /**
     * Adds the given permission to this <tt>UserAdminPermissionCollection</tt>.
     * The key for the hash is the name.
     *
     * @param permission the <tt>Permission</tt> object to add.
     *
     * @throws IllegalArgumentException If the given permission is not a
     * <tt>UserAdminPermission</tt>
     * @throws SecurityException If this <tt>UserAdminPermissionCollection</tt>
     * object has been marked readonly
     */
    public void add(Permission permission)
    {
        if (!(permission instanceof UserAdminPermission))
            throw new IllegalArgumentException("Invalid permission: "+
                                               permission);
        if (isReadOnly())
        {
            throw new SecurityException("Attempt to add a Permission to a " +
                                        "readonly PermissionCollection");
        }

        UserAdminPermission uap = (UserAdminPermission) permission;
        String name = uap.getName();

        UserAdminPermission existing =
        (UserAdminPermission) permissions.get(name);

        if (existing != null)
        {
            int oldMask = existing.getMask();
            int newMask = uap.getMask();
            if (oldMask != newMask)
            {
                permissions.put(name,
                                new UserAdminPermission(name,
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
     * Checks to see if this <tt>PermissionCollection</tt> implies the given
     * permission.
     *
     * @param permission the <tt>Permission</tt> object to check against
     *
     * @return true if the given permission is implied by this
     * <tt>PermissionCollection</tt>, false otherwise.
     */
    public boolean implies(Permission permission)
    {
        if (!(permission instanceof UserAdminPermission))
        {
            return(false);
        }

        UserAdminPermission uap = (UserAdminPermission) permission;
        UserAdminPermission x;

        int desired = uap.getMask();
        int effective = 0;

        // Short circuit if the "*" Permission was added.
        // desired can only be ACTION_NONE when name is "admin".
        if (all_allowed && desired != UserAdminPermission.ACTION_NONE)
        {
            x = (UserAdminPermission) permissions.get("*");
            if (x != null)
            {
                effective |= x.getMask();
                if ((effective & desired) == desired)
                {
                    return(true);
                }
            }
        }

        // strategy:
        // Check for full match first. Then work our way up the
        // name looking for matches on a.b.*
        String name = uap.getName();

        x = (UserAdminPermission) permissions.get(name);

        if (x != null)
        {
            // we have a direct hit!
            effective |= x.getMask();
            if ((effective & desired) == desired)
            {
                return(true);
            }
        }

        // work our way up the tree...
        int last;

        int offset = name.length()-1;

        while ((last = name.lastIndexOf(".", offset)) != -1)
        {
            name = name.substring(0, last+1) + "*";
            x = (UserAdminPermission) permissions.get(name);

            if (x != null)
            {
                effective |= x.getMask();
                if ((effective & desired) == desired)
                {
                    return(true);
                }
            }
            offset = last -1;
        }

        // we don't have to check for "*" as it was already checked
        // at the top (all_allowed), so we just return false
        return(false);
    }

    /**
     * Returns an enumeration of all the <tt>UserAdminPermission</tt> objects in the
     * container.
     *
     * @return an enumeration of all the <tt>UserAdminPermission</tt> objects.
     */
    public Enumeration elements()
    {
        return(permissions.elements());
    }
}

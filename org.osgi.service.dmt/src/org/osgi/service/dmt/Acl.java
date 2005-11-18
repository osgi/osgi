/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package org.osgi.service.dmt;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Arrays;
import java.util.Map;

/**
 * <code>Acl</code> is an immutable class representing structured access to
 * DMT ACLs. Under OMA DM the ACLs are defined as strings with an internal
 * syntax.
 * <p>
 * The methods of this class taking a principal as parameter accept remote
 * server IDs (as passed to
 * {@link DmtAdmin#getSession(String, String, int) DmtAdmin.getSession}), as
 * well as &quot; <code>*</code> &quot; indicating any principal.
 */
public final class Acl implements Cloneable {

    //----- Public constants -----//

    /**
     * Principals holding this permission can issue GET command on the node
     * having this ACL.
     */
    public static final int GET = 1;

    /**
     * Principals holding this permission can issue ADD commands on the node
     * having this ACL.
     */
    public static final int ADD = 2;

    /**
     * Principals holding this permission can issue REPLACE commands on the node
     * having this ACL.
     */
    public static final int REPLACE = 4;

    /**
     * Principals holding this permission can issue DELETE commands on the node
     * having this ACL.
     */
    public static final int DELETE = 8;

    /**
     * Principals holding this permission can issue EXEC commands on the node
     * having this ACL.
     */
    public static final int EXEC = 16;

    /**
     * Principals holding this permission can issue any command on the node
     * having this ACL. This permission is the logical OR of {@link #ADD},
     * {@link #DELETE}, {@link #EXEC}, {@link #GET} and {@link #REPLACE}
     * permissions.
     */
    public static final int ALL_PERMISSION =
        ADD | DELETE | EXEC | GET | REPLACE;
    
    //----- Private constants -----//

    private static final    int[] PERMISSION_CODES =
                        new int[] {  ADD,   DELETE,   EXEC,   GET,   REPLACE  };
    private static final String[] PERMISSION_NAMES =
                     new String[] { "Add", "Delete", "Exec", "Get", "Replace" };

    private static final String ALL_PRINCIPALS = "*";


    //----- Private fields -----//

        // the implementation takes advantage of this being a sorted map
    private TreeMap principalPermissions;
    private int globalPermissions;


    //----- Constructors -----//

    /**
     * Create an instance of the ACL that represents an empty list of principals
     * with no permissions.
     */
    public Acl()
    {
        clearPermissions();
    }

    /**
     * Create an instance of the ACL from its canonic string representation.
     * 
     * @param acl The string representation of the ACL as defined in OMA DM. If
     *        <code>null</code> or empty then it represents an empty list of
     *        principals with no permissions.
     * @throws IllegalArgumentException if acl is not a valid OMA DM ACL string
     */
    public Acl(String acl)
    {
        parseAcl(acl);
    }
    
    /**
     * Creates an instance with specifying the list of principals and the
     * permissions they hold. The two arrays run in parallel, that is
     * <code>principals[i]</code> will hold <code>permissions[i]</code> in
     * the ACL.
     * <p>
     * A principal name may not appear multiple times in the 'principals'
     * argument. If the &quot;*&quot; principal appears in the array, the
     * corresponding permissions will be granted to all principals (regardless
     * of whether they appear in the array or not).
     * 
     * @param principals The array of principals
     * @param permissions The array of permissions
     * @throws IllegalArgumentException if the length of the two arrays are not
     *         the same, if any array element is invalid, or if a principal
     *         appears multiple times in the <code>principals</code> array
     */
    public Acl(String[] principals, int[] permissions) {
        if(principals.length != permissions.length)
            throw new IllegalArgumentException(
                    "The lengths of the principal and permission arrays are not the same.");
        
        clearPermissions();
        
        for (int i = 0; i < principals.length; i++) {
            // allow one * in 'principals' array, remove after loop
            if(!ALL_PRINCIPALS.equals(principals[i]))
                checkPrincipal(principals[i]);
            checkPermissions(permissions[i]);
            // ### That previous line looks VERY fishy
        
            Integer permInt = new Integer(permissions[i]);
            Object old = principalPermissions.put(principals[i], permInt);                
            if(old != null)
                throw new IllegalArgumentException("Principal '" + 
                        principals[i] + 
                        "' appears multiple times in the principal array.");
        }
        
        // set the global permissions if there was a * in the array
        Object globalPermObj = principalPermissions.remove(ALL_PRINCIPALS);
        if(globalPermObj != null)
            globalPermissions = ((Integer) globalPermObj).intValue();
    }

    //----- Public methods -----//
   
    /**
     * Creates a copy of this ACL object.
     * 
     * @return a <code>Acl</code> instance describing the same permissions
     *         as this instance
     */
    public Object clone() {
        Acl cloned = null;
        try {
            cloned = (Acl) super.clone();
        } catch(CloneNotSupportedException e) {
            // never happens because this class is Cloneable
            throw new UnsupportedOperationException();
        }
        
        // make a shallow copy of the permission table, the keys (String) and
        // values (Integer) are immutable anyway
        cloned.principalPermissions = (TreeMap) principalPermissions.clone();
        
        return cloned;
    }

    /**
     * Checks whether the given object is equal to this <code>Acl</code>
     * instance. Two <code>Acl</code> instances are equal if they allow the
     * same set of permissions for the same set of principals.
     * 
     * @param obj the object to compare with this <code>Acl</code> instance
     * @return <code>true</code> if the parameter represents the same ACL as
     *         this instance
     */
    public boolean equals(Object obj) {
        if(obj == this)
            return true;
        
        if(!(obj instanceof Acl))
            return false;
        
        Acl other = (Acl) obj;
        
        if(globalPermissions != other.globalPermissions ||
                principalPermissions.size() != other.principalPermissions.size())
            return false;
        
        // principalPermissions sets cannot be easily compared, because they are
        // not canonical: the global permissions may or may not be present for 
        // each principal, without changing the meaning of the Acl object.
        
        // Compare canonical string representations, inefficient but simple.
        return toString().equals(other.toString());
    }
    
    /**
     * Returns the hash code for this ACL instance. If two <code>Acl</code>
     * instances are equal according to the {@link #equals} method, then calling
     * this method on each of them must produce the same integer result.
     * 
     * @return hash code for this ACL
     */
    public int hashcode() {
        // Using the hash code of the canonical string representation, because
        // the principalPermissions set is not canonical (see above). 
        return toString().hashCode();
    }

    /**
     * Create a new <code>Acl</code> instance by adding a specific
     * permission to a given principal. The already existing permissions of the
     * principal are not affected.
     * 
     * @param principal The entity to which permissions should be granted, or
     *        &quot;*&quot; to grant permissions to all principals.
     * @param permissions The permissions to be given. The parameter can be a
     *        logical <code>or</code> of more permission constants defined in
     *        this class.
     * @return a new <code>Acl</code> instance
     * @throws IllegalArgumentException if <code>principal</code> is not a
     *         valid principal name or if <code>permissions</code> is not a
     *         valid combination of the permission constants defined in this
     *         class
     */
    public synchronized Acl addPermission(String principal, int permissions)
    {
        checkPermissions(permissions);

        int oldPermissions = getPermissions(principal);
        return setPermission(principal, oldPermissions | permissions);
    }

    /**
     * Create a new <code>Acl</code> instance by revoking a specific
     * permission from a given principal. Other permissions of the principal are
     * not affected.
     * <p>
     * Note, that it is not valid to revoke a permission from a specific
     * principal if that permission is granted globally to all principals.
     * 
     * @param principal The entity from which permissions should be revoked, or
     *        &quot;*&quot; to revoke permissions from all principals.
     * @param permissions The permissions to be revoked. The parameter can be a
     *        logical <code>or</code> of more permission constants defined in
     *        this class.
     * @return a new <code>Acl</code> instance
     * @throws IllegalArgumentException if <code>principal</code> is not a
     *         valid principal name, if <code>permissions</code> is not a
     *         valid combination of the permission constants defined in this
     *         class, or if a globally granted permission would have been
     *         revoked from a specific principal
     */
    public synchronized Acl deletePermission(String principal, int permissions)
    {
        checkPermissions(permissions);

        int oldPermissions = getPermissions(principal);
        return setPermission(principal, oldPermissions & ~permissions);
    }

    /**
     * Get the permissions associated to a given principal.
     * 
     * @param principal The entity whose permissions to query, or &quot;*&quot;
     *        to query the permissions that are granted globally, to all
     *        principals
     * @return The permissions which the given principal has. The returned
     *         <code>int</code> is the logical <code>or</code> of the
     *         permission constants defined in this class.
     * @throws IllegalArgumentException if <code>principal</code> is not a
     *         valid principal name     
     */
    public synchronized int getPermissions(String principal)
    {
        int permissions = 0;

        if(!(ALL_PRINCIPALS.equals(principal))) {
            checkPrincipal(principal);
            Object po = principalPermissions.get(principal);
            if(po != null)
                permissions = ((Integer) po).intValue();
        }

        return permissions | globalPermissions;
    }

    /**
     * Check whether the given permissions are granted to a certain principal.
     * 
     * @param principal The entity to check, or &quot;*&quot; to check whether
     *        the given permissions are granted globally, to all principals
     * @param permissions The permission to check
     * @return <code>true</code> if the principal holds the given permission
     * @throws IllegalArgumentException if <code>principal</code> is not a
     *         valid principal name or if <code>permissions</code> is not a
     *         valid combination of the permission constants defined in this
     *         class
     */
    public synchronized boolean isPermitted(String principal, int permissions)
    {
        checkPermissions(permissions);

        int hasPermissions = getPermissions(principal);
        return (permissions & hasPermissions) == permissions;
    }

    /**
     * Create a new <code>Acl</code> instance by setting the list of
     * permissions a given principal has. All permissions the principal had will
     * be overwritten.
     * <p>
     * Note, that when changing the permissions of a specific principal, it is
     * not allowed to specify a set of permissions stricter than the global set
     * of permissions (that apply to all principals).
     * 
     * @param principal The entity to which permissions should be granted, or
     *        &quot;*&quot; to grant permissions globally, to all principals.
     * @param permissions The set of permissions to be given. The parameter can
     *        be a logical <code>or</code> of the permission constants defined
     *        in this class.
     * @return a new <code>Acl</code> instance
     * @throws IllegalArgumentException if <code>principal</code> is not a
     *         valid principal name, if <code>permissions</code> is not a
     *         valid combination of the permission constants defined in this
     *         class, or if a globally granted permission would have been
     *         revoked from a specific principal
     */
    public synchronized Acl setPermission(String principal, int permissions)
    {
        checkPermissions(permissions);
        
        Acl newPermission = (Acl) clone();
        newPermission.changePermission(principal, permissions);
        return newPermission;
    }

    /**
     * Get the list of principals who have any kind of permissions on this node.
     * The list only includes those principals that have been explicitly
     * assigned permissions (so &quot;*&quot; is never returned), globally set 
     * permissions naturally apply to all other principals as well.
     * 
     * @return The array of principals having permissions on this node.
     */
    public String[] getPrincipals()
    {
        return (String[])(principalPermissions.keySet().toArray(new String[0]));
    }

    /**
     * Give the canonic string representation of this ACL. The operations are in
     * the following order: {Add, Delete, Exec, Get, Replace}, principal names
     * are sorted alphabetically.
     * 
     * @return The string representation as defined in OMA DM.
     */
    public synchronized String toString()
    {
        String acl = null;
        for(int i = 0; i < PERMISSION_CODES.length; i++)
            acl = writeEntry(PERMISSION_CODES[i], acl);

        return (acl != null) ? acl : "";
    }


    //----- Private utility methods -----//

    private String writeEntry(int command, String acl)
    {
        String aclEntry = null;

        if((command & globalPermissions) > 0)
            aclEntry = ALL_PRINCIPALS;
        else {
            // TreeMap guarantees alphabetical ordering of keys during traversal
            Iterator i = principalPermissions.entrySet().iterator();
            while(i.hasNext()) {
                Map.Entry entry = (Map.Entry) i.next();
                if((command & ((Integer) entry.getValue()).intValue()) > 0)
                    aclEntry = appendEntry(aclEntry, '+',
                                           (String) entry.getKey());
            }
        }

        if(aclEntry == null)
            return acl;

        return appendEntry(acl, '&', writeCommands(command) + '=' + aclEntry);
    }

    /**
     * Set the list of permissions a given principal has. All permissions the
     * principal had will be overwritten.
     * <p>
     * Assumes that the permissions parameter has been checked. All
     * modifications of a <code>Acl</code> instance (add, delete, set) are
     * done through this method.
     * 
     * @param principal The entity to which permission should be granted.
     * @param permissions The set of permissions to be given. The parameter can
     *        be a logical <code>or</code> of the permission constants defined
     *        in this class.
     */
    private void changePermission(String principal, int permissions) {
        if(ALL_PRINCIPALS.equals(principal)) {
            deleteFromAll(globalPermissions & ~permissions);
            globalPermissions = permissions;
        }
        else {
            checkPrincipal(principal);
            
            int deletedGlobalPerm = globalPermissions & ~permissions;
            if(deletedGlobalPerm != 0)
                throw new IllegalArgumentException(
                        "Cannot revoke globally set permissions (" +
                        writeCommands(deletedGlobalPerm) +
                        ") from a specific principal (" + principal + ").");
            
            setPrincipalPermission(principal, permissions);
        }
    }
    
    private void deleteFromAll(int perm)
    {
        Iterator i = principalPermissions.entrySet().iterator();
        while(i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            setPrincipalPermission((String) entry.getKey(),
                ((Integer) entry.getValue()).intValue() & ~perm);
        }
    }

    private void setPrincipalPermission(String principal, int perm)
    {
        if(perm == 0)
            principalPermissions.remove(principal);
        else
            principalPermissions.put(principal, new Integer(perm));
    }

    private void clearPermissions()
    {
        principalPermissions = new TreeMap();
        globalPermissions = 0;
    }

    // ACL commands and server IDs are treated case-sensitively
    private void parseAcl(String acl)
    {
        clearPermissions();

        if(acl == null)
            return;

        if(acl.equals(""))
            return;             // empty permission set

        String[] aclEntries = split(acl, '&', -1);
        for(int i = 0; i < aclEntries.length; i++) {
            if(aclEntries[i].length() == 0)
                throw new IllegalArgumentException(
                    "Invalid ACL string: empty ACL entry.");

            String[] entryParts = split(aclEntries[i], '=', 2);
            if(entryParts.length == 1)
                throw new IllegalArgumentException(
                    "Invalid ACL string: no '=' in ACL entry.");
            if(entryParts[1].length() == 0)
                throw new IllegalArgumentException(
                    "Invalid ACL string: no server identifiers in ACL entry.");

            int command = parseCommand(entryParts[0]);
            String[] serverIds = split(entryParts[1], '+', -1);
            for(int j = 0; j < serverIds.length; j++) {
                if(serverIds[j].length() == 0)
                    throw new IllegalArgumentException(
                        "Invalid ACL string: empty server identifier.");

                if(serverIds[j].equals(ALL_PRINCIPALS))
                    globalPermissions |= command;
                else {
                    checkServerId(serverIds[j], "Invalid ACL string: " +
                                  "server ID contains illegal character");
                    Integer n = (Integer)
                        principalPermissions.get(serverIds[j]);
                    int oldPermission = (n != null) ? n.intValue() : 0;
                    principalPermissions.put(serverIds[j],
                        new Integer(oldPermission | command));
                }
            }
        }
    }


    private static String writeCommands(int command)
    {
        String commandStr = null;
        for(int i = 0; i < PERMISSION_CODES.length; i++)
            if((command & PERMISSION_CODES[i]) != 0)
                commandStr = appendEntry(commandStr, ',', PERMISSION_NAMES[i]);

        return (commandStr != null) ? commandStr : "";
    }

    private static String appendEntry(String base, char separator, String entry)
    {
        return (base != null) ? base + separator + entry : entry;
    }

    private static int parseCommand(String command)
    {
        int i = Arrays.asList(PERMISSION_NAMES).indexOf(command);
        if(i == -1)
            throw new IllegalArgumentException(
                "Invalid ACL string: unknown command '" + command + "'.");

        return PERMISSION_CODES[i];
    }

    private static void checkPermissions(int perm)
    {
        if((perm & ~ALL_PERMISSION) != 0)
            throw new IllegalArgumentException(
                "Invalid ACL permission value: " + perm);
    }

    private static void checkPrincipal(String principal)
    {
        if(principal == null)
            throw new IllegalArgumentException("Principal is null.");

        checkServerId(principal, "Principal name contains illegal character");
    }

    private static void checkServerId(String serverId, String errorText)
    {
        char[] chars = serverId.toCharArray();
        for(int i = 0; i < chars.length; i++)
            if("*=+&".indexOf(chars[i]) != -1 ||
               Character.isWhitespace(chars[i]))
                throw new IllegalArgumentException(errorText + " '" +
                                                   chars[i] + "'.");
    }


    private static String[] split(String input, char sep, int limit) {
        Vector v = new Vector();
        boolean limited = (limit > 0);
        int applied = 0;
        int index = 0;
        StringBuffer part = new StringBuffer();

        while (index < input.length()) {
            char ch = input.charAt(index);
            if (ch != sep)
                part.append(ch);
            else {
                ++applied;
                v.add(part.toString());
                part = new StringBuffer();
            }
            ++index;
            if (limited && applied == limit - 1)
                break;
        }
        while (index < input.length()) {
            char ch = input.charAt(index);
            part.append(ch);
            ++index;
        }
        v.add(part.toString());

        int last = v.size();
        if (0 == limit) {
            for (int j = v.size()-1; j >= 0; --j) {
                String s = (String) v.elementAt(j);
                if ("".equals(s))
                    --last;
                else
                    break;
            }
        }

        String[] ret = new String[last];
        for (int i = 0; i < last; ++i)
            ret[i] = (String) v.elementAt(i);

        return ret;
    }
}

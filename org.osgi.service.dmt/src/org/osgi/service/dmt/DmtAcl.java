package org.osgi.service.dmt;

import java.util.*;

// TODO write equals() and hashCode() if needed

/**
 * The DmtAcl class represents structured access to DMT ACLs. Under OMA DM 
 * the ACLs are defined as strings with an internal syntax. 
 * <p> 
 * The methods of this class taking a principal as parameter will accept
 * string values used in the constructor of the DmtPrincipal class,
 * as well as "<code>*</code>" indicating any principal.  
 */
public class DmtAcl {
    
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
    

    //----- Private constants -----//

    private static final    int[] PERMISSION_CODES = 
                        new int[] {  ADD,   DELETE,   EXEC,   GET,   REPLACE  };
    private static final String[] PERMISSION_NAMES = 
                     new String[] { "Add", "Delete", "Exec", "Get", "Replace" };

    private static final int ALL_PERMISSION = 
        ADD | DELETE | EXEC | GET | REPLACE;

    private static final String ALL_PRINCIPALS = "*";


    //----- Private fields -----//

    private Hashtable principalPermissions;
    private int globalPermissions;


    //----- Constructors -----//

    /**
     * Create an instance of the ACL that represents an empty list of
     * principals with no permissions.
     */
    public DmtAcl()
    {
        clearPermissions();
    }

    /**
     * Create an instance of the ACL from its canonic string
     * representation.
     * @param acl The string representation of the ACL as defined in
     * OMA DM.  If <code>null</code> then it represents an empty list
     * of principals with no permissions.
     * @throws IllegalArgumentException if acl is not a valid OMA DM
     * ACL string
     */
    public DmtAcl(String acl)
    {
        parseAcl(acl);
    }

    /**
     * Creates an instance of the ACL that represents the same
     * permissions as the parameter ACL object.
     * @throws IllegalArgumentException if the given ACL object is not
     * consistent (e.g. some principals do not have all the global
     * permissions), or if the parameter changes during the call
     */
    public DmtAcl(DmtAcl acl)
    {
        principalPermissions = new Hashtable();
        globalPermissions = acl.getPermissions(ALL_PRINCIPALS);

        Iterator i = acl.getPrincipals().iterator();
        while(i.hasNext()) {
            String principal = (String) i.next();
            int perm = acl.getPermissions(principal);
            if((perm & globalPermissions) != globalPermissions)
                throw new IllegalArgumentException("Invalid parameter ACL, " +
                    "all principals must have all global permissions.");
            if(perm != 0)
                principalPermissions.put(principal, new Integer(perm));
        }
    }

    
    //----- Public methods -----//
    
    /**
     * Add a specific permission to a given principal. The already
     * existing permissions of the principal are not affected.
     * @param principal The entity to which permission should be
     * granted.
     * @param permissions The permissions to be given. The parameter
     * can be a logical <code>or</code> of more permission constants
     * defined in this class.
     */
    public synchronized void addPermission(String principal, int permissions)
    {
        checkPermissions(permissions);

        int oldPermissions = getPermissions(principal);
        setPermission(principal, oldPermissions | permissions);
    }
    
    /**
     * Revoke a specific permission from a given principal. Other
     * permissions of the principal are not affected.
     * @param principal The entity from which a permission should be
     * revoked.
     * @param permissions The permissions to be revoked. The parameter
     * can be a logical <code>or</code> of more permission constants
     * defined in this class.
     */
    public synchronized void deletePermission(String principal, int permissions)
    {
        checkPermissions(permissions);

        int oldPermissions = getPermissions(principal);
        setPermission(principal, oldPermissions & ~permissions);
    }
    
    /**
     * Get the permissions associated to a given principal.
     * @param principal The entity whose permissions to query
     * @return The permissions which the given principal has. The
     * returned <code>int</code> is the logical <code>or</code> of the
     * permission constants defined in this class.
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
     * Check whether a given permission is given to a certain
     * principal.
     * @param principal The entity to check
     * @param permissions The permission to check
     * @return <code>true</code> if the principal holds the given
     * permission
     */
    public synchronized boolean isPermitted(String principal, int permissions)
    {
        checkPermissions(permissions);

        int hasPermissions = getPermissions(principal);
        return (permissions & hasPermissions) == permissions;
    }
    
    /**
     * Set the list of permissions a given principal has. All
     * permissions the principal had will be overwritten.
     * @param principal The entity to which permission should be
     * granted.
     * @param permissions The set of permissions to be given. The
     * parameter can be a logical <code>or</code> of the permission
     * constants defined in this class.
     */
    public synchronized void setPermission(String principal, int permissions)
    {
        checkPermissions(permissions);

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

    /**
     * Get the list of principals who have any kind of permissions on
     * this node.
     * @return The set of principals having permissions on this node.
     */
    public Set getPrincipals()
    {
        return principalPermissions.keySet();
       // TODO: Hashset is not in mimimum
     //   return new Hashtable(principalPermissions.keySet());
    }

    /** 
     * Give the canonic string representation of this ACL.
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
        principalPermissions = new Hashtable();
        globalPermissions = 0;
    }

    // TODO find out if ACL string (commands and/or server IDs) should be case insensitive
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
        // TODO is there any other syntactic requirement for principal names?
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

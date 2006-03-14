/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.service.dmt.security;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.osgi.service.dmt.Acl;

/**
 * Controls access to management objects in the Device Management Tree (DMT). 
 * It is intended to control local access to the DMT. DmtPermission target 
 * string identifies the management object URI and the action field lists the 
 * OMA DM commands that are permitted on the management object. Example:
 *
 * <pre>
 * DmtPermission(&quot;./OSGi/bundles&quot;, &quot;Add,Replace,Get&quot;);
 * </pre>
 *
 * This means that owner of this permission can execute Add, Replace
 * and Get commands on the ./OSGi/bundles management object. It is
 * possible to use wildcards in both the target and the actions
 * field. Wildcard in the target field means that the owner of the
 * permission can access children nodes of the target node. Example
 *
 * <pre>
 * DmtPermission(&quot;./OSGi/bundles/*&quot;, &quot;Get&quot;);
 * </pre>
 *
 * This means that owner of this permission has Get access on every
 * child node of ./OSGi/bundles. The asterix does not necessarily have
 * to follow a '/' character. For example the 
 * <code>&quot;./OSGi/a*&quot;<code> target matches the 
 * <code>./OSGi/applications</code> subtree.
 * <p>
 * If wildcard is present in the actions field, all legal OMA DM commands are 
 * allowed on the designated nodes(s) by the owner of the permission.  Action
 * names are interpreted case-insensitively, but the canonical action string
 * returned by {@link #getActions} uses the forms defined by the action 
 * constants.
 */
public class DmtPermission extends Permission {
    // TODO serialization
    // TODO add static final serialVersionUID

    /**
     * Holders of DmtPermission with the Add action present can create new nodes
     * in the DMT, that is they are authorized to execute the
     * createInteriorNode() and createLeafNode() methods of the DmtSession.
     * This action is also required for the copy() command, which needs to 
     * perform node creation operations (among others). 
     */
    public static final String ADD = "Add";

    /**
     * Holders of DmtPermission with the Delete action present can delete nodes
     * from the DMT, that is they are authorized to execute the deleteNode()
     * method of the DmtSession.
     */
    public static final String DELETE = "Delete";

    /**
     * Holders of DmtPermission with the Exec action present can execute nodes
     * in the DMT, that is they are authorized to call the execute() method of
     * the DmtSession.
     */
    public static final String EXEC = "Exec";

    /**
     * Holders of DmtPermission with the Get action present can query DMT node
     * value or properties, that is they are authorized to execute the
     * isLeafNode(), getNodeAcl(), getEffectiveNodeAcl(), getMetaNode(),
     * getNodeValue(), getChildNodeNames(), getNodeTitle(), getNodeVersion(),
     * getNodeTimeStamp(), getNodeSize() and getNodeType() methods of the
     * DmtSession.  This action is also required for the copy() command, which
     * needs to perform node query operations (among others).
     */
    public static final String GET = "Get";

    /**
     * Holders of DmtPermission with the Replace action present can update DMT
     * node value or properties, that is they are authorized to execute the
     * setNodeAcl(), setNodeTitle(), setNodeValue(), setNodeType() and 
     * renameNode() methods of the DmtSession.  This action is also be required 
     * for the copy() command if the original node had a title property (which 
     * must be set in the new node).
     */
    public static final String REPLACE = "Replace";

    // does this permission have a wildcard at the end?
    private boolean prefixPath;

    // the name without the wildcard on the end
    private String path;
    
    // the actions mask
    private int mask;
    
    // the canonical action string (redundant)
    private String actions;

    // initializes the member fields from the given URI and actions mask
    private void init(String dmtUri, int mask) {
        if(dmtUri == null || dmtUri.length() == 0)
            throw new NullPointerException(
                    "'dmtUri' parameter cannot be null or empty.");
        
        // TODO check all other constraints for URIs
        // URI must be absolute, i.e. equal to . or beginning with ./
        if(!dmtUri.startsWith("./") && !dmtUri.equals("."))
            throw new IllegalArgumentException(
                    "'dmtUri' parameter is not an absolute URI.");
        
        if (dmtUri.endsWith("*")) {
            prefixPath = true;
            path = dmtUri.substring(0, dmtUri.length() - 1);
        }
        else {
            // Trailing slash ignored
            if(dmtUri.endsWith("/"))
                dmtUri = dmtUri.substring(0, dmtUri.length() - 1);
            
            prefixPath = false;
            path = dmtUri;
        }
        
        if(mask == 0)
            throw new IllegalArgumentException("Action mask cannot be empty.");
        
        this.mask = mask;
        
        actions = canonicalActions(mask);
    }

    /**
     * Creates a new DmtPermission object for the specified DMT URI with the
     * specified actions. The given URI must be an absolute URI, possibly ending
     * with the "*" wildcard. The actions string must either be "*" to allow all
     * actions, or it must contain a non-empty subset of the valid actions,
     * defined as constants in this class.
     * 
     * @param dmtUri URI of the management object (or subtree)
     * @param actions OMA DM actions allowed
     * @throws NullPointerException if any of the parameters are
     *         <code>null</code>
     * @throws IllegalArgumentException if any of the parameters are invalid
     */
    public DmtPermission(String dmtUri, String actions) {
        super(dmtUri);
        
        init(dmtUri, getMask(actions));
    }

    /**
     * Checks whether the given object is equal to this DmtPermission instance.
     * Two DmtPermission instances are equal if they have the same target string
     * and the same action mask.  The "*" action mask is considered equal to a
     * mask containing all actions.
     * 
     * @param obj the object to compare to this DmtPermission instance
     * @return <code>true</code> if the parameter represents the same
     *         permissions as this instance
     */
    public boolean equals(Object obj) {
        if(obj == this)
            return true;
        
        if(!(obj instanceof DmtPermission))
            return false;

        DmtPermission other = (DmtPermission) obj;

        return
            mask == other.mask && 
            prefixPath == other.prefixPath &&
            path.equals(other.path);
    }

    /**
     * Returns the String representation of the action list. The allowed actions
     * are listed in the following order: Add, Delete, Exec, Get, Replace. The
     * wildcard character is not used in the returned string, even if the class
     * was created using the "*" wildcard.
     * 
     * @return canonical action list for this permission object
     */
    public String getActions() {
        return actions;
    }

    /**
     * Returns the hash code for this permission object. If two DmtPermission
     * objects are equal according to the {@link #equals} method, then calling
     * this method on each of the two DmtPermission objects must produce the
     * same integer result.
     * 
     * @return hash code for this permission object
     */
    public int hashCode() {
        return
            new Integer(mask).hashCode() ^
            new Boolean(prefixPath).hashCode() ^ 
            path.hashCode();
    }

    /**
     * Checks if this DmtPermission object &quot;implies&quot; the
     * specified permission.  This method returns <code>false</code> if and only
     * if at least one of the following conditions are fulfilled for the 
     * specified permission:
     * <ul>
     * <li>it is not a DmtPermission
     * <li>its set of actions contains an action not allowed by this permission
     * <li>the set of nodes defined by its path contains a node not defined by
     * the path of this permission 
     * </ul>
     *
     * @param p the permission to check for implication
     * @return true if this DmtPermission instance implies the specified
     * permission
     */
    public boolean implies(Permission p) {
        if(!(p instanceof DmtPermission))
            return false;

        DmtPermission other = (DmtPermission) p;

        if((mask & other.mask) != other.mask)
            return false;

        return impliesPath(other);
    }

    /**
     * Returns a new PermissionCollection object for storing
     * DmtPermission objects.
     *
     * @return the new PermissionCollection
     */
    public PermissionCollection newPermissionCollection() {
        return new DmtPermissionCollection();
    }
    
    // parses the given action string, and returns the corresponding action mask
    private static int getMask(String actions) {
        int mask = 0;

        if(actions == null)
            throw new NullPointerException(
                    "'actions' parameter cannot be null.");

        if(actions.equals("*"))
            return Acl.ALL_PERMISSION;

        // empty tokens (swallowed by StringTokenizer) are not considered errors
        StringTokenizer st = new StringTokenizer(actions, ",");
        while(st.hasMoreTokens()) {
            String action = st.nextToken();
            if (action.equalsIgnoreCase(GET)) {
                mask |= Acl.GET;
            } else if (action.equalsIgnoreCase(ADD)) {
                mask |= Acl.ADD;
            } else if (action.equalsIgnoreCase(REPLACE)) {
                mask |= Acl.REPLACE;
            } else if (action.equalsIgnoreCase(DELETE)) {
                mask |= Acl.DELETE;
            } else if (action.equalsIgnoreCase(EXEC)) {
                mask |= Acl.EXEC;
            } else
                throw new IllegalArgumentException(
                        "Invalid action '" + action + "'");
        }
        
        return mask;
    }

    // generates the canonical string representation of the action list
    private static String canonicalActions(int mask) {
        StringBuffer sb = new StringBuffer();
        addAction(sb, mask, Acl.ADD,     ADD);
        addAction(sb, mask, Acl.DELETE,  DELETE);
        addAction(sb, mask, Acl.EXEC,    EXEC);
        addAction(sb, mask, Acl.GET,     GET);
        addAction(sb, mask, Acl.REPLACE, REPLACE);
        return sb.toString();
    }
    
    // if 'flag' appears in 'mask', appends the 'action' string to the contents
    // of 'sb', separated by a comma if needed
    private static void addAction(StringBuffer sb, int mask, int flag, 
            String action) {
        if((mask & flag) != 0) {
            if(sb.length() > 0)
                sb.append(',');
            sb.append(action);
        }
    }

    // used by DmtPermissionCollection to retrieve the action mask
    int getMask() {
        return mask;
    }

    // returns true if the path parameter of the given DmtPermission is
    // implied by the path of this permission, i.e. this path is a prefix of the
    // other path, but ends with a *, or the two path strings are equal
    boolean impliesPath(DmtPermission p) {
        return prefixPath ? p.path.startsWith(path) :
            !p.prefixPath && p.path.equals(path);
    }
}

/**
 * Represents a homogeneous collection of DmtPermission objects.
 */
final class DmtPermissionCollection extends PermissionCollection {
    // OPTIMIZE keep a special flag for permissions of "*" path
    // TODO serialization
    
    private List perms;
    
    /**
     * Create an empty DmtPermissionCollection object.
     */
    public DmtPermissionCollection() {
        perms = new ArrayList();
    }
    
    /**
     * Adds a permission to the DmtPermissionCollection.
     * 
     * @param permission the Permission object to add
     * @exception IllegalArgumentException if the permission is not a
     *            DmtPermission
     * @exception SecurityException if this DmtPermissionCollection object has
     *            been marked readonly
     */
    public void add(Permission permission) {
        if (!(permission instanceof DmtPermission))
            throw new IllegalArgumentException(
                    "Cannot add permission, invalid permission type: "
                            + permission);
        if (isReadOnly())
            throw new SecurityException(
                    "Cannot add permission, collection is marked read-only.");

        // No need to synchronize because all adds are done sequentially
        // before any implies() calls
        perms.add(permission);
    }
    
    /**
     * Check whether this set of permissions implies the permission specified in
     * the parameter.
     * 
     * @param permission the Permission object to compare
     * @return true if the parameter permission is a proper subset of the
     *         permissions in the collection, false otherwise
     */
    public boolean implies(Permission permission) 
    {
        if (!(permission instanceof DmtPermission))
            return false;
        
        DmtPermission other = (DmtPermission) permission;
        
        int required = other.getMask();
        int available = 0;
        int needed = required;
        
        Iterator i = perms.iterator();
        while (i.hasNext()) {
            DmtPermission p = (DmtPermission) i.next();
            if (((needed & p.getMask()) != 0) && p.impliesPath(other)) {
                available |=  p.getMask();
                if ((available & required) == required)
                    return true;
                needed = (required ^ available);
            }
        }

        return false;
    }
    
    /**
     * Returns an enumeration of all the DmtPermission objects in the container.
     * The returned value cannot be <code>null</code>.
     * 
     * @return an enumeration of all the DmtPermission objects
     */
    public Enumeration elements() {
        // Convert Iterator into Enumeration
        return Collections.enumeration(perms);
    }
}

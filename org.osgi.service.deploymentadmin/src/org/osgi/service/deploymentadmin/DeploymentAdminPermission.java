/*
 * $Header$
 * 
 * Copyright (c) The OSGi Alliance (2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.service.deploymentadmin;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * DeploymentAdminPermission controls access to MEG management framework functions.
 * This permission controls only Deployment Admin-specific functions;
 * framework-specific access is controlled by usual OSGi permissions
 * (<code>AdminPermission</code>, etc.) <p>
 * In addition to <code>DeploymentAdminPermission</code>, the caller
 * of Deployment Admin must hold the appropriate <code>AdminPermission</code>s.<p>
 * For example, installing a deployment package requires <code>DeploymentAdminPermission</code>
 * to access the <code>installDeploymentPackage</code> method and <code>AdminPermission</code> to access
 * the framework's install/update/uninstall methods. <p>
 * The permission uses a &lt;filter&gt; string formatted similarly to the filter in RFC 73.
 * The <code>DeploymentAdminPermission</code> filter does not use the id and location filters.
 * The "signer" filter is matched against the signer chain of the deployment package, and
 * the "name" filter is matched against the DeploymentPackage-Name header.
 * <pre>
 * DeploymentAdminPermission( "&lt;filter&gt;", "list" )<p>
 * </pre>
 * A holder of this permission can access the inventory information of the deployment
 * packages selected by the &lt;filter&gt; string. The filter selects the deployment packages
 * on which the holder of the permission can acquire detailed inventory information.
 * See {@link DeploymentAdmin#getDeploymentPackage} and
 * {@link DeploymentAdmin#listDeploymentPackages}.
 * <pre>
 * DeploymentAdminPermission( "&lt;filter&gt;", "install" )
 * </pre>
 * A holder of this permission can install/upgrade deployment packages if the deployment
 * package satisfies the &lt;filter&gt; string. See {@link DeploymentAdmin#installDeploymentPackage}.
 * <pre>
 * DeploymentAdminPermission( "&lt;filter&gt;", "uninstall" )
 * </pre>
 * A holder of this permission can uninstall deployment packages if the deployment
 * package satisfies the &lt;filter&gt; string. See {@link DeploymentPackage#uninstall}.
 * <pre>
 * DeploymentAdminPermission( "&lt;filter&gt;", "uninstallForceful" )
 * </pre>
 * A holder of this permission can forcefully uninstall deployment packages if the deployment
 * package satisfies the  string. See {@link DeploymentPackage#uninstallForceful}.
 * <pre>
 * DeploymentAdminPermission( "&lt;filter&gt;", "cancel" )
 * </pre>
 * A holder of this permission can cancel an active deployment action. This action being
 * cancelled could correspond to the install, update or uninstall of a deployment package
 * that satisfies the  string. See {@link DeploymentAdmin#cancel}<p>
 * Wildcards can be used both in the name and the signer (see RFC-95) filters.<p>
 */
public class DeploymentAdminPermission extends Permission {
    
    /**
     * Constant String to the "install" action.
     */
    public static final String ACTION_INSTALL            = "install";

    /**
     * Constant String to the "list" action.
     */
    public static final String ACTION_LIST               = "list";
    
    /**
     * Constant String to the "uninstall" action.
     */
    public static final String ACTION_UNINSTALL          = "uninstall";

    /**
     * Constant String to the "uninstallForced" action.
     */
    public static final String ACTION_UNINSTALL_FORCED   = "uninstallForced";
    
    /**
     * Constant String to the "cancel" action.
     */
    public static final String ACTION_CANCEL             = "cancel";  
    
    private static final Vector ACTIONS = new Vector();
    static {
        ACTIONS.add(ACTION_INSTALL.toLowerCase());
        ACTIONS.add(ACTION_LIST.toLowerCase());
        ACTIONS.add(ACTION_UNINSTALL.toLowerCase());
        ACTIONS.add(ACTION_UNINSTALL_FORCED.toLowerCase());
        ACTIONS.add(ACTION_CANCEL.toLowerCase());
    }
    
    private           String actions;
    private transient Vector actionsVector;
    private transient Representation rep;
    
    /**
     * Creates a new <code>DeploymentAdminPermission</code> object for the given name (containing the name
     * of the target deployment package) and action.<p>
     * The <code>name</code> parameter identifies the target depolyment package the permission 
     * relates to. The <code>actions</code> parameter contains the comma separated list of allowed actions. 
     * @param name Target string, must not be null.
     * @param action Action string, must not be null.
     * @throws IllegalArgumentException if the filter is invalid or the list of actions 
     *         contains unknown operations
     */
    public DeploymentAdminPermission(String name, String actions) {
        // TODO canonicalize "name"
        super(name);
        this.actions = actions;
        rep = new Representation(getName());
        check();
    }

    /**
     * Checks two DeploymentAdminPermission objects for equality. 
     * Two permission objects are equal if: <p>
     * - their target filters 
     * are equal and<p>
     * - their actions are the same. 
     * @param obj The reference object with which to compare.
     * @return true if the two objects are equal.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (null == obj)
            return false;
        if (!(obj instanceof DeploymentAdminPermission))
            return false;
        DeploymentAdminPermission other = (DeploymentAdminPermission) obj;
        
        Vector avThis = getActionVector();
        Vector avOther = other.getActionVector();
        boolean eqActions = (avThis.containsAll(avOther) &&
                avOther.containsAll(avThis));
        return getRepresentation().equals(other.getRepresentation()) && eqActions;
    }

    /**
     * Returns hash code for this permission object.
     * @return Hash code for this permission object.
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        // TODO
        return getActionVector().hashCode();
    }

    /**
     * Returns the String representation of the action list.
     * @return Action list of this permission instance. This is a comma-separated 
     * list that reflects the action parameter of the constructor.
     * @see java.security.Permission#getActions()
     */
    public String getActions() {
        return actions;
    }

    /**
     * Checks if this DeploymentAdminPermission would imply the parameter permission.
     * @param permission Permission to check.
     * @return true if this DeploymentAdminPermission object implies the 
     * specified permission.
     * @see java.security.Permission#implies(java.security.Permission)
     */
    public boolean implies(Permission permission) {
        if (null == permission)
            return false;
        if (!(permission instanceof DeploymentAdminPermission))
            return false;
        DeploymentAdminPermission other = (DeploymentAdminPermission) permission;
        boolean ret = getRepresentation().match(other.getRepresentation()) && 
 	   			getActionVector().containsAll(other.getActionVector());
        return ret;
    }

    /**
     * Returns a new PermissionCollection object for storing DeploymentAdminPermission 
     * objects. 
     * @return The new PermissionCollection.
     * @see java.security.Permission#newPermissionCollection()
     */
    public PermissionCollection newPermissionCollection() {
        return null;
    }
    
    private Vector getActionVector() {
        if (null != actionsVector)
            return actionsVector;
        
        actionsVector = new Vector();
        StringTokenizer t = new StringTokenizer(actions.toUpperCase(), ",");
        while (t.hasMoreTokens()) {
            String action = t.nextToken().trim();
            actionsVector.add(action.toLowerCase());
        }
        return actionsVector;
    }

    private Representation getRepresentation() {
        return rep; 
    }

    private void check() {
        if (!ACTIONS.containsAll(getActionVector()))
            throw new IllegalArgumentException("Illegal action");
    }

}

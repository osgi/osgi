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
     * Constant String to the "uninstallForceful" action.
     */
    public static final String ACTION_UNINSTALL_FORCEFUL = "uninstallForceful";
    
    /**
     * Constant String to the "cancel" action.
     */
    public static final String ACTION_CANCEL             = "cancel";  
    
    private static final Vector ACTIONS = new Vector();
    static {
        ACTIONS.add(ACTION_INSTALL.toLowerCase());
        ACTIONS.add(ACTION_LIST.toLowerCase());
        ACTIONS.add(ACTION_UNINSTALL.toLowerCase());
        ACTIONS.add(ACTION_UNINSTALL_FORCEFUL.toLowerCase());
        ACTIONS.add(ACTION_CANCEL.toLowerCase());
    }
    
    private String namePart;
    private String signerPart;
    private Vector actionsVector;
    
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
        super(name);
        namePart = namePart(name);
        signerPart = signerPart(name);
        actionsVector = actionsVector(actions);
        check();
    }

    /**
     * Checks two DeploymentAdminPermission objects for equality. 
     * Two permission objects are equal if: <p>
     * - their "signer" and "name" parts of the name (target deployment package) 
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
        return namePart.equals(namePart(other.getName())) &&
        	   signerPart.equals(signerPart(other.getName())) &&
        	   actionsVector.equals(actionsVector(other.getActions()));
    }

    /**
     * Returns hash code for this permission object.
     * @return Hash code for this permission object.
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return (namePart + signerPart + actionsVector).hashCode();
    }

    /**
     * Returns the String representation of the action list.
     * @return Action list of this permission instance. This is a comma-separated 
     * list that reflects the action parameter of the constructor.
     * @see java.security.Permission#getActions()
     */
    public String getActions() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < actionsVector.size(); i++) {
            String action = (String) actionsVector.get(i);
            sb.append(action + (i == actionsVector.size() -1 ? "" : ", "));
        }
        return sb.toString();
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
        
        return impliesPackagePart(namePart, namePart(other.getName())) &&
               impliessignerPart(signerPart, signerPart(other.getName())) &&
               actionsVector.containsAll(actionsVector(other.getActions()));
    }

    /**
     * Returns a new PermissionCollection object for storing DeploymentAdminPermission 
     * objects. 
     * @return The new PermissionCollection.
     * @see java.security.Permission#newPermissionCollection()
     */
    public PermissionCollection newPermissionCollection() {
        // TODO
        return null;
    }
    
    /**
     * Returns a string representation of the object. Returns a string describing 
     * this Permission. The convention is to specify the class name, the permission 
     * name, and the actions in the following format:<p>
     * '("ClassName" "name" "actions")'.
     * @return string representation of the permission
     */
    public String toString() {
        // TODO see its JavaDoc
        return "name: \"" + namePart(getName()) + "\" signer: \"" +
        		signerPart(getName()) + "\"";
    }
    
    private String namePart(String name) {
        if (0 == name.length())
            return "";
        int i = name.indexOf("name:");
        if (-1 == i)
            return "";
        String s = name.substring("name:".length()).trim();
        i = s.indexOf("signer:");
        if (-1 == i)
            return s.trim();
        else
            return s.substring(0, i).trim();
    }

    private String signerPart(String name) {
        int i = name.indexOf("signer:");
        if (-1 == i)
            return "";
        return name.substring(i + "signer:".length()).trim();
    }

    private static Vector actionsVector(DeploymentAdminPermission perm) {
        return actionsVector(perm.getActions());
    }

    private static Vector actionsVector(String actions) {
        Vector v = new Vector();
        StringTokenizer t = new StringTokenizer(actions.toUpperCase(), ",");
        while (t.hasMoreTokens()) {
            String action = t.nextToken().trim();
            v.add(action.toLowerCase());
        }
        return v;
    }

    private void check() {
        if (!ACTIONS.containsAll(actionsVector))
            throw new IllegalArgumentException("Illegal action");
    }

    private boolean impliesPackagePart(String p1, String p2) {
        if (p1.equals(p2))
            return true;
        if ("*".equals(p1))
            return true;
        return false;
    }
    
    private boolean impliessignerPart(String s1, String s2) {
        if (s1.equals(s2))
            return true;
        if ("*".equals(s1))
            return true;
        return false;
    }
    
}

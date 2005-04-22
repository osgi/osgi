/*
 * $Header$
 * 
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
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
 * (AdminPermission, etc.) <b>In addition to DeploymentAdminPermission, the caller 
 * of Deployment Admin must hold the appropriate AdminPermissions.</b> 
 * For example, installing a deployment package requires DeploymentAdminPermission 
 * to access the installDeploymentPackage method and AdminPermission to access 
 * the framework's install/update/uninstall methods. <p>
 * The permission uses a &lt;filter&gt; string formatted similarly to the filter in RFC 73. 
 * The DeploymentAdminPermission filter does not use the id and location filters. 
 * The "signer" filter is matched against the signer chain of the deployment package, and 
 * the "name" filter is matched against the DeploymentPackage-Name header.
 * <blockquote><code>
 * DeploymentAdminPermission( "&lt;filter&gt;", "list" )<p>
 * </code></blockquote>
 * A holder of this permission can access the inventory information of the deployment 
 * packages selected by the &lt;filter&gt; string. The filter selects the deployment packages 
 * on which the holder of the permission can acquire detailed inventory information. 
 * See {@link DeploymentAdmin#getDeploymentPackage} and 
 * {@link DeploymentAdmin#listDeploymentPackages}.
 * <blockquote><code>
 * DeploymentAdminPermission( "&lt;filter&gt;", "install" )
 * </code></blockquote>
 * A holder of this permission can install/upgrade deployment packages if the deployment 
 * package satisfies the &lt;filter&gt; string. See {@link DeploymentAdmin#installDeploymentPackage}. 
 * <blockquote><code>
 * DeploymentAdminPermission( "&lt;filter&gt;", "uninstall" )
 * </code></blockquote>
 * A holder of this permission can uninstall deployment packages if the deployment 
 * package satisfies the &lt;filter&gt; string. See {@link DeploymentPackage#uninstall}.
 * <blockquote><code>
 * DeploymentAdminPermission( "&lt;filter&gt;", "uninstallForceful" )
 * </code></blockquote>
 * A holder of this permission can forecfully uninstall deployment packages if the deployment 
 * package satisfies the  string. See {@link DeploymentPackage#uninstallForceful}.
 * <blockquote><code>
 * DeploymentAdminPermission( "&lt;filter&gt;", "cancel" )
 * </code></blockquote>
 * A holder of this permission can cancel an active deployment action. This action being 
 * cancelled could correspond to the install, update or uninstall of a deployment package  
 * satisfies the  string. See {@link DeploymentAdmin#cancel}<p>
 * Wildcards can be used both in the name and the signer (see RFC-95) filers.<p>
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
     * Creates a new DeploymentAdminPermission for the given name (containing the name 
     * of the target deployment package) and action.
     * @param target Target string.
     * @param action Action string.
     * @throws IllegalArgumentException if the arguments are incorrect
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
     * Returns a string representation of the object.
     * @return string representation of the object
     */
    public String toString() {
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

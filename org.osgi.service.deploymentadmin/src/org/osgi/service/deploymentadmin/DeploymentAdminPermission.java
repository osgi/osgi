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

/**
 * DeploymentAdminPermission controls access to MEG management framework functions. 
 * This permission controls only Deployment Admin-specific functions; 
 * framework-specific access is controlled by usual OSGi permissions 
 * (AdminPermission, etc.) In addition to DeploymentAdminPermission, the caller 
 * of Deployment Admin must in addition hold the appropriate AdminPermissions. 
 * For example, installing a deployment package requires DeploymentAdminPermission 
 * to access the installDeploymentPackage method and AdminPermission to access 
 * the framework's install/update/uninstall methods. <p>
 * The permission uses a &lt;filter&gt; string formatted similarly to the filter in RFC 73. 
 * The DeploymentAdminPermission filter does not use the id and location filters. 
 * The signer filter is matched against the signer of the deployment package, and 
 * the name filter is matched against the DeploymentPackage-Name header.
 * <blockquote> 
 * DeploymentAdminPermission( "&lt;filter&gt;","listDeploymentPackages" )
 * </blockquote>
 * A holder of this permission can access the inventory information of the deployment 
 * packages selected by the &lt;filter&gt; string. The filter selects the deployment packages 
 * on which the holder of the permission can acquire detailed inventory information.
 * <blockquote>
 * DeploymentAdminPermission( "&lt;filter&gt;","installDeploymentPackage" )
 * </blockquote>
 * A holder of this permission can install/upgrade deployment packages if the deployment 
 * package satisfies the &lt;filter&gt; string.
 * <blockquote>
 * DeploymentAdminPermission( "&lt;filter&gt;","uninstall" )
 * </blockquote>
 * A holder of this permission can uninstall deployment packages if the deployment 
 * package satisfies the &lt;filter&gt; string.
 */
public class DeploymentAdminPermission extends Permission {

    public static final String ACTION_INSTALL_DP = "installDeploymentPackage";
    public static final String ACTION_LIST_DPS   = "listDeploymentPackages";
    public static final String ACTION_UNINSTALL  = "uninstall";
    public static final String ACTION_INVENTORY  = "inventory";
    
    private String actions;
    
    static {
        System.out.println("88888888888888888888888888888888888888888888888");
        System.out.println("888888888888888888       8888888888888888888888");
        System.out.println("8888888888888888 88888888  88888888888888888888");
        System.out.println("888888888888888 888 888 888 8888888888888888888");
        System.out.println("888888888888888 88888888888 8888888888888888888");
        System.out.println("888888888888888 888 888 88 88888888888888888888");
        System.out.println("88888888888888888         888888888888888888888");
        System.out.println("88888888888888888888888888888888888888888888888");
    }
    
    /**
     * Creates a new DeploymentAdminPermission for the given target and action.
     * @param target Target string.
     * @param action Action string.
     */
    public DeploymentAdminPermission(String target, String action) {
        super(target);
        System.out.println(">>>***>>> <INIT>");
        this.actions = action;
    }

    /**
     * Checks two DeploymentAdminPermission objects for equality. 
     * Two permission objects are equal if their target and action strings are equal. 
     * @param obj The reference object with which to compare.
     * @return true if the two objects are equal.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        System.out.println(">>>***>>> implies equals 1");
        if (!(obj instanceof DeploymentAdminPermission))
            return false;
        System.out.println(">>>***>>> implies equals 2");
        DeploymentAdminPermission other = (DeploymentAdminPermission) obj;
        System.out.println(">>>***>>> implies equals 3");
        System.out.println(getName() + "\t" + other.getName());
        System.out.println(getActions() + "\t" + other.getActions());
        return getName().equals(other.getName()) && 
               getActions().equals(other.getActions());
    }

    /**
     * Returns hash code for this permission object.
     * @return Hash code for this permission object.
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return (getName() + getActions()).hashCode();
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
        System.out.println(">>>***>>> implies called");
        return equals(permission);
    }

    /**
     * Returns a new PermissionCollection object for storing DeploymentAdminPermission 
     * objects. 
     * @return The new PermissionCollection.
     * @see java.security.Permission#newPermissionCollection()
     */
    public PermissionCollection newPermissionCollection() {
        return super.newPermissionCollection();
    }
}

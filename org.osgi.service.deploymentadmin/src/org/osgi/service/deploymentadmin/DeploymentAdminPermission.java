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
 * The permission uses a <filter> string formatted similarly to the filter in RFC 73. 
 * The DeploymentAdminPermission filter does not use the id and location filters. 
 * The signer filter is matched against the signer of the deployment package, and 
 * the name filter is matched against the DeploymentPackage-Name header.
 * <blockquote> 
 * DeploymentAdminPermission( "<filter>","listDeploymentPackages" )
 * </blockquote>
 * A holder of this permission can access the inventory information of the deployment 
 * packages selected by the <filter> string. The filter selects the deployment packages 
 * on which the holder of the permission can acquire detailed inventory information.
 * <blockquote>
 * DeploymentAdminPermission( "<filter>","installDeploymentPackage" )
 * </blockquote>
 * A holder of this permission can install/upgrade deployment packages if the deployment 
 * package satisfies the <filter> string.
 * <blockquote>
 * DeploymentAdminPermission( "<filter>","uninstall" )
 * </blockquote>
 * A holder of this permission can uninstall deployment packages if the deployment 
 * package satisfies the <filter> string.
 */
public class DeploymentAdminPermission extends Permission {

    /**
     * Creates a new DeploymentAdminPermission for the given target and action.
     * @param target Target string.
     * @param action Action string.
     */
    public DeploymentAdminPermission(String target, String action) {
        // TODO
        super(target);
    }

    /**
     * Checks two DeploymentAdminPermission objects for equality. 
     * Two permission objects are equal if their target and action strings are equal. 
     * @param obj The reference object with which to compare.
     * @return true if the two objects are equal.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        // TODO
        return false;
    }

    /**
     * Returns hash code for this permission object.
     * @return Hash code for this permission object.
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        // TODO
        return 0;
    }

    /**
     * Returns the String representation of the action list.
     * @return Action list of this permission instance. This is a comma-separated 
     * list that reflects the action parameter of the constructor.
     * @see java.security.Permission#getActions()
     */
    public String getActions() {
        // TODO
        return null;
    }

    /**
     * Checks if this DeploymentAdminPermission would imply the parameter permission.
     * @param permission Permission to check.
     * @return true if this DeploymentAdminPermission object implies the 
     * specified permission.
     * @see java.security.Permission#implies(java.security.Permission)
     */
    public boolean implies(Permission permission) {
        // TODO
        return false;
    }

    /**
     * Returns a new PermissionCollection object for storing DeploymentAdminPermission 
     * objects. 
     * @return The new PermissionCollection.
     * @see java.security.Permission#newPermissionCollection()
     */
    public PermissionCollection newPermissionCollection() {
        // TODO
        return super.newPermissionCollection();
    }
}

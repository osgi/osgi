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
 * (AdminPermission, etc.) In addition to DeploymentAdminPermission, the caller 
 * of Deployment Admin must in addition hold the appropriate AdminPermissions. 
 * For example, installing a deployment package requires DeploymentAdminPermission 
 * to access the installDeploymentPackage method and AdminPermission to access 
 * the framework's install/update/uninstall methods. <p>
 * The permission uses a &lt;filter&gt; string formatted similarly to the filter in RFC 73. 
 * The DeploymentAdminPermission filter does not use the id and location filters. 
 * The "signer" filter is matched against the signer chain of the deployment package, and 
 * the "name" filter is matched against the DeploymentPackage-Name header.
 * <blockquote> 
 * DeploymentAdminPermission( "&lt;filter&gt;","list" )
 * </blockquote>
 * A holder of this permission can access the inventory information of the deployment 
 * packages selected by the &lt;filter&gt; string. The filter selects the deployment packages 
 * on which the holder of the permission can acquire detailed inventory information.
 * <blockquote>
 * DeploymentAdminPermission( "&lt;filter&gt;","install" )
 * </blockquote>
 * A holder of this permission can install/upgrade deployment packages if the deployment 
 * package satisfies the &lt;filter&gt; string.
 * <blockquote>
 * DeploymentAdminPermission( "&lt;filter&gt;","uninstall" )
 * </blockquote>
 * A holder of this permission can uninstall deployment packages if the deployment 
 * package satisfies the &lt;filter&gt; string.
 * <blockquote>
 * DeploymentAdminPermission( "&lt;filter&gt;","uninstallForceful" )
 * </blockquote>
 * A holder of this permission can forecfully uninstall deployment packages if the deployment 
 * package satisfies the  string.
 * <blockquote>
 * DeploymentAdminPermission( "&lt;filter&gt;","cancel" )
 * </blockquote>
 * A holder of this permission can cancel an active deployment action. This action being 
 * cancelled could correspond to the install, update or uninstall of a deployment package  
 * satisfies the  string. <p>
 * Wildcards can be used both in the name and the signer (see RFC-95) filers.<p>
 * ??? WILDCARDS IN THE NAME FILTER ???<p>
 * only "name:* signer:CN=Super Man, O=ACME, C=US" OR<p>
 * * only at the end: "name:easy* signer:CN=Super Man, O=ACME, C=US" OR<p>
 * "name:eas?game* signer:CN=Super Man, O=ACME, C=US"<p>
 * or do we use the LDAP syntax:<p>
 * "&(name=eas?game\*)(signer=CN=Super Man, O=ACME, C=US)" 
 */
public class DeploymentAdminPermission extends Permission {

    public static final String ACTION_INSTALL_DP    = "installDeploymentPackage";
    public static final String ACTION_UNINSTALL_DP  = "uninstallDeploymentPackage";
    public static final String ACTION_LIST_DPS      = "listDeploymentPackages";
    public static final String ACTION_INVENTORY     = "inventory";
    private static final Vector ACTIONS = new Vector();
    static {
        ACTIONS.add(ACTION_INSTALL_DP.toLowerCase());
        ACTIONS.add(ACTION_UNINSTALL_DP.toLowerCase());
        ACTIONS.add(ACTION_LIST_DPS.toLowerCase());
        ACTIONS.add(ACTION_INVENTORY.toLowerCase());
    }
    
    private String namePart;
    private String signerPart;
    private Vector actionsVector;
    
    /**
     * Creates a new DeploymentAdminPermission for the given target and action.
     * @param target Target string.
     * @param actions Action string.
     */
    public DeploymentAdminPermission(String name, String actions) {
        super(name);
        namePart = namePart(name);
        signerPart = signerPart(name);
        actionsVector = actionsVector(actions);
        check();
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
        if ( "".equals(namePart) && "".equals(signerPart) &&
             (actionsVector.contains(ACTION_INSTALL_DP) || actionsVector.contains(ACTION_UNINSTALL_DP)) )
            	throw new IllegalArgumentException("Bad name part");
        if ( (!"".equals(namePart) || !"".equals(signerPart)) &&
                (actionsVector.contains(ACTION_LIST_DPS) || actionsVector.contains(ACTION_INVENTORY)) )
            	throw new IllegalArgumentException("Bad name part");
    }

    /**
     * Checks two DeploymentAdminPermission objects for equality. 
     * Two permission objects are equal if their target and action strings are equal. 
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

    /**
     * Returns a new PermissionCollection object for storing DeploymentAdminPermission 
     * objects. 
     * @return The new PermissionCollection.
     * @see java.security.Permission#newPermissionCollection()
     */
    public PermissionCollection newPermissionCollection() {
        return super.newPermissionCollection();
    }
    
    public String toString() {
        return "name: \"" + namePart(getName()) + "\" signer: \"" +
        		signerPart(getName()) + "\"";
    }
    
}
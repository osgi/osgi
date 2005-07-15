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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.*;

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
 * DeploymentAdminPermission( "&lt;filter&gt;", "uninstallForced" )
 * </pre>
 * A holder of this permission can forcefully uninstall deployment packages if the deployment
 * package satisfies the  string. See {@link DeploymentPackage#uninstallForced}.
 * <pre>
 * DeploymentAdminPermission( "&lt;filter&gt;", "cancel" )
 * </pre>
 * A holder of this permission can cancel an active deployment action. This action being
 * cancelled could correspond to the install, update or uninstall of a deployment package
 * that satisfies the  string. See {@link DeploymentAdmin#cancel}<p>
 * Wildcards can be used both in the name and the signer (see RFC-95) filters.<p>
 */
public final class DeploymentAdminPermission extends Permission {
    
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
    
    private static final String      delegateProperty = "org.osgi.vendor.deploymentadmin";
    private static final Constructor constructor;
    private final        Permission  delegate;
    static {
        constructor = (Constructor) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                String pckg = System.getProperty(delegateProperty);
                if (null == pckg)
                    throw new RuntimeException("Property '" + delegateProperty + "' is not set");
                try {
                    Class c = Class.forName(pckg + ".DeploymentAdminPermission");
                    return c.getConstructor(new Class[] {String.class, String.class});    
                }
                catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }});
    }
    
    /**
     * Creates a new <code>DeploymentAdminPermission</code> object for the given name (containing the name
     * of the target deployment package) and action.<p>
     * The <code>name</code> parameter identifies the target depolyment package the permission 
     * relates to. The <code>actions</code> parameter contains the comma separated list of allowed actions. 
     * @param name Target string, must not be null.
     * @param actions Action string, must not be null.
     * @throws IllegalArgumentException if the filter is invalid, the list of actions 
     *         contains unknown operations or one of the parameters is null
     */
    public DeploymentAdminPermission(String name, String actions) {
        super(name);
		try {
			try {
	            delegate = (Permission) constructor.newInstance(new Object[] {name, actions});
			}
			catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
		}
		catch (Error e) {
			throw e;
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Throwable e) {
			throw new RuntimeException(e.toString());
		}
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
        if (obj == this)
        	return true;
        if (!(obj instanceof DeploymentAdminPermission))
            return false;
        DeploymentAdminPermission dap = (DeploymentAdminPermission) obj;
        return delegate.equals(dap.delegate);
    }

    /**
     * Returns hash code for this permission object.
     * @return Hash code for this permission object.
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return delegate.hashCode();
    }

    /**
     * Returns the String representation of the action list.
     * @return Action list of this permission instance. This is a comma-separated 
     * list that reflects the action parameter of the constructor.
     * @see java.security.Permission#getActions()
     */
    public String getActions() {
        return delegate.getActions();
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
    	        
        DeploymentAdminPermission dap = (DeploymentAdminPermission) permission;
        
        return delegate.implies(dap.delegate);
    }

    /**
     * Returns a new PermissionCollection object for storing DeploymentAdminPermission 
     * objects. 
     * @return The new PermissionCollection.
     * @see java.security.Permission#newPermissionCollection()
     */
    public PermissionCollection newPermissionCollection() {
        return delegate.newPermissionCollection();
    }

}

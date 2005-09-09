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

import java.io.InputStream;

/**
  * This is the interface of Deployment Admin service. The service provides 
  * functionality to manage deployment packages. The deployment admin functionality 
  * is exposed as a standard OSGi service with no mandatory service parameters.<p>
  * Each operation requires {@link DeploymentAdminPermission} <b>and in addition to 
  * this the appropriate <code>org.osgi.framework.AdminPermission</code></b>.   
  */
public interface DeploymentAdmin {

	/**
	 * Installs a deployment package from an input stream. If a version of that deployment package
	 * is already installed and the versions are different, the installed version is updated
	 * with this new version even if it is older. If the two versions are the same, then this 
	 * method simply returns with the old (target) deployment package without any action. 
	 * <code>DeploymentAdminPermission("&lt;filter&gt;", "install")</code> is 
	 * needed for this operation.
	 * @param  in the input stream the deployment package can be read from. It mustn't be null.
	 * @return A DeploymentPackage object representing the newly installed/updated deployment package. 
	 *         Return value can only be null if the action was cancelled (see {@link #cancel}).
	 * @throws DeploymentException if the installation was not successful
	 * @throws SecurityException if access is not permitted based on the current security policy.
	 * @see DeploymentAdminPermission
	 */
    DeploymentPackage installDeploymentPackage(InputStream in) throws DeploymentException;

    /**
      * Lists the deployment packages currently installed on the platform.
      * <code>DeploymentAdminPermission("&lt;filter&gt;", "list")</code> is 
      * needed for this operation.<p>
      * During an installation of an existing package (update), the target must remain 
      * in this list until the installation process is completed, after which the source 
      * replaces the target.
      * @return the array of <code>DeploymentPackage</code> objects representing all the 
      *         installed deployment packages (including the "System" deployment package). 
      *         The return value cannot be null. In case of missing permissions it may 
      *         give back an empty array.
      * @see DeploymentPackage
      * @see DeploymentAdminPermission
      */
    DeploymentPackage[] listDeploymentPackages();

    /**
     * Gets the currenlty installed {@link DeploymentPackage} instance which has the given 
     * symbolic name. {@link DeploymentAdminPermission}("&lt;filter&gt;", "list") is 
     * needed for this operation.<p>
     * During an installation of an existing package (update), the target deployment package 
     * must remain the return value until the installation process is completed, after which 
     * the source is the return value.
     * @param  symbName the symbolic name of the deployment package to be retrieved. It mustn't be null.
     * @return The <code>DeploymentPackage</code> for the given symbolic name. 
     *         If there is no deployment package with that symbolic name currently installed, 
     *         null is returned.
     * @throws SecurityException if access to the deployment package identified by the symbolic name 
     * 	       is not permitted based on the current security policy.
     * @throws IllegalArgumentException if the given <code>symbName</code> is null
     * @see DeploymentPackage
     * @see DeploymentAdminPermission
     */
    DeploymentPackage getDeploymentPackage(String symbName);  
  
    /**
     * This method cancels the currently active deployment session. This method addresses the need
     * to cancel the processing of excessively long running, or resource consuming install, updates
     * or uninstalls.
     * <code>DeploymentAdminPermission("&lt;filter&gt;", "cancel")</code> is needed for this operation. 
     * @return true if there was an active session and it was successfully cancelled.
     * @throws SecurityException if the operation is not permitted based on the current security policy.
     * @see DeploymentAdminPermission
     */
    boolean cancel();     
    
}

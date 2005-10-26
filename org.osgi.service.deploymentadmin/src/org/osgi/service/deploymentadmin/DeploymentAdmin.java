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

import org.osgi.framework.Bundle;

/**
  * This is the interface of Deployment Admin service.<p>
  * 
  * The OSGi Service Platform provides mechanisms to manage the life cycle of
  * bundles, configuration objects, and permission objects but the overall consistency
  * of the runtime configuration is the responsibility of the management
  * agent. In other words, the management agent decides to install, update,
  * or uninstall bundles, create or delete configuration or permission objects, as
  * well as manage other resource types.<p>
  * 
  * The Deployment Admin service standardizes the access to some of the responsibilities
  * of the management agent.<p>
  * 
  * The service provides functionality to manage deployment packages 
  * (see {@link DeploymentPackage}).<p> 
  * 
  * The deployment admin functionality is exposed as a standard OSGi service with no 
  * mandatory service parameters.
  */
public interface DeploymentAdmin {

	/**
	 * Installs a deployment package from an input stream. If a version of that deployment package
	 * is already installed and the versions are different, the installed version is updated
	 * with this new version even if it is older. If the two versions are the same, then this 
	 * method simply returns with the old (target) deployment package without any action.<p>
	 *  
	 * {@link DeploymentAdminPermission}("&lt;filter&gt;", "install")</code> is needed for this operation.
	 * 
	 * @param  in the input stream the deployment package can be read from. It mustn't be null.
	 * @return A DeploymentPackage object representing the newly installed/updated deployment package. 
	 *         Return value can only be null if the action was cancelled (see {@link #cancel}).
	 * @throws IllegalArgumentException if the got InputStream parameter is null         
	 * @throws DeploymentException if the installation was not successful
	 * @throws SecurityException if access is not permitted based on the current security policy.
	 * @see DeploymentAdminPermission
	 */
    DeploymentPackage installDeploymentPackage(InputStream in) throws DeploymentException;

    /**
      * Lists the deployment packages currently installed on the platform.<p>
      * 
      * {@link DeploymentAdminPermission}("&lt;filter&gt;", "list")</code> is 
      * needed for this operation to the effect that only those packages are listed in  
      * the array to which the caller has appropriate DeploymentAdminPermission. It has 
      * the consequence that the method never throws SecurityException only doesn't 
      * put certain deployment packages into the array.<p>
      * 
      * During an installation of an existing package (update) or during an uninstallation, 
      * the target must remain in this list until the installation process is completed, 
      * after which the source replaces the target.
      * 
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
     * symbolic name.<p>
     * 
     * {@link DeploymentAdminPermission}("&lt;filter&gt;", "list") is needed for this operation.<p>
     * 
     * During an installation of an existing package (update) or during an uninstallation, 
     * the target deployment package must remain the return value until the installation process 
     * is completed, after which the source is the return value.
     * 
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
     * Gives back the installed {@link DeploymentPackage} that owns the bundle. Deployment Packages own their 
     * bundles by their Bundle Symbolic Name. It means that if a bundle belongs to an installed 
     * Deployment Packages (and at most to one) the Deployment Admin assigns the bundle to its owner  
     * Deployment Package by the Symbolic Name of the bundle.<p>
     * 
     * {@link DeploymentAdminPermission}("&lt;filter&gt;", "list") is needed for this operation.<p>
     * 
     * @param bundle the bundle whose owner is queried 
     * @return the Deployment Package Object that owns the bundle or <code>null</code> if the bundle doesn't 
     *         belong to any Deployment Packages (standalone bundles)
     * @throws SecurityException if the caller hasn't got right to <code>list</code> the Deployment Package.
     */
    DeploymentPackage getDeploymentPackage(Bundle bundle);  
  
    /**
     * This method cancels the currently active deployment session. This method addresses the need
     * to cancel the processing of excessively long running, or resource consuming install, updates
     * or uninstalls.<p>
     * 
     * {@link DeploymentAdminPermission}("&lt;filter&gt;", "cancel")</code> is needed for this operation. 
     * 
     * @return true if there was an active session and it was successfully cancelled.
     * @throws SecurityException if the operation is not permitted based on the current security policy.
     * @see DeploymentAdminPermission
     */
    boolean cancel();     
    
}

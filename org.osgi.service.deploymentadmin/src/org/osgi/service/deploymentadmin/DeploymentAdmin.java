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
	 * Installs a deployment package from an Input Stream. If a version of that deployment package
	 * is already installed and the versions are different, the installed version is updated
	 * with this new version even if it is older. If the two versions are the same, then this 
	 * method simply returns without any action. 
	 * <code>DeploymentAdminPermission}("&lt;filter&gt;", "install")</code> is 
	 * needed for this operation.
	 * @param  in The input stream which where the deployment package can be read. It mustn't be null.
	 * @return A DeploymentPackage object representing the newly installed/updated deployment package. 
	 *         Return value can only be null if the action was cancelled (see {@see #cancel}).
	 * @throws DeploymentException if the installation was not successful
	 * @throws SecurityException if access is not permitted based on the current security policy.
	 * @see DeploymentAdminPermission
	 */
    DeploymentPackage installDeploymentPackage(InputStream in) throws DeploymentException;

    /**
      * Lists the deployment packages currently installed on the platform.
      * <code>DeploymentAdminPermission("&lt;filter&gt;", "list")</code> is 
      * needed for this operation. 
      * @return Array of <code>DeploymentPackage</code> objects representing all the installed deployment 
      *         packages. Return value cannot be null. If there are no deployment packages installed it 
      * gives back 
      *         an empty array.  
      * @see DeploymentAdminPermission
      */
    DeploymentPackage[] listDeploymentPackages();

    /**
     * Get the deployment package instance based on the id of the package.
     * {@link DeploymentAdminPermission}("&lt;filter&gt;", "list") is 
     * needed for this operation. 
     * @param  id the id of the deployment package to be retrieved. It mustn't be negative.
     * @return The <code>DeploymentPackage</code> for the request id. If there is no deployment 
     *         package with that id, null is returned.
     * @throws SecurityException if access to the deployment package identified by <code>id</code> 
     * 	       is not permitted based on the current security policy.
     * @see DeploymentAdminPermission
     */
    DeploymentPackage getDeploymentPackage(long id);  
  
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

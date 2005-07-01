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

/**
 * The session interface represents a currently running deployment session and provides 
 * information about the objects involved in the processing of the deployment package 
 * currently being deployed.<p>
 * When a deployment package is installed the target package, when uninstalled the 
 * source package is an empty deployment package. The empty package has version 
 * <code>0.0.0</code>, its name is an empty string, it has no bundles and no resources.   
 */
public interface DeploymentSession {
    
    /**
     * If the deployment action is an update or an uninstall, this call returns
     * the <code>DeploymentPackage</code> instance for the installed deployment package. If the 
     * deployment action is an install, this call returns the empty deploymet package (see
     * {@link DeploymentPackage}).
     * @return the target deployment package
     * @see DeploymentPackage
     */
    DeploymentPackage getTargetDeploymentPackage();
    
    /**
     * If the deployment action is an install or an update, this call returns
     * the <code>DeploymentPackage</code> instance that corresponds to the deployment package
     * being streamed in for this session. If the deployment action is an uninstall, this call 
     * returns the empty deploymet package (see {@link DeploymentPackage}).
     * @return the source deployment package
     * @see DeploymentPackage
     */ 
    DeploymentPackage getSourceDeploymentPackage();

    /**
     * Returns the private data area of the specified bundle. The bundle must be part of 
     * either the source or the target deployment packages. The permission set the caller 
     * resource processor needs to manipulate the private area of the bundle is set by the 
     * Deployment Admin on the fly when this method is called. The permissions remain availble 
     * during the deployment action only.
     * <code>DeploymentCustomizerPermission("&lt;filter&gt;", "privatearea")</code> is also 
	 * needed for this operation.
     * @param bundle the bundle the private are belongs to
     * @return file representing the private area of the bundle. It cannot be null.
     * @throws SecurityException if the caller is not the customizer of the corresponding 
     *         deployment package.
     * @see DeploymentPackage, DeploymentCustomizerPermission
     */     
    java.io.File getDataFile(org.osgi.framework.Bundle bundle);
     
}


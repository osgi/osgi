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
 * The Session interface represents a currently running Deployment session and provides 
 * access to information/objects involved in the processing of the currently being 
 * deployed deployment package.
 */
public interface DeploymentSession {
    
    /**
     * If the deployment action is an update or an uninstall, this call returns
     * the <code>DeploymentPackage</code> instance for the installed deployment package. If the 
     * deployment action is an install, a deployment package with version 0 is created that 
     * contains no bundles and no resources.
     */
    DeploymentPackage getTargetDeploymentPackage();
    
    /**
     * If the deployment action is an install or an update, this call returns
     * the <code>DeploymentPackage</code> instance that corresponds to the deployment package
     *  being streamed in for this session.  Since the session would not be created until after
     * the manifest has been read in, all the necessary information will be available
     * to create this object. If the deployment action is an uninstall, this call 
     * returns the empty deploymet package.
     */ 
    DeploymentPackage getSourceDeploymentPackage();

    /**
     * Returns the private data area of the specified bundle. The bundle must be part of 
     * either the source or target deployment packages.
     * @param bundle the bundle the private are belongs to
     * @return file representing the private area of the bundle. It cannot be null.
     * @throws SecurityException if the caller is not the customizer of the corresponding 
     *         deployment package.
     */     
     java.io.File getDataFile(org.osgi.framework.Bundle bundle);
     
}


/*
 * $Header$
 * 
 * Copyright (c) The OSGi Alliance (2005). All Rights Reserved.
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

/**
 * The Session interface represents a currently running Deployment session and provides 
 * access to information/objects involved in the processing of the currently being 
 * deployed deployment package.
 */
public interface DeploymentSession {
    /**
     * The action value INSTALL indicates this session is associated with the 
     * installation of a deployment package.  
     */
    public static final int INSTALL = 1;
    /**
     * The action value UPDATE indicates this session is associated with the 
     * update of a deployment package.  
     */
    public static final int UPDATE = 2;
    /**
     * The action value UNINSTALL indicates this session is associated with the 
     * uninstalling of a deployment package.  
     */
    public static final int UNINSTALL = 3;   
    /**
     * Returns whether this session is doing an install, an update, or an uninstall.
     * While this can be determined by inspecting the source and target deployment package, 
     * providing this action saves resource processors from going through the trouble of doing so.
     */
    int getDeploymentAction();
    
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


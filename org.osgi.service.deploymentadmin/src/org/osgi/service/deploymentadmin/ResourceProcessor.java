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
  * ResourceProcessor interface is implemented by processors handling resource files
  * in deployment packages. The ResourceProcessor interfaces are exported as OSGi services.
  * Bundles exporting the service may arrive in the deployment package (customizers) or may be 
  * preregistered. Resource processors has to define the <code>service.pid</code> standard 
  * OSGi service property which should be a unique string.
  */
public interface ResourceProcessor {

	/**
	  * Called when the Deployment Admin starts a new operation on the given deployment package, 
	  * and the resource processor is associated a resource within the package.  Only one 
	  * deployment package can be processed at a time.
	  * It is important to note that the deployment action (i.e. install, update, uninstall) 
	  * that is part of the deployment session instance passed to the begin method corresponds
	  * to what is happening to the resources being processed by this RP.  For example, an
	  * "update" of a DP, can result in the "install" of a new resource. 
	  * @param session object that represents the current session to the resource processor
	  */
    void begin(DeploymentSession session);
  
    /**
     * Called when a resource is encountered in the deployment package for which this resource 
     * processor has been  selected to handle the processing of that resource.
     * @param name The name of the resource relative to the deployment package root directory. 
     * @param stream The stream for the resource. 
     * @throws DeploymentException if the resource cannot be processed.
     */
    void process(String name, InputStream stream) throws DeploymentException;

	/**
	  * Called when a resource, associated with a particular resource processor, had belonged to 
	  * an earlier version of a deployment package but is not present in the current version of 
	  * the deployment package.  This provides an opportunity for the processor to cleanup any 
	  * memory and persistent data being maintained for the particular resource.  
	  * This method will only be called during "update" deployment sessions.
	  * @param name Name of the resource being dropped from the deployment package.
	  * @throws DeploymentException if the resource is not allowed to be dropped.
	  */
    void dropped(String name) throws DeploymentException;
    
    /**
     * This method is called during an "uninstall" deployment session.
     * This method will be called on all RPs that are associated with resources in the DP 
     * being uninstalled. This provides an opportunity for the processor to cleanup any 
     * memory and persistent data being maintained for the deployment package.
     * @throws DeploymentException if all resources could not be dropped.
     */
    void dropAllResources() throws DeploymentException;
  
    /**
     * This method is called on the Resource Processor immediately before calling the commit 
     * method.  The Resource Processor has to check whether it is able to commit the operations
     * since the last begin method call. If it determines that it is not able to commit the
     * changes, it has to raise a DeploymentException with {@link DeploymentException#CODE_PREPARE} 
     * exception code (see {@link DeploymentException}).
     * @throws DeploymentException if the resource processor is able to determine it is not able to commit.
     */
    void prepare() throws DeploymentException;        
   
    /**
     * Called when the processing of the current deployment package is finished. 
     * This method is called if the processing of the current deployment package was successful, 
     * and the changes must be made permanent.
     */
    void commit();
   
     
    /**
     * Called when the processing of the current deployment package is finished. 
     * This method is called if the processing of the current deployment package was unsuccessful, 
     * and the changes made during the processing of the deployment package should be removed.  
     */
    void rollback();
    
    /**
     * Processing of a resource passed to the resource processor may take long. 
     * The <code>cancel()</code> method notifies the resource processor that it should 
     * interrupt the processing of the current resource. This method is called by the 
     * <code>DeploymentAdmin</code> implementation after the
     * <code>DeploymentAdmin.cancel()</code> method is called.
     */
    void cancel();

}

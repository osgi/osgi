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
  * ResourceProcessor interface is implemented by processors handling resource files
  * in deployment packages. The ResourceProcessor interfaces are exported as OSGi services.
  * Bundles exporting the service may arrive in the deployment package (customizers) or may be 
  * preregistered. Resource processors has to define the <code>service.pid</code> standard 
  * OSGi service property which should be a unique string.
  */
public interface ResourceProcessor {

	/**
	  * Called when the Deployment Admin starts a new operation on the given deployment package, 
	  * and the resource processor is associated a resource within the package. Only one 
	  * deployment package can be processed at a time.
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
	  * @param resource the name of the resource to drop (it is the same as the value of the 
	  *                 "Name" attribute in the deployment package's manifest)
	  * @throws DeploymentException if the resource is not allowed to be dropped.
	  */
    void dropped(String resource) throws DeploymentException;
    
    /**
     * This method is called during an "uninstall" deployment session.
     * This method will be called on all resource processors that are associated with resources 
     * in the deployment package being uninstalled. This provides an opportunity for the processor 
     * to cleanup any memory and persistent data being maintained for the deployment package.
     * @throws DeploymentException if all resources could not be dropped.
     */
    void dropAllResources() throws DeploymentException;
  
    /**
     * This method is called on the Resource Processor immediately before calling the 
     * <code>commit</code> method. The Resource Processor has to check whether it is able 
     * to commit the operations since the last <code>begin</code> method call. If it determines 
     * that it is not able to commit the changes, it has to raise a 
     * <code>DeploymentException</code> with {@link DeploymentException#CODE_PREPARE} exception 
     * code (see {@link DeploymentException}).
     * @throws DeploymentException if the resource processor is able to determine it is 
     *         not able to commit.
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

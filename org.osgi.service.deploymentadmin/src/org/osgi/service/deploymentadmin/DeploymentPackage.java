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

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

/**
  * The DeploymentPackage object represents a deployment package (already installed
  * or being currently processed).
  */
public interface DeploymentPackage {
 
	/**
	 * Returns the identifier of the deployment package.  Every installed deployment package 
	 * has its own unique identifier.
	 * @return The ID of the resource package.
	 */
    long getId();
	  
	/**
	 * Returns the name of the deployment package.
	 * @return The name of the deployment package.
	 */
	String getName();
	  
	/**
	 * Returns the version of the deployment package.
	 * @return version of the deployment package
	 */
	Version getVersion();
	  
	/**
	 * Returns an 2D array of strings representing the bundles and their version that
	 * are specified in the manifest of this deployment package
	 * @return The 2d string array corresponding to bundle symbolic name and version pairs
	 */
    String[][] getBundleSymNameVersionPairs();  
 
    /**
     * Returns the bundle instance that corresponds to the bundle's name/version pair.
     * This method will return null for request for bundle/version pairs that are not part 
     * of this deployment package.
     * As this instance is transient, this method may return null if the bundle/version pair
     * is part of this deployment package, but is not currently defined to the framework.
     * @return The deployment package instance for a given bundle name/version pair.
     */
    Bundle getBundle(String bundleSymName);
    
    /**
     * Returns an array of strings representing the resources that are specified in 
     * the  manifest of this deployment package
     * @return The string array corresponding to resources
     */
    String[] getResources();   
    
    /**
     * At the time of deployment, resource processor service instances are located to 
     * processor the resources contained in a deployment package.  This call returns a 
     * service reference 
     * to the corresponding service instance.
     * If this call is made during deployment, prior to the locating of the service to 
     * process a given resource, null will be returned.
     * Services can be updated after a deployment packahge has been deployed.  In this event, 
     * this call will return a reference to the updated service, not to the instance that was
     * used at deployment time.
     * @return resource procesor for the resource 
     */
    ServiceReference getResourceProcessor(String resource);    

    /**
     * Returns the requested deployment package manifest header from the main section. 
     * Header names are case insensitive.  
     * @param name the requested header
     * @return the value of the header
     */
    String getHeader(String name);

    /**
     * Returns the requested deployment package manifest header from the name 
     * section determined by the path parameter. Header names are case insensitive.  
     * @param name the requested header
     * @return the value of the header
     */
    String getResourceHeader(String path, String header);
    
	/**
	  * Uninstalls the deployment package. After uninstallation, the deployment package 
	  * object becomes stale.  This can be checked by using DeploymentPackage:getId(), which 
	  * will return a -1 when stale.   
	  * @throws DeploymentException if the deployment package could not be successfully uninstalled. 
	  */
    void uninstall() throws DeploymentException;
 
    /**
     * This method is called to completely uninstall a deployment package, which couldn't be uninstalled
     * using traditional means due to exceptions.
     * @return true if the operation was successful
     */  
    boolean uninstallForceful();  
 
    /**
     * Returns a hash code value for the object.
     * @return a hash code value for this object
     */
    int hashCode();
  
    /**
     * Indicates whether some other object is "equal to" this one. Two deployment packages 
     * are equal if they have the sam name and version.
     * @param other the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    boolean equals(Object other);
  
}

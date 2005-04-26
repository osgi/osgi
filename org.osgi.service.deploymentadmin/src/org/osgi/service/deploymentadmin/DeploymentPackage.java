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
  * or being currently processed). A Deployment Package groups resources as a unit 
  * of management. A deployment package is something that can be installed, updated, 
  * and uninstalled as a unit. A deployment package is a reified concept, like a bundle, 
  * in an OSGi Service Platform. It is not known by the OSGi Framework, but it is managed 
  * by the Deployment Admin service. A deployment package is a stream of resources 
  * (including bundles) which, once processed, will result in new artifacts being added 
  * to the OSGi platform.  These new artifacts can include installed Bundles, new 
  * configuration objects added to the Configuration Admin service, new Wire objects 
  * added to the Wire Admin service, or changed system properties. All the changes caused 
  * by the processing of a deployment package are persistently associated with the 
  * deployment package, so that they can be appropriately cleaned up when the deployment
  * package is uninstalled. There is a strict “no overlap” rule imposed on deployment packages. 
  * Two deployment packages are not allowed to create or manipulate the same artifact.  
  * Obviously, this means that a bundle cannot be in two different DPs. Any violation of 
  * this “no overlap” rule is considered an error and the install or update of the 
  * offending deployment package must be aborted.  
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
     * Returns the bundle instance that corresponds to the bundle's symbolic name.
     * This method will return null for request for bundles that are not part 
     * of this deployment package.<p>
     * As this instance is transient, this method may return null if the bundle
     * is part of this deployment package, but is not currently defined to the framework.
     * @return The <code>Bundle</code> instance for a given bundle symbolic name.
     */
    Bundle getBundle(String bundleSymName);
    
    /**
     * Returns an array of strings representing the resources that are specified in 
     * the  manifest of this deployment package. A string element of the array is the 
     * same as the value of the "Name" attribute in the manifest.<p>
     * E.g. if the "Name" section of the resource (or individual-section as the 
     * {@see <a href="http://java.sun.com/j2se/1.4.2/docs/guide/jar/jar.html#Manifest%20Specification">Manifest Specification</a>} 
     * calls it) in the manifest is the following
     * <blockquote><pre>
     *     Name: foo/readme.txt
     *     Resource-Processor: foo.rp
     * </pre></blockquote>
     * then the corresponding array element is the "foo/readme.txt" string.
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

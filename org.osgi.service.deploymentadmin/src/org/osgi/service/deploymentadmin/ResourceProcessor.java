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

/**
  * ResourceProcessor interface is implemented by processors handling resource files
  * in deployment packages. The ResourcePackage interfaces are exported as OSGi services.
  * Bundles exporting ResourcePackage services may arrive in the deployment package
  * or may be preregistered.
  */
public interface ResourceProcessor {
  /**
   * The action value INSTALL indicates a desired install operation to be performed on a 
   * deployment package.  
   */
  public static final int INSTALL = 1;
  /**
   * The action value UPDATE indicates a desired update operation to be performed on a 
   * deployment package.
   */
  public static final int UPDATE = 2;
  /**
   * The action value UNINSTALL indicates a desired uninstall operation to be performed on 
   * a deployment package.
   */
  public static final int UNINSTALL = 3;

/**
  * Called when the Deployment Admin starts a new operation on the given deployment package, 
  * and the resource processor is associated with the package.  Only one deployment package 
  * can be processed at a time.
  * @param rp An object representing the deployment package being processed.
  * @param operation The operation in progress (INSTALL, UPDATE and UNINSTALL).
  */
  void begin(DeploymentPackage rp, int operation);
/**
  * Called when the processing of the current deployment package is finished. 
  * @param commit True if the processing of the current deployment package was successful, 
  * and the changes must be made permanent.  If False, the deployment package operation was 
  * unsuccessful, and the changes made during the processing of the deployment package should 
  * be removed.  
  */
  void complete(boolean commit);
/**
  * Called when a resource is encountered in the deployment package for which this resource 
  * processor is registered.  
  * @param name The name of the resource relative to the deployment package root directory. 
  * @throws Exception if the resource cannot be processed.
  */
  void process(String name) throws Exception;
/**
  * Called when a resource, associated with a particular resource processor, had belonged to 
  * an earlier version of a deployment package but is not present in the current version of 
  * the deployment package.  This provides an opportunity for the processor to cleanup any 
  * memory and persistent data being maintained for the particular resource.  
  * @param name Name of the resource being dropped from the deployment package.
  * @throws Exception if the resource is not allowed to be dropped.
  */
  void dropped(String name) throws Exception;
  /**
   * Called when the resource processor is dropped entirely. This occurs when the processor 
   * is no longer associated with a deployment package, which provides an opportunity for the 
   * processor to cleanup any memory and persistent data being kept.
   */
  void dropped();
}

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
import org.osgi.framework.Bundle;
import java.io.File;

/**
  * The ResourcePackage object represents the a resource package (already installed
  * or being currently processed)
  */
public interface DeploymentPackage {
/**
  * Returns the identifier of the deployment package.  Every installed deployment package 
  * has its own unique identifier.  Once uninstalled, a deployment package will have an 
  * identifier value of -1.
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
  */
  String getVersion();
/**
  * Uninstalls the deployment package. After uninstallation, the deployment package 
  * object becomes stale.  This can be checked by using DeploymentPackage:getId(), which 
  * will return a -1 when stale.   
  */
  void uninstall();
/**
  * Lists the bundles belonging to the deployment package.  The returned list is not 
  * guaranteed to be up-to-date before the complete() method on ResourceProcessor is called.
  * @return An array of bundles contained within the deployment package.    
  */
  Bundle[] listBundles ();
/**
  * Allows querying whether the specified bundle is being newly installed by the current 
  * operation on the deployment package.  This method gives resource processors the ability 
  * to query the effects of the current operation, which may be either commit or rollback.  
  * In the case of a rollback, the bundle would be returned to its previous version before the 
  * update operation was attempted.
  * @param b Bundle to query.
  * @return True if the bundle is newly installed by this deployment package, other False.
  * @throws ###WhatException Throws an exception if called outside an operation on this deployment package.
  */
  boolean isNew(Bundle b);
/**
  * Allows querying whether this bundle is being updated by the current operation on the 
  * deployment package.  This method allows resource processors to query the effects of the 
  * current operation, which may be either commit or rollback.  If the operation commits, the 
  * bundle will be uninstalled.  In the case of a rollback, the bundle would be returned to its 
  * previous version before the update operation was attempted.  
  * @param b Bundle to query.
  * @return True if the bundle is updated by this deployment package, otherwise False.
  * @throws ###WhatException Throws an exception if called outside an operation on this deployment package.
  */
  boolean isUpdated(Bundle b);
/**
  * Allows querying whether this bundle is pending removal within the current operation on 
  * the deployment package.  This method allows resource processors to query the effects of 
  * the current operation, which may be either commit or rollback.  If the operation commits, 
  * the bundle will be uninstalled.  In the case of a rollback, the bundle will not be 
  * uninstalled.  
  * @param b Bundle to query.
  * @return True if the bundle is updated by this deployment package, otherwise False.
  * @throws ###WhatException Throws an exception if called outside an operation on this deployment package.
  */
  boolean isPendingRemoval(Bundle b);
/**
  * This method allows a resource processor to get access to resources within a deployment 
  * package as an object.  This method may only be called by resource processors from within 
  * their process() method, which only occurs during install and update operations on the 
  * deployment package.
  * @param path name of the resource file to read.
  * @return The resource file in Object format.
  * @throws ###WhatException Throws an exception if called outside an operation on this deployment package.
  */
  Object getResource (String path);
/**
  * This method allows a resource processor to get access to resources within a deployment 
  * package as an input stream.  This method may only be called by resource processors from 
  * within their process() method, which only occurs during install and update operations on 
  * the deployment package.
  * @param path name of the resource file to read.
  * @return The resource file in stream format.
  * @throws ###WhatException Throws an exception if called outside an operation on this deployment package.
  */
  InputStream getResourceAsStream(String path);
/**
  * Returns the private data area descriptor area of the specified bundle, which must be a 
  * part of the deployment package.  
  * @param bundle A specified bundle
  * @return The private data area descriptor of the bundle, or null in case of error.
  */
  File getDataFile( Bundle bundle );
}

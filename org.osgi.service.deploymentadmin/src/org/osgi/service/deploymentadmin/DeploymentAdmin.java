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
  * This is the interface of Deployment Admin service. <i>Service ID to register
  * the service?</i>
  */
public interface DeploymentAdmin {

/**
  * Installs a deployment package an input stream. If a version of that deployment package
  * is already installed and the versions are different, the installed version is updated
  * with this new version even if it is older. If the two versions are the same, then this 
  * method simply returns without any action.
  * @param in The input stream which where the deployment package can be read.
  * @return A DeploymentPackage object representing the newly installed/updated deployment 
  *         package or null in case of error.
  */
  DeploymentPackage installDeploymentPackage (InputStream in);
/**
  * Lists the deployment packages currently installed on the platform. 
  * MEGMgmtPermission( "","listDeploymentPackages" ) is needed to access this method.
  * @return Array of DeploymentPackage objects representing all the installed deployment packages.
  */
  DeploymentPackage[] listDeploymentPackages ();
  /**
   * Gives back the location string generated from the bundle symbolic name and version.
   * The deployment admin has to use this location string when installs an OSGi bundle 
   * with the given symbolic name and version.
   * @param symbName The symbolic name of the bundle.
   * @param version The version of the bundle.
   * @return The location of the bundle generated from the paramaters.
   */
  String location(String symbName, String version);
}

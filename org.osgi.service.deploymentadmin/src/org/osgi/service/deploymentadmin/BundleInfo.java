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

import org.osgi.framework.Version;

/**
 * Represents a bundle in the array given back by the {@link DeploymentPackage#getBundleInfos()}  
 * method.
 */
public interface BundleInfo {
	
	/**
	 * Returns the Bundle Symbolic Name of the represented bundle.
	 * 
	 * @return the Bundle Symbolic Name 
	 */
	String getSymbolicName();
	
	/**
	 * Returns the version of the represented bundle.
	 * 
	 * @return the version of the represented bundle
	 */
	Version getVersion();

}

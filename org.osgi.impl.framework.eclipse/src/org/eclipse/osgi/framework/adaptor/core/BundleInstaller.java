/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.framework.adaptor.core;

import org.eclipse.osgi.service.resolver.BundleDescription;
import org.osgi.framework.BundleException;

/**
 * A bundle installer allows the platform admin implementation to 
 * delegate the behavior of installing/uninstalling bundles to 
 * another object.
 * <p>
 * Clients may implement this interface.
 * </p>
 * @since 3.1 
 * @see StateManager#commit 
 */
public interface BundleInstaller {
	/**
	 * Installs a bundle associated with the specified BundleDescription
	 * @param toInstall the BundleDescription associated with the bundle to install
	 * @throws BundleException if an error occurs while installing the bundle
	 */
	public void installBundle(BundleDescription toInstall) throws BundleException;

	/**
	 * Uninstalls a bundle associated with the specified BundleDescription
	 * @param toUninstall the BundleDescriptoin associated with the bundle to uninstall
	 * @throws BundleException if an error occurs while uninstalling the bundle
	 */
	public void uninstallBundle(BundleDescription toUninstall) throws BundleException;

	/**
	 * Updates a bundle associated with the specified BundleDescription
	 * @param toRefresh the BundleDescription associated with the bundle to update
	 * @throws BundleException if an error occurs while updating the bundle
	 */
	public void updateBundle(BundleDescription toRefresh) throws BundleException;
}

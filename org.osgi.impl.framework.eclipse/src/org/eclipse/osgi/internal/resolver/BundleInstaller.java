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
package org.eclipse.osgi.internal.resolver;

import org.eclipse.osgi.service.resolver.BundleDescription;
import org.osgi.framework.BundleException;

/**
 * A bundle installer allows the platform admin implementation to 
 * delegate the behavior of installing/uninstalling bundles to 
 * another object.
 *  
 * @see StateManager#commit 
 */
public interface BundleInstaller {
	public void installBundle(BundleDescription toInstall) throws BundleException;

	public void uninstallBundle(BundleDescription toUninstall) throws BundleException;

	public void updateBundle(BundleDescription toRefresh) throws BundleException;
}

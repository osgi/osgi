/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.internal.core;

import java.security.PermissionCollection;

/**
 * An abstract subclass of PermissionCollection.
 *
 */
abstract class BundlePermissionCollection extends PermissionCollection {
	/**
	 * The Permission collection will unresolve the permissions in these packages.
	 *
	 * @param refreshedBundles A list of the bundles which have been refreshed
	 * as a result of a packageRefresh
	 */
	abstract void unresolvePermissions(AbstractBundle[] refreshedBundles);
}

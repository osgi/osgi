/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.internal.core;

import java.security.PermissionCollection;
import java.util.Hashtable;

/**
 * An abstract subclass of PermissionCollection.
 *
 */
abstract class BundlePermissionCollection extends PermissionCollection {
	/**
	 * The Permission collection will unresolve the permissions in these packages.
	 *
	 * @param unresolvedPackages A list of the package which have been unresolved
	 * as a result of a packageRefresh
	 */
	abstract void unresolvePermissions(Hashtable unresolvedPackages);
}
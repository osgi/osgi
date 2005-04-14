/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.adaptor;

import java.security.PermissionCollection;
import java.security.ProtectionDomain;

/**
 * 
 * This is a specialized ProtectionDomain for a bundle.
 */
public abstract class BundleProtectionDomain extends ProtectionDomain {

	/**
	 * Constructs a special ProtectionDomain for a bundle.
	 * 
	 * @param permCollection
	 *            the PermissionCollection for the Bundle
	 */
	public BundleProtectionDomain(PermissionCollection permCollection) {
		super(null, permCollection);
	}
}

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

package org.eclipse.osgi.framework.internal.core;

import java.security.PermissionCollection;
import org.eclipse.osgi.framework.adaptor.BundleProtectionDomain;

/**
 *
 * This class tracks the signature information for a bundle in addition to
 * managing bundle permissions.
 */
public class BundleProtectionDomainImpl extends BundleProtectionDomain {
	AbstractBundle bundle;

	/**
	 * @param bundle the bundle to which this ProtectionDomain corresponds.
	 * @param permCollection the PermissionCollection used by this ProtectionDomain.
	 */
	public BundleProtectionDomainImpl(AbstractBundle bundle, PermissionCollection permCollection) {
		super(permCollection);
		this.bundle = bundle;
	}
}

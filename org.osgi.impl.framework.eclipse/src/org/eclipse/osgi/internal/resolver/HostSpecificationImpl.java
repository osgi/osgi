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

package org.eclipse.osgi.internal.resolver;

import org.eclipse.osgi.service.resolver.*;

public class HostSpecificationImpl extends VersionConstraintImpl implements HostSpecification {

	private BundleDescription[] hosts;

	public boolean isSatisfiedBy(BaseDescription supplier) {
		if (!(supplier instanceof BundleDescription))
			return false;
		BundleDescription candidate = (BundleDescription) supplier;
		if (candidate.getHost() != null)
			return false;
		if (getName() != null && getName().equals(candidate.getSymbolicName()) &&
				(getVersionRange() == null || getVersionRange().isIncluded(candidate.getVersion())))
			return true;
		return false;
	}

	public BundleDescription[] getHosts() {
		return hosts;
	}

	public boolean isResolved() {
		return hosts != null && hosts.length > 0;
	}

	/*
	 * The resolve algorithm will call this method to set the hosts.
	 */
	protected void setHosts(BundleDescription[] hosts) {
		this.hosts = hosts;
	}

	public String toString() {
		return "Fragment-Host: " + getName() + " - version: " + getVersionRange(); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
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

public abstract class VersionConstraintImpl implements VersionConstraint {
	private String name;
	private VersionRange versionRange;
	private BundleDescription bundle;
	private BaseDescription supplier;

	public String getName() {
		return name;
	}

	public VersionRange getVersionRange() {
		if (versionRange == null)
			return VersionRange.emptyRange;
		return versionRange;
	}

	public BundleDescription getBundle() {
		return bundle;
	}

	public boolean isResolved() {
		return supplier != null;
	}

	public BaseDescription getSupplier() {
		return supplier;
	}

	public boolean isSatisfiedBy(BaseDescription supplier) {
		return false;
	}
	protected void setName(String name) {
		this.name = name;
	}

	protected void setVersionRange(VersionRange versionRange) {
		this.versionRange = versionRange;
	}

	protected void setBundle(BundleDescription bundle) {
		this.bundle = bundle;
	}

	protected void setSupplier(BaseDescription supplier) {
		this.supplier = supplier;
	}
}
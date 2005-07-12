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
package org.eclipse.osgi.internal.module;

import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.VersionConstraint;

/*
 * A companion to VersionConstraint from the state used while resolving
 */
public abstract class ResolverConstraint {
	ResolverBundle bundle;
	VersionConstraint constraint;

	ResolverConstraint(ResolverBundle bundle, VersionConstraint constraint) {
		this.bundle = bundle;
		this.constraint = constraint;
	}

	// returns the Resolver bundle requiring the ResolverConstraint
	ResolverBundle getBundle() {
		return bundle;
	}

	// returns the BundleDescription requiring the ResolverConstraint
	BundleDescription getBundleDescription() {
		return bundle.getBundle();
	}

	// returns whether this constraint is from an attached fragment
	boolean isFromFragment() {
		return constraint.getBundle().getHost() != null;
	}

	// Same as VersionConstraint but does additinal permission checks
	boolean isSatisfiedBy(VersionSupplier vs) {
		if (!bundle.getResolver().getPermissionChecker().checkPermission(constraint, vs.getBaseDescription()))
			return false;
		return constraint.isSatisfiedBy(vs.getBaseDescription());
	}

	// returns the companion VersionConstraint object from the State
	VersionConstraint getVersionConstraint() {
		return constraint;
	}

	// returns the name of this constraint
	public String getName() {
		return constraint.getName();
	}

	public String toString() {
		return constraint.toString();
	}

	// returns whether this constraint is optional
	abstract boolean isOptional();
}

/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.internal.module;

import java.util.ArrayList;
import java.util.Iterator;
import org.eclipse.osgi.service.resolver.*;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.VersionConstraint;

public class BundleConstraint {
	private ResolverBundle bundle;
	private VersionConstraint bundleConstraint;
	private ResolverBundle matchingBundle;
	private ArrayList matchingBundles;

	BundleConstraint(ResolverBundle bundle, VersionConstraint bundleConstraint) {
		this.bundle = bundle;
		this.bundleConstraint = bundleConstraint;
	}

	boolean isFromFragment() {
		return bundleConstraint.getBundle().getHost() != null;
	}

	ResolverBundle getBundle() {
		return bundle;
	}

	BundleDescription getActualBundle() {
		return bundle.getBundle();
	}

	boolean isSatisfiedBy(ResolverBundle rb) {
		if (!bundle.getResolver().getPermissionChecker().checkBundlePermission(bundleConstraint, rb.getBundle()))
			return false;
		return bundleConstraint.isSatisfiedBy(rb.getBundle());
	}

	boolean isOptional() {
		if (bundleConstraint instanceof HostSpecification)
			return false;
		return ((BundleSpecification) bundleConstraint).isOptional();
	}

	VersionConstraint getVersionConstraint() {
		return bundleConstraint;
	}

	ResolverBundle getMatchingBundle() {
		return matchingBundle;
	}

	ResolverBundle[] getMatchingBundles() {
		if (matchingBundles == null)
			return null;
		ResolverBundle[] results = new ResolverBundle[matchingBundles.size()];
		int i = 0;
		for (Iterator iter = matchingBundles.iterator(); iter.hasNext(); i++) {
			results[i] = (ResolverBundle) iter.next();
		}
		return results.length == 0 ? null : results;
	}

	void addMatchingBundle(ResolverBundle rb) {
		if (matchingBundles == null)
			matchingBundles = new ArrayList(1); // rare to have more than one
		if (!matchingBundles.contains(rb))
			matchingBundles.add(rb);
	}

	void removeMatchingBundle(ResolverBundle rb) {
		if (matchingBundles == null)
			return;
		matchingBundles.remove(rb);
	}

	void removeAllMatchingBundles() {
		matchingBundles = null;
	}

	void setMatchingBundle(ResolverBundle rb) {
		this.matchingBundle = rb;
	}

	public boolean foundMatchingBundles() {
		return matchingBundles == null ? false : matchingBundles.size() > 0;
	}
}

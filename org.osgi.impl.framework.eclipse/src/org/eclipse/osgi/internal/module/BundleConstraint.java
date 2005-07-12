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
import org.eclipse.osgi.service.resolver.*;

/*
 * A companion to BundleSpecification from the state for use while resolving
 */
public class BundleConstraint extends ResolverConstraint {
	// a list of matching bundles; multiple matches are only supported for fragment host constraints
	private ArrayList matchingBundles;

	BundleConstraint(ResolverBundle bundle, VersionConstraint bundleConstraint) {
		super(bundle, bundleConstraint);
	}

	boolean isOptional() {
		if (constraint instanceof HostSpecification)
			return false;
		return ((BundleSpecification) constraint).isOptional();
	}

	ResolverBundle getMatchingBundle() {
		return (ResolverBundle) (matchingBundles != null && matchingBundles.size() > 0 ? matchingBundles.get(0) : null);
	}

	ResolverBundle[] getMatchingBundles() {
		if (matchingBundles == null || matchingBundles.size() == 0)
			return null;
		return (ResolverBundle[]) matchingBundles.toArray(new ResolverBundle[matchingBundles.size()]);
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
		removeAllMatchingBundles();
		if (rb != null)
			addMatchingBundle(rb);
	}

	boolean foundMatchingBundles() {
		return matchingBundles == null ? false : matchingBundles.size() > 0;
	}
}

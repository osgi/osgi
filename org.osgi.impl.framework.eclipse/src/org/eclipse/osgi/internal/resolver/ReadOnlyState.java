/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
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
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;

public class ReadOnlyState implements State {
	private State target;

	public ReadOnlyState(State target) {
		this.target = target;
	}

	public boolean addBundle(BundleDescription description) {
		throw new UnsupportedOperationException();
	}

	public StateDelta compare(State state) throws BundleException {
		return target.compare(state);
	}

	public BundleDescription getBundle(long id) {
		return target.getBundle(id);
	}

	public BundleDescription getBundle(String symbolicName, Version version) {
		return target.getBundle(symbolicName, version);
	}

	public BundleDescription getBundleByLocation(String location) {
		return target.getBundleByLocation(location);
	}

	public BundleDescription[] getBundles() {
		return target.getBundles();
	}

	public BundleDescription[] getBundles(String symbolicName) {
		return target.getBundles(symbolicName);
	}

	public StateDelta getChanges() {
		return target.getChanges();
	}

	public ExportPackageDescription[] getExportedPackages() {
		return target.getExportedPackages();
	}

	public StateObjectFactory getFactory() {
		return target.getFactory();
	}

	public BundleDescription[] getResolvedBundles() {
		return target.getResolvedBundles();
	}

	public long getTimeStamp() {
		return target.getTimeStamp();
	}

	public boolean isEmpty() {
		return target.isEmpty();
	}

	public boolean isResolved() {
		return target.isResolved();
	}

	public boolean removeBundle(BundleDescription bundle) {
		throw new UnsupportedOperationException();
	}

	public BundleDescription removeBundle(long bundleId) {
		throw new UnsupportedOperationException();
	}

	public StateDelta resolve() {
		throw new UnsupportedOperationException();
	}

	public StateDelta resolve(boolean incremental) {
		throw new UnsupportedOperationException();
	}

	public StateDelta resolve(BundleDescription[] discard) {
		throw new UnsupportedOperationException();
	}

	public void setOverrides(Object value) {
		throw new UnsupportedOperationException();
	}

	public boolean updateBundle(BundleDescription newDescription) {
		throw new UnsupportedOperationException();
	}

	public void resolveConstraint(VersionConstraint constraint, BaseDescription supplier) {
		throw new UnsupportedOperationException();
	}

	public void resolveBundle(BundleDescription bundle, boolean status, BundleDescription[] host, ExportPackageDescription[] selectedExports, BundleDescription[] resolvedRequires, ExportPackageDescription[] resolveImports) {
		throw new UnsupportedOperationException();
	}

	public void removeBundleComplete(BundleDescription bundle) {
		throw new UnsupportedOperationException();
	}

	public Resolver getResolver() {
		return null;
	}

	public void setResolver(Resolver value) {
		throw new UnsupportedOperationException();
	}

	public ExportPackageDescription linkDynamicImport(BundleDescription importingBundle, String requestedPackage) {
		throw new UnsupportedOperationException();
	}

}
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
import org.osgi.framework.Constants;

public class ResolverImport {
	private ImportPackageSpecification importPackageSpecification;
	private ResolverExport matchingExport;
	private ResolverBundle bundle;
	// Wirings we know will cause the module to become unresolvable (due to grouping dependencies)
	private ArrayList unresolvableWirings = new ArrayList();
	private String name = null;

	ResolverImport(ResolverBundle bundle, ImportPackageSpecification ips) {
		this.bundle = bundle;
		importPackageSpecification = ips;
	}

	boolean isFromFragment() {
		return importPackageSpecification.getBundle().getHost() != null;
	}

	String getName() {
		if (name != null) {
			return name;
		} else {
			return importPackageSpecification.getName();
		}
	}

	void setName(String name) {
		this.name = name;
	}

	ResolverBundle getBundle() {
		return bundle;
	}

	BundleDescription getActualBundle() {
		return bundle.getBundle();
	}

	boolean isSatisfiedBy(ResolverExport re) {
		// first check permissions
		if (!bundle.getResolver().getPermissionChecker().checkImportPermission(importPackageSpecification, re.getExportPackageDescription()))
			return false;
		// See if the import is satisfied
		if (!importPackageSpecification.isSatisfiedBy(re.getExportPackageDescription())) {
			return false;
		}
		return true;
	}

	ResolverExport getMatchingExport() {
		return matchingExport;
	}

	void setMatchingExport(ResolverExport matchingExport) {
		this.matchingExport = matchingExport;
	}

	ResolverExport getRoot() {
		ResolverImport ri = this;
		ResolverExport re = ri.getMatchingExport();
		while (re != null && !re.getExportPackageDescription().isRoot()) {
			ResolverBundle reExporter = re.getExporter();
			ri = reExporter.getImport(re.getName());
			if (ri != null) {
				re = ri.getMatchingExport();
				continue;
			}
			// If there is no import then we need to try going thru the requires
			re = getRootRequires(re, reExporter);
		}
		return re;
	}

	// Recurse down the requires, until we find the root export
	private ResolverExport getRootRequires(ResolverExport re, ResolverBundle reExporter) {
		BundleConstraint[] requires = reExporter.getRequires();
		for (int i = 0; i < requires.length; i++) {
			if (requires[i].getMatchingBundle() == null)
				continue;
			ResolverExport[] exports = requires[i].getMatchingBundle().getExportPackages();
			for (int j = 0; j < exports.length; j++) {
				if (re.getName().equals(exports[j].getName())) {
					return exports[j];
				}
			}
			re = getRootRequires(re, requires[i].getMatchingBundle());
			if (re.getExportPackageDescription().isRoot())
				return re;
		}
		return re;
	}

	boolean isOnRootPath(ResolverBundle rb) {
		ResolverImport ri = this;
		ResolverExport re = ri.getMatchingExport();
		if (re.getExporter() == rb)
			return true;
		while (re != null && !re.getExportPackageDescription().isRoot()) {
			ResolverBundle reExporter = re.getExporter();
			ri = reExporter.getImport(re.getName());
			if (ri != null) {
				re = ri.getMatchingExport();
				if (re.getExporter() == rb)
					return true;
				continue;
			}
			re = getRootRequires(re, reExporter);
			if (re.getExporter() == rb)
				return true;
		}
		return false;
	}

	boolean isOnRootPathSplit(ResolverBundle bundle, ResolverBundle toFind) {
		if (bundle == null)
			return false;
		BundleConstraint[] requires = bundle.getRequires();
		for (int i = 0; i < requires.length; i++) {
			if (requires[i].getMatchingBundle() == toFind)
				return true;
			if (isOnRootPathSplit(requires[i].getMatchingBundle(), toFind))
				return true;
		}
		return false;
	}

	// Records an unresolvable wiring for this import (grouping dependencies)
	void addUnresolvableWiring(ResolverBundle module) {
		unresolvableWirings.add(module);
	}

	void removeUnresolvableWiring(ResolverBundle module) {
		unresolvableWirings.remove(module);
	}

	// Clear the list of all the unresovable wirings. This is called when we move
	// the module to UNRESOLVED so we can start from scratch if we ever try again
	void clearUnresolvableWirings() {
		unresolvableWirings = new ArrayList();
	}

	// Returns true if the supplied export has not been recorded as
	// an unresolvable wiring for this import
	boolean isNotAnUnresolvableWiring(ResolverExport exp) {
		return !unresolvableWirings.contains(exp.getExporter());
	}

	ImportPackageSpecification getImportPackageSpecification() {
		return importPackageSpecification;
	}

	boolean isOptional() {
		return ImportPackageSpecification.RESOLUTION_OPTIONAL.equals(importPackageSpecification.getDirective(Constants.RESOLUTION_DIRECTIVE));
	}

	boolean isDynamic() {
		return ImportPackageSpecification.RESOLUTION_DYNAMIC.equals(importPackageSpecification.getDirective(Constants.RESOLUTION_DIRECTIVE));
	}

	public String toString() {
		return importPackageSpecification.toString();
	}
}

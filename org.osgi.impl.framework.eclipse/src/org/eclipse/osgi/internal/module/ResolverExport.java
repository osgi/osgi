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

import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.ExportPackageDescription;
import org.osgi.framework.Version;

public class ResolverExport implements VersionSupplier {
	private ResolverBundle exporter;
	private ExportPackageDescription exportPackageDescription;
	private boolean reprovide = false;
	private boolean dropped = false;

	ResolverExport(ResolverBundle bundle, ExportPackageDescription export) {
		exporter = bundle;
		exportPackageDescription = export;
	}

	ResolverExport(ResolverBundle bundle, ExportPackageDescription export, boolean reprovide) {
		this(bundle, export);
		this.reprovide = reprovide;
	}

	public String getName() {
		return exportPackageDescription.getName();
	}

	public Version getVersion() {
		return exportPackageDescription.getVersion();
	}

	public BundleDescription getBundle() {
		return exportPackageDescription.getExporter();
	}

	ResolverBundle getExporter() {
		return exporter;
	}

	String[] getUses() {
		return exportPackageDescription.getUses();
	}

	ExportPackageDescription getExportPackageDescription() {
		return exportPackageDescription;
	}

	ResolverExport getRoot() {
		ResolverImport ri;
		ResolverExport re = this;
		while (re != null && !re.getExportPackageDescription().isRoot()) {
			ResolverBundle reExporter = re.getExporter();
			ri = reExporter.getImport(re.getName());
			if (ri != null) {
				re = ri.getMatchingExport();
				continue;
			}
			// If there is no import then we need to try going thru the requires
			ResolverExport root = getRootRequires(re, reExporter);
			if (root != re)
				return root;
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
		ResolverImport ri;
		ResolverExport re = this;
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

	boolean isReprovide() {
		return reprovide;
	}

	public String toString() {
		return exportPackageDescription.toString();
	}

	boolean isDropped() {
		return dropped;
	}

	void setDropped(boolean dropped) {
		this.dropped = dropped;
	}
}

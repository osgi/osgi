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
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.ExportPackageDescription;

/*
 * A companion to ExportPackageDescription from the state used while resolving.
 */
public class ResolverExport extends VersionSupplier {
	private ResolverBundle resolverBundle;

	ResolverExport(ResolverBundle resolverBundle, ExportPackageDescription epd) {
		super(epd);
		this.resolverBundle = resolverBundle;
	}

	public ExportPackageDescription getExportPackageDescription() {
		return (ExportPackageDescription) base;
	}

	public BundleDescription getBundle() {
		return getExportPackageDescription().getExporter();
	}

	ResolverBundle getExporter() {
		return resolverBundle;
	}

	/*
	 * returns a list of roots for this export.  The returned list will include and exports
	 * which the exporting bundle may get access to from imported packages or required bundles.
	 */
	ResolverExport[] getRoots() {
		ArrayList results = new ArrayList(1); // usually only one root
		addRoots(results);
		return (ResolverExport[]) results.toArray(new ResolverExport[results.size()]);
	}

	/*
	 * Adds roots for this export to the specified roots list.
	 */
	private void addRoots(ArrayList roots) {
		if (roots.contains(this))
			return;
		ResolverImport ri = getExporter().getImport(getName());
		if (ri != null && ri.getMatchingExport() != null && ri.getMatchingExport() != this) {
			ri.getMatchingExport().addRoots(roots);
			return;
		}
		// always add to the front of the list
		roots.add(0, this);
		BundleConstraint[] requires = getExporter().getRequires();
		for (int i = 0; i < requires.length; i++) {
			if (requires[i].getMatchingBundle() == null)
				continue;
			ResolverExport requiredExport = requires[i].getBundle().getExport(getName());
			if (requiredExport != null && !requiredExport.isDropped())
				requiredExport.addRoots(roots);
		}
	}
}

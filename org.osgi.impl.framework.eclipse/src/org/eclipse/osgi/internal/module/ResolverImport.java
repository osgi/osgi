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
import org.eclipse.osgi.service.resolver.ImportPackageSpecification;
import org.osgi.framework.Constants;

/*
 * A companion to ImportPackageSpecification from the state used while resolving
 */
public class ResolverImport extends ResolverConstraint {
	private ResolverExport matchingExport;
	// Wires we know will cause the module to become unresolvable (due to grouping dependencies)
	private ArrayList unresolvableWirings = new ArrayList();
	// only used for dynamic imports
	private String name;

	ResolverImport(ResolverBundle bundle, ImportPackageSpecification ips) {
		super(bundle, ips);
	}

	boolean isOptional() {
		return ImportPackageSpecification.RESOLUTION_OPTIONAL.equals(((ImportPackageSpecification) constraint).getDirective(Constants.RESOLUTION_DIRECTIVE));
	}

	boolean isDynamic() {
		return ImportPackageSpecification.RESOLUTION_DYNAMIC.equals(((ImportPackageSpecification) constraint).getDirective(Constants.RESOLUTION_DIRECTIVE));
	}

	ResolverExport getMatchingExport() {
		return matchingExport;
	}

	void setMatchingExport(ResolverExport matchingExport) {
		this.matchingExport = matchingExport;
	}

	// Records an unresolvable wiring for this import (grouping dependencies)
	void addUnresolvableWiring(ResolverBundle exporter) {
		unresolvableWirings.add(exporter);
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

	public String getName() {
		if (name != null)
			return name; // return the required package set for a dynamic import
		return super.getName();
	}

	// used for dynamic import package when wildcards are used
	void setName(String requestedPackage) {
		this.name = requestedPackage;
	}
}

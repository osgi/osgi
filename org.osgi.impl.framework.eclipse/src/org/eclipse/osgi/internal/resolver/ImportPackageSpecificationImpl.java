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

import java.util.Iterator;
import java.util.Map;

import org.eclipse.osgi.service.resolver.*;
import org.osgi.framework.Constants;

public class ImportPackageSpecificationImpl extends VersionConstraintImpl implements ImportPackageSpecification {
	private static final String ALL_PACKAGES = "*"; //$NON-NLS-1$
	private String[] propagate;
	private int resolution = ImportPackageSpecification.RESOLUTION_STATIC; // the default is static
	private String symbolicName;
	private VersionRange bundleVersionRange;
	private Map attributes;

	public String[] getPropagate() {
		return propagate;
	}

	public int getResolution() {
		return resolution;
	}

	public String getBundleSymbolicName() {
		return symbolicName;
	}

	public VersionRange getBundleVersionRange() {
		if (bundleVersionRange == null)
			return VersionRange.emptyRange;
		return bundleVersionRange;
	}

	public Map getAttributes() {
		return attributes;
	}

	public boolean isSatisfiedBy(BaseDescription supplier) {
		if (!(supplier instanceof ExportPackageDescription))
			return false;
		ExportPackageDescription pkgDes = (ExportPackageDescription) supplier;
		boolean matchName = false;
		if (symbolicName != null) {
			BundleDescription exporter = pkgDes.getExporter();
			if (!symbolicName.equals(exporter.getSymbolicName()))
				return false;
			if (getBundleVersionRange() != null && !getBundleVersionRange().isIncluded(exporter.getVersion()))
				return false;
		}

		String name = getName();
		// shortcut '*'
		// TODO we have logic here for "*" exports for now to make progress in the resolver implementation
		if ("*".equals(name) || "*".equals(pkgDes.getName())) {//$NON-NLS-1$ //$NON-NLS-2$
			matchName = true;
		}
		else if (name.endsWith(".*")) { //$NON-NLS-1$
			if (pkgDes.getName().startsWith((name.substring(0, name.length() - 1))))
				matchName =  true;
		}
		else if (pkgDes.getName().equals(name)) {
			matchName = true;
		}
		else if (pkgDes.getName().endsWith(".*")) { //$NON-NLS-1$
			if (name.startsWith((pkgDes.getName().substring(0, name.length() - 1))))
				matchName =  true;
		}
		if (!matchName)
			return false;
		if (getVersionRange() != null && !getVersionRange().isIncluded(pkgDes.getVersion()))
			return false;

		Map importAttrs = getAttributes();
		if (importAttrs != null) {
			Map exportAttrs = pkgDes.getAttributes();
			if (exportAttrs == null)
				return false;
			for (Iterator i = importAttrs.keySet().iterator(); i.hasNext();) {
				String importKey = (String) i.next();
				String importValue = (String) importAttrs.get(importKey);
				String exportValue = (String) exportAttrs.get(importKey);
				if (exportValue == null || !importValue.equals(exportValue))
					return false;
			}
		}
		String[] mandatory = pkgDes.getMandatory();
		if (mandatory != null) {
			for(int i = 0; i < mandatory.length; i++) {
				if (Constants.BUNDLE_SYMBOLICNAME_ATTRIBUTE.equals(mandatory[i])) {
					if (symbolicName == null)
						return false;
				}
				else if (Constants.BUNDLE_VERSION_ATTRIBUTE.equals(mandatory[i])) {
					if (bundleVersionRange == null)
						return false;
				}
				else if (Constants.PACKAGE_SPECIFICATION_VERSION.equals(mandatory[i])) {
					if (getVersionRange() == null)
						return false;
				}
				else { // arbitrary attribute
					if (importAttrs == null)
						return false;
					if (importAttrs.get(mandatory[i]) == null)
						return false;
				}
			}
		}
		return true;
	}

	protected void setPropagate(String[] propagate) {
		this.propagate = propagate;
	}

	protected void setBundleSymbolicName(String symbolicName) {
		this.symbolicName = symbolicName;
	}

	protected void setBundleVersionRange(VersionRange bundleVersionRange) {
		this.bundleVersionRange = bundleVersionRange;
	}

	protected void setResolution(int resolution) {
		this.resolution = resolution;
	}

	protected void setAttributes(Map attributes) {
		this.attributes = attributes;
	}

	public String toString() {
		return "Import-Package: " + getName() + " - version: " + getVersionRange(); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
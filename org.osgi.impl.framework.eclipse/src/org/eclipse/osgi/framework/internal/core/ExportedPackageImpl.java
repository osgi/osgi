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
package org.eclipse.osgi.framework.internal.core;

import java.util.ArrayList;
import org.eclipse.osgi.service.resolver.*;
import org.eclipse.osgi.service.resolver.ExportPackageDescription;
import org.osgi.framework.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.service.packageadmin.ExportedPackage;

public class ExportedPackageImpl implements ExportedPackage {

	String specVersion;
	ExportPackageDescription exportedPackage;
	BundleLoaderProxy supplier;

	public ExportedPackageImpl(ExportPackageDescription exportedPackage, BundleLoaderProxy supplier) {
		this.exportedPackage = exportedPackage;
		this.supplier = supplier;
		Version version = exportedPackage.getVersion();
		if (version != null) {
			this.specVersion = version.toString();
		}
	}

	public String getName() {
		return exportedPackage.getName();
	}

	public org.osgi.framework.Bundle getExportingBundle() {
		if (supplier.isStale()) {
			return null;
		}
		return supplier.getBundleHost();
	}

	public Bundle[] getImportingBundles() {
		if (supplier.isStale()) {
			return null;
		}

		BundleDescription[] dependentBundles = supplier.getBundleDescription().getDependents();
		ArrayList importingBundles = new ArrayList();

		for (int i = 0; i < dependentBundles.length; i++) {
			if (dependentBundles[i].getHost() != null)
				continue;
			BundleLoaderProxy proxy = supplier.getBundleLoader().getLoaderProxy(dependentBundles[i]);
			if (proxy == null)
				continue; // this is probably because the bundle was uninstalled; not an error
			/* check to make sure this package is really imported or the bundle is required */
			addImporters(proxy, importingBundles);
		}

		AbstractBundle[] result = new AbstractBundle[importingBundles.size()];
		importingBundles.toArray(result);
		return result;
	}

	private void addImporters(BundleLoaderProxy proxy, ArrayList importingBundles) {
		if (importingBundles.contains(proxy.getBundle()))
			return;
		if (isImportedBy(proxy.getBundleLoader())) {
			importingBundles.add(proxy.getBundle());
			return;
		}
		if (isRequiredBy(proxy.getBundleLoader())) {
			supplier.addRequirers(proxy.getBundleDescription(), importingBundles);
		}
	}

	private boolean isImportedBy(BundleLoader loader) {
		return loader.importedPackages != null && loader.importedPackages.getByKey(getName()) != null;
	}

	private boolean isRequiredBy(BundleLoader loader) {
		BundleLoaderProxy[] requiredBundles = loader.requiredBundles;
		if (requiredBundles == null)
			return false;
		for (int i = 0; i < requiredBundles.length; i++)
			if (requiredBundles[i] == supplier)
				return true;
		return false;
	}

	public String getSpecificationVersion() {
		return specVersion;
	}

	public boolean isRemovalPending() {
		return isRemovalPending(exportedPackage);
	}

	private boolean isRemovalPending(ExportPackageDescription exportDescription) {
		BundleDescription exporter = exportDescription.getExporter();
		if (exporter != null)
			return exporter.isRemovalPending();
		return true;
	}

	public String toString() {
		StringBuffer result = new StringBuffer(getName());
		if (specVersion != null) {
			result.append("; ").append(Constants.PACKAGE_SPECIFICATION_VERSION); //$NON-NLS-1$
			result.append("=\"").append(specVersion).append("\"");  //$NON-NLS-1$//$NON-NLS-2$
		}
		return result.toString();
	}

	BundleLoaderProxy getSuppler() {
		return supplier;
	}
}
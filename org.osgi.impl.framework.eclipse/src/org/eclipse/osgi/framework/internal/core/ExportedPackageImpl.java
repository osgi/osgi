/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
		if (version != null)
			this.specVersion = version.toString();
	}

	public String getName() {
		return exportedPackage.getName();
	}

	public org.osgi.framework.Bundle getExportingBundle() {
		if (supplier.isStale())
			return null;
		return supplier.getBundleHost();
	}

	public Bundle[] getImportingBundles() {
		if (supplier.isStale())
			return null;
		AbstractBundle bundle = (AbstractBundle) getExportingBundle();
		if (bundle == null)
			return null;
		AbstractBundle[] bundles = bundle.framework.getAllBundles();
		ArrayList importers = new ArrayList(10);
		PackageSource supplierSource = supplier.createPackageSource(exportedPackage, false);
		for (int i = 0; i < bundles.length; i++) {
			if (!(bundles[i] instanceof BundleHost))
				continue;
			BundleLoader loader = ((BundleHost) bundles[i]).getBundleLoader();
			if (loader == null)
				continue;
			PackageSource importerSource = loader.getPackageSource(getName());
			if (supplierSource != null && supplierSource.hasCommonSource(importerSource))
				importers.add(bundles[i]);
		}
		return (Bundle[]) importers.toArray(new Bundle[importers.size()]);
	}

	public String getSpecificationVersion() {
		return specVersion;
	}

	public boolean isRemovalPending() {
		BundleDescription exporter = exportedPackage.getExporter();
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
}

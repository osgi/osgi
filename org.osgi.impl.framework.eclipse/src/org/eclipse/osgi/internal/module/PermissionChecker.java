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
package org.eclipse.osgi.internal.module;

import org.eclipse.osgi.service.resolver.ExportPackageDescription;
import org.eclipse.osgi.service.resolver.ImportPackageSpecification;
import org.osgi.framework.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class PermissionChecker {
	private BundleContext context;

	public PermissionChecker(BundleContext context) {
		this.context = context;
	}

	public boolean checkImportPermission(ImportPackageSpecification ips, ExportPackageDescription epd) {
		if (context == null) // a null context indicates that permissions should not be checked.
			return true;
		boolean success = true;
		// first check the exporter permissions
		Bundle exporter = context.getBundle(epd.getExporter().getBundleId());
		if (exporter != null && (exporter.getState() & Bundle.UNINSTALLED) == 0)
			success = exporter.hasPermission(new PackagePermission(epd.getName(), PackagePermission.EXPORT));
		// now check the importer permissions
		Bundle importer = context.getBundle(ips.getBundle().getBundleId());
		if (success && importer != null && (importer.getState() & Bundle.UNINSTALLED) == 0)
			success = importer.hasPermission(new PackagePermission(ips.getName(), PackagePermission.IMPORT));
		return success;
	}
}

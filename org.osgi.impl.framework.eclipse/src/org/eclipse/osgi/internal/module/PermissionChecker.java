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

import org.eclipse.osgi.service.resolver.*;
import org.osgi.framework.*;

public class PermissionChecker {
	private BundleContext context;
	private boolean checkPermissions = false;

	public PermissionChecker(BundleContext context, boolean checkPermissions) {
		this.context = context;
		this.checkPermissions = checkPermissions;
	}

	public boolean checkImportPermission(ImportPackageSpecification ips, ExportPackageDescription epd) {
		if (!checkPermissions)
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

	public boolean checkBundlePermission(VersionConstraint vc, BundleDescription bd) {
		if (!checkPermissions)
			return true;
		boolean success = true;
		boolean requireBundle = vc instanceof BundleSpecification;
		// first check the bundle description
		Bundle provider = context.getBundle(bd.getBundleId());
		if (provider != null && (provider.getState() & Bundle.UNINSTALLED) == 0)
			success = provider.hasPermission(new BundlePermission(bd.getSymbolicName(), requireBundle ? BundlePermission.PROVIDE : BundlePermission.HOST));
		// now check the requirer permissions
		Bundle requirer = context.getBundle(vc.getBundle().getBundleId());
		if (success && requirer != null && (requirer.getState() & Bundle.UNINSTALLED) == 0)
			success = requirer.hasPermission(new BundlePermission(vc.getName(), requireBundle ? BundlePermission.REQUIRE : BundlePermission.FRAGMENT));
		return success;
	}
}

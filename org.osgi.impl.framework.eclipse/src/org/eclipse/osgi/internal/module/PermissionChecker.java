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

import java.security.Permission;
import org.eclipse.osgi.service.resolver.*;
import org.osgi.framework.*;

public class PermissionChecker {
	private BundleContext context;
	private boolean checkPermissions = false;

	public PermissionChecker(BundleContext context, boolean checkPermissions) {
		this.context = context;
		this.checkPermissions = checkPermissions;
	}

	/*
	 * checks the permission for a bundle to import/reqiure a constraint
	 * and for a bundle to export/provide a package/BSN
	 */
	public boolean checkPermission(VersionConstraint vc, BaseDescription bd) {
		if (!checkPermissions)
			return true;
		boolean success = false;
		Permission producerPermission = null, consumerPermission = null;
		Bundle producer = null, consumer = null;
		if (vc instanceof ImportPackageSpecification) {
			producerPermission = new PackagePermission(bd.getName(), PackagePermission.EXPORT);
			consumerPermission = new PackagePermission(vc.getName(), PackagePermission.IMPORT);
			producer = context.getBundle(((ExportPackageDescription) bd).getExporter().getBundleId());
		} else {
			boolean requireBundle = vc instanceof BundleSpecification;
			producerPermission = new BundlePermission(bd.getName(), requireBundle ? BundlePermission.PROVIDE : BundlePermission.HOST);
			consumerPermission = new BundlePermission(vc.getName(), requireBundle ? BundlePermission.REQUIRE : BundlePermission.FRAGMENT);
			producer = context.getBundle(((BundleDescription) bd).getBundleId());
		}
		consumer = context.getBundle(vc.getBundle().getBundleId());
		if (producer != null && (producer.getState() & Bundle.UNINSTALLED) == 0)
			success = producer.hasPermission(producerPermission);
		if (success && consumer != null && (consumer.getState() & Bundle.UNINSTALLED) == 0)
			success = consumer.hasPermission(consumerPermission);
		return success;
	}
}

/*
 * Copyright (c) OSGi Alliance (2000-2009).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.permissions.filtered.setPermission;

import java.security.AllPermission;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.permissions.filtered.util.*;

/**
 * 
 * @author Shigekuni KONDO, Ikuo YAMASAKI, NTT Corporation
 */
public class SetPermissionActivator implements BundleActivator {

	private PermissionAdmin permAdmin;
	private BundleContext context;
	private static final String RESET_PERMISSION_BUNDLE_LOCATION = "bundles/resetPermission.jar";

	public void start(BundleContext context) throws Exception {
		this.context = context;
		this.setAllpermission(RESET_PERMISSION_BUNDLE_LOCATION);
		System.out.println("SetPermission Bundle is going to start.");
		try {
			context.registerService(ISetPermissionService.class.getName(), new SetPermissionService(context), null);
			System.out
					.println("# SetPermission Bundle > Succeed in registering service: " + ISetPermissionService.class.getName());

		} catch (Exception e) {
			System.out.println("# SetPermission Bundle > Fail to register service: " + ISetPermissionService.class.getName());
			throw e;
		}
	}

	public void stop(BundleContext context) throws Exception {

		ServiceReference ref = context
				.getServiceReference(PermissionAdmin.class.getName());
		if (ref == null) {
			System.out.println("Fail to get ServiceReference of " + PermissionAdmin.class.getName());
		}
		permAdmin = (PermissionAdmin) context.getService(ref);
		permAdmin.setDefaultPermissions(null);
		String[] location = permAdmin.getLocations();
		if (location == null) {
			System.out.println("getLocations ERROR");
			return;
		}
		for (int i = 0; i < location.length; i++) {
			permAdmin.setPermissions(location[i], null);
		}
	}
	
	private void setAllpermission(String bundleLocation){
		ServiceReference ref = context
			.getServiceReference(PermissionAdmin.class.getName());
		if (ref == null) {
			System.out.println("Fail to get ServiceReference of "
					+ PermissionAdmin.class.getName());
			return;
		}
		permAdmin = (PermissionAdmin) context.getService(ref);
		PermissionInfo[] pisAllPerm = new PermissionInfo[1];
		pisAllPerm[0] = new PermissionInfo("("+AllPermission.class.getName()+")");
		permAdmin.setPermissions(bundleLocation, pisAllPerm);
	}
	
}

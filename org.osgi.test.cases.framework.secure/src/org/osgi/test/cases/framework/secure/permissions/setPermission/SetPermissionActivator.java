/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.test.cases.framework.secure.permissions.setPermission;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.test.cases.framework.secure.permissions.util.Util;

/**
 * 
 * @author Shigekuni KONDO, Ikuo YAMASAKI, NTT Corporation
 */
public class SetPermissionActivator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		// empty
	}

	public void stop(BundleContext context) throws Exception {

		ServiceReference<PermissionAdmin> ref = context
				.getServiceReference(PermissionAdmin.class);
		if (ref == null) {
			if (Util.debug)
				System.out.println("Fail to get ServiceReference of "
						+ PermissionAdmin.class.getName());
		}
		PermissionAdmin permAdmin = context.getService(ref);
		permAdmin.setDefaultPermissions(null);
		String[] locations = permAdmin.getLocations();
		if (locations == null) {
			if (Util.debug)
				System.out.println("getLocations ERROR");
		}
		else
			for (int i = 0; i < locations.length; i++) {
				permAdmin.setPermissions(locations[i], null);
			}
	}

}

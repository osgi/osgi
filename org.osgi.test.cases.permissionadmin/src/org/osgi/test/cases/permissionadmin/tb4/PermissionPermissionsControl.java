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
package org.osgi.test.cases.permissionadmin.tb4;

import static junit.framework.TestCase.*;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.permissionadmin.PermissionAdmin;

public class PermissionPermissionsControl implements
		BundleActivator {

	public void start(BundleContext context) throws Exception {
		ServiceReference<PermissionAdmin> ref = context
				.getServiceReference(PermissionAdmin.class);
		PermissionAdmin	permissionAdmin = ref == null ? null : context.getService(ref);
		assertNotNull("No Permission Admin", permissionAdmin);
		try {
			System.out.println(System.getSecurityManager());
			assertNotNull(System.getSecurityManager());
			permissionAdmin.setDefaultPermissions(null);
			fail("Were able to set default permissions without "
					+ "admin permission");
		}
		catch (SecurityException e) {
			// Do nothing; PASS
		}
		try {
			permissionAdmin.setPermissions("fake.jar", null);
			fail("Were able to set permissions without " + "admin permission");
		}
		catch (SecurityException e) {
			// Do nothing; PASS
		}
		/*
		 * trace("Will try to write to a file"); File f =
		 * getContext().getDataFile("nicke.txt"); FileOutputStream o = new
		 * FileOutputStream(f); o.write(65); o.write(66); o.write(67);
		 * o.close(); trace("Succeeded writing to the file!"); try {
		 * Thread.sleep(30000); } catch(InterruptedException e) {}
		 */
	}

	public void stop(BundleContext context) throws Exception {
		// Do nothing.
	}
}

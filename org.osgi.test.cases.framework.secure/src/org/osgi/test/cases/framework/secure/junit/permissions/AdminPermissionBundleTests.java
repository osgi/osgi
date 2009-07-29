/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.osgi.test.cases.framework.secure.junit.permissions;

import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.test.support.PermissionTestCase;

public class AdminPermissionBundleTests extends PermissionTestCase {

	public void testRecursion() throws Exception {
		Bundle admin2 = install("permissions.admin2.jar");
		Bundle admin1 = install("permissions.admin1.jar");
		try {
			admin2.start();
			admin1.start();
		}
		finally {
			admin1.uninstall();
			admin2.uninstall();
		}
	}

	public void testBundleSource() {
		AdminPermission granted = new AdminPermission("(id=*)", "class");
		AdminPermission requested = new AdminPermission(getContext()
				.getBundle(), "class");
		assertImplies(granted, requested);
	}
}

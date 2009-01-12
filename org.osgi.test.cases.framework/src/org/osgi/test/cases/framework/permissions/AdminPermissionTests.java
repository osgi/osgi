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

package org.osgi.test.cases.framework.permissions;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.PropertyPermission;

import org.osgi.framework.AdminPermission;
import org.osgi.test.support.PermissionTestCase;

public class AdminPermissionTests extends PermissionTestCase {

	public void testAdminPermission() {
		AdminPermission p1 = new AdminPermission();
		AdminPermission p2 = new AdminPermission("*", "*"); //$NON-NLS-1$ //$NON-NLS-2$
		Permission op = new PropertyPermission("java.home", "read"); //$NON-NLS-1$ //$NON-NLS-2$

		shouldImply(p1, p2);
		shouldImply(p1, p1);
		shouldNotImply(p1, op);

		shouldEqual(p1, p2);
		shouldNotEqual(p1, op);

		PermissionCollection pc = p1.newPermissionCollection();

		checkEnumeration(pc.elements(), true);

		shouldNotImply(pc, p1);

		shouldAdd(pc, p1);
		shouldAdd(pc, p2);
		shouldNotAdd(pc, op);

		pc.setReadOnly();

		shouldNotAdd(pc, new AdminPermission());

		shouldImply(pc, p1);
		shouldImply(pc, p2);
		shouldNotImply(pc, op);

		checkEnumeration(pc.elements(), false);

		testSerialization(p1);
		testSerialization(p2);
	}

}

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

package org.osgi.test.cases.clusterinfo.junit;

import java.security.PermissionCollection;

import org.osgi.service.clusterinfo.ClusterTagPermission;
import org.osgi.test.support.PermissionTestCase;

public class ClusterTagPermissionTestCase extends PermissionTestCase {

	public void testInvalid() {
		invalidClusterTagPermission("mycluster", "x");
		invalidClusterTagPermission("mycluster", "   add  ,  x   ");
		invalidClusterTagPermission("mycluster", "");
		invalidClusterTagPermission("mycluster", "      ");
		invalidClusterTagPermission("mycluster", null);
		invalidClusterTagPermission("mycluster", ",");
		invalidClusterTagPermission("mycluster", ",xxx");
		invalidClusterTagPermission("mycluster", "xxx,");
		invalidClusterTagPermission("mycluster", "add,");
		invalidClusterTagPermission("mycluster", "add,   ");
		invalidClusterTagPermission("mycluster", "addme,");
		invalidClusterTagPermission("mycluster", "addme,");
		invalidClusterTagPermission("mycluster", ",add");
		invalidClusterTagPermission("mycluster", "   addme   ");
		invalidClusterTagPermission("mycluster", "   ad");
		invalidClusterTagPermission("", "   add");
	}

	public void testPermissions() {

		ClusterTagPermission p = new ClusterTagPermission("mycluster", "ADD");
		ClusterTagPermission p2 = new ClusterTagPermission("mycluster", "ADD");
		ClusterTagPermission p3 = new ClusterTagPermission("tag", "ADD");
		ClusterTagPermission p_all = new ClusterTagPermission("*", "ADD");

		assertImplies(p, p2);
		assertImplies(p_all, p);
		assertImplies(p_all, p2);
		assertImplies(p_all, p3);

		assertNotImplies(p, p3);
		assertNotImplies(p, p_all);
		assertNotImplies(p2, p_all);
		assertNotImplies(p3, p_all);

		assertEquals(p, p);
		assertEquals(p2, p2);
		assertEquals(p3, p3);
		assertEquals(p_all, p_all);

		assertEquals(p, p2);
		assertNotEquals(p, p3);
		assertNotEquals(p, p_all);
		assertNotEquals(p2, p3);
		assertNotEquals(p2, p_all);
		assertNotEquals(p3, p_all);

		PermissionCollection pc = p.newPermissionCollection();

		checkEnumeration(pc.elements(), true);

		assertNotImplies(pc, p3);

		assertAddPermission(pc, p3);
		assertImplies(pc, p3);
		assertNotImplies(pc, p_all);

		assertAddPermission(pc, p_all);
		assertImplies(pc, p);
		assertImplies(pc, p2);
		assertImplies(pc, p3);
		assertImplies(pc, p_all);

		pc.setReadOnly();

		assertNotAddPermission(pc, p2);

		checkEnumeration(pc.elements(), false);

		assertSerializable(p);
		assertSerializable(p2);
		assertSerializable(p3);
		assertSerializable(p_all);
	}

	private void invalidClusterTagPermission(String name, String actions) {
		try {
			ClusterTagPermission p = new ClusterTagPermission(name, actions);
			fail(p + " created with invalid actions"); 
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}
}

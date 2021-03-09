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

package org.osgi.test.cases.cm.junit;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.PropertyPermission;

import org.osgi.service.cm.ConfigurationPermission;
import org.osgi.test.support.PermissionTestCase;

public class ConfigurationPermissionTests extends PermissionTestCase {

	public void testInvalid() {
		invalidConfigurationPermission("a/b/c", "x");
		invalidConfigurationPermission("a/b/c", "   configure  ,  x   ");
		invalidConfigurationPermission("a/b/c", "");
		invalidConfigurationPermission("a/b/c", "      ");
		invalidConfigurationPermission("a/b/c", null);
		invalidConfigurationPermission("a/b/c", ",");
		invalidConfigurationPermission("a/b/c", ",xxx");
		invalidConfigurationPermission("a/b/c", "xxx,");
		invalidConfigurationPermission("a/b/c", "configure,");
		invalidConfigurationPermission("a/b/c", "target,   ");
		invalidConfigurationPermission("a/b/c", "attribute, ");
		invalidConfigurationPermission("a/b/c", "configureme,");
		invalidConfigurationPermission("a/b/c", "targetme,");
		invalidConfigurationPermission("a/b/c", "attributeme,");
		invalidConfigurationPermission("a/b/c", ",configure");
		invalidConfigurationPermission("a/b/c", ",target");
		invalidConfigurationPermission("a/b/c", ",attribute");
		invalidConfigurationPermission("a/b/c", "   configureme   ");
		invalidConfigurationPermission("a/b/c", "   targetme     ");
		invalidConfigurationPermission("a/b/c", "   attributeme     ");
		invalidConfigurationPermission("a/b/c", "   configur");
		invalidConfigurationPermission("a/b/c", "   targe");
		invalidConfigurationPermission("a/b/c", "   attribut");
		invalidConfigurationPermission("", "   target");
	}

	public void testActions() {
		Permission op = new PropertyPermission("java.home", "read"); 

		ConfigurationPermission p10 = new ConfigurationPermission(
				"com/foo/service1", "    CONFIGURE,target , attribute   ");
		ConfigurationPermission p11 = new ConfigurationPermission(
				"com/foo/service1",
				"    CONFIGURE,target   ");
		ConfigurationPermission p12 = new ConfigurationPermission(
				"com/foo/service1",
 "TARGET  ,   configure");
		ConfigurationPermission p13 = new ConfigurationPermission(
				"com/foo/service1",
 "tarGET   ");
		ConfigurationPermission p14 = new ConfigurationPermission(
				"com/foo/service1",
 "    Configure    ");
		ConfigurationPermission p15 = new ConfigurationPermission(
				"com/foo/service1", " aTTribute ");

		assertImplies(p10, p10);
		assertImplies(p10, p11);
		assertImplies(p10, p12);
		assertImplies(p10, p13);
		assertImplies(p10, p14);
		assertImplies(p10, p15);

		assertImplies(p11, p11);
		assertImplies(p11, p12);
		assertImplies(p11, p13);
		assertImplies(p11, p14);

		assertImplies(p12, p11);
		assertImplies(p12, p12);
		assertImplies(p12, p13);
		assertImplies(p12, p14);

		assertImplies(p13, p13);
		assertImplies(p14, p14);

		assertNotImplies(p13, p10);
		assertNotImplies(p13, p11);
		assertNotImplies(p13, p14);
		assertNotImplies(p13, p15);

		assertNotImplies(p14, p10);
		assertNotImplies(p14, p11);
		assertNotImplies(p14, p13);
		assertNotImplies(p14, p15);

		assertNotImplies(p15, p10);
		assertNotImplies(p15, p11);
		assertNotImplies(p15, p13);
		assertNotImplies(p15, p14);

		assertNotImplies(p11, op);

		assertEquals(p10, p10);
		assertEquals(p11, p11);
		assertEquals(p11, p12);
		assertEquals(p12, p11);
		assertEquals(p12, p12);
		assertEquals(p13, p13);
		assertEquals(p14, p14);

		assertNotEquals(p10, p11);
		assertNotEquals(p10, p12);
		assertNotEquals(p10, p13);
		assertNotEquals(p10, p14);
		assertNotEquals(p10, p15);
		assertNotEquals(p11, p10);
		assertNotEquals(p11, p13);
		assertNotEquals(p11, p14);
		assertNotEquals(p11, p15);
		assertNotEquals(p12, p10);
		assertNotEquals(p12, p13);
		assertNotEquals(p12, p14);
		assertNotEquals(p12, p15);
		assertNotEquals(p13, p10);
		assertNotEquals(p13, p11);
		assertNotEquals(p13, p12);
		assertNotEquals(p13, p14);
		assertNotEquals(p13, p15);
		assertNotEquals(p14, p10);
		assertNotEquals(p14, p11);
		assertNotEquals(p14, p12);
		assertNotEquals(p14, p13);
		assertNotEquals(p14, p15);
		assertNotEquals(p15, p10);
		assertNotEquals(p15, p11);
		assertNotEquals(p15, p12);
		assertNotEquals(p15, p13);
		assertNotEquals(p15, p14);

		PermissionCollection pc = p13.newPermissionCollection();

		checkEnumeration(pc.elements(), true);

		assertNotImplies(pc, p10);

		assertAddPermission(pc, p14);
		assertImplies(pc, p14);
		assertNotImplies(pc, p10);
		assertNotImplies(pc, p11);
		assertNotImplies(pc, p12);
		assertNotImplies(pc, p13);
		assertNotImplies(pc, p15);

		assertAddPermission(pc, p13);
		assertNotImplies(pc, p10);
		assertImplies(pc, p11);
		assertImplies(pc, p12);
		assertImplies(pc, p13);
		assertImplies(pc, p14);
		assertNotImplies(pc, p15);

		assertAddPermission(pc, p15);
		assertImplies(pc, p10);
		assertImplies(pc, p11);
		assertImplies(pc, p12);
		assertImplies(pc, p13);
		assertImplies(pc, p14);
		assertImplies(pc, p15);

		assertNotAddPermission(pc, op);

		pc = p13.newPermissionCollection();

		assertAddPermission(pc, p13);
		assertImplies(pc, p13);
		assertNotImplies(pc, p11);
		assertNotImplies(pc, p12);
		assertNotImplies(pc, p14);

		assertAddPermission(pc, p14);
		assertImplies(pc, p11);
		assertImplies(pc, p12);
		assertImplies(pc, p13);
		assertImplies(pc, p14);

		pc = p10.newPermissionCollection();

		assertAddPermission(pc, p10);
		assertImplies(pc, p10);
		assertImplies(pc, p11);
		assertImplies(pc, p12);
		assertImplies(pc, p13);
		assertImplies(pc, p14);
		assertImplies(pc, p15);

		pc.setReadOnly();

		assertNotAddPermission(pc, p12);

		checkEnumeration(pc.elements(), false);

		assertSerializable(p10);
		assertSerializable(p11);
		assertSerializable(p12);
		assertSerializable(p13);
		assertSerializable(p14);
		assertSerializable(p15);
	}

	public void testNames() {
		ConfigurationPermission p21 = new ConfigurationPermission(
				"com/foo/service2",
 "configure");
		ConfigurationPermission p22 = new ConfigurationPermission("com/foo/*",
				"configure");
		ConfigurationPermission p23 = new ConfigurationPermission("com/*",
				"configure");
		ConfigurationPermission p24 = new ConfigurationPermission("*",
				"configure");

		assertImplies(p21, p21);
		assertImplies(p22, p21);
		assertImplies(p23, p21);
		assertImplies(p24, p21);

		assertImplies(p22, p22);
		assertImplies(p23, p22);
		assertImplies(p24, p22);

		assertImplies(p23, p23);
		assertImplies(p24, p23);

		assertImplies(p24, p24);

		assertNotImplies(p21, p22);
		assertNotImplies(p21, p23);
		assertNotImplies(p21, p24);

		assertNotImplies(p22, p23);
		assertNotImplies(p22, p24);

		assertNotImplies(p23, p24);

		PermissionCollection pc = p21.newPermissionCollection();

		assertAddPermission(pc, p21);
		assertImplies(pc, p21);
		assertNotImplies(pc, p22);
		assertNotImplies(pc, p23);
		assertNotImplies(pc, p24);

		assertAddPermission(pc, p22);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertNotImplies(pc, p23);
		assertNotImplies(pc, p24);

		assertAddPermission(pc, p23);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertImplies(pc, p23);
		assertNotImplies(pc, p24);

		assertAddPermission(pc, p24);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertImplies(pc, p23);
		assertImplies(pc, p24);

		pc = p22.newPermissionCollection();

		assertAddPermission(pc, p22);
		assertSerializable(pc);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertNotImplies(pc, p23);
		assertNotImplies(pc, p24);

		pc = p23.newPermissionCollection();

		assertAddPermission(pc, p23);
		assertSerializable(pc);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertImplies(pc, p23);
		assertNotImplies(pc, p24);

		pc = p24.newPermissionCollection();

		assertAddPermission(pc, p24);
		assertSerializable(pc);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertImplies(pc, p23);
		assertImplies(pc, p24);

		assertSerializable(p21);
		assertSerializable(p22);
		assertSerializable(p23);
		assertSerializable(p24);
	}
	
	public void testWildcardNames() {
		ConfigurationPermission p21 = new ConfigurationPermission(
				"com/foo/service2", "configure");
		ConfigurationPermission p22 = new ConfigurationPermission("com/foo/*",
				"configure");
		ConfigurationPermission p23 = new ConfigurationPermission(
				"com/*/service2", "configure");
		ConfigurationPermission p24 = new ConfigurationPermission("*/service2",
				"configure");

		assertImplies(p21, p21);
		assertImplies(p22, p21);
		assertImplies(p23, p21);
		assertImplies(p24, p21);

		assertImplies(p22, p22);
		assertNotImplies(p23, p22);
		assertNotImplies(p24, p22);

		assertImplies(p23, p23);
		assertImplies(p24, p23);

		assertImplies(p24, p24);

		assertNotImplies(p21, p22);
		assertNotImplies(p21, p23);
		assertNotImplies(p21, p24);

		assertNotImplies(p22, p23);
		assertNotImplies(p22, p24);

		assertNotImplies(p23, p24);

		PermissionCollection pc = p21.newPermissionCollection();

		assertAddPermission(pc, p21);
		assertImplies(pc, p21);
		assertNotImplies(pc, p22);
		assertNotImplies(pc, p23);
		assertNotImplies(pc, p24);

		assertAddPermission(pc, p22);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertNotImplies(pc, p23);
		assertNotImplies(pc, p24);

		assertAddPermission(pc, p23);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertImplies(pc, p23);
		assertNotImplies(pc, p24);

		assertAddPermission(pc, p24);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertImplies(pc, p23);
		assertImplies(pc, p24);

		pc = p22.newPermissionCollection();

		assertAddPermission(pc, p22);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertNotImplies(pc, p23);
		assertNotImplies(pc, p24);

		pc = p23.newPermissionCollection();

		assertAddPermission(pc, p23);
		assertImplies(pc, p21);
		assertNotImplies(pc, p22);
		assertImplies(pc, p23);
		assertNotImplies(pc, p24);

		pc = p24.newPermissionCollection();

		assertAddPermission(pc, p24);
		assertImplies(pc, p21);
		assertNotImplies(pc, p22);
		assertImplies(pc, p23);
		assertImplies(pc, p24);

		assertSerializable(p21);
		assertSerializable(p22);
		assertSerializable(p23);
		assertSerializable(p24);
	}

	public void testActionImplications() {
		ConfigurationPermission target = new ConfigurationPermission("*",
				"target");
		ConfigurationPermission configure = new ConfigurationPermission("*",
				"configure");
		ConfigurationPermission attribute = new ConfigurationPermission("*",
				"attribute");

		assertImplies(target, target);
		assertNotImplies(target, configure);
		assertNotImplies(target, attribute);

		assertNotImplies(configure, target);
		assertImplies(configure, configure);
		assertNotImplies(configure, attribute);

		assertNotImplies(attribute, target);
		assertNotImplies(attribute, configure);
		assertImplies(attribute, attribute);
	}

	private void invalidConfigurationPermission(String name, String actions) {
		try {
			ConfigurationPermission p = new ConfigurationPermission(name,
					actions);
			fail(p + " created with invalid actions"); 
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}
}

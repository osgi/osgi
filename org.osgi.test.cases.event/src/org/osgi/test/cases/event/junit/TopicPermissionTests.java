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

package org.osgi.test.cases.event.junit;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.PropertyPermission;

import org.osgi.service.event.TopicPermission;
import org.osgi.test.support.PermissionTestCase;

public class TopicPermissionTests extends PermissionTestCase {

	public void testInvalid() {
		invalidTopicPermission("a/b/c", "x");
		invalidTopicPermission("a/b/c", "   subscribe  ,  x   ");
		invalidTopicPermission("a/b/c", "");
		invalidTopicPermission("a/b/c", "      ");
		invalidTopicPermission("a/b/c", null);
		invalidTopicPermission("a/b/c", ",");
		invalidTopicPermission("a/b/c", ",xxx");
		invalidTopicPermission("a/b/c", "xxx,");
		invalidTopicPermission("a/b/c", "subscribe,");
		invalidTopicPermission("a/b/c", "publish,   ");
		invalidTopicPermission("a/b/c", "subscribeme,");
		invalidTopicPermission("a/b/c", "publishme,");
		invalidTopicPermission("a/b/c", ",subscribe");
		invalidTopicPermission("a/b/c", ",publish");
		invalidTopicPermission("a/b/c", "   subscribeme   ");
		invalidTopicPermission("a/b/c", "   publishme     ");
		invalidTopicPermission("a/b/c", "   subscrib");
		invalidTopicPermission("a/b/c", "   publis"); 
		invalidTopicPermission("", "   publish"); 
	}

	public void testActions() {
		Permission op = new PropertyPermission("java.home", "read"); 

		TopicPermission p11 = new TopicPermission("com/foo/service1",
				"    SUBSCRIBE,publish   ");
		TopicPermission p12 = new TopicPermission("com/foo/service1",
				"PUBLISH  ,   subscribe");
		TopicPermission p13 = new TopicPermission("com/foo/service1",
				"publISH   ");
		TopicPermission p14 = new TopicPermission("com/foo/service1",
				"    Subscribe    "); 

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

		assertNotImplies(p13, p11);
		assertNotImplies(p13, p12);

		assertNotImplies(p14, p11);
		assertNotImplies(p14, p12);

		assertNotImplies(p13, p14);
		assertNotImplies(p14, p13);

		assertNotImplies(p11, op);

		assertEquals(p11, p11);
		assertEquals(p11, p12);
		assertEquals(p12, p11);
		assertEquals(p12, p12);
		assertEquals(p13, p13);
		assertEquals(p14, p14);

		assertNotEquals(p11, p13);
		assertNotEquals(p11, p14);
		assertNotEquals(p12, p13);
		assertNotEquals(p12, p14);
		assertNotEquals(p13, p11);
		assertNotEquals(p13, p12);
		assertNotEquals(p13, p14);
		assertNotEquals(p14, p11);
		assertNotEquals(p14, p12);
		assertNotEquals(p14, p13);

		PermissionCollection pc = p13.newPermissionCollection();

		checkEnumeration(pc.elements(), true);

		assertNotImplies(pc, p11);

		assertAddPermission(pc, p14);
		assertImplies(pc, p14);
		assertNotImplies(pc, p11);
		assertNotImplies(pc, p12);
		assertNotImplies(pc, p13);

		assertAddPermission(pc, p13);
		assertImplies(pc, p11);
		assertImplies(pc, p12);
		assertImplies(pc, p13);
		assertImplies(pc, p14);

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

		pc = p11.newPermissionCollection();

		assertAddPermission(pc, p11);
		assertImplies(pc, p11);
		assertImplies(pc, p12);
		assertImplies(pc, p13);
		assertImplies(pc, p14);

		pc.setReadOnly();

		assertNotAddPermission(pc, p12);

		checkEnumeration(pc.elements(), false);

		assertSerializable(p11);
		assertSerializable(p12);
		assertSerializable(p13);
		assertSerializable(p14);
	}

	public void testNames() {
		TopicPermission p21 = new TopicPermission("com/foo/service2",
				"subscribe");
		TopicPermission p22 = new TopicPermission("com/foo/*", "subscribe");
		TopicPermission p23 = new TopicPermission("com/*", "subscribe");
		TopicPermission p24 = new TopicPermission("*", "subscribe"); 

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
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertNotImplies(pc, p23);
		assertNotImplies(pc, p24);

		pc = p23.newPermissionCollection();

		assertAddPermission(pc, p23);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertImplies(pc, p23);
		assertNotImplies(pc, p24);

		pc = p24.newPermissionCollection();

		assertAddPermission(pc, p24);
		assertImplies(pc, p21);
		assertImplies(pc, p22);
		assertImplies(pc, p23);
		assertImplies(pc, p24);

		assertSerializable(p21);
		assertSerializable(p22);
		assertSerializable(p23);
		assertSerializable(p24);
	}
	
	public void testActionImplications() {
		TopicPermission publish = new TopicPermission("*", "publish");
		TopicPermission subscribe = new TopicPermission("*", "subscribe");

		assertImplies(publish, publish);
		assertNotImplies(publish, subscribe);
		assertNotImplies(subscribe, publish);
		assertImplies(subscribe, subscribe);
	}

	private void invalidTopicPermission(String name, String actions) {
		try {
			TopicPermission p = new TopicPermission(name, actions);
			fail(p + " created with invalid actions"); 
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}
}

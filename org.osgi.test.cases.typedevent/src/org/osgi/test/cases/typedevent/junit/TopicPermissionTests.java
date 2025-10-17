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

package org.osgi.test.cases.typedevent.junit;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.PropertyPermission;

import org.osgi.service.typedevent.TopicPermission;
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

	public void testSingleLevelWildcards() {
		TopicPermission p31 = new TopicPermission("com/foo/service3",
				"subscribe");
		TopicPermission p32 = new TopicPermission("com/+/service3",
				"subscribe");
		TopicPermission p33 = new TopicPermission("com/+/+", "subscribe");
		TopicPermission p34 = new TopicPermission("+", "subscribe");
		TopicPermission p35 = new TopicPermission("+/foo/+", "subscribe");

		// Specific topic implies itself
		assertImplies(p31, p31);

		// Single-level wildcard implies specific matching topic
		assertImplies(p32, p31);

		// Multiple single-level wildcards imply matching topics
		assertImplies(p33, p31);
		assertImplies(p33, p32);

		// + alone only implies single-level topics
		assertNotImplies(p34, p31);
		assertNotImplies(p34, p32);
		assertNotImplies(p34, p33);

		// Specific topic does not imply wildcard
		assertNotImplies(p31, p32);
		assertNotImplies(p31, p33);
		assertNotImplies(p31, p34);

		// More specific single-level wildcard does not imply less specific
		assertNotImplies(p32, p33);
		assertNotImplies(p32, p35);

		// Test with permission collection
		PermissionCollection pc = p31.newPermissionCollection();

		assertAddPermission(pc, p31);
		assertImplies(pc, p31);
		assertNotImplies(pc, p32);
		assertNotImplies(pc, p33);

		assertAddPermission(pc, p32);
		assertImplies(pc, p31);
		assertImplies(pc, p32);
		assertNotImplies(pc, p33);

		assertAddPermission(pc, p33);
		assertImplies(pc, p31);
		assertImplies(pc, p32);
		assertImplies(pc, p33);

		assertSerializable(p31);
		assertSerializable(p32);
		assertSerializable(p33);
		assertSerializable(p34);
		assertSerializable(p35);
	}

	public void testSingleLevelWildcardWithMultiLevel() {
		TopicPermission p41 = new TopicPermission("com/foo/bar/baz",
				"subscribe");
		TopicPermission p42 = new TopicPermission("com/+/*", "subscribe");
		TopicPermission p43 = new TopicPermission("com/foo/*", "subscribe");
		TopicPermission p44 = new TopicPermission("+/*", "subscribe");
		TopicPermission p45 = new TopicPermission("com/+/+", "subscribe");
		TopicPermission p46 = new TopicPermission("com/foo", "subscribe");

		// Single-level wildcard + multi-level wildcard implies matching topics
		assertImplies(p42, p41);
		assertImplies(p44, p41);

		// Multi-level wildcard alone implies matching topics
		assertImplies(p43, p41);

		// More specific does not imply less specific
		assertNotImplies(p41, p42);
		assertNotImplies(p41, p43);
		assertNotImplies(p41, p44);
		assertNotImplies(p43, p42);
		assertNotImplies(p43, p44);

		// com/+/* implies com/foo/*
		assertImplies(p42, p43);
		// com/+/* implies com/+/+ but com/+/+ does not imply com/+/*
		assertImplies(p42, p45);
		assertNotImplies(p45, p42);

		// neither com/+/* nor com/+/+ imply com/foo
		assertNotImplies(p42, p46);
		assertNotImplies(p45, p46);

		assertSerializable(p41);
		assertSerializable(p42);
		assertSerializable(p43);
		assertSerializable(p44);
		assertSerializable(p45);
		assertSerializable(p46);
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

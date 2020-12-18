/*
 * Copyright (c) OSGi Alliance (2018, 2020). All Rights Reserved.
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

package org.osgi.test.cases.bundle.annotations.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.osgi.framework.namespace.PackageNamespace.CAPABILITY_VERSION_ATTRIBUTE;
import static org.osgi.resource.Namespace.*;

import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.osgi.framework.Version;
import org.osgi.resource.Capability;
import org.osgi.test.support.string.Strings;

public class CapabilityAnnotationsTestClass extends AnnotationsTestCase {

	@Test
	public void testDirectlyAnnotatedCapability() throws Exception {
		testAnnotatedCapability();
	}

	@Test
	public void testIndirectlyAnnotatedCapability() throws Exception {
		testAnnotatedCapability();
	}

	private void testAnnotatedCapability() throws Exception {
		String namespace = testName.getMethodName();
		String name = "allOptions";
		Capability capability = getCapability(namespace);
		Map<String,String> directives = capability.getDirectives();
		assertThat(directives).as("capability directives")
				.containsKey(CAPABILITY_USES_DIRECTIVE)
				.containsEntry("x-directive", "directiveValue")
				.containsEntry(CAPABILITY_EFFECTIVE_DIRECTIVE, "osgi.test")
				.doesNotContainKeys("attr", "longAttr", "stringAttr",
						"doubleAttr", "versionAttr", "longList", "stringList",
						"doubleList", "versionList",
						CAPABILITY_VERSION_ATTRIBUTE, namespace);
		assertThat(Strings.split(directives.get(CAPABILITY_USES_DIRECTIVE)))
				.containsExactlyInAnyOrder(
						"org.osgi.impl.bundle.annotations.reqcap",
						"org.osgi.impl.bundle.annotations.export");

		Map<String,Object> attributes = capability.getAttributes();
		assertThat(attributes).as("capability attributes")
				.containsEntry(namespace, name)
				.containsEntry(CAPABILITY_VERSION_ATTRIBUTE,
						Version.valueOf("1.3"))
				.containsEntry("attr", "value")
				.containsEntry("longAttr", Long.valueOf(42))
				.containsEntry("stringAttr", "stringValue")
				.containsEntry("doubleAttr", Double.valueOf(4.2))
				.containsEntry("versionAttr", Version.valueOf("4.2"))
				.containsEntry("longList",
						Arrays.asList(Long.valueOf(2), Long.valueOf(3),
								Long.valueOf(4)))
				.containsEntry("stringList",
						Arrays.asList("one", "two", "three"))
				.containsEntry("doubleList",
						Arrays.asList(Double.valueOf(2.3), Double.valueOf(3.4),
								Double.valueOf(4.5)))
				.containsEntry("versionList",
						Arrays.asList(Version.valueOf("2.3"),
								Version.valueOf("3.4"), Version.valueOf("4.5")))
				.doesNotContainKeys("x-directive", CAPABILITY_USES_DIRECTIVE,
						CAPABILITY_EFFECTIVE_DIRECTIVE);
	}

	@Test
	public void testDirectlyAnnotatedDefaultOptions() throws Exception {
		String namespace = testName.getMethodName();
		Capability capability = getCapability(namespace);
		Map<String,String> directives = capability.getDirectives();
		assertThat(directives).as("capability directives").isEmpty();

		Map<String,Object> attributes = capability.getAttributes();
		assertThat(attributes).as("capability attributes").isEmpty();
	}

	@Test
	public void testDirectlyAnnotatedAttributes() throws Exception {
		String namespace = testName.getMethodName();
		Capability capability = getCapability(namespace);
		Map<String,String> directives = capability.getDirectives();
		assertThat(directives).as("capability directives")
				.containsOnlyKeys("x-directive")
				.containsEntry("x-directive", "directiveValue");

		Map<String,Object> attributes = capability.getAttributes();
		assertThat(attributes).as("capability attributes")
				.containsOnlyKeys("attr", "longAttr", "stringAttr",
						"doubleAttr", "longList", "stringList", "doubleList")
				.containsEntry("attr", "value")
				.containsEntry("longAttr", Long.valueOf(42))
				.containsEntry("stringAttr", "stringValue")
				.containsEntry("doubleAttr", Double.valueOf(4.2))
				.containsEntry("longList",
						Arrays.asList(Long.valueOf(2), Long.valueOf(3),
								Long.valueOf(4)))
				.containsEntry("stringList",
						Arrays.asList("one", "two", "three"))
				.containsEntry("doubleList", Arrays.asList(Double.valueOf(2.3),
						Double.valueOf(3.4), Double.valueOf(4.5)));
	}

	@Test
	public void testDirectlyAnnotatedAttributesDefaults() throws Exception {
		String namespace = testName.getMethodName();
		Capability capability = getCapability(namespace);
		Map<String,String> directives = capability.getDirectives();
		assertThat(directives).as("capability directives").isEmpty();

		Map<String,Object> attributes = capability.getAttributes();
		assertThat(attributes).as("capability attributes").isEmpty();
	}

	@Test
	public void testOverriding() throws Exception {
		String namespace = testName.getMethodName();
		String name = "override";
		Capability capability = getCapability(namespace);
		Map<String,String> directives = capability.getDirectives();
		assertThat(directives).as("capability directives")
				.containsEntry("x-name", "Second")
				.containsEntry("x-top-name", "fizzbuzz2");

		Map<String,Object> attributes = capability.getAttributes();
		assertThat(attributes).as("capability attributes")
				.containsEntry("foo", Arrays.asList("foobar3"))
				.containsEntry("name", "First")
				.containsEntry(namespace, name)
				.containsEntry(CAPABILITY_VERSION_ATTRIBUTE,
						Version.valueOf("1.5"));
	}
}

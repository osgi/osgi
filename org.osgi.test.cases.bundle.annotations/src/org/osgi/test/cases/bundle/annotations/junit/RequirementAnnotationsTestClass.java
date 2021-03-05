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

package org.osgi.test.cases.bundle.annotations.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.osgi.framework.namespace.PackageNamespace.CAPABILITY_VERSION_ATTRIBUTE;
import static org.osgi.resource.Namespace.*;

import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.osgi.framework.Filter;
import org.osgi.framework.Version;
import org.osgi.resource.Requirement;
import org.osgi.test.assertj.filter.FilterCondition;
import org.osgi.test.support.map.Maps;

public class RequirementAnnotationsTestClass extends AnnotationsTestCase {

	@Test
	public void testDirectlyAnnotatedRequirement() throws Exception {
		testAnnotatedRequirement();
	}

	@Test
	public void testIndirectlyAnnotatedRequirement() throws Exception {
		testAnnotatedRequirement();
	}

	private void testAnnotatedRequirement() throws Exception {
		String namespace = testName.getMethodName();
		String name = "allOptions";
		Requirement requirement = getRequirement(namespace);
		Map<String,String> directives = requirement.getDirectives();
		assertThat(directives).as("requirement directives")
				.containsKey(REQUIREMENT_FILTER_DIRECTIVE)
				.containsEntry("x-directive", "directiveValue")
				.containsEntry(REQUIREMENT_RESOLUTION_DIRECTIVE,
						RESOLUTION_OPTIONAL)
				.containsEntry(REQUIREMENT_CARDINALITY_DIRECTIVE,
						CARDINALITY_MULTIPLE)
				.containsEntry(REQUIREMENT_EFFECTIVE_DIRECTIVE, "osgi.test")
				.doesNotContainKeys("attr", "longAttr", "stringAttr",
						"doubleAttr", "versionAttr", "longList", "stringList",
						"doubleList", "versionList");
		String filterString = directives.get(REQUIREMENT_FILTER_DIRECTIVE);
		Filter filter = getContext().createFilter(filterString);
		assertThat(filter).as("requirement filter incorrect: %s", filterString)
				.is(new FilterCondition(Maps.mapOf(namespace, name,
						"foo", "bar", CAPABILITY_VERSION_ATTRIBUTE,
						Version.valueOf("1.3"))))
				.isNot(new FilterCondition(Maps.mapOf(namespace, name,
						"foo", "bar", CAPABILITY_VERSION_ATTRIBUTE,
						Version.valueOf("1.2"))))
				.is(new FilterCondition(Maps.mapOf(namespace, name,
						"foo", "bar", CAPABILITY_VERSION_ATTRIBUTE,
						Version.valueOf("1.9"))))
				.isNot(new FilterCondition(Maps.mapOf(namespace, name,
						"foo", "bar", CAPABILITY_VERSION_ATTRIBUTE,
						Version.valueOf("2.0"))))
				.isNot(new FilterCondition(Maps.mapOf(namespace, name,
						CAPABILITY_VERSION_ATTRIBUTE, Version.valueOf("1.3"))))
				.isNot(new FilterCondition(Maps.mapOf(namespace, name,
						"foo", "baz", CAPABILITY_VERSION_ATTRIBUTE,
						Version.valueOf("1.3"))));

		Map<String,Object> attributes = requirement.getAttributes();
		assertThat(attributes).as("requirement attributes")
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
				.doesNotContainKeys("x-directive",
						REQUIREMENT_RESOLUTION_DIRECTIVE,
						REQUIREMENT_CARDINALITY_DIRECTIVE,
						REQUIREMENT_EFFECTIVE_DIRECTIVE);
	}

	@Test
	public void testDirectlyAnnotatedDefaultOptions() throws Exception {
		String namespace = testName.getMethodName();
		Requirement requirement = getRequirement(namespace);
		Map<String,String> directives = requirement.getDirectives();
		assertThat(directives).as("requirement directives").isEmpty();

		Map<String,Object> attributes = requirement.getAttributes();
		assertThat(attributes).as("requirement attributes").isEmpty();
	}

	@Test
	public void testDirectlyAnnotatedAttributes() throws Exception {
		String namespace = testName.getMethodName();
		Requirement requirement = getRequirement(namespace);
		Map<String,String> directives = requirement.getDirectives();
		assertThat(directives).as("requirement directives")
				.containsOnlyKeys("x-directive")
				.containsEntry("x-directive", "directiveValue");

		Map<String,Object> attributes = requirement.getAttributes();
		assertThat(attributes).as("requirement attributes")
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
		Requirement requirement = getRequirement(namespace);
		Map<String,String> directives = requirement.getDirectives();
		assertThat(directives).as("requirement directives").isEmpty();

		Map<String,Object> attributes = requirement.getAttributes();
		assertThat(attributes).as("requirement attributes").isEmpty();
	}

	@Test
	public void testOverriding() throws Exception {
		String namespace = testName.getMethodName();
		String name = "override";
		Requirement requirement = getRequirement(namespace);
		Map<String,String> directives = requirement.getDirectives();
		assertThat(directives).as("requirement directives")
				.containsKey(REQUIREMENT_FILTER_DIRECTIVE)
				.containsEntry("x-name", "Second")
				.containsEntry("x-top-name", "fizzbuzz2");
		String filterString = directives.get(REQUIREMENT_FILTER_DIRECTIVE);
		Filter filter = getContext().createFilter(filterString);
		assertThat(filter).as("requirement filter incorrect: %s", filterString)
				.is(new FilterCondition(Maps.mapOf(namespace, name,
						CAPABILITY_VERSION_ATTRIBUTE, Version.valueOf("1.5"))))
				.isNot(new FilterCondition(Maps.mapOf(namespace, name,
						CAPABILITY_VERSION_ATTRIBUTE, Version.valueOf("1.4"))))
				.is(new FilterCondition(Maps.mapOf(namespace, name,
						CAPABILITY_VERSION_ATTRIBUTE, Version.valueOf("1.9"))))
				.isNot(new FilterCondition(Maps.mapOf(namespace, name,
						CAPABILITY_VERSION_ATTRIBUTE, Version.valueOf("2.0"))));

		Map<String,Object> attributes = requirement.getAttributes();
		assertThat(attributes).as("requirement attributes")
				.containsEntry("foo", Arrays.asList("foobar3"))
				.containsEntry("name", "First");
	}
}

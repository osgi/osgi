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
import static org.osgi.framework.namespace.AbstractWiringNamespace.CAPABILITY_BUNDLE_VERSION_ATTRIBUTE;
import static org.osgi.framework.namespace.PackageNamespace.CAPABILITY_BUNDLE_SYMBOLICNAME_ATTRIBUTE;
import static org.osgi.framework.namespace.PackageNamespace.CAPABILITY_VERSION_ATTRIBUTE;
import static org.osgi.framework.namespace.PackageNamespace.PACKAGE_NAMESPACE;
import static org.osgi.resource.Namespace.CAPABILITY_USES_DIRECTIVE;
import static org.osgi.resource.Namespace.REQUIREMENT_FILTER_DIRECTIVE;

import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.osgi.framework.Filter;
import org.osgi.framework.Version;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.test.assertj.filter.FilterCondition;
import org.osgi.test.support.map.Maps;
import org.osgi.test.support.string.Strings;

@SuppressWarnings("unused")
public class ExportAnnotationsTestCase extends AnnotationsTestCase {

	@Test
	public void testNoAttributes() throws Exception {
		String pkgName = "org.osgi.impl.bundle.annotations.export";
		Capability pkg = getPackageCapability(pkgName);
		Map<String,Object> attributes = pkg.getAttributes();
		assertThat(attributes).as("Package %s capability attributes", pkgName)
				.containsOnlyKeys(PACKAGE_NAMESPACE,
						CAPABILITY_VERSION_ATTRIBUTE,
						CAPABILITY_BUNDLE_SYMBOLICNAME_ATTRIBUTE,
						CAPABILITY_BUNDLE_VERSION_ATTRIBUTE)
				.containsEntry(CAPABILITY_VERSION_ATTRIBUTE,
						Version.valueOf("1.0"));
	}

	@Test
	public void testAttributes() throws Exception {
		String pkgName = "org.osgi.impl.bundle.annotations.export.attributes";
		Capability pkg = getPackageCapability(pkgName);
		Map<String,Object> attributes = pkg.getAttributes();
		assertThat(attributes).as("Package %s capability attributes", pkgName)
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
				.containsEntry(CAPABILITY_VERSION_ATTRIBUTE,
						Version.valueOf("1.0"))
				.doesNotContainKey("x-directive");

		Map<String,String> directives = pkg.getDirectives();
		assertThat(directives).as("Package %s capability directives", pkgName)
				.containsEntry("x-directive", "directiveValue")
				.doesNotContainKeys("attr", "longAttr", "stringAttr",
						"doubleAttr", "versionAttr", "longList", "stringList",
						"doubleList", "versionList");
	}

	@Test
	public void testSubstitutionCalculated() throws Exception {
		String pkgName = "org.osgi.impl.bundle.annotations.export.calculated";
		assertThat(getPackageCapability(pkgName).getAttributes())
				.as("Package %s capability attributes", pkgName)
				.containsEntry(CAPABILITY_VERSION_ATTRIBUTE,
						Version.valueOf("1.0"));
		Requirement pkg = getPackageRequirement(pkgName);
		Map<String,String> directives = pkg.getDirectives();
		String filterString = directives.get(REQUIREMENT_FILTER_DIRECTIVE);
		Filter filter = getContext().createFilter(filterString);
		assertThat(filter)
				.as("Package %s requirement filter incorrect: %s", pkgName,
						filterString)
				.is(new FilterCondition(Maps.mapOf(PACKAGE_NAMESPACE,
						pkgName, CAPABILITY_VERSION_ATTRIBUTE,
						Version.valueOf("1.0"))))
				.isNot(new FilterCondition(Maps.mapOf(PACKAGE_NAMESPACE,
						pkgName, CAPABILITY_VERSION_ATTRIBUTE,
						Version.valueOf("0.9"))))
				.isNot(new FilterCondition(Maps.mapOf(PACKAGE_NAMESPACE,
						pkgName, CAPABILITY_VERSION_ATTRIBUTE,
						Version.valueOf("1.1"))))
				.isNot(new FilterCondition(Maps.mapOf(PACKAGE_NAMESPACE,
						pkgName, CAPABILITY_VERSION_ATTRIBUTE,
						Version.valueOf("2.0"))));
	}

	@Test
	public void testSubstitutionProvider() throws Exception {
		String pkgName = "org.osgi.impl.bundle.annotations.export.provider";
		assertThat(getPackageCapability(pkgName).getAttributes())
				.as("Package %s capability attributes", pkgName)
				.containsEntry(CAPABILITY_VERSION_ATTRIBUTE,
						Version.valueOf("1.0"));
		Requirement pkg = getPackageRequirement(pkgName);
		Map<String,String> directives = pkg.getDirectives();
		String filterString = directives.get(REQUIREMENT_FILTER_DIRECTIVE);
		Filter filter = getContext().createFilter(filterString);
		assertThat(filter)
				.as("Package %s requirement filter incorrect: %s", pkgName,
						filterString)
				.is(new FilterCondition(Maps.mapOf(PACKAGE_NAMESPACE,
						pkgName, CAPABILITY_VERSION_ATTRIBUTE,
						Version.valueOf("1.0"))))
				.isNot(new FilterCondition(Maps.mapOf(PACKAGE_NAMESPACE,
						pkgName, CAPABILITY_VERSION_ATTRIBUTE,
						Version.valueOf("0.9"))))
				.isNot(new FilterCondition(Maps.mapOf(PACKAGE_NAMESPACE,
						pkgName, CAPABILITY_VERSION_ATTRIBUTE,
						Version.valueOf("1.1"))))
				.isNot(new FilterCondition(Maps.mapOf(PACKAGE_NAMESPACE,
						pkgName, CAPABILITY_VERSION_ATTRIBUTE,
						Version.valueOf("2.0"))));
	}

	@Test
	public void testSubstitutionConsumer() throws Exception {
		String pkgName = "org.osgi.impl.bundle.annotations.export.consumer";
		assertThat(getPackageCapability(pkgName).getAttributes())
				.as("Package %s capability attributes", pkgName)
				.containsEntry(CAPABILITY_VERSION_ATTRIBUTE,
						Version.valueOf("1.0"));
		Requirement pkg = getPackageRequirement(pkgName);
		Map<String,String> directives = pkg.getDirectives();
		String filterString = directives.get(REQUIREMENT_FILTER_DIRECTIVE);
		Filter filter = getContext().createFilter(filterString);
		assertThat(filter)
				.as("Package %s requirement filter incorrect: %s", pkgName,
						filterString)
				.is(new FilterCondition(Maps.mapOf(PACKAGE_NAMESPACE,
						pkgName, CAPABILITY_VERSION_ATTRIBUTE,
						Version.valueOf("1.0"))))
				.isNot(new FilterCondition(Maps.mapOf(PACKAGE_NAMESPACE,
						pkgName, CAPABILITY_VERSION_ATTRIBUTE,
						Version.valueOf("0.9"))))
				.is(new FilterCondition(Maps.mapOf(PACKAGE_NAMESPACE,
						pkgName, CAPABILITY_VERSION_ATTRIBUTE,
						Version.valueOf("1.1"))))
				.isNot(new FilterCondition(Maps.mapOf(PACKAGE_NAMESPACE,
						pkgName, CAPABILITY_VERSION_ATTRIBUTE,
						Version.valueOf("2.0"))));
	}

	@Test
	public void testSubstitutionNoimport() throws Exception {
		String pkgName = "org.osgi.impl.bundle.annotations.export.noimport";
		assertThat(getPackageCapability(pkgName).getAttributes())
				.as("Package %s capability attributes", pkgName)
				.containsEntry(CAPABILITY_VERSION_ATTRIBUTE,
						Version.valueOf("1.0"));
		Requirement pkg = findPackageRequirement(pkgName);
		assertThat(pkg).as("Package %s is incorrectly imported", pkgName)
				.isNull();
	}

	@Test
	public void testNoUses() throws Exception {
		String pkgName = "org.osgi.impl.bundle.annotations.export.nouses";
		Capability pkg = getPackageCapability(pkgName);
		Map<String,String> directives = pkg.getDirectives();
		assertThat(directives).as("Package capability directives")
				.doesNotContainKey(CAPABILITY_USES_DIRECTIVE);
	}

	@Test
	public void testUses() throws Exception {
		String pkgName = "org.osgi.impl.bundle.annotations.export.uses";
		Capability pkg = getPackageCapability(pkgName);
		Map<String,String> directives = pkg.getDirectives();
		assertThat(directives).as("Package capability directives")
				.containsKey(CAPABILITY_USES_DIRECTIVE);
		assertThat(Strings.split(directives.get(CAPABILITY_USES_DIRECTIVE)))
				.containsExactlyInAnyOrder("bar", "foo");
	}

	@Test
	public void testUsesCalculated() throws Exception {
		String pkgName = "org.osgi.impl.bundle.annotations.export.user";
		Capability pkg = getPackageCapability(pkgName);
		Map<String,String> directives = pkg.getDirectives();
		assertThat(directives).as("Package capability directives")
				.containsKey(CAPABILITY_USES_DIRECTIVE);
		assertThat(Strings.split(directives.get(CAPABILITY_USES_DIRECTIVE)))
				.containsExactlyInAnyOrder(
						"org.osgi.impl.bundle.annotations.export.uses");
	}

	@Test
	public void testProviderType() throws Exception {
		String pkgName = "org.osgi.impl.bundle.annotations.pkg.provider";
		assertThat(getPackageCapability(pkgName).getAttributes())
				.as("Package %s capability attributes", pkgName)
				.containsEntry(CAPABILITY_VERSION_ATTRIBUTE,
						Version.valueOf("1.0"));
		Requirement pkg = getPackageRequirement(pkgName);
		Map<String,String> directives = pkg.getDirectives();
		String filterString = directives.get(REQUIREMENT_FILTER_DIRECTIVE);
		Filter filter = getContext().createFilter(filterString);
		assertThat(filter)
				.as("Package %s requirement filter incorrect: %s", pkgName,
						filterString)
				.is(new FilterCondition(Maps.mapOf(PACKAGE_NAMESPACE,
						pkgName, CAPABILITY_VERSION_ATTRIBUTE,
						Version.valueOf("1.0"))))
				.isNot(new FilterCondition(Maps.mapOf(PACKAGE_NAMESPACE,
						pkgName, CAPABILITY_VERSION_ATTRIBUTE,
						Version.valueOf("0.9"))))
				.isNot(new FilterCondition(Maps.mapOf(PACKAGE_NAMESPACE,
						pkgName, CAPABILITY_VERSION_ATTRIBUTE,
						Version.valueOf("1.1"))))
				.isNot(new FilterCondition(Maps.mapOf(PACKAGE_NAMESPACE,
						pkgName, CAPABILITY_VERSION_ATTRIBUTE,
						Version.valueOf("2.0"))));
	}

}

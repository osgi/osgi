/*
 * Copyright (c) OSGi Alliance (2018). All Rights Reserved.
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
import static org.osgi.framework.namespace.PackageNamespace.PACKAGE_NAMESPACE;
import static org.osgi.resource.Namespace.REQUIREMENT_FILTER_DIRECTIVE;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.test.support.junit4.AbstractOSGiTestCase;

public abstract class AnnotationsTestCase extends AbstractOSGiTestCase {
	protected Bundle			impl;
	protected BundleRevision	revision;

	@Before
	public void obtainBundleRevision() throws Exception {
		final String propName = "org.osgi.test.cases.bundle.annotations.bundle.symbolic.name";
		String bsn = getProperty(propName);
		assertThat(bsn).as(
				"The system property \"%s\" must be set to the Bundle Symbolic Name of the bundle processed by the Bundle Annotations tool.",
				propName).isNotNull();
		impl = null;
		for (Bundle b : getContext().getBundles()) {
			if (bsn.equals(b.getSymbolicName())) {
				impl = b;
				break;
			}
		}
		assertThat(impl).as(
				"Could not locate the bundle with Bundle Symbolic Name %s", bsn)
				.isNotNull();
		revision = impl.adapt(BundleRevision.class);
		assertThat(revision).as("Bundle %s has no BundleRevision", impl)
				.isNotNull();
	}

	public Capability findPackageCapability(String name) {
		List<Capability> capabilities = revision
				.getCapabilities(PACKAGE_NAMESPACE);
		assertThat(capabilities).as("Package capabilities")
				.isNotNull()
				.isNotEmpty();
		for (Capability capability : capabilities) {
			Map<String,Object> attributes = capability.getAttributes();
			assertThat(attributes).as("Package %s capability has no name", name)
					.containsKey(PACKAGE_NAMESPACE);

			String packageName = (String) attributes.get(PACKAGE_NAMESPACE);
			assertThat(packageName).as("Package %s capability name empty", name)
					.isNotEmpty();
			if (packageName.equals(name)) {
				return capability;
			}
		}
		return null;
	}

	public Capability getPackageCapability(String name) {
		Capability capability = findPackageCapability(name);
		assertThat(capability)
				.as("Package %s capability could not be found", name)
				.isNotNull();
		return capability;
	}

	public Requirement findPackageRequirement(String name) {
		List<Requirement> requirements = revision
				.getRequirements(PACKAGE_NAMESPACE);
		assertThat(requirements).as("Package %s requirements", name)
				.isNotNull()
				.isNotEmpty();
		String packageFilter = "(" + PACKAGE_NAMESPACE + "=" + name + ")";
		for (Requirement requirement : requirements) {
			Map<String,String> directives = requirement.getDirectives();
			assertThat(directives)
					.as("Package %s requirement has no filter", name)
					.containsKey(REQUIREMENT_FILTER_DIRECTIVE);

			String filter = directives.get(REQUIREMENT_FILTER_DIRECTIVE);
			assertThat(filter).as("Package %s requirement filter empty", name)
					.isNotEmpty();
			if (filter.indexOf(packageFilter) > 0) {
				return requirement;
			}
		}
		return null;
	}

	public Requirement getPackageRequirement(String name) {
		Requirement requirement = findPackageRequirement(name);
		assertThat(requirement)
				.as("Package %s requirement could not be found", name)
				.isNotNull();
		return requirement;
	}

	public Requirement getRequirement(String namespace) {
		List<Requirement> requirements = revision.getRequirements(namespace);
		assertThat(requirements).as("%s requirements", namespace)
				.isNotNull()
				.hasSize(1);
		return requirements.get(0);
	}

	public Capability getCapability(String namespace) {
		List<Capability> capabilities = revision.getCapabilities(namespace);
		assertThat(capabilities).as("%s capabilities", namespace)
				.isNotNull()
				.hasSize(1);
		return capabilities.get(0);
	}

}

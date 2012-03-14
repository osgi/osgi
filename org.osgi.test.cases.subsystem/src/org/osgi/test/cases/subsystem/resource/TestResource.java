/*
 * Copyright (c) OSGi Alliance (2012). All Rights Reserved.
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
package org.osgi.test.cases.subsystem.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.namespace.IdentityNamespace;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.service.repository.RepositoryContent;

public class TestResource implements Resource, RepositoryContent {
	public static volatile boolean failContent = false;
	private final Map<String, List<Capability>> capabilities;
	private final Map<String, List<Requirement>> requirements;
	private final URL content;

	public TestResource(Map<String, ? extends Object> subsystemAttrs, Map<String, String> subsystemDirs, URL content) {
		this.requirements = Collections.emptyMap();
		this.capabilities = new HashMap<String, List<Capability>>(); 
		this.capabilities.put(
				IdentityNamespace.IDENTITY_NAMESPACE,
				new ArrayList<Capability>(Arrays.asList(new TestCapability(
						IdentityNamespace.IDENTITY_NAMESPACE, subsystemAttrs,
						subsystemDirs, this))));
		this.content = content;
	}
	public TestResource(Bundle bundle, URL content) {
		BundleRevision revision = bundle.adapt(BundleRevision.class);
		this.capabilities = createCapabilities(revision.getCapabilities(null));
		this.requirements = createRequirements(revision.getRequirements(null));
		this.content = content;
	}

	private Map<String, List<Capability>> createCapabilities(
			List<Capability> capabilityList) {
		Map <String, List<Capability>> result = new HashMap<String, List<Capability>>();
		for (Capability capability : capabilityList) {
			List<Capability> capabilities = result.get(capability.getNamespace());
			if (capabilities == null) {
				capabilities = new ArrayList<Capability>();
				result.put(capability.getNamespace(), capabilities);
			}
			capabilities.add(new TestCapability(capability, this));
		}
		return result;
	}

	private Map<String, List<Requirement>> createRequirements(
			List<Requirement> requirementList) {
		Map <String, List<Requirement>> result = new HashMap<String, List<Requirement>>();
		for (Requirement requirement : requirementList) {
			List<Requirement> requirements = result.get(requirement.getNamespace());
			if (requirements == null) {
				requirements = new ArrayList<Requirement>();
				result.put(requirement.getNamespace(), requirements);
			}
			requirements.add(new TestRequirement(requirement, this));
		}
		return result;
	}

	public List<Capability> getCapabilities(String namespace) {
		List<Capability> result = capabilities.get(namespace);
		if (result == null)
			return Collections.emptyList();
		return Collections.unmodifiableList(result);
	}

	public List<Requirement> getRequirements(String namespace) {
		List<Requirement> result = requirements.get(namespace);
		if (result == null)
			return Collections.emptyList();
		return Collections.unmodifiableList(result);
	}

	static class TestCapability implements Capability {
		private final String namespace;
		private final Map<String, String> directives;
		private final Map<String, Object> attributes;
		private final TestResource resource;

		TestCapability(String namespace, Map<String, ? extends Object> attrs, Map<String, String> dirs, TestResource resource) {
			this.namespace = namespace;
			if (dirs == null)
				this.directives = Collections.emptyMap();
			else 
				this.directives = Collections.unmodifiableMap(new HashMap<String, String>(dirs));
			if (attrs == null)
				this.attributes = Collections.emptyMap();
			else
				this.attributes = Collections.unmodifiableMap(new HashMap<String, Object>(attrs));
			this.resource = resource;
		}

		TestCapability(Capability capability, TestResource resource) {
			namespace = capability.getNamespace();
			directives = Collections.unmodifiableMap(new HashMap<String, String>(capability.getDirectives()));
			attributes = Collections.unmodifiableMap(new HashMap<String, Object>(capability.getAttributes()));
			this.resource = resource;
		}

		public String getNamespace() {
			return namespace;
		}

		public Map<String, String> getDirectives() {
			return directives;
		}

		public Map<String, Object> getAttributes() {
			return attributes;
		}

		public Resource getResource() {
			return resource;
		}
	}

	static class TestRequirement implements Requirement {
		private final String namespace;
		private final Map<String, String> directives;
		private final Map<String, Object> attributes;
		private final TestResource resource;

		TestRequirement(Requirement requirement, TestResource resource) {
			namespace = requirement.getNamespace();
			directives = Collections.unmodifiableMap(new HashMap<String, String>(requirement.getDirectives()));
			attributes = Collections.unmodifiableMap(new HashMap<String, Object>(requirement.getAttributes()));
			this.resource = resource;
		}

		public String getNamespace() {
			return namespace;
		}

		public Map<String, String> getDirectives() {
			return directives;
		}

		public Map<String, Object> getAttributes() {
			return attributes;
		}

		public Resource getResource() {
			return resource;
		}
	}

	public InputStream getContent() {
		try {
			if (failContent) {
				return new InputStream() {
					@Override
					public int read() throws IOException {
						throw new IOException("Testing failed stream.");
					}
				};
			}
			return content.openStream();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
